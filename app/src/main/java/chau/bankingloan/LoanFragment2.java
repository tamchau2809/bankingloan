package chau.bankingloan;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created on 25-Apr-16 by com08
 */
public class LoanFragment2  extends Fragment implements View.OnClickListener
{
    View rootView;

    SharedPreferences LoanDetails;

    Spinner spLoanType, spTenure, spLoanPurpose;
    EditText edLoanAmount, edMonthlyPayment, edMaxInterest;
    TextView tvLastPayment;
    FloatingActionButton fabNext;

    String arrLoanType[] = {"UPL"};
    String arrTenure[] = {"12", "24", "48"};
    String arrLoanPurpose[] = {"Renovation"};

    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat dateFormater;

    public LoanFragment2() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_loan_2, container, false);

        initWiget();
        populateSpinner(spTenure, arrTenure);
        populateSpinner(spLoanPurpose, arrLoanPurpose);
        populateSpinner(spLoanType, arrLoanType);

        LoanDetails = this.getActivity().getSharedPreferences("LOAN_DETAILS", Context.MODE_APPEND);
        loadFromSharedPreference(LoanDetails);

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                MainActivity act = (MainActivity)getActivity();
                act.switchTab(3);
            }
        });

        dateFormater = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SetDateTime();
        return rootView;
    }

    private void SetDateTime()
    {
        tvLastPayment.setOnClickListener(this);
        Calendar calendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvLastPayment.setText(dateFormater.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.setTitle("Please Choose The Day Of Your Last Pay");
    }

    public void loadFromSharedPreference(SharedPreferences test)
    {
        if(test.contains("loan_type"))
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

    public void initWiget()
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
}
