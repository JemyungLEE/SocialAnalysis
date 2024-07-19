package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import populationEntropy.data.*;

public class CurvatureModel {
	
	int populationFunctionType;
	int distanceFunctionType;
	double populatoinConstant;
	double distanceConstant;
	
	public CurvatureModel(){
		
	}
	
	
	public CurvatureModel(int pop_type, double pop_constant, int dist_type, double dist_constant){
		
		this.populationFunctionType = pop_type;
		this.populatoinConstant = pop_constant;
		this.distanceFunctionType = dist_type;
		this.distanceConstant = dist_constant;
	}
	
	
	public double populationFunction(int year, int district, EntropyData eData){
	
		int i;
		double max_population;		 
		double population;
						
		/*** get population ***/
		population = eData.getPopulation(year, district);			
		
		/*** get normalized population ***/		
		max_population = 0;
		for(i=0 ; i<eData.getDistricNumber() ; i++) 
			if(max_population < eData.getPopulation(year, i)) max_population = eData.getPopulation(year, i);  
		population /= max_population;	
				
		/*** return population value ***/
		/*
		if(this.populationFunctionType == 0 && population>0) 
			return Math.log(population)/Math.log(this.populatoinConstant);
		else if(this.populationFunctionType == 1 && population>0) 
			return population / this.populatoinConstant;
		else if(this.populationFunctionType == 2 && population>0) 
			return Math.pow( population, this.populatoinConstant);
		else return 0.0;
		*/
		return population;
	}
	
	public double populationFunction(int year, int district, int age_class, PopulationData pData){
			
		double population = (double) pData.getPopulation(year, district, age_class);
		
		/*** set low limit boundary of emigration ***/
		if((double) pData.getPopulation(year, district) > (double) pData.getPopulation(0, district) * 0.0 ){
			
			/*** get normalized population ***/		
			population /= (double) pData.getMaxPopulation(year, age_class);	 
		//	population /= (double) pData.getMaxPopulation(year, 0);	 
		}
		else population = 0.0;
		
		/*** return population value ***/
		/*
		if(this.populationFunctionType == 0 && population>0) 
			return Math.log(population)/Math.log(this.populatoinConstant);
		else if(this.populationFunctionType == 1 && population>0) 
			return population / this.populatoinConstant;
		else if(this.populationFunctionType == 2 && population>0) 
			return Math.pow( population, this.populatoinConstant);
		else return 0.0;
		*/	
		
		return population;
	}

	
	public double populationFunction(int year, int district, PopulationData pData){
				
		int i;
		int lower_class = 1;
		int upper_class = 20;		
		double max_population;		 
		double population;
						
		/*** get population ***/
		//population = pData.getPopulation(year, district);	
		
		/*** get partial population ***/
		population = 0.0;
		for(i=lower_class ; i<=upper_class ; i++) 
			population += (double) pData.getPopulation(year, district, i);
		
		/*** get normalized population ***/		
		max_population = 0;
		for(i=0 ; i<pData.getDistricNumber() ; i++) 
			if(max_population < pData.getPopulation(year, i)) 
				max_population = (double) pData.getPopulation(year, i);  
		population /= max_population;	 
				
		/*** return population value ***/
		/*
		if(this.populationFunctionType == 0 && population>0) 
			return Math.log(population)/Math.log(this.populatoinConstant);
		else if(this.populationFunctionType == 1 && population>0) 
			return population / this.populatoinConstant;
		else if(this.populationFunctionType == 2 && population>0) 
			return Math.pow( population, this.populatoinConstant);
		else return 0.0;
		*/
		return population;
	}
	
	
	public double distanceFuction(int year, int district_i, int district_j, EntropyData eData){
		
		double distance = Math.sqrt(
				 (Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
			      + Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
		/*
		if(this.distanceFunctionType == 0) 
			return 1.0 / (1.0 + Math.pow(this.distanceConstant, (-1.0 * distance + 0.5)));
		else if(this.distanceFunctionType == 1) return Math.pow(distance, this.distanceConstant);
		else return 0.0;
		*/
		return Math.pow(1.0 / (1.0 + Math.pow(this.distanceConstant, (0.5 - distance))), 2);
	}

	public double distanceFuction(int year, int district_i, int district_j, EntropyData eData, double rambda){
		
		double distance = Math.sqrt(
				 (Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
			      + Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
		/*
		if(this.distanceFunctionType == 0) return 1.0 / (1.0 + Math.pow(rambda, (-1.0 * distance + 0.5)));
		else if(this.distanceFunctionType == 1) return Math.pow(distance, rambda);
		else return 0.0;
		*/		
		return Math.pow(1.0 / (1.0 + Math.pow(10, rambda * (0.5 - distance))), 2);
	}

	public double distanceFuction(int year, int district_i, int district_j, EntropyData eData, 
								  double rambdaA, double rambdaB){
		
		double distance = Math.sqrt(
				 (Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
			      + Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
		/*
		if(this.distanceFunctionType == 0) return 1.0 / (1.0 + Math.pow(rambda, (-1.0 * distance + 0.5)));
		else if(this.distanceFunctionType == 1) return Math.pow(distance, rambda);
		else return 0.0;
		*/		
		return Math.pow(1.0 / (1.0 + Math.pow(10, rambdaA * (rambdaB - distance))), 2);
	}
	
	
	public double calculateCurvature(int year, int district_i, int district_j, EntropyData eData){
				
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
				
		if (entropy_i == 0 || entropy_j ==0) return 0.0;
		else if(district_i == district_j) return 0.0;
		else return (entropy_i-entropy_j) / this.distanceFuction(year,district_i,district_j,eData);
	}

	public double calculateClassCurvature(int year, int district_i, int district_j, int age_class, 
										  EntropyData eData){
		
		double entropy_i = eData.getClassEntropy(year, district_i, age_class); 
		double entropy_j = eData.getClassEntropy(year, district_j, age_class); 
		
		if (entropy_i == 0 || entropy_j ==0) return 0.0;
		else if(district_i == district_j) return 0.0;
		else return (entropy_i-entropy_j) / this.distanceFuction(year,district_i,district_j,eData);
	}

	public double calculateClassCurvature(int year, int district_i, int district_j, int age_class, 
			  							  EntropyData eData, double rambda){

		double entropy_i = eData.getClassEntropy(year, district_i, age_class); 
		double entropy_j = eData.getClassEntropy(year, district_j, age_class); 
		
		if (entropy_i == 0 || entropy_j ==0) return 0.0;
		else if(district_i == district_j) return 0.0;
		else return (entropy_i-entropy_j) 
						/ this.distanceFuction(year, district_i, district_j, eData, rambda);
	}

	public double calculateClassCurvature(int year, int district_i, int district_j, int age_class, 
			  EntropyData eData, double rambdaA, double rambdaB){

	//	double entropy_i = eData.getClassEntropy(year, district_i, age_class); 
	//	double entropy_j = eData.getClassEntropy(year, district_j, age_class);
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j); 
		double distance  = this.distanceFuction(year, district_i, district_j, eData, rambdaA, rambdaB);
				
		if (entropy_i == 0 || entropy_j ==0) return 0.0;
		else if(district_i == district_j) return 0.0;
		else if(distance > 0) return (entropy_i-entropy_j) / distance;
		else return 0.0;
	}
	/*
	public double calculatePressure(int year, int district_i, int district_j, EntropyData eData){
		
		return this.calculateCurvature(year, district_i, district_j, eData)
				* this.populationFunction(year, district_i, eData)
				* this.populationFunction( year, district_j, eData);
	}
	
	public double calculateClassPressure(int year, int district_i, int district_j, int age_class, 
										 EntropyData eData, PopulationData pData){
		
		return this.calculateClassCurvature(year, district_i, district_j, age_class, eData)
				* this.populationFunction(year, district_i, age_class, pData)
				* this.populationFunction( year, district_j, age_class, pData);
	}

	public double calculateClassPressure(int year, int district_i, int district_j, int age_class, 
										 EntropyData eData, PopulationData pData, double rambda){

		return this.calculateClassCurvature(year, district_i, district_j, age_class, eData, rambda)
				* this.populationFunction(year, district_i, age_class, pData)
				* this.populationFunction( year, district_j, age_class, pData);
	}
	
	
	public double calculateClassPressure(int year, int district_i, int district_j, int age_class, 
			 						 EntropyData eData, PopulationData pData, double rambdaA, double rambdaB){

		return this.calculateClassCurvature(year, district_i, district_j, age_class, eData, rambdaA, rambdaB)
				* this.populationFunction(year, district_i, age_class, pData)
				* this.populationFunction( year, district_j, age_class, pData);
	}
	
	public double calculatePressure(int year, int district_i, int district_j, EntropyData eData, 
									PopulationData pData){
		
		return this.calculateCurvature(year, district_i, district_j, eData)
				* this.populationFunction(year, district_i, pData)
				* this.populationFunction(year, district_j, pData);
	}
	*/	
	public double calculateCurvature(int year, int district, EntropyData eData){
		
		int i;
		double curvature = 0.0;
						
		for(i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district) 
				curvature += (this.calculateCurvature(year, district, i, eData) 
				  	 	     *this.populationFunction(year, i, eData));
				
		return curvature;		
	}
	
	public double calculateCurvature(int year, int district, EntropyData eData, PopulationData pData){
		
		int i;
		double curvature = 0.0;
						
		for(i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district) 
				curvature += (this.calculateCurvature(year, district, i, eData) 
				  	 	     *this.populationFunction(year, i, pData));
				
		return curvature;		
	}
	
	public double calculateClassCurvature(int year, int district, int age_class, EntropyData eData, 
									 PopulationData pData){		
		int i;
		double curvature = 0.0;
						
		for(i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district) 
				curvature += (this.calculateClassCurvature(year, district, i, age_class, eData) 
				  	 	     *this.populationFunction(year, i, age_class, pData));		
		return curvature;		
	}
	
	public double calculateClassCurvature(int year, int district, int age_class, EntropyData eData, 
			 PopulationData pData, double rambda){
		int i;
		double curvature = 0.0;
		
		for(i=0 ; i<eData.getDistricNumber() ; i++)
		if(i != district) 
			curvature += (this.calculateClassCurvature(year, district, i, age_class, eData, rambda) 
			 			  *this.populationFunction(year, i, age_class, pData));		
		return curvature;		
	}
	
	public double calculateClassCurvature(int year, int district, int age_class, EntropyData eData, 
			 PopulationData pData, double rambdaA, double rambdaB){
		int i;
		double curvature = 0.0;
		
		for(i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district) 
				curvature += (this.calculateClassCurvature(year,district,i,age_class,eData, rambdaA, rambdaB) 
								*this.populationFunction(year, i, age_class, pData));		

		return curvature;		
	}
	
	public double calculatePressure(int year, int district, EntropyData eData){
	
		return this.calculateCurvature(year, district, eData)
				* this.populationFunction(year, district, eData);		
	}

	public double calculatePressure(int year, int district, EntropyData eData, PopulationData pData){
		
		return this.calculateCurvature(year, district, eData, pData)
				* this.populationFunction(year, district, pData);		
	}

	public double calculateClassPressure(int year, int district, int age_class, EntropyData eData, 
									PopulationData pData){
		
		return this.calculateClassCurvature(year, district, age_class, eData, pData)
				* this.populationFunction(year, district, age_class, pData);		
	}

	public double calculateClassPressure(int year, int district, int age_class, EntropyData eData, 
										 PopulationData pData, double rambda){

		return this.calculateClassCurvature(year, district, age_class, eData, pData, rambda)
				* this.populationFunction(year, district, age_class, pData);		
	}
	
	public double calculateClassPressure(int year, int district, int age_class, EntropyData eData, 
										 PopulationData pData, double rambdaA, double rambdaB){
			
		/*
		System.out.println("class pressure: "+year+"  "+district+"  "+age_class+"  "+
				this.calculateClassCurvature(year, district, age_class, eData, pData, rambdaA, rambdaB)+"  "+
				this.populationFunction(year, district, age_class, pData)+"  "+
				pData.getPopulation(year, district, age_class)   );
		*/
		
		if(pData.getPopulation(year, district, age_class) == 0) return 0.0;
		else return this.calculateClassCurvature(year, district, age_class, eData, pData, rambdaA, rambdaB)
					* this.populationFunction(year, district, age_class, pData);	
		

	}
	
	public void calculateCurvature(CurvatureData cData, EntropyData eData){
		
		int i, j;
		int duration = cData.getDuration();
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<district ; j++)
				cData.setCurvature(i, j, this.calculateCurvature(i, j, eData));
		
		/*** calculate curvature sum ***/
		for(i=0 ; i<duration ; i++){
			tempSum = 0.0;
			for(j=0 ; j<district ; j++) 
				if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
					tempSum += cData.getCurvature(i, j);
			cData.setCurvatureSum(i, tempSum);
		}
	}

	public void calculateCurvature(CurvatureData cData, EntropyData eData, PopulationData pData){
		
		int i, j;
		int duration = cData.getDuration();
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<district ; j++)
				cData.setCurvature(i, j, this.calculateCurvature(i, j, eData, pData));
		
		/*** calculate curvature sum ***/
		for(i=0 ; i<duration ; i++){
			tempSum = 0.0;
			for(j=0 ; j<district ; j++) 
				if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
					tempSum += cData.getCurvature(i, j);
			cData.setCurvatureSum(i, tempSum);
		}
	}
	
	public void calculatePressure(CurvatureData cData, EntropyData eData){

		int i, j;
		int duration = cData.getDuration();
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				pressure = this.calculateCurvature(i,j,eData)*this.populationFunction(i, j, eData);
				cData.setPressure(i, j, pressure);
			}
		}
		
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
	}
	
	public void calculatePressure(CurvatureData cData, EntropyData eData, PopulationData pData){

		int i, j;
		int duration = cData.getDuration();
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				pressure = this.calculateCurvature(i, j, eData, pData) * this.populationFunction(i, j, pData);
				cData.setPressure(i, j, pressure);
			}
		}
		
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
	}
	
	public void calculatePressureSum(CurvatureData cData){		
		int i, j;
		int duration = cData.getDuration();
		int district = cData.getDistricNumber();		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		double tempSum;
		
		for(i=0 ; i<duration ; i++){
			tempSum = 0.0;
			for(j=0 ; j<district ; j++) 
				if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
					tempSum += cData.getPressure(i, j);
			cData.setPressureSum(i, tempSum);
		}
	}
	
	public void calculateClassPressure(CurvatureData cData, EntropyData eData, PopulationData pData){

		int i, j, k;
		int duration = cData.getDuration();
		int district = cData.getDistricNumber();
		int classNum = cData.getClassNumber();
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<district ; j++)
				for(k=0 ; k<classNum ; k++)
					cData.setClassPressure(i, j, k, this.calculateClassPressure(i, j, k, eData, pData));				
		
		/*** calculate pressure sum ***/
		this.calculateClassPressureSum(cData);
	}
	
	public void calculateClassPressure(CurvatureData cData, EntropyData eData, PopulationData pData,
										double[][] rambda){

		int i, j, k;
		int duration = cData.getDuration();
		int district = cData.getDistricNumber();
		int classNum = cData.getClassNumber();
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<district ; j++)
				for(k=0 ; k<classNum ; k++)
					cData.setClassPressure(i,j,k, this.calculateClassPressure(i,j,k, eData,pData, rambda[i][k]));				
		
		/*** calculate pressure sum ***/
		this.calculateClassPressureSum(cData);
	}
	
	public void calculateClassPressure(CurvatureData cData, EntropyData eData, PopulationData pData, 
										double[][] rambdaA, double[][] rambdaB){
		int i, j, k;
		int duration = cData.getDuration();
		int district = cData.getDistricNumber();
		int classNum = cData.getClassNumber();
		double pressure, classPressure;
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				pressure = 0.0;
				for(k=0 ; k<classNum ; k++){
					classPressure = this.calculateClassPressure(i,j,k,eData,pData,rambdaA[i][k],rambdaB[i][k]);
					//if(pData.getPopulation(i, j, k) == 0 && classPressure < 0) classPressure = 0.0;
					cData.setClassPressure(i, j, k, classPressure);
					//if(k > 0) pressure += classPressure * pData.getProbability(i, j, k);
					if(k > 0) pressure += classPressure;
					//System.out.println("ClassPressure: "+i+"  "+j+"  "+k+"  "+classPressure);
				}
				cData.setPressure(i, j, pressure);
				//System.out.println("Pressure: "+i+"  "+j+"  "+pressure);
			}
		}
		
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
		this.calculateClassPressureSum(cData);
	}
	
	public void calculateClassPressureSum(CurvatureData cData){
		int i, j, k;
		int duration = cData.getDuration();
		int district = cData.getDistricNumber();
		int classNum = cData.getClassNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
				
		for(i=0 ; i<duration ; i++){
			for(k=0 ; k<classNum ; k++){
				tempSum = 0.0;
				for(j=0 ; j<district ; j++) 
					if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
						tempSum += cData.getClassPressure(i, j, k);
				cData.setClassPressureSum(i, k, tempSum);
			}
		}
	}

	public void calculateClassPressure(int year, int age_class, CurvatureData cData, EntropyData eData, 
			PopulationData pData, double rambda){
		int i;
		int district = cData.getDistricNumber();
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();		
		double classPressure;
		double tempSum = 0.0;	
		
		/*** calculate pressure ***/
		for(i=0 ; i<district ; i++){
			classPressure = this.calculateClassPressure(year, i, age_class, eData, pData, rambda);
			cData.setClassPressure(year, i, age_class, classPressure);
			if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong) tempSum += classPressure;		
		}
		
		/*** calculate pressure sum ***/
		cData.setClassPressureSum(year, age_class, tempSum);	
		
		//	System.out.println("class pressure sum: "+year+"  "+age_class+"  "+cData.getClassPressureSum(year, age_class));
	}
	
	public void calculateClassPressure(int year, int age_class, CurvatureData cData, EntropyData eData, 
										PopulationData pData, double rambdaA, double rambdaB){
		int i;
		int district = cData.getDistricNumber();
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();		
		double classPressure;
		double tempSum = 0.0;	
		
		/*** calculate pressure ***/
		for(i=0 ; i<district ; i++){
			classPressure = this.calculateClassPressure(year, i, age_class, eData, pData, rambdaA, rambdaB);
			cData.setClassPressure(year, i, age_class, classPressure);
			if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong) tempSum += classPressure;		
		}
		
		/*** calculate pressure sum ***/
		cData.setClassPressureSum(year, age_class, tempSum);	
		
	//	System.out.println("class pressure sum: "+year+"  "+age_class+"  "+cData.getClassPressureSum(year, age_class));
	}
	
	public CurvatureData composeCurvatureData(EntropyData eData){
		
		int i;
		int districtNumber = eData.getDistricNumber();
		
		CurvatureData cData = new CurvatureData();
		
		cData.initiate(eData.getStartYear(), eData.getEndYear(), eData.getClassNumber(), 
						eData.getDistricNumber(), eData.getDistricClass());
		cData.setMapBoundary(eData.getMaxLatitude(), eData.getMinLatitude(), 
							 eData.getMaxLongitude(), eData.getMinLongitude());
		
		/*** set location data ***/
		for(i=0 ; i<districtNumber ; i++){
			cData.setDistrictName(i, eData.getDistrictName(i));	
			cData.setCoordinates(i, eData.getLatitude(i), eData.getLongitude(i));
		}
		
		return cData;
	}
	
	
	public CurvatureData proceedCurvaturModel(EntropyData eData, double dist_constant){
		
		CurvatureData cData;
		this.distanceConstant = dist_constant;
		
		//curvature data composing
		cData = this.composeCurvatureData(eData);		
		//curvature calculating
		this.calculateCurvature(cData, eData);		
		//pressure calculating
		this.calculatePressure(cData, eData);
		
		return cData;
	}

	public CurvatureData proceedCurvaturModel(EntropyData eData, PopulationData pData, double dist_constant){
		
		CurvatureData cData;
		this.distanceConstant = dist_constant;
		
		//curvature data composing
		cData = this.composeCurvatureData(eData);		
		//curvature calculating
		this.calculateCurvature(cData, eData, pData);		
		//pressure calculating
		this.calculatePressure(cData, eData, pData);
		
		return cData;
	}	

	public CurvatureData proceedClassCurvaturModel(EntropyData eData,PopulationData pData,double dist_constant){
		
		CurvatureData cData;
		this.distanceConstant = dist_constant;
		
		//curvature data composing
		cData = this.composeCurvatureData(eData);		
		//pressure calculating
		this.calculateClassPressure(cData, eData, pData);
		
		return cData;
	}	

	public CurvatureData proceedClassCurvaturModel(EntropyData eData,PopulationData pData, double[][] rambda){
		
		CurvatureData cData;
		
		//curvature data composing
		cData = this.composeCurvatureData(eData);		
		//pressure calculating
		this.calculateClassPressure(cData, eData, pData, rambda);
		
		return cData;
	}
	
	public CurvatureData proceedClassCurvaturModel(EntropyData eData,PopulationData pData, 
													double[][] rambdaA, double[][] rambdaB){
		
		CurvatureData cData;
		
		//curvature data composing
		cData = this.composeCurvatureData(eData);		
		//pressure calculating
		this.calculateClassPressure(cData, eData, pData, rambdaA, rambdaB);
		
		return cData;
	}
	
	public void proceedCurvaturModel(CurvatureData cData, EntropyData eData, double dist_constant){
		
		this.distanceConstant = dist_constant;
		
		//curvature calculating
		this.calculateCurvature(cData, eData);		
		//pressure calculating
		this.calculatePressure(cData, eData);
	}
	
	public void proceedCurvaturModel(CurvatureData cData, EntropyData eData, PopulationData pData, 
									 double dist_constant){
		
		this.distanceConstant = dist_constant;
		
		//curvature calculating
		this.calculateCurvature(cData, eData, pData);		
		//pressure calculating
		this.calculatePressure(cData, eData, pData);
	}	

	
	public void printCurvatureAndPressure(String outputFile, CurvatureData cData){
		
		int i, j;
		int duration = cData.getDuration();
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("max_latitude: " + cData.getMaxLatitude());
			pw.println("min_latitude: " + cData.getMinLatitude());
			pw.println("max_longitude:" + cData.getMaxLongitude());
			pw.println("min_longitude:" + cData.getMinLongitude());
			pw.println();
			/*
			pw.println("curvature:");
			pw.print("district \t latitude \t longitude \t");
			for(i=0 ; i<cData.getDuration() ; i++) pw.print((cData.getStartYear()+i) + "\t");
			pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				if(cData.getLatitude(i) > cData.getMinLatitude() 
						|| cData.getLongitude(i) > cData.getMinLongitude()){
					pw.print(cData.getDistrictName(i)+"\t");
					pw.print(cData.getLatitude(i)+"\t"+cData.getLongitude(i)+"\t");				
					for(j=0 ; j<cData.getDuration() ; j++) pw.print(cData.getCurvature(j, i)+"\t");
					pw.println();
				}
			}
			pw.print("sum\t\t\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getCurvatureSum(i)+"\t");
			pw.println();
			pw.println();
			*/
			pw.println("pressure:");
			//pw.print("district \t latitude \t longitude \t");
			pw.print("district\t");
			for(i=0 ; i<cData.getDuration() ; i++) pw.print((cData.getStartYear()+i) + "\t");
			pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				if(cData.getLatitude(i) > cData.getMinLatitude() 
						|| cData.getLongitude(i) > cData.getMinLongitude()){
					pw.print(cData.getDistrictName(i)+"\t");
			//		pw.print(cData.getLatitude(i)+"\t"+cData.getLongitude(i)+"\t");				
					for(j=0 ; j<cData.getDuration() ; j++) pw.print(cData.getPressure(j, i)+"\t");
					pw.println();
				}	
			}
			//pw.print("sum\t\t\t");
			pw.print("sum\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getPressureSum(i)+"\t");
			pw.println();			
			
			pw.close();
		}catch(IOException e) {}	

		
		System.out.println();
		System.out.print("sum   ");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f   ",cData.getPressureSum(i));
		System.out.println();
	}
	
	
	public void printCurvatureAndPressure(String outputFile, CurvatureData cData, EntropyData eData){
		
		int i, j;
		int duration = cData.getDuration();
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			/*
			pw.println("max_latitude: " + cData.getMaxLatitude());
			pw.println("min_latitude: " + cData.getMinLatitude());
			pw.println("max_longitude:" + cData.getMaxLongitude());
			pw.println("min_longitude:" + cData.getMinLongitude());
			pw.println();
			
			pw.println("curvature:");
			pw.print("district \t latitude \t longitude \t");
			for(i=0 ; i<cData.getDuration() ; i++) pw.print((cData.getStartYear()+i) + "\t");
			pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				if(cData.getLatitude(i) > cData.getMinLatitude() 
						|| cData.getLongitude(i) > cData.getMinLongitude()){
					pw.print(cData.getDistrictName(i)+"\t");
					pw.print(cData.getLatitude(i)+"\t"+cData.getLongitude(i)+"\t");				
					for(j=0 ; j<cData.getDuration() ; j++) pw.print(cData.getCurvature(j, i)+"\t");
					pw.println();
				}
			}
			pw.print("sum\t\t\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getCurvatureSum(i)+"\t");
			pw.println();
			pw.println();
			
			pw.println("pressure:");
			pw.print("district \t latitude \t longitude \t");
			for(i=0 ; i<cData.getDuration() ; i++) pw.print((cData.getStartYear()+i) + "\t");
			pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				if(cData.getLatitude(i) > cData.getMinLatitude() 
						|| cData.getLongitude(i) > cData.getMinLongitude()){
					pw.print(cData.getDistrictName(i)+"\t");
					pw.print(cData.getLatitude(i)+"\t"+cData.getLongitude(i)+"\t");				
					for(j=0 ; j<cData.getDuration() ; j++) pw.print(cData.getPressure(j, i)+"\t");
					pw.println();
				}	
			}
			pw.print("sum\t\t\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getPressureSum(i)+"\t");
			pw.println();

			*/
			System.out.println("test");
			
			//pw.println("population:");
			pw.print("district \t ");
			for(i=0 ; i<eData.getDuration() ; i++) pw.print((eData.getStartYear()+i)+"\t");
			pw.println();
			for(i=0 ; i<eData.getDistricNumber() ; i++){
				pw.print(eData.getDistrictName(i)+"\t");							
				for(j=0 ; j<eData.getDuration() ; j++) pw.print(eData.getEntropy(j, i)+"\t");
				//for(j=0 ; j<eData.getDuration() ; j++) pw.print(eData.getPopulation(j, i)+"\t");
				pw.println();
					
			}
			
			/*
			System.out.println("population:");
			System.out.print("district \t latitude \t longitude \t");			
			for(i=0 ; i<eData.getDistricNumber() ; i++){
				if(cData.getLatitude(i)>eData.getMinLatitude() || eData.getLongitude(i)>eData.getMinLongitude()){
					System.out.print(eData.getDistrictName(i)+"\t"+eData.getLatitude(i)+"\t"+eData.getLongitude(i)+"\t");
										
					for(j=0 ; j<eData.getDuration() ; j++) System.out.print(eData.getPopulation(j, i)+"\t");
					System.out.println();
				}	
			}
			*/
			/*
			System.out.println("entropy:");
			System.out.print("district \t ");			
			for(i=0 ; i<cData.getDuration() ; i++) System.out.print((cData.getStartYear()+i) + "\t");
			System.out.println();
			for(i=0 ; i<eData.getDistricNumber() ; i++){
				if(cData.getLatitude(i)>eData.getMinLatitude() || eData.getLongitude(i)>eData.getMinLongitude()){
					System.out.print(eData.getDistrictName(i)+"\t");
					for(j=0 ; j<eData.getDuration() ; j++) System.out.print(eData.getEntropy(j, i)+"\t");
					System.out.println();
				}	
			}
			*/
			
			
			pw.close();
		}catch(IOException e) {}	
	}
	
	
	public void optimizeDistanceConstant(EntropyData eData, double initialConstant){
		
		int i, j;		
		int duration = eData.getDuration();
		double[] rambda = new double[duration];
		double[] sum = new double[duration];
		
		int iter = 20;
		double xu;
		double xl;
		double x1, x2;
		double fx1, fx2;
		double goldRatio = (Math.sqrt(5)-1)/2;
		double d;
		
		//time variable
		long startTime, endTime;
		double operationTime;

		//checking start time
		startTime = System.currentTimeMillis();
		
		
		for(i=0 ; i<duration ; i++){
			//initiate
			xu = 10.0;
			xl = -10;
			d = goldRatio * (xu-xl);
			x1 = xl + d;
			x2 = xu - d;

			fx1 = Math.abs((this.proceedCurvaturModel(eData, x1)).getPressureSum(i));
			fx2 = Math.abs((this.proceedCurvaturModel(eData, x2)).getPressureSum(i));		
			
			for(j=0 ; j<iter ; j++){			
				if(fx1 < fx2){
					xl = x2;
					d = goldRatio * (xu-xl);
					x2 = x1;
					x1 = xl + d;
				}
				else if(fx2 < fx1){	
					xu = x1;
					d = goldRatio * (xu-xl);
					x1 = x2;
					x2 = xu - d;				
				}
				fx1 = Math.abs((this.proceedCurvaturModel(eData, x1)).getPressureSum(i));
				fx2 = Math.abs((this.proceedCurvaturModel(eData, x2)).getPressureSum(i));		
								
				//System.out.println(j+"  x1: "+x1+"\tfx1: "+fx1+"\tx2: "+x2+"\tfx2: "+fx2);
			}
			
			rambda[i] = x1;
			sum[i] = (this.proceedCurvaturModel(eData, x1)).getPressureSum(i);
			
			System.out.println((eData.getStartYear()+i)+"\tx1:\t"+x1+"\tfx1:\t"+fx1+"\tx2:\t"+x2+"\tfx2:\t"+fx2);			
		}		
		/*
		System.out.print("rambda\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",rambda[i]);		
		System.out.println();
		System.out.print("sum\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",sum[i]);		
		System.out.println();
		*/
		
		//checking operation time	
		endTime = System.currentTimeMillis();
		operationTime = (double)(endTime - startTime)/1000.0;
		
		System.out.println("operation time: "+operationTime);
	}
	
	public void optimizeDistanceConstant(CurvatureData cData, EntropyData eData, double initialConstant){
		
		int i, j;		
		int duration = eData.getDuration();
		double[] rambda = new double[duration];
		double[] sum = new double[duration];
		
		int iter = 20;
		double xu;
		double xl;
		double x1, x2;
		double fx1, fx2;
		double goldRatio = (Math.sqrt(5)-1)/2;
		double d;
		
		//time variable
		long startTime, endTime;
		double operationTime;

		//checking start time
		startTime = System.currentTimeMillis();
		
		
		for(i=0 ; i<duration ; i++){
			//initiate
			xu = 6.0;
			xl = 0.5;
			d = goldRatio * (xu-xl);
			x1 = xl + d;
			x2 = xu - d;

			this.proceedCurvaturModel(cData, eData, x1);
			fx1 = Math.abs(cData.getPressureSum(i));
			this.proceedCurvaturModel(cData, eData, x2);				
			fx2 = Math.abs(cData.getPressureSum(i));	

			for(j=0 ; j<iter ; j++){			
				if(fx1 < fx2){
					xl = x2;
					d = goldRatio * (xu-xl);
					x2 = x1;
					x1 = xl + d;
				}
				else if(fx2 < fx1){	
					xu = x1;
					d = goldRatio * (xu-xl);
					x1 = x2;
					x2 = xu - d;				
				}
				this.proceedCurvaturModel(cData, eData, x1);
				fx1 = Math.abs(cData.getPressureSum(i));
				this.proceedCurvaturModel(cData, eData, x2);				
				fx2 = Math.abs(cData.getPressureSum(i));		
			
				//System.out.println(j+"  x1: "+x1+"\tfx1: "+fx1+"\tx2: "+x2+"\tfx2: "+fx2);
			}
			
			rambda[i] = x1;
			sum[i] = (this.proceedCurvaturModel(eData, x1)).getPressureSum(i);
			
			System.out.println((eData.getStartYear()+i)+"\tx1:\t"+x1+"\tfx1:\t"+fx1+"\tx2:\t"+x2+"\tfx2:\t"+fx2);	
		}		
		/*
		System.out.print("rambda\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",rambda[i]);		
		System.out.println();
		System.out.print("sum\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",sum[i]);		
		System.out.println();
		*/
		
		//checking operation time	
		endTime = System.currentTimeMillis();
		operationTime = (double)(endTime - startTime)/1000.0;
		
		System.out.println("operation time: "+operationTime);
	}
	
	
	public void optimizeDistanceConstant(EntropyData eData, PopulationData pData, double initialConstant){
		
		int i, j;		
		int duration = eData.getDuration();
		double[] rambda = new double[duration];
		double[] sum = new double[duration];
		
		int iter = 10;
		double xu; 
		double xl;
		double x1, x2;
		double fx1, fx2;
		double goldRatio = (Math.sqrt(5)-1)/2;
		double d;
		
		//time variable
		long startTime, endTime;
		double operationTime;

		//checking start time
		startTime = System.currentTimeMillis();
		
		
		for(i=0 ; i<duration ; i++){
			//initiate
			xu = 20.0;
			xl = -20;
			d = goldRatio * (xu-xl);
			x1 = xl + d;
			x2 = xu - d;

			fx1 = Math.abs((this.proceedCurvaturModel(eData, pData, x1)).getPressureSum(i));
			fx2 = Math.abs((this.proceedCurvaturModel(eData, pData, x2)).getPressureSum(i));		
			
			for(j=0 ; j<iter ; j++){			
				if(fx1 < fx2){
					xl = x2;
					d = goldRatio * (xu-xl);
					x2 = x1;
					x1 = xl + d;
				}
				else if(fx2 < fx1){	
					xu = x1;
					d = goldRatio * (xu-xl);
					x1 = x2;
					x2 = xu - d;				
				}
				fx1 = Math.abs((this.proceedCurvaturModel(eData, pData, x1)).getPressureSum(i));
				fx2 = Math.abs((this.proceedCurvaturModel(eData, pData, x2)).getPressureSum(i));		
								
				//System.out.println(j+"  x1: "+x1+"\tfx1: "+fx1+"\tx2: "+x2+"\tfx2: "+fx2);
			}
			
			rambda[i] = x1;
			sum[i] = (this.proceedCurvaturModel(eData, pData, x1)).getPressureSum(i);
			
			System.out.println((eData.getStartYear()+i)+"\tx1:\t"+x1+"\tfx1:\t"+fx1+"\tx2:\t"+x2+"\tfx2:\t"+fx2);			
		}		
		/*
		System.out.print("rambda\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",rambda[i]);		
		System.out.println();
		System.out.print("sum\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",sum[i]);		
		System.out.println();
		*/
		
		//checking operation time	
		endTime = System.currentTimeMillis();
		operationTime = (double)(endTime - startTime)/1000.0;
		
		System.out.println("operation time: "+operationTime);
	}
	
	public void optimizeDistanceConstant(CurvatureData cData, EntropyData eData, PopulationData pData, 
										 double initialConstant){
		
		int i, j;		
		int duration = eData.getDuration();
		double[] rambda = new double[duration];
		double[] sum = new double[duration];
		
		int iter = 20;
		double xu;
		double xl;
		double x1, x2;
		double fx1, fx2;
		double goldRatio = (Math.sqrt(5)-1)/2;
		double d;
		
		//time variable
		long startTime, endTime;
		double operationTime;

		//checking start time
		startTime = System.currentTimeMillis();
		
		
		for(i=0 ; i<duration ; i++){
			//initiate
			xu = 6.0;
			xl = 0.5;
			d = goldRatio * (xu-xl);
			x1 = xl + d;
			x2 = xu - d;

			this.proceedCurvaturModel(cData, eData, pData, x1);
			fx1 = Math.abs(cData.getPressureSum(i));
			this.proceedCurvaturModel(cData, eData, pData, x2);				
			fx2 = Math.abs(cData.getPressureSum(i));	

			for(j=0 ; j<iter ; j++){			
				if(fx1 < fx2){
					xl = x2;
					d = goldRatio * (xu-xl);
					x2 = x1;
					x1 = xl + d;
				}
				else if(fx2 < fx1){	
					xu = x1;
					d = goldRatio * (xu-xl);
					x1 = x2;
					x2 = xu - d;				
				}
				this.proceedCurvaturModel(cData, eData, pData, x1);
				fx1 = Math.abs(cData.getPressureSum(i));
				this.proceedCurvaturModel(cData, eData,pData, x2);				
				fx2 = Math.abs(cData.getPressureSum(i));		
			
				//System.out.println(j+"  x1: "+x1+"\tfx1: "+fx1+"\tx2: "+x2+"\tfx2: "+fx2);
			}
			
			rambda[i] = x1;
			sum[i] = (this.proceedCurvaturModel(eData, pData, x1)).getPressureSum(i);
			
			System.out.println((eData.getStartYear()+i)+"\tx1:\t"+x1+"\tfx1:\t"+fx1+"\tx2:\t"+x2+"\tfx2:\t"+fx2);	
		}		
		/*
		System.out.print("rambda\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",rambda[i]);		
		System.out.println();
		System.out.print("sum\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",sum[i]);		
		System.out.println();
		*/
		
		//checking operation time	
		endTime = System.currentTimeMillis();
		operationTime = (double)(endTime - startTime)/1000.0;
		
		System.out.println("operation time: "+operationTime);
	}
	
	public void optimizeClassDistanceConstant(CurvatureData cData, EntropyData eData, PopulationData pData){
		
		int i, j, k;		
		int duration = pData.getNumberOfYears();
		int classNum = pData.getClassNumber();
		double[][] tmpRambdaA = new double[duration][classNum];
		double[][] tmpRambdaB = new double[duration][classNum];
		double[][] tmpSum = new double[duration][classNum];
		
		int iter = 1000;
		double xuA, xlA, x1A, x2A;
		double xuB, xlB, x1B, x2B;
		double fx1, fx2;
		double goldRatio = (Math.sqrt(5)-1)/2;
		double dA, dB;
		double initial_xB = 0.5;
		double xA_low = 0.0;
		double xA_high = 10.0;
		double xB_low = -1.5;
		double xB_high = 1.5;
		
		
		//time variable
		long startTime, endTime;
		double operationTime;

		//checking start time
		startTime = System.currentTimeMillis();
		
		for(i=0 ; i<duration ; i++){
			for(k=0 ; k<classNum ; k++){			
				//initiate
				xuA = xA_high;
				xlA = xA_low;
				dA = goldRatio * (xuA-xlA);
				x1A = xlA + dA;
				x2A = xuA - dA;
				x1B = initial_xB;
				x2B = initial_xB;
				
				this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
				fx1 = Math.abs(cData.getClassPressureSum(i, k));
				this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
				fx2 = Math.abs(cData.getClassPressureSum(i, k));		
								
				for(j=0 ; j<iter ; j++){	
					
					if(fx1 < fx2){
						xlA = x2A;
						dA = goldRatio * (xuA-xlA);
						x2A = x1A;
						x1A = xlA + dA;
					}
					else if(fx2 < fx1){	
						xuA = x1A;
						dA = goldRatio * (xuA-xlA);
						x1A = x2A;
						x2A = xuA - dA;				
					}
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));					
				}
				
				xuB = xB_high;
				xlB = xB_low;
				dB = goldRatio * (xuB-xlB);
				x1B = xlB + dB;
				x2B = xuB - dB;
				
				this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
				fx1 = Math.abs(cData.getClassPressureSum(i, k));
				this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
				fx2 = Math.abs(cData.getClassPressureSum(i, k));
				
				for(j=0 ; j<iter ; j++){			
					if(fx1 < fx2){
						xlB = x2B;
						dB = goldRatio * (xuB-xlB);
						x2B = x1B;
						x1B = xlB + dB;
					}
					else if(fx2 < fx1){	
						xuB = x1B;
						dB = goldRatio * (xuB-xlB);
						x1B = x2B;
						x2B = xuB - dB;				
					}
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));					
				}
				
				xuA = xA_high;
				xlA = xA_low;
				dA = goldRatio * (xuA-xlA);
				x1A = xlA + dA;
				x2A = xuA - dA;
				
				for(j=0 ; j<iter ; j++){			
					if(fx1 < fx2){
						xlA = x2A;
						dA = goldRatio * (xuA-xlA);
						x2A = x1A;
						x1A = xlA + dA;
					}
					else if(fx2 < fx1){	
						xuA = x1A;
						dA = goldRatio * (xuA-xlA);
						x1A = x2A;
						x2A = xuA - dA;				
					}
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));					
				}
				
				xuB = xB_high;
				xlB = xB_low;
				dB = goldRatio * (xuB-xlB);
				x1B = xlB + dB;
				x2B = xuB - dB;
				
				for(j=0 ; j<iter ; j++){			
					if(fx1 < fx2){
						xlB = x2B;
						dB = goldRatio * (xuB-xlB);
						x2B = x1B;
						x1B = xlB + dB;
					}
					else if(fx2 < fx1){	
						xuB = x1B;
						dB = goldRatio * (xuB-xlB);
						x1B = x2B;
						x2B = xuB - dB;				
					}
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));					
				}
				
				xuA = xA_high;
				xlA = xA_low;
				dA = goldRatio * (xuA-xlA);
				x1A = xlA + dA;
				x2A = xuA - dA;
				
				for(j=0 ; j<iter ; j++){			
					if(fx1 < fx2){
						xlA = x2A;
						dA = goldRatio * (xuA-xlA);
						x2A = x1A;
						x1A = xlA + dA;
					}
					else if(fx2 < fx1){	
						xuA = x1A;
						dA = goldRatio * (xuA-xlA);
						x1A = x2A;
						x2A = xuA - dA;				
					}
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));					
				}
				
				xuB = xB_high;
				xlB = xB_low;
				dB = goldRatio * (xuB-xlB);
				x1B = xlB + dB;
				x2B = xuB - dB;
				
				for(j=0 ; j<iter ; j++){			
					if(fx1 < fx2){
						xlB = x2B;
						dB = goldRatio * (xuB-xlB);
						x2B = x1B;
						x1B = xlB + dB;
					}
					else if(fx2 < fx1){	
						xuB = x1B;
						dB = goldRatio * (xuB-xlB);
						x1B = x2B;
						x2B = xuB - dB;				
					}
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));					
				}
				
				xuA = xA_high;
				xlA = xA_low;
				dA = goldRatio * (xuA-xlA);
				x1A = xlA + dA;
				x2A = xuA - dA;
				
				for(j=0 ; j<iter ; j++){			
					if(fx1 < fx2){
						xlA = x2A;
						dA = goldRatio * (xuA-xlA);
						x2A = x1A;
						x1A = xlA + dA;
					}
					else if(fx2 < fx1){	
						xuA = x1A;
						dA = goldRatio * (xuA-xlA);
						x1A = x2A;
						x2A = xuA - dA;				
					}
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));					
				}
				
				xuB = xB_high;
				xlB = xB_low;
				dB = goldRatio * (xuB-xlB);
				x1B = xlB + dB;
				x2B = xuB - dB;
				
				for(j=0 ; j<iter ; j++){			
					if(fx1 < fx2){
						xlB = x2B;
						dB = goldRatio * (xuB-xlB);
						x2B = x1B;
						x1B = xlB + dB;
					}
					else if(fx2 < fx1){	
						xuB = x1B;
						dB = goldRatio * (xuB-xlB);
						x1B = x2B;
						x2B = xuB - dB;				
					}
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));					
				}
				
				xuA = xA_high;
				xlA = xA_low;
				dA = goldRatio * (xuA-xlA);
				x1A = xlA + dA;
				x2A = xuA - dA;
				
				for(j=0 ; j<iter ; j++){			
					if(fx1 < fx2){
						xlA = x2A;
						dA = goldRatio * (xuA-xlA);
						x2A = x1A;
						x1A = xlA + dA;
					}
					else if(fx2 < fx1){	
						xuA = x1A;
						dA = goldRatio * (xuA-xlA);
						x1A = x2A;
						x2A = xuA - dA;				
					}
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));					
				}
				
				xuB = xB_high;
				xlB = xB_low;
				dB = goldRatio * (xuB-xlB);
				x1B = xlB + dB;
				x2B = xuB - dB;
				
				for(j=0 ; j<iter ; j++){			
					if(fx1 < fx2){
						xlB = x2B;
						dB = goldRatio * (xuB-xlB);
						x2B = x1B;
						x1B = xlB + dB;
					}
					else if(fx2 < fx1){	
						xuB = x1B;
						dB = goldRatio * (xuB-xlB);
						x1B = x2B;
						x2B = xuB - dB;				
					}
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));					
				}
				
				tmpRambdaA[i][k] = x1A;
				tmpRambdaB[i][k] = x2A;
				tmpSum[i][k] = fx1;				
			
				cData.setClassRambda(i, k, x1A);
				cData.setClassRambdaB(i, k, x1B);
				cData.setClassPressureSum(i, k, fx1);
				
				System.out.print((eData.getStartYear()+i)+"\t"+k+"\tx1A:\t"+x1A+"\tx1B:\t"+x1B+"\tfx1:\t"+fx1
																  +"\tx2A:\t"+x2A+"\tx2B:\t"+x2B+"\tfx2:\t"+fx2);

				//checking operation time	
				endTime = System.currentTimeMillis();
				operationTime = (double)(endTime - startTime)/1000.0;
			
				System.out.println("\t"+(int)((duration*classNum-i*classNum-(k+1))*operationTime/(i*classNum+(k+1)))
										+" seconds left");	
			}					
		}			
		
		/*
		System.out.print("rambda\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",rambda[i]);		
		System.out.println();
		System.out.print("sum\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",sum[i]);		
		System.out.println();
		*/
		
		//checking operation time	
		endTime = System.currentTimeMillis();
		operationTime = (double)(endTime - startTime)/1000.0;
		
		System.out.println("operation time: "+operationTime);
	}
	
	
	public void optimizeClassDistanceConstant(CurvatureData cData, EntropyData eData, PopulationData pData,
												int iterA, int iterB, int iterAB){
		
		int i, j, k;		
		int iter;
		int duration = pData.getNumberOfYears();
		int classNum = pData.getClassNumber();
		double[][] tmpRambdaA = new double[duration][classNum];
		double[][] tmpRambdaB = new double[duration][classNum];
		double[][] tmpSum = new double[duration][classNum];
		
		double xuA, xlA, x1A, x2A;
		double xuB, xlB, x1B, x2B;
		double fx1, fx2;
		double goldRatio = (Math.sqrt(5)-1)/2;
		double dA, dB;
		double initial_xB = 0.5;
		double xA_low = 0.0;
		double xA_high = 10.0;
		double xB_low = -1.5;
		double xB_high = 1.5;
		
		
		//time variable
		long startTime, endTime;
		double operationTime;

		//checking start time
		startTime = System.currentTimeMillis();
		
		for(i=0 ; i<duration ; i++){
			for(k=0 ; k<classNum ; k++){			
				//initiate
				iter = 0;
				xuA = xA_high;
				xlA = xA_low;
				dA = goldRatio * (xuA-xlA);
				x1A = xlA + dA;
				x2A = xuA - dA;
				x1B = initial_xB;
				x2B = initial_xB;
				
				this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
				fx1 = Math.abs(cData.getClassPressureSum(i, k));
				this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
				fx2 = Math.abs(cData.getClassPressureSum(i, k));		
								
				for(j=0 ; j<iterA ; j++){	
					
					if(fx1 < fx2){
						xlA = x2A;
						dA = goldRatio * (xuA-xlA);
						x2A = x1A;
						x1A = xlA + dA;
					}
					else if(fx2 < fx1){	
						xuA = x1A;
						dA = goldRatio * (xuA-xlA);
						x1A = x2A;
						x2A = xuA - dA;				
					}
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));					
				}
				
				while(iter <iterAB){
								
					xuB = xB_high;
					xlB = xB_low;
					dB = goldRatio * (xuB-xlB);
					x1B = xlB + dB;
					x2B = xuB - dB;
					
					this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
					fx1 = Math.abs(cData.getClassPressureSum(i, k));
					this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
					fx2 = Math.abs(cData.getClassPressureSum(i, k));
					
					for(j=0 ; j<iterB ; j++){			
						if(fx1 < fx2){
							xlB = x2B;
							dB = goldRatio * (xuB-xlB);
							x2B = x1B;
							x1B = xlB + dB;
						}
						else if(fx2 < fx1){	
							xuB = x1B;
							dB = goldRatio * (xuB-xlB);
							x1B = x2B;
							x2B = xuB - dB;				
						}
						this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
						fx1 = Math.abs(cData.getClassPressureSum(i, k));
						this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
						fx2 = Math.abs(cData.getClassPressureSum(i, k));					
					}
					
					xuA = xA_high;
					xlA = xA_low;
					dA = goldRatio * (xuA-xlA);
					x1A = xlA + dA;
					x2A = xuA - dA;
					
					for(j=0 ; j<iterA ; j++){			
						if(fx1 < fx2){
							xlA = x2A;
							dA = goldRatio * (xuA-xlA);
							x2A = x1A;
							x1A = xlA + dA;
						}
						else if(fx2 < fx1){	
							xuA = x1A;
							dA = goldRatio * (xuA-xlA);
							x1A = x2A;
							x2A = xuA - dA;				
						}
						this.calculateClassPressure(i, k, cData, eData, pData, x1A, x1B);
						fx1 = Math.abs(cData.getClassPressureSum(i, k));
						this.calculateClassPressure(i, k, cData, eData, pData, x2A, x2B);
						fx2 = Math.abs(cData.getClassPressureSum(i, k));					
					}
					
					iter++;
				}
				
				tmpRambdaA[i][k] = x1A;
				tmpRambdaB[i][k] = x2A;
				tmpSum[i][k] = fx1;				
			
				cData.setClassRambda(i, k, x1A);
				cData.setClassRambdaB(i, k, x1B);
				cData.setClassPressureSum(i, k, fx1);
				
				System.out.print((eData.getStartYear()+i)+"\t"+k+"\tx1A:\t"+x1A+"\tx1B:\t"+x1B+"\tfx1:\t"+fx1
																  +"\tx2A:\t"+x2A+"\tx2B:\t"+x2B+"\tfx2:\t"+fx2);

				//checking operation time	
				endTime = System.currentTimeMillis();
				operationTime = (double)(endTime - startTime)/1000.0;
			
				System.out.println("\t"+(int)((duration*classNum-i*classNum-(k+1))*operationTime/(i*classNum+(k+1)))
										+" seconds left");	
			}					
		}			
		
		/*
		System.out.print("rambda\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",rambda[i]);		
		System.out.println();
		System.out.print("sum\t");
		for(i=0 ; i<duration ; i++)	System.out.printf("%4.4f   ",sum[i]);		
		System.out.println();
		*/
		
		//checking operation time	
		endTime = System.currentTimeMillis();
		operationTime = (double)(endTime - startTime)/1000.0;
		
		System.out.println("operation time: "+operationTime);
	}
	
	public void readClassRambdaAB(String inputFile, CurvatureData cData){
		int i;
		int year;
		int ageClass;
		
		try{
			File file = new File(inputFile);			
			Scanner scan = new Scanner(file);		
						
			while(scan.hasNext()){
				
				year = scan.nextInt() - cData.getStartYear();
				ageClass = scan.nextInt();
				
				scan.next();
				cData.setClassRambda(year, ageClass, scan.nextDouble());
				scan.next();
				cData.setClassRambdaB(year, ageClass, scan.nextDouble());			
			}
			
			for(i=0 ; i<cData.getDuration() ; i++){
				cData.setLambdaA(i, cData.getClassRambda(i, 0));
				cData.setLambdaB(i, cData.getClassRambdaB(i, 0));
			}
			
			scan.close();			
		} catch(IOException e) {
			System.err.print("read class rambda error\t");
		}
	}
	
	public void printClassRambdaAB(String outputFile, CurvatureData cData){
		int i, j;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			for(i=0 ; i<cData.getDuration() ; i++){
				for(j=0 ; j<cData.getClassNumber() ; j++){
					pw.print((cData.getStartYear()+i)+"\t"+j+"\tx1A\t");
					pw.println(cData.getClassRambda(i, j)+"\tx1B\t"+cData.getClassRambdaB(i, j)+"\t");
				}
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public static void main(String[] args) {
		
		int iterA	= 100;
		int iterB	= 100;
		int iterAB	= 10;
		
		String filePath = "/Users/jml/Desktop/Research/data_storage/population/";
		String populatoinFile	= filePath + "population_region.txt";
		String coordinatesFile	= filePath + "coordinates_region.txt";
		String curvatureFile	= filePath + "Curvature_region.txt";
		String rambdaFile		= filePath + "weighted_entropy/RambdaAB_region_"+iterA+"_"+iterAB+".txt";
		String pressureFile		= filePath + "weighted_entropy/Pressure_RambdaAB_region_"+iterA+"_"+iterAB+".txt";
		//String curvatureFile = filePath +  "population_test_region.txt";
		
		
		int pop_type = 1;
		int dist_type = 0;
		double pop_constant = 1.0;
		double dist_constant;
		//dist_constant = 1;
		//dist_constant = Math.pow(10, 0.575);
		dist_constant = Math.pow(10, 3.02964621417009);
		
		PopulationData pData = new PopulationData();
		Coordinates points = new Coordinates();
		EntropyData	eData = new EntropyData();
		EntropyData	norm_eData = new EntropyData();
		CurvatureData cData;
		
		EntropyCalculator ec = new EntropyCalculator();
		DataReader dr = new DataReader();
		MapGenerator mg = new MapGenerator();
		CurvatureModel cm = new CurvatureModel(pop_type, pop_constant, dist_type, dist_constant);		
		
		System.out.print("data reading: ");
		dr.readData(populatoinFile, pData);
		System.out.println("complete");
		
		System.out.print("probability calculating: ");
		ec.calculateProbability(pData);
		System.out.println("complete");
		
		System.out.print("entropy calculating: ");
		ec.calculateEntropy(pData);
		ec.calculateClassEntropy(pData);
		//ec.calculateAgeWeightedEntropy(pData);
		System.out.println("complete");
				
		System.out.print("coordinates reading: ");
		mg.readCoordinates(coordinatesFile, points);
		System.out.println("complete");
				
		System.out.print("entropy composing: ");
		//eData = mg.composeEntropyData(points, pData);
		eData = mg.composeClassEntropyData(points, pData);
		System.out.println("complete");
		
		System.out.print("entropy normalizing: ");
		//norm_eData = mg.normalizeEntropyData(eData);
		norm_eData = mg.normalizeClassEntropy(eData);
		System.out.println("complete");

		
		System.out.print("curvature data composing: ");
		cData = cm.composeCurvatureData(norm_eData);
		System.out.println("complete");
		
		/*
		System.out.print("curvature model processing: ");
		cData = cm.proceedCurvaturModel(norm_eData, dist_constant);
		//cData = cm.proceedCurvaturModel(norm_eData, pData, dist_constant);
		System.out.println("complete");		
		
		System.out.print("class curvature model processing: ");
		cm.calculateClassPressure(cData, norm_eData, pData);
		System.out.println("complete");	
		*/
		
		/*
		System.out.print("curvature data printing: ");
		cm.printCurvatureAndPressure(curvatureFile, cData);
		//cm.printCurvatureAndPressure(curvatureFile, cData, eData);
		System.out.println("complete");
		*/
		
				
		System.out.print("pressure optimizing: ");
		//cm.optimizeDistanceConstant(norm_eData, pData, dist_constant);
		cm.optimizeClassDistanceConstant(cData, norm_eData, pData, iterA, iterB, iterAB);
		System.out.println("complete");
		
		System.out.print("class rambda printing: ");
		cm.printClassRambdaAB(rambdaFile, cData);
		System.out.println("complete");				
		
		System.out.print("class rambda reading: ");
		cm.readClassRambdaAB(rambdaFile, cData);
		System.out.println("complete");		
		
		System.out.print("class curvature model processing: ");
		cm.calculateClassPressure(cData, norm_eData, pData, cData.classLamdaA, cData.classLamdaB);
		System.out.println("complete");	
		
		System.out.print("class pressure data printing: ");
		cm.printCurvatureAndPressure(pressureFile, cData);
		System.out.println("complete");
		
		
		System.out.println();
		System.out.println("[process complete]");
		
		
		/*** modified function ***/
		/*** 
		 * populationFunction(int year, int district, int age_class, PopulationData pData)
		 * 						- normalized by max total population
		 * calculateClassCurvature(int year, int district_i, int district_j, int age_class, 
		 *  						EntropyData eData, double rambdaA, double rambdaB)
		 *  					- use total entropy
		 *  populationFunction(int year, int district, int age_class, PopulationData pData)
		 *  					- set low limit of emigration
		 ***/
	}

}
