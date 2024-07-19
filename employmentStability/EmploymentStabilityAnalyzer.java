package employmentStability;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import employmentStability.data.CompanyData;

public class EmploymentStabilityAnalyzer {
	int locationCodeDepth;
	int industryCodeDepth;
	int numberOfYears;
	int numberOfRegion;
	int numberOfIndustry;
	int numberOfDuration;
	int numberOfSize;
	int retainedBusinessType;
	
	int[] yearList;
	CompanyData cdata;
	
	int[] durationSector;
	int[] sizeSector;
	ArrayList<String> standardLocationCode;
	ArrayList<String> standardIndustryCode;
	HashMap<String, String> standardLocationMap;
	HashMap<String, String> standardIndustryMap;
	
	double[][] employmentStability;													//[year, 0: average][region, 0: all region]
	double[][] employmentStabilityRural;	
	double[][] employmentStabilityUrban;	
	double[][] employmentStabilityStartup;										//[year, 0: average][region, 0: all region]
	double[][] employmentStabilityStartupRural;	
	double[][] employmentStabilityStartupUrban;	
	
	double[][][] employmentStabilityByIndustry;								//[year][region][industry]
	double[][][] employmentStabilityByIndustryRural;	
	double[][][] employmentStabilityByIndustryUrban;	
	double[][][] employmentStabilityStartupByIndustry;					//[year][region][industry]
	double[][][] employmentStabilityStartupByIndustryRural;	
	double[][][] employmentStabilityStartupByIndustryUrban;	
	
	double[][][] employmentStabilityByDuration;							//[year][region][duration]
	double[][][] employmentStabilityByDurationRural;	
	double[][][] employmentStabilityByDurationUrban;	
	double[][][] employmentStabilityStartupByDuration;				//[year][region][duration]
	double[][][] employmentStabilityStartupByDurationRural;	
	double[][][] employmentStabilityStartupByDurationUrban;	
	
	double[][][] employmentStabilityBySize;									//[year][region][size]
	double[][][] employmentStabilityBySizeRural;	
	double[][][] employmentStabilityBySizeUrban;	
	double[][][] employmentStabilityStartupBySize;						//[year][region][size]
	double[][][] employmentStabilityStartupBySizeRural;	
	double[][][] employmentStabilityStartupBySizeUrban;	
	
	public EmploymentStabilityAnalyzer(){
		this.initate();
	}
	
	public EmploymentStabilityAnalyzer(int[] years, int locationDepth, int industryDepth){
		this.numberOfYears = years.length;
		this.yearList = years;
		this.locationCodeDepth = locationDepth;
		this.industryCodeDepth = industryDepth;
		this.initate();
	}
	
	public void initate(){
		this.standardLocationCode = new ArrayList<String>();
		this.standardIndustryCode = new ArrayList<String>();
		this.standardLocationMap = new HashMap<String, String>();
		this.standardIndustryMap = new HashMap<String, String>();
	}
	
	public void initateVariables(){
		this.employmentStability = new double[this.numberOfYears][this.numberOfRegion+1];
		this.employmentStabilityRural = new double[this.numberOfYears][this.numberOfRegion+1];
		this.employmentStabilityUrban = new double[this.numberOfYears][this.numberOfRegion+1];	
		this.employmentStabilityStartup = new double[this.numberOfYears][this.numberOfRegion+1];
		this.employmentStabilityStartupRural = new double[this.numberOfYears][this.numberOfRegion+1];
		this.employmentStabilityStartupUrban = new double[this.numberOfYears][this.numberOfRegion+1];	
	}
	
	public void initateDetailedVariables(){
		this.employmentStabilityByIndustry = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfIndustry];	
		this.employmentStabilityByIndustryRural = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfIndustry];	
		this.employmentStabilityByIndustryUrban = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfIndustry];	
		this.employmentStabilityStartupByIndustry = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfIndustry];	
		this.employmentStabilityStartupByIndustryRural = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfIndustry];	
		this.employmentStabilityStartupByIndustryUrban = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfIndustry];	

		this.employmentStabilityByDuration = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfDuration];
		this.employmentStabilityByDurationRural = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfDuration];	
		this.employmentStabilityByDurationUrban = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfDuration];	
		this.employmentStabilityStartupByDuration = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfDuration];
		this.employmentStabilityStartupByDurationRural = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfDuration];	
		this.employmentStabilityStartupByDurationUrban = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfDuration];	
		
		this.employmentStabilityBySize = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfSize];
		this.employmentStabilityBySizeRural = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfSize];	
		this.employmentStabilityBySizeUrban = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfSize];
		this.employmentStabilityStartupBySize = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfSize];
		this.employmentStabilityStartupBySizeRural = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfSize];	
		this.employmentStabilityStartupBySizeUrban = new double[this.numberOfYears][this.numberOfRegion+1][this.numberOfSize];
	}
	
	public void setGroupProperty(int[] durations, int[] sizes, int retainedBusinessType){
		this.setBusinessDurationSectors(durations);
		this.setEmployeeSizeSectors(sizes);
		this.setRetainedBusinessType(retainedBusinessType);
	}
	
	public void setRetainedBusinessType(int type){
		this.retainedBusinessType = type;
	}
	
	public void setBusinessDurationSectors(int[] duration){
		this.durationSector = duration;
		this.numberOfDuration = duration.length;
	}
	
	public void setEmployeeSizeSectors(int[] sizes){
		this.sizeSector = sizes;
		this.numberOfSize = sizes.length;
	}
	
	public void readStandardLocationCode(String locationCodeFile){
		String tmpCode, tmpRegion;
		
		try{
			File file = new File(locationCodeFile);
			Scanner scan = new Scanner(file);
		
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpRegion = scan.next();
				if(tmpCode.length() == this.locationCodeDepth) this.standardLocationCode.add(tmpCode);
				this.standardLocationMap.put(tmpCode, tmpRegion);
			}
			this.numberOfRegion = this.standardLocationCode.size();
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void readStandardIndustryCode(String industryCodeFile){
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
				if(tmpCode.length() == codeLength) this.standardIndustryCode.add(tmpCode);
				this.standardIndustryMap.put(tmpCode, tmpIndustry);
			}
			
			if(this.industryCodeDepth == 0) this.numberOfIndustry = 3;
			else	this.numberOfIndustry = this.standardIndustryCode.size();
			
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void analyzeCompanyData(String locationCodeFile, String industryCodeFile, String microdata){
		int i;
		String year;
		String locationFile, industryFile, microdataFile;
		
		CompanyDataReader cdr;
		this.cdata = new CompanyData(this.yearList, this.locationCodeDepth, this.industryCodeDepth);
		 
		for(i=0 ; i<numberOfYears ; i++){
			year =  ""+this.yearList[i];
			System.out.print(year+" analyzing ...");
			locationFile = locationCodeFile.replace("year", ""+year);
			industryFile = industryCodeFile.replace("year", ""+year);
			microdataFile = microdata.replace("year", ""+year);
			cdr = new CompanyDataReader(this.yearList[i], this.locationCodeDepth, 
																	this.industryCodeDepth, this.retainedBusinessType);
			if(this.numberOfDuration > 0 || this.numberOfSize > 0)
				cdr.proceedDataReading(locationFile, industryFile, microdataFile, 
															this.durationSector, this.sizeSector);
			else cdr.proceedDataReading(locationFile, industryFile, microdataFile);
			cdr.proceedAnalysis(this.cdata);
			System.out.println(" done.");
		}
	}
	
	public int getRegionIndex(int year, int index){
		String tmpCode, tmpRegion;
		String standardCode = this.standardLocationCode.get(index);
		String standardName = this.standardLocationMap.get(standardCode);
		ArrayList<String> cdataLocationList =  this.cdata.getLocationCodeList(year);
		HashMap<String, String> cdataLocationMap = this.cdata.getLocationCodeMap(year);
		
		if(this.cdata.getLocationCodeList(year).contains(standardCode)){
			tmpRegion = this.cdata.getLocationCodeMap(year).get(standardCode);
			if(tmpRegion.equals(standardName) ||
					standardName.startsWith(tmpRegion.substring(0, tmpRegion.length()-1)))
				return this.cdata.getLocationCodeList(year).indexOf(standardCode);
		}
		
		if(standardCode.length() == 5) tmpCode = standardCode.substring(0, 2);
		else if(standardCode.length() == 7) tmpCode = standardCode.substring(0, 5);
		else tmpCode = "";
		
		for(int i=0 ; i<this.cdata.getNumberOfRegion(year) ; i++){
			if(!tmpCode.isEmpty() && cdataLocationList.get(i).startsWith(tmpCode)){
				tmpRegion = cdataLocationMap.get(cdataLocationList.get(i));
				if(standardName.equals(tmpRegion) ||
						standardName.startsWith(tmpRegion.substring(0, tmpRegion.length()-1)))
					return i;
			}
		}
				
		return -1;
	}
	
	public void analyzeStability(){
		int i, j;
		int current, previous;
		double yearsNation, yearsUrban, yearsRural;
		double years = (double) this.numberOfYears - 1;
		
		int[] totalNation = new int[this.numberOfYears];
		int[] totalUrban = new int[this.numberOfYears];
		int[] totalRural = new int[this.numberOfYears];
		
		for(i=0 ; i<this.numberOfYears ; i++){
			for(j=0 ; j<this.cdata.getNumberOfRegion(i) ; j++){
				totalNation[i] += this.cdata.getTotalEmployee(i)[j];
				totalUrban[i] += this.cdata.getTotalUrbanEmployee(i)[j];
				totalRural[i] += this.cdata.getTotalRuralEmployee(i)[j];
			}
		}
		for(i=1 ; i<this.numberOfYears ; i++){
			this.employmentStability[i][0] = (double) totalNation[i] / (double) totalNation[i-1];
			this.employmentStabilityUrban[i][0] = (double) totalUrban[i] / (double) totalUrban[i-1];
			this.employmentStabilityRural[i][0] = (double) totalRural[i] / (double) totalRural[i-1];
			
			this.employmentStability[0][0] += this.employmentStability[i][0] / years;
			this.employmentStabilityUrban[0][0] += this.employmentStabilityUrban[i][0] / years;
			this.employmentStabilityRural[0][0] += this.employmentStabilityRural[i][0] / years;
		}
		
		for(j=0 ; j<this.numberOfRegion ; j++){
			yearsNation = (double) this.numberOfYears - 1;
			yearsUrban = (double) this.numberOfYears - 1;
			yearsRural = (double) this.numberOfYears - 1;
			for(i=1 ; i<this.numberOfYears ; i++){
				previous = this.getRegionIndex(i-1, j);
				current = this.getRegionIndex(i, j);
				
				if(previous >= 0 && current >=0){
					if(this.cdata.getTotalEmployee(i)[current] >= 0 
							&& this.cdata.getTotalEmployee(i-1)[previous] > 0)
						this.employmentStability[i][j+1] = (double) this.cdata.getTotalEmployee(i)[current] / 
																				(double) this.cdata.getTotalEmployee(i-1)[previous];
					else{
						this.employmentStability[i][j+1]  = -1.0;
						yearsNation--;
					}
					if(this.cdata.getTotalUrbanEmployee(i)[current] >= 0 
							&& this.cdata.getTotalUrbanEmployee(i-1)[previous] > 0)
						this.employmentStabilityUrban[i][j+1] 
								= (double) this.cdata.getTotalUrbanEmployee(i)[current] / 
									(double) this.cdata.getTotalUrbanEmployee(i-1)[previous];
					else{
						this.employmentStabilityUrban[i][j+1] = -1.0;
						yearsUrban--;
					}
					if(this.cdata.getTotalRuralEmployee(i)[current] >= 0
							&& this.cdata.getTotalRuralEmployee(i-1)[previous] > 0)
						this.employmentStabilityRural[i][j+1]
								= (double) this.cdata.getTotalRuralEmployee(i)[current] / 
									(double) this.cdata.getTotalRuralEmployee(i-1)[previous];
					else{
						this.employmentStabilityRural[i][j+1] = -1.0;
						yearsRural--;
					}
					
					this.employmentStability[0][j+1] += this.employmentStability[i][j+1];
					this.employmentStabilityUrban[0][j+1]  += this.employmentStabilityUrban[i][j+1];
					this.employmentStabilityRural[0][j+1] += this.employmentStabilityRural[i][j+1];
				}else{
					this.employmentStability[i][j+1] = -1.0;
					this.employmentStabilityUrban[i][j+1] = -1.0;
					this.employmentStabilityRural[i][j+1] = -1.0;
					yearsNation--;
					yearsUrban--;
					yearsRural--;
				}	
			}
			this.employmentStability[0][j+1] /= yearsNation;
			this.employmentStabilityUrban[0][j+1] /= yearsUrban;
			this.employmentStabilityRural[0][j+1] /= yearsRural;
		}
	}
	
	public void analyzeStartupStability(){
		int i, j;
		int current, previous;
		double yearsNation, yearsUrban, yearsRural;
		double years = (double) this.numberOfYears - 1;
		
		int[] startupNation = new int[this.numberOfYears];
		int[] startupUrban = new int[this.numberOfYears];
		int[] startupRural = new int[this.numberOfYears];
		
		for(i=0 ; i<this.numberOfYears ; i++){
			for(j=0 ; j<this.cdata.getNumberOfRegion(i) ; j++){
				startupNation[i] += this.cdata.getStartupEmployee(i)[j];
				startupUrban[i] += this.cdata.getStartupUrbanEmployee(i)[j];
				startupRural[i] += this.cdata.getStartupRuralEmployee(i)[j];
			}
		}
		for(i=1 ; i<this.numberOfYears ; i++){
			this.employmentStabilityStartup[i][0] = (double) startupNation[i] / (double) startupNation[i-1];
			this.employmentStabilityStartupUrban[i][0] = (double) startupUrban[i] / (double) startupUrban[i-1];
			this.employmentStabilityStartupRural[i][0] = (double) startupRural[i] / (double) startupRural[i-1];
			
			this.employmentStabilityStartup[0][0] += this.employmentStabilityStartup[i][0] / years;
			this.employmentStabilityStartupUrban[0][0] += this.employmentStabilityStartupUrban[i][0] / years;
			this.employmentStabilityStartupRural[0][0] += this.employmentStabilityStartupRural[i][0] / years;
		}
		
		for(j=0 ; j<this.numberOfRegion ; j++){
			yearsNation = (double) this.numberOfYears - 1;
			yearsUrban = (double) this.numberOfYears - 1;
			yearsRural = (double) this.numberOfYears - 1;
			for(i=1 ; i<this.numberOfYears ; i++){
				previous = this.getRegionIndex(i-1, j);
				current = this.getRegionIndex(i, j);
				
				if(previous >= 0 && current >=0){
					if(this.cdata.getStartupEmployee(i)[current] >= 0 
							&& this.cdata.getStartupEmployee(i-1)[previous] > 0)
						this.employmentStabilityStartup[i][j+1] 
								= (double) this.cdata.getStartupEmployee(i)[current] / 
										(double) this.cdata.getStartupEmployee(i-1)[previous];
					else{
						this.employmentStabilityStartup[i][j+1]  = -1.0;
						yearsNation--;
					}
					if(this.cdata.getStartupUrbanEmployee(i)[current] >= 0 
							&& this.cdata.getStartupUrbanEmployee(i-1)[previous] > 0)
						this.employmentStabilityStartupUrban[i][j+1] 
								= (double) this.cdata.getStartupUrbanEmployee(i)[current] / 
									(double) this.cdata.getStartupUrbanEmployee(i-1)[previous];
					else{
						this.employmentStabilityStartupUrban[i][j+1] = -1.0;
						yearsUrban--;
					}
					if(this.cdata.getStartupRuralEmployee(i)[current] >= 0
							&& this.cdata.getStartupRuralEmployee(i-1)[previous] > 0)
						this.employmentStabilityStartupRural[i][j+1]
								= (double) this.cdata.getStartupRuralEmployee(i)[current] / 
									(double) this.cdata.getStartupRuralEmployee(i-1)[previous];
					else{
						this.employmentStabilityStartupRural[i][j+1] = -1.0;
						yearsRural--;
					}
					
					this.employmentStabilityStartup[0][j+1] += this.employmentStabilityStartup[i][j+1];
					this.employmentStabilityStartupUrban[0][j+1]  += this.employmentStabilityStartupUrban[i][j+1];
					this.employmentStabilityStartupRural[0][j+1] += this.employmentStabilityStartupRural[i][j+1];
				}else{
					this.employmentStabilityStartup[i][j+1] = -1.0;
					this.employmentStabilityStartupUrban[i][j+1] = -1.0;
					this.employmentStabilityStartupRural[i][j+1] = -1.0;
					yearsNation--;
					yearsUrban--;
					yearsRural--;
				}	
			}
			this.employmentStabilityStartup[0][j+1] /= yearsNation;
			this.employmentStabilityStartupUrban[0][j+1] /= yearsUrban;
			this.employmentStabilityStartupRural[0][j+1] /= yearsRural;
		}
	}
	
	public void analyzeStabilityByIndustry(){
		int i, j, k;
		int current, previous;
		double[] yearsNation = new double[this.numberOfIndustry];
		double[] yearsUrban = new double[this.numberOfIndustry];
		double[] yearsRural = new double[this.numberOfIndustry];
		double years = (double) this.numberOfYears - 1;
		
		int[][] totalNation = new int[this.numberOfYears][this.numberOfIndustry];
		int[][] totalUrban = new int[this.numberOfYears][this.numberOfIndustry];
		int[][] totalRural = new int[this.numberOfYears][this.numberOfIndustry];
		
		for(i=0 ; i<this.numberOfYears ; i++){
			for(j=0 ; j<this.cdata.getNumberOfRegion(i) ; j++){
				for(k=0 ; k<this.cdata.getNumberOfIndustry(i) ; k++){
					totalNation[i][k] += this.cdata.getTotalEmployeeByIndustry(i)[j][k];
					totalUrban[i][k] += this.cdata.getTotalUrbanEmployeeByIndustry(i)[j][k];
					totalRural[i][k] += this.cdata.getTotalRuralEmployeeByIndustry(i)[j][k];
				}
			}
		}
		
		for(i=1 ; i<this.numberOfYears ; i++){
			for(k=0 ; k<this.cdata.getNumberOfIndustry(i) ; k++){
				this.employmentStabilityByIndustry[i][0][k] 
						= (double) totalNation[i][k] / (double) totalNation[i-1][k];
				this.employmentStabilityByIndustryUrban[i][0][k] 
						= (double) totalUrban[i][k] / (double) totalUrban[i-1][k];
				this.employmentStabilityByIndustryRural[i][0][k] 
						= (double) totalRural[i][k] / (double) totalRural[i-1][k];
				
				this.employmentStabilityByIndustry[0][0][k] 
						+= this.employmentStabilityByIndustry[i][0][k] / years;
				this.employmentStabilityByIndustryUrban[0][0][k] 
						+= this.employmentStabilityByIndustryUrban[i][0][k] / years;
				this.employmentStabilityByIndustryRural[0][0][k] 
						+= this.employmentStabilityByIndustryRural[i][0][k] / years;
			}
		}
		for(j=0 ; j<this.numberOfRegion ; j++){
			for(k=0 ; k<this.numberOfIndustry ; k++){
				yearsNation[k] = (double) this.numberOfYears - 1;
				yearsUrban[k] = (double) this.numberOfYears - 1;
				yearsRural[k] = (double) this.numberOfYears - 1;
			}
			
			for(i=1 ; i<this.numberOfYears ; i++){
				previous = this.getRegionIndex(i-1, j);
				current = this.getRegionIndex(i, j);
	
				if(previous >= 0 && current >=0){
					for(k=0 ; k<this.cdata.getNumberOfIndustry(i) ; k++){
						if(this.cdata.getTotalEmployeeByIndustry(i)[current][k] >= 0 
								&& this.cdata.getTotalEmployeeByIndustry(i-1)[previous][k] > 0)
							this.employmentStabilityByIndustry[i][j+1][k]
									= (double) this.cdata.getTotalEmployeeByIndustry(i)[current][k] / 
										(double) this.cdata.getTotalEmployeeByIndustry(i-1)[previous][k];
						else{
							this.employmentStabilityByIndustry[i][j+1][k]  = -1.0;
							yearsNation[k]--;
						}
						if(this.cdata.getTotalUrbanEmployeeByIndustry(i)[current][k] >= 0 
								&& this.cdata.getTotalUrbanEmployeeByIndustry(i-1)[previous][k] > 0)
							this.employmentStabilityByIndustryUrban[i][j+1][k] 
									= (double) this.cdata.getTotalUrbanEmployeeByIndustry(i)[current][k] / 
										(double) this.cdata.getTotalUrbanEmployeeByIndustry(i-1)[previous][k];
						else{
							this.employmentStabilityByIndustryUrban[i][j+1][k] = -1.0;
							yearsUrban[k]--;
						}
						if(this.cdata.getTotalRuralEmployeeByIndustry(i)[current][k] >= 0
								&& this.cdata.getTotalRuralEmployeeByIndustry(i-1)[previous][k] > 0)
							this.employmentStabilityByIndustryRural[i][j+1][k]
									= (double) this.cdata.getTotalRuralEmployeeByIndustry(i)[current][k] / 
										(double) this.cdata.getTotalRuralEmployeeByIndustry(i-1)[previous][k];
						else{
							this.employmentStabilityByIndustryRural[i][j+1][k] = -1.0;
							yearsRural[k]--;
						}
						
						this.employmentStabilityByIndustry[0][j+1][k] 
								+= this.employmentStabilityByIndustry[i][j+1][k];
						this.employmentStabilityByIndustryUrban[0][j+1][k]  
								+= this.employmentStabilityByIndustryUrban[i][j+1][k];
						this.employmentStabilityByIndustryRural[0][j+1][k] 
								+= this.employmentStabilityByIndustryRural[i][j+1][k];
					}
				}else{
					for(k=0 ; k<this.cdata.getNumberOfIndustry(i) ; k++){
						this.employmentStabilityByIndustry[i][j+1][k] = -1.0;
						this.employmentStabilityByIndustryUrban[i][j+1][k] = -1.0;
						this.employmentStabilityByIndustryRural[i][j+1][k] = -1.0;
						yearsNation[k]--;
						yearsUrban[k]--;
						yearsRural[k]--;
					}
				}	
			}
			for(k=0 ; k<this.numberOfIndustry ; k++){
				this.employmentStabilityByIndustry[0][j+1][k] /= yearsNation[k];
				this.employmentStabilityByIndustryUrban[0][j+1][k] /= yearsUrban[k];
				this.employmentStabilityByIndustryRural[0][j+1][k] /= yearsRural[k];
			}
		}
	}
	
	public void analyzeStartupStabilityByIndustry(){
		int i, j, k;
		int current, previous;
		double[] yearsNation = new double[this.numberOfIndustry];
		double[] yearsUrban = new double[this.numberOfIndustry];
		double[] yearsRural = new double[this.numberOfIndustry];
		double years = (double) this.numberOfYears - 1;
		
		int[][] startupNation = new int[this.numberOfYears][this.numberOfIndustry];
		int[][] startupUrban = new int[this.numberOfYears][this.numberOfIndustry];
		int[][] startupRural = new int[this.numberOfYears][this.numberOfIndustry];
		
		for(i=0 ; i<this.numberOfYears ; i++){
			for(j=0 ; j<this.cdata.getNumberOfRegion(i) ; j++){
				for(k=0 ; k<this.cdata.getNumberOfIndustry(i) ; k++){
					startupNation[i][k] += this.cdata.getStartupEmployeeByIndustry(i)[j][k];
					startupUrban[i][k] += this.cdata.getStartupUrbanEmployeeByIndustry(i)[j][k];
					startupRural[i][k] += this.cdata.getStartupRuralEmployeeByIndustry(i)[j][k];
				}
			}
		}
		
		for(i=1 ; i<this.numberOfYears ; i++){
			for(k=0 ; k<this.cdata.getNumberOfIndustry(i) ; k++){
				this.employmentStabilityStartupByIndustry[i][0][k] 
						= (double) startupNation[i][k] / (double) startupNation[i-1][k];
				this.employmentStabilityStartupByIndustryUrban[i][0][k] 
						= (double) startupUrban[i][k] / (double) startupUrban[i-1][k];
				this.employmentStabilityStartupByIndustryRural[i][0][k] 
						= (double) startupRural[i][k] / (double) startupRural[i-1][k];
				
				this.employmentStabilityStartupByIndustry[0][0][k] 
						+= this.employmentStabilityStartupByIndustry[i][0][k] / years;
				this.employmentStabilityStartupByIndustryUrban[0][0][k] 
						+= this.employmentStabilityStartupByIndustryUrban[i][0][k] / years;
				this.employmentStabilityStartupByIndustryRural[0][0][k] 
						+= this.employmentStabilityStartupByIndustryRural[i][0][k] / years;
			}
		}
		for(j=0 ; j<this.numberOfRegion ; j++){
			for(k=0 ; k<this.numberOfIndustry ; k++){
				yearsNation[k] = (double) this.numberOfYears - 1;
				yearsUrban[k] = (double) this.numberOfYears - 1;
				yearsRural[k] = (double) this.numberOfYears - 1;
			}
			
			for(i=1 ; i<this.numberOfYears ; i++){
				previous = this.getRegionIndex(i-1, j);
				current = this.getRegionIndex(i, j);
	
				if(previous >= 0 && current >=0){
					for(k=0 ; k<this.cdata.getNumberOfIndustry(i) ; k++){
						if(this.cdata.getStartupEmployeeByIndustry(i)[current][k] >= 0 
								&& this.cdata.getStartupEmployeeByIndustry(i-1)[previous][k] > 0)
							this.employmentStabilityStartupByIndustry[i][j+1][k]
									= (double) this.cdata.getStartupEmployeeByIndustry(i)[current][k] / 
										(double) this.cdata.getStartupEmployeeByIndustry(i-1)[previous][k];
						else{
							this.employmentStabilityStartupByIndustry[i][j+1][k]  = -1.0;
							yearsNation[k]--;
						}
						if(this.cdata.getStartupUrbanEmployeeByIndustry(i)[current][k] >= 0 
								&& this.cdata.getStartupUrbanEmployeeByIndustry(i-1)[previous][k] > 0)
							this.employmentStabilityStartupByIndustryUrban[i][j+1][k] 
									= (double) this.cdata.getStartupUrbanEmployeeByIndustry(i)[current][k] / 
										(double) this.cdata.getStartupUrbanEmployeeByIndustry(i-1)[previous][k];
						else{
							this.employmentStabilityStartupByIndustryUrban[i][j+1][k] = -1.0;
							yearsUrban[k]--;
						}
						if(this.cdata.getStartupRuralEmployeeByIndustry(i)[current][k] >= 0
								&& this.cdata.getStartupRuralEmployeeByIndustry(i-1)[previous][k] > 0)
							this.employmentStabilityStartupByIndustryRural[i][j+1][k]
									= (double) this.cdata.getStartupRuralEmployeeByIndustry(i)[current][k] / 
										(double) this.cdata.getStartupRuralEmployeeByIndustry(i-1)[previous][k];
						else{
							this.employmentStabilityStartupByIndustryRural[i][j+1][k] = -1.0;
							yearsRural[k]--;
						}
						
						this.employmentStabilityStartupByIndustry[0][j+1][k] 
								+= this.employmentStabilityStartupByIndustry[i][j+1][k];
						this.employmentStabilityStartupByIndustryUrban[0][j+1][k]  
								+= this.employmentStabilityStartupByIndustryUrban[i][j+1][k];
						this.employmentStabilityStartupByIndustryRural[0][j+1][k] 
								+= this.employmentStabilityStartupByIndustryRural[i][j+1][k];
					}
				}else{
					for(k=0 ; k<this.cdata.getNumberOfIndustry(i) ; k++){
						this.employmentStabilityStartupByIndustry[i][j+1][k] = -1.0;
						this.employmentStabilityStartupByIndustryUrban[i][j+1][k] = -1.0;
						this.employmentStabilityStartupByIndustryRural[i][j+1][k] = -1.0;
						yearsNation[k]--;
						yearsUrban[k]--;
						yearsRural[k]--;
					}
				}	
			}
			for(k=0 ; k<this.numberOfIndustry ; k++){
				this.employmentStabilityStartupByIndustry[0][j+1][k] /= yearsNation[k];
				this.employmentStabilityStartupByIndustryUrban[0][j+1][k] /= yearsUrban[k];
				this.employmentStabilityStartupByIndustryRural[0][j+1][k] /= yearsRural[k];
			}
		}
	}
	
	public void analyzeStabilityByDuration(){
		int i, j, k;
		int current, previous;
		double[] yearsNation = new double[this.numberOfDuration];
		double[] yearsUrban = new double[this.numberOfDuration];
		double[] yearsRural = new double[this.numberOfDuration];
		double years = (double) this.numberOfYears - 1;
		
		int[][] totalNation = new int[this.numberOfYears][this.numberOfDuration];
		int[][] totalUrban = new int[this.numberOfYears][this.numberOfDuration];
		int[][] totalRural = new int[this.numberOfYears][this.numberOfDuration];
		
		for(i=0 ; i<this.numberOfYears ; i++){
			for(j=0 ; j<this.cdata.getNumberOfRegion(i) ; j++){
				for(k=0 ; k<this.cdata.getNumberOfDuration(i) ; k++){
					totalNation[i][k] += this.cdata.getTotalEmployeeByDuration(i)[j][k];
					totalUrban[i][k] += this.cdata.getTotalUrbanEmployeeByDuration(i)[j][k];
					totalRural[i][k] += this.cdata.getTotalRuralEmployeeByDuration(i)[j][k];
				}
			}
		}
		
		for(i=1 ; i<this.numberOfYears ; i++){
			for(k=0 ; k<this.cdata.getNumberOfDuration(i) ; k++){
				this.employmentStabilityByDuration[i][0][k] 
						= (double) totalNation[i][k] / (double) totalNation[i-1][k];
				this.employmentStabilityByDurationUrban[i][0][k] 
						= (double) totalUrban[i][k] / (double) totalUrban[i-1][k];
				this.employmentStabilityByDurationRural[i][0][k] 
						= (double) totalRural[i][k] / (double) totalRural[i-1][k];
				
				this.employmentStabilityByDuration[0][0][k] 
						+= this.employmentStabilityByDuration[i][0][k] / years;
				this.employmentStabilityByDurationUrban[0][0][k] 
						+= this.employmentStabilityByDurationUrban[i][0][k] / years;
				this.employmentStabilityByDurationRural[0][0][k] 
						+= this.employmentStabilityByDurationRural[i][0][k] / years;
			}
		}
		for(j=0 ; j<this.numberOfRegion ; j++){
			for(k=0 ; k<this.numberOfDuration ; k++){
				yearsNation[k] = (double) this.numberOfYears - 1;
				yearsUrban[k] = (double) this.numberOfYears - 1;
				yearsRural[k] = (double) this.numberOfYears - 1;
			}
			
			for(i=1 ; i<this.numberOfYears ; i++){
				previous = this.getRegionIndex(i-1, j);
				current = this.getRegionIndex(i, j);
	
				if(previous >= 0 && current >=0){
					for(k=0 ; k<this.cdata.getNumberOfDuration(i) ; k++){
						if(this.cdata.getTotalEmployeeByDuration(i)[current][k] >= 0 
								&& this.cdata.getTotalEmployeeByDuration(i-1)[previous][k] > 0)
							this.employmentStabilityByDuration[i][j+1][k]
									= (double) this.cdata.getTotalEmployeeByDuration(i)[current][k] / 
										(double) this.cdata.getTotalEmployeeByDuration(i-1)[previous][k];
						else{
							this.employmentStabilityByDuration[i][j+1][k]  = -1.0;
							yearsNation[k]--;
						}
						if(this.cdata.getTotalUrbanEmployeeByDuration(i)[current][k] >= 0 
								&& this.cdata.getTotalUrbanEmployeeByDuration(i-1)[previous][k] > 0)
							this.employmentStabilityByDurationUrban[i][j+1][k] 
									= (double) this.cdata.getTotalUrbanEmployeeByDuration(i)[current][k] / 
										(double) this.cdata.getTotalUrbanEmployeeByDuration(i-1)[previous][k];
						else{
							this.employmentStabilityByDurationUrban[i][j+1][k] = -1.0;
							yearsUrban[k]--;
						}
						if(this.cdata.getTotalRuralEmployeeByDuration(i)[current][k] >= 0
								&& this.cdata.getTotalRuralEmployeeByDuration(i-1)[previous][k] > 0)
							this.employmentStabilityByDurationRural[i][j+1][k]
									= (double) this.cdata.getTotalRuralEmployeeByDuration(i)[current][k] / 
										(double) this.cdata.getTotalRuralEmployeeByDuration(i-1)[previous][k];
						else{
							this.employmentStabilityByDurationRural[i][j+1][k] = -1.0;
							yearsRural[k]--;
						}
						
						this.employmentStabilityByDuration[0][j+1][k] 
								+= this.employmentStabilityByDuration[i][j+1][k];
						this.employmentStabilityByDurationUrban[0][j+1][k]  
								+= this.employmentStabilityByDurationUrban[i][j+1][k];
						this.employmentStabilityByDurationRural[0][j+1][k] 
								+= this.employmentStabilityByDurationRural[i][j+1][k];
					}
				}else{
					for(k=0 ; k<this.cdata.getNumberOfDuration(i) ; k++){
						this.employmentStabilityByDuration[i][j+1][k] = -1.0;
						this.employmentStabilityByDurationUrban[i][j+1][k] = -1.0;
						this.employmentStabilityByDurationRural[i][j+1][k] = -1.0;
						yearsNation[k]--;
						yearsUrban[k]--;
						yearsRural[k]--;
					}
				}	
			}
			for(k=0 ; k<this.numberOfDuration ; k++){
				this.employmentStabilityByDuration[0][j+1][k] /= yearsNation[k];
				this.employmentStabilityByDurationUrban[0][j+1][k] /= yearsUrban[k];
				this.employmentStabilityByDurationRural[0][j+1][k] /= yearsRural[k];
			}
		}
		

	}
	
	public void analyzeStartupStabilityByDuration(){
		int i, j, k;
		int current, previous;
		double[] yearsNation = new double[this.numberOfDuration];
		double[] yearsUrban = new double[this.numberOfDuration];
		double[] yearsRural = new double[this.numberOfDuration];
		double years = (double) this.numberOfYears - 1;
		
		int[][] startupNation = new int[this.numberOfYears][this.numberOfDuration];
		int[][] startupUrban = new int[this.numberOfYears][this.numberOfDuration];
		int[][] startupRural = new int[this.numberOfYears][this.numberOfDuration];
		
		for(i=0 ; i<this.numberOfYears ; i++){
			for(j=0 ; j<this.cdata.getNumberOfRegion(i) ; j++){
				for(k=0 ; k<this.cdata.getNumberOfDuration(i) ; k++){
					startupNation[i][k] += this.cdata.getStartupEmployeeByDuration(i)[j][k];
					startupUrban[i][k] += this.cdata.getStartupUrbanEmployeeByDuration(i)[j][k];
					startupRural[i][k] += this.cdata.getStartupRuralEmployeeByDuration(i)[j][k];
				}
			}
		}
		
		for(i=1 ; i<this.numberOfYears ; i++){
			for(k=0 ; k<this.cdata.getNumberOfDuration(i) ; k++){
				this.employmentStabilityStartupByDuration[i][0][k] 
						= (double) startupNation[i][k] / (double) startupNation[i-1][k];
				this.employmentStabilityStartupByDurationUrban[i][0][k] 
						= (double) startupUrban[i][k] / (double) startupUrban[i-1][k];
				this.employmentStabilityStartupByDurationRural[i][0][k] 
						= (double) startupRural[i][k] / (double) startupRural[i-1][k];
				
				this.employmentStabilityStartupByDuration[0][0][k] 
						+= this.employmentStabilityStartupByDuration[i][0][k] / years;
				this.employmentStabilityStartupByDurationUrban[0][0][k] 
						+= this.employmentStabilityStartupByDurationUrban[i][0][k] / years;
				this.employmentStabilityStartupByDurationRural[0][0][k] 
						+= this.employmentStabilityStartupByDurationRural[i][0][k] / years;
			}
		}
		for(j=0 ; j<this.numberOfRegion ; j++){
			for(k=0 ; k<this.numberOfDuration ; k++){
				yearsNation[k] = (double) this.numberOfYears - 1;
				yearsUrban[k] = (double) this.numberOfYears - 1;
				yearsRural[k] = (double) this.numberOfYears - 1;
			}
			
			for(i=1 ; i<this.numberOfYears ; i++){
				previous = this.getRegionIndex(i-1, j);
				current = this.getRegionIndex(i, j);
	
				if(previous >= 0 && current >=0){
					for(k=0 ; k<this.cdata.getNumberOfDuration(i) ; k++){
						if(this.cdata.getStartupEmployeeByDuration(i)[current][k] >= 0 
								&& this.cdata.getStartupEmployeeByDuration(i-1)[previous][k] > 0)
							this.employmentStabilityStartupByDuration[i][j+1][k]
									= (double) this.cdata.getStartupEmployeeByDuration(i)[current][k] / 
										(double) this.cdata.getStartupEmployeeByDuration(i-1)[previous][k];
						else{
							this.employmentStabilityStartupByDuration[i][j+1][k]  = -1.0;
							yearsNation[k]--;
						}
						if(this.cdata.getStartupUrbanEmployeeByDuration(i)[current][k] >= 0 
								&& this.cdata.getStartupUrbanEmployeeByDuration(i-1)[previous][k] > 0)
							this.employmentStabilityStartupByDurationUrban[i][j+1][k] 
									= (double) this.cdata.getStartupUrbanEmployeeByDuration(i)[current][k] / 
										(double) this.cdata.getStartupUrbanEmployeeByDuration(i-1)[previous][k];
						else{
							this.employmentStabilityStartupByDurationUrban[i][j+1][k] = -1.0;
							yearsUrban[k]--;
						}
						if(this.cdata.getStartupRuralEmployeeByDuration(i)[current][k] >= 0
								&& this.cdata.getStartupRuralEmployeeByDuration(i-1)[previous][k] > 0)
							this.employmentStabilityStartupByDurationRural[i][j+1][k]
									= (double) this.cdata.getStartupRuralEmployeeByDuration(i)[current][k] / 
										(double) this.cdata.getStartupRuralEmployeeByDuration(i-1)[previous][k];
						else{
							this.employmentStabilityStartupByDurationRural[i][j+1][k] = -1.0;
							yearsRural[k]--;
						}
						
						this.employmentStabilityStartupByDuration[0][j+1][k] 
								+= this.employmentStabilityStartupByDuration[i][j+1][k];
						this.employmentStabilityStartupByDurationUrban[0][j+1][k]  
								+= this.employmentStabilityStartupByDurationUrban[i][j+1][k];
						this.employmentStabilityStartupByDurationRural[0][j+1][k] 
								+= this.employmentStabilityStartupByDurationRural[i][j+1][k];
					}
				}else{
					for(k=0 ; k<this.cdata.getNumberOfDuration(i) ; k++){
						this.employmentStabilityStartupByDuration[i][j+1][k] = -1.0;
						this.employmentStabilityStartupByDurationUrban[i][j+1][k] = -1.0;
						this.employmentStabilityStartupByDurationRural[i][j+1][k] = -1.0;
						yearsNation[k]--;
						yearsUrban[k]--;
						yearsRural[k]--;
					}
				}	
			}
			for(k=0 ; k<this.numberOfDuration ; k++){
				this.employmentStabilityStartupByDuration[0][j+1][k] /= yearsNation[k];
				this.employmentStabilityStartupByDurationUrban[0][j+1][k] /= yearsUrban[k];
				this.employmentStabilityStartupByDurationRural[0][j+1][k] /= yearsRural[k];
			}
		}
	}
	
	public void analyzeStabilityBySize(){
		int i, j, k;
		int current, previous;
		double[] yearsNation = new double[this.numberOfSize];
		double[] yearsUrban = new double[this.numberOfSize];
		double[] yearsRural = new double[this.numberOfSize];
		double years = (double) this.numberOfYears - 1;
		
		int[][] totalNation = new int[this.numberOfYears][this.numberOfSize];
		int[][] totalUrban = new int[this.numberOfYears][this.numberOfSize];
		int[][] totalRural = new int[this.numberOfYears][this.numberOfSize];
		
		for(i=0 ; i<this.numberOfYears ; i++){
			for(j=0 ; j<this.cdata.getNumberOfRegion(i) ; j++){
				for(k=0 ; k<this.cdata.getNumberOfSize(i) ; k++){
					totalNation[i][k] += this.cdata.getTotalEmployeeBySize(i)[j][k];
					totalUrban[i][k] += this.cdata.getTotalUrbanEmployeeBySize(i)[j][k];
					totalRural[i][k] += this.cdata.getTotalRuralEmployeeBySize(i)[j][k];
				}
			}
		}
		
		for(i=1 ; i<this.numberOfYears ; i++){
			for(k=0 ; k<this.cdata.getNumberOfSize(i) ; k++){
				this.employmentStabilityBySize[i][0][k] 
						= (double) totalNation[i][k] / (double) totalNation[i-1][k];
				this.employmentStabilityBySizeUrban[i][0][k] 
						= (double) totalUrban[i][k] / (double) totalUrban[i-1][k];
				this.employmentStabilityBySizeRural[i][0][k] 
						= (double) totalRural[i][k] / (double) totalRural[i-1][k];
				
				this.employmentStabilityBySize[0][0][k] 
						+= this.employmentStabilityBySize[i][0][k] / years;
				this.employmentStabilityBySizeUrban[0][0][k] 
						+= this.employmentStabilityBySizeUrban[i][0][k] / years;
				this.employmentStabilityBySizeRural[0][0][k] 
						+= this.employmentStabilityBySizeRural[i][0][k] / years;
			}
		}
		for(j=0 ; j<this.numberOfRegion ; j++){
			for(k=0 ; k<this.numberOfSize ; k++){
				yearsNation[k] = (double) this.numberOfYears - 1;
				yearsUrban[k] = (double) this.numberOfYears - 1;
				yearsRural[k] = (double) this.numberOfYears - 1;
			}
			
			for(i=1 ; i<this.numberOfYears ; i++){
				previous = this.getRegionIndex(i-1, j);
				current = this.getRegionIndex(i, j);
	
				if(previous >= 0 && current >=0){
					for(k=0 ; k<this.cdata.getNumberOfSize(i) ; k++){
						if(this.cdata.getTotalEmployeeBySize(i)[current][k] >= 0 
								&& this.cdata.getTotalEmployeeBySize(i-1)[previous][k] > 0)
							this.employmentStabilityBySize[i][j+1][k]
									= (double) this.cdata.getTotalEmployeeBySize(i)[current][k] / 
										(double) this.cdata.getTotalEmployeeBySize(i-1)[previous][k];
						else{
							this.employmentStabilityBySize[i][j+1][k]  = -1.0;
							yearsNation[k]--;
						}
						if(this.cdata.getTotalUrbanEmployeeBySize(i)[current][k] >= 0 
								&& this.cdata.getTotalUrbanEmployeeBySize(i-1)[previous][k] > 0)
							this.employmentStabilityBySizeUrban[i][j+1][k] 
									= (double) this.cdata.getTotalUrbanEmployeeBySize(i)[current][k] / 
										(double) this.cdata.getTotalUrbanEmployeeBySize(i-1)[previous][k];
						else{
							this.employmentStabilityBySizeUrban[i][j+1][k] = -1.0;
							yearsUrban[k]--;
						}
						if(this.cdata.getTotalRuralEmployeeBySize(i)[current][k] >= 0
								&& this.cdata.getTotalRuralEmployeeBySize(i-1)[previous][k] > 0)
							this.employmentStabilityBySizeRural[i][j+1][k]
									= (double) this.cdata.getTotalRuralEmployeeBySize(i)[current][k] / 
										(double) this.cdata.getTotalRuralEmployeeBySize(i-1)[previous][k];
						else{
							this.employmentStabilityBySizeRural[i][j+1][k] = -1.0;
							yearsRural[k]--;
						}
						
						this.employmentStabilityBySize[0][j+1][k] 
								+= this.employmentStabilityBySize[i][j+1][k];
						this.employmentStabilityBySizeUrban[0][j+1][k]  
								+= this.employmentStabilityBySizeUrban[i][j+1][k];
						this.employmentStabilityBySizeRural[0][j+1][k] 
								+= this.employmentStabilityBySizeRural[i][j+1][k];
					}
				}else{
					for(k=0 ; k<this.cdata.getNumberOfSize(i) ; k++){
						this.employmentStabilityBySize[i][j+1][k] = -1.0;
						this.employmentStabilityBySizeUrban[i][j+1][k] = -1.0;
						this.employmentStabilityBySizeRural[i][j+1][k] = -1.0;
						yearsNation[k]--;
						yearsUrban[k]--;
						yearsRural[k]--;
					}
				}	
			}
			for(k=0 ; k<this.numberOfSize ; k++){
				this.employmentStabilityBySize[0][j+1][k] /= yearsNation[k];
				this.employmentStabilityBySizeUrban[0][j+1][k] /= yearsUrban[k];
				this.employmentStabilityBySizeRural[0][j+1][k] /= yearsRural[k];
			}
		}
	}
	
	public void analyzeStartupStabilityBySize(){
		int i, j, k;
		int current, previous;
		double[] yearsNation = new double[this.numberOfSize];
		double[] yearsUrban = new double[this.numberOfSize];
		double[] yearsRural = new double[this.numberOfSize];
		double years = (double) this.numberOfYears - 1;
		
		int[][] startupNation = new int[this.numberOfYears][this.numberOfSize];
		int[][] startupUrban = new int[this.numberOfYears][this.numberOfSize];
		int[][] startupRural = new int[this.numberOfYears][this.numberOfSize];
		
		for(i=0 ; i<this.numberOfYears ; i++){
			for(j=0 ; j<this.cdata.getNumberOfRegion(i) ; j++){
				for(k=0 ; k<this.cdata.getNumberOfSize(i) ; k++){
					startupNation[i][k] += this.cdata.getStartupEmployeeBySize(i)[j][k];
					startupUrban[i][k] += this.cdata.getStartupUrbanEmployeeBySize(i)[j][k];
					startupRural[i][k] += this.cdata.getStartupRuralEmployeeBySize(i)[j][k];
				}
			}
		}
		
		for(i=1 ; i<this.numberOfYears ; i++){
			for(k=0 ; k<this.cdata.getNumberOfSize(i) ; k++){
				this.employmentStabilityStartupBySize[i][0][k] 
						= (double) startupNation[i][k] / (double) startupNation[i-1][k];
				this.employmentStabilityStartupBySizeUrban[i][0][k] 
						= (double) startupUrban[i][k] / (double) startupUrban[i-1][k];
				this.employmentStabilityStartupBySizeRural[i][0][k] 
						= (double) startupRural[i][k] / (double) startupRural[i-1][k];
				
				this.employmentStabilityStartupBySize[0][0][k] 
						+= this.employmentStabilityStartupBySize[i][0][k] / years;
				this.employmentStabilityStartupBySizeUrban[0][0][k] 
						+= this.employmentStabilityStartupBySizeUrban[i][0][k] / years;
				this.employmentStabilityStartupBySizeRural[0][0][k] 
						+= this.employmentStabilityStartupBySizeRural[i][0][k] / years;
			}
		}
		for(j=0 ; j<this.numberOfRegion ; j++){
			for(k=0 ; k<this.numberOfSize ; k++){
				yearsNation[k] = (double) this.numberOfYears - 1;
				yearsUrban[k] = (double) this.numberOfYears - 1;
				yearsRural[k] = (double) this.numberOfYears - 1;
			}
			
			for(i=1 ; i<this.numberOfYears ; i++){
				previous = this.getRegionIndex(i-1, j);
				current = this.getRegionIndex(i, j);
	
				if(previous >= 0 && current >=0){
					for(k=0 ; k<this.cdata.getNumberOfSize(i) ; k++){
						if(this.cdata.getStartupEmployeeBySize(i)[current][k] >= 0 
								&& this.cdata.getStartupEmployeeBySize(i-1)[previous][k] > 0)
							this.employmentStabilityStartupBySize[i][j+1][k]
									= (double) this.cdata.getStartupEmployeeBySize(i)[current][k] / 
										(double) this.cdata.getStartupEmployeeBySize(i-1)[previous][k];
						else{
							this.employmentStabilityStartupBySize[i][j+1][k]  = -1.0;
							yearsNation[k]--;
						}
						if(this.cdata.getStartupUrbanEmployeeBySize(i)[current][k] >= 0 
								&& this.cdata.getStartupUrbanEmployeeBySize(i-1)[previous][k] > 0)
							this.employmentStabilityStartupBySizeUrban[i][j+1][k] 
									= (double) this.cdata.getStartupUrbanEmployeeBySize(i)[current][k] / 
										(double) this.cdata.getStartupUrbanEmployeeBySize(i-1)[previous][k];
						else{
							this.employmentStabilityStartupBySizeUrban[i][j+1][k] = -1.0;
							yearsUrban[k]--;
						}
						if(this.cdata.getStartupRuralEmployeeBySize(i)[current][k] >= 0
								&& this.cdata.getStartupRuralEmployeeBySize(i-1)[previous][k] > 0)
							this.employmentStabilityStartupBySizeRural[i][j+1][k]
									= (double) this.cdata.getStartupRuralEmployeeBySize(i)[current][k] / 
										(double) this.cdata.getStartupRuralEmployeeBySize(i-1)[previous][k];
						else{
							this.employmentStabilityStartupBySizeRural[i][j+1][k] = -1.0;
							yearsRural[k]--;
						}
						
						this.employmentStabilityStartupBySize[0][j+1][k] 
								+= this.employmentStabilityStartupBySize[i][j+1][k];
						this.employmentStabilityStartupBySizeUrban[0][j+1][k]  
								+= this.employmentStabilityStartupBySizeUrban[i][j+1][k];
						this.employmentStabilityStartupBySizeRural[0][j+1][k] 
								+= this.employmentStabilityStartupBySizeRural[i][j+1][k];
					}
				}else{
					for(k=0 ; k<this.cdata.getNumberOfSize(i) ; k++){
						this.employmentStabilityStartupBySize[i][j+1][k] = -1.0;
						this.employmentStabilityStartupBySizeUrban[i][j+1][k] = -1.0;
						this.employmentStabilityStartupBySizeRural[i][j+1][k] = -1.0;
						yearsNation[k]--;
						yearsUrban[k]--;
						yearsRural[k]--;
					}
				}	
			}
			for(k=0 ; k<this.numberOfSize ; k++){
				this.employmentStabilityStartupBySize[0][j+1][k] /= yearsNation[k];
				this.employmentStabilityStartupBySizeUrban[0][j+1][k] /= yearsUrban[k];
				this.employmentStabilityStartupBySizeRural[0][j+1][k] /= yearsRural[k];
			}
		}
	}
	
	public void printCompanyStatistics(){
		int i, j;
		int years = this.cdata.getNumberOfYears();
		System.out.println("Number of years: "+years);
		for(i=0 ; i<years ; i++) System.out.print("\t"+this.cdata.getYearList(i));
		System.out.println();
		System.out.print("Region\t");
		for(i=0 ; i<years ; i++) System.out.print(this.cdata.getNumberOfRegion(i)+"\t");
		System.out.println();
		System.out.print("Industry\t");
		for(i=0 ; i<years ; i++) System.out.print(this.cdata.getNumberOfIndustry(i)+"\t");
		System.out.println();
		System.out.print("Company\t");
		for(i=0 ; i<years ; i++) System.out.print(this.cdata.getNumberOfCompany(i)+"\t");
		System.out.println();
		System.out.print("Startup Company\t");
		for(i=0 ; i<years ; i++) System.out.print(this.cdata.getSumOfStartupCompany(i)+"\t");
		System.out.println();
		System.out.print("Employee\t");
		for(i=0 ; i<years ; i++) System.out.print(this.cdata.getTotalEmployees(i)+"\t");
		System.out.println();
		System.out.print("Startup Employee\t");
		for(i=0 ; i<years ; i++) System.out.print(this.cdata.getSumOfStartupEmployee(i)+"\t");
		System.out.println();
		System.out.println();
	}
	
	public void printStability(String[] industryTitle, String[] durationTitle, String[] sizeTitle){
		int i, j, k;
		
		System.out.println("\tNation\tUrban\tRural");
		for(i=0 ; i<this.numberOfYears ; i++){
			if(i==0) System.out.print("Average\t");
			else System.out.print((this.cdata.getYearList(i-1)+1)+"\t");
			System.out.println(this.employmentStability[i][0]+"\t"+this.employmentStabilityUrban[i][0]+"\t"+this.employmentStabilityRural[i][0]);
		}	
		System.out.println();
		
		for(k=0 ; k<this.numberOfIndustry ; k++) System.out.print("\t\tNation\tUrban\tRural");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			if(i==0) System.out.print("Average");
			else System.out.print((this.cdata.getYearList(i-1)+1));
			for(k=0 ; k<this.cdata.getNumberOfIndustry(i) ; k++){
				System.out.print("\t"+industryTitle[k]+"\t");
				System.out.print(this.employmentStabilityByIndustry[i][0][k]
						+"\t"+this.employmentStabilityByIndustryUrban[i][0][k]
								+"\t"+this.employmentStabilityByIndustryRural[i][0][k]);
			}
			System.out.println();
		}
		System.out.println();
		
		for(k=0 ; k<this.numberOfDuration ; k++) System.out.print("\t\tNation\tUrban\tRural");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			if(i==0) System.out.print("Average");
			else System.out.print((this.cdata.getYearList(i-1)+1));
			for(k=0 ; k<this.cdata.getNumberOfDuration(i) ; k++){
				System.out.print("\t"+durationTitle[k]+"\t");
				System.out.print(this.employmentStabilityByDuration[i][0][k]
						+"\t"+this.employmentStabilityByDurationUrban[i][0][k]
								+"\t"+this.employmentStabilityByDurationRural[i][0][k]);
			}
			System.out.println();
		}
		System.out.println();
		
		for(k=0 ; k<this.numberOfSize ; k++) System.out.print("\t\tNation\tUrban\tRural");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			if(i==0) System.out.print("Average");
			else System.out.print((this.cdata.getYearList(i-1)+1));
			for(k=0 ; k<this.cdata.getNumberOfSize(i) ; k++){
				System.out.print("\t"+sizeTitle[k]+"\t");
				System.out.print(this.employmentStabilityBySize[i][0][k]
						+"\t"+this.employmentStabilityBySizeUrban[i][0][k]
								+"\t"+this.employmentStabilityBySizeRural[i][0][k]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void printStartupStability(String[] industryTitle, String[] durationTitle, String[] sizeTitle){
		int i, j, k;
		
		System.out.println("\tNation\tUrban\tRural");
		for(i=0 ; i<this.numberOfYears ; i++){
			if(i==0) System.out.print("Average\t");
			else System.out.print(this.cdata.getYearList(i-1)+1+"\t");
			System.out.println(this.employmentStabilityStartup[i][0]+"\t"+this.employmentStabilityStartupUrban[i][0]+"\t"+this.employmentStabilityStartupRural[i][0]);
		}
		System.out.println();
		
		for(k=0 ; k<this.numberOfIndustry ; k++) System.out.print("\t\tNation\tUrban\tRural");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			if(i==0) System.out.print("Average");
			else System.out.print((this.cdata.getYearList(i-1)+1));
			for(k=0 ; k<this.cdata.getNumberOfIndustry(i) ; k++){
				System.out.print("\t"+industryTitle[k]+"\t");
				System.out.print(this.employmentStabilityStartupByIndustry[i][0][k]
						+"\t"+this.employmentStabilityStartupByIndustryUrban[i][0][k]
								+"\t"+this.employmentStabilityStartupByIndustryRural[i][0][k]);
			}
			System.out.println();
		}
		System.out.println();
		
		for(k=0 ; k<this.numberOfSize ; k++) System.out.print("\t\tNation\tUrban\tRural");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			if(i==0) System.out.print("Average");
			else System.out.print((this.cdata.getYearList(i-1)+1));
			for(k=0 ; k<this.cdata.getNumberOfSize(i) ; k++){
				System.out.print("\t"+sizeTitle[k]+"\t");
				System.out.print(this.employmentStabilityStartupBySize[i][0][k]
						+"\t"+this.employmentStabilityStartupBySizeUrban[i][0][k]
								+"\t"+this.employmentStabilityStartupBySizeRural[i][0][k]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void printEmployees(String[] industryTitle, String[] durationTitle, String[] sizeTitle){
		int i, j;
		
		System.out.println("Year\tNation\tUrban\tRural\t");
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			System.out.print(this.cdata.getSumOfEmployee(i)+"\t");
			System.out.print(this.cdata.getSumOfUrbanEmployee(i)+"\t");
			System.out.print(this.cdata.getSumOfRuralEmployee(i)+"\t");
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Industry\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfIndustry(i) ; j++){
				System.out.print(industryTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfEmployeeByIndustry(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfUrbanEmployeeByIndustry(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfRuralEmployeeByIndustry(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Duration\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfDuration(i) ; j++){
				System.out.print(durationTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfEmployeeByDuration(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfUrbanEmployeeByDuration(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfRuralEmployeeByDuration(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Size\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfSize(i) ; j++){
				System.out.print(sizeTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfEmployeeBySize(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfUrbanEmployeeBySize(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfRuralEmployeeBySize(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void printStartupEmployees(String[] industryTitle, String[] durationTitle, String[] sizeTitle){
		int i, j;
		
		System.out.println("Year\tNation\tUrban\tRural\t");
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			System.out.print(this.cdata.getSumOfStartupEmployee(i)+"\t");
			System.out.print(this.cdata.getSumOfStartupUrbanEmployee(i)+"\t");
			System.out.print(this.cdata.getSumOfStartupRuralEmployee(i)+"\t");
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Industry\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfIndustry(i) ; j++){
				System.out.print(industryTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupEmployeeByIndustry(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupUrbanEmployeeByIndustry(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupRuralEmployeeByIndustry(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Duration\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfDuration(i) ; j++){
				System.out.print(durationTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupEmployeeByDuration(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupUrbanEmployeeByDuration(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupRuralEmployeeByDuration(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Size\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfSize(i) ; j++){
				System.out.print(sizeTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupEmployeeBySize(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupUrbanEmployeeBySize(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupRuralEmployeeBySize(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void printCompanies(String[] industryTitle, String[] durationTitle, String[] sizeTitle){
		int i, j;
		
		System.out.println("YeartNation\tUrban\tRural\t");
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			System.out.print(this.cdata.getSumOfCompany(i)+"\t");
			System.out.print(this.cdata.getSumOfUrbanCompany(i)+"\t");
			System.out.print(this.cdata.getSumOfRuralCompany(i)+"\t");
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Industry\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfIndustry(i) ; j++){
				System.out.print(industryTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfCompanyByIndustry(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfUrbanCompanyByIndustry(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfRuralCompanyByIndustry(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Duration\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfDuration(i) ; j++){
				System.out.print(durationTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfCompanyByDuration(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfUrbanCompanyByDuration(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfRuralCompanyByDuration(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Size\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfSize(i) ; j++){
				System.out.print(sizeTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfCompanyBySize(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfUrbanCompanyBySize(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfRuralCompanyBySize(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void printStartupCompanies(String[] industryTitle, String[] durationTitle, String[] sizeTitle){
		int i, j;
		
		System.out.println("YeartNation\tUrban\tRural\t");
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			System.out.print(this.cdata.getSumOfStartupCompany(i)+"\t");
			System.out.print(this.cdata.getSumOfStartupUrbanCompany(i)+"\t");
			System.out.print(this.cdata.getSumOfStartupRuralCompany(i)+"\t");
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Industry\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfIndustry(i) ; j++){
				System.out.print(industryTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupCompanyByIndustry(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupUrbanCompanyByIndustry(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupRuralCompanyByIndustry(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Duration\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfDuration(i) ; j++){
				System.out.print(durationTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupCompanyByDuration(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupUrbanCompanyByDuration(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupRuralCompanyByDuration(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.print("Year\t");
		for(i=0 ; i<this.numberOfIndustry ; i++) System.out.print("Size\tNation\tUrban\tRural\t");
		System.out.println();
		for(i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.cdata.getYearList(i)+"\t");
			for(j=0 ; j<this.cdata.getNumberOfSize(i) ; j++){
				System.out.print(sizeTitle[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupCompanyBySize(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupUrbanCompanyBySize(i)[j]+"\t");
				System.out.print(this.cdata.getSumOfStartupRuralCompanyBySize(i)[j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void proceedStabilityAnalysis(String locationFile, String industryFile, String microdataFile){
		this.initateVariables();
		this.initateDetailedVariables();
		this.analyzeCompanyData(locationFile, industryFile, microdataFile);
		
		this.analyzeStability();
		this.analyzeStabilityByIndustry();
		this.analyzeStabilityByDuration();
		this.analyzeStabilityBySize();
		
		this.analyzeStartupStability();
		this.analyzeStartupStabilityByIndustry();
		this.analyzeStartupStabilityByDuration();
		this.analyzeStartupStabilityBySize();
	}
	
	public void printAnalyzedData(String[] industryTitle, String[] durationTitle, String[] sizeTitle){
		this.printCompanyStatistics();
		this.printStability(industryTitle, durationTitle, sizeTitle);
		this.printStartupStability(industryTitle, durationTitle, sizeTitle);
	}
	
	public static void main(String[] args) {
		String filePath ="/Users/Jemyung/Desktop/Research/data_storage/company/";
		String locationcodeFile = filePath+"location_code/"+"year"+"_location_code.txt";
		String industrycodeFile = filePath+"industry_code/"+"year"+"_industry_code.txt";
		String microdataFile = filePath+"extracted/"+"year"+"_microdataCode.txt";
		int locationDepth = 2;
		int industryDepth = 0;				//0: primary, secondary, tertiary classification
		int businessType = 0;				//0: all business types
		String[] industryTitle = {"primary", "secondary", "tertiary"};
		String[] durationTitle = {"0_3", "4_9", "over 10"};
		String[] sizeTitle = {"1_4", "5_29", "30_299", "over 300"};
		int[] durations = {0, 4, 10};
		int[] sizes = {1, 5, 30, 300};
		int[] years = {1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010};
//		int[] years = {1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012};
//		int[] years = {2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010};
//		int[] years = {2008, 2009, 2010};

		EmploymentStabilityAnalyzer esa;
		esa = new EmploymentStabilityAnalyzer(years, locationDepth, industryDepth);
		esa.setGroupProperty(durations, sizes, businessType);
		esa.readStandardLocationCode(locationcodeFile.replace("year", "2010"));
		esa.readStandardIndustryCode(industrycodeFile.replace("year", "2010"));
		esa.proceedStabilityAnalysis(locationcodeFile, industrycodeFile, microdataFile);
		esa.printAnalyzedData(industryTitle, durationTitle, sizeTitle);
		esa.printEmployees(industryTitle, durationTitle, sizeTitle);
		esa.printStartupEmployees(industryTitle, durationTitle, sizeTitle);
	}

}
