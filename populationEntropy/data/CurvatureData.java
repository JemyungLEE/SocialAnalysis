package populationEntropy.data;

public class CurvatureData {

	int startYear, endYear;		//start and end years of statistics
	int numberOfYears;				//duration or number of years of statistics
	int classNumber;					//number of age groups
	int districtNumber;				//number of districts
	int districtType;					//[0]:si-doo, [1]:si-gon-gu, [2]:eup-myung-dong
	int[] years;							//years' list of this data
	String[] districtName;			//[district]
	
	double maxLatitude;
	double minLatitude;
	double maxLongitude;
	double minLongitude;
	double[][] coordinates;			//[district, [latitude, longitude]]	
	double[][] latlongCorrd;			//[district, [latitude, longitude]]	: LatLong coordinate
	
	double[][] curvature;					//[year][district]
	double[][] pressure;					//[year][district]
	double[][] migration;					//[year][district]
	double[][][] classPressure;			//[year][district][age-class],  age-class[0] = all class
	double[][][] interaction;				//]year][district][district]	
	
	double[] curvatureSum;				//[year]
	double[] pressureSum;				//[year]
	double[] residualSum;				//[year]
	double[] rmse;							//[year]
	double[] rsquare;						//[year]
	double[][] classPressureSum; 	//[year][age-class],  age-class[0] = all class
	
	double[] scale;					//[year]
	double[] minimum;				//[year]
	double[] weight;					//[year]
	double[] lamdaA;				//[year]
	double[] lamdaB;				//[year]
	double[][] classLamdaA;	//[year][age-class],  age-class[0] = all class
	double[][] classLamdaB;	//[year][age-class],  age-class[0] = all class
	
	public CurvatureData(){
		
	}
	
	public CurvatureData(int start_year, int end_year, int class_number, int district_number,int district_type){
		
		this.initiate(start_year, end_year, class_number, district_number, district_type);
	}
	
	public CurvatureData(int[] years, int class_number, int district_number,int district_type){
		
		this.initiate(years, class_number, district_number, district_type);
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
		this.curvature = new double[this.numberOfYears][this.districtNumber];
		this.pressure = new double[this.numberOfYears][this.districtNumber]; 
		this.migration = new double[this.numberOfYears][this.districtNumber]; 
		this.classPressure = new double[this.numberOfYears][this.districtNumber][this.classNumber];
		this.interaction = new double[this.numberOfYears][this.districtNumber][this.districtNumber];
		this.coordinates = new double[this.districtNumber][2];
		this.latlongCorrd = new double[this.districtNumber][2];
		
		this.curvatureSum = new double[this.numberOfYears];
		this.pressureSum = new double[this.numberOfYears];
		this.residualSum = new double[this.numberOfYears];
		this.rmse = new double[this.numberOfYears];
		this.rsquare = new double[this.numberOfYears];
		this.classPressureSum = new double[this.numberOfYears][this.classNumber];
	
		this.scale = new double[this.numberOfYears];
		this.minimum = new double[this.numberOfYears];
		this.weight = new double[this.numberOfYears];
		this.lamdaA = new double[this.numberOfYears];
		this.lamdaB = new double[this.numberOfYears];
		this.classLamdaA = new double[this.numberOfYears][this.classNumber];
		this.classLamdaB = new double[this.numberOfYears][this.classNumber];
	}
	
	/*** set function ***/	
	
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

	public void setLatLongCoordinates(int district, double latitude, double longitude){		
		this.latlongCorrd[district][0] = latitude;
		this.latlongCorrd[district][1] = longitude;
	}
	
	public void setCurvature(int year, int district, double value){
		this.curvature[year][district] = value;
	}
	
	public void setPressure(int year, int district, double value){
		this.pressure[year][district] = value;
	}

	public void setMigration(int year, int district, double value){
		this.migration[year][district] = value;
	}
	
	public void setClassPressure(int year, int district, int age_class, double value){
		this.classPressure[year][district][age_class] = value;
	}

	public void setInteraction(int year, int district_i, int district_j, double value){
		this.interaction[year][district_i][district_j] = value;
	}
	
	public void setCurvatureSum(int year, double value){
		this.curvatureSum[year] = value;
	}
	
	public void setPressureSum(int year, double value){
		this.pressureSum[year] = value;
	}

	public void setResidualSum(int year, double value){
		this.residualSum[year] = value;
	}
	
	public void setRMSE(int year, double value){
		this.rmse[year] = value;
	}
	
	public void setRsquare(int year, double value){
		this.rsquare[year] = value;
	}
	
	public void setClassPressureSum(int year, int age_class, double value){
		this.classPressureSum[year][age_class] = value;
	}
	
	public void setScale(int year, double value){
		this.scale[year] = value;
	}	
	
	public void setMinimum(int year, double value){
		this.minimum[year] = value;
	}	
	
	public void setWeight(int year, double value){
		this.weight[year] = value;
	}	
	
	public void setLambdaA(int year, double value){
		this.lamdaA[year] = value;
	}	

	public void setLambdaB(int year, double value){
		this.lamdaB[year] = value;
	}
	
	public void setClassLamdaA(int year, int age_class, double value){
		this.classLamdaA[year][age_class] = value;
	}	

	public void setClassLamdaB(int year, int age_class, double value){
		this.classLamdaB[year][age_class] = value;
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
	
	public double[] getLatLongCoordinates(int district){		
		
		double[] points = new double[2];
		points[0] = this.latlongCorrd[district][0];
		points[1] = this.latlongCorrd[district][1];
			
		return points;
	}
	
	public double getLatLongLatitude(int district){
		return this.latlongCorrd[district][0];
	}

	public double getLatLongLongitude(int district){
		return this.latlongCorrd[district][1];	
	}
	
	public double getCurvature(int year, int district){
		return this.curvature[year][district];		
	}
	
	public double getPressure(int year, int district){
		return this.pressure[year][district];		
	}

	public double getMigration(int year, int district){
		return this.migration[year][district];		
	}
	
	public double getClassPressure(int year, int district, int age_class){
		return this.classPressure[year][district][age_class];		
	}

	public double getInteraction(int year, int district_i, int district_j){
		return this.interaction[year][district_i][district_j];		
	}
	
	public double getCurvatureSum(int year){
		return this.curvatureSum[year];
	}
	
	public double getPressureSum(int year){
		return this.pressureSum[year];
	}
	
	public double getResidualSum(int year){
		return this.residualSum[year];
	}
	
	public double getRMSE(int year){
		return this.rmse[year];
	}

	public double getRsquare(int year){
		return this.rsquare[year];
	}
	
	public boolean checkRMSE(){
		if(this.rmse.length > 0) return true;
		else return false;
	}
	
	public boolean checkRsquare(){
		if(this.rsquare.length > 0) return true;
		else return false;
	}
	
	public double getClassPressureSum(int year, int age_class){
		return this.classPressureSum[year][age_class];
	}
	
	public double getScale(int year){
		return this.scale[year];
	}
	
	public double getMinimum(int year){
		return this.minimum[year];
	}
	
	public double getWeight(int year){
		return this.weight[year];
	}
	
	public double getLamdaA(int year){
		return this.lamdaA[year];
	}
	
	public double getLamdaB(int year){
		return this.lamdaB[year];
	}
	
	public double[] getLamdaAList(){
		return this.lamdaA;
	}
	
	public double[] getLamdaBList(){
		return this.lamdaB;
	}
	
	public double[] getScaleList(){
		return this.scale;
	}
	
	public double[] getMinimumList(){
		return this.minimum;
	}
	
	public double[] getWeightList(){
		return this.weight;
	}
	
	public double getClassLamdaA(int year, int age_class){
		return this.classLamdaA[year][age_class];
	}

	public double getClassLamdaB(int year, int age_class){
		return this.classLamdaB[year][age_class];
	}

	public double[][] getClassLamdaA(){
		return this.classLamdaA;
	}

	public double[][] getClassLamdaB(){
		return this.classLamdaB;
	}
	
	public void clearData(){
		this.curvature = new double[this.numberOfYears][this.districtNumber];
		this.pressure = new double[this.numberOfYears][this.districtNumber]; 
		this.classPressure = new double[this.numberOfYears][this.districtNumber][this.classNumber];
		this.interaction = new double[this.numberOfYears][this.districtNumber][this.districtNumber];
		
		this.curvatureSum = new double[this.numberOfYears];
		this.pressureSum = new double[this.numberOfYears];
		this.residualSum = new double[this.numberOfYears];
		this.rmse = new double[this.numberOfYears];
		this.classPressureSum = new double[this.numberOfYears][this.classNumber];
	}
	
	public void clearData(int year){
		int i, j ,k;
		for(i=0 ; i<this.numberOfYears ; i++){
			this.curvatureSum[i] = 0.0;
			this.pressureSum[i] = 0.0;
			this.residualSum[i] = 0.0;
			this.rmse[i] = 0.0;
			for(j=0 ; j<this.districtNumber ; j++){
				this.curvature[i][j] = 0.0;
				this.pressure[i][j] = 0.0;
				for(k=0 ; k<this.districtNumber ; k++) this.interaction[i][j][k] = 0.0;
				for(k=0 ; k<this.classNumber ; k++){
					this.classPressure[i][j][k] = 0.0;
					this.classPressureSum[i][k] = 0.0;

				}
			}
		}
	}

	public void normalizePressure(){
		int i, j;
		double tmpMax;
		for(i=0 ; i<this.numberOfYears ; i++){
			tmpMax = 0.0;
			for(j=0 ; j<this.districtNumber ; j++)
				if(Math.abs(this.pressure[i][j]) > tmpMax) tmpMax = Math.abs(this.pressure[i][j]);
			for(j=0 ; j<this.districtNumber ; j++) this.pressure[i][j] /= tmpMax;
		}
	}
	
	public void normalizeSymmetricPressure(){
		int i, j;
		double tmpMax, tmpMin;
		for(i=0 ; i<this.numberOfYears ; i++){
			tmpMax = 0.0;
			tmpMin = 0.0;
			for(j=0 ; j<this.districtNumber ; j++){
				if(this.pressure[i][j] > tmpMax) tmpMax = this.pressure[i][j];
				else if(this.pressure[i][j] < tmpMin) tmpMin = this.pressure[i][j];
			}
			tmpMin = Math.abs(tmpMin);
			for(j=0 ; j<this.districtNumber ; j++){
				if(this.pressure[i][j] > 0.0) this.pressure[i][j] /= tmpMax;
				else if(this.pressure[i][j] < 0.0) this.pressure[i][j] /= tmpMin;
			}
		}
	}
}
