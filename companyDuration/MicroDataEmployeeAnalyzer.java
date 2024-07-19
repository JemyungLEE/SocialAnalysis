package companyDuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import companyDuration.data.MicroData;


public class MicroDataEmployeeAnalyzer {
	
	/**
	 *  Subject: Extract data code from  Micro-data file
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2013.6.25
	 *  Last Modified Data: 2013.6.25 
	 *  Department: Seoul Nat. Univ. depart. of Rural Systems Engineering
	 *  Description: 
	 */

	int startYear, endYear;
	int duration;
	int n_region;
	int n_industry;
	
	int industryClassDepth;		//2:2nd_jung, 3: 3rd_so order depth
	int regionClassDepth;		//2:si_do,    5: si_gun_gu,   7: eup_myun_dong
	
	HashMap<Integer, String> locationHashMap;	//<code, name>
	HashMap<Integer, String> industryHashMap;	//<code, name>
	ArrayList<Integer> locatoinCode;			//use to find region's index	
	ArrayList<Integer> industryCode;			//use to find industry's index	
	ArrayList<String> locatoinName;				//use to find region's index	
	ArrayList<String> industryName;				//use to find industry's index

	double populationMax;						
	double populationMin;						
	double[][] population;						//[region][year]
	double[][] populationNormalized;			//[region][year]

	double[][] employeeSum;						//[region][year]
	double[][][] employee;						//[region][industry][year]
	double[][][] employeeRatio;					//[region][industry][year]
	double[][] employeeRatioMean;				//[region][industry]
	
	
	double[][] correlation;						//[region][industry]
	double[] correlationMean;					//[industry]
	double[] correlationStd;					//[industry]
	
	double[][] correlationIndustry;				//[industry][industry]
	double[][] groupEmployee;					//[region][year]
	double[] groupCorrelation;					//[region]
	
	public MicroDataEmployeeAnalyzer(int start, int end){
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;
		this.locationHashMap = new HashMap<Integer, String>();
		this.industryHashMap = new HashMap<Integer, String>();		
		this.locatoinCode = new ArrayList<Integer>();			
		this.industryCode = new ArrayList<Integer>();		
		this.locatoinName = new ArrayList<String>();		
		this.industryName = new ArrayList<String>();	
	}
		
	public MicroDataEmployeeAnalyzer(int start, int end, int industryClass, int regionClass){
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;
		this.industryClassDepth = industryClass;
		this.regionClassDepth = regionClass;
		this.locationHashMap = new HashMap<Integer, String>();
		this.industryHashMap = new HashMap<Integer, String>();		
		this.locatoinCode = new ArrayList<Integer>();			
		this.industryCode = new ArrayList<Integer>();		
		this.locatoinName = new ArrayList<String>();		
		this.industryName = new ArrayList<String>();	
	}
	
	
	public void initiate(){
		int i,j,k;
		
		this.employee = new double[this.n_region][this.n_industry][this.duration];
		this.employeeRatio = new double[this.n_region][this.n_industry][this.duration];
		this.employeeSum = new double[this.n_region][this.duration];
		this.employeeRatioMean = new double[this.n_region][this.n_industry];
		this.population = new double[this.n_region][this.duration];
		this.populationNormalized = new double[this.n_region][this.duration];
		
		this.correlation = new double[this.n_region][this.n_industry];		
		this.correlationMean = new double[this.n_industry];
		this.correlationStd = new double[this.n_industry];
		
		for(i=0 ; i<this.n_region ; i++){
			for(k=0 ; k<this.duration ; k++){
				this.population[i][k] = 0.0;
				this.employeeSum[i][k] = 0.0;
			}
			for(j=0 ; j<this.n_industry ; j++){
				this.correlation[i][j] = 0.0;
				for(k=0 ; k<this.duration ; k++) this.employee[i][j][k] = 0.0;
			}
		}
		for(i=0 ; i<this.n_industry ; i++){
			this.correlationMean[i] = 0.0;
			this.correlationStd[i] = 0.0;
		}			
	}
	
	public void readLocationCode(String inputFile){		
		int count = 0;
		String tmpCode;
		String tmpName;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpName = scan.next();				
				if( tmpCode.length() == this.regionClassDepth){				
					this.locationHashMap.put(Integer.parseInt(tmpCode), tmpName);
					this.locatoinCode.add(Integer.parseInt(tmpCode));
					this.locatoinName.add(tmpName);
					count++;
				}		
			}				
			this.n_region = count;
			
			scan.close();	
		} catch(IOException e) {}
			
	}
	
	public void readIndustryCode(String inputFile){		
		int count = 0;
		String tmpCode;
		String tmpName;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.next();
			scan.next();
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpName = scan.next();
				if( tmpCode.length() == this.industryClassDepth){
					this.industryHashMap.put(Integer.parseInt(tmpCode), tmpName);
					this.industryCode.add(Integer.parseInt(tmpCode));
					this.industryName.add(tmpName);
					count++;
				}		
			}				
			this.n_industry = count;
			
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void readPopulationData(String inputFile){
		
		int i;
		String tmpName;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
						
			for(i=0 ; i<this.duration+1 ; i++) scan.next();
			
			while(scan.hasNext()){
				tmpName = scan.next();				
				if(this.locationHashMap.containsValue(tmpName)){
					for(i=0 ; i<this.duration ; i++) 
						this.population[this.locatoinName.indexOf(tmpName)][i] = scan.nextDouble();						
				}			
			}					
			
			scan.close();	
		} catch(IOException e) {}
	}
	
	
	
	
	public void calculateCorrelation(){
		
		int i, j, k;
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
		
		int timeShift = 10;
		int period = this.duration - timeShift;
		
		
		for(i=0 ; i<this.n_region ; i++){
			meanPop = 0.0;
			for(k=0 ; k<period ; k++) meanPop += this.population[i][k+timeShift]; 
			meanPop /= period;
			
			tmpSumPop = 0.0;
			for(k=0 ; k<period ; k++) tmpSumPop += Math.pow((this.population[i][k+timeShift] - meanPop), 2);
			
			for(j=0 ; j<this.n_industry ; j++){
				meanEmp = 0.0;
				for(k=0 ; k<period ; k++) meanEmp += this.employee[i][j][k];
				meanEmp /= period;
				
				tmpSumEmp = 0.0;
				for(k=0 ; k<period ; k++) tmpSumEmp += Math.pow((this.employee[i][j][k] - meanEmp), 2);
				
				normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
				
				this.correlation[i][j] = 0.0;				
				for(k=0 ; k<period ; k++) 
					this.correlation[i][j] += (this.population[i][k+timeShift]-meanPop)
															*(this.employee[i][j][k]-meanEmp);
				this.correlation[i][j] /= normalizer;
			}
		}	
	}
	
	public void calculateCorrelation(double lowcut, int timeShift){
		
		int i, j, k;
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
		
		int period = this.duration - timeShift;
		
		
		for(i=0 ; i<this.n_region ; i++){
			meanPop = 0.0;
			tmpSumPop = 0.0;
			
			for(k=0 ; k<period ; k++) meanPop += this.population[i][k+timeShift]; 
			meanPop /= period;			
			
			for(k=0 ; k<period ; k++) tmpSumPop += Math.pow((this.population[i][k+timeShift] - meanPop), 2);
						
			for(j=0 ; j<this.n_industry ; j++){				
				meanEmp = 0.0;				
				tmpSumEmp = 0.0;
				
				if(this.employeeRatioMean[i][j] > lowcut){					
					
					for(k=0 ; k<period ; k++) meanEmp += this.employee[i][j][k];
					meanEmp /= period;
					
					
					for(k=0 ; k<period ; k++) tmpSumEmp += Math.pow((this.employee[i][j][k] - meanEmp), 2);
					
					normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
					
					this.correlation[i][j] = 0.0;				
					for(k=0 ; k<period ; k++) 
						this.correlation[i][j] += (this.population[i][k+timeShift]-meanPop)
																*(this.employee[i][j][k]-meanEmp);
					this.correlation[i][j] /= normalizer;
				}
			}
		}	
	}
	
	public void calculateNormalizedCorrelation(double lowcut, int timeShift){
		
		int i, j, k;
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
		double tmpMax, tmpMin;
		
		double popHighCut = 0.9;
		double popLowCut = 0.6;
		
		int period = this.duration - timeShift;		
		
		double[] tmpPopulation = new double[period];
		double[] tmpEmployee = new double[period];		
		
		for(i=0 ; i<this.n_region ; i++){
			meanPop = 0.0;
			tmpSumPop = 0.0;
			
			if(this.populationNormalized[i][timeShift] > popLowCut 
					&& this.populationNormalized[i][timeShift] < popHighCut){
			
				for(k=0 ; k<period ; k++) meanPop += this.populationNormalized[i][k+timeShift]; 
				meanPop /= period;			
				
				for(k=0 ; k<period ; k++)tmpSumPop+= Math.pow((this.populationNormalized[i][k+timeShift]-meanPop),2);
							
				for(j=0 ; j<this.n_industry ; j++){				
					meanEmp = 0.0;				
					tmpSumEmp = 0.0;
					
					if(this.employeeRatioMean[i][j] > lowcut){					
						
						for(k=0 ; k<period ; k++) meanEmp += this.employeeRatio[i][j][k];
						meanEmp /= period;
						
						
						for(k=0 ; k<period ; k++) tmpSumEmp += Math.pow((this.employeeRatio[i][j][k] - meanEmp), 2);
						
						normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
						
						this.correlation[i][j] = 0.0;				
						for(k=0 ; k<period ; k++) 
							this.correlation[i][j] += (this.populationNormalized[i][k+timeShift]-meanPop)
																	*(this.employeeRatio[i][j][k]-meanEmp);
						this.correlation[i][j] /= normalizer;
					}
				}
			}
		}	
	}
	
	public void calculateMeanStdOfCorrelation(){
		int i, j;
		double count;
		
		/*** calculate average of correlation ***/		
		for(i=0 ; i<this.n_industry ; i++){
			count = 0.0;
			for(j=0 ; j<this.n_region ; j++){
				if(this.correlation[j][i] > 0 || this.correlation[j][i] < 0 ){
					this.correlationMean[i] += this.correlation[j][i];
					count++;
				}
			}
			if(count > 0) this.correlationMean[i] /= count;
			
//			System.out.println(this.industryName.get(i)+"\t"+count);
		}	
		
		/*** calculate std of correlation ***/
		for(i=0 ; i<this.n_industry ; i++){
			count = 0.0;
			for(j=0 ; j<this.n_region ; j++){
				if(this.correlation[j][i] > 0 || this.correlation[j][i] < 0 ) {
					this.correlationStd[i] += Math.pow((this.correlation[j][i] - this.correlationMean[i]), 2);
					count++;
				}
			}
			if(count > 0) this.correlationStd[i] /= count;
			this.correlationStd[i] = Math.sqrt(this.correlationStd[i]);
		}	
	}
	
	/*
	public void calculateCorrelation(int type){
		
		int i, j, k;		
		double count;		
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
		
		if(type==1){
		
			for(i=0 ; i<this.n_region ; i++){
				meanPop = 0.0;
				for(k=0 ; k<this.duration-1 ; k++) meanPop += this.population[i][k+1]/this.population[i][k]; 
				meanPop /= (this.duration-1.0);
				
				tmpSumPop = 0.0;
				for(k=0 ; k<this.duration-1 ; k++) tmpSumPop += Math.pow((this.population[i][k+1]/this.population[i][k] - meanPop), 2);
				
				for(j=0 ; j<this.n_industry ; j++){
					meanEmp = 0.0;
					for(k=0 ; k<this.duration-1 ; k++) meanEmp += this.employee[i][j][k+1]/this.employee[i][j][k];
					meanEmp /= (this.duration-1.0);
					
					tmpSumEmp = 0.0;
					for(k=0 ; k<this.duration-1 ; k++) tmpSumEmp += Math.pow((this.employee[i][j][k+1]/this.employee[i][j][k] - meanEmp), 2);
					
					normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
					
					this.correlation[i][j] = 0.0;				
					for(k=0 ; k<this.duration-1 ; k++) 
						this.correlation[i][j] += (this.population[i][k+1]/this.population[i][k]-meanPop)*(this.employee[i][j][k+1]/this.employee[i][j][k]-meanEmp);
					this.correlation[i][j] /= normalizer;
				}
			}
						
		
		}
		
		else if(type==2){
			
			for(i=0 ; i<this.n_region ; i++){
				meanPop = 0.0;
				for(k=0 ; k<this.duration ; k++) meanPop += this.population[i][k]/this.population[i][this.duration-1]; 
				meanPop /= this.duration;
				
				tmpSumPop = 0.0;
				for(k=0 ; k<this.duration ; k++) 
					tmpSumPop += Math.pow((this.population[i][k]/this.population[i][this.duration-1] - meanPop), 2);
				
				for(j=0 ; j<this.n_industry ; j++){
					meanEmp = 0.0;
					for(k=0 ; k<this.duration ; k++) meanEmp += this.employee[i][j][k]/this.employee[i][j][this.duration-1];
					meanEmp /= this.duration;
					
					tmpSumEmp = 0.0;
					for(k=0 ; k<this.duration ; k++) tmpSumEmp += Math.pow((this.employee[i][j][k]/this.employee[i][j][this.duration-1] - meanEmp), 2);
					
					normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
					
					this.correlation[i][j] = 0.0;				
					for(k=0 ; k<this.duration ; k++) 
						this.correlation[i][j] += (this.population[i][k]/this.population[i][this.duration-1] - meanPop)
													*(this.employee[i][j][k]/this.employee[i][j][this.duration-1] - meanEmp);
					this.correlation[i][j] /= normalizer;
				}
			}
		
		}	
	}
	*/
	
	public void printDataCodes(String filePath, String outputFile, ArrayList<ArrayList<MicroData>> dataList){
		
		int i, j;
		MicroData tmpMdata;			
		ArrayList<MicroData> tmpDataList;
		
		String tmpOutputFile;
		
		for(i=0 ; i<this.duration ; i++){
					
			System.out.println("printing: "+(this.startYear+i));
			tmpOutputFile = filePath+(this.startYear+i)+outputFile;
			
			tmpDataList = dataList.get(i);			
						
			try{
				File file = new File(tmpOutputFile);
				PrintWriter pw = new PrintWriter(file);
				
				pw.println("Location_Code\tIndustry_Code\tWorkers");
				for(j=0 ; j<dataList.size() ; j++){
					tmpMdata = tmpDataList.get(j);				
					pw.println(tmpMdata.getLocationCode()+"\t"+tmpMdata.getIndustryCode()+"\t"+tmpMdata.getWorkers());
				}
	
				pw.close();
			}catch(IOException e) {}
		
		}
		
	}
	
	public void readEmployee(String filePath, String inputFile){
		//1999~2001: year(15,19), location(0,7), industry( 7,13), workers(107,112), business(13,14), store(14,15)
		//2004~2010: year(15,19), location(0,7), industry( 7,13), workers(107,112), business(13,14), store(14,15)
		//2003	   : year(17,21), location(0,7), industry(23,29), workers(199,209), business(15,16), store(16,17)
		//2002	   : year(16,20), location(0,7), industry(22,28), workers(113,118), business(14,15), store(15,16)
		//1998	   : year(17,21), location(0,7), industry(23,29), workers(114,119), business(14,15), store(16,17)
		
		int i, j;
		int check[], count, errcount[];
		
		int location = 0;
		int industry = 0;
		int workers = 0;
		
		int locationIndex;
		int industryIndex;		
		
		String tmpInputFile;
		String tmpStr;
		String microdataCode;		
		
		int[] locationStart = new int[this.duration];
		int[] locationEnd = new int[this.duration];
		int[] industryStart = new int[this.duration];
		int[] industryEnd = new int[this.duration];
		int[] workerStart = new int[this.duration];
		int[] workerEnd = new int[this.duration];
		
		
		locationStart[0]=0; locationEnd[0]=5; industryStart[0]=23;industryEnd[0]=26; workerStart[0]=114; workerEnd[0]=119;
		locationStart[1]=0; locationEnd[1]=5; industryStart[1]=7; industryEnd[1]=10; workerStart[1]=107; workerEnd[1]=112;
		locationStart[2]=0; locationEnd[2]=5; industryStart[2]=7; industryEnd[2]=10; workerStart[2]=107; workerEnd[2]=112;
		locationStart[3]=0; locationEnd[3]=5; industryStart[3]=7; industryEnd[3]=10; workerStart[3]=107; workerEnd[3]=112;
		locationStart[4]=0; locationEnd[4]=5; industryStart[4]=22;industryEnd[4]=25; workerStart[4]=113; workerEnd[4]=118;
		locationStart[5]=0; locationEnd[5]=5; industryStart[5]=23;industryEnd[5]=26; workerStart[5]=199; workerEnd[5]=209;
		locationStart[6]=0; locationEnd[6]=5; industryStart[6]=7; industryEnd[6]=10; workerStart[6]=107; workerEnd[6]=112;
		locationStart[7]=0; locationEnd[7]=5; industryStart[7]=7; industryEnd[7]=10; workerStart[7]=107; workerEnd[7]=112;
		locationStart[8]=0; locationEnd[8]=5; industryStart[8]=7; industryEnd[8]=10; workerStart[8]=107; workerEnd[8]=112;
		locationStart[9]=0; locationEnd[9]=5; industryStart[9]=7; industryEnd[9]=10; workerStart[9]=107; workerEnd[9]=112;
		locationStart[10]=0;locationEnd[10]=5;industryStart[10]=7;industryEnd[10]=10;workerStart[10]=107;workerEnd[10]=112;
		locationStart[11]=0;locationEnd[11]=5;industryStart[11]=7;industryEnd[11]=10;workerStart[11]=107;workerEnd[11]=112;	
		locationStart[12]=0;locationEnd[12]=5;industryStart[12]=7;industryEnd[12]=10;workerStart[12]=107;workerEnd[12]=112;	

		File file;
		Scanner scan;
		
		for(j=0 ; j <this.duration ; j++){
		
			System.out.print((this.startYear+j)+" ");
			tmpInputFile = filePath+(this.startYear+j)+inputFile;
			
			try{
				file = new File(tmpInputFile);
				scan = new Scanner(file);
				
				count = 0;
				errcount = new int[4];
				check = new int[4];
				for(i=0 ; i< 4 ; i++) errcount[i] = 0;
				while(scan.hasNext()){	
					microdataCode = scan.nextLine();
					for(i=0 ; i<4 ; i++) check[i] = 0;
					
					//extract location code
					tmpStr = microdataCode.substring(locationStart[j], locationEnd[j]);
					if(tmpStr.contains(" ") == false) location = Integer.parseInt(tmpStr);
					else check[1]++; 
					
					//extract industry code
					//tmpStr = microdataCode.substring(industryStart[j]+1, industryEnd[j]);
					tmpStr=microdataCode.substring(industryStart[j]+1,industryStart[j]+1+this.industryClassDepth);
					if(tmpStr.contains(" ") == false) industry = Integer.parseInt(tmpStr);
					else check[2]++;
					
					//extract workers number
					tmpStr = microdataCode.substring(workerStart[j], workerEnd[j]).trim();
					if(tmpStr.contains(" ") == false) workers = Integer.parseInt(tmpStr);
					else check[3]++;	
											
					for(i=1 ; i<4 ; i++) if(check[i] != 0) check[0] = check[i];
					
					//add micro-data at data list
					if(check[0] == 0){
						if(this.locatoinCode.contains(location) && this.industryCode.contains(industry)){
							locationIndex = this.locatoinCode.indexOf(location);
							industryIndex = this.industryCode.indexOf(industry);
							
							this.employee[locationIndex][industryIndex][j] += workers;
						}
					}
					else for(i=0 ; i<4 ; i++) errcount[i] += check[i];
					
					count++;
				}	
				if(errcount[0]>0){
					System.out.println("error: "+errcount[0]+" damaged data in "+count+" data");
					for(i=1 ; i<4 ; i++) System.out.println("\ttype "+i+" error: "+errcount[i]);
				}
				scan.close();	
			} catch(IOException e) {}
		
		}		
	}
	
	public void printEmployee(String outputFile){
		
		int i, j, k;	
						
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("Location\tIndustry\tYear\tWorkers");

			for(i=0 ; i<this.n_region ; i++)
				for(j=0 ; j<this.n_industry ; j++)
					for(k=0 ; k<this.duration ; k++)
						pw.println(this.locatoinName.get(i)+"\t"+this.industryName.get(j)+"\t"
									+(this.startYear+k)+"\t"+this.employee[i][j][k]);	
			pw.close();
		}catch(IOException e) {}		
	}
		
	public void readEmployee(String inputFile){
		
		int i, j, k;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.nextLine();
			
			for(i=0 ; i<this.n_region ; i++){
				for(j=0 ; j<this.n_industry ; j++){
					for(k=0 ; k<this.duration ; k++){
						scan.next();
						scan.next();
						scan.next();
						this.employee[i][j][k] = scan.nextDouble();
					}
				}
			}
						
			scan.close();	
		} catch(IOException e) {}	
	}
	
	
	public void calculateEmployeeStatistics(){
		
		int i, j, k;
		
		/*** sum region's total employees ***/
		for(i=0 ; i<this.n_region ; i++)
			for(j=0 ; j<this.duration ; j++)
				for(k=0 ; k<this.n_industry ; k++) this.employeeSum[i][j] += this.employee[i][k][j];	
		
		/*** calculate employee ratio ***/
		for(i=0 ; i<this.n_region ; i++)
			for(j=0 ; j<this.duration ; j++)
				for(k=0 ; k<this.n_industry ; k++) 
					this.employeeRatio[i][k][j] = this.employee[i][k][j] / this.employeeSum[i][j];
		
		/*** calculate mean employee ratio ***/
		for(i=0 ; i<this.n_region ; i++){
			for(k=0 ; k<this.n_industry ; k++){
				for(j=0 ; j<this.duration ; j++) this.employeeRatioMean[i][k] += this.employeeRatio[i][k][j];				
				this.employeeRatioMean[i][k] /= this.duration;
			}			
		}
	}
	
	public void printPopulaton(String outputFile){
		
		int i, j;	
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("Location\tYear\tPopulation");

			for(i=0 ; i<this.n_region ; i++)
				for(j=0 ; j<this.duration ; j++)
					pw.println(this.locatoinName.get(i)+"\t"+(this.startYear+j)+"\t"+this.population[i][j]);	
			pw.close();
		}catch(IOException e) {}	
	}
	
	public void readPopulation(String inputFile){
		
		int i, j;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.nextLine();
			
			for(i=0 ; i<this.n_region ; i++){
				for(j=0 ; j<this.duration ; j++){
					scan.next();
					scan.next();
					this.population[i][j] = scan.nextDouble();					
				}
			}
						
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void calculatePopulationStatistics(){
		
		int i, j;
		double min = 0.1;
		double max = 0.9;
		
		j=0;
		do{
			this.populationMax = this.population[0][j];
			this.populationMin = this.population[0][j];
			j++;
		}while(population[0][j] == 0);
		
		for(j=0 ; j<this.duration ; j++){
			for(i=0 ; i<this.n_region ; i++){
				if(this.populationMax < this.population[i][j]) 
					this.populationMax = this.population[i][j];
				else if(this.populationMin > this.population[i][j] && this.population[i][j] > 0) 
					this.populationMin = this.population[i][j];				
			}
		}
		
		for(j=0 ; j<this.duration ; j++)
			for(i=0 ; i<this.n_region ; i++)
				this.populationNormalized[i][j] = max * (this.population[i][j] - this.populationMin) 
													/ (this.populationMax - this.populationMin) + min;		
	}
	
	public void printCorrelation(String outputFile){
		
		int i, j;	
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("Location\tIndustry\tCorrelation");

			for(i=0 ; i<this.n_region ; i++)
				for(j=0 ; j<this.n_industry ; j++)
					pw.println(this.locatoinName.get(i)+"\t"+this.industryName.get(j)+"\t"
								+this.correlation[i][j]);	
			pw.close();
		}catch(IOException e) {}			

		try{
			File file = new File(outputFile.replace(".txt", "_mean_std.txt"));
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("Industry\tMean_correlation\tStd_correlation");

			for(i=0 ; i<this.n_industry ; i++)
				pw.println(this.industryName.get(i)+"\t"+this.correlationMean[i]+"\t"+this.correlationStd[i]);	
			pw.close();
		}catch(IOException e) {}	
	}	
	
	public void calculateCorrelation(double lowcut, int timeShift, double popHighCut, double popLowCut){
		
		int i, j, k;
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
		
		int period = this.duration - timeShift;
		
		
		for(i=0 ; i<this.n_region ; i++){
			meanPop = 0.0;
			tmpSumPop = 0.0;
			
			if(this.populationNormalized[i][timeShift] > popLowCut 
					&& this.populationNormalized[i][timeShift] < popHighCut){
			
				for(k=0 ; k<period ; k++) meanPop += this.population[i][k+timeShift]; 
				meanPop /= period;			
				
				for(k=0 ; k<period ; k++) tmpSumPop += Math.pow((this.population[i][k+timeShift] - meanPop), 2);
							
				for(j=0 ; j<this.n_industry ; j++){				
					meanEmp = 0.0;				
					tmpSumEmp = 0.0;
					
					if(this.employeeRatioMean[i][j] > lowcut){					
						
						for(k=0 ; k<period ; k++) meanEmp += this.employee[i][j][k];
						meanEmp /= period;
						
						
						for(k=0 ; k<period ; k++) tmpSumEmp += Math.pow((this.employee[i][j][k] - meanEmp), 2);
						
						normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
						
						this.correlation[i][j] = 0.0;				
						for(k=0 ; k<period ; k++) 
							this.correlation[i][j] += (this.population[i][k+timeShift]-meanPop)
																	*(this.employee[i][j][k]-meanEmp);
						this.correlation[i][j] /= normalizer;
					}
				}
			}
		}	
	}
	
	public void calculateGroupCorrelation(int timeShift, double positiveCut, double negativeCut){

		int i, j, k;
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
	
		int period = this.duration - timeShift;
	
		double[][] groupEmployee = new double[this.n_region][this.duration];
		double[] groupCorrelation = new double[this.n_region];	
	
		for(i=0 ; i<this.n_region ; i++){
		
			meanPop = 0.0;
			for(k=0 ; k<period ; k++) meanPop += this.population[i][k+timeShift]; 
			meanPop /= period;
	
			tmpSumPop = 0.0;
			for(k=0 ; k<period ; k++) tmpSumPop += Math.pow((this.population[i][k+timeShift] - meanPop), 2);
	
			for(j=0 ; j<this.n_industry ; j++){				
				if(this.correlationMean[j] > positiveCut)
					for(k=0 ; k<this.duration ; k++) groupEmployee[i][k] += this.employee[i][j][k];
				else if(this.correlationMean[j] < negativeCut)
					for(k=0 ; k<this.duration ; k++) groupEmployee[i][k] -= this.employee[i][j][k];				
			}
	
			meanEmp = 0.0;
			for(k=0 ; k<period ; k++) meanEmp += groupEmployee[i][k];
			meanEmp /= period;
	
			tmpSumEmp = 0.0;
			for(k=0 ; k<period ; k++) tmpSumEmp += Math.pow((groupEmployee[i][k] - meanEmp), 2);
	
			normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
	
			groupCorrelation[i] = 0.0;				
			for(k=0 ; k<period ; k++) 
				groupCorrelation[i] += (this.population[i][k+timeShift]-meanPop)
										*(groupEmployee[i][k]-meanEmp);
			groupCorrelation[i] /= normalizer;			
		}


		for(i=0 ; i<this.n_region ; i++){
			System.out.println(this.locatoinName.get(i)+"\t"+groupCorrelation[i]
								+"\t"+this.population[i][this.duration-1]);
		}		
	}
	
	public void calculateGroupCorrelation(int timeShift, double positiveCut, double negativeCut
											,double popHighCut, double popLowCut){
		
		int i, j, k;
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
				
		int period = this.duration - timeShift;
		
		double[][] groupEmployee = new double[this.n_region][this.duration];
		double[] groupCorrelation = new double[this.n_region];	
		
		for(i=0 ; i<this.n_region ; i++){
			
		
			if(this.populationNormalized[i][timeShift] > popLowCut 
					&& this.populationNormalized[i][timeShift] < popHighCut){
			
				meanPop = 0.0;
				for(k=0 ; k<period ; k++) meanPop += this.population[i][k+timeShift]; 
				meanPop /= period;
			
				tmpSumPop = 0.0;
				for(k=0 ; k<period ; k++) tmpSumPop += Math.pow((this.population[i][k+timeShift] - meanPop), 2);
			
				for(j=0 ; j<this.n_industry ; j++){				
					if(this.correlationMean[j] > positiveCut)
						for(k=0 ; k<this.duration ; k++) groupEmployee[i][k] += this.employee[i][j][k];
					else if(this.correlationMean[j] < negativeCut)
						for(k=0 ; k<this.duration ; k++) groupEmployee[i][k] -= this.employee[i][j][k];				
				}
			
				meanEmp = 0.0;
				for(k=0 ; k<period ; k++) meanEmp += groupEmployee[i][k];
				meanEmp /= period;
			
				tmpSumEmp = 0.0;
				for(k=0 ; k<period ; k++) tmpSumEmp += Math.pow((groupEmployee[i][k] - meanEmp), 2);
			
				normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
			
				groupCorrelation[i] = 0.0;				
				for(k=0 ; k<period ; k++) groupCorrelation[i] += (this.population[i][k+timeShift]-meanPop) 
																	* (groupEmployee[i][k]-meanEmp);
				groupCorrelation[i] /= normalizer;		
				
				/*** print group correlation ***/
				System.out.println(this.locatoinName.get(i)+"\t"+groupCorrelation[i]
									+"\t"+this.population[i][this.duration-1]);				
			}
		}		
	}
	
	public void calculateGroupCorrelation(int timeShift){

		int i, j, k;
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
	
		int period = this.duration - timeShift;
	
		this.groupEmployee = new double[this.n_region][this.duration];
		this.groupCorrelation = new double[this.n_region];	
	
		for(i=0 ; i<this.n_region ; i++){
			
			
			meanPop = 0.0;
			for(k=0 ; k<period ; k++) meanPop += this.population[i][k+timeShift]; 
			meanPop /= period;
	
			tmpSumPop = 0.0;
			for(k=0 ; k<period ; k++) tmpSumPop += Math.pow((this.population[i][k+timeShift] - meanPop), 2);
	
			for(j=0 ; j<this.n_industry ; j++)				
				for(k=0 ; k<this.duration ; k++) 
					groupEmployee[i][k] += this.correlationMean[j] * this.employee[i][j][k];			
			
	
			meanEmp = 0.0;
			for(k=0 ; k<period ; k++) meanEmp += groupEmployee[i][k];
			meanEmp /= period;
	
			tmpSumEmp = 0.0;
			for(k=0 ; k<period ; k++) tmpSumEmp += Math.pow((groupEmployee[i][k] - meanEmp), 2);
	
			normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
	
			groupCorrelation[i] = 0.0;				
			for(k=0 ; k<period ; k++) 
				groupCorrelation[i] += (this.population[i][k+timeShift]-meanPop)
										*(groupEmployee[i][k]-meanEmp);
			groupCorrelation[i] /= normalizer;					
		}		
	}
	
	public void printGroupCorrelation(String outputFile){
		
		int i;	
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("Region\tCorrelation\tPopulation");

			for(i=0 ; i<this.n_region ; i++) pw.println(this.locatoinName.get(i)+"\t"+groupCorrelation[i]
														+"\t"+this.population[i][this.duration-1]);
			pw.close();
		}catch(IOException e) {}	
	}	
	
	
	double[][] entropy;		//[region][year]
	double[][] migration;	//[region][year]
	
	public void readEntropy(String inputFile){
		
		int i, j;
		int regionIndex;
		String region;
		
		this.entropy = new double[this.n_region][this.duration];
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.nextLine();
			
			while(scan.hasNext()){
				region = scan.next();
				for(i=0 ; i<6 ; i++) scan.nextDouble();
				if(this.locatoinName.contains(region)){
					regionIndex = this.locatoinName.indexOf(region);
					for(j=0 ; j<this.duration ; j++) this.entropy[regionIndex][j] = scan.nextDouble();
				}
				else for(j=0 ; j<this.duration ; j++) scan.nextDouble(); 				
			}
						
			scan.close();	
		} catch(IOException e) {}	
	}
	
	public void readMigration(String inputFile){
		
		int j;
		int regionIndex;
		String region;
		
		this.migration = new double[this.n_region][this.duration+2];
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.nextLine();
			
			while(scan.hasNext()){
				region = scan.next();
				if(this.locatoinName.contains(region)){
					regionIndex = this.locatoinName.indexOf(region);
					for(j=0 ; j<this.duration+2 ; j++){
						if(scan.hasNextDouble()) this.migration[regionIndex][j] = scan.nextDouble();
						else{
							scan.next();
							this.migration[regionIndex][j] = -63279;
						}
					}
				}
				else for(j=0 ; j<this.duration+2 ; j++) scan.next(); 				
			}
						
			scan.close();	
		} catch(IOException e) {}	
	}
	
	double[] entropyCorrelation;		//[region]
	
	public void calculateEntropyCorrelation(int timeShift){

		int i, j, k;
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
	
		int count;
		
		int period = this.duration;
		if(timeShift>2) period = this.duration - timeShift + 2;
		
		this.entropyCorrelation = new double[this.n_region];	
			
		for(i=0 ; i<this.n_region ; i++){
			count = 0;
			for(k=0 ; k<period ; k++) if(this.migration[i][k+timeShift] == -63279) count++; 
			
			if(count == 0){
			
				meanPop = 0.0;
				for(k=0 ; k<period ; k++) meanPop += this.migration[i][k+timeShift]; 
				meanPop /= period;
		
				tmpSumPop = 0.0;
				for(k=0 ; k<period ; k++) tmpSumPop += Math.pow((this.migration[i][k+timeShift] - meanPop), 2);
					
		
				meanEmp = 0.0;
				for(k=0 ; k<period ; k++) meanEmp += this.entropy[i][k];
				meanEmp /= period;
		
				tmpSumEmp = 0.0;
				for(k=0 ; k<period ; k++) tmpSumEmp += Math.pow((this.entropy[i][k] - meanEmp), 2);
		
				normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
		
				this.entropyCorrelation[i] = 0.0;				
				for(k=0 ; k<period ; k++) 
					this.entropyCorrelation[i] += (this.migration[i][k+timeShift]-meanPop)
													*(this.entropy[i][k]-meanEmp);
				this.entropyCorrelation[i] /= normalizer;
				
				System.out.println(this.locatoinName.get(i)+"\t"+this.entropyCorrelation[i]);
			}
		}		
	}
	
	public void printEntropyMigration(String outputFile){
		int i,j;
		int count;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			for(i=0 ; i<this.n_region ; i++){
				count = 0;
				for(j=0 ; j<this.duration ; j++) if(this.migration[i][j] == -63279) count++;
				for(j=0 ; j<this.duration ; j++) if(this.population[i][j] == 0) count++;			 
				
				if(count == 0){
					pw.print(this.locatoinName.get(i));			
					for(j=0 ; j<this.duration ; j++)			
						pw.print("\t"+this.entropy[i][j]+"\t"+(this.migration[i][j]/this.population[i][j]));			
					pw.println();
				}
			}

			pw.close();
		}catch(IOException e) {}
	}
	
	public void calculateEmployeeMigrationCorrelation(double lowcut, int timeShift){

		int i, j, k;
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
	
		int count;
		
		int period = this.duration;
		if(timeShift>2) period = this.duration - timeShift + 2;
		
			
		for(i=0 ; i<this.n_region ; i++){
			count = 0;
			for(k=0 ; k<period ; k++) if(this.migration[i][k+timeShift] == -63279) count++; 
			
			if(count == 0){
			
				meanPop = 0.0;
				for(k=0 ; k<period ; k++) meanPop += this.migration[i][k+timeShift]; 
				meanPop /= period;
		
				tmpSumPop = 0.0;
				for(k=0 ; k<period ; k++) tmpSumPop += Math.pow((this.migration[i][k+timeShift] - meanPop), 2);
					
		
				
				for(j=0 ; j<this.n_industry ; j++){				
					meanEmp = 0.0;				
					tmpSumEmp = 0.0;
					
					if(this.employeeRatioMean[i][j] > lowcut){					
						
						for(k=0 ; k<period ; k++) meanEmp += this.employeeRatio[i][j][k];
						meanEmp /= period;
						
						
						for(k=0 ; k<period ; k++) tmpSumEmp += Math.pow((this.employeeRatio[i][j][k] - meanEmp), 2);
						
						normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
						
						this.correlation[i][j] = 0.0;				
						for(k=0 ; k<period ; k++) 
							this.correlation[i][j] += (this.migration[i][k+timeShift]-meanPop)
																	*(this.employeeRatio[i][j][k]-meanEmp);
						this.correlation[i][j] /= normalizer;
					}
				}

			}
		}		
	}
	
	
	public void printEmployeeMigration(String outputFile){
		int i,j;
		int count;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			for(i=0 ; i<this.n_region ; i++){
				count = 0;
				for(j=0 ; j<this.duration ; j++) if(this.migration[i][j] == -63279) count++;
				for(j=0 ; j<this.duration ; j++) if(this.population[i][j] == 0) count++;			 
				
				if(count == 0){
					pw.print(this.locatoinName.get(i));			
					for(j=0 ; j<this.duration ; j++)			
						pw.print("\t"+this.entropy[i][j]+"\t"+(this.migration[i][j]/this.population[i][j]));			
					pw.println();
				}
			}

			pw.close();
		}catch(IOException e) {}
	}
	
	public static void main(String[] args){			
	
		String filePath = "/Users/jml/Desktop/Research/data_storage/company/";
		String microdataFile = "_microdata.txt";
		String locationCodeFile = filePath+"location_code/location_code.txt";
		String industryCodeFile = filePath+"industry_code/industry_code.txt";
		String populationDataFile = filePath+"population_region.txt";
		String employeeFile = filePath+"employee_1stOrder.txt";
		String populationFile = filePath+"population.txt";
		String correlationFile = filePath+"correlation.txt";
		String groupCorrelationFile = filePath+"correlation_group.txt";
		
		
		int start = 1998;
		int end = 2010;
		
		int industryClass = 2;		//2: 중분류,  3: 소분류,   4: 세분류  
		int regionClass = 2;	    //2: 시도,    5: 시군구,   7: 읍면동
		int timeShift = 0;		
		
		MicroDataEmployeeAnalyzer mea = new MicroDataEmployeeAnalyzer(start, end, industryClass, regionClass);
			
		System.out.print("location code reading: ");
		mea.readLocationCode(locationCodeFile);
		System.out.println("complete");

		System.out.print("industry code reading: ");
		mea.readIndustryCode(industryCodeFile);
		System.out.println("complete");

		System.out.print("variables initializing: ");
		mea.initiate();
		System.out.println("complete");
		
		/*
		System.out.print("population data reading: ");
		mea.readPopulationData(populationDataFile);
		System.out.println("complete");		
		
		System.out.print("population data printing: ");
		mea.printPopulaton(populationFile);
		System.out.println("complete");
		*/
		
		
		System.out.print("employee data reading: ");
		mea.readEmployee(filePath, microdataFile);
		System.out.println("complete");
				
		System.out.print("employee data printing: ");
		mea.printEmployee(employeeFile);
		System.out.println("complete");	
				
		
		System.out.print("population reading: ");
		mea.readPopulation(populationFile);
		mea.calculatePopulationStatistics();
		System.out.println("complete");
		
		System.out.print("employee reading: ");
		mea.readEmployee(employeeFile);
		mea.calculateEmployeeStatistics();
		System.out.println("complete");	
		
		mea.readEntropy(filePath+"normEntropy_region.txt");
		mea.readMigration(filePath+"migration_region.txt");
		//mea.calculateEntropyCorrelation(0);
		//mea.printEntropyMigration(filePath+"entropy_migration.txt");
		mea.calculateEmployeeMigrationCorrelation(0.01, 0);
		mea.calculateMeanStdOfCorrelation();
		mea.printCorrelation(filePath+"employee_migration_correlation.txt");
		/*
		System.out.print("correlation calculating: ");
		mea.calculateCorrelation(0.001, timeShift);
		//mea.calculateCorrelation(0.01, 0, 0.3, 0.1);
		mea.calculateMeanStdOfCorrelation();
		mea.calculateGroupCorrelation(timeShift);
		//mea.calculateGroupCorrelation(0, 0.2, -0.2);
		//mea.calculateGroupCorrelation(0, 0.2, -0.2, 0.3, 0.1);
		System.out.println("complete");		

		System.out.print("correlation printing: ");
		mea.printCorrelation(correlationFile);
		mea.printGroupCorrelation(groupCorrelationFile);
		System.out.println("complete");	
		*/	
			
		System.out.println("process complete.");
	}

}