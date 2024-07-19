package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import populationEntropy.data.*;

public class DataReader {

	int numberOfIndex;
	ArrayList<String> firstLevelDistrictIndicators;
	PopulationData pData;
	
	public DataReader(){
		pData = new PopulationData();
	}
	
	public PopulationData getPopulationData(String inputFile){
		if(this.firstLevelDistrictIndicators == null || this.firstLevelDistrictIndicators.isEmpty()) 
			this.setFirstLevelDistrictIndicator();
		this.readPopulationData(inputFile);
		return this.pData;
	}
	
	public void  readPopulationData(String inputFile, PopulationData dataSet){
		if(this.firstLevelDistrictIndicators == null || this.firstLevelDistrictIndicators.isEmpty()) 
			this.setFirstLevelDistrictIndicator();
		this.readPopulationData(inputFile);
		dataSet =  this.pData;
	}
	
	public void setFirstLevelDistrictIndicator(){
		this.firstLevelDistrictIndicators = new ArrayList<String>();
		this.firstLevelDistrictIndicators.add("특별시");
		this.firstLevelDistrictIndicators.add("광역시");
		this.firstLevelDistrictIndicators.add("도");
		this.firstLevelDistrictIndicators.add("특별자치시");
	}
	
	public void readPopulationData(String inputFile){
		int i;
		int totalYears;
		
		int classNumber;				//number of age groups
		int districtNumber;			//number of districts
		int districtType = 1;		//[0]:si-doo, [1]:si-gon-gu, [2]:eup-myung-dong
		
		int tmpInt, tmpDistrictIndex, tmpClassIndex;
		int[] tmpYearList;
		String tmpName, tmpDistrictName;
		String[] tmpStr;
		ArrayList<Integer> tmpIntList = new ArrayList<Integer>();
		ArrayList<String> tmpDistrictList = new ArrayList<String>();
		ArrayList<String> tmpClassList = new ArrayList<String>();
		
		boolean lowerLevelCheck;
		this.numberOfIndex = 2;

		/*** reading process ***/
		try{
			File file = new File(inputFile);
			
			/*** pre-process of population analysis ***/
			Scanner scan = new Scanner(file);
			
			tmpInt = 0;
			tmpIntList.clear();		
			tmpStr = scan.nextLine().split("\t");
			for(i=this.numberOfIndex ; i<tmpStr.length ; i++){
				if(tmpInt < Integer.parseInt(tmpStr[i])){
					tmpInt = Integer.parseInt(tmpStr[i]);
					tmpIntList.add(tmpInt);
				}
			}
			totalYears = tmpIntList.size();
			tmpYearList = new int[totalYears];
			for(i=0 ; i<totalYears ; i++) tmpYearList[i] = tmpIntList.get(i);
			
			tmpDistrictName = null;
			while(scan.hasNext()){
				tmpStr = scan.nextLine().split("\t");
				lowerLevelCheck = true;
				for(i=0 ; i<this.firstLevelDistrictIndicators.size() ; i++){
					if(tmpStr[0].endsWith(this.firstLevelDistrictIndicators.get(i))){
						tmpDistrictName = tmpStr[0];
						lowerLevelCheck = false;
					}
				}
				if(lowerLevelCheck){
					tmpName = tmpDistrictName + tmpStr[0];
					if(!tmpDistrictList.contains(tmpName)) tmpDistrictList.add(tmpName);
				}
			
				if(!tmpClassList.contains(tmpStr[this.numberOfIndex-1])) 
					tmpClassList.add(tmpStr[this.numberOfIndex-1]);
			}
			classNumber = tmpClassList.size();
			districtNumber = tmpDistrictList.size();
			scan.close();
			
			/*** initiate population data ***/
			this.pData.initiate(tmpYearList, classNumber, districtNumber, districtType);
			for(i=0 ; i<classNumber ; i++) this.pData.setClassName(i, tmpClassList.get(i));
			for(i=0 ; i<districtNumber ; i++) this.pData.setDistrictName(i, tmpDistrictList.get(i));
			
			/*** read population data ***/
			scan = new Scanner(file);
						
			scan.nextLine();
			while(scan.hasNext()){
				tmpStr = scan.nextLine().split("\t");
				lowerLevelCheck = true;
				for(i=0 ; i<this.firstLevelDistrictIndicators.size() ; i++){
					if(tmpStr[0].endsWith(this.firstLevelDistrictIndicators.get(i))){
						tmpDistrictName = tmpStr[0];
						lowerLevelCheck = false;
					}
				}
				if(lowerLevelCheck){
					tmpName = tmpDistrictName + tmpStr[0];
					tmpDistrictIndex = tmpDistrictList.indexOf(tmpName);
					tmpClassIndex = tmpClassList.indexOf(tmpStr[this.numberOfIndex-1]);
					
					for(i=this.numberOfIndex ; i< tmpStr.length ; i++){
						if(tmpStr[i].trim().isEmpty() || Double.parseDouble(tmpStr[i]) < 0) tmpInt = -1;
						else tmpInt = (int) Double.parseDouble(tmpStr[i]);
						
						this.pData.setPopulation(i-this.numberOfIndex, tmpDistrictIndex, tmpClassIndex, tmpInt);
					}
				}
			}
			scan.close();
		} catch(IOException e) {
			System.err.println(e);
		}
		
		this.removeEmptyData();
		this.setMaxMinPopulation();
	}
	
	public void addPopulationData(String inputFile){
		int i, j, k;;	
		int totalYears, addingYear;
		int[] tmpYearList;
		int tmpInt, tmpDistrictIndex, tmpClassIndex;
		String tmpName;
		String tmpDistrictName = null;
		String[] tmpStr;
		int classNumber = this.pData.getClassNumber();			//number of age groups
		int districtNumber = this.pData.getDistricNumber();	//number of districts
		int districtType = this.pData.getDistricClass();				//[0]:si-doo, [1]:si-gon-gu, [2]:eup-myung-dong
		boolean lowerLevelCheck;
		ArrayList<Integer> tmpIntList = new ArrayList<Integer>();
		ArrayList<String> tmpDistrictList = new ArrayList<String>();
		ArrayList<String> tmpClassList = new ArrayList<String>();
		ArrayList<String> excludedClassList = new ArrayList<String>();
		
		PopulationData addedPData = new PopulationData();

		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			/*** get information from original population data ***/
			tmpStr = scan.nextLine().split("\t");
			for(i=0 ; i<pData.getNumberOfYears() ; i++) tmpIntList.add(pData.getYears()[i]);
			for(i=this.numberOfIndex ; i<tmpStr.length ; i++)
				if(!tmpIntList.contains(Integer.parseInt(tmpStr[i]))) tmpIntList.add(Integer.parseInt(tmpStr[i]));
			totalYears = tmpIntList.size();
			tmpYearList = new int[totalYears];
			for(i=0 ; i<tmpIntList.size() ; i++) tmpYearList[i] = tmpIntList.get(i);
			for(i=0 ; i<classNumber ; i++) tmpClassList.add(this.pData.getClassName(i));
			for(i=0 ; i<classNumber ; i++) excludedClassList.add(this.pData.getClassName(i));
			for(i=0 ; i<districtNumber ; i++) tmpDistrictList.add( this.pData.getDistrictName(i));
			
			/*** check added year, district, and age-class ***/
			while(scan.hasNext()){
				tmpStr = scan.nextLine().split("\t");
				lowerLevelCheck = true;
				for(i=0 ; i<this.firstLevelDistrictIndicators.size() ; i++){
					if(tmpStr[0].endsWith(this.firstLevelDistrictIndicators.get(i))){
						tmpDistrictName = tmpStr[0];
						lowerLevelCheck = false;
					}
				}
				if(lowerLevelCheck){
					tmpName = tmpDistrictName + tmpStr[0];
					if(!tmpDistrictList.contains(tmpName)) tmpDistrictList.add(tmpName);
				}
			
				if(!tmpClassList.contains(tmpStr[this.numberOfIndex-1])) 
					tmpClassList.add(tmpStr[this.numberOfIndex-1]);
				if(excludedClassList.contains(tmpStr[this.numberOfIndex-1])) 
					excludedClassList.remove(tmpStr[this.numberOfIndex-1]);
			}
			classNumber = tmpClassList.size();
			districtNumber = tmpDistrictList.size();
			
			/*** initiate revising population data ***/
			addedPData.initiate(tmpYearList, classNumber, districtNumber, districtType);
			for(i=0 ; i<classNumber ; i++) addedPData.setClassName(i, tmpClassList.get(i));
			for(i=0 ; i<districtNumber ; i++) addedPData.setDistrictName(i, tmpDistrictList.get(i));
			for(i=0 ; i<this.pData.getNumberOfYears()  ; i++){
				for(j=0 ; j<this.pData.getDistricNumber() ; j++){
					for(k=0  ; k<this.pData.getClassNumber() ; k++) 
						addedPData.setPopulation(i, j, k, this.pData.getPopulation(i, j, k));
					for(k=this.pData.getClassNumber()  ; k<classNumber ; k++) 
						addedPData.setPopulation(i, j, k, -1);
				}
				for(j=this.pData.getDistricNumber() ; j<districtNumber ; j++)
					for(k=0 ; k<classNumber ; k++) addedPData.setPopulation(i, j, k, -1);			
			}
			scan.close();
			
			/*** add population data ***/
			scan = new Scanner(file);
			scan.nextLine();
			while(scan.hasNext()){
				tmpStr = scan.nextLine().split("\t");
				lowerLevelCheck = true;
				for(i=0 ; i<this.firstLevelDistrictIndicators.size() ; i++){
					if(tmpStr[0].endsWith(this.firstLevelDistrictIndicators.get(i))){
						tmpDistrictName = tmpStr[0];
						lowerLevelCheck = false;
					}
				}
				if(lowerLevelCheck){
					tmpName = tmpDistrictName + tmpStr[0];
					tmpDistrictIndex = tmpDistrictList.indexOf(tmpName);
					tmpClassIndex = tmpClassList.indexOf(tmpStr[this.numberOfIndex-1]);
					
					for(i=this.numberOfIndex ; i<tmpStr.length ; i++){
						if(tmpStr[i].trim().isEmpty() || Integer.parseInt(tmpStr[i]) < 0) tmpInt = -1;
						else if(!tmpStr[i].trim().isEmpty()) tmpInt =  Integer.parseInt(tmpStr[i]);
						else tmpInt = -1;
						addingYear = i - this.numberOfIndex + this.pData.getNumberOfYears();
						addedPData.setPopulation(addingYear, tmpDistrictIndex, tmpClassIndex, tmpInt);
					}
				}
			}
			for(i=this.pData.getNumberOfYears() ; i<tmpYearList.length ; i++){
				for(j=0 ; j<districtNumber ; j++){
					for(k=0 ; k<excludedClassList.size() ; k++){
						tmpClassIndex = tmpClassList.indexOf(excludedClassList.get(k));
						addedPData.setPopulation(i, j, tmpClassIndex, -1);
					}
				}
			}
			scan.close();
		} catch(IOException e) {
			System.err.println(e);
		}
		
		this.pData = addedPData;
		this.setMaxMinPopulation();
	}
	
	public PopulationData getData(String inputFile){
		this.readData(inputFile);
		return this.pData;
	}
	
	public void readData(String inputFile){
	
		int i,j,k;
		int count;
		int duration;
		
		int startYear, endYear;		//start and end years of statistics
		int classNumber;			//number of age groups
		int districtNumber;			//number of districts
		int districtType;			//[0]:si-doo, [1]:si-gon-gu, [2]:eup-myung-dong
		
		
		try{
			File file = new File(inputFile);
			
			/*** check population data ***/
			
			Scanner scan = new Scanner(file);
		
			count = 0;			
			scan.next();
			startYear = scan.nextInt();
			scan.next();
			endYear = scan.nextInt();
			scan.next();
			classNumber = scan.nextInt();
			scan.next();
			districtType = scan.nextInt();
			
			duration = endYear - startYear + 1;
			
			for(i=0 ; i<duration+2 ; i++) scan.next();
		
			while(scan.hasNext()){
				scan.next();
				for(i=0 ; i<classNumber ; i++) for(j=0 ; j<duration+1 ; j++) scan.next();
				count++;				
			}
			districtNumber = count;
			
			scan.close();	
			
			
			/*** read population data ***/
			
			scan = new Scanner(file);
			this.pData.initiate(startYear, endYear, classNumber, districtNumber, districtType);
						
			for(i=0 ; i<8 ; i++) scan.next();			
			for(i=0 ; i<this.pData.getNumberOfYears()+2 ; i++) scan.next();
					
			for(i=0 ; i<districtNumber ; i++){				
				this.pData.setDistrictName(i, scan.next());
				for(j=0 ; j<classNumber ; j++){					
					if(i==0) this.pData.setClassName(j, scan.next());
					else scan.next();
						
					for(k=0 ; k<duration ; k++){
						if(scan.hasNextInt()) this.pData.setPopulation(k, i, j, scan.nextInt());
						else{
							this.pData.setPopulation(k, i, j, 0);
							scan.next();
						}
					}
				}
			}
			
			scan.close();
			
		} catch(IOException e) {}
		
		this.removeEmptyData();
		this.setMaxMinPopulation();
	}
	
	public void removeEmptyData(){
		int i, j, k;
		int sum;
		
		/*** remove empty year ***/
		for(i=0 ; i<this.pData.getNumberOfYears() ; i++)
			for(j=0 ; j<this.pData.getDistricNumber() ; j++)
				if(this.pData.getPopulation(i, j) == 0)
					for(k=0 ; k<this.pData.getClassNumber() ; k++) this.pData.setPopulation(i, j, k, -1);

		/*** remove empty class ***/
		for(j=0 ; j<this.pData.getDistricNumber() ; j++){
			for(k=0 ; k<this.pData.getClassNumber() ; k++){
				sum = 0;
				for(i=0 ; i<this.pData.getNumberOfYears() ; i++) 
					if(this.pData.getPopulation(i, j, k)>0) sum += this.pData.getPopulation(i, j, k);
				if(sum == 0) 
					for(i=0 ; i<this.pData.getNumberOfYears() ; i++) this.pData.setPopulation(i, j, k, -1);
			}
		}
	}
	
	public void removeRedundancy(){
		for(int i=0 ; i<this.pData.getDistricNumber()-1 ; i++)
			if(pData.getDistrictName(i+1).startsWith(pData.getDistrictName(i))) pData.removeDistrict(i--);
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
					if(population > 0){
						if(max < population) max = population;
						if(min > population) min = population;		
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
				if(population > 0){
					if(max < population) max = population;
					if(min > population) min = population;		
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
	
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		//String inputFile = "population_nation.txt";
		String inputFile = filePath + "population_survey_data.txt";
		String outputFile = filePath + "populatoin_data_output.txt";

		
		inputFile = filePath + "migration/population_registered_data.txt";
		String inputFile2 = filePath + "migration/population_registered_data2.txt";
		outputFile = filePath + "migration/populatoin_data_output.txt";
		
		DataReader dr = new DataReader();
		dr.setFirstLevelDistrictIndicator();
		dr.readPopulationData(inputFile);
		dr.addPopulationData(inputFile2);
		dr.removeRedundancy();
		dr.printData(outputFile);
		/*
		PopulationData dataSet = new PopulationData();		
		System.out.print("data reading: ");
		dr.readData(inputFile, dataSet);
		System.out.println("complete");
		System.out.print("data printing: ");
		dr.printData(outputFile, dataSet);
		System.out.println("complete");
		*/
		
	}

}
