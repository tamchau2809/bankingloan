package chau.bankingloan;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Created on 06-May-16 by com08.
 */
public class InsuranceDetailsFragment extends Fragment
{
    View rootView;
    ProgressBar pb;
    FloatingActionButton fabNext, fabPre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_insurance_details, container, false);

        fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabPrintNext);
        pb = (ProgressBar)rootView.findViewById(R.id.testm);
        pb.setVisibility(View.INVISIBLE);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
            }
        });

        fabPre = (FloatingActionButton) rootView.findViewById(R.id.fabPrint);
        fabPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.INVISIBLE);
            }
        });

        return rootView;
    }
}
