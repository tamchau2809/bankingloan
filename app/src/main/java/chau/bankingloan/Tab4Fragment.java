package chau.bankingloan;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import chau.bankingloan.customThings.ServerInfo;

/**
 * Created on 01-07-2016 by com08.
 */
public class Tab4Fragment extends Fragment {
    View rootView;
    LinearLayout lnrTab4;
    ArrayList<ServerInfo> serverInfos;

    ProgressDialog progressDialog;
    FloatingActionButton fabNext, fabPre, fabRefresh;
    View.OnClickListener listenerNext, listenerPre, listenerRefresh;

    SharedPreferences preferences;

    String json;
    JSONObject object;
    JSONArray array;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_3, container, false);

        return rootView;
    }
}
