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
import java.util.HashSet;
import java.util.Set;

import chau.bankingloan.customThings.CustomTextwatcher;
import chau.bankingloan.customThings.ServerBoldTextview;
import chau.bankingloan.customThings.ServerCheckbox;
import chau.bankingloan.customThings.ServerEditText;
import chau.bankingloan.customThings.ServerInfo;
import chau.bankingloan.customThings.ServerSpinner;
import chau.bankingloan.customThings.ServerTvDate;
import chau.bankingloan.customThings.ServiceHandler;

/**
 * Created on 01-07-2016 by com08.
 */
public class Tab4Fragment extends Fragment {
    View rootView;
    LinearLayout lnrTab4;
    ArrayList<ServerInfo> arrayListTab4;

    ProgressDialog progressDialog;
    ImageButton imgBtnNext, imgBtnPre, imgBtnRefresh;
    View.OnClickListener listenerNext, listenerPre, listenerRefresh;

    SharedPreferences preferences;

    ServerEditText edResult;

    public String arrSpinner = "";
    String json;
    JSONObject object;
    JSONArray array;
    TextWatcher textWatcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_4, container, false);
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
                    for (int j = 0; i < arrayListTab4.size(); i++) {
                        if(arrayListTab4.get(i).getType().equals("edPlusNumberA"))
                        {
                            t = t+Integer.valueOf((String)arrayListTab4.get(i).getData());
                        }
                        edResult.setValue(String.valueOf(t));
                    }
                }
                catch (Exception e)
                {}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        imgBtnNext.setOnClickListener(listenerNext);
        imgBtnPre.setOnClickListener(listenerPre);
        imgBtnRefresh.setOnClickListener(listenerRefresh);

        return rootView;
    }

    private void initWidget()
    {
        lnrTab4 = (LinearLayout)rootView.findViewById(R.id.lnrTab4);
        imgBtnNext = (ImageButton)rootView.findViewById(R.id.imgBtnNextTab4);
        imgBtnPre = (ImageButton)rootView.findViewById(R.id.imgBtnPreTab4);
        imgBtnRefresh = (ImageButton)rootView.findViewById(R.id.imgBtnRefreshTab4);
        arrayListTab4 = new ArrayList<>();
        preferences = this.getActivity().getSharedPreferences("TAB4", Context.MODE_APPEND);
    }

    private void initListener()
    {
        listenerNext = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckFields())
                {
                    SaveData();
                    Log.e("ChauVu", preferences.getAll().toString());
                    Toast.makeText(getContext(), "Tezuka", Toast.LENGTH_SHORT).show();
                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(4);
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

    private void SaveData()
    {
        try {
            int i;
            SharedPreferences.Editor editor = preferences.edit();
            Set<String> set = new HashSet<>();
            editor.clear().apply();
            for (i = 0; i < arrayListTab4.size(); i++) {
                if (!arrayListTab4.get(i).getType().equals("textviewColumn")) {
                    String fieldValue = (String) arrayListTab4.get(i).getData();
                    editor.putString(arrayListTab4.get(i).getLabel().trim().replace(" ", "").replace(":", ""), fieldValue);

//                    set.add(arrayListTab4.get(i).jsonObject().toString());
                }
                if(arrayListTab4.get(i).getType().equals("edPlusResultA"))
                {
                    editor.putString(arrayListTab4.get(i).getLabel().trim().replace(" ", "").replace(":", ""), edResult.getValue());
                }
            }
//            editor.putStringSet("tab4", set);
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
            int t = 0;
            boolean good = true;
            for(i = 0; i < arrayListTab4.size(); i++)
            {
                String fieldValue = (String) arrayListTab4.get(i).getData();
                if (arrayListTab4.get(i).isRequired()) {
                    if (fieldValue == null) {
                        good = false;
                    } else {
                        if (fieldValue.trim().length() == 0) {
                            good = false;
                        }
                    }
                }

                if(arrayListTab4.get(i).getType().equals("edPlusNumberA"))
                {
                    t = t+Integer.valueOf((String)arrayListTab4.get(i).getData());
                }
            }
            if(t > 0) {
                Log.e("NUMBER", String.valueOf(t));
                edResult.setValue(String.valueOf(t));
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
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Getting Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            lnrTab4.removeAllViews();
            arrayListTab4.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            json = sh.makeServiceCall(MainActivity.TAB_4_LINK, ServiceHandler.GET);
            if(json!= null)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    array = jsonObject.getJSONArray("tab4");
                    for(int i = 0; i < array.length(); i++) {
                        object = (JSONObject) array.get(i);
                        ServerInfo serverInfo = new ServerInfo(object.getString("label"),
                                object.getString("type"), object.getString("value"),
                                object.getString("url"),
                                object.getString("column"), object.getBoolean("require"));
                        arrayListTab4.add(serverInfo);
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
                layoutParams.setMargins(15,10,10,15);

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

                for (int i = 0; i < arrayListTab4.size(); i++)
                {
                    if(arrayListTab4.get(i).getType().equals("textviewColumn"))
                    {
                        if(arrayListTab4.get(i).getColumn().equals("1")) {
                            arrayListTab4.get(i).obj = new ServerBoldTextview(getContext(), arrayListTab4.get(i).getLabel(), true);
                            l1.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                        if(arrayListTab4.get(i).getColumn().equals("2")){
                            arrayListTab4.get(i).obj = new ServerBoldTextview(getContext(), arrayListTab4.get(i).getLabel(), true);
                            l2.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab4.get(i).getType().equals("spinner"))
                    {
                        arrSpinner = new SpinnerData(arrayListTab4.get(i).getUrl(), arrayListTab4.get(i).getLabel()).execute().get();
                        if(arrayListTab4.get(i).getColumn().equals("1")) {
                            if(arrSpinner.equals(""))
                            {
                                arrayListTab4.get(i).obj = new ServerSpinner(getContext(),
                                        arrayListTab4.get(i).getLabel()
                                        , arrayListTab4.get(i).getValue());
                                Toast.makeText(getContext(), "Can not get " + arrayListTab4.get(i).getLabel().replace(":", "") + " from Server!", Toast.LENGTH_SHORT).show();
                            }
                            else
                                arrayListTab4.get(i).obj = new ServerSpinner(getContext(),
                                        arrayListTab4.get(i).getLabel(), arrSpinner);
                            Log.e("DISPLAY", arrayListTab4.get(i).getLabel());
                            Log.e("DISPLAY", arrSpinner);
                            l1.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                        if(arrayListTab4.get(i).getColumn().equals("2")){
                            arrayListTab4.get(i).obj = new ServerSpinner(getContext(), arrayListTab4.get(i).getLabel(), arrayListTab4.get(i).getValue());
                            l2.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab4.get(i).getType().equals("edittext"))
                    {
                        if(arrayListTab4.get(i).getColumn().equals("1")) {
                            arrayListTab4.get(i).obj = new ServerEditText(getContext(), arrayListTab4.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l1.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                        if(arrayListTab4.get(i).getColumn().equals("2")){
                            arrayListTab4.get(i).obj = new ServerEditText(getContext(), arrayListTab4.get(i).getLabel(), EditorInfo.TYPE_CLASS_TEXT);
                            l2.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab4.get(i).getType().equals("edittextnumber"))
                    {
                        if(arrayListTab4.get(i).getColumn().equals("1")) {
                            arrayListTab4.get(i).obj = new ServerEditText(getContext(), arrayListTab4.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            l1.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                        if(arrayListTab4.get(i).getColumn().equals("2")){
                            arrayListTab4.get(i).obj = new ServerEditText(getContext(), arrayListTab4.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            l2.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab4.get(i).getType().equals("edittextemail"))
                    {
                        if(arrayListTab4.get(i).getColumn().equals("1")) {
                            arrayListTab4.get(i).obj = new ServerEditText(getContext(), arrayListTab4.get(i).getLabel(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            l1.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                        if(arrayListTab4.get(i).getColumn().equals("2")){
                            arrayListTab4.get(i).obj = new ServerEditText(getContext(), arrayListTab4.get(i).getLabel(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            l2.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab4.get(i).getType().equals("textviewDate"))
                    {
                        if(arrayListTab4.get(i).getColumn().equals("1")){
                            arrayListTab4.get(i).obj = new ServerTvDate(getContext(), arrayListTab4.get(i).getLabel(), "Choose Date");
                            l1.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                        if(arrayListTab4.get(i).getColumn().equals("2")){
                            arrayListTab4.get(i).obj = new ServerTvDate(getContext(), arrayListTab4.get(i).getLabel(), "Choose Date");
                            l2.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab4.get(i).getType().equals("checkbox"))
                    {
                        if(arrayListTab4.get(i).getColumn().equals("1")){
                            arrayListTab4.get(i).obj = new ServerCheckbox(getContext(), arrayListTab4.get(i).getLabel());
                            l1.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                        if(arrayListTab4.get(i).getColumn().equals("2")){
                            arrayListTab4.get(i).obj = new ServerCheckbox(getContext(), arrayListTab4.get(i).getLabel());
                            l2.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab4.get(i).getType().equals("edPlusNumberA")) {
                        if(arrayListTab4.get(i).getColumn().equals("1")) {
                            arrayListTab4.get(i).obj = new ServerEditText(getContext(), arrayListTab4.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, textWatcher);
                            l1.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                        if(arrayListTab4.get(i).getColumn().equals("2")){
                            arrayListTab4.get(i).obj = new ServerEditText(getContext(), arrayListTab4.get(i).getLabel(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, textWatcher);
                            l2.addView((View) arrayListTab4.get(i).obj, layoutParams);
                        }
                    }
                    if (arrayListTab4.get(i).getType().equals("edPlusResultA")) {
                        if(arrayListTab4.get(i).getColumn().equals("1")) {
                            edResult = new ServerEditText(getContext(), "0",
                                    InputType.TYPE_CLASS_NUMBER
                                            | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        }
                        if(arrayListTab4.get(i).getColumn().equals("2")){
                            edResult = new ServerEditText(getContext(), arrayListTab4.get(i).getLabel(),
                                    InputType.TYPE_CLASS_NUMBER
                                            | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            edResult.setEnabled(false);
                            l2.addView(edResult, layoutParams);
                        }
                    }
                }
                lnrTab4.addView(l1);
                lnrTab4.addView(l2);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public class SpinnerData extends AsyncTask<Void, Void, String> {
        String url;
        String key;
        String arr;
        JSONArray array;
        JSONObject object;
        String jsonSpinner;

        public SpinnerData(String url, String key) {
            this.url = url;
            this.key = key;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arr = "";
        }

        @Override
        protected String doInBackground(Void... strings) {
            ServiceHandler jsonParser = new ServiceHandler();
            if(url.isEmpty())
            {
                arr = "";
            }
            else {
                jsonSpinner = jsonParser.makeServiceCall(url, ServiceHandler.GET);
                if (jsonSpinner != null) {
                    try {
                        object = new JSONObject(jsonSpinner);
                        array = object.getJSONArray(key.trim().replace(":", "")
                                .replace(" ", ""));
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = (JSONObject) array.get(i);
                            arr += jsonObject.getString("DATA") + ",";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else arr = "";
            }
            return arr;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            Log.e("EXECUTION", key);
            Log.e("EXECUTION", aVoid);
        }
    }
}
