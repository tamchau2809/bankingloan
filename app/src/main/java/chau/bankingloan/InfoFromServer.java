package chau.bankingloan;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoFromServer {
	private String id;
	private String name;
	
	public InfoFromServer(){}
	
	public InfoFromServer(String id, String name){
		this.id = id;
		this.name = name;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getID(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}

	public JSONObject getJSONObject()
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("id", this.id);
			obj.put("name", this.name);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return obj;
	}
}
