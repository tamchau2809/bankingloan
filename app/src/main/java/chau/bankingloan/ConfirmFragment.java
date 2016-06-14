package chau.bankingloan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import chau.bankingloan.customThings.URLConnect;

/**
 * Created on 04-May-16 by com08.
 */
public class ConfirmFragment extends Fragment
{


    private static String PIN_SERVER = null;

    View rootView;
    CheckBox cbCorrect, cbAccept;
    TextView tvConfirmName, tvConfirmDoB, tvConfirmId, tvConfirmAdd, tvConfirmTelephone,
            tvConfirmMobile, tvConfirmEmail, tvConfirmWorkingStt, tvConfirmEmployer, tvConfirmEmployerAdd;
    FloatingActionButton fabConfirmPre, fabConfirmNext;
    SharedPreferences personalPreferences, contactPreferences, employmentPreferences, loanPreferences;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_confirm, container, false);

        initWidget();
        personalPreferences = this.getActivity().getSharedPreferences("PERSONAL", Context.MODE_APPEND);
        loadFromPersonal(personalPreferences);
        contactPreferences = this.getActivity().getSharedPreferences("CONTACT", Context.MODE_APPEND);
        loadFromContact(contactPreferences);
        employmentPreferences = this.getActivity().getSharedPreferences("EMPLOYMENT", Context.MODE_APPEND);
        loadFromEmployment(employmentPreferences);
        loanPreferences = this.getActivity().getSharedPreferences("LOAN_DETAILS", Context.MODE_APPEND);

        cbCorrect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    cbCorrect.setError(null);
                } else {
                    cbCorrect.setError("Please Check Your Details!");
                }
            }
        });

        cbAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    cbAccept.setError(null);
                } else {
                    cbAccept.setError("Please Read The Terms and Conditions!");
                }
            }
        });

        fabConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cbCorrect.isChecked()) {
                    cbCorrect.setError("Please Check Your Details!");
                }
                else if (!cbAccept.isChecked())
                {
                    cbAccept.setError("Please Read The Terms and Conditions!");
                }
                else
                {
                    showDialog();
                    new PINCreate().execute();
                }
            }
        });

        fabConfirmPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity) getActivity();
                act.switchTab(4);
            }
        });
        return rootView;
    }

    public void loadFromPersonal(SharedPreferences personal)
    {
        tvConfirmName.setText(personal.getString("name", ""));
        tvConfirmDoB.setText(personal.getString("birthday", ""));
        tvConfirmId.setText(personal.getString("identityNum", ""));
    }

    @SuppressLint("SetTextI18n")
    public void loadFromContact(SharedPreferences contact)
    {
        tvConfirmTelephone.setText(contact.getString("telephone", ""));
        tvConfirmMobile.setText(contact.getString("mobile", ""));
        tvConfirmEmail.setText(contact.getString("email", ""));
        tvConfirmAdd.setText(contact.getString("street", "") + ", "
                + contact.getString("city", ""));
    }

    public void loadFromEmployment(SharedPreferences employment)
    {
        tvConfirmWorkingStt.setText(employment.getString("workingStt", ""));
        tvConfirmEmployer.setText(employment.getString("employer", ""));
        tvConfirmEmployerAdd.setText(employment.getString("employerAdd", ""));
    }

    public void showDialog()
    {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View alertDialogView = factory.inflate(R.layout.otp_dialog, null);
        final EditText edPIN = (EditText) alertDialogView.findViewById(R.id.edConfirmString);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.NUMBER_YOU_VE_BEEN_RECEIVED))
                .setView(alertDialogView)
                .setPositiveButton(
                        android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        View btnTest = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edPIN.getText().toString().equals(PIN_SERVER))
                {
                    new SendInfo().execute();
                    MainActivity act = (MainActivity) getActivity();
                    act.switchTab(6);
                    alertDialog.dismiss();
                }
                else
                {
                    edPIN.setError("Please Check Your PIN again!");
                    edPIN.requestFocus();
                }
            }
        });
    }

    public void initWidget()
    {
        cbAccept = (CheckBox)rootView.findViewById(R.id.cbAccept);
        cbCorrect = (CheckBox)rootView.findViewById(R.id.cbCorrect);
        tvConfirmAdd = (TextView)rootView.findViewById(R.id.tvConfirmAdd);
        tvConfirmName = (TextView)rootView.findViewById(R.id.tvConfirmName);
        tvConfirmDoB = (TextView)rootView.findViewById(R.id.tvConfirmDoB);
        tvConfirmId = (TextView)rootView.findViewById(R.id.tvConfirmId);
        tvConfirmTelephone = (TextView)rootView.findViewById(R.id.tvConfirmTelephone);
        tvConfirmMobile = (TextView)rootView.findViewById(R.id.tvConfirmMobile);
        tvConfirmEmail = (TextView)rootView.findViewById(R.id.tvConfirmEmail);
        tvConfirmWorkingStt = (TextView)rootView.findViewById(R.id.tvComfirmWorkingStt);
        tvConfirmEmployer = (TextView)rootView.findViewById(R.id.tvConfirmEmployer);
        tvConfirmEmployerAdd = (TextView)rootView.findViewById(R.id.tvConfirmEmployerAdd);
        fabConfirmPre = (FloatingActionButton) rootView.findViewById(R.id.fabConfirmPre);
//        fabConfirmPre.setEnabled(false);
        fabConfirmNext = (FloatingActionButton) rootView.findViewById(R.id.fabConfirmNext);
//        fabConfirmNext.setEnabled(false);
    }

    private class SendInfo extends AsyncTask<Void, Float, String>
    {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            HttpClient httpClient = new DefaultHttpClient();
            HttpParams test = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(test, 5000);
            HttpConnectionParams.setSoTimeout(test, 5000);
            HttpPost httpPost = new HttpPost(URLConnect.URL_UPLOAD);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            try
            {
                UploadProgress2.ProgressListener lis = new UploadProgress2.ProgressListener() {

                    @Override
                    public void transferred(float num) {
                        // TODO Auto-generated method stub
                        publishProgress((num));
                    }
                };

                builder.addPart("LOAN_TYPE", new StringBody(loanPreferences.getString("loan_type",""), ContentType.TEXT_PLAIN));
                builder.addPart("TENURE", new StringBody(loanPreferences.getString("tenure",""), ContentType.TEXT_PLAIN));
                builder.addPart("LOAN_PURPOSE", new StringBody(loanPreferences.getString("loan_purpose",""), ContentType.TEXT_PLAIN));

                httpPost.setEntity(new UploadProgress2(builder.build(), lis));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if(statusCode == 200)
                {
                    response = EntityUtils.toString(httpEntity);
                }
                else
                {
                    response = "Error: + " + statusCode;
                }
            } catch (IOException e) {
                response = e.toString();
            }
            catch(Exception ignored)
            {}
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }

    private class PINCreate extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            HttpClient httpClient = new DefaultHttpClient();
            HttpParams test = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(test, 5000);
            HttpConnectionParams.setSoTimeout(test, 5000);
            HttpPost httpPost = new HttpPost(URLConnect.PIN_GENERATE);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            try {
                String email = contactPreferences.getString("email","");
                builder.addPart("EMAIL", new StringBody(email, ContentType.TEXT_PLAIN));
                httpPost.setEntity(builder.build());

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if(statusCode == 200)
                {
                    response = EntityUtils.toString(httpEntity);
                }
                else
                {
                    response = "Error: + " + statusCode;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            PIN_SERVER = s;
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            Toast.makeText(getContext(), PIN_SERVER, Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
        }
    }
}
