package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import populationEntropy.data.*;

public class MapGenerator {

	/**
	 * @param args
	 */
	
	public MapGenerator(){
		
	}
	
	public void readCoordinates(String inputFile, Coordinates points){
		
		int i;
		double latitude, longitude;
		double maxLat, minLat, maxLong, minLong;
		String district;
		
		
		try{
			File file = new File(inputFile);			
			Scanner scan = new Scanner(file);
		
			scan.nextLine();
			district = scan.next();
			latitude = scan.nextDouble();
			longitude = scan.nextDouble();
			maxLat = latitude;
			minLat = latitude;
			maxLong = longitude;
			minLong = longitude;
			points.setCoordinates(district, latitude, longitude);
			
			while(scan.hasNext()){				
				district = scan.next();
				latitude = scan.nextDouble();
				longitude = scan.nextDouble();
				
				if(maxLat < latitude) maxLat = latitude;
				if(minLat > latitude) minLat = latitude;
				if(maxLong < longitude) maxLong = longitude;
				if(minLong > longitude) minLong = longitude;
				
				points.setCoordinates(district, latitude, longitude);
			}			

			points.setMaxLatitude(maxLat);
			points.setMinLatitude(minLat);
			points.setMaxLongitude(maxLong);
			points.setMinLongitude(minLong);
			
			scan.close();
			
		} catch(IOException e) {}
			
	}
	
	public void readCoordinates(String inputFile, DataReader_Japan drj, Coordinates points){
		
		double latitude, longitude;
		double maxLat=0, minLat=0, maxLong=0, minLong=0;
		String code, district;
		boolean checkExistance;
		
		try{
			File file = new File(inputFile);			
			Scanner scan = new Scanner(file);
		
			scan.nextLine();

			checkExistance = false;
			while(!checkExistance){
				code = scan.next();
				if(drj.standardCode.contains(code)){
					district = drj.standardRegions.get(drj.standardCode.indexOf(code));
					latitude = scan.nextDouble();
					longitude = scan.nextDouble();
					maxLat = latitude;
					minLat = latitude;
					maxLong = longitude;
					minLong = longitude;
					points.setCoordinates(district, latitude, longitude);
					checkExistance = true;
				}
			}
			
			while(scan.hasNext()){				
				code = scan.next();
				if(drj.standardCode.contains(code)){
					district = drj.standardRegions.get(drj.standardCode.indexOf(code));
					latitude = scan.nextDouble();
					longitude = scan.nextDouble();
					if(maxLat < latitude) maxLat = latitude;
					if(minLat > latitude) minLat = latitude;
					if(maxLong < longitude) maxLong = longitude;
					if(minLong > longitude) minLong = longitude;
					points.setCoordinates(district, latitude, longitude);
				}
			}			

			points.setMaxLatitude(maxLat);
			points.setMinLatitude(minLat);
			points.setMaxLongitude(maxLong);
			points.setMinLongitude(minLong);
			
			scan.close();
			
		} catch(IOException e) {}
			
	}
	
	public EntropyData composeEntropyData(Coordinates points, PopulationData pData){
		
		int i, j;
		int duration = pData.getNumberOfYears();
		int districtNumber = pData.getDistricNumber();
		int tmpDistrictCount = 0, tmpIndex = 0;
		String districtName, tmpDistrict;
		EntropyData eData = new EntropyData();
		
		/*** count number of effective districts ***/
		for(i=0 ; i<districtNumber ; i++)
			if(points.hasDistrict(pData.getDistrictName(i)) || points.hasSimilarDistrict(pData.getDistrictName(i))) 
				tmpDistrictCount++;
		
		/*** initiate entropyData ***/
		eData.initiate(pData.getYears(), pData.getClassNumber(), tmpDistrictCount, pData.getDistricClass());
		eData.setMapBoundary(points.getMaxLatitude(), points.getMinLatitude(), 
												points.getMaxLongitude(), points.getMinLongitude());
		
		/*** set min. and max. ***/
		for(i=0 ; i<duration ; i++){
			eData.setMinPopulation(i, pData.getMinPopulation(i));
			eData.setMaxPopulation(i, pData.getMaxPopulation(i));
			eData.setMaxEntropy(i, pData.getMaxEntropy(i));
			eData.setMinEntropy(i, pData.getMinEntropy(i));
		}
		
		/*** set values ***/
		for(j=0 ; j<districtNumber ; j++){
			districtName = pData.getDistrictName(j);		
			if(points.hasDistrict(districtName)) tmpDistrict = districtName;
			else if(points.hasSimilarDistrict(districtName)) tmpDistrict = points.getSimilarDistrict(districtName);
			else tmpDistrict = "";
			
			if(!tmpDistrict.isEmpty()){
				//set location data
				eData.setDistrictName(tmpIndex, tmpDistrict);
				eData.setCoordinates(tmpIndex, points.getLatitude(tmpDistrict), points.getLongitude(tmpDistrict));
				
				for(i=0 ; i<duration ; i++){
					//set population data
					eData.setPopulation(i, tmpIndex, pData.getPopulation(i, j));	
					eData.setAgedIndex(i, tmpIndex, pData.getAgedRatio(i, j));
					//set entropy data
					eData.setEntropy(i, tmpIndex, pData.getEntropy(i, j));	
				}
				tmpIndex++;
			}
		}
		
		return eData;
	}
	
	public EntropyData composeEntropyData_Backup(Coordinates points, PopulationData pData){
		
		int i, j;
		int duration = pData.getNumberOfYears();
		int districtNumber = pData.getDistricNumber();
		int tmpDistrictCount = 0, tmpIndex = 0;
		String districtName, tmpDistrict;
		EntropyData eData = new EntropyData();
		
		/*** count number of effective districts ***/
		for(i=0 ; i<districtNumber ; i++)
			if(points.hasDistrict(pData.getDistrictName(i)) || points.hasSimilarDistrict(pData.getDistrictName(i))) 
				tmpDistrictCount++;
		
		/*** initiate entropyData ***/
		eData.initiate(pData.getYears(), pData.getClassNumber(), tmpDistrictCount, pData.getDistricClass());
		eData.setMapBoundary(points.getMaxLatitude(), points.getMinLatitude(), 
												points.getMaxLongitude(), points.getMinLongitude());
				
		/*** set population data ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<districtNumber ; j++){
				eData.setPopulation(i, j, pData.getPopulation(i, j));	
				eData.setAgedIndex(i, j, pData.getAgedRatio(i, j));
			}
			eData.setMinPopulation(i, pData.getMinPopulation(i));
			eData.setMaxPopulation(i, pData.getMaxPopulation(i));
		}
		
		/*** set entropy data ***/ 
		for(i=0 ; i<duration ; i++){
			eData.setMaxEntropy(i, pData.getMaxEntropy(i));
			eData.setMinEntropy(i, pData.getMinEntropy(i));
			for(j=0 ; j<districtNumber ; j++) eData.setEntropy(i, j, pData.getEntropy(i, j));			
		}
		
		/*** set location data ***/
		for(j=0 ; j<districtNumber ; j++){
			districtName = pData.getDistrictName(j);		
			eData.setDistrictName(j, districtName);
			if(points.hasDistrict(districtName)){
				eData.setCoordinates(j, points.getLatitude(districtName), points.getLongitude(districtName));
			}else if(points.hasSimilarDistrict(districtName)){
				//get coordinates if the region have similar name, only last character is different 
				tmpDistrict = points.getSimilarDistrict(districtName);
				eData.setCoordinates(j, points.getLatitude(tmpDistrict), points.getLongitude(tmpDistrict));
			//	System.out.println("similar:\t"+districtName+"\t"+tmpDistrict);
			}else{
				eData.setCoordinates(j, Math.abs(points.getMinLatitude())*(-2.0), Math.abs(points.getMinLongitude())*(-2.0));	
			//	System.out.println("no corrdinate:\t"+districtName);
			}
		}
		
		return eData;
	}
	
	public EntropyData composeClassEntropyData(Coordinates points, PopulationData pData){
		
		int i, j, k;
		int duration = pData.getNumberOfYears();
		int districtNumber = pData.getDistricNumber();
		int classNumber = pData.getClassNumber();
		EntropyData eData;
	
		eData = this.composeEntropyData(points, pData);
		
		/*** set class entropy data ***/
		for(i=0 ; i<duration ; i++){
			for(k=0 ; k<classNumber ; k++){
				eData.setMaxClassEntropy(i, k, pData.getMaxClassEntropy(i, k));
				eData.setMinClassEntropy(i, k, pData.getMinClassEntropy(i, k));
				for(j=0 ; j<districtNumber ; j++) eData.setClassEntropy(i, j, k, pData.getClassEntropy(i, j, k));				
			}			
		}
		
		return eData;
	}
	/*
	public void normalizeAgedRatio(EntropyData eData, double maxRatio, double minRatio){
		int i, j;
		int duration = eData.getNumberOfYears();
		int districtNumber = eData.getDistricNumber();
		double tmpMax, tmpMin, tmpRatio;
		
		for(i=0 ; i<duration ; i++){
			tmpMax = 0.0;
			tmpMin = 100.0;
			for(j=0 ; j<districtNumber ; j++){
				tmpRatio = eData.getAgedIndex(i, j);
				if(tmpRatio > tmpMax) tmpMax = tmpRatio;
				else if(tmpRatio < tmpMin && tmpRatio > 0.0) tmpMin = tmpRatio;
			}
			tmpRatio = (maxRatio - minRatio) / (tmpMax - tmpMin);
			for(j=0 ; j<districtNumber ; j++)
				if(eData.getAgedIndex(i, j)>0.0)
					eData.setAgedIndex(i, j, (eData.getAgedIndex(i, j) - tmpMin) * tmpRatio + minRatio );	
		}
	}
	*/
	public EntropyData normalizeEntropyData(EntropyData eData, double maxEntropy, double minEntropy,
											double maxLat, double minLat, double maxLong, double minLong){

		int i, j;
		int duration = eData.getNumberOfYears();
		int districtNumber = eData.getDistricNumber();
		double tmpLatitude, tmpLongitude, tmpMinLat, tmpMinLong;
		double entropyTransformer, latTransformer, longTransformer;
		double tmpEntropy;
		
		EntropyData normalizedData = new EntropyData();
				
		normalizedData.initiate(eData.getYears(), eData.getClassNumber(), eData.getDistricNumber(), 
												eData.getDistricClass());
		normalizedData.setMapBoundary(maxLat, minLat, maxLong, minLong);
		
		/*** set population data ***/
		for(i=0 ; i<duration ; i++){
			for(j=0 ; j<districtNumber ; j++){
				normalizedData.setPopulation(i, j, eData.getPopulation(i, j));
				normalizedData.setAgedIndex(i, j, eData.getAgedIndex(i, j));
			}
			normalizedData.setMinPopulation(i, eData.getMinPopulation(i));
			normalizedData.setMaxPopulation(i, eData.getMaxPopulation(i));
		}
		
		/*** normalize entropy data ***/ 
		for(i=0 ; i<duration ; i++){
			normalizedData.setMaxEntropy(i, maxEntropy);
			normalizedData.setMinEntropy(i, minEntropy);
			entropyTransformer = (maxEntropy - minEntropy) / (eData.getMaxEntropy(i) - eData.getMinEntropy(i));
			
			for(j=0 ; j<districtNumber ; j++){
				if(eData.getEntropy(i, j) >0) 
					tmpEntropy = (eData.getEntropy(i, j) - eData.getMinEntropy(i)) * entropyTransformer + minEntropy;
				else tmpEntropy = -1.0;
				normalizedData.setEntropy(i, j, tmpEntropy);	
			}
		}
		
		/*** normalize location data ***/
		tmpMinLat = eData.getMinLatitude();
		tmpMinLong = eData.getMinLongitude();
		latTransformer = (maxLat - minLat) / (eData.getMaxLatitude() - eData.getMinLatitude());
		longTransformer = (maxLong - minLong) / (eData.getMaxLongitude() - eData.getMinLongitude());		
		for(i=0 ; i<districtNumber ; i++){
			tmpLatitude = eData.getLatitude(i);
			tmpLongitude = eData.getLongitude(i);
			normalizedData.setDistrictName(i, eData.getDistrictName(i));		
			if(tmpLatitude >= tmpMinLat && tmpLongitude >= tmpMinLong)
				normalizedData.setCoordinates(i, ((tmpLatitude - tmpMinLat) * latTransformer + minLat),											 
																		((tmpLongitude - tmpMinLong) * longTransformer + minLong));
			else normalizedData.setCoordinates(i, -1.0, -1.0);
		}
			
		return normalizedData;
	}
			
	public EntropyData normalizeEntropyData(EntropyData eData, double maxValue, double minValue){
		
		return normalizeEntropyData(eData, maxValue, minValue, maxValue, minValue, maxValue, minValue);
	}
	
	public EntropyData normalizeEntropyData(EntropyData eData){
		
		double maxEntropy = 1.0;
		double minEntropy = 0.0;
		double maxLat = 1.0;
		double minLat = 0.0;
		double maxLong = 1.0;
		double minLong = 0.0;
		
		return normalizeEntropyData(eData, maxEntropy, minEntropy, maxLat, minLat, maxLong, minLong);
	}

	
	public EntropyData normalizeClassEntropy(EntropyData eData, double maxEntropy, double minEntropy,
											 double maxLat, double minLat, double maxLong, double minLong){

		int i, j, k;
		int duration = eData.getNumberOfYears();
		int districtNumber = eData.getDistricNumber();
		int classNumber = eData.getClassNumber();
		double entropyTransformer;
		double latTransformer;
		double longTransformer;
		
		EntropyData normalizedData = new EntropyData();
				
		normalizedData.initiate(eData.getYears(), eData.getClassNumber(), eData.getDistricNumber(), 
												eData.getDistricClass());
		normalizedData.setMapBoundary(maxLat, minLat, maxLong, minLong);
		
		/*** set population data ***/
		for(i=0 ; i<duration ; i++)
			for(j=0 ; j<districtNumber ; j++) normalizedData.setPopulation(i, j, eData.getPopulation(i, j));
						
		/*** normalize entropy data ***/ 
		for(i=0 ; i<duration ; i++){
			normalizedData.setMaxEntropy(i, maxEntropy);
			normalizedData.setMinEntropy(i, minEntropy);
			entropyTransformer = (maxEntropy - minEntropy) / (eData.getMaxEntropy(i) - eData.getMinEntropy(i));
			
			for(j=0 ; j<districtNumber ; j++) 
				normalizedData.setEntropy(i, j, 
						((eData.getEntropy(i, j) - eData.getMinEntropy(i)) * entropyTransformer + minEntropy));			
		}
		
		/*** normalize class entropy data ***/
		for(i=0 ; i<duration ; i++){
			for(k=0 ; k<classNumber ; k++){
				normalizedData.setMaxClassEntropy(i, k, maxEntropy);			
				normalizedData.setMinClassEntropy(i, k, minEntropy);
				
				entropyTransformer = (maxEntropy - minEntropy) / 
									 (eData.getMaxClassEntropy(i, k) - eData.getMinClassEntropy(i, k));	
				
				for(j=0 ; j<districtNumber ; j++){
					normalizedData.setClassEntropy(i, j, k,  
											((eData.getClassEntropy(i, j, k) - eData.getMinClassEntropy(i, k)) 
												* entropyTransformer + minEntropy));					
				}				
			}			
		}
		
		/*** normalize location data ***/
		latTransformer = (maxLat - minLat) / (eData.getMaxLatitude() - eData.getMinLatitude());
		longTransformer = (maxLong - minLong) / (eData.getMaxLongitude() - eData.getMinLongitude());		
		for(i=0 ; i<districtNumber ; i++){
			normalizedData.setDistrictName(i, eData.getDistrictName(i));			
			normalizedData.setCoordinates(i, 
					((eData.getLatitude(i) - eData.getMinLatitude()) * latTransformer + minLat),											 
					((eData.getLongitude(i) - eData.getMinLongitude()) * longTransformer + minLong));
		}
			
		return normalizedData;
	}

	public EntropyData normalizeClassEntropy(EntropyData eData){
	
		double maxEntropy = 1.0;
		double minEntropy = 0.0;
		double maxLat = 1.0;
		double minLat = 0.0;
		double maxLong = 1.0;
		double minLong = 0.0;
		
		return this.normalizeClassEntropy(eData, maxEntropy, minEntropy,maxLat, minLat, maxLong, minLong);
	}
	
	public void calculateDistances(EntropyData eData){
		
		int i, j;
		int districtNumber = eData.getDistricNumber();
		
		double x, y;
		double distance;
		double total, squareTotal;
		
		for(i=0 ; i<districtNumber ; i++){
			total = 0.0;
			squareTotal = 0.0;
			for(j=0 ; j<districtNumber ; j++){
				x = Math.abs( eData.getLongitude(i) - eData.getLongitude(j) );
				y = Math.abs( eData.getLatitude(i) - eData.getLatitude(j) );
				distance = Math.sqrt((x*x) + (y*y));
				total += distance;
				squareTotal += distance * distance;
			}
			eData.setTotalDistance(i, total);
			eData.setTotalSquareDistance(i, squareTotal);
		}		
	}
	
	public void printEntropy(String outputFile, int year, EntropyData eData){
		
		int i;
		int yearIndex = eData.getYearIndex(year);
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("year: "+year);
			pw.println("max_entropy: " + eData.getMaxEntropy(yearIndex));
			pw.println("min_entropy:" + eData.getMinEntropy(yearIndex));
			pw.println("max_latitude: " + eData.getMaxLatitude());
			pw.println("min_latitude: " + eData.getMinLatitude());
			pw.println("max_longitude:" + eData.getMaxLongitude());
			pw.println("min_longitude:" + eData.getMinLongitude());
			pw.println();
			
			pw.println("district \t latitude \t longitude \t entropy");		
			for(i=0 ; i<eData.getDistricNumber() ; i++){
				pw.print(eData.getDistrictName(i)+"\t");
				pw.print(eData.getLatitude(i)+"\t"+eData.getLongitude(i)+"\t");
				pw.print(eData.getEntropy(yearIndex, i));
				pw.println();
			}
			pw.close();
		}catch(IOException e) {}	
	}
	
	public void printEntropyAndDistance(String outputFile, int year, EntropyData eData){
		
		int i;
		int yearIndex = eData.getYearIndex(year);
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("year: "+year);
			pw.println("max_entropy: " + eData.getMaxEntropy(yearIndex));
			pw.println("min_entropy:" + eData.getMinEntropy(yearIndex));
			pw.println("max_latitude: " + eData.getMaxLatitude());
			pw.println("min_latitude: " + eData.getMinLatitude());
			pw.println("max_longitude:" + eData.getMaxLongitude());
			pw.println("min_longitude:" + eData.getMinLongitude());
			pw.println();
			
			pw.println("district \t latitude \t longitude \t entropy \t total_distance \t total_square_distance");		
			for(i=0 ; i<eData.getDistricNumber() ; i++){
				pw.print(eData.getDistrictName(i)+"\t");
				pw.print(eData.getLatitude(i)+"\t"+eData.getLongitude(i)+"\t");
				pw.print(eData.getEntropy(yearIndex, i)+"\t");
				pw.print(eData.getTotalDistance(i)+"\t");
				pw.print(eData.getTotalSquareDistance(i));
				pw.println();
			}
			pw.close();
		}catch(IOException e) {}	
	}
	
	public static void main(String[] args) {
		
		int year = 2009;
		
		String filePath = "/Users/jml/Desktop/Research/data_storage/population/";
		String populatoinFile = filePath + "population_nation.txt";
		String coordinatesFile = filePath + "coordinates_nation.txt";
		String normalizedEntropyFile = filePath +  "Entropy_normalized_"+year+".txt";
		

		PopulationData dataSet = new PopulationData();
		Coordinates points = new Coordinates();
		EntropyData	eData = new EntropyData();
		EntropyData	norm_eData = new EntropyData();
		
		EntropyCalculator ec = new EntropyCalculator();
		DataReader dr = new DataReader();
		MapGenerator mg = new MapGenerator();
				
		
		System.out.print("data reading: ");
		dataSet = dr.getPopulationData(populatoinFile);
		System.out.println("complete");
		
		System.out.print("probability calculating: ");
		ec.calculateProbability(dataSet);
		System.out.println("complete");
		
		System.out.print("entropy calculating: ");
		ec.calculateEntropy(dataSet);
		System.out.println("complete");
				
		System.out.print("coordinates reading: ");
		mg.readCoordinates(coordinatesFile, points);
		System.out.println("complete");
		
		System.out.print("entropy composing: ");
		eData = mg.composeEntropyData(points, dataSet);
		System.out.println("complete");
		
		System.out.print("entropy normalizing: ");
		norm_eData = mg.normalizeEntropyData(eData);
		System.out.println("complete");

		System.out.print("total distance calculating: ");
		mg.calculateDistances(norm_eData);
		System.out.println("complete");
		
		System.out.print("normalized entropy printing: ");
		//mg.printEntropy(normalizedEntropyFile, year, norm_eData);
		mg.printEntropyAndDistance(normalizedEntropyFile, year, norm_eData);
		System.out.println("complete");
		
		System.out.println();
		System.out.println("[process complete]");
	}

}
