package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import populationEntropy.data.*;

public class EntropyCalculator {
	
	public EntropyCalculator(){
		
	}
	
	public void calculateAverage(PopulationData dataSet){
		int i, j, k;
		double tmpSum;
		
		for(i=0 ; i<dataSet.getNumberOfYears() ; i++){
			for(k=0  ; k<dataSet.getClassNumber() ; k++){
				tmpSum = 0.0;
				for(j=0 ; j<dataSet.getDistricNumber() ; j++)
					if(dataSet.getPopulation(i, j, k) > 0) tmpSum += dataSet.getPopulation(i, j, k);
				dataSet.setAveragePopulation(i, k, tmpSum / dataSet.getDistricNumber());
			}
		}
	}
	
	public void calculateStandardDeviation(PopulationData dataSet){
		int i, j, k;
		double tmpVariance, tmpAverage;
		
		for(i=0 ; i<dataSet.getNumberOfYears() ; i++){
			for(k=0  ; k<dataSet.getClassNumber() ; k++){
				tmpVariance = 0.0;
				tmpAverage = dataSet.getAveragePopulation(i, k);
				for(j=0 ; j<dataSet.getDistricNumber() ; j++)
					if(dataSet.getPopulation(i, j, k) >= 0) 
						tmpVariance += Math.pow(dataSet.getPopulation(i, j, k) - tmpAverage, 2.0);
				tmpVariance /= dataSet.getDistricNumber();
				
				dataSet.setStdPopulation(i, k, Math.sqrt(tmpVariance));
			}
		}
	}
	
	public void calculateAgedIndex(PopulationData dataSet){
		int i, j, k;
		int youthIndex = 3;
		int agedIndex = 14;
		double tmpYouth, tmpAged, tmpPopulation;
		double tmpAgedIndex;
		
		for(i=0 ; i<dataSet.getNumberOfYears() ; i++){
			for(j=0  ; j<dataSet.getDistricNumber() ; j++){
				tmpYouth = 0.0;
				tmpAged = 0.0;
				if(dataSet.getPopulation(i, j)>0) tmpPopulation = dataSet.getPopulation(i, j);
				else tmpPopulation = -1;
				
				for(k=1 ; k<=youthIndex ; k++)
					if(dataSet.getPopulation(i, j, k)>0) tmpYouth += dataSet.getPopulation(i, j, k);
				for(k=agedIndex ; k<dataSet.getClassNumber() ; k++) 
					if(dataSet.getPopulation(i, j, k)>0) tmpAged += dataSet.getPopulation(i, j, k);
						
				if(tmpPopulation > 0 && tmpYouth >0)  tmpAgedIndex = tmpAged/tmpYouth;
				else if(tmpPopulation <= 0) tmpAgedIndex = -1.0;
				else if(tmpYouth <= 0 && tmpAged <=0) tmpAgedIndex = -2.0;
				else if(tmpYouth <= 0 && tmpAged > 0) tmpAgedIndex = -3.0;
				else tmpAgedIndex = -5.0;
				
				dataSet.setAgedRatio(i, j, tmpAgedIndex);
			}	
		}
	}
	
	public void calculateProbability(PopulationData dataSet){		
		int i, j, k;
		double total;
		double population;
		double probability;
		double sum;
		
		for(i=0 ; i<dataSet.getDistricNumber() ; i++){					
			for(j=0 ; j<dataSet.getNumberOfYears() ; j++){
				sum = 0;
				total = (double) dataSet.getPopulation(j, i);
				for(k=1 ; k<dataSet.getClassNumber() ; k++){
					population = (double) dataSet.getPopulation(j, i, k);
					
					if(population > 0){
						probability = population / total;
						sum += probability;
					}
					else probability = -1.0;
					
					dataSet.setProbability(j, i, k, probability);
				}
				dataSet.setProbability(j, i, 0, sum);	
			}
		}		
	}
	
	public void setMinMaxPopulation(PopulationData pData){
		int i, j, k;
		int min, max;
		
		for(i=0 ; i<pData.getNumberOfYears() ; i++){	 
			for(k=0 ; k<pData.getClassNumber() ; k++){
				max = 0;
				for(j=0 ; j<pData.getDistricNumber() ; j++) 
					if(max < pData.getPopulation(i, j, k)) max = pData.getPopulation(i, j, k);
				min = max;
				for(j=0 ; j<pData.getDistricNumber() ; j++) 
					if(pData.getPopulation(i, j, k)>0 && min>pData.getPopulation(i, j, k)) 
						min = pData.getPopulation(i, j, k);
				
				pData.setMinPopulation(i, k, min);
				pData.setMaxPopulation(i, k, max);
			}
		}		
	}
	
	public void calculateEntropy(PopulationData pData){
		int i, j, k;
		double probability;
		double subEntropy;
		double entropy;
		double base = Math.log(2);
		double max, min;			
		
		for(i=0 ; i<pData.getNumberOfYears() ; i++){
			max = 0;				//possible minimum entropy
			min = 10000;		//possible maximum entropy
			for(j=0 ; j<pData.getDistricNumber() ; j++){
				entropy = 0.0;
				for(k=1 ; k<pData.getClassNumber() ; k++){
					probability = pData.getProbability(i, j, k);
					
					if(probability > 0) subEntropy = -1.0 * probability * Math.log(probability)/base;
					else if(probability == 0) subEntropy = 0.0;
					else subEntropy = -1.0;
					
					if(subEntropy > 0.0) entropy += subEntropy;
					pData.setSubEntropy(i, j, k, subEntropy);
				}
				
				if(max < entropy) max = entropy;
				else if(entropy > 0 && min > entropy) min = entropy;
				
				pData.setSubEntropy(i, j, 0, entropy);
				pData.setEntropy(i, j, entropy);
			}
			pData.setMaxEntropy(i, max);
			pData.setMinEntropy(i, min);	
		}		
	}
	
	public void calculateAgeWeightedEntropy(PopulationData pData){
		
		int i, j, k;
		double probability;
		double subEntropy;
		double entropy;
		double base = Math.log(2);
		double max, min;			
		double ageClassSum;
		
		for(i=0 ; i<pData.getNumberOfYears() ; i++){
			max = 0;		//possible minimum entropy
			min = 10000;	//possible maximum entropy
			for(j=0 ; j<pData.getDistricNumber() ; j++){
				entropy = 0;
				ageClassSum = 0;
				for(k=1 ; k<pData.getClassNumber() ; k++){
					probability = pData.getProbability(i, j, k);
					
					if(probability > 0) subEntropy = -1.0 * probability * Math.log(probability)/base;
					else if(probability == 0) subEntropy = 0.0;
					else subEntropy = -1.0;
					
					if(pData.getPopulation(i, j, k) >=0 ) ageClassSum += (double) (k * pData.getPopulation(i, j, k));
					if(subEntropy > 0) entropy += subEntropy;
					pData.setSubEntropy(i, j, k, subEntropy);
				}
				
				if(pData.getPopulation(i, j) > 0)	entropy *= ageClassSum / pData.getPopulation(i, j);
				
				if(max < entropy) max = entropy;
				else if(entropy > 0 && min > entropy) min = entropy;
				
				pData.setSubEntropy(i, j, 0, entropy);
				pData.setEntropy(i, j, entropy);
				pData.setMaxEntropy(i, max);
				pData.setMinEntropy(i, min);				
			}
		}		
	}
	
	public void calculateClassEntropy(PopulationData pData){
		
		int i, j, k, l;
		double probability;
		double subEntropy;
		double entropy;
		double weight;
		double base = Math.log(2);
		double max, min;			
		
		double normalizer = Math.pow((pData.getClassNumber() - 1.0), 2);
				
		/*** set entropy value ***/
		for(i=0 ; i<pData.getNumberOfYears() ; i++){
			for(j=0 ; j<pData.getDistricNumber() ; j++){
				pData.setClassEntropy(i, j, 0, pData.getEntropy(i, j));
			}
			pData.setMaxClassEntropy(i, 0, pData.getMaxEntropy(i));
			pData.setMinClassEntropy(i, 0, pData.getMinEntropy(i));
		}		
		
		/*** calculate class weighted entropy ***/
		for(i=0 ; i<pData.getNumberOfYears() ; i++){
			for(k=1 ; k<pData.getClassNumber() ; k++){
				max = 0;		//possible minimum entropy
				min = 10000;	//possible maximum entropy
				for(j=0 ; j<pData.getDistricNumber() ; j++){				
				
					entropy = 0;
					for(l=1 ; l<pData.getClassNumber() ; l++){
						
						weight = 1 + (Math.pow((k - l), 2) / normalizer);
						
						probability = pData.getProbability(i, j, l);
						
						if(probability > 0) subEntropy = -1.0 * weight * probability*Math.log(probability)/base;
						else if(probability == 0) subEntropy = 0;
						else subEntropy = -1.0;
						
						if(subEntropy > 0) entropy += subEntropy;
					}
					
					if(max < entropy) max = entropy;
					else if(entropy > 0 && min > entropy) min = entropy;
					
					pData.setClassEntropy(i, j, k, entropy);
				}
				
				pData.setMaxClassEntropy(i, k, max);
				pData.setMinClassEntropy(i, k, min);				
			}
		}		
	}
	
	public void proceedEntropyCalculation(PopulationData dataSet){
		this.calculateAverage(dataSet);
		this.calculateStandardDeviation(dataSet);
		this.calculateAgedIndex(dataSet);
		this.calculateProbability(dataSet);
		this.calculateEntropy(dataSet);
	}
	
	public void printProbability(String outputFile, PopulationData dataSet){
		
		int i,j,k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.print("district \t year \t sum \t");
			for(i=1 ; i<dataSet.getClassNumber() ; i++) pw.print(dataSet.getClassName(i)+"\t");
			pw.println();
		
			for(i=0 ; i<dataSet.getDistricNumber() ; i++){					
				for(j=0 ; j<dataSet.getNumberOfYears() ; j++){
					pw.print(dataSet.getDistrictName(i)+"\t");
					pw.print((dataSet.getStartYear()+j)+"\t");
					for(k=0 ; k<dataSet.getClassNumber() ; k++){
						pw.print(dataSet.getProbability(j, i, k)+"\t");
					}
					pw.println();
				}
			}
			pw.close();
		}catch(IOException e) {}	
	}
	
	
	public void printSubEntropy(String outputFile, PopulationData dataSet){
		
		int i,j,k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.print("district \t year \t entropy \t");
			for(i=0 ; i<dataSet.getClassNumber() ; i++) pw.print(dataSet.getClassName(i)+"\t");
			pw.println();
		
			for(i=0 ; i<dataSet.getDistricNumber() ; i++){					
				for(j=0 ; j<dataSet.getNumberOfYears() ; j++){
					pw.print(dataSet.getDistrictName(i)+"\t");
					pw.print((dataSet.getStartYear()+j)+"\t");
					for(k=0 ; k<dataSet.getClassNumber() ; k++){
						pw.print(dataSet.getSubEntropy(j, i, k)+"\t");
					}
					pw.println();
				}
			}
			pw.close();
		}catch(IOException e) {}	
	}
	
	
	public void printEntropy(String outputFile, PopulationData dataSet){
		
		int i,j;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.print("year");
			for(i=0 ; i<dataSet.getNumberOfYears() ; i++) pw.print("\t"+(dataSet.getYears()[i]));
			pw.println();
			pw.print("average_popualtion:");
			for(i=0 ; i<dataSet.getNumberOfYears() ; i++) pw.print("\t" + dataSet.getAveragePopulation(i));
			pw.println();
			pw.print("std_population:");
			for(i=0 ; i<dataSet.getNumberOfYears() ; i++) pw.print("\t" + dataSet.getStdPopulation(i));
			pw.println();
			
			pw.print("max_entropy:");
			for(i=0 ; i<dataSet.getNumberOfYears() ; i++) pw.print("\t" + dataSet.getMaxEntropy(i));
			pw.println();
			pw.print("min_entropy:");
			for(i=0 ; i<dataSet.getNumberOfYears() ; i++) pw.print("\t" + dataSet.getMinEntropy(i));
			pw.println();
			pw.println();
			
			pw.print("district");
			for(i=0 ; i<dataSet.getNumberOfYears() ; i++) pw.print("\t"+dataSet.getYears()[i]);
			pw.println();
		
			for(i=0 ; i<dataSet.getDistricNumber() ; i++){
				pw.print(dataSet.getDistrictName(i));
				for(j=0 ; j<dataSet.getNumberOfYears() ; j++) pw.print("\t"+dataSet.getEntropy(j, i));						
				pw.println();
			}
			pw.close();
		}catch(IOException e) {}	
	}
	
	
	public void printMigration(String outputFile, PopulationData dataSet){
		
		int i,j;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			/*
			pw.print("year");
			for(i=0 ; i<dataSet.getDuration() ; i++) pw.print("\t"+(dataSet.getStartYear()+i));
			pw.println();
			pw.print("max_entropy:");
			for(i=0 ; i<dataSet.getDuration() ; i++) pw.print("\t" + dataSet.getMaxEntropy(i));
			pw.println();
			pw.print("min_entropy:");
			for(i=0 ; i<dataSet.getDuration() ; i++) pw.print("\t" + dataSet.getMinEntropy(i));
			pw.println();
			pw.println();
			*/
			
			pw.println("district \t year \t migration_rate \t population \t entropy");
		
			for(i=0 ; i<dataSet.getDistricNumber() ; i++){
				for(j=0 ; j<dataSet.getNumberOfYears()-1 ; j++)
					//if(dataSet.getDistrictName(i).endsWith("구")==false){
					if(dataSet.getPopulation((j+1), i, 0)>0 && dataSet.getPopulation(j, i, 0)>0){					
						pw.println(dataSet.getDistrictName(i)+"\t"
								+(dataSet.getStartYear()+j)+"\t"
								+((double)(dataSet.getPopulation((j+1), i, 0)
										-(double)dataSet.getPopulation(j, i, 0))
										/(double)dataSet.getPopulation(j, i, 0)	)+"\t"
								+dataSet.getPopulation(j, i, 0)+"\t"
								+dataSet.getEntropy(j, i)+"\t");		
					}
					//}
			}
			
			pw.close();
		}catch(IOException e) {}	
	}
	
	public static void main(String[] args) {
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		//String inputFile = filePath + "population_nation.txt";
		String inputFile = filePath + "population_survey_data.txt";
		String probabilityFile = filePath + "probability_population_nation.txt";
		String subEntropyFile = filePath +  "SubEntropy_population_nation.txt";
		//String entropyFile = filePath +  "Entropy_population_nation.txt";
		String entropyFile = filePath +  "Entropy_population_output.txt";
		
		String migrationFile = filePath +  "Migration_population_nation.txt";	
		
		EntropyCalculator ec = new EntropyCalculator();
		DataReader dr = new DataReader();
		PopulationData dataSet = new PopulationData();	

		System.out.print("data reading: ");
		dataSet = dr.getPopulationData(inputFile);
		ec.calculateAverage(dataSet);
		ec.calculateStandardDeviation(dataSet);
		ec.calculateAgedIndex(dataSet);
		System.out.println("complete");
		
		System.out.print("probability calculating: ");
		ec.calculateProbability(dataSet);
		System.out.println("complete");
		
		System.out.print("entropy calculating: ");
		ec.calculateEntropy(dataSet);
		System.out.println("complete");
		
		/*
		System.out.print("probability printing: ");
		ec.printProbability(probabilityFile, dataSet);
		System.out.println("complete");
		*/
		
		System.out.print("entropy printing: ");
		ec.printSubEntropy(subEntropyFile, dataSet);
		ec.printEntropy(entropyFile, dataSet);
		System.out.println("complete");	
		
		/*
		System.out.print("migration printing: ");
		ec.printMigration(migrationFile, dataSet);
		System.out.println("complete");	
		*/
		
		System.out.println();
		System.out.println("process complete.");	
	}

}
