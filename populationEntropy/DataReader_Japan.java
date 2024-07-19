package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import populationEntropy.data.*;

public class DataReader_Japan {

	int numberOfIndex;
	int districtType = 1;		//[0]:do-do-bu-ken, [1]:shi-chou-son
	int totalClass, totalDistrict;
	
	ArrayList<Integer> yearList;
	ArrayList<String> standardCode, standardRegions, standardClasses;
	ArrayList<Integer> classNumberList, districtNumberList;
	ArrayList<ArrayList<String>> districtList, classList;
	ArrayList<HashMap<Integer, String>> districtCodeList;
	ArrayList<ArrayList<int[]>> populationList;

	HashMap<String, String> mergedDistrictList;
	PopulationData pData;
	
	public DataReader_Japan(){
		this.pData = new PopulationData();
		this.initate();
	}
	
	public void initate(){
		this.yearList = new ArrayList<Integer>();
		this.standardCode = new ArrayList<String>();
		this.standardRegions = new ArrayList<String>();
		this.standardClasses = new ArrayList<String>();
		this.classNumberList = new ArrayList<Integer>();
		this.districtNumberList = new ArrayList<Integer>();
		this.districtList = new ArrayList<ArrayList<String>>();
		this.classList = new ArrayList<ArrayList<String>>();
		this.populationList = new ArrayList<ArrayList<int[]>>();
		this.districtCodeList = new ArrayList<HashMap<Integer, String>>();
		this.mergedDistrictList = new HashMap<String, String>();
	}
	
	public PopulationData getPopulationData(){
		return this.pData;
	}
	
	public PopulationData getPopulationData(String inputFile){
		this.readPopulationData(inputFile);
		return this.pData;
	}
	
	public void  readPopulationData(String inputFile, PopulationData dataSet){
		this.readPopulationData(inputFile);
		dataSet =  this.pData;
	}
	
	public PopulationData readPopulationData(int[] years, String[] inputFiles){
		int i, j, k;
		int tmpClassIndex, tmpDistrictIndex;
		
		String tmpRegion;
		
		for(i=0 ; i<years.length ; i++) this.yearList.add(years[i]);
		
		/*** read population data ***/
		for(i=0 ; i<inputFiles.length ; i++) this.readPopulationData(inputFiles[i]);
		
		/*** merge districts ***/
		for(i=0 ; i<years.length ; i++) this.mergeDistricts(years[i]);
		
		/*** initiate population data ***/
		this.pData.initiate(years, this.totalClass, this.totalDistrict, this.districtType);
		for(i=0 ; i<this.totalClass ; i++) this.pData.setClassName(i, this.standardClasses.get(i));
		for(i=0 ; i<this.totalDistrict ; i++) this.pData.setDistrictName(i, this.standardRegions.get(i));
		
		/*** store population data ***/
		for(i=0 ; i<years.length ; i++){
			for(j=0 ; j<this.districtList.get(i).size() ; j++){
				tmpRegion = this.districtList.get(i).get(j);
				if(this.standardRegions.contains(tmpRegion)){
					tmpDistrictIndex = this.standardRegions.indexOf(tmpRegion);
					for(k=0 ; k<this.classList.get(i).size() ; k++){
						tmpClassIndex = this.standardClasses.indexOf(this.classList.get(i).get(k));
						this.pData.addPopulation(i,tmpDistrictIndex,tmpClassIndex, this.populationList.get(i).get(j)[k]);
					}
				}else System.out.println(years[i]+": "+tmpRegion+" isn't in the standard region list.");
			}
		}
		
		this.setMaxMinPopulation();
		return this.pData;
	}
	
	public void readPopulationData(String inputFile){
		int i;
		
		int classNumber;				//number of age groups
		int districtNumber;			//number of districts
		
		int tmpCode;
		int[] tmpPopulation;
		String[] tmpStr;
		String tmpDistrictType, tmpName, tmpCityName, tmpUpperDistrictName, tmpDistrictName;
		ArrayList<String> targetRegionType = new ArrayList<String>();
		ArrayList<String> tmpDistrictList = new ArrayList<String>();
		ArrayList<String> tmpClassList = new ArrayList<String>();
		ArrayList<int[]> tmpPopulationList = new ArrayList<int[]>();
		HashMap<Integer, String> districtCode = new HashMap<Integer, String>();
		
		this.numberOfIndex = 3;
		targetRegionType.add("0");
		targetRegionType.add("2");
		targetRegionType.add("3");
		
		/*** reading process ***/
		try{
			File file = new File(inputFile);
			
			/*** pre-process of population analysis ***/
			Scanner scan = new Scanner(file);
			
			tmpCityName = "";
			tmpDistrictName = "";
			tmpUpperDistrictName = "";
			tmpStr = scan.nextLine().split("\t");
			for(i=this.numberOfIndex ; i<tmpStr.length ; i++) tmpClassList.add(tmpStr[i]);
			classNumber = tmpClassList.size();
			this.classNumberList.add(classNumber);
			
			/*** read population data ***/
			while(scan.hasNext()){
				tmpCode = scan.nextInt();
				tmpDistrictType = scan.next();
				tmpName = scan.next();
				districtCode.put(tmpCode, tmpName);
				if(tmpDistrictType.equals("a")) tmpUpperDistrictName = tmpName;
				else if(tmpDistrictType.equals("1")) tmpCityName = tmpName;
				else if(tmpDistrictType.equals("0") && !tmpName.startsWith(tmpCityName))
					tmpDistrictName = tmpUpperDistrictName + tmpCityName + tmpName;
				else tmpDistrictName = tmpUpperDistrictName + tmpName;

				tmpPopulation = new int[classNumber];
				if(targetRegionType.contains(tmpDistrictType)){
					tmpStr = scan.nextLine().trim().split("\t");
					for(i=0 ; i<classNumber ; i++) tmpPopulation[i] = Integer.parseInt(tmpStr[i]);
					tmpDistrictList.add(tmpDistrictName);
					tmpPopulationList.add(tmpPopulation);
				}else scan.nextLine();
			}
			districtNumber = tmpDistrictList.size();

			/*** store data ***/
			this.districtNumberList.add(districtNumber);
			this.districtList.add(tmpDistrictList);
			this.classList.add(tmpClassList);
			this.populationList.add(tmpPopulationList);
			this.districtCodeList.add(districtCode);
			
			scan.close();
		} catch(IOException e) {
			System.err.println(e);
		}
	}
	
	public void readStandardRegionInformation(String classListFile, String regionListFile){
		Scanner scan;
		String regionCode, regionType, regionName;
		String cityName = "";
		String upperDistrictName = "";
		ArrayList<String> targetRegionType = new ArrayList<String>();
		targetRegionType.add("0");
		targetRegionType.add("2");
		targetRegionType.add("3");
		
		/*** read standard class list ***/
		try{
			scan = new Scanner(new File(classListFile));
			
			scan.nextLine();
			while(scan.hasNext()) this.standardClasses.add(scan.next());
			this.totalClass = this.standardClasses.size();
			
			scan.close();
		} catch(IOException e) { System.err.println(e); }
		
		/*** read standard region list ***/
		try{
			scan = new Scanner(new File(regionListFile));
			
			scan.nextLine();
			while(scan.hasNext()){
				regionCode = scan.next().trim();
				regionType = scan.next().trim();
				regionName = scan.next().trim();
				
				if(regionType.equals("a")) upperDistrictName = regionName;
				else if(regionType.equals("1")) cityName = regionName;
				else if(regionType.equals("0") && !regionName.startsWith(cityName)){
					this.standardRegions.add(upperDistrictName + cityName + regionName);		
					this.standardCode.add(regionCode);
				}
				else if(targetRegionType.contains(regionType)){ 
					this.standardRegions.add(upperDistrictName+regionName);
					this.standardCode.add(regionCode);
				}
				
			}
			this.totalDistrict = this.standardRegions.size();
			
			scan.close();
		} catch(IOException e) { System.err.println(e); }
	}
	
	public void makeMergingList(String standardListFile){
		String regionType, previousType = "";
		String upperDistrictName = "";
		String regionName, mergedName, standardName = "";	

		ArrayList<String> targetRegionType = new ArrayList<String>();
		targetRegionType.add("0");
		targetRegionType.add("2");
		targetRegionType.add("3");
		
		/*** make merging list ***/
		try{
			Scanner scan = new Scanner(new File(standardListFile));
			
			scan.nextLine();
			while(scan.hasNext()){
				scan.next();
				regionType = scan.next().trim();
				regionName = scan.next().trim();
				
				if(regionType.equals("a")) upperDistrictName = regionName;
				else if(targetRegionType.contains(regionType)) standardName = upperDistrictName+regionName;
				else if(regionType.equals("9") && targetRegionType.contains(previousType)){
					mergedName = upperDistrictName+regionName.substring(5, regionName.length()-1);
					this.mergedDistrictList.put(mergedName, standardName);
				}	
				
				if(!regionType.equals("9")) previousType = regionType;
			}
			scan.close();
		} catch(IOException e) { System.err.println(e); }
	}
	
	public void mergeDistricts(int mergedYear){
		String tmpRegion;
		ArrayList<String> tmpMergedRegionList = this.districtList.get(this.yearList.indexOf(mergedYear));
		
		for(int i=0 ; i<tmpMergedRegionList.size() ; i++){
			tmpRegion = tmpMergedRegionList.get(i);
			if(this.mergedDistrictList.containsKey(tmpRegion))
				tmpMergedRegionList.set(i, this.mergedDistrictList.get(tmpRegion));
		}
	}
	
	public void checkDuplication(int year){
		int i, j;
		int count;
		String tmpRegion;
		ArrayList<String> tmpRegionList = this.districtList.get(this.yearList.indexOf(year));
		
		for(i=0 ; i<tmpRegionList.size() ; i++){
			count = 0;
			tmpRegion = tmpRegionList.get(i);
			for(j=0 ; j<tmpRegionList.size() ; j++) if(tmpRegion.equals(tmpRegionList.get(j))) count++;
			if(count != 1) System.err.println(year+" data '"+tmpRegion+"' is duplicated "+count+" times");  
		}
	}
	
	public void setMaxMinPopulation(){
		int i, j, k;
		int max, min;
		int population;
		
		/*** set max, min class population ***/
		for(i=0 ; i<this.pData.getNumberOfYears() ; i++){
			for(k=0 ; k<this.pData.getClassNumber() ; k++){
				max = 0;
				min = 100000000;	//possible maximum population
				for(j=0 ; j<this.pData.getDistricNumber() ; j++){
					population = this.pData.getPopulation(i, j, k);
					if(population >= 0){
						if(max < population) max = population;
						if(population > 0 && min > population) min = population;		
					}			
				}
				this.pData.setMaxPopulation(i, k, max);
				this.pData.setMinPopulation(i, k, min);
			}
		}
		
		/*** set max, min region population ***/
		for(j=0 ; j<this.pData.getDistricNumber() ; j++){
			max = 0;
			min = 100000000;	//possible maximum population
			for(i=0 ; i<this.pData.getNumberOfYears() ; i++){
				population = this.pData.getPopulation(i, j);
				if(population >= 0){
					if(max < population) max = population;
					if(population > 0 && min > population) min = population;		
				}
			}
			this.pData.setMaxRegionPopulation(j, max);
			this.pData.setMinRegionPopulation(j, min);
		}
	}
	
	public void printData(String outputFile){
		int i, j, k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.print("district\tyear\t");
			for(i=0 ; i<this.pData.getClassNumber() ; i++) pw.print(this.pData.getClassName(i)+"\t");
			pw.println();
		
			for(i=0 ; i<this.pData.getDistricNumber() ; i++){					
				for(j=0 ; j<this.pData.getNumberOfYears() ; j++){
					pw.print(this.pData.getDistrictName(i));
					pw.print("\t"+this.pData.getYears()[j]);
					for(k=0 ; k<this.pData.getClassNumber() ; k++) pw.print("\t"+this.pData.getPopulation(j, i, k));
					pw.println();
				}
			}
			pw.close();
		}catch(IOException e) {}			
	}
	
	public static void main(String[] args) {
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/Japan/";
		String stdClassFile = filePath+"standard_class.txt";
		String stdRegionFile = filePath+"region_merged_list.txt";
		int[] years = {2000, 2005};
		String[] inputFile = new String[years.length];
		inputFile[0] = filePath + "population_japan_2000.txt";
		inputFile[1] = filePath + "population_japan_2005.txt";
		String outputFile = filePath + "population_japan_output.txt";
		
		DataReader_Japan drj = new DataReader_Japan();
		drj.readStandardRegionInformation(stdClassFile, stdRegionFile);
		drj.makeMergingList(stdRegionFile);
		drj.readPopulationData(years, inputFile);
		drj.printData(outputFile);	
	}

}
