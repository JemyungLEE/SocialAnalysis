package regionalDiversity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import populationEntropy.CurvatureModel;
import populationEntropy.DataReader;
import populationEntropy.EntropyCalculator;
import populationEntropy.MapGenerator;
import populationEntropy.data.Coordinates;
import populationEntropy.data.CurvatureData;
import populationEntropy.data.EntropyData;
import populationEntropy.data.PopulationData;


public class IndustryEntropyCalculator {

	/**
	 *  Subject: Extract data code from  Micro-data file
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2013.7.3
	 *  Last Modified Data: 2013.7.3 
	 *  Department: Seoul Nat. Univ. depart. of Rural Systems Engineering
	 *  Description: 
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
	
	HashMap<Integer, String> locationHashMap;	//<code, name>
	HashMap<Integer, String> industryHashMap;	//<code, name>
	ArrayList<Integer> locatoinCode;			//use to find region's index	
	ArrayList<Integer> industryCode;			//use to find industry's index	
	ArrayList<String> locatoinName;				//use to find region's index	
	ArrayList<String> industryName;				//use to find industry's index


	ArrayList<double[]> employeeSumList;		//[region]
	ArrayList<double[][]> employeeList; 		//[region][industry]
	ArrayList<double[][]> employeeRatioList; 	//[region][industry]
	ArrayList<double[]> entropyList;			//[region]
	
	
	public IndustryEntropyCalculator(int start, int end){
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;
		
		this.initiate();
	}
		
	public IndustryEntropyCalculator(int start, int end, int industryClass, int regionClass){
		
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;

		this.initiate();
		this.setClassDepth(industryClass, regionClass);
	}
	
	public void initiate(){
		
		this.n_region = new int[this.duration];
		this.n_industry = new int[this.duration];
		this.employeeList = new ArrayList<double[][]>();
		this.employeeSumList = new ArrayList<double[]>();
		this.employeeRatioList = new ArrayList<double[][]>();
		this.entropyList =  new ArrayList<double[]>();
	}
	
	public void setIndexMap(){
		
		this.locationHashMap = new HashMap<Integer, String>();
		this.industryHashMap = new HashMap<Integer, String>();		
		this.locatoinCode = new ArrayList<Integer>();			
		this.industryCode = new ArrayList<Integer>();		
		this.locatoinName = new ArrayList<String>();		
		this.industryName = new ArrayList<String>();		
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
		String tmpCode;
		String tmpName;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){
				tmpCode = scan.next().trim();
				tmpName = scan.next().trim();	
								
				if( tmpCode.length() == this.regionClassDepth){				
					this.locationHashMap.put(Integer.parseInt(tmpCode), tmpName);
					this.locatoinCode.add(Integer.parseInt(tmpCode));
					this.locatoinName.add(tmpName);
					count++;
				}		
			}				
			this.n_region[year] = count;
						
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
					this.industryHashMap.put(Integer.parseInt(tmpCode), tmpName);
					this.industryCode.add(Integer.parseInt(tmpCode));
					this.industryName.add(tmpName);
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
		
		
		String tmpPath = "/Users/jml/Desktop/Research/data_storage/company/";
		
		for(i=0 ; i<this.duration ; i++){			
			
			System.out.print(" "+(this.startYear+i)+" ");
			
			inputFile = filePath + (this.startYear+i) + fileName;
						
			this.setIndexMap();
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
					regionIndex = this.locatoinCode.indexOf(region);
					industryIndex = this.industryCode.indexOf(industry);
					
					//System.out.println(region+"\t"+industry+"\t"+workers+"\t"+regionIndex+"\t"+industryIndex);
					
					/*** add workers ***/
					if(regionIndex >= 0)
						if(industryIndex >= 0)
							employee[regionIndex][industryIndex] += workers;
										
				}
				
				scan.close();	
			} catch(IOException e) {}
			
			this.employeeList.add(employee);
		}
	}		

	
	public void calculateEmployeeStatistics(){
		
		int i, j, k;
		double[][] employee;						//[region][industry]
		double[] employeeSum;						//[region]
		double[][] employeeRatio;					//[region][industry]


		for(i=0 ; i<this.duration ; i++){
			
			/*** initiate variables ***/
			employee = this.employeeList.get(i);			
			employeeRatio = new double[this.n_region[i]][this.n_industry[i]];
			employeeSum = new double[this.n_region[i]];			
			for(j=0 ; j<this.n_region[i] ; j++) employeeSum[j] = 0.0;
			
			/*** sum region's total employees ***/
			for(j=0 ; j<this.n_region[i] ; j++)
				for(k=0 ; k<this.n_industry[i] ; k++) employeeSum[j] += employee[j][k];	
			
			/*** calculate employee ratio ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_industry[i] ; k++){
					if(employeeSum[j] > 0) employeeRatio[j][k] = employee[j][k] / employeeSum[j];
					else if(employeeSum[j] == 0) employeeRatio[j][k] = 0.0;
					else{
						employeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
				}
			}
			
			this.employeeSumList.add(employeeSum);
			this.employeeRatioList.add(employeeRatio);
		}
	}
	
	public void calculateEmployeeEntropy(){
		
		int i, j, k;
		
		double[][] probability;	//[region][industry]
		
		double tmpProb;
		double subEntropy;
		double base = Math.log(2);
		
		double[] entropy;			//[region]

		for(i=0 ; i<this.duration ; i++){
			entropy = new double[this.n_region[i]];
			probability = this.employeeRatioList.get(i);
		
			for(j=0 ; j<this.n_region[i]; j++){
				entropy[j] = 0.0;
				for(k=0 ; k<this.n_industry[i] ; k++){
					tmpProb = probability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					entropy[j] += subEntropy;
				}				
			}			
			this.entropyList.add(entropy);
		}		
	}
	
	
	public void printEmployeeEntropy(String outputFile){
		int i,j;
		
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
			
			for(i=0 ; i<this.locatoinName.size() ; i++){	
				pw.print(this.locatoinName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.entropyList.get(j)[i]);			
				pw.println();
			}
			
			pw.close();
		}catch(IOException e) {}
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
		regionClass = 0;		
		
		String filePath = "/Users/jml/Desktop/Research/data_storage/company/";
		String locationCodeFile = filePath+"location_code/location_code.txt";
		String industryCodeFile = filePath+"industry_code/industry_code.txt";
		String employeeEntropyFile = filePath+"entropy/employeeEntropy_"+industClassName[industryClass]+"_"
																+regionClassName[regionClass]+".txt";
		
		IndustryEntropyCalculator iec = new IndustryEntropyCalculator(start, end, industryClass, regionClass);
		
		System.out.print("employee reading: ");
		iec.readEmployee(filePath+"extracted/", "_microdataCode.txt");
		System.out.println("complete");	
		
		System.out.print("statistics calculating: ");		
		iec.calculateEmployeeStatistics();
		System.out.println("complete");	
						
		System.out.print("entropy calculating: ");
		iec.calculateEmployeeEntropy();
		System.out.println("complete");	
		
		System.out.print("entropy printing: ");
		iec.printEmployeeEntropy(employeeEntropyFile);
		System.out.println("complete");	
				
		System.out.println("process complete.");
	}

}
