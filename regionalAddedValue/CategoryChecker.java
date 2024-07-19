package regionalAddedValue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CategoryChecker {

	int categoryDepth;
	ArrayList<String> standardList;
	HashMap<String, String> standardMap;
	
	public CategoryChecker(){
		
	}
	
	public void readAddedValueList(String inputFile){
		
		this.standardList = new ArrayList<String>();
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.nextLine();
			while(scan.hasNext()){
				this.standardList.add(scan.next());
				scan.nextLine();
			}
			scan.close();	
		} catch(IOException e) {}
	}

	public void checkCategories(int classificationDepth, String inpuFile){
		this.categoryDepth = classificationDepth;
		this.checkCategories(inpuFile);
	}
	
	public void checkCategories(String inputFile){
		int total = 0;
		int excluded = 0;
		String code;
		String category;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){
				code = scan.next();
				category = scan.next();
				
				if(code.length() == this.categoryDepth){
					total++;
					if(!this.standardList.contains(category)){
						excluded++;
						System.out.println("\t"+code+"\t"+category);
					}
				}
			}
			System.out.println("\ttotal "+total+"\texcluded:"+excluded);
			scan.close();	
		} catch(IOException e) {}
	}
	
	public static void main(String[] args) {
		String filePath ="/Users/Jemyung/Desktop/Research/data_storage/company/";
		String addedValueFile = filePath+"added_value/"+"added_value_"+"depth"+".txt";
		String industrycodeFile = filePath+"industry_code/"+"year"+"_industry_code.txt";

		int industryDepth = 5;

		String[] depth = {"1st", "2nd", "3rd", "4th", "5th"};
		int[] years = {1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012};

		CategoryChecker cc = new CategoryChecker();
		
		cc.readAddedValueList(addedValueFile.replace("depth", depth[industryDepth-1]));
		for(int i=0 ; i<years.length ; i++){
			System.out.print(years[i]);
			cc.checkCategories(industryDepth, industrycodeFile.replace("year", ""+years[i]));
		}
		
	}
}
