package chau.bankingloan.customThings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import chau.bankingloan.R;

/**
 * Created on 01-07-2016 by com08.
 */
public class ServerTvSum extends LinearLayout {
    TextView tvSum;
    ServerBoldTextview tvLabel;

    public ServerTvSum (Context ctx, String label)
    {
        super(ctx);
        tvSum = new TextView(ctx);
        tvSum.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        tvSum.setGravity(Gravity.CENTER);
        tvSum.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tvSum.setText(0);
        tvLabel = new ServerBoldTextview(ctx, label, false);

        this.addView(tvLabel);
        this.addView(tvSum);
    }

    public void setTvSum(String a, String b)
    {
        int i = Integer.parseInt(a.isEmpty() ? "0" : a);
        int i1 = Integer.parseInt(b.isEmpty() ? "0" : b);
        tvSum.setText(String.valueOf(i + i1));
    }

    public String getValue() {
        return tvSum.getText().toString();
    }
}
