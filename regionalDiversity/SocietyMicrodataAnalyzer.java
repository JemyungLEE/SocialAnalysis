package regionalDiversity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import regionalDiversity.data.SocietyMicroData;

public class SocietyMicrodataAnalyzer {
	
	int n_region;
	int duration;
	int startYear, endYear;
	int regionClassDepth;		//2:si_do,    5: si_gun_gu,   7: eup_myun_dong
	
	HashMap<Integer, String> locationHashMap;	//<code, name>
	ArrayList<Integer> locatoinCode;			//use to find region's index	
	ArrayList<String> locatoinName;				//use to find region's index	
	
	
	double[][] lifeSatisfaction;		//[year][region]
	double[][] n_samples;					//[year][region]
	
	public SocietyMicrodataAnalyzer(){
		this.regionClassDepth = 2;
		this.startYear = 2009;
		this.endYear = 2010;
		this.duration = this.endYear - this.startYear + 1;
		
		this.setIndexMap();
	}
	
	public void setIndexMap(){
		
		this.locationHashMap = new HashMap<Integer, String>();	
		this.locatoinCode = new ArrayList<Integer>();		
		this.locatoinName = new ArrayList<String>();		
	}
	
	public void initiate(){
		int i, j;
		
		this.lifeSatisfaction = new double[this.duration][this.n_region];
		this.n_samples = new double[this.duration][this.n_region];
		
		for(i=0 ; i<this.duration ; i++){
			for(j=0 ; j<this.n_region ; j++){
				this.lifeSatisfaction[i][j] = 0.0;
				this.n_samples[i][j] = 0.0;
			}
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
		} catch(IOException e) {
			System.err.println("location code reading error.");
		}
			
	}
	
	public void extractSocieyMicrodata(String filePath, String fileName){
		//2009: region(7,9), life satisfaction(204,205): 1_very good, 5_very bad
		//2010: region(7,9), life satisfaction(20,21): 1_very good, 5_very bad
		
		int i, j;
		int check[], count, errcount[];
		
		int region = 0;
		int satisfaction = 0;
		
		int locationIndex;	
		
		String inputFile;
		String tmpStr;
		String microdataCode;		
		
		int[] locationStart = new int[this.duration];
		int[] locationEnd = new int[this.duration];
		int[] satisfactionStart = new int[this.duration];
		int[] satisfactionEnd = new int[this.duration];
		
		
		locationStart[0]=7; locationEnd[0]=9; satisfactionStart[0]=204;satisfactionEnd[0]=205;
		locationStart[1]=7; locationEnd[1]=9; satisfactionStart[1]=20; satisfactionEnd[1]=21;
		
		File file;
		Scanner scan;
		
		for(j=0 ; j <this.duration ; j++){
		
			System.out.print((this.startYear+j)+" ");
			inputFile = filePath+(this.startYear+j)+fileName;
			
			try{
				file = new File(inputFile);
				scan = new Scanner(file);
				
				count = 0;
				errcount = new int[3];
				check = new int[3];
				for(i=0 ; i< 3 ; i++) errcount[i] = 0;
				while(scan.hasNext()){	
					microdataCode = scan.nextLine();
					for(i=0 ; i<3 ; i++) check[i] = 0;
					
					//extract region code
					tmpStr = microdataCode.substring(locationStart[j], locationEnd[j]);
					if(tmpStr.contains(" ") == false) region = Integer.parseInt(tmpStr);
					else check[1]++; 
					
					//extract life satisfaction score
					tmpStr = microdataCode.substring(satisfactionStart[j], satisfactionEnd[j]);
					if(tmpStr.contains(" ") == false) satisfaction = Integer.parseInt(tmpStr);					
					else check[2]++;
																
					for(i=1 ; i<3 ; i++) if(check[i] != 0) check[0] = check[i];
					
					//add micro-data at data list
					if(check[0] == 0){
						if(this.locatoinCode.contains(region) && satisfaction>=1 && satisfaction<=5){
							locationIndex = this.locatoinCode.indexOf(region);
							
							if(satisfaction == 1) this.lifeSatisfaction[j][locationIndex] += 2.0;
							else if(satisfaction == 2) this.lifeSatisfaction[j][locationIndex] += 1.0;
							else if(satisfaction == 3) this.lifeSatisfaction[j][locationIndex] += 0.0;
							else if(satisfaction == 4) this.lifeSatisfaction[j][locationIndex] -= 1.0;
							else if(satisfaction == 5) this.lifeSatisfaction[j][locationIndex] -= 2.0;
							this.n_samples[j][locationIndex] += 1.0;
						}
					}
					else for(i=0 ; i<3 ; i++) errcount[i] += check[i];					
					count++;
				}	
				if(errcount[0]>0){
					System.out.println("error: "+errcount[0]+" damaged data in "+count+" data");
					for(i=1 ; i<3 ; i++) System.out.println("\ttype "+i+" error: "+errcount[i]);
				}
				scan.close();	
			} catch(IOException e) {
				System.err.println("microdata reagind error.");
			}		
		}			
	}
	
	public void calculateSocietyStatistics(){
		int i, j;
		
		for(i=0 ; i<this.duration ; i++){
			for(j=0 ; j<this.n_region ; j++){
				this.lifeSatisfaction[i][j] /= this.n_samples[i][j];
			}
		}
	}
	
	public void printLifeSatisfaction(String outputFile){		
		int i,j;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
					
			pw.print("n_samples");
			for(j=0 ; j<this.duration ; j++) pw.print("\t"+(this.startYear+j));
			pw.println();
			
			for(i=0 ; i<this.n_region ; i++){	
				pw.print(this.locatoinName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.n_samples[j][i]);			
				pw.println();
			}

			pw.println();
			pw.print("life_satisfaction");
			for(j=0 ; j<this.duration ; j++) pw.print("\t"+(this.startYear+j));
			pw.println();
			
			for(i=0 ; i<this.n_region ; i++){	
				pw.print(this.locatoinName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.lifeSatisfaction[j][i]);			
				pw.println();
			}
			
			pw.close();
		}catch(IOException e) {}
		
	}
	
	public static void main(String[] args) {
		
		String filePath = "/Users/jml/Desktop/Research/data_storage/society/";
		String locationCodeFile = filePath+"location_code/location_code.txt";
		String outputFile = filePath+"life_satisfaction.txt";		
		
		SocietyMicrodataAnalyzer sma = new SocietyMicrodataAnalyzer();
		
		System.out.print("location code reading: ");
		sma.readLocationCode(locationCodeFile);
		System.out.println("complete");

		System.out.print("variables initializing: ");
		sma.initiate();
		System.out.println("complete");
		
		System.out.print("microdata reading: ");
		sma.extractSocieyMicrodata(filePath+"microdata/", "_Society_MicroData.txt");
		sma.calculateSocietyStatistics();
		System.out.println("complete");	
		
		System.out.print("data printing: ");
		sma.printLifeSatisfaction(outputFile);
		System.out.println("complete");	
				
		System.out.println("process complete.");

	}

}
