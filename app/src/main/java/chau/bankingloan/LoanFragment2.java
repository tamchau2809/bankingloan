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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created on 25-Apr-16 by com08
 */
public class LoanFragment2  extends Fragment implements View.OnClickListener
{
    View rootView;

    final String GET_TYPE_URL = "http://192.168.1.17/chauvu/loanGetType.php";
    final String GET_TENURE_URL = "http://192.168.1.17/chauvu/loanGetTenure.php";

    SharedPreferences LoanDetails;
    ProgressDialog pDialog;

    Spinner spLoanType, spTenure, spLoanPurpose;
    EditText edLoanAmount, edMonthlyPayment, edMaxInterest;
    TextView tvLastPayment;
    FloatingActionButton fabNext;

//    String arrLoanType[] = {"UPL"};
//    String arrTenure[] = {"12", "24", "48"};
    String arrLoanPurpose[] = {"Renovation"};

    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    ArrayList<InfoFromServer> details = new ArrayList<>();

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
//        populateSpinner(spTenure, arrTenure);
        populateSpinner(spLoanPurpose, arrLoanPurpose);
//        populateSpinner(spLoanType, arrLoanType);

        LoanDetails = this.getActivity().getSharedPreferences("LOAN_DETAILS", Context.MODE_APPEND);
        loadFromSharedPreference(LoanDetails);

        final SharedPreferences pref = this.getActivity().getSharedPreferences("TYPE", Context.MODE_PRIVATE);
        if(!pref.contains("TYPE"))
        {
            if(isConnectedToInternet(getContext())) {
                new GetTYPE().execute();
            }
            else
            {
                showAlert("Xin Kiểm Tra Lại Kết Nối!");
            }
        }
        else
        {
            populateSpinnerType();
            spLoanType.setSelection(LoanDetails.getInt("loanTypeLoca", 0));
        }

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabLoanRefresh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnectedToInternet(getContext())) {
                    new GetTYPE().execute();
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
                SharedPreferences.Editor editor = LoanDetails.edit();
//                editor.putString("loan_type", spLoanType.getSelectedItem().toString());
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

                MainActivity act = (MainActivity)getActivity();
                act.switchTab(3);
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
        if(test.contains("tenure"))
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

    private void populateSpinner(Spinner spn, String[] arr)
    {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.custom_spinner_item, arr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(spinnerAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view == tvLastPayment) {
            mDatePickerDialog.show();
        }
    }

    private void populateSpinnerType()
    {
        details = loadMKHFromSharePreferences();
        List<String> labels = new ArrayList<>();
        for(int i = 0; i < details.size(); i++)
        {
            labels.add(details.get(i).getID());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.custom_spinner_item, labels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLoanType.setAdapter(spinnerAdapter);
    }

    public ArrayList<InfoFromServer> loadMKHFromSharePreferences()
    {
        SharedPreferences mPrefs = this.getActivity().getSharedPreferences("TYPE", Context.MODE_PRIVATE);
        ArrayList<InfoFromServer> items = new ArrayList<>();
        Set<String> set = mPrefs.getStringSet("TYPE", null);
        for(String s : set)
        {
            try
            {
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
        return items;
    }

    public void storeTYPEList(ArrayList<InfoFromServer> list)
    {
        SharedPreferences sharedPrefs = this.getActivity().getSharedPreferences("TYPE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Set<String> set= new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            set.add(list.get(i).getJSONInfo().toString());
        }
        editor.putStringSet("TYPE", set);
        editor.apply();
    }

    private class GetTYPE extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Fetching Information...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(GET_TYPE_URL, ServiceHandler.GET);
            if(json != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray MKH = jsonObject.getJSONArray("tbloantype");
                    for(int i = 0; i < MKH.length(); i++)
                    {
                        JSONObject catObj = (JSONObject)MKH.get(i);
                        InfoFromServer info = new InfoFromServer(catObj.getString("TYPE"), catObj.getString("DETAILS"));
                        details.add(info);
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
                storeTYPEList(details);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinnerType();
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