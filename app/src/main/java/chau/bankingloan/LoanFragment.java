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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chau.bankingloan.customThings.ConnectURL;
import chau.bankingloan.customThings.InfoFromServer;

/**
 * Created on 20/04/2016 by com08.
 */
public class LoanFragment extends Fragment {
    final String GET_MKH_URL = "http://192.168.1.17/chauvu/getMKH.php";
    final String GET_MNV_URL = "http://192.168.1.17/chauvu/getMNV.php";

    View rootView;

    SharedPreferences contractDetails;

    EditText edNum;
    Spinner spinnerMKH;
    Spinner spinnerMNV;
    ProgressDialog pDialog;
    ArrayList<InfoFromServer> listMKH = new ArrayList<>();
    ArrayList<InfoFromServer> listMNV = new ArrayList<>();

    public LoanFragment() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_loan, container, false);

        initWidget();
        contractDetails = this.getActivity().getSharedPreferences("contractDetails", Context.MODE_APPEND);

        final SharedPreferences pref = this.getActivity().getSharedPreferences("MKH", Context.MODE_PRIVATE);
        if(!pref.contains("MKH"))
        {
            if(isConnectedToInternet(getContext())) {
                new GetMKH().execute();
                new GetMNV().execute();
            }
            else
            {
                showAlert("Xin Kiểm Tra Lại Kết Nối!");
            }
        }
        else
        {
            populateSpinnerMKH();
            populateSpinnerMNV();
            spinnerMKH.setSelection(contractDetails.getInt("MKH_LOCA", 0));
            spinnerMNV.setSelection(contractDetails.getInt("MNV_LOCA", 0));
        }

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabNextLoanFrag);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnectedToInternet(getContext())) {
                    new GetMKH().execute();
                    new GetMNV().execute();
                }
                else
                {
                    showAlert("Xin Kiểm Tra Lại Kết Nối!");
                }
            }
        });

        FloatingActionButton fab1 = (FloatingActionButton) rootView.findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edNum.getText().toString().length() == 0) {
                    edNum.setError("Chưa Nhập Số Hóa Đơn!");
                    edNum.requestFocus();
                } else {
//                    MainActivity.MAKH = listMKH.get(spinnerMKH.getSelectedItemPosition()).getID();
//                    MainActivity.contractNum = edNum.getText().toString();
//                    MainActivity.MANV = listMNV.get(spinnerMNV.getSelectedItemPosition()).getID();

                    SharedPreferences.Editor editor = contractDetails.edit();
                    editor.putInt("MKH_LOCA", spinnerMKH.getSelectedItemPosition());
                    editor.putInt("MNV_LOCA", spinnerMNV.getSelectedItemPosition());
                    editor.putString("number", edNum.getText().toString());
                    editor.apply();

                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(1);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void storeMKHList(ArrayList<InfoFromServer> list)
    {
        SharedPreferences sharedPrefs = this.getActivity().getSharedPreferences("MKH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Set<String> set= new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            set.add(list.get(i).getJSONInfo().toString());
        }
        editor.putStringSet("MKH", set);
        editor.apply();
    }

    public void storeMNVList(ArrayList<InfoFromServer> list) {
        SharedPreferences sharedPrefs = this.getActivity().getSharedPreferences("MNV", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Set<String> set = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            set.add(list.get(i).getJSONInfo().toString());
        }
        editor.putStringSet("MNV", set);
        editor.apply();
    }

    public ArrayList<InfoFromServer> loadMKHFromSharePreferences()
    {
        SharedPreferences mPrefs = this.getActivity().getSharedPreferences("MKH", Context.MODE_PRIVATE);
        ArrayList<InfoFromServer> items = new ArrayList<>();
        Set<String> set = mPrefs.getStringSet("MKH", null);
        for(String s : set)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(s);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                InfoFromServer info = new InfoFromServer(id, name);
                items.add(info);
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
        }
        return items;
    }

    public ArrayList<InfoFromServer> loadMNVFromSharePreferences()
    {
        SharedPreferences mPrefs = this.getActivity().getSharedPreferences("MNV", Context.MODE_PRIVATE);
        ArrayList<InfoFromServer> items = new ArrayList<>();
        Set<String> set = mPrefs.getStringSet("MNV", null);
        for(String s : set)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(s);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                InfoFromServer info = new InfoFromServer(id, name);
                items.add(info);
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
        }
        return items;
    }

    public void initWidget()
    {
        edNum = (EditText)rootView.findViewById(R.id.edContractNum);
        spinnerMKH = (Spinner)rootView.findViewById(R.id.spinnerMKH);
        spinnerMNV = (Spinner)rootView.findViewById(R.id.spinnerMNV);
    }

    private class GetMKH extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Fetching Information...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            ConnectURL jsonParser = new ConnectURL();
            String json = jsonParser.makeServiceCall(GET_MKH_URL, ConnectURL.GET);
            if(json != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray MKH = jsonObject.getJSONArray("tbkhachhang");
                    for(int i = 0; i < MKH.length(); i++)
                    {
                        JSONObject catObj = (JSONObject)MKH.get(i);
                        InfoFromServer info = new InfoFromServer(catObj.getString("MKH"), catObj.getString("TenKH"));
                        listMKH.add(info);
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
                storeMKHList(listMKH);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinnerMKH();
        }
    }

    private class GetMNV extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            ConnectURL jsonParser = new ConnectURL();
            String json = jsonParser.makeServiceCall(GET_MNV_URL, ConnectURL.GET);
            if(json != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray MKH = jsonObject.getJSONArray("tbnhanvien");
                    for(int i = 0; i < MKH.length(); i++)
                    {
                        JSONObject catObj = (JSONObject)MKH.get(i);
                        InfoFromServer info = new InfoFromServer(catObj.getString("MNV"), catObj.getString("TenNV"));
                        listMNV.add(info);
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
                storeMNVList(listMNV);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//			if (pDialog.isShowing())
//				pDialog.dismiss();
            populateSpinnerMNV();
        }
    }

    private void populateSpinnerMKH()
    {
        listMKH = loadMKHFromSharePreferences();
        List<String> labels = new ArrayList<>();
        for(int i = 0; i < listMKH.size(); i++)
        {
            labels.add(listMKH.get(i).getID() + " - " + listMKH.get(i).getData());
        }
        Collections.sort(labels);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.custom_spinner_item, labels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMKH.setAdapter(spinnerAdapter);
    }

    private void populateSpinnerMNV()
    {
        listMNV = loadMNVFromSharePreferences();
        List<String> labels = new ArrayList<>();
        for(int i = 0; i < listMNV.size(); i++)
        {
            labels.add(listMNV.get(i).getID() + " - " + listMNV.get(i).getData());
        }
        Collections.sort(labels);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.custom_spinner_item, labels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMNV.setAdapter(spinnerAdapter);
    }

    public boolean isConnectedToInternet(Context ctx)
    {
        ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message).setTitle("Thông Báo!!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}