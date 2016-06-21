package chau.bankingloan.customThings;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import chau.bankingloan.R;

/**
 * Created on 14-Jun-16 by com08.
 */
public class SpinnerServer extends LinearLayout {
    ArrayAdapter<String> arrayAdapter;
    Spinner spinner;
    BoldTextview boldTextview;
    public SpinnerServer(Context ctx, String label, String value)
    {
        super(ctx);
        spinner = new Spinner(getContext());
        spinner.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        String[] arr = value.split(",");
        arrayAdapter = new ArrayAdapter<>(ctx,
                R.layout.custom_spinner_item, arr);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        boldTextview = new BoldTextview(ctx, label, false);
        this.addView(boldTextview);
        this.addView(spinner);
    }

    public String getValue()
    {
        return spinner.getSelectedItem().toString();
    }
}
