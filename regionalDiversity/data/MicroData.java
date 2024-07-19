package regionalDiversity.data;

public class MicroData {
	int foundedYear;
	int locationCode;
	String industryCode;
	int workers;
	int businessType;
	int storeStype;
	
	public MicroData(){		
	}
	
	public MicroData(int fYear, int loc, String indust, int wrk){
		this.setMicroData(fYear, loc, indust, wrk);
	}
	
	public int getFoundedYear(){
		return this.foundedYear;
	}
	
	public int getLocationCode(){
		return this.locationCode;
	}	
	
	public String getIndustryCode(){
		return this.industryCode;
	}
	
	public int getWorkers(){
		return this.workers;
	}		

	public int getBusinessType(){
		return this.businessType;
	}
	
	public int getStoreType(){
		return this.storeStype;
	}
	
	public void setMicroData(int fYear, int loc, String indust, int wrk){
		this.foundedYear = fYear;
		this.locationCode = loc;
		this.industryCode = indust;
		this.workers = wrk;
	}

	public void setMicroData(int fYear, int loc, String indust, int wrk, int busTyp, int storeTyp){
		this.foundedYear = fYear;
		this.locationCode = loc;
		this.industryCode = indust;
		this.workers =  wrk;
		this.businessType = busTyp;
		this.storeStype = storeTyp;
	}
	
	public void setFoundedYear(int value){
		this.foundedYear = value;
	}
	
	public void setLocationCode(int value){
		this.locationCode = value;
	}	
	
	public void setIndustryCode(String value){
		this.industryCode = value;
	}
	
	public void setWorkers(int value){
		this.workers = value;
	}
	
	public void setBusinessType(int value){
		this.businessType = value;
	}
	
	public void setStoreType(int value){
		this.storeStype = value;
	}
}
