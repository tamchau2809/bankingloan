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
    String arrMaritalStt[] = {"Single","Married", "Divorced", "Widowed"};

    SharedPreferences Personal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_personal, container, false);

        initWiget();

        populateSpinner(spEducation, arrEducation);
        populateSpinner(spGender, arrGender);
        populateSpinner(spMaritalStt, arrMaritalStt);

        Personal = this.getActivity().getSharedPreferences("PERSONAL", Context.MODE_APPEND);
        loadFromSharedPreference(Personal);

        FloatingActionButton fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabPersonalNext);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edName.getText().length() == 0)
                {
                    edName.setError("Please Enter Your Name!");
                    edName.requestFocus();
                }
                else if (tvBirthDay.getText().length() == 0)
                {
                    tvBirthDay.setError("Please Enter Your Birthday!");
                }
                else if (edIdenCard.getText().length() == 0)
                {
                    edIdenCard.setError("Please Enter Your Identity Card's Number!");
                    edIdenCard.requestFocus();
                }
                else if (tvDateOfIssue.getText().length() == 0)
                {
                    tvDateOfIssue.setError("What's Your Day of Issue?");
                }
                else if (edChildrenNum.getText().length() == 0)
                {
                    edChildrenNum.setError("How Many Children Do You Have?A");
                }
                else {
                    SharedPreferences.Editor editor = Personal.edit();
                    editor.putString("name", edName.getText().toString());
                    editor.putString("birthday", tvBirthDay.getText().toString());
                    editor.putString("identityNum", edIdenCard.getText().toString());
                    editor.putString("dateOissue", tvDateOfIssue.getText().toString());
                    editor.putInt("education", spEducation.getSelectedItemPosition());
                    editor.putInt("gender", spGender.getSelectedItemPosition());
                    editor.putInt("maritalStt", spMaritalStt.getSelectedItemPosition());
                    editor.putString("numOc", edChildrenNum.getText().toString());
                    editor.apply();

                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(2);
                }
            }
        });

        FloatingActionButton fabPre = (FloatingActionButton)rootView.findViewById(R.id.fabPersonalPre);
        fabPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity)getActivity();
                act.switchTab(0);
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
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.custom_spinner_item, arr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(spinnerAdapter);
    }

    public void loadFromSharedPreference(SharedPreferences test)
    {
        if(test.contains("name"))
        {
            edName.setText(test.getString("name", ""));
            tvBirthDay.setText(test.getString("birthday", ""));
            edIdenCard.setText(test.getString("identityNum", ""));
            tvDateOfIssue.setText(test.getString("dateOissue", ""));
            spEducation.setSelection(test.getInt("education", 0));
            spGender.setSelection(test.getInt("gender", 0));
            spMaritalStt.setSelection(test.getInt("maritalStt", 0));
            edChildrenNum.setText(test.getString("numOc", ""));
        }
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
