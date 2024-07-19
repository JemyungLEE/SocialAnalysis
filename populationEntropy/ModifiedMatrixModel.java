package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import populationEntropy.data.*;


public class ModifiedMatrixModel {
	
	int startYear, endYear;
	int duration;	
	int span;
	int steps;
	int forwardSteps;
	int districtNumber;
	int classNumber;
	
	double[] birth;				//[year]
	
	double[][][] survivalRate;	//[district][age_class][time_steps: 0 is average]
	double[][][] birthRate;		//[district][age_class][forward_steps]
	double[][][] fertilityRate;	//[district][age_class][forward_steps]
	double[][][] predict;		//[district][age_class:0 is total][forward_steps]
	
	public ModifiedMatrixModel(){
		
	}
	
	public ModifiedMatrixModel(int start, int end, int span, PopulationData pData){
		this.initate(start, end, span, pData);
	}

	public ModifiedMatrixModel(int start, int end, int span, int forward_steps, PopulationData pData){
		this.initate(start, end, span, pData);
		this.preparePrediction(forward_steps);
	}
	
	public ModifiedMatrixModel(int period, int span, PopulationData pData){
		this.startYear = pData.getStartYear();
		this.endYear = this.startYear + period - 1;
		
		this.initate(this.startYear, this.endYear, span, pData);
	}		
	
	public void initate(int start, int end, int term, PopulationData pData){
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;
		this.span = term;
		this.steps = (end - start) / term;
		this.districtNumber = pData.getDistricNumber();
		this.classNumber = pData.getClassNumber();
		
		this.survivalRate = new double[this.districtNumber][this.classNumber][this.steps+1];
	}
		
	public void preparePrediction(int forward_steps){
		this.forwardSteps = forward_steps;
		
		this.birthRate = new double[this.districtNumber][this.classNumber][this.forwardSteps];
		this.fertilityRate = new double[this.districtNumber][this.classNumber][this.forwardSteps];
		this.predict = new double[this.districtNumber][this.classNumber+1][this.forwardSteps];
	}
	
	/*
	public void readBornPopulation(String inputFile){
		
		int year;
		double bornPopulation;
		int count = 0;
		
		try{
			File file = new File(inputFile);			
			Scanner scan = new Scanner(file);		
			
			scan.next(); 
			scan.next();
			while(scan.hasNext()){
				year = scan.nextInt();
				bornPopulation = scan.nextDouble();
								
				if(year == this.startYear){
					this.birth[count] = bornPopulation;
					count++;
					year = scan.nextInt();
					bornPopulation = scan.nextDouble();
					while(year <= this.endYear){
						this.birth[count] = bornPopulation;
						
						if(scan.hasNext()){
							year = scan.nextInt();
							bornPopulation = scan.nextDouble();
							count++;			
						}else{
							year++;
						}
					}
				}else if(year > this.startYear){
					//not defined: it has to be defined for lack of data					
					
				}else if(year < this.startYear){
					//not defined: just pass
				}				
			}			
			scan.close();
			
		} catch(IOException e) {}
		
		// for error checking 
		//for(int i=0 ; i<this.duration ; i++) System.out.println((this.startYear+i)+"\t"+this.birth[i]);		
	}
	*/	

	public void calculateSurvivalRate(PopulationData pData){
		
		int i, j, k;
		int year_delay = this.startYear - pData.getStartYear();
		double current_population, next_population;
		double count;
		double subTotalBirth;
		
		for(i=0 ; i<this.districtNumber ; i++)
			for(j=0 ; j<this.classNumber ; j++) this.survivalRate[i][j][0] = 0.0;
				
		for(i=0 ; i<this.districtNumber ; i++){
			/*
			// calculating baby's survival rate: national rate	
			for(k=0 ; k<this.steps ; k++){	
				subTotalBirth = 0;
				for(j=0 ; j<this.span ; j++) subTotalBirth += this.birth[(this.span*k)+j];
				this.survivalRate[i][0][k+1] = (double)pData.getPopulation(((k*this.span)+year_delay),0,1)
												/subTotalBirth;
				this.survivalRate[i][0][0] += this.survivalRate[i][0][k+1] / (double)this.steps; 
			}		
			*/	
			
			for(j=0 ; j<(this.classNumber-1) ; j++){
				count = (double)this.steps;
				for(k=0 ; k<this.steps ; k++){
					current_population = (double) pData.getPopulation(((k*this.span)+year_delay), i, j);
					next_population = (double) pData.getPopulation((((k+1)*this.span)+year_delay), i, (j+1));
					if(next_population > 0 && current_population > 0){
						this.survivalRate[i][j][k+1] = next_population / current_population; 
						this.survivalRate[i][j][0] += this.survivalRate[i][j][k+1]; 
					}
					else count -= 1.0;		
				}
				if(count>0)	this.survivalRate[i][j][0] /= count;
			}					
		}		
	}

	
	public void calculateBirthRate(int type){
		//trend function predict from 1992
		int i, j, k;
		int year;
		int start_year = 1992;
		
		for(i=0 ; i<this.districtNumber ; i++)
			for(j=0 ; j<this.classNumber ; j++)
				for(k=0 ; k<this.forwardSteps ; k++) this.birthRate[i][j][k] = 0.0;
		
		if(type == 0){
			for(i=0 ; i<this.districtNumber ; i++){
				for(j=0 ; j<this.forwardSteps ; j++){
					//type 0 takes value of 2000
					this.birthRate[i][3][j] =   2.5 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][4][j] =  38.8 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][5][j] = 149.6 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][6][j] =  83.5 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][7][j] =  17.2 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][8][j] =   2.5 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][9][j] =   0.2 / 1000.0 * 0.5 * (double)this.span;					
					/*
					//type 0 takes value of 2005
					this.birthRate[i][3][j] =   2.1 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][4][j] =  17.8 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][5][j] =  91.7 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][6][j] =  81.5 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][7][j] =  18.7 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][8][j] =   2.4 / 1000.0 * 0.5 * (double)this.span;
					this.birthRate[i][9][j] =   0.2 / 1000.0 * 0.5 * (double)this.span;					
					*/
				}
			}
		}
		else if(type ==1){			
			for(i=0 ; i<this.districtNumber ; i++){
				for(j=0 ; j<this.forwardSteps ; j++){
					year = this.endYear - start_year + 1 + (this.span * j);
					//for 2000
					/*
					this.birthRate[i][3][j] =  5.08668 * Math.exp(-0.083317 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][4][j] = 89.26742 * Math.exp(-0.089332 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][5][j] =195.46186 * Math.exp(-0.032537 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][6][j] = 62.39841 * Math.exp( 0.024518 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][7][j] = 13.03826 * Math.exp( 0.027240 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][8][j] =  1.89921 * Math.exp( 0.032498 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][9][j] =  0.2 * (double)this.span;	
					*/
					//for 2005
					this.birthRate[i][3][j] =  4.57943 * Math.exp(-0.058825 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][4][j] =100.34102 * Math.exp(-0.117386 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][5][j] =213.55630 * Math.exp(-0.053590 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][6][j] = 63.85373 * Math.exp( 0.018728 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][7][j] = 13.19748 * Math.exp( 0.024077 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][8][j] =  2.03833 * Math.exp( 0.015646 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][9][j] =  0.2 * (double)this.span;
					/*
					//for 2010
					this.birthRate[i][3][j] =  4.47555 * Math.exp(-0.054236 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][4][j] = 90.48494 * Math.exp(-0.100399 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][5][j] =212.98316 * Math.exp(-0.053300 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][6][j] = 60.52888 * Math.exp( 0.027730 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][7][j] = 11.80465 * Math.exp( 0.042695 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][8][j] =  1.87293 * Math.exp( 0.029342 * year)/1000.0*0.5*(double)this.span;
					this.birthRate[i][9][j] =  0.2 * (double)this.span;
					*/
				}
			}
		}
		
	}
	
	public void calculateFertilityRate(){
		
		int i, j, k;
		
		for(i=0 ; i<this.districtNumber ; i++){			
			for(j=0 ; j<this.classNumber ; j++){
				for(k=0 ; k<this.forwardSteps ; k++){
					this.fertilityRate[i][j][k] = this.birthRate[i][j][k] * this.survivalRate[i][j][0];
			//		System.out.println(i+"\t"+j+"\t"+k+"\t"+this.birthRate[i][j][k]+"\t"+this.survivalRate[i][j][0]);
				}
			}
		}
	}
		
	public void preidctPopulation(PopulationData pData){
		
		int i, j, k;
		int predictionStart = this.startYear - pData.getStartYear() + this.duration - 1;
		
		for(i=0 ; i<this.districtNumber ; i++)
			for(j=0 ; j<this.classNumber+1 ; j++)
				for(k=0 ; k<this.forwardSteps ; k++) this.predict[i][j][k] = 0.0;
		
		for(i=0 ; i<this.districtNumber ; i++){			
			//for k=0
			for(j=1 ; j<this.classNumber ; j++){
				this.predict[i][1][0] += this.fertilityRate[i][j][0] * pData.getPopulation(predictionStart,i,j);				
				this.predict[i][j+1][0] = this.survivalRate[i][j-1][0]*pData.getPopulation(predictionStart,i,j);
				
			//	System.out.println(this.fertilityRate[i][j][0]+"\t"+pData.getPopulation(predictionStart, i, j));
			//	System.out.println(i+"\t"+j+"\t"+this.predict[i][1][0]+"\t"+this.predict[i][j+1][0]);
			}
			
			for(k=1 ; k<this.forwardSteps ; k++){
				for(j=1 ; j<this.classNumber ; j++){	
					this.predict[i][1][k] += this.fertilityRate[i][j][k] * this.predict[i][j][k-1];				
					this.predict[i][j+1][k] = this.survivalRate[i][j-1][0] * this.predict[i][j-1][k-1];	
					
				}				
			}
			for(k=0 ; k<this.forwardSteps ; k++)
				for(j=0 ; j<this.classNumber ; j++)	this.predict[i][0][k] += this.predict[i][j+1][k];
			
			//for(k=0 ; k<this.forwardSteps ; k++) System.out.println(i+"\t"+this.predict[i][0][k]);
		}
		
	}
	
	public void printPredictionResult(String outputFile, PopulationData pData){
		
		int i, j;
		int delayedYear = this.startYear - pData.getStartYear();
		
		int observedPopulation;
		double error, errorRate, meanError, meanErrorRate;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("district\t ");			
			pw.print("predicted\t");
			for(i=0 ; i<this.forwardSteps ; i++) pw.print((this.endYear+((i+1)*this.span))+"\t");
			pw.print("observed\t");
			for(i=0 ; i<this.forwardSteps ; i++) pw.print((this.endYear+((i+1)*this.span))+"\t");
			pw.print("error\t");
			for(i=0 ; i<this.forwardSteps ; i++) pw.print((this.endYear+((i+1)*this.span))+"\t\t");
			pw.println("average");
			
			for(i=0 ; i<this.districtNumber ; i++){
				meanError = 0.0;
				meanErrorRate = 0.0;
				pw.print(pData.getDistrictName(i)+"\t\t");
				for(j=0 ; j<this.forwardSteps ; j++) pw.print(predict[i][0][j]+"\t");
				pw.print("\t");
				for(j=0 ; j<this.forwardSteps ; j++) 
					pw.print(pData.getPopulation((delayedYear + this.duration + ((j+1)*this.span) - 1),i)+"\t");
				pw.print("\t");
				for(j=0 ; j<this.forwardSteps ; j++){
					observedPopulation = pData.getPopulation((delayedYear+this.duration+((j+1)*this.span)-1),i);
					error = Math.abs( observedPopulation - predict[i][0][j] );
					errorRate = error / observedPopulation; 
					meanError += error / forwardSteps;
					meanErrorRate += errorRate / forwardSteps;
					pw.print(error +"\t" + errorRate + "\t");					
				}
				pw.print(meanError +"\t" + meanErrorRate + "\t");	
				pw.println();
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printSurvivalRate(String outputFile, PopulationData pData){
	
		int i, j, k;
				
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		

			
			for(i=0 ; i<this.districtNumber ; i++){
				pw.print(pData.getDistrictName(i)+"\t");				
				pw.print("average\t");	
				for(k=0 ; k<this.steps ; k++) pw.print((this.startYear+((k+1)*this.span))+"\t");
				pw.println();
				
				for(j=0 ; j<this.classNumber ; j++){
					pw.print(pData.getClassName(j)+"\t");					
					for(k=0 ; k<this.steps+1 ; k++) pw.print(this.survivalRate[i][j][k]+"\t");
					pw.println();
				}					
				pw.println();
			}
			
			pw.close();
		}catch(IOException e) {}	
	}
	
	
	public static void main(String[] args) {
	
		int start = 1995;
		int end = 2005;
		int span = 5;
		int forward_steps = 1;
		
		String filePath = "/Users/jml/Desktop/Research/data_storage/population/model_test/";
		String populatoinFile = filePath + "population_nation.txt";
		String predictionResultFile = filePath+"ModifiedMatrixModel_"+start+"_"+end+"_"+(forward_steps*span)+".txt";
		String survivalRateFile = filePath+"ModifiedMatrixModel_survivalRate_"+start+"_"+end+".txt";
		
		PopulationData pData = new PopulationData();
		DataReader dr = new DataReader();	
		ModifiedMatrixModel mmm = new ModifiedMatrixModel();
		
		System.out.print("data reading: ");
		dr.readData(populatoinFile, pData);
		System.out.println("complete");
		
		System.out.print("initiating: ");
		mmm.initate(start, end, span, pData);
		mmm.preparePrediction(forward_steps);
		System.out.println("complete");		
		
		System.out.print("survival rates calculating: ");
		mmm.calculateSurvivalRate(pData);
		System.out.println("complete");
		
		System.out.print("birth rates calculating: ");
		mmm.calculateBirthRate(1);
		System.out.println("complete");

		System.out.print("fertility rates calculating: ");
		mmm.calculateFertilityRate();
		System.out.println("complete");
		
		System.out.print("population predicting: ");				
		mmm.preidctPopulation(pData);
		System.out.println("complete");
						
		System.out.print("predected data printing: ");
		mmm.printPredictionResult(predictionResultFile, pData);
		System.out.println("complete");		

		System.out.print("survival rates printing: ");
		mmm.printSurvivalRate(survivalRateFile, pData);
		System.out.println("complete");	
		
		System.out.println();
		System.out.println("[process complete]");

	}

}