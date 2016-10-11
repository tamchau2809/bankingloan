package chau.bankingloan.customThings;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by com08 (10-Oct-16).
 */

public class SpinnerData extends AsyncTask<Void, Void, String> {
    private String url;
    private String key;
    private String arr;

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
        ConnectURL connectURL = new ConnectURL();
        if(url.isEmpty())
        {
            arr = "";
        }
        else {
            String jsonSpinner = connectURL.makeServiceCall(url, ConnectURL.GET);
            if (jsonSpinner != null) {
                try {
                    JSONObject object = new JSONObject(jsonSpinner);
                    JSONArray array = object.getJSONArray(key.trim().replace(":", "")
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
    }
}
