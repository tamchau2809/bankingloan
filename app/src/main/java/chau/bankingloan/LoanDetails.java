package chau.bankingloan;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created on 16-May-16 by com08.
 */
public class LoanDetails {
    String loanType;
    String loanAmount, tenure, loanPurpose, maxInterest, monthlyPayment, lastPayment;

    public LoanDetails(String type, String amount, String ten, String purpose, String max, String pay, String lastPay)
    {
        this.loanType = type;
        this.loanAmount = amount;
        this.tenure = ten;
        this.loanPurpose = purpose;
        this.maxInterest = max;
        this.monthlyPayment = pay;
        this.lastPayment = lastPay;
    }
    public String getLoanType()
    {
        return loanType;
    }
    public String getLoanAmount()
    {
        return loanAmount;
    }
    public String getTenure()
    {
        return tenure;
    }
    public String getLoanPurpose()
    {
        return loanPurpose;
    }
    public String getMaxInterest()
    {
        return maxInterest;
    }
    public String getMonthlyPayment()
    {
        return monthlyPayment;
    }
    public String getLastPayment()
    {
        return lastPayment;
    }
    public JSONObject getJSONInfo()
    {
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("loanType", this.loanType);
            obj.put("loanAmount", this.loanAmount);
            obj.put("tenure", this.tenure);
            obj.put("loanPurpose", this.loanPurpose);
            obj.put("maxInterest", this.maxInterest);
            obj.put("monthlyPayment", this.monthlyPayment);
            obj.put("lastPayment", this.lastPayment);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        return obj;
    }
}
