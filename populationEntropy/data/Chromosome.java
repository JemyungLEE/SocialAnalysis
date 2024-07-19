package populationEntropy.data;

import java.util.ArrayList;

public class Chromosome {
	/**
	 *    
	 *  Description: Chromosome data structure for Genetic Algorithm
	 */	
	
	int population;									//population size
	int chromosomeSize;						//chromosome length
	ArrayList<int[]> chromosomeList;	//list of chromosome strings, [0] is the best
	
	int variableNumber;					//number of variables
	int[] variableLengths;		//chromosome string length of each variable
	ArrayList<double[]> variableValueList;		//basically values of from 0.0 to 1.0
	ArrayList<Double> functionResults;
	
	public Chromosome(){}
	
	public Chromosome(int population, int chromosomeSize){
		this.generateChromosomes(population, chromosomeSize);
	}
	
	public Chromosome(int population, int variableNumber, int variableLength){
		int chromosomeSize = variableNumber*variableLength;
		this.setVariableProperty(variableNumber, variableLength);
		this.generateChromosomes(population, chromosomeSize);
	}
	
	public Chromosome(int population, int variableNumber, int[] variableLength){
		int chromosomeSize = 0;
		for(int i=0 ; i<variableNumber ; i++) chromosomeSize += variableNumber*variableLength[i];
		this.setVariableProperty(variableNumber, variableLength);
		this.generateChromosomes(population, chromosomeSize);
	}
	
	public Chromosome(ArrayList<int[]> list, int chromosomeSize){
		this.population = list.size();
		this.chromosomeSize = chromosomeSize;
		this.chromosomeList = list;
		this.functionResults = new ArrayList<Double>();
		for(int i=0 ; i<this.population ; i++) this.functionResults.add(0.0);
	}
	
	public void generateChromosomes(int population, int chromosomeSize){
		int i,j;
		int[] chromosome;
		this.population = population;
		this.chromosomeSize = chromosomeSize;
		this.chromosomeList = new ArrayList<int[]>();
		for(i=0 ; i<population ; i++){
			chromosome = new int[chromosomeSize];
			for(j=0 ; j<chromosomeSize ; j++){
				if(Math.random()>0.5) chromosome[j] = 1;
				else chromosome[j] = 0;
			}
			chromosomeList.add(chromosome);
		}
	}
	
	public void setVariableProperty(int size, int[] length){
		this.variableNumber = size;
		this.variableLengths = length;
	}
	
	public void setVariableProperty(int size, int length){
		this.variableNumber = size;
		this.variableLengths = new int[size];
		for(int i=0 ; i<size ; i++) this.variableLengths[i] = length;
	}
	
	public void setChromosome(int index, int[] chromosome){
		this.chromosomeList.set(index, chromosome);
	}
	
	public void setFunctionResult(int index, double value){
		this.functionResults.set(index, value);
	}
	
	public int getPopulation(){
		return this.population;
	}
	
	public int getChromosomeSize(){
		return this.chromosomeSize;
	}
	
	public int[] getChromosome(int index){
		return this.chromosomeList.get(index);
	}
	
	public double[] getVariableValues(int index){
		return this.variableValueList.get(index);
	}
	
	public double getFunctionResult(int index){
		return this.functionResults.get(index);
	}
	
	public void removeChromosome(int index){
		this.chromosomeList.remove(index);
		if(!this.variableValueList.isEmpty()) this.variableValueList.remove(index);
		if(!this.functionResults.isEmpty()) this.functionResults.remove(index);
	}
	
	public void exchangeChromosome(int index_i, int index_j){
		int[] tmpChromosome;
		double tmpFunctionResult;
		double[] tmpRealValues;
		
		tmpChromosome = this.chromosomeList.get(index_i);
		this.chromosomeList.set(index_i, this.chromosomeList.get(index_j));
		this.chromosomeList.set(index_j, tmpChromosome);
		
		if(!this.functionResults.isEmpty()){
			tmpFunctionResult = this.functionResults.get(index_i);
			this.functionResults.set(index_i, this.functionResults.get(index_j));
			this.functionResults.set(index_j, tmpFunctionResult);
		}
		
		if(!this.variableValueList.isEmpty()){
			tmpRealValues = this.variableValueList.get(index_i);
			this.variableValueList.set(index_i, this.variableValueList.get(index_j));
			this.variableValueList.set(index_j, tmpRealValues);
		}
	}
	
	public void transform(int size, int length){
		this.setVariableProperty(size, length);
		this.transform();
	}
	
	public void transform(int size, int[] length){
		this.setVariableProperty(size, length);
		this.transform();
	}
	
	public void transform(){	
		int i, j, k;
		int tmpPoint;
		double tmpValue, maxValue;
		int[] tmpString;
		double[] tmpRealnumber;
		this.variableValueList = new ArrayList<double[]>();
		this.functionResults = new ArrayList<Double>();
		
		if(this.variableNumber>0){
			for(i=0 ; i<this.population ; i++){
				tmpPoint = 0;
				tmpString = this.chromosomeList.get(i);
				tmpRealnumber = new double[variableNumber];
				for(j=0 ; j<this.variableNumber ; j++){
					tmpValue = 0.0;			
					maxValue = Math.pow(2, this.variableLengths[j]) - 1.0;
					for(k=0 ; k<this.variableLengths[j] ; k++){
						tmpValue += (int)Math.pow(2, k) * tmpString[tmpPoint]; 
						tmpPoint++;
					}
					tmpRealnumber[j] = tmpValue / maxValue;
				}
				this.variableValueList.add(tmpRealnumber);
				this.functionResults.add(0.0);
			}
		}else System.err.println("Values property isn't set.");
	}
	
	public ArrayList<double[]> getVariableValueList(){
		this.transform();
		return this.variableValueList;
	}
	
	public ArrayList<double[]> getVariableValueList(double minValue, double maxValue){
		double[] minValues = new double[this.variableNumber];
		double[] maxValues = new double[this.variableNumber];
		
		for(int i=0 ; i<this.variableNumber ; i++){
			minValues[i] = minValue;
			maxValues[i] = maxValue;
		}
		
		return this.getVariableValueList(minValues, maxValues);
	}
	
	public ArrayList<double[]> getVariableValueList(double[] minValues, double[] maxValues){
		int i, j;
		
		this.transform();
		if(minValues.length == this.variableNumber && maxValues.length == this.variableNumber){
			for(i=0 ; i<this.population ; i++)
				for(j=0 ; j<this.variableNumber ; j++)
					this.variableValueList.get(i)[j] = this.variableValueList.get(i)[j] * (maxValues[j] - minValues[j]) + minValues[j];
			return this.variableValueList;
		}else{
			System.err.println("variables size don't match.");
			return null;
		}
	}
	
	public void sortAscendindOrder(){
		int i, j;
		
		for(i=1 ; i<this.population ; i++)
			for(j=0 ; j<i ; j++)
				if(this.functionResults.get(j) > this.functionResults.get(i)) this.exchangeChromosome(i, j);
	}
}