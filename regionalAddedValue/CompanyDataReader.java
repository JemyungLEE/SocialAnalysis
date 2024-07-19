package regionalAddedValue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import regionalAddedValue.data.CompanyData;
import regionalAddedValue.data.MicroData;

public class CompanyDataReader {
	/**
	 *  Subject: Read company data from extracted micro-data
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2017.1.12
	 *  Last Modified Data: 2017.1.12
	 *  Description: read region, industrial category, employee of  companies
	 */

	int yearOfData;
	int locationCodeDepth;		
	int industryCodeDepth;						//0: primary, secondary, tertiary industrial group
	int numberOfCompany, numberOfRegion;
	int numberOfIndustry;
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
			//	if(this.classifytIndustrialGroup(mdata) == 1)
				this.microdataList.add(mdata);
			}
			this.numberOfCompany = this.microdataList.size();
			scan.close();	
		} catch(IOException e) {}
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
		this.readMicrodataCode(microdataFile);
	}
	
	public void proceedAnalysis(){
		this.analyzeCompanyData();
		this.analyzeCompanyDataByIndustry();
		this.analyzeEmployeesStatistics();
	}
	
	public void proceedAnalysis(CompanyData cdata){
		this.proceedAnalysis();
		this.addBasicData(cdata);
		this.addVariables(cdata);
	}
	
	public void addBasicData(CompanyData cdata){
		cdata.addLocationCodeList(this.locationCodeList);
		cdata.addIndustryCodeList(this.industryCodeList);
		cdata.addLocationCodeMap(this.locationCodeMap);
		cdata.addIndustryCodeMap(this.industryCodeMap);
		
		cdata.addNumberOfRegion(this.numberOfRegion);
		cdata.addNumberOfCompany(this.numberOfCompany);
		cdata.addNumberOfIndustry(this.numberOfIndustry);
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
	}
}
