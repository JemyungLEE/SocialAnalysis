package companyDuration.tools;

/**
 *  Subject: Analyze industry class statistics
 *  Developer: Jemyung Lee
 *  Developed Data: 2013. 1. 8
 *  Last Modified Data: 
 *  Department: Seoul Nat. Univ. depart. of Rural Systems Engineering
 *  Description: 
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class IndustryClassAnalyzer {
	HashMap<Integer, Integer> locationCodeMap;
	
	ArrayList<Integer> foundYear;
	ArrayList<Integer> locationCode;
	ArrayList<String> industryCode;
	
	ArrayList<String> industryCodeList;
	ArrayList<Integer> industryCodeCount;
	
	public IndustryClassAnalyzer(){
		this.initiate();
	}
	
	public void initiate(){
		this.locationCodeMap = new HashMap<Integer, Integer>();
		
		this.foundYear = new ArrayList<Integer>();
		this.locationCode = new ArrayList<Integer>();
		this.industryCode = new ArrayList<String>(); 
		this.industryCodeList = new ArrayList<String>(); 
		this.industryCodeCount = new ArrayList<Integer>();
	}
	
	public void readLocationCode(String locationCodeFile){
		
		try{
			File file = new File(locationCodeFile);
			Scanner scan = new Scanner(file);
		
			while(scan.hasNext()) locationCodeMap.put(scan.nextInt(), scan.nextInt());
					
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void readMicroData(String microdataFile){
		
		try{
			File file = new File(microdataFile);
			Scanner scan = new Scanner(file);
		
			scan.nextLine();
			while(scan.hasNext()){
				this.foundYear.add(scan.nextInt());
				this.locationCode.add(scan.nextInt());
				this.industryCode.add(scan.next());
				scan.next();
				scan.next();
				scan.next();
			}
					
			scan.close();	
		} catch(IOException e) {}
		
	}

	public void classifyIndustryCategory(){
		//classify with all company
		
		int i;
		int tmpIndex;
		int tmpFoundYear;
		int tmpLocationCode;
		String tmpIndustryCode;
				
		for(i=0 ; i<this.industryCode.size() ; i++){			
			tmpIndustryCode = this.industryCode.get(i);
			if(this.industryCodeList.contains(tmpIndustryCode)){
				tmpIndex = this.industryCodeList.indexOf(tmpIndustryCode);
				this.industryCodeCount.set(tmpIndex, (this.industryCodeCount.get(tmpIndex)+1));
			}
			else{
				this.industryCodeList.add(tmpIndustryCode);
				this.industryCodeCount.add(1);
			}
		}
	}

	public void classifyIndustryCategory(int year){
		//classify with specific founding year
		
		int i;
		int tmpIndex;
		int tmpFoundYear;
		int tmpLocationCode;
		String tmpIndustryCode;
				
		for(i=0 ; i<this.industryCode.size() ; i++){	
			if(this.foundYear.get(i) == year){			
				tmpIndustryCode = this.industryCode.get(i);
				if(this.industryCodeList.contains(tmpIndustryCode)){
					tmpIndex = this.industryCodeList.indexOf(tmpIndustryCode);
					this.industryCodeCount.set(tmpIndex, (this.industryCodeCount.get(tmpIndex)+1));
				}
				else{
					this.industryCodeList.add(tmpIndustryCode);
					this.industryCodeCount.add(1);
				}			
			}
		}
	}
	
	public void analysisIndustryClass(){
		int i;
		int totalCount = this.industryCode.size();
		double tmpProb;
		double entropy = 0;
		
		for(i=0 ; i<this.industryCodeCount.size() ; i++){
			tmpProb = (double)this.industryCodeCount.get(i)/(double)totalCount;
			entropy += -1.0 * tmpProb * Math.log(tmpProb)/Math.log(2);
		}
		
		System.out.println("industry class entropy: "+entropy);
		System.out.println("sort of industry class: "+this.industryCodeList.size());		
	}
	
	public static void main(String[] args) {
		int year;
		
		for(year = 1998 ; year<=2010 ; year++){
			System.out.println("year: "+year);
			
			String filePath = "/Users/jml/Desktop/Research/temp/";
			String locationCodeFile = filePath+"location_code/"+year+"_location_type.txt";
			String microdataFile = filePath+"extracted/"+year+"_microdataCode.txt";
			
			System.out.println("process: initiating");
			IndustryClassAnalyzer ica = new IndustryClassAnalyzer();
			
			//System.out.println("process: location code reading");
			//ica.readLocationCode(locationCodeFile);			
			
			System.out.print("process: reading company file...");
			ica.readMicroData(microdataFile);
			System.out.println("ok");
			System.out.print("process: classify industry category...");
			ica.classifyIndustryCategory(year);
			System.out.println("ok");
			System.out.print("process: analysis industry category...");
			ica.analysisIndustryClass();
			System.out.println("ok");
			
			System.out.println("process: complete");
		}		
	}

}
