package populationEntropy;

import java.util.ArrayList;

import populationEntropy.GeneticAlgorithm;
import populationEntropy.data.Chromosome;

public class GAtest {

	public static void main(String[] args) {
		int i, j, k;
		int iteration = 20;
		int variableNumber = 1;
		int variableLength = 20;
		int  chromosomePopulation =100;
		double crossoverRate = 0.9;
		double mutationRate = 0.05;
		ArrayList<double[]> variables;
		double convergence = -1.0;
		double x;
		GeneticAlgorithm ga = new GeneticAlgorithm(crossoverRate, mutationRate);
		Chromosome cs = new Chromosome(chromosomePopulation, variableNumber, variableLength);
		
		for(j=0 ; j<=iteration ; j++){
			if(j>0) ga.nextGeneration(cs);
			variables = cs.getVariableValueList(0.0, 10.0);
			for(k=0 ; k<chromosomePopulation ; k++){
				x = variables.get(k)[0];
				cs.setFunctionResult(k, Math.pow((x-6.0), 2)); 
			}
			convergence = ga.calculateConvergence(cs);
		}
		cs.sortAscendindOrder();
		
		for(i=0 ; i<5 ; i++) System.out.println(cs.getVariableValues(i)[0]+"\t"+cs.getFunctionResult(i)+"\t"+convergence);
	}

}
