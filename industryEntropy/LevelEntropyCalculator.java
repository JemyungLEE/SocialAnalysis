package industryEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import industryEntropy.data.*;
import populationEntropy.data.EntropyData;

public class LevelEntropyCalculator {

	/**
	 *  Subject: Regional industry level entropy calculator
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2014.5.19
	 *  Last Modified Data: 2014.5.19 
	 *  Department: Seoul Nat. Univ. depart. of Rural Systems Engineering
	 *  Description: Entropy of profit level classified industry  
	 */

	int startYear, endYear;
	int duration;
	int levelNumber;
	int[] n_region;
	int[] n_industry;
	
	int industryClassDepth;		//2:2nd_jung, 3: 3rd_so order depth
	int regionClassDepth;		//2:si_do,    5: si_gun_gu,   7: eup_myun_dong
	
	int[] industClassKey;		//2: 중분류,  3: 소분류,   4: 세분류,	5:세세분류
	int[] regionClassKey;		//2: 시도,    5: 시군구,   7: 읍면동
	String[] industClassName;
	String[] regionClassName;
	
	HashMap<String, String> locationList;	//<code, name>
	ArrayList<String> locationNameList;		//name list of all years, all regions 
	ArrayList<Integer> locationCodeList;	//code list of all years, all regions 
	
	ArrayList<HashMap<String, String>> locationHashMap;	//[year] <code, name>
	ArrayList<HashMap<Integer, String>> industryHashMap;	//[year] <code, name>
	ArrayList<ArrayList<Integer>> locationCode;				//[year] use to find region's index	
	ArrayList<ArrayList<Integer>> industryCode;				//[year] use to find industry's index	
	ArrayList<ArrayList<String>> locationName;				//[year] use to find region's index	
	ArrayList<ArrayList<String>> industryName;				//[year] use to find industry's index


	double[] totalEmployee;						//[year]
	ArrayList<double[]>   employeeRegion;		//[year] [region]
	ArrayList<double[]>   employeeIndustry;		//[year] [industry]
	ArrayList<double[][]> employee; 			//[year] [region][industry]
	ArrayList<double[][]> employeeClass;		//[year] [region][class]
	
	ArrayList<double[]>   meanRegion;			//[year] [region]
	ArrayList<double[][]> ratioRegion;		 	//[year] [region][class]
	
	ArrayList<double[]>   entropy;				//[year] [region]
	ArrayList<double[]>   attraction;			//[year] [region]
	
	public LevelEntropyCalculator(){
		
	}	
	
	public LevelEntropyCalculator(int start, int end){
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;
		
		this.initiate();
		this.setIndexMap();
	}
		
	public LevelEntropyCalculator(int start, int end, int industryClass, int regionClass){
		
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;

		this.initiate();
		this.setIndexMap();
		this.setClassDepth(industryClass, regionClass);
	}
	
	public void initiate(){
		
		this.n_region		= new int[this.duration];
		this.n_industry		= new int[this.duration];
		this.totalEmployee	= new double[this.duration];
		this.employee		= new ArrayList<double[][]>();
		this.employeeClass	= new ArrayList<double[][]>();
		this.employeeRegion	= new ArrayList<double[]>();
		this.employeeIndustry = new ArrayList<double[]>();
		
		this.meanRegion		= new ArrayList<double[]>();
		this.ratioRegion	= new ArrayList<double[][]>();
				
		this.entropy		= new ArrayList<double[]>();
		this.attraction		= new ArrayList<double[]>();
	}
	
	public void setIndexMap(){		
		
		this.locationList = new HashMap<String, String>();
		this.locationCodeList = new ArrayList<Integer>();	
		this.locationNameList = new ArrayList<String>();		
		
		this.locationHashMap = new ArrayList<HashMap<String, String>>();
		this.industryHashMap = new ArrayList<HashMap<Integer, String>>();		
		this.locationCode = new ArrayList<ArrayList<Integer>>();			
		this.industryCode = new ArrayList<ArrayList<Integer>>();		
		this.locationName = new ArrayList<ArrayList<String>>();		
		this.industryName = new ArrayList<ArrayList<String>>();	
		
		for(int i=0 ; i<this.duration ; i++){
			this.locationHashMap.add(new HashMap<String, String>());
			this.industryHashMap.add(new HashMap<Integer, String>());		
			this.locationCode.add(new ArrayList<Integer>());			
			this.industryCode.add(new ArrayList<Integer>());		
			this.locationName.add(new ArrayList<String>());		
			this.industryName.add(new ArrayList<String>());				
		}
	}
	
	public void setClassDepth(int industryClass, int regionClass){
		industClassKey = new int[4];
		regionClassKey = new int[3];
		industClassName = new String[4];
		regionClassName = new String[3];
		
		industClassKey[0] = 2;
		industClassKey[1] = 3;
		industClassKey[2] = 4;
		industClassKey[3] = 5;
		regionClassKey[0] = 2;
		regionClassKey[1] = 5;
		regionClassKey[2] = 7;
		industClassName[0] = "1st";
		industClassName[1] = "2nd";
		industClassName[2] = "3rd";
		industClassName[2] = "4th";
		regionClassName[0] = "do";
		regionClassName[1] = "gun";
		regionClassName[2] = "myun";
		
		this.industryClassDepth = this.industClassKey[industryClass];
		this.regionClassDepth = this.regionClassKey[regionClass];
	}
	
	public void readLocationCode(int year, String inputFile){		
		int count = 0;
		int tmpCodeIndex;
		String tmpCode, tmpName;
		HashMap<String, String> fullRegion = new HashMap<String, String>();
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){
				tmpCode = scan.next().trim();
				tmpName = scan.next().trim();	
				tmpCodeIndex = Integer.parseInt(tmpCode);
				
				this.locationHashMap.get(year).put(tmpCode, tmpName);
				if(tmpCode.length() == 2) fullRegion.put(tmpCode, tmpName);
				else if(tmpCode.length() == 5)
					fullRegion.put(tmpCode, fullRegion.get(tmpCode.substring(0, 2))+tmpName);
				else if(tmpCode.length() == 7)
					fullRegion.put(tmpCode, fullRegion.get(tmpCode.substring(0, 5))+tmpName);
				
				if(tmpCode.length() == this.regionClassDepth){						
					this.locationCode.get(year).add(tmpCodeIndex);
					this.locationName.get(year).add(fullRegion.get(tmpCode));
					count++;
				}		
			}				
			this.n_region[year] = count;
						
			scan.close();	
		} catch(IOException e) {
			System.err.println("location code reading error.");
		}			
	}
	
	public void readLocationCodeList(String inputFile){	
		int tmpCodeIndex;
		String tmpCode, tmpName;
		HashMap<String, String> fullRegion = new HashMap<String, String>();
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){
				tmpCode = scan.next().trim();
				tmpName = scan.next().trim();	
				tmpCodeIndex = Integer.parseInt(tmpCode);
				
				this.locationList.put(tmpCode, tmpName);
				if(tmpCode.length() == 2) fullRegion.put(tmpCode, tmpName);
				else if(tmpCode.length() == 5)
					fullRegion.put(tmpCode, fullRegion.get(tmpCode.substring(0, 2))+tmpName);
				else if(tmpCode.length() == 7)
					fullRegion.put(tmpCode, fullRegion.get(tmpCode.substring(0, 5))+tmpName);
				
				if(tmpCode.length() == this.regionClassDepth){						
					this.locationCodeList.add(tmpCodeIndex);
					this.locationNameList.add(fullRegion.get(tmpCode));
				}		
			}				
						
			scan.close();	
		} catch(IOException e) {
			System.err.println("location code reading error.");
		}			
	}
	
	public void readIndustryCode(int year, String inputFile){		
		int count = 0;
		String tmpCode;
		String tmpName;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){
				tmpCode = scan.next().trim();
				tmpName = scan.next().trim();
				if( tmpCode.length() == this.industryClassDepth){
					this.industryHashMap.get(year).put(Integer.parseInt(tmpCode), tmpName);
					this.industryCode.get(year).add(Integer.parseInt(tmpCode));
					this.industryName.get(year).add(tmpName);
					count++;
				}		
			}				
			this.n_industry[year] = count;
			
			scan.close();	
		} catch(IOException e) {
			System.err.println("industry code reading error.");
		}
	}
	
	public void readEmployee(String filePath, String fileName){
		
		int i, j, k;
		int region;
		int industry;
		int regionIndex;
		int industryIndex;
		double workers;
		String inputFile;				
		
		double[][] employee;			//[region][industry]
		
		
		String tmpPath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
		
		for(i=0 ; i<this.duration ; i++){			
			
			System.out.print(" "+(this.startYear+i)+" ");
			
			inputFile = filePath + (this.startYear+i) + fileName;

			this.readIndustryCode(i, tmpPath+"industry_code/"+(this.startYear+i)+"_industry_code.txt");
			this.readLocationCode(i, tmpPath+"location_code/"+(this.startYear+i)+"_location_code.txt");		
			
			
			/*** initiate variables ***/
			employee = new double[this.n_region[i]][this.n_industry[i]];			
			for(j=0 ; j<this.n_region[i] ; j++)	
				for(k=0 ; k<this.n_industry[i] ; k++) employee[j][k] = 0.0;						
							
			/*** extract microdata ***/
			try{
				File file = new File(inputFile);
				Scanner scan = new Scanner(file);
				
				scan.nextLine();	//read column name
				
				while(scan.hasNext()){					
					/*** read micro-data code ***/
					scan.nextInt();		//read founding year				
					region = Integer.parseInt(scan.next().substring(0, this.regionClassDepth));
					industry = Integer.parseInt(scan.next().substring(1, 1+this.industryClassDepth)); 
					workers = scan.nextDouble();
					scan.next();		//read business type
					scan.next();		//read store type
										
					/*** find index ***/
					regionIndex = this.locationCode.get(i).indexOf(region);
					industryIndex = this.industryCode.get(i).indexOf(industry);
					
					//System.out.println(region+"\t"+industry+"\t"+workers+"\t"+regionIndex+"\t"+industryIndex);
					
					/*** add workers ***/
					if(regionIndex >= 0)
						if(industryIndex >= 0)
							employee[regionIndex][industryIndex] += workers;
										
				}
				
				//System.out.println(this.n_region[i]+"\t"+this.n_industry[i]);
				
				scan.close();	
			} catch(IOException e) {}
			
			this.employee.add(employee);
		}
	}		

	
	public void calculateEmployeeStatistics(IndustryData iData){
		
		int i, j, k, l;
		int classNumber = iData.getClassNumber();
		this.levelNumber = classNumber;
		int check;
		int tmpCode;
		double total;
		double[][] employee;		//[region][industry]
		double[][] employeeClass;	//[region][class]
		double[] regionSum;			//[region]
		double[] industrySum;		//[industry]
		double[][] regionRatio;		//[region][class]
		double[] regionAverage;		//[region]


		for(i=0 ; i<this.duration ; i++){
			
			/*** initiate variables ***/
			total = 0.0;
			employee = this.employee.get(i);			
			employeeClass = new double[this.n_region[i]][classNumber];
			regionSum = new double[this.n_region[i]];	
			industrySum = new double[this.n_industry[i]];
			regionRatio = new double[this.n_region[i]][classNumber];
			regionAverage = new double[this.n_region[i]];
			
			for(j=0 ; j<this.n_region[i] ; j++){
				regionSum[j] = 0.0;
				regionAverage[j] = 0.0;
				for(k=0 ; k<classNumber ; k++) employeeClass[j][k] = 0.0;
			}
			for(j=0 ; j<this.n_industry[i] ; j++) industrySum[j] = 0.0;
			
			
			/*** calculate employee sum: total, region, industry ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_industry[i] ; k++){
					regionSum[j] += employee[j][k];
					industrySum[k] += employee[j][k];
					total += employee[j][k];
				}
			}
			
			/*** calculate class employee sum ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_industry[i] ; k++){
					tmpCode = this.industryCode.get(i).get(k); 
					for(l=0 ; l<classNumber ; l++)						
						if(iData.classList.get(l).contains(tmpCode)) employeeClass[j][l] += employee[j][k]; 
				}
				
			}
			
			/*** calculate employee class ratio ***/
			for(j=0 ; j<this.n_region[i] ; j++){				
				for(k=0 ; k<classNumber ; k++){
					if(regionSum[j] > 0) regionRatio[j][k] = employeeClass[j][k] / regionSum[j];
					else if(regionSum[j] == 0) regionRatio[j][k] = 0.0;
					else{
						regionRatio[j][k] = 0.0;
						System.err.println((i+this.startYear)+"\t"+j+"\t"+"probability calculation error");
					}
				}
			}			
			
			/*** calculate regional level average ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_industry[i] ; k++){
					tmpCode = this.industryCode.get(i).get(k);
					if(iData.codeList.contains(tmpCode)){
						if(employee[j][k] > 0)
							regionAverage[j] += iData.getValue(iData.codeList.indexOf(tmpCode)) * employee[j][k];
					}else {
						//System.err.println(j+"\t"+k+"\t"+tmpCode+"\tregion average industry not match");
					}
				}
				if(regionSum[j]>0) regionAverage[j] /= regionSum[j];
				else if(regionSum[j] == 0) regionAverage[j] = 0.0;
				else{
					regionAverage[j] = 0.0;
					System.err.println((i+this.startYear)+"\t"+j+"\t"+"average calculation error");
				}
			}
			
			/*** add statistics ***/
			this.totalEmployee[i] = total;
			this.employeeRegion.add(regionSum);
			this.employeeIndustry.add(industrySum);
			this.employeeClass.add(employeeClass);
			this.ratioRegion.add(regionRatio);
			this.meanRegion.add(regionAverage);
		}
	}
	
	public void calculateIndustryLevelEntropy(){
		
		int i, j, k;
		int classNumber = this.levelNumber;
		double[][] probability;	//[region][class]
		
		double tmpProb;
		double subEntropy;
		double base = Math.log(2);
		double minEnt, maxEnt, minAvg, maxAvg;
		
		double[] entropy;		//[region]
		double[] average;		//[region]
		double[] attraction;		//[region]

		for(i=0 ; i<this.duration ; i++){
			
			//System.out.println((i+this.startYear)+"\t"+this.n_region[i]+"\t"+this.n_industry[i]);
			
			entropy = new double[this.n_region[i]];
			attraction = new double[this.n_region[i]];
			probability = new double[this.n_region[i]][classNumber];
			
			/*** calculate weighted probability ***/
			for(j=0 ; j<this.n_region[i]; j++)
				for(k=0 ; k<classNumber ; k++)
					probability[j][k] = this.ratioRegion.get(i)[j][k];
			
			/*** calculate entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				entropy[j] = 0.0;
				for(k=0 ; k<classNumber ; k++){
					tmpProb = probability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println((i+this.startYear)+"\t"+j+"\t"+k+"\t"+"entropy calculation error");
					}					
					entropy[j] += subEntropy;
				}				
			}			
			this.entropy.add(entropy);
			
			/*** calculate attraction ***/
			average = this.meanRegion.get(i);
			minEnt = entropy[0]; 
			maxEnt = entropy[0]; 
			minAvg = average[0];
			maxAvg = average[0];
			for(j=1 ; j<this.n_region[i]; j++){				
				if(minEnt > entropy[j] && entropy[j] > 0) minEnt = entropy[j];
				if(maxEnt < entropy[j] && entropy[j] > 0) maxEnt = entropy[j];
				if(minAvg > average[j] && average[j] > 0) minAvg = average[j];
				if(maxAvg < average[j] && average[j] > 0) maxAvg = average[j];
			}
			
			for(j=0 ; j<this.n_region[i]; j++){ 
				if(average[j] > 0)				
					attraction[j] = ((entropy[j]-minEnt)/(maxEnt-minEnt)*(1.0 - 0.1) + 0.1) 
									* Math.pow(((average[j]-minAvg)/(maxAvg-minAvg)*(1.0 - 0.1) + 0.1 ),1/Math.E);
				else attraction[j] = 0;
			}
			this.attraction.add(attraction);
		}		
	}
	
	
	public void printEntropy(String outputFile){
		int i,j;
		int tmpIndex;
		int tmpCode;
		String tmpName;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("range:\t"+this.startYear+"\t"+this.endYear);
			pw.println();
			pw.print("N_class:\t"+this.levelNumber);
			pw.println();
			pw.print("Max_entropy:\t"+(Math.log(this.levelNumber)/Math.log(2)));
			pw.println();	
			pw.println();	
			
			pw.print("region");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			
			for(i=0 ; i<this.locationNameList.size() ; i++){	
				tmpCode = this.locationCodeList.get(i); 
				tmpName = this.locationNameList.get(i);
				pw.print(tmpName);			
				for(j=0 ; j<this.duration ; j++){
					if(this.locationCode.get(j).contains(tmpCode)){
						tmpIndex = this.locationCode.get(j).indexOf(tmpCode);
						pw.print("\t"+this.entropy.get(j)[tmpIndex]);	
					}else pw.print("\t0");					
				}
				pw.println();
			}			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printAverage(String outputFile){
		int i,j;
		int tmpIndex;
		int tmpCode;
		String tmpName;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("range:\t"+this.startYear+"\t"+this.endYear);
			pw.println();
			pw.print("N_class:\t"+this.levelNumber);
			pw.println();
			pw.print("Max_entropy:\t"+(Math.log(this.levelNumber)/Math.log(2)));
			pw.println();	
			pw.println();	
			
			pw.print("region");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			
			for(i=0 ; i<this.locationNameList.size() ; i++){	
				tmpCode = this.locationCodeList.get(i); 
				tmpName = this.locationNameList.get(i);
				pw.print(tmpName);			
				for(j=0 ; j<this.duration ; j++){
					if(this.locationCode.get(j).contains(tmpCode)){
						tmpIndex = this.locationCode.get(j).indexOf(tmpCode);
						pw.print("\t"+this.meanRegion.get(j)[tmpIndex]);	
					}else pw.print("\t0");					
				}
				pw.println();
			}			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printAttraction(String outputFile){
		int i,j;
		int tmpIndex;
		int tmpCode;
		String tmpName;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("range:\t"+this.startYear+"\t"+this.endYear);
			pw.println();
			pw.print("N_class:\t"+this.levelNumber);
			pw.println();
			pw.print("Max_entropy:\t"+(Math.log(this.levelNumber)/Math.log(2)));
			pw.println();	
			pw.println();	
			
			pw.print("region");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			
			for(i=0 ; i<this.locationNameList.size() ; i++){	
				tmpCode = this.locationCodeList.get(i); 
				tmpName = this.locationNameList.get(i);
				pw.print(tmpName);			
				for(j=0 ; j<this.duration ; j++){
					if(this.locationCode.get(j).contains(tmpCode)){
						tmpIndex = this.locationCode.get(j).indexOf(tmpCode);
						pw.print("\t"+this.attraction.get(j)[tmpIndex]);	
					}else pw.print("\t0");					
				}
				pw.println();
			}			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void transformEntropyToEntropyData(EntropyData eData){
		int i, j;
		int years = eData.getNumberOfYears();
		int regions = eData.getDistricNumber();
		int[][] index = new int[years][regions];		//index of regions in this variables
		int tmpIndex;
		int tmpCode;
		String tmpName;	
		double tmpEntropy;
		double[] max = new double[years];
		double[] min = new double[years];
		
		/*** match index ***/
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<regions ; j++){
				tmpName = eData.getDistrictName(j);
				if(this.locationNameList.contains(tmpName)){ 
					tmpIndex = this.locationNameList.indexOf(tmpName);
					tmpCode = this.locationCodeList.get(tmpIndex);
					if(this.locationCode.get(i).contains(tmpCode)){
						index[i][j] = this.locationCode.get(i).indexOf(tmpCode);	
					}else index[i][j] = -1;					
				}else{
					index[i][j] = -1;
					System.err.println(tmpName+" region miss match");
				}
			}
		}
		
		/*** transfer entropy values ***/
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<regions ; j++){
				if(index[i][j]>=0) eData.setEntropy(i, j, this.entropy.get(i)[index[i][j]]);
				else eData.setEntropy(i, j, 0); 
			}
		}
		
		/*** Find max and min entropy ***/
		for(i=0 ; i<years ; i++){
			tmpEntropy = eData.getEntropy(i, 0); 
			max[i] = tmpEntropy;
			min[i] = tmpEntropy;
			
			for(j=1 ; j<regions ; j++){
				tmpEntropy = eData.getEntropy(i, j);
				if(tmpEntropy > max[i] && tmpEntropy > 0) max[i] = tmpEntropy;
				if(tmpEntropy < min[i] && tmpEntropy > 0) min[i] = tmpEntropy;
			}
		}		
		
		/*** normalize entropy values ***/		
		
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<regions ; j++){
				if(index[i][j]>=0 && eData.getEntropy(i, j) > 0){
					tmpEntropy = (eData.getEntropy(i, j) - min[i]) / (max[i] - min[i]);
					eData.setEntropy(i, j, tmpEntropy);
				}else eData.setEntropy(i, j, 0); 
			}
			
			eData.setMaxEntropy(i, 1.0);
			eData.setMinEntropy(i, 0.0);
		}
				
	}
	
	public void transformAverageToEntropyData(EntropyData eData){
		int i, j;
		int years = eData.getNumberOfYears();
		int regions = eData.getDistricNumber();
		int[][] index = new int[years][regions];		//index of regions in this variables
		int tmpIndex;
		int tmpCode;
		String tmpName;	
		double tmpEntropy;
		double[] max = new double[years];
		double[] min = new double[years];
		
		/*** match index ***/
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<regions ; j++){
				tmpName = eData.getDistrictName(j);
				if(this.locationNameList.contains(tmpName)){ 
					tmpIndex = this.locationNameList.indexOf(tmpName);
					tmpCode = this.locationCodeList.get(tmpIndex);
					if(this.locationCode.get(i).contains(tmpCode)){
						index[i][j] = this.locationCode.get(i).indexOf(tmpCode);	
					}else index[i][j] = -1;					
				}else{
					index[i][j] = -1;
					System.err.println(tmpName+" region miss match");
				}
			}
		}
		
		/*** transfer entropy values ***/
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<regions ; j++){
				if(index[i][j]>=0) eData.setEntropy(i, j, this.meanRegion.get(i)[index[i][j]]);
				else eData.setEntropy(i, j, 0); 
			}
		}
		
		/*** Find max and min entropy ***/
		for(i=0 ; i<years ; i++){
			tmpEntropy = eData.getEntropy(i, 0); 
			max[i] = tmpEntropy;
			min[i] = tmpEntropy;
			
			for(j=1 ; j<regions ; j++){
				tmpEntropy = eData.getEntropy(i, j);
				if(tmpEntropy > max[i] && tmpEntropy > 0) max[i] = tmpEntropy;
				if(tmpEntropy < min[i] && tmpEntropy > 0) min[i] = tmpEntropy;
			}
		}		
		
		/*** normalize entropy values ***/		
		
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<regions ; j++){
				if(index[i][j]>=0 && eData.getEntropy(i, j) > 0){
					tmpEntropy = (eData.getEntropy(i, j) - min[i]) / (max[i] - min[i]);
					eData.setEntropy(i, j, tmpEntropy);
				}else eData.setEntropy(i, j, 0); 
			}
			
			eData.setMaxEntropy(i, 1.0);
			eData.setMinEntropy(i, 0.0);
		}		
	}
	
	public void transformAttractionToEntropyData(EntropyData eData){
		int i, j;
		int years = eData.getNumberOfYears();
		int regions = eData.getDistricNumber();
		int[][] index = new int[years][regions];		//index of regions in this variables
		int tmpIndex;
		int tmpCode;
		String tmpName;	
		double tmpEntropy;
		double[] max = new double[years];
		double[] min = new double[years];
		
		/*** match index ***/
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<regions ; j++){
				tmpName = eData.getDistrictName(j);
				if(this.locationNameList.contains(tmpName)){ 
					tmpIndex = this.locationNameList.indexOf(tmpName);
					tmpCode = this.locationCodeList.get(tmpIndex);
					if(this.locationCode.get(i).contains(tmpCode)){
						index[i][j] = this.locationCode.get(i).indexOf(tmpCode);	
					}else index[i][j] = -1;					
				}else{
					index[i][j] = -1;
					System.err.println(tmpName+" region miss match");
				}
			}
		}
		
		/*** transfer entropy values ***/
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<regions ; j++){
				if(index[i][j]>=0) eData.setEntropy(i, j, this.attraction.get(i)[index[i][j]]);
				else eData.setEntropy(i, j, 0); 
			}
		}
		
		/*** Find max and min entropy ***/
		for(i=0 ; i<years ; i++){
			tmpEntropy = eData.getEntropy(i, 0); 
			max[i] = tmpEntropy;
			min[i] = tmpEntropy;
			
			for(j=1 ; j<regions ; j++){
				tmpEntropy = eData.getEntropy(i, j);
				if(tmpEntropy > max[i] && tmpEntropy > 0) max[i] = tmpEntropy;
				if(tmpEntropy < min[i] && tmpEntropy > 0) min[i] = tmpEntropy;
			}
		}		
		
		/*** normalize entropy values ***/		
		
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<regions ; j++){
				if(index[i][j]>=0 && eData.getEntropy(i, j) > 0){
					tmpEntropy = (eData.getEntropy(i, j) - min[i]) / (max[i] - min[i]);
					eData.setEntropy(i, j, tmpEntropy);
				}else eData.setEntropy(i, j, 0); 
			}
			
			eData.setMaxEntropy(i, 1.0);
			eData.setMinEntropy(i, 0.0);
		}	
	}
	
	public void processEntropyTransfer(int start,int end,int classNumber,IndustryData iData,EntropyData eData){
		
		int industryClass = 3;		//0: 중분류,  1: 소분류,   2: 세분류,	3:세세분
		int regionClass = 1;			//0: 시도,    1: 시군구,   2: 읍면동

		String[] industClassName = {"1st","2nd","3rd","4th"} ;
		String[] regionClassName ={"do", "gun", "myun"};
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
		String locationCodeFile = filePath+"location_code/location_code.txt";
		String classFile = filePath + "profit/industry_profit.txt";
		
		IndustryClassifier ic = new IndustryClassifier(industryClass, classNumber);
		
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;

		this.initiate();
		this.setIndexMap();
		this.setClassDepth(industryClass, regionClass);
		this.readEmployee(filePath+"extracted/", "_microdataCode.txt");
		this.readLocationCodeList(locationCodeFile);
		ic.readIndustryData(classFile, iData);
		ic.setIndustryLevel(classNumber, iData);
		this.calculateEmployeeStatistics(iData);
		this.calculateIndustryLevelEntropy();
		this.transformEntropyToEntropyData(eData);	
		
	}
	
	public void processAverageTransfer(int start,int end,int classNumber,IndustryData iData,EntropyData eData){
		
		int industryClass = 3;		//0: 중분류,  1: 소분류,   2: 세분류,	3:세세분
		int regionClass = 1;			//0: 시도,    1: 시군구,   2: 읍면동

		String[] industClassName = {"1st","2nd","3rd","4th"} ;
		String[] regionClassName ={"do", "gun", "myun"};	
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
		String locationCodeFile = filePath+"location_code/location_code.txt";
		String classFile = filePath + "profit/industry_profit.txt";
		
		IndustryClassifier ic = new IndustryClassifier(industryClass, classNumber);
		
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;

		this.initiate();
		this.setIndexMap();
		this.setClassDepth(industryClass, regionClass);
		this.readEmployee(filePath+"extracted/", "_microdataCode.txt");
		this.readLocationCodeList(locationCodeFile);
		ic.readIndustryData(classFile, iData);
		ic.setIndustryLevel(classNumber, iData);
		this.calculateEmployeeStatistics(iData);
		this.calculateIndustryLevelEntropy();
		this.transformAverageToEntropyData(eData);	
	}
	
	public void processAttractionTransfer(int start,int end,int classNumber,IndustryData iData,EntropyData eData){
		
		int industryClass = 3;		//0: 중분류,  1: 소분류,   2: 세분류,	3:세세분
		int regionClass = 1;			//0: 시도,    1: 시군구,   2: 읍면동

		String[] industClassName = {"1st","2nd","3rd","4th"} ;
		String[] regionClassName ={"do", "gun", "myun"};
	
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
		String locationCodeFile = filePath+"location_code/2010_location_code.txt";
		String classFile = filePath + "profit/industry_profit.txt";
		
		IndustryClassifier ic = new IndustryClassifier(industryClass, classNumber);
		
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;

		this.initiate();
		this.setIndexMap();
		this.setClassDepth(industryClass, regionClass);
		this.readEmployee(filePath+"extracted/", "_microdataCode.txt");
		this.readLocationCodeList(locationCodeFile);
		ic.readIndustryData(classFile, iData);
		ic.setIndustryLevel(classNumber, iData);
		this.calculateEmployeeStatistics(iData);
		this.calculateIndustryLevelEntropy();
		this.transformAttractionToEntropyData(eData);	
	}
	
	public void printIndustryClassDistribution(int year, String outputFile){
		int i,j;
		double[][] classRatio = this.ratioRegion.get(year);
		ArrayList<String> regionName = this.locationName.get(year);
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.println("Year:\t"+(this.startYear + year));
			pw.println("ClassNumber:\t"+this.levelNumber);
			pw.println();	
			
			pw.print("region");
			pw.print("\tentropy\taverage\tattraction");	
			for(i=0 ; i<this.levelNumber ; i++) pw.print("\t"+(i+1));
			pw.println();
			
			for(i=0 ; i<regionName.size() ; i++){			
				pw.print(regionName.get(i));	
				pw.print("\t"+this.entropy.get(year)[i]);
				pw.print("\t"+this.meanRegion.get(year)[i]);
				pw.print("\t"+this.attraction.get(year)[i]);
				for(j=0 ; j<this.levelNumber ; j++) pw.print("\t"+classRatio[i][j]);				
				pw.println();
			}			
			pw.close();
		}catch(IOException e) {}	
	}
	
	public static void main(String[] args) {
	
		int start = 2010;
		int end = 2010;
		
		int industryClass;		//0: 중분류,  1: 소분류,   2: 세분류,	3:세세분
		int regionClass;	    //0: 시도,    1: 시군구,   2: 읍면동

		int classNumber = 20;
		
		String[] industClassName = new String[4];
		String[] regionClassName = new String[3];

		industClassName[0] = "1st";
		industClassName[1] = "2nd";
		industClassName[2] = "3rd";
		industClassName[3] = "4th";
		regionClassName[0] = "do";
		regionClassName[1] = "gun";
		regionClassName[2] = "myun";
		
		industryClass = 2;
		regionClass = 1;		
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
		String entropyFile = filePath+"level_entropy/entropy_"+industClassName[industryClass]+"_"
																+regionClassName[regionClass]+".txt";
		String averageFile = filePath+"level_entropy/average_"+industClassName[industryClass]+"_"
																+regionClassName[regionClass]+".txt";
		String attractionFile = filePath+"level_entropy/attraction_"+industClassName[industryClass]+"_"
																+regionClassName[regionClass]+".txt";		
		String locationCodeFile = filePath+"location_code/location_code.txt";
				
		String classFile = filePath + "profit/industry_profit.txt";
		
		LevelEntropyCalculator lec = new LevelEntropyCalculator(start, end, industryClass, regionClass);
		IndustryClassifier ic = new IndustryClassifier(industryClass, classNumber);
		IndustryData iData = new IndustryData();

		/*** setting ***/
		System.out.print("employee reading: ");
		lec.readEmployee(filePath+"extracted/", "_microdataCode.txt");
		System.out.println("complete");	

		System.out.print("location reading: ");
		lec.readLocationCodeList(locationCodeFile);
		System.out.println("complete");	
		
		/*** industry classification ***/
		System.out.print("reading: ");
		ic.readIndustryData(classFile, iData);
		System.out.println("complete");
		
		System.out.print("leveling: ");
		ic.setIndustryLevel(classNumber, iData);
		System.out.println("complete");
				
		/*** entropy calculation ***/
		System.out.print("statistics calculating: ");		
		lec.calculateEmployeeStatistics(iData);
		System.out.println("complete");	
						
		System.out.print("entropy calculating: ");
		lec.calculateIndustryLevelEntropy();
		System.out.println("complete");	
		
		System.out.print("entropy printing: ");
		lec.printIndustryClassDistribution(0, filePath+"level_entropy/2010_industry_20ClassDistribution.txt");
		//lec.printEntropy(entropyFile);
		//lec.printAverage(averageFile);
		//lec.printAttraction(attractionFile);
		System.out.println("complete");	
				
		System.out.println("process complete.");
	}

}
