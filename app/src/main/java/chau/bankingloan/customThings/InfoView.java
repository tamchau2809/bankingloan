package chau.bankingloan.customThings;

/**
 * Created on 13-Jun-16 by com08.
 */
public class InfoView {
    String label;
    String type;
    String value;

    public InfoView(String label, String type, String value)
    {
        this.label = label;
        this.type = type;
        this.value = value;
    }

    public InfoView(String label, String type)
    {
        this.label = label;
        this.type = type;
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

    public String getLabel(){
        return this.label;
    }
    public String getType(){
        return this.type;
    }
    public String getValue(){
        return this.value;
    }
}
