package populationEntropy.data;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class EntropyData {

	int startYear, endYear;			//start and end years of statistics
	int numberOfYears;					//duration or number of years of statistics
	int classNumber;				//number of age groups
	int districtNumber;				//number of districts
	int districtType;				//[0]:si-doo, [1]:si-gon-gu, [2]:eup-myung-dong
	int[] years;								//years' list of this data
	String[] districtName;			//[district]
	
	double maxLatitude;
	double minLatitude;
	double maxLongitude;
	double minLongitude;
	double[][] coordinates;			//[district, [latitude, longitude]]
	double[] totalDistance;			//[district]
	double[] totalSquareDistance;	//[district]
	
	double[] maxEntropy;			//[year]
	double[] minEntropy;			//[year]
	double[][] maxClassEntropy;		//[year][age-class],  age-class[0] = max entropy
	double[][] minClassEntropy;		//[year][age-class],  age-class[0] = min entropy
	
	double[][] entropy;					//[year][district]
	double[][] population;				//[year][district]
	double[][] agedIndex;				//[year][district]
	int[] maxPopulation;				//[year]
	int[] minPopulation;				//[year]

	double[][][] classEntropy;		//[year][district][age-class],  age-class[0] = entropy
	
	public EntropyData(){
		
	}
	
	public EntropyData(int start_year, int end_year, int class_number, int district_number, int district_type){
		
		this.initiate(start_year, end_year, class_number, district_number,district_type);
	}
	
	public EntropyData(int[] years, int class_number, int district_number, int district_type){
		
		this.initiate(years, class_number, district_number,district_type);
	}
	
	public void initiate(int start_year, int end_year, int class_number, int district_number,int district_type){
		
		this.startYear = start_year;
		this.endYear = end_year;
		this.numberOfYears = end_year - start_year + 1;
		this.districtNumber = district_number;
		this.districtType = district_type;		
		this.classNumber = class_number;
		
		this.initiate();
	}	
	
	public void initiate(int[] year, int class_number, int district_number,int district_type){
		
		this.years = year.clone();
		this.startYear = year[0];
		this.endYear = year[year.length-1];
		this.numberOfYears = year.length;
		this.districtNumber = district_number;
		this.districtType = district_type;		
		this.classNumber = class_number;
		
		this.initiate();
	}	
	
	public void initiate(){
		this.districtName = new String[this.districtNumber];
		this.entropy = new double[this.numberOfYears][this.districtNumber];
		this.population = new double[this.numberOfYears][this.districtNumber]; 
		this.agedIndex = new double[this.numberOfYears][this.districtNumber]; 
		this.maxEntropy = new double[this.numberOfYears];
		this.minEntropy = new double[this.numberOfYears];
		this.coordinates = new double[this.districtNumber][2];
		this.totalDistance = new double[this.districtNumber];
		this.totalSquareDistance = new double[this.districtNumber];
		
		this.maxPopulation = new int[this.numberOfYears];
		this.minPopulation = new int[this.numberOfYears];	
		this.classEntropy = new double[this.numberOfYears][this.districtNumber][this.classNumber];
		this.maxClassEntropy = new double[this.numberOfYears][this.classNumber];
		this.minClassEntropy = new double[this.numberOfYears][this.classNumber];
	}
	
	public void setMapBoundary(double maxLat, double minLat, double maxLong, double minLong){
		
		this.maxLatitude = maxLat;
		this.minLatitude = minLat;
		this.maxLongitude = maxLong;
		this.minLongitude = minLong;		
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
	
	public void setDistrictName(int district, String name){
		this.districtName[district] = name;
	}
	
	public void setMaxLatitude(double value){
		this.maxLatitude = value;
	}

	public void setMinLatitude(double value){
		this.minLatitude = value;
	}
	
	public void setMaxLongitude(double value){
		this.maxLongitude = value;
	}
	
	public void setMinLongitude(double value){
		this.minLongitude = value;
	}
	
	public void setCoordinates(int district, double latitude, double longitude){		
		this.coordinates[district][0] = latitude;
		this.coordinates[district][1] = longitude;
	}	

	public void setTotalDistance(int district, double value){
		this.totalDistance[district] = value;
	}

	public void setTotalSquareDistance(int district, double value){
		this.totalSquareDistance[district] = value;
	}
	
	public void setPopulation(int year, int district, double value){
		this.population[year][district] = value;
	}
	
	public void setClassEntropy(int year, int district, int ageClass, double value){
		this.classEntropy[year][district][ageClass] = value;
	}
	
	public void setMaxClassEntropy(int year, int ageClass, double value){
		this.maxClassEntropy[year][ageClass] = value;
	}

	public void setMinClassEntropy(int year, int ageClass, double value){
		this.minClassEntropy[year][ageClass] = value;
	}
	
	public void setEntropy(int year, int district, double value){
		this.entropy[year][district] = value;
	}

	public void setMaxEntropy(int year, double value){
		this.maxEntropy[year] = value;
	}
	
	public void setMinEntropy(int year, double value){
		this.minEntropy[year] = value;
	}
		
	public void setMaxPopulation(int year, int value){
		this.maxPopulation[year] = value;
	}

	public void setMinPopulation(int year, int value){
		this.minPopulation[year] = value;
	}
	
	public void setAgedIndex(int year, int district, double value){
		this.agedIndex[year][district] = value;
	}
	
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
		
	public int getClassNumber(){
		return this.classNumber;
	}
	
	public int getDistricClass(){
		return this.districtType;
	}	

	public int getDistricNumber(){
		return this.districtNumber;
	}	
	
	public String getDistrictName(int district){
		return this.districtName[district];
	}
	
	public String[] getDistrictList(){
		return this.districtName;
	}
	
	public double getMaxLatitude(){
		return this.maxLatitude;
	}

	public double getMinLatitude(){
		return this.minLatitude;
	}
	
	public double getMaxLongitude(){
		return this.maxLongitude;
	}
	
	public double getMinLongitude(){
		return this.minLongitude;
	}

	public double[] getCoordinates(int district){		
		
		double[] points = new double[2];
		points[0] = this.coordinates[district][0];
		points[1] = this.coordinates[district][1];
			
		return points;
	}
	
	public double getLatitude(int district){
		return this.coordinates[district][0];
	}

	public double getLongitude(int district){
		return this.coordinates[district][1];	
	}
	
	public double getTotalDistance(int district){
		return this.totalDistance[district];
	}

	public double getTotalSquareDistance(int district){
		return this.totalSquareDistance[district];
	}
	
	public double getPopulation(int year, int district){
		return this.population[year][district];		
	}
	
	public double getAgedIndex(int year, int district){
		return this.agedIndex[year][district];		
	}
	
	public double getClassEntropy(int year, int district, int ageClass){
		return this.classEntropy[year][district][ageClass];		
	}

	public double getMaxClassEntropy(int year, int ageClass){
		return this.maxClassEntropy[year][ageClass];		
	}

	public double getMinClassEntropy(int year, int ageClass){
		return this.minClassEntropy[year][ageClass];		
	}
	
	public double getEntropy(int year, int district){
		return this.entropy[year][district];		
	}
	
	public double getMaxEntropy(int year){
		return this.maxEntropy[year];		
	}
	
	public double getMinEntropy(int year){
		return this.minEntropy[year];		
	}
	
	public int getMaxPopulation(int year){
		return this.maxPopulation[year];
	}

	public int getMinPopulation(int year){
		return this.minPopulation[year];
	}
	
	public void printData(String outputFile){
		int i, j;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
		pw.print("\t\t");
			for(i=0 ; i<this.numberOfYears ; i++){
				pw.print("\t"+this.years[i]);
				for(j=0 ; j<2 ; j++) pw.print("\t");
			}
			pw.println();
			
			pw.print("Region\tlatitude\tlongitude");
			for(i=0 ; i<this.numberOfYears ; i++){
				pw.print("\tpopulation\tentropy\tage_index");
			}
			pw.println();
			
			for(i=0 ; i<this.districtNumber ; i++){
				pw.print(this.districtName[i]);
				pw.print("\t"+this.coordinates[i][0]+"\t"+this.coordinates[i][1]);
				for(j=0 ; j<this.numberOfYears ; j++){
					pw.print("\t"+this.population[j][i]);
					pw.print("\t"+this.entropy[j][i]);
					pw.print("\t"+this.agedIndex[j][i]);
				}
				pw.println();
			}
		
			pw.close();
		}catch(IOException e) {}	
	}
	
}
