package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import populationEntropy.data.*;

public class CurvatureModel_AllAgeCurve {

	double rambdaA, rambdaB;
	
	double maxDistance, minDistance, avgDistance, nDistance;
	String maxRegionA, maxRegionB, minRegionA, minRegionB;
	
	public CurvatureModel_AllAgeCurve(){
		
	}
	
	
	public double populationFunction(int year, int district, EntropyData eData){
		double population;
						
		/*** get population ***/
		population = (double) eData.getPopulation(year, district);	
		
		/*** get normalized population ***/ 
		population /= (double) eData.getMaxPopulation(year);	
		
		/*** return population value ***/
		return population;
	}

	public double distanceFuction(int district_i, int district_j, EntropyData eData){
		/*** Note: distance should be normalized ***/		
		
		return Math.sqrt((Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
										+ Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
	}
	
	public double distanceFuction(int district_i, int district_j, EntropyData eData, double rambda){
		/*** Note: distance should be normalized ***/		
		
		double distance = Math.sqrt(
				 (Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
			      + Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
	
		return Math.pow(1.0 / (1.0 + Math.pow(10, rambda* (0.5 -(1.0 * distance)))), 2);
	}

	public double distanceFuction(int district_i, int district_j, EntropyData eData, 
								  double rambdaA, double rambdaB){
		/*** Note: distance should be normalized ***/
		double distance;
		double latitude_i = eData.getLatitude(district_i);
		double longitude_i = eData.getLongitude(district_i);
		double latitude_j = eData.getLatitude(district_j);
		double longitude_j = eData.getLongitude(district_j);
		double minLatitude = eData.getMinLatitude();
		double minLongitude = eData.getMinLongitude();
		
		if(latitude_i >= minLatitude && longitude_i >= minLongitude &&
				latitude_j >= minLatitude && longitude_j >= minLongitude){
			distance = Math.sqrt((Math.pow((latitude_i-latitude_j),2)+Math.pow((longitude_i-longitude_j),2)));
			return Math.pow(1.0 / (1.0 + Math.pow(10, rambdaA* (rambdaB -(1.0 * distance)))), 2);
		}else return -1.0;
	}
	
	public double distanceFuction(int district_i, int district_j, EntropyData eData, 
														double scale, double min, double rambdaA, double rambdaB){
		/*** Note: distance should be normalized ***/
		double distance;
		double latitude_i = eData.getLatitude(district_i);
		double longitude_i = eData.getLongitude(district_i);
		double latitude_j = eData.getLatitude(district_j);
		double longitude_j = eData.getLongitude(district_j);
		double minLatitude = eData.getMinLatitude();
		double minLongitude = eData.getMinLongitude();
	
		if(latitude_i >= minLatitude && longitude_i >= minLongitude &&
				latitude_j >= minLatitude && longitude_j >= minLongitude){
			distance = Math.sqrt((Math.pow((latitude_i-latitude_j),2)+Math.pow((longitude_i-longitude_j),2)));
			distance = Math.pow(1.0 / (1.0 + Math.pow(10, rambdaA* (rambdaB -(1.0 * distance)))), 2);
			distance = distance * scale + min;
			if(distance > 1.0) return -1.0;
			return distance;
		}else return -1.0;
	}
	
	public double distancePowerFuction(int district_i, int district_j, EntropyData eData){
		/*** Note: distance should be normalized ***/		
		
		double distance = Math.sqrt(
				 (Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
			      + Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
	
		return Math.pow(distance, 2);
	}
	
	public double distanceExpFuction(int district_i, int district_j, EntropyData eData){
		/*** Note: distance should be normalized ***/		
		
		double distance = Math.sqrt(
				 (Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
			      + Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
	
		return Math.exp(distance);
	}
	
	public double calculateCurvature(int year,int district_i,int district_j,EntropyData eData,double rambdaA){
				
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
				
		if (entropy_i == 0 || entropy_j ==0) return 0.0;
		else if(district_i == district_j) return 0.0;
		else return (entropy_i-entropy_j) 
						/ this.distanceFuction(district_i, district_j, eData, rambdaA);
	}

	public double calculateCurvature(int year, int district_i, int district_j, EntropyData eData, 
															double rambdaA, double rambdaB){		
		double population_i = eData.getPopulation(year, district_i);
		double population_j = eData.getPopulation(year, district_j);
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
		double agedRatio_i = eData.getAgedIndex(year, district_i);
		double agedRatio_j = eData.getAgedIndex(year, district_j);
		double index_i, index_j;
		double distance = this.distanceFuction(district_i, district_j, eData, rambdaA, rambdaB);
		double curvature;
		
		/*** calculate curvature ***/
		if(district_i == district_j) curvature = 0.0;
		else if(distance <= 0) curvature = 0.0;
		else if(population_i <= 0 || population_j <= 0) curvature = 0.0;
		else if (entropy_i <= 0 || entropy_j <=0 || agedRatio_i < 0 || agedRatio_j < 0) curvature = 0.0;
		else if(agedRatio_i == 0 || agedRatio_j == 0){
			System.err.println("Zero 65+/15- ratio.");
			curvature = (Double) null;
		}
		else{
			entropy_i = -1.0 * Math.log10(entropy_i); 
			entropy_j = -1.0 * Math.log10(entropy_j);
			agedRatio_i = -1.0 * Math.log10(agedRatio_i);
			agedRatio_j = -1.0 * Math.log10(agedRatio_j);
			
			index_i = entropy_i * agedRatio_i;
			index_j = entropy_j * agedRatio_j;
			
			curvature = (index_i - index_j) / distance;		
			
			//previous version
			//curvature = (eData.getEntropy(year, district_i) - eData.getEntropy(year, district_j)) / distance;
		}
		
		/***return curvature ***/
		return curvature;
	}
	
	public double calculateCurvature(int year, int district_i, int district_j, EntropyData eData, 
											double scale, double min, double weight, double rambdaA, double rambdaB){		
		double population_i = eData.getPopulation(year, district_i);
		double population_j = eData.getPopulation(year, district_j);
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
		double agedRatio_i = eData.getAgedIndex(year, district_i);
		double agedRatio_j = eData.getAgedIndex(year, district_j);
		double index_i, index_j;
		double distance = this.distanceFuction(district_i, district_j, eData, min, scale, rambdaA, rambdaB);
		double curvature;

		/*** calculate curvature ***/
		if(district_i == district_j) curvature = 0.0;
		else if(distance <= 0) curvature = 0.0;
		else if(population_i <= 0 || population_j <= 0) curvature = 0.0;
		else if (entropy_i <= 0 || entropy_j <=0 || agedRatio_i < 0 || agedRatio_j < 0) curvature = 0.0;
		else if(agedRatio_i == 0 || agedRatio_j == 0){
			System.err.println("Zero 65+/15- ratio.");
			curvature = (Double) null;
		}
		else{
			entropy_i = -1.0 * Math.log10(entropy_i); 
			entropy_j = -1.0 * Math.log10(entropy_j);
			agedRatio_i = -1.0 * Math.log10(agedRatio_i);
			agedRatio_j = -1.0 * Math.log10(agedRatio_j);
		
			index_i = Math.pow(entropy_i, weight) * Math.pow(agedRatio_i, 1.0-weight);
			index_j = Math.pow(entropy_j, weight) * Math.pow(agedRatio_j, 1.0-weight);
		
			curvature = (index_i - index_j) / distance;		
		}

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

	public double calculateCurvature(int year, int district, EntropyData eData, 
															double rambdaA, double rambdaB){
		int i;
		double curvature = 0.0;
						
		for(i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district && eData.getPopulation(year, i) > 0) 
				curvature += this.calculateCurvature(year, district, i, eData, rambdaA, rambdaB) 
										* this.populationFunction(year, i, eData);
		return curvature;		
	}
	
	public double calculateCurvature(int year, int district, EntropyData eData, 
											double scale, double min, double weight, double rambdaA, double rambdaB){
		int i;
		double curvature = 0.0;
	
		for(i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district && eData.getPopulation(year, i) > 0) 
				curvature += this.calculateCurvature(year,district,i,eData,scale,min,weight,rambdaA,rambdaB) 
										* this.populationFunction(year, i, eData);
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
		for(i=0 ; i<district ; i++) cData.setCurvature(year, i, this.calculateCurvature(year, i, eData, rambdaA));
		
		/*** calculate curvature sum ***/
		tempSum = 0.0;
		for(i=0 ; i<district ; i++) 
			if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong)
				tempSum += cData.getCurvature(year, i);
		cData.setCurvatureSum(year, tempSum);		
	}
	
	public void calculateCurvature(int year, CurvatureData cData, EntropyData eData,
														double rambdaA, double rambdaB){
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
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<district ; j++) cData.setCurvature(i, j, this.calculateCurvature(i, j, eData, rambdaA[i]));
		
		/*** calculate curvature sum ***/
		for(i=0 ; i<duration ; i++){
			tempSum = 0.0;
			for(j=0 ; j<district ; j++) 
				if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
					tempSum += cData.getCurvature(i, j);
			cData.setCurvatureSum(i, tempSum);
		}
	}
	
	public void calculateCurvature(CurvatureData cData, EntropyData eData, 
														double[] rambdaA, double[] rambdaB){
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<district ; j++) 
				cData.setCurvature(i, j, this.calculateCurvature(i, j, eData, rambdaA[i], rambdaB[i]));
		
		/*** calculate curvature sum ***/
		for(i=0 ; i<duration ; i++){
			tempSum = 0.0;
			for(j=0 ; j<district ; j++) 
				if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
					tempSum += cData.getCurvature(i, j);
			cData.setCurvatureSum(i, tempSum);
		}
	}
	
	public void calculateCurvature(CurvatureData cData, EntropyData eData, 
								double[] scale, double[] min, double[] weight, double[] rambdaA, double[] rambdaB){
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double tempSum;
	
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
	
		/*** calculate curvature ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<district ; j++) 
				cData.setCurvature(i, j, this.calculateCurvature(i, j, eData, scale[i], min[i], weight[i],
												rambdaA[i], rambdaB[i]));
	
		/*** calculate curvature sum ***/
		for(i=0 ; i<duration ; i++){
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
		this.calculatePressureSum(year, cData);
		this.calculateResidualSum(year, eData, cData);
	}

	public void calculatePressure(int year, CurvatureData cData, EntropyData eData,
													double rambdaA, double rambdaB){
		int j;
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(j=0 ; j<district ; j++){
			pressure = 0.0;
			if(eData.getPopulation(year, j) > 0) 
				pressure = this.calculateCurvature(year, j, eData, rambdaA, rambdaB)
									* this.populationFunction(year, j, eData);
			cData.setPressure(year, j, pressure);
		}	
		/*** calculate pressure sum ***/
		this.calculatePressureSum(year, cData);
		this.calculateResidualSum(year, eData, cData);
	}
	
	public void calculatePressure(int year, CurvatureData cData, EntropyData eData,
											double scale, double min, double weight, double rambdaA, double rambdaB){
		int j;
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(j=0 ; j<district ; j++){
			pressure = 0.0;
			if(eData.getPopulation(year, j) > 0) 
				pressure = this.calculateCurvature(year, j, eData, scale, min, weight, rambdaA, rambdaB)
									* this.populationFunction(year, j, eData);
			cData.setPressure(year, j, pressure);
		}	
		/*** calculate pressure sum ***/
		this.calculatePressureSum(year, cData);
		this.calculateResidualSum(year, eData, cData);
	}
	
	public void calculatePressure(CurvatureData cData, EntropyData eData, double[] rambdaA){

		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				pressure = this.calculateCurvature(i, j, eData, rambdaA[i]) * this.populationFunction(i, j, eData);
				cData.setPressure(i, j, pressure);
			}
		}
		
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
		this.calculateResidualSum(eData, cData);
	}

	public void calculatePressure(CurvatureData cData, EntropyData eData, 
													double[] rambdaA, double[] rambdaB){

		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				pressure = 0.0;
				if(eData.getPopulation(i, j) > 0) 
					pressure = this.calculateCurvature(i, j, eData, rambdaA[i], rambdaB[i]) 
										* this.populationFunction(i, j, eData);
				cData.setPressure(i, j, pressure);
			}
		}
		
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
		this.calculateResidualSum(eData, cData);
	}
													
	public void calculatePressure(CurvatureData cData, EntropyData eData, 
								double[] scale, double[] min, double[] weight, double[] rambdaA, double[] rambdaB){	
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double pressure;
				
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				pressure = 0.0;
				if(eData.getPopulation(i, j) > 0) 
					pressure = this.calculateCurvature(i, j, eData, scale[i], min[i], weight[i], rambdaA[i], rambdaB[i]) 
										* this.populationFunction(i, j, eData);
				cData.setPressure(i, j, pressure);
			}
		}
				
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
		this.calculateResidualSum(eData, cData);
	}
	
	public void calculateTraditionalGravity(CurvatureData cData, EntropyData eData, int mode){
		//mode: [0]: power distance function, [1] exponential distance function
		int i, j, k;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double curvature;
		double tmpCurvature = 0.0;
		double pressure;
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				/*** calculate curvature ***/
				curvature = 0.0;		
				for(k=0 ; k<district ; k++){
					if(k != j &&  this.distancePowerFuction(j, k, eData) > 0) {	
						if(mode == 0) tmpCurvature = 1.0 / this.distancePowerFuction(j, k, eData);
						else if(mode == 1) tmpCurvature = 1.0 / this.distanceExpFuction(j, k, eData);
						
						if (eData.getPopulation(i, j) == eData.getPopulation(i, k)) tmpCurvature *= 0.0;
						else if (eData.getPopulation(i, j) > eData.getPopulation(i, k)) tmpCurvature *= 1.0;
						else if (eData.getPopulation(i, j) < eData.getPopulation(i, k)) tmpCurvature *= -1.0;
							
						curvature += (tmpCurvature * this.populationFunction(i, k, eData));
					}
				}
				pressure = curvature * this.populationFunction(i, j, eData);
				cData.setPressure(i, j, pressure);
			}
		}
		
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
		this.calculateResidualSum(eData, cData);
	}
	
	public void calculatePressureSum(int year, CurvatureData cData){		
		int i;
		int district = cData.getDistricNumber();		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		double tempSum;
		
		tempSum = 0.0;
		for(i=0 ; i<district ; i++) 
			if(cData.getLatitude(i) >= minLat && cData.getLongitude(i) >= minLong)
				tempSum += cData.getPressure(year, i);
		cData.setPressureSum(year, tempSum);
	}
	
	public void calculatePressureSum(CurvatureData cData){
		
		for(int i=0 ; i<cData.getNumberOfYears() ; i++) this.calculatePressureSum(i, cData);
	}
	
	public void calculateResidualSum(int year, EntropyData eData, CurvatureData cData){		
		int i;
		int district = cData.getDistricNumber();		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		double maxPressure, maxEntropy, tmpSum;
		double tmpPressure, tmpEntropy;
		
		tmpSum = 0.0;
		maxEntropy = 0.0;
		maxPressure = 0.0;
		
		/*** find max entropy and pressure ***/
		for(i=0 ; i<district ; i++) {
			if(cData.getLatitude(i) >= minLat && cData.getLongitude(i) >= minLong){
				if(maxEntropy < eData.getEntropy(year, i)) maxEntropy = eData.getEntropy(year, i);			
				if(maxPressure < Math.abs(cData.getPressure(year, i))) 
					maxPressure = Math.abs(cData.getPressure(year, i));
			}
		}
				
		/*** calculate square sum of residuals ***/
		for(i=0 ; i<district ; i++){
			if(cData.getLatitude(i) >= minLat && cData.getLongitude(i) >= minLong){
				if(maxPressure >0) tmpPressure = cData.getPressure(year, i) / maxPressure;
				else tmpPressure = 0.0;
				if(maxEntropy > 0) tmpEntropy = eData.getEntropy(year, i) / maxEntropy;
				else tmpEntropy = 0.0;
				tmpSum += Math.pow((tmpPressure - tmpEntropy), 2);

			}
		}
		cData.setResidualSum(year, Math.sqrt(tmpSum));

//		System.out.println("Residual: "+eData.getYear(year)+"\t"+maxPressure+"\t"+maxEntropy+"\t"+tmpSum);
	}
	
	public void calculateResidualSum(EntropyData eData, CurvatureData cData){		
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		double maxPressure, maxEntropy, tmpSum;
		double tmpPressure, tmpEntropy;
		
		for(i=0 ; i<duration ; i++){
			tmpSum = 0.0;
			maxEntropy = 0.0;
			maxPressure = 0.0;
			
			/*** find max entropy and pressure ***/
			for(j=0 ; j<district ; j++) {
				if(cData.getLatitude(j) >= minLat && cData.getLongitude(j) >= minLong){
					if(maxEntropy < eData.getEntropy(i, j)) maxEntropy = eData.getEntropy(i, j);			
					if(maxPressure < Math.abs(cData.getPressure(i, j))) 
						maxPressure = Math.abs(cData.getPressure(i, j));
				}
			}
					
			/*** calculate square sum of residuals ***/
			for(j=0 ; j<district ; j++){
				if(cData.getLatitude(j) >= minLat && cData.getLongitude(j) >= minLong){
					if(maxPressure >0) tmpPressure = cData.getPressure(i, j) / maxPressure;
					else tmpPressure = 0.0;
					if(maxEntropy > 0) tmpEntropy = eData.getEntropy(i, j) / maxEntropy;
					else tmpEntropy = 0.0;
					tmpSum += Math.pow((tmpPressure - tmpEntropy), 2);

				}
			}
			cData.setResidualSum(i, Math.sqrt(tmpSum));
			
	//		System.out.println("Residual: "+eData.getYear(i)+"\t"+maxPressure+"\t"+maxEntropy+"\t"+tmpSum);
		}
	}
	
	public void calculateInteraction(EntropyData eData, CurvatureData cData, 
														double[] rambdaA, double[] rambdaB){
		int i, j;
		int year;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();		
		double entropy_i, entropy_j; 
		double curvature, interaction;
		double max;
		
		/*** calculate interaction ***/
		for(year=0 ; year<duration ; year++){
			for(i=0 ; i<district ; i++){
				for(j=0 ; j<district ; j++){
					curvature = this.calculateCurvature(year, i, j, eData, rambdaA[year], rambdaB[year]);
					interaction = curvature * this.populationFunction(year, i, eData) 
											* this.populationFunction(year, j, eData);
					cData.setInteraction(year, i, j, interaction);
				}			
			}		
		}	

		/*** normalize interaction ***/
		for(year=0 ; year<duration ; year++){
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
		
		cData.initiate(eData.getYears(), eData.getClassNumber(), eData.getDistricNumber(), 
								eData.getDistricClass());
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
	
	public CurvatureData proceedTraditionalGravityModel(EntropyData eData, int mode){
		//mode: [0]: power distance function, [1] exponential distance function
		
		CurvatureData cData;
		
		//curvature data composing
		cData = this.composeCurvatureData(eData);			
		//pressure calculating
		this.calculateTraditionalGravity(cData, eData, mode);
		
		return cData;
	}
	
	public void proceedTraditionalGravityModel(CurvatureData cData, EntropyData eData, int mode){
		//mode: [0]: power distance function, [1] exponential distance function
		
		//pressure calculating
		this.calculateTraditionalGravity(cData, eData, mode);
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
	
	public void proceedCurvaturModel(CurvatureData cData, EntropyData eData, 
								double[] scale, double[] min, double[] weight, double[] rambdaA, double[] rambdaB){
		
		//curvature calculating
		this.calculateCurvature(cData, eData, scale, min, weight, rambdaA, rambdaB);		
		//pressure calculating
		this.calculatePressure(cData, eData, scale, min, weight, rambdaA, rambdaB);
	}		
	
	public void printCurvatureAndPressure(String outputFile, CurvatureData cData){
		
		int i, j;
		int duration = cData.getNumberOfYears();
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("max_latitude: " + cData.getMaxLatitude());
			pw.println("min_latitude: " + cData.getMinLatitude());
			pw.println("max_longitude:" + cData.getMaxLongitude());
			pw.println("min_longitude:" + cData.getMinLongitude());
			pw.println();
			
			pw.println("pressure:");
			//pw.print("district \t latitude \t longitude \t");
			pw.print("district\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getYear(i) + "\t");
			pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				if(cData.getLatitude(i) > cData.getMinLatitude() 
						|| cData.getLongitude(i) > cData.getMinLongitude()){
					pw.print(cData.getDistrictName(i)+"\t");
			//		pw.print(cData.getLatitude(i)+"\t"+cData.getLongitude(i)+"\t");				
					for(j=0 ; j<duration ; j++) pw.print(cData.getPressure(j, i)+"\t");
					pw.println();
				}	
			}
			
			//pw.print("sum\t\t\t");
			pw.print("sum\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getPressureSum(i)+"\t");
			pw.println();			
			pw.println();	
			
			pw.println("curvature:");
			pw.print("district \t latitude \t longitude \t");
			for(i=0 ; i<cData.getNumberOfYears() ; i++) pw.print(cData.getYear(i) + "\t");
			pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				if(cData.getLatitude(i) > cData.getMinLatitude() 
						|| cData.getLongitude(i) > cData.getMinLongitude()){
					pw.print(cData.getDistrictName(i)+"\t");
					pw.print(cData.getLatitude(i)+"\t"+cData.getLongitude(i)+"\t");				
					for(j=0 ; j<duration ; j++) pw.print(cData.getCurvature(j, i)+"\t");
					pw.println();
				}
			}
			pw.print("sum\t\t\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getCurvatureSum(i)+"\t");
			pw.println();
			
			pw.print("RMSE\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getRMSE(i)+"\t");
			pw.println();		
			
			pw.print("residual sum\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getResidualSum(i)+"\t");
			pw.println();		
			
			pw.close();
		}catch(IOException e) {}	

		
		System.out.println();
		System.out.print("sum   ");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f   ",cData.getPressureSum(i));
		System.out.println();
		System.out.print("RMSE   ");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f   ",cData.getRMSE(i));
		System.out.println();
		System.out.print("residual sum   ");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f   ",cData.getResidualSum(i));
		System.out.println();
	}
	
	
	public void printCurvatureAndPressure(String outputFile, CurvatureData cData, EntropyData eData){
		
		int i, j;
		int duration = cData.getNumberOfYears();
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("max_latitude: " + cData.getMaxLatitude());
			pw.println("min_latitude: " + cData.getMinLatitude());
			pw.println("max_longitude:" + cData.getMaxLongitude());
			pw.println("min_longitude:" + cData.getMinLongitude());
			pw.println();
			
			pw.println("curvature:");
			pw.print("district \t latitude \t longitude \t");
			for(i=0 ; i<duration ; i++) pw.print((cData.getYear(i)) + "\t");
			pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				if(cData.getLatitude(i) > cData.getMinLatitude() 
						|| cData.getLongitude(i) > cData.getMinLongitude()){
					pw.print(cData.getDistrictName(i)+"\t");
					pw.print(cData.getLatitude(i)+"\t"+cData.getLongitude(i)+"\t");				
					for(j=0 ; j<duration ; j++) pw.print(cData.getCurvature(j, i)+"\t");
					pw.println();
				}
			}
			pw.print("sum\t\t\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getCurvatureSum(i)+"\t");
			pw.println();
			pw.println();
			
			pw.println("pressure:");
			pw.print("district \t latitude \t longitude \t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getYear(i) + "\t");
			pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				if(cData.getLatitude(i) > cData.getMinLatitude() 
						|| cData.getLongitude(i) > cData.getMinLongitude()){
					pw.print(cData.getDistrictName(i)+"\t");
					pw.print(cData.getLatitude(i)+"\t"+cData.getLongitude(i)+"\t");				
					for(j=0 ; j<duration ; j++) pw.print(cData.getPressure(j, i)+"\t");
					pw.println();
				}	
			}
			pw.print("sum\t\t\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getPressureSum(i)+"\t");
			pw.println();
			
			pw.print("residual sum\t\t\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getResidualSum(i)+"\t");
			pw.println();

			pw.print("RMSE\t\t\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getRMSE(i)+"\t");
			pw.println();
			
			/*
			System.out.println("test");
			
			//pw.println("population:");
			pw.print("district \t ");
			for(i=0 ; i<eData.getNumberOfYears() ; i++) pw.print(eData.getYear(i)+"\t");
			pw.println();
			for(i=0 ; i<eData.getDistricNumber() ; i++){
				pw.print(eData.getDistrictName(i)+"\t");							
				for(j=0 ; j<eData.getNumberOfYears() ; j++) pw.print(eData.getEntropy(j, i)+"\t");
				//for(j=0 ; j<eData.getDuration() ; j++) pw.print(eData.getPopulation(j, i)+"\t");
				pw.println();
					
			}
			*/
			
			System.out.println("population:");
			System.out.print("district \t latitude \t longitude \t");			
			for(i=0 ; i<eData.getDistricNumber() ; i++){
				if(cData.getLatitude(i)>eData.getMinLatitude() || eData.getLongitude(i)>eData.getMinLongitude()){
					System.out.print(eData.getDistrictName(i)+"\t"+eData.getLatitude(i)+"\t"+eData.getLongitude(i)+"\t");
										
					for(j=0 ; j<duration ; j++) System.out.print(eData.getPopulation(j, i)+"\t");
					System.out.println();
				}	
			}

			System.out.println("entropy:");
			System.out.print("district \t ");			
			for(i=0 ; i<duration ; i++) System.out.print(cData.getYear(i) + "\t");
			System.out.println();
			for(i=0 ; i<eData.getDistricNumber() ; i++){
				if(cData.getLatitude(i)>eData.getMinLatitude() || eData.getLongitude(i)>eData.getMinLongitude()){
					System.out.print(eData.getDistrictName(i)+"\t");
					for(j=0 ; j<duration ; j++) System.out.print(eData.getEntropy(j, i)+"\t");
					System.out.println();
				}	
			}
					
			pw.close();
		}catch(IOException e) {}	
	}
	
	public void optimizeDistanceConstant(CurvatureData cData, EntropyData eData, int iter){
		
		int i, j;		
		int duration = eData.getNumberOfYears();
		double[] rambda = new double[duration];
		double[] sum = new double[duration];
		
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

			this.calculatePressure(i, cData, eData, x1);
	//		fx1 = Math.abs(cData.getPressureSum(i));
			fx1 = Math.abs(cData.getResidualSum(i));
			this.calculatePressure(i, cData, eData, x2);
	//		fx2 = Math.abs(cData.getPressureSum(i));
			fx2 = Math.abs(cData.getResidualSum(i));
			
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
		//		fx1 = Math.abs(cData.getPressureSum(i));
				fx1 = Math.abs(cData.getResidualSum(i));
				this.calculatePressure(i, cData, eData, x2);
		//		fx2 = Math.abs(cData.getPressureSum(i));	
				fx2 = Math.abs(cData.getResidualSum(i));
			
				//System.out.println(j+"  x1: "+x1+"\tfx1: "+fx1+"\tx2: "+x2+"\tfx2: "+fx2);
			}
			
			rambda[i] = x1;
			sum[i] = fx1;
			
			System.out.println(eData.getYear(i)+"\tx1:\t"+x1+"\tfx1:\t"+fx1+"\tx2:\t"+x2+"\tfx2:\t"+fx2);	
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
		
	
	public void optimizeDistanceConstant(CurvatureData cData, EntropyData eData, int iterA, int iterB, int iterAB){
		
		int i, j;		
		int iter;
		int duration = eData.getNumberOfYears();
		double[] tmpRambdaA = new double[duration];
		double[] tmpRambdaB = new double[duration];
		double[] tmpSum = new double[duration];
		
		double xuA, xlA, x1A, x2A;
		double xuB, xlB, x1B, x2B;
		double fx1, fx2;
		double goldRatio = (Math.sqrt(5)-1)/2;
		double dA, dB;
		double initial_xB = 0.5;
		double xA_low = 0.0;
		double xA_high = 10.0;
		double xB_low = 0.0;
		double xB_high = 2.0;
		
		//time variable
		long startTime, endTime;
		double operationTime, remainedTime;

		//checking start time
		startTime = System.currentTimeMillis();
		
		for(i=0 ; i<duration ; i++){
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
	//		fx1 = Math.abs(cData.getPressureSum(i));
			fx1 = Math.abs(cData.getResidualSum(i));
			this.calculatePressure(i, cData, eData, x2A, x2B);
	//		fx2 = Math.abs(cData.getPressureSum(i));	
			fx2 = Math.abs(cData.getResidualSum(i));
							
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
		//		fx1 = Math.abs(cData.getPressureSum(i));
				fx1 = Math.abs(cData.getResidualSum(i));
				this.calculatePressure(i, cData, eData, x2A, x2B);
		//		fx2 = Math.abs(cData.getPressureSum(i));	
				fx2 = Math.abs(cData.getResidualSum(i));
			}
			
			while(iter < iterAB){
				xuB = xB_high;
				xlB = xB_low;
				dB = goldRatio * (xuB-xlB);
				x1B = xlB + dB;
				x2B = xuB - dB;
				
				this.calculatePressure(i, cData, eData, x1A, x1B);
		//		fx1 = Math.abs(cData.getPressureSum(i));
				fx1 = Math.abs(cData.getResidualSum(i));
				this.calculatePressure(i, cData, eData, x2A, x2B);
		//		fx2 = Math.abs(cData.getPressureSum(i));
				fx2 = Math.abs(cData.getResidualSum(i));
				
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
		//			fx1 = Math.abs(cData.getPressureSum(i));
					fx1 = Math.abs(cData.getResidualSum(i));
					this.calculatePressure(i, cData, eData, x2A, x2B);
		//			fx2 = Math.abs(cData.getPressureSum(i));
					fx2 = Math.abs(cData.getResidualSum(i));
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
		//			fx1 = Math.abs(cData.getPressureSum(i));
					fx1 = Math.abs(cData.getResidualSum(i));
					this.calculatePressure(i, cData, eData, x2A, x2B);
		//			fx2 = Math.abs(cData.getPressureSum(i));	
					fx2 = Math.abs(cData.getResidualSum(i));
				}
				
				iter++;
			}
			
			tmpRambdaA[i] = x1A;
			tmpRambdaB[i] = x2A;
			tmpSum[i]= fx1;				
		
			cData.setLambdaA(i, x1A);
			cData.setLambdaB(i, x1B);
			cData.setPressureSum(i, fx1);
			
			System.out.print(eData.getYear(i)+"\tx1A:\t"+x1A+"\tx1B:\t"+x1B+"\tfx1:\t"+fx1
															  +"\tx2A:\t"+x2A+"\tx2B:\t"+x2B+"\tfx2:\t"+fx2+"\t");
			//checking operation time	
			endTime = System.currentTimeMillis();
			operationTime = (double)(endTime - startTime)/1000.0;
			remainedTime = (double)(duration-i-1)/(i+1) * operationTime;
			this.printRemainedTime(remainedTime);
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
		
		System.out.println("total operation time: "+operationTime);
	}

	public void optimizeDistanceConstantRMSE(CurvatureData cData, EntropyData eData, int iterA, int iterB, int iterAB){
		
		int i, j;		
		int iter;
		int duration = eData.getNumberOfYears();
		double[] tmpRambdaA = new double[duration];
		double[] tmpRambdaB = new double[duration];
		double[] tmpSum = new double[duration];
		
		double xuA, xlA, x1A, x2A;
		double xuB, xlB, x1B, x2B;
		double fx1, fx2;
		double goldRatio = (Math.sqrt(5)-1)/2;
		double dA, dB;
		double initial_xB = 0.5;
		double xA_low = 0.0;
		double xA_high = 10.0;
		double xB_low = 0.0;
		double xB_high = 2.0;
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		String inflowFile = filePath + "migration/migration_in.txt";
		String outflowFile = filePath + "migration/migration_out.txt";
		MigrationCalculator mc = new MigrationCalculator();
		mc.readMigrationData(inflowFile, outflowFile);
		
		//time variable
		long startTime, endTime;
		double operationTime, remainedTime;

		//checking start time
		startTime = System.currentTimeMillis();
		
		for(i=0 ; i<duration ; i++){
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
			fx1 = mc.calculateRMSE(cData.getYear(i), cData, eData); 
			this.calculatePressure(i, cData, eData, x2A, x2B);
			fx2 = mc.calculateRMSE(cData.getYear(i), cData, eData); 
							
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
				fx1 = mc.calculateRMSE(cData.getYear(i), cData, eData); 
				this.calculatePressure(i, cData, eData, x2A, x2B);
				fx2 = mc.calculateRMSE(cData.getYear(i), cData, eData); 
			}
			
			while(iter < iterAB){
				xuB = xB_high;
				xlB = xB_low;
				dB = goldRatio * (xuB-xlB);
				x1B = xlB + dB;
				x2B = xuB - dB;
				
				this.calculatePressure(i, cData, eData, x1A, x1B);
				fx1 = mc.calculateRMSE(cData.getYear(i), cData, eData); 
				this.calculatePressure(i, cData, eData, x2A, x2B);
				fx2 = mc.calculateRMSE(cData.getYear(i), cData, eData); 
				
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
					fx1 = mc.calculateRMSE(cData.getYear(i), cData, eData); 
					this.calculatePressure(i, cData, eData, x2A, x2B);
					fx2 = mc.calculateRMSE(cData.getYear(i), cData, eData); 
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
					fx1 = mc.calculateRMSE(cData.getYear(i), cData, eData); 
					this.calculatePressure(i, cData, eData, x2A, x2B);
					fx2 = mc.calculateRMSE(cData.getYear(i), cData, eData); 
				}
				iter++;
			}
			
			tmpRambdaA[i] = x1A;
			tmpRambdaB[i] = x2A;
			tmpSum[i]= fx1;				
		
			cData.setLambdaA(i, x1A);
			cData.setLambdaB(i, x1B);
			cData.setRMSE(i, fx1);
			
			System.out.print(eData.getYear(i)+"\tx1A:\t"+x1A+"\tx1B:\t"+x1B+"\tfx1:\t"+fx1
															  +"\tx2A:\t"+x2A+"\tx2B:\t"+x2B+"\tfx2:\t"+fx2+"\t");
			//checking operation time	
			endTime = System.currentTimeMillis();
			operationTime = (double)(endTime - startTime)/1000.0;
			remainedTime = (double)(duration-i-1)/(i+1) * operationTime;
			this.printRemainedTime(remainedTime);
		}				
		
		//checking operation time	
		endTime = System.currentTimeMillis();
		operationTime = (double)(endTime - startTime)/1000.0;
		
		System.out.println("total operation time: "+operationTime);
	}
	
	public void proceedOptimizationGAprocess(CurvatureData cData, EntropyData eData, int iteration){
		int i, j, k;
		int duration = eData.getNumberOfYears();
		int variableNumber = 5;		//[0]: scale, [1]: minimum,  [2]: weight, [3]:lamda A, [4]: lamda B
		int variableLength = 20;
		int  chromosomePopulation =100;
		double crossoverRate = 0.9;
		double mutationRate = 0.05;
		double[] minValues = new double[variableNumber];
		double[] maxValues = new double[variableNumber];
		ArrayList<double[]> variables = null;
		double convergence = -1.0;
		
		GeneticAlgorithm ga;
		Chromosome cs;
		
		/***set range of variable: weight[0], lamda A[1] and B[2] ***/
		minValues[0] = 1.0;
		maxValues[0] = 2.0;
		minValues[1] = 0.0;
		maxValues[1] = 0.5;
		minValues[2] = 0.0;
		maxValues[2] = 1.0;
		minValues[3] = 0.0;
		maxValues[3] = 10.0;
		minValues[4] = 0.0;
		maxValues[4] = 2.0;
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		String inflowFile = filePath + "migration/migration_in.txt";
		String outflowFile = filePath + "migration/migration_out.txt";
		MigrationCalculator mc = new MigrationCalculator();
		mc.readMigrationData(inflowFile, outflowFile);
		
		//time variable
		long startTime, endTime;
		double operationTime, remainedTime;
		//checking start time
		startTime = System.currentTimeMillis();
		
		/*** optimization process ***/
		for(i=0 ; i<duration ; i++){
			ga = new GeneticAlgorithm(crossoverRate, mutationRate);
			cs = new Chromosome(chromosomePopulation, variableNumber, variableLength);
			 
			for(j=0 ; j<iteration ; j++){
				variables = cs.getVariableValueList(minValues, maxValues);
				for(k=0 ; k<chromosomePopulation ; k++){
					cData.clearData(i);
					this.calculatePressure(i, cData, eData, variables.get(k)[0], variables.get(k)[1], 
															variables.get(k)[2], variables.get(k)[3], variables.get(k)[4]);
					cs.setFunctionResult(k, mc.calculateSqrtRMSE(cData.getYear(i), cData, eData)); 
		//			System.out.println(eData.getYear(i)+"\t"+(j+1)+"\t"+(k+1)+"\t"+variables.get(k)[0]+"\t"+variables.get(k)[1]+"\t"+variables.get(k)[2]+"\t"+functionResults[k]);
				}
				convergence = ga.nextGeneration(cs);
			}
			variables = cs.getVariableValueList(minValues, maxValues);
			
			cData.setScale(i, variables.get(0)[0]);
			cData.setMinimum(i, variables.get(0)[1]);
			cData.setWeight(i, variables.get(0)[2]);
			cData.setLambdaA(i, variables.get(0)[3]);
			cData.setLambdaB(i, variables.get(0)[4]);
			cData.setRMSE(i, cs.getFunctionResult(0));
			
			int lowCut;
			if(cs.getPopulation() < 5) lowCut = cs.getPopulation(); else lowCut = 5;
			for(j=0 ; j<lowCut ; j++)
				System.out.println(eData.getYear(i)+"\t"+variables.get(j)[0]+"\t"+variables.get(j)[1]+"\t"
												+variables.get(j)[2]+"\t"+variables.get(j)[3]+"\t"+variables.get(j)[4]+"\t"
												+cs.getFunctionResult(j)+"\t"+convergence+"\t");
		
			//checking operation time	
			endTime = System.currentTimeMillis();
			operationTime = (double)(endTime - startTime)/1000.0;
			remainedTime = (double)(duration-i-1)/(i+1) * operationTime;
			this.printRemainedTime(remainedTime);
		}
		//checking operation time	
		endTime = System.currentTimeMillis();
		operationTime = (double)(endTime - startTime)/1000.0;

		System.out.println("total operation time: "+operationTime);
	}
	
	
	public void checkDistance(EntropyData eData){
		int i, j, k;
		int districts = eData.getDistricNumber();
		int duration = eData.getNumberOfYears();
		double distance;;
		
		for(i=0 ; i<duration ; i++){
			this.maxDistance = 0.0;
			this.minDistance = 100000000;
			this.avgDistance = 0.0;
			this.nDistance = 0.0;
			for(j=0 ; j<districts ; j++){
				for(k=j ; k<districts ; k++){
					distance = this.distanceFuction(j, k, eData);
					if(j!=k && distance >0 && eData.getPopulation(i, j)>0 && eData.getPopulation(i, k)>0){
						this.avgDistance =( (this.avgDistance * this.nDistance++ ) + distance ) / this.nDistance;
						if(this.maxDistance < distance ){
							this.maxDistance = distance;
							this.maxRegionA = eData.getDistrictList()[j];
							this.maxRegionB = eData.getDistrictList()[k];
						}
						if(this.minDistance > distance){
							this.minDistance = distance;
							this.minRegionA = eData.getDistrictList()[j];
							this.minRegionB = eData.getDistrictList()[k];
						}
					}
				}
			}
			System.out.println(eData.getYear(i)+"\t"+this.maxDistance+"\t"+this.maxRegionA+"\t"+this.maxRegionB+"\t"+this.minDistance+"\t"+this.minRegionA+"\t"+this.minRegionB+"\t"+this.avgDistance+"\t"+this.nDistance);
		}
	}
	
	public void checkDistance(EntropyData eData, CurvatureData cData){
		int i, j, k;
		int districts = eData.getDistricNumber();
		int duration = eData.getNumberOfYears();
		double distance;;
		
		for(i=0 ; i<duration ; i++){
			this.maxDistance = 0.0;
			this.minDistance = 100000000;
			this.avgDistance = 0.0;
			this.nDistance = 0.0;
			for(j=0 ; j<districts ; j++){
				for(k=j ; k<districts ; k++){
					distance = this.distanceFuction(j, k, eData, cData.getLamdaA(i), cData.getLamdaB(i));
					if(j!=k && distance >0 && eData.getPopulation(i, j)>0 && eData.getPopulation(i, k)>0){
						this.avgDistance =( (this.avgDistance * this.nDistance++ ) + distance ) / this.nDistance;
						if(this.maxDistance < distance ){
							this.maxDistance = distance;
							this.maxRegionA = eData.getDistrictList()[j];
							this.maxRegionB = eData.getDistrictList()[k];
						}
						if(this.minDistance > distance){
							this.minDistance = distance;
							this.minRegionA = eData.getDistrictList()[j];
							this.minRegionB = eData.getDistrictList()[k];
						}
					}
				}
			}
			System.out.println(eData.getYear(i)+"\t"+this.maxDistance+"\t"+this.maxRegionA+"\t"+this.maxRegionB+"\t"+this.minDistance+"\t"+this.minRegionA+"\t"+this.minRegionB+"\t"+this.avgDistance+"\t"+this.nDistance);
		}
	}
	
	public void printRemainedTime(double remainedTime){
		
		if(remainedTime >= 86400){
			System.out.print((int)(remainedTime/86400)+" day(s), ");
			System.out.print((int)(remainedTime%86400/3600)+" hour(s), ");
			System.out.print((int)(remainedTime%3600/60)+" minute(s), ");
			System.out.println((int)(remainedTime%60)+ " second(s) left");	
		}else if(remainedTime >= 3600){
			System.out.print((int)(remainedTime/3600)+" hour(s), ");
			System.out.print((int)(remainedTime%3600/60)+" minute(s), ");
			System.out.println((int)(remainedTime%60)+ " second(s) left");	
		}else if(remainedTime >= 60){
			System.out.print((int)(remainedTime/60)+" minute(s), ");
			System.out.println((int)(remainedTime%60)+ " second(s) left");	
		}else{
			System.out.println((int)(remainedTime)+ " second(s) left");	
		}
	}
	
	public void readLamdaAB(String inputFile, CurvatureData cData){
		int i;
		int year, yearIndex;
		
		try{
			File file = new File(inputFile);			
			Scanner scan = new Scanner(file);		
						
			while(scan.hasNext()){
				year = scan.nextInt();
				yearIndex = cData.getYearIndex(year);
				scan.next();
				cData.setLambdaA(yearIndex, scan.nextDouble());
				scan.next();
				cData.setLambdaB(yearIndex, scan.nextDouble());		
			}
			
			scan.close();			
		} catch(IOException e) {
			System.err.print("read class rambda error\t");
		}
	}
	
	public void printLamdaAB(String outputFile, CurvatureData cData){
		int i;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			for(i=0 ; i<cData.getNumberOfYears() ; i++){
				pw.print(cData.getYear(i));
				pw.println("\trambdaA\t"+cData.getLamdaA(i)+"\trambdaB\t"+cData.getLamdaB(i));			
			}
			
			pw.close();
		}catch(IOException e) {}
	}

	public void printModelProperties(String outputFile, CurvatureData cData){
		int i;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.println("Year\tScale\tMinimum\tWeight\tLamba_A\tLambda_B");
			for(i=0 ; i<cData.getNumberOfYears() ; i++){
				pw.print(cData.getYear(i));
				pw.println("\t"+cData.getScale(i)+"\t"+cData.getMinimum(i)+"\t"+cData.getWeight(i)+"\t"
									+cData.getLamdaA(i)+"\t"+cData.getLamdaB(i));			
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printInteraction(String outputFile, CurvatureData cData){
		
		int i, j;
		int year;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double minLat = cData.getMinLatitude();
		double maxLat = cData.getMaxLatitude();
		double minLong = cData.getMinLongitude();
		double maxLong = cData.getMaxLongitude();
		
		
		for(year=0 ; year<duration ; year++){			
		
			try{
				File file = new File(outputFile.replace(".txt", "_"+(cData.getYearIndex(year))+".txt"));
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
	
	public static void main(String[] args) {
	
		int iteration = 5;
		int iter	= 50;
		int iterAB	= 5;
				
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		String outputPath = filePath + "all_age_curve/";
	//	String outputPath = filePath + "traditional_gravity/";
	//	String populationFile = filePath + "population_region.txt";
		String populationFile = filePath + "population_registered_1995_2010.txt";
	//	String populationFile = filePath + "population_registered_data.txt";
		String coordinatesFile = filePath + "coordinates_TM.txt";
		String latLngCoordFile = filePath + "coordinates_WGS84.txt";
	//	String rambdaFile = outputPath + "RambdaAB_region_"+iter+"_"+iterAB+".txt";
		String rambdaFile = outputPath + "RambdaAB_GAoptimized_"+iteration+".txt";
	//	String pressureFile = outputPath + "Pressure_RambdaAB_region_"+iter+"_"+iterAB+".txt";
		String pressureFile = outputPath + "Pressure_GAoptimized_"+iteration+".txt";
	//	String interactionFile = outputPath + "interaction/Interaction_region_"+iter+"_"+iterAB+".txt";
		String interactionFile = outputPath + "interaction/Interaction_GAoptimized_"+iteration+".txt";
		String graphFile = outputPath + "interaction/GraphGDF_"+iter+"_"+iterAB+".txt";
		
		String inflowFile = filePath + "migration/migration_in.txt";
		String outflowFile = filePath + "migration/migration_out.txt";

		
		PopulationData pData = new PopulationData();
		Coordinates points = new Coordinates();
		EntropyData	eData = new EntropyData();
		EntropyData	norm_eData = new EntropyData();
		CurvatureData cData;
		
		EntropyCalculator ec = new EntropyCalculator();
		DataReader dr = new DataReader();
		MapGenerator mg = new MapGenerator();
		CurvatureModel_AllAgeCurve cm = new CurvatureModel_AllAgeCurve();		
		GraphFormat gf = new GraphFormat();
		MigrationCalculator mc = new MigrationCalculator();

		
		System.out.print("data reading: ");
		pData = dr.getPopulationData(populationFile);
		//pData = dr.getData(populationFile);
		//dr.printData(outputPath+"population_readed.txt");
		System.out.println("complete");
		
		System.out.print("entropy calculating: ");
		ec.proceedEntropyCalculation(pData);
		//ec.calculateClassEntropy(pData);
		//ec.calculateAgeWeightedEntropy(pData);
		System.out.println("complete");
				
		System.out.print("coordinates reading: ");
		mg.readCoordinates(coordinatesFile, points);
		eData = mg.composeEntropyData(points, pData);
		//eData = mg.composeClassEntropyData(points, pData);
		//eData.printData(outputPath+"entropy_result.txt");
		System.out.println("complete");
		
		System.out.print("entropy normalizing: ");
		norm_eData = mg.normalizeEntropyData(eData, 0.9, 0.1, 1.0, 0.0, 1.0, 0.0);
		//norm_eData = mg.normalizeClassEntropy(eData);
		//norm_eData.printData(outputPath+"entropy_normalized_result.txt");
		System.out.println("complete");

		System.out.print("curvature data composing: ");
		cData = cm.composeCurvatureData(norm_eData);
		System.out.println("complete");
		
		/*
		System.out.print("curvature model processing: ");
		cData = cm.proceedCurvaturModel(norm_eData, dist_constant);
		//cData = cm.proceedCurvaturModel(norm_eData, pData, dist_constant);
		System.out.println("complete");		
		*/
		/*
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
		
		/*** friction function optimizing part ***/
		
		System.out.println("pressure optimizing: ");
		//cm.optimizeDistanceConstant(cData, norm_eData, iter, iter, iterAB);
		//cm.optimizeDistanceConstantRMSE(cData, norm_eData, iter, iter, iterAB);
		cm.proceedOptimizationGAprocess(cData, norm_eData, iteration);
		System.out.println("complete");

		//cm.checkDistance(norm_eData, cData);
		System.out.print("class rambda printing: ");
		//cm.printLamdaAB(rambdaFile, cData);
		cm.printModelProperties(rambdaFile, cData);
		System.out.println("complete");				
		
		
		/*** Potential assessment part ***/		
		/*
		System.out.print("rambda reading: ");
		cm.readRambdaAB(rambdaFile, cData);
		System.out.println("complete");		
		*/
		
		System.out.print("curvature model processing: ");
	//	cm.proceedCurvaturModel(cData, norm_eData, cData.getLamdaAList(), cData.getLamdaBList());
		cm.proceedCurvaturModel(cData, norm_eData, cData.getScaleList(), cData.getMinimumList(), 
													cData.getWeightList(), cData.getLamdaAList(), cData.getLamdaBList());
		mc.readMigrationData(inflowFile, outflowFile);
		mc.calculateRMSE(cData, norm_eData);
		System.out.println("complete");			
		
		System.out.print("pressure data printing: ");
		cm.printCurvatureAndPressure(pressureFile, cData);
		System.out.println("complete");
		
		/*
		System.out.print("gravity model processing: ");
		cm.proceedTraditionalGravityModel(cData, norm_eData, 1);
		System.out.println("complete");			
	
		
		System.out.print("pressure data printing: ");
		cm.printCurvatureAndPressure(pressureFile, cData);
		System.out.println("complete");
		*/
		
		/*** Interaction calculating part ***/
		/*
		System.out.print("interaction calculating: ");
		cm.calculateInteraction(norm_eData, cData, cData.rambda, cData.rambdaB);
		System.out.println("complete");			
		
		System.out.print("interaction data printing: ");
		cm.printInteraction(interactionFile, cData);
		System.out.println("complete");
		
		System.out.print("latlng coordinate reading: ");
		gf.readCoordinate(latLngCoordFile, cData);
		System.out.println("complete");
						
		
		System.out.print("interaction graph file making: ");
		gf.makeGDFFormatInteraction(graphFile, cData);
		System.out.println("complete");
				
		System.out.println();
		System.out.println("pressure file: "+ pressureFile);
		System.out.println("rambda file: "+ rambdaFile);
		System.out.println("interaction file: "+interactionFile);
		System.out.println("graph file: "+graphFile);
		*/
		
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