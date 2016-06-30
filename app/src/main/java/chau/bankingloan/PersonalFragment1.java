package chau.bankingloan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import chau.bankingloan.customThings.BoldTextview;
import chau.bankingloan.customThings.DynamicInfo;
import chau.bankingloan.customThings.EditTextServer;
import chau.bankingloan.customThings.ServiceHandler;
import chau.bankingloan.customThings.SpinnerServer;
import chau.bankingloan.customThings.TextViewDate;
import chau.bankingloan.customThings.URLConnect;

/**
 * Created on 29-06-2016 by com08.
 */
public class PersonalFragment1 extends Fragment
{
    View rootView;
    LinearLayout lnrTab2;
    Vector<DynamicInfo> dynamicInfo;

    ProgressDialog progressDialog;
    FloatingActionButton fabNext, fabPre, fabRefresh;
    SharedPreferences PersonalDetails;

    View.OnClickListener listenerRefresh, listenerNext, listenerPre;

    String json;
    JSONObject object;
    JSONArray array;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_personal_1, container, false);
        initWidget();
        initListener();

        new GetData().execute();

        fabRefresh.setOnClickListener(listenerRefresh);
        fabNext.setOnClickListener(listenerNext);
        fabPre.setOnClickListener(listenerPre);

        return rootView;
    }

    public void initWidget()
    {
        lnrTab2 = (LinearLayout)rootView.findViewById(R.id.lnrTab2);
        fabRefresh = (FloatingActionButton)rootView.findViewById(R.id.fabRefreshPersonal);
        fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabNextPersonal);
        fabPre = (FloatingActionButton)rootView.findViewById(R.id.fabPrePersonal);
        dynamicInfo = new Vector<>();
        PersonalDetails = this.getActivity().getSharedPreferences("PERSONAL_1", Context.MODE_APPEND);
    }

    public void initListener()
    {
        listenerNext = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckFields()) {
                    SaveData();
                    Toast.makeText(getContext(), "Tezuka", Toast.LENGTH_SHORT).show();
                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(1);
                }
            }
        };

        listenerPre = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity)getActivity();
                act.switchTab(0);
            }
        };

        listenerRefresh = new View.OnClickListener() {
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
            progressDialog.setMessage("Fetching Information...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            lnrTab2.removeAllViews();
            dynamicInfo.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            json = sh.makeServiceCall(URLConnect.GET_TAB_2, ServiceHandler.GET);
            if(json!= null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    array = jsonObject.getJSONArray("tab2");
                    for(int i = 0; i < array.length(); i++) {
                        object = (JSONObject) array.get(i);
                        DynamicInfo dynamicInfo = new DynamicInfo(object.getString("label"),
                                object.getString("type"), object.getString("value"),
                                object.getString("column"), object.getBoolean("require"));
                        PersonalFragment1.this.dynamicInfo.add(dynamicInfo);
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
            if(progressDialog.isShowing())
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

                LinearLayout l1, l2;
                l1 = new LinearLayout(getContext());
                l1.setOrientation(LinearLayout.VERTICAL);
                l1.setLayoutParams(layoutParams1);
                l2 = new LinearLayout(getContext());
                l2.setOrientation(LinearLayout.VERTICAL);
                l2.setLayoutParams(layoutParams1);

                for (int i = 0; i < dynamicInfo.size(); i++)
                {
                    if(dynamicInfo.get(i).getType().equals("textviewColumn"))
                    {
                        if(dynamicInfo.get(i).getColumn().equals("1")) {
                            dynamicInfo.elementAt(i).obj = new BoldTextview(getContext(), dynamicInfo.elementAt(i).getLabel(), true);
                            l1.addView((View) dynamicInfo.elementAt(i).obj, layoutParams);
                        }
                        if(dynamicInfo.get(i).getColumn().equals("2")){
                            dynamicInfo.elementAt(i).obj = new BoldTextview(getContext(), dynamicInfo.elementAt(i).getLabel(), true);
                            l2.addView((View) dynamicInfo.elementAt(i).obj, layoutParams);
                        }
                    }
                    if (dynamicInfo.get(i).getType().equals("spinner"))
                    {
                        if(dynamicInfo.get(i).getColumn().equals("1")) {
                            dynamicInfo.elementAt(i).obj = new SpinnerServer(getContext(), dynamicInfo.get(i).getLabel(), dynamicInfo.get(i).getValue());
                            l1.addView((View) dynamicInfo.elementAt(i).obj, layoutParams);
                        }
                        if(dynamicInfo.get(i).getColumn().equals("2")){
                            dynamicInfo.elementAt(i).obj = new SpinnerServer(getContext(), dynamicInfo.get(i).getLabel(), dynamicInfo.get(i).getValue());
                            l2.addView((View) dynamicInfo.elementAt(i).obj, layoutParams);
                        }
                    }
                    if (dynamicInfo.get(i).getType().equals("edittext"))
                    {
                        if(dynamicInfo.get(i).getColumn().equals("1")) {
                            dynamicInfo.elementAt(i).obj = new EditTextServer(getContext(), dynamicInfo.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l1.addView((View) dynamicInfo.elementAt(i).obj, layoutParams);
                        }
                        if(dynamicInfo.get(i).getColumn().equals("2")){
                            dynamicInfo.elementAt(i).obj = new EditTextServer(getContext(), dynamicInfo.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l2.addView((View) dynamicInfo.elementAt(i).obj, layoutParams);
                        }
                    }
                    if (dynamicInfo.get(i).getType().equals("textviewDate"))
                    {
                        if(dynamicInfo.get(i).getColumn().equals("1")){
                            dynamicInfo.elementAt(i).obj = new TextViewDate(getContext(), dynamicInfo.get(i).getLabel(), "Choose Date");
                            l1.addView((View) dynamicInfo.elementAt(i).obj, layoutParams);
                        }
                        if(dynamicInfo.get(i).getColumn().equals("2")){
                            dynamicInfo.elementAt(i).obj = new TextViewDate(getContext(), dynamicInfo.get(i).getLabel(), "Choose Date");
                            l2.addView((View) dynamicInfo.elementAt(i).obj, layoutParams);
                        }
                    }
                }
                lnrTab2.addView(l1);
                lnrTab2.addView(l2);
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
            SharedPreferences.Editor editor = PersonalDetails.edit();
            editor.clear().apply();
            for (i = 0; i < dynamicInfo.size(); i++) {
                String fieldValue = (String) dynamicInfo.get(i).getData();
                editor.putString(dynamicInfo.elementAt(i).getLabel().trim().replace(" ", "").replace(":",""), fieldValue);
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
            for(i = 0; i < dynamicInfo.size(); i++)
            {
                String fieldValue = (String) dynamicInfo.elementAt(i).getData();
                Log.e("ChauVu", dynamicInfo.elementAt(i).getLabel() + " is [" + fieldValue + "]" + "\n------------------------");
                if (dynamicInfo.elementAt(i).isRequired()) {
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
