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
 * Created on 13-Jun-16 by com08.
 */
public class LoanFragment3 extends Fragment
{
    View rootView;
    LinearLayout lnrTab1;
    Vector<DynamicInfo> dynamicInfos;

    ProgressDialog progressDialog;
    FloatingActionButton fabNext, fabRefresh;
    SharedPreferences LoanDetails;

    View.OnClickListener fabNextListener, fabRefreshListener;

    String json;
    JSONObject viewObj;
    JSONArray array;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_loan_3, container, false);
        initWidget();
        initListener();

        LoanDetails = this.getActivity().getSharedPreferences("LOAN_1", Context.MODE_APPEND);

        new GetData().execute();

        fabNext.setOnClickListener(fabNextListener);
        fabRefresh.setOnClickListener(fabRefreshListener);

        return rootView;
    }

    public void initWidget()
    {
        lnrTab1 = (LinearLayout)rootView.findViewById(R.id.lnrTab1);
        fabNext = (FloatingActionButton)rootView.findViewById(R.id.fabNextF3);
        fabRefresh = (FloatingActionButton)rootView.findViewById(R.id.fabRefreshF3);

        dynamicInfos = new Vector<>();
    }

    public void initListener()
    {
        fabNextListener = new View.OnClickListener() {
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

        fabRefreshListener = new View.OnClickListener() {
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
            lnrTab1.removeAllViews();
            dynamicInfos.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            json = sh.makeServiceCall(URLConnect.GET_TAB_1, ServiceHandler.GET);
            if(json!= null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    array = jsonObject.getJSONArray("tab1");
                    for(int i = 0; i < array.length(); i++) {
                        viewObj = (JSONObject) array.get(i);
                        DynamicInfo dynamicInfo = new DynamicInfo(viewObj.getString("label"),
                                viewObj.getString("type"), viewObj.getString("value"),
                                viewObj.getString("column"), viewObj.getBoolean("require"));
                        dynamicInfos.add(dynamicInfo);
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
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            DisplayForm(dynamicInfos);
        }

        private void DisplayForm(Vector<DynamicInfo> arrayList)
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

                for (int i = 0; i < arrayList.size(); i++)
                {
                    if(arrayList.get(i).getType().equals("textviewColumn"))
                    {
                        if(arrayList.get(i).getColumn().equals("1")) {
                            dynamicInfos.elementAt(i).obj = new BoldTextview(getContext(), dynamicInfos.elementAt(i).getLabel(), true);
                            l1.addView((View)dynamicInfos.elementAt(i).obj, layoutParams);
                        }
                        if(arrayList.get(i).getColumn().equals("2")){
                            dynamicInfos.elementAt(i).obj = new BoldTextview(getContext(), dynamicInfos.elementAt(i).getLabel(), true);
                            l2.addView((View)dynamicInfos.elementAt(i).obj, layoutParams);
                        }
                    }
                    if (arrayList.get(i).getType().equals("spinner"))
                    {
                        if(arrayList.get(i).getColumn().equals("1")) {
                            dynamicInfos.elementAt(i).obj = new SpinnerServer(getContext(), dynamicInfos.get(i).getLabel(), dynamicInfos.get(i).getValue());
                            l1.addView((View)dynamicInfos.elementAt(i).obj, layoutParams);
                        }
                        if(arrayList.get(i).getColumn().equals("2")){
                            dynamicInfos.elementAt(i).obj = new SpinnerServer(getContext(), dynamicInfos.get(i).getLabel(), dynamicInfos.get(i).getValue());
                            l2.addView((View)dynamicInfos.elementAt(i).obj, layoutParams);
                        }
                    }
                    if (arrayList.get(i).getType().equals("edittext"))
                    {
                        if(arrayList.get(i).getColumn().equals("1")) {
                            dynamicInfos.elementAt(i).obj = new EditTextServer(getContext(), dynamicInfos.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l1.addView((View)dynamicInfos.elementAt(i).obj, layoutParams);
                        }
                        if(arrayList.get(i).getColumn().equals("2")){
                            dynamicInfos.elementAt(i).obj = new EditTextServer(getContext(), dynamicInfos.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l2.addView((View)dynamicInfos.elementAt(i).obj, layoutParams);
                        }
                    }
                    if (arrayList.get(i).getType().equals("textviewDate"))
                    {
                        if(arrayList.get(i).getColumn().equals("1")){
                            dynamicInfos.elementAt(i).obj = new TextViewDate(getContext(), dynamicInfos.get(i).getLabel(), "Choose Date");
                            l1.addView((View)dynamicInfos.elementAt(i).obj, layoutParams);
                        }
                        if(arrayList.get(i).getColumn().equals("2")){
                            dynamicInfos.elementAt(i).obj = new TextViewDate(getContext(), dynamicInfos.get(i).getLabel(), "Choose Date");
                            l2.addView((View)dynamicInfos.elementAt(i).obj, layoutParams);
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
            SharedPreferences.Editor editor = LoanDetails.edit();
            editor.clear().apply();
            for (i = 0; i < dynamicInfos.size(); i++) {
                String fieldValue = (String) dynamicInfos.get(i).getData();
                editor.putString(dynamicInfos.elementAt(i).getLabel().toString().trim().replace(" ", "").replace(":",""), fieldValue);
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
            for (i=0;i<dynamicInfos.size();i++) {
                String fieldValue = (String) dynamicInfos.get(i).getData();
                Log.i("ChauVu", dynamicInfos.elementAt(i).getLabel() + " is [" + fieldValue + "]" + "\n------------------------");
                if (dynamicInfos.elementAt(i).isRequired()) {
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
