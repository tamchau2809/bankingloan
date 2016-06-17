package chau.bankingloan.customThings;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created on 13-Jun-16 by com08.
 */
public class BoldTextview extends LinearLayout
{
    TextView tvLabel;
    public BoldTextview(Context context, String label, Boolean isBold) {
        super(context);
        tvLabel = new TextView(context);
        tvLabel.setText(label);
        tvLabel.setTextSize(18);
        tvLabel.setGravity(Gravity.RIGHT);
        if(isBold)
            tvLabel.setTypeface(null, Typeface.BOLD);
        tvLabel.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(tvLabel);
    }
}
