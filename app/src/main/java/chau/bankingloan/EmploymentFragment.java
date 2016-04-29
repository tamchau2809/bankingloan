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
 * Created on 25-Apr-16 by com08.
 */
public class EmploymentFragment extends Fragment implements View.OnClickListener{
    View rootView;

    SharedPreferences employment;

    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat dateFormater;

    String arrWorkingStt[] = {"Full-time", "Part-time"};
    String arrCompanyType[] = {"Public"};
    String arrIndustry[] = {"Cosmetics"};

    TextView tvDateJoined;
    Spinner spWorkingStt, spCompanyType, spIndustry;
    EditText edEmployer, edDesignation, edSalaryIncome, edEmployerAdd, edOtherIncome, edTotalIncome, edEmployerContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_employment, container, false);

        initWiget();
        dateFormater = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        populateSpinner(spWorkingStt, arrWorkingStt);
        populateSpinner(spCompanyType, arrCompanyType);
        populateSpinner(spIndustry, arrIndustry);

        employment = this.getActivity().getSharedPreferences("EMPLOYMENT", Context.MODE_APPEND);
        loadFromSharedPreference(employment);

        FloatingActionButton fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabEmployNext);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = employment.edit();
                editor.putInt("workingStt", spWorkingStt.getSelectedItemPosition());
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
                editor.apply();

                MainActivity act = (MainActivity)getActivity();
                act.switchTab(6);
            }
        });

        FloatingActionButton fabPre = (FloatingActionButton)rootView.findViewById(R.id.fabEmployPre);
        fabPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity)getActivity();
                act.switchTab(4);
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
            spWorkingStt.setSelection(test.getInt("workingStt", 0));
            spCompanyType.setSelection(test.getInt("companyType", 0));
            spIndustry.setSelection(test.getInt("industry", 0));
        }
    }

    public void initWiget()
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

    private void populateSpinner(Spinner spn, String[] arr)
    {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.custom_spinner_item, arr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(spinnerAdapter);
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
                tvDateJoined.setText(dateFormater.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == tvDateJoined) {
            mDatePickerDialog.show();
        }
    }
}
