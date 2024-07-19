package industrialDiversity.data;

public class CompanyData {

	int foundedYear;
	int locationType;
	int industryType;
	int scaleType;
	int numberOfCompany;
	
	public CompanyData(){		
	}
	
	public CompanyData(int fYear, int loc, int indust, int scale, int number){
		this.setCompanyData(fYear, loc, indust, scale, number);
	}
	
	public int getFoundedYear(){
		return this.foundedYear;
	}
	
	public int getLocationType(){
		return this.locationType;
	}	
	
	public int getIndustryType(){
		return this.industryType;
	}
	
	public int getScaleType(){
		return this.scaleType;
	}	
	
	public int getNumberOfCompany(){
		return this.numberOfCompany;
	}		
	
	public void setCompanyData(int fYear, int loc, int indust, int scale, int number){
		this.foundedYear = fYear;
		this.locationType = loc;
		this.industryType = indust;
		this.scaleType =  scale;
		this.numberOfCompany = number;
	}
	
	public void setFoundedYear(int value){
		this.foundedYear = value;
	}
	
	public void setLocationType(int value){
		this.locationType = value;
	}	
	
	public void setIndustryType(int value){
		this.industryType = value;
	}
	
	public void setScaleType(int value){
		this.scaleType = value;
	}	
	
	public void setNumberOfCompany(int value){
		this.numberOfCompany = value;
	}	
}
