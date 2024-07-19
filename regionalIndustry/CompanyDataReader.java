package regionalIndustry;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.HashMap;

import regionalIndustry.data.MicroData;

public class CompanyDataReader {
	/**
	 *  Subject: Read company data from extracted micro-data
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2016.11.9
	 *  Last Modified Data: 2016.11.10
	 *  Description: read region, industry classification, employee of new established companies
	 */

	int yearOfData;
	int locationCodeDepth;
	int industryCodeDepth;
	int numberOfCompany;
	int numberOfRegion;
	int numberOfIndustry;
	int maxEmployees, totalEmployees;
	double averageEmployees;
	
	int[] totalCompany, totalEmployee;													//[region]
	int[] startupCompany, startupEmployee;											//[region]
	int[][] totalCompanyByIndustry, totalEmployeeByIndustry;				//[region][industry]
	int[][] startupCompanyByIndustry, startupEmployeeByIndustry;		//[region][industry]
	
	ArrayList<String> locationCodeList;
	ArrayList<String> industryCodeList;
	
	HashMap<String, String> locationCodeMap;
	HashMap<String, String> industryCodeMap;

	ArrayList<MicroData> microdataList;
	
	public CompanyDataReader(){
		this.initiate();
	}
	
	public CompanyDataReader(int year){
		this.initiate(year);
	}
	
	public CompanyDataReader(int year, int location, int industry){
		this.initiate(year, location, industry);
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
		this.totalCompanyByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.totalEmployeeByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.startupCompanyByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
		this.startupEmployeeByIndustry = new int[this.numberOfRegion][this.numberOfIndustry];
	}
	
	public void readLocationCode(String locationCodeFile){
		String tmpCode, tmpRegion;
		
		try{
			File file = new File(locationCodeFile);
			Scanner scan = new Scanner(file);
		
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpRegion = scan.next();
				if(tmpCode.length() == this.locationCodeDepth) this.locationCodeList.add(tmpRegion);
				this.locationCodeMap.put(tmpCode, tmpRegion);
			}
			this.numberOfRegion = this.locationCodeList.size();
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void readIndustryCode(String industryCodeFile){
		String tmpCode, tmpIndustry;
		
		try{
			File file = new File(industryCodeFile);
			Scanner scan = new Scanner(file);
		
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpIndustry = scan.next();
				if(tmpCode.length() == this.industryCodeDepth) this.industryCodeList.add(tmpIndustry);
				this.industryCodeMap.put(tmpCode, tmpIndustry);
			}
			this.numberOfIndustry = this.industryCodeList.size();
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void readMicrodataCode(String microdataFile){
		MicroData mdata;
		int foundedYear , workers,  business, store;
		String location,  industry;
		
		try{
			File file = new File(microdataFile);
			Scanner scan = new Scanner(file);
		
			scan.nextLine();
			while(scan.hasNext()){
				foundedYear = scan.nextInt();
				location = scan.next().substring(0, this.locationCodeDepth);
				if(this.industryCodeDepth>1) industry = scan.next().substring(1, this.industryCodeDepth);
				else industry = scan.next().substring(0, 1);
				workers = scan.nextInt();
				business = scan.nextInt();
				store = scan.nextInt();
				mdata =  new MicroData(foundedYear, location, industry, workers, business, store);
				this.microdataList.add(mdata);
			}
			this.numberOfCompany = this.microdataList.size();
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void analyzeCompanies(){
		int i, j, k;
		int tmpTotal, tmpStartup;
		String tmpRegionCode, tmpIndustryCode;
		MicroData mdata;
		
		/*** analyze company by region ***/
		for(i=0 ; i<this.numberOfRegion ; i++){
			tmpTotal = 0;
			tmpStartup = 0;
			tmpRegionCode = this.locationCodeList.get(i);
			
			for(j=0 ; j<this.numberOfCompany ; j++){
				mdata = this.microdataList.get(j);
				if(mdata.getLocationCode().equals(tmpRegionCode)){
					tmpTotal++;
					if(mdata.getFoundedYear() == this.yearOfData) tmpStartup++;
				}
			}
			
			this.totalCompany[i] = tmpTotal;
			this.startupCompany[i] = tmpStartup;
		}
		
		/*** analyze company by region and by industry ***/
		for(i=0 ; i<this.numberOfRegion ; i++){
			tmpRegionCode = this.locationCodeList.get(i);
			for(j=0 ; j<this.numberOfIndustry ; j++){
				tmpTotal = 0;
				tmpStartup = 0;
				tmpIndustryCode = this.industryCodeList.get(j);
				for(k=0 ; k<this.numberOfCompany ; k++){
					mdata = this.microdataList.get(k);
					if(mdata.getLocationCode().equals(tmpRegionCode) 
							&& mdata.getIndustryCode().equals(tmpIndustryCode)){
						tmpTotal++;
						if(mdata.getFoundedYear() == this.yearOfData) tmpStartup++;
					}
				}
				
				this.totalCompanyByIndustry[i][j] = tmpTotal;
				this.startupCompanyByIndustry[i][j] = tmpStartup;
			}
		}
	}
	
	public void analyzeEmployees(){
		int i, j, k;
		int tmpTotal, tmpStartup, tmpEmployees, tmpMax;
		double tmpAverage;
		String tmpRegionCode, tmpIndustryCode;
		MicroData mdata;
		
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
		
		/*** analyze employees by region ***/
		for(i=0 ; i<this.numberOfRegion ; i++){
			tmpTotal = 0;
			tmpStartup = 0;
			tmpRegionCode = this.locationCodeList.get(i);
			
			for(j=0 ; j<this.numberOfCompany ; j++){
				mdata = this.microdataList.get(j);
				if(mdata.getLocationCode().equals(tmpRegionCode)){
					tmpTotal += mdata.getWorkers();
					if(mdata.getFoundedYear() == this.yearOfData) tmpStartup += mdata.getWorkers();
				}
			}
			
			this.totalEmployee[i] = tmpTotal;
			this.startupEmployee[i] = tmpStartup;
		}
		
		/*** analyze employees by region and by industry ***/
		for(i=0 ; i<this.numberOfRegion ; i++){
			tmpRegionCode = this.locationCodeList.get(i);
			for(j=0 ; j<this.numberOfIndustry ; j++){
				tmpTotal = 0;
				tmpStartup = 0;
				tmpIndustryCode = this.industryCodeList.get(j);
				for(k=0 ; k<this.numberOfCompany ; k++){
					mdata = this.microdataList.get(k);
					if(mdata.getLocationCode().equals(tmpRegionCode) 
							&& mdata.getIndustryCode().equals(tmpIndustryCode)){
						tmpTotal += mdata.getWorkers();
						if(mdata.getFoundedYear() == this.yearOfData) tmpStartup += mdata.getWorkers();
					}
				}
				
				this.totalEmployeeByIndustry[i][j] = tmpTotal;
				this.startupEmployeeByIndustry[i][j] = tmpStartup;
			}
		}
	}
	
	public int getTotalOfCompany(int region){
		return this.totalCompany[region];
	}
	
	public int getTotalOfEmployee(int region){
		return this.totalEmployee[region];
	}
	
	public int getTotalOfStartupCompany(int region){
		return this.startupCompany[region];
	}
	
	public int getTotalOfStartupEmployee(int region){
		return this.startupEmployee[region];
	}
	
	public int getTotalOfCompanyByIndustry(int region, int industry){
		return this.totalCompanyByIndustry[region][industry];
	}
	
	public int getTotalOfEmployeeByIndustry(int region, int industry){
		return this.totalEmployeeByIndustry[region][industry];
	}
	
	public int getTotalOfStartupCompanyByIndustry(int region, int industry){
		return this.startupCompanyByIndustry[region][industry];
	}
	
	public int getTotalOfStartupEmployeeByIndustry(int region, int industry){
		return this.startupEmployeeByIndustry[region][industry];
	}
	
	public static void main(String[] args) {

	}

}
