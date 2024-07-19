package industryEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import industryEntropy.data.*;
import populationEntropy.data.EntropyData;


public class IndustryEntropyCalculator {

	/**
	 *  Subject: Regional industry weighted entropy calculator
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2014.5.17
	 *  Last Modified Data: 2014.5.17 
	 *  Department: Seoul Nat. Univ. depart. of Rural Systems Engineering
	 *  Description: Industry entropy is weighted by global and local factors 
	 */

	int startYear, endYear;
	int duration;
	int[] n_region;
	int[] n_industry;
	
	int industryClassDepth;		//2:2nd_jung, 3: 3rd_so order depth
	int regionClassDepth;		//2:si_do,    5: si_gun_gu,   7: eup_myun_dong
	
	int[] industClassKey;		//2: 중분류,  3: 소분류,   4: 세분류,	5:세세분류
	int[] regionClassKey;		//2: 시도,    5: 시군구,   7: 읍면동
	String[] industClassName;
	String[] regionClassName;
	
	HashMap<Integer, String> locationList;	//<code, name>
	ArrayList<String> locationNameList;		//name list of all years, all regions 
	ArrayList<Integer> locationCodeList;	//code list of all years, all regions 
	
	ArrayList<HashMap<Integer, String>> locationHashMap;	//[year] <code, name>
	ArrayList<HashMap<Integer, String>> industryHashMap;	//[year] <code, name>
	ArrayList<ArrayList<Integer>> locationCode;				//[year] use to find region's index	
	ArrayList<ArrayList<Integer>> industryCode;				//[year] use to find industry's index	
	ArrayList<ArrayList<String>> locationName;				//[year] use to find region's index	
	ArrayList<ArrayList<String>> industryName;				//[year] use to find industry's index


	double[] totalEmployee;						//[year]
	ArrayList<double[]>   employeeRegion;		//[year] [region]
	ArrayList<double[]>   employeeIndustry;		//[year] [industry]
	ArrayList<double[][]> employee; 			//[year] [region][industry]
	ArrayList<double[]>   ratioIndustry;		//[year] [industry]
	ArrayList<double[][]> ratioRegion;		 	//[year] [region][industry]
	
	ArrayList<double[]>   globalWeight;			//[year] [industry]
	ArrayList<double[][]> localWeight; 			//[year] [region][industry]	
	ArrayList<double[]>   entropy;				//[year] [region]
	
	
	public IndustryEntropyCalculator(){
		
	}	
	
	public IndustryEntropyCalculator(int start, int end){
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;
		
		this.initiate();
		this.setIndexMap();
	}
		
	public IndustryEntropyCalculator(int start, int end, int industryClass, int regionClass){
		
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
		this.employeeRegion	= new ArrayList<double[]>();
		this.employeeIndustry = new ArrayList<double[]>();
		this.ratioRegion	= new ArrayList<double[][]>();
		this.ratioIndustry = new ArrayList<double[]>();
		
		this.globalWeight = new ArrayList<double[]>();
		this.localWeight = new ArrayList<double[][]>();
		this.entropy = new ArrayList<double[]>();
	}
	
	public void setIndexMap(){		
		
		this.locationList = new HashMap<Integer, String>();
		this.locationCodeList = new ArrayList<Integer>();	
		this.locationNameList = new ArrayList<String>();		
		
		this.locationHashMap = new ArrayList<HashMap<Integer, String>>();
		this.industryHashMap = new ArrayList<HashMap<Integer, String>>();		
		this.locationCode = new ArrayList<ArrayList<Integer>>();			
		this.industryCode = new ArrayList<ArrayList<Integer>>();		
		this.locationName = new ArrayList<ArrayList<String>>();		
		this.industryName = new ArrayList<ArrayList<String>>();	
		
		for(int i=0 ; i<this.duration ; i++){
			this.locationHashMap.add(new HashMap<Integer, String>());
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
		String tmpCode;
		String tmpName;
		String oldName;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){
				tmpCode = scan.next().trim();
				tmpName = scan.next().trim();	
				tmpCodeIndex = Integer.parseInt(tmpCode);
				
				if( tmpCode.length() == this.regionClassDepth){				
					this.locationHashMap.get(year).put(tmpCodeIndex, tmpName);
					this.locationCode.get(year).add(tmpCodeIndex);
					this.locationName.get(year).add(tmpName);
					count++;
					
					/*
					if(this.locationList.containsKey(tmpCodeIndex)){
						oldName = this.locationList.get(tmpCodeIndex);
						if(oldName.equals(tmpName) == false){		
							if(tmpName.contains(oldName)){							
								this.locationList.put(tmpCodeIndex, tmpName);
								this.locationNameList.set(this.locationNameList.indexOf(oldName), tmpName);
							}else if(oldName.contains(tmpName) == false){
								System.err.println("location code miss matching at: "+(year+this.startYear)
											+"\t"+tmpCode+"\t"+oldName+"\t"+tmpName);							
							}
						}
					}
					else{
						this.locationList.put(tmpCodeIndex, tmpName);
						this.locationNameList.add(tmpName);
					}
					*/
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
		String tmpCode;
		String tmpName;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){
				tmpCode = scan.next().trim();
				tmpName = scan.next().trim();	
				tmpCodeIndex = Integer.parseInt(tmpCode);
				
				if(tmpCode.length() == this.regionClassDepth){		

					this.locationList.put(tmpCodeIndex, tmpName);
					this.locationCodeList.add(tmpCodeIndex);
					this.locationNameList.add(tmpName);

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

	
	public void calculateEmployeeStatistics(){
		
		int i, j, k;
		double total;
		double[][] employee;		//[region][industry]		
		double[] regionSum;			//[region]
		double[] industrySum;		//[industry]
		double[] industryRatio;		//[industry]
		double[][] regionalRatio;	//[region][industry]


		for(i=0 ; i<this.duration ; i++){
			
			/*** initiate variables ***/
			total = 0.0;
			employee = this.employee.get(i);			
			regionSum = new double[this.n_region[i]];	
			industrySum = new double[this.n_industry[i]];
			industryRatio = new double[this.n_industry[i]];
			regionalRatio = new double[this.n_region[i]][this.n_industry[i]];					
			for(j=0 ; j<this.n_region[i] ; j++) regionSum[j] = 0.0;
			for(j=0 ; j<this.n_industry[i] ; j++) industrySum[i] = 0.0;
			
			/*** calculate employee sum: total, region, industry ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_industry[i] ; k++){
					regionSum[j] += employee[j][k];
					industrySum[k] += employee[j][k];
					total += employee[j][k];
				}
			}
			
			/*** calculate employee ratio ***/
			for(j=0 ; j<this.n_industry[i] ; j++) 
				industryRatio[j] = industrySum[j] / total;
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_industry[i] ; k++){
					if(regionSum[j] > 0) regionalRatio[j][k] = employee[j][k] / regionSum[j];
					else if(regionSum[j] == 0) regionalRatio[j][k] = 0.0;
					else{
						regionalRatio[j][k] = 0.0;
						System.err.println((i+this.startYear)+"\t"+j+"\t"+"probability calculation error");
					}
				}
			}			
			
			this.totalEmployee[i] = total;
			this.employeeRegion.add(regionSum);
			this.employeeIndustry.add(industrySum);
			this.ratioRegion.add(regionalRatio);
			this.ratioIndustry.add(industryRatio);
		}
	}
	
	public void calculateIndustryEntropy(){
		
		int i, j, k;
		
		double[][] probability;	//[region][industry]
		double[][] localW;		//[region][industry]
		double[]   globalW;		//[industry]
		
		double tmpProb;
		double tmpSum;
		double subEntropy;
		double base = Math.log(2);
		
		double[] entropy;		//[region]

		for(i=0 ; i<this.duration ; i++){
			
			//System.out.println((i+this.startYear)+"\t"+this.n_region[i]+"\t"+this.n_industry[i]);
			
			entropy = new double[this.n_region[i]];
			probability = new double[this.n_region[i]][this.n_industry[i]];
			globalW = new double[this.n_industry[i]];
			localW = new double[this.n_region[i]][this.n_industry[i]];
			
			/*** calculate global and local weights ***/
			for(j=0 ; j<this.n_region[i]; j++)
				for(k=0 ; k<this.n_industry[i] ; k++) localW[j][k] = this.ratioRegion.get(i)[j][k]; 
			for(j=0 ; j<this.n_industry[i] ; j++){
				if(this.ratioIndustry.get(i)[j]>0) globalW[j] = 1.0/this.ratioIndustry.get(i)[j];
				else if(this.ratioIndustry.get(i)[j] == 0) globalW[j] = 0;
				else System.err.println((i+this.startYear)+"\t"+j+"\t"+"weight calculation error");
			}
			
			/*** calculate weighted probability ***/
			for(j=0 ; j<this.n_region[i]; j++){
				tmpSum = 0.0;
				for(k=0 ; k<this.n_industry[i] ; k++){
					//probability[j][k] = globalW[k] * localW[j][k];
					probability[j][k] = localW[j][k];
					tmpSum += probability[j][k]; 
				}
				for(k=0 ; k<this.n_industry[i] ; k++){
					if(tmpSum>0) probability[j][k] /= tmpSum;
					else if(tmpSum == 0) probability[j][k] = 0;
					else System.err.println((i+this.startYear)+"\t"+j+"\t"+"probability calculation error");
				}
			}
			
			/*** calculate entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				entropy[j] = 0.0;
				for(k=0 ; k<this.n_industry[i] ; k++){
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
		}		
	}
	
	
	public void printEmployeeEntropy(String outputFile){
		int i,j;
		int tmpIndex;
		int tmpCode;
		String tmpName;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("range");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			pw.print("N_industry:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_industry[i]);
			pw.println();
			pw.print("Max_entropy:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(Math.log(this.n_industry[i])/Math.log(2)));
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
	
	public void transformToEntropyData(EntropyData eData){
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
				if(tmpEntropy > max[i]) max[i] = tmpEntropy;
				if(tmpEntropy < min[i]) min[i] = tmpEntropy;
			}
		}		
		
		/*** normalize entropy values ***/		
		for(i=0 ; i<years ; i++){
			for(j=0 ; j<regions ; j++){
				if(index[i][j]>=0){
					tmpEntropy = (eData.getEntropy(i, j) - min[i]) / (max[i] - min[i]);
					eData.setEntropy(i, j, tmpEntropy);
				}else eData.setEntropy(i, j, 0); 
			}
			
			eData.setMaxEntropy(i, 1.0);
			eData.setMinEntropy(i, 0.0);
		}		
	}
	
	public void processTransfer(int start, int end, EntropyData eData){
		
		int industryClass;		//0: 중분류,  1: 소분류,   2: 세분류,	3:세세분
		int regionClass;	    //0: 시도,    1: 시군구,   2: 읍면동

		String[] industClassName = new String[4];
		String[] regionClassName = new String[3];

		industClassName[0] = "1st";
		industClassName[1] = "2nd";
		industClassName[2] = "3rd";
		industClassName[3] = "4th";
		regionClassName[0] = "do";
		regionClassName[1] = "gun";
		regionClassName[2] = "myun";
		
		industryClass = 1;
		regionClass = 1;		
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
		String locationCodeFile = filePath+"location_code/location_code.txt";
		
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;

		this.initiate();
		this.setIndexMap();
		this.setClassDepth(industryClass, regionClass);
		this.readEmployee(filePath+"extracted/", "_microdataCode.txt");
		this.readLocationCodeList(locationCodeFile);
		this.calculateEmployeeStatistics();
		this.calculateIndustryEntropy();
		this.transformToEntropyData(eData);	
	}
	
	
	public static void main(String[] args) {
	
		int start = 1998;
		int end = 2011;
		
		int industryClass;		//0: 중분류,  1: 소분류,   2: 세분류,	3:세세분
		int regionClass;	    //0: 시도,    1: 시군구,   2: 읍면동

		String[] industClassName = new String[4];
		String[] regionClassName = new String[3];

		industClassName[0] = "1st";
		industClassName[1] = "2nd";
		industClassName[2] = "3rd";
		industClassName[3] = "4th";
		regionClassName[0] = "do";
		regionClassName[1] = "gun";
		regionClassName[2] = "myun";
		
		industryClass = 3;
		regionClass = 1;		
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
		String employeeEntropyFile = filePath+"weighted_entropy/employeeEntropy_"+industClassName[industryClass]+"_"
																+regionClassName[regionClass]+".txt";
		String locationCodeFile = filePath+"location_code/location_code.txt";
		
		IndustryEntropyCalculator iec = new IndustryEntropyCalculator(start, end, industryClass, regionClass);
		
		System.out.print("employee reading: ");
		iec.readEmployee(filePath+"extracted/", "_microdataCode.txt");
		System.out.println("complete");	

		System.out.print("location reading: ");
		iec.readLocationCodeList(locationCodeFile);
		System.out.println("complete");	
		
		System.out.print("statistics calculating: ");		
		iec.calculateEmployeeStatistics();
		System.out.println("complete");	
						
		System.out.print("entropy calculating: ");
		iec.calculateIndustryEntropy();
		System.out.println("complete");	
		
		System.out.print("entropy printing: ");
		iec.printEmployeeEntropy(employeeEntropyFile);
		System.out.println("complete");	
				
		System.out.println("process complete.");
	}

}
