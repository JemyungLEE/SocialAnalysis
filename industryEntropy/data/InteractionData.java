package industryEntropy.data;

public class InteractionData {

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
	
	double[] minimum;					//[year]
	double[] weightA;					//[year]
	double[] weightB;					//[year]
	double[] lamdaA;					//[year]
	double[] lamdaB;					//[year]
	double[] weightD;					//[year]
	
	double[][] ageStructure;		//[year][district]
	
	double[][] distance;				//[district][district]	
	double[][][] distanceIndex;		//]year][district][district]	
	
	double[][][] interactionIn;		//]year][district][district]	
	double[][][] interactionOut;	//]year][district][district]	
	double[][][] interactionNet;	//]year][district][district]	
	
	double[][] migrationIn;			//[year][district]
	double[][] migrationOut;		//[year][district]
	double[][] migrationNet;			//[year][district]
		
	double[] totalIn;						//[year]
	double[] totalOut;					//[year]
	double[] totalNet;					//[year]
	
	double[][] observedIn;			//[year][district]
	double[][] observedOut;			//[year][district]
	double[][] observedNet;			//[year][district]
	double[][] observedInRate;	//[year][district]
	double[][] observedOutRate;	//[year][district]
	
	double[] rmseIn;						//[year]
	double[] rmseOut;					//[year]
	double[] rmseNet;					//[year]
	double[] rmseInRate;				//[year]
	double[] rmseOutRate;			//[year]
	
	double[] rsquareIn;					//[year]
	double[] rsquareOut;				//[year]
	double[] rsquareNet;				//[year]
	double[] rsquareInRate;			//[year]
	double[] rsquareOutRate;		//[year]
	
	public InteractionData(){
		
	}
	
	public InteractionData(int start_year, int end_year, int class_number, int district_number,int district_type){
		
		this.initiate(start_year, end_year, class_number, district_number, district_type);
	}
	
	public InteractionData(int[] years, int class_number, int district_number,int district_type){
		
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
		this.coordinates = new double[this.districtNumber][2];
		this.latlongCorrd = new double[this.districtNumber][2];
		
		this.minimum = new double[this.numberOfYears];
		this.weightA = new double[this.numberOfYears];
		this.weightB = new double[this.numberOfYears];
		this.lamdaA = new double[this.numberOfYears];
		this.lamdaB = new double[this.numberOfYears];
		this.weightD = new double[this.numberOfYears];
		
		this.ageStructure = new double[this.numberOfYears][this.districtNumber];
		
		this.distance = new double[this.districtNumber][this.districtNumber];
		this.distanceIndex = new double[this.numberOfYears][this.districtNumber][this.districtNumber];
		
		this.interactionIn = new double[this.numberOfYears][this.districtNumber][this.districtNumber];
		this.interactionOut = new double[this.numberOfYears][this.districtNumber][this.districtNumber];
		this.interactionNet = new double[this.numberOfYears][this.districtNumber][this.districtNumber];
	
		this.migrationIn = new double[this.numberOfYears][this.districtNumber]; 
		this.migrationOut = new double[this.numberOfYears][this.districtNumber]; 
		this.migrationNet = new double[this.numberOfYears][this.districtNumber]; 
		
		this.totalIn = new double[this.numberOfYears];
		this.totalOut = new double[this.numberOfYears];
		this.totalNet = new double[this.numberOfYears];
		
		this.observedIn = new double[this.numberOfYears][this.districtNumber]; 
		this.observedOut = new double[this.numberOfYears][this.districtNumber]; 
		this.observedNet = new double[this.numberOfYears][this.districtNumber]; 
		this.observedInRate = new double[this.numberOfYears][this.districtNumber]; 
		this.observedOutRate = new double[this.numberOfYears][this.districtNumber]; 

		this.rmseIn = new double[this.numberOfYears];
		this.rmseOut = new double[this.numberOfYears];
		this.rmseNet = new double[this.numberOfYears];
		this.rmseInRate = new double[this.numberOfYears];
		this.rmseOutRate = new double[this.numberOfYears];

		this.rsquareIn = new double[this.numberOfYears];
		this.rsquareOut = new double[this.numberOfYears];
		this.rsquareNet = new double[this.numberOfYears];
		this.rsquareInRate = new double[this.numberOfYears];
		this.rsquareOutRate = new double[this.numberOfYears];
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
	
	public void setAgeStructureIndex(int year, int district, double value){
		this.ageStructure[year][district] = value;
	}

	public void setDistance(int district_i, int district_j, double value){
		this.distance[district_i][district_j] = value;
	}
	
	public void setDistanceIndex(int year, int district_i, int district_j, double value){
		this.distanceIndex[year][district_i][district_j] = value;
	}
	
	public void setInteractionIn(int year, int district_i, int district_j, double value){
		this.interactionIn[year][district_i][district_j] = value;
	}
	
	public void setInteractionOut(int year, int district_i, int district_j, double value){
		this.interactionOut[year][district_i][district_j] = value;
	}
	
	public void setInteractionNet(int year, int district_i, int district_j, double value){
		this.interactionNet[year][district_i][district_j] = value;
	}
	
	public void setMigrationIn(int year, int district, double value){
		this.migrationIn[year][district] = value;
	}
	
	public void setMigrationOut(int year, int district, double value){
		this.migrationOut[year][district] = value;
	}
	
	public void setMigrationNet(int year, int district, double value){
		this.migrationNet[year][district] = value;
	}

	public void setTotalIn(int year, double value){
		this.totalIn[year] = value;
	}
	
	public void setTotalOut(int year, double value){
		this.totalOut[year] = value;
	}
	
	public void setTotalNet(int year, double value){
		this.totalNet[year] = value;
	}
	
	public void setObservedIn(int year, int district, double value){
		this.observedIn[year][district] = value;
	}
	
	public void setObservedOut(int year, int district, double value){
		this.observedOut[year][district] = value;
	}
	
	public void setObservedNet(int year, int district, double value){
		this.observedNet[year][district] = value;
	}
	
	public void setObservedInRate(int year, int district, double value){
		this.observedInRate[year][district] = value;
	}
	
	public void setObservedOutRate(int year, int district, double value){
		this.observedOutRate[year][district] = value;
	}

	
	public void setRMSE(int year, double[] values){
		if(values.length==3 || values.length==5){
			this.rmseIn[year] = values[0];
			this.rmseOut[year] = values[1];
			this.rmseNet[year] = values[2];
			if(values.length == 5){
				this.rmseInRate[year] = values[3];
				this.rmseOutRate[year] = values[4];
			}
		}else{
			System.err.println("RMSE arraya size error");
		}
	}
	
	public void setRmseIn(int year, double value){
		this.rmseIn[year] = value;
	}
	
	public void setRmseOut(int year, double value){
		this.rmseOut[year] = value;
	}
	
	public void setRmseNet(int year, double value){
		this.rmseNet[year] = value;
	}
	
	public void setRmseInRate(int year, double value){
		this.rmseInRate[year] = value;
	}
	
	public void setRmseOutRate(int year, double value){
		this.rmseOutRate[year] = value;
	}

	
	public void setRsquare(int year, double[] values){
		if(values.length==3 || values.length==5){
			this.rsquareIn[year] = values[0];
			this.rsquareOut[year] = values[1];
			this.rsquareNet[year] = values[2];
			if(values.length==5){
				this.rsquareInRate[year] = values[3];
				this.rsquareOutRate[year] = values[4];
			}
		}else{
			System.err.println("R-square arraya size error");
		}
	}
	
	public void setRsquareIn(int year, double value){
		this.rsquareIn[year] = value;
	}
	
	public void setRsquareOut(int year, double value){
		this.rsquareOut[year] = value;
	}
	
	public void setRsquareNet(int year, double value){
		this.rsquareNet[year] = value;
	}
	
	public void setRsquareInRate(int year, double value){
		this.rsquareInRate[year] = value;
	}
	
	public void setRsquareOutRate(int year, double value){
		this.rsquareOutRate[year] = value;
	}

	
	public void setMinimum(int year, double value){
		this.minimum[year] = value;
	}	
	
	public void setWeightA(int year, double value){
		this.weightA[year] = value;
	}	
	
	public void setWeightB(int year, double value){
		this.weightB[year] = value;
	}	
	
	public void setLambdaA(int year, double value){
		this.lamdaA[year] = value;
	}	

	public void setLambdaB(int year, double value){
		this.lamdaB[year] = value;
	}
	
	public void setWeightD(int year, double value){
		this.weightD[year] = value;
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

	public double getAgeStructureIndex(int year, int district){
		return this.ageStructure[year][district];
	}

	public double getDistance(int district_i, int district_j){
		return this.distance[district_i][district_j];
	}
	
	public double getDistanceIndex(int year, int district_i, int district_j){
		return this.distanceIndex[year][district_i][district_j];
	}
	
	public double getInteractionIn(int year, int district_i, int district_j){
		return this.interactionIn[year][district_i][district_j];		
	}
	
	public double getInteractionOut(int year, int district_i, int district_j){
		return this.interactionOut[year][district_i][district_j];		
	}
	
	public double getInteractionNet(int year, int district_i, int district_j){
		return this.interactionNet[year][district_i][district_j];		
	}
	
	public double getMigrationIn(int year, int district){
		return this.migrationIn[year][district];
	}
	
	public double getMigrationOut(int year, int district){
		return this.migrationOut[year][district];
	}
	
	public double getMigrationNet(int year, int district){
		return this.migrationNet[year][district];
	}

	public double getTotalIn(int year){
		return this.totalIn[year];
	}
	
	public double getTotalOut(int year){
		return this.totalOut[year];
	}
	
	public double getTotalNet(int year){
		return this.totalNet[year];
	}
	
	public double getObservedIn(int year, int district){
		return this.observedIn[year][district];
	}
	
	public double getObservedOut(int year, int district){
		return this.observedOut[year][district];
	}
	
	public double getObservedNet(int year, int district){
		return this.observedNet[year][district];
	}

	public double getObservedInRate(int year, int district){
		return this.observedInRate[year][district];
	}
	
	public double getObservedOutRate(int year, int district){
		return this.observedOutRate[year][district];
	}
	
	public double[] getRMSE(int year){
		double[] rmse = {this.rmseIn[year], this.rmseOut[year], this.rmseNet[year], this.rmseInRate[year], this.rmseOutRate[year]};
		return rmse;
	}
	
	public double getRmseIn(int year){
		return this.rmseIn[year];
	}

	public double getRmseOut(int year){
		return this.rmseOut[year];
	}
	
	public double getRmseNet(int year){
		return this.rmseNet[year];
	}
	
	public double getRmseInRate(int year){
		return this.rmseInRate[year];
	}

	public double getRmseOutRate(int year){
		return this.rmseOutRate[year];
	}
	
	public double[] getRsquare(int year){
		double[] rSquare = {this.rsquareIn[year], this.rsquareOut[year], this.rsquareNet[year], this.rsquareInRate[year], this.rsquareOutRate[year]};
		return rSquare;
	}
	
	public double getRsquareIn(int year){
		return this.rsquareIn[year];
	}
	
	public double getRsquareOut(int year){
		return this.rsquareOut[year];
	}
	
	public double getRsquareNet(int year){
		return this.rsquareNet[year];
	}
	
	public double getRsquareInRate(int year){
		return this.rsquareInRate[year];
	}
	
	public double getRsquareOutRate(int year){
		return this.rsquareOutRate[year];
	}
	
	public boolean checkRMSE(){
		if(this.rmseIn.length > 0 || this.rmseOut.length > 0 || this.rmseNet.length > 0) return true;
		else return false;
	}
	
	public boolean checkRsquare(){
		if(this.rsquareIn.length > 0 || this.rsquareOut.length > 0 || this.rsquareNet.length > 0) return true;
		else return false;
	}
	
	public double getMinimum(int year){
		return this.minimum[year];
	}
	
	public double getWeightA(int year){
		return this.weightA[year];
	}
	
	public double getWeightB(int year){
		return this.weightB[year];
	}
	
	public double getLamdaA(int year){
		return this.lamdaA[year];
	}
	
	public double getLamdaB(int year){
		return this.lamdaB[year];
	}

	public double getWeightD(int year){
		return this.weightD[year];
	}
	
	public double[] getLamdaAList(){
		return this.lamdaA;
	}
	
	public double[] getLamdaBList(){
		return this.lamdaB;
	}
	
	public double[] getMinimumList(){
		return this.minimum;
	}
	
	public double[] getWeightAList(){
		return this.weightA;
	}
	
	public double[] getWeightBList(){
		return this.weightB;
	}
	
	public double[] getWeightDList(){
		return this.weightD;
	}
	
	public void clearData(){
		this.minimum = new double[this.numberOfYears];
		this.weightA = new double[this.numberOfYears];
		this.weightB = new double[this.numberOfYears];
		this.lamdaA = new double[this.numberOfYears];
		this.lamdaB = new double[this.numberOfYears];
		this.weightD = new double[this.numberOfYears];
		
		this.ageStructure = new double[this.numberOfYears][this.districtNumber];
		
		this.distanceIndex = new double[this.numberOfYears][this.districtNumber][this.districtNumber];
		
		this.interactionIn = new double[this.numberOfYears][this.districtNumber][this.districtNumber];
		this.interactionOut = new double[this.numberOfYears][this.districtNumber][this.districtNumber];
		this.interactionNet = new double[this.numberOfYears][this.districtNumber][this.districtNumber];
		
		this.migrationIn = new double[this.numberOfYears][this.districtNumber]; 
		this.migrationOut = new double[this.numberOfYears][this.districtNumber]; 
		this.migrationNet = new double[this.numberOfYears][this.districtNumber]; 
		
		this.totalIn = new double[this.numberOfYears];
		this.totalOut = new double[this.numberOfYears];
		this.totalNet = new double[this.numberOfYears];
		
		this.rmseIn = new double[this.numberOfYears];
		this.rmseOut = new double[this.numberOfYears];
		this.rmseNet = new double[this.numberOfYears];
		this.rmseInRate = new double[this.numberOfYears];
		this.rmseOutRate = new double[this.numberOfYears];

		this.rsquareIn = new double[this.numberOfYears];
		this.rsquareOut = new double[this.numberOfYears];
		this.rsquareNet = new double[this.numberOfYears];
		this.rsquareInRate = new double[this.numberOfYears];
		this.rsquareOutRate = new double[this.numberOfYears];
	}
	
	public void clearData(int year){		
		int i, j;
		this.minimum[year] = 0.0;
		this.weightA[year] = 0.0;
		this.weightB[year] = 0.0;
		this.lamdaA[year] = 0.0;
		this.lamdaB[year] = 0.0;
		this.weightD[year] = 0.0;
		
		this.totalIn[year] = 0.0;
		this.totalOut[year] = 0.0;
		this.totalNet[year] = 0.0;
		this.rmseIn[year] = 0.0;
		this.rmseOut[year] = 0.0;
		this.rmseNet[year] = 0.0;
		this.rmseInRate[year] = 0.0;
		this.rmseOutRate[year] = 0.0;
		this.rsquareIn[year] = 0.0;
		this.rsquareOut[year] = 0.0;
		this.rsquareNet[year] = 0.0;
		this.rsquareInRate[year] = 0.0;
		this.rsquareOutRate[year] = 0.0;

		for(i=0 ; i<this.districtNumber ; i++){
			this.ageStructure[year][i] = 0.0;
			this.migrationIn[year][i] = 0.0;
			this.migrationOut[year][i] = 0.0;
			this.migrationNet[year][i] = 0.0;
			for(j=0 ; j<this.districtNumber ; j++){
				this.distanceIndex[year][i][j] = 0.0;
				this.interactionIn[year][i][j] = 0.0;
				this.interactionOut[year][i][j] = 0.0;
				this.interactionNet[year][i][j] = 0.0;
			}
		}
	}

	public void normalizeMigration(){
		this.normalizeMigration(1.0, 0.1);
	}
	
	public void normalizeMigration(double max, double min){
		int i, j;
		double tmpMaxIn, tmpMaxOut, tmpMaxNet;
		double tmpMinIn, tmpMinOut, tmpMinNet;
		double transferIn, transferOut, transferNet;
		
		for(i=0 ; i<this.numberOfYears ; i++){
			tmpMaxIn = 0.0;
			tmpMaxOut = 0.0;
			tmpMaxNet = 0.0;
			for(j=0 ; j<this.districtNumber ; j++){
				if(this.migrationIn[i][j] > tmpMaxIn) tmpMaxIn = this.migrationIn[i][j];
				if(this.migrationOut[i][j] > tmpMaxOut) tmpMaxOut = this.migrationOut[i][j];
				if(Math.abs(this.migrationNet[i][j]) > tmpMaxNet) tmpMaxNet = Math.abs(this.migrationNet[i][j]);
			}
			tmpMinIn = tmpMaxIn;
			tmpMinOut = tmpMaxOut;
			tmpMinNet = tmpMaxNet;
			for(j=0 ; j<this.districtNumber ; j++){
				if(this.migrationIn[i][j] > 0 && this.migrationIn[i][j] < tmpMinIn) tmpMinIn = this.migrationIn[i][j];
				if(this.migrationOut[i][j] > 0 && this.migrationOut[i][j] < tmpMinOut) tmpMinOut = this.migrationOut[i][j];
				if(Math.abs(this.migrationNet[i][j]) > 0 && Math.abs(this.migrationNet[i][j]) < tmpMinNet) tmpMinNet = Math.abs(this.migrationNet[i][j]);
			}
			transferIn = (max - min) / (tmpMaxIn - tmpMinIn);
			transferOut = (max - min) / (tmpMaxOut - tmpMinOut);
			transferNet = (max - min) / (tmpMaxNet - tmpMinNet);
				
			
			for(j=0 ; j<this.districtNumber ; j++){
				if(this.migrationIn[i][j] > 0)
					this.migrationIn[i][j] = (this.migrationIn[i][j] - tmpMinIn) * transferIn + min;
				if(this.migrationOut[i][j] > 0)
					this.migrationOut[i][j] = (this.migrationOut[i][j] - tmpMinOut) * transferOut + min;
				if(Math.abs(this.migrationNet[i][j]) > 0)
					this.migrationNet[i][j] = (Math.abs(this.migrationNet[i][j]) - tmpMinNet) * transferNet + min;
				if(this.migrationNet[i][j] < 0) this.migrationNet[i][j] *= -1.0;
			}
			
			/*
			for(j=0 ; j<this.districtNumber ; j++){
				if(this.migrationIn[i][j] > 0)
					this.migrationIn[i][j] = this.migrationIn[i][j] / tmpMaxIn;
				if(this.migrationOut[i][j] > 0)
					this.migrationOut[i][j] = this.migrationOut[i][j] / tmpMaxOut;
				if(Math.abs(this.migrationNet[i][j]) > 0)
					this.migrationNet[i][j] = this.migrationNet[i][j] / tmpMaxNet;
			}
			*/
		}
	}
	
	public void normalizeMigration(int year, double max, double min){
		int i;
		int yearIndex = this.getYearIndex(year);
		double tmpMaxIn, tmpMaxOut, tmpMaxNet;
		double tmpMinIn, tmpMinOut, tmpMinNet;
		double transferIn, transferOut, transferNet;
		
		tmpMaxIn = 0.0;
		tmpMaxOut = 0.0;
		tmpMaxNet = 0.0;
		for(i=0 ; i<this.districtNumber ; i++){
			if(this.migrationIn[yearIndex][i] > tmpMaxIn) tmpMaxIn = this.migrationIn[yearIndex][i];
			if(this.migrationOut[yearIndex][i] > tmpMaxOut) tmpMaxOut = this.migrationOut[yearIndex][i];
			if(Math.abs(this.migrationNet[yearIndex][i]) > tmpMaxNet) tmpMaxNet = Math.abs(this.migrationNet[yearIndex][i]);
		}
		tmpMinIn = tmpMaxIn;
		tmpMinOut = tmpMaxOut;
		tmpMinNet = tmpMaxNet;
		for(i=0 ; i<this.districtNumber ; i++){
			if(this.migrationIn[yearIndex][i] > 0 && this.migrationIn[yearIndex][i] < tmpMinIn) tmpMinIn = this.migrationIn[yearIndex][i];
			if(this.migrationOut[yearIndex][i] > 0 && this.migrationOut[yearIndex][i] < tmpMinOut) tmpMinOut = this.migrationOut[yearIndex][i];
			if(Math.abs(this.migrationNet[yearIndex][i]) > 0 && Math.abs(this.migrationNet[yearIndex][i]) < tmpMinNet) tmpMinNet = Math.abs(this.migrationNet[yearIndex][i]);
		}
		transferIn = (max - min) / (tmpMaxIn - tmpMinIn);
		transferOut = (max - min) / (tmpMaxOut - tmpMinOut);
		transferNet = (max - min) / (tmpMaxNet - tmpMinNet);
		
		
		for(i=0 ; i<this.districtNumber ; i++){
			if(this.migrationIn[yearIndex][i] > 0)
				this.migrationIn[yearIndex][i] = (this.migrationIn[yearIndex][i] - tmpMinIn) * transferIn + min;
			if(this.migrationOut[yearIndex][i] > 0)
				this.migrationOut[yearIndex][i] = (this.migrationOut[yearIndex][i] - tmpMinOut) * transferOut + min;
			if(Math.abs(this.migrationNet[yearIndex][i]) > 0)
				this.migrationNet[yearIndex][i] = (Math.abs(this.migrationNet[yearIndex][i]) - tmpMinNet) * transferNet + min;
			if(this.migrationNet[yearIndex][i] < 0) this.migrationNet[yearIndex][i] *= -1.0;
		}
		
		/*
		for(i=0 ; i<this.districtNumber ; i++){
			if(this.migrationIn[yearIndex][i] > 0)
				this.migrationIn[yearIndex][i] = this.migrationIn[yearIndex][i] / tmpMaxIn;
			if(this.migrationOut[yearIndex][i] > 0)
				this.migrationOut[yearIndex][i] = this.migrationOut[yearIndex][i] / tmpMaxOut;
			if(Math.abs(this.migrationNet[yearIndex][i]) > 0)
				this.migrationNet[yearIndex][i] = this.migrationNet[yearIndex][i] / tmpMaxNet;
		}
		*/
	}
	
	public void logarithmMigration(int year){
		int yearIndex = this.getYearIndex(year);
		
		for(int i=0 ; i<this.districtNumber ; i++){
			if(this.migrationIn[yearIndex][i]>0) this.migrationIn[yearIndex][i] = 1.0+Math.log10(this.migrationIn[yearIndex][i]);
			if(this.migrationOut[yearIndex][i]>0) this.migrationOut[yearIndex][i] = 1.0+Math.log10(this.migrationOut[yearIndex][i]);
			if(Math.abs(this.migrationNet[yearIndex][i])>0) this.migrationNet[yearIndex][i] = 1.0+Math.log10(Math.abs(this.migrationNet[yearIndex][i]));
			if(this.migrationNet[yearIndex][i]<0) this.migrationNet[yearIndex][i] *= -1.0;
		}
	}
}
