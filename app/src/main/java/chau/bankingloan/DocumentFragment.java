package chau.bankingloan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created on 28-Apr-16 by com08.
 */
public class DocumentFragment extends Fragment {
    View rootView;

    private LinearLayout lnrImages;

    final String FILE_UPLOAD_URL = "http://192.168.1.18/chauvu/up.php";
    FloatingActionButton fabAddImg, fabCamera, fabUpload, fabBack;

    private ArrayList<String> imagesPathList;
    ProgressBar prgPercent;
    TextView tvPercent;
    private final int PICK_IMAGE_MULTIPLE =1;
    File sourceFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_document, container, false);
        initWiget();

        fabAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomPhotoGalleryActivity.class);
                startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                startActivity(intent);
            }
        });

        fabUpload.setOnClickListener(new View.OnClickListener() {
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

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity act = (MainActivity) getActivity();
                act.switchTab(5);
            }
        });

        return rootView;
    }

    public void initWiget()
    {
        lnrImages = (LinearLayout)rootView.findViewById(R.id.llImage);
        tvPercent = (TextView)rootView.findViewById(R.id.tvPercentUpload);
        prgPercent = (ProgressBar)rootView.findViewById(R.id.prgUpload);
        fabAddImg = (FloatingActionButton) rootView.findViewById(R.id.fabAddImg);
        fabCamera = (FloatingActionButton) rootView.findViewById(R.id.fabTakePic);
        fabUpload = (FloatingActionButton) rootView.findViewById(R.id.fabUpload);
        fabBack = (FloatingActionButton) rootView.findViewById(R.id.fabPre);
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

                for (int i=0;i<imagesPath.length;i++)
                {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inSampleSize = 2;
                    imagesPathList.add(imagesPath[i]);
                    Bitmap yourbitmap = BitmapFactory.decodeFile(imagesPath[i], opt);
                    ImageView imageView = new ImageView(getContext());
                    imageView.setImageBitmap(yourbitmap);
                    imageView.setAdjustViewBounds(true);
                    lnrImages.addView(imageView);
                }
            }
        }
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message).setTitle("Thông Báo!!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showProgress(final boolean show, float value)
    {
        prgPercent.setVisibility(show ? View.VISIBLE : View.GONE);
        tvPercent.setVisibility(show ? View.VISIBLE : View.GONE);
        lnrImages.setEnabled(!show);
        fabBack.setEnabled(!show);
        fabUpload.setEnabled(!show);
        fabAddImg.setEnabled(!show);
        fabCamera.setEnabled(!show);

        prgPercent.setProgressTintList(ColorStateList.valueOf(Color.GREEN));

        tvPercent.setText(String.valueOf((int)value) + "%");
    }

    private class UploadFile extends AsyncTask<Void, Float, String>
    {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            prgPercent.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            // TODO Auto-generated method stub
            showProgress(true, values[0]);
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
            HttpPost httpPost = new HttpPost(FILE_UPLOAD_URL);
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
                    String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss",
                            Locale.getDefault()).format(new Date());
                    builder.addPart("cus_time", new StringBody(timeStamp, ContentType.TEXT_PLAIN));
                }

                builder.addPart("MAKH", new StringBody(MainActivity.MAKH, ContentType.TEXT_PLAIN));
                builder.addPart("cus_number", new StringBody(MainActivity.contractNum, ContentType.TEXT_PLAIN));
                builder.addPart("MANV", new StringBody(MainActivity.MANV, ContentType.TEXT_PLAIN));

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
            else if(result.equals("111") || result.equals("11") || result.equals("1"))
            {
                showAlert("Hoàn Thành!");
            }
            else
            {
                showAlert("Thất Bại!" + result);
            }
            showProgress(false, 0);
            super.onPostExecute(result);
        }
    }
}
