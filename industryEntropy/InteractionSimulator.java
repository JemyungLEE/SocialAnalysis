package industryEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import industryEntropy.data.EntropyData;
import industryEntropy.data.IndustryData;
import industryEntropy.data.InteractionData;
import industryEntropy.data.CurvatureData;

public class InteractionSimulator {

	/**
	 *  Subject: Spatial Interaction Simulator
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2016.9.10
	 *  Last Modified Data: 2017.11.7 
	 *  Affiliation:	Lab of Rural Planning, 
	 *						Division of Environmental Science and Technology, 
	 *						Graduate School of Agriculture,
	 *						Kyoto University
	 *  Description: Simulate spatial interactions and evaluate demographic attraction forces 
	 *  					 analyzing geographic distribution and diversity of regional industry
	 */
	
	double rambdaA, rambdaB;

	double populationFunction(int year, int district, EntropyData eData){
	
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

	public double distanceFunction(int year, int district_i, int district_j, EntropyData eData, 
								  double rambdaA, double rambdaB){
		/*** Note: distance should be normalized ***/
		
		double distance = Math.sqrt(
				 (Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
			      + Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
	
		return Math.pow(1.0 / (1.0 + Math.pow(10, rambdaA* (rambdaB -(1.0 * distance)))), 3);
	}
	
	public double calculateAttraction(int year, int district_i, int district_j, EntropyData eData, 
																		double rambdaA, double rambdaB){
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
		double curvature;
		
		/*** calculate curvature ***/
		if (entropy_i == 0 || entropy_j ==0) curvature = 0.0;
		else if(district_i == district_j) curvature = 0.0;
		else curvature = (entropy_i-entropy_j)
		/ this.distanceFunction(year,district_i,district_j,eData,rambdaA,rambdaB);		
		
		/***return curvature ***/
		return curvature;
	}
	
	public double calculateAttraction(int year, int district, EntropyData eData, double rambdaA, double rambdaB){
		
		int i;
		double curvature = 0.0;
						
		for(i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district) 
				curvature += (this.calculateAttraction(year, district, i, eData, rambdaA, rambdaB) 
				  	 	     *this.populationFunction(year, i, eData));
				
		return curvature;		
	}
	
	public void calculateAttraction(int year,CurvatureData cData,EntropyData eData,double rambdaA,double rambdaB){
		
		int i;
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<district ; i++)
			cData.setCurvature(year, i, this.calculateAttraction(year, i, eData, rambdaA, rambdaB));
		
		/*** calculate curvature sum ***/
		tempSum = 0.0;
		for(i=0 ; i<district ; i++) 
			if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong)
				tempSum += cData.getCurvature(year, i);
		cData.setCurvatureSum(year, tempSum);
		
	}
	
	public void calculateAttraction(CurvatureData cData, EntropyData eData, double[] rambdaA, double[] rambdaB){
		
		int i, j;
		int years = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<years ; i++)
			for(j=0 ; j<district ; j++)
				cData.setCurvature(i, j, this.calculateAttraction(i, j, eData, rambdaA[i], rambdaB[i]));
		
		/*** calculate curvature sum ***/
		for(i=0 ; i<years ; i++){
			tempSum = 0.0;
			for(j=0 ; j<district ; j++) 
				if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
					tempSum += cData.getCurvature(i, j);
			cData.setCurvatureSum(i, tempSum);
		}
	}

	public double calculateForce(int year, int district, EntropyData eData, double rambdaA, double rambdaB){
		
		return this.calculateAttraction(year, district, eData, rambdaA, rambdaB)
				* this.populationFunction(year, district, eData);		
	}
	
	public void calculateForce(int year,CurvatureData cData,EntropyData eData,double rambdaA,double rambdaB){

		int j;
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(j=0 ; j<district ; j++){
			pressure =this.calculateAttraction(year,j,eData,rambdaA,rambdaB)*this.populationFunction(year,j,eData);
			cData.setPressure(year, j, pressure);
		}
				
		/*** calculate pressure sum ***/
		this.aggregateForce(cData);
	}
	
	public void calculateForce(CurvatureData cData, EntropyData eData, double[] rambdaA, double[] rambdaB){

		int i, j;
		int years = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<district ; j++){
				pressure = this.calculateAttraction(i, j, eData, rambdaA[i], rambdaB[i]) 
							* this.populationFunction(i, j, eData);
				cData.setPressure(i, j, pressure);
			}
		}
		
		/*** calculate pressure sum ***/
		this.aggregateForce(cData);
	}
	
	public void aggregateForce(CurvatureData cData){		
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
	
	public void simulateInteraction(EntropyData eData, CurvatureData cData, double[] rambdaA, double[] rambdaB){
		
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
					else curvature = (entropy_i-entropy_j)/this.distanceFunction(year, i, j, eData, rambdaA[year], rambdaB[year]);	
					
					interaction = curvature * this.populationFunction(year, i, eData) * this.populationFunction(year, j, eData);
							
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
	
	public CurvatureData composeInteractionData(EntropyData eData){
		
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
	
	public void proceedInteractionModel(CurvatureData cData, EntropyData eData, double[] rambdaA, double[] rambdaB){
		
		//curvature calculating
		this.calculateAttraction(cData, eData, rambdaA, rambdaB);		
		//pressure calculating
		this.calculateForce(cData, eData, rambdaA, rambdaB);
	}

}
