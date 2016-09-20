package chau.bankingloan.customThings;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.ArrayList;

/**
 * Created by com08 (15-Sep-16).
 */
public class CustomTextwatcher implements TextWatcher {

    String inputA, inputB;
    ServerEditText edResult;
    ArrayList<ServerInfo> tab;

    public CustomTextwatcher(ArrayList<ServerInfo> tab, String inputA, String inputB, ServerEditText edResult)
    {
        this.inputA = inputA;
        this.inputB = inputB;
        this.tab = tab;
        this.edResult = edResult;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        int a = Integer.parseInt(inputA.isEmpty() ? "0" : inputA);
        int b = Integer.parseInt(inputB.isEmpty() ? "0" : inputB);
        int t = 0;

        if(tab.get(i).getType().equals("edPlusNumberA"))
        {
            t = t+Integer.valueOf((String)tab.get(i).getData());
        }

        edResult.setValue(String.valueOf(t));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
