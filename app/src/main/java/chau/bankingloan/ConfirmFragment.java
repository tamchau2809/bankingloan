package chau.bankingloan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import chau.bankingloan.customThings.ConstantStuff;
import chau.bankingloan.customThings.JustifyTextView;

/**
 * Created on 04-May-16 by com08.
 */
public class ConfirmFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private static String PIN_SERVER = null;
    List<String> test;
    String loanAmount, lastPayment, maxInterest, loanPurpose,
            loanType, monthlyPayment, tenure;

    JustifyTextView jtvCondition;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    double latitude;
    double longitude;

    View rootView;
    CheckBox cbAccept;
    ImageButton imgBtnPreTab6, imgBtnNextTab6;
    SharedPreferences personalPreferences, contactPreferences,
            employmentPreferences, tab1;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_confirm, container, false);

        initWidget();
        personalPreferences = this.getActivity().getSharedPreferences("PERSONAL", Context.MODE_APPEND);
//        loadFromPersonal(personalPreferences);
        contactPreferences = this.getActivity().getSharedPreferences("CONTACT", Context.MODE_APPEND);
//        loadFromContact(contactPreferences);
        employmentPreferences = this.getActivity().getSharedPreferences("EMPLOYMENT", Context.MODE_APPEND);
//        loadFromEmployment(employmentPreferences);
        tab1 = this.getActivity().getSharedPreferences("TAB1", Context.MODE_APPEND);
//        loanAmount = tab1.getString("LoanAmount", "");
        getData(tab1, "TAB1");
        Log.e("LoanAmount", loanAmount);
        Log.e("LastPayment", lastPayment);
        Log.e("MaxInterest", maxInterest);
        Log.e("Tenure", tenure);
        Log.e("LoanPurpose", loanPurpose);
        Log.e("MonthlyPayment", monthlyPayment);
        Log.e("LoanType", loanType);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        cbAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbAccept.setError(null);
                } else {
                    cbAccept.setError("Please Read The Terms and Conditions!");
                }
            }
        });

        imgBtnNextTab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cbAccept.isChecked()) {
                    cbAccept.setError("Please Read The Terms and Conditions!");
                } else {
                    showDialog();
                    new PINCreate().execute();
                }
            }
        });

        imgBtnPreTab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity) getActivity();
                act.switchTab(4);
            }
        });

        SharedPreferences tezt = this.getActivity().getSharedPreferences("TAB1", Context.MODE_APPEND);
        test = loadData(tezt, "tab1");
        Log.e("TEST", test.toString());
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();

            Log.e("GPS", latitude + "," + longitude);
        }
    }

    protected void startLocationUpdates() {
        int UPDATE_INTERVAL = 30*1000;
        int FASTEST_INTERVAL = 5000;
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        int DISPLACEMENT = 5;
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("GPS", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    public void loadFromPersonal(SharedPreferences personal)
//    {
//        tvConfirmName.setText(personal.getString("name", ""));
//        tvConfirmDoB.setText(personal.getString("birthday", ""));
//        tvConfirmId.setText(personal.getString("identityNum", ""));
//    }
//
//    @SuppressLint("SetTextI18n")
//    public void loadFromContact(SharedPreferences contact)
//    {
//        tvConfirmTelephone.setText(contact.getString("telephone", ""));
//        tvConfirmMobile.setText(contact.getString("mobile", ""));
//        tvConfirmEmail.setText(contact.getString("email", ""));
//        tvConfirmAdd.setText(contact.getString("street", "") + ", "
//                + contact.getString("city", ""));
//    }
//
//    public void loadFromEmployment(SharedPreferences employment)
//    {
//        tvConfirmWorkingStt.setText(employment.getString("workingStt", ""));
//        tvConfirmEmployer.setText(employment.getString("employer", ""));
//        tvConfirmEmployerAdd.setText(employment.getString("employerAdd", ""));
//    }

    public void showDialog()
    {
        LayoutInflater factory = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") final View alertDialogView = factory.inflate(R.layout.otp_dialog, null);
        final EditText edPIN = (EditText) alertDialogView.findViewById(R.id.edConfirmString);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.NUMBER_YOU_VE_BEEN_RECEIVED))
                .setView(alertDialogView)
                .setPositiveButton(
                        android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        View btnTest = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edPIN.getText().toString().equals(PIN_SERVER))
                {
                    new SendInfo().execute();
                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(6);
                    alertDialog.dismiss();
                }
                else
                {
                    edPIN.setError("Please Check Your PIN again!");
                    edPIN.requestFocus();
                }
            }
        });
    }

    public List<String> loadData(SharedPreferences tezt, String key)
    {
        List<String> items = new ArrayList<>();
        Set<String> set = tezt.getStringSet(key, null);
        if (set != null) {
            for(String s : set)
            {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String name = jsonObject.getString("value");
                    items.add(name);
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return items;
    }

    public void initWidget()
    {
        cbAccept = (CheckBox)rootView.findViewById(R.id.cbAcceptConfirm);
        imgBtnPreTab6 = (ImageButton) rootView.findViewById(R.id.imgBtnPreTab6);
//        fabConfirmPre.setEnabled(false);
        imgBtnNextTab6 = (ImageButton) rootView.findViewById(R.id.imgBtnNextTab6);
//        fabConfirmNext.setEnabled(false);
        jtvCondition = (JustifyTextView)rootView.findViewById(R.id.jtvConditionConfirm);
    }

    public void getData(SharedPreferences pref, String tab)
    {
        pref = this.getActivity().getSharedPreferences(tab, Context.MODE_APPEND);
        loanAmount = pref.getString("LoanAmount", "");
        lastPayment = pref.getString("LastPayment", "");
        maxInterest = pref.getString("MaxInterest", "");
        loanPurpose = pref.getString("LoanPurpose", "");
        loanType = pref.getString("LoanType", "");
        monthlyPayment = pref.getString("MonthlyPayment", "");
        tenure = pref.getString("Tenure", "");
    }

    @SuppressWarnings("deprecation")
    private class SendInfo extends AsyncTask<Void, Float, String>
    {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            HttpClient httpClient = new DefaultHttpClient();
            HttpParams test = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(test, 5000);
            HttpConnectionParams.setSoTimeout(test, 5000);
            HttpPost httpPost = new HttpPost(ConstantStuff.URL_UPLOAD);
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

                builder.addPart("LOAN_TYPE", new StringBody(tab1.getString("LoanType",""), ContentType.TEXT_PLAIN));
                builder.addPart("TENURE", new StringBody(tab1.getString("Tenure",""), ContentType.TEXT_PLAIN));
                builder.addPart("LOAN_PURPOSE", new StringBody(tab1.getString("LoanPurpose",""), ContentType.TEXT_PLAIN));
                builder.addPart("LATITUDE", new StringBody(String.valueOf(latitude), ContentType.TEXT_PLAIN));
                builder.addPart("LONGITUDE", new StringBody(String.valueOf(longitude), ContentType.TEXT_PLAIN));

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
        protected void onPostExecute(String s) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }

    @SuppressWarnings("deprecation")
    private class PINCreate extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            HttpClient httpClient = new DefaultHttpClient();
            HttpParams test = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(test, 5000);
            HttpConnectionParams.setSoTimeout(test, 5000);
            HttpPost httpPost = new HttpPost(ConstantStuff.PIN_GENERATE);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            try {
                String email = contactPreferences.getString("email","");
                builder.addPart("EMAIL", new StringBody(email, ContentType.TEXT_PLAIN));
                httpPost.setEntity(builder.build());

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
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            PIN_SERVER = s;
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
        }
    }
}
