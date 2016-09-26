package chau.bankingloan;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import chau.bankingloan.customThings.ConnectURL;
import chau.bankingloan.customThings.ServerBoldTextview;
import chau.bankingloan.customThings.ServerCheckbox;
import chau.bankingloan.customThings.ServerEditText;
import chau.bankingloan.customThings.ServerInfo;
import chau.bankingloan.customThings.ServerSpinner;
import chau.bankingloan.customThings.ServerTvDate;

/**
 * Created on 13-Jun-16 by com08.
 */
public class Tab1Fragment extends Fragment
{
    View rootView;
    LinearLayout lnrTab1;
    public String arrSpinner = "";
    public ArrayList<ServerInfo> arrayListTab1;
    ProgressDialog progressDialog;
    ImageButton imgBtnNext, imgBtnRefresh;
    SharedPreferences preferences;

    ServerEditText edResult;

    View.OnClickListener listenerNext, listenerRef;

    GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;

    static final Integer LOCATION = 0x1;
    static final Integer GPS_SETTINGS = 0x7;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_1, container, false);
        initWidget();
        initListener();

        statusCheck();

        preferences = this.getActivity().getSharedPreferences("TAB1", Context.MODE_APPEND);

        client = new GoogleApiClient.Builder(getActivity())
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();

        new GetData().execute();

        imgBtnNext.setOnClickListener(listenerNext);
        imgBtnRefresh.setOnClickListener(listenerRef);

        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);

        return rootView;
    }

    public void statusCheck()
    {
        final LocationManager manager = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, Please turn it on!")
                .setCancelable(false)
                .setPositiveButton("OK, I got it!", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getContext().startActivity(myIntent);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        statusCheck();
    }

    public void askForPermission(String permission, Integer requestCode)
    {
        if (ContextCompat.checkSelfPermission(this.getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), permission)) {

            //This is called if user has denied the permission before
            //In this case I am just asking the permission again
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{permission}, requestCode);

        } else {

            ActivityCompat.requestPermissions(this.getActivity(), new String[]{permission}, requestCode);
        }
    } else
        {
            Toast.makeText(this.getActivity(), "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this.getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 1:
                    askForGPS();
                    break;
            }
            Toast.makeText(this.getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this.getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void askForGPS(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }


    public void initWidget()
    {
        lnrTab1 = (LinearLayout)rootView.findViewById(R.id.lnrTab1);
        imgBtnNext = (ImageButton) rootView.findViewById(R.id.imgBtnNextTab1);
        imgBtnRefresh = (ImageButton) rootView.findViewById(R.id.imgBtnRefreshTab1);
        arrayListTab1 = new ArrayList<>();
    }

    public void initListener()
    {
        listenerNext = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckFields()) {
                    SaveData();
                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(1);
                }
                else
                {
                    android.support.v7.app.AlertDialog.Builder builder =
                            new android.support.v7.app.AlertDialog.Builder(getContext());
                    builder.setMessage("Please Fill In The Blank...");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }
        };

        listenerRef = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetData().execute();
            }
        };
    }

    private void SaveData()
    {
        try {
            int i;
            SharedPreferences.Editor editor = preferences.edit();
            Set<String> set = new HashSet<>();
            editor.clear().apply();
            for (i = 0; i < arrayListTab1.size(); i++) {
                if (!arrayListTab1.get(i).getType().equals("textviewColumn")) {
                    String fieldValue = (String) arrayListTab1.get(i).getData();
                    editor.putString(arrayListTab1.get(i).getLabel().trim().replace(" ", "").replace(":", ""), fieldValue);

                    set.add(arrayListTab1.get(i).jsonObject().toString());
                }
                if(arrayListTab1.get(i).getType().equals("edPlusResultA"))
                {
                    editor.putString(arrayListTab1.get(i).getLabel().trim().replace(" ", "").replace(":", ""), edResult.getValue());
                }
            }
            editor.putStringSet("tab1", set);
            editor.apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean CheckFields()
    {
        try
        {
            int i;
            int t = 0;
            boolean good = true;
            for (i=0; i< arrayListTab1.size(); i++) {
                String fieldValue = (String) arrayListTab1.get(i).getData();
                if (arrayListTab1.get(i).isRequired()) {
                    if (fieldValue == null) {
                        good = false;
                    } else {
                        if (fieldValue.trim().length() == 0) {
                            good = false;
                        }
                    }
                }
                if(arrayListTab1.get(i).getType().equals("edPlusNumberA"))
                {
                    t = t+Integer.valueOf((String)arrayListTab1.get(i).getData());
                }
            }
            if(t > 0) {
                Log.e("NUMBER", String.valueOf(t));
                edResult.setValue(String.valueOf(t));
//                arrayListTab1.get(3).setData("a");
            }
            return good;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }

    private class GetData extends AsyncTask<Void, Void, Void>
    {
        String jsonMain;
        JSONObject object;
        JSONArray array;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Getting Data...");
            progressDialog.setCancelable(false);
//            progressDialog.show();
            lnrTab1.removeAllViews();
            arrayListTab1.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectURL connectURL = new ConnectURL();
            jsonMain = connectURL.makeServiceCall(MainActivity.TAB_1_LINK, ConnectURL.GET);
            if(jsonMain != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonMain);
                    array = jsonObject.getJSONArray("tab1");
                    for(int i = 0; i < array.length(); i++) {
                        object = (JSONObject) array.get(i);
                        ServerInfo serverInfo = new ServerInfo(object.getString("label"),
                                object.getString("type"), object.getString("value"),
                                object.getString("url"),
                                object.getString("column"), object.getBoolean("require"));
                        arrayListTab1.add(serverInfo);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            DisplayForm();
        }

        public void DisplayForm()
        {
            try
            {
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(15,15,15,15);

                LinearLayout.LayoutParams layoutParams1 =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                LinearLayout l1,l2;
                l1 = new LinearLayout(getContext());
                l1.setOrientation(LinearLayout.VERTICAL);
                l1.setLayoutParams(layoutParams1);
                l2 = new LinearLayout(getContext());
                l2.setOrientation(LinearLayout.VERTICAL);
                l2.setLayoutParams(layoutParams1);

                for (int i = 0; i < arrayListTab1.size(); i++)
                {
                    if(arrayListTab1.get(i).getType().equals("textviewColumn"))
                    {
                        if(arrayListTab1.get(i).getColumn().equals("1")) {
                            arrayListTab1.get(i).obj = new ServerBoldTextview(getContext(), arrayListTab1.get(i).getLabel(), true);
                            l1.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                        if(arrayListTab1.get(i).getColumn().equals("2")){
                            arrayListTab1.get(i).obj = new ServerBoldTextview(getContext(), arrayListTab1.get(i).getLabel(), true);
                            l2.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab1.get(i).getType().equals("spinner"))
                    {
                        arrSpinner = new SpinnerData(arrayListTab1.get(i).getUrl(), arrayListTab1.get(i).getLabel()).execute().get();
                        if(arrayListTab1.get(i).getColumn().equals("1")) {
                            if(arrSpinner.equals(""))
                            {
                                arrayListTab1.get(i).obj = new ServerSpinner(getContext(),
                                        arrayListTab1.get(i).getLabel()
                                        , arrayListTab1.get(i).getValue());
                                Toast.makeText(getContext(), "Can not get " + arrayListTab1.get(i).getLabel().replace(":", "") + " from Server!", Toast.LENGTH_SHORT).show();
                            }
                            else
                                arrayListTab1.get(i).obj = new ServerSpinner(getContext(),
                                        arrayListTab1.get(i).getLabel(), arrSpinner);
                            Log.e("DISPLAY", arrayListTab1.get(i).getLabel());
                            Log.e("DISPLAY", arrSpinner);
                            l1.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                        if(arrayListTab1.get(i).getColumn().equals("2")){
                            if(arrSpinner.equals(""))
                            {
                                arrayListTab1.get(i).obj = new ServerSpinner(getContext(),
                                        arrayListTab1.get(i).getLabel()
                                        , arrayListTab1.get(i).getValue());
                                Toast.makeText(getContext(), "Can not get " + arrayListTab1.get(i).getLabel().replace(":", "") + " from Server!", Toast.LENGTH_SHORT).show();
                            }
                            else
                                arrayListTab1.get(i).obj = new ServerSpinner(getContext(),
                                        arrayListTab1.get(i).getLabel(), arrSpinner);
                            Log.e("DISPLAY", arrayListTab1.get(i).getLabel());
                            Log.e("DISPLAY", arrSpinner);
                            l2.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab1.get(i).getType().equals("edittext"))
                    {
                        if(arrayListTab1.get(i).getColumn().equals("1")) {
                            arrayListTab1.get(i).obj = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l1.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                        if(arrayListTab1.get(i).getColumn().equals("2")){
                            arrayListTab1.get(i).obj = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l2.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab1.get(i).getType().equals("edittextnumber"))
                    {
                        if(arrayListTab1.get(i).getColumn().equals("1")) {
                            arrayListTab1.get(i).obj = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            l1.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                        if(arrayListTab1.get(i).getColumn().equals("2")){
                            arrayListTab1.get(i).obj = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            l2.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab1.get(i).getType().equals("edittextemail"))
                    {
                        if(arrayListTab1.get(i).getColumn().equals("1")) {
                            arrayListTab1.get(i).obj = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            l1.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                        if(arrayListTab1.get(i).getColumn().equals("2")){
                            arrayListTab1.get(i).obj = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            l2.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab1.get(i).getType().equals("textviewDate"))
                    {
                        if(arrayListTab1.get(i).getColumn().equals("1")){
                            arrayListTab1.get(i).obj = new ServerTvDate(getContext(), arrayListTab1.get(i).getLabel(), "Choose Date");
                            l1.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                        if(arrayListTab1.get(i).getColumn().equals("2")){
                            arrayListTab1.get(i).obj = new ServerTvDate(getContext(), arrayListTab1.get(i).getLabel(), "Choose Date");
                            l2.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab1.get(i).getType().equals("checkbox"))
                    {
                        if(arrayListTab1.get(i).getColumn().equals("1")){
                            arrayListTab1.get(i).obj = new ServerCheckbox(getContext(), arrayListTab1.get(i).getLabel());
                            l1.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                        if(arrayListTab1.get(i).getColumn().equals("2")){
                            arrayListTab1.get(i).obj = new ServerCheckbox(getContext(), arrayListTab1.get(i).getLabel());
                            l2.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab1.get(i).getType().equals("edPlusNumberA")) {
                        if(arrayListTab1.get(i).getColumn().equals("1")) {
                            arrayListTab1.get(i).obj = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            l1.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                        if(arrayListTab1.get(i).getColumn().equals("2")){
                            arrayListTab1.get(i).obj = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                            l2.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab1.get(i).getType().equals("edPlusResultA")) {
                        if(arrayListTab1.get(i).getColumn().equals("1")) {
                            edResult = new ServerEditText(getContext(), "10",
                                    InputType.TYPE_CLASS_NUMBER
                                            | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        }
                        if(arrayListTab1.get(i).getColumn().equals("2")){
                            edResult = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(),
                                    InputType.TYPE_CLASS_NUMBER
                                            | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            edResult.setEnabled(false);
                            l2.addView(edResult, layoutParams);
                        }
                    }
                }
                lnrTab1.addView(l1);
                lnrTab1.addView(l2);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }



    public class SpinnerData extends AsyncTask<Void, Void, String> {
        String url;
        String key;
        String arr;
        JSONArray array;
        JSONObject object;
        String jsonSpinner;

        public SpinnerData(String url, String key) {
            this.url = url;
            this.key = key;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arr = "";
        }

        @Override
        protected String doInBackground(Void... strings) {
            ConnectURL connectURL = new ConnectURL();
            if(url.isEmpty())
            {
                arr = "";
            }
            else {
                jsonSpinner = connectURL.makeServiceCall(url, ConnectURL.GET);
                if (jsonSpinner != null) {
                    try {
                        object = new JSONObject(jsonSpinner);
                        array = object.getJSONArray(key.trim().replace(":", "")
                                .replace(" ", ""));
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = (JSONObject) array.get(i);
                            arr += jsonObject.getString("DATA") + ",";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else arr = "";
            }
            return arr;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            Log.e("EXECUTION", key);
            Log.e("EXECUTION", aVoid);
        }
    }
}
