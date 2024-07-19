package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import populationEntropy.data.PopulationData;
import populationEntropy.data.EntropyData;
import populationEntropy.data.CurvatureData;
import populationEntropy.data.InteractionData;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class MigrationCalculator {
	
	int numberOfIndex;
	int numberOfYears, numberOfDistricts;
	ArrayList<String> districtList;
	ArrayList<Integer> yearList;
	double[] totalPopulation;									//[year]
	int[][] netFlows, inFlows, outFlows;					//[year][district]
	double[][] inFlowsRate, outFlowsRate;				//[year][district]
	double[][] netFlows_norm, inFlows_norm, outFlows_norm, inFlowsRate_norm, outFlowsRate_norm;	//[year][district]
	double[][] inFlowsShifted, outFlowsShifted;		//[year][district]
	ArrayList<String> upperLevelDistrictIndicators;
	
	ArrayList<Integer> assembledYears;
	ArrayList<ArrayList<String>> assembledDistricts;
	
	boolean normalizedCheck = false;
	boolean[] fragmented;										//[year]
	ArrayList<ArrayList<Integer>> fragmentedRegionIndex;			//index of fragmented observed region
	ArrayList<ArrayList<ArrayList<Integer>>> fragmentedIntRegIndex;		//index of fragmented interaction region
	
	public MigrationCalculator(){
		this.initiate();
	}
	
	public void initiate(){
		this.upperLevelDistrictIndicators = new ArrayList<String>();
		this.districtList = new ArrayList<String>();
		this.yearList = new ArrayList<Integer>();
		this.assembledYears = new ArrayList<Integer>();
		this.assembledDistricts = new ArrayList<ArrayList<String>>();
	}
	
	public void initiateVariables(){
		this.inFlows = new int[this.numberOfYears][this.numberOfDistricts];
		this.outFlows = new int[this.numberOfYears][this.numberOfDistricts];
		this.netFlows = new int[this.numberOfYears][this.numberOfDistricts];
	}
	
	public void initiateNormalizedVariables(){
		this.normalizedCheck = false;
		
		this.totalPopulation = new double[this.numberOfYears];
		this.inFlowsRate = new double[this.numberOfYears][this.numberOfDistricts];
		this.outFlowsRate = new double[this.numberOfYears][this.numberOfDistricts];
		
		this.inFlows_norm = new double[this.numberOfYears][this.numberOfDistricts];
		this.outFlows_norm = new double[this.numberOfYears][this.numberOfDistricts];		
		this.netFlows_norm = new double[this.numberOfYears][this.numberOfDistricts];
		this.inFlowsRate_norm = new double[this.numberOfYears][this.numberOfDistricts];
		this.outFlowsRate_norm = new double[this.numberOfYears][this.numberOfDistricts];	
	}
	
	public void readMigrationData(String inflowsFile, String outflowsFile){
		int i, j;
		
		int tmpInt, tmpDistrictIndex;
		String tmpDistrictName, tmpName;
		String[] tmpStr;
		
		boolean lowerLevelCheck;
		
		File file;
		Scanner scan;
		
		/*** set indicators ***/
		this.numberOfIndex = 1;
		this.upperLevelDistrictIndicators = new ArrayList<String>();
		this.upperLevelDistrictIndicators.add("특별시");
		this.upperLevelDistrictIndicators.add("광역시");
		this.upperLevelDistrictIndicators.add("도");
		this.upperLevelDistrictIndicators.add("특별자치시");
		
		/*** reading process ***/
		try{
			/*** pre-process of migration data reading ***/
			file = new File(inflowsFile);
			scan = new Scanner(file);
			
			tmpInt = 0;	
			tmpStr = scan.nextLine().split("\t");
			for(i=this.numberOfIndex ; i<tmpStr.length ; i++)
				if(tmpInt < Integer.parseInt(tmpStr[i])) this.yearList.add(Integer.parseInt(tmpStr[i]));
			this.numberOfYears = this.yearList.size();

			tmpDistrictName = null;
			while(scan.hasNext()){
				tmpStr = scan.nextLine().split("\t");
				lowerLevelCheck = true;
				for(i=0 ; i<this.upperLevelDistrictIndicators.size() ; i++){
					if(tmpStr[0].endsWith(this.upperLevelDistrictIndicators.get(i))){
						tmpDistrictName = tmpStr[0];
						lowerLevelCheck = false;
					}
				}
				if(lowerLevelCheck){
					tmpName = tmpDistrictName + tmpStr[0];
					if(!this.districtList.contains(tmpName)) this.districtList.add(tmpName);
				}
			}
			this.numberOfDistricts = this.districtList.size();
			scan.close();
			
			/*** initiate variables ***/
			this.initiateVariables();
			
			/*** read in-migration data ***/
			file = new File(inflowsFile);
			scan = new Scanner(file);
			
			scan.nextLine();
			
			tmpDistrictName = null;
			while(scan.hasNext()){
				tmpStr = scan.nextLine().split("\t");
				lowerLevelCheck = true;
				for(i=0 ; i<this.upperLevelDistrictIndicators.size() ; i++){
					if(tmpStr[0].endsWith(this.upperLevelDistrictIndicators.get(i))){
						tmpDistrictName = tmpStr[0];
						lowerLevelCheck = false;
					}
				}
				if(lowerLevelCheck){
					tmpName = tmpDistrictName + tmpStr[0];
					tmpDistrictIndex = this.districtList.indexOf(tmpName);
					for(i=this.numberOfIndex ; i<tmpStr.length ; i++){
						if(tmpStr[i].isEmpty() || Integer.parseInt(tmpStr[i]) < 0) tmpInt = -1;
						else if(!tmpStr[i].isEmpty()) tmpInt =  Integer.parseInt(tmpStr[i]);
						else tmpInt = -1;
						
						this.inFlows[i-this.numberOfIndex][tmpDistrictIndex] = tmpInt;
					}
				}
			}
			scan.close();
			
			/*** read out-migration data ***/
			file = new File(outflowsFile);
			scan = new Scanner(file);
			
			scan.nextLine();
			while(scan.hasNext()){
				tmpStr = scan.nextLine().split("\t");
				lowerLevelCheck = true;
				for(i=0 ; i<this.upperLevelDistrictIndicators.size() ; i++){
					if(tmpStr[0].endsWith(this.upperLevelDistrictIndicators.get(i))){
						tmpDistrictName = tmpStr[0];
						lowerLevelCheck = false;
					}
				}
				if(lowerLevelCheck){
					tmpName = tmpDistrictName + tmpStr[0];
					tmpDistrictIndex = this.districtList.indexOf(tmpName);
					
					for(i=this.numberOfIndex ; i<tmpStr.length ; i++){
						if(tmpStr[i].isEmpty() || Integer.parseInt(tmpStr[i]) < 0) tmpInt = -1;
						else if(!tmpStr[i].isEmpty()) tmpInt =  Integer.parseInt(tmpStr[i]);
						else tmpInt = -1;
						
						this.outFlows[i-this.numberOfIndex][tmpDistrictIndex] = tmpInt;
					}
				}
			}
			scan.close();
			
			/*** calculate net-migration ***/
			for(i=0 ; i<this.numberOfYears ; i++)
				for(j=0 ; j<this.numberOfDistricts ; j++) this.netFlows[i][j] = this.inFlows[i][j] - this.outFlows[i][j];
			
			/*
			for(j=0 ; j<this.numberOfYears ; j++) System.out.print("\t"+this.yearList.get(j));
			System.out.println();
			for(i=0 ; i<this.numberOfDistricts ; i++){
				System.out.print(this.districtList.get(i));
				for(j=0 ; j<this.numberOfYears ; j++) System.out.print("\t"+this.inFlows[j][i]);
				System.out.println();
			}
			*/
		} catch(IOException e) {
			System.err.println(e);
		}
	}
	
	public void checkFragmentedRegion(boolean fragment, InteractionData iData, EntropyData eData){

		//initiate fragmented region variables
		this.fragmented = new boolean[this.numberOfYears];
		this.fragmentedRegionIndex = new ArrayList<ArrayList<Integer>>();
		this.fragmentedIntRegIndex = new ArrayList<ArrayList<ArrayList<Integer>>>();
		
		//check fragmented regions
		if(fragment)
			for(int i=0 ; i<this.numberOfYears ; i++)
				this.checkFragmentedRegion(this.yearList.get(i), iData, eData);
		else{
			for(int i=0 ; i<this.numberOfYears ; i++){
				this.fragmented[i] = false;
				this.fragmentedRegionIndex.add(null);
				this.fragmentedIntRegIndex.add(null);
			}
		}
		
		/*
		for(int i=0 ; i<this.numberOfYears ; i++){
			System.out.print(this.yearList.get(i)+"\t"+this.fragmented[i]);
			if(this.fragmented[i]){
				for(int j=0 ; j<this.fragmentedRegionIndex.get(i).size() ; j++){
					System.out.print(this.districtList.get(this.fragmentedRegionIndex.get(i).get(j)));
					for(int k=0 ; k<this.fragmentedIntRegIndex.get(i).get(j).size() ; k++)
						System.out.print("\t"+iData.getDistrictName(this.fragmentedIntRegIndex.get(i).get(j).get(k)));
					System.out.println("\t");
				}						
			}
		}
		*/
	}
	
	public void checkFragmentedRegion(int year, InteractionData iData, EntropyData eData){
		int i, j;
		int interactionYearIndex = iData.getYearIndex(year);
		int observedYearIndex = this.yearList.indexOf(year);
		boolean check, checkAll = false;	//[true] there are fragmented regions
		String tmpRegionName;
		ArrayList<Integer> tmpFragRegion, fragObsRegion;
		ArrayList<ArrayList<Integer>> fragIntRegion;
		
		if(interactionYearIndex < 0 && year > iData.getStartYear())
			for(i=1 ; interactionYearIndex<0 ; i++) interactionYearIndex = iData.getYearIndex(year - i);
		
		if(interactionYearIndex>=0){
			fragObsRegion = new ArrayList<Integer>();
			fragIntRegion = new ArrayList<ArrayList<Integer>>();
			
			for(i=0 ; i<this.numberOfDistricts ; i++){
				if(this.inFlows[observedYearIndex][i] > 0 || this.outFlows[observedYearIndex][i] > 0){
					check = true;
					for(j=0 ; j<iData.getDistricNumber() && check; j++)
						if(this.districtList.get(i).equals(iData.getDistrictName(j))
								&& eData.getPopulation(interactionYearIndex, j)>0 ) check = false;
					
					if(check){
						check = false;
						tmpFragRegion = new ArrayList<Integer>();
						tmpRegionName = this.districtList.get(i).substring(0, this.districtList.get(i).length()-1);
						
						for(j=0 ; j<iData.getDistricNumber() ; j++){
							if(!this.districtList.get(i).equals(iData.getDistrictName(j))
									&& iData.getDistrictName(j).startsWith(tmpRegionName)
									&& eData.getPopulation(interactionYearIndex, j)>0){
								check = true;
								tmpFragRegion.add(j);
							}
						}
						
						if(check){
							checkAll = true;
							fragObsRegion.add(i);
							fragIntRegion.add(tmpFragRegion);
						}
					}
				}
				
			}
			
			this.fragmented[observedYearIndex] = checkAll;
			this.fragmentedRegionIndex.add(fragObsRegion);
			this.fragmentedIntRegIndex.add(fragIntRegion);
		}else{
			this.fragmented[observedYearIndex] = false;
			this.fragmentedRegionIndex.add(null);
			this.fragmentedIntRegIndex.add(null);
		}

	}
	
	public void normalizeMigration(){
		int i, j;
		double tmpNet, tmpNetMax, tmpInMax, tmpOutMax;
		
		/*** initiate variable ***/
		this.netFlows_norm = new double[this.numberOfYears][this.numberOfDistricts];
		this.inFlows_norm = new double[this.numberOfYears][this.numberOfDistricts];
		this.outFlows_norm = new double[this.numberOfYears][this.numberOfDistricts];
		
		for(i=0 ; i<this.numberOfYears ; i++){
			tmpNetMax = 0.0;
			tmpInMax = 0.0;
			tmpOutMax = 0.0;
			for(j=0 ; j<this.numberOfDistricts ; j++){
				tmpNet = (double) Math.abs(this.netFlows[i][j]);
				if(tmpNet > tmpNetMax) tmpNetMax = tmpNet;
				if((double)this.inFlows[i][j] > tmpInMax) tmpInMax = (double)this.inFlows[i][j];
				if((double)this.outFlows[i][j] > tmpOutMax) tmpOutMax = (double)this.outFlows[i][j];
			}
			for(j=0 ; j<this.numberOfDistricts ; j++){
				this.netFlows_norm[i][j] = (double)this.netFlows[i][j] / tmpNetMax;
				this.inFlows_norm[i][j] = (double)this.inFlows[i][j] / tmpInMax;
				this.outFlows_norm[i][j] = (double)this.outFlows[i][j] / tmpOutMax;
			}
		}
	}
	
	public void normalizeObservedMigration(InteractionData iData, EntropyData eData){
		/*** mode: [0] normal (+1 year), [1] current year ***/
		this.normalizeObservedMigration(1, iData, eData);
	}
	
	public void normalizeObservedMigration(int mode, InteractionData iData, EntropyData eData){
		/*** Prepare optimization process for separated migration model ***/
		double max = 1.0;
		double min = 0.1;
		
		int i, j, k;
		int duration = iData.getNumberOfYears();
		int obsYearIndex, districtIndex;
		double tmpPopulation, tmpTotalPopulation;
		double tmpObsIn, tmpObsOut, tmpObsNet, tmpObsInRate, tmpObsOutRate; //observed migrations
		double maxObsIn, maxObsOut, maxObsNet, maxObsInRate, maxObsOutRate;
		double minObsIn, minObsOut, minObsNet, minObsInRate, minObsOutRate;
		double transferIn, transferOut, transferNet, transferInRate, transferOutRate;
		
		ArrayList<Integer> fragObsRegionIndex = null;
		ArrayList<ArrayList<Integer>> fragIntRegIndex = null;
		
		//initiate variables
		this.initiateNormalizedVariables();
		
		//normalization process
		for(i=0 ; i<duration ; i++){
			obsYearIndex = this.yearList.indexOf(iData.getYear(i)+1-mode);
			tmpTotalPopulation = 0.0;
			maxObsIn = 0.0;
			maxObsOut = 0.0;
			maxObsNet = 0.0;
			maxObsInRate = 0.0;
			maxObsOutRate = 0.0;
			
			if(this.fragmented[obsYearIndex]){
				fragObsRegionIndex = this.fragmentedRegionIndex.get(obsYearIndex);
				fragIntRegIndex = this.fragmentedIntRegIndex.get(obsYearIndex);
			}
			
			// calculate total population
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex >= 0 && eData.getPopulation(i, j) > 0
						&& this.inFlows[obsYearIndex][districtIndex] >= 0 && this.outFlows[obsYearIndex][districtIndex] >= 0)
					tmpTotalPopulation += (double) eData.getPopulation(i, j);			
			}
			if(this.fragmented[obsYearIndex])
				for(j=0 ; j<fragIntRegIndex.size() ; j++)
					for(k=0 ; k<fragIntRegIndex.get(j).size() ; k++)
						tmpTotalPopulation += (double) eData.getPopulation(i, fragIntRegIndex.get(j).get(k));	
			this.totalPopulation[obsYearIndex] = tmpTotalPopulation;
			
			// find max values and calculate rate-values
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex >=0 && eData.getPopulation(i, j) > 0
					&& this.inFlows[obsYearIndex][districtIndex]>=0 && this.outFlows[obsYearIndex][districtIndex]>=0){
					tmpPopulation = (double) eData.getPopulation(i, j);
					
					tmpObsIn = (double) this.inFlows[obsYearIndex][districtIndex];
					tmpObsOut = (double) this.outFlows[obsYearIndex][districtIndex];			
					tmpObsNet = (double) Math.abs(this.netFlows[obsYearIndex][districtIndex]);	
					tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
					tmpObsOutRate = tmpObsOut / tmpPopulation;	
					if(maxObsIn < tmpObsIn) maxObsIn = tmpObsIn;
					if(maxObsOut < tmpObsOut) maxObsOut = tmpObsOut;
					if(maxObsNet < tmpObsNet) maxObsNet = tmpObsNet;
					if(maxObsInRate < tmpObsInRate) maxObsInRate = tmpObsInRate;
					if(maxObsOutRate < tmpObsOutRate) maxObsOutRate = tmpObsOutRate;
					
					this.inFlowsRate[obsYearIndex][districtIndex] = tmpObsInRate; 
					this.outFlowsRate[obsYearIndex][districtIndex] = tmpObsOutRate; 
				}
			}
			minObsIn = maxObsIn;
			minObsOut = maxObsOut;
			minObsNet = maxObsNet;
			minObsInRate = maxObsInRate;
			minObsOutRate = maxObsOutRate;
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex >=0 && eData.getPopulation(i, j) > 0
					&& this.inFlows[obsYearIndex][districtIndex]>=0 && this.outFlows[obsYearIndex][districtIndex]>=0){
					tmpPopulation = (double) eData.getPopulation(i, j);
					
					tmpObsIn = (double) this.inFlows[obsYearIndex][districtIndex];
					tmpObsOut = (double) this.outFlows[obsYearIndex][districtIndex];			
					tmpObsNet = (double) Math.abs(this.netFlows[obsYearIndex][districtIndex]);	
					tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
					tmpObsOutRate = tmpObsOut / tmpPopulation;	
					if(tmpObsIn > 0 && minObsIn > tmpObsIn) minObsIn = tmpObsIn;
					if(tmpObsOut > 0 && minObsOut > tmpObsOut) minObsOut = tmpObsOut;
					if(tmpObsNet > 0 && minObsNet > tmpObsNet) minObsNet = tmpObsNet;
					if(tmpObsInRate > 0 && minObsInRate > tmpObsInRate) minObsInRate = tmpObsInRate;
					if(tmpObsOutRate > 0 && minObsOutRate > tmpObsOutRate) minObsOutRate = tmpObsOutRate;
				}
			}
			
			if(this.fragmented[obsYearIndex]){
				for(j=0 ; j<fragObsRegionIndex.size() ; j++){
					tmpPopulation = 0.0;
					for(k=0 ; k<fragIntRegIndex.get(j).size() ; k++){
						districtIndex = fragIntRegIndex.get(j).get(k);
						tmpPopulation += (double) eData.getPopulation(i, districtIndex);
					}
					districtIndex = fragObsRegionIndex.get(j);
					tmpObsIn = (double) this.inFlows[obsYearIndex][districtIndex];
					tmpObsOut = (double) this.outFlows[obsYearIndex][districtIndex];			
					tmpObsNet = (double) Math.abs(this.netFlows[obsYearIndex][districtIndex]);	
					tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
					tmpObsOutRate = tmpObsOut / tmpPopulation;	
					if(maxObsIn < tmpObsIn) maxObsIn = tmpObsIn;
					if(tmpObsIn > 0 && minObsIn > tmpObsIn) minObsIn = tmpObsIn;
					if(maxObsOut < tmpObsOut) maxObsOut = tmpObsOut;
					if(tmpObsOut > 0 && minObsOut > tmpObsOut) minObsOut = tmpObsOut;
					if(maxObsNet < tmpObsNet) maxObsNet = tmpObsNet;
					if(tmpObsNet > 0 && minObsNet > tmpObsNet) minObsNet = tmpObsNet;
					if(maxObsInRate < tmpObsInRate) maxObsInRate = tmpObsInRate;
					if(tmpObsInRate > 0 && minObsInRate > tmpObsInRate) minObsInRate = tmpObsInRate;
					if(maxObsOutRate < tmpObsOutRate) maxObsOutRate = tmpObsOutRate;
					if(tmpObsOutRate > 0 && minObsOutRate > tmpObsOutRate) minObsOutRate = tmpObsOutRate;
					
					this.inFlowsRate[obsYearIndex][districtIndex] = tmpObsInRate; 
					this.outFlowsRate[obsYearIndex][districtIndex] = tmpObsOutRate; 
				}
			}

			//save observed migration to interactionData: exclude data of fragmented region
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex >=0 && eData.getPopulation(i, j) > 0
					&& this.inFlows[obsYearIndex][districtIndex]>=0 && this.outFlows[obsYearIndex][districtIndex]>=0){
					iData.setObservedIn(i, j, (double) this.inFlows[obsYearIndex][districtIndex]);
					iData.setObservedOut(i, j, (double) this.outFlows[obsYearIndex][districtIndex]);			
					iData.setObservedNet(i, j, (double) this.netFlows[obsYearIndex][districtIndex]);	
					iData.setObservedInRate(i, j, this.inFlowsRate[obsYearIndex][districtIndex]); 
					iData.setObservedOutRate(i, j, this.outFlowsRate[obsYearIndex][districtIndex]); 
				}
			}
			if(this.fragmented[obsYearIndex]){
				for(j=0 ; j<fragObsRegionIndex.size() ; j++){
					if(fragIntRegIndex.get(j).size()==1){
						districtIndex = fragIntRegIndex.get(j).get(0);
						iData.setObservedIn(i, districtIndex, (double) this.inFlows[obsYearIndex][ fragObsRegionIndex.get(j)]);
						iData.setObservedOut(i, districtIndex, (double) this.outFlows[obsYearIndex][ fragObsRegionIndex.get(j)]);			
						iData.setObservedNet(i, districtIndex, (double) this.netFlows[obsYearIndex][ fragObsRegionIndex.get(j)]);	
						iData.setObservedInRate(i, districtIndex, this.inFlowsRate[obsYearIndex][ fragObsRegionIndex.get(j)]); 
						iData.setObservedOutRate(i, districtIndex, this.outFlowsRate[obsYearIndex][ fragObsRegionIndex.get(j)]); 
					}
				}
			}
			
			//normalize observed migration data
			transferIn =  (max - min) / (maxObsIn - minObsIn);
			transferOut = (max - min) / (maxObsOut - minObsOut);
			transferNet = (max - min) / (maxObsNet - minObsNet);
			transferInRate = (max - min) / (maxObsInRate - minObsInRate);
			transferOutRate = (max - min) / (maxObsOutRate - minObsOutRate);
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex >=0  && eData.getPopulation(i, j) > 0
						&& this.inFlows[obsYearIndex][districtIndex]>=0 && this.outFlows[obsYearIndex][districtIndex]>=0){
					if(this.inFlows[obsYearIndex][districtIndex]>0)
						this.inFlows_norm[obsYearIndex][districtIndex] = ((double) this.inFlows[obsYearIndex][districtIndex] - minObsIn) * transferIn + min; 
					if(this.outFlows[obsYearIndex][districtIndex]>0)
						this.outFlows_norm[obsYearIndex][districtIndex] = ((double) this.outFlows[obsYearIndex][districtIndex] - minObsOut) * transferOut + min; 
					if(Math.abs(this.netFlows[obsYearIndex][districtIndex])>0)
						this.netFlows_norm[obsYearIndex][districtIndex] = ((double) this.netFlows[obsYearIndex][districtIndex] - minObsNet) * transferNet + min; 
					if(this.inFlowsRate[obsYearIndex][districtIndex]>0)
						this.inFlowsRate_norm[obsYearIndex][districtIndex] = (this.inFlowsRate[obsYearIndex][districtIndex] - minObsInRate) * transferInRate + min; 
					if(this.outFlowsRate[obsYearIndex][districtIndex]>0)
						this.outFlowsRate_norm[obsYearIndex][districtIndex] = (this.outFlowsRate[obsYearIndex][districtIndex] - minObsOutRate) * transferOutRate + min;
				}
			}
			
			//normalize fragmented regions' migration data
			if(this.fragmented[obsYearIndex]){
				for(j=0 ; j<fragIntRegIndex.size() ; j++){
					districtIndex = fragObsRegionIndex.get(j);
					if(this.inFlows[obsYearIndex][districtIndex]>0)
						this.inFlows_norm[obsYearIndex][districtIndex] = ((double) this.inFlows[obsYearIndex][districtIndex] - minObsIn) * transferIn + min; 
					if(this.outFlows[obsYearIndex][districtIndex]>0)
						this.outFlows_norm[obsYearIndex][districtIndex] = ((double) this.outFlows[obsYearIndex][districtIndex] - minObsOut) * transferOut + min; 
					if(Math.abs(this.netFlows[obsYearIndex][districtIndex])>0)
						this.netFlows_norm[obsYearIndex][districtIndex] = ((double) this.netFlows[obsYearIndex][districtIndex] - minObsNet) * transferNet + min; 
					if(this.inFlowsRate[obsYearIndex][districtIndex]>0)
						this.inFlowsRate_norm[obsYearIndex][districtIndex] = (this.inFlowsRate[obsYearIndex][districtIndex] - minObsInRate) * transferInRate + min; 
					if(this.outFlowsRate[obsYearIndex][districtIndex]>0)
						this.outFlowsRate_norm[obsYearIndex][districtIndex] = (this.outFlowsRate[obsYearIndex][districtIndex] - minObsOutRate) * transferOutRate + min;
				}
			}
		}
		
		this.normalizedCheck = true;
		
		/*
		for(i=0 ; i<iData.getNumberOfYears() ; i++){
			obsYearIndex = this.yearList.indexOf(iData.getYear(i));
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex>=0)
					System.out.println(this.inFlowsRate_norm[obsYearIndex][districtIndex]+"\t"+this.outFlowsRate_norm[obsYearIndex][districtIndex]);
			}
		}
		*/
	}
	
	public void normalizeLogNormObservedMigration(InteractionData iData, EntropyData eData){
		/*** mode: [0] normal (+1 year), [1] current year ***/
		this.normalizeLogNormObservedMigration(1, iData, eData);
	}
	
	public void normalizeLogNormObservedMigration(int mode, InteractionData iData, EntropyData eData){
		/*** mode: [0] normal (+1 year), [1] current year ***/
		this.normalizeObservedMigration(mode, iData, eData);
		
		double max = 1.0;
		double min = 0.1;
		
		int i, j;
		int duration = iData.getNumberOfYears();
		int obsYearIndex, districtIndex;
		
		ArrayList<Integer> fragObsRegionIndex = null;
		ArrayList<ArrayList<Integer>> fragIntRegIndex = null;
		
		//normalization process
		for(i=0 ; i<duration ; i++){
			obsYearIndex = this.yearList.indexOf(iData.getYear(i)+1-mode);
			
			if(this.fragmented[obsYearIndex]){
				fragObsRegionIndex = this.fragmentedRegionIndex.get(obsYearIndex);
				fragIntRegIndex = this.fragmentedIntRegIndex.get(obsYearIndex);
			}
		
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex >=0  && eData.getPopulation(i, j) > 0
						&& this.inFlows[obsYearIndex][districtIndex]>=0 && this.outFlows[obsYearIndex][districtIndex]>=0){				
					this.inFlows_norm[obsYearIndex][districtIndex] = (1.0+Math.log10(this.inFlows_norm[obsYearIndex][districtIndex]))*(max-min)+min;
					this.outFlows_norm[obsYearIndex][districtIndex] = (1.0+Math.log10(this.outFlows_norm[obsYearIndex][districtIndex]))*(max-min)+min;				
				}
			}
			
			//normalize fragmented regions' migration data
			if(this.fragmented[obsYearIndex]){
				for(j=0 ; j<fragIntRegIndex.size() ; j++){
					districtIndex = fragObsRegionIndex.get(j);
					this.inFlows_norm[obsYearIndex][districtIndex] = (1.0+Math.log10(this.inFlows_norm[obsYearIndex][districtIndex]))*(max-min)+min;
					this.outFlows_norm[obsYearIndex][districtIndex] = (1.0+Math.log10(this.outFlows_norm[obsYearIndex][districtIndex]))*(max-min)+min;
				}
			}
		}
		
	}
	
	public void normalizeLogarithmObservedMigration(InteractionData iData, EntropyData eData){
		/*** mode: [0] normal (+1 year), [1] current year ***/
		this.normalizeLogarithmObservedMigration(1, iData, eData);
	}
	
	public void normalizeLogarithmObservedMigration(int mode, InteractionData iData, EntropyData eData){
		/*** Prepare optimization process for separated migration model ***/
		/*** mode: [0] normal (+1 year), [1] current year ***/
		double max = 1.0;
		double min = 0.1;
		
		int i, j, k;
		int duration = iData.getNumberOfYears();
		int obsYearIndex, districtIndex;
		double tmpPopulation, tmpTotalPopulation;
		double tmpObsIn, tmpObsOut, tmpObsNet, tmpObsInRate, tmpObsOutRate; //observed migrations
		double maxObsIn, maxObsOut, maxObsNet, maxObsInRate, maxObsOutRate;
		double minObsIn, minObsOut, minObsNet, minObsInRate, minObsOutRate;
		double transferIn, transferOut, transferNet, transferInRate, transferOutRate;
		
		ArrayList<Integer> fragObsRegionIndex = null;
		ArrayList<ArrayList<Integer>> fragIntRegIndex = null;
		
		//initiate variables
		this.initiateNormalizedVariables();
		
		//normalization process
		for(i=0 ; i<duration ; i++){
			obsYearIndex = this.yearList.indexOf(iData.getYear(i)+1-mode);
			tmpTotalPopulation = 0.0;
			maxObsIn = 0.0;
			maxObsOut = 0.0;
			maxObsNet = 0.0;
			maxObsInRate = 0.0;
			maxObsOutRate = 0.0;
			
			if(this.fragmented[obsYearIndex]){
				fragObsRegionIndex = this.fragmentedRegionIndex.get(obsYearIndex);
				fragIntRegIndex = this.fragmentedIntRegIndex.get(obsYearIndex);
			}
			
			// calculate total population
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex >= 0 && eData.getPopulation(i, j) > 0
						&& this.inFlows[obsYearIndex][districtIndex] >= 0 && this.outFlows[obsYearIndex][districtIndex] >= 0)
					tmpTotalPopulation += (double) eData.getPopulation(i, j);			
			}
			if(this.fragmented[obsYearIndex])
				for(j=0 ; j<fragIntRegIndex.size() ; j++)
					for(k=0 ; k<fragIntRegIndex.get(j).size() ; k++)
						tmpTotalPopulation += (double) eData.getPopulation(i, fragIntRegIndex.get(j).get(k));	
			this.totalPopulation[obsYearIndex] = tmpTotalPopulation;
			
			// find max values and calculate rate-values
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex >=0 && eData.getPopulation(i, j) > 0
					&& this.inFlows[obsYearIndex][districtIndex]>=0 && this.outFlows[obsYearIndex][districtIndex]>=0){
					tmpPopulation = (double) eData.getPopulation(i, j);
					
					tmpObsIn = (double) this.inFlows[obsYearIndex][districtIndex];
					tmpObsOut = (double) this.outFlows[obsYearIndex][districtIndex];			
					tmpObsNet = (double) Math.abs(this.netFlows[obsYearIndex][districtIndex]);	
					tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
					tmpObsOutRate = tmpObsOut / tmpPopulation;	
					if(maxObsIn < tmpObsIn) maxObsIn = tmpObsIn;
					if(maxObsOut < tmpObsOut) maxObsOut = tmpObsOut;
					if(maxObsNet < tmpObsNet) maxObsNet = tmpObsNet;
					if(maxObsInRate < tmpObsInRate) maxObsInRate = tmpObsInRate;
					if(maxObsOutRate < tmpObsOutRate) maxObsOutRate = tmpObsOutRate;
					
					this.inFlowsRate[obsYearIndex][districtIndex] = tmpObsInRate; 
					this.outFlowsRate[obsYearIndex][districtIndex] = tmpObsOutRate; 
				}
			}
			minObsIn = maxObsIn;
			minObsOut = maxObsOut;
			minObsNet = maxObsNet;
			minObsInRate = maxObsInRate;
			minObsOutRate = maxObsOutRate;
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex >=0 && eData.getPopulation(i, j) > 0
					&& this.inFlows[obsYearIndex][districtIndex]>=0 && this.outFlows[obsYearIndex][districtIndex]>=0){
					tmpPopulation = (double) eData.getPopulation(i, j);
					
					tmpObsIn = (double) this.inFlows[obsYearIndex][districtIndex];
					tmpObsOut = (double) this.outFlows[obsYearIndex][districtIndex];			
					tmpObsNet = (double) Math.abs(this.netFlows[obsYearIndex][districtIndex]);	
					tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
					tmpObsOutRate = tmpObsOut / tmpPopulation;	
					if(tmpObsIn > 0 && minObsIn > tmpObsIn) minObsIn = tmpObsIn;
					if(tmpObsOut > 0 && minObsOut > tmpObsOut) minObsOut = tmpObsOut;
					if(tmpObsNet > 0 && minObsNet > tmpObsNet) minObsNet = tmpObsNet;
					if(tmpObsInRate > 0 && minObsInRate > tmpObsInRate) minObsInRate = tmpObsInRate;
					if(tmpObsOutRate > 0 && minObsOutRate > tmpObsOutRate) minObsOutRate = tmpObsOutRate;
				}
			}
			
			if(this.fragmented[obsYearIndex]){
				for(j=0 ; j<fragObsRegionIndex.size() ; j++){
					tmpPopulation = 0.0;
					for(k=0 ; k<fragIntRegIndex.get(j).size() ; k++){
						districtIndex = fragIntRegIndex.get(j).get(k);
						tmpPopulation += (double) eData.getPopulation(i, districtIndex);
					}
					districtIndex = fragObsRegionIndex.get(j);
					tmpObsIn = (double) this.inFlows[obsYearIndex][districtIndex];
					tmpObsOut = (double) this.outFlows[obsYearIndex][districtIndex];			
					tmpObsNet = (double) Math.abs(this.netFlows[obsYearIndex][districtIndex]);	
					tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
					tmpObsOutRate = tmpObsOut / tmpPopulation;	
					if(maxObsIn < tmpObsIn) maxObsIn = tmpObsIn;
					if(tmpObsIn > 0 && minObsIn > tmpObsIn) minObsIn = tmpObsIn;
					if(maxObsOut < tmpObsOut) maxObsOut = tmpObsOut;
					if(tmpObsOut > 0 && minObsOut > tmpObsOut) minObsOut = tmpObsOut;
					if(maxObsNet < tmpObsNet) maxObsNet = tmpObsNet;
					if(tmpObsNet > 0 && minObsNet > tmpObsNet) minObsNet = tmpObsNet;
					if(maxObsInRate < tmpObsInRate) maxObsInRate = tmpObsInRate;
					if(tmpObsInRate > 0 && minObsInRate > tmpObsInRate) minObsInRate = tmpObsInRate;
					if(maxObsOutRate < tmpObsOutRate) maxObsOutRate = tmpObsOutRate;
					if(tmpObsOutRate > 0 && minObsOutRate > tmpObsOutRate) minObsOutRate = tmpObsOutRate;
					
					this.inFlowsRate[obsYearIndex][districtIndex] = tmpObsInRate; 
					this.outFlowsRate[obsYearIndex][districtIndex] = tmpObsOutRate; 
				}
			}

			//save observed migration to interactionData: exclude data of fragmented region
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex >=0 && eData.getPopulation(i, j) > 0
					&& this.inFlows[obsYearIndex][districtIndex]>=0 && this.outFlows[obsYearIndex][districtIndex]>=0){
					iData.setObservedIn(i, j, (double) this.inFlows[obsYearIndex][districtIndex]);
					iData.setObservedOut(i, j, (double) this.outFlows[obsYearIndex][districtIndex]);			
					iData.setObservedNet(i, j, (double) this.netFlows[obsYearIndex][districtIndex]);	
					iData.setObservedInRate(i, j, this.inFlowsRate[obsYearIndex][districtIndex]); 
					iData.setObservedOutRate(i, j, this.outFlowsRate[obsYearIndex][districtIndex]); 
				}
			}
			
			//normalize observed migration data
			maxObsIn = Math.log10(maxObsIn);
			minObsIn = Math.log10(minObsIn);
			maxObsOut = Math.log10(maxObsOut);
			minObsOut = Math.log10(minObsOut);
			maxObsNet = Math.log10(maxObsNet);
			minObsNet = Math.log10(minObsNet);
			maxObsInRate = Math.log10(maxObsInRate);
			minObsInRate = Math.log10(minObsInRate);
			maxObsOutRate = Math.log10(maxObsOutRate);
			minObsOutRate = Math.log10(minObsOutRate);
			
			transferIn =  (max - min) / (maxObsIn - minObsIn);
			transferOut = (max - min) / (maxObsOut - minObsOut);
			transferNet = (max - min) / (maxObsNet - minObsNet);
			transferInRate = (max - min) / (maxObsInRate - minObsInRate);
			transferOutRate = (max - min) / (maxObsOutRate - minObsOutRate);
			
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex >=0  && eData.getPopulation(i, j) > 0
						&& this.inFlows[obsYearIndex][districtIndex]>=0 && this.outFlows[obsYearIndex][districtIndex]>=0){
					if(this.inFlows[obsYearIndex][districtIndex]>0)
						this.inFlows_norm[obsYearIndex][districtIndex] = (Math.log10((double) this.inFlows[obsYearIndex][districtIndex]) - minObsIn) * transferIn + min; 
					if(this.outFlows[obsYearIndex][districtIndex]>0)
						this.outFlows_norm[obsYearIndex][districtIndex] = (Math.log10((double) this.outFlows[obsYearIndex][districtIndex]) - minObsOut) * transferOut + min; 
					if(Math.abs(this.netFlows[obsYearIndex][districtIndex])>0)
						this.netFlows_norm[obsYearIndex][districtIndex] = (Math.log10(Math.abs((double) this.netFlows[obsYearIndex][districtIndex])) - minObsNet) * transferNet + min; 
					if(this.netFlows[obsYearIndex][districtIndex]<0) this.netFlows_norm[obsYearIndex][districtIndex] *= -1.0;
					if(this.inFlowsRate[obsYearIndex][districtIndex]>0)
						this.inFlowsRate_norm[obsYearIndex][districtIndex] = (Math.log10(this.inFlowsRate[obsYearIndex][districtIndex]) - minObsInRate) * transferInRate + min; 
					if(this.outFlowsRate[obsYearIndex][districtIndex]>0)
						this.outFlowsRate_norm[obsYearIndex][districtIndex] = (Math.log10(this.outFlowsRate[obsYearIndex][districtIndex]) - minObsOutRate) * transferOutRate + min;
				}
			}
			
			//normalize fragmented regions' migration data
			if(this.fragmented[obsYearIndex]){
				for(j=0 ; j<fragIntRegIndex.size() ; j++){
					districtIndex = fragObsRegionIndex.get(j);
					if(this.inFlows[obsYearIndex][districtIndex]>0)
						this.inFlows_norm[obsYearIndex][districtIndex] = (Math.log10((double) this.inFlows[obsYearIndex][districtIndex]) - minObsIn) * transferIn + min; 
					if(this.outFlows[obsYearIndex][districtIndex]>0)
						this.outFlows_norm[obsYearIndex][districtIndex] = (Math.log10((double) this.outFlows[obsYearIndex][districtIndex]) - minObsOut) * transferOut + min; 
					if(Math.abs(this.netFlows[obsYearIndex][districtIndex])>0)
						this.netFlows_norm[obsYearIndex][districtIndex] = (Math.log10(Math.abs((double) this.netFlows[obsYearIndex][districtIndex])) - minObsNet) * transferNet + min; 
					if(this.netFlows[obsYearIndex][districtIndex]<0) this.netFlows_norm[obsYearIndex][districtIndex] *= -1.0;
					if(this.inFlowsRate[obsYearIndex][districtIndex]>0)
						this.inFlowsRate_norm[obsYearIndex][districtIndex] = (Math.log10(this.inFlowsRate[obsYearIndex][districtIndex]) - minObsInRate) * transferInRate + min; 
					if(this.outFlowsRate[obsYearIndex][districtIndex]>0)
						this.outFlowsRate_norm[obsYearIndex][districtIndex] = (Math.log10(this.outFlowsRate[obsYearIndex][districtIndex]) - minObsOutRate) * transferOutRate + min;
				}
			}
			
		}
		
		this.normalizedCheck = true;
		
		/*
		for(i=0 ; i<iData.getNumberOfYears() ; i++){
			obsYearIndex = this.yearList.indexOf(iData.getYear(i));
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex>=0)
					System.out.println(this.inFlowsRate_norm[obsYearIndex][districtIndex]+"\t"+this.outFlowsRate_norm[obsYearIndex][districtIndex]);
			}
		}
		*/
	}
	
	public void normalizeSymmetricNetMigration(){
		int i, j;
		double tmpNet, tmpMax, tmpMin;
		
		/*** initiate variable ***/
		this.netFlows_norm = new double[this.numberOfYears][this.numberOfDistricts];
		
		
		for(i=0 ; i<this.numberOfYears ; i++){
			/*** find min. and  max. ***/
			tmpMax = 0.0;
			tmpMin = 0.0;
			for(j=0 ; j<this.numberOfDistricts ; j++){
				tmpNet = (double) this.netFlows[i][j];
				if(tmpNet > tmpMax) tmpMax = tmpNet;
				else if(tmpNet < tmpMin) tmpMin = tmpNet;
			}
			/*** normalize values ***/
			tmpMin = Math.abs(tmpMin);
			for(j=0 ; j<this.numberOfDistricts ; j++){
				tmpNet = (double) this.netFlows[i][j];
				if(tmpNet > 0.0) this.netFlows_norm[i][j] = tmpNet / tmpMax;
				else if(tmpNet < 0.0) this.netFlows_norm[i][j] = tmpNet / tmpMin;
			}
		}
	}
	
	public void calculateShiftedMigration(){
		int i, j;
		double count, averageInflow, averageOutflow;
		
		/*** initiate variables ***/
		this.inFlowsShifted = new double[this.numberOfYears][this.numberOfDistricts];
		this.outFlowsShifted = new double[this.numberOfYears][this.numberOfDistricts];
		
		for(i=0 ; i<this.numberOfYears ; i++){
			/*** calculate average in and out migration ***/
			count = 0.0;
			averageInflow = 0.0;
			averageOutflow = 0.0;
			for(j=0 ; j<this.numberOfDistricts ; j++){
				if(this.inFlows[i][j]>0 || this.outFlows[i][j]>0){
					count++;
					averageInflow += (double) this.inFlows[i][j];
					averageOutflow += (double) this.outFlows[i][j];
				}
			}
			averageInflow /= count;
			averageOutflow /= count;
			
			/*** calculate shifted in and out migration ***/
			for(j=0 ; j<this.numberOfDistricts ; j++){
				this.inFlowsShifted[i][j] = (double) this.inFlows[i][j] - averageInflow;
				this.outFlowsShifted[i][j] = (double) this.outFlows[i][j] - averageOutflow;
			}
		}
	}
	
	public int getDistrictIndex(String districtName){
		int districtIndex = -1;
		
		if(this.districtList.contains(districtName)) districtIndex = this.districtList.indexOf(districtName);
		else for(int i=0 ; i<this.numberOfDistricts ; i++)
			if(districtName.substring(0, districtName.length()-1)
					.equals(this.districtList.get(i).substring(0, this.districtList.get(i).length()-1))) districtIndex = i;	
		return districtIndex;
	}
	
	public void calculateStatistics(int year, CurvatureData cData, EntropyData eData){
		int i;
		int pressureYearIndex = cData.getYearIndex(year);
		int migrationYearIndex = this.yearList.indexOf(year);
		int districtIndex;
		double tmpPressure, tmpMigration;
		double maxPressure = 0.0;
		double maxMigration = 0.0;
		double count = 0.0;
		
		/*** find max values ***/
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			tmpPressure = Math.abs(cData.getPressure(pressureYearIndex, i));
			if(maxPressure < tmpPressure) maxPressure = tmpPressure;
		}
		for(i=0 ; i<this.numberOfDistricts ; i++){
			tmpMigration = Math.abs(this.netFlows[migrationYearIndex][i]);
			if(maxMigration < tmpMigration) maxMigration = tmpMigration;
		}

		/*** calculate statistics ***/
		double avgPressure = 0.0;
		double stdPressure = 0.0;		
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			if(eData.getPopulation(pressureYearIndex, i) > 0){
				avgPressure += Math.abs(cData.getPressure(pressureYearIndex, i) / maxPressure);
				count++;
			}
		}
		avgPressure /= count;
		for(i=0 ; i<cData.getDistricNumber() ; i++)
			if(eData.getPopulation(pressureYearIndex, i) > 0)
				stdPressure += Math.pow(Math.abs(cData.getPressure(pressureYearIndex, i) / maxPressure) - avgPressure, 2);
		stdPressure = Math.sqrt(stdPressure / count);
		System.out.print("RMSE:\t"+year+"\t"+avgPressure+"\t"+stdPressure+"\t"+count+"\t");
		count = 0.0;
		
		double avgMigration = 0.0;
		double stdMigration = 0.0;
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((cData.getDistrictName(i)));
			if(districtIndex >= 0){
				avgMigration += Math.abs(this.netFlows[migrationYearIndex][districtIndex] / maxMigration);
				count++;
			}
		}
		avgMigration /= count;
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((cData.getDistrictName(i)));
			if(districtIndex >= 0) 
				stdMigration += Math.pow(Math.abs(this.netFlows[migrationYearIndex][districtIndex]/maxMigration)
																- avgMigration, 2);
		}
		stdMigration = Math.sqrt(stdMigration/count);
		System.out.println(avgMigration+"\t"+stdMigration+"\t"+count);
	}
	
	public void calculateRMSE(InteractionData iData, EntropyData eData){
		/*** set a double RMSE[5] array: 
		 * 		[0] immigration, [1] emigration, [2] net-migration, 
		 * 		[3] immigration rate = immigration / out of the region's population, 
		 * 		[4] emigration rate = emigration / the region's population
		***/
		for(int i=0 ; i<iData.getNumberOfYears() ; i++)
			iData.setRMSE(i, this.calculateRMSE(iData.getYear(i), iData, eData));
	}
	
	public double[] calculateRMSE(int year, InteractionData iData, EntropyData eData){
		/*** return a double RMSE[5] array: 
		 * 		[0] immigration, [1] emigration, [2] net-migration, 
		 * 		[3] immigration rate = immigration / out of the region's population, 
		 * 		[4] emigration rate = emigration / the region's population
		***/
		
		int i, j;
		int interactionYearIndex = iData.getYearIndex(year);
		int observedYearIndex = this.yearList.indexOf(year);
		int districtIndex;
		double tmpIn, tmpOut, tmpNet;		//calculated interactions
		double tmpObsIn, tmpObsOut, tmpObsNet, tmpObsInRate, tmpObsOutRate; //observed migrations
		double tmpPopulation, tmpTotalPopulation = 0.0;
		double maxIn = 0.0, maxOut = 0.0, maxNet = 0.0;
		double maxObsIn = 0.0, maxObsOut = 0.0, maxObsNet = 0.0, maxObsInRate = 0.0, maxObsOutRate = 0.0;
		double[] rmse = new double[5];
		double count = 0.0;
		ArrayList<Integer> fragObsRegionIndex = null;
		ArrayList<ArrayList<Integer>> fragIntRegIndex = null;
		
		if(this.fragmented[observedYearIndex]){
			fragObsRegionIndex = this.fragmentedRegionIndex.get(observedYearIndex);
			fragIntRegIndex = this.fragmentedIntRegIndex.get(observedYearIndex);
		}
		
		/*** calculate total population ***/
		for(i=0 ; i<iData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((iData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(interactionYearIndex, i) > 0
					&& this.inFlows[observedYearIndex][districtIndex] >= 0 && this.outFlows[observedYearIndex][districtIndex] >= 0)
				tmpTotalPopulation += (double) eData.getPopulation(interactionYearIndex, i);
		}
		if(this.fragmented[observedYearIndex])
			for(i=0 ; i<fragIntRegIndex.size() ; i++)
				for(j=0 ; j<fragIntRegIndex.get(i).size() ; j++)
					tmpTotalPopulation += (double) eData.getPopulation(interactionYearIndex, fragIntRegIndex.get(i).get(j));	
		
		/*** find max values ***/
		for(i=0 ; i<iData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((iData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(interactionYearIndex, i) > 0
					&& this.inFlows[observedYearIndex][districtIndex] >= 0 && this.outFlows[observedYearIndex][districtIndex] >= 0){
				tmpIn = iData.getMigrationIn(interactionYearIndex, i);
				tmpOut = iData.getMigrationOut(interactionYearIndex, i);
				tmpNet = Math.abs(iData.getMigrationNet(interactionYearIndex, i));
				if(maxIn < tmpIn) maxIn = tmpIn;
				if(maxOut < tmpOut) maxOut = tmpOut;
				if(maxNet < tmpNet) maxNet = tmpNet;
				
				tmpPopulation = (double) eData.getPopulation(interactionYearIndex, i);
				
				tmpObsIn = (double) this.inFlows[observedYearIndex][districtIndex];
				tmpObsOut = (double) this.outFlows[observedYearIndex][districtIndex];			
				tmpObsNet = (double) Math.abs(this.netFlows[observedYearIndex][districtIndex]);	
				tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
				tmpObsOutRate = tmpObsOut / tmpPopulation;	
				if(maxObsIn < tmpObsIn) maxObsIn = tmpObsIn;
				if(maxObsOut < tmpObsOut) maxObsOut = tmpObsOut;
				if(maxObsNet < tmpObsNet) maxObsNet = tmpObsNet;
				if(maxObsInRate < tmpObsInRate) maxObsInRate = tmpObsInRate;
				if(maxObsOutRate < tmpObsOutRate) maxObsOutRate = tmpObsOutRate;
			}
		}
		if(this.fragmented[observedYearIndex]){
			for(i=0 ; i<fragObsRegionIndex.size() ; i++){
				tmpIn = 0.0;
				tmpOut = 0.0;
				tmpNet = 0.0;
				tmpPopulation = 0.0;
				for(j=0 ; j<fragIntRegIndex.get(i).size() ; j++){
					districtIndex = fragIntRegIndex.get(i).get(j);
					tmpIn += iData.getMigrationIn(interactionYearIndex, districtIndex);
					tmpOut += iData.getMigrationOut(interactionYearIndex, districtIndex);
					tmpNet += Math.abs(iData.getMigrationNet(interactionYearIndex, districtIndex));
					tmpPopulation += (double) eData.getPopulation(interactionYearIndex, districtIndex);
				}
				if(maxIn < tmpIn) maxIn = tmpIn;
				if(maxOut < tmpOut) maxOut = tmpOut;
				if(maxNet < tmpNet) maxNet = tmpNet;
				
				districtIndex = fragObsRegionIndex.get(i);
				tmpObsIn = (double) this.inFlows[observedYearIndex][districtIndex];
				tmpObsOut = (double) this.outFlows[observedYearIndex][districtIndex];			
				tmpObsNet = (double) Math.abs(this.netFlows[observedYearIndex][districtIndex]);	
				tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
				tmpObsOutRate = tmpObsOut / tmpPopulation;	
				if(maxObsIn < tmpObsIn) maxObsIn = tmpObsIn;
				if(maxObsOut < tmpObsOut) maxObsOut = tmpObsOut;
				if(maxObsNet < tmpObsNet) maxObsNet = tmpObsNet;
				if(maxObsInRate < tmpObsInRate) maxObsInRate = tmpObsInRate;
				if(maxObsOutRate < tmpObsOutRate) maxObsOutRate = tmpObsOutRate;
			}
		}
		
		/*** calculate RMSE ***/
		for(i=0 ; i<iData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((iData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(interactionYearIndex, i) > 0
					&& this.inFlows[observedYearIndex][districtIndex] >= 0 && this.outFlows[observedYearIndex][districtIndex] >= 0){
				tmpIn = iData.getMigrationIn(interactionYearIndex, i);
				tmpOut = iData.getMigrationOut(interactionYearIndex, i);
				tmpNet = iData.getMigrationNet(interactionYearIndex, i);
				tmpObsIn = (double) this.inFlows[observedYearIndex][districtIndex];
				tmpObsOut = (double) this.outFlows[observedYearIndex][districtIndex];
				tmpObsNet = (double) this.netFlows[observedYearIndex][districtIndex];
				
				tmpPopulation = (double) eData.getPopulation(interactionYearIndex, i);
				tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
				tmpObsOutRate = tmpObsOut / tmpPopulation;	
				
				rmse[0] += Math.pow(tmpIn / maxIn - tmpObsIn / maxObsIn, 2);
				rmse[1] += Math.pow(tmpOut / maxOut - tmpObsOut / maxObsOut, 2);
				rmse[2] += Math.pow(tmpNet / maxNet - tmpObsNet / maxObsNet, 2);
				rmse[3] += Math.pow(tmpIn / maxIn - tmpObsInRate / maxObsInRate, 2);
				rmse[4] += Math.pow(tmpOut / maxOut - tmpObsOutRate / maxObsOutRate, 2);
				count++;
			}
		}
		
		/*** calculate fragmented regions' RMSE ***/
		if(this.fragmented[observedYearIndex]){
			for(i=0 ; i<fragIntRegIndex.size() ; i++){
				tmpIn = 0.0;
				tmpOut = 0.0;
				tmpNet = 0.0;
				tmpPopulation = 0.0;
				for(j=0 ; j<fragIntRegIndex.get(i).size() ; j++){
					districtIndex = fragIntRegIndex.get(i).get(j);
					tmpIn += iData.getMigrationIn(interactionYearIndex, districtIndex);
					tmpOut += iData.getMigrationOut(interactionYearIndex, districtIndex);
					tmpNet += iData.getMigrationNet(interactionYearIndex, districtIndex);
					tmpPopulation += (double) eData.getPopulation(interactionYearIndex, districtIndex);
				}
				districtIndex = fragObsRegionIndex.get(i);
				tmpObsIn = (double) this.inFlows[observedYearIndex][districtIndex];
				tmpObsOut = (double) this.outFlows[observedYearIndex][districtIndex];			
				tmpObsNet = (double) this.netFlows[observedYearIndex][districtIndex];	
				tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
				tmpObsOutRate = tmpObsOut / tmpPopulation;	
					
				rmse[0] += Math.pow(tmpIn / maxIn - tmpObsIn / maxObsIn, 2);
				rmse[1] += Math.pow(tmpOut / maxOut - tmpObsOut / maxObsOut, 2);
				rmse[2] += Math.pow(tmpNet / maxNet - tmpObsNet / maxObsNet, 2);
				rmse[3] += Math.pow(tmpIn / maxIn - tmpObsInRate / maxObsInRate, 2);
				rmse[4] += Math.pow(tmpOut / maxOut - tmpObsOutRate / maxObsOutRate, 2);
				count++;
			}
		}
		
		for(i=0 ; i<5 ; i++) rmse[i] = Math.sqrt(rmse[i]/count);
		
		return rmse;
	}
	
	public void calculateRMSE(int mode, CurvatureData cData, EntropyData eData){
		
		for(int i=0 ; i<cData.getNumberOfYears() ; i++) 
			cData.setRMSE(i, this.calculateRMSE(mode, cData.getYear(i), cData, eData));
	}
	
	public double calculateRMSE(int mode, int year, CurvatureData cData, EntropyData eData){
		/*** mode: [0]: net-migration, [1]: in-flow, [2]: out-flow [3]: net./cap., [4]: in./cap., [5]: out./cap. ***/
		int i, j;
		int pressureYearIndex = cData.getYearIndex(year);
		int migrationYearIndex = this.yearList.indexOf(year);
		int districtIndex;
		double tmpPressure, tmpMigration, tmpPopulation, tmpError;
		double maxPressure = 0.0;
		double maxMigration = 0.0;
		double rmse = 0.0;
		double count = 0.0;
		boolean check;
		
		/*** find max values ***/
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			tmpPressure = Math.abs(cData.getPressure(pressureYearIndex, i));
		//	if(mode <3) tmpPressure = Math.abs(cData.getPressure(pressureYearIndex, i));
		//	else tmpPressure = Math.abs(cData.getCurvature(pressureYearIndex, i));
			if(maxPressure < tmpPressure) maxPressure = Math.abs(tmpPressure);
		}
		
		if(mode == 0){
			for(i=0 ; i<this.numberOfDistricts ; i++)
				if(maxMigration < (double) Math.abs(this.netFlows[migrationYearIndex][i])) 
					maxMigration = (double) Math.abs(this.netFlows[migrationYearIndex][i]);
		}
		else if(mode == 1){
			for(i=0 ; i<this.numberOfDistricts ; i++)
				if(maxMigration < (double) this.inFlows[migrationYearIndex][i]) 
					maxMigration = (double) this.inFlows[migrationYearIndex][i];
		}
		else if(mode == 2){
			for(i=0 ; i<this.numberOfDistricts ; i++)
				if(maxMigration < (double) this.outFlows[migrationYearIndex][i]) 
					maxMigration = (double) this.outFlows[migrationYearIndex][i];
		}
		else if(mode >2){
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				districtIndex = this.getDistrictIndex((cData.getDistrictName(i)));
				if(districtIndex >=0 ){
					tmpPopulation = (double) eData.getPopulation(pressureYearIndex, i);
					if(mode == 3) tmpMigration = (double) Math.abs(this.netFlows[migrationYearIndex][districtIndex]) / tmpPopulation;
					else if(mode == 4) tmpMigration = (double) this.inFlows[migrationYearIndex][districtIndex] / tmpPopulation;
					else if(mode == 5) tmpMigration = (double) this.outFlows[migrationYearIndex][districtIndex] / tmpPopulation;
					else tmpMigration = 0;
					if(maxMigration < tmpMigration) maxMigration = tmpMigration;
				}
			}
		}
		
		/*** calculate RMSE ***/
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((cData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(pressureYearIndex, i) > 0
					&& this.inFlows[migrationYearIndex][districtIndex] >= 0
					&& this.outFlows[migrationYearIndex][districtIndex] >= 0){
				if(mode == 0 || mode ==3) tmpMigration = (double) this.netFlows[migrationYearIndex][districtIndex];
				else if(mode == 1 || mode ==4) tmpMigration = (double) this.inFlows[migrationYearIndex][districtIndex];
				else if(mode == 2 || mode ==5) tmpMigration = (double) this.outFlows[migrationYearIndex][districtIndex];
				else tmpMigration = Double.NaN;
				
				if(mode > 2){
					tmpMigration /= (double) eData.getPopulation(pressureYearIndex, i);
					tmpPressure = cData.getPressure(pressureYearIndex, i);
					//	tmpPressure = cData.getCurvature(pressureYearIndex, i);
				}else tmpPressure = cData.getPressure(pressureYearIndex, i);
				
				if(mode == 2 || mode ==5) tmpPressure *= -1.0;
				
				tmpError = tmpPressure / maxPressure - tmpMigration / maxMigration;
				rmse += Math.pow(tmpError, 2);
				count++;
			}
		}
		
		/*** calculate fragmented regions' RMSE ***/
		for(i=0 ; i<this.numberOfDistricts ; i++){
			check = false;
			tmpPressure = 0.0;
			for(j=0 ; j<cData.getDistricNumber() ; j++){
				if(this.getDistrictIndex(cData.getDistrictName(j))<0 
						&& cData.getDistrictName(j).startsWith(this.districtList.get(i))
						&& eData.getPopulation(pressureYearIndex, j)>0){
					tmpPressure += cData.getPressure(pressureYearIndex, j);	
				//	if(mode <3) tmpPressure += cData.getPressure(pressureYearIndex, j);			
				//	else tmpPressure += cData.getCurvature(pressureYearIndex, j);		
					check = true;
				}
			}
			if(check){
				if(mode == 0 || mode ==3) tmpMigration = (double) this.netFlows[migrationYearIndex][i];
				else if(mode == 1 || mode ==4) tmpMigration = (double) this.inFlows[migrationYearIndex][i];
				else if(mode == 2 || mode ==5) tmpMigration = (double) this.outFlows[migrationYearIndex][i];
				else tmpMigration = Double.NaN;
				if(mode > 2) tmpMigration /= (double) eData.getPopulation(pressureYearIndex, i);
				
				if(mode == 2 || mode ==5) tmpPressure *= -1.0;
				
				tmpError = tmpPressure / maxPressure - tmpMigration / maxMigration;
				rmse += Math.pow(tmpError, 2);
				count++;
			}
		}
		
	//	System.out.println("RMSE: "+year+"\t"+maxPressure+"\t"+maxMigration+"\t"+rmse+"\t"+count);
		return Math.sqrt(rmse/count);
	}
	
	public void calculateNormalizedRMSE(InteractionData iData, EntropyData eData){
		/*** set a double RMSE[5] array: 
		 * 		[0] immigration, [1] emigration, [2] net-migration, 
		 * 		[3] immigration rate = immigration / out of the region's population, 
		 * 		[4] emigration rate = emigration / the region's population
		***/
		for(int i=0 ; i<iData.getNumberOfYears() ; i++)
			iData.setRMSE(i, this.calculateNormalizedRMSE(iData.getYear(i), iData, eData));
	}
	
	public double[] calculateNormalizedRMSE(int year, InteractionData iData, EntropyData eData){
		/*** return a double RMSE[5] array: 
		 * 		[0] immigration, [1] emigration, [2] net-migration, 
		 * 		[3] immigration rate = immigration / out of the region's population, 
		 * 		[4] emigration rate = emigration / the region's population
		***/
		
		int i, j;
		int interactionYearIndex = iData.getYearIndex(year);
		int observedYearIndex = this.yearList.indexOf(year);
		int districtIndex;
		double tmpIn, tmpOut, tmpNet;		//calculated interactions
		double tmpObsIn, tmpObsOut, tmpObsNet, tmpObsInRate, tmpObsOutRate; //observed migrations
		double[] rmse = new double[5];
		double count = 0.0;
		ArrayList<Integer> fragObsRegionIndex = null;
		ArrayList<ArrayList<Integer>> fragIntRegIndex = null;
		
		if(this.fragmented[observedYearIndex]){
			fragObsRegionIndex = this.fragmentedRegionIndex.get(observedYearIndex);
			fragIntRegIndex = this.fragmentedIntRegIndex.get(observedYearIndex);
		}
		
		/*** calculate RMSE ***/
		for(i=0 ; i<iData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((iData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(interactionYearIndex, i) > 0
					&& this.inFlows[observedYearIndex][districtIndex] > 0 && this.outFlows[observedYearIndex][districtIndex] > 0){
				tmpIn = iData.getMigrationIn(interactionYearIndex, i);
				tmpOut = iData.getMigrationOut(interactionYearIndex, i);
				tmpNet = iData.getMigrationNet(interactionYearIndex, i);
				tmpObsIn = this.inFlows_norm[observedYearIndex][districtIndex];
				tmpObsOut = this.outFlows_norm[observedYearIndex][districtIndex];
				tmpObsNet = this.netFlows_norm[observedYearIndex][districtIndex];
				tmpObsInRate = this.inFlowsRate_norm[observedYearIndex][districtIndex];
				tmpObsOutRate = this.outFlowsRate_norm[observedYearIndex][districtIndex];
				
				rmse[0] += Math.pow(tmpIn - tmpObsIn, 2);
				rmse[1] += Math.pow(tmpOut - tmpObsOut, 2);
				rmse[2] += Math.pow(tmpNet - tmpObsNet, 2);
				rmse[3] += Math.pow(tmpIn - tmpObsInRate, 2);
				rmse[4] += Math.pow(tmpOut - tmpObsOutRate, 2);
				count++;
			}
		}
		
		/*** calculate fragmented regions' RMSE ***/
		if(this.fragmented[observedYearIndex]){
			for(i=0 ; i<fragIntRegIndex.size() ; i++){
				tmpIn = 0.0;
				tmpOut = 0.0;
				tmpNet = 0.0;
				for(j=0 ; j<fragIntRegIndex.get(i).size() ; j++){
					districtIndex = fragIntRegIndex.get(i).get(j);
					tmpIn += iData.getMigrationIn(interactionYearIndex, districtIndex);
					tmpOut += iData.getMigrationOut(interactionYearIndex, districtIndex);
					tmpNet += iData.getMigrationNet(interactionYearIndex, districtIndex);
				}
				districtIndex = fragObsRegionIndex.get(i);
			
				tmpObsIn = this.inFlows_norm[observedYearIndex][districtIndex];
				tmpObsOut = this.outFlows_norm[observedYearIndex][districtIndex];
				tmpObsNet = this.netFlows_norm[observedYearIndex][districtIndex];
				tmpObsInRate = this.inFlowsRate_norm[observedYearIndex][districtIndex];
				tmpObsOutRate = this.outFlowsRate_norm[observedYearIndex][districtIndex];
				
				rmse[0] += Math.pow(tmpIn - tmpObsIn, 2);
				rmse[1] += Math.pow(tmpOut - tmpObsOut, 2);
				rmse[2] += Math.pow(tmpNet - tmpObsNet, 2);
				rmse[3] += Math.pow(tmpIn - tmpObsInRate, 2);
				rmse[4] += Math.pow(tmpOut - tmpObsOutRate, 2);	
				count++;
			}
		}
		
		/*
		for(i=0 ; i<iData.getNumberOfYears() ; i++){
			observedYearIndex = this.yearList.indexOf(iData.getYear(i));
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex>=0)
					System.out.println(this.inFlowsRate_norm[observedYearIndex][districtIndex]+"\t"+this.outFlowsRate_norm[observedYearIndex][districtIndex]);
			}
		}
		*/
		
		/*
		for(i=0 ; i<this.numberOfDistricts ; i++){
			check = false;
			tmpIn = 0.0;
			tmpOut = 0.0;
			tmpNet = 0.0;
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				if(this.getDistrictIndex(iData.getDistrictName(j))<0){
					if(iData.getDistrictName(j).startsWith(this.districtList.get(i))
							&& eData.getPopulation(interactionYearIndex, j)>0){
						tmpIn += iData.getMigrationIn(interactionYearIndex, j);		
						tmpOut += iData.getMigrationOut(interactionYearIndex, j);	
						tmpNet += iData.getMigrationNet(interactionYearIndex, j);	
						check = true;
					}
				}
			}
			if(check){
				tmpObsIn = this.inFlows_norm[observedYearIndex][i];
				tmpObsOut = this.outFlows_norm[observedYearIndex][i];
				tmpObsNet = this.netFlows_norm[observedYearIndex][i];
				tmpObsInRate = this.inFlowsRate_norm[observedYearIndex][i];
				tmpObsOutRate = this.outFlowsRate_norm[observedYearIndex][i];
				
				rmse[0] += Math.pow(tmpIn - tmpObsIn, 2);
				rmse[1] += Math.pow(tmpOut - tmpObsOut, 2);
				rmse[2] += Math.pow(tmpNet - tmpObsNet, 2);
				rmse[3] += Math.pow(tmpIn - tmpObsInRate, 2);
				rmse[4] += Math.pow(tmpOut - tmpObsOutRate, 2);
				count++;
			}
		}
	*/
		for(i=0 ; i<5 ; i++) rmse[i] = Math.sqrt(rmse[i]/count);
		
		return rmse;
	}
	
	public void calculateNormalizedRMSE(int mode, CurvatureData cData, EntropyData eData){
		
		for(int i=0 ; i<cData.getNumberOfYears() ; i++) 
			cData.setRMSE(i, this.calculateNormalizedRMSE(mode, cData.getYear(i), cData, eData));
	}
	
	public double calculateNormalizedRMSE(int mode, int year, CurvatureData cData, EntropyData eData){
		/*** mode: [0]: net-migration, [1]: in-flow, [2]: out-flow ***/
		int i;
		int pressureYearIndex = cData.getYearIndex(year);
		int migrationYearIndex = this.yearList.indexOf(year);
		int districtIndex;
		double tmpPressure, tmpMigration, tmpError;
		double rmse = 0.0;
		double count = 0.0;
		
		/*** calculate RMSE ***/
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((cData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(pressureYearIndex, i) > 0
					&& this.inFlows[migrationYearIndex][districtIndex] >= 0
					&& this.outFlows[migrationYearIndex][districtIndex] >= 0){
				if(mode == 0) tmpMigration = this.netFlows_norm[migrationYearIndex][districtIndex];
				else if(mode == 1) tmpMigration = this.inFlows_norm[migrationYearIndex][districtIndex];
				else if(mode == 2) tmpMigration = this.outFlows_norm[migrationYearIndex][districtIndex];
				else tmpMigration = Double.NaN;
				
				tmpPressure = cData.getPressure(pressureYearIndex, i);
				
				if(mode == 2) tmpPressure *= -1.0;
				
				tmpError = tmpPressure - tmpMigration;
				rmse += Math.pow(tmpError, 2);
				count++;
			}
		}
		
		return Math.sqrt(rmse/count);
	}
	
	public void calculateAvgRMSE(int mode, CurvatureData cData, PopulationData pData){
		
		for(int i=0 ; i<cData.getNumberOfYears() ; i++) 
			cData.setRMSE(i, this.calculateAvgRMSE(mode, cData.getYear(i), cData, pData));
	}
	
	public double calculateAvgRMSE(int mode, int year, CurvatureData cData, PopulationData pData){
		/*** mode: [1]: in-flow, [2]: out-flow, [4]: in./cap., [5]: out./cap. ***/
		int i, j;
		int pressureYearIndex = cData.getYearIndex(year);
		int migrationYearIndex = this.yearList.indexOf(year);
		int districtIndex;
		double tmpPressure, tmpMigration, tmpPopulation, tmpError;
		double maxPressure = 0.0;
		double maxMigration = 0.0;
		double rmse = 0.0;
		double count = 0.0;
		boolean check;
		
		/*** find max values ***/
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			if(mode <3) tmpPressure = Math.abs(cData.getPressure(pressureYearIndex, i));
			else tmpPressure = Math.abs(cData.getCurvature(pressureYearIndex, i));
			if(maxPressure < tmpPressure) maxPressure = Math.abs(tmpPressure);
		}
		
		if(mode == 1){
			for(i=0 ; i<this.numberOfDistricts ; i++)
				if(maxMigration < Math.abs(this.inFlowsShifted[migrationYearIndex][i]))
					maxMigration = Math.abs(this.inFlowsShifted[migrationYearIndex][i]);
		}
		else if(mode == 2){
			for(i=0 ; i<this.numberOfDistricts ; i++)
				if(maxMigration < Math.abs(this.outFlowsShifted[migrationYearIndex][i])) 
					maxMigration = Math.abs(this.outFlowsShifted[migrationYearIndex][i]);
		}
		else System.err.println("this mode "+mode+" do not fit with the average-RMSE.");
		
		/*** calculate RMSE ***/
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((cData.getDistrictName(i)));
			if(districtIndex >= 0 && pData.getPopulation(pressureYearIndex, i) > 0
					&& this.inFlows[migrationYearIndex][districtIndex] >= 0.0
					&& this.outFlows[migrationYearIndex][districtIndex] >= 0.0){
				if(mode == 1) tmpMigration = this.inFlowsShifted[migrationYearIndex][districtIndex];
				else if(mode == 2) tmpMigration = this.outFlowsShifted[migrationYearIndex][districtIndex];
				else tmpMigration = Double.NaN;
				
				tmpPressure = cData.getPressure(pressureYearIndex, i);
				if(mode == 2) tmpPressure *= -1.0;
				
				tmpError = tmpPressure / maxPressure - tmpMigration / maxMigration;
				rmse += Math.pow(tmpError, 2);
				count++;
			}
		}
		
		/*** calculate fragmented regions' RMSE ***/
		for(i=0 ; i<this.numberOfDistricts ; i++){
			check = false;
			tmpPressure = 0.0;
			for(j=0 ; j<cData.getDistricNumber() ; j++){
				if(this.getDistrictIndex(cData.getDistrictName(j))<0 
						&& cData.getDistrictName(j).startsWith(this.districtList.get(i))
						&& pData.getPopulation(pressureYearIndex, j)>0){
					tmpPressure += cData.getPressure(pressureYearIndex, j);			
					check = true;
				}
			}
			if(check){
				 if(mode == 1) tmpMigration = this.inFlowsShifted[migrationYearIndex][i];
				else if(mode == 2) tmpMigration = this.outFlowsShifted[migrationYearIndex][i];
				else tmpMigration = Double.NaN;
				
				if(mode == 2) tmpPressure *= -1.0;
				
				tmpError = tmpPressure / maxPressure - tmpMigration / maxMigration;
				rmse += Math.pow(tmpError, 2);
				count++;
			}
		}
		
	//	System.out.println("RMSE: "+year+"\t"+maxPressure+"\t"+maxMigration+"\t"+rmse+"\t"+count);
		return Math.sqrt(rmse/count);
	}
	
	public double calculateSqrtRMSE(int mode, int year, CurvatureData cData, EntropyData eData){
		/*** mode: [0]: net-migration, [1]: in-flow, [2]: out-flow [3]: net./cap., [4]: in./cap., [5]: out./cap. ***/
		int i, j;
		int pressureYearIndex = cData.getYearIndex(year);
		int migrationYearIndex = this.yearList.indexOf(year);
		int districtIndex;
		double tmpPressure, tmpMigration, tmpError, tmpFlow;
		double maxPressure = 0.0;
		double maxMigration = 0.0;
		double rmse = 0.0;
		double count = 0.0;
		boolean check;
		
		/*** find max values ***/
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			if(eData.getPopulation(pressureYearIndex, i) > 0){
				tmpPressure = Math.sqrt(Math.abs(cData.getPressure(pressureYearIndex, i)));
				if(maxPressure < tmpPressure) maxPressure = tmpPressure;
			}
		}
		for(i=0 ; i<this.numberOfDistricts ; i++){
			if(this.inFlows[migrationYearIndex][i] >= 0.0 && this.outFlows[migrationYearIndex][i] >= 0.0){
				tmpMigration = Math.sqrt(Math.abs(this.netFlows[migrationYearIndex][i]));
				if(maxMigration < tmpMigration) maxMigration = tmpMigration;
			}
		}
		
		/*** calculate RMSE ***/
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((cData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(pressureYearIndex, i) > 0
					&& this.inFlows[migrationYearIndex][districtIndex] >= 0.0
					&& this.outFlows[migrationYearIndex][districtIndex] >= 0.0){
				if(mode == 0) tmpFlow = this.netFlows[migrationYearIndex][districtIndex];
				else if(mode == 1) tmpFlow = this.inFlows[migrationYearIndex][districtIndex];
				else if(mode == 2) tmpFlow = -1.0 * this.outFlows[migrationYearIndex][districtIndex];
				else tmpFlow = Double.NaN;
				
				tmpPressure = Math.sqrt(Math.abs(cData.getPressure(pressureYearIndex, i)));
				tmpMigration = Math.sqrt(Math.abs(tmpFlow));
				if(cData.getPressure(pressureYearIndex, i) < 0) tmpPressure *= -1.0;			
				if(tmpFlow < 0) tmpMigration *= -1.0;
				tmpError = tmpPressure / maxPressure - tmpMigration / maxMigration;
				rmse += Math.pow(tmpError, 2);
				count++;
			}
		}
		
		/*** calculate fragmented regions' RMSE ***/
		for(i=0 ; i<this.numberOfDistricts ; i++){
			check = false;
			tmpError = 0.0;
			for(j=0 ; j<cData.getDistricNumber() ; j++){
				if(this.getDistrictIndex(cData.getDistrictName(j))<0 
						&& cData.getDistrictName(j).startsWith(this.districtList.get(i))
						&& eData.getPopulation(pressureYearIndex, j)>0){
					tmpError += cData.getPressure(pressureYearIndex, j);			
					check = true;
				}
			}
			if(check){
				if(mode == 0) tmpFlow = this.netFlows[migrationYearIndex][i];
				else if(mode == 1) tmpFlow = this.inFlows[migrationYearIndex][i];
				else if(mode == 2) tmpFlow = -1.0 * this.outFlows[migrationYearIndex][i];
				else tmpFlow = Double.NaN;
				
				tmpPressure = Math.sqrt(Math.abs(tmpError));
				tmpMigration = Math.sqrt(Math.abs(tmpFlow));
				if(tmpError < 0) tmpPressure *= -1.0;			
				if(tmpFlow < 0) tmpMigration *= -1.0;
				tmpError = tmpPressure/maxPressure - tmpMigration/maxMigration;
				rmse += Math.pow(tmpError, 2);
				count++;
			}
		}
		
//		System.out.println("RMSE: "+year+"\t"+maxPressure+"\t"+maxMigration+"\t"+rmse+"\t"+count);
		return Math.sqrt(rmse/count);
	}
	
	public void calculateRsquare(InteractionData iData, EntropyData eData){		
		for(int i=0 ; i<iData.getNumberOfYears() ; i++) 
			iData.setRsquare(i, this.calculateRsquare(iData.getYear(i), iData, eData));
	}
	
	public double[] calculateRsquare(int year, InteractionData iData, EntropyData eData){
		/*** return a double R-square[5] array: 
		 * 		[0] immigration, [1] emigration, [2] net-migration, 
		 * 		[3] immigration rate = immigration / out of the region's population, 
		 * 		[4] emigration rate = emigration / the region's population
		***/
		
		int i, j;
		int count = 0;
		int interactionYearIndex = iData.getYearIndex(year);
		int observedYearIndex = this.yearList.indexOf(year);
		int districtIndex;
		double tmpIn, tmpOut, tmpNet;		//calculated interactions
		double tmpObsIn, tmpObsOut, tmpObsNet, tmpObsInRate, tmpObsOutRate; //observed migrations
		double tmpPopulation, tmpTotalPopulation = 0.0;
		double maxIn = 0.0, maxOut = 0.0, maxNet = 0.0;
		double maxObsIn = 0.0, maxObsOut = 0.0, maxObsNet = 0.0, maxObsInRate = 0.0, maxObsOutRate = 0.0;
		double[] rCorrelation = new double[5];
		double[] rSquare = new double[5];
		boolean check;
		
		double[][] tmpArrayIn, tmpArrayOut, tmpArrayNet, tmpArrayInRate, tmpArrayOutRate;
		RealMatrix corrIn, corrOut, corrNet, corrInRate, corrOutRate;	//correlation
		RealMatrix pValueIn, pValueOut, pValueNet, pValueInRate, pValueOutRate;	//p-value
		PearsonsCorrelation pcIn, pcOut, pcNet, pcInRate, pcOutRate;
		
		ArrayList<Integer> fragObsRegionIndex = null;
		ArrayList<ArrayList<Integer>> fragIntRegIndex = null;
		
		if(this.fragmented[observedYearIndex]){
			fragObsRegionIndex = this.fragmentedRegionIndex.get(observedYearIndex);
			fragIntRegIndex = this.fragmentedIntRegIndex.get(observedYearIndex);
		}
		
		/*** calculate total population ***/
		for(i=0 ; i<iData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((iData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(interactionYearIndex, i) > 0
					&& this.inFlows[observedYearIndex][districtIndex] >= 0 && this.outFlows[observedYearIndex][districtIndex] >= 0)
				tmpTotalPopulation += (double) eData.getPopulation(interactionYearIndex, i);
		}
		if(this.fragmented[observedYearIndex])
			for(i=0 ; i<fragIntRegIndex.size() ; i++)
				for(j=0 ; j<fragIntRegIndex.get(i).size() ; j++)
					tmpTotalPopulation += (double) eData.getPopulation(interactionYearIndex, fragIntRegIndex.get(i).get(j));
		
		/*** find max values ***/
		for(i=0 ; i<iData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((iData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(interactionYearIndex, i) > 0
					&& this.inFlows[observedYearIndex][districtIndex] >= 0 && this.outFlows[observedYearIndex][districtIndex] >= 0){
				tmpIn = iData.getMigrationIn(interactionYearIndex, i);
				tmpOut = iData.getMigrationOut(interactionYearIndex, i);
				tmpNet = Math.abs(iData.getMigrationNet(interactionYearIndex, i));
				if(maxIn < tmpIn) maxIn = tmpIn;
				if(maxOut < tmpOut) maxOut = tmpOut;
				if(maxNet < tmpNet) maxNet = tmpNet;
				
				tmpPopulation = (double) eData.getPopulation(interactionYearIndex, i);
				
				tmpObsIn = (double) this.inFlows[observedYearIndex][districtIndex];
				tmpObsOut = (double) this.outFlows[observedYearIndex][districtIndex];			
				tmpObsNet = (double) Math.abs(this.netFlows[observedYearIndex][districtIndex]);	
				tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
				tmpObsOutRate = tmpObsOut / tmpPopulation;	
				if(maxObsIn < tmpObsIn) maxObsIn = tmpObsIn;
				if(maxObsOut < tmpObsOut) maxObsOut = tmpObsOut;
				if(maxObsNet < tmpObsNet) maxObsNet = tmpObsNet;
				if(maxObsInRate < tmpObsInRate) maxObsInRate = tmpObsInRate;
				if(maxObsOutRate < tmpObsOutRate) maxObsOutRate = tmpObsOutRate;
				count++;
			}
		}
		if(this.fragmented[observedYearIndex]){
			for(i=0 ; i<fragObsRegionIndex.size() ; i++){
				tmpIn = 0.0;
				tmpOut = 0.0;
				tmpNet = 0.0;
				tmpPopulation = 0.0;
				for(j=0 ; j<fragIntRegIndex.get(i).size() ; j++){
					districtIndex = fragIntRegIndex.get(i).get(j);
					tmpIn += iData.getMigrationIn(interactionYearIndex, districtIndex);
					tmpOut += iData.getMigrationOut(interactionYearIndex, districtIndex);
					tmpNet += Math.abs(iData.getMigrationNet(interactionYearIndex, districtIndex));
					tmpPopulation += (double) eData.getPopulation(interactionYearIndex, districtIndex);
				}
				if(maxIn < tmpIn) maxIn = tmpIn;
				if(maxOut < tmpOut) maxOut = tmpOut;
				if(maxNet < tmpNet) maxNet = tmpNet;
				
				districtIndex = fragObsRegionIndex.get(i);
				tmpObsIn = (double) this.inFlows[observedYearIndex][districtIndex];
				tmpObsOut = (double) this.outFlows[observedYearIndex][districtIndex];			
				tmpObsNet = (double) Math.abs(this.netFlows[observedYearIndex][districtIndex]);	
				tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
				tmpObsOutRate = tmpObsOut / tmpPopulation;	
				if(maxObsIn < tmpObsIn) maxObsIn = tmpObsIn;
				if(maxObsOut < tmpObsOut) maxObsOut = tmpObsOut;
				if(maxObsNet < tmpObsNet) maxObsNet = tmpObsNet;
				if(maxObsInRate < tmpObsInRate) maxObsInRate = tmpObsInRate;
				if(maxObsOutRate < tmpObsOutRate) maxObsOutRate = tmpObsOutRate;
				count++;
			}
		}
		
		/*** initiate arrays ***/
		tmpArrayIn = new double[count][2];
		tmpArrayOut = new double[count][2];
		tmpArrayNet = new double[count][2];
		tmpArrayInRate = new double[count][2];
		tmpArrayOutRate = new double[count][2];
		
		/*** assemble interaction and observed migration values ***/		
		count = 0;
		for(i=0 ; i<iData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((iData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(interactionYearIndex, i) > 0
					&& this.inFlows[observedYearIndex][districtIndex] >= 0.0 && this.outFlows[observedYearIndex][districtIndex] >= 0.0){
				tmpIn = iData.getMigrationIn(interactionYearIndex, i);
				tmpOut = iData.getMigrationOut(interactionYearIndex, i);
				tmpNet = iData.getMigrationNet(interactionYearIndex, i);
				tmpObsIn = (double) this.inFlows[observedYearIndex][districtIndex];
				tmpObsOut = (double) this.outFlows[observedYearIndex][districtIndex];
				tmpObsNet = (double) this.netFlows[observedYearIndex][districtIndex];
				
				tmpPopulation = (double) eData.getPopulation(interactionYearIndex, i);
				tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
				tmpObsOutRate = tmpObsOut / tmpPopulation;	
				
				tmpArrayIn[count][0] = tmpIn / maxIn;
				tmpArrayIn[count][1] = tmpObsIn / maxObsIn;
				tmpArrayOut[count][0] = tmpOut / maxOut;
				tmpArrayOut[count][1] = tmpObsOut / maxObsOut;
				tmpArrayNet[count][0] = tmpNet / maxNet;
				tmpArrayNet[count][1] = tmpObsNet / maxObsNet;
				tmpArrayInRate[count][0] = tmpIn / maxIn;
				tmpArrayInRate[count][1] = tmpObsInRate / maxObsInRate;
				tmpArrayOutRate[count][0] = tmpOut / maxOut;
				tmpArrayOutRate[count][1] = tmpObsOutRate / maxObsOutRate;
				count++;
			}
		}
			
		/*** assemble fragmented regions' values ***/
		if(this.fragmented[observedYearIndex]){
			for(i=0 ; i<fragIntRegIndex.size() ; i++){
				tmpIn = 0.0;
				tmpOut = 0.0;
				tmpNet = 0.0;
				tmpPopulation = 0.0;
				for(j=0 ; j<fragIntRegIndex.get(i).size() ; j++){
					districtIndex = fragIntRegIndex.get(i).get(j);
					tmpIn += iData.getMigrationIn(interactionYearIndex, districtIndex);
					tmpOut += iData.getMigrationOut(interactionYearIndex, districtIndex);
					tmpNet += iData.getMigrationNet(interactionYearIndex, districtIndex);
					tmpPopulation += (double) eData.getPopulation(interactionYearIndex, districtIndex);
				}
				districtIndex = fragObsRegionIndex.get(i);
				tmpObsIn = (double) this.inFlows[observedYearIndex][districtIndex];
				tmpObsOut = (double) this.outFlows[observedYearIndex][districtIndex];			
				tmpObsNet = (double) this.netFlows[observedYearIndex][districtIndex];	
				tmpObsInRate = tmpObsIn / (tmpTotalPopulation - tmpPopulation);
				tmpObsOutRate = tmpObsOut / tmpPopulation;	
					
				tmpArrayIn[count][0] = tmpIn / maxIn;
				tmpArrayIn[count][1] = tmpObsIn / maxObsIn;
				tmpArrayOut[count][0] =tmpOut / maxOut;
				tmpArrayOut[count][1] = tmpObsOut / maxObsOut;
				tmpArrayNet[count][0] = tmpNet / maxNet;
				tmpArrayNet[count][1] = tmpObsNet / maxObsNet;
				tmpArrayInRate[count][0] = tmpIn / maxIn;
				tmpArrayInRate[count][1] = tmpObsInRate / maxObsInRate;
				tmpArrayOutRate[count][0] = tmpOut / maxOut;
				tmpArrayOutRate[count][1] = tmpObsOutRate / maxObsOutRate;
				count++;
			}
		}

		/*** calculate R  correlation ***/
		pcIn =  new PearsonsCorrelation(tmpArrayIn);
		pcOut =  new PearsonsCorrelation(tmpArrayOut);
		pcNet =  new PearsonsCorrelation(tmpArrayNet);
		pcInRate =  new PearsonsCorrelation(tmpArrayInRate);
		pcOutRate =  new PearsonsCorrelation(tmpArrayOutRate);
		
		corrIn = pcIn.getCorrelationMatrix();
		corrOut = pcOut.getCorrelationMatrix();
		corrNet = pcNet.getCorrelationMatrix();
		corrInRate = pcInRate.getCorrelationMatrix();
		corrOutRate = pcOutRate.getCorrelationMatrix();
		
		pValueIn = pcIn.getCorrelationPValues();
		pValueOut = pcOut.getCorrelationPValues();
		pValueNet = pcNet.getCorrelationPValues();
		pValueInRate = pcInRate.getCorrelationPValues();
		pValueOutRate = pcOutRate.getCorrelationPValues();
		
		/*** return R-square correlation ***/
		rCorrelation[0] = corrIn.getEntry(0, 1);
		rCorrelation[1] = corrOut.getEntry(0, 1);
		rCorrelation[2] = corrNet.getEntry(0, 1);
		rCorrelation[3] = corrInRate.getEntry(0, 1);
		rCorrelation[4] = corrOutRate.getEntry(0, 1);
		for(i=0 ; i<5 ; i++) rSquare[i] = Math.pow(rCorrelation[i], 2);
		
		return rSquare;
	}

	public void calculateRsquare(int mode, CurvatureData cData, EntropyData eData){
		
		for(int i=0 ; i<cData.getNumberOfYears() ; i++) 
			cData.setRsquare(i, this.calculateRsquare(mode, cData.getYear(i), cData, eData));
	}
	
	public double calculateRsquare(int mode, int year, CurvatureData cData, EntropyData eData){
		/*** mode: [0]: net-migration, [1]: in-flow, [2]: out-flow [3]: net./cap., [4]: in./cap., [5]: out./cap. ***/
		int i, j;
		int count = 0;
		int pressureYearIndex = cData.getYearIndex(year);
		int migrationYearIndex = this.yearList.indexOf(year);
		int districtIndex;
		double tmpPressure, tmpMigration;
		double maxPressure = 0.0;
		double maxMigration = 0.0;
		boolean check;
		
		double[][] tmpArrays;
		RealMatrix tmpCorrelation;
		RealMatrix tmpPvalue;
		PearsonsCorrelation pc;
		
		/*** find max values ***/
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			tmpPressure = Math.abs(cData.getPressure(pressureYearIndex, i));
			if(maxPressure < tmpPressure) maxPressure = tmpPressure;
		}
		for(i=0 ; i<this.numberOfDistricts ; i++){
			tmpMigration = Math.abs(this.netFlows[migrationYearIndex][i]);
			if(maxMigration < tmpMigration) maxMigration = tmpMigration;
		}
		
		/*** determine size of arrays ***/
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((cData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(pressureYearIndex, i) > 0
					&& this.inFlows[migrationYearIndex][districtIndex] >= 0.0
					&& this.outFlows[migrationYearIndex][districtIndex] >= 0.0) count++;
		}
		for(i=0 ; i<this.numberOfDistricts ; i++){
			check = false;
			for(j=0 ; j<cData.getDistricNumber() ; j++)
				if(this.getDistrictIndex(cData.getDistrictName(j))<0 
					&& cData.getDistrictName(j).startsWith(this.districtList.get(i))
					&& eData.getPopulation(pressureYearIndex, j)>0) check = true;
			if(check) count++;
		}
		tmpArrays = new double[count][2];
		
		/*** assemble attraction and migration values ***/
		count = 0;
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((cData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(pressureYearIndex, i) > 0
					&& this.inFlows[migrationYearIndex][districtIndex] >= 0.0
					&& this.outFlows[migrationYearIndex][districtIndex] >= 0.0){
				if(mode == 0) tmpMigration = this.netFlows[migrationYearIndex][districtIndex];
				else if(mode == 1) tmpMigration = this.inFlows[migrationYearIndex][districtIndex];
				else if(mode == 2) tmpMigration = this.outFlows[migrationYearIndex][districtIndex];
				else tmpMigration = Double.NaN;
				
				tmpArrays[count][0] = cData.getPressure(pressureYearIndex, i) / maxPressure;
				tmpArrays[count][1] = tmpMigration / maxMigration;
				count++;
			}
		}
		
		/*** assemble fragmented regions' values ***/
		for(i=0 ; i<this.numberOfDistricts ; i++){
			check = false;
			tmpPressure = 0.0;
			for(j=0 ; j<cData.getDistricNumber() ; j++){
				if(this.getDistrictIndex(cData.getDistrictName(j))<0 
						&& cData.getDistrictName(j).startsWith(this.districtList.get(i))
						&& eData.getPopulation(pressureYearIndex, j)>0){
					tmpPressure += cData.getPressure(pressureYearIndex, j);			
					check = true;
				}
			}
			if(check){
				if(mode == 0) tmpMigration = this.netFlows[migrationYearIndex][i];
				else if(mode == 1) tmpMigration = this.inFlows[migrationYearIndex][i];
				else if(mode == 2) tmpMigration = this.outFlows[migrationYearIndex][i];
				else tmpMigration = Double.NaN;
				
				tmpArrays[count][0] = tmpPressure / maxPressure;
				tmpArrays[count][1] = tmpMigration / maxMigration;
				count++;
			}
		}

		/*** calculate R square correlation ***/
		pc =  new PearsonsCorrelation(tmpArrays);
		tmpCorrelation = pc.getCorrelationMatrix();
		tmpPvalue = pc.getCorrelationPValues();
		
		/*** return R-square ***/
		return Math.pow(tmpCorrelation.getEntry(0, 1), 2);
	}
	
	public void calculateNormalizedRsquare(InteractionData iData, EntropyData eData){
		/*** set double R-square[5] arrays: 
		 * 		[0] immigration, [1] emigration, [2] net-migration, 
		 * 		[3] immigration rate = immigration / out of the region's population, 
		 * 		[4] emigration rate = emigration / the region's population
		***/
		for(int i=0 ; i<iData.getNumberOfYears() ; i++) 
			iData.setRsquare(i, this.calculateNormalizedRsquare(iData.getYear(i), iData, eData));
	}
	
	public double[] calculateNormalizedRsquare(int year, InteractionData iData, EntropyData eData){
		/*** set a double R-square[5] array: 
		 * 		[0] immigration, [1] emigration, [2] net-migration, 
		 * 		[3] immigration rate = immigration / out of the region's population, 
		 * 		[4] emigration rate = emigration / the region's population
		***/
		
		int i, j;
		int count = 0;
		int interactionYearIndex = iData.getYearIndex(year);
		int observedYearIndex = this.yearList.indexOf(year);
		int districtIndex;
		double tmpIn, tmpOut, tmpNet;		//calculated interactions
		double[] rCorrelation = new double[5];
		double[] rSquare = new double[5];
		
		double[][] tmpArrayIn, tmpArrayOut, tmpArrayNet, tmpArrayInRate, tmpArrayOutRate;
		RealMatrix corrIn, corrOut, corrNet, corrInRate, corrOutRate;	//correlation
		RealMatrix pValueIn, pValueOut, pValueNet, pValueInRate, pValueOutRate;	//p-value
		PearsonsCorrelation pcIn, pcOut, pcNet, pcInRate, pcOutRate;
		
		ArrayList<Integer> fragObsRegionIndex = null;
		ArrayList<ArrayList<Integer>> fragIntRegIndex = null;
		
		if(this.fragmented[observedYearIndex]){
			fragObsRegionIndex = this.fragmentedRegionIndex.get(observedYearIndex);
			fragIntRegIndex = this.fragmentedIntRegIndex.get(observedYearIndex);
		}
		
		/*** determine size of arrays ***/
		for(i=0 ; i<iData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((iData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(interactionYearIndex, i) > 0
					&& this.inFlows[observedYearIndex][districtIndex] >= 0 && this.outFlows[observedYearIndex][districtIndex] >= 0)
				count++;
		}
		if(this.fragmented[observedYearIndex])
			for(i=0 ; i<fragIntRegIndex.size() ; i++)
				for(j=0 ; j<fragIntRegIndex.get(i).size() ; j++)
					count++;
		tmpArrayIn = new double[count][2];
		tmpArrayOut = new double[count][2];
		tmpArrayNet = new double[count][2];
		tmpArrayInRate = new double[count][2];
		tmpArrayOutRate = new double[count][2];
		
		/*** assemble interaction and observed migration values ***/
		count = 0;
		for(i=0 ; i<iData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((iData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(interactionYearIndex, i) > 0
					&& this.inFlows[observedYearIndex][districtIndex] >= 0.0 && this.outFlows[observedYearIndex][districtIndex] >= 0.0){
				tmpArrayIn[count][0] = iData.getMigrationIn(interactionYearIndex, i);
				tmpArrayIn[count][1] = this.inFlows_norm[observedYearIndex][districtIndex];
				tmpArrayOut[count][0] = iData.getMigrationOut(interactionYearIndex, i);
				tmpArrayOut[count][1] = this.outFlows_norm[observedYearIndex][districtIndex];
				tmpArrayNet[count][0] = iData.getMigrationNet(interactionYearIndex, i);
				tmpArrayNet[count][1] = this.netFlows_norm[observedYearIndex][districtIndex];
				tmpArrayInRate[count][0] = iData.getMigrationIn(interactionYearIndex, i);
				tmpArrayInRate[count][1] = this.inFlowsRate_norm[observedYearIndex][districtIndex];
				tmpArrayOutRate[count][0] = iData.getMigrationOut(interactionYearIndex, i);
				tmpArrayOutRate[count][1] = this.outFlowsRate_norm[observedYearIndex][districtIndex];
				count++;
			}
		}
		
		/*** assemble fragmented regions' values ***/
		if(this.fragmented[observedYearIndex]){
			for(i=0 ; i<fragIntRegIndex.size() ; i++){
				tmpIn = 0.0;
				tmpOut = 0.0;
				tmpNet = 0.0;
				for(j=0 ; j<fragIntRegIndex.get(i).size() ; j++){
					districtIndex = fragIntRegIndex.get(i).get(j);
					tmpIn += iData.getMigrationIn(interactionYearIndex, districtIndex);
					tmpOut += iData.getMigrationOut(interactionYearIndex, districtIndex);
					tmpNet += iData.getMigrationNet(interactionYearIndex, districtIndex);
				}
				districtIndex = fragObsRegionIndex.get(i);
				tmpArrayIn[count][0] = tmpIn;
				tmpArrayIn[count][1] = this.inFlows_norm[observedYearIndex][districtIndex];
				tmpArrayOut[count][0] =tmpOut;
				tmpArrayOut[count][1] = this.outFlows_norm[observedYearIndex][districtIndex];
				tmpArrayNet[count][0] = tmpNet;
				tmpArrayNet[count][1] = this.netFlows_norm[observedYearIndex][districtIndex];
				tmpArrayInRate[count][0] = tmpIn;
				tmpArrayInRate[count][1] = this.inFlowsRate_norm[observedYearIndex][districtIndex];
				tmpArrayOutRate[count][0] = tmpOut;
				tmpArrayOutRate[count][1] = this.outFlowsRate_norm[observedYearIndex][districtIndex];
				count++;
			}
		}
		
		/*
		for(i=0 ; i<this.numberOfDistricts ; i++){
			check = false;
			tmpIn = 0.0;
			tmpOut = 0.0;
			tmpNet = 0.0;
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				if(this.getDistrictIndex(iData.getDistrictName(j))<0 
						&& iData.getDistrictName(j).startsWith(this.districtList.get(i))
						&& eData.getPopulation(interactionYearIndex, j)>0){
					tmpIn += iData.getMigrationIn(interactionYearIndex, j);		
					tmpOut += iData.getMigrationOut(interactionYearIndex, j);		
					tmpNet += iData.getMigrationNet(interactionYearIndex, j);	
					check = true;
				}
			}
			if(check){
				tmpArrayIn[count][0] = tmpIn ;
				tmpArrayIn[count][1] = this.inFlows_norm[observedYearIndex][i];
				tmpArrayOut[count][0] =tmpOut;
				tmpArrayOut[count][1] = this.outFlows_norm[observedYearIndex][i];
				tmpArrayNet[count][0] = tmpNet;
				tmpArrayNet[count][1] = this.netFlows_norm[observedYearIndex][i];
				tmpArrayInRate[count][0] = tmpIn;
				tmpArrayInRate[count][1] = this.inFlowsRate_norm[observedYearIndex][i];
				tmpArrayOutRate[count][0] = tmpOut;
				tmpArrayOutRate[count][1] = this.outFlowsRate_norm[observedYearIndex][i];
				
				count++;
			}
		}
		 */
		
		/*** calculate R  correlation ***/
		pcIn =  new PearsonsCorrelation(tmpArrayIn);
		pcOut =  new PearsonsCorrelation(tmpArrayOut);
		pcNet =  new PearsonsCorrelation(tmpArrayNet);
		pcInRate =  new PearsonsCorrelation(tmpArrayInRate);
		pcOutRate =  new PearsonsCorrelation(tmpArrayOutRate);
		
		corrIn = pcIn.getCorrelationMatrix();
		corrOut = pcOut.getCorrelationMatrix();
		corrNet = pcNet.getCorrelationMatrix();
		corrInRate = pcInRate.getCorrelationMatrix();
		corrOutRate = pcOutRate.getCorrelationMatrix();
		
		pValueIn = pcIn.getCorrelationPValues();
		pValueOut = pcOut.getCorrelationPValues();
		pValueNet = pcNet.getCorrelationPValues();
		pValueInRate = pcInRate.getCorrelationPValues();
		pValueOutRate = pcOutRate.getCorrelationPValues();
		
		/*** return R-square correlation ***/
		rCorrelation[0] = corrIn.getEntry(0, 1);
		rCorrelation[1] = corrOut.getEntry(0, 1);
		rCorrelation[2] = corrNet.getEntry(0, 1);
		rCorrelation[3] = corrInRate.getEntry(0, 1);
		rCorrelation[4] = corrOutRate.getEntry(0, 1);
		for(i=0 ; i<5 ; i++) rSquare[i] = Math.pow(rCorrelation[i], 2);
		
		return rSquare;
	}
	
	public void calculateNormalizedRsquare(int mode, CurvatureData cData, EntropyData eData){
		
		for(int i=0 ; i<cData.getNumberOfYears() ; i++) 
			cData.setRsquare(i, this.calculateNormalizedRsquare(mode, cData.getYear(i), cData, eData));
	}
	
	public double calculateNormalizedRsquare(int mode, int year, CurvatureData cData, EntropyData eData){
		/*** mode: [0]: net-migration, [1]: in-flow, [2]: out-flow ***/
		int i, j;
		int count = 0;
		int pressureYearIndex = cData.getYearIndex(year);
		int migrationYearIndex = this.yearList.indexOf(year);
		int districtIndex;
		double tmpMigration;
		
		double[][] tmpArrays;
		RealMatrix tmpCorrelation;
		RealMatrix tmpPvalue;
		PearsonsCorrelation pc;
		
		/*** determine size of arrays ***/
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((cData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(pressureYearIndex, i) > 0
					&& this.inFlows[migrationYearIndex][districtIndex] >= 0
					&& this.outFlows[migrationYearIndex][districtIndex] >= 0) count++;
		}
		tmpArrays = new double[count][2];
		
		/*** assemble attraction and migration values ***/
		count = 0;
		for(i=0 ; i<cData.getDistricNumber() ; i++){
			districtIndex = this.getDistrictIndex((cData.getDistrictName(i)));
			if(districtIndex >= 0 && eData.getPopulation(pressureYearIndex, i) > 0
					&& this.inFlows[migrationYearIndex][districtIndex] >= 0
					&& this.outFlows[migrationYearIndex][districtIndex] >= 0){
				if(mode == 0) tmpMigration = this.netFlows_norm[migrationYearIndex][districtIndex];
				else if(mode == 1) tmpMigration = this.inFlows_norm[migrationYearIndex][districtIndex];
				else if(mode == 2) tmpMigration = this.outFlows_norm[migrationYearIndex][districtIndex];
				else tmpMigration = Double.NaN;
				
				tmpArrays[count][0] = cData.getPressure(pressureYearIndex, i);
				tmpArrays[count][1] = tmpMigration;
				count++;
			}
		}

		/*** calculate R square correlation ***/
		pc =  new PearsonsCorrelation(tmpArrays);
		tmpCorrelation = pc.getCorrelationMatrix();
		tmpPvalue = pc.getCorrelationPValues();
		
		/*** return R-square ***/
		return Math.pow(tmpCorrelation.getEntry(0, 1), 2);
	}
	
	public void printResults(int mode, String outputFile, InteractionData iData, EntropyData eData){
		/*** mode: [0] normal (+1 year), [1] current year ***/		
		for(int i=0 ; i<iData.getNumberOfYears() ; i++)
			this.printResults(mode, iData.getYear(i), outputFile.replace(".txt", "_"+iData.getYear(i)+".txt"), iData, eData);
	}
	
	public void printResults(int mode, int year, String outputFile, InteractionData iData, EntropyData eData){
		/*** mode: [0] normal (+1 year), [1] current year ***/
		int i, j, k, l;
		int interactionIdx = iData.getYearIndex(year);					//Year index of calculated interaction data
		int observedIdx = this.yearList.indexOf(year+1-mode);	//Year index of observed migration data
		int districtIdx;		//Index of district
		int endYear = this.yearList.get(this.numberOfYears-1);
		boolean check5y;
		double netSum, inSum, outSum;
		double tmpPopulation, totalPopulation;
		String tmpStr;
		ArrayList<Integer> fragObsRegionIndex;
		ArrayList<ArrayList<Integer>> fragIntRegIndex;
				
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			/*
			pw.println("Total immigration: "+iData.getTotalIn(interactionIdx));
			pw.println("Total emigration: "+iData.getTotalOut(interactionIdx));
			pw.println("Total net-migration: "+iData.getTotalNet(interactionIdx));
			if(iData.checkRMSE()){
				pw.println("RMSE between in-migration potential and observed immigration: "+iData.getRmseIn(interactionIdx));
				pw.println("RMSE between out-migration potential and observed emigration: "+iData.getRmseOut(interactionIdx));
				pw.println("RMSE between net-migration potential and observed net-migration: "+iData.getRmseNet(interactionIdx));
				pw.println("RMSE between in-migration potential and observed immigration rate: "+iData.getRmseInRate(interactionIdx));
				pw.println("RMSE between out-migration potential and observed emigration rate: "+iData.getRmseOutRate(interactionIdx));
			}
			if(iData.checkRsquare()){
				pw.println("R-square between in-migration potential and observed immigration: "+iData.getRsquareIn(interactionIdx));
				pw.println("R-square between out-migration potential and observed emigration: "+iData.getRsquareOut(interactionIdx));
				pw.println("R-square between net-migration potential and observed net-migration: "+iData.getRsquareNet(interactionIdx));
				pw.println("R-square between in-migration potential and observed immigration: "+iData.getRsquareInRate(interactionIdx));
				pw.println("R-square between out-migration potential and observed emigration: "+iData.getRsquareOutRate(interactionIdx));
			}
			pw.println();
			*/
			
			//calculate total population
			/*
			totalPopulation = 0.0;
			for(i=0 ; i<iData.getDistricNumber() ; i++){
				districtIdx = this.getDistrictIndex((iData.getDistrictName(i)));
				if(districtIdx >= 0 && eData.getPopulation(interactionIdx, i) > 0 && year+1 <= endYear){
					check1y = (this.inFlows[observedIdx][districtIdx]>0 || this.outFlows[observedIdx][districtIdx]>0);
					tmpStr = this.districtList.get(districtIdx);
					tmpStr = tmpStr.substring(0, tmpStr.length()-1);
					for(k=0 ; !check1y && k<this.numberOfDistricts ; k++){
						if(this.districtList.get(k).startsWith(tmpStr)
								&& (this.inFlows[observedIdx][k] > 0 || this.outFlows[observedIdx][k] > 0)){
							districtIdx = k;	
							check1y = true;
						}
					}
					if(check1y) totalPopulation += (double) eData.getPopulation(interactionIdx, i);
				}
			}
			*/
				
			totalPopulation = this.totalPopulation[observedIdx];
			
			pw.print("Region\tIn-migration\tOut-migration\t");
			pw.print("Population\tEntropy\tAged-Young_Index\tAge-structure_Index\t");
			pw.print("Immigration\tEmigration\tImmigration_rate\tEmigration_rate\t");
			pw.print("Immigration_norm\tEmigration_norm\tImmigration_rate_norm\tEmigration_rate_norm\t");
			if(year+6-mode <= endYear) 
				pw.print("5y_immigration\t5y_emigration\t5y_immigration_rate\t5y_emigration_rate");
			pw.println();
			
			for(i=0 ; i<iData.getDistricNumber() ; i++){
				observedIdx = this.yearList.indexOf(year+1-mode);
				districtIdx = this.getDistrictIndex((iData.getDistrictName(i)));
				fragObsRegionIndex = this.fragmentedRegionIndex.get(observedIdx);
				fragIntRegIndex = this.fragmentedIntRegIndex.get(observedIdx);
				
				if(this.fragmented[observedIdx])
					for(j=0 ; j<fragIntRegIndex.size() ; j++)
						for(k=0 ; k<fragIntRegIndex.get(j).size() ; k++)
							if(i == fragIntRegIndex.get(j).get(k)) districtIdx = fragObsRegionIndex.get(j);
				
				if(districtIdx >= 0 && eData.getPopulation(interactionIdx, i) > 0 && year+1-mode <= endYear){
					if(this.inFlows[observedIdx][districtIdx]>0 || this.outFlows[observedIdx][districtIdx]>0){
						tmpPopulation = (double) eData.getPopulation(interactionIdx, i);
						pw.print(iData.getDistrictName(i)+"\t"
								+iData.getMigrationIn(interactionIdx, i)+"\t"
								+iData.getMigrationOut(interactionIdx, i)+"\t"
								+eData.getPopulation(interactionIdx, i)+"\t"
								+eData.getEntropy(interactionIdx, i)+"\t"
								+eData.getAgedIndex(interactionIdx, i)+"\t"
								+iData.getAgeStructureIndex(interactionIdx, i)+"\t"
								
								+this.inFlows[observedIdx][districtIdx]+"\t"
								+this.outFlows[observedIdx][districtIdx]+"\t"
								+this.inFlowsRate[observedIdx][districtIdx]+"\t"
								+this.outFlowsRate[observedIdx][districtIdx]+"\t"
								
								+this.inFlows_norm[observedIdx][districtIdx]+"\t"
								+this.outFlows_norm[observedIdx][districtIdx]+"\t"
								+this.inFlowsRate_norm[observedIdx][districtIdx]+"\t"
								+this.outFlowsRate_norm[observedIdx][districtIdx]					
								);	
					
						if(year+6-mode <= endYear){
							inSum = 0.0;
							outSum = 0.0;
							netSum = 0.0;
							tmpStr = "";
							for(j=0, check5y = true; check5y && j<5 ; j++){
								observedIdx = this.yearList.indexOf(j+year+1-mode);
								districtIdx = this.getDistrictIndex((iData.getDistrictName(i)));
								
								check5y = (this.inFlows[observedIdx][districtIdx]>0.0||this.outFlows[observedIdx][districtIdx]>0.0);
								
								if(!check5y){
									if(this.fragmented[observedIdx]){
										fragObsRegionIndex = this.fragmentedRegionIndex.get(observedIdx);
										fragIntRegIndex = this.fragmentedIntRegIndex.get(observedIdx);
										
										for(k=0 ; k<fragIntRegIndex.size() ; k++)
											for(l=0 ; l<fragIntRegIndex.get(k).size() ; l++)
												if(i == fragIntRegIndex.get(k).get(l))
													districtIdx = fragObsRegionIndex.get(k);
	
										check5y = (this.inFlows[observedIdx][districtIdx]>0.0||this.outFlows[observedIdx][districtIdx]>0.0);
									}
									if(check5y) tmpStr = this.districtList.get(districtIdx);
								}
								if(!check5y && !tmpStr.isEmpty()){
									districtIdx = this.getDistrictIndex(tmpStr);
									check5y = (this.inFlows[observedIdx][districtIdx]>0.0||this.outFlows[observedIdx][districtIdx]>0.0);
								}
								
								if(check5y){
									inSum += this.inFlows[observedIdx][districtIdx];
									outSum += this.outFlows[observedIdx][districtIdx];
						//			netSum += this.netFlows[observedIdx][districtIdx];
								}
							}
							if(check5y){
								tmpPopulation = (double) eData.getPopulation(interactionIdx, i);
								pw.print("\t"
										+inSum+"\t"
										+outSum+"\t"
						//				+netSum+"\t"
										+inSum / (totalPopulation - tmpPopulation)+"\t"
										+outSum / tmpPopulation );
							}
						}
						pw.println();
					}
				}
			}
			/*
			for(i=0 ; i<this.numberOfDistricts ; i++){
				check5y = false;
				inSum = 0.0;
				outSum = 0.0;
				netSum = 0.0;
				tmpPopulation = 0.0;
				for(j=0 ; j<iData.getDistricNumber() ; j++){
					if(this.getDistrictIndex(iData.getDistrictName(j))<0 
							&& iData.getDistrictName(j).startsWith(this.districtList.get(i))
							&& eData.getPopulation(interactionIdx, j)>0){
						inSum += iData.getMigrationIn(interactionIdx, j);
						outSum += iData.getMigrationOut(interactionIdx, j);
						netSum += iData.getMigrationNet(interactionIdx, j);
						tmpPopulation += (double) eData.getPopulation(interactionIdx, j);
						check5y = true;
					}
				}
				if(check5y && tmpPopulation > 0
						&&( this.inFlows[observedIdx][i] > 0.0 ||  this.outFlows[observedIdx][i] > 0.0)){
					pw.println(iData.getDistrictName(i)+"\t"
							+inSum+"\t"
							+outSum+"\t"
					//		+netSum+"\t"
							+tmpPopulation+"\t"	
							+"\t"+"\t"	
							+"\t"+"\t"
							+"\t"+"\t"
							+this.inFlows[observedIdx][i]+"\t"
							+this.outFlows[observedIdx][i]+"\t"
				//			+this.netFlows[observedIdx][i]+"\t"
							+this.inFlows[observedIdx][i] / (totalPopulation - tmpPopulation)+"\t"
							+this.outFlows[observedIdx][i] / tmpPopulation 
							);	
				}
			}
			*/
			pw.close();
		}catch(IOException e) {}
		
		/*
		int observedYearIndex, districtIndex;
		
		for(i=0 ; i<iData.getNumberOfYears() ; i++){
			observedYearIndex = this.yearList.indexOf(iData.getYear(i));
			for(j=0 ; j<iData.getDistricNumber() ; j++){
				districtIndex = this.getDistrictIndex((iData.getDistrictName(j)));
				if(districtIndex>=0)
					System.out.println(this.inFlowsRate_norm[observedYearIndex][districtIndex]+"\t"+this.outFlowsRate_norm[observedYearIndex][districtIndex]);
			}
		}
		*/
	}
	
	public void printResults(String outputFile, CurvatureData cData, EntropyData eData){
		for(int i=0 ; i<cData.getNumberOfYears() ; i++)
			this.printResults(cData.getYear(i), outputFile.replace(".txt", "_"+cData.getYear(i)+".txt"), cData, eData);
	}
	
	public void printResults(int year, String outputFile, CurvatureData cData, EntropyData eData){
		int i, j, k;
		int pressureIdx = cData.getYearIndex(year);				//Year index of pressure data
		int migrationIdx = this.yearList.indexOf(year+1);		//Year index of migration data
		int districtIdx;		//Index of district
		int endYear = this.yearList.get(this.numberOfYears-1);
		boolean check1y, check5y;
		double netSum, inSum, outSum;
		String tmpStr;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.println("Sum: "+cData.getPressureSum(pressureIdx));
			if(cData.checkRMSE()) pw.println("RMSE: "+cData.getRMSE(pressureIdx));
		//	if(cData.checkRsquare()) pw.println("R^2: "+cData.getRsquare(pressureYearIndex));

			pw.println();
			pw.print("Region\tAttraction\tPopulation\tEntropy\tAged-Index\tNet-migration\tInflow\tOutflow\tNet-migration per capita\tInflow per capita\tOutflow per capita");
			if(year+5 <= endYear) 
				pw.print("\t5yNet-migration\t5yInflow\t5yOutflow\t5yNet-migration per capita\t5yInflow per capita\t5yOutflow per capita");
			pw.println();
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				districtIdx = this.getDistrictIndex((cData.getDistrictName(i)));
				if(districtIdx >= 0 && eData.getPopulation(pressureIdx, i) > 0 && year+1 <= endYear){
					check1y = (this.inFlows[migrationIdx][districtIdx]>0 || this.outFlows[migrationIdx][districtIdx]>0);
					tmpStr = this.districtList.get(districtIdx);
					tmpStr = tmpStr.substring(0, tmpStr.length()-1);
					for(k=0 ; !check1y && k<this.numberOfDistricts ; k++){
						if(this.districtList.get(k).startsWith(tmpStr)
								&& (this.inFlows[migrationIdx][k] > 0 || this.outFlows[migrationIdx][k] > 0)){
							districtIdx = k;	
							check1y = true;
						}
					}
					if(check1y){	
						pw.print(cData.getDistrictName(i)+"\t"
								+cData.getPressure(pressureIdx, i)+"\t"
								+eData.getPopulation(pressureIdx, i)+"\t"
								+eData.getEntropy(pressureIdx, i)+"\t"
								+eData.getAgedIndex(pressureIdx, i)+"\t"
								+this.netFlows[migrationIdx][districtIdx]+"\t"
								+this.inFlows[migrationIdx][districtIdx]+"\t"
								+this.outFlows[migrationIdx][districtIdx]+"\t"
								+this.netFlows[migrationIdx][districtIdx]/(double) eData.getPopulation(pressureIdx, i)+"\t"
								+this.inFlows[migrationIdx][districtIdx]/(double) eData.getPopulation(pressureIdx, i)+"\t"
								+this.outFlows[migrationIdx][districtIdx]/(double) eData.getPopulation(pressureIdx, i));	
					
						if(year+5 <= endYear){
							netSum = 0.0;
							inSum = 0.0;
							outSum = 0.0;
							for(j=0, check5y = true; check5y && j<5 ; j++){
								check5y = (this.inFlows[migrationIdx+j][districtIdx]>0.0||this.outFlows[migrationIdx+j][districtIdx]>0.0);
								tmpStr = this.districtList.get(districtIdx);
								tmpStr = tmpStr.substring(0, tmpStr.length()-1);
								for(k=0 ; !check5y && k<this.numberOfDistricts ; k++){
									if(this.districtList.get(k).startsWith(tmpStr)
											&& (this.inFlows[migrationIdx+j][k]>0.0 || this.outFlows[migrationIdx+j][k]>0.0)){
										districtIdx = k;	
										check5y = true;
									}
								}
								if(check5y){
									netSum += this.netFlows[migrationIdx+j][districtIdx];
									inSum += this.inFlows[migrationIdx+j][districtIdx];
									outSum += this.outFlows[migrationIdx+j][districtIdx];
								}
							}
							if(check5y) pw.print("\t"+netSum+"\t"+inSum+"\t"+outSum+"\t"
												+netSum/(double) eData.getPopulation(pressureIdx, i)+"\t"
												+inSum/(double) eData.getPopulation(pressureIdx, i)+"\t"
												+outSum/(double) eData.getPopulation(pressureIdx, i));
						}
						pw.println();
					}
				}
			}
			
			for(i=0 ; i<this.numberOfDistricts ; i++){
				districtIdx = 0;
				check5y = false;
				for(j=0 ; j<cData.getDistricNumber() ; j++){
					if(this.getDistrictIndex(cData.getDistrictName(j))<0 
							&& cData.getDistrictName(j).startsWith(this.districtList.get(i))
							&& eData.getPopulation(pressureIdx, j)>0){
						districtIdx = j;	
						check5y = true;
					}
				}
				if(check5y && eData.getPopulation(pressureIdx, districtIdx) > 0
						&&( this.inFlows[migrationIdx][i] > 0.0 ||  this.outFlows[migrationIdx][i] > 0.0)){
					pw.println(cData.getDistrictName(i)+"\t"
							+cData.getPressure(pressureIdx, districtIdx)+"\t"
							+eData.getPopulation(pressureIdx, districtIdx)+"\t"	
							+eData.getEntropy(pressureIdx, districtIdx)+"\t"	
							+eData.getAgedIndex(pressureIdx, districtIdx)+"\t"
							+this.netFlows[migrationIdx][i]+"\t"
							+this.inFlows[migrationIdx][i]+"\t"
							+this.outFlows[migrationIdx][i]+"\t"
							+this.netFlows[migrationIdx][i]/(double) eData.getPopulation(pressureIdx, districtIdx)+"\t"
							+this.inFlows[migrationIdx][i]/(double) eData.getPopulation(pressureIdx, districtIdx)+"\t"
							+this.outFlows[migrationIdx][i]/(double) eData.getPopulation(pressureIdx, districtIdx));	
				}
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printCurrentResults(String outputFile, CurvatureData cData, EntropyData eData){
		for(int i=0 ; i<cData.getNumberOfYears() ; i++)
			this.printCurrentResults(cData.getYear(i), outputFile.replace(".txt", "_"+cData.getYear(i)+".txt"), cData, eData);
	}
	
	public void printCurrentResults(int year, String outputFile, CurvatureData cData, EntropyData eData){
		int i, j, k;
		int pressureIdx = cData.getYearIndex(year);				//Year index of pressure data
		int migrationIdx = this.yearList.indexOf(year);		//Year index of migration data
		int districtIdx;		//Index of district
		int endYear = this.yearList.get(this.numberOfYears-1);
		boolean check;
		double netSum, inSum, outSum;
		String tmpStr;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.println("Sum: "+cData.getPressureSum(pressureIdx));
			if(cData.checkRMSE()) pw.println("RMSE: "+cData.getRMSE(pressureIdx));
		//	if(cData.checkRsquare()) pw.println("R^2: "+cData.getRsquare(pressureYearIndex));

			pw.println();
			pw.print("Region\tAttraction\tPopulation\tEntropy\tAged-Index\tNet-migration\tInflow\tOutflow\tNet-migration per capita\tInflow per capita\tOutflow per capita");
			if(year+5 <= endYear) 
				pw.print("\t5yNet-migration\t5yInflow\t5yOutflow\t5yNet-migration per capita\t5yInflow per capita\t5yOutflow per capita");
			pw.println();
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				districtIdx = this.getDistrictIndex((cData.getDistrictName(i)));
				if(districtIdx >= 0 && eData.getPopulation(pressureIdx, i) > 0 && year+1 <= endYear
						&&(this.inFlows[migrationIdx][districtIdx]>0.0 || this.outFlows[migrationIdx][districtIdx]>0.0)){
					pw.print(cData.getDistrictName(i)+"\t"
								+cData.getPressure(pressureIdx, i)+"\t"
								+eData.getPopulation(pressureIdx, i)+"\t"
								+eData.getEntropy(pressureIdx, i)+"\t"
								+eData.getAgedIndex(pressureIdx, i)+"\t"
								+this.netFlows[migrationIdx][districtIdx]+"\t"
								+this.inFlows[migrationIdx][districtIdx]+"\t"
								+this.outFlows[migrationIdx][districtIdx]+"\t"
								+this.netFlows[migrationIdx][districtIdx]/(double) eData.getPopulation(pressureIdx, i)+"\t"
								+this.inFlows[migrationIdx][districtIdx]/(double) eData.getPopulation(pressureIdx, i)+"\t"
								+this.outFlows[migrationIdx][districtIdx]/(double) eData.getPopulation(pressureIdx, i));	
					if(year+5 <= endYear){
						netSum = 0.0;
						inSum = 0.0;
						outSum = 0.0;
						for(j=0, check = true; check && j<5 ; j++){
							check = (this.inFlows[migrationIdx+j][districtIdx]>0.0||this.outFlows[migrationIdx+j][districtIdx]>0.0);
							tmpStr = this.districtList.get(districtIdx);
							tmpStr = tmpStr.substring(0, tmpStr.length()-1);
							for(k=0 ; !check && k<this.numberOfDistricts ; k++){
								if(this.districtList.get(k).startsWith(tmpStr)
										&& (this.inFlows[migrationIdx+j][k]>0.0 || this.outFlows[migrationIdx+j][k]>0.0)){
									districtIdx = k;	
									check = true;
								}
							}
							if(check){
								netSum += this.netFlows[migrationIdx+j][districtIdx];
								inSum += this.inFlows[migrationIdx+j][districtIdx];
								outSum += this.outFlows[migrationIdx+j][districtIdx];
							}
						}
						if(check) pw.print("\t"+netSum+"\t"+inSum+"\t"+outSum+"\t"
											+netSum/(double) eData.getPopulation(pressureIdx, i)+"\t"
											+inSum/(double) eData.getPopulation(pressureIdx, i)+"\t"
											+outSum/(double) eData.getPopulation(pressureIdx, i));
					}
					pw.println();
				}
			}
			
			for(i=0 ; i<this.numberOfDistricts ; i++){
				districtIdx = 0;
				check = false;
				for(j=0 ; j<cData.getDistricNumber() ; j++){
					if(this.getDistrictIndex(cData.getDistrictName(j))<0 
							&& cData.getDistrictName(j).startsWith(this.districtList.get(i))
							&& eData.getPopulation(pressureIdx, j)>0){
						districtIdx = j;	
						check = true;
					}
				}
				if(check && eData.getPopulation(pressureIdx, districtIdx) > 0
						&&( this.inFlows[migrationIdx][i] > 0.0 ||  this.outFlows[migrationIdx][i] > 0.0)){
					pw.println(cData.getDistrictName(i)+"\t"
							+cData.getPressure(pressureIdx, districtIdx)+"\t"
							+eData.getPopulation(pressureIdx, districtIdx)+"\t"	
							+eData.getEntropy(pressureIdx, districtIdx)+"\t"	
							+eData.getAgedIndex(pressureIdx, districtIdx)+"\t"
							+this.netFlows[migrationIdx][i]+"\t"
							+this.inFlows[migrationIdx][i]+"\t"
							+this.outFlows[migrationIdx][i]+"\t"
							+this.netFlows[migrationIdx][i]/(double) eData.getPopulation(pressureIdx, districtIdx)+"\t"
							+this.inFlows[migrationIdx][i]/(double) eData.getPopulation(pressureIdx, districtIdx)+"\t"
							+this.outFlows[migrationIdx][i]/(double) eData.getPopulation(pressureIdx, districtIdx));	
				}
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printNormalizedResults(String outputFile, CurvatureData cData, EntropyData eData){
		for(int i=0 ; i<cData.getNumberOfYears() ; i++)
			this.printNormalizedResults(cData.getYear(i), outputFile.replace(".txt", "_"+cData.getYear(i)+".txt"), cData, eData);
	}
	
	public void printNormalizedResults(int year, String outputFile, CurvatureData cData, EntropyData eData){
		int i, j, k;
		int pressureIdx = cData.getYearIndex(year);				//Year index of pressure data
		int migrationIdx = this.yearList.indexOf(year+1);		//Year index of migration data
		int districtIdx;		//Index of district
		int endYear = this.yearList.get(this.numberOfYears-1);
		boolean check;
		double netSum, inSum, outSum;
		String tmpStr;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.println("Sum: "+cData.getPressureSum(pressureIdx));
			if(cData.checkRMSE()) pw.println("RMSE: "+cData.getRMSE(pressureIdx));
		//	if(cData.checkRsquare()) pw.println("R^2: "+cData.getRsquare(pressureYearIndex));

			pw.println();
			pw.print("Region\tAttraction\tPopulation\tEntropy\tAged-Index\tNet-migration\tInflow\tOutflow\tNet-migration per capita\tInflow per capita\tOutflow per capita");
			if(year+5 <= endYear) 
				pw.print("\t5yNet-migration\t5yInflow\t5yOutflow\t5yNet-migration per capita\t5yInflow per capita\t5yOutflow per capita");
			pw.println();
			for(i=0 ; i<cData.getDistricNumber() ; i++){
				districtIdx = this.getDistrictIndex((cData.getDistrictName(i)));
				if(districtIdx >= 0 && eData.getPopulation(pressureIdx, i) > 0 && year+1 <= endYear
						&&(this.inFlows[migrationIdx][districtIdx] > 0 || this.outFlows[migrationIdx][districtIdx] > 0)){
					pw.print(cData.getDistrictName(i)+"\t"
								+cData.getPressure(pressureIdx, i)+"\t"
								+eData.getPopulation(pressureIdx, i)+"\t"
								+eData.getEntropy(pressureIdx, i)+"\t"
								+eData.getAgedIndex(pressureIdx, i)+"\t"
								+this.netFlows_norm[migrationIdx][districtIdx]+"\t"
								+this.inFlows_norm[migrationIdx][districtIdx]+"\t"
								+this.outFlows_norm[migrationIdx][districtIdx]+"\t"
								+this.netFlows_norm[migrationIdx][districtIdx]/(double) eData.getPopulation(pressureIdx, i)+"\t"
								+this.inFlows_norm[migrationIdx][districtIdx]/(double) eData.getPopulation(pressureIdx, i)+"\t"
								+this.outFlows_norm[migrationIdx][districtIdx]/(double) eData.getPopulation(pressureIdx, i));	
					if(year+5 <= endYear){
						netSum = 0.0;
						inSum = 0.0;
						outSum = 0.0;
						for(j=0, check = true; check && j<5 ; j++){
							check = (this.inFlows[migrationIdx+j][districtIdx]>0||this.outFlows[migrationIdx+j][districtIdx]>0);
							tmpStr = this.districtList.get(districtIdx);
							tmpStr = tmpStr.substring(0, tmpStr.length()-1);
							for(k=0 ; !check && k<this.numberOfDistricts ; k++){
								if(this.districtList.get(k).startsWith(tmpStr)
										&& (this.inFlows[migrationIdx+j][k]>0 || this.outFlows[migrationIdx+j][k]>0)){
									districtIdx = k;	
									check = true;
								}
							}
							if(check){
								netSum += this.netFlows_norm[migrationIdx+j][districtIdx];
								inSum += this.inFlows_norm[migrationIdx+j][districtIdx];
								outSum += this.outFlows_norm[migrationIdx+j][districtIdx];
							}
						}
						if(check) pw.print("\t"+netSum+"\t"+inSum+"\t"+outSum+"\t"
											+netSum/(double) eData.getPopulation(pressureIdx, i)+"\t"
											+inSum/(double) eData.getPopulation(pressureIdx, i)+"\t"
											+outSum/(double) eData.getPopulation(pressureIdx, i));
					}
					pw.println();
				}
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
}


