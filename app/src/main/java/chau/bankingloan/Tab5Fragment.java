package chau.bankingloan;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import chau.bankingloan.customThings.ConstantStuff;
import chau.bankingloan.customThings.CustomPhotoGalleryActivity;
import chau.bankingloan.customThings.UploadFile;

/**
 * Created on 28-Apr-16 by com08.
 */
public class Tab5Fragment extends Fragment
{
    View rootView;

    private LinearLayout lnrImages;
    SharedPreferences preferences;

    ImageButton imgBtnAdd, imgBtnCamera, imgBtnUpload, imgBtnBack;

    private ArrayList<String> imagesPathList;
    private final int PICK_IMAGE_MULTIPLE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_tab_5, container, false);
        initWidget();

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 2101);
        }

        preferences = this.getActivity().getSharedPreferences("TAB5", Context.MODE_APPEND);

        imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomPhotoGalleryActivity.class);
                startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
            }
        });

        imgBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
//                startActivity(intent);
                doTakePhotoAction();
            }
        });

        imgBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imagesPathList == null)
                {
                    showAlert("Không Có Hình Ảnh Để Upload!");
                }
                else
                {
                    new BackgroundUploader(imagesPathList).execute();
                }
            }
        });

        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity act = (MainActivity) getActivity();
                act.switchTab(3);
            }
        });

        return rootView;
    }

    private void doTakePhotoAction() {

//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // 创建目录
//        File fileDir = new File(Environment.getExternalStorageDirectory()
//                + "/zuijiao");
//        if (!fileDir.exists()) {
//            fileDir.mkdirs();
//        }
//        // 拍照后的路径
//        String imagePath = Environment.getExternalStorageDirectory() + "/zuijiao/"
//                + System.currentTimeMillis() + ".jpg";
//        File carmeraFile = new File(imagePath);
//        Uri imageCarmeraUri = Uri.fromFile(carmeraFile);
//
//        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
//                imageCarmeraUri);

//        int imageNum = 0;
//        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Punch");
//        imagesFolder.mkdirs(); // <----
//        String fileName = "image_" + String.valueOf(imageNum) + ".jpg";
//        File output = new File(imagesFolder, fileName);
//        while (output.exists()){
//            imageNum++;
//            fileName = "image_" + String.valueOf(imageNum) + ".jpg";
//            output = new File(imagesFolder, fileName);
//        }
//        Uri uriSavedImage = Uri.fromFile(output);
//        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
//
//
//        OutputStream imageFileOS;
//        try {
//            imageFileOS = getActivity().getContentResolver().openOutputStream(uriSavedImage);
//            imageFileOS.write(arg0);
//            imageFileOS.flush();
//            imageFileOS.close();
//
//            Toast.makeText(getContext(),
//                    "Image saved: ",
//                    Toast.LENGTH_LONG).show();
//
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        try {
//            intent.putExtra("return-data", true);
//            startActivityForResult(intent, 2100);
//        } catch (ActivityNotFoundException e) {
//            // Do nothing for now
//        }
    }

    public void initWidget()
    {
        lnrImages = (LinearLayout)rootView.findViewById(R.id.lnrImage);
        imgBtnAdd = (ImageButton) rootView.findViewById(R.id.imgBtnAddTab5);
        imgBtnCamera = (ImageButton) rootView.findViewById(R.id.imgBtnCameraTab5);
        imgBtnUpload = (ImageButton) rootView.findViewById(R.id.imgBtnUploadTab5);
        imgBtnBack = (ImageButton) rootView.findViewById(R.id.imgBtnPreTab5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == PICK_IMAGE_MULTIPLE){
                imagesPathList = new ArrayList<>();
                String[] imagesPath = data.getStringExtra("data").split("\\|");
                try{
                    lnrImages.removeAllViews();
                }catch (Throwable e){
                    e.printStackTrace();
                }
                for (String anImagesPath : imagesPath) {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inSampleSize = 2;
                    imagesPathList.add(anImagesPath);
                    Bitmap yourBitmap = BitmapFactory.decodeFile(anImagesPath, opt);
                    ImageView imageView = new ImageView(getContext());
                    imageView.setImageBitmap(yourBitmap);
                    imageView.setAdjustViewBounds(true);
                    lnrImages.addView(imageView);
                }
            }
        }
    }

    private void showAlert(String message) {

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(message)
                .setPositiveButton(
                        android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                .create();

        alertDialog.setCancelable(false);
        alertDialog.show();

        View btnTest = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void setEnabled(final boolean show)
    {
        lnrImages.setEnabled(show);
        imgBtnBack.setEnabled(show);
        imgBtnUpload.setEnabled(show);
        imgBtnAdd.setEnabled(show);
        imgBtnCamera.setEnabled(show);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ThrowablePrintedToSystemOut"})
    class BackgroundUploader extends AsyncTask<Void, Void, String>
    {
        String result = "";
        private ProgressDialog progressDialog;
        ArrayList<String> arrayList;

        BackgroundUploader(ArrayList<String> strings) {
            this.arrayList = strings;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            setEnabled(false);
        }

        @Override
        protected String doInBackground(Void... v) {
            try {
                return uploadFile(arrayList, ConstantStuff.UPLOAD_URL);
            } catch (Exception e) {
                // Exception
            }

            return null;
        }

        private String uploadFile(ArrayList<String> imgPaths, String requestURL) {

            String charset = "UTF-8";

            File sourceFile[] = new File[imgPaths.size()];
            for (int i=0;i<imgPaths.size();i++)
            {
                Bitmap bitmap = BitmapFactory.decodeFile(imgPaths.get(i));
                String a = randomName(3);
                sourceFile[i] = compressImage(bitmap, a, getContext());
//                sourceFile[i] = new File(imgPaths.get(i));
            }

            try {
                UploadFile multipart = new UploadFile(requestURL, charset);

                multipart.addHeaderField("User-Agent", "CodeJava");
                multipart.addHeaderField("Test-Header", "Header-Value");

                multipart.addFormField("description", "Cool Pictures");
                multipart.addFormField("keywords", "Java,upload,Spring");

                for (int i=0;i<imgPaths.size();i++){
                    multipart.addFilePart("uploaded_file[]", sourceFile[i]);
                }

                List<String> response = multipart.finish();

                System.out.println("SERVER REPLIED:");

                for (String line : response) {
//                    System.out.println(line);
                    result = result + line  + ",";
                }
                result = result.substring(0, result.length() - 1);
                System.out.println(result);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().apply();
                editor.putString("IMG_URL", result);
                editor.apply();

            } catch (IOException ex) {
                System.err.println(ex);
            }
            return result;
        }

        /**
         * Dùng để nén hình ảnh
         * @param bitmap Hình ảnh
         * @param context getContext
         * @return hình ảnh được nén
         */
        File compressImage(Bitmap bitmap, String name, Context context)
        {
            File myDir=new File( Environment.getExternalStorageDirectory(), context.getPackageName());
            if(!myDir.exists()) myDir.mkdir();
            File file = new File (myDir, name);
            if (file.exists ()) {
                file.delete();
            }
            try
            {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.flush();
                out.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return file;
        }

        String randomName(int length)
        {
            Random random = new SecureRandom();
            return String.format("%"+length+"s", new BigInteger(length*5/*base 32,2^5*/, random)
                    .toString(32)).replace('\u0020', '0');
        }

        @Override
        protected void onPostExecute(String v) {
            progressDialog.dismiss();
            if(v.startsWith("2"))
            {
                showAlert("Thất Bại!" + v);
            }
            else
            {
                showAlert("Uploads Completed!");
                MainActivity act = (MainActivity) getActivity();
                act.switchTab(5);
            }
            setEnabled(true);
        }
    }
}
