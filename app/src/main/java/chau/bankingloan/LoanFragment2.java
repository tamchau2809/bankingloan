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
        contractDetails = this.getActivity().getSharedPreferences("contractDetails", Context.MODE_APPEND);

        final SharedPreferences pref = this.getActivity().getSharedPreferences("MKH", Context.MODE_PRIVATE);
        if(!pref.contains("MKH"))
        {
            if(isConnectedToInternet(getContext())) {
                new GetMKH().execute();
            }
            else
            {
                showAlert("Xin Kiểm Tra Lại Kết Nối!");
            }
        }
        else
        {
            populateSpinnerMKH();
            spinnerMKH.setSelection(contractDetails.getInt("MKH_LOCA", 0));
        }
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

        Set<String> set= new HashSet<String>();
        for (int i = 0; i < list.size(); i++) {
            set.add(list.get(i).getJSONObject().toString());
        }
        editor.putStringSet("MKH", set);
        editor.commit();
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

    public void initWiget()
    {
//        btnCapture = (ImageButton)rootView.findViewById(R.id.btnCapture);
        spinnerMKH = (Spinner)rootView.findViewById(R.id.idLoanType);
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

            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(GET_MKH_URL, ServiceHandler.GET);
            if(json != null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject != null)
                    {
                        JSONArray MKH = jsonObject.getJSONArray("tbkhachhang");
                        for(int i = 0; i < MKH.length(); i++)
                        {
                            JSONObject catObj = (JSONObject)MKH.get(i);
                            InfoFromServer info = new InfoFromServer(catObj.getString("MKH"), catObj.getString("TenKH"));
                            listMKH.add(info);
                        }
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

    private void populateSpinnerMKH()
    {
        listMKH = loadMKHFromSharePreferences();
        List<String> labels = new ArrayList<String>();
        for(int i = 0; i < listMKH.size(); i++)
        {
            labels.add(listMKH.get(i).getID());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.custom_spinner_item, labels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMKH.setAdapter(spinnerAdapter);
    }

    public boolean isConnectedToInternet(Context ctx)
    {
        ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        boolean b = info != null && info.isConnectedOrConnecting();
        return b;
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
