package companyDuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

import companyDuration.data.*;

public class CompanyClassifier {

	/**
	 *  Subject: Classify company with extract data code file
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2012.9.10
	 *  Last Modified Data: 2016.8.12
	 *  Department: Seoul Nat. Univ. depart. of Rural Systems Engineering
	 *  Description: Classify location, founding year, industrial classification, worker scale,
	 *               type of business entity and type of store network
	 *               ; copr., co. ltd , ... & alone, main, sub, ... 
	 */

	int yearOfData;
	int sortOfData = 6;
	int sortOfLocationType;
	int sortOfIndustryType;
	int sortOfScaleType;
	int sortOfBusinessType;
	int sortOfStoreType;
	
	int numberOfIndustryClass;
	int maxEmployees, totalEmployees;
	double averageEmployees;
	double[] scaleInterval;
		
	ArrayList<MicroData> microdataList;
	ArrayList<CompanyData> companydataList;
	
	ArrayList<Integer> locationCodeList;
	ArrayList<String> industryCodeList;
	
	HashMap<Integer, String> locationCodeMap;
	HashMap<String, String> industryCodeMap;
	
	int[][] companyData;	//[micro][type]: [0]foundation, [1]location, [2]industry, [3]employee, [4]business, [5]store
	
	public CompanyClassifier(){
		this.initiate();
	}
	
	public CompanyClassifier(int year, int location, int industry, int scale){
		this.initiate(year, location, industry, scale);
	}
	
	public CompanyClassifier(int year, int location, int industry, int scale, int business, int store){
		this.initiate(year, location, industry, scale, business, store);
	}
	
	public CompanyClassifier(int year, int location, int industry, double[] scale){
		this.initiate(year, location, industry, scale);
	}
	
	public CompanyClassifier(int year, int location, int industry, double[] scale, int business, int store){
		this.initiate(year, location, industry, scale, business, store);
	}
	
	public int getDataSize(){
		return this.microdataList.size();
	}
	
	public int[][] getCompanyData(){
		return this.companyData;
	}
	
	public int getCompanyData(int microData, int dataSort){
		/*** [0]foundation, [1]location, [2]industry, [3]employee, [4]business, [5]store ***/
		return this.companyData[microData][dataSort];
	}
	
	public ArrayList<MicroData> getMicorDataList(){
		return this.microdataList;
	}
	
	public int getNumberofIndustryClass(){
		return this.numberOfIndustryClass;
	}
	
	public int getMaxEmployeeSize(){
		return this.maxEmployees;
	}
	
	public void initiate(int year, int location, int industry, int scale){
		this.yearOfData = year;
		this.sortOfLocationType = location;
		this.sortOfIndustryType = industry;
		this.sortOfScaleType = scale;
		this.sortOfBusinessType = 0;
		this.sortOfStoreType = 0;
		
		this.initiate();
	}
	
	public void initiate(int year, int location, int industry, int scale, int business, int store){
		this.yearOfData = year;
		this.sortOfLocationType = location;
		this.sortOfIndustryType = industry;
		this.sortOfScaleType = scale;
		this.sortOfBusinessType = business;
		this.sortOfStoreType = store;
		
		this.initiate();
	}
	
	public void initiate(int year, int location, int industry, double[] scale){
		this.yearOfData = year;
		this.sortOfLocationType = location;
		this.sortOfIndustryType = industry;
		this.sortOfBusinessType = 0;
		this.sortOfStoreType = 0;
		this.setScaleInterval(scale);
		
		this.initiate();
	}
	
	public void initiate(int year, int location, int industry, double[] scale, int business, int store){
		this.yearOfData = year;
		this.sortOfLocationType = location;
		this.sortOfIndustryType = industry;
		this.sortOfBusinessType = business;
		this.sortOfStoreType = store;
		this.setScaleInterval(scale);
		
		this.initiate();
	}
	
	public void initiate(){
		this.locationCodeMap = new HashMap<Integer, String>();
		this.industryCodeMap = new HashMap<String, String>();
		this.locationCodeList = new ArrayList<Integer>();
		this.industryCodeList = new ArrayList<String>();
		this.microdataList = new ArrayList<MicroData>();
		this.companydataList = new ArrayList<CompanyData>();
	}
	
	public void initiateCompanyData(){
		this.companyData = new int[this.microdataList.size()][this.sortOfData];
	}
	
	public void readLocationCode(String locationCodeFile){
		try{
			File file = new File(locationCodeFile);
			Scanner scan = new Scanner(file);
		
			while(scan.hasNext()) locationCodeMap.put(scan.nextInt(), scan.next());
					
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void readIndustryCode(String industryCodeFile){
		String tmpStr;
		
		try{
			File file = new File(industryCodeFile);
			Scanner scan = new Scanner(file);
		
			while(scan.hasNext()) industryCodeMap.put(scan.next(), scan.next());
					
			scan.close();	
		} catch(IOException e) {}
		
		Iterator<String> iter = industryCodeMap.keySet().iterator();
		
		while(iter.hasNext()){
			tmpStr = iter.next();
			if(tmpStr.length() == this.sortOfIndustryType) this.industryCodeList.add(tmpStr);
		}
	}
	
	public void readMicrodataCode(String microdataFile){
		MicroData mdata;
		
		try{
			File file = new File(microdataFile);
			Scanner scan = new Scanner(file);
		
			scan.nextLine();
			while(scan.hasNext()){
				mdata =  new MicroData(scan.nextInt(), scan.nextInt(), scan.next(), scan.nextInt(),
															scan.nextInt(), scan.nextInt());
				this.microdataList.add(mdata);
			}
					
			scan.close();	
		} catch(IOException e) {}
		
		this.initiateCompanyData();
	}
	
	public void analyzeEmployeeStatistics(){
		int tmpEmployees = 0;
		int tmpMax = 0;
		int tmpTotal = 0;
		double tmpAverage;
		
		for(int i=0 ; i<this.microdataList.size() ; i++){
			tmpEmployees = this.microdataList.get(i).getWorkers();
			if(tmpMax < tmpEmployees) tmpMax = tmpEmployees;
			tmpTotal += tmpEmployees;
		}
		tmpAverage = (double) tmpTotal / (double) this.microdataList.size();
		
		this.maxEmployees = tmpMax;
		this.totalEmployees = tmpTotal;
		this.averageEmployees = tmpAverage;
	}
	
	public void countNumberOfIndustry(){
		String tmpStr = null;
		ArrayList<String> codeList = new ArrayList<String>();
		
		for(int i=0 ; i<this.microdataList.size() ; i++){
			if(this.sortOfIndustryType == 1) tmpStr = this.microdataList.get(i).getIndustryCode().substring(0, 1);
			else if(this.sortOfIndustryType > 1) 
				tmpStr = this.microdataList.get(i).getIndustryCode().substring(1, this.sortOfIndustryType+1);
			else System.err.println("industry class setting error");
			
			if(!codeList.contains(tmpStr) && tmpStr != null) codeList.add(tmpStr);
		}
		
		this.numberOfIndustryClass = codeList.size();
	}
	
	public void setScaleIntervalLogarithm(){
		int i;
		int scaleSize = this.sortOfScaleType;
		double logBase = 10.0;
		this.scaleInterval = new double[scaleSize+1];
		double[] tmpLogInterval = new double[scaleSize+1];
		
		for(i=0 ; i<scaleSize+1 ; i++)
			tmpLogInterval[i] = (double)i/scaleSize*Math.log(this.maxEmployees)/Math.log(logBase);
		for(i=1 ; i<scaleSize ; i++) this.scaleInterval[i] = Math.pow(10, tmpLogInterval[i]);
		this.scaleInterval[0] = 1;
		this.scaleInterval[scaleSize] = this.maxEmployees;
	}
	
	public void setScaleInterval(double[] interval){
		if(interval.length>0){
			this.sortOfScaleType = interval.length;
			this.scaleInterval = new double[interval.length];
			for(int i=0 ; i<interval.length ; i++) this.scaleInterval[i] = interval[i];
		}else System.err.println("empty scale interval data");
	}
	
	public void classifyCompanyMicroData(){
		int i, j;
		int dataSize = this.microdataList.size();
		int industryErrorCodes = 0;
		int tmpLocationType;		//0: dong, 1:eup, 2:myun
		int tmpScaleType;			//scale level
		int tmpIndustryType;		//industry classification
		double tmpDouble;
		String tmpStr;
		
		MicroData mdata;
		for(i=0 ; i<dataSize ; i++){
			mdata = this.microdataList.get(i);
			
			/*** set location type ***/
			tmpStr = this.locationCodeMap.get(mdata.getLocationCode());
			if(tmpStr.endsWith("동")) tmpLocationType = 1;
			else if(tmpStr.endsWith("읍")) tmpLocationType = 2;
			else if(tmpStr.endsWith("면")) tmpLocationType = 2;
			else tmpLocationType = 2;
			
			/*** set scale level ***/
			tmpDouble = (double) mdata.getWorkers();
			tmpScaleType = -1;
			for(j=0 ; j<this.scaleInterval.length-1 ; j++)
				if(tmpDouble>=this.scaleInterval[j] && tmpDouble<this.scaleInterval[j+1]) tmpScaleType = j+1;
			if(tmpScaleType==0) System.err.println("scale data error: "+this.yearOfData+"\t"+i);		
			
			/*** set industry class ***/
			tmpIndustryType = -1;
			tmpStr = mdata.getIndustryCode().substring(0, 1);
			if(this.yearOfData >= 2007){
				if(tmpStr.equals("A")) tmpIndustryType = 1;
				else if(tmpStr.equals("B") || tmpStr.equals("C") || tmpStr.equals("D")
							|| tmpStr.equals("E") || tmpStr.equals("F")) tmpIndustryType = 2;
				else tmpIndustryType = 3;
			}else if(this.yearOfData < 2007){
				if(tmpStr.equals("A") || tmpStr.equals("B")) tmpIndustryType = 1;
				else if(tmpStr.equals("C") || tmpStr.equals("D") || tmpStr.equals("E") || tmpStr.equals("F")) 
					tmpIndustryType = 2;
				else tmpIndustryType = 3;
			}
			
			/*** save classified data ***/
			this.companyData[i][0] = mdata.getFoundedYear();
			this.companyData[i][1] = tmpLocationType;
			this.companyData[i][2] = tmpIndustryType;
			this.companyData[i][3] = tmpScaleType;
			this.companyData[i][4] = mdata.getBusinessType();
			this.companyData[i][5] = mdata.getStoreType();
		}
		
		if(industryErrorCodes>0) 
			System.err.println(industryErrorCodes+"/"+dataSize+" codes aren't matched with industry code");
		
	}
	
	public void classifyCompanyType(int classifyingType, String microdataFile, String companyDataFile){
		//classification type 0: all class type
		//classification type 1: location code & other class type
		//classification type 2: location code + industry class code & other class type
		
		int i;
		ArrayList<ArrayList<String>> industryClassCode;
		ArrayList<Integer> scaleInterval;
		String tmpClassCode;
		this.sortOfIndustryType = 3;
		this.sortOfScaleType = 5;
		this.sortOfBusinessType = 5;
		this.sortOfStoreType = 3;
		
		int tmpFyear;
		int tmpLocation;
		int tmpWorkers, tmpScaleType = -1;
		int tmpBusinessType, tmpStoreType;
		String tmpIndustry;
		String tmpLocationType = "-1";
		String tmpIndustryType = "-1";
		ArrayList<Integer> errorCodeList = new ArrayList<Integer>();
		
		//initiate IndustryClassCode
		industryClassCode = new ArrayList<ArrayList<String>>();
		for(i=0 ; i<this.sortOfIndustryType ; i++) industryClassCode.add(new ArrayList<String>());
		scaleInterval = new ArrayList<Integer>();
		
		//set IndustryClassCode
		
		//for 2006~2010
		industryClassCode.get(0).add("A");
		industryClassCode.get(0).add("B");
		industryClassCode.get(1).add("C");
		industryClassCode.get(1).add("D");
		industryClassCode.get(2).add("E");
		industryClassCode.get(1).add("F");
		industryClassCode.get(2).add("G");
		industryClassCode.get(2).add("H");
		industryClassCode.get(2).add("I");
		industryClassCode.get(2).add("J");
		industryClassCode.get(2).add("K");
		industryClassCode.get(2).add("L");
		industryClassCode.get(2).add("M");
		industryClassCode.get(2).add("O");
		industryClassCode.get(2).add("P");
		industryClassCode.get(2).add("Q");
		industryClassCode.get(2).add("R");
		industryClassCode.get(2).add("S");
		industryClassCode.get(2).add("T");
		industryClassCode.get(2).add("U");
		
		/*
		//for 1998~2005
		industryClassCode.get(0).add("A");
		industryClassCode.get(0).add("B");
		industryClassCode.get(0).add("C");
		industryClassCode.get(1).add("D");
		industryClassCode.get(1).add("E");
		industryClassCode.get(1).add("F");
		industryClassCode.get(2).add("G");
		industryClassCode.get(2).add("H");
		industryClassCode.get(2).add("I");
		industryClassCode.get(2).add("J");
		industryClassCode.get(2).add("K");
		industryClassCode.get(2).add("L");
		industryClassCode.get(2).add("M");
		industryClassCode.get(2).add("O");
		industryClassCode.get(2).add("P");
		industryClassCode.get(2).add("Q");
		industryClassCode.get(2).add("R");
		industryClassCode.get(2).add("S");
		industryClassCode.get(2).add("T");
		*/
		
		//set Scale Interval
		scaleInterval.add(10);
		scaleInterval.add(100);
		scaleInterval.add(1000);
		scaleInterval.add(10000);
		
		//classify company type from microdata		
		try{
			File scanFile = new File(microdataFile);
			Scanner scan = new Scanner(scanFile);

			File pwfile = new File(companyDataFile);
			PrintWriter pw = new PrintWriter(pwfile);
		
			scan.nextLine();
			pw.println("Founding_Year\tLocation_Type\tIndustry_Type\tScale_Type\tBusiness_Type\tStore_type");
			while(scan.hasNext()){
				tmpFyear = scan.nextInt();
				tmpLocation = scan.nextInt();
				tmpIndustry = scan.next();
				tmpWorkers = scan.nextInt();
				tmpBusinessType = scan.nextInt()-1;
				tmpStoreType = scan.nextInt()-1;
				
				//classify location type: dong = 0, eup = 1, myun = 2;
				if(classifyingType == 0){
					if(this.locationCodeMap.containsKey(tmpLocation))
						tmpLocationType = this.locationCodeMap.get(tmpLocation);
					else if(errorCodeList.contains(tmpLocation)==false) errorCodeList.add(tmpLocation); 
				}else if(classifyingType == 1 || classifyingType == 2){
					tmpLocationType = Integer.toString(tmpLocation).substring(0, 5);
				}
				
				//classify industry type
				if(classifyingType == 0 || classifyingType == 1){
					tmpClassCode = tmpIndustry.substring(0,1);
					for(i=0 ; i<this.sortOfIndustryType ; i++)
						if(industryClassCode.get(i).contains(tmpClassCode)) tmpIndustryType=Integer.toString(i);
				}else if(classifyingType == 2){
					tmpIndustryType = tmpIndustry;
				}				
				
				//classify scale type						
				if(tmpWorkers < scaleInterval.get(0)) tmpScaleType = 0;
				else if(tmpWorkers>=scaleInterval.get(scaleInterval.size()-1)) tmpScaleType=scaleInterval.size();
				else for(i=1 ; i<scaleInterval.size() ; i++)
					if(tmpWorkers>=scaleInterval.get(i-1) && tmpWorkers<scaleInterval.get(i)) tmpScaleType=i;					
								
				pw.println(tmpFyear+"\t"+tmpLocationType+"\t"+tmpIndustryType+"\t"+tmpScaleType
							+"\t"+tmpBusinessType+"\t"+tmpStoreType);
			}
		
			for(i=0 ; i<errorCodeList.size() ; i++)	
				System.err.println("null location code: "+errorCodeList.get(i));
			
			scan.close();	
			pw.close();
		} catch(IOException e) {}
		
	}

	public void makeCompanyDataFile(String companyDataFile){
		CompanyData cdata;
		
		try{
			File file = new File(companyDataFile);
			PrintWriter pw = new PrintWriter(file);
			
			for(int i=0 ; i<this.companydataList.size() ; i++){
				cdata = companydataList.get(i);
				pw.println(cdata.getFoundedYear()+"\t"+cdata.getLocationType()+"\t"
							+cdata.getIndustryType()+"\t"+cdata.getScaleType());
			}
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printCompanyClassification(String outpuFile){
		int i, j;
		try{
			File file = new File(outpuFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("# of industry class: "+this.numberOfIndustryClass+" / "+this.industryCodeList.size());
			pw.println("# of emloyee size class: "+(this.scaleInterval.length-1));
			pw.println("max employee size: "+this.maxEmployees);
			pw.println("Founded_year\tLocation_type\tIndustry_class\tScale_class\tBusiness_type\tStore_type");
			for(i=0 ; i<this.companyData.length ; i++){
				for(j=0 ; j<this.companyData[i].length ; j++) pw.print(this.companyData[i][j]+"\t");
				pw.println();
			}
			pw.close();
		}catch(IOException e) {}
	}
	
	public void proceedClassificationProcess(int year, int location, int industry, int scale,
																		int business, int store,
																		String locationcode, String industrycode, String microdata){
		this.initiate(year, location, industry, scale);
		this.proceedClassificationProcess(locationcode, industrycode, microdata);
	}
	
	public void proceedClassificationProcess(int year, int location, int industry, double[] scale, 
																		int business, int store,
																		String locationcode, String industrycode, String microdata){
		this.initiate(year, location, industry, scale);
		this.proceedClassificationProcess(locationcode, industrycode, microdata);
	}
	
	public void proceedClassificationProcess(String locationcode, String industrycode, String microdata){
		
		this.readLocationCode(locationcode);
		this.readIndustryCode(industrycode);
		this.readMicrodataCode(microdata);
		this.analyzeEmployeeStatistics();
		this.countNumberOfIndustry();
		if(this.sortOfScaleType>1 && (this.scaleInterval == null || this.scaleInterval.length == 0)) 
			this.setScaleIntervalLogarithm();
		this.classifyCompanyMicroData();
	}
	
	public void printDataStatus(){
		System.out.println("year\ttotal company\ttotal industry class"
										+ "\ttotal employee\taverage employee\tmax employee");
		
		System.out.print(this.yearOfData);
		System.out.print("\t"+this.microdataList.size());
		System.out.print("\t"+this.numberOfIndustryClass);
		System.out.print("\t"+this.totalEmployees);
		System.out.print("\t"+this.averageEmployees);
		System.out.print("\t"+this.maxEmployees);
		System.out.println();
		
	}
	
	
	public void printStatisticsOfStartupCompanies(String outputFile){
		int i, j, k, l;
		
		String[] scaleType = {"all", "small", "middle", "large", "huge"};
		double[] scaleInterval = {0.0, 6.0, 30.0, 300.0, 30000.0};
		int tmpCount;
		int[] tmpScaleCount = new int[this.scaleInterval.length];
		int[] tmpBusinessCount = new int[this.sortOfBusinessType];
		int[] tmpStoreCount = new int[this.sortOfStoreType];
		//[micro][type]: [0]foundation, [1]location, [2]industry, [3]employee, [4]business, [5]store
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			

			pw.print("Area\tYear\ttotal");
			for(i=1 ; i<scaleInterval.length ; i++) pw.print("\t"+scaleInterval[i]);
			for(i=0 ; i<this.sortOfBusinessType ; i++) pw.print("\tbusiness_"+(i+1));
			for(i=0 ; i<this.sortOfStoreType ; i++) pw.print("\tstore_"+(i+1));
			pw.println();
			pw.print("urban\t"+this.yearOfData);	
			tmpCount = 0;
			for(i=0 ; i<this.companyData.length ; i++){
				if(this.companyData[i][0] == this.yearOfData && this.companyData[i][1] == 1 && this.companyData[i][2] == 1){
					tmpCount++;
					for(j=0 ; j<scaleInterval.length-1 ; j++)
						if(this.companyData[i][3] >= scaleInterval[j] && this.companyData[i][3] < scaleInterval[j+1])
							tmpScaleCount[j]++;
					for(k=0 ; k<this.sortOfBusinessType ; k++)
						if(this.companyData[i][4] == k) tmpBusinessCount[k]++;
					for(l=0 ; l<this.sortOfStoreType ; l++)
						if(this.companyData[i][5] == l) tmpStoreCount[l]++;
				}
			}
			pw.print("\t"+tmpCount);
			for(i=0 ; i<scaleInterval.length ; i++) pw.print("\t"+tmpScaleCount[i]);
			for(i=0 ; i<this.sortOfBusinessType ; i++) pw.print("\t"+tmpBusinessCount[i]);
			for(i=0 ; i<this.sortOfStoreType ; i++) pw.print("\t"+tmpStoreCount[i]);
			pw.println();
			
			tmpScaleCount = new int[this.scaleInterval.length];
			tmpBusinessCount = new int[this.sortOfBusinessType];
			tmpStoreCount = new int[this.sortOfStoreType];
			
			pw.print("rural\t"+this.yearOfData);	
			tmpCount = 0;
			for(i=0 ; i<this.companyData.length ; i++){
				if(this.companyData[i][0] == this.yearOfData && this.companyData[i][1] == 2 && this.companyData[i][2] == 1){
					tmpCount++;
					for(j=0 ; j<scaleInterval.length-1 ; j++)
						if(this.companyData[i][3] >= scaleInterval[j] && this.companyData[i][3] < scaleInterval[j+1])
							tmpScaleCount[j]++;
					for(k=0 ; k<this.sortOfBusinessType ; k++)
						if(this.companyData[i][4] == k) tmpBusinessCount[k]++;
					for(l=0 ; l<this.sortOfStoreType ; l++)
						if(this.companyData[i][5] == l) tmpStoreCount[l]++;
				}
			}
			pw.print("\t"+tmpCount);
			for(i=0 ; i<scaleInterval.length ; i++) pw.print("\t"+tmpScaleCount[i]);
			for(i=0 ; i<this.sortOfBusinessType ; i++) pw.print("\t"+tmpBusinessCount[i]);
			for(i=0 ; i<this.sortOfStoreType ; i++) pw.print("\t"+tmpStoreCount[i]);
			pw.println();
			pw.close();
		}catch(IOException e) {}
	}
	
	public static void main(String[] args) {
		int year;
		String filePath;
		String locationCodeFile, industryCodeFile, microdataFile;
		String companyTypeFile;
		
		for(year = 1998 ; year<=2012 ; year++){
			System.out.println("year: "+year);
			
			filePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
			locationCodeFile = filePath+"location_code/"+year+"_location_code.txt";
			industryCodeFile = filePath+"industry_code/"+year+"_industry_code.txt";
			microdataFile = filePath+"extracted/"+year+"_microdataCode.txt";
			companyTypeFile = filePath+"classified/"+year+"microdata_classified.txt";
			
			System.out.print("process: initiation");
			CompanyClassifier cc = new CompanyClassifier(year, 3, 4, 4);
			
			System.out.print("\tdata reading");
			cc.readLocationCode(locationCodeFile);
			cc.readIndustryCode(industryCodeFile);
			cc.readMicrodataCode(microdataFile);
			
			System.out.print("\tclassification");
			cc.analyzeEmployeeStatistics();
			cc.countNumberOfIndustry();
		//	cc.printDataStatus();
		//	cc.setScaleIntervalLogarithm();
			
			double[] intervals = {0, 6, 30, 300, cc.getMaxEmployeeSize()};
			cc.setScaleInterval(intervals);
			cc.classifyCompanyMicroData();
			
			System.out.print("\tfile creation");
	//		cc.printStatisticsOfStartupCompanies(companyTypeFile.replace(".txt", "_statistics.txt"));
			cc.printCompanyClassification(companyTypeFile);
			System.out.println("...\tcomplete");
			
		}		
	}

}
