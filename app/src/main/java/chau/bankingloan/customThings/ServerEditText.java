package chau.bankingloan.customThings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
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
    EditText edInput = new EditText(getContext());
    ServerBoldTextview tvLabel;

    public ServerEditText(Context ctx)
    {
        super(ctx);
    }

    public ServerEditText(Context ctx, String label, int type)
    {
        super(ctx);
        edInput.setInputType(type);
        edInput.setTextSize(18);
        edInput.setGravity(Gravity.CENTER);
        edInput.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        edInput.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        edInput.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        tvLabel = new ServerBoldTextview(ctx, label, false);
        this.addView(tvLabel);

        this.addView(edInput);
    }

    public void setValue(String a)
    {
        edInput.setText(a);
    }

    public void setEnabled(boolean isEnabled)
    {
        edInput.setEnabled(isEnabled);
        edInput.setFocusable(false);
    }

    public String getValue() {
        return edInput.getText().toString();
    }

    public void AddTextChangeListener(TextWatcher textWatcher)
    {
        edInput.addTextChangedListener(textWatcher);
    }
}
