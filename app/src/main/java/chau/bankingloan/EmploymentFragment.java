package chau.bankingloan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created on 25-Apr-16 by com08.
 */
public class EmploymentFragment extends Fragment implements View.OnClickListener{
    View rootView;

    ProgressDialog pDialog;
    final String GET_DATA = "http://192.168.1.17/chauvu/loanData.php";

    SharedPreferences employment, spinnerStorage;

    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    TextView tvDateJoined;
    Spinner spWorkingStt, spCompanyType, spIndustry;
    EditText edEmployer, edDesignation, edSalaryIncome, edEmployerAdd, edOtherIncome, edTotalIncome, edEmployerContact;

    ArrayList<InfoFromServer> arrWorkingStt = new ArrayList<>();
    ArrayList<InfoFromServer> arrCompanyType = new ArrayList<>();
    ArrayList<InfoFromServer> arrIndustry = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_employment, container, false);

        initWidget();
        spinnerStorage = this.getActivity().getSharedPreferences("SPINNER", Context.MODE_APPEND);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        employment = this.getActivity().getSharedPreferences("EMPLOYMENT", Context.MODE_APPEND);
        loadFromSharedPreference(employment);

        FloatingActionButton fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabEmployNext);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edEmployer.getText().toString().length() == 0)
                {
                    edEmployer.setError("Please Enter Employer's Name!");
                    edEmployer.requestFocus();
                }
                else if (edEmployerAdd.getText().toString().length() == 0)
                {
                    edEmployerAdd.setError("Please Enter Employer's Address!");
                    edEmployerAdd.requestFocus();
                }
                else if (edDesignation.getText().toString().length() == 0)
                {
                    edDesignation.setError("Please Enter Designation!");
                    edDesignation.requestFocus();
                }
                else if (edSalaryIncome.getText().toString().length() == 0)
                {
                    edSalaryIncome.setError("Please Enter Salary Income!");
                    edSalaryIncome.requestFocus();
                }
                else if (edOtherIncome.getText().toString().length() == 0)
                {
                    edOtherIncome.setError("Please Enter Other Income!");
                    edOtherIncome.requestFocus();
                }
                else if (edTotalIncome.getText().toString().length() == 0)
                {
                    edTotalIncome.setError("Please Enter Salary Income!");
                    edSalaryIncome.requestFocus();
                }
                else {
                    SharedPreferences.Editor editor = employment.edit();
                    editor.putInt("workingSttLoca", spWorkingStt.getSelectedItemPosition());
                    editor.putInt("companyType", spCompanyType.getSelectedItemPosition());
                    editor.putInt("industry", spIndustry.getSelectedItemPosition());
                    editor.putString("employer", edEmployer.getText().toString());
                    editor.putString("employerAdd", edEmployerAdd.getText().toString());
                    editor.putString("dateJoined", tvDateJoined.getText().toString());
                    editor.putString("designation", edDesignation.getText().toString());
                    editor.putString("salaryIncome", edSalaryIncome.getText().toString());
                    editor.putString("otherIncome", edOtherIncome.getText().toString());
                    editor.putString("totalIncome", edTotalIncome.getText().toString());
                    editor.putString("employerContact", edEmployerContact.getText().toString());
                    editor.putString("workingStt", spWorkingStt.getSelectedItem().toString());
                    editor.apply();

                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(4);
                }
            }
        });

        if(!spinnerStorage.contains("WorkingStt") ||
                !spinnerStorage.contains("CompanyType") ||
                !spinnerStorage.contains("Industry"))
        {
            if(isConnectedToInternet(getContext()))
            {
                new getDataForSpinner().execute();
            }
            else {
                showAlert("Xin Kiểm Tra Lại Kết Nối!");
            }
        }
        else {
            populateSpinner(spWorkingStt, "WorkingStt");
            populateSpinner(spCompanyType, "CompanyType");
            populateSpinner(spIndustry, "Industry");
        }

        FloatingActionButton fabRefresh = (FloatingActionButton)rootView.findViewById(R.id.fabEmployRefresh);
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectedToInternet(getContext())) {
                    new getDataForSpinner().execute();
                }
                else
                {
                    showAlert("Xin Kiểm Tra Lại Kết Nối!");
                }
            }
        });

        FloatingActionButton fabPre = (FloatingActionButton)rootView.findViewById(R.id.fabEmployPre);
        fabPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity)getActivity();
                act.switchTab(2);
            }
        });
        SetDateTime();
        return rootView;
    }

    public void loadFromSharedPreference(SharedPreferences test)
    {
        if(test.contains("employer"))
        {
            edEmployer.setText(test.getString("employer", ""));
            edEmployerAdd.setText(test.getString("employerAdd", ""));
            tvDateJoined.setText(test.getString("dateJoined", ""));
            edDesignation.setText(test.getString("designation", ""));
            edSalaryIncome.setText(test.getString("salaryIncome", ""));
            edOtherIncome.setText(test.getString("otherIncome", ""));
            edTotalIncome.setText(test.getString("totalIncome", ""));
            edEmployerContact.setText(test.getString("employerContact", ""));
            spWorkingStt.setSelection(test.getInt("workingSttLoca", 0));
            spCompanyType.setSelection(test.getInt("companyType", 0));
            spIndustry.setSelection(test.getInt("industry", 0));
        }
    }

    public void initWidget()
    {
        spWorkingStt = (Spinner)rootView.findViewById(R.id.spWorkingStt);
        spCompanyType = (Spinner)rootView.findViewById(R.id.spCompanyType);
        spIndustry = (Spinner)rootView.findViewById(R.id.spIndustry);

        edDesignation = (EditText)rootView.findViewById(R.id.edDesignation);
        edEmployer = (EditText)rootView.findViewById(R.id.edEmployer);
        edEmployerAdd = (EditText)rootView.findViewById(R.id.edEmployerAdd);
        edEmployerContact = (EditText)rootView.findViewById(R.id.edEmployerContact);
        edOtherIncome = (EditText) rootView.findViewById(R.id.edOtherIncome);
        edSalaryIncome = (EditText)rootView.findViewById(R.id.edSalaryIncome);
        edTotalIncome = (EditText)rootView.findViewById(R.id.edTotalIncome);

        tvDateJoined = (TextView)rootView.findViewById(R.id.tvDateJonied);
    }

    private void SetDateTime()
    {
        tvDateJoined.setOnClickListener(this);
        Calendar calendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvDateJoined.setText(dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == tvDateJoined) {
            mDatePickerDialog.show();
        }
    }

    public void storeSpinnerData(ArrayList<InfoFromServer> list, String key)
    {
        SharedPreferences.Editor editor = spinnerStorage.edit();
        Set<String> set = new HashSet<>();
        for(int i = 0; i < list.size(); i++)
        {
            set.add(list.get(i).getJSONInfo().toString());
        }
        editor.putStringSet(key, set);
        editor.apply();
    }

    public ArrayList<InfoFromServer> loadFromSharedPreferences(String key)
    {
        ArrayList<InfoFromServer> items = new ArrayList<>();
        Set<String> set = spinnerStorage.getStringSet(key, null);
        if (set != null) {
            for(String s : set)
            {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("name");
                    InfoFromServer info = new InfoFromServer(id, name);
                    items.add(info);
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return items;
    }

    private void populateSpinner(Spinner sp, String key)
    {
        ArrayList<InfoFromServer> list = loadFromSharedPreferences(key);
        List<String> labels = new ArrayList<>();
        for(int i = 0; i < list.size(); i++)
        {
            labels.add(list.get(i).getID());
        }
        Collections.sort(labels);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.custom_spinner_item, labels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(spinnerAdapter);
    }

    private class getDataForSpinner extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrWorkingStt.clear();
            arrWorkingStt.clear();
            spinnerStorage.edit().clear().apply();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Fetching Information...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            String json = serviceHandler.makeServiceCall(GET_DATA, ServiceHandler.GET);
            if(json != null)
            {
                getIt(json, "tbworkingstatus", "STATUS", "DETAILS", arrWorkingStt);
                getIt(json, "tbcompanytype", "COMPANY_TYPE", "DETAILS", arrCompanyType);
                getIt(json, "tbindustry", "INDUSTRY", "DETAILS", arrIndustry);

                storeSpinnerData(arrWorkingStt, "WorkingStt");
                storeSpinnerData(arrCompanyType, "CompanyType");
                storeSpinnerData(arrIndustry, "Industry");
            }
            return null;
        }

        private void getIt(String json, String key, String data1, String data2, ArrayList<InfoFromServer> list)
        {
            try {
                JSONObject object = new JSONObject(json);
                JSONArray array = object.getJSONArray(key);
                for(int i = 0; i < array.length(); i++)
                {
                    JSONObject jsonObject = (JSONObject)array.get(i);
                    InfoFromServer info = new InfoFromServer(jsonObject.getString(data1),
                            jsonObject.getString(data2));
                    list.add(info);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner(spWorkingStt, "WorkingStt");
            populateSpinner(spCompanyType, "CompanyType");
            populateSpinner(spIndustry, "Industry");
        }
    }

    public boolean isConnectedToInternet(Context ctx)
    {
        ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
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
}
