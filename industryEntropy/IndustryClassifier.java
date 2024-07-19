package industryEntropy;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import industryEntropy.data.IndustryData;

public class IndustryClassifier {

	int industryClassDepth;
	int industryClassNumber;
	
	public IndustryClassifier(int depth, int classNum){
		int[] industClassKey = {2, 3, 4, 5};
		
		this.industryClassDepth = industClassKey[depth];	
		this.industryClassNumber = classNum;
	}
	
	public void readIndustryData(String inputFile, IndustryData iData){	
		int code;
		int index;
		String tmpCode;
		String tmpName;
		double value;

		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.next();
			scan.next();
			scan.next();
			
			index = 0;
						
			while(scan.hasNext()){
				scan.next();
				scan.next();	
				scan.nextDouble();
								
				index++;
			}				
					
			iData.initiate(this.industryClassNumber, index);
			
			scan.close();	
		} catch(IOException e) {
			System.err.println("industry profit reading error.");
		}
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.next();
			scan.next();
			scan.next();
			
			index = 0;
						
			while(scan.hasNext()){
				tmpCode = scan.next().trim();
				tmpName = scan.next().trim();	
				value = scan.nextDouble();
				code = Integer.parseInt(tmpCode);
				
				
				if(tmpCode.length() == this.industryClassDepth){		
					iData.setCode(index, code);
					iData.codeList.add(code);
					iData.setValue(index, value);
					iData.setTitle(index, tmpName);
				}		
				
				index++;
			}				
			
			scan.close();	
		} catch(IOException e) {
			System.err.println("industry profit reading error.");
		}			
	}

	public void setIndustryLevelSection(int classNum, IndustryData iData){
		int i, j;
		int industry = iData.getIndustryNumber();
		int check;
		double max, min;
		double interval;
		double tmpValue;
		
		
		/*** find max, min ***/
		max = iData.getValue(0);
		min = iData.getValue(0);
		for(i=1 ; i<industry ; i++){
			if(iData.getValue(i) > max) max = iData.getValue(i);
			if(iData.getValue(i) < min) min = iData.getValue(i);
		}
		iData.setMax(max);
		iData.setMin(min);
		
		/*** set section ***/		
		interval = (max-min)/(double)classNum;
		iData.setSection(0, min);
		for(i=0 ; i<classNum ; i++) iData.setSection(i+1, iData.getSection(i) + interval);
		
		/*** set industry profit-level list ***/
		for(i=0 ; i<industry ; i++){
			check = 0;
			tmpValue = iData.getValue(i);
			for(j=0 ; j<classNum ; j++){
				if(tmpValue >= iData.getSection(j) && tmpValue < iData.getSection(j+1)){
					iData.setLevel(i, j);
					iData.classList.get(j).add(iData.getCode(i));
					check++;
				}
			}
			if(check == 0) System.err.println("industry level check error: "+i+" not matched");
			else if(check >1) System.err.println("industry level check error: "+i+" over matched");
		}
		
	}
	

	public void setIndustryLevel(int classNum, IndustryData iData){
		int i, j;
		int industry = iData.getIndustryNumber();
		int check;
		double max, min;
		double interval;
		int tmpInt;
		double tmpDouble;
		int[] tmpCode = new int[industry];
		double[] tmpValue = new double[industry];
		
		/*** sorting ***/
		for(i=0 ; i<industry ; i++){
			tmpCode[i] = iData.getCode(i);
			tmpValue[i] = iData.getValue(i);
		}
		
		for(i=0 ; i<industry ; i++){
			for(j=0 ; j<i ; j++){
				if(tmpValue[i] > tmpValue[j]){
					tmpInt = tmpCode[i];
					tmpCode[i] = tmpCode[j];
					tmpCode[j] = tmpInt;
					tmpDouble = tmpValue[i];
					tmpValue[i] = tmpValue[j];
					tmpValue[j] = tmpDouble;
				}
			}
		}
		max = tmpValue[industry-1];
		min = tmpValue[0];
		
		/*** set industry level ***/
		interval = (double)industry/(double)classNum;
		for(i=0 ; i<classNum ; i++){
			for(j=(int)(i*interval) ; j<(int)((i+1)*interval) ; j++){
				iData.classList.get(i).add(tmpCode[j]);
			}
		}
		if(iData.classList.get(classNum-1).contains(tmpCode[industry-1])==false) 
			iData.classList.get(classNum-1).add(tmpCode[industry-1]);
		
		/*** check ***/
		for(i=0 ; i<industry ; i++){
			check = 0;
			for(j=0 ; j<classNum ; j++){
				if(iData.classList.get(j).contains(iData.getCode(i))) check++;
			}
			if(check == 0) System.err.println("industry level check error: "+i+" not matched");
			else if(check >1) System.err.println("industry level check error: "+i+" over matched");	
		}
	}
	
	public void printIndustryLevelSection(IndustryData iData){
		int i, j, k;
		
		for(i=0; i<iData.getClassNumber() ; i++){
			System.out.print((i+1)+" section: ");
			for(j=0 ; j<iData.classList.get(i).size() ; j++) 
				System.out.print("\t"+iData.classList.get(i).get(j));			
			System.out.println();
		}
		System.out.println();
		
		for(i=0; i<iData.getClassNumber() ; i++){
			System.out.print((i+1)+" section: ");
			for(j=0 ; j<iData.classList.get(i).size() ; j++){
				for(k=0 ; k<iData.getIndustryNumber() ; k++){
					if(iData.classList.get(i).get(j) == iData.getCode(k))
						System.out.println("\t"+iData.getValue(k));				
				}			
			}
		}		
	}
	
	public static void main(String[] args) {
		
		int classDepth = 3;
		int classNumber = 20;
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
		String classFile = filePath + "profit/industry_profit.txt";
		
		IndustryClassifier ic = new IndustryClassifier(classDepth, classNumber);
		IndustryData iData = new IndustryData();
		
		System.out.print("reading: ");
		ic.readIndustryData(classFile, iData);
		System.out.println("complete");
		
		System.out.print("leveling: ");
		ic.setIndustryLevel(classNumber, iData);
		System.out.println("complete");
		
		System.out.println("printing");
		ic.printIndustryLevelSection(iData);
		System.out.println("complete");

	}

}
