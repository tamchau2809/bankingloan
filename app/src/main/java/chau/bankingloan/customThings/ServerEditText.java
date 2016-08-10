package chau.bankingloan.customThings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import chau.bankingloan.R;

/**
 * Created on 13-Jun-16 by com08.
 */
public class ServerEditText extends LinearLayout {
    EditText edLabel;
    ServerBoldTextview serverBoldTextview;

    public ServerEditText(Context ctx)
    {
        super(ctx);
    }

    public ServerEditText(Context ctx, String label, int type)
    {
        super(ctx);
        edLabel = new EditText(getContext());
        edLabel.setInputType(type);
        edLabel.setTextSize(18);
        edLabel.setGravity(Gravity.CENTER);
        edLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        edLabel.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        edLabel.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        serverBoldTextview = new ServerBoldTextview(ctx, label, false);
        this.addView(serverBoldTextview);

        this.addView(edLabel);
    }

    public void setValue(String a)
    {
        edLabel.setText(a);
    }

    public String getValue() {
        return edLabel.getText().toString();
    }
}
