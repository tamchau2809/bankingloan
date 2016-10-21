package chau.bankingloan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import chau.bankingloan.customThings.ConstantStuff;
import chau.bankingloan.customThings.JustifyTextView;
import chau.bankingloan.customThings.UploadFile;

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
    double latitude;
    double longitude;

    View rootView;
    CheckBox cbAccept;
    ImageButton imgBtnPreTab6, imgBtnNextTab6;
    SharedPreferences personalPreferences, tab3,
            tab4, tab1, tab5;

    ProgressDialog progressDialog;
    String img_url = "";

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_confirm, container, false);

        initWidget();
        personalPreferences = this.getActivity().getSharedPreferences("PERSONAL", Context.MODE_APPEND);
//        loadFromPersonal(personalPreferences);
        tab3 = this.getActivity().getSharedPreferences("TAB3", Context.MODE_APPEND);
//        loadFromContact(preferences);
        tab4 = this.getActivity().getSharedPreferences("TAB4", Context.MODE_APPEND);
//        loadFromEmployment(tab4);
        tab1 = this.getActivity().getSharedPreferences("TAB1", Context.MODE_APPEND);
//        loanAmount = tab1.getString("LoanAmount", "");
        getData("TAB1");
        tab5 = this.getActivity().getSharedPreferences("TAB5", Context.MODE_APPEND);
        Log.e("asd", tab5.getAll().toString());
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
//        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

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
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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
        LocationRequest mLocationRequest = LocationRequest.create()
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

    public void getData(String tab)
    {
        SharedPreferences pref = this.getActivity().getSharedPreferences(tab, Context.MODE_APPEND);
        loanAmount = pref.getString("LoanAmount", "");
        lastPayment = pref.getString("LastPayment", "");
        maxInterest = pref.getString("MaxInterest", "");
        loanPurpose = pref.getString("LoanPurpose", "");
        loanType = pref.getString("LoanType", "");
        monthlyPayment = pref.getString("MonthlyPayment", "");
        tenure = pref.getString("Tenure", "");
    }

    class SendInfo extends AsyncTask<Void, Void, String>
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
            try {
                return uploadData(ConstantStuff.FILE_UPLOAD_URL);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        private String uploadData(String requestURL)
        {
            String charset = "UTF-8";
            String result = "";
            try
            {
                UploadFile multipart = new UploadFile(requestURL, charset);

                multipart.addHeaderField("User-Agent", "CodeJava");
                multipart.addHeaderField("Test-Header", "Header-Value");

                multipart.addFormField("LOAN_TYPE", (tab1.getString("LoanType","")));
                multipart.addFormField("TENURE", (tab1.getString("Tenure","")));
                multipart.addFormField("LOAN_PURPOSE", (tab1.getString("LoanPurpose","")));
                multipart.addFormField("INDUSTRY", (tab4.getString("Industry","")));
                multipart.addFormField("IMG_URL", (tab5.getString("IMG_URL","")));
                multipart.addFormField("LATITUDE", String.valueOf(latitude));
                multipart.addFormField("LONGITUDE", String.valueOf(longitude));

                List<String> response = multipart.finish();

                System.out.println("SERVER REPLIED:");

                for (String line : response) {
                    System.out.println(line);
                    result = line;
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            return result;
        }



        @Override
        protected void onPostExecute(String s) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }

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
            try {
                return uploadData(ConstantStuff.PIN_GENERATE);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        private String uploadData(String requestURL)
        {
            String charset = "UTF-8";
            String result = "";
            try
            {
                UploadFile multipart = new UploadFile(requestURL, charset);

                multipart.addHeaderField("User-Agent", "CodeJava");
                multipart.addHeaderField("Test-Header", "Header-Value");

                multipart.addFormField("EMAIL", tab3.getString("Email",""));

                List<String> response = multipart.finish();

                System.out.println("SERVER REPLIED:");

                for (String line : response) {
                    System.out.println(line);
                    result = line;
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            return result;
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
