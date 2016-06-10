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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import chau.bankingloan.customview.ServiceHandler;
import chau.bankingloan.customview.URLConnect;

/**
 * Created on 25-Apr-16 by com08
 */
public class LoanFragment2  extends Fragment implements View.OnClickListener
{
    View rootView;

    SharedPreferences LoanDetails;
    ProgressDialog pDialog;

    Spinner spLoanType, spTenure, spLoanPurpose;
    EditText edLoanAmount, edMonthlyPayment, edMaxInterest;
    TextView tvLastPayment;
    FloatingActionButton fabNext;

    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    ArrayList<InfoFromServer> arrType = new ArrayList<>();
    ArrayList<InfoFromServer> arrTenure = new ArrayList<>();
    ArrayList<InfoFromServer> arrPurpose = new ArrayList<>();

    public LoanFragment2() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_loan_2, container, false);
        initWidget();
        LoanDetails = this.getActivity().getSharedPreferences("LOAN_DETAILS", Context.MODE_APPEND);
        loadFromSharedPreference(LoanDetails);

        if(!LoanDetails.contains("Type") ||
                !LoanDetails.contains("Tenure") ||
                !LoanDetails.contains("Purpose"))
        {
            if(isConnectedToInternet(getContext())) {
                new GetDataForSpinner().execute();
            }
            else
            {
                showAlert("Xin Kiểm Tra Lại Kết Nối!");
            }
        }
        else
        {
            populateSpinner(spTenure, "Tenure");
            populateSpinner(spLoanPurpose, "Purpose");
            populateSpinner(spLoanType, "Type");
            spLoanType.setSelection(LoanDetails.getInt("loanTypeLoca", 0));
            spTenure.setSelection(LoanDetails.getInt("tenureLoca", 0));
            spLoanPurpose.setSelection(LoanDetails.getInt("loanPurposeLoca", 0));
        }

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabLoanRefresh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isConnectedToInternet(getContext())) {
                    new GetDataForSpinner().execute();
                }
                else
                {
                    showAlert("Xin Kiểm Tra Lại Kết Nối!");
                }
            }
        });

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edLoanAmount.getText().toString().length() == 0) {
                    edLoanAmount.setError("Please Enter The Loan Amount!");
                    edLoanAmount.requestFocus();
                }
                else if(edMaxInterest.getText().toString().length() == 0) {
                    edMaxInterest.setError("Please Enter The The Max Interest!");
                    edMaxInterest.requestFocus();
                }
                else if(edMonthlyPayment.getText().toString().length() == 0)
                {
                    edMonthlyPayment.setError("Please Enter The Monthly Payment!");
                    edMonthlyPayment.requestFocus();
                }
                else if(tvLastPayment.getText().toString().length() == 0)
                {
                    tvLastPayment.setError("Please Choose The Last Payment Day!");
                }
                else {
                    LoanDetails = getActivity().getSharedPreferences("LOAN_DETAILS", Context.MODE_APPEND);
                    SharedPreferences.Editor editor = LoanDetails.edit();
                    editor.putString("loan_type", spLoanType.getSelectedItem().toString());
                    editor.putString("loan_amount", edLoanAmount.getText().toString());
                    editor.putString("tenure", spTenure.getSelectedItem().toString());
                    editor.putString("loan_purpose", spLoanPurpose.getSelectedItem().toString());
                    editor.putString("max_interest", edMaxInterest.getText().toString());
                    editor.putString("monthly_payment", edMonthlyPayment.getText().toString());
                    editor.putString("last_payment", tvLastPayment.getText().toString());
                    editor.putInt("loanTypeLoca", spLoanType.getSelectedItemPosition());
                    editor.putInt("tenureLoca", spTenure.getSelectedItemPosition());
                    editor.putInt("loanPurposeLoca", spLoanPurpose.getSelectedItemPosition());
                    editor.apply();

                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(1);
                }
            }
        });

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SetDateTime();

        return rootView;
    }

    /**
     * Set day trên textview. Click vào để chọn ngày
     */
    private void SetDateTime()
    {
        tvLastPayment.setOnClickListener(this);
        Calendar calendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvLastPayment.setText(dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.setTitle("Please Choose The Day Of Your Last Pay");
    }

    public void loadFromSharedPreference(SharedPreferences test)
    {
        if(test.contains("loan_amount"))
        {
            edLoanAmount.setText(test.getString("loan_amount", ""));
            edMaxInterest.setText(test.getString("max_interest", ""));
            edMonthlyPayment.setText(test.getString("monthly_payment", ""));
            tvLastPayment.setText(test.getString("last_payment", ""));
            spLoanType.setSelection(test.getInt("loanTypeLoca", 0));
            spLoanPurpose.setSelection(test.getInt("loanPurposeLoca", 0));
            spTenure.setSelection(test.getInt("tenureLoca", 0));
        }
    }

    public void initWidget()
    {
        spLoanPurpose = (Spinner)rootView.findViewById(R.id.spLoanPurpose);
        spTenure = (Spinner)rootView.findViewById(R.id.spTenure);
        spLoanType = (Spinner)rootView.findViewById(R.id.spLoanType);
        edLoanAmount = (EditText)rootView.findViewById(R.id.edLoanAmout);
        edMonthlyPayment = (EditText)rootView.findViewById(R.id.edMonthlyPayment);
        tvLastPayment = (TextView)rootView.findViewById(R.id.edLastPayment);
        edMaxInterest = (EditText)rootView.findViewById(R.id.edMaxInterest);
        fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabLoanNext);
    }

    @Override
    public void onClick(View view) {
        if(view == tvLastPayment) {
            mDatePickerDialog.show();
        }
    }

    public ArrayList<InfoFromServer> loadFromSharedPreferences(String key)
    {
        ArrayList<InfoFromServer> items = new ArrayList<>();
//        Set<String> set = spinnerStorage.getStringSet(key, null);
        Set<String> set = LoanDetails.getStringSet(key, null);
        if (set != null) {
            for(String s : set)
            {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String id = jsonObject.getString("id");
                    String name = jsonObject.getString("data");
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

    public void storeSpinnerData(ArrayList<InfoFromServer> list, String key)
    {
//        SharedPreferences.Editor editor = spinnerStorage.edit();
        SharedPreferences.Editor editor = LoanDetails.edit();
        Set<String> set = new HashSet<>();
        for(int i = 0; i < list.size(); i++)
        {
            set.add(list.get(i).getJSONInfo().toString());
        }
        editor.putStringSet(key, set);
        editor.apply();
    }

    private class GetDataForSpinner extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            arrType.clear();
            arrPurpose.clear();
            arrTenure.clear();

            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Fetching Information...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(URLConnect.GET_DATA_LOAN, ServiceHandler.GET);
            if(json != null)
            {
                getIt(json, "tbtenure", "TENURE", "DETAILS", arrTenure);
                storeSpinnerData(arrTenure, "Tenure");
                getIt(json, "tbloantype", "TYPE", "DETAILS", arrType);
                storeSpinnerData(arrType, "Type");
                getIt(json, "tbpurpose", "PURPOSE", "DETAILS", arrPurpose);
                storeSpinnerData(arrPurpose, "Purpose");
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
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner(spTenure, "Tenure");
            populateSpinner(spLoanType, "Type");
            populateSpinner(spLoanPurpose, "Purpose");
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