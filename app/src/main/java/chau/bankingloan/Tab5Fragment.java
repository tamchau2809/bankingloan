package chau.bankingloan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    SharedPreferences tab2Pref;

    ImageButton imgBtnAdd, imgBtnCamera, imgBtnUpload, imgBtnBack;

    private ArrayList<String> imagesPathList;
    private final int PICK_IMAGE_MULTIPLE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_tab_5, container, false);
        initWidget();

        tab2Pref = this.getActivity().getSharedPreferences("TAB2", Context.MODE_APPEND);

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
                Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                startActivity(intent);
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

    public void initWidget()
    {
        lnrImages = (LinearLayout)rootView.findViewById(R.id.lnrImage);
        imgBtnAdd = (ImageButton) rootView.findViewById(R.id.imgBtnAddTab5);
        imgBtnCamera = (ImageButton) rootView.findViewById(R.id.imgBtnCameraTab5);
        imgBtnUpload = (ImageButton) rootView.findViewById(R.id.imgBtnUploadTab5);
        imgBtnBack = (ImageButton) rootView.findViewById(R.id.imgBtnPreTab5);
    }

//    public void getDataFromSP(SharedPreferences pref, String tab, String name)
//    {
//        pref = this.getActivity().getSharedPreferences(tab, Context.MODE_APPEND);
//        Set<String> set = pref.getStringSet(name, null);
//        if (set != null) {
//            for(String s : set)
//            {
//                try
//                {
//                    JSONObject jsonObject = new JSONObject(s);
//                    String value = jsonObject.getString("value");
//                }
//                catch(JSONException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

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
                return uploadFile(arrayList, ConstantStuff.FILE_UPLOAD_URL);
            } catch (Exception e) {
                // Exception
            }

            return null;
        }

        private String uploadFile(ArrayList<String> imgPaths, String requestURL) {

            String charset = "UTF-8";
            String result = "";

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
                    System.out.println(line);
                    result = line;
                }
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
            else if(v.startsWith("1"))
            {
                showAlert("Uploads Completed!");
                MainActivity act = (MainActivity) getActivity();
                act.switchTab(5);
            }
            setEnabled(true);
        }
    }
}
