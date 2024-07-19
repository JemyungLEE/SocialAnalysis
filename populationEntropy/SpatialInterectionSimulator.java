package spatialInteraction;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import spatialInteraction.data.EntropyData;
import spatialInteraction.data.InteractionData;

public class SpatialInterectionSimulator {
	/**
	 *  Subject: Spatial Interaction Simulator
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2015.7.12
	 *  Last Modified Data: 2017.12.5 
	 *  Affiliation:	Lab of Rural Planning, 
	 *						Division of Environmental Science and Technology, 
	 *						Graduate School of Agriculture,
	 *						Kyoto University
	 *  Description: Simulate spatial interactions and evaluate population attraction forces 
	 *  					 analyzing geographic distribution of aged-child ratio and entropy of age distribution
	 */
	
	double lamdaA, lamdaB;
	
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
	
	public double getDistance(int district_i, int district_j, EntropyData eData, double min, double lamdaA, double lamdaB){
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
			distance = 1.0 / (1.0 + Math.pow(10, lamdaA* (lamdaB - distance)));
			distance = distance * (1.0 - min) + min;
			return distance;
		}else return -1.0;
	}

	public double calculateAgeStructureIndex(int year, int district_i, int district_j, EntropyData eData, 
															double min, double weight, double lamdaA, double lamdaB){		
		double population_i = eData.getPopulation(year, district_i);
		double population_j = eData.getPopulation(year, district_j);
		double entropy_i = eData.getEntropy(year, district_i); 
		double entropy_j = eData.getEntropy(year, district_j);
		double agedRatio_i = eData.getAgedIndex(year, district_i);
		double agedRatio_j = eData.getAgedIndex(year, district_j);
		double distance = this.distanceSigmoidFunction(district_i, district_j, eData, min, lamdaA, lamdaB);
		double ageStructure;
		double index_i, index_j;
		double logBase = Math.log(10);
		
		/*** calculate AgeStructureIndex ***/
		if(district_i == district_j) ageStructure = 0.0;
		else if(distance <= 0) ageStructure = 0.0;
		else if(population_i <= 0 || population_j <= 0) ageStructure = 0.0;
		else if (entropy_i <= 0 || entropy_j <=0 || agedRatio_i < 0 || agedRatio_j < 0) ageStructure = 0.0;
		else if(agedRatio_i == 0 || agedRatio_j == 0){
			System.err.println("Zero 65+/15- ratio.");
			ageStructure = (Double) null;
		}
		else{
			index_i = Math.pow(entropy_i, Math.pow(agedRatio_i, weight));
			index_j = Math.pow(entropy_j, Math.pow(agedRatio_j, weight));
			ageStructure = (index_i - index_j) / Math.pow(distance, 2);		
		}

		return ageStructure;
	}
	
	public double calculateAgeStructureIndex(int year, int district, EntropyData eData, 
															double min, double weight, double lamdaA, double lamdaB){
		double ageStructure = 0.0;
		
		for(int i=0 ; i<eData.getDistricNumber() ; i++)
			if(i != district && eData.getPopulation(year, i) > 0) 
				ageStructure += this.calculateAgeStructureIndex(year, district, i, eData, min, weight, lamdaA, lamdaB) 
										* this.populationFunction(year, i, eData);
		return ageStructure;		
	}
	
	public void calculateAgeStructureIndex(InteractionData cData, EntropyData eData, 
														double[] min, double[] weight, double[] lamdaA, double[] lamdaB){
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double tempSum;
		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		
		/*** calculate ageStructure ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<district ; j++) 
				cData.setAgeStructureIndex(i, j, this.calculateAgeStructureIndex(i,j,eData,min[i],weight[i],lamdaA[i],lamdaB[i]));
		
		/*** calculate ageStructure sum ***/
		for(i=0 ; i<duration ; i++){
			tempSum = 0.0;
			for(j=0 ; j<district ; j++) 
				if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong)
					tempSum += cData.getAgeStructureIndex(i, j);
			cData.setAgeStructureIndexSum(i, tempSum);
		}
	}
	
	public void calculateForce(int year, InteractionData cData, EntropyData eData,
														double min, double weight, double lamdaA, double lamdaB){
		int district = cData.getDistricNumber();
		
		/*** calculate force ***/
		for(int i=0 ; i<district ; i++){
			if(eData.getPopulation(year, i) > 0){
				cData.setAgeStructureIndex(year, i, this.calculateAgeStructureIndex(year, i, eData, min, weight, lamdaA, lamdaB));
				cData.setForce(year, i, cData.getAgeStructureIndex(year, i) * this.populationFunction(year, i, eData));
			}else{
				cData.setAgeStructureIndex(year, i, 0.0);
				cData.setForce(year, i, 0.0);
			}
		}
	}
	
	public void calculateForce(InteractionData cData, EntropyData eData, 
														double[] min, double[] weight, double[] lamdaA, double[] lamdaB){	
		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		
		/*** calculate force ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				cData.setAgeStructureIndex(i, j, this.calculateAgeStructureIndex(i, j, eData, min[i], weight[i], lamdaA[i], lamdaB[i]));
				if(eData.getPopulation(i, j) > 0) 
					cData.setForce(i, j, cData.getAgeStructureIndex(i, j) * this.populationFunction(i, j, eData));
				else cData.setForce(i, j, 0.0);	
			}
		}
	}
	
	public void calculateForceSum(int year, InteractionData cData){		
		int district = cData.getDistricNumber();		
		double minLat = cData.getMinLatitude();
		double minLong = cData.getMinLongitude();
		double tempSum;
		
		tempSum = 0.0;
		for(int i=0 ; i<district ; i++) 
			if(cData.getLatitude(i) >= minLat && cData.getLongitude(i) >= minLong)
				tempSum += cData.getForce(year, i);
		cData.setForceSum(year, tempSum);
	}
	
	public void simulateInteraction(EntropyData eData, InteractionData cData, 
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
							this.calculateAgeStructureIndex(yr, i, j, eData, min[yr], weight[yr], lamdaA[yr], lamdaB[yr])
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
	
	public void proceedInteractionModel(InteractionData cData, EntropyData eData, 
																double[] min, double[] weight, double[] lamdaA, double[] lamdaB){
		//age structure calculation
		this.calculateAgeStructureIndex(cData, eData, min, weight, lamdaA, lamdaB);		
		//force calculation
		this.calculateForce(cData, eData, min, weight, lamdaA, lamdaB);
	}	
	
	public void proceedOptimizationGAprocess(int rmseMode, InteractionData cData, EntropyData eData, int iteration){
		int i, j, k;
		int duration = eData.getNumberOfYears();
		int variableNumber = 4;		//[0]: min, [1]: weight, [2]:lamda A, [3]: lamda B
		int[] variableLength = new int[]{20, 20, 20, 20};
		int  chromosomePopulation =100;
		double crossoverRate = 0.9;
		double mutationRate = 0.05;
		ArrayList<double[]> variables;
		double convergence = -1.0;
		
		GeneticAlgorithm ga;
		Chromosome cs;
		
		/*** optimization process ***/
		for(i=0 ; i<duration ; i++){
			ga = new GeneticAlgorithm(crossoverRate, mutationRate);
			cs = new Chromosome(chromosomePopulation, variableNumber, variableLength);
			 
			for(j=0 ; j<=iteration ; j++){
				if(j>0) ga.nextGeneration(cs);
				variables = cs.getVariableValueList();
				for(k=0 ; k<chromosomePopulation ; k++){
					cData.clearData(i);
					this.calculateForce(i, cData, eData, variables.get(k)[0], variables.get(k)[1],
							variables.get(k)[2], variables.get(k)[3]);
					cs.setFunctionResult(k, mc.calculateRMSD(rmseMode, cData.getYear(i), cData, eData));
				}
				convergence = ga.calculateConvergence(cs);
			}
			cs.sortAscendindOrder();			
			
			cData.setMinimum(i, cs.getVariableValues(0)[0]);
			cData.setWeightA(i, cs.getVariableValues(0)[1]);
			cData.setLambdaA(i, cs.getVariableValues(0)[2]);
			cData.setLambdaB(i, cs.getVariableValues(0)[3]);
			cData.setRMSD(i, cs.getFunctionResult(0));
		}
	}
}
