package industryEntropy;

import industryEntropy.data.IndustryData;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import populationEntropy.*;
import populationEntropy.data.*;

public class IndustryCurvatureModel_AllAgeCurve {

	double rambdaA, rambdaB;
	
	public IndustryCurvatureModel_AllAgeCurve(){
		
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
		return population;
	}

	public double distanceFuction(int year, int district_i, int district_j, EntropyData eData, double rambda){
		/*** Note: distance should be normalized ***/		
		
		double distance = Math.sqrt(
				 (Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
			      + Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
	
		return Math.pow(1.0 / (1.0 + Math.pow(10, rambda* (0.5 -(1.0 * distance)))), 3);
	}

	public double distanceFuction(int year, int district_i, int district_j, EntropyData eData, 
								  double rambdaA, double rambdaB){
		/*** Note: distance should be normalized ***/
		
		double distance = Math.sqrt(
				 (Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
			      + Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
	
		return Math.pow(1.0 / (1.0 + Math.pow(10, rambdaA* (rambdaB -(1.0 * distance)))), 3);
	}
	
	
	public double calculateCurvature(int year, int district_i, int district_j, EntropyData eData, double rambdaA){
				
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
				
		if (entropy_i == 0 || entropy_j ==0) return 0.0;
		else if(district_i == district_j) return 0.0;
		else return (entropy_i-entropy_j) 
						/ this.distanceFuction(year, district_i, district_j, eData, rambdaA);
	}

	public double calculateCurvature(int year, int district_i, int district_j, EntropyData eData, 
																		double rambdaA, double rambdaB){
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
		double curvature;
		
		/*** calculate curvature ***/
		if (entropy_i == 0 || entropy_j ==0) curvature = 0.0;
		else if(district_i == district_j) curvature = 0.0;
		else curvature = (entropy_i-entropy_j)
		/ this.distanceFuction(year,district_i,district_j,eData,rambdaA,rambdaB);		
		
		/***return curvature ***/
		return curvature;
	}
	
	/*
	public double calculatePressure(int year, int district_i, int district_j, EntropyData eData){
		
		return this.calculateCurvature(year, district_i, district_j, eData)
				* this.populationFunction(year, district_i, eData)
				* this.populationFunction( year, district_j, eData);
	}
		
	public double calculatePressure(int year, int district_i, int district_j, EntropyData eData, 
									PopulationData pData){
		
		return this.calculateCurvature(year, district_i, district_j, eData)
				* this.populationFunction(year, district_i, pData)
				* this.populationFunction(year, district_j, pData);
	}
	*/	
	public double calculateCurvature(int year, int district, EntropyData eData, double rambdaA){
		
		int i;
		double curvature = 0.0;
						
		for(i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district) 
				curvature += (this.calculateCurvature(year, district, i, eData, rambdaA) 
				  	 	     *this.populationFunction(year, i, eData));
				
		return curvature;		
	}

	public double calculateCurvature(int year, int district, EntropyData eData, double rambdaA, double rambdaB){
		
		int i;
		double curvature = 0.0;
						
		for(i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district) 
				curvature += (this.calculateCurvature(year, district, i, eData, rambdaA, rambdaB) 
				  	 	     *this.populationFunction(year, i, eData));
				
		return curvature;		
	}
	
	public double calculatePressure(int year, int district, EntropyData eData, double rambdaA){
	
		return this.calculateCurvature(year, district, eData, rambdaA)
				* this.populationFunction(year, district, eData);		
	}
	
	public double calculatePressure(int year, int district, EntropyData eData, double rambdaA, double rambdaB){
		
		return this.calculateCurvature(year, district, eData, rambdaA, rambdaB)
				* this.populationFunction(year, district, eData);		
	}
	
	public void calculateCurvature(int year, CurvatureData cData, EntropyData eData, double rambdaA){
		
		int i;
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<district ; i++)
			cData.setCurvature(year, i, this.calculateCurvature(year, i, eData, rambdaA));
		
		/*** calculate curvature sum ***/
		tempSum = 0.0;
		for(i=0 ; i<district ; i++) 
			if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong)
				tempSum += cData.getCurvature(year, i);
		cData.setCurvatureSum(year, tempSum);		
	}
	
	public void calculateCurvature(int year,CurvatureData cData,EntropyData eData,double rambdaA,double rambdaB){
		
		int i;
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<district ; i++)
			cData.setCurvature(year, i, this.calculateCurvature(year, i, eData, rambdaA, rambdaB));
		
		/*** calculate curvature sum ***/
		tempSum = 0.0;
		for(i=0 ; i<district ; i++) 
			if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong)
				tempSum += cData.getCurvature(year, i);
		cData.setCurvatureSum(year, tempSum);
		
	}
	
	public void calculateCurvature(CurvatureData cData, EntropyData eData, double[] rambdaA){
		
		int i, j;
		int years = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<years ; i++)
			for(j=0 ; j<district ; j++)
				cData.setCurvature(i, j, this.calculateCurvature(i, j, eData, rambdaA[i]));
		
		/*** calculate curvature sum ***/
		for(i=0 ; i<years ; i++){
			tempSum = 0.0;
			for(j=0 ; j<district ; j++) 
				if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
					tempSum += cData.getCurvature(i, j);
			cData.setCurvatureSum(i, tempSum);
		}
	}
	
	public void calculateCurvature(CurvatureData cData, EntropyData eData, double[] rambdaA, double[] rambdaB){
		
		int i, j;
		int years = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<years ; i++)
			for(j=0 ; j<district ; j++)
				cData.setCurvature(i, j, this.calculateCurvature(i, j, eData, rambdaA[i], rambdaB[i]));
		
		/*** calculate curvature sum ***/
		for(i=0 ; i<years ; i++){
			tempSum = 0.0;
			for(j=0 ; j<district ; j++) 
				if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
					tempSum += cData.getCurvature(i, j);
			cData.setCurvatureSum(i, tempSum);
		}
	}

	public void calculatePressure(int year, CurvatureData cData, EntropyData eData, double rambdaA){

		int j;
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(j=0 ; j<district ; j++){
			pressure = this.calculateCurvature(year,j,eData,rambdaA)*this.populationFunction(year,j,eData);
			cData.setPressure(year, j, pressure);
		}
			
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
	}

	public void calculatePressure(int year,CurvatureData cData,EntropyData eData,double rambdaA,double rambdaB){

		int j;
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(j=0 ; j<district ; j++){
			pressure =this.calculateCurvature(year,j,eData,rambdaA,rambdaB)*this.populationFunction(year,j,eData);
			cData.setPressure(year, j, pressure);
		}
				
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
	}
	
	public void calculatePressure(CurvatureData cData, EntropyData eData, double[] rambdaA){

		int i, j;
		int years = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<district ; j++){
				pressure = this.calculateCurvature(i,j,eData,rambdaA[i])*this.populationFunction(i, j, eData);
				cData.setPressure(i, j, pressure);
			}
		}
		
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
	}

	public void calculatePressure(CurvatureData cData, EntropyData eData, double[] rambdaA, double[] rambdaB){

		int i, j;
		int years = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<district ; j++){
				pressure = this.calculateCurvature(i, j, eData, rambdaA[i], rambdaB[i]) 
							* this.populationFunction(i, j, eData);
				cData.setPressure(i, j, pressure);
			}
		}
		
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
	}
	
	public void calculatePressureSum(CurvatureData cData){		
		int i, j;
		int years = cData.getNumberOfYears();
		int district = cData.getDistricNumber();		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		double tempSum;
		
		for(i=0 ; i<years ; i++){
			tempSum = 0.0;
			for(j=0 ; j<district ; j++) 
				if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
					tempSum += cData.getPressure(i, j);
			cData.setPressureSum(i, tempSum);
		}
	}
	
	public void calculateInteraction(EntropyData eData, CurvatureData cData, double[] rambdaA, double[] rambdaB){
		
		int i,j;
		int year;
		int years = cData.getNumberOfYears();
		int district = cData.getDistricNumber();		
		double entropy_i, entropy_j; 
		double curvature, interaction;
		double max;
		
		/*** calculate interaction ***/
		for(year=0 ; year<years ; year++){
			for(i=0 ; i<district ; i++){
				entropy_i = eData.getEntropy(year, i); 
				for(j=0 ; j<district ; j++){
					entropy_j = eData.getEntropy(year, j);
					
					if (entropy_i == 0 || entropy_j ==0) curvature = 0.0;
					else if(i == j) curvature = 0.0;
					else curvature = (entropy_i-entropy_j)
										/this.distanceFuction(year, i, j, eData, rambdaA[year], rambdaB[year]);	
					
					interaction = curvature * this.populationFunction(year, i, eData) 
											* this.populationFunction(year, j, eData);
							
					cData.setInteraction(year, i, j, interaction);
				}			
			}		
		}	

		/*** normalize interaction ***/
		for(year=0 ; year<years ; year++){
			max = 0;
			for(i=0 ; i<district ; i++){		
				for(j=0 ; j<district ; j++){
					interaction = cData.getInteraction(year, i, j);
					if(max < Math.abs(interaction))	max = interaction; 
				}
			}

			for(i=0 ; i<district ; i++){		
				for(j=0 ; j<district ; j++){
					interaction = cData.getInteraction(year, i, j);
					cData.setInteraction(year, i, j, interaction/max);
				}
			}
		}
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
	
	
	public CurvatureData proceedCurvaturModel(EntropyData eData, double[] rambdaA){
		
		CurvatureData cData;
		
		//curvature data composing
		cData = this.composeCurvatureData(eData);		
		//curvature calculating
		this.calculateCurvature(cData, eData, rambdaA);		
		//pressure calculating
		this.calculatePressure(cData, eData, rambdaA);
		
		return cData;
	}

	public CurvatureData proceedCurvaturModel(EntropyData eData, double[] rambdaA, double[] rambdaB){
		
		CurvatureData cData;
		
		//curvature data composing
		cData = this.composeCurvatureData(eData);		
		//curvature calculating
		this.calculateCurvature(cData, eData, rambdaA, rambdaB);		
		//pressure calculating
		this.calculatePressure(cData, eData, rambdaA, rambdaB);
		
		return cData;
	}
	
	public void proceedCurvaturModel(CurvatureData cData, EntropyData eData, double[] rambdaA){
				
		//curvature calculating
		this.calculateCurvature(cData, eData, rambdaA);		
		//pressure calculating
		this.calculatePressure(cData, eData, rambdaA);
	}

	public void proceedCurvaturModel(CurvatureData cData, EntropyData eData, double[] rambdaA, double[] rambdaB){
		
		//curvature calculating
		this.calculateCurvature(cData, eData, rambdaA, rambdaB);		
		//pressure calculating
		this.calculatePressure(cData, eData, rambdaA, rambdaB);
	}		
	
	public void printCurvatureAndPressure(String outputFile, CurvatureData cData){
		
		int i, j;
		int years = cData.getNumberOfYears();
		
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
			for(i=0 ; i<cData.getNumberOfYears() ; i++) pw.print((cData.getStartYear()+i) + "\t");
			pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				if(cData.getLatitude(i) > cData.getMinLatitude() 
						|| cData.getLongitude(i) > cData.getMinLongitude()){
					pw.print(cData.getDistrictName(i)+"\t");
			//		pw.print(cData.getLatitude(i)+"\t"+cData.getLongitude(i)+"\t");				
					for(j=0 ; j<cData.getNumberOfYears() ; j++) pw.print(cData.getPressure(j, i)+"\t");
					pw.println();
				}	
			}
			//pw.print("sum\t\t\t");
			pw.print("sum\t");
			for(i=0 ; i<years ; i++) pw.print(cData.getPressureSum(i)+"\t");
			pw.println();			
			
			pw.close();
		}catch(IOException e) {}	

		
		System.out.println();
		System.out.print("sum   ");
		for(i=0 ; i<years ; i++) System.out.printf("%4.6f   ",cData.getPressureSum(i));
		System.out.println();
	}
	
	
	public void printCurvatureAndPressure(String outputFile, CurvatureData cData, EntropyData eData){
		
		int i, j;
		int years = cData.getNumberOfYears();
		
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
			for(i=0 ; i<eData.getNumberOfYears() ; i++) pw.print((eData.getStartYear()+i)+"\t");
			pw.println();
			for(i=0 ; i<eData.getDistricNumber() ; i++){
				pw.print(eData.getDistrictName(i)+"\t");							
				for(j=0 ; j<eData.getNumberOfYears() ; j++) pw.print(eData.getEntropy(j, i)+"\t");
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
	
	
	public void optimizeDistanceConstant(CurvatureData cData, EntropyData eData, int iter){
		
		int i, j;		
		int years = eData.getNumberOfYears();
		double[] rambda = new double[years];
		double[] sum = new double[years];
		
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
		
		
		for(i=0 ; i<years ; i++){
			//initiate
			xu = 6.0;
			xl = 0.5;
			d = goldRatio * (xu-xl);
			x1 = xl + d;
			x2 = xu - d;

			this.calculatePressure(i, cData, eData, x1);
			fx1 = Math.abs(cData.getPressureSum(i));
			this.calculatePressure(i, cData, eData, x2);
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
				this.calculatePressure(i, cData, eData, x1);
				fx1 = Math.abs(cData.getPressureSum(i));
				this.calculatePressure(i, cData, eData, x2);
				fx2 = Math.abs(cData.getPressureSum(i));		
			
				//System.out.println(j+"  x1: "+x1+"\tfx1: "+fx1+"\tx2: "+x2+"\tfx2: "+fx2);
			}
			
			rambda[i] = x1;
			sum[i] = fx1;
			
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
		
	
	public void optimizeDistanceConstant(CurvatureData cData, EntropyData eData, int iterA, int iterAB){
		
		int i, j;		
		int iter;
		int iterB = iterA;
		int years = eData.getNumberOfYears();
		double[] tmpRambdaA = new double[years];
		double[] tmpRambdaB = new double[years];
		double[] tmpSum = new double[years];
		
		double xuA, xlA, x1A, x2A;
		double xuB, xlB, x1B, x2B;
		double fx1, fx2;
		double goldRatio = (Math.sqrt(5)-1)/2;
		double dA, dB;
		double initial_xB = 0.5;
		double xA_low = 0.0;
		double xA_high = 10.0;
		double xB_low = -0.5;
		double xB_high = 1.5;
		
		
		//time variable
		long startTime, endTime;
		double operationTime;

		//checking start time
		startTime = System.currentTimeMillis();
		
		for(i=0 ; i<years ; i++){
			//initiate
			xuA = xA_high;
			xlA = xA_low;
			dA = goldRatio * (xuA-xlA);
			x1A = xlA + dA;
			x2A = xuA - dA;
			x1B = initial_xB;
			x2B = initial_xB;
			
			iter = 0;
			
			this.calculatePressure(i, cData, eData, x1A, x1B);
			fx1 = Math.abs(cData.getPressureSum(i));
			this.calculatePressure(i, cData, eData, x2A, x2B);
			fx2 = Math.abs(cData.getPressureSum(i));		
							
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
				this.calculatePressure(i, cData, eData, x1A, x1B);
				fx1 = Math.abs(cData.getPressureSum(i));
				this.calculatePressure(i, cData, eData, x2A, x2B);
				fx2 = Math.abs(cData.getPressureSum(i));					
			}
			
			while(iter < iterAB){
				xuB = xB_high;
				xlB = xB_low;
				dB = goldRatio * (xuB-xlB);
				x1B = xlB + dB;
				x2B = xuB - dB;
				
				this.calculatePressure(i, cData, eData, x1A, x1B);
				fx1 = Math.abs(cData.getPressureSum(i));
				this.calculatePressure(i, cData, eData, x2A, x2B);
				fx2 = Math.abs(cData.getPressureSum(i));
				
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
					this.calculatePressure(i, cData, eData, x1A, x1B);
					fx1 = Math.abs(cData.getPressureSum(i));
					this.calculatePressure(i, cData, eData, x2A, x2B);
					fx2 = Math.abs(cData.getPressureSum(i));					
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
					this.calculatePressure(i, cData, eData, x1A, x1B);
					fx1 = Math.abs(cData.getPressureSum(i));
					this.calculatePressure(i, cData, eData, x2A, x2B);
					fx2 = Math.abs(cData.getPressureSum(i));					
				}
				
				iter++;
			}
			
			tmpRambdaA[i] = x1A;
			tmpRambdaB[i] = x2A;
			tmpSum[i]= fx1;				
		
			cData.setLambdaA(i, x1A);
			cData.setLambdaB(i, x1B);
			cData.setPressureSum(i, fx1);
			
			System.out.print((eData.getStartYear()+i)+"\tx1A:\t"+x1A+"\tx1B:\t"+x1B+"\tfx1:\t"+fx1
															  +"\tx2A:\t"+x2A+"\tx2B:\t"+x2B+"\tfx2:\t"+fx2);
				//checking operation time	
			endTime = System.currentTimeMillis();
			operationTime = (double)(endTime - startTime)/1000.0;
		
			System.out.println("\t"+(int)(((years-(i+1)))*operationTime/(i+1))	+" seconds left");	
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
	
	public void readRambdaAB(String inputFile, CurvatureData cData){
		int i;
		int year;
		
		try{
			File file = new File(inputFile);			
			Scanner scan = new Scanner(file);		
						
			while(scan.hasNext()){
				
				year = scan.nextInt() - cData.getStartYear();
				
				scan.next();
				cData.setLambdaA(year, scan.nextDouble());
				scan.next();
				cData.setLambdaB(year, scan.nextDouble());		
			}
			
			scan.close();			
		} catch(IOException e) {
			System.err.print("read class rambda error\t");
		}
	}
	
	public void printRambdaAB(String outputFile, CurvatureData cData){
		int i;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			for(i=0 ; i<cData.getNumberOfYears() ; i++){
				pw.print((cData.getStartYear()+i)+"\trambdaA\t");
				pw.println(cData.getLamdaA(i)+"\trambdaB\t"+cData.getLamdaB(i));			
			}
			
			pw.close();
		}catch(IOException e) {}
	}

	public void printInteraction(String outputFile, CurvatureData cData){
		
		int i, j;
		int year;
		int years = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double minLat = cData.getMinLatitude();
		double maxLat = cData.getMaxLatitude();
		double minLong = cData.getMinLongitude();
		double maxLong = cData.getMaxLongitude();
		
		
		for(year=0 ; year<years ; year++){			
		
			try{
				File file = new File(outputFile.replace(".txt", "_"+(year+cData.getStartYear())+".txt"));
				PrintWriter pw = new PrintWriter(file);
				
				pw.println("interaction:");
				
				pw.print("district\t");
				for(i=0 ; i<cData.getDistricNumber() ; i++){
					if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong){
						pw.print(cData.getDistrictName(i)+"\t");
					}
				}				
				pw.println();
				
				for(i=0 ; i<district ; i++){
					if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong){
						pw.print(cData.getDistrictName(i)+"\t");

						for(j=0 ; j<district ; j++){
							if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong){
								pw.print(cData.getInteraction(year, i, j)+"\t");
							}
						}								
						pw.println();
					}	
				}			
				pw.close();
			}catch(IOException e) {}
			
			/*** test ***/
			/*
			double tempSum = 0;
			for(i=0 ; i<district ; i++){
				for(j=0 ; j<district ; j++){
					if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong){
						tempSum += cData.getInteraction(year, i, j); 
					}
				}
			}
			System.out.println(year+" interaction sum = "+tempSum);
			*/
		}
	}	
	
	public void printIndustryEntropy(String outputFile, EntropyData eData){
		
		int i, j;
		int years = eData.getNumberOfYears();
		int district = eData.getDistricNumber();
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("entropy:");
			
			pw.print("district\t");
			for(i=0 ; i<years ; i++) pw.print((i+eData.getStartYear())+"\t");				
			pw.println();
			
			for(i=0 ; i<district ; i++){
				pw.print(eData.getDistrictName(i)+"\t");
				for(j=0 ; j<years ; j++){
					pw.print(eData.getEntropy(j, i)+"\t");			
				}	
				pw.println();
			}			
			pw.close();
		}catch(IOException e) {}
		
	}	
	
	public static void main(String[] args) {
	
		int start = 2010;
		int end = 2010;
		int iter	= 100;
		int iterAB	= 10;
		int classNumber;		
		int[] classList = {20, 50};
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/level_industry_all_age_curve/test/";
		String populatoinFile	= filePath + "data/population ID registered mid-year_2010.txt";
		String coordinatesFile	= filePath + "data/coordinates_region.txt";
		String latLngCoordFile	= filePath + "data/coordinates_region_LatLng.txt";
		String lamdaFile;
		String pressureFile;
		String interactionFile;
		String graphFile;
		String entropyFile;
		
		for(int i=0 ; i<4 ; i++){
		
		classNumber = classList[i];	
			
		lamdaFile		= filePath + "LamdaAB_region_"+classNumber+"class_"+iter+"_"+iterAB+".txt";
		pressureFile	= filePath + "Pressure_LamdaAB_region_"+classNumber+"class_"+iter+"_"+iterAB+".txt";
		interactionFile	= filePath + "interaction/Interaction_region_"+classNumber+"_class_"+iter+"_"+iterAB+".txt";
		graphFile		= filePath + "interaction/GraphGDF_"+classNumber+"class_"+iter+"_"+iterAB+".txt";
		entropyFile		= filePath + "entropy_region_"+classNumber+"class"+".txt";
		
		PopulationData pData = new PopulationData();
		Coordinates points = new Coordinates();
		EntropyData	eData = new EntropyData();
		EntropyData	norm_eData = new EntropyData();
		CurvatureData cData;
		
		EntropyCalculator ec = new EntropyCalculator();
		DataReader dr = new DataReader();
		MapGenerator mg = new MapGenerator();
		IndustryCurvatureModel_AllAgeCurve icm = new IndustryCurvatureModel_AllAgeCurve();		
		GraphFormat gf = new GraphFormat();
		
		//IndustryEntropyCalculator iec = new IndustryEntropyCalculator();
		LevelEntropyCalculator lec = new LevelEntropyCalculator();
		IndustryData iData = new IndustryData();
		
		System.out.print("data reading: ");
		pData = dr.getPopulationData(populatoinFile);
		System.out.println("complete");
		
		System.out.print("probability calculating: ");
		ec.calculateProbability(pData);
		System.out.println("complete");
		
		System.out.print("entropy calculating: ");
		ec.calculateEntropy(pData);
		System.out.println("complete");
				
		System.out.print("coordinates reading: ");
		mg.readCoordinates(coordinatesFile, points);
		System.out.println("complete");
				
		System.out.print("entropy composing: ");
		eData = mg.composeEntropyData(points, pData);
		System.out.println("complete");
		
		System.out.print("entropy transforming: ");
		//iec.processTransfer(start, end, eData);
		//lec.processEntropyTransfer(start, end, classNumber, iData, eData);	
		//lec.processAverageTransfer(start, end, classNumber, iData, eData);	
		lec.processAttractionTransfer(start, end, classNumber, iData, eData);
		icm.printIndustryEntropy(entropyFile, eData);
		System.out.println("complete");		
		
		System.out.print("entropy normalizing: ");
		norm_eData = mg.normalizeEntropyData(eData);
		//norm_eData = eData;
		System.out.println("complete");
		
		System.out.print("curvature data composing: ");
		cData = icm.composeCurvatureData(norm_eData);
		System.out.println("complete");
		
		
		/*** friction function optimizing part ***/
				
		System.out.print("pressure optimizing: ");
		icm.optimizeDistanceConstant(cData, norm_eData, iter, iterAB);
		System.out.println("complete");
		
		
		System.out.print("class rambda printing: ");
		icm.printRambdaAB(lamdaFile, cData);
		System.out.println("complete");				
		
		
		/*** Potential assessment part ***/		
		
		System.out.print("rambda reading: ");
		icm.readRambdaAB(lamdaFile, cData);
		System.out.println("complete");		
		
		
		System.out.print("curvature model processing: ");
		icm.proceedCurvaturModel(cData, norm_eData, cData.getLamdaAList(), cData.getLamdaBList());
		System.out.println("complete");			
		
		System.out.print("pressure data printing: ");
		icm.printCurvatureAndPressure(pressureFile, cData);
		System.out.println("complete");
		
		
		/*** Interaction calculating part ***/
		
		System.out.print("interaction calculating: ");
		icm.calculateInteraction(norm_eData, cData, cData.getLamdaAList(), cData.getLamdaBList());
		System.out.println("complete");			
		
		System.out.print("interaction data printing: ");
		icm.printInteraction(interactionFile, cData);
		System.out.println("complete");
		
		System.out.print("latlng coordinate reading: ");
		gf.readCoordinate(latLngCoordFile, cData);
		System.out.println("complete");
						
		
		System.out.print("interaction graph file making: ");
		gf.makeGDFFormatInteraction(graphFile, cData);
		System.out.println("complete");
				
		System.out.println();
		System.out.println("pressure file: "+ pressureFile);
		System.out.println("lamda file: "+ lamdaFile);
		System.out.println("interaction file: "+interactionFile);
		System.out.println("graph file: "+graphFile);
				
		System.out.println();
		System.out.println("[process complete]");
		
		}
		System.out.println("[All process complete]");
		
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
