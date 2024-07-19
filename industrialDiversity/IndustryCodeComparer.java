package industrialDiversity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class IndustryCodeComparer {
	
	public IndustryCodeComparer(){
	}
	
	public void initiateCodeList(ArrayList<ArrayList<String>> stdCode, 
											  ArrayList<ArrayList<String>> stdName){
		ArrayList<String> firstCode = new ArrayList<String>();
		ArrayList<String> secondCode = new ArrayList<String>();
		ArrayList<String> thirdCode = new ArrayList<String>();
		ArrayList<String> fourthCode = new ArrayList<String>();
		ArrayList<String> fifthCode = new ArrayList<String>();
		
		ArrayList<String> firstName = new ArrayList<String>();
		ArrayList<String> secondName  = new ArrayList<String>();
		ArrayList<String> thirdName = new ArrayList<String>();
		ArrayList<String> fourthName  = new ArrayList<String>();
		ArrayList<String> fifthName = new ArrayList<String>();
		
		stdCode.add(firstCode);
		stdCode.add(secondCode);
		stdCode.add(thirdCode);
		stdCode.add(fourthCode);
		stdCode.add(fifthCode);
		
		stdName.add(firstName);
		stdName.add(secondName);
		stdName.add(thirdName);
		stdName.add(fourthName);
		stdName.add(fifthName);
	}
	
	public void readCode(String codeFile, ArrayList<ArrayList<String>> stdCode, 
																			ArrayList<ArrayList<String>> stdName){
		int codeLength;
		String tmpCode, tmpName;
		
		try{
			File file = new File(codeFile);
			Scanner scan = new Scanner(file);
		
			scan.next();
			scan.next();
			
			while(scan.hasNext()){		
				tmpCode = scan.next();
				tmpName = scan.next();
				
				codeLength = tmpCode.length();
				stdCode.get(codeLength-1).add(tmpCode);
				stdName.get(codeLength-1).add(tmpName);
			}
			
			scan.close();	
		} catch(IOException e) {}	
	}
	
	public void compareCode(String outputFile, ArrayList<ArrayList<String>> stdCode, ArrayList<ArrayList<String>> stdName,
			ArrayList<ArrayList<String>> tmpCode, ArrayList<ArrayList<String>> tmpName){
		int i, j;
		int tmpIndex;
		int[] codeSize = new int[stdCode.size()];
		int[][] check = new int[stdCode.size()][stdCode.get(stdCode.size()-1).size()]; //0:absent, 1:accord, 2: different
		
		for(i=0 ; i<stdCode.size() ; i++) codeSize[i] = stdCode.get(i).size();
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			for(i=0 ; i<stdCode.size() ; i++){
				pw.println((i+1)+"-depth");
				for(j=0 ; j<codeSize[i] ; j++){
					if(tmpCode.get(i).contains(stdCode.get(i).get(j))){
						tmpIndex = tmpCode.get(i).indexOf(stdCode.get(i).get(j));
						if(stdName.get(i).get(j).equals(tmpName.get(i).get(tmpIndex))){
							check[i][j] = 1;
						}else{
							check[i][j] = 2;
							pw.println(stdCode.get(i).get(j)+"\t"+stdName.get(i).get(j)+"\t"+tmpCode.get(i).get(tmpIndex)+"\t"+tmpName.get(i).get(tmpIndex));
						}
					}else{
						check[i][j] = 0;
						pw.println(stdCode.get(i).get(j)+"\t"+stdName.get(i).get(j));
					}
				}
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
		ArrayList<ArrayList<String>> stdCodeList, stdNameList;
		ArrayList<ArrayList<String>> tmpCodeList, tmpNameList;
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/company/industry_code/";
		String codeFile, resultFile;
		
		codeFile = filePath+"industry_code.txt";
		
		IndustryCodeComparer icc = new IndustryCodeComparer();
		
		/*** read standard code ***/
		stdCodeList = new ArrayList<ArrayList<String>>();
		stdNameList = new ArrayList<ArrayList<String>>();
		icc.initiateCodeList(stdCodeList, stdNameList);
		icc.readCode(codeFile, stdCodeList, stdNameList);
		//icc.printCode(2, stdCodeList, stdNameList);
		
		/*** compare codes ***/
		for(i=start ; i<=end ; i++){
			codeFile = filePath+i+"_industry_code.txt";
			resultFile = filePath+"code_comparison/"+i+"_industryCode_comparedResult.txt";
			
			tmpCodeList = new ArrayList<ArrayList<String>>();
			tmpNameList = new ArrayList<ArrayList<String>>();
			
			icc.initiateCodeList(tmpCodeList, tmpNameList);
			icc.readCode(codeFile, tmpCodeList, tmpNameList);
			
			System.out.print("year: "+i+"\t");
			for(j=0 ; j<stdCodeList.size() ; j++) System.out.print("\t"+stdCodeList.get(j).size()+","+tmpCodeList.get(j).size());
			icc.compareCode(resultFile, stdCodeList, stdNameList, tmpCodeList, tmpNameList);
			System.out.println();
		}
	}

}
