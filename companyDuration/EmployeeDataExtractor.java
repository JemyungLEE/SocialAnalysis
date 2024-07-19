package companyDuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import companyDuration.data.MicroData;

public class EmployeeDataExtractor {

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

	double[][] employeeSum;						//[region][year]
	double[][][] employee;						//[region][industry][year]
	double[][][] employeeRatio;					//[region][industry][year]
	double[][] employeeRatioMean;				//[region][industry]
	double[][] population;						//[region][year]	
	
	public EmployeeDataExtractor(int start, int end){
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
		
	public EmployeeDataExtractor(int start, int end, int industryClass, int regionClass){
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
		this.employee = new double[this.n_region][this.n_industry][this.duration];
		this.employeeRatio = new double[this.n_region][this.n_industry][this.duration];
		this.employeeSum = new double[this.n_region][this.duration];
		this.employeeRatioMean = new double[this.n_region][this.n_industry];
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
	
	public void calculateEmployeeData(){
		
		int i, j, k;
		
		double[][][] tmpSum = new double[this.n_region][this.duration][7];
		double[][] seoulSum = new double[this.duration][7];
		double[][] kkSum = new double[this.duration][7];
		
		
		for(i=0 ; i<this.n_region ; i++) 
			for(j=0 ; j<this.duration ; j++) for(k=0 ; k<7 ; k++) tmpSum[i][j][k] = 0.0;
		for(j=0 ; j<this.duration ; j++){
			for(k=0 ; k<7 ; k++){
				seoulSum[j][k] = 0.0;
				kkSum[j][k] = 0.0;
			}
		}
		
		
		for(i=0 ; i<this.n_region ; i++){
			for(j=0 ; j<this.duration ; j++){
				for(k=0 ; k<3 ; k++){
					tmpSum[i][j][0] += this.employee[i][k][j];				
					tmpSum[i][j][1] += this.employee[i][k][j];	
				}
				for(k=3 ; k<7 ; k++){
					tmpSum[i][j][0] += this.employee[i][k][j];				
					tmpSum[i][j][2] += this.employee[i][k][j];	
				}
				for(k=7 ; k<31 ; k++){
					tmpSum[i][j][0] += this.employee[i][k][j];				
					tmpSum[i][j][3] += this.employee[i][k][j];	
				}
				for(k=31 ; k<38 ; k++){
					tmpSum[i][j][0] += this.employee[i][k][j];				
					tmpSum[i][j][4] += this.employee[i][k][j];	
				}
				for(k=38 ; k<64 ; k++){
					tmpSum[i][j][0] += this.employee[i][k][j];				
					tmpSum[i][j][5] += this.employee[i][k][j];	
				}
				for(k=64 ; k<68 ; k++){
					tmpSum[i][j][0] += this.employee[i][k][j];				
					tmpSum[i][j][6] += this.employee[i][k][j];	
				}
				for(k=68 ; k<this.n_industry ; k++){
					tmpSum[i][j][0] += this.employee[i][k][j];	
				}				
			}
		}
		
		for(i=0 ; i<this.n_region ; i++){
			if(this.locatoinName.get(i).contains("서울시")){
				for(j=0 ; j<this.duration ; j++){
					for(k=0 ; k<7 ; k++){
						seoulSum[j][k] += tmpSum[i][j][k];
					}
				}
			}
			else if(this.locatoinName.get(i).contains("경기도")){
				for(j=0 ; j<this.duration ; j++){
					for(k=0 ; k<7 ; k++){
						kkSum[j][k] += tmpSum[i][j][k];
					}
				}
			}
		}
		
		for(i=0 ; i<this.n_region ; i++){
			if(this.locatoinName.get(i).contains("충주시")){
				System.out.println();
				System.out.println(this.locatoinName.get(i));			
				
				for(j=0 ; j<7 ; j++){
					System.out.print(j+"\t");
					for(k=0 ; k<this.duration ; k++) System.out.print(tmpSum[i][k][j]+"\t");
					System.out.println();
				}				
			}			
		}

		System.out.println();
		System.out.println("서울시");			
		
		for(j=0 ; j<7 ; j++){
			System.out.print(j+"\t");
			for(k=0 ; k<this.duration ; k++) System.out.print(seoulSum[k][j]+"\t");
			System.out.println();
		}	

		System.out.println();
		System.out.println("경기도");			
		
		for(j=0 ; j<7 ; j++){
			System.out.print(j+"\t");
			for(k=0 ; k<this.duration ; k++) System.out.print(kkSum[k][j]+"\t");
			System.out.println();
		}
		
	}
	
	public void printEmployeeData(String outputFile){
		
		int i, j, k;	
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			


			for(i=0 ; i<this.n_region ; i++){
				pw.println(this.locatoinName.get(i));
				pw.print("Industry\t");
				for(i=0 ; i<this.duration ; i++) pw.print((i+this.startYear)+"\t");
				pw.println();				
				
				for(j=0 ; j<this.n_industry ; j++){
					pw.print(this.industryName.get(j)+"\t");
					for(k=0 ; k<this.duration ; k++) pw.print(this.employee[i][j][k]);
					pw.println();
				}
			}
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
	
	public static void main(String[] args) {
		
		String filePath = "/Users/jml/Desktop/Research/data_storage/company/";
		String microdataFile = "_microdata.txt";
		String locationCodeFile = filePath+"location_code/location_code.txt";
		String industryCodeFile = filePath+"industry_code/industry_code.txt";
		String employeeFile = "/Users/jml/Desktop/Research/data_storage/employee/employee.txt";
		
		int start = 1998;
		int end = 2010;
		
		int industryClass = 2;		//1: 대분류,  2: 중분류,   3: 소분류,   4: 세분류  
		int regionClass = 5;	    //2: 시도,    5: 시군구,   7: 읍면동
		
		EmployeeDataExtractor ede = new EmployeeDataExtractor(start, end, industryClass, regionClass);
			
		System.out.print("location code reading: ");
		ede.readLocationCode(locationCodeFile);
		System.out.println("complete");

		System.out.print("industry code reading: ");
		ede.readIndustryCode(industryCodeFile);
		System.out.println("complete");

		System.out.print("variables initializing: ");
		ede.initiate();
		System.out.println("complete");
		
		System.out.print("employee data reading: ");
		//ede.readEmployee(filePath, microdataFile);
		ede.readEmployee(employeeFile);
		System.out.println("complete");
				
		ede.calculateEmployeeData();
		
		/*
		System.out.print("employee data printing: ");
		ede.printEmployeeData(employeeFile);
		System.out.println("complete");	
			*/
		System.out.println("process complete.");

	}

}
