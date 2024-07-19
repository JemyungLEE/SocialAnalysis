package regionalIndustry;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.ranking.RankingAlgorithm;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;

public class RegionalDataAnalyzer {

	int startYear, endYear;
	int numberOfYears, numberOfRegions, numberOfIndustries;
	
	int regionDepth, industryDepth;
	
	double[][] population, migration, populationGrowth;			//[year][region]
	double[][] startupCompany, totalCompany;						//[year][region]
	double[][] startupEmployee, totalEmployee;						//[year][region]
	double[][][] company, employee;										//[year][region][industry]
	double[][] amountIndex, valueIndex;									//[year][region]
	double[][] grdp, grdpGrouth; 												//[year][region]
	
	ArrayList<Integer> yearList;
	ArrayList<String> regionList;
	ArrayList<String> industryList;
	HashMap<String, String> regionMap;		//<code, region name>
	HashMap<String, String> industryMap;		//<code, industry class name>
	HashMap<String, Double> profitMap;		//<code, profit>
	
	public  RegionalDataAnalyzer(){
		this.initiate();	
	}

	public  RegionalDataAnalyzer(int[] years){
		this.setYears(years);
		this.initiate();
	}
	
	public  RegionalDataAnalyzer(int[] years, int region, int industry){
		this.setYears(years);
		this.regionDepth = region;
		this.industryDepth = industry;
		this.initiate();
	}
	
	public void initiate(){
		this.regionList = new ArrayList<String>();
		this.industryList = new ArrayList<String>();
		this.industryMap = new HashMap<String, String>();
		this.profitMap = new HashMap<String, Double>();
	}
	
	public void setYears(int[] years){
		this.yearList = new ArrayList<Integer>();
		this.startYear = years[0];
		this.endYear = years[years.length-1];
		this.numberOfYears = years.length;
		for(int i=0 ; i<years.length ; i++) this.yearList.add(years[i]);
	}
	
	public void setYears(int start, int end){
		this.yearList = new ArrayList<Integer>();
		this.startYear = start;
		this.endYear = end;
		this.numberOfYears = end - start + 1;
		for(int i=0 ; i<this.numberOfYears ; i++) this.yearList.add(start+i);
	}
	
	public void readStandardLocationCode(String locationCodeFile){
		String tmpCode, tmpRegion;
		
		try{
			File file = new File(locationCodeFile);
			Scanner scan = new Scanner(file);
		
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpRegion = scan.next();
				if(tmpCode.length() == this.regionDepth) this.regionList.add(tmpRegion);
				this.regionMap.put(tmpCode, tmpRegion);
			}
			this.numberOfRegions = this.regionList.size();
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void readStandardIndustryCode(String industryCodeFile){
		String tmpCode, tmpIndustry;
		
		try{
			File file = new File(industryCodeFile);
			Scanner scan = new Scanner(file);
		
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpIndustry = scan.next();
				if(tmpCode.length() == this.industryDepth) this.industryList.add(tmpIndustry);
				this.industryMap.put(tmpCode, tmpIndustry);
			}
			this.numberOfIndustries = this.industryList.size();
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void readStandardIndustryProfit(String industryProfitFile){
		double tmpProfit;
		String tmpCode, tmpIndustry;
		
		try{
			File file = new File(industryProfitFile);
			Scanner scan = new Scanner(file);
		
			scan.nextLine();
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpIndustry = scan.next();
				tmpProfit = scan.nextDouble();
				if(this.industryMap.get(tmpCode).equals(tmpIndustry)) this.profitMap.put(tmpCode, tmpProfit);
				else System.err.println(tmpCode+"\t"+tmpIndustry+"\tprofit industry code doesn't match");
			}
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void analyzeCompanyData(String regionList, String industryList, String companyData){
		int i, j, k;
		int yearIndex, regionIndex, industryIndex;
		String tmpCode;
		boolean regionCheck, industryCheck;
		String companyFile, regionListFile, industryListFile;
		
		CompanyDataReader cdr;
		
		for(i=0 ; i<this.numberOfYears ; i++){
			companyFile = companyData.replace("year", ""+this.yearList.get(i));
			regionListFile = regionList.replace("year", ""+this.yearList.get(i));
			industryListFile = industryList.replace("year", ""+this.yearList.get(i));
			
			cdr = new CompanyDataReader(this.yearList.get(i));
			cdr.readLocationCode(regionListFile);
			cdr.readIndustryCode(industryListFile);
			cdr.readMicrodataCode(companyFile);
			cdr.analyzeCompanies();
			cdr.analyzeEmployees();
			
			yearIndex = i;
			for(j=0 ; j<cdr.getNumberofRegion() ; j++){
				tmpCode = cdr.getLocationCode(j);
				regionIndex = this.regionList.indexOf(tmpCode);
				if(this.regionMap.get(tmpCode).equals(cdr.getLocationMapValue(tmpCode))) 
					regionCheck = true;
				else regionCheck = false;
				
				if(regionCheck){
					this.totalCompany[yearIndex][regionIndex] = cdr.getTotalOfCompany(j);
					this.totalEmployee[yearIndex][regionIndex] = cdr.getTotalOfEmployee(j);
					this.startupCompany[yearIndex][regionIndex] = cdr.getTotalOfStartupCompany(j);
					this.startupEmployee[yearIndex][regionIndex] = cdr.getTotalOfStartupEmployee(j);
				}
				
				for(k=0 ; k<cdr.getNumberofIndustry() ; k++){
					tmpCode = cdr.getIndustryCode(k);
					industryIndex = this.industryList.indexOf(tmpCode);
					if(this.industryMap.get(tmpCode).equals(cdr.getIndustryMapValue(tmpCode))) 
						industryCheck = true;
					else industryCheck = false;

					if(regionCheck && industryCheck){
						this.company[yearIndex][regionIndex][industryIndex] 
								= cdr.getTotalOfStartupCompanyByIndustry(j, k);
						this.employee[yearIndex][regionIndex][industryIndex] 
								= cdr.getTotalOfStartupEmployeeByIndustry(j, k);
					}
					
				}
			}

		}
		
		
		
	}
	
	public static void main(String[] args) {
		
		int[] years = {2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015};
		
		int[] industryClassDepth = {1, 2, 3, 4, 5};		//1:dae, 2:jung, 3:so, 4:se, 5:sese order depth
		int[] regionClassDepth = {2, 5, 7};					//2:si_do,    5: si_gun_gu,   7: eup_myun_dong
		
		int location = 2;		//1:si_do,    2: si_gun_gu,   3: eup_myun_dong
		int industry = 4;		//1:dae, 2:jung, 3:so, 4:se, 5:sese order depth
		
		int regionClass = regionClassDepth[location];
		int industryClass = industryClassDepth[industry];
		
		String filePath ="/Users/Jemyung/Desktop/Research/data_storage/company/";
		String companyFile = filePath + "survival_rate/company_survival_rate.txt";
		String employeeFile = filePath + "survival_rate/employee_survival_rate.txt";
				
		System.out.print("process: initiating...");
		RegionalDataAnalyzer rda = new RegionalDataAnalyzer(years, regionClass, industryClass);
		System.out.println("ok");
		System.out.println("process: complete");

	}

}
