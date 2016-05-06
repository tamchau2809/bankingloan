package chau.bankingloan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created on 04-May-16 by com08.
 */
public class ConfirmFragment extends Fragment
{
    View rootView;
    CheckBox cbCorrect, cbAccept;
    TextView tvConfirmName, tvConfirmDoB, tvConfirmId, tvConfirmAdd, tvConfirmTelephone,
            tvConfirmMobile, tvConfirmEmail, tvComfirmWorkingStt, tvConfirmEmployer, tvConfirmEmployerAdd;
    FloatingActionButton fabConfirmPre, fabConfirmNext;
    SharedPreferences personalPreferences, contactPreferences, employmentPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_confirm, container, false);

        initWiget();
        personalPreferences = this.getActivity().getSharedPreferences("PERSONAL", Context.MODE_APPEND);
        loadFromPersonal(personalPreferences);
        contactPreferences = this.getActivity().getSharedPreferences("CONTACT", Context.MODE_APPEND);
        loadFromContact(contactPreferences);
        employmentPreferences = this.getActivity().getSharedPreferences("EMPLOYMENT", Context.MODE_APPEND);
        loadFromEmployment(employmentPreferences);

        cbCorrect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    cbCorrect.setError(null);
                } else {
                    cbCorrect.setError("Please Check Your Details!");
                }
            }
        });

        cbAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    cbAccept.setError(null);
                } else {
                    cbAccept.setError("Please Read The Terms and Conditions!");
                }
            }
        });

        fabConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbCorrect.isChecked() && cbAccept.isChecked()) {
                    Toast.makeText(getContext(), "Okay", Toast.LENGTH_SHORT).show();
                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(8);
                }
                else if(!cbCorrect.isChecked() && !cbAccept.isChecked()) {
                    cbCorrect.setError("Please Check Your Details!");
                    cbAccept.setError("Please Read The Terms and Conditions!");
                }
            }
        });

        fabConfirmPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity) getActivity();
                act.switchTab(6);
            }
        });
        return rootView;
    }

    public void loadFromPersonal(SharedPreferences personal)
    {
        tvConfirmName.setText(personal.getString("name", ""));
        tvConfirmDoB.setText(personal.getString("birthday", ""));
        tvConfirmId.setText(personal.getString("identityNum", ""));
    }

    @SuppressLint("SetTextI18n")
    public void loadFromContact(SharedPreferences contact)
    {
        tvConfirmTelephone.setText(contact.getString("telephone", ""));
        tvConfirmMobile.setText(contact.getString("mobile", ""));
        tvConfirmEmail.setText(contact.getString("email", ""));
        tvConfirmAdd.setText(contact.getString("street", "") + ", "
                + contact.getString("city", ""));
    }

    public void loadFromEmployment(SharedPreferences employment)
    {
        tvComfirmWorkingStt.setText(employment.getString("workingStt", ""));
        tvConfirmEmployer.setText(employment.getString("employer", ""));
        tvConfirmEmployerAdd.setText(employment.getString("employerAdd", ""));
    }

    public void initWiget()
    {
        cbAccept = (CheckBox)rootView.findViewById(R.id.cbAccept);
        cbCorrect = (CheckBox)rootView.findViewById(R.id.cbCorrect);
        tvConfirmAdd = (TextView)rootView.findViewById(R.id.tvConfirmAdd);
        tvConfirmName = (TextView)rootView.findViewById(R.id.tvConfirmName);
        tvConfirmDoB = (TextView)rootView.findViewById(R.id.tvConfirmDoB);
        tvConfirmId = (TextView)rootView.findViewById(R.id.tvConfirmId);
        tvConfirmTelephone = (TextView)rootView.findViewById(R.id.tvConfirmTelephone);
        tvConfirmMobile = (TextView)rootView.findViewById(R.id.tvConfirmMobile);
        tvConfirmEmail = (TextView)rootView.findViewById(R.id.tvConfirmEmail);
        tvComfirmWorkingStt = (TextView)rootView.findViewById(R.id.tvComfirmWorkingStt);
        tvConfirmEmployer = (TextView)rootView.findViewById(R.id.tvConfirmEmployer);
        tvConfirmEmployerAdd = (TextView)rootView.findViewById(R.id.tvConfirmEmployerAdd);
        fabConfirmPre = (FloatingActionButton) rootView.findViewById(R.id.fabConfirmPre);
//        fabConfirmPre.setEnabled(false);
        fabConfirmNext = (FloatingActionButton) rootView.findViewById(R.id.fabConfirmNext);
//        fabConfirmNext.setEnabled(false);
    }
}
