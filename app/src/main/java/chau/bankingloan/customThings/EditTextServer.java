package chau.bankingloan.customThings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
public class EditTextServer extends LinearLayout {
    EditText edLabel;
    BoldTextview boldTextview;
    public EditTextServer(Context ctx, String label, int inputType)
//    public EditTextServer(Context ctx, int inputType)
    {
        super(ctx);
        edLabel = new EditText(getContext());
        edLabel.setInputType(inputType);
        edLabel.setTextSize(18);
        edLabel.setGravity(Gravity.CENTER);
        edLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        edLabel.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        edLabel.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        boldTextview = new BoldTextview(ctx, label, false);
        this.addView(boldTextview);

        this.addView(edLabel);
    }
}
