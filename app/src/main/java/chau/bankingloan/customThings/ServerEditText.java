package chau.bankingloan.customThings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
    EditText editText = new EditText(getContext());
    ServerBoldTextview tvLabel;

    public ServerEditText(Context ctx)
    {
        super(ctx);
    }

    public ServerEditText(Context ctx, String label, int type)
    {
        super(ctx);
        editText.setInputType(type);
        editText.setTextSize(18);
        editText.setGravity(Gravity.CENTER);
        editText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        editText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        tvLabel = new ServerBoldTextview(ctx, label, false);
        this.addView(tvLabel);

        this.addView(editText);
    }

    public ServerEditText(Context ctx, String label, int type, TextWatcher watcher)
    {
        super(ctx);
        editText.setInputType(type);
        editText.setTextSize(18);
        editText.setGravity(Gravity.CENTER);
        editText.addTextChangedListener(watcher);
        editText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        editText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        tvLabel = new ServerBoldTextview(ctx, label, false);
        this.addView(tvLabel);

        this.addView(editText);
    }

    public void setValue(String a)
    {
        editText.setText(a);
    }

    public void setEnabled(boolean isEnabled)
    {
        editText.setEnabled(isEnabled);
        editText.setFocusable(false);
    }

    public String getValue() {
        return editText.getText().toString();
    }

    public void AddTextChangeListener(CustomTextwatcher textWatcher)
    {
        editText.addTextChangedListener(textWatcher);
    }
}
