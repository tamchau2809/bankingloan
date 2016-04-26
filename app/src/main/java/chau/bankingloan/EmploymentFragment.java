package chau.bankingloan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by com08 on 25-Apr-16.
 */
public class EmploymentFragment extends Fragment {
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_employment, container, false);

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

        return rootView;
    }
}
