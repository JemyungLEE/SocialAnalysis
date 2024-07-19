package companyDuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MicrodataIntegrator {

	public void integrateData(ArrayList<String> inputFile, String outputFile){
		File ifile, ofile;
		
		try{
			ofile = new File(outputFile);
			PrintWriter pw = new PrintWriter(ofile);	
			Scanner scan;
			
			for(int i=0 ; i<inputFile.size() ; i++){
				 ifile = new File(inputFile.get(i));
				 scan = new Scanner(ifile);
				
				while(scan.hasNext()) pw.println(scan.nextLine());		
				scan.close();
			}			
			pw.close();
		}catch(IOException e) {}			
	}
	
	public static void main(String[] args) {
		int year = 1998;
		String filePath = "/Users/jml/Desktop/Research/temp/";
		ArrayList<String> inputFile =  new ArrayList<String>();
		String outputFile = filePath+year+"_MicroData.txt";
		/*
		inputFile.add(filePath+year+"/"+"11(서울).txt");
		inputFile.add(filePath+year+"/"+"21(부산).txt");
		inputFile.add(filePath+year+"/"+"22(대구).txt");
		inputFile.add(filePath+year+"/"+"23(인천).txt");
		inputFile.add(filePath+year+"/"+"24(광주).txt");
		inputFile.add(filePath+year+"/"+"25(대전).txt");
		inputFile.add(filePath+year+"/"+"26(울산).txt");
		inputFile.add(filePath+year+"/"+"31(경기).txt");
		inputFile.add(filePath+year+"/"+"32(강원).txt");
		inputFile.add(filePath+year+"/"+"33(충북).txt");
		inputFile.add(filePath+year+"/"+"34(충남).txt");
		inputFile.add(filePath+year+"/"+"35(전북).txt");
		inputFile.add(filePath+year+"/"+"36(전남).txt");
		inputFile.add(filePath+year+"/"+"37(경북).txt");
		inputFile.add(filePath+year+"/"+"38(경남).txt");
		inputFile.add(filePath+year+"/"+"39(제주).txt");
		*/
		/*
		inputFile.add(filePath+year+"/"+"cb11.txt");
		inputFile.add(filePath+year+"/"+"cb21.txt");
		inputFile.add(filePath+year+"/"+"cb22.txt");
		inputFile.add(filePath+year+"/"+"cb23.txt");
		inputFile.add(filePath+year+"/"+"cb24.txt");
		inputFile.add(filePath+year+"/"+"cb25.txt");
		inputFile.add(filePath+year+"/"+"cb26.txt");
		inputFile.add(filePath+year+"/"+"cb31.txt");
		inputFile.add(filePath+year+"/"+"cb32.txt");
		inputFile.add(filePath+year+"/"+"cb33.txt");
		inputFile.add(filePath+year+"/"+"cb34.txt");
		inputFile.add(filePath+year+"/"+"cb35.txt");
		inputFile.add(filePath+year+"/"+"cb36.txt");
		inputFile.add(filePath+year+"/"+"cb37.txt");
		inputFile.add(filePath+year+"/"+"cb38.txt");
		inputFile.add(filePath+year+"/"+"cb39.txt");
		*/
		
		inputFile.add(filePath+year+"/"+"CB9811.txt");
		inputFile.add(filePath+year+"/"+"CB9821.txt");
		inputFile.add(filePath+year+"/"+"CB9822.txt");
		inputFile.add(filePath+year+"/"+"CB9823.txt");
		inputFile.add(filePath+year+"/"+"CB9824.txt");
		inputFile.add(filePath+year+"/"+"CB9825.txt");
		inputFile.add(filePath+year+"/"+"CB9826.txt");
		inputFile.add(filePath+year+"/"+"CB9831.txt");
		inputFile.add(filePath+year+"/"+"CB9832.txt");
		inputFile.add(filePath+year+"/"+"CB9833.txt");
		inputFile.add(filePath+year+"/"+"CB9834.txt");
		inputFile.add(filePath+year+"/"+"CB9835.txt");
		inputFile.add(filePath+year+"/"+"CB9836.txt");
		inputFile.add(filePath+year+"/"+"CB9837.txt");
		inputFile.add(filePath+year+"/"+"CB9838.txt");
		inputFile.add(filePath+year+"/"+"CB9839.txt");
		
		MicrodataIntegrator mi = new MicrodataIntegrator();
		mi.integrateData(inputFile, outputFile);
	}
}
