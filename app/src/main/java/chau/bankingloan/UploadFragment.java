package chau.bankingloan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created on 11-May-16 by com08.
 */
public class UploadFragment extends Fragment
{
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_upload, container, false);

        return rootView;
    }

    private class UploadFile extends AsyncTask<Void, Float, String>
    {
        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        private String uploadFile()
        {
            String response = null;
            return response;
        }
    }
}
