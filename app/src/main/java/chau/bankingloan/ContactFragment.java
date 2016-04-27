package chau.bankingloan;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by com08
 * on 25-Apr-16.
 */
public class ContactFragment extends Fragment{
    View rootView;

    String arrRelationship[] = {"UPL"};
    Spinner spRelationship;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        spRelationship = (Spinner)rootView.findViewById(R.id.spRelationship);
        populateSpinner(spRelationship, arrRelationship);

        FloatingActionButton fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabContactNext);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void populateSpinner(Spinner spn, String[] arr)
    {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.custom_spinner_item, arr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(spinnerAdapter);
    }
}
