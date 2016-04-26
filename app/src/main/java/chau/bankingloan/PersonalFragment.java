package chau.bankingloan;

import android.app.DatePickerDialog;
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
 * Created by com08 on 25-Apr-16.
 */
public class PersonalFragment extends Fragment implements View.OnClickListener
{
    View rootView;

    private DatePickerDialog mDatePickerDialogBD, mDatePickerDialogDoI;

    private SimpleDateFormat dateFormater;

    TextView tvBirthDay, tvDateOfIssue;
    EditText edName, edIdenCard, edChildrenNum;
    Spinner spEducation, spGender, spMaritalStt;
    String arrEducation[] = {"Undergraduate"};
    String arrGender[] = {"N.A", "Male", "Female"};
    String arrMaritalStt[] = {"Married"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_personal, container, false);

        initWiget();

        populateSpinner(spEducation, arrEducation);
        populateSpinner(spGender, arrGender);
        populateSpinner(spMaritalStt, arrMaritalStt);

        FloatingActionButton fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabPersonalNext);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity)getActivity();
                act.switchTab(4);
            }
        });

        FloatingActionButton fabPre = (FloatingActionButton)rootView.findViewById(R.id.fabPersonalPre);
        fabPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity)getActivity();
                act.switchTab(2);
            }
        });

        dateFormater = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SetDateTime();

        return rootView;
    }

    public void initWiget()
    {
        tvBirthDay = (TextView)rootView.findViewById(R.id.tvBirthDay);
        tvDateOfIssue = (TextView)rootView.findViewById(R.id.tvDateOfIssue);
        edName = (EditText)rootView.findViewById(R.id.edName);
        edChildrenNum = (EditText)rootView.findViewById(R.id.edChildrenNum);
        edIdenCard = (EditText)rootView.findViewById(R.id.edIdenCard);
        spEducation = (Spinner)rootView.findViewById(R.id.spEducation);
        spGender = (Spinner)rootView.findViewById(R.id.spGender);
        spMaritalStt = (Spinner)rootView.findViewById(R.id.spMaritalStt);
    }

    private void SetDateTime()
    {
        tvBirthDay.setOnClickListener(this);
        Calendar calendar = Calendar.getInstance();
        mDatePickerDialogBD = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvBirthDay.setText(dateFormater.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        tvDateOfIssue.setOnClickListener(this);
        mDatePickerDialogDoI = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvDateOfIssue.setText(dateFormater.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void populateSpinner(Spinner spn, String[] arr)
    {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.custom_spinner_item, arr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(spinnerAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.tvBirthDay:
                mDatePickerDialogBD.show();
                break;
            case R.id.tvDateOfIssue:
                mDatePickerDialogDoI.show();
                break;
            default:
                break;
        }
    }
}
