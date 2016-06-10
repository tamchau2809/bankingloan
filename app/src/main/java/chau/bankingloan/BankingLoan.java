package chau.bankingloan;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BankingLoan extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static String tab_loan = "";
    private static String MAKH = "";
    private static String contractNum = "";

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

        try
        {
            URL url = new URL("http://192.168.1.13/chauvu/bankingloan.xml");
            InputStream is = url.openConnection().getInputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document dom = db.parse(is);
            Element root = dom.getDocumentElement();

            NodeList forms = root.getElementsByTagName("form");
            if (forms.getLength() < 1) {
                // nothing here??
                AlertDialog.Builder bd = new AlertDialog.Builder(this);
                AlertDialog ad = bd.create();
                ad.setTitle("Error");
                ad.setMessage("Could not parse the Form data");
                ad.show();
            }
            Node form = forms.item(0);
            NamedNodeMap map = form.getAttributes();
            Toast.makeText(getApplicationContext(), forms.toString(), Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

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
                    return new LoanFragment2();
                case 1:
                    return new PersonalFragment();
                case 2:
                    return new ContactFragment();
                case 3:
                    return new EmploymentFragment();
                case 4:
                    return new DocumentFragment();
                case 5:
                    return new ConfirmFragment();
                case 6:
                    return new PrintFragment();
                case 7:
                    return new ApproveFragment();
                default:
                    return new LoanFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 8;
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
                    return "PRINT";
                case 7:
                    return "APPROVE";
            }
            return null;
        }
    }

    public void GetFormData()
    {
        try
        {
            URL url = new URL("http://192.168.1.13/chauvu/bankingloan.xml");
            InputStream is = url.openConnection().getInputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document dom = db.parse(is);
            Element root = dom.getDocumentElement();

            NodeList forms = root.getElementsByTagName("form");
            if (forms.getLength() < 1) {
                // nothing here??
                AlertDialog.Builder bd = new AlertDialog.Builder(this);
                AlertDialog ad = bd.create();
                ad.setTitle("Error");
                ad.setMessage("Could not parse the Form data");
                ad.show();
            }
            Node form = forms.item(0);
            NamedNodeMap map = form.getAttributes();
            Toast.makeText(getApplicationContext(), map.getNamedItem("name").getNodeValue(), Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            e.printStackTrace();
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
            default:
                break;
        }
        return false;
    }
}
