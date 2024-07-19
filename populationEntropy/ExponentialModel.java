package populationEntropy;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import populationEntropy.data.*;


public class ExponentialModel {

	int startYear, endYear;
	int duration;
	int forwardSteps;
	int span;	
	
	double[] ramda;			//[district]
	double[][] predict;		//[district][forwardSteps]
	
	public ExponentialModel(int start, int end){
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;
	}
	
	public ExponentialModel(int period, PopulationData pData){
		this.duration = period;
		this.startYear = pData.getStartYear();
		this.endYear = this.startYear + period - 1;
		
	}	
	
	public void calculateGrowthRate(PopulationData pData){
	
		int i, j;
		double span;
		
		this.ramda = new double[pData.getDistricNumber()];
		
		for(i=0 ; i<pData.getDistricNumber() ; i++){
			this.ramda[i] = 0.0;
			span = (double)this.duration - 1.0;
			for(j=0 ; j<(this.duration - 1) ; j++){
				if(pData.getPopulation((j+1), i) > 0 && pData.getPopulation(j, i) > 0)
					this.ramda[i] += ( (double)pData.getPopulation((j+1),i) / (double)pData.getPopulation(j,i) );
				else span -= 1.0;				
			}
			this.ramda[i] /= span; 
			this.ramda[i] -= 1.0; 
		}
	}
	
	public void predictPopulation(int forward, PopulationData pData){
		
		int i, j;
		
		this.forwardSteps = forward;
		this.predict = new double[pData.getDistricNumber()][forwardSteps];
		
		for(i=0 ; i<pData.getDistricNumber() ; i++){
			predict[i][0] = (double)pData.getPopulation((this.duration - 1), i) * Math.exp(this.ramda[i]);
			for(j=1 ; j<forward ; j++) predict[i][j] = predict[i][j-1] * Math.exp(this.ramda[i]);
		}
				
	}
	
	
	public void printPrediction(String outputFile, PopulationData pData){
		
		int i, j;
		double error, errorRate, meanError, meanErrorRate;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("district\t ramda\t");			
			pw.print("predicted\t");
			for(i=0 ; i<this.forwardSteps ; i++) pw.print((this.endYear+i+1)+"\t");
			pw.print("observed\t");
			for(i=0 ; i<this.forwardSteps ; i++) pw.print((this.endYear+i+1)+"\t");
			pw.print("error\t");
			for(i=0 ; i<this.forwardSteps ; i++) pw.print((this.endYear+i+1)+"\t\t");
			pw.println("average");
			
			for(i=0 ; i<pData.getDistricNumber() ; i++){
				meanError = 0.0;
				meanErrorRate = 0.0;
				pw.print(pData.getDistrictName(i)+"\t"+ramda[i]+"\t\t");
				for(j=0 ; j<this.forwardSteps ; j++) pw.print(predict[i][j]+"\t");
				pw.print("\t");
				for(j=0 ; j<this.forwardSteps ; j++) pw.print(pData.getPopulation((this.duration+j), i)+"\t");
				pw.print("\t");
				for(j=0 ; j<this.forwardSteps ; j++){
					error = Math.abs( pData.getPopulation((this.duration+j), i) - predict[i][j] );
					errorRate = error / pData.getPopulation((this.duration+j), i); 
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
	
	
	public static void main(String[] args) {
		
		int start = 1995;
		int end = 2005;
		int forward = 5;
		
		String filePath = "/Users/jml/Desktop/Research/data_storage/population/model_test/";
		String populatoinFile = filePath + "population_nation.txt";
		String predictionResultFile = filePath + "exponentialModel_"+start+"_"+end+"_"+forward+"_"+".txt";
		
		
		PopulationData pData = new PopulationData();
		DataReader dr = new DataReader();	
		ExponentialModel em = new ExponentialModel(start, end);
		
		System.out.print("data reading: ");
		dr.readData(populatoinFile, pData);
		System.out.println("complete");
		
		System.out.print("ramda calculating: ");
		em.calculateGrowthRate(pData);
		System.out.println("complete");
		
		System.out.print("population predicting: ");
		em.predictPopulation(forward, pData);
		System.out.println("complete");
						
		System.out.print("predected data printing: ");
		em.printPrediction(predictionResultFile, pData);
		System.out.println("complete");		
		
		System.out.println();
		System.out.println("[process complete]");

	}

}
