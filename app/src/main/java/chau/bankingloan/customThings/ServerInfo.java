package chau.bankingloan.customThings;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created on 27-06-2016 by com08.
 */
public class ServerInfo {
    String label;
    String type;
    String value;
    String column;
    String url;
    boolean isRequired;
    public Object obj;

    public ServerInfo(String label, String type, String value, String url, String col, boolean isRequired)
    {
        this.label = label;
        this.type = type;
        this.value = value;
        this.url = url;
        this.column = col;
        this.isRequired = isRequired;
    }

    public void setLabel(String label){
        this.label = label;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setValue(String value){
        this.value = value;
    }
    public void setColumn(String column1){
        this.column = column1;
    }
    public void setRequired(boolean required) {
        this.isRequired = required;
    }
    public void setUrl(String url1) {
        this.url = url1;
    }

    public String getLabel(){
        return this.label;
    }
    public String getType(){
        return this.type;
    }
    public String getValue(){
        return this.value;
    }
    public String getColumn() {
        return column;
    }
    public boolean isRequired() {
        return isRequired;
    }
    public String getUrl() {
        return this.url;
    }

    public String getNumber()
    {
        if(type.equals("edPlusNumberA"))
        {
            if(obj != null)
            {
                ServerEditText serverEditText = (ServerEditText)obj;
                return serverEditText.getValue();
            }
        }
        return null;
    }

    public void setData(int data)
    {
        if(type.equals("edPlusResultA"))
        {
            if(obj != null)
            {
                ServerEditText serverEditText = (ServerEditText)obj;
                serverEditText.setValue(String.valueOf(data));
            }
        }
    }

    public Object getData()
    {
        if(type.equals("spinner"))
        {
            if(obj != null)
            {
                ServerSpinner serverSpinner = (ServerSpinner)obj;
                return serverSpinner.getValue();
            }
        }
        if(type.equals("edittext") || type.equals("edittextnumber")
                || type.equals("edittextemail")
                || type.equals("edPlusNumberA"))
        {
            if(obj != null)
            {
                ServerEditText serverEditText = (ServerEditText)obj;
                return serverEditText.getValue();
            }
        }
        if(type.equals("textviewDate"))
        {
            if(obj != null)
            {
                ServerTvDate serverTvDate = (ServerTvDate)obj;
                return serverTvDate.getValue();
            }
        }
        if(type.equals("checkbox"))
        {
            if(obj != null)
            {
                ServerCheckbox serverCheckbox = (ServerCheckbox) obj;
                return serverCheckbox.isChecked();
            }
        }
        return null;
    }

    public JSONObject jsonObject()
    {
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("value", this.label.trim().replace(" ", "").replace(":", ""));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return obj;
    }
}
