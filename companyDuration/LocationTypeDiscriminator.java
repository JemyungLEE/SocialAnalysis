package companyDuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import companyDuration.data.*;

public class LocationTypeDiscriminator {

	/**
	 *  Subject: Location type analyzer
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2012.9.10
	 *  Last Modified Data: 2012.10.29 
	 *  Department: Seoul Nat. Univ. depart. of Rural Systems Engineering
	 *  Description: Distinguish location type from location code-name list, dong(0)/eup(1)/myun(2) area
	 */
	
	int sortOfLocationType;
	ArrayList<Integer> locationCode;
	ArrayList<String> locationName;	
	ArrayList<ArrayList<Integer>> locationType;
	
	
	public LocationTypeDiscriminator(){
		this.initiate();
	}
	
	public LocationTypeDiscriminator(CompanyStatistic cstat){
		this.initiate(cstat);
	}	
	
	public void initiate(CompanyStatistic cstat){
		this.sortOfLocationType = cstat.getSortOfLocationType();
		this.locationType = new ArrayList<ArrayList<Integer>>();
		this.locationCode = new ArrayList<Integer>();
		this.locationName = new ArrayList<String>();
		for(int i=0 ; i<this.sortOfLocationType ; i++)	this.locationType.add(new ArrayList<Integer>());
	}

	public void initiate(){
		//set default location type: 0(dong) / 1(eup) / 2(myun)
		this.sortOfLocationType = 3;
		this.locationType = new ArrayList<ArrayList<Integer>>();
		this.locationCode = new ArrayList<Integer>();
		this.locationName = new ArrayList<String>();
		for(int i=0 ; i<this.sortOfLocationType ; i++)	this.locationType.add(new ArrayList<Integer>());
	}
	
	public void readLocationIndex(String locationIndexFile){
						
		try{
			File file = new File(locationIndexFile);
			Scanner scan = new Scanner(file);
		
			while(scan.hasNext()){				
				this.locationCode.add(scan.nextInt());
				this.locationName.add(scan.next());
				if(scan.hasNext() && scan.hasNextInt()==false) scan.next();
			}
		
			scan.close();	
		} catch(IOException e) {}	
	}
	
	public void discriminate(String locationIndexFile){
		this.readLocationIndex(locationIndexFile);
		this.discriminate();
	}

	public void discriminate(){
		//set default location type: 0(dong) / 1(eup) / 2(myun)
		int i,j;
		String[] indexingWord = new String[3];
		indexingWord[0] = "동";
		indexingWord[1] = "읍";
		indexingWord[2] = "면";
		
		for(i=0 ; i<this.locationCode.size() ; i++)
			for(j=0 ; j<3 ; j++)
				if(this.locationName.get(i).endsWith(indexingWord[j])) 
					this.locationType.get(j).add(this.locationCode.get(i));		
	}
	
	public ArrayList<ArrayList<Integer>> getLocationTypeList(){
		return this.locationType;
	}
	
	public void printLocationType(){
		int i,j;
		String[] indexingWord = new String[3];
		indexingWord[0] = "동";
		indexingWord[1] = "읍";
		indexingWord[2] = "면";
		int code;
		
		for(i=0 ; i<3 ; i++){
			System.out.println(indexingWord[i]);
			for(j=0 ; j<this.locationType.get(i).size() ; j++){
				code = this.locationType.get(i).get(j);
				System.out.print("type: "+i+", "+code);
				System.out.println(", "+this.locationName.get(this.locationCode.indexOf(code)));
			}
			System.out.println();
		}
	}
	
	public void makeLocationTypeFile(String outputFile){
		int i,j;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			for(i=0 ; i<3 ; i++)
				for(j=0 ; j<this.locationType.get(i).size() ; j++)
					pw.println(this.locationType.get(i).get(j)+"\t"+i);
			
			System.out.println(this.locationType.get(0).size()+" dong, "
								+this.locationType.get(1).size()+" eup, "
								+this.locationType.get(2).size()+" myun");
			pw.close();
		}catch(IOException e) {}	
	}
	
	public static void main(String[] args) {
		int year;
		for(year=2009 ; year<=2010 ; year++){
			System.out.println("year: "+year);
			
			String filePath = "/Users/jml/Desktop/Research/temp/location_code/";
			String fileName = year+"_location_code.txt";
			String locationTypeFile = year+"_location_type.txt";
			
			System.out.println("process: initiating");
			LocationTypeDiscriminator ltd = new LocationTypeDiscriminator();
			System.out.println("process: file reading");
			ltd.readLocationIndex(filePath+fileName);
			System.out.println("process: discrimination");
			ltd.discriminate();
			//System.out.println("process: printing...");
			//ltd.printLocationType();
			System.out.println("process: file creation");
			ltd.makeLocationTypeFile(filePath+locationTypeFile);
			System.out.println("process: complete");
		}
		
	}

}
