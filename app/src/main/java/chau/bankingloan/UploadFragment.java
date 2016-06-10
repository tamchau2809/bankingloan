package chau.bankingloan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created on 11-May-16 by com08.
 */
public class UploadFragment extends Fragment {
    View rootView;

    private LinearLayout lnrImages;

    final String FILE_UPLOAD_URL = "http://192.168.1.17/chauvu/up.php";

    FloatingActionButton fab2, fab3, fab4, fab5;

    private ArrayList<String> imagesPathList;
    ProgressBar prgPercent;
    TextView tvPercent;
    private final int PICK_IMAGE_MULTIPLE = 1;
    File sourceFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_upload, container, false);
        initWidget();

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomPhotoGalleryActivity.class);
                startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                startActivity(intent);
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagesPathList == null) {
                    showAlert("Không Có Hình Ảnh Để Upload!");
                } else {
                    new BackgroundUploader(FILE_UPLOAD_URL).execute();
                }
            }
        });

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity act = (MainActivity) getActivity();
                act.switchTab(0);
            }
        });
        return rootView;
    }

    public void initWidget() {
        lnrImages = (LinearLayout) rootView.findViewById(R.id.lnrImages);
        tvPercent = (TextView) rootView.findViewById(R.id.tvPercent);
//        prgPercent = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        fab2 = (FloatingActionButton) rootView.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) rootView.findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) rootView.findViewById(R.id.fab4);
        fab5 = (FloatingActionButton) rootView.findViewById(R.id.fab5);
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

    @SuppressLint("SetTextI18n")
    public void showProgress(final boolean show, float value) {
        prgPercent.setVisibility(show ? View.VISIBLE : View.GONE);
        tvPercent.setVisibility(show ? View.VISIBLE : View.GONE);
        lnrImages.setEnabled(!show);
        fab2.setEnabled(!show);
        fab3.setEnabled(!show);
        fab4.setEnabled(!show);
        fab5.setEnabled(!show);

//        prgPercent.getIndeterminateDrawable().setColorFilter(Color.GREEN,
//                android.graphics.PorterDuff.Mode.MULTIPLY);

        prgPercent.setProgressTintList(ColorStateList.valueOf(Color.GREEN));

        tvPercent.setText(String.valueOf((int) value) + "%");
    }

    public File saveImage(Bitmap myBitmap, String name, Context context) {

        File myDir = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        if (!myDir.exists()) {
            myDir.mkdir();
        }
        File file = new File(myDir, name);
        if (file.exists()) file.delete();
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
            if (requestCode == PICK_IMAGE_MULTIPLE) {
                imagesPathList = new ArrayList<>();
                String[] imagesPath = data.getStringExtra("data").split("\\|");
                try {
                    lnrImages.removeAllViews();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                for (String anImagesPath : imagesPath) {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inSampleSize = 2;
                    imagesPathList.add(anImagesPath);
                    Bitmap yourbitmap = BitmapFactory.decodeFile(anImagesPath, opt);
                    ImageView imageView = new ImageView(getContext());
                    imageView.setImageBitmap(yourbitmap);
                    imageView.setAdjustViewBounds(true);
                    lnrImages.addView(imageView);
                }
            }
        }
    }
    class BackgroundUploader extends AsyncTask<Void, Integer, Void> implements DialogInterface.OnCancelListener {

        private ProgressDialog progressDialog;
        private String url;
        private File file;

        public BackgroundUploader(String url, File file) {
            this.url = url;
            this.file = file;
        }

        public BackgroundUploader(String url) {
            this.url = url;
            Bitmap bitmap = BitmapFactory.decodeFile(imagesPathList.get(0));
            file = saveImage(bitmap, "test", getContext());
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... v) {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = null;
            String fileName = file.getName();
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
//                String boundary = "---------------------------boundary";
//                String tail = "\r\n--" + boundary + "--\r\n";
//                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setDoOutput(true);

//                String metadataPart = "--" + boundary + "\r\n"
//                        + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
//                        + "" + "\r\n";

//                String fileHeader1 = "--" + boundary + "\r\n"
//                        + "Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
//                        + fileName + "\"\r\n"
//                        + "Content-Type: application/octet-stream\r\n"
//                        + "Content-Transfer-Encoding: binary\r\n";

//                long fileLength = file.length() + tail.length();
//                String fileHeader2 = "Content-length: " + fileLength + "\r\n";
//                String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
//                String stringData = metadataPart + fileHeader;

//                long requestLength = stringData.length() + fileLength;
//                connection.setRequestProperty("Content-length", "" + requestLength);
//                connection.setFixedLengthStreamingMode((int) requestLength);
                connection.connect();

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
//                out.writeBytes(stringData);
                out.flush();

                int progress = 0;
                int bytesRead;
                byte buf[] = new byte[1024];
                BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(file));
                while ((bytesRead = bufInput.read(buf)) != -1) {
                    // write output
                    out.write(buf, 0, bytesRead);
                    out.flush();
                    progress += bytesRead;
                    // update progress bar
                    publishProgress(progress);
                }

                // Write closing boundary and close stream
//                out.writeBytes(tail);
                out.flush();
                out.close();

                // Get server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                }

            } catch (Exception e) {
                // Exception
            } finally {
                if (connection != null) connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            cancel(true);
            dialog.dismiss();
        }
    }
}