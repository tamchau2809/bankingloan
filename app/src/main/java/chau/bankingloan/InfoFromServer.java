package chau.bankingloan;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoFromServer {
	private String id;
	private String data;

	public InfoFromServer(String id, String name){
		this.id = id;
		this.data = name;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public void setData(String data){
		this.data = data;
	}
	
	public String getID(){
		return this.id;
	}
	
	public String getData(){
		return this.data;
	}

	public JSONObject getJSONInfo()
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("id", this.id);
			obj.put("data", this.data);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return obj;
	}
}
