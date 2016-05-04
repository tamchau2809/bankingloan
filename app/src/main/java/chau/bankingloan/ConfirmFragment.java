package chau.bankingloan;

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
            tvConfirmMobile, tvConfirmEmail, tvComfirmWorkingStt, tvConfirmEmployer;
    FloatingActionButton fabConfirmPre, fabConfirmNext;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_confirm, container, false);

        initWiget();
        final int count = 0;


        cbCorrect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });

        fabConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbCorrect.isChecked() && cbAccept.isChecked())
                    Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Please Check Your Details and Read Terms and Conditions!", Toast.LENGTH_SHORT).show();
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
        fabConfirmPre = (FloatingActionButton) rootView.findViewById(R.id.fabConfirmPre);
//        fabConfirmPre.setEnabled(false);
        fabConfirmNext = (FloatingActionButton) rootView.findViewById(R.id.fabConfirmNext);
//        fabConfirmNext.setEnabled(false);
    }
}
