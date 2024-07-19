package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import populationEntropy.data.Chromosome;
import populationEntropy.data.Coordinates;
import populationEntropy.data.PopulationData;
import populationEntropy.data.EntropyData;
import populationEntropy.data.InteractionData;

public class MigrationModel {
	
	double maxDistance, minDistance, avgDistance, nDistance;
	String maxRegionA, maxRegionB, minRegionA, minRegionB;
	
	public MigrationModel(){
		
	}
	
	public double populationFunction(int year, int district, EntropyData eData){
		double population;
						
		/*** get population ***/
		population = (double) eData.getPopulation(year, district);	
		
		/*** normalize population ***/ 
		population /= (double) eData.getMaxPopulation(year);	
		
		/*** return population value ***/
		return population;
	}

	public double calculateDistance(int district_i, int district_j, EntropyData eData){
		/*** Note: distance should be normalized ***/		
		return Math.sqrt((Math.pow((eData.getLatitude(district_i) - eData.getLatitude(district_j)), 2)
										+ Math.pow((eData.getLongitude(district_i) - eData.getLongitude(district_j)), 2)));
	}
	
	public void calculateDistance(EntropyData eData, InteractionData iData){
		int i, j;
		int districts = iData.getDistricNumber();
		
		for(i=0 ; i<districts ; i++){
			for(j=0 ; j<districts ; j++){
				if(i==j) iData.setDistance(i, j, 0.0);
				else iData.setDistance(i, j, this.calculateDistance(i, j, eData));
			}
		}
		
	}
	
	public double distanceSigmoidFunction(int district_i, int district_j, EntropyData eData, InteractionData iData,
																	double min, double lamdaA, double lamdaB){
		/*** Note: distance should be normalized ***/
		double distance = iData.getDistance(district_i, district_j);
		double lat_i = eData.getLatitude(district_i);
		double long_i = eData.getLongitude(district_i);
		double lat_j = eData.getLatitude(district_j);
		double long_j = eData.getLongitude(district_j);
		double minLatitude = eData.getMinLatitude();
		double minLongitude = eData.getMinLongitude();
		
		if(lat_i >= minLatitude && long_i >= minLongitude && lat_j >= minLatitude && long_j >= minLongitude)
			return (1.0 / (1.0 + Math.pow(10, lamdaA* (lamdaB - distance)))) * (1.0 - min) + min;
		else return -1.0;
	}
	
	public double distancePowerFuction(int district_i, int district_j, EntropyData eData){
		/*** Note: distance should be normalized ***/		
		return Math.pow(this.calculateDistance(district_i, district_j, eData), 2);
	}
	
	public double distanceExpFuction(int district_i, int district_j, EntropyData eData){
		/*** Note: distance should be normalized ***/		
		return Math.exp(this.calculateDistance(district_i, district_j, eData));
	}
	
	public void calculateDistanceIndex(int year, double min, double lamdaA, double lamdaB, double weightD, 
																EntropyData eData, InteractionData iData){
		int i, j;
		int districts = iData.getDistricNumber();
		double distance;
		
		for(i=0 ; i<districts ; i++){
			for(j=0 ; j<districts ; j++){
				if(i==j) iData.setDistanceIndex(year, i, j, 0.0);
				else{
					distance = this.distanceSigmoidFunction(i, j, eData, iData, min, lamdaA, lamdaB);
					iData.setDistanceIndex(year, i, j, Math.pow(distance, weightD));
				}
			}
		}
	}
	
	public void calculateDistanceIndex(double[] min, double[] lamdaA, double[] lamdaB, double[] weightD, 
																EntropyData eData, InteractionData iData){
		int i, j, k;
		int years = iData.getNumberOfYears();
		int districts = iData.getDistricNumber();
		double distance;

		for(i=0 ; i<years ; i++){
			for(j=0 ; j<districts ; j++){
				for(k=0 ; k<districts ; k++){
					if(j==k) iData.setDistanceIndex(i, j, k, 0.0);
					else{
						distance = this.distanceSigmoidFunction(j, k, eData, iData, min[i], lamdaA[i], lamdaB[i]);
						iData.setDistanceIndex(i, j, k, Math.pow(distance, weightD[i]));
					}
				}
			}
		}
	}
	
	public void calculateAgeStructureIndex(int mode, int year, double weightA, double weightB, EntropyData eData, InteractionData iData){
		int districts = eData.getDistricNumber();
		
		for(int i=0 ; i<districts ; i++)
			iData.setAgeStructureIndex(year, i, this.calculateAgeStructureIndex(mode, year, i, weightA, weightB, eData));
	}
	
	public double calculateAgeStructureIndex(int mode, int year, int district, double weightA, double weightB
																		, EntropyData eData){
		/*** mode:	[0] H_normalized^(Aged_ratio^weight)
		 * 					[1] log10(H)^(Aged_ratio^weight)
		 * 					[2] 1/(H_normalized^weight_A * Aged_ratio^weight_B)
		 */
		
		double entropy= eData.getEntropy(year, district); 
		double agedRatio = eData.getAgedIndex(year, district);
		double index;
		
		/*** calculate curvature ***/
		if(entropy <= 0 || agedRatio < 0) index = 0.0;
		else if(agedRatio == 0 || Double.isInfinite(agedRatio)){
			System.err.println("Zero 65+/15- ratio error.");
			index = 0.0;
		}else if(mode == 0) index = Math.pow(entropy, Math.pow(agedRatio, weightA));
		else if(mode == 1) index = Math.pow(Math.log10(entropy), Math.pow(agedRatio, weightA));
		else if(mode == 2) index = Math.pow(entropy, weightA) * Math.pow(agedRatio, weightB);
		else index = 0.0;	
		
		/*
		double logBase = Math.log(10);
		entropy = -1.0 * Math.log(entropy)/logBase; 
		agedRatio = -1.0 * Math.log(agedRatio)/logBase;
		index = Math.pow(entropy, weight) * Math.pow(Math.abs(agedRatio), 1.0-weight);
		if (agedRatio < 0) index *= -1.0;
		*/
		
	//	index = Math.pow(entropy, weight*agedRatio);
		
	//	index = -1.0*Math.pow(entropy, -1.0*Math.log10(agedRatio));
		
		return index;
	}
	
	public void  normalizeAgeStructureIndex(InteractionData iData){
		int years = iData.getNumberOfYears();
		
		for(int i=0 ; i<years ; i++)
			this.normalizeAgeStructureIndex(i, iData);
	}
	
	public void normalizeAgeStructureIndex(int year, InteractionData iData){
		double max = 1.0;
		double min = 0.1;
		
		this.normalizeAgeStructureIndex(year, max, min, iData);
	}
	
	public void normalizeAgeStructureIndex(int year, double max, double min, InteractionData iData){
		int i;
		int districts = iData.getDistricNumber();

		double minIndex, maxIndex, transform;
		
		maxIndex = 0.0;
		for(i=0 ; i<districts ; i++)
			if(maxIndex < iData.getAgeStructureIndex(year, i)) 
				maxIndex = iData.getAgeStructureIndex(year, i);
		minIndex = maxIndex;
		for(i=0 ; i<districts ; i++)
			if(iData.getAgeStructureIndex(year, i) > 0 && minIndex > iData.getAgeStructureIndex(year, i)) 
				minIndex = iData.getAgeStructureIndex(year, i);
		transform = (max - min) / (maxIndex - minIndex);
		for(i=0 ; i<districts ; i++)
			if(iData.getAgeStructureIndex(year, i) > 0)
				iData.setAgeStructureIndex(year, i, (iData.getAgeStructureIndex(year, i) - minIndex) * transform + min);
	}
	
	public void calculateMigration(int year, int district_i, int district_j, EntropyData eData, InteractionData iData){		
		double minLat = iData.getMinLatitude();
		double minLong = iData.getMinLongitude();
		
		double population_i = eData.getPopulation(year, district_i);
		double population_j = eData.getPopulation(year, district_j);
		double distance = iData.getDistanceIndex(year, district_i, district_j);
		double index_i = iData.getAgeStructureIndex(year, district_i);
		double index_j = iData.getAgeStructureIndex(year, district_j);
		double interactionIn;		// immigration to district_i from district_j
		double interactionOut;	// emigration from district_i to district_j
		double interactionNet;	// net-migration of district_i with district_j
				
		double gravity;
		
		/*** calculate curvature ***/
		if(district_i != district_j && population_i > 0 && population_j > 0 && index_i > 0 && index_j >0
				&& iData.getLatitude(district_i) >= minLat && iData.getLongitude(district_i) >= minLong
				&& iData.getLatitude(district_j) >= minLat && iData.getLongitude(district_j) >= minLong){
			population_i = this.populationFunction(year, district_i, eData);
			population_j = this.populationFunction(year, district_j, eData);
					
			interactionIn = index_i * population_j  / distance;	
			interactionOut = index_j * population_i  / distance;	
			
		//	interactionIn = 0;
		//	interactionOut = 0;
		//	gravity = index_i - index_j;
		//	if(gravity > 0) interactionIn = gravity  * population_i * population_j  / distance;	
		//	else if(gravity < 0) interactionOut = -1.0 * gravity  * population_i * population_j  / distance;			
						
			/*
			if(gravity > 0) interactionIn = gravity * population_j  / distance;	
			else if(gravity < 0) interactionOut = -1.0 * gravity  * population_i  / distance;	
			
			interactionIn = index_i * population_j  / distance		 * population_i;	
			interactionOut = index_j * population_i  / distance	 * population_j;	
			*/
			
			interactionNet = interactionIn - interactionOut;
		}else{
			interactionIn = 0.0;
			interactionOut = 0.0;
			interactionNet = 0.0;
		}
		iData.setInteractionIn(year, district_i, district_j, interactionIn);
		iData.setInteractionOut(year, district_i, district_j, interactionOut);
		iData.setInteractionNet(year, district_i, district_j, interactionNet);	
	}
	
	public void calculateMigration(int year, int district, EntropyData eData, InteractionData iData){
		double interactionIn = 0.0;			// immigration to the district
		double interactionOut = 0.0;		// emigration from the district
		double interactionNet = 0.0;		// net-migration of the district
		double minLat = iData.getMinLatitude();
		double minLong = iData.getMinLongitude();
		
		for(int i=0 ; i<eData.getDistricNumber() ; i++){
			if(i != district && eData.getPopulation(year, i) > 0
					&& iData.getLatitude(i) >= minLat && iData.getLongitude(i) >= minLong){		
				this.calculateMigration(year, district, i, eData, iData);
				interactionIn += iData.getInteractionIn(year, district, i);
				interactionOut += iData.getInteractionOut(year, district, i);
				interactionNet += iData.getInteractionNet(year, district, i);
			}
		}	
		iData.setMigrationIn(year, district, interactionIn);
		iData.setMigrationOut(year, district, interactionOut);
		iData.setMigrationNet(year, district, interactionNet);
		
	//	System.out.println(iData.getDistrictName(district)+"\t"+interactionIn+"\t"+interactionOut);
	}
	
	public void calculateMigration(int year, EntropyData eData, InteractionData iData){
		this.calculateMigration(false, year, eData, iData);
	}
	
	public void calculateMigration(boolean normalization, int year, EntropyData eData, InteractionData iData){
		//normalization: [true] normalize migrationIn and migrationOut to rates of immigration and emigration 
		
		int i;
		int district = iData.getDistricNumber();
		double totalIn = 0.0, totalOut = 0.0, totalNet = 0.0;
		double minLat = iData.getMinLatitude();
		double minLong = iData.getMinLongitude();
		
		for(i=0 ; i<district ; i++){
			if(iData.getLatitude(i) >= minLat && iData.getLongitude(i) >= minLong){
				this.calculateMigration(year, i, eData, iData);
				totalIn += iData.getMigrationIn(year, i);
				totalOut += iData.getMigrationOut(year, i);
				totalNet += iData.getMigrationNet(year, i);
			}
		}
		iData.setTotalIn(year, totalIn);
		iData.setTotalOut(year, totalOut);
		iData.setTotalNet(year, totalNet);
		
		//normalize to rates of immigration and emigration 
		if(normalization){
			double tmpTotal = 0.0, tmpPopulation;
			tmpTotal = 0.0;
			for(i=0 ; i<district ; i++)
				if(iData.getLatitude(i) >= minLat && iData.getLongitude(i) >= minLong 
					&& eData.getPopulation(year, i) > 0)
					tmpTotal += eData.getPopulation(year, i);
			for(i=0 ; i<district ; i++){
				tmpPopulation = eData.getPopulation(year, i);
				if(iData.getLatitude(i) >= minLat && iData.getLongitude(i) >= minLong && tmpPopulation > 0){
					iData.setMigrationIn(year, i, iData.getMigrationIn(year, i)/(tmpTotal - tmpPopulation));
					iData.setMigrationOut(year, i, iData.getMigrationOut(year, i)/tmpPopulation);
				}
			}
		}
		
		
	}
	
	public void calculateMigration(int ageStructureMode, InteractionData iData, EntropyData eData, 
														double[] min, double[] weightA, double[] weightB, 
														double[] lamdaA, double[] lamdaB, double[] weightD){	
		this.calculateMigration(false, ageStructureMode, iData, eData, min, weightA, weightB, lamdaA, lamdaB, weightD);
	}
	
	public void calculateMigration(boolean normalization, int ageStructureMode, 
														InteractionData iData, EntropyData eData, 
														double[] min, double[] weightA, double[] weightB, 
														double[] lamdaA, double[] lamdaB, double[] weightD){			
		/*** calculate interactions ***/
		for(int i=0 ; i<iData.getNumberOfYears() ; i++){
			this.calculateDistanceIndex(i, min[i], lamdaA[i], lamdaB[i], weightD[i], eData, iData);
			this.calculateAgeStructureIndex(ageStructureMode, i, weightA[i], weightB[i], eData, iData);
	//		this.normalizeAgeStructureIndex(i, iData);
			this.calculateMigration(normalization, i, eData, iData);		
		}
	}
	
	public void calculateTraditionalGravity(InteractionData iData, EntropyData eData, int mode){
		//mode: [0]: power distance function, [1] exponential distance function
		int i, j, k;
		int duration = iData.getNumberOfYears();
		int district = iData.getDistricNumber();
		double tmpInteraction, tmpIn, tmpOut, tmpNet;
		double migrationIn, migrationOut, migrationNet;
		double totalIn, totalOut, totalNet;
		
		for(i=0 ; i<duration ; i++){
			totalIn = 0.0;
			totalOut = 0.0;
			totalNet = 0.0;
			
			/*** calculate migrations ***/
			for(j=0 ; j<district ; j++){
				migrationIn = 0.0;
				migrationOut = 0.0;
				migrationNet = 0.0;
				
				/*** calculate interactions ***/
				for(k=0 ; k<district ; k++){
					tmpIn = 0.0;
					tmpOut = 0.0;
					tmpNet = 0.0;
					if(k != j &&  this.distancePowerFuction(j, k, eData) > 0) {	
						if(mode == 0) 
							tmpInteraction = this.populationFunction(i, k, eData) / this.distancePowerFuction(j, k, eData);
						else if(mode == 1) 
							tmpInteraction = this.populationFunction(i, k, eData) / this.distanceExpFuction(j, k, eData);
						else tmpInteraction = 0.0;
						
						if (eData.getPopulation(i, j) > eData.getPopulation(i, k)) tmpIn = tmpInteraction;
						else if (eData.getPopulation(i, j) < eData.getPopulation(i, k)) tmpOut = tmpInteraction;
						tmpNet = tmpIn - tmpOut;	
						
						iData.setInteractionIn(i, j, k, tmpIn);
						iData.setInteractionOut(i, j, k, tmpOut);
						iData.setInteractionNet(i, j, k, tmpNet);
						
						migrationIn += tmpIn;
						migrationOut += tmpOut;
						migrationNet += tmpNet;
					}
				}
				iData.setMigrationIn(i, j, migrationIn);
				iData.setMigrationOut(i, j, migrationOut);
				iData.setMigrationNet(i, j, migrationNet);
				totalIn += migrationIn;
				totalOut += migrationOut;
				totalNet += migrationNet;
			}
			iData.setTotalIn(i, totalIn);
			iData.setTotalOut(i, totalOut);
			iData.setTotalNet(i, totalNet);
		}
	}
	
	public InteractionData composeInteractionData(EntropyData eData){
		
		int i;
		int districtNumber = eData.getDistricNumber();
		
		InteractionData iData = new InteractionData();
		
		iData.initiate(eData.getYears(), eData.getClassNumber(), eData.getDistricNumber(), eData.getDistricClass());
		iData.setMapBoundary(eData.getMaxLatitude(), eData.getMinLatitude(), eData.getMaxLongitude(), eData.getMinLongitude());
		
		/*** set location data ***/
		for(i=0 ; i<districtNumber ; i++){
			iData.setDistrictName(i, eData.getDistrictName(i));	
			iData.setCoordinates(i, eData.getLatitude(i), eData.getLongitude(i));
		}
		
		return iData;
	}
	
	public InteractionData proceedTraditionalGravityModel(EntropyData eData, int mode){
		//mode: [0]: power distance function, [1] exponential distance function	
		InteractionData iData;
		
		//interaction data composing
		iData = this.composeInteractionData(eData);			
		//migration calculating
		this.calculateTraditionalGravity(iData, eData, mode);
		
		return iData;
	}
	
	public void proceedTraditionalGravityModel(InteractionData iData, EntropyData eData, int mode){
		//mode: [0]: power distance function, [1] exponential distance function
		
		//migration calculating
		this.calculateTraditionalGravity(iData, eData, mode);
	}		
	
	public void printInteractionAndMigration(String outputFile, InteractionData iData){
		
		int i, j;
		int duration = iData.getNumberOfYears();
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			/*
			pw.println("Max_latitude: " + iData.getMaxLatitude());
			pw.println("Min_latitude: " + iData.getMinLatitude());
			pw.println("Max_longitude:" + iData.getMaxLongitude());
			pw.println("Min_longitude:" + iData.getMinLongitude());
			pw.println();	
			*/
			pw.println("Immigration:");
			//pw.print("district \t latitude \t longitude \t");
			pw.print("District\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getYear(i) + "\t");
			pw.println();
			for(i=0 ; i<iData.getDistricNumber() ; i++){
				if(iData.getLatitude(i) > iData.getMinLatitude() || iData.getLongitude(i) > iData.getMinLongitude()){
					pw.print(iData.getDistrictName(i)+"\t");
			//		pw.print(cData.getLatitude(i)+"\t"+cData.getLongitude(i)+"\t");				
					for(j=0 ; j<duration ; j++) pw.print(iData.getMigrationIn(j, i)+"\t");
					pw.println();
				}	
			}
			//pw.print("sum\t\t\t");
			pw.print("Sum\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getTotalIn(i)+"\t");
			pw.println();		
			pw.print("RMSE_In\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getRmseIn(i)+"\t");
			pw.println();		
			pw.print("R-square_In\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getRsquareIn(i)+"\t");
			pw.println();		
			pw.print("RMSE_InRate\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getRmseInRate(i)+"\t");
			pw.println();		
			pw.print("R-square_InRate\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getRsquareInRate(i)+"\t");
			pw.println();	
			pw.println();	
			
			pw.println("Emigration:");
			pw.print("District\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getYear(i) + "\t");
			pw.println();		
			for(i=0 ; i<iData.getDistricNumber() ; i++){
				if(iData.getLatitude(i) > iData.getMinLatitude() || iData.getLongitude(i) > iData.getMinLongitude()){
					pw.print(iData.getDistrictName(i)+"\t");			
					for(j=0 ; j<duration ; j++) pw.print(iData.getMigrationOut(j, i)+"\t");
					pw.println();
				}	
			}
			pw.print("Sum\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getTotalOut(i)+"\t");
			pw.println();		
			pw.print("RMSE_Out\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getRmseOut(i)+"\t");
			pw.println();		
			pw.print("R-square_Out\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getRsquareOut(i)+"\t");
			pw.println();	
			pw.print("RMSE_OutRate\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getRmseOutRate(i)+"\t");
			pw.println();		
			pw.print("R-square_OutRate\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getRsquareOutRate(i)+"\t");
			pw.println();	
			pw.println();		
			
			pw.println("Net-migration:");
			pw.print("District\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getYear(i) + "\t");
			pw.println();		
			for(i=0 ; i<iData.getDistricNumber() ; i++){
				if(iData.getLatitude(i) > iData.getMinLatitude() || iData.getLongitude(i) > iData.getMinLongitude()){
					pw.print(iData.getDistrictName(i)+"\t");			
					for(j=0 ; j<duration ; j++) pw.print(iData.getMigrationNet(j, i)+"\t");
					pw.println();
				}	
			}
			pw.print("Sum\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getTotalNet(i)+"\t");
			pw.println();				
			pw.print("RMSE_Net\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getRmseNet(i)+"\t");
			pw.println();		
			pw.print("R-square_Net\t");
			for(i=0 ; i<duration ; i++) pw.print(iData.getRsquareNet(i)+"\t");
			pw.println();	
			
			pw.close();
		}catch(IOException e) {}	

		
		System.out.println();
		System.out.print("RMSE: Immigration\t");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f\t",iData.getRmseIn(i));
		System.out.println();
		System.out.print("RMSE: Emigration\t");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f\t",iData.getRmseOut(i));
		System.out.println();
		System.out.print("RMSE: Net-migration\t");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f\t",iData.getRmseNet(i));
		System.out.println();
		System.out.print("RMSE: Immigration Rate\t");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f\t",iData.getRmseInRate(i));
		System.out.println();
		System.out.print("RMSE: Emigration  Rate\t");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f\t",iData.getRmseOutRate(i));
		System.out.println();
		System.out.print("Rsquare: Immigration\t");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f\t",iData.getRsquareIn(i));
		System.out.println();
		System.out.print("Rsquare: Emigration\t");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f\t",iData.getRsquareOut(i));
		System.out.println();
		System.out.print("Rsquare: Net-migration\t");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f\t",iData.getRsquareNet(i));
		System.out.println();
		System.out.print("Rsquare: Immigration Rate\t");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f\t",iData.getRsquareInRate(i));
		System.out.println();
		System.out.print("Rsquare: Emigration Rate\t");
		for(i=0 ; i<duration ; i++) System.out.printf("%4.6f\t",iData.getRsquareOutRate(i));
		System.out.println();
	}
	
	public void proceedOptimizationGAprocess(int optimizingFactor, int optimizingMethod, int ageStructueMode, 
																			boolean normalization, boolean fragment,
																			String inflowFile, String outflowFile,
																			InteractionData iData, EntropyData eData, int iteration){
		/***	optimizingFactor:	[0] immigration, [1] emigration, [2] net-migration 
		 * 										[3] immigration rate, [4] emigration rate 				
		 * 		optimizingMethod:	[0] RMSE minimizing, [1] R-square maximizing, 
		 * 										[2] Fast mode - RMSE minimizing , [3] Fast mode - R-square maximizing,
		 * 										[4] Fast mode -RMSE minimizing both immigration and emigration rates
		 *		 ageStructureMode:[0] H_normalized^(Aged_ratio^weight)
		 * 										[1] log10(H)^(Aged_ratio^weight)
		 * 										[2] H_normalized^weight_A * Aged_ratio^weight_B
		 ***/
		int i, j, k;
		int duration = eData.getNumberOfYears();
		int variableNumber = 6;			//[0] min, [1] weight A, [2] weight B, [3] lamda A, [4] lamda B, [5]: weight D
//		int variableLength = 20;
		int[] variableLength = null;
		int  chromosomePopulation =200;
		double crossoverRate = 0.9;
		double mutationRate = 0.05;
		double[] minValues = null;
		double[] maxValues = null;

		ArrayList<double[]> variables;
		double convergence = -1.0;
		
		if(ageStructueMode==0 || ageStructueMode==1){
			variableLength = new int[]{20, 20, 1, 20, 20, 1};
			minValues = new double[]{0.0, -2.0, 0.0, 0.0, 0.0, 2.0};
			maxValues = new double[]{0.5, 2.0, 0.0, 10.0, 2.0, 2.0};
		}else if(ageStructueMode==2){
			variableLength = new int[]{10, 20, 20, 20, 20, 1};
			minValues =  new double[]{0.0, -3.0, -3.0, 0.0, 0.0, 2.0};
			maxValues = new double[]{0.5, 7.0, 0.0, 10.0, 2.0, 2.0};
		}
		else System.err.println("age structure mode error.");
		
		GeneticAlgorithm ga;
		Chromosome cs;
		MigrationCalculator mc = new MigrationCalculator();
		mc.readMigrationData(inflowFile, outflowFile);
		mc.checkFragmentedRegion(fragment, iData, eData);
		
		if(optimizingMethod == 2 || optimizingMethod == 3 || optimizingMethod == 4) 
			mc.normalizeObservedMigration(iData, eData);
		//	mc.normalizeLogarithmObservedMigration(iData, eData);
		//	mc.normalizeLogNormObservedMigration(iData, eData);
		
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
					iData.clearData(i);
					this.calculateDistanceIndex(i, variables.get(k)[0], variables.get(k)[3], variables.get(k)[4], variables.get(k)[5], eData, iData);
					this.calculateAgeStructureIndex(ageStructueMode, i, variables.get(k)[1], variables.get(k)[2], eData, iData);
			//		this.normalizeAgeStructureIndex(i, iData);
					this.calculateMigration(normalization, i, eData, iData);
					iData.normalizeMigration(iData.getYear(i), 1.0, 0.1);
			//		iData.logarithmMigration(iData.getYear(i));
					
					if(optimizingMethod == 0) 
						cs.setFunctionResult(k, mc.calculateRMSE(iData.getYear(i), iData, eData)[optimizingFactor]); 
					else if(optimizingMethod == 1) 
						cs.setFunctionResult(k, 1.0/mc.calculateRsquare(iData.getYear(i), iData, eData)[optimizingFactor]); 
					else if(optimizingMethod == 2) 
						cs.setFunctionResult(k, mc.calculateNormalizedRMSE(iData.getYear(i), iData, eData)[optimizingFactor]); 
					else if(optimizingMethod == 3) 
						cs.setFunctionResult(k, 1.0/mc.calculateNormalizedRsquare(iData.getYear(i), iData, eData)[optimizingFactor]); 
					else if(optimizingMethod == 4) 
						cs.setFunctionResult(k, mc.calculateNormalizedRMSE(iData.getYear(i), iData, eData)[3] 
																* mc.calculateNormalizedRMSE(iData.getYear(i), iData, eData)[4]); 
					else System.err.println("optimizing method selecting error");
				}
				convergence = ga.calculateConvergence(cs);
			}
			cs.sortAscendindOrder();			
			
			iData.setMinimum(i, cs.getVariableValues(0)[0]);
			iData.setWeightA(i, cs.getVariableValues(0)[1]);
			iData.setWeightB(i, cs.getVariableValues(0)[2]);
			iData.setLambdaA(i, cs.getVariableValues(0)[3]);
			iData.setLambdaB(i, cs.getVariableValues(0)[4]);
			iData.setWeightD(i, cs.getVariableValues(0)[5]);
			if(optimizingMethod == 0 || optimizingMethod == 1){
				iData.setRMSE(i, mc.calculateRMSE(iData.getYear(i), iData, eData));
				iData.setRsquare(i, mc.calculateRsquare(iData.getYear(i), iData, eData));
			}else if(optimizingMethod == 2 || optimizingMethod == 3){
				iData.setRMSE(i, mc.calculateNormalizedRMSE(iData.getYear(i), iData, eData));
				iData.setRsquare(i, mc.calculateNormalizedRsquare(iData.getYear(i), iData, eData));
			}
			
			int lowCut;
			if(cs.getPopulation() < 5) lowCut = cs.getPopulation(); else lowCut = 5;
			System.out.println(" \t\tMinimum \t\tWeight _A\tWeight _B\tLambda_A \tLambda_B \tWeight _D\tGov. Function \tConvergence");
			for(j=0 ; j<lowCut ; j++)
				System.out.printf("%d\t%4.6f\t%4.6f\t%4.6f\t%4.6f\t%4.6f\t%4.6f\t%4.6f\t%e\n", 
						eData.getYear(i), cs.getVariableValues(j)[0], cs.getVariableValues(j)[1], cs.getVariableValues(j)[2], 
						cs.getVariableValues(j)[3], cs.getVariableValues(j)[4], cs.getVariableValues(j)[5], 
						cs.getFunctionResult(j), convergence);
			
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
					distance = this.calculateDistance(j, k, eData);
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
	
	public void checkDistance(EntropyData eData, InteractionData iData){
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
					distance=this.distanceSigmoidFunction(j, k, eData, iData, iData.getMinimum(i), iData.getLamdaA(i), iData.getLamdaB(i));
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

	public void readModelProperties(String inputFile, InteractionData iData){
		int year, yearIndex;
		
		try{
			File file = new File(inputFile);			
			Scanner scan = new Scanner(file);		
						
			scan.nextLine();
			while(scan.hasNext()){
				year = scan.nextInt();
				yearIndex = iData.getYearIndex(year);
				iData.setMinimum(yearIndex, scan.nextDouble());
				iData.setWeightA(yearIndex, scan.nextDouble());
				iData.setWeightB(yearIndex, scan.nextDouble());
				iData.setLambdaA(yearIndex, scan.nextDouble());
				iData.setLambdaB(yearIndex, scan.nextDouble());	
				iData.setWeightD(yearIndex, scan.nextDouble());
			}
			
			scan.close();			
		} catch(IOException e) {
			System.err.print("Model properties reading error.\t");
		}
	}
	
	public void printModelProperties(String outputFile, InteractionData iData){
		int i;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.println("Year\tMinimum\tWeight_A\tWeight_B\tLamba_A\tLambda_B\tWeight_D");
			for(i=0 ; i<iData.getNumberOfYears() ; i++)
				pw.println(iData.getYear(i)+"\t"+iData.getMinimum(i)+"\t"+iData.getWeightA(i)+"\t"+iData.getWeightB(i)+"\t"+iData.getLamdaA(i)+"\t"+iData.getLamdaB(i)+"\t"+iData.getWeightD(i));
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printInteraction(String outputFile, InteractionData iData){
		int i, j;
		int year;
		int duration = iData.getNumberOfYears();
		int district = iData.getDistricNumber();
		double minLat = iData.getMinLatitude();
		double maxLat = iData.getMaxLatitude();
		double minLong = iData.getMinLongitude();
		double maxLong = iData.getMaxLongitude();
			
		for(year=0 ; year<duration ; year++){					
			try{
				File file = new File(outputFile.replace(".txt", "_"+(iData.getYear(year))+".txt"));
				PrintWriter pw = new PrintWriter(file);
				
				pw.println("Interaction: immigration");
				pw.print("District\t");
				for(i=0 ; i<iData.getDistricNumber() ; i++)
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong)
						pw.print(iData.getDistrictName(i)+"\t");			
				pw.println();
				
				for(i=0 ; i<district ; i++){
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong){
						pw.print(iData.getDistrictName(i)+"\t");

						for(j=0 ; j<district ; j++)
							if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong)
								pw.print(iData.getInteractionIn(year, i, j)+"\t");				
						pw.println();
					}	
				}			
				pw.println();
				
				pw.println("Interaction: emigration");
				pw.print("District\t");
				for(i=0 ; i<iData.getDistricNumber() ; i++)
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong)
						pw.print(iData.getDistrictName(i)+"\t");			
				pw.println();
				
				for(i=0 ; i<district ; i++){
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong){
						pw.print(iData.getDistrictName(i)+"\t");

						for(j=0 ; j<district ; j++)
							if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong)
								pw.print(iData.getInteractionOut(year, i, j)+"\t");				
						pw.println();
					}	
				}			
				pw.println();
				
				pw.println("Interaction: net-migration");
				pw.print("District\t");
				for(i=0 ; i<iData.getDistricNumber() ; i++)
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong)
						pw.print(iData.getDistrictName(i)+"\t");			
				pw.println();
				
				for(i=0 ; i<district ; i++){
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong){
						pw.print(iData.getDistrictName(i)+"\t");

						for(j=0 ; j<district ; j++)
							if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong)
								pw.print(iData.getInteractionNet(year, i, j)+"\t");				
						pw.println();
					}	
				}			
				
				pw.close();
			}catch(IOException e) {}
		}
	}	
	
	public void printDistances(String outputFile, InteractionData iData){
		int i, j;
		int year;
		int duration = iData.getNumberOfYears();
		int district = iData.getDistricNumber();

		double minLat = iData.getMinLatitude();
		double minLong = iData.getMinLongitude();
		
		for(year=0 ; year<duration ; year++){					
			try{
				File file = new File(outputFile.replace(".txt", "_"+(iData.getYear(year))+".txt"));
				PrintWriter pw = new PrintWriter(file);
				
				pw.println("Distance index:");
				pw.print("District\t");
				for(i=0 ; i<iData.getDistricNumber() ; i++)
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong)
						pw.print(iData.getDistrictName(i)+"\t");			
				pw.println();
				
				for(i=0 ; i<district ; i++){
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong){
						pw.print(iData.getDistrictName(i)+"\t");

						for(j=0 ; j<district ; j++)
							if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong)
								pw.print(iData.getDistanceIndex(year, i, j)+"\t");				
						pw.println();
					}	
				}			
				pw.println();
				
				/*
				pw.println("Distance:");
				pw.print("District\t");
				for(i=0 ; i<iData.getDistricNumber() ; i++)
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong)
						pw.print(iData.getDistrictName(i)+"\t");			
				pw.println();
				
				for(i=0 ; i<district ; i++){
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong){
						pw.print(iData.getDistrictName(i)+"\t");

						for(j=0 ; j<district ; j++)
							if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong)
								pw.print(iData.getDistance(i, j)+"\t");				
						pw.println();
					}	
				}			
				pw.println();
				*/
				
				pw.close();
			}catch(IOException e) {}
		}
	}
	
	public void printResults(String outputFile, InteractionData iData, EntropyData eData){
		int i, j;
		int duration = iData.getNumberOfYears();
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.print("District\t");
			for(i=0 ; i<duration ; i++) 
				pw.print(iData.getYear(i)+"_entropy\t"+iData.getYear(i)+"_agedRatio\t"+iData.getYear(i)+"_ageStructure\t"+iData.getYear(i)+"_immigration\t"+iData.getYear(i)+"_emigration\t"+iData.getYear(i)+"_net-migration\t");
			pw.println();
			
			for(i=0 ; i<iData.getDistricNumber() ; i++){
				if(iData.getLatitude(i)>iData.getMinLatitude() || iData.getLongitude(i)>iData.getMinLongitude()){
					pw.print(iData.getDistrictName(i));			
					for(j=0 ; j<duration ; j++) 
						pw.print("\t"+eData.getEntropy(j, i)+"\t"+eData.getAgedIndex(j, i)+"\t"+iData.getAgeStructureIndex(j, i)+"\t"+iData.getMigrationIn(j, i)+"\t"+iData.getMigrationOut(j, i)+"\t"+iData.getMigrationNet(j, i));
					pw.println();
				}
			}
			pw.println();
					
			pw.close();
		}catch(IOException e) {}	
	}
	
	public static void main(String[] args) {
		int iteration = 100;
		int optimizingFactor = 0;	//[0]immigration, [1]emigration, [2]net-migration, [3]immigration rate, [4]emigration rate
		int optimizingMethod = 2;	//[0]RMSE minimization, [1]R-square maximization, [2]Fast mode: 0, [3]Fast mode: 1 
													//[4] Fast mode -RMSE minimizing both immigration and emigration rates
		int ageStructueMode = 2;	//[0]H_n^(R_age^w), [1]log10(H)^(R_age^w), [2](H_n^wA)*(R_age^wB)
		boolean normalization = false;	//[true]: normalize migrationIn and migrationOut to rates of in & out
		boolean optimization = true;		//[true]: proceed optimization process
		boolean fragment = true;			//[true]: check fragmented regions
		String[] factorName = {"_in", "_out", "_net", "_inRate", "_outRate", "_bothRates"};
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		String outputPath = filePath + "separated/";
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
		String traditionalFile = outputPath+"Traditional_exponential.txt";
		String lamdaFile = outputPath + "LamdaAB_separated_"+iteration+factorName[optimizingFactor]+".txt";
		String potentialFile = outputPath + "Potential_separated_"+iteration+factorName[optimizingFactor]+".txt";
		String interactionFile = outputPath + "interaction/Interaction_separated_"+iteration+factorName[optimizingFactor]+".txt";
		String distanceFile = outputPath + "interaction/Distance_separated_"+iteration+factorName[optimizingFactor]+".txt";
		String graphFile = outputPath + "interaction/GraphGDF_separated_"+iteration+factorName[optimizingFactor]+".gdf";
		String rmseFile = outputPath + "RMSE_"+iteration+factorName[optimizingFactor]+".txt";
		String resultFile = outputPath +"Results_"+iteration+factorName[optimizingFactor]+".txt";
		
		if(normalization){
			potentialFile = outputPath + "Potential_separated_"+iteration+factorName[optimizingFactor]+"_norm.txt";
			interactionFile = outputPath + "interaction/Interaction_separated_"+iteration+factorName[optimizingFactor]+"_norm.txt";
			rmseFile = outputPath + "RMSE_"+iteration+factorName[optimizingFactor]+"_norm.txt";
			resultFile = outputPath +"Results_"+iteration+factorName[optimizingFactor]+"_norm.txt";
		}
		
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
		InteractionData iData;
		
		EntropyCalculator ec = new EntropyCalculator();
		DataReader dr = new DataReader();
		MapGenerator mg = new MapGenerator();
		MigrationModel migModel = new MigrationModel();		
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

		System.out.print("interaction data composing: ");
		iData = migModel.composeInteractionData(norm_eData);
		migModel.calculateDistance(norm_eData, iData);
		System.out.println("complete");
		
		/*** Migration function optimizing part ***/
		if(optimization){
			System.out.println("parameters optimizing: ");
			if(ageStructueMode == 0 || ageStructueMode == 2)
				migModel.proceedOptimizationGAprocess(optimizingFactor, optimizingMethod, ageStructueMode, normalization, fragment, inflowFile, outflowFile, iData, norm_eData, iteration);
			else if(ageStructueMode == 1)
				migModel.proceedOptimizationGAprocess(optimizingFactor, optimizingMethod, ageStructueMode, normalization, fragment, inflowFile, outflowFile, iData, eData, iteration);
			System.out.println("complete");
	
			//cm.checkDistance(norm_eData, cData);
			System.out.print("model properties printing: ");
			migModel.printModelProperties(lamdaFile, iData);
			System.out.println("complete");				
		}
		
		/*** Potential assessment part ***/		
		System.out.print("model properties reading: ");
		iData.clearData();
		migModel.readModelProperties(lamdaFile, iData);
		System.out.println("complete");		
		
		System.out.print("migration model processing: ");
		mc = new MigrationCalculator();
		if(ageStructueMode == 0 || ageStructueMode == 2)
			migModel.calculateMigration(normalization, ageStructueMode, iData, norm_eData, iData.getMinimumList(), 
															iData.getWeightAList(), iData.getWeightBList(),
															iData.getLamdaAList(), iData.getLamdaBList(), iData.getWeightDList());
		else if(ageStructueMode == 1)
			migModel.calculateMigration(normalization, ageStructueMode, iData, eData, iData.getMinimumList(), 
															iData.getWeightAList(),iData.getWeightAList(),
															iData.getLamdaAList(), iData.getLamdaBList(), iData.getWeightDList());
		iData.normalizeMigration();
		mc.readMigrationData(inflowFile, outflowFile);
		mc.checkFragmentedRegion(fragment, iData, norm_eData);
		mc.normalizeObservedMigration(iData, norm_eData);
	//	mc.normalizeLogarithmObservedMigration(iData, norm_eData);
	//	mc.normalizeLogNormObservedMigration(iData, norm_eData);
		
		mc.calculateNormalizedRMSE(iData, norm_eData);
		mc.calculateNormalizedRsquare(iData, norm_eData);
		System.out.println("complete");			
		
		System.out.print("migration data printing: ");
		migModel.printInteractionAndMigration(potentialFile, iData);
		migModel.printResults(resultFile, iData, norm_eData);
		migModel.printInteraction(interactionFile, iData);
	//	migModel.printDistances(distanceFile, iData);
		mc.printResults(1, rmseFile.replace(".txt", "_current.txt"), iData, eData);
		mc.normalizeObservedMigration(0, iData, norm_eData);
		mc.printResults(0, rmseFile, iData, eData);
		System.out.println("complete");
		
		
	/*** Interaction calculating part ***/	
		/*
		System.out.print("latlng coordinate reading: ");
		gf.readCoordinate(latLngCoordFile, iData);
		System.out.println("complete");
		
		System.out.print("interaction graph file making: ");
		gf.makeGDFFormatInteraction(graphFile, iData);
		System.out.println("complete");
		*/
		
		
		optimizingFactor = 1;
		
		lamdaFile = outputPath + "LamdaAB_separated_"+iteration+factorName[optimizingFactor]+".txt";
		potentialFile = outputPath + "Potential_separated_"+iteration+factorName[optimizingFactor]+".txt";
		interactionFile = outputPath + "interaction/Interaction_separated_"+iteration+factorName[optimizingFactor]+".txt";
		rmseFile = outputPath + "RMSE_"+iteration+factorName[optimizingFactor]+".txt";
		resultFile = outputPath +"Results_"+iteration+factorName[optimizingFactor]+".txt";
		
		if(normalization){
			potentialFile = outputPath + "Potential_separated_"+iteration+factorName[optimizingFactor]+"_norm.txt";
			interactionFile = outputPath + "interaction/Interaction_separated_"+iteration+factorName[optimizingFactor]+"_norm.txt";
			rmseFile = outputPath + "RMSE_"+iteration+factorName[optimizingFactor]+"_norm.txt";
			resultFile = outputPath +"Results_"+iteration+factorName[optimizingFactor]+"_norm.txt";
		}
		
		System.out.print("interaction data composing: ");
		iData = migModel.composeInteractionData(norm_eData);
		migModel.calculateDistance(norm_eData, iData);
		System.out.println("complete");
		
		if(optimization){
			System.out.println("parameters optimizing: ");
			if(ageStructueMode == 0 || ageStructueMode == 2)
				migModel.proceedOptimizationGAprocess(optimizingFactor, optimizingMethod, ageStructueMode, normalization, fragment, inflowFile, outflowFile, iData, norm_eData, iteration);
			else if(ageStructueMode == 1)
				migModel.proceedOptimizationGAprocess(optimizingFactor, optimizingMethod, ageStructueMode, normalization, fragment, inflowFile, outflowFile, iData, eData, iteration);
			System.out.println("complete");
	
			System.out.print("model properties printing: ");
			migModel.printModelProperties(lamdaFile, iData);
			System.out.println("complete");				
		}
		
		System.out.print("model properties reading: ");
		iData.clearData();
		migModel.readModelProperties(lamdaFile, iData);
		System.out.println("complete");		
		
		System.out.print("migration model processing: ");
		mc = new MigrationCalculator();
		if(ageStructueMode == 0 || ageStructueMode == 2)
			migModel.calculateMigration(normalization, ageStructueMode, iData, norm_eData, iData.getMinimumList(), 
															iData.getWeightAList(), iData.getWeightBList(),
															iData.getLamdaAList(), iData.getLamdaBList(), iData.getWeightDList());
		else if(ageStructueMode == 1)
			migModel.calculateMigration(normalization, ageStructueMode, iData, eData, iData.getMinimumList(), 
															iData.getWeightAList(),iData.getWeightAList(),
															iData.getLamdaAList(), iData.getLamdaBList(), iData.getWeightDList());
		iData.normalizeMigration();
		mc.readMigrationData(inflowFile, outflowFile);
		mc.checkFragmentedRegion(fragment, iData, norm_eData);
		mc.normalizeObservedMigration(iData, norm_eData);
	//	mc.normalizeLogarithmObservedMigration(iData, norm_eData);
		
		mc.calculateNormalizedRMSE(iData, norm_eData);
		mc.calculateNormalizedRsquare(iData, norm_eData);
		System.out.println("complete");			
		
		System.out.print("migration data printing: ");
		migModel.printInteractionAndMigration(potentialFile, iData);
		migModel.printResults(resultFile, iData, norm_eData);
		migModel.printInteraction(interactionFile, iData);
		mc.printResults(1, rmseFile.replace(".txt", "_current.txt"), iData, eData);
		mc.normalizeObservedMigration(0, iData, norm_eData);
		mc.printResults(0, rmseFile, iData, eData);
		System.out.println("complete");
		
		// Interaction calculating part
		/*
		System.out.print("latlng coordinate reading: ");
		gf.readCoordinate(latLngCoordFile, iData);
		System.out.println("complete");
		
		System.out.print("interaction graph file making: ");
		gf.makeGDFFormatInteraction(graphFile, iData);
		System.out.println("complete");
		*/
		
		/*
		System.out.print("gravity model processing: ");
		migModel.proceedTraditionalGravityModel(iData, norm_eData, 1);
		iData.normalizeMigration();
		mc.readMigrationData(inflowFile, outflowFile);
		mc.printResults(rmseFile, cData, eData);
		migModel.printInteractionAndMigration(traditionalFile, cData);
		System.out.println("complete");			
		*/
		
	
		
		System.out.println("[process complete]");
	}

}
