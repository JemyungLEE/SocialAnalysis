package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import populationEntropy.data.*;

public class CurvatureModel_Japan {

	double rambdaA, rambdaB;
	
	public CurvatureModel_Japan(){
		
	}
	
	
	public double populationFunction(int year, int district, EntropyData eData){
	
		double population;
						
		/*** get population ***/
		population = eData.getPopulation(year, district);			
		
		/*** get normalized population ***/ 
		if(population>=0) population /= eData.getMaxPopulation(year);	
		else population = 0.0;
		
		/*** return population value ***/
		return population;
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
		}
		
		/***return curvature ***/
		return curvature;
	}

	public double calculateCurvature(int year,int district,EntropyData eData,double rambdaA,double rambdaB){
		int i;
		double curvature = 0.0;
						
		for(i=0 ; i<eData.getDistricNumber() ; i++)
			curvature += this.calculateCurvature(year, district, i, eData, rambdaA, rambdaB)
									* this.populationFunction(year, i, eData);
				
	//	if(Double.isNaN(curvature)) System.out.println(eData.getYear(year)+"\t"+eData.getDistrictName(district)+"curvature NaN");
		
		return curvature;		
	}
	
	public double calculatePressure(int year, int district, EntropyData eData, double rambdaA, double rambdaB){
		
		return this.calculateCurvature(year, district, eData, rambdaA, rambdaB)
						* this.populationFunction(year, district, eData);		
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
	
	public void calculateCurvature(CurvatureData cData, EntropyData eData, double[] rambdaA, double[] rambdaB){
		
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

	public void calculatePressure(CurvatureData cData, EntropyData eData, double[] rambdaA, double[] rambdaB){

		int i, j;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double pressure;
		
		/*** calculate pressure ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<district ; j++){
				pressure = this.calculateCurvature(i, j, eData, rambdaA[i], rambdaB[i]) 
									* this.populationFunction(i, j, eData);
				cData.setPressure(i, j, pressure);
			}
		}
		
		/*** calculate pressure sum ***/
		this.calculatePressureSum(cData);
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
	}
	
	public void calculatePressureSum(CurvatureData cData){		
		int i, j;
		int duration = cData.getNumberOfYears();
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
	
	public void calculateInteraction(EntropyData eData, CurvatureData cData, double[] rambdaA, double[] rambdaB){
		
		int i, j;
		int year;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();		
		double curvature, interaction;
		double max;
		
		//calculateCurvature(int year, int district_i, int district_j, EntropyData eData,
		//		double rambdaA, double rambdaB){
		
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
					if(max < Math.abs(interaction))	max = Math.abs(interaction); 
				}
			}

			for(i=0 ; i<district ; i++)
				for(j=0 ; j<district ; j++) cData.setInteraction(year, i, j, cData.getInteraction(year, i, j)/max);
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

	public void proceedCurvaturModel(CurvatureData cData, EntropyData eData, double[] rambdaA, double[] rambdaB){
		
		//curvature calculating
		this.calculateCurvature(cData, eData, rambdaA, rambdaB);		
		//pressure calculating
		this.calculatePressure(cData, eData, rambdaA, rambdaB);
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
			for(i=0 ; i<cData.getNumberOfYears() ; i++) pw.print(cData.getYear(i) + "\t");
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
			for(i=0 ; i<duration ; i++) pw.print(cData.getPressureSum(i)+"\t");
			pw.println();			
			
			pw.close();
		}catch(IOException e) {}	

		
		System.out.println();
		System.out.print("sum   ");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f   ",cData.getPressureSum(i));
		System.out.println();
	}
	
	
	public void printCurvatureAndPressure(String outputFile, CurvatureData cData, EntropyData eData,
																	DataReader_Japan drj){
		
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
			pw.print("code\tdistrict\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getYear(i) + "\t");
			pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				pw.print(drj.standardCode.get(i)+"\t");
				pw.print(cData.getDistrictName(i)+"\t");			
				for(j=0 ; j<duration ; j++) pw.print(cData.getCurvature(j, i)+"\t");
				pw.println();
			}
			pw.print("sum\t\t\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getCurvatureSum(i)+"\t");
			pw.println();
			pw.println();
			
			pw.println("pressure:");
			pw.print("code\tdistrict\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getYear(i) + "\t");
			pw.println();
			
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				pw.print(drj.standardCode.get(i)+"\t");
				pw.print(cData.getDistrictName(i)+"\t");			
				for(j=0 ; j<duration ; j++) pw.print(cData.getPressure(j, i)+"\t");
				pw.println();
			}
			pw.print("sum\t\t\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getPressureSum(i)+"\t");
			pw.println();

			pw.println("population:");
			pw.print("code\tdistrict\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getYear(i) + "\t");	
			pw.println();
			
			for(i=0 ; i<eData.getDistricNumber() ; i++){
				pw.print(drj.standardCode.get(i)+"\t");
				pw.print(eData.getDistrictName(i)+"\t");
				for(j=0 ; j<duration ; j++) pw.print(eData.getPopulation(j, i)+"\t");
				pw.println();
			}

			pw.println("entropy:");
			pw.print("code\tdistrict\t");
			for(i=0 ; i<duration ; i++) pw.print(cData.getYear(i) + "\t");			
			pw.println();
			
			for(i=0 ; i<eData.getDistricNumber() ; i++){
				pw.print(drj.standardCode.get(i)+"\t");
				pw.print(eData.getDistrictName(i)+"\t");
				for(j=0 ; j<duration ; j++) pw.print(eData.getEntropy(j, i)+"\t");
				pw.println();
			}
			
			pw.close();
		}catch(IOException e) {}	
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
		double xB_low =  0.0;
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
			
			System.out.print(eData.getYear(i)+"\tx1A:\t"+x1A+"\tx1B:\t"+x1B+"\tfx1:\t"+fx1
															  +"\tx2A:\t"+x2A+"\tx2B:\t"+x2B+"\tfx2:\t"+fx2+"\t");
			//checking operation time	
			endTime = System.currentTimeMillis();
			operationTime = (double)(endTime - startTime) / 1000.0;
			remainedTime = (double)(duration-i-1)/(i+1) * operationTime;
			this.printRemainedTime(remainedTime);
		}					
		
		//checking operation time	
		endTime = System.currentTimeMillis();
		operationTime = (double)(endTime - startTime)/1000.0;
		
		System.out.println("operation time: "+operationTime);
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
	
	public void readRambdaAB(String inputFile, CurvatureData cData){
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
	
	public void printRambdaAB(String outputFile, CurvatureData cData){
		int i;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			for(i=0 ; i<cData.getNumberOfYears() ; i++){
				pw.print(cData.getYear(i)+"\trambdaA\t");
				pw.println(cData.getRambda(i)+"\trambdaB\t"+cData.getRambdaB(i));			
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
				File file = new File(outputFile.replace(".txt", "_"+(cData.getYear(year))+".txt"));
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
	
		int iter	= 30;
		int iterAB	= 10;
				
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/Japan/";
		String outputPath = filePath+ "output/";
		String coordinatesFile	= filePath + "coordinates_TM.txt";
		String latLngCoordFile	= filePath + "coordinates.txt";
		String rambdaFile		= outputPath + "RambdaAB_region_"+iter+"_"+iterAB+".txt";
		String curvatureFile		= outputPath + "Curvature_RambdaAB_region_"+iter+"_"+iterAB+".txt";
		String interactionFile	= outputPath + "interaction/Interaction_region_"+iter+"_"+iterAB+".txt";
		String graphFile		= outputPath + "interaction/GraphGDF_"+iter+"_"+iterAB+".txt";
		
		String stdClassFile = filePath+"standard_class.txt";
		String stdRegionFile = filePath+"standard_region.txt";
		int[] years = {2000, 2005, 2010};
		String[] populationFile = new String[years.length];
		for(int i=0 ; i<years.length ; i++) populationFile[i] = filePath + "population_japan_"+years[i]+".txt";
		String outputFile = filePath + "population_japan_output.txt";
		
		PopulationData pData = new PopulationData();
		Coordinates points = new Coordinates();
		EntropyData eData = new EntropyData();
		EntropyData norm_eData = new EntropyData();
		CurvatureData cData;
		
		EntropyCalculator ec = new EntropyCalculator();
		DataReader_Japan drj = new DataReader_Japan();
		MapGenerator mg = new MapGenerator();
		CurvatureModel_Japan cm = new CurvatureModel_Japan();		
		GraphFormat gf = new GraphFormat();
		
		
		System.out.print("data reading: ");
		drj.readStandardRegionInformation(stdClassFile, stdRegionFile);
		drj.makeMergingList(stdRegionFile);
		pData = drj.readPopulationData(years, populationFile);
		System.out.println("complete");
		
		System.out.print("entropy calculating: ");
		ec.calculateAgedIndex(pData);
		ec.calculateProbability(pData);
		ec.calculateEntropy(pData);
		System.out.println("complete");
				
		System.out.print("coordinates reading: ");
		mg.readCoordinates(coordinatesFile, drj, points);
		eData = mg.composeEntropyData(points, pData);
		eData.printData(outputPath+"entropy_result.txt");
		System.out.println("complete");
		
		System.out.print("entropy normalizing: ");
		norm_eData = mg.normalizeEntropyData(eData, 0.9, 0.1, 1.0, 0.0, 1.0, 0.0);
		norm_eData.printData(outputPath+"entropy_normalized_result.txt");
		System.out.println("complete");

		System.out.print("curvature data composing: ");
		cData = cm.composeCurvatureData(norm_eData);
		System.out.println("complete");
		
		/*** friction function optimizing part ***/
		
		System.out.print("pressure optimizing: ");
		cm.optimizeDistanceConstant(cData, norm_eData, iter, iter, iterAB);
		System.out.println("complete");
		
		
		System.out.print("class rambda printing: ");
		cm.printRambdaAB(rambdaFile, cData);
		System.out.println("complete");				
		
		
		/*** Potential assessment part ***/		
		
		System.out.print("rambda reading: ");
		cm.readRambdaAB(rambdaFile, cData);
		System.out.println("complete");		
		
		System.out.print("curvature model processing: ");
		cm.proceedInteractionModel(cData, norm_eData, cData.lamdaA, cData.lamdaB);
		System.out.println("complete");			
		
		System.out.print("pressure data printing: ");
		cm.printCurvatureAndPressure(curvatureFile, cData, eData, drj);
		System.out.println("complete");
		
		
		/*** Interaction calculating part ***/
		
		System.out.print("interaction calculating: ");
		cm.simulateInteraction(norm_eData, cData, cData.lamdaA, cData.lamdaB);
		System.out.println("complete");			
		
		System.out.print("interaction data printing: ");
		cm.printInteraction(interactionFile, cData);
		System.out.println("complete");
		
		
		System.out.print("latlng coordinate reading: ");
		gf.readCoordinate(latLngCoordFile, cData, drj);
		System.out.println("complete");

		System.out.print("interaction graph file making: ");
		gf.makeGDFFormatInteraction(graphFile, cData);
		System.out.println("complete");
				
		System.out.println();
		System.out.println("curvature file: "+ curvatureFile);
		System.out.println("rambda file: "+ rambdaFile);
		System.out.println("interaction file: "+interactionFile);
		System.out.println("graph file: "+graphFile);
		
		
		System.out.println();
		System.out.println("[process complete]");
	}

}
