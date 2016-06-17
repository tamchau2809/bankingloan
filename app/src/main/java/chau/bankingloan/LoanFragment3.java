package chau.bankingloan;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import chau.bankingloan.customThings.BoldTextview;
import chau.bankingloan.customThings.EditTextServer;
import chau.bankingloan.customThings.InfoView;
import chau.bankingloan.customThings.ServiceHandler;
import chau.bankingloan.customThings.SpinnerServer;
import chau.bankingloan.customThings.TextViewDate;
import chau.bankingloan.customThings.URLConnect;

/**
 * Created on 13-Jun-16 by com08.
 */
public class LoanFragment3 extends Fragment implements View.OnClickListener
{
    View rootView;
    LinearLayout lnrMain;
    ArrayList<InfoView> infoViewArrayList;

    FloatingActionButton fabRefresh;
    ProgressDialog pDialog;

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
        infoViewArrayList = new ArrayList<>();
        new GetData().execute();

        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetData().execute();
            }
        });

        return rootView;
    }

    public void initWidget()
    {
        lnrMain = (LinearLayout)rootView.findViewById(R.id.lnrMain);
        fabRefresh = (FloatingActionButton)rootView.findViewById(R.id.fabRef);
    }

    @Override
    public void onClick(View v) {

    }

    private class GetData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Fetching Information...");
            pDialog.setCancelable(false);
            pDialog.show();
            infoViewArrayList.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            json = sh.makeServiceCall(URLConnect.GET_LOAN_FRAG, ServiceHandler.GET);
            if(json!= null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    array = jsonObject.getJSONArray("loandetails");
                    for(int i = 0; i < array.length(); i++) {
                        viewObj = (JSONObject) array.get(i);
                        InfoView infoView = new InfoView(viewObj.getString("label"),
                                viewObj.getString("type"), viewObj.getString("value"));
                        infoViewArrayList.add(infoView);
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
            if (pDialog.isShowing())
                pDialog.dismiss();
            lnrMain.setWeightSum(2);

            LinearLayout ln = new LinearLayout(getContext());
            ln.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,30);

            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            LinearLayout l1,l2;
            l1 = new LinearLayout(getContext());
            l1.setOrientation(LinearLayout.VERTICAL);
            l1.setLayoutParams(layoutParams1);
            l2 = new LinearLayout(getContext());
            l2.setOrientation(LinearLayout.VERTICAL);
            l2.setLayoutParams(layoutParams1);
//
            BoldTextview boldTextview = new BoldTextview(getContext(), infoViewArrayList.get(1).getLabel(), false);
//            BoldTextview boldTextview1 = new BoldTextview(getContext(), infoViewArrayList.get(2).getLabel(), false);
//            BoldTextview boldTextview2 = new BoldTextview(getContext(), infoViewArrayList.get(3).getLabel(), false);
//            BoldTextview boldTextview3 = new BoldTextview(getContext(), infoViewArrayList.get(4).getLabel(), false);
//            BoldTextview boldTextview4 = new BoldTextview(getContext(), infoViewArrayList.get(5).getLabel(), false);
//
//
//            ln.addView(new SpinnerServer(getContext(), infoViewArrayList.get(1).getLabel(), infoViewArrayList.get(1).getValue()),0);
//            ln.addView(boldTextview, 0);
            l1.addView(new BoldTextview(getContext(), infoViewArrayList.get(0).getLabel(), true));
            l1.addView(new SpinnerServer(getContext(), infoViewArrayList.get(1).getLabel(), infoViewArrayList.get(1).getValue()),layoutParams);
            l1.addView(new EditTextServer(getContext(), infoViewArrayList.get(2).getLabel(), EditorInfo.TYPE_CLASS_NUMBER),layoutParams);
            l1.addView(new SpinnerServer(getContext(), infoViewArrayList.get(3).getLabel(), infoViewArrayList.get(3).getValue()),layoutParams);
            l1.addView(new SpinnerServer(getContext(), infoViewArrayList.get(4).getLabel(), infoViewArrayList.get(4).getValue()),layoutParams);
            l1.addView(new EditTextServer(getContext(), infoViewArrayList.get(5).getLabel(), EditorInfo.TYPE_CLASS_NUMBER),layoutParams);
            l2.addView(new BoldTextview(getContext(), infoViewArrayList.get(6).getLabel(), true));
            l2.addView(new EditTextServer(getContext(), infoViewArrayList.get(7).getLabel(), EditorInfo.TYPE_CLASS_NUMBER),layoutParams);
            l2.addView(new TextViewDate(getContext(), infoViewArrayList.get(8).getLabel()), layoutParams);
            lnrMain.addView(l1);
            lnrMain.addView(l2);

//
//            lnr1.addView(boldTextview1, 1, layoutParams);
//            lnr2.addView(new EditTextServer(getContext(), EditorInfo.TYPE_CLASS_NUMBER),1,layoutParams);
//
//            lnr1.addView(boldTextview2, 2, layoutParams);
//            lnr2.addView(new SpinnerServer(getContext(), infoViewArrayList.get(3).getLabel(), infoViewArrayList.get(3).getValue()),2);
//
//            lnr1.addView(boldTextview3, 3, layoutParams);
//            lnr2.addView(new SpinnerServer(getContext(), infoViewArrayList.get(4).getLabel(), infoViewArrayList.get(4).getValue()),3);
//
//            lnr1.addView(boldTextview4, 4, layoutParams);
//            lnr2.addView(new EditTextServer(getContext(), EditorInfo.TYPE_CLASS_NUMBER),4,layoutParams);


        }
    }
}
