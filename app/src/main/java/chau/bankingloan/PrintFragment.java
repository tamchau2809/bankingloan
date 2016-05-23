package chau.bankingloan;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import chau.bankingloan.customview.JustifyTextView;

/**
 * Created on 06-May-16 by com08.
 */
public class PrintFragment extends Fragment
{
    View rootView;
    JustifyTextView test, test2, test3;
    ProgressBar pgBar;
    FloatingActionButton fabPrintPre, fabPrintNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_print, container, false);
        test = (JustifyTextView) rootView.findViewById(R.id.test);
        test2 = (JustifyTextView)rootView.findViewById(R.id.tvPrintCongra);
        test3 = (JustifyTextView)rootView.findViewById(R.id.tvPrintInfo);
        pgBar = (ProgressBar)rootView.findViewById(R.id.prgPrint);
        fabPrintNext = (FloatingActionButton)rootView.findViewById(R.id.fabPrintNext);
        fabPrintPre = (FloatingActionButton)rootView.findViewById(R.id.fabPrintPre);

        fabPrintNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test.setVisibility(View.GONE);
                pgBar.setVisibility(View.GONE);
                test2.setVisibility(View.VISIBLE);
                test3.setVisibility(View.VISIBLE);
            }
        });

        fabPrintPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test.setVisibility(View.VISIBLE);
                pgBar.setVisibility(View.VISIBLE);
                test2.setVisibility(View.GONE);
                test3.setVisibility(View.GONE);
            }
        });

        return rootView;
    }
}
