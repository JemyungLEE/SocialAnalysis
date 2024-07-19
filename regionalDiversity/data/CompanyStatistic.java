package regionalDiversity.data;

public class CompanyStatistic {

	int startYear, endYear;
	int totalPeriod;
	int sortOfLocationType;
	int sortOfIndustryType;
	int sortOfScaleType;
	
	CompanyData[][][][] statistic;		//[year][region][industry][scale]
	
	public CompanyStatistic(){
		
	}
	
	public CompanyStatistic(int start, int end, int location, int industry, int scale){
		this.initiate(start, end, location, industry, scale);
	}
	
	public void initiate(int start, int end, int location, int industry, int scale){
		this.startYear = start;
		this.endYear = end;
		this.totalPeriod = end - start + 1;
		this.sortOfLocationType = location;
		this.sortOfIndustryType = industry;
		this.sortOfScaleType = scale;
		
		this.statistic = new CompanyData[this.totalPeriod][location][industry][scale];
	}
	
	public void newStatistic(){
		this.statistic = new CompanyData[this.totalPeriod][this.sortOfLocationType]
									[this.sortOfIndustryType][this.sortOfScaleType];
	}
	
	public void setStatistic(int year, int locType, int industType, int scaleType, int number){
		this.statistic[year][locType][industType][scaleType] 
				= new CompanyData(year, locType, industType, scaleType, number);
	}

	public void setStatistic(int year, int locType, int industType, int scaleType, CompanyData cdata){
		this.statistic[year][locType][industType][scaleType] = cdata;
	}
	
	public CompanyData getStatistic(int year, int locType, int industType, int scaleType){
		return this.statistic[year][locType][industType][scaleType];
	}
	
	public void setPeriod(int start, int end){
		this.startYear = start;
		this.endYear = end;
		this.totalPeriod = end - start +1;
	}
	
	public void setSortOfLocationType(int value){
		this.sortOfLocationType = value;
	}
	
	public void setSortOfIndustryType(int value){
		this.sortOfIndustryType = value;
	}
	
	public void setSortOfScaleType(int value){
		this.sortOfScaleType = value;
	}
	
	
	public int getPeriod(){
		return this.totalPeriod;
	}
	
	public int getStartYear(){
		return this.startYear;
	}
	
	public int getEndYear(){
		return this.endYear;
	}
	
	public int getSortOfLocationType(){
		return this.sortOfLocationType;
	}
	
	public int getSortOfIndustryType(){
		return this.sortOfIndustryType;
	}
	
	public int getSortOfScaleType(){
		return this.sortOfScaleType;
	}
	
}
