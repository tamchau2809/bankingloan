package chau.bankingloan.customThings;

/**
 * Created on 27-06-2016 by com08.
 */
public class DynamicInfo {
    String label;
    String type;
    String value;
    String column;
    boolean isRequired;
    public Object obj;

    public DynamicInfo(String label, String type, String value, String col, boolean isRequired)
    {
        this.label = label;
        this.type = type;
        this.value = value;
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

    public Object getData()
    {
        if(type.equals("spinner"))
        {
            if(obj != null)
            {
                SpinnerServer spinnerServer = (SpinnerServer)obj;
                return spinnerServer.getValue();
            }
        }
        if(type.equals("edittext"))
        {
            if(obj != null)
            {
                EditTextServer editTextServer = (EditTextServer)obj;
                return editTextServer.getValue();
            }
        }
        if(type.equals("textviewDate"))
        {
            if(obj != null)
            {
                TextViewDate textViewDate = (TextViewDate)obj;
                return textViewDate.getValue();
            }
        }
        return null;
    }
}
