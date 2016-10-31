package chau.bankingloan;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import chau.bankingloan.customThings.ConnectURL;
import chau.bankingloan.customThings.ServerBoldTextview;
import chau.bankingloan.customThings.ServerCheckbox;
import chau.bankingloan.customThings.ServerEditText;
import chau.bankingloan.customThings.ServerInfo;
import chau.bankingloan.customThings.ServerSpinner;
import chau.bankingloan.customThings.ServerTvDate;
import chau.bankingloan.customThings.SpinnerData;

/**
 * Created on 13-Jun-16 by com08.
 */
public class Tab1Fragment extends Fragment
{
    View rootView;
    LinearLayout lnrTab1;
    public String arrSpinner = "";
    public ArrayList<ServerInfo> arrayListTab1;
    ImageButton imgBtnNext, imgBtnRefresh;
    SharedPreferences preferences;

    ServerEditText edResult;

    View.OnClickListener listenerNext, listenerRef;

    AlertDialog alert;

    GoogleApiClient client;
//    LocationRequest mLocationRequest;
//    PendingResult<LocationSettingsResult> result;
    TextWatcher textWatcher;

//    static final Integer GPS_SETTINGS = 0x7;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_1, container, false);

        if(!hasPermissions(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
        hasDeniedPermissions(getActivity(), getContext(), PERMISSIONS);

        initWidget();
        initListener();

        final LocationManager manager = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        preferences = this.getActivity().getSharedPreferences("TAB1", Context.MODE_APPEND);

        client = new GoogleApiClient.Builder(getActivity())
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();

        new GetData().execute();

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int t = 0;
                try {
                    for (int j = 0; j < arrayListTab1.size(); j++) {
                        if(arrayListTab1.get(j).getType().equals("edPlusNumberA"))
                        {
                            if(!arrayListTab1.get(j).getData().toString().trim().isEmpty())
                                t = t + Integer.valueOf(arrayListTab1.get(j).getData()
                                        .toString().trim());
                        }
                    }
                    if(t == 0)
                        edResult.setValue(String.valueOf(0));
                    else
                        edResult.setValue(String.valueOf(t));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        imgBtnNext.setOnClickListener(listenerNext);
        imgBtnRefresh.setOnClickListener(listenerRef);

//        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);

        return rootView;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, please turn it on!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void hasDeniedPermissions(Activity activity, Context context, String... permissions)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                        ActivityCompat.requestPermissions(getActivity(), new String[] {permission}, PERMISSION_ALL);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alert != null)
        {
            alert.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ALL) {
            // for each permission check if the user grantet/denied them
            // you may want to group the rationale in a single dialog,
            // this is just an example
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = shouldShowRequestPermissionRationale( permission );
                    if (showRationale) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            for (String perm : PERMISSIONS) {
                                ActivityCompat.requestPermissions(getActivity(), new String[] {perm}, PERMISSION_ALL);
                            }
                        }
                    }
                }
            }
        }
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
            editor.clear().apply();
            for (i = 0; i < arrayListTab1.size(); i++) {
                if (!arrayListTab1.get(i).getType().equals("textviewColumn")) {
                    String fieldValue = (String) arrayListTab1.get(i).getData();
                    editor.putString(arrayListTab1.get(i).getLabel().trim().replace(" ", "").replace(":", ""), fieldValue);
                }
                if(arrayListTab1.get(i).getType().equals("edPlusResultA"))
                {
                    editor.putString(arrayListTab1.get(i).getLabel().trim().replace(" ", "").replace(":", ""), edResult.getValue());
                }
            }
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
        ProgressDialog progressDialog;

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

        void DisplayForm()
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
                        SpinnerData spinnerData = new SpinnerData(arrayListTab1.get(i).getUrl(), arrayListTab1.get(i).getLabel());
                        arrSpinner = spinnerData.execute().get();
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
                            arrayListTab1.get(i).obj = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(),
                                    InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, textWatcher);
                            l1.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                        if(arrayListTab1.get(i).getColumn().equals("2")){
                            arrayListTab1.get(i).obj = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(),
                                    InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, textWatcher);
                            l2.addView((View) arrayListTab1.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab1.get(i).getType().equals("edPlusResultA")) {
                        if(arrayListTab1.get(i).getColumn().equals("1")) {
                            edResult = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(),
                                    InputType.TYPE_CLASS_NUMBER
                                            | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            edResult.setEnabled(false);
                            edResult.setValue(String.valueOf(0));
                            l1.addView(edResult, layoutParams);
                        }
                        if(arrayListTab1.get(i).getColumn().equals("2")){
                            edResult = new ServerEditText(getContext(), arrayListTab1.get(i).getLabel(),
                                    InputType.TYPE_CLASS_NUMBER
                                            | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            edResult.setEnabled(false);
                            edResult.setValue(String.valueOf(0));
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
}
