package chau.bankingloan.customThings;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import chau.bankingloan.R;

/**
 * Created on 16-06-2016 by com08.
 */
public class ServerTvDate extends LinearLayout{
    TextView tvDate;
    private DatePickerDialog mDatePickerDialog;
    ServerBoldTextview serverBoldTextview;

    public ServerTvDate(Context ctx)
    {
        super(ctx);
    }
    public ServerTvDate(Context ctx, String label, String content)
    {
        super(ctx);
        tvDate = new TextView(getContext());
        tvDate.setTextSize(18);
        tvDate.setHint(content);
        tvDate.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        tvDate.setGravity(Gravity.CENTER);
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
//        mDatePickerDialog.setTitle("Please Choose The Day Of Your Last Pay");

        tvDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.show();
            }
        });

        serverBoldTextview = new ServerBoldTextview(ctx, label, false);

        this.addView(serverBoldTextview);
        this.addView(tvDate);
    }

    public String getValue()
    {
        return tvDate.getText().toString();
    }
}
