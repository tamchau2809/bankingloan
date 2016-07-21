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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import chau.bankingloan.customThings.ConstantStuff;

/**
 * Created on 28-Apr-16 by com08.
 */
public class Tab5Fragment extends Fragment {
    View rootView;

    private LinearLayout lnrImages;
    SharedPreferences tab2Pref;


    ImageButton imgBtnAdd, imgBtnCamera, imgBtnUpload, imgBtnBack, imgBtnNext;

    private ArrayList<String> imagesPathList;
    ProgressDialog pDialog;
    private final int PICK_IMAGE_MULTIPLE = 1;
    File sourceFile;

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
                    new UploadFile().execute();
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

        imgBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity act = (MainActivity) getActivity();
                act.switchTab(5);
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
        imgBtnNext = (ImageButton)rootView.findViewById(R.id.imgBtnNextTab5);
    }

    public File saveImage(Bitmap myBitmap, String name, Context context) {

        File myDir = new File( Environment.getExternalStorageDirectory(), context.getPackageName());
        if(!myDir.exists()){
            myDir.mkdir();
        }
        File file = new File (myDir, name);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
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

    private class UploadFile extends AsyncTask<Void, Float, String>
    {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Uploading...");
            pDialog.setCancelable(false);
            pDialog.show();
            setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            // TODO Auto-generated method stub
            pDialog.setMessage("Uploading..." + String.valueOf(values[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            return uploadFile();
        }

        private String uploadFile()
        {
            String response = null;
            HttpClient httpClient = new DefaultHttpClient();
            HttpParams test = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(test, 5000);
            HttpConnectionParams.setSoTimeout(test, 5000);
            HttpPost httpPost = new HttpPost(ConstantStuff.FILE_UPLOAD_URL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            try
            {
                UploadProgress2.ProgressListener lis = new UploadProgress2.ProgressListener() {

                    @Override
                    public void transferred(float num) {
                        // TODO Auto-generated method stub
                        publishProgress((num));
                    }
                };
                for(int i = 0; i < imagesPathList.size(); i++)
                {
                    int x = i + 1;
                    final Bitmap bitmap = BitmapFactory.decodeFile(imagesPathList.get(i));
                    sourceFile = saveImage(bitmap, String.valueOf(x), getContext());
                    builder.addPart("image" + x, new FileBody(sourceFile));
                }

                String ID = tab2Pref.getString("IdentityCardNum", "");
                String name = tab2Pref.getString("Name", "");
                builder.addPart("NAME_ID", new StringBody(name, ContentType.TEXT_PLAIN));
                builder.addPart("ID_NUM", new StringBody(ID, ContentType.TEXT_PLAIN));

                httpPost.setEntity(new UploadProgress2(builder.build(), lis));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if(statusCode == 200)
                {
                    response = EntityUtils.toString(httpEntity);
                }
                else
                {
                    response = "Error: + " + statusCode;
                }
            } catch (IOException e) {
                response = e.toString();
            }
            catch(Exception ignored)
            {}
            return response;
        }


        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            File myDir=new File( Environment.getExternalStorageDirectory(), getContext().getPackageName());
            if(!myDir.exists()){
                myDir.mkdir();
            }
            if(result == null)
            {
                showAlert("Thất Bại!" + result);
            }
            else if(result.equals("11111") || result.equals("1111")
                    || result.equals("111") || result.equals("11")
                    || result.equals("1"))
            {
                showAlert("Uploads Completed!");
                MainActivity act = (MainActivity) getActivity();
                act.switchTab(5);
            }
            else
            {
                showAlert("Thất Bại!" + result);
            }
            setEnabled(true);
            if (pDialog.isShowing())
                pDialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
