package chau.bankingloan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by com08 on 25-Apr-16.
 */
public class LoanFragment2  extends Fragment
{
    final String GET_MKH_URL = "http://192.168.1.11/chauvu/getMKH.php";
    final String GET_MNV_URL = "http://192.168.1.11/chauvu/getMNV.php";

    View rootView;;

    SharedPreferences contractDetails;

    Spinner spinnerMKH;
    ProgressDialog pDialog;
    ArrayList<InfoFromServer> listMKH = new ArrayList<InfoFromServer>();

    String arrLoanType[] = {"UPL"};

    public LoanFragment2() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_loan_2, container, false);

        initWiget();
        populateSpinnerMKH();

        FloatingActionButton fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabLoanNext);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity)getActivity();
                act.switchTab(3);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void initWiget()
    {
//        btnCapture = (ImageButton)rootView.findViewById(R.id.btnCapture);
        spinnerMKH = (Spinner)rootView.findViewById(R.id.idLoanType);
    }

    private void populateSpinnerMKH()
    {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.custom_spinner_item, arrLoanType);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMKH.setAdapter(spinnerAdapter);
    }
}
