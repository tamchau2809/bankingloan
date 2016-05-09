package chau.bankingloan;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created on 09-May-16 by com08.
 */
public class ApproveFragment extends Fragment {
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_approve, container, false);

        WebView a = (WebView)rootView.findViewById(R.id.myWebView);
        a.setBackgroundColor(Color.TRANSPARENT);
        a.loadUrl("file:///android_asset/test.gif");

        return rootView;
    }
}
