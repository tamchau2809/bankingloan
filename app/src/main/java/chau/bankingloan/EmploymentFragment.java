package chau.bankingloan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by com08 on 25-Apr-16.
 */
public class EmploymentFragment extends Fragment implements View.OnClickListener{
    View rootView;

    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat dateFormater;

    String arrWorkingStt[] = {"UPL"};
    String arrCompanyType[] = {"Public"};
    String arrIndustry[] = {"Cosmetics"};

    TextView tvDateJoined;
    Spinner spWorkingStt, spCompanyType, spIndustry;
    EditText edEmployer, edDesignation, edSalaryIncome, edEmployerAdd, edOtherIncome, edTotalIncome, edEmployerContact;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_employment, container, false);

        initWiget();
        dateFormater = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        populateSpinner(spWorkingStt, arrWorkingStt);
        populateSpinner(spCompanyType, arrCompanyType);
        populateSpinner(spIndustry, arrIndustry);

        FloatingActionButton fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabEmployNext);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity act = (MainActivity)getActivity();
//                act.switchTab(4);
                Toast.makeText(getContext(), "Updating...", Toast.LENGTH_SHORT).show();
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
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),
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
