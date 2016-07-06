package chau.bankingloan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import chau.bankingloan.customThings.ServerBoldTextview;
import chau.bankingloan.customThings.ServerCheckbox;
import chau.bankingloan.customThings.ServerInfo;
import chau.bankingloan.customThings.ServerEditText;
import chau.bankingloan.customThings.ServiceHandler;
import chau.bankingloan.customThings.ServerSpinner;
import chau.bankingloan.customThings.ServerTvDate;

/**
 * Created on 13-Jun-16 by com08.
 */
public class Tab1Fragment extends Fragment
{
    View rootView;
    LinearLayout lnrTab1;
    ArrayList<ServerInfo> serverInfos;

    ProgressDialog progressDialog;
    ImageButton imgBtnNext, imgBtnRefresh;
    SharedPreferences preferences;

    View.OnClickListener listenerNext, listenerRef;

    String json;
    JSONObject object;
    JSONArray array;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_1, container, false);
        initWidget();
        initListener();

        preferences = this.getActivity().getSharedPreferences("TAB1", Context.MODE_APPEND);

        new GetData().execute();

        imgBtnNext.setOnClickListener(listenerNext);
        imgBtnRefresh.setOnClickListener(listenerRef);

        return rootView;
    }

    public void initWidget()
    {
        lnrTab1 = (LinearLayout)rootView.findViewById(R.id.lnrTab1);
        imgBtnNext = (ImageButton) rootView.findViewById(R.id.imgBtnNextTab1);
        imgBtnRefresh = (ImageButton) rootView.findViewById(R.id.imgBtnRefreshTab1);

        serverInfos = new ArrayList<>();
    }

    public void initListener()
    {
        listenerNext = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckFields()) {
                    SaveData();
                    Log.e("ChauVu", preferences.getAll().toString());
                    Toast.makeText(getContext(), "Tezuka", Toast.LENGTH_SHORT).show();
                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(1);
                }
                else
                {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                    builder.setMessage("Please FIll In The Blank...");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }
        };

        listenerRef = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetData().execute();
            }
        };
    }

    private class GetData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Getting Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            lnrTab1.removeAllViews();
            serverInfos.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            json = sh.makeServiceCall(MainActivity.TAB_1_LINK, ServiceHandler.GET);
            if(json!= null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    array = jsonObject.getJSONArray("tab1");
                    for(int i = 0; i < array.length(); i++) {
                        object = (JSONObject) array.get(i);
                        ServerInfo serverInfo = new ServerInfo(object.getString("label"),
                                object.getString("type"), object.getString("value"),
                                object.getString("column"), object.getBoolean("require"));
                        serverInfos.add(serverInfo);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("TESST", MainActivity.TAB_1_LINK);
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            DisplayForm();
        }

        private void DisplayForm()
        {
            try
            {
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(15,15,15,15);

                LinearLayout.LayoutParams layoutParams1 =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                LinearLayout l1,l2;
                l1 = new LinearLayout(getContext());
                l1.setOrientation(LinearLayout.VERTICAL);
                l1.setLayoutParams(layoutParams1);
                l2 = new LinearLayout(getContext());
                l2.setOrientation(LinearLayout.VERTICAL);
                l2.setLayoutParams(layoutParams1);

                for (int i = 0; i < serverInfos.size(); i++)
                {
                    if(serverInfos.get(i).getType().equals("textviewColumn"))
                    {
                        if(serverInfos.get(i).getColumn().equals("1")) {
                            serverInfos.get(i).obj = new ServerBoldTextview(getContext(), serverInfos.get(i).getLabel(), true);
                            l1.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                        if(serverInfos.get(i).getColumn().equals("2")){
                            serverInfos.get(i).obj = new ServerBoldTextview(getContext(), serverInfos.get(i).getLabel(), true);
                            l2.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                    }

                    if (serverInfos.get(i).getType().equals("spinner"))
                    {
                        if(serverInfos.get(i).getColumn().equals("1")) {
                            serverInfos.get(i).obj = new ServerSpinner(getContext(), serverInfos.get(i).getLabel(), serverInfos.get(i).getValue());
                            l1.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                        if(serverInfos.get(i).getColumn().equals("2")){
                            serverInfos.get(i).obj = new ServerSpinner(getContext(), serverInfos.get(i).getLabel(), serverInfos.get(i).getValue());
                            l2.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                    }
                    if (serverInfos.get(i).getType().equals("edittext"))
                    {
                        if(serverInfos.get(i).getColumn().equals("1")) {
                            serverInfos.get(i).obj = new ServerEditText(getContext(), serverInfos.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l1.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                        if(serverInfos.get(i).getColumn().equals("2")){
                            serverInfos.get(i).obj = new ServerEditText(getContext(), serverInfos.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l2.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                    }
                    if (serverInfos.get(i).getType().equals("edittextnumber"))
                    {
                        if(serverInfos.get(i).getColumn().equals("1")) {
                            serverInfos.get(i).obj = new ServerEditText(getContext(), serverInfos.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            l1.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                        if(serverInfos.get(i).getColumn().equals("2")){
                            serverInfos.get(i).obj = new ServerEditText(getContext(), serverInfos.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            l2.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                    }
                    if (serverInfos.get(i).getType().equals("edittextemail"))
                    {
                        if(serverInfos.get(i).getColumn().equals("1")) {
                            serverInfos.get(i).obj = new ServerEditText(getContext(), serverInfos.get(i).getLabel(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            l1.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                        if(serverInfos.get(i).getColumn().equals("2")){
                            serverInfos.get(i).obj = new ServerEditText(getContext(), serverInfos.get(i).getLabel(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            l2.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                    }
                    if (serverInfos.get(i).getType().equals("textviewDate"))
                    {
                        if(serverInfos.get(i).getColumn().equals("1")){
                            serverInfos.get(i).obj = new ServerTvDate(getContext(), serverInfos.get(i).getLabel(), "Choose Date");
                            l1.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                        if(serverInfos.get(i).getColumn().equals("2")){
                            serverInfos.get(i).obj = new ServerTvDate(getContext(), serverInfos.get(i).getLabel(), "Choose Date");
                            l2.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                    }

                    if (serverInfos.get(i).getType().equals("checkbox"))
                    {
                        if(serverInfos.get(i).getColumn().equals("1")){
                            serverInfos.get(i).obj = new ServerCheckbox(getContext(), serverInfos.get(i).getLabel());
                            l1.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                        if(serverInfos.get(i).getColumn().equals("2")){
                            serverInfos.get(i).obj = new ServerCheckbox(getContext(), serverInfos.get(i).getLabel());
                            l2.addView((View) serverInfos.get(i).obj, layoutParams);
                        }
                    }
                }
                lnrTab1.addView(l1);
                lnrTab1.addView(l2);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void SaveData()
    {
        try {
            int i;
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear().apply();
            for (i = 0; i < serverInfos.size(); i++) {
                String fieldValue = (String) serverInfos.get(i).getData();
                editor.putString(serverInfos.get(i).getLabel().toString().trim().replace(" ", "").replace(":",""), fieldValue);
            }
            editor.apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean CheckFields()
    {
        try
        {
            int i;
            boolean good = true;
            for (i=0; i< serverInfos.size(); i++) {
                String fieldValue = (String) serverInfos.get(i).getData();
                Log.i("ChauVu", serverInfos.get(i).getLabel() + " is [" + fieldValue + "]" + "\n------------------------");
                if (serverInfos.get(i).isRequired()) {
                    if (fieldValue == null) {
                        good = false;
                    } else {
                        if (fieldValue.trim().length() == 0) {
                            good = false;
                        }
                    }
                }
            }
            return good;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
