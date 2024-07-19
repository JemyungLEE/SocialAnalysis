package populationEntropy;

import java.util.ArrayList;

import Jama.Matrix;
import populationEntropy.data.Chromosome;

public class GeneticAlgorithm {
	
	double crossoverRate; 
	double mutationRate;
	double bestValue;
	
	public GeneticAlgorithm(){
		this.setProperties(0.9, 0.05);
	}
	
	public GeneticAlgorithm(double crossoverRate, double mutationRate){
		this.setProperties(crossoverRate, mutationRate);
	}
	
	public void setProperties(double crossoverRate, double mutationRate){
		this.crossoverRate = crossoverRate; 
		this.mutationRate = mutationRate;
	}
	
	public Chromosome createInitialPopulation(int population, int variables, int length){
		int i;
		int[] variableLengths = new int[variables];
		for(i=0 ; i<variables ; i++) variableLengths[i] = length;
		Chromosome list = new Chromosome(population, variables, length);
		list.setVariableProperty(variables, variableLengths);
		
		return list;
	}
	
	public int[] reproduction(Chromosome list){
		//roulette wheel selection
		int i,j;
		int size = list.getPopulation();
		double k=3.0;	//pressure parameter
		int[] selection = new int[2];
		double[] fitness = new double[size];
		double max=list.getFunctionResult(0), min=list.getFunctionResult(0), sum=0, tmpSum;
		double pressureFactor;
		double selectionPoint;
		
		for(i=0 ; i<size ; i++){
			if(list.getFunctionResult(i)>max) max = list.getFunctionResult(i);
			if(list.getFunctionResult(i)<min) min = list.getFunctionResult(i);
		}
		pressureFactor = (max-min)/(k-1.0);
		for(i=0 ; i<size ; i++){
			fitness[i] = max - list.getFunctionResult(i) + pressureFactor;
			sum += fitness[i];
		}
		for(i=0 ; i<2 ; i++){
			selectionPoint = Math.random() * sum;
			tmpSum = 0;
			for(j=0 ; j<size ; j++){
				tmpSum += fitness[i];
				if(selectionPoint<tmpSum){
					selection[i] = j;
					break;
				}
			}
		}		
		
		return selection;		
	}
	
	public int[][] crossover(int size, int[] one, int[] two, double rate){
		int crossoverPoint;
		int[][] offspring = new int[2][size];
		
		if(Math.random()<rate){			
			crossoverPoint = (int)(Math.random()*size);			
			for(int i=0 ; i<size ; i++){
				if(i<crossoverPoint){
					offspring[0][i] = one[i];
					offspring[1][i] = two[i];
				}
				else{
					offspring[0][i] = two[i];
					offspring[1][i] = one[i];					
				}
			}
		}
		
		return offspring;
	}
	
	public void mutation(int size, int[][] offspring, double rate){
		//typically mutation rate is 0.001
		int i,j;
		for(i=0 ; i<2 ; i++){
			for(j =0 ; j<size ; j++){			
				if(Math.random() < rate){
					if(offspring[i][j]==0) offspring[i][j]=1;
					else offspring[i][j]=0;
				}
			}
		}
	}
	
	public int[][] geneticOperation(Chromosome list){		
		int[][] offspring;
		int[] selection;
		int[] one, two;
		
		selection = reproduction(list);
		one = list.getChromosome(selection[0]);
		two = list.getChromosome(selection[1]);
		offspring = crossover(list.getChromosomeSize(), one, two, this.crossoverRate);
		mutation(list.getChromosomeSize(), offspring, this.mutationRate);
		
		return offspring;
	}
	
	public void nextGeneration(Chromosome list){
		int i;
		int variations = (int)(list.getPopulation()*0.1);
		int[][][] offspring = new int[variations][2][list.getChromosomeSize()];

		for(i=0 ; i<variations ; i++) offspring[i] = this.geneticOperation(list);
		
		list.sortAscendindOrder();

		for(i=0 ; i<variations ; i++){
			list.setChromosome(list.getPopulation()-(i*2)-1, offspring[i][0]);
			list.setChromosome(list.getPopulation()-(i*2+1)-1, offspring[i][1]);
		}
	}
	
	public double calculateConvergence(Chromosome list){
		int population = list.getPopulation();
		
		double previousBestValue = this.bestValue;
		this.bestValue = list.getFunctionResult(0);
		
	//	for(int i=1 ; i<population ; i++) 
	//		if(this.bestValue>list.getFunctionResult(i)) this.bestValue = list.getFunctionResult(i);

		return Math.abs((this.bestValue - previousBestValue)/previousBestValue);
	}
	
	public static void main(String[] args) {
		int iteration = 30;
		int population = 10;
		double result;
		ArrayList<double[]> variables = null;
		
		GeneticAlgorithm ga = new GeneticAlgorithm(0.9, 0.05);
		Chromosome cs = new Chromosome(population, 1, 10);

		for(int i=0 ; i<iteration ; i++){
			if(i>0)  ga.nextGeneration(cs);
			variables = cs.getVariableValueList(-100, 100);
			
			for(int j=0 ; j<population ; j++){
				result = Math.pow(variables.get(j)[0]-13.0, 2) + 2.0;
				cs.setFunctionResult(j, result);
			}
		}
		cs.sortAscendindOrder();	
		
		for(int i=0 ; i<5 ; i++) System.out.println(variables.get(i)[0]+"\t\t"+cs.getFunctionResult(i));
	}
}
