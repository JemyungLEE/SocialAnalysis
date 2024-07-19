package regionalAddedValue.data;

public class MicroData {

	int foundedYear;
	String locationCode;
	String industryCode;
	int workers;
	int businessType;
	int storeType;
	
	public MicroData(){		
	}
	
	public MicroData(int fYear, String loc, String indust, int wrk){
		this.setMicroData(fYear, loc, indust, wrk);
	}
	
	public MicroData(int fYear, String loc, String indust, int wrk, int busTyp, int storeTyp){
		this.setMicroData(fYear, loc, indust, wrk, busTyp, storeTyp);
	}
	
	public int getFoundedYear(){
		return this.foundedYear;
	}
	
	public String getLocationCode(){
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
		return this.storeType;
	}
	
	public void setMicroData(int fYear, String loc, String indust, int wrk){
		this.foundedYear = fYear;
		this.locationCode = loc;
		this.industryCode = indust;
		this.workers = wrk;
	}

	public void setMicroData(int fYear, String loc, String indust, int wrk, int busTyp, int storeTyp){
		this.foundedYear = fYear;
		this.locationCode = loc;
		this.industryCode = indust;
		this.workers =  wrk;
		this.businessType = busTyp;
		this.storeType = storeTyp;
	}
	
	public void setFoundedYear(int value){
		this.foundedYear = value;
	}
	
	public void setLocationCode(String value){
		this.locationCode = value;
	}	
	
	public void setWorkers(int value){
		this.workers = value;
	}
	
	public void setBusinessType(int value){
		this.businessType = value;
	}
	
	public void setStoreType(int value){
		this.storeType = value;
	}
}
