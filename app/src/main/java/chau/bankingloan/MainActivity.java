package chau.bankingloan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import chau.bankingloan.customThings.ConnectURL;
import chau.bankingloan.customThings.InfoFromServer;
import chau.bankingloan.customThings.ConstantStuff;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
//    private ProgressDialog pDialog;
    JSONArray arrToolbar = null;
    JSONArray arrLink = null;
    ArrayList<InfoFromServer> list;
    ArrayList<InfoFromServer> serverArrayList;

    public static String TAB_1_LINK;
    public static String TAB_2_LINK;
    public static String TAB_3_LINK;
    public static String TAB_4_LINK;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    TabLayout tabLayout;
    NavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        list = new ArrayList<>();
        serverArrayList = new ArrayList<>();

        new GetToolbarData().execute();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if(mViewPager != null)
            mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        if(tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                mViewPager.setCurrentItem(tab.getPosition());
//                switch(tab.getPosition())
//                {
//                    case 1:
//                        Toast.makeText(getBaseContext(), MAKH, Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                            Toast.makeText(getBaseContext(), "test", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

            LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
            tabStrip.setEnabled(false);
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                tabStrip.getChildAt(i).setClickable(false);
            }
        }

        navigation = (NavigationView)findViewById(R.id.navigation);
        if(navigation != null)
            navigation.setNavigationItemSelectedListener(this);
    }

    public void switchTab(int pos)
    {
        mViewPager.setCurrentItem(pos);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
//                case 0:
//                    return new LoanFragment();
//                case 1:
//                    return new UploadFragment2();
                case 0:
                    return new Tab1Fragment();
                case 1:
                    return new Tab2Fragment();
                case 2:
                    return new Tab3Fragment();
                case 3:
                    return new Tab4Fragment();
                case 4:
                    return new Tab5Fragment();
                case 5:
                    return new ConfirmFragment();
                case 6:
                    return new ApproveFragment();
                default:
                    return new Tab1Fragment();
            }
        }

        @Override
        public int getCount() {
            // Show 8 total pages.
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
//                case 0:
//                    return "INFORMATION";
//                case 1:
//                    return "UPLOAD";
                case 0:
                    return "LOAN";
                case 1:
                    return "PERSONAL";
                case 2:
                    return "CONTACT";
                case 3:
                    return "EMPLOYMENT";
                case 4:
                    return "DOCUMENT";
                case 5:
                    return "CONFIRM";
                case 6:
                    return "APPROVE";
            }
            return null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.navHome:
                Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("http://www.vpbank.com.vn/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.navAbout:
                Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                Uri uri1 = Uri.parse("http://www.vpbank.com.vn/bai-viet/gioi-thieu-vpbank");
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                startActivity(intent1);
                break;
            case R.id.navTel:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:0439288869"));
                    startActivity(callIntent);
                    break;
                }
                else {
                    Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.navEmail:
                Toast.makeText(getApplicationContext(), "Contact", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.fromParts("mailto","customercare@vpb.com.vn", null));
                startActivity(i);
                break;
            case R.id.navRefresh:
                new GetToolbarData().execute();
                break;
            default:
                break;
        }
        return false;
    }

    private class GetToolbarData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list.clear();
            serverArrayList.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectURL sh = new ConnectURL();
            String jsonStr = sh.makeServiceCall(ConstantStuff.GET_TOOLBAR_TEXT, ConnectURL.GET);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    arrToolbar = jsonObj.getJSONArray("toolbar");
                    for(int i = 0; i < arrToolbar.length(); i++)
                    {
                        JSONObject obj = (JSONObject) arrToolbar.get(i);
                        InfoFromServer info = new InfoFromServer(obj.getString("id"),
                                obj.getString("name"));
                        list.add(info);
                    }
                    arrLink = jsonObj.getJSONArray("url");
                    for(int i = 0; i < arrLink.length(); i++)
                    {
                        JSONObject obj = (JSONObject) arrLink.get(i);
                        InfoFromServer info = new InfoFromServer(obj.getString("id"),
                                obj.getString("link"));
                        serverArrayList.add(info);
                    }
                    TAB_1_LINK = serverArrayList.get(0).getData();
                    TAB_2_LINK = serverArrayList.get(1).getData();
                    TAB_3_LINK = serverArrayList.get(2).getData();
                    TAB_4_LINK = serverArrayList.get(3).getData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ConnectURL", "Couldn't get any data from the url");
            }
            return null;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onPostExecute(Void aVoid) throws NullPointerException {
            super.onPostExecute(aVoid);
            for (int i = 0; i < 7; i++) {
                int j = i + 1;
                tabLayout.getTabAt(i).setText(j + "." + list.get(i).getData());
            }
        }
    }
}
