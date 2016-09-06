package chau.bankingloan;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import chau.bankingloan.customThings.ConstantStuff;

public class CustomPhotoGalleryActivity extends Activity {

    private String[] arrPath;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;
    Cursor imagecursor;
    GridView grdImages;
    Button btnSelect;

    final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
    final String orderBy = MediaStore.Images.Media._ID;

    private TextView requestPermission;
    private Button btnPermission;
    private final String[] requiredPermissions = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE };
    private Handler handler;
    private ContentObserver observer;


    /**
     * Overrides methods
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_custom_gallery);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        grdImages = (GridView) findViewById(R.id.grdImages);
        btnSelect = (Button) findViewById(R.id.btnSelect);
        btnPermission = (Button)findViewById(R.id.btnSetPermission);
        hidePermissionHelp();
        btnSelect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String selectImages = "";
                final int len = thumbnailsselection.length;
                int cnt = 0;
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectImages = selectImages + arrPath[i] + "|";
                    }
                }
                if(cnt == 0)
                {
                    showAlert("Hãy Chọn Ít Nhất 1 Hình!");
                }
                else if (cnt > 5) {
                    showAlert("Chỉ Được Chọn Tối Đa 5 file!");
                } else {

                    Log.d("SelectedImages", selectImages);
                    Intent i = new Intent();
                    i.putExtra("data", selectImages);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });

        btnPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ConstantStuff.PERMISSION_GRANTED:
                    {
                        LoadImages();
                        hidePermissionHelp();
                        break;
                    }
                    case ConstantStuff.PERMISSION_DENIED:
                    {
                        showPermissionHelp();
                        break;
                    }
                    default:
                        super.handleMessage(msg);
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                LoadImages();
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);
        checkIfPermissionGranted();
    }

    private void checkIfPermissionGranted() {
        if (ContextCompat.checkSelfPermission(CustomPhotoGalleryActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }

        Message message = handler.obtainMessage();
        message.what = ConstantStuff.PERMISSION_GRANTED;
        message.sendToTarget();
    }

    private void showPermissionHelp()
    {
        btnPermission.setVisibility(View.VISIBLE);
        btnSelect.setVisibility(View.GONE);
    }

    private void hidePermissionHelp()
    {
        btnPermission.setVisibility(View.GONE);
        btnSelect.setVisibility(View.VISIBLE);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(CustomPhotoGalleryActivity.this,
                requiredPermissions,
                ConstantStuff.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
    }

    public void LoadImages()
    {
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;

        imagecursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        count = imagecursor.getCount();
        arrPath = new String[count];
        ids = new int[count];
        thumbnailsselection = new boolean[count];
        for (int i = 0; i < count; i++) {
            imagecursor.moveToPosition(i);
            ids[i] = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = imagecursor.getString(dataColumnIndex);
        }

        ImageAdapter imageAdapter = new ImageAdapter();
        grdImages.setAdapter(imageAdapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ConstantStuff.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            Message message = handler.obtainMessage();
            message.what = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ?
                    ConstantStuff.PERMISSION_GRANTED : ConstantStuff.PERMISSION_DENIED;
            message.sendToTarget();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(imagecursor != null)
        {
            imagecursor.close();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    private void setBitmap(final ImageView iv, final int id) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(),
                        id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.execute();
    }

    @SuppressLint("InflateParams")
    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.activity_upload_custom_gallery_item, null);
                holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
                holder.chkImage = (CheckBox) convertView.findViewById(R.id.chkImage);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.chkImage.setId(position);
            holder.imgThumb.setId(position);
            holder.chkImage.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });
            holder.imgThumb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.chkImage.getId();
                    if (thumbnailsselection[id]) {
                        holder.chkImage.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        holder.chkImage.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });
            try {
                setBitmap(holder.imgThumb, ids[position]);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            holder.chkImage.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imgThumb;
        CheckBox chkImage;
        int id;
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
}