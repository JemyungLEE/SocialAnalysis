package industrialDiversity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class RegionCodeComparer {
	
	
	public RegionCodeComparer(){
	}
	
	public void initiateCodeList(ArrayList<ArrayList<String>> stdCode, 
											  ArrayList<ArrayList<String>> stdName){
		ArrayList<String> firstCode = new ArrayList<String>();
		ArrayList<String> secondCode = new ArrayList<String>();
		ArrayList<String> thirdCode = new ArrayList<String>();
		
		ArrayList<String> firstName = new ArrayList<String>();
		ArrayList<String> secondName  = new ArrayList<String>();
		ArrayList<String> thirdName = new ArrayList<String>();
		
		stdCode.add(firstCode);
		stdCode.add(secondCode);
		stdCode.add(thirdCode);
		
		stdName.add(firstName);
		stdName.add(secondName);
		stdName.add(thirdName);
		
	}
	
	public void readStandardCode(String codeFile, HashMap<String, String> regionHashMap ){

		String tmpCode, tmpName;
		
		try{
			File file = new File(codeFile);
			Scanner scan = new Scanner(file);
		
			while(scan.hasNext()){		
				tmpCode = scan.next();
				tmpName = scan.next();
		
				regionHashMap.put(tmpCode,  tmpName);
			}
		
			scan.close();	
		} catch(IOException e) {}	
}
	
	public void readCode(String codeFile, ArrayList<ArrayList<String>> stdCode, 
																			ArrayList<ArrayList<String>> stdName){
		String tmpCode, tmpName;
		
		try{
			File file = new File(codeFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){		
				tmpCode = scan.next();
				tmpName = scan.next();
				
				if(tmpCode.length()==2){
					stdCode.get(0).add(tmpCode);
					stdName.get(0).add(tmpName);
				}else if(tmpCode.length()==5){
					stdCode.get(1).add(tmpCode);
					stdName.get(1).add(tmpName);
				}else if(tmpCode.length()==7){
					stdCode.get(2).add(tmpCode);
					stdName.get(2).add(tmpName);
				}
			}
			
			scan.close();	
		} catch(IOException e) {}	
	}
	
	public void compareCode(String outputFile, HashMap<String, String> regionHashMap,
								ArrayList<ArrayList<String>> tmpCode, ArrayList<ArrayList<String>> tmpName){
		int i, j;
		String comparedCode, comparedName;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			for(i=0 ; i<tmpCode.size() ; i++){
				pw.println(i+"_depth");
				for(j=0 ; j<tmpCode.get(i).size() ; j++){
					comparedCode =  tmpCode.get(i).get(j);
					comparedName = tmpName.get(i).get(j);
					if(regionHashMap.containsKey(comparedCode)){
						if(regionHashMap.get(comparedCode).equals(comparedName)){
							//same code & same name
						}else{
							//different name
							pw.println(comparedCode+"\t"+regionHashMap.get(comparedCode)+"\t"+comparedName+"\t[different name]");
						}
					}else if(regionHashMap.containsValue(comparedName)){
						//different code
						pw.println(comparedName+"\t"+comparedCode+"\t[different code]");
					}else{
						//not exist
						pw.println(comparedCode+"\t"+comparedName+"\t[not exist]");
					}
				}
				pw.println();
			}
		
			pw.close();
		}catch(IOException e) {}	
	}
	
	public void printCode(int printLevel, ArrayList<ArrayList<String>> stdCode, ArrayList<ArrayList<String>> stdName){
		
		int i, j;
		
		for(i=0 ; i<printLevel ; i++){
			System.out.println((i+1)+"-depth:"+stdCode.get(i).size());
			for(j=0 ; j<stdCode.get(i).size() ; j++) 
				System.out.println(stdCode.get(i).get(j)+"\t"+stdName.get(i).get(j));
			System.out.println();
		}
		
	}
	
	public static void main(String[] args) {
		int i,j;
		int start = 1998;
		int end = 2011;
		ArrayList<ArrayList<String>> tmpCodeList, tmpNameList;
		HashMap<String,String> regionHashMap = new HashMap<String,String>();
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/society/location_code/";
		String codeFile, compareFile, resultFile;
		
		codeFile = filePath+"2012_location_code.txt";
		
		RegionCodeComparer icc = new RegionCodeComparer();
		
		/*** read standard code ***/
		icc.readStandardCode(codeFile, regionHashMap);
		
		/*** compare codes ***/
		for(i=start ; i<=end ; i++){
			compareFile = filePath+i+"_location_code.txt";
			resultFile = filePath+"code_comparison/"+i+"_regionCode_comparedResult.txt";
			
			tmpCodeList = new ArrayList<ArrayList<String>>();
			tmpNameList = new ArrayList<ArrayList<String>>();
			
			icc.initiateCodeList(tmpCodeList, tmpNameList);
			icc.readCode(compareFile, tmpCodeList, tmpNameList);
			
			System.out.print(i+"\t");
			icc.compareCode(resultFile, regionHashMap, tmpCodeList, tmpNameList);
		}
		System.out.println();
	}

}
