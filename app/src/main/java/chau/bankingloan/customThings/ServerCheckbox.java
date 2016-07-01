package chau.bankingloan.customThings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import chau.bankingloan.R;

/**
 * Created on 30-06-2016 by com08.
 */
public class ServerCheckbox extends LinearLayout {
    CheckBox checkBox;
    ServerBoldTextview textview;
    public ServerCheckbox(Context ctx, String label)
    {
        super(ctx);
        checkBox = new CheckBox(ctx);
        checkBox.setText(label.replace(":", ""));
        checkBox.setTextSize(18);
        checkBox.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        checkBox.setTextColor(ContextCompat.getColor(ctx, R.color.colorAccent));
        checkBox.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(checkBox);
    }

    public String isChecked() {
        return String.valueOf(checkBox.isChecked());
    }
}
