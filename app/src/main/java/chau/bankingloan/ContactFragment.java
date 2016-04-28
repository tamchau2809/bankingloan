package chau.bankingloan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by com08
 * on 25-Apr-16.
 */
public class ContactFragment extends Fragment{
    View rootView;

    String arrRelationship[] = {"Spouse"};
    Spinner spRelationship;
    EditText edPerAdd1, edPerAdd2, edTelephone, edMobile, edEmail,
            edRefName, edContactNum;
    CheckBox cbSame;

    SharedPreferences contact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        initWiget();

        contact = this.getActivity().getSharedPreferences("CONTACT", Context.MODE_APPEND);
        loadFromSharedPreference(contact);

        populateSpinner(spRelationship, arrRelationship);

        FloatingActionButton fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabContactNext);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = contact.edit();
                editor.putString("street", edPerAdd1.getText().toString());
                editor.putString("city", edPerAdd2.getText().toString());
                editor.putBoolean("isChecked", cbSame.isChecked());
                editor.putString("telephone", edTelephone.getText().toString());
                editor.putString("mobile", edMobile.getText().toString());
                editor.putString("email", edEmail.getText().toString());
                editor.putString("refname", edRefName.getText().toString());
                editor.putInt("relationship", spRelationship.getSelectedItemPosition());
                editor.putString("contactNum", edContactNum.getText().toString());
                editor.apply();

                MainActivity act = (MainActivity)getActivity();
                act.switchTab(5);
            }
        });

        FloatingActionButton fabPre = (FloatingActionButton)rootView.findViewById(R.id.fabContactPre);
        fabPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity)getActivity();
                act.switchTab(3);
            }
        });
        return rootView;
    }

    public void loadFromSharedPreference(SharedPreferences test)
    {
        if(test.contains("refname"))
        {
            edRefName.setText(test.getString("refname", ""));
            edPerAdd1.setText(test.getString("street", ""));
            edPerAdd2.setText(test.getString("city", ""));
            edTelephone.setText(test.getString("telephone", ""));
            edMobile.setText(test.getString("mobile", ""));
            edEmail.setText(test.getString("email", ""));
            edContactNum.setText(test.getString("contactNum", ""));
            cbSame.setChecked(test.getBoolean("isChecked", false));
            spRelationship.setSelection(test.getInt("relationship", 0));
        }
    }

    public void initWiget()
    {
        spRelationship = (Spinner)rootView.findViewById(R.id.spRelationship);
        edPerAdd1 = (EditText)rootView.findViewById(R.id.edPerAdd1);
        edPerAdd2 = (EditText)rootView.findViewById(R.id.edPerAdd2);
        edTelephone = (EditText)rootView.findViewById(R.id.edTelephone);
        edMobile = (EditText)rootView.findViewById(R.id.edMobile);
        edEmail = (EditText)rootView.findViewById(R.id.edEmail);
        edRefName = (EditText)rootView.findViewById(R.id.edRefName);
        edContactNum = (EditText)rootView.findViewById(R.id.edContactNum);
        cbSame = (CheckBox)rootView.findViewById(R.id.cbSame);
    }

    private void populateSpinner(Spinner spn, String[] arr)
    {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.custom_spinner_item, arr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(spinnerAdapter);
    }
}
