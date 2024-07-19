package employmentStability;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

import employmentStability.data.MicroData;
import employmentStability.data.CompanyData;

public class CompanyDataReader {
	/**
	 *  Subject: Read company data from extracted micro-data
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2016.11.21
	 *  Last Modified Data: 2016.11.21
	 *  Description: read region, industry classification, employee of new established companies
	 */

	int yearOfData;
	int locationCodeDepth;		
	int industryCodeDepth;						//0: primary, secondary, tertiary industrial group
	int retainedBusinessType ;					//if 0, retain all business type data
	int numberOfCompany, numberOfRegion;
	int numberOfIndustry, numberOfDuration, numberOfSize;
	int maxEmployees, totalEmployees;
	double averageEmployees;
	
	int[] totalCompany, totalEmployee;													//[region]
	int[] startupCompany, startupEmployee;											//[region]
	int[] totalRuralCompany, totalRuralEmployee;									//[region]
	int[] startupRuralCompany, startupRuralEmployee;							//[region]
	int[] totalUrbanCompany, totalUrbanEmployee;									//[region]
	int[] startupUrbanCompany, startupUrbanEmployee;						//[region]
	
	int[][] totalCompanyByIndustry, totalEmployeeByIndustry;								//[region][industry]
	int[][] startupCompanyByIndustry, startupEmployeeByIndustry;						//[region][industry]
	int[][] totalRuralCompanyByIndustry, totalRuralEmployeeByIndustry;				//[region][industry]
	int[][] startupRuralCompanyByIndustry, startupRuralEmployeeByIndustry;		//[region][industry]
	int[][] totalUrbanCompanyByIndustry, totalUrbanEmployeeByIndustry;			//[region][industry]
	int[][] startupUrbanCompanyByIndustry, startupUrbanEmployeeByIndustry;	//[region][industry]
	
	int[][] totalCompanyByDuration, totalEmployeeByDuration;								//[region][duration]
	int[][] startupCompanyByDuration, startupEmployeeByDuration;						//[region][duration]
	int[][] totalRuralCompanyByDuration, totalRuralEmployeeByDuration;				//[region][duration]
	int[][] startupRuralCompanyByDuration, startupRuralEmployeeByDuration;	//[region][duration]
	int[][] totalUrbanCompanyByDuration, totalUrbanEmployeeByDuration;			//[region][duration]
	int[][] startupUrbanCompanyByDuration, startupUrbanEmployeeByDuration;	//[region][duration]
	
	int[][] totalCompanyBySize, totalEmployeeBySize;											//[region][size]
	int[][] startupCompanyBySize, startupEmployeeBySize;									//[region][size]
	int[][] totalRuralCompanyBySize, totalRuralEmployeeBySize;							//[region][size]
	int[][] startupRuralCompanyBySize, startupRuralEmployeeBySize;					//[region][size]
	int[][] totalUrbanCompanyBySize, totalUrbanEmployeeBySize;						//[region][size]
	int[][] startupUrbanCompanyBySize, startupUrbanEmployeeBySize;				//[region][size]
	
	int sumOfCompany, sumOfEmployee;													
	int sumOfStartupCompany, sumOfStartupEmployee;											
	int sumOfRuralCompany, sumOfRuralEmployee;									
	int sumOfStartupRuralCompany, sumOfStartupRuralEmployee;							
	int sumOfUrbanCompany, sumOfUrbanEmployee;									
	int sumOfStartupUrbanCompany, sumOfStartupUrbanEmployee;						
	
	int[] sumOfCompanyByIndustry, sumOfEmployeeByIndustry;												//[industry]
	int[] sumOfStartupCompanyByIndustry, sumOfStartupEmployeeByIndustry;						//[industry]
	int[] sumOfRuralCompanyByIndustry, sumOfRuralEmployeeByIndustry;								//[industry]
	int[] sumOfStartupRuralCompanyByIndustry, sumOfStartupRuralEmployeeByIndustry;		//[industry]
	int[] sumOfUrbanCompanyByIndustry, sumOfUrbanEmployeeByIndustry;							//[industry]
	int[] sumOfStartupUrbanCompanyByIndustry, sumOfStartupUrbanEmployeeByIndustry;	//[industry]
	
	int[] sumOfCompanyByDuration, sumOfEmployeeByDuration;												//[duration]
	int[] sumOfStartupCompanyByDuration, sumOfStartupEmployeeByDuration;						//[duration]
	int[] sumOfRuralCompanyByDuration, sumOfRuralEmployeeByDuration;								//[duration]
	int[] sumOfStartupRuralCompanyByDuration, sumOfStartupRuralEmployeeByDuration;		//[duration]
	int[] sumOfUrbanCompanyByDuration, sumOfUrbanEmployeeByDuration;							//[duration]
	int[] sumOfStartupUrbanCompanyByDuration, sumOfStartupUrbanEmployeeByDuration;	//[duration]
	
	int[] sumOfCompanyBySize, sumOfEmployeeBySize;															//[size]
	int[] sumOfStartupCompanyBySize, sumOfStartupEmployeeBySize;									//[size]
	int[] sumOfRuralCompanyBySize, sumOfRuralEmployeeBySize;											//[size]
	int[] sumOfStartupRuralCompanyBySize, sumOfStartupRuralEmployeeBySize;					//[size]
	int[] sumOfUrbanCompanyBySize, sumOfUrbanEmployeeBySize;										//[size]
	int[] sumOfStartupUrbanCompanyBySize, sumOfStartupUrbanEmployeeBySize;				//[size]
	
	int[] durationSector;
	int[] sizeSector;
	ArrayList<String> locationCodeList;
	ArrayList<String> industryCodeList;
	
	HashMap<String, String> locationCodeMap;			//<code, region name>
	HashMap<String, String> industryCodeMap;			//<code, industrial category>

	ArrayList<MicroData> microdataList;
	
	public CompanyDataReader(){
		this.initiate();
	}
	
	public CompanyDataReader(int year){
		this.initiate(year);
	}
	
	public CompanyDataReader(int year, int locationDepth, int industryDepth){
		this.locationCodeDepth = locationDepth;
		this.industryCodeDepth = industryDepth;
		this.retainedBusinessType = 0;
		this.initiate(year);
	}
	
	public CompanyDataReader(int year, int locationDepth, int industryDepth, int retainedBusiness){
		this.locationCodeDepth = locationDepth;
		this.industryCodeDepth = industryDepth;
		this.retainedBusinessType = retainedBusiness;
		this.initiate(year);
	}
	
	public int getDataSize(){
		return this.microdataList.size();
	}
	
	public MicroData getMicroData(int index){
		return this.microdataList.get(index);
	}
	
	public ArrayList<MicroData> getMicorDataList(){
		return this.microdataList;
	}
	
	public int getNumberofRegion(){
		return this.numberOfRegion;
	}
	
	public int getNumberofIndustry(){
		return this.numberOfIndustry;
	}
	
	public int getMaxEmployeeSize(){
		return this.maxEmployees;
	}
	
	public String getLocationCode(int index){
		return this.locationCodeList.get(index);
	}
	
	public String getIndustryCode(int index){
		return this.industryCodeList.get(index);
	}
	
	public String getLocationMapValue(String code){
		return this.locationCodeMap.get(code);
	}
	
	public String getIndustryMapValue(String code){
		return this.industryCodeMap.get(code);
	}
	
	public void initiate(int year){
		this.yearOfData = year;
		
		this.initiate();
	}
	
	public void initiate(int year, int location, int industry){
		this.yearOfData = year;
		this.numberOfRegion = location;
		this.numberOfIndustry = industry;
		
		this.initiate();
		this.initiateVariables();
		this.initateDetailedVariables();
	}
	
	public void initiate(){
		this.locationCodeList = new ArrayList<String>();
		this.industryCodeList = new ArrayList<String>();
		this.locationCodeMap = new HashMap<String, String>();
		this.industryCodeMap = new HashMap<String, String>();
		this.microdataList = new ArrayList<MicroData>();
	}
	
	public void initiateVariables(){
		this.totalCompany = new int[this.numberOfRegion];
		this.totalEmployee = new int[this.numberOfRegion];
		this.startupCompany = new int[this.numberOfRegion];
		this.startupEmployee = new int[this.numberOfRegion];
		this.totalRuralCompany = new int[this.numberOfRegion];
		this.totalRuralEmployee = new int[this.numberOfRegion];
		this.startupRuralCompany = new int[this.numberOfRegion];
		this.startupRuralEmployee = new int[this.numberOfRegion];
		this.totalUrbanCompany = new int[this.numberOfRegion];
		this.totalUrbanEmployee = new int[this.numberOfRegion];
		this.startupUrbanCompany = new int[this.numberOfRegion];
		this.startupUrbanEmployee = new int[this.numberOfRegion];
	}
	
	public void initateDetailedVariables(){
		this.totalCompanyByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.totalEmployeeByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.startupCompanyByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.startupEmployeeByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.totalRuralCompanyByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.totalRuralEmployeeByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.startupRuralCompanyByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.startupRuralEmployeeByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.totalUrbanCompanyByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.totalUrbanEmployeeByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.startupUrbanCompanyByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.startupUrbanEmployeeByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		
		this.totalCompanyByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		this.totalEmployeeByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		this.startupCompanyByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		this.startupEmployeeByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		this.totalRuralCompanyByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		this.totalRuralEmployeeByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		this.startupRuralCompanyByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		this.startupRuralEmployeeByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		this.totalUrbanCompanyByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		this.totalUrbanEmployeeByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		this.startupUrbanCompanyByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		this.startupUrbanEmployeeByDuration = new int[this.numberOfRegion][this.numberOfDuration];
		
		this.totalCompanyBySize = new int[this.numberOfRegion][this.numberOfSize];
		this.totalEmployeeBySize = new int[this.numberOfRegion][this.numberOfSize];
		this.startupCompanyBySize = new int[this.numberOfRegion][this.numberOfSize];
		this.startupEmployeeBySize = new int[this.numberOfRegion][this.numberOfSize];
		this.totalRuralCompanyBySize = new int[this.numberOfRegion][this.numberOfSize];
		this.totalRuralEmployeeBySize = new int[this.numberOfRegion][this.numberOfSize];
		this.startupRuralCompanyBySize = new int[this.numberOfRegion][this.numberOfSize];
		this.startupRuralEmployeeBySize = new int[this.numberOfRegion][this.numberOfSize];
		this.totalUrbanCompanyBySize = new int[this.numberOfRegion][this.numberOfSize];
		this.totalUrbanEmployeeBySize = new int[this.numberOfRegion][this.numberOfSize];
		this.startupUrbanCompanyBySize = new int[this.numberOfRegion][this.numberOfSize];
		this.startupUrbanEmployeeBySize = new int[this.numberOfRegion][this.numberOfSize];
		
		this.sumOfCompanyByIndustry = new int[this.numberOfIndustry];
		this.sumOfEmployeeByIndustry = new int[this.numberOfIndustry];
		this.sumOfStartupCompanyByIndustry = new int[this.numberOfIndustry];
		this.sumOfStartupEmployeeByIndustry = new int[this.numberOfIndustry];
		this.sumOfRuralCompanyByIndustry = new int[this.numberOfIndustry];
		this.sumOfRuralEmployeeByIndustry = new int[this.numberOfIndustry];
		this.sumOfStartupRuralCompanyByIndustry = new int[this.numberOfIndustry];
		this.sumOfStartupRuralEmployeeByIndustry = new int[this.numberOfIndustry];
		this.sumOfUrbanCompanyByIndustry = new int[this.numberOfIndustry];
		this.sumOfUrbanEmployeeByIndustry = new int[this.numberOfIndustry];
		this.sumOfStartupUrbanCompanyByIndustry = new int[this.numberOfIndustry];
		this.sumOfStartupUrbanEmployeeByIndustry = new int[this.numberOfIndustry];
		
		this.sumOfCompanyByDuration = new int[this.numberOfDuration];
		this.sumOfEmployeeByDuration = new int[this.numberOfDuration];
		this.sumOfStartupCompanyByDuration = new int[this.numberOfDuration];
		this.sumOfStartupEmployeeByDuration = new int[this.numberOfDuration];
		this.sumOfRuralCompanyByDuration = new int[this.numberOfDuration];
		this.sumOfRuralEmployeeByDuration = new int[this.numberOfDuration];
		this.sumOfStartupRuralCompanyByDuration = new int[this.numberOfDuration];
		this.sumOfStartupRuralEmployeeByDuration = new int[this.numberOfDuration];
		this.sumOfUrbanCompanyByDuration = new int[this.numberOfDuration];
		this.sumOfUrbanEmployeeByDuration = new int[this.numberOfDuration];
		this.sumOfStartupUrbanCompanyByDuration = new int[this.numberOfDuration];
		this.sumOfStartupUrbanEmployeeByDuration = new int[this.numberOfDuration];
		
		this.sumOfCompanyBySize = new int[this.numberOfSize];
		this.sumOfEmployeeBySize = new int[this.numberOfSize];
		this.sumOfStartupCompanyBySize = new int[this.numberOfSize];
		this.sumOfStartupEmployeeBySize = new int[this.numberOfSize];
		this.sumOfRuralCompanyBySize = new int[this.numberOfSize];
		this.sumOfRuralEmployeeBySize = new int[this.numberOfSize];
		this.sumOfStartupRuralCompanyBySize = new int[this.numberOfSize];
		this.sumOfStartupRuralEmployeeBySize = new int[this.numberOfSize];
		this.sumOfUrbanCompanyBySize = new int[this.numberOfSize];
		this.sumOfUrbanEmployeeBySize = new int[this.numberOfSize];
		this.sumOfStartupUrbanCompanyBySize = new int[this.numberOfSize];
		this.sumOfStartupUrbanEmployeeBySize = new int[this.numberOfSize];
	}
	
	public void setBusinessDurationSectors(int[] durations){
		this.durationSector = durations;
		this.numberOfDuration = durations.length;
	}
	
	public void setEmployeeSizeSectors(int[] sizes){
		this.sizeSector = sizes;
		this.numberOfSize = sizes.length;
	}
	
	public void readLocationCode(String locationCodeFile){
		String tmpCode, tmpRegion;
		
		try{
			File file = new File(locationCodeFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpRegion = scan.next();
				if(tmpCode.length() == this.locationCodeDepth) this.locationCodeList.add(tmpCode);
				this.locationCodeMap.put(tmpCode, tmpRegion);
			}
			this.numberOfRegion = this.locationCodeList.size();
			scan.close();	
		} catch(IOException e) {
			System.err.print(e);
		}
	}
	
	public void readIndustryCode(String industryCodeFile){
		int codeLength;
		String tmpCode, tmpIndustry;
		
		try{
			File file = new File(industryCodeFile);
			Scanner scan = new Scanner(file);
		
			if(this.industryCodeDepth == 0) codeLength = 1;
			else codeLength = this.industryCodeDepth;
			
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpIndustry = scan.next();
				if(tmpCode.length() == codeLength) this.industryCodeList.add(tmpCode);
				this.industryCodeMap.put(tmpCode, tmpIndustry);
			}
			
			if(this.industryCodeDepth == 0) this.numberOfIndustry = 3;
			else	this.numberOfIndustry = this.industryCodeList.size();
	
			scan.close();	
		}  catch(IOException e) {
			System.err.print(e);
		}
	}
	
	public void readMicrodataCode(String microdataFile){
		MicroData mdata;
		int foundedYear , workers,  business, store;
		String location, industry;
		
		try{
			File file = new File(microdataFile);
			Scanner scan = new Scanner(file);
		
			scan.nextLine();
			while(scan.hasNext()){
				foundedYear = scan.nextInt();
				location = scan.next();
				industry = scan.next();
				workers = scan.nextInt();
				business = scan.nextInt();
				store = scan.nextInt();
				mdata =  new MicroData(foundedYear, location, industry, workers, business, store);
				if(this.retainedBusinessType == 0 
						|| this.classifyBusinessGroup(mdata) == this.retainedBusinessType) 
			//		if(this.classifytIndustrialGroup(mdata) == 3)
					this.microdataList.add(mdata);
			}
			this.numberOfCompany = this.microdataList.size();
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void retainBusinessType(int retainedBusinessType){
		for(int i=0 ; i< this.numberOfCompany ; i++){
			if(this.classifyBusinessGroup(this.microdataList.get(i)) != retainedBusinessType) {
				this.microdataList.remove(i--);
				this.numberOfCompany--;
			}
		}
	}
	
	public void analyzeCompanyData(){
		int regionIndex;
		int tmpWorkers;
		boolean areaCheck;			//true: urban area, false: rural area
		String regionCode;
		MicroData mdata;
		ArrayList<String> wrongRegionCode = new ArrayList<String>();
		
		/*** analyze company by region ***/
		for(int i=0 ; i<this.numberOfCompany ; i++){
			mdata = this.microdataList.get(i);
			regionCode = mdata.getLocationCode().substring(0, this.locationCodeDepth);
			
			if(this.locationCodeList.contains(regionCode)){
				regionIndex = this.locationCodeList.indexOf(regionCode);
				if(this.locationCodeMap.get(mdata.getLocationCode()).endsWith("동")) areaCheck = true;
				else areaCheck = false;
				tmpWorkers = mdata.getWorkers();
				this.sumOfCompany++;
				this.sumOfEmployee += tmpWorkers;
				this.totalCompany[regionIndex]++;
				this.totalEmployee[regionIndex] +=tmpWorkers;
				if(areaCheck){
					this.sumOfUrbanCompany++;
					this.sumOfUrbanEmployee += tmpWorkers;
					this.totalUrbanCompany[regionIndex]++;	
					this.totalUrbanEmployee[regionIndex] +=tmpWorkers;
				}
				else{
					this.sumOfRuralCompany++;
					this.sumOfRuralEmployee += tmpWorkers;
					this.totalRuralCompany[regionIndex]++;	
					 this.totalRuralEmployee[regionIndex] +=tmpWorkers;
				}
				if(mdata.getFoundedYear() == this.yearOfData){	
					this.sumOfStartupCompany++;
					this.sumOfStartupEmployee += tmpWorkers;
					this.startupCompany[regionIndex]++;	
					this.startupEmployee[regionIndex] +=tmpWorkers;	
					if(areaCheck){
						this.sumOfStartupUrbanCompany++;
						this.sumOfStartupUrbanEmployee += tmpWorkers;
						this.startupUrbanCompany[regionIndex]++;	
						this.startupUrbanEmployee[regionIndex] +=tmpWorkers;	
					}
					else{
						this.sumOfStartupRuralCompany++;
						this.sumOfStartupRuralEmployee += tmpWorkers;
						this.startupRuralCompany[regionIndex]++;	
						this.startupRuralEmployee[regionIndex] +=tmpWorkers;
					}
				}
			}else if(!wrongRegionCode.contains(regionCode)) wrongRegionCode.add(regionCode);
		}
		
		for(int i=0 ; i<wrongRegionCode.size() ; i++) 
			System.err.println(this.yearOfData+" wrong region code: "+wrongRegionCode.get(i));
	}

	public int classifyDurationGroup(MicroData mdata){
		int duration = this.yearOfData - mdata.getFoundedYear();
		
		for(int i = 0 ; i<this.numberOfDuration-1 ; i++)
			if(duration >= this.durationSector[i] && duration < this.durationSector[i+1]) return i;
		if(duration >= this.durationSector[this.numberOfDuration-1]) return this.numberOfDuration-1;
		
		return -1;
	}
	
	public int classifySizeGroup(MicroData mdata){
		int size = mdata.getWorkers();
		
		for(int i = 0 ; i<this.numberOfSize-1 ; i++)
			if(size >= this.sizeSector[i] && size < this.sizeSector[i+1]) return i;
		if(size >= this.sizeSector[this.numberOfSize-1]) return this.numberOfSize-1;
		
		return -1;
	}
	
	public int classifyBusinessGroup(MicroData mdata){
		int startYear = 1998;
		int endYear = 2012;
		int tmpBusinessType = mdata.getBusinessType();
		int[] personal = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		int[] company = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
		int[] corporation = {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
		int[] incorporation = {4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 4, 4, 4, 4, 4};
		int[] national = {-1, -1, -1, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5};
		
		if(this.yearOfData > endYear || this.yearOfData<startYear) return -1;
		else if(tmpBusinessType == -1) return -1;
		else if(tmpBusinessType == personal[this.yearOfData-startYear]) return 1;
		else if(tmpBusinessType == company[this.yearOfData-startYear]) return 2;
		else if(tmpBusinessType == corporation[this.yearOfData-startYear]) return 3;
		else if(tmpBusinessType == incorporation[this.yearOfData-startYear]) return 4;
		else if(tmpBusinessType == national[this.yearOfData-startYear]) return 5;
		else return -1;
	}
	
	public int classifytIndustrialGroup(MicroData mdata){
		int tmpIndustryType = -1;		//1: primary, 2: secondary, 3: tertiary industrial group
		String tmpStr = mdata.getIndustryCode().substring(0, 1);;
		
		/*** set industry class ***/
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
		return tmpIndustryType;
	}
	
	public void analyzeCompanyDataByIndustry(){
		int regionIndex, industryIndex;
		int tmpWorkers;
		boolean areaCheck;					//true: urban area, false: rural area
		String regionCode, industryCode;
		MicroData mdata;
		
		/*** analyze company by region and by industry ***/
		for(int i=0 ; i<this.numberOfCompany ; i++){
			mdata = this.microdataList.get(i);
			regionCode = mdata.getLocationCode().substring(0, this.locationCodeDepth);
			if(this.industryCodeDepth <= 1) industryCode = mdata.getIndustryCode().substring(0, 1);
			else	industryCode = mdata.getIndustryCode().substring(1, this.industryCodeDepth);
			if(this.locationCodeList.contains(regionCode) && this.industryCodeList.contains(industryCode)){		
				regionIndex = this.locationCodeList.indexOf(regionCode);
				if(this.industryCodeDepth == 0)  industryIndex = this.classifytIndustrialGroup(mdata)-1;
				else industryIndex = this.industryCodeList.indexOf(industryCode);
				if(this.locationCodeMap.get(mdata.getLocationCode()).endsWith("동")) areaCheck = true;
				else areaCheck = false;
				tmpWorkers = mdata.getWorkers();
				this.sumOfCompanyByIndustry[industryIndex]++;
				this.sumOfEmployeeByIndustry[industryIndex] += tmpWorkers;
				this.totalCompanyByIndustry[regionIndex][industryIndex]++;
				this.totalEmployeeByIndustry[regionIndex][industryIndex] += tmpWorkers;
				if(areaCheck){
					this.sumOfUrbanCompanyByIndustry[industryIndex]++;
					this.sumOfUrbanEmployeeByIndustry[industryIndex] += tmpWorkers;
					this.totalUrbanCompanyByIndustry[regionIndex][industryIndex]++;
					this.totalUrbanEmployeeByIndustry[regionIndex][industryIndex] += tmpWorkers;
				}
				else{
					this.sumOfRuralCompanyByIndustry[industryIndex]++;
					this.sumOfRuralEmployeeByIndustry[industryIndex] += tmpWorkers;
					this.totalRuralCompanyByIndustry[regionIndex][industryIndex]++;
					this.totalRuralEmployeeByIndustry[regionIndex][industryIndex] += tmpWorkers;
				}
				if(mdata.getFoundedYear() == this.yearOfData){
					this.sumOfStartupCompanyByIndustry[industryIndex]++;
					this.sumOfStartupEmployeeByIndustry[industryIndex] += tmpWorkers;
					this.startupCompanyByIndustry[regionIndex][industryIndex]++;
					this.startupEmployeeByIndustry[regionIndex][industryIndex] += tmpWorkers;
					if(areaCheck){
						this.sumOfStartupUrbanCompanyByIndustry[industryIndex]++;
						this.sumOfStartupUrbanEmployeeByIndustry[industryIndex] += tmpWorkers;
						this.startupUrbanCompanyByIndustry[regionIndex][industryIndex]++;
						this.startupUrbanEmployeeByIndustry[regionIndex][industryIndex] += tmpWorkers;
					}
					else{
						this.sumOfStartupRuralCompanyByIndustry[industryIndex]++;
						this.sumOfStartupRuralEmployeeByIndustry[industryIndex] += tmpWorkers;
						this.startupRuralCompanyByIndustry[regionIndex][industryIndex]++;
						this.startupRuralEmployeeByIndustry[regionIndex][industryIndex] += tmpWorkers;
					}
				}
			}
		}
	}
	
	public void analyzeCompanyDataByDuration(){
		int regionIndex, durationClass;
		int tmpWorkers;
		boolean areaCheck;					//true: urban area, false: rural area
		String regionCode;
		MicroData mdata;
		
		/*** analyze company by region and by duration ***/
		for(int i=0 ; i<this.numberOfCompany ; i++){
			mdata = this.microdataList.get(i);
			regionCode = mdata.getLocationCode().substring(0, this.locationCodeDepth);
			durationClass = this.classifyDurationGroup(mdata);
			if(this.locationCodeList.contains(regionCode) && durationClass >= 0){		
				tmpWorkers = mdata.getWorkers();
				regionIndex = this.locationCodeList.indexOf(regionCode);
				if(this.locationCodeMap.get(mdata.getLocationCode()).endsWith("동")) areaCheck = true;
				else areaCheck = false;
				this.sumOfCompanyByDuration[durationClass]++;
				this.sumOfEmployeeByDuration[durationClass] += tmpWorkers;
				this.totalCompanyByDuration[regionIndex][durationClass]++;
				this.totalEmployeeByDuration[regionIndex][durationClass] += tmpWorkers;
				if(areaCheck){
					this.sumOfUrbanCompanyByDuration[durationClass]++;
					this.sumOfUrbanEmployeeByDuration[durationClass] += tmpWorkers;
					this.totalUrbanCompanyByDuration[regionIndex][durationClass]++;
					this.totalUrbanEmployeeByDuration[regionIndex][durationClass] += tmpWorkers;
				}
				else{
					this.sumOfRuralCompanyByDuration[durationClass]++;
					this.sumOfRuralEmployeeByDuration[durationClass] += tmpWorkers;
					this.totalRuralCompanyByDuration[regionIndex][durationClass]++;
					this.totalRuralEmployeeByDuration[regionIndex][durationClass] += tmpWorkers;
				}
				if(mdata.getFoundedYear() == this.yearOfData){
					this.sumOfStartupCompanyByDuration[durationClass]++;
					this.sumOfStartupEmployeeByDuration[durationClass] += tmpWorkers;
					this.startupCompanyByDuration[regionIndex][durationClass]++;
					this.startupEmployeeByDuration[regionIndex][durationClass] += tmpWorkers;
					if(areaCheck){
						this.sumOfStartupUrbanCompanyByDuration[durationClass]++;
						this.sumOfStartupUrbanEmployeeByDuration[durationClass] += tmpWorkers;
						this.startupUrbanCompanyByDuration[regionIndex][durationClass]++;
						this.startupUrbanEmployeeByDuration[regionIndex][durationClass] += tmpWorkers;
					}
					else{
						this.sumOfStartupRuralCompanyByDuration[durationClass]++;
						this.sumOfStartupRuralEmployeeByDuration[durationClass] += tmpWorkers;
						this.startupRuralCompanyByDuration[regionIndex][durationClass]++;
						this.startupRuralEmployeeByDuration[regionIndex][durationClass] += tmpWorkers;
					}
				}
			}
		}
	}
	
	public void analyzeCompanyDataBySize(){
		int regionIndex, sizeClass;
		int tmpWorkers;
		boolean areaCheck;					//true: urban area, false: rural area
		String regionCode;
		MicroData mdata;
		
		/*** analyze company by region and by duration ***/
		for(int i=0 ; i<this.numberOfCompany ; i++){
			mdata = this.microdataList.get(i);
			regionCode = mdata.getLocationCode().substring(0, this.locationCodeDepth);
			sizeClass = this.classifySizeGroup(mdata);
			if(this.locationCodeList.contains(regionCode) && sizeClass >= 0){		
				tmpWorkers = mdata.getWorkers();
				regionIndex = this.locationCodeList.indexOf(regionCode);
				if(this.locationCodeMap.get(mdata.getLocationCode()).endsWith("동")) areaCheck = true;
				else areaCheck = false;
				this.sumOfCompanyBySize[sizeClass]++;
				this.sumOfEmployeeBySize[sizeClass] += tmpWorkers;
				this.totalCompanyBySize[regionIndex][sizeClass]++;
				this.totalEmployeeBySize[regionIndex][sizeClass] += tmpWorkers;
				if(areaCheck){
					this.sumOfUrbanCompanyBySize[sizeClass]++;
					this.sumOfUrbanEmployeeBySize[sizeClass] += tmpWorkers;
					this.totalUrbanCompanyBySize[regionIndex][sizeClass]++;
					this.totalUrbanEmployeeBySize[regionIndex][sizeClass] += tmpWorkers;
				}
				else{
					this.sumOfRuralCompanyBySize[sizeClass]++;
					this.sumOfRuralEmployeeBySize[sizeClass] += tmpWorkers;
					this.totalRuralCompanyBySize[regionIndex][sizeClass]++;
					this.totalRuralEmployeeBySize[regionIndex][sizeClass] += tmpWorkers;
				}
				if(mdata.getFoundedYear() == this.yearOfData){
					this.sumOfStartupCompanyBySize[sizeClass]++;
					this.sumOfStartupEmployeeBySize[sizeClass] += tmpWorkers;
					this.startupCompanyBySize[regionIndex][sizeClass]++;
					this.startupEmployeeBySize[regionIndex][sizeClass] += tmpWorkers;
					if(areaCheck){
						this.sumOfStartupUrbanCompanyBySize[sizeClass]++;
						this.sumOfStartupUrbanEmployeeBySize[sizeClass] += tmpWorkers;
						this.startupUrbanCompanyBySize[regionIndex][sizeClass]++;
						this.startupUrbanEmployeeBySize[regionIndex][sizeClass] += tmpWorkers;
					}
					else{
						this.sumOfStartupRuralCompanyBySize[sizeClass]++;
						this.sumOfStartupRuralEmployeeBySize[sizeClass] += tmpWorkers;
						this.startupRuralCompanyBySize[regionIndex][sizeClass]++;
						this.startupRuralEmployeeBySize[regionIndex][sizeClass] += tmpWorkers;
					}
				}
			}
		}
	}
	
	public void analyzeEmployeesStatistics(){
		int i;
		int tmpTotal, tmpEmployees, tmpMax;
		double tmpAverage;
		
		/*** analyze total, average, and maximum employees ***/
		tmpMax = 0;
		tmpTotal = 0;
		tmpEmployees = 0;
		for(i=0 ; i<this.numberOfCompany ; i++){
			tmpEmployees = this.microdataList.get(i).getWorkers();
			if(tmpMax < tmpEmployees) tmpMax = tmpEmployees;
			tmpTotal += tmpEmployees;
		}
		tmpAverage = (double) tmpTotal / (double) this.numberOfCompany;
		
		this.maxEmployees = tmpMax;
		this.totalEmployees = tmpTotal;
		this.averageEmployees = tmpAverage;
	}
	
	public void proceedDataReading(String locationFile, String industryFile, String microdataFile){
		this.readLocationCode(locationFile);
		this.readIndustryCode(industryFile);
		this.initiateVariables();
		this.initateDetailedVariables();
		this.readMicrodataCode(microdataFile);
	}
	
	public void proceedDataReading(String locationFile, String industryFile, String microdataFile, 
															int[] durations, int[] sizes){
		this.setBusinessDurationSectors(durations);
		this.setEmployeeSizeSectors(sizes);
		this.proceedDataReading(locationFile, industryFile, microdataFile);
	}
	
	public void proceedAnalysis(){
		this.analyzeCompanyData();
		this.analyzeCompanyDataByIndustry();
		this.analyzeCompanyDataByDuration();
		this.analyzeCompanyDataBySize();
		this.analyzeEmployeesStatistics();
	}
	
	public void proceedAnalysis(CompanyData cdata){
		this.proceedAnalysis();
		this.addBasicData(cdata);
		this.addVariables(cdata);
		this.addDetailedVariables(cdata);
	}
	
	public void addBasicData(CompanyData cdata){
		cdata.addLocationCodeList(this.locationCodeList);
		cdata.addIndustryCodeList(this.industryCodeList);
		cdata.addLocationCodeMap(this.locationCodeMap);
		cdata.addIndustryCodeMap(this.industryCodeMap);
		
		cdata.addNumberOfRegion(this.numberOfRegion);
		cdata.addNumberOfCompany(this.numberOfCompany);
		cdata.addNumberOfIndustry(this.numberOfIndustry);
		cdata.addNumberOfDuration(this.numberOfDuration);
		cdata.addNumberOfSize(this.numberOfSize);
		cdata.addMaxEmployees(this.maxEmployees);
		cdata.addTotalEmployees(this.totalEmployees);
		cdata.addAverageEmployees(this.averageEmployees);
	}
	
	public void addVariables(CompanyData cdata){
		cdata.addTotalCompany(this.totalCompany);
		cdata.addTotalEmployee(this.totalEmployee);
		cdata.addStartupCompany(this.startupCompany);
		cdata.addStartupEmployee(this.startupEmployee);
		cdata.addTotalRuralCompany(this.totalRuralCompany);
		cdata.addTotalRuralEmployee(this.totalRuralEmployee);
		cdata.addStartupRuralCompany(this.startupRuralCompany);
		cdata.addStartupRuralEmployee(this.startupRuralEmployee);
		cdata.addTotalUrbanCompany(this.totalUrbanCompany);
		cdata.addTotalUrbanEmployee(this.totalUrbanEmployee);
		cdata.addStartupUrbanCompany(this.startupUrbanCompany);
		cdata.addStartupUrbanEmployee(this.startupUrbanEmployee);
		
		cdata.addSumOfCompany(this.sumOfCompany);
		cdata.addSumOfEmployee(this.sumOfEmployee);
		cdata.addSumOfStartupCompany(this.sumOfStartupCompany);
		cdata.addSumOfStartupEmployee(this.sumOfStartupEmployee);
		cdata.addSumOfRuralCompany(this.sumOfRuralCompany);
		cdata.addSumOfRuralEmployee(this.sumOfRuralEmployee);
		cdata.addSumOfStartupRuralCompany(this.sumOfStartupRuralCompany);
		cdata.addSumOfStartupRuralEmployee(this.sumOfStartupRuralEmployee);
		cdata.addSumOfUrbanCompany(this.sumOfUrbanCompany);
		cdata.addSumOfUrbanEmployee(this.sumOfUrbanEmployee);
		cdata.addSumOfStartupUrbanCompany(this.sumOfStartupUrbanCompany);
		cdata.addSumOfStartupUrbanEmployee(this.sumOfStartupUrbanEmployee);
	}
	
	public void addDetailedVariables(CompanyData cdata){
		cdata.addTotalCompanyByIndustry(this.totalCompanyByIndustry);
		cdata.addTotalEmployeeByIndustry(this.totalEmployeeByIndustry);
		cdata.addStartupCompanyByIndustry(this.startupCompanyByIndustry);
		cdata.addStartupEmployeeByIndustry(this.startupEmployeeByIndustry);
		cdata.addTotalRuralCompanyByIndustry(this.totalRuralCompanyByIndustry);
		cdata.addTotalRuralEmployeeByIndustry(this.totalRuralEmployeeByIndustry);
		cdata.addStartupRuralCompanyByIndustry(this.startupRuralCompanyByIndustry);
		cdata.addStartupRuralEmployeeByIndustry(this.startupRuralEmployeeByIndustry);
		cdata.addTotalUrbanCompanyByIndustry(this.totalUrbanCompanyByIndustry);
		cdata.addTotalUrbanEmployeeByIndustry(this.totalUrbanEmployeeByIndustry);
		cdata.addStartupUrbanCompanyByIndustry(this.startupUrbanCompanyByIndustry);
		cdata.addStartupUrbanEmployeeByIndustry(this.startupUrbanEmployeeByIndustry);

		cdata.addTotalCompanyByDuration(this.totalCompanyByDuration);
		cdata.addTotalEmployeeByDuration(this.totalEmployeeByDuration);
		cdata.addStartupCompanyByDuration(this.startupCompanyByDuration);
		cdata.addStartupEmployeeByDuration(this.startupEmployeeByDuration);
		cdata.addTotalRuralCompanyByDuration(this.totalRuralCompanyByDuration);
		cdata.addTotalRuralEmployeeByDuration(this.totalRuralEmployeeByDuration);
		cdata.addStartupRuralCompanyByDuration(this.startupRuralCompanyByDuration);
		cdata.addStartupRuralEmployeeByDuration(this.startupRuralEmployeeByDuration);
		cdata.addTotalUrbanCompanyByDuration(this.totalUrbanCompanyByDuration);
		cdata.addTotalUrbanEmployeeByDuration(this.totalUrbanEmployeeByDuration);
		cdata.addStartupUrbanCompanyByDuration(this.startupUrbanCompanyByDuration);
		cdata.addStartupUrbanEmployeeByDuration(this.startupUrbanEmployeeByDuration);
		
		cdata.addTotalCompanyBySize(this.totalCompanyBySize);
		cdata.addTotalEmployeeBySize(this.totalEmployeeBySize);
		cdata.addStartupCompanyBySize(this.startupCompanyBySize);
		cdata.addStartupEmployeeBySize(this.startupEmployeeBySize);
		cdata.addTotalRuralCompanyBySize(this.totalRuralCompanyBySize);
		cdata.addTotalRuralEmployeeBySize(this.totalRuralEmployeeBySize);
		cdata.addStartupRuralCompanyBySize(this.startupRuralCompanyBySize);
		cdata.addStartupRuralEmployeeBySize(this.startupRuralEmployeeBySize);
		cdata.addTotalUrbanCompanyBySize(this.totalUrbanCompanyBySize);
		cdata.addTotalUrbanEmployeeBySize(this.totalUrbanEmployeeBySize);
		cdata.addStartupUrbanCompanyBySize(this.startupUrbanCompanyBySize);
		cdata.addStartupUrbanEmployeeBySize(this.startupUrbanEmployeeBySize);
		
		cdata.addSumOfCompanyByIndustry(this.sumOfCompanyByIndustry);
		cdata.addSumOfEmployeeByIndustry(this.sumOfEmployeeByIndustry);
		cdata.addSumOfStartupCompanyByIndustry(this.sumOfStartupCompanyByIndustry);
		cdata.addSumOfStartupEmployeeByIndustry(this.sumOfStartupEmployeeByIndustry);
		cdata.addSumOfRuralCompanyByIndustry(this.sumOfRuralCompanyByIndustry);
		cdata.addSumOfRuralEmployeeByIndustry(this.sumOfRuralEmployeeByIndustry);
		cdata.addSumOfStartupRuralCompanyByIndustry(this.sumOfStartupRuralCompanyByIndustry);
		cdata.addSumOfStartupRuralEmployeeByIndustry(this.sumOfStartupRuralEmployeeByIndustry);
		cdata.addSumOfUrbanCompanyByIndustry(this.sumOfUrbanCompanyByIndustry);
		cdata.addSumOfUrbanEmployeeByIndustry(this.sumOfUrbanEmployeeByIndustry);
		cdata.addSumOfStartupUrbanCompanyByIndustry(this.sumOfStartupUrbanCompanyByIndustry);
		cdata.addSumOfStartupUrbanEmployeeByIndustry(this.sumOfStartupUrbanEmployeeByIndustry);

		cdata.addSumOfCompanyByDuration(this.sumOfCompanyByDuration);
		cdata.addSumOfEmployeeByDuration(this.sumOfEmployeeByDuration);
		cdata.addSumOfStartupCompanyByDuration(this.sumOfStartupCompanyByDuration);
		cdata.addSumOfStartupEmployeeByDuration(this.sumOfStartupEmployeeByDuration);
		cdata.addSumOfRuralCompanyByDuration(this.sumOfRuralCompanyByDuration);
		cdata.addSumOfRuralEmployeeByDuration(this.sumOfRuralEmployeeByDuration);
		cdata.addSumOfStartupRuralCompanyByDuration(this.sumOfStartupRuralCompanyByDuration);
		cdata.addSumOfStartupRuralEmployeeByDuration(this.sumOfStartupRuralEmployeeByDuration);
		cdata.addSumOfUrbanCompanyByDuration(this.sumOfUrbanCompanyByDuration);
		cdata.addSumOfUrbanEmployeeByDuration(this.sumOfUrbanEmployeeByDuration);
		cdata.addSumOfStartupUrbanCompanyByDuration(this.sumOfStartupUrbanCompanyByDuration);
		cdata.addSumOfStartupUrbanEmployeeByDuration(this.sumOfStartupUrbanEmployeeByDuration);
		
		cdata.addSumOfCompanyBySize(this.sumOfCompanyBySize);
		cdata.addSumOfEmployeeBySize(this.sumOfEmployeeBySize);
		cdata.addSumOfStartupCompanyBySize(this.sumOfStartupCompanyBySize);
		cdata.addSumOfStartupEmployeeBySize(this.sumOfStartupEmployeeBySize);
		cdata.addSumOfRuralCompanyBySize(this.sumOfRuralCompanyBySize);
		cdata.addSumOfRuralEmployeeBySize(this.sumOfRuralEmployeeBySize);
		cdata.addSumOfStartupRuralCompanyBySize(this.sumOfStartupRuralCompanyBySize);
		cdata.addSumOfStartupRuralEmployeeBySize(this.sumOfStartupRuralEmployeeBySize);
		cdata.addSumOfUrbanCompanyBySize(this.sumOfUrbanCompanyBySize);
		cdata.addSumOfUrbanEmployeeBySize(this.sumOfUrbanEmployeeBySize);
		cdata.addSumOfStartupUrbanCompanyBySize(this.sumOfStartupUrbanCompanyBySize);
		cdata.addSumOfStartupUrbanEmployeeBySize(this.sumOfStartupUrbanEmployeeBySize);
	}
}
