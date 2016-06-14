package chau.bankingloan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import chau.bankingloan.customThings.URLConnect;

/**
 * Created on 13-Jun-16 by com08.
 */
public class LoanFragment3 extends Fragment implements View.OnClickListener
{
    View rootView;
    LinearLayout lnr1, lnr2, lnr3, lnr4;
    ArrayList<InfoView> infoViewArrayList;

    String json, category1, category2;
    BoldTextview boldTextview;
    JSONObject viewObj;
    JSONArray array;
    String[] arr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_loan_3, container, false);
        initWidget();
        lnr1.setDividerPadding(LinearLayout.SHOW_DIVIDER_MIDDLE);


        lnr2.setDividerPadding(LinearLayout.SHOW_DIVIDER_MIDDLE);
        infoViewArrayList = new ArrayList<>();
        new GetData().execute();

        return rootView;
    }

    public void initWidget()
    {
        lnr1 = (LinearLayout)rootView.findViewById(R.id.lnr1);
        lnr2 = (LinearLayout)rootView.findViewById(R.id.lnr2);
        lnr3 = (LinearLayout)rootView.findViewById(R.id.lnr3);
        lnr4 = (LinearLayout)rootView.findViewById(R.id.lnr4);
    }

    @Override
    public void onClick(View v) {

    }

    private class GetData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,20);

            BoldTextview boldTextview = new BoldTextview(getContext(), infoViewArrayList.get(1).getLabel(), true);
            BoldTextview boldTextview1 = new BoldTextview(getContext(), infoViewArrayList.get(2).getLabel(), true);

//            lnr2.addView(new BoldTextview(getContext(), infoViewArrayList.get(0).getLabel(), true));
            lnr2.addView(new SpinnerServer(getContext(), infoViewArrayList.get(1).getLabel(), infoViewArrayList.get(1).getValue()),0);
            lnr1.addView(boldTextview, 0, layoutParams);

            lnr1.addView(boldTextview1, 1, layoutParams);
            lnr2.addView(new EditTextServer(getContext(), EditorInfo.TYPE_CLASS_NUMBER),1,layoutParams);
//            lnr1.addView(new EditTextServer(getContext(), infoViewArrayList.get(2).getLabel(), EditorInfo.TYPE_CLASS_NUMBER), 2);
//            lnr1.addView(new SpinnerServer(getContext(), infoViewArrayList.get(3).getLabel(), infoViewArrayList.get(3).getValue()), 3);
//            lnr2.addView(new BoldTextview(getContext(), infoViewArrayList.get(6).getLabel(), true));

        }
    }
}
