package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import populationEntropy.data.*;

public class CurvatureModel_Korea {

	double lamdaA, lamdaB;
	
	double maxDistance, minDistance, avgDistance, nDistance;
	String maxRegionA, maxRegionB, minRegionA, minRegionB;
	
	public CurvatureModel_Korea(){
		
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

	public double getDistance(int district_i, int district_j, EntropyData eData){
		/*** Note: distance should be normalized ***/		
		
		return Math.sqrt((Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
										+ Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
	}

	public double getDistance(int district_i, int district_j, EntropyData eData, 
								  double lamdaA, double lamdaB){
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
		//	return 1.0 / (1.0 + Math.pow(10, lamdaA* (lamdaB -(1.0 * distance))));
			return 1.0 / (1.0 + Math.pow(10, lamdaA* (lamdaB - distance)));
		}else return -1.0;
	}
	
	public double getDistance(int district_i, int district_j, EntropyData eData, 
												double min, double lamdaA, double lamdaB){
		/*** Note: distance should be normalized ***/
		double distance;
		double latitude_i = eData.getLatitude(district_i);
		double longitude_i = eData.getLongitude(district_i);
		double latitude_j = eData.getLatitude(district_j);
		double longitude_j = eData.getLongitude(district_j);
		double minLatitude = eData.getMinLatitude();
		double minLongitude = eData.getMinLongitude();
	//	double maxValue = 1.0 / (1.0 + Math.pow(10, lamdaA* (lamdaB - 1.0)));
	//	double minValue = 1.0 / (1.0 + Math.pow(10, lamdaA* (lamdaB)));
		
		if(latitude_i >= minLatitude && longitude_i >= minLongitude &&
				latitude_j >= minLatitude && longitude_j >= minLongitude){
			distance = Math.sqrt((Math.pow((latitude_i-latitude_j),2)+Math.pow((longitude_i-longitude_j),2)));
	//		distance = 1.0 / (1.0 + Math.pow(10, lamdaA* (lamdaB -(1.0 * distance))));
	//		distance = (distance - minValue) / (maxValue - minValue) * (1.0 - min) + min;
			distance = 1.0 / (1.0 + Math.pow(10, lamdaA* (lamdaB - distance)));
			distance = distance * (1.0 - min) + min;
			return distance;
		}else return -1.0;
	}
	
	public double getDistance(int district_i, int district_j, EntropyData eData, 
												double scale, double min, double lamdaA, double lamdaB){
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
	//		distance = 1.0 / (1.0 + Math.pow(10, lamdaA* (lamdaB -(1.0 * distance))));
	//		distance = distance * scale + min;
			distance = 1.0 / (1.0 + Math.pow(10, lamdaA* (lamdaB - distance)));
			distance = distance * (1.0 - min) + min;
	//		if(distance > 1.0) return 1.0;
			return distance;
		}else return -1.0;
	}
	
	public double distancePowerFuction(int district_i, int district_j, EntropyData eData){
		/*** Note: distance should be normalized ***/		
		return Math.pow(this.getDistance(district_i, district_j, eData), 2);
	}
	
	public double distanceExpFuction(int district_i, int district_j, EntropyData eData){
		/*** Note: distance should be normalized ***/		
		return Math.exp(this.getDistance(district_i, district_j, eData));
	}

	public double calculateCurvature(int year, int district_i, int district_j, EntropyData eData, 
															double lamdaA, double lamdaB){		
		double population_i = eData.getPopulation(year, district_i);
		double population_j = eData.getPopulation(year, district_j);
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
		double agedRatio_i = eData.getAgedIndex(year, district_i);
		double agedRatio_j = eData.getAgedIndex(year, district_j);
		double index_i, index_j;
		double distance = this.getDistance(district_i, district_j, eData, lamdaA, lamdaB);
		double curvature;
		double logBase = Math.log(10);
		
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
			entropy_i = -1.0 * Math.log(entropy_i)/logBase; 
			entropy_j = -1.0 * Math.log(entropy_j)/logBase;
			agedRatio_i = -1.0 * Math.log(agedRatio_i)/logBase;
			agedRatio_j = -1.0 * Math.log(agedRatio_j)/logBase;
			
			index_i = Math.sqrt(Math.abs(entropy_i * agedRatio_i));
			if (agedRatio_i < 0) index_i *= -1.0;
			index_j = Math.sqrt(Math.abs(entropy_j * agedRatio_j));
			if (agedRatio_j < 0) index_j *= -1.0;
			
			curvature = (index_i - index_j) / Math.pow(distance, 2);		
			
			//previous version
			//curvature = (eData.getEntropy(year, district_i) - eData.getEntropy(year, district_j)) / distance;
		}
		
		/***return curvature ***/
		return curvature;
	}
	
	public double calculateCurvature(int year, int district_i, int district_j, EntropyData eData, 
															double weight, double lamdaA, double lamdaB){		
		double population_i = eData.getPopulation(year, district_i);
		double population_j = eData.getPopulation(year, district_j);
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
		double agedRatio_i = eData.getAgedIndex(year, district_i);
		double agedRatio_j = eData.getAgedIndex(year, district_j);
		double index_i, index_j;
		double distance = this.getDistance(district_i, district_j, eData, lamdaA, lamdaB);
		double curvature;
		double logBase = Math.log(10);
		
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
			entropy_i = -1.0 * Math.log(entropy_i)/logBase; 
			entropy_j = -1.0 * Math.log(entropy_j)/logBase;
			agedRatio_i = -1.0 * Math.log(agedRatio_i)/logBase;
			agedRatio_j = -1.0 * Math.log(agedRatio_j)/logBase;

			index_i = Math.pow(entropy_i, weight) * Math.pow(Math.abs(agedRatio_i), 1.0-weight);
			if (agedRatio_i < 0) index_i *= -1.0;
			index_j = Math.pow(entropy_j, weight) * Math.pow(Math.abs(agedRatio_j), 1.0-weight);
			if (agedRatio_j < 0) index_j *= -1.0;
			
			curvature = (index_i - index_j) / Math.pow(distance, 2);		
		}

		return curvature;
	}
	
	public double calculateCurvature(int year, int district_i, int district_j, EntropyData eData, 
															double min, double weight, double lamdaA, double lamdaB){		
		double population_i = eData.getPopulation(year, district_i);
		double population_j = eData.getPopulation(year, district_j);
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
		double agedRatio_i = eData.getAgedIndex(year, district_i);
		double agedRatio_j = eData.getAgedIndex(year, district_j);
		double distance = this.getDistance(district_i, district_j, eData, min, lamdaA, lamdaB);
		double curvature;
		double index_i, index_j;
		double logBase = Math.log(10);
		
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
			/*
			entropy_i = -1.0 * Math.log(entropy_i)/logBase; 
			entropy_j = -1.0 * Math.log(entropy_j)/logBase;
			agedRatio_i = -1.0 * Math.log(agedRatio_i)/logBase;
			agedRatio_j = -1.0 * Math.log(agedRatio_j)/logBase;
			
			index_i = Math.pow(entropy_i, weight) * Math.pow(Math.abs(agedRatio_i), 1.0-weight);
			if (agedRatio_i < 0) index_i *= -1.0;
			index_j = Math.pow(entropy_j, weight) * Math.pow(Math.abs(agedRatio_j), 1.0-weight);
			if (agedRatio_j < 0) index_j *= -1.0;		
			*/
			
		//	index_i = agedRatio_i;
		//	index_j = agedRatio_j;
			
		//	index_i = Math.pow(entropy_i, weight*agedRatio_i);
		//	index_j = Math.pow(entropy_j, weight*agedRatio_j);
	
			index_i = Math.pow(entropy_i, Math.pow(agedRatio_i, weight));
			index_j = Math.pow(entropy_j, Math.pow(agedRatio_j, weight));
			
		//	index_i = -1.0*Math.pow(entropy_i, -1.0*Math.log10(agedRatio_i));
		//	index_j = -1.0*Math.pow(entropy_j, -1.0*Math.log10(agedRatio_j));
			
			curvature = (index_i - index_j) / Math.pow(distance, 2);		
		}

		return curvature;
	}
	
	public double calculateCurvature(int year, int district_i, int district_j, EntropyData eData, 
											double scale, double min, double weight, double lamdaA, double lamdaB){		
		double population_i = eData.getPopulation(year, district_i);
		double population_j = eData.getPopulation(year, district_j);
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
		double agedRatio_i = eData.getAgedIndex(year, district_i);
		double agedRatio_j = eData.getAgedIndex(year, district_j);
		double index_i, index_j;
		double distance = this.getDistance(district_i, district_j, eData, scale, min, lamdaA, lamdaB);
		double curvature;
		double logBase = Math.log(10);
		
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
			entropy_i = -1.0 * Math.log(entropy_i)/logBase; 
			entropy_j = -1.0 * Math.log(entropy_j)/logBase;
			agedRatio_i = -1.0 * Math.log(agedRatio_i)/logBase;
			agedRatio_j = -1.0 * Math.log(agedRatio_j)/logBase;

			index_i = Math.pow(entropy_i, weight) * Math.pow(Math.abs(agedRatio_i), 1.0-weight);
			if (agedRatio_i < 0) index_i *= -1.0;
			index_j = Math.pow(entropy_j, weight) * Math.pow(Math.abs(agedRatio_j), 1.0-weight);
			if (agedRatio_j < 0) index_j *= -1.0;
			
			curvature = (index_i - index_j) / Math.pow(distance, 2);		
		}

		return curvature;
	}

	public double calculateCurvature(int year, int district, EntropyData eData, 
															double lamdaA, double lamdaB){
		double curvature = 0.0;
						
		for(int i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district && eData.getPopulation(year, i) > 0) 
				curvature += this.calculateCurvature(year, district, i, eData, lamdaA, lamdaB) 
										* this.populationFunction(year, i, eData);
		return curvature;		
	}
	
	public double calculateCurvature(int year, int district, EntropyData eData, 
															double weight, double lamdaA, double lamdaB){
		double curvature = 0.0;

		for(int i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district && eData.getPopulation(year, i) > 0) 
				curvature += this.calculateCurvature(year, district, i, eData, weight, lamdaA, lamdaB) 
										* this.populationFunction(year, i, eData);
		return curvature;		
	}
	
	public double calculateCurvature(int year, int district, EntropyData eData, 
															double min, double weight, double lamdaA, double lamdaB){
		double curvature = 0.0;
		
		for(int i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district && eData.getPopulation(year, i) > 0) 
				curvature += this.calculateCurvature(year, district, i, eData, min, weight, lamdaA, lamdaB) 
										* this.populationFunction(year, i, eData);
		return curvature;		
	}
	
	public double calculateCurvature(int year, int district, EntropyData eData, 
											double scale, double min, double weight, double lamdaA, double lamdaB){
		double curvature = 0.0;
	
		for(int i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district && eData.getPopulation(year, i) > 0) 
				curvature += this.calculateCurvature(year,district,i,eData,scale,min,weight,lamdaA,lamdaB) 
										* this.populationFunction(year, i, eData);
		return curvature;		
	}
	
	public void calculateCurvature(int year, CurvatureData cData, EntropyData eData,
														double lamdaA, double lamdaB){
		int i;
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<district ; i++)
			cData.setCurvature(year, i, this.calculateCurvature(year, i, eData, lamdaA, lamdaB));
		
		/*** calculate curvature sum ***/
		tempSum = 0.0;
		for(i=0 ; i<district ; i++) 
			if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong)
				tempSum += cData.getCurvature(year, i);
		cData.setCurvatureSum(year, tempSum);
	}
	
	public void calculateCurvature(CurvatureData cData, EntropyData eData, 
														double[] lamdaA, double[] lamdaB){
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<district ; j++) 
				cData.setCurvature(i, j, this.calculateCurvature(i, j, eData, lamdaA[i], lamdaB[i]));
		
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
														double[] weight, double[] lamdaA, double[] lamdaB){
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<district ; j++) 
				cData.setCurvature(i, j, this.calculateCurvature(i, j, eData, weight[i], lamdaA[i], lamdaB[i]));
		
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
														double[] min, double[] weight, double[] lamdaA, double[] lamdaB){
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate curvature ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<district ; j++) 
				cData.setCurvature(i, j, this.calculateCurvature(i,j,eData,min[i],weight[i],lamdaA[i],lamdaB[i]));
		
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
								double[] scale, double[] min, double[] weight, double[] lamdaA, double[] lamdaB){
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
												lamdaA[i], lamdaB[i]));
		
		/*** calculate curvature sum ***/
		for(i=0 ; i<duration ; i++){
			tempSum = 0.0;
			for(j=0 ; j<district ; j++) 
				if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
					tempSum += cData.getCurvature(i, j);
			cData.setCurvatureSum(i, tempSum);
		}
	}

	public void calculatePressure(int year, CurvatureData cData, EntropyData eData,
													double lamdaA, double lamdaB){
		int district = cData.getDistricNumber();
		
		/*** calculate pressure ***/
		for(int i=0 ; i<district ; i++){
			cData.setCurvature(year, i, this.calculateCurvature(year, i, eData, lamdaA, lamdaB));
			if(eData.getPopulation(year, i) > 0) 
				cData.setPressure(year, i, cData.getCurvature(year, i) * this.populationFunction(year, i, eData));
			else cData.setPressure(year, i, 0.0);
		}	
		/*** calculate pressure sum ***/
		this.calculatePressureSum(year, cData);
	//	this.calculateResidualSum(year, eData, cData);
	}
	
	public void calculatePressure(int year, CurvatureData cData, EntropyData eData,
														double weight, double lamdaA, double lamdaB){
		int district = cData.getDistricNumber();
		
		/*** calculate pressure ***/
		for(int i=0 ; i<district ; i++){
			cData.setCurvature(year, i, this.calculateCurvature(year, i, eData, weight, lamdaA, lamdaB));
			if(eData.getPopulation(year, i) > 0) 
				cData.setPressure(year, i, cData.getCurvature(year, i) * this.populationFunction(year, i, eData));
			else cData.setPressure(year, i, 0.0);
		}	
		/*** calculate pressure sum ***/
		this.calculatePressureSum(year, cData);
	//	this.calculateResidualSum(year, eData, cData);
	}
	
	public void calculatePressure(int year, CurvatureData cData, EntropyData eData,
														double min, double weight, double lamdaA, double lamdaB){
		int district = cData.getDistricNumber();
		
		/*** calculate pressure ***/
		for(int i=0 ; i<district ; i++){
			if(eData.getPopulation(year, i) > 0){
				cData.setCurvature(year, i, this.calculateCurvature(year, i, eData, min, weight, lamdaA, lamdaB));
				cData.setPressure(year, i, cData.getCurvature(year, i) * this.populationFunction(year, i, eData));
			}else{
				cData.setCurvature(year, i, 0.0);
				cData.setPressure(year, i, 0.0);
			}
		}
		/*** calculate pressure sum ***/
		this.calculatePressureSum(year, cData);
	//	this.calculateResidualSum(year, eData, cData);
	}
	
	public void calculatePressure(int year, CurvatureData cData, EntropyData eData,
											double scale, double min, double weight, double lamdaA, double lamdaB){
		int district = cData.getDistricNumber();
		
		/*** calculate pressure ***/
		for(int i=0 ; i<district ; i++){
			cData.setCurvature(year, i, this.calculateCurvature(year, i, eData, scale, min, weight, lamdaA, lamdaB));
			if(eData.getPopulation(year, i) > 0) 
				cData.setPressure(year, i, cData.getCurvature(year, i) * this.populationFunction(year, i, eData));
			else cData.setPressure(year, i, 0.0);
		}
		/*** calculate pressure sum ***/
		this.calculatePressureSum(year, cData);
	//	this.calculateResidualSum(year, eData, cData);
	}

	public void calculatePressure(CurvatureData cData, EntropyData eData, 
													double[] lamdaA, double[] lamdaB){
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				cData.setCurvature(i, j, this.calculateCurvature(i, j, eData, lamdaA[i], lamdaB[i]));
				if(eData.getPopulation(i, j) > 0) 
					cData.setPressure(i, j, cData.getCurvature(i, j) * this.populationFunction(i, j, eData));
				else cData.setPressure(i, j, 0.0);	
			}
		}
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
	//	this.calculateResidualSum(eData, cData);
	}
		
	public void calculatePressure(CurvatureData cData, EntropyData eData, 
														double[] weight, double[] lamdaA, double[] lamdaB){	
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				cData.setCurvature(i, j, this.calculateCurvature(i, j, eData, weight[i], lamdaA[i], lamdaB[i]));
				if(eData.getPopulation(i, j) > 0) 
					cData.setPressure(i, j, cData.getCurvature(i, j) * this.populationFunction(i, j, eData));
				else cData.setPressure(i, j, 0.0);	
			}
		}
		
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
	//	this.calculateResidualSum(eData, cData);
	}
	
	public void calculatePressure(CurvatureData cData, EntropyData eData, 
														double[] min, double[] weight, double[] lamdaA, double[] lamdaB){	
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				cData.setCurvature(i, j, this.calculateCurvature(i, j, eData, min[i], weight[i], lamdaA[i], lamdaB[i]));
				if(eData.getPopulation(i, j) > 0) 
					cData.setPressure(i, j, cData.getCurvature(i, j) * this.populationFunction(i, j, eData));
				else cData.setPressure(i, j, 0.0);	
//				System.out.println(cData.getYear(i)+"\t"+cData.getDistrictName(j)+"\t"+pressure);
			}
		}
		
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
	//	this.calculateResidualSum(eData, cData);
	}
	
	public void calculatePressure(CurvatureData cData, EntropyData eData, 
								double[] scale, double[] min, double[] weight, double[] lamdaA, double[] lamdaB){	
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
				
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				cData.setCurvature(i, j, this.calculateCurvature(i,j,eData,scale[i],min[i],weight[i],lamdaA[i],lamdaB[i]));
				if(eData.getPopulation(i, j) > 0) 
					cData.setPressure(i, j, cData.getCurvature(i, j) * this.populationFunction(i, j, eData));
				else cData.setPressure(i, j, 0.0);	
			}
		}
				
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
	//	this.calculateResidualSum(eData, cData);
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
	//	this.calculateResidualSum(eData, cData);
	}
	
	public double calculateCurvatureSum(int year, CurvatureData cData){		
		int district = cData.getDistricNumber();		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		double tempSum;
		
		tempSum = 0.0;
		for(int i=0 ; i<district ; i++) 
			if(cData.getLatitude(i) >= minLat && cData.getLongitude(i) >= minLong)
				tempSum += cData.getCurvature(year, i);
		cData.setCurvatureSum(year, tempSum);
		
		return tempSum;
	}
	
	public void calculateCurvatureSum(CurvatureData cData){
		for(int i=0 ; i<cData.getNumberOfYears() ; i++) this.calculateCurvatureSum(i, cData);
	}
	
	public void calculatePressureSum(int year, CurvatureData cData){		
		int district = cData.getDistricNumber();		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		double tempSum;
		
		tempSum = 0.0;
		for(int i=0 ; i<district ; i++) 
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
														double[] lamdaA, double[] lamdaB){
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
					curvature = this.calculateCurvature(year, i, j, eData, lamdaA[year], lamdaB[year]);
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
	
	public void calculateInteraction(EntropyData eData, CurvatureData cData, 
													double[] min, double[] weight, double[] lamdaA, double[] lamdaB){
		int yr, i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();		
		double max;
	
		/*** calculate interaction ***/
		for(yr=0 ; yr<duration ; yr++)
			for(i=0 ; i<district ; i++)
				for(j=0 ; j<district ; j++)
					cData.setInteraction(yr, i, j, 
							this.calculateCurvature(yr, i, j, eData, min[yr], weight[yr], lamdaA[yr], lamdaB[yr])
							* this.populationFunction(yr, i, eData) * this.populationFunction(yr, j, eData) );
		
		/*** normalize interaction ***/
		for(yr=0 ; yr<duration ; yr++){
			max = 0;
			for(i=0 ; i<district ; i++)
				for(j=0 ; j<district ; j++) 
					if(max < Math.abs(cData.getInteraction(yr, i, j)))	max = cData.getInteraction(yr, i, j); 
	
			for(i=0 ; i<district ; i++)
				for(j=0 ; j<district ; j++) cData.setInteraction(yr, i, j, cData.getInteraction(yr, i, j)/max);
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

	public CurvatureData proceedCurvaturModel(EntropyData eData, double[] lamdaA, double[] lamdaB){
		
		CurvatureData cData;
		
		//curvature data composing
		cData = this.composeCurvatureData(eData);		
		//curvature calculating
		this.calculateCurvature(cData, eData, lamdaA, lamdaB);		
		//pressure calculating
		this.calculatePressure(cData, eData, lamdaA, lamdaB);
		
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

	public void proceedCurvaturModel(CurvatureData cData, EntropyData eData, double[] lamdaA, double[] lamdaB){
		//curvature calculating
		this.calculateCurvature(cData, eData, lamdaA, lamdaB);		
		//pressure calculating
		this.calculatePressure(cData, eData, lamdaA, lamdaB);
	}		
	
	public void proceedCurvaturModel(CurvatureData cData, EntropyData eData, 
																double[] weight, double[] lamdaA, double[] lamdaB){
		//curvature calculating
		this.calculateCurvature(cData, eData, weight, lamdaA, lamdaB);		
		//pressure calculating
		this.calculatePressure(cData, eData,  weight, lamdaA, lamdaB);
	}		
	
	public void proceedCurvaturModel(CurvatureData cData, EntropyData eData, 
																double[] min, double[] weight, double[] lamdaA, double[] lamdaB){
		//curvature calculating
		this.calculateCurvature(cData, eData, min, weight, lamdaA, lamdaB);		
		//pressure calculating
		this.calculatePressure(cData, eData, min, weight, lamdaA, lamdaB);
	}	
	
	public void proceedCurvaturModel(CurvatureData cData, EntropyData eData, 
								double[] scale, double[] min, double[] weight, double[] lamdaA, double[] lamdaB){
		//curvature calculating
		this.calculateCurvature(cData, eData, scale, min, weight, lamdaA, lamdaB);		
		//pressure calculating
		this.calculatePressure(cData, eData, scale, min, weight, lamdaA, lamdaB);
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
			
	//		pw.print("residual sum\t");
	//		for(i=0 ; i<duration ; i++) pw.print(cData.getResidualSum(i)+"\t");
	//		pw.println();		
			
			pw.close();
		}catch(IOException e) {}	

		
		System.out.println();
		System.out.print("sum   ");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f   ",cData.getPressureSum(i));
		System.out.println();
		System.out.print("RMSE   ");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f   ",cData.getRMSE(i));
		System.out.println();
	//	System.out.print("residual sum   ");
	//	for(i=0 ; i<duration ; i++) System.out.printf("%4.6f   ",cData.getResidualSum(i));
	//	System.out.println();
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
			
	//		pw.print("residual sum\t\t\t");
	//		for(i=0 ; i<duration ; i++) pw.print(cData.getResidualSum(i)+"\t");
	//		pw.println();

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
	
	public void proceedOptimizationGAprocessBasic(int rmseMode, CurvatureData cData, EntropyData eData, int iteration){
		int i, j, k;
		int duration = eData.getNumberOfYears();
		int variableNumber = 2;		//[0]:lamda A, [1]: lamda B
		int variableLength = 10;
		int  chromosomePopulation =100;
		double crossoverRate = 0.9;
		double mutationRate = 0.05;
		double[] minValues = new double[]{0.0, 0.1};
		double[] maxValues = new double[]{10.0, 1.0};
		ArrayList<double[]> variables;
		double convergence = -1.0;
		
		GeneticAlgorithm ga;
		Chromosome cs;
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		String inflowFile = filePath + "migration/migration_in_noIsland.txt";
		String outflowFile = filePath + "migration/migration_out_noIsland.txt";
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
			 
			for(j=0 ; j<=iteration ; j++){
				if(j>0) ga.nextGeneration(cs);
				variables = cs.getVariableValueList(minValues, maxValues);
				for(k=0 ; k<chromosomePopulation ; k++){
					cData.clearData(i);
					this.calculatePressure(i,cData,eData,variables.get(k)[0],variables.get(k)[1]);
					cs.setFunctionResult(k, mc.calculateRMSE(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, mc.calculateSqrtRMSE(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, 1.0/mc.calculateRsquare(rmseMode, cData.getYear(i), cData, eData)); 
				}
				convergence = ga.calculateConvergence(cs);
			}
			cs.sortAscendindOrder();
			
			cData.setLambdaA(i, cs.getVariableValues(0)[0]);
			cData.setLambdaB(i, cs.getVariableValues(0)[1]);
			cData.setRMSE(i, cs.getFunctionResult(0));
			
			int lowCut;
			if(cs.getPopulation() < 5) lowCut = cs.getPopulation(); else lowCut = 5;
			for(j=0 ; j<lowCut ; j++)
				System.out.println(eData.getYear(i)+"\t"
												+cs.getVariableValues(j)[0]+"\t"+cs.getVariableValues(j)[1]+"\t"
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
	
	public void proceedOptimizationGAprocessSimple(int rmseMode, CurvatureData cData, EntropyData eData, int iteration){
		int i, j, k;
		int duration = eData.getNumberOfYears();
		int variableNumber = 3;		//[0]: weight, [1]:lamda A, [2]: lamda B
		int variableLength = 10;
		int  chromosomePopulation =100;
		double crossoverRate = 0.9;
		double mutationRate = 0.05;
		double[] minValues = new double[]{0.0, 0.0, 0.1};
		double[] maxValues = new double[]{1.0, 10.0, 1.0};
		ArrayList<double[]> variables;
		double convergence = -1.0;
		
		GeneticAlgorithm ga;
		Chromosome cs;
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		String inflowFile = filePath + "migration/migration_in_noIsland.txt";
		String outflowFile = filePath + "migration/migration_out_noIsland.txt";
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
			 
			for(j=0 ; j<=iteration ; j++){
				if(j>0) ga.nextGeneration(cs);
				variables = cs.getVariableValueList(minValues, maxValues);
				for(k=0 ; k<chromosomePopulation ; k++){
					cData.clearData(i);
					this.calculatePressure(i,cData,eData,variables.get(k)[0],variables.get(k)[1],variables.get(k)[2]);
					cs.setFunctionResult(k, mc.calculateRMSE(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, mc.calculateSqrtRMSE(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, 1.0/mc.calculateRsquare(rmseMode, cData.getYear(i), cData, eData)); 
				}
				convergence = ga.calculateConvergence(cs);
			}
			cs.sortAscendindOrder();
			
			cData.setWeight(i, cs.getVariableValues(0)[0]);
			cData.setLambdaA(i, cs.getVariableValues(0)[1]);
			cData.setLambdaB(i, cs.getVariableValues(0)[2]);
			cData.setRMSE(i, cs.getFunctionResult(0));
			
			int lowCut;
			if(cs.getPopulation() < 5) lowCut = cs.getPopulation(); else lowCut = 5;
			for(j=0 ; j<lowCut ; j++)
				System.out.println(eData.getYear(i)+"\t"
												+cs.getVariableValues(j)[0]+"\t"+cs.getVariableValues(j)[1]+"\t"
												+cs.getVariableValues(j)[2]+"\t"
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
	
	public void proceedOptimizationGAprocessLimit(int rmseMode, CurvatureData cData, EntropyData eData, int iteration){
		int i, j, k;
		int duration = eData.getNumberOfYears();
		int variableNumber = 4;		//[0]: min, [1]: weight, [2]:lamda A, [3]: lamda B
//		int variableLength = 20;
		int[] variableLength = new int[]{20, 20, 20, 20};
		int  chromosomePopulation =100;
		double crossoverRate = 0.9;
		double mutationRate = 0.05;
		double[] minValues = new double[]{0.0, 0.0, 0.0, 0.0};
		double[] maxValues = new double[]{1.0, 2.0, 10.0, 2.0};
		ArrayList<double[]> variables;
		double convergence = -1.0;
		
		GeneticAlgorithm ga;
		Chromosome cs;
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
	//	String inflowFile = filePath + "migration/migration_in_withIsland.txt";
	//	String outflowFile = filePath + "migration/migration_out_withIsland.txt";
	//	String inflowFile = filePath + "migration/migration_in_noIsland.txt";
	//	String outflowFile = filePath + "migration/migration_out_noIsland.txt";
		String inflowFile = filePath + "migration/migration_in.txt";
		String outflowFile = filePath + "migration/migration_out.txt";
		MigrationCalculator mc = new MigrationCalculator();
		mc.readMigrationData(inflowFile, outflowFile);
		
		mc.normalizeMigration();
	//	mc.normalizeSymmetricNetMigration();
	//	mc.calculateShiftedMigration();
		
		//time variable
		long startTime, endTime;
		double operationTime, remainedTime;
		//checking start time
		startTime = System.currentTimeMillis();
		
		/*** optimization process ***/
		for(i=0 ; i<duration ; i++){
			ga = new GeneticAlgorithm(crossoverRate, mutationRate);
			cs = new Chromosome(chromosomePopulation, variableNumber, variableLength);
			 
			for(j=0 ; j<=iteration ; j++){
				if(j>0) ga.nextGeneration(cs);
				variables = cs.getVariableValueList(minValues, maxValues);
				for(k=0 ; k<chromosomePopulation ; k++){
					cData.clearData(i);
					this.calculatePressure(i, cData, eData, variables.get(k)[0], variables.get(k)[1],
							variables.get(k)[2], variables.get(k)[3]);
					cData.normalizePressure();
				//	cData.normalizeSymmetricPressure();
					cs.setFunctionResult(k, mc.calculateNormalizedRMSE(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, 1.0/mc.calculateNormalizedRsquare(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, mc.calculateRMSE(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, 1.0/mc.calculateRsquare(rmseMode, cData.getYear(i), cData, eData)); 
				//	System.out.println(cData.getYear(i)+"\t"+cData.getPressureSum(i)+"\t"+cs.getFunctionResult(k));
				}
				convergence = ga.calculateConvergence(cs);
			}
			cs.sortAscendindOrder();			
			
			cData.setMinimum(i, cs.getVariableValues(0)[0]);
			cData.setWeight(i, cs.getVariableValues(0)[1]);
			cData.setLambdaA(i, cs.getVariableValues(0)[2]);
			cData.setLambdaB(i, cs.getVariableValues(0)[3]);
			cData.setRMSE(i, cs.getFunctionResult(0));
			
			int lowCut;
			if(cs.getPopulation() < 5) lowCut = cs.getPopulation(); else lowCut = 5;
			for(j=0 ; j<lowCut ; j++)
				System.out.println(eData.getYear(i)+"\t"
						+cs.getVariableValues(j)[0]+"\t"+cs.getVariableValues(j)[1]+"\t"
						+cs.getVariableValues(j)[2]+"\t"+cs.getVariableValues(j)[3]+"\t"
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

		System.out.println("total operation time: "+(int)operationTime/3600+" hr. "+(int)(operationTime%3600)/60+" min. "+(int)operationTime%60+" sec.");
	}
	
	public void proceedOptimizationGAprocessMin(int rmseMode, CurvatureData cData, EntropyData eData, int iteration){
		int i, j, k;
		int duration = eData.getNumberOfYears();
		int variableNumber = 3;		//[0]: min, [1]:lamda A, [2]: lamda B
//		int variableLength = 20;
		int[] variableLength = new int[]{20, 20, 20};
		int  chromosomePopulation =100;
		double crossoverRate = 0.9;
		double mutationRate = 0.05;
		double[] minValues = new double[]{0.0, 0.0, 0.0};
		double[] maxValues = new double[]{1.0, 10.0, 2.0};
		ArrayList<double[]> variables;
		double convergence = -1.0;
		
		GeneticAlgorithm ga;
		Chromosome cs;
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
	//	String inflowFile = filePath + "migration/migration_in_withIsland.txt";
	//	String outflowFile = filePath + "migration/migration_out_withIsland.txt";
	//	String inflowFile = filePath + "migration/migration_in_noIsland.txt";
	//	String outflowFile = filePath + "migration/migration_out_noIsland.txt";
		String inflowFile = filePath + "migration/migration_in.txt";
		String outflowFile = filePath + "migration/migration_out.txt";
		MigrationCalculator mc = new MigrationCalculator();
		mc.readMigrationData(inflowFile, outflowFile);
		
		mc.normalizeMigration();
	//	mc.normalizeSymmetricNetMigration();
	//	mc.calculateShiftedMigration();
		
		//time variable
		long startTime, endTime;
		double operationTime, remainedTime;
		//checking start time
		startTime = System.currentTimeMillis();
		
		/*** optimization process ***/
		for(i=0 ; i<duration ; i++){
			ga = new GeneticAlgorithm(crossoverRate, mutationRate);
			cs = new Chromosome(chromosomePopulation, variableNumber, variableLength);
			 
			for(j=0 ; j<=iteration ; j++){
				if(j>0) ga.nextGeneration(cs);
				variables = cs.getVariableValueList(minValues, maxValues);
				for(k=0 ; k<chromosomePopulation ; k++){
					cData.clearData(i);
					this.calculatePressure(i,cData,eData,variables.get(k)[0],0.0,variables.get(k)[1],variables.get(k)[2]);
					cData.normalizePressure();
				//	cData.normalizeSymmetricPressure();
					cs.setFunctionResult(k, mc.calculateNormalizedRMSE(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, 1.0/mc.calculateNormalizedRsquare(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, mc.calculateRMSE(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, 1.0/mc.calculateRsquare(rmseMode, cData.getYear(i), cData, eData)); 
				//	System.out.println(cData.getYear(i)+"\t"+cData.getPressureSum(i)+"\t"+cs.getFunctionResult(k));
				}
				convergence = ga.calculateConvergence(cs);
			}
			cs.sortAscendindOrder();
			
			cData.setMinimum(i, cs.getVariableValues(0)[0]);
			cData.setWeight(i, 0.0);
			cData.setLambdaA(i, cs.getVariableValues(0)[1]);
			cData.setLambdaB(i, cs.getVariableValues(0)[2]);
			cData.setRMSE(i, cs.getFunctionResult(0));
			
			int lowCut;
			if(cs.getPopulation() < 5) lowCut = cs.getPopulation(); else lowCut = 5;
			for(j=0 ; j<lowCut ; j++)
				System.out.println(eData.getYear(i)+"\t"+cs.getVariableValues(j)[0]+"\t"+cs.getVariableValues(j)[1]+"\t"
												+cs.getVariableValues(j)[2]+"\t"+cs.getFunctionResult(j)+"\t"+convergence+"\t");
		
			//checking operation time	
			endTime = System.currentTimeMillis();
			operationTime = (double)(endTime - startTime)/1000.0;
			remainedTime = (double)(duration-i-1)/(i+1) * operationTime;
			this.printRemainedTime(remainedTime);
		}
		//checking operation time	
		endTime = System.currentTimeMillis();
		operationTime = (double)(endTime - startTime)/1000.0;

		System.out.println("total operation time: "+(int)operationTime/3600+" hr. "+(int)(operationTime%3600)/60+" min. "+(int)operationTime%60+" sec.");
	}
	

	
	public void proceedOptimizationGAprocess(int rmseMode, CurvatureData cData, EntropyData eData, int iteration){
		int i, j, k;
		int duration = eData.getNumberOfYears();
		int variableNumber = 5;		//[0]: scale, [1]: minimum,  [2]: weight, [3]:lamda A, [4]: lamda B
		int variableLength = 10;
	//	int[] variableLength = new int[]{2, 10, 10, 15, 15};
		int  chromosomePopulation =100;
		double crossoverRate = 0.9;
		double mutationRate = 0.05;
		double[] minValues = new double[]{1.0,		0.000000000001,		0.0,		0.0,		0.0};
		double[] maxValues = new double[]{1.0,		0.1,		1.0,		10.0,	2.0};
		ArrayList<double[]> variables;
		double convergence = -1.0;
		
		GeneticAlgorithm ga;
		Chromosome cs;
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		String inflowFile = filePath + "migration/migration_in_noIsland.txt";
		String outflowFile = filePath + "migration/migration_out_noIsland.txt";
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
			 
			for(j=0 ; j<=iteration ; j++){
				if(j>0) ga.nextGeneration(cs);
				variables = cs.getVariableValueList(minValues, maxValues);
				for(k=0 ; k<chromosomePopulation ; k++){
					cData.clearData(i);
					this.calculatePressure(i, cData, eData, variables.get(k)[0], variables.get(k)[1], 
															variables.get(k)[2], variables.get(k)[3], variables.get(k)[4]);
					cs.setFunctionResult(k, mc.calculateRMSE(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, mc.calculateSqrtRMSE(rmseMode, cData.getYear(i), cData, eData)); 
				//	cs.setFunctionResult(k, 1.0/mc.calculateRsquare(rmseMode, cData.getYear(i), cData, eData)); 
				//	System.out.println(eData.getYear(i)+"\t"+(j+1)+"\t"+(k+1)+"\t"+variables.get(k)[0]+"\t"+variables.get(k)[1]+"\t"+variables.get(k)[2]+"\t"+cs.getFunctionResult(k));
				}
				convergence = ga.calculateConvergence(cs);
			}
			cs.sortAscendindOrder();
			
			cData.setScale(i, cs.getVariableValues(0)[0]);
			cData.setMinimum(i, cs.getVariableValues(0)[1]);
			cData.setWeight(i, cs.getVariableValues(0)[2]);
			cData.setLambdaA(i, cs.getVariableValues(0)[3]);
			cData.setLambdaB(i, cs.getVariableValues(0)[4]);
			cData.setRMSE(i, cs.getFunctionResult(0));
			
			int lowCut;
			if(cs.getPopulation() < 5) lowCut = cs.getPopulation(); else lowCut = 5;
			for(j=0 ; j<lowCut ; j++)
				System.out.println(eData.getYear(i)+"\t"
												+cs.getVariableValues(j)[0]+"\t"+cs.getVariableValues(j)[1]+"\t"
												+cs.getVariableValues(j)[2]+"\t"+cs.getVariableValues(j)[3]+"\t"
												+cs.getVariableValues(j)[4]+"\t"
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

		System.out.println("total operation time: "+(int)operationTime/60+" min. "+operationTime%60+" sec.");
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
					distance = this.getDistance(j, k, eData);
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
					distance = this.getDistance(j, k, eData, cData.getLamdaA(i), cData.getLamdaB(i));
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
			System.err.print("read class lamda error\t");
		}
	}
	
	public void printLamdaAB(String outputFile, CurvatureData cData){
		int i;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			for(i=0 ; i<cData.getNumberOfYears() ; i++){
				pw.print(cData.getYear(i));
				pw.println("\tlamdaA\t"+cData.getLamdaA(i)+"\tlamdaB\t"+cData.getLamdaB(i));			
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
		}
	}	
	
	public void processLimit(int iteration, int rmseMode){
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		String outputPath = filePath + "all_age_curve/";
		String populationFile = filePath + "populatoin ID registered mid-year_test.txt";
	//	String populationFile = filePath + "population_registered_1995_2010.txt";
		String coordinatesFile = filePath + "coordinates_TM_noIsland.txt";
		String latLngCoordFile = filePath + "coordinates_WGS84_noIsland.txt";
		String lamdaFile = outputPath + "lamdaAB_GAoptimized_"+iteration+".txt";
		String pressureFile = outputPath + "Pressure_GAoptimized_"+iteration+".txt";
		String interactionFile = outputPath + "interaction/Interaction_GAoptimized_"+iteration+".txt";
		String graphFile = outputPath + "interaction/GraphGDF_GAoptimized_"+iteration+".gdf";
		String rmseFile = outputPath + "RMSE_"+iteration+".txt";
		
		String inflowFile = filePath + "migration/migration_in.txt";
		String outflowFile = filePath + "migration/migration_out.txt";

		PopulationData pData = new PopulationData();
		Coordinates points = new Coordinates();
		EntropyData eData = new EntropyData();
		EntropyData norm_eData = new EntropyData();
		CurvatureData cData;
		
		EntropyCalculator ec = new EntropyCalculator();
		DataReader dr = new DataReader();
		MapGenerator mg = new MapGenerator();
		GraphFormat gf = new GraphFormat();
		MigrationCalculator mc = new MigrationCalculator();
		
		System.out.print("data reading: ");
		pData = dr.getPopulationData(populationFile);
		System.out.println("complete");
		
		System.out.print("entropy calculating: ");
		ec.proceedEntropyCalculation(pData);
		System.out.println("complete");
				
		System.out.print("coordinates reading: ");
		mg.readCoordinates(coordinatesFile, points);
		eData = mg.composeEntropyData(points, pData);
		System.out.println("complete");
		
		System.out.print("entropy normalizing: ");
		norm_eData = mg.normalizeEntropyData(eData, 1.0, 0.1, 1.0, 0.0, 1.0, 0.0);
		System.out.println("complete");

		System.out.print("curvature data composing: ");
		cData = this.composeCurvatureData(norm_eData);
		System.out.println("complete");
		
		/*** friction function optimizing part ***/
		System.out.println("pressure optimizing: ");
		this.proceedOptimizationGAprocessLimit(rmseMode, cData, norm_eData, iteration);
		System.out.println("complete");
		
		System.out.print("class lamda printing: ");
		this.printModelProperties(lamdaFile, cData);
		System.out.println("complete");				
		
		
		/*** Potential assessment part ***/	
		System.out.print("curvature model processing: ");
		this.proceedCurvaturModel(cData, norm_eData, cData.getMinimumList(), cData.getWeightList(), 
													cData.getLamdaAList(), cData.getLamdaBList());
		cData.normalizePressure();
		mc.readMigrationData(inflowFile, outflowFile);
		mc.calculateRMSE(rmseMode, cData, eData);
		System.out.println("complete");			
		
		System.out.print("pressure data printing: ");
		this.printCurvatureAndPressure(pressureFile, cData);
		mc.printResults(rmseFile, cData, eData);
		System.out.println("complete");
		
		/*** Interaction calculating part ***/
		System.out.print("interaction calculating: ");
		this.calculateInteraction(norm_eData, cData, cData.getMinimumList(), cData.getWeightList(), cData.getLamdaAList(), cData.getLamdaBList());
		System.out.println("complete");			
		
		System.out.print("latlng coordinate reading: ");
		gf.readCoordinate(latLngCoordFile, cData);
		System.out.println("complete");
		
		System.out.print("interaction graph file making: ");
		gf.makeGDFFormatInteraction(graphFile, cData);
		System.out.println("complete");
		
		System.out.println();
		System.out.println("[iteration: "+iteration+"\trmse mode: "+rmseMode+"\tprocess complete]");
	}
	
	public void printResults(String outputFile, CurvatureData cData, EntropyData eData, EntropyData norm_eData){
		
		int i, j;
		int duration = cData.getNumberOfYears();
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.print("district\tlatitude\tlongitude");
			for(i=0 ; i<duration ; i++) {
				pw.print("\t"+cData.getYear(i)+"_population\t"+cData.getYear(i)+"_+1netmigration\t"+cData.getYear(i)+"_+5netmigrationpotential\t");
				pw.print("\t"+cData.getYear(i)+"employment\t"+cData.getYear(i)+"_+GRDP per cap\t"+cData.getYear(i));
				pw.print("\t"+cData.getYear(i)+"_entropy\t"+cData.getYear(i)+"_agedRatio\t"+cData.getYear(i)+"_potential\t"+cData.getYear(i)+"_force");
				pw.print("\t"+cData.getYear(i)+"_gravityPow\t"+cData.getYear(i)+"_gravityExp\t");
			}
				pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				if(cData.getLatitude(i)>cData.getMinLatitude() || cData.getLongitude(i)>cData.getMinLongitude()){
					pw.print(cData.getDistrictName(i));			
					for(j=0 ; j<duration ; j++) 
						pw.print("\t"+eData.getEntropy(j, i)+"\t"+eData.getAgedIndex(j, i)+"\t"
								+Math.pow(norm_eData.getEntropy(j, i), norm_eData.getAgedIndex(j, i))
								+"\t"+cData.getPressure(j, i));
					pw.println();
				}
			}
			pw.println();
					
			pw.close();
		}catch(IOException e) {}	
	}
	
	public static void main(String[] args) {
		
		int iteration = 1;
		int rmseMode = 0;		
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		String outputPath = filePath + "all_age_curve/";
	//	String outputPath = filePath + "traditional_gravity/";
	//	String populationFile = filePath + "population_region.txt";
	//	String populationFile = filePath + "population_registered_data.txt";
	//	String populationFile = filePath + "population_registered_1995_2015.txt";
	//	String populationFile = filePath + "population_census_1995_2015.txt";
		String populationFile = filePath + "population ID registered mid-year_test2.txt";
	//	String coordinatesFile = filePath + "coordinates_TM_withIsland.txt";
	//	String latLngCoordFile = filePath + "coordinates_WGS84_withIsland.txt";
		String coordinatesFile = filePath + "coordinates_TM_noIsland.txt";
		String latLngCoordFile = filePath + "coordinates_WGS84_noIsland.txt";
		String lamdaFile = outputPath + "lamdaAB_GAoptimized_"+iteration+".txt";
		String pressureFile = outputPath + "Pressure_GAoptimized_"+iteration+".txt";
		String traditionalFile = outputPath+"Traditional_exponential.txt";
		String interactionFile = outputPath + "interaction/Interaction_GAoptimized_"+iteration+".txt";
		String graphFile = outputPath + "interaction/GraphGDF_GAoptimized_"+iteration+".gdf";
		String rmseFile = outputPath + "RMSE_"+iteration+".txt";
		String resultFile = outputPath +"Results_"+iteration+".txt";
		
	//	String inflowFile = filePath + "migration/migration_in_noIsland.txt";
	//	String outflowFile = filePath + "migration/migration_out_noIsland.txt";
	//	String inflowFile = filePath + "migration/migration_in_withIsland.txt";
	//	String outflowFile = filePath + "migration/migration_out_withIsland.txt";
		String inflowFile = filePath + "migration/migration_in.txt";
		String outflowFile = filePath + "migration/migration_out.txt";
		
		PopulationData pData = new PopulationData();
		Coordinates points = new Coordinates();
		EntropyData eData = new EntropyData();
		EntropyData norm_eData = new EntropyData();
		CurvatureData cData;
		
		EntropyCalculator ec = new EntropyCalculator();
		DataReader dr = new DataReader();
		MapGenerator mg = new MapGenerator();
		CurvatureModel_Korea cm = new CurvatureModel_Korea();		
		GraphFormat gf = new GraphFormat();
		MigrationCalculator mc = new MigrationCalculator();

		
		System.out.print("data reading: ");
		pData = dr.getPopulationData(populationFile);
		//pData = dr.getData(populationFile);
		//dr.printData(outputPath+"population_readed.txt");
		System.out.println("complete");
		
		System.out.print("entropy calculating: ");
		ec.proceedEntropyCalculation(pData);
		System.out.println("complete");
				
		System.out.print("coordinates reading: ");
		mg.readCoordinates(coordinatesFile, points);
		eData = mg.composeEntropyData(points, pData);
		//eData.printData(outputPath+"entropy_result.txt");
		System.out.println("complete");
		
		System.out.print("entropy normalizing: ");
		norm_eData = mg.normalizeEntropyData(eData, 1.0, 0.1, 1.0, 0.0, 1.0, 0.0);
		//norm_eData.printData(outputPath+"entropy_normalized_result.txt");
		System.out.println("complete");

		System.out.print("curvature data composing: ");
		cData = cm.composeCurvatureData(norm_eData);
		System.out.println("complete");
		
		/*** friction function optimizing part ***/
		
		System.out.println("pressure optimizing: ");
	//	cm.proceedOptimizationGAprocessBasic(rmseMode, cData, norm_eData, pData, iteration);
	//	cm.proceedOptimizationGAprocessSimple(rmseMode, cData, norm_eData, pData, iteration);
		cm.proceedOptimizationGAprocessLimit(rmseMode, cData, norm_eData, iteration);
	//	cm.proceedOptimizationGAprocessMin(rmseMode, cData, norm_eData, iteration);
	//	cm.proceedOptimizationGAprocess(rmseMode, cData, norm_eData, pData, iteration);
		System.out.println("complete");

		//cm.checkDistance(norm_eData, cData);
		System.out.print("class lamda printing: ");
		cm.printModelProperties(lamdaFile, cData);
		System.out.println("complete");				
		
		
		/*** Potential assessment part ***/		
		/*
		System.out.print("lamda reading: ");
		cm.readlamdaAB(lamdaFile, cData);
		System.out.println("complete");		
		*/
		
		System.out.print("curvature model processing: ");
	//	cm.proceedCurvaturModel(cData, norm_eData, cData.getLamdaAList(), cData.getLamdaBList());
	//	cm.proceedCurvaturModel(cData, norm_eData, cData.getWeightList(), 
	//												cData.getLamdaAList(), cData.getLamdaBList());
		cm.proceedCurvaturModel(cData, norm_eData, cData.getMinimumList(), cData.getWeightList(), 
													cData.getLamdaAList(), cData.getLamdaBList());
	//	cm.proceedCurvaturModel(cData, norm_eData, cData.getScaleList(), cData.getMinimumList(), 
	//												cData.getWeightList(), cData.getLamdaAList(), cData.getLamdaBList());
		mc.readMigrationData(inflowFile, outflowFile);
		cData.normalizePressure();
	//	cData.normalizeSymmetricPressure();
	//	mc.normalizeMigration();
	//	mc.normalizeSymmetricNetMigration();
		mc.calculateRMSE(rmseMode, cData, eData);
	//	mc.calculateShiftedMigration();
	//	mc.calculateAvgRMSE(rmseMode, cData, pData);	
		mc.calculateRsquare(rmseMode, cData, norm_eData);
		System.out.println("complete");			
		
		System.out.print("pressure data printing: ");
		cm.printCurvatureAndPressure(pressureFile, cData);
		cm.printResults(resultFile, cData, eData, norm_eData);
		mc.printResults(rmseFile, cData, eData);
		mc.printCurrentResults(rmseFile.replace(".txt", "_current.txt"), cData, eData);
	//	mc.printNormalizedResults(rmseFile.replace(".txt", "_normalized.txt"), cData, eData);
		System.out.println("complete");
		
		
		System.out.print("pressure data printing: ");
		cm.printCurvatureAndPressure(pressureFile, cData);
		System.out.println("complete");
		
		
		/*
		System.out.print("gravity model processing: ");
		cm.proceedTraditionalGravityModel(cData, norm_eData, 1);
		cData.normalizePressure();
		mc.readMigrationData(inflowFile, outflowFile);
		mc.printResults(rmseFile, cData, eData);
		cm.printCurvatureAndPressure(traditionalFile, cData);
		System.out.println("complete");			
		*/
		
		/*** Interaction calculating part ***/
		
		System.out.print("interaction calculating: ");
	//	cm.calculateInteraction(norm_eData, cData, cData.getLamdaAList(), cData.getLamdaBList());
		cm.calculateInteraction(norm_eData, cData, cData.getMinimumList(), cData.getWeightList(), cData.getLamdaAList(), cData.getLamdaBList());
		System.out.println("complete");			
		
		System.out.print("interaction data printing: ");
	//	cm.printInteraction(interactionFile, cData);
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

}
