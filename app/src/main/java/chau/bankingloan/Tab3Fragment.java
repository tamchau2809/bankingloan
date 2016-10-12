package chau.bankingloan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

import chau.bankingloan.customThings.ConnectURL;
import chau.bankingloan.customThings.ServerBoldTextview;
import chau.bankingloan.customThings.ServerCheckbox;
import chau.bankingloan.customThings.ServerInfo;
import chau.bankingloan.customThings.ServerEditText;
import chau.bankingloan.customThings.ServerSpinner;
import chau.bankingloan.customThings.ServerTvDate;
import chau.bankingloan.customThings.SpinnerData;

/**
 * Created on 29-06-2016 by com08.
 */
public class Tab3Fragment extends Fragment
{
    View rootView;
    LinearLayout lnrTab3;
    ArrayList<ServerInfo> arrayListTab3;

    ProgressDialog progressDialog;
    ImageButton imgBtnNext, imgBtnPre, imgBtnRefresh;
    SharedPreferences preferences;
    ServerEditText edResult;
    TextWatcher textWatcher;
    public String arrSpinner = "";

    View.OnClickListener listenerRefresh, listenerNext, listenerPre;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_2, container, false);
        initWidget();
        initListener();

        new GetData().execute();

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int t = 0;
                try {
                    for (int j = 0; j < arrayListTab3.size(); j++) {
                        if(arrayListTab3.get(j).getType().equals("edPlusNumberA"))
                        {
                            if(!arrayListTab3.get(j).getData().toString().trim().isEmpty())
                                t = t + Integer.valueOf(arrayListTab3.get(j).getData()
                                        .toString().trim());
                        }
                    }
                    if(t == 0)
                        edResult.setValue(String.valueOf(0));
                    else
                        edResult.setValue(String.valueOf(t));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        imgBtnRefresh.setOnClickListener(listenerRefresh);
        imgBtnNext.setOnClickListener(listenerNext);
        imgBtnPre.setOnClickListener(listenerPre);

        return rootView;
    }

    public void initWidget()
    {
        lnrTab3 = (LinearLayout)rootView.findViewById(R.id.lnrTab2);
        imgBtnRefresh = (ImageButton)rootView.findViewById(R.id.imgBtnRefreshTab2);
        imgBtnNext = (ImageButton)rootView.findViewById(R.id.imgBtnNextTab2);
        imgBtnPre = (ImageButton)rootView.findViewById(R.id.imgBtnPreTab2);
        arrayListTab3 = new ArrayList<>();
        preferences = this.getActivity().getSharedPreferences("TAB2", Context.MODE_APPEND);
    }

    public void initListener()
    {
        listenerNext = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckFields()) {
                    SaveData();
                    Log.e("TEST",  preferences.getAll().toString());
                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(3);
                }
            }
        };

        listenerPre = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity)getActivity();
                act.switchTab(1);
            }
        };

        listenerRefresh = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetData().execute();
            }
        };
    }

    private boolean CheckFields()
    {
        try
        {
            int i;
            boolean good = true;
            for(i = 0; i < arrayListTab3.size(); i++)
            {
                String fieldValue = (String) arrayListTab3.get(i).getData();
                if (arrayListTab3.get(i).isRequired()) {
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

    private class GetData extends AsyncTask<Void, Void, Void>
    {
        String json;
        JSONObject object;
        JSONArray array;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Getting Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            lnrTab3.removeAllViews();
            arrayListTab3.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectURL connectURL = new ConnectURL();
            json = connectURL.makeServiceCall(MainActivity.TAB_3_LINK, ConnectURL.GET);
            if(json!= null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    array = jsonObject.getJSONArray("tab3");
                    for(int i = 0; i < array.length(); i++) {
                        object = (JSONObject) array.get(i);
                        ServerInfo serverInfo = new ServerInfo(object.getString("label"),
                                object.getString("type"), object.getString("value"),
                                object.getString("url"),
                                object.getString("column"), object.getBoolean("require"));
                        arrayListTab3.add(serverInfo);
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

                for (int i = 0; i < arrayListTab3.size(); i++)
                {
                    if(arrayListTab3.get(i).getType().equals("textviewColumn"))
                    {
                        if(arrayListTab3.get(i).getColumn().equals("1")) {
                            arrayListTab3.get(i).obj = new ServerBoldTextview(getContext(), arrayListTab3.get(i).getLabel(), true);
                            l1.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                        if(arrayListTab3.get(i).getColumn().equals("2")){
                            arrayListTab3.get(i).obj = new ServerBoldTextview(getContext(), arrayListTab3.get(i).getLabel(), true);
                            l2.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab3.get(i).getType().equals("spinner"))
                    {
                        SpinnerData spinnerData = new SpinnerData(arrayListTab3.get(i).getUrl(), arrayListTab3.get(i).getLabel());
                        arrSpinner = spinnerData.execute().get();
                        if(arrayListTab3.get(i).getColumn().equals("1")) {
                            if(arrSpinner.equals(""))
                            {
                                arrayListTab3.get(i).obj = new ServerSpinner(getContext(),
                                        arrayListTab3.get(i).getLabel()
                                        , arrayListTab3.get(i).getValue());
                                Toast.makeText(getContext(), "Can not get " + arrayListTab3.get(i).getLabel().replace(":", "") + " from Server!", Toast.LENGTH_SHORT).show();
                            }
                            else
                                arrayListTab3.get(i).obj = new ServerSpinner(getContext(),
                                        arrayListTab3.get(i).getLabel(), arrSpinner);
                            l1.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                        if(arrayListTab3.get(i).getColumn().equals("2")){
                            if(arrSpinner.equals(""))
                            {
                                arrayListTab3.get(i).obj = new ServerSpinner(getContext(),
                                        arrayListTab3.get(i).getLabel()
                                        , arrayListTab3.get(i).getValue());
                                Toast.makeText(getContext(), "Can not get " + arrayListTab3.get(i).getLabel().replace(":", "") + " from Server!", Toast.LENGTH_SHORT).show();
                            }
                            else
                                arrayListTab3.get(i).obj = new ServerSpinner(getContext(),
                                        arrayListTab3.get(i).getLabel(), arrSpinner);
                            l2.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab3.get(i).getType().equals("edittext"))
                    {
                        if(arrayListTab3.get(i).getColumn().equals("1")) {
                            arrayListTab3.get(i).obj = new ServerEditText(getContext(), arrayListTab3.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l1.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                        if(arrayListTab3.get(i).getColumn().equals("2")){
                            arrayListTab3.get(i).obj = new ServerEditText(getContext(), arrayListTab3.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l2.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab3.get(i).getType().equals("edittextnumber"))
                    {
                        if(arrayListTab3.get(i).getColumn().equals("1")) {
                            arrayListTab3.get(i).obj = new ServerEditText(getContext(), arrayListTab3.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            l1.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                        if(arrayListTab3.get(i).getColumn().equals("2")){
                            arrayListTab3.get(i).obj = new ServerEditText(getContext(), arrayListTab3.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            l2.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab3.get(i).getType().equals("edittextemail"))
                    {
                        if(arrayListTab3.get(i).getColumn().equals("1")) {
                            arrayListTab3.get(i).obj = new ServerEditText(getContext(), arrayListTab3.get(i).getLabel(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            l1.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                        if(arrayListTab3.get(i).getColumn().equals("2")){
                            arrayListTab3.get(i).obj = new ServerEditText(getContext(), arrayListTab3.get(i).getLabel(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            l2.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab3.get(i).getType().equals("textviewDate"))
                    {
                        if(arrayListTab3.get(i).getColumn().equals("1")){
                            arrayListTab3.get(i).obj = new ServerTvDate(getContext(), arrayListTab3.get(i).getLabel(), "Choose Date");
                            l1.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                        if(arrayListTab3.get(i).getColumn().equals("2")){
                            arrayListTab3.get(i).obj = new ServerTvDate(getContext(), arrayListTab3.get(i).getLabel(), "Choose Date");
                            l2.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab3.get(i).getType().equals("checkbox"))
                    {
                        if(arrayListTab3.get(i).getColumn().equals("1")){
                            arrayListTab3.get(i).obj = new ServerCheckbox(getContext(), arrayListTab3.get(i).getLabel());
                            l1.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                        if(arrayListTab3.get(i).getColumn().equals("2")){
                            arrayListTab3.get(i).obj = new ServerCheckbox(getContext(), arrayListTab3.get(i).getLabel());
                            l2.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab3.get(i).getType().equals("edPlusNumberA")) {
                        if(arrayListTab3.get(i).getColumn().equals("1")) {
                            arrayListTab3.get(i).obj = new ServerEditText(getContext(), arrayListTab3.get(i).getLabel(),
                                    InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, textWatcher);
                            l1.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                        if(arrayListTab3.get(i).getColumn().equals("2")){
                            arrayListTab3.get(i).obj = new ServerEditText(getContext(), arrayListTab3.get(i).getLabel(),
                                    InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, textWatcher);
                            l2.addView((View) arrayListTab3.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab3.get(i).getType().equals("edPlusResultA")) {
                        if(arrayListTab3.get(i).getColumn().equals("1")) {
                            edResult = new ServerEditText(getContext(), arrayListTab3.get(i).getLabel(),
                                    InputType.TYPE_CLASS_NUMBER
                                            | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            edResult.setEnabled(false);
                            edResult.setValue(String.valueOf(0));
                            l1.addView(edResult, layoutParams);
                        }
                        if(arrayListTab3.get(i).getColumn().equals("2")){
                            edResult = new ServerEditText(getContext(), arrayListTab3.get(i).getLabel(),
                                    InputType.TYPE_CLASS_NUMBER
                                            | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            edResult.setEnabled(false);
                            edResult.setValue(String.valueOf(0));
                            l2.addView(edResult, layoutParams);
                        }
                    }
                }
                lnrTab3.addView(l1);
                lnrTab3.addView(l2);
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
            for (i = 0; i < arrayListTab3.size(); i++) {
                if (!arrayListTab3.get(i).getType().equals("textviewColumn")) {
                    String fieldValue = (String) arrayListTab3.get(i).getData();
                    editor.putString(arrayListTab3.get(i).getLabel().trim().replace(" ", "").replace(":", ""), fieldValue);
                }
                if(arrayListTab3.get(i).getType().equals("edPlusResultA"))
                {
                    editor.putString(arrayListTab3.get(i).getLabel().trim().replace(" ", "").replace(":", ""), edResult.getValue());
                }
            }
            editor.apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
