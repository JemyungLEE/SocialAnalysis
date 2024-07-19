package companyDuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DurationEstimator {

	int startYear, endYear;
	ArrayList<ArrayList<Integer>> foundingData;
	int[][] arrangedData;
	int[][] durationData;
	int[][] closedData;
	int[][] survivalData;
	double[][] survivalRatio;	// = survival(t)/survival(t-1), t: time step
	
	public  DurationEstimator(){
		foundingData = new ArrayList<ArrayList<Integer>>(); 
	}

	public  DurationEstimator(int sYear, int eYear){
		this.startYear = sYear;
		this.endYear = eYear;
		foundingData = new ArrayList<ArrayList<Integer>>(); 
	}
	
	public void readYears(String filepath){		
		ArrayList<Integer> foundingYear;
		
		for(int i = this.startYear ; i<=this.endYear ; i++){	
			foundingYear = new ArrayList<Integer>();
			
			try{
				File file = new File(filepath+i+"_foundingYear.txt");
				Scanner scan = new Scanner(file);
					
				while(scan.hasNextInt()) 	foundingYear.add(scan.nextInt());
			
				scan.close();	
			} catch(IOException e) {}	
				
			foundingData.add(foundingYear);
		}
	}
	
	public void arrangeYears(){
		int i,j,k;
		int period = this.endYear-this.startYear+1;
		ArrayList<Integer> tmpYears;
		this.arrangedData = new int[period][period];
		this.survivalData = new int[period][period];
		
		for(i=0 ; i<period ; i++) for(j=0 ; j<period ; j++) this.arrangedData[i][j] = 0;
		
		for(i=0 ; i<period ; i++){
			for(j=0 ; j<=i ; j++){
				tmpYears = this.foundingData.get(i);
				for(k=0 ; k<tmpYears.size() ; k++)
					if(tmpYears.get(k) == this.startYear+j) this.arrangedData[i][j]++;				
			}
		}
		
		for(i=0 ; i<period ; i++) for(j=0 ; j<period ; j++) this.survivalData[i][j] = this.arrangedData[j][i];
	}
	
	public void calculateDuration(){
		int i,j;
		int period = this.endYear-this.startYear+1;
		this.durationData = new int[period][period+1];
		this.closedData = new int[period][period-1];
		this.survivalRatio = new double[period][period-1];
				
		for(i=0 ; i<period ; i++) for(j=0 ; j<period+1 ; j++) this.durationData[i][j] = 0;
		for(i=0 ; i<period ; i++) for(j=0 ; j<period-1 ; j++){
			this.closedData[i][j] = 0;
			this.survivalRatio[i][j] = 0.0;
		}
		
		for(i=0 ; i<period ; i++){
			this.durationData[i][0] = this.arrangedData[i][i];
			for(j=1 ; j<period-i ; j++)	this.durationData[i][j] = this.arrangedData[i+j-1][i] - this.arrangedData[i+j][i];
			this.durationData[i][period-i] = this.arrangedData[period-1][i];
		}
		
		for(i=0 ; i<period ; i++){
			for(j=i ; j<period-1 ; j++)	this.closedData[i][j] = this.survivalData[i][j] - this.survivalData[i][j+1];			
			for(j=i ; j<period-1 ; j++)	this.survivalRatio[i][j] = (double) this.survivalData[i][j+1] / (double) this.survivalData[i][j];
						
		}
	}

	public void printArrangedData(){
		int i,j;
		int period = this.endYear-this.startYear+1;
		
		for(i=0 ; i<period ; i++){
			System.out.print(this.startYear+i+"\t");
			for(j=0 ; j<=i ; j++){
				System.out.print(this.arrangedData[i][j]+"\t");
			}
			System.out.println();
		}			
	}
	
	public void printSurvivalData(){
		int i,j;
		int period = this.endYear-this.startYear+1;
	
		System.out.println("Survival Data:");
		for(i=0 ; i<period ; i++) System.out.print("\t"+(this.startYear+i));
		System.out.println();
		for(i=0 ; i<period ; i++){
			System.out.print((this.startYear+i)+"\t");
			for(j=0 ; j<i ; j++) System.out.print("\t");
			for(j=i ; j<period ; j++)	System.out.print(this.survivalData[i][j]+"\t");			
			System.out.println();
		}	
	}

	public void printSurvivalRatio(){
		int i,j;
		int period = this.endYear-this.startYear+1;
	
		System.out.println("Survival Ratio:");
		for(i=0 ; i<period-1 ; i++) System.out.print("\t"+(this.startYear+i+1));
		System.out.println();
		for(i=0 ; i<period-1 ; i++){
			System.out.print((this.startYear+i)+"\t");			
			for(j=0 ; j<i ; j++) System.out.print("\t");
			for(j=i ; j<period-1 ; j++)	System.out.print(this.survivalRatio[i][j]+"\t");			
			System.out.println();
		}	
	}
	
	public void printDuration(){
		int i,j;
		int period = this.endYear-this.startYear+1;
		
		System.out.println("Duration Data:");
		for(i=0 ; i<period ; i++) System.out.print("\t"+i);
		System.out.println("\tsurvival");
		for(i=0 ; i<period ; i++){
			System.out.print(this.startYear+i+"\t");
			for(j=0 ; j<period-i ; j++)	System.out.print(this.durationData[i][j]+"\t");
			for(j=0 ; j<i ; j++) System.out.print("\t");
			System.out.println(this.durationData[i][period-i]);
		}
	}
	
	public void printClosingData(){
		int i,j;
		int period = this.endYear-this.startYear+1;
		
		System.out.println("Closing Data:");
		for(i=0 ; i<period-1 ; i++) System.out.print("\t"+(this.startYear+i+1));
		System.out.println();
		for(i=0 ; i<period-1 ; i++){
			System.out.print((this.startYear+i)+"\t");
			for(j=0 ; j<i ; j++) System.out.print("\t");
			for(j=i ; j<period-1 ; j++) System.out.print(this.closedData[i][j]+"\t");			
			System.out.println();
		}		
	}
	
	public static void main(String[] args) {

		DurationEstimator ed = new DurationEstimator(1998,2008);
				
		System.out.print("process: read ...");
		ed.readYears("/Users/jml/Desktop/Research/temp/extracted/");
		System.out.println(" ok");
		System.out.print("process: arrangement ...");
		ed.arrangeYears();
		System.out.println(" ok");
		ed.printArrangedData();		
		System.out.print("process: calculation ...");
		ed.calculateDuration();
		System.out.println(" ok");
		ed.printSurvivalData();
		ed.printDuration();		
		ed.printClosingData();
		ed.printSurvivalRatio();
		System.out.println("process: completed");
	}

}
