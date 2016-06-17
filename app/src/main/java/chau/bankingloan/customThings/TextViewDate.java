package chau.bankingloan.customThings;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import chau.bankingloan.R;

/**
 * Created on 16-06-2016 by com08.
 */
public class TextViewDate extends LinearLayout{
    TextView tvDate;
    private DatePickerDialog mDatePickerDialog;
    BoldTextview boldTextview;
    public TextViewDate(Context ctx, String label)
    {
        super(ctx);
        tvDate = new TextView(getContext());
        tvDate.setTextSize(18);
        tvDate.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        tvDate.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT < 23)
        {
            tvDate.setTextAppearance(ctx, android.R.style.Widget_Material_Light_TextView_SpinnerItem);
        }
        else
        {
            tvDate.setTextAppearance(android.R.style.Widget_Material_Light_TextView_SpinnerItem);
        }
//        tvDate.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.C0));
        tvDate.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar calendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvDate.setText(dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.setTitle("Please Choose The Day Of Your Last Pay");

        tvDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.show();
            }
        });

        boldTextview = new BoldTextview(ctx, label, false);

        this.addView(boldTextview);
        this.addView(tvDate);
    }
}
