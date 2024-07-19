package populationEntropy.data;

import java.util.ArrayList;

public class PopulationData {

	int startYear, endYear;			//start and end years of statistics
	int numberOfYears;					//duration or number of years of statistics
	int classNumber;						//number of age groups
	int districtNumber;					//number of districts
	int districtType;						//[0]:si-doo, [1]:si-gon-gu, [2]:eup-myung-dong
	int[] years;								//years' list of this data
	String[] className;					//name of age groups
	String[] districtName;				//name of district
	
	int[][][] population;					//[year][district][age-class],  age-class[0] = total population
	int[][] maxPopulation;				//[year][age-class],  age-class[0] = max total population
	int[][] minPopulation;				//[year][age-class],  age-class[0] = min total population
	int[] maxRegionPopulation;		//[district]
	int[] minRegionPopulation;		//[district]
	double[][] avgPopulation;		//[year][age-class],  age-class[0] = average total population
	double[][] stdPopulation;		//[year][age-class],  age-class[0] = standard deviation of total population
	double[][] agedRatio;				//[year][district]

	
	double[][][] probability;			//[year][district][age-class]
	double[][][] subEntropy;			//[year][district][age-class],  age-class[0] = entropy 
	double[][] entropy;					//[year][district]
	double[][][] classEntropy;		//[year][district][age-class],  age-class[0] = entropy
	double[] maxEntropy;				//[year]
	double[] minEntropy;				//[year]
	double[][] maxClassEntropy;	//[year][age-class],  age-class[0] = max entropy
	double[][] minClassEntropy;	//[year][age-class],  age-class[0] = min entropy
	
	public PopulationData(){
		
	}
	
	public PopulationData(int start_year, int end_year, int class_number, int district_number,int district_type){
		
		this.initiate(start_year, end_year, class_number, district_number,district_type);
	}
	
	public PopulationData(int[] years, int class_number, int district_number,int district_type){
		
		this.initiate(years, class_number, district_number,district_type);
	}
	
	public void initiate(){
		
		this.className = new String[this.classNumber];
		this.districtName = new String[this.districtNumber];
		this.population = new int[this.numberOfYears][this.districtNumber][this.classNumber];
		this.probability = new double[this.numberOfYears][this.districtNumber][this.classNumber];
		this.subEntropy = new double[this.numberOfYears][this.districtNumber][this.classNumber];
		this.classEntropy = new double[this.numberOfYears][this.districtNumber][this.classNumber];
		this.entropy = new double[this.numberOfYears][this.districtNumber];
		this.agedRatio = new double[this.numberOfYears][this.districtNumber];
		
		this.maxPopulation = new int[this.numberOfYears][this.classNumber];
		this.minPopulation = new int[this.numberOfYears][this.classNumber];	
		this.maxRegionPopulation = new int[this.districtNumber];
		this.minRegionPopulation = new int[this.districtNumber];	
		this.avgPopulation = new double[this.numberOfYears][this.classNumber];
		this.stdPopulation = new double[this.numberOfYears][this.classNumber];
		
		this.maxEntropy = new double[this.numberOfYears];
		this.minEntropy = new double[this.numberOfYears];
		this.maxClassEntropy = new double[this.numberOfYears][this.classNumber];
		this.minClassEntropy = new double[this.numberOfYears][this.classNumber];
	}
	
	public void initiate(int[] year, int class_number, int district_number,int district_type){
		
		this.years = year.clone();
		this.startYear = year[0];
		this.endYear = year[year.length-1];
		this.numberOfYears = year.length;
		this.classNumber = class_number;
		this.districtNumber = district_number;
		this.districtType = district_type;		
		
		this.initiate();
	}
	
	public void initiate(int start_year, int end_year, int class_number, int district_number,int district_type){
		
		this.startYear = start_year;
		this.endYear = end_year;
		this.numberOfYears = end_year - start_year + 1;
		this.classNumber = class_number;
		this.districtNumber = district_number;
		this.districtType = district_type;		
		this.years = new int[this.numberOfYears];
		for(int i=0 ; i<this.numberOfYears ; i++) this.years[i] = this.startYear+i;
		
		this.initiate();
	}
	
	public void setStartYear(int value){
		this.startYear = value;
	}
	
	public void setEndYear(int value){
		this.endYear = value;
	}

	public void setNumberOfYears(int value){
		this.numberOfYears = value;
	}
	
	public void setClassNumber(int value){
		this.classNumber = value;
	}
	
	public void setDistricClass(int value){
		this.districtType = value;
	}

	public void setDistricNumber(int value){
		this.districtNumber = value;
	}
	
	public void setClassName(int classN, String name){
		this.className[classN] = name;
	}
	
	public void setClassName(String[] name){
		if(this.classNumber == name.length) this.className = name.clone();
		else System.err.println("Class name list error");
	}
	
	public void setDistrictName(int district, String name){
		this.districtName[district] = name;
	}
	
	public void setDistrictName(String[] name){
		if(this.districtNumber == name.length) this.districtName = name.clone();
		else System.err.println("District name list error");
	}
	
	public void setPopulation(int year, int district, int ageClass, int value){
		this.population[year][district][ageClass] = value;
	}
	
	public void addPopulation(int year, int district, int ageClass, int value){
		if(this.population[year][district][ageClass]>0) 
			this.population[year][district][ageClass] += value;
		else this.population[year][district][ageClass] = value;
	}
	
	public void setAveragePopulation(int year, int ageClass, double value){
		this.avgPopulation[year][ageClass] = value;
	}
	
	public void setStdPopulation(int year, int ageClass, double value){
		this.stdPopulation[year][ageClass] = value;
	}
	
	public void setProbability(int year, int district, int ageClass, double value){
		this.probability[year][district][ageClass] = value;
	}
	
	public void setSubEntropy(int year, int district, int ageClass, double value){
		this.subEntropy[year][district][ageClass] = value;
	}

	public void setClassEntropy(int year, int district, int ageClass, double value){
		this.classEntropy[year][district][ageClass] = value;
	}
	
	public void setEntropy(int year, int district, double value){
		this.entropy[year][district] = value;
	}
	
	public void setAgedRatio(int year, int district, double value){
		this.agedRatio[year][district] = value;
	}

	public void setMaxEntropy(int year, double value){
		this.maxEntropy[year] = value;
	}
	
	public void setMinEntropy(int year, double value){
		this.minEntropy[year] = value;
	}
		
	public void setMaxClassEntropy(int year, int ageClass, double value){
		this.maxClassEntropy[year][ageClass] = value;
	}

	public void setMinClassEntropy(int year, int ageClass, double value){
		this.minClassEntropy[year][ageClass] = value;
	}

	public void setMaxPopulation(int year, int ageClass, int value){
		this.maxPopulation[year][ageClass] = value;
	}

	public void setMinPopulation(int year, int ageClass, int value){
		this.minPopulation[year][ageClass] = value;
	}

	public void setMaxRegionPopulation(int district, int value){
		this.maxRegionPopulation[district] = value;
	}

	public void setMinRegionPopulation(int district, int value){
		this.minRegionPopulation[district] = value;
	}
	
	/*** get function ***/
	public int getStartYear(){
		return this.startYear;
	}
	
	public int getEndYear(){
		return this.endYear;
	}
	
	public int getYear(int i){
		return this.years[i];
	}
	
	public int[] getYears(){
		return this.years;
	}
	
	public int getYearIndex(int year){
		for(int i=0 ; i<this.numberOfYears ; i++) if(year == this.years[i]) return i;
		return -1;
	}
	
	public int getNumberOfYears(){
		return this.numberOfYears;
	}
		
	public String[] getDistrictList(){
		return this.districtName;
	}
	
	public int getClassNumber(){
		return this.classNumber;
	}
	
	public int getDistricClass(){
		return this.districtType;
	}	

	public int getDistricNumber(){
		return this.districtNumber;
	}	
	
	public String getClassName(int classN){
		return this.className[classN];
	}
	
	public String getDistrictName(int district){
		return this.districtName[district];
	}

	public int getPopulation(int year, int district){
		return this.population[year][district][0];		
	}
	
	public int getPopulation(int year, int district, int ageClass){
		return this.population[year][district][ageClass];		
	}
	
	public double getAveragePopulation(int year){
		return this.avgPopulation[year][0];
	}
	
	public double getAveragePopulation(int year, int ageClass){
		return this.avgPopulation[year][ageClass];
	}
	
	public double getStdPopulation(int year, int ageClass){
		return this.stdPopulation[year][ageClass];
	}
	
	public double getStdPopulation(int year){
		return this.stdPopulation[year][0];
	}
	
	public double getProbability(int year, int district, int ageClass){
		return this.probability[year][district][ageClass];		
	}
	
	public double getSubEntropy(int year, int district, int ageClass){
		return this.subEntropy[year][district][ageClass];		
	}

	public double getClassEntropy(int year, int district, int ageClass){
		return this.classEntropy[year][district][ageClass];		
	}
	
	public double getEntropy(int year, int district){
		return this.entropy[year][district];		
	}
	
	public double getAgedRatio(int year, int district){
		return this.agedRatio[year][district];		
	}

	public int getMaxPopulation(int year, int ageClass){
		return this.maxPopulation[year][ageClass];		
	}

	public int getMinPopulation(int year, int ageClass){
		return this.minPopulation[year][ageClass];		
	}	
	
	public int getMaxPopulation(int year){
		return this.maxPopulation[year][0];		
	}

	public int getMinPopulation(int year){
		return this.minPopulation[year][0];		
	}	
	
	public int getMaxRegionPopulation(int district){
		return this.maxRegionPopulation[district];		
	}

	public int getMinRegionPopulation(int district){
		return this.minRegionPopulation[district];		
	}	
	
	public double getMaxEntropy(int year){
		return this.maxEntropy[year];		
	}
	
	public double getMinEntropy(int year){
		return this.minEntropy[year];		
	}
	
	public double getMaxClassEntropy(int year, int ageClass){
		return this.maxClassEntropy[year][ageClass];		
	}

	public double getMinClassEntropy(int year, int ageClass){
		return this.minClassEntropy[year][ageClass];		
	}
	
	public void removeDistrict(int district){
		int i, j, k;
		String[] tmpDistrictName = new String[this.districtNumber-1];
		int[][][] tmpPopulation = new int[this.numberOfYears][this.districtNumber-1][this.classNumber];
		
		for(i=0 ; i<district ; i++) tmpDistrictName[i] = this.districtName[i];	
		for(i=district ; i<this.districtNumber-1 ; i++) tmpDistrictName[i] = this.districtName[i+1];
		
		for(i=0 ; i<this.numberOfYears ; i++){
			for(k=0 ; k<this.classNumber ; k++){
				for(j=0 ; j<district ; j++) tmpPopulation[i][j][k] = this.population[i][j][k];
				for(j=district ; j<this.districtNumber-1 ; j++) tmpPopulation[i][j][k] = this.population[i][j+1][k];
			}
		}
		
		this.districtName = tmpDistrictName;
		this.population = tmpPopulation;
		this.districtNumber--;
	}
	
}
