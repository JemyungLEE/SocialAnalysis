package companyDuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import populationEntropy.CurvatureModel;
import populationEntropy.DataReader;
import populationEntropy.EntropyCalculator;
import populationEntropy.MapGenerator;
import populationEntropy.data.Coordinates;
import populationEntropy.data.CurvatureData;
import populationEntropy.data.EntropyData;
import populationEntropy.data.PopulationData;


public class IndustryEntropyCalculator {

	/**
	 *  Subject: Extract data code from  Micro-data file
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2013.7.3
	 *  Last Modified Data: 2013.7.3 
	 *  Department: Seoul Nat. Univ. depart. of Rural Systems Engineering
	 *  Description: 
	 */

	int startYear, endYear;
	int duration;
	int n_region;
	int n_industry;
	
	int industryClassDepth;		//2:2nd_jung, 3: 3rd_so order depth
	int regionClassDepth;		//2:si_do,    5: si_gun_gu,   7: eup_myun_dong
	
	HashMap<Integer, String> locationHashMap;	//<code, name>
	HashMap<Integer, String> industryHashMap;	//<code, name>
	ArrayList<Integer> locatoinCode;			//use to find region's index	
	ArrayList<Integer> industryCode;			//use to find industry's index	
	ArrayList<String> locatoinName;				//use to find region's index	
	ArrayList<String> industryName;				//use to find industry's index

	double populationMax;						
	double populationMin;						
	double[][] population;						//[region][year]
	double[][] populationNormalized;			//[region][year]

	double[][] employeeSum;						//[region][year]
	double[][][] employee;						//[region][industry][year]
	double[][][] employeeRatio;					//[region][industry][year]
	double[][] employeeRatioMean;				//[region][industry]
	
	double[][] entropy;		//[region][year]
	double[][] migration;	//[region][year]	
	
	double[][] correlation;						//[region][industry]
	double[] correlationMean;					//[industry]
	double[] correlationStd;					//[industry]
	
	double[][] correlationIndustry;				//[industry][industry]
	double[][] groupEmployee;					//[region][year]
	double[] groupCorrelation;					//[region]
	double[] entropyCorrelation;				//[region]
	
	
	public IndustryEntropyCalculator(int start, int end){
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;
		this.locationHashMap = new HashMap<Integer, String>();
		this.industryHashMap = new HashMap<Integer, String>();		
		this.locatoinCode = new ArrayList<Integer>();			
		this.industryCode = new ArrayList<Integer>();		
		this.locatoinName = new ArrayList<String>();		
		this.industryName = new ArrayList<String>();	
	}
		
	public IndustryEntropyCalculator(int start, int end, int industryClass, int regionClass){
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;
		this.industryClassDepth = industryClass;
		this.regionClassDepth = regionClass;
		this.locationHashMap = new HashMap<Integer, String>();
		this.industryHashMap = new HashMap<Integer, String>();		
		this.locatoinCode = new ArrayList<Integer>();			
		this.industryCode = new ArrayList<Integer>();		
		this.locatoinName = new ArrayList<String>();		
		this.industryName = new ArrayList<String>();	
	}
	
	
	public void initiate(){
		int i,j,k;
		
		this.employee = new double[this.n_region][this.n_industry][this.duration];
		this.employeeRatio = new double[this.n_region][this.n_industry][this.duration];
		this.employeeSum = new double[this.n_region][this.duration];
		this.employeeRatioMean = new double[this.n_region][this.n_industry];
		this.population = new double[this.n_region][this.duration];
		this.populationNormalized = new double[this.n_region][this.duration];
		
		this.correlation = new double[this.n_region][this.n_industry];		
		this.correlationMean = new double[this.n_industry];
		this.correlationStd = new double[this.n_industry];
		
		for(i=0 ; i<this.n_region ; i++){
			for(k=0 ; k<this.duration ; k++){
				this.population[i][k] = 0.0;
				this.employeeSum[i][k] = 0.0;
			}
			for(j=0 ; j<this.n_industry ; j++){
				this.correlation[i][j] = 0.0;
				for(k=0 ; k<this.duration ; k++) this.employee[i][j][k] = 0.0;
			}
		}
		for(i=0 ; i<this.n_industry ; i++){
			this.correlationMean[i] = 0.0;
			this.correlationStd[i] = 0.0;
		}			
	}
	
	public void readLocationCode(String inputFile){		
		int count = 0;
		String tmpCode;
		String tmpName;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpName = scan.next();				
				if( tmpCode.length() == this.regionClassDepth){				
					this.locationHashMap.put(Integer.parseInt(tmpCode), tmpName);
					this.locatoinCode.add(Integer.parseInt(tmpCode));
					this.locatoinName.add(tmpName);
					count++;
				}		
			}				
			this.n_region = count;
			
			scan.close();	
		} catch(IOException e) {}
			
	}
	
	public void readIndustryCode(String inputFile){		
		int count = 0;
		String tmpCode;
		String tmpName;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.next();
			scan.next();
			while(scan.hasNext()){
				tmpCode = scan.next();
				tmpName = scan.next();
				if( tmpCode.length() == this.industryClassDepth){
					this.industryHashMap.put(Integer.parseInt(tmpCode), tmpName);
					this.industryCode.add(Integer.parseInt(tmpCode));
					this.industryName.add(tmpName);
					count++;
				}		
			}				
			this.n_industry = count;
			
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void readEmployee(String inputFile){
		
		int i = 0;
		int j = 0;
		int k = 0;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.nextLine();
			
			for(i=0 ; i<this.n_region ; i++){
				for(j=0 ; j<this.n_industry ; j++){
					for(k=0 ; k<this.duration ; k++){
						if(this.locatoinName.get(i).equals(scan.next())){
							if(this.industryName.get(j).equals(scan.next())){
								if((k+this.startYear) == scan.nextInt()) {
									this.employee[i][j][k] = scan.nextDouble();
								}
							}
						}
						else System.err.println("read employee error.");
						
						//System.out.println(this.locatoinName.get(i)+"\t"+this.industryName.get(j));
					}
				}
			}
						
			scan.close();	
		} catch(IOException e) {}	
	}
	
	public void calculateEmployeeStatistics(){
		
		int i, j, k;
		
		/*** sum region's total employees ***/
		for(i=0 ; i<this.n_region ; i++)
			for(j=0 ; j<this.duration ; j++)
				for(k=0 ; k<this.n_industry ; k++) this.employeeSum[i][j] += this.employee[i][k][j];	
		
		/*** calculate employee ratio ***/
		for(i=0 ; i<this.n_region ; i++)
			for(j=0 ; j<this.duration ; j++)
				for(k=0 ; k<this.n_industry ; k++) 
					this.employeeRatio[i][k][j] = this.employee[i][k][j] / this.employeeSum[i][j];
		
		/*** calculate mean employee ratio ***/
		for(i=0 ; i<this.n_region ; i++){
			for(k=0 ; k<this.n_industry ; k++){
				for(j=0 ; j<this.duration ; j++) this.employeeRatioMean[i][k] += this.employeeRatio[i][k][j];				
				this.employeeRatioMean[i][k] /= this.duration;
			}			
		}
	}
	
	public void readPopulation(String inputFile){
		
		int i, j;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.nextLine();
			
			for(i=0 ; i<this.n_region ; i++){
				for(j=0 ; j<this.duration ; j++){
					scan.next();
					scan.next();
					this.population[i][j] = scan.nextDouble();					
				}
			}
						
			scan.close();	
		} catch(IOException e) {}
	}
	
	public void calculatePopulationStatistics(){
		
		int i, j;
		double min = 0.1;
		double max = 0.9;
		
		j=0;
		do{
			this.populationMax = this.population[0][j];
			this.populationMin = this.population[0][j];
			j++;
		}while(population[0][j] == 0);
		
		for(j=0 ; j<this.duration ; j++){
			for(i=0 ; i<this.n_region ; i++){
				if(this.populationMax < this.population[i][j]) 
					this.populationMax = this.population[i][j];
				else if(this.populationMin > this.population[i][j] && this.population[i][j] > 0) 
					this.populationMin = this.population[i][j];				
			}
		}
		
		for(j=0 ; j<this.duration ; j++)
			for(i=0 ; i<this.n_region ; i++)
				this.populationNormalized[i][j] = max * (this.population[i][j] - this.populationMin) 
													/ (this.populationMax - this.populationMin) + min;		
	}
	
	public void readEntropy(String inputFile){
		
		int i, j;
		int regionIndex;
		String region;
		
		this.entropy = new double[this.n_region][this.duration];
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.nextLine();
			
			while(scan.hasNext()){
				region = scan.next();
				for(i=0 ; i<6 ; i++) scan.nextDouble();
				if(this.locatoinName.contains(region)){
					regionIndex = this.locatoinName.indexOf(region);
					for(j=0 ; j<this.duration ; j++) this.entropy[regionIndex][j] = scan.nextDouble();
				}
				else for(j=0 ; j<this.duration ; j++) scan.nextDouble(); 				
			}
						
			scan.close();	
		} catch(IOException e) {}	
	}
	
	public void readMigration(String inputFile){
		
		int j;
		int regionIndex;
		String region;
		
		this.migration = new double[this.n_region][this.duration+2];
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			scan.nextLine();
			
			while(scan.hasNext()){
				region = scan.next();
				if(this.locatoinName.contains(region)){
					regionIndex = this.locatoinName.indexOf(region);
					for(j=0 ; j<this.duration+2 ; j++){
						if(scan.hasNextDouble()) this.migration[regionIndex][j] = scan.nextDouble();
						else{
							scan.next();
							this.migration[regionIndex][j] = -63279;
						}
					}
				}
				else for(j=0 ; j<this.duration+2 ; j++) scan.next(); 				
			}
						
			scan.close();	
		} catch(IOException e) {}	
	}	
	
	public void calculateEmployeeEntropy(){
		
		int i, j, k;
		
		double sum;
		double[][][] probability;	//[region][industry][year]
		
		double tmpProb;
		double subEntropy;
		double base = Math.log(2);
		double max, min;			
		
		probability = new double[this.n_region][this.n_industry][this.duration];
		this.entropy = new double[this.n_region][this.duration];
		
		/*** probability calculation ***/
		for(i=0 ; i<this.n_region ; i++){					
			for(j=0 ; j<this.duration ; j++){
				sum = this.employeeSum[i][j];
				for(k=0 ; k<this.n_industry ; k++){					
					if(sum>0) probability[i][k][j] = this.employee[i][k][j] / sum;
					else if(sum == 0) probability[i][k][j] = 0.0;
					else{
						probability[i][k][j] = 0.0;
						System.err.println("probability calculation error");
					}					
				}				
			}
		}	
		
		/*** entropy calculation ***/
		for(i=0 ; i<this.n_region ; i++){
			//max = 0;		//possible minimum entropy
			//min = 10000;	//possible maximum entropy
			for(j=0 ; j<this.duration ; j++){
				this.entropy[i][j] = 0.0;
				for(k=0 ; k<this.n_industry ; k++){
					tmpProb = probability[i][k][j];
					
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}
					
					this.entropy[i][j] += subEntropy;
				}
				
				//if(max < this.entropy[i][j]) max = this.entropy[i][j];
				//else if(this.entropy[i][j] > 0 && min > this.entropy[i][j]) min = this.entropy[i][j];
						
			}
		}		
	}
	
	public void calculateEntropyCorrelation(int timeShift){

		int i, j, k;
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
		
		/*
		int period = this.duration;
		if(timeShift>2) period = this.duration - timeShift + 2;
		*/
		
		int period = this.duration - timeShift;
		
		this.entropyCorrelation = new double[this.n_region];	
			
		for(i=0 ; i<this.n_region ; i++){
				
			meanPop = 0.0;
			for(k=0 ; k<period ; k++) meanPop += this.population[i][k+timeShift]; 
			meanPop /= period;
				
			tmpSumPop = 0.0;
			for(k=0 ; k<period ; k++) tmpSumPop += Math.pow((this.population[i][k+timeShift] - meanPop), 2);
					
		
			meanEmp = 0.0;
			for(k=0 ; k<period ; k++) meanEmp += this.entropy[i][k];
			meanEmp /= period;
		
			tmpSumEmp = 0.0;
			for(k=0 ; k<period ; k++) tmpSumEmp += Math.pow((this.entropy[i][k] - meanEmp), 2);
		
			normalizer = Math.sqrt(tmpSumPop) * Math.sqrt(tmpSumEmp);
			
			this.entropyCorrelation[i] = 0.0;				
			for(k=0 ; k<period ; k++) 
				this.entropyCorrelation[i] += (this.population[i][k+timeShift]-meanPop)
													*(this.entropy[i][k]-meanEmp);
			this.entropyCorrelation[i] /= normalizer;
			
			System.out.println(this.locatoinName.get(i)+"\t"+this.entropyCorrelation[i]);
			
		}		
	}
	
	public void calculateEntropyMigrationCorrelation(int timeShift){

		int i, j, k;
		double meanPop, meanEmp;
		double tmpSumPop, tmpSumEmp;
		double normalizer;
	
		int count;
		
		int period = this.duration;
		if(timeShift>2) period = this.duration - timeShift + 2;
		
		this.entropyCorrelation = new double[this.n_region];	
			
		for(i=0 ; i<this.n_region ; i++){
			count = 0;
			for(k=0 ; k<period ; k++) if(this.migration[i][k+timeShift] == -63279) count++; 
			
			if(count == 0){
			
				meanPop = 0.0;
				for(k=0 ; k<period ; k++) meanPop += this.migration[i][k+timeShift]; 
				meanPop /= period;
		
				tmpSumPop = 0.0;
				for(k=0 ; k<period ; k++) tmpSumPop += Math.pow((this.migration[i][k+timeShift] - meanPop), 2);
					
		
				meanEmp = 0.0;
				for(k=0 ; k<period ; k++) meanEmp += this.entropy[i][k];
				meanEmp /= period;
		
				tmpSumEmp = 0.0;
				for(k=0 ; k<period ; k++) tmpSumEmp += Math.pow((this.entropy[i][k] - meanEmp), 2);
		
				normalizer = Math.sqrt(tmpSumPop * tmpSumEmp);
		
				this.entropyCorrelation[i] = 0.0;				
				for(k=0 ; k<period ; k++) 
					this.entropyCorrelation[i] += (this.migration[i][k+timeShift]-meanPop)
													*(this.entropy[i][k]-meanEmp);
				this.entropyCorrelation[i] /= normalizer;
				
				System.out.println(this.locatoinName.get(i)+"\t"+this.entropyCorrelation[i]);
			}
		}		
	}
	
	public void printEmployeeEntropyMigration(String outputFile){
		int i,j;
		int count;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			for(i=0 ; i<this.n_region ; i++){
				count = 0;
				for(j=0 ; j<this.duration ; j++) if(this.migration[i][j] == -63279) count++;
				for(j=0 ; j<this.duration ; j++) if(this.population[i][j] == 0) count++;			 
				
				if(count == 0){
					pw.print(this.locatoinName.get(i));			
					for(j=0 ; j<this.duration ; j++)			
						pw.print("\t"+this.entropy[i][j]+"\t"+(this.migration[i][j]/this.population[i][j]));			
					pw.println();
				}
			}

			pw.close();
		}catch(IOException e) {}
	}
	
	public void printEmployeeEntropy(String outputFile){
		int i,j;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.println("Number_of_industry:\t"+this.n_industry);
			pw.println("Max_entropy:\t"+(Math.log(n_industry)/Math.log(2)));
			pw.println();
			
			pw.print("region");
			for(j=0 ; j<this.duration ; j++) pw.print("\t"+(this.startYear+j));
			pw.println();
			
			for(i=0 ; i<this.n_region ; i++){	
				pw.print(this.locatoinName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.entropy[i][j]);			
				pw.println();
			}

			pw.close();
		}catch(IOException e) {}
	}
	
	public void printANNinputFile(String outputFile){
		int i, j, k;
		int count;
		int no;
		
		double max = 0.9;
		double min = 0.1;
		
		double[] maxEmployeeRatio = new double[this.n_industry];
		double[] minEmployeeRatio = new double[this.n_industry];
		double maxMigrationRate;
		double minMigrationRate;
		
		double tmpMigrationRate;
		double normalizedPopulation;
		double normalizedEmployeeRatio;
		double normalizedMigrationRate;
		
		for(j=0 ; j<this.n_industry ; j++){
			maxEmployeeRatio[j] = this.employeeRatio[0][j][0];
			minEmployeeRatio[j] = this.employeeRatio[0][j][0];
					
			for(i=0 ; i<this.n_region ; i++){				
				for(k= 0 ; k<this.duration ; k++){
					if(this.employeeRatio[i][j][k] > maxEmployeeRatio[j]) 
						maxEmployeeRatio[j] = this.employeeRatio[i][j][k];
					else if(this.employeeRatio[i][j][k] < minEmployeeRatio[j]) 
						minEmployeeRatio[j] = this.employeeRatio[i][j][k];				}
			}
		}
				
		maxMigrationRate = 0.0;
		minMigrationRate = 0.0;
		for(i=0 ; i<this.n_region ; i++){				
			for(j= 0 ; j<this.duration ; j++){
				if(this.migration[i][j] != -63279 && this.population[i][j] != 0){
					tmpMigrationRate = this.migration[i][j] / this.population[i][j];
					if(tmpMigrationRate > maxMigrationRate) maxMigrationRate = tmpMigrationRate;
					else if(tmpMigrationRate < minMigrationRate) minMigrationRate = tmpMigrationRate;	
				}
			}
		}
		for(i=0 ; i<this.n_industry ; i++) System.out.println(maxEmployeeRatio[i]+"\t"+minEmployeeRatio[i]);
		System.out.println(maxMigrationRate+"\t"+minMigrationRate);
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);

			no = 1;
			
			pw.println("industry: "+this.n_industry);
			/*
			pw.print("no\tpopulation\t");
			for(i=0 ; i<this.n_industry-3 ; i++) pw.print(this.industryName.get(i)+"\t");
			pw.println("migration_rate");
			*/
			for(i=0 ; i<this.n_region ; i++){
				count = 0;
				for(j=0 ; j<this.duration ; j++) if(this.migration[i][j] == -63279) count++;
				for(j=0 ; j<this.duration ; j++) if(this.population[i][j] == 0) count++;			 
				for(j=0 ; j<this.duration ; j++) 
					for(k= 0 ; k<this.n_industry-3 ; k++) 
						if(Double.isNaN(this.employeeRatio[i][k][j]) 
								&& Double.isInfinite(this.employeeRatio[i][k][j])) count++;
				
				if(count == 0){							
					for(j=0 ; j<this.duration ; j++){
						normalizedPopulation = (max-min) * (this.population[i][j] - this.populationMin)
												/ (this.populationMax - this.populationMin) + min;
						pw.print(no+"\t"+normalizedPopulation+"\t");
						for(k= 0 ; k<this.n_industry-3 ; k++){
							normalizedEmployeeRatio = (max-min) * (this.employeeRatio[i][k][j] - minEmployeeRatio[k])
													  / (maxEmployeeRatio[k] - minEmployeeRatio[k]) + min;
							pw.print(normalizedEmployeeRatio+"\t");
						}						
						tmpMigrationRate = this.migration[i][j]/this.population[i][j];
						normalizedMigrationRate = (max-min) * (tmpMigrationRate - minMigrationRate)
													/ (maxMigrationRate - minMigrationRate) + min;
						pw.print(normalizedMigrationRate);
						pw.println();
						no++;
					}			
				}
			}

			pw.close();
		}catch(IOException e) {}
	}
	
	
	public void printANNinputFile(String outputFile, PopulationData pData){
		int i, j, k;
		int count;
		int no;
		int regionIndex;
		int check;
		
		double max = 0.9;
		double min = 0.1;
		
		double maxPopulatoinRatio;
		double maxMigrationRate;
		double minMigrationRate;
		
		double tmpMigrationRate;
		double normalizedPopulation;
		double normalizedEmployeeRatio;
		double normalizedMigrationRate;
		
		
		maxPopulatoinRatio = 0.0;
		for(i=0 ; i<pData.getNumberOfYears() ; i++)
			for(j=0 ; j<pData.getDistricNumber() ; j++)
				for(k=1 ; k<pData.getClassNumber() ; k++)
					if(maxPopulatoinRatio < pData.getProbability(i, j, k)) 
						maxPopulatoinRatio = pData.getProbability(i, j, k); 
				
		maxMigrationRate = 0.0;
		minMigrationRate = 0.0;
		for(i=0 ; i<this.n_region ; i++){				
			for(j= 0 ; j<this.duration ; j++){
				if(this.migration[i][j] != -63279 && this.population[i][j] != 0){
					tmpMigrationRate = this.migration[i][j] / this.population[i][j];
					if(tmpMigrationRate > maxMigrationRate) maxMigrationRate = tmpMigrationRate;
					else if(tmpMigrationRate < minMigrationRate) minMigrationRate = tmpMigrationRate;	
				}
			}
		}
		System.out.println(maxMigrationRate+"\t"+minMigrationRate);
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);

			no = 1;
			
			pw.println("max_population_rate: "+maxPopulatoinRatio+"\tmin_population_rate: 0");
			pw.println("max_migration_rate: "+maxMigrationRate+"\tmin_migration_rate: "+minMigrationRate);

			for(i=0 ; i<this.n_region ; i++){
				count = 0;
				for(j=0 ; j<this.duration ; j++) if(this.migration[i][j] == -63279) count++;
				for(j=0 ; j<this.duration ; j++) if(this.population[i][j] == 0) count++;			 
				
				check = 0;
				regionIndex = -1;
				for(j=0 ; j<pData.getDistricNumber() ; j++){
					if(this.locatoinName.get(i).equals(pData.getDistrictName(j))){
						regionIndex = j;
						check = 1;
					}
				}					
				
				if(count == 0 && check == 1){							
					for(j=0 ; j<this.duration ; j++){
						normalizedPopulation = (max-min) * (this.population[i][j] - this.populationMin)
												/ (this.populationMax - this.populationMin) + min;
						pw.print(no+"\t"+normalizedPopulation+"\t");

						for(k=1 ; k<pData.getClassNumber() ; k++){
							pw.print( ((max-min) * (pData.getProbability((j+6), regionIndex, k) )
						   			                / maxPopulatoinRatio + min) + "\t");
						}
						
						tmpMigrationRate = this.migration[i][j]/this.population[i][j];
						normalizedMigrationRate = (max-min) * (tmpMigrationRate - minMigrationRate)
													/ (maxMigrationRate - minMigrationRate) + min;
						pw.print(normalizedMigrationRate);
						pw.println();
						no++;
					}			
				}
			}

			pw.close();
		}catch(IOException e) {}
	}
	
	public static void main(String[] args) {
	
		int start = 1998;
		int end = 2010;
		
		int industryClass;		//0: 중분류,  1: 소분류,   2: 세분류  
		int regionClass;	    //0: 시도,    1: 시군구,   2: 읍면동
		
		int[] industClassKey = new int[3];			//2: 중분류,  3: 소분류,   4: 세분류 
		int[] regionCLassKey = new int[3];			//2: 시도,    5: 시군구,   7: 읍면동
		String[] industClassName = new String[3];
		String[] regionClassName = new String[3];
		
		industClassKey[0] = 2;
		industClassKey[1] = 3;
		industClassKey[2] = 4;
		regionCLassKey[0] = 2;
		regionCLassKey[1] = 5;
		regionCLassKey[2] = 7;
		industClassName[0] = "1st";
		industClassName[1] = "2nd";
		industClassName[2] = "3rd";
		regionClassName[0] = "do";
		regionClassName[1] = "gun";
		regionClassName[2] = "myun";
		
		industryClass = 0;
		regionClass = 1;
			
		String filePath = "/Users/jml/Desktop/Research/data_storage/company/";
		String locationCodeFile = filePath+"location_code/location_code.txt";
		String industryCodeFile = filePath+"industry_code/industry_code.txt";
		String employeeFile = filePath+"employee.txt";
		String employeeEntropyFile = filePath+"entropy/employeeEntropy_"+industClassName[industryClass]+"_"
																+regionClassName[regionClass]+".txt";
		String populationFile = filePath+"population.txt";
		
		IndustryEntropyCalculator iec = new IndustryEntropyCalculator(start, end, 
											industClassKey[industryClass], regionCLassKey[regionClass]);
			
		System.out.print("location code reading: ");
		iec.readLocationCode(locationCodeFile);
		System.out.println("complete");

		System.out.print("industry code reading: ");
		iec.readIndustryCode(industryCodeFile);
		System.out.println("complete");

		System.out.print("variables initializing: ");
		iec.initiate();
		System.out.println("complete");
		
		/*
		System.out.print("population reading: ");
		iec.readPopulation(populationFile);
		iec.calculatePopulationStatistics();
		System.out.println("complete");
		*/
		
		System.out.print("employee reading: ");
		iec.readEmployee(employeeFile);
		iec.calculateEmployeeStatistics();
		System.out.println("complete");	
				
		//mea.readEntropy(filePath+"normEntropy_region.txt");
		//iec.readMigration(filePath+"migration_region.txt");
		
		System.out.print("entropy calculating: ");
		iec.calculateEmployeeEntropy();
		System.out.println("complete");	
		
		System.out.print("entropy printing: ");
		iec.printEmployeeEntropy(employeeEntropyFile);
		System.out.println("complete");	
		
		//iec.calculateEntropyMigrationCorrelation(0);
		//iec.calculateEntropyCorrelation(0);
		//mea.printEntropyMigration(filePath+"entropy_migration.txt");

		//iec.printANNinputFile(filePath+"ann_input.txt");
			
		/*
		String popfilePath = "/Users/jml/Desktop/Research/data_storage/population/";
		String populatoinFile = popfilePath + "population_region.txt";
					
		PopulationData pData = new PopulationData();
		
		EntropyCalculator ec = new EntropyCalculator();
		DataReader dr = new DataReader();	
		
		System.out.print("data reading: ");
		dr.readData(populatoinFile, pData);
		System.out.println("complete");
		
		System.out.print("probability calculating: ");
		ec.calculateProbability(pData);
		System.out.println("complete");
		
		iec.printANNinputFile(filePath+"ann_input_pop_mig.txt", pData);
		*/
		
		System.out.println("process complete.");
	}

}
