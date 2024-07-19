package industryEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import companyDuration.data.*;


public class MicrodataAnalyzer {
	
	/**
	 *  Subject: Extract data code from  Micro-data file
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2012.9.10
	 *  Last Modified Data: 2012.10.29 
	 *  Department: Seoul Nat. Univ. depart. of Rural Systems Engineering
	 *  Description: Extract location code, founding year, industrial classification code, workers number,
	 *               type of business entity and type of store network
	 *               ; copr., co. ltd , ... & alone, main, sub, ... 
	 */
	
	int year;
	
	public MicrodataAnalyzer(){
		
	}
	
	public MicrodataAnalyzer(int year){
		this.year = year;
	}
	
	public void extractFoundingYear(String inputFile, ArrayList<Integer> foundingYears){
		
		int year;
		String tmpStr;
		
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){				
				tmpStr = scan.nextLine().substring(17, 21);
				if(tmpStr.contains(" ")==false){
					year = Integer.parseInt(tmpStr);
					if(year>1990 && year<2010)  foundingYears.add(year);
					//else System.err.println("no."+foundingYears.size()+", wrong year: "+year);
				}			
			}
			scan.close();	
		} catch(IOException e) {}	
	}
	
	public void makeFoundingYearDataFile(String outputFile, ArrayList<Integer> foundingYears){
				
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			for(int i=0 ; i<foundingYears.size() ; i++) pw.println(foundingYears.get(i));

			pw.close();
		}catch(IOException e) {}	
	}
	
	public void makeMicroCodeDataFile(String outputFile, ArrayList<MicroData> dataList){
		MicroData tmpMdata;			
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("Founing_Year\tLocation_Code\tIndustry_Code\tWorkers\tBusiness_Type\tStore_Type");
			for(int i=0 ; i<dataList.size() ; i++){
				tmpMdata = dataList.get(i);				
				pw.println(tmpMdata.getFoundedYear()+"\t"+tmpMdata.getLocationCode()+"\t"
							+tmpMdata.getIndustryCode()+"\t"+tmpMdata.getWorkers()+"\t"
							+tmpMdata.getBusinessType()+"\t"+tmpMdata.getStoreType());
			}

			pw.close();
		}catch(IOException e) {}		
	}
	
	public void extractDataCodes(String inputFile, ArrayList<MicroData> dataList){		
		//1999~2001: year(15,19), location(0,7), industry(7,13), workers(107,112), business(13,14), store(14,15)
		//2004~2011: year(15,19), location(0,7), industry(7,13), workers(107,112), business(13,14), store(14,15)
		//2003: year(17,21), location(0,7), industry(23,29), workers(199,209), business(15,16), store(16,17)
		//2002: year(16,20), location(0,7), industry(22,28), workers(113,118), business(14,15), store(15,16)
		//1998: year(17,21), location(0,7), industry(23,29), workers(114,119), business(14,15), store(16,17)
		
		int i;
		int tmpInt;
		int check[], count, errcount[];
		String tmpStr;
		String microdataCode;
		MicroData tmpMdata;		
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			count = 0;
			errcount = new int[7];
			check = new int[7];
			for(i=0 ; i< 7 ; i++) errcount[i] = 0;
			while(scan.hasNext()){	
				microdataCode = scan.nextLine().trim();
				tmpMdata = new MicroData();
				for(i=0 ; i<7 ; i++) check[i] = 0;
				
				//extract founding year
				tmpStr = microdataCode.substring(15, 19);
				if(tmpStr.contains(" ") == false){
					tmpInt = Integer.parseInt(tmpStr);
					if(tmpInt>1800 && tmpInt<=this.year) tmpMdata.setFoundedYear(tmpInt);
					else check[1]++;
				}else check[1]++;
				
				//extract location code
				tmpStr = microdataCode.substring(0, 7);
				if(tmpStr.contains(" ") == false){
					tmpInt = Integer.parseInt(tmpStr);
					tmpMdata.setLocationCode(tmpInt);
				}else check[2]++; 
				
				//extract industry code
				tmpStr = microdataCode.substring(7, 13);
				if(tmpStr.contains(" ") == false){
					tmpMdata.setIndustryCode(tmpStr);
				}else check[3]++;
				
				//extract workers number
				tmpStr = microdataCode.substring(107, 112).trim();
				if(tmpStr.contains(" ") == false){
					tmpInt = Integer.parseInt(tmpStr);
					tmpMdata.setWorkers(tmpInt);
				}else check[4]++;	

				//extract type of business entity
				tmpStr = microdataCode.substring(13, 14);
				if(tmpStr.contains(" ") == false){
					tmpInt = Integer.parseInt(tmpStr);
					if(tmpInt>0 && tmpInt<6){
						if(tmpInt==4) tmpMdata.setBusinessType(5);
						else if(tmpInt ==5) tmpMdata.setBusinessType(4);
						else tmpMdata.setBusinessType(tmpInt);
						//tmpMdata.setBusinessType(tmpInt);
					}				
					else{
				//		System.out.println("Business entity type error.");
						check[5]++;
					}
				}else check[5]++;				
				
				//extract type of store network 				
				tmpStr = microdataCode.substring(14, 15);
				if(tmpStr.contains(" ") == false){
					tmpInt = Integer.parseInt(tmpStr);
					if(tmpInt>0 && tmpInt<4){
						if(tmpInt==2) tmpMdata.setStoreType(3);
						else if(tmpInt==3) tmpMdata.setStoreType(2);
						else tmpMdata.setStoreType(tmpInt);
						//tmpMdata.setStoreType(tmpInt);	
					}
					else{
				//		System.out.println("Store nerwork type error.");
						check[6]++;
					}
				}else check[6]++;
								
				for(i=1 ; i<7 ; i++) if(check[i] != 0) check[0] = check[i];
				
				//add micro-data at data list
				
				if(check[0] == 0) dataList.add(tmpMdata);
				else for(i=0 ; i<7 ; i++) errcount[i] += check[i];
								
				count++;
			}	
			if(errcount[0]>0){
				System.out.println("error: "+errcount[0]+" damaged data in "+count+" data");
				for(i=1 ; i<7 ; i++) System.out.println("\ttype "+i+" error: "+errcount[i]);
			}
			scan.close();	
		} catch(IOException e) {}
	}
	
	
	public static void main(String[] args){		
		int year = 2011;
		String filePath;
		String microdataFile, microCodeFile;	
		ArrayList<MicroData> microdataList;
		MicrodataAnalyzer ma = new MicrodataAnalyzer();
		
		filePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";

		microdataFile = filePath+year+"_microdata.txt";
		
		ma = new MicrodataAnalyzer(year);
		microdataList = new ArrayList<MicroData>();
		microCodeFile =  filePath+"extracted/"+year+"_microdataCode.txt";
		System.out.println("process: extraction");
		ma.extractDataCodes(microdataFile, microdataList);
		System.out.println("process: file creation");
		ma.makeMicroCodeDataFile(microCodeFile, microdataList);	
		System.out.println("process complete.");
	}

}