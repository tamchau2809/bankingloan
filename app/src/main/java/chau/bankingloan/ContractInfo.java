package chau.bankingloan;

import java.io.Serializable;

public class ContractInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String MAKH;
	public String MANV;
	public String contractNumber;
	public String urlImage;
	public String urlImageContract;
	
	public ContractInfo(String name, String nv, String num, String urlImage, String url) {
		// TODO Auto-generated constructor stub
		this.MAKH = name;
		this.MANV = nv;
		this.contractNumber = num;
		this.urlImage = urlImage;
		this.urlImageContract = url;
	}
	
	public void setKH(String n)
	{
		this.MAKH = n;
	}
	
	public String getKH()
	{
		return MAKH;
	}
	
	public void setNV(String n)
	{
		this.MANV = n;
	}
	
	public String getNV()
	{
		return MANV;
	}
	
	public void setContractNum(String num)
	{
		this.contractNumber = num;
	}
	
	public String getContractNum()
	{
		return contractNumber;
	}
	
	public void setURLImage(String url)
	{
		this.urlImage = url;
	}
	
	public String getURLImage()
	{
		return urlImage;
	}
	
	public void setURLImageContract(String url)
	{
		this.urlImageContract = url;
	}
	
	public String getURLImageContract()
	{
		return urlImageContract;
	}
}
