package chau.bankingloan;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created on 06-May-16 by com08.
 */
public class PrintFragment extends Fragment
{
    View rootView;
    JustifyTextView test;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_print, container, false);
        test = (JustifyTextView) rootView.findViewById(R.id.test);
        test.setText("Please wait while we process your application." + "\r\n" + "This may take a few minutes...");
        return rootView;
    }
}
