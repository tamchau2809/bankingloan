package chau.bankingloan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UploadActivity extends Activity implements View.OnClickListener
{
	public static String NAME = "name";
	public static String CONTRACT = "contract";
	public static String CMND = "cmnd";
	public static String IMAGE = "contractImg";
	
    private LinearLayout lnrImages;
//    final String FILE_UPLOAD_URL = "http://tamchau.somee.com/up.php";
//    final String FILE_STORE_URL = "http://tamchau.somee.com/uploads";
//    final String FILE_UPLOAD_URL = "http://chauvu.96.lt/up.php";
//    final String FILE_STORE_URL = "http://chauvu.96.lt/uploads";
//    final String FILE_UPLOAD_URL = "http://chauvu.comlu.com/up.php";
//    final String FILE_STORE_URL = "http://chauvu.comlu.com/uploads";
	  final String FILE_UPLOAD_URL = "http://192.168.1.25/chauvu/up.php";
	  final String FILE_STORE_URL = "http://192.168.1.25/chauvu/uploads";
    private Button btnAddPhots;
    private Button btnUpload;
    private ArrayList<String> imagesPathList;
    ProgressBar prgPercent;
    TextView tvPercent;
    private Bitmap yourbitmap;
    private final int PICK_IMAGE_MULTIPLE =1;
    String MAKH;
    String contractNum;
    String MANV;
    File sourceFile;
    
    UploadFile a;
    
    long totalSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_upload);
        initWiget();
        
        Intent intent = getIntent();
        MAKH = intent.getStringExtra("MAKH").toString().trim().replace(" ", "");
		contractNum = intent.getStringExtra("NUM").toString().trim().replace(" ", "");
		MANV = intent.getStringExtra("MANV").toString().trim().replace(" ", "");
        
        btnAddPhots.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        
        ContractInfo info = new ContractInfo(MAKH, MANV, contractNum, null, null);
        JSONObject a = createJSONFile(info);
        Toast.makeText(getApplicationContext(), a.toString(), Toast.LENGTH_LONG).show();
    }
    
    public void initWiget()
    {
    	lnrImages = (LinearLayout)findViewById(R.id.lnrImages);
        btnAddPhots = (Button)findViewById(R.id.btnAddPhots);
        btnUpload = (Button)findViewById(R.id.btnUpload);
        tvPercent = (TextView)findViewById(R.id.tvPercent);
		prgPercent = (ProgressBar)findViewById(R.id.progressBar1);
    }
    public void showProgress(final boolean show, float value) 
    {    	
    	prgPercent.setVisibility(show ? View.VISIBLE : View.GONE);
		tvPercent.setVisibility(show ? View.VISIBLE : View.GONE);
		btnAddPhots.setEnabled(show ? false : true);
		btnUpload.setEnabled(show ? false : true);
		lnrImages.setEnabled(show ? false : true);
		
		prgPercent.getIndeterminateDrawable().setColorFilter(0xFF008800,
				android.graphics.PorterDuff.Mode.MULTIPLY);
//		prgPercent.setProgress((int)value);
		
		tvPercent.setText(String.valueOf((int)value) + "%");
	}
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddPhots:
                Intent intent = new Intent(UploadActivity.this, CustomPhotoGalleryActivity.class);
                startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
                break;
            case R.id.btnUpload:
            	if(imagesPathList == null)
				{
					showAlert("Không Có Hình Ảnh Để Upload!");
				}
				else
				{
					new UploadFile().execute();
//					a = new UploadFile();
//					a.execute();
				}
                break;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_MULTIPLE){
                imagesPathList = new ArrayList<String>();
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
                    yourbitmap = BitmapFactory.decodeFile(imagesPath[i], opt);
                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(yourbitmap);
                    imageView.setAdjustViewBounds(true);
                    lnrImages.addView(imageView);
                }
            }
        }
    }
    
//    private class UploadFile extends AsyncTask<Void, Integer, String>
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
//				UploadProgress progress = new UploadProgress(
//						new ProgressListener() 
//						{
//							@Override
//							public void transferred(long num) 
//							{
//								// TODO Auto-generated method stub
//								publishProgress((int) ((num / (float) totalSize) * 100));
//							}
//						});
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
					sourceFile = saveImage(bitmap, String.valueOf(x), getApplicationContext());
					builder.addPart("image" + x, new FileBody(sourceFile));
					String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss",
			                Locale.getDefault()).format(new Date());
					builder.addPart("cus_time", new StringBody(timeStamp, ContentType.TEXT_PLAIN));
					
//					progress.addPart("image" + x, new FileBody(sourceFile));					
//					progress.addPart("cus_time", new StringBody(timeStamp));
				}

				builder.addPart("MAKH", new StringBody(MAKH, ContentType.TEXT_PLAIN));
				builder.addPart("cus_number", new StringBody(contractNum, ContentType.TEXT_PLAIN));
				builder.addPart("MANV", new StringBody(MANV, ContentType.TEXT_PLAIN));
				
//				progress.addPart("cus_name", new StringBody(name));
//				progress.addPart("cus_number", new StringBody(num));				
//				totalSize = progress.getContentLength();
								
				
//				StringEntity a = new StringEntity("a");
//				String json = "";
//				JSONObject customer = new JSONObject();
//				customer.accumulate("MKH", name);
//				customer.accumulate("CMND", FILE_STORE_URL);
//				customer.accumulate("HD", FILE_STORE_URL);
				
				httpPost.setEntity(new UploadProgress2(builder.build(), lis));
				
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();

//				JSONObject customer = new JSONObject();
//				customer = new JSONObject(EntityUtils.toString(httpEntity));
//				Log.e("Chau", customer.toString());
				
				
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				if(statusCode == 200)
				{
					response = EntityUtils.toString(httpEntity);
				}
				else
				{
					response = "Error: + " + statusCode;
				}
			}
			catch (ClientProtocolException e) {
				response = e.toString();
			} 
			catch (IOException e) {
				response = e.toString();
			}
			catch(Exception e)
			{}
			return response;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			File myDir=new File( Environment.getExternalStorageDirectory(), getApplicationContext().getPackageName());
	        if(!myDir.exists()){  
	            myDir.mkdir();  
	        }
			if(result.equals("111") || result.equals("11") || result.equals("1"))
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
    
    public File saveImage(Bitmap myBitmap, String name, Context context) {

        File myDir=new File( Environment.getExternalStorageDirectory(), context.getPackageName());
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
    
    public String decodeImage(Bitmap myBitmap, Context context)
    {
    	return "a";
    }
    
    /**
	 * Method to show alert dialog
	 * */
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
	
	public JSONObject createJSONFile(ContractInfo customer)
	{
		JSONObject jsonObj = new JSONObject();
		try
		{
			jsonObj.put(NAME, customer.getKH());
			jsonObj.put(CONTRACT, customer.getContractNum());
		}
		catch(JSONException e)
		{}
		return jsonObj;
	}
}
