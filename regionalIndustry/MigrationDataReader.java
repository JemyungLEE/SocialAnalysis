package regionalIndustry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import regionalIndustry.data.PopulationData;

public class MigrationDataReader implements Cloneable {

	int numberOfIndex;
	int numberOfYears, numberOfRegion;
	ArrayList<String> districtList;
	ArrayList<Integer> yearList;
	int[][] netFlows, inFlows, outFlows;			//[year][region]
	double[][] grdp, employeeRate;
	ArrayList<String> upperLevelDistrictIndicators;
	
	ArrayList<Integer> assembledYears;
	ArrayList<ArrayList<String>> assembledDistricts;
	ArrayList<ArrayList<ArrayList<Double>>> assembledData;
		
	ArrayList<String> titleList;
	
	public MigrationDataReader(){
		this.initiate();
	}
	
	public void initiate(){
		this.upperLevelDistrictIndicators = new ArrayList<String>();
		this.districtList = new ArrayList<String>();
		this.yearList = new ArrayList<Integer>();
		this.assembledYears = new ArrayList<Integer>();
		this.assembledDistricts = new ArrayList<ArrayList<String>>();
		this.assembledData = new ArrayList<ArrayList<ArrayList<Double>>>();
		this.titleList = new ArrayList<String>();
	}
	
	public MigrationDataReader clone(){
		int i, j, k;
		ArrayList<String> tmpArray;
		ArrayList<Double> tmpDoubles;
		ArrayList<ArrayList<Double>> tmpDoubleList;
		MigrationDataReader tmpMda = new MigrationDataReader();
		
		tmpMda.numberOfIndex = this.numberOfIndex;
		tmpMda.numberOfYears = this.numberOfYears;
		tmpMda.numberOfRegion = this.numberOfYears;
		tmpMda.netFlows = new int[this.netFlows.length][this.netFlows[0].length];
		tmpMda.inFlows = new int[this.inFlows.length][this.inFlows[0].length];
		tmpMda.outFlows = new int[this.outFlows.length][this.outFlows[0].length];

		
		for(i=0 ; i<this.districtList.size() ; i++) tmpMda.districtList.add(this.districtList.get(i));
		for(i=0 ; i<this.yearList.size() ; i++) tmpMda.yearList.add(this.yearList.get(i));
		for(i=0 ; i<this.netFlows.length ; i++)
			for(j=0 ; j<this.netFlows[i].length ; j++) tmpMda.netFlows[i][j] = this.netFlows[i][j];
		for(i=0 ; i<this.inFlows.length ; i++)
			for(j=0 ; j<this.inFlows[i].length ; j++) tmpMda.inFlows[i][j] = this.inFlows[i][j];
		for(i=0 ; i<this.outFlows.length ; i++)
			for(j=0 ; j<this.outFlows[i].length ; j++) tmpMda.outFlows[i][j] = this.outFlows[i][j];
		
		if(this.grdp != null){
			tmpMda.grdp = new double[this.grdp.length][this.grdp[0].length];
			for(i=0 ; i<this.grdp.length ; i++)
				for(j=0 ; j<this.grdp[i].length ; j++) tmpMda.grdp[i][j] = this.grdp[i][j];
		}
		else tmpMda.grdp = null;
		if(this.employeeRate != null){
			tmpMda.employeeRate = new double[this.employeeRate.length][this.employeeRate[0].length];
			for(i=0 ; i<this.employeeRate.length ; i++)
				for(j=0 ; j<this.employeeRate[i].length ; j++) tmpMda.employeeRate[i][j] = this.employeeRate[i][j];

		}
		else tmpMda.employeeRate = null;
		
		for(i=0 ; i<this.upperLevelDistrictIndicators.size() ; i++) 
			tmpMda.upperLevelDistrictIndicators.add(this.upperLevelDistrictIndicators.get(i));
		
		for(i=0 ; i<this.assembledYears.size() ; i++) tmpMda.assembledYears.add(this.assembledYears.get(i));
		
		for(i=0 ; i<this.assembledDistricts.size() ; i++){
			tmpArray = new ArrayList<String>();
			for(j=0 ; j<this.assembledDistricts.get(i).size() ; j++) 
				tmpArray.add(this.assembledDistricts.get(i).get(j));
			tmpMda.assembledDistricts.add(tmpArray);			
		}

		for(i=0 ; i<this.assembledData.size() ; i++){
			tmpDoubleList = new ArrayList<ArrayList<Double>>();
			for(j=0 ; j<this.assembledData.get(i).size() ; j++){
				tmpDoubles = new ArrayList<Double>();
				for(k=0 ; k<this.assembledData.get(i).get(j).size() ; k++) 
					tmpDoubles.add(this.assembledData.get(i).get(j).get(k));
				tmpDoubleList.add(tmpDoubles);
			}
			tmpMda.assembledData.add(tmpDoubleList);
		}

		for(i=0 ; i<this.titleList.size() ; i++) tmpMda.titleList.add(this.titleList.get(i));
		
		return tmpMda;
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
			this.numberOfRegion = this.districtList.size();
			scan.close();
			
			/*** initiate variables ***/
			this.netFlows = new int[this.numberOfYears][this.numberOfRegion];
			this.inFlows = new int[this.numberOfYears][this.numberOfRegion];
			this.outFlows = new int[this.numberOfYears][this.numberOfRegion];
			
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
				for(j=0 ; j<this.numberOfRegion ; j++) this.netFlows[i][j] = this.inFlows[i][j] - this.outFlows[i][j];
			
		} catch(IOException e) {
			System.err.println(e);
		}
	}
	
	public void readGRDPdata(String inputFile){
		int i, j;
		
		int tmpDistrictIndex;
		int[] yearIndex;
		double tmpDouble;
		String tmpDistrictName, tmpName;
		String[] tmpStr;
		
		boolean lowerLevelCheck;
		
		File file;
		Scanner scan;
		
		/*** reading process ***/
		try{
			/*** initiate variables ***/
			this.grdp = new double[this.numberOfYears][this.numberOfRegion];
			for(i=0 ; i<this.numberOfYears ; i++) for(j=0 ; j<this.numberOfRegion ; j++) this.grdp[i][j] = -1.0;
			
			/*** read GRDP data ***/
			file = new File(inputFile);
			scan = new Scanner(file);
			
			tmpStr = scan.nextLine().split("\t");
			yearIndex = new int[tmpStr.length];
			for(i=0; i<this.numberOfIndex ; i++) yearIndex[i] = -1;
			for(i=this.numberOfIndex; i<tmpStr.length ; i++) 
				yearIndex[i] = this.yearList.indexOf(Integer.parseInt(tmpStr[i]));
			
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
						if(tmpStr[i].trim().isEmpty() || Double.parseDouble(tmpStr[i]) < 0) tmpDouble = -1.0;
						else if(!tmpStr[i].trim().isEmpty()) tmpDouble =  Double.parseDouble(tmpStr[i]);
						else tmpDouble = -1.0;
						
						this.grdp[yearIndex[i]][tmpDistrictIndex] = tmpDouble;
					}
				}
			}
			scan.close();
			
		} catch(IOException e) {
			System.err.println(e);
		}
	}
	
	public void readEmploymentRates(String inputFile){
		int i, j;
		
		int tmpDistrictIndex;
		int[] yearIndex;
		double tmpDouble;
		String tmpDistrictName, tmpName;
		String[] tmpStr;
		
		boolean lowerLevelCheck;
		
		File file;
		Scanner scan;
		
		/*** reading process ***/
		try{
			/*** initiate variables ***/
			this.employeeRate = new double[this.numberOfYears][this.numberOfRegion];
			for(i=0 ; i<this.numberOfYears ; i++) 
				for(j=0 ; j<this.numberOfRegion ; j++) this.employeeRate[i][j] = -1.0;
			
			/*** read employment rate data ***/
			file = new File(inputFile);
			scan = new Scanner(file);
			
			tmpStr = scan.nextLine().split("\t");
			yearIndex = new int[tmpStr.length];
			for(i=0; i<this.numberOfIndex ; i++) yearIndex[i] = -1;
			for(i=this.numberOfIndex; i<tmpStr.length ; i++) 
				yearIndex[i] = this.yearList.indexOf(Integer.parseInt(tmpStr[i]));
			
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
						if(tmpStr[i].trim().isEmpty() || Double.parseDouble(tmpStr[i]) < 0) tmpDouble = -1.0;
						else if(!tmpStr[i].trim().isEmpty()) tmpDouble =  Double.parseDouble(tmpStr[i]);
						else tmpDouble = -1.0;
						
						this.employeeRate[yearIndex[i]][tmpDistrictIndex] = tmpDouble;
					}
				}
			}
			scan.close();
			
		} catch(IOException e) {
			System.err.println(e);
		}
	}
	
	public void assemblePopulationAndMigration(PopulationData pData){
		int i, j, k;
		
		int midSpan = 3;
		int longSpan = 5;
		int flowYearIndex, pDataYearIndex = 0;
		int flowDistrictIndex, pDataDistrictIndex = 0;
		int shiftYearIndex;
		double tmpGRDP, tmpEmploymentRate;
		double tmpStack, tmpPopulation, tmpInflow, tmpOutflow;
		double logBase = Math.log(2);
		boolean dataCheck;
		
		ArrayList<Integer> pDataYears = new ArrayList<Integer>();
		ArrayList<String> pDataDistricts = new ArrayList<String>();
		
		ArrayList<String> commonDistricts = new ArrayList<String>();
		ArrayList<String> tmpDistricts;
		
		ArrayList<ArrayList<Double>> listByYear;
		
		ArrayList<Double> population, agedIndex;
		ArrayList<Double> populationLog, agedIndexLog, agedIndexRescaled;
		ArrayList<Double> grdp, grdpPerCapita, employmentRate;
		
		ArrayList<Double> netflow, inflow, outflow, netflowPerCapita, inflowPerCapita, outflowPerCapita;
		ArrayList<Double> nextYearNetflow, nextYearInflow, nextYearOutflow;
		ArrayList<Double> nextYearNetflowPerCapita, nextYearInflowPerCapita, nextYearOutflowPerCapita;
		
		ArrayList<Double> threeYearStackedNetflow, threeYearStackedNetflowPerCapita;
		ArrayList<Double> fiveYearStackedNetflow, fiveYearStackedNetflowPerCapita;
		ArrayList<Double> threeYearPreviousNetflow, threeYearPreviousNetflowPerCapita;
		ArrayList<Double> fiveYearPreviousNetflow, fiveYearPreviousNetflowPerCapita;
		
		for(i=0 ; i<pData.getYears().length ; i++) pDataYears.add(pData.getYears()[i]);
		Collections.addAll(pDataDistricts, pData.getDistrictList());
			
		for(i=0 ; i<pDataYears.size() ; i++) 
			if(this.yearList.contains(pDataYears.get(i))) this.assembledYears.add(pDataYears.get(i));
		for(i=0 ; i<pDataDistricts.size() ; i++)
			if(this.districtList.contains(pDataDistricts.get(i))) commonDistricts.add(pDataDistricts.get(i));
		
		for(i=0 ; i<this.assembledYears.size() ; i++){
			tmpDistricts = new ArrayList<String>();
			tmpDistricts.addAll(commonDistricts);
			listByYear = new ArrayList<ArrayList<Double>>();
			flowYearIndex = this.yearList.indexOf(this.assembledYears.get(i));
			pDataYearIndex = pDataYears.indexOf(this.assembledYears.get(i));
			
			population = new ArrayList<Double>();
			agedIndex = new ArrayList<Double>();
			
			populationLog = new ArrayList<Double>();
			agedIndexLog = new ArrayList<Double>();
			agedIndexRescaled = new ArrayList<Double>();
			
			grdp = new ArrayList<Double>();
			grdpPerCapita = new ArrayList<Double>();
			employmentRate = new ArrayList<Double>();
			
			netflow = new ArrayList<Double>();
			inflow = new ArrayList<Double>();
			outflow = new ArrayList<Double>();
			netflowPerCapita = new ArrayList<Double>();
			inflowPerCapita = new ArrayList<Double>();
			outflowPerCapita = new ArrayList<Double>();
			
			nextYearNetflow = new ArrayList<Double>();
			nextYearInflow = new ArrayList<Double>();
			nextYearOutflow = new ArrayList<Double>();
			nextYearNetflowPerCapita = new ArrayList<Double>();
			nextYearInflowPerCapita = new ArrayList<Double>();
			nextYearOutflowPerCapita = new ArrayList<Double>();
			
			threeYearStackedNetflow = new ArrayList<Double>();
			threeYearStackedNetflowPerCapita = new ArrayList<Double>();
			fiveYearStackedNetflow = new ArrayList<Double>();
			fiveYearStackedNetflowPerCapita = new ArrayList<Double>();
			threeYearPreviousNetflow = new ArrayList<Double>();
			threeYearPreviousNetflowPerCapita = new ArrayList<Double>();
			fiveYearPreviousNetflow = new ArrayList<Double>();
			fiveYearPreviousNetflowPerCapita = new ArrayList<Double>();
			
			for(j=0 ; j<tmpDistricts.size() ; j++){
				flowDistrictIndex = this.districtList.indexOf(tmpDistricts.get(j));
				pDataDistrictIndex = pDataDistricts.indexOf( tmpDistricts.get(j));
				
				tmpPopulation = (double) pData.getPopulation(pDataYearIndex, pDataDistrictIndex);
				tmpInflow = (double) this.inFlows[flowYearIndex][flowDistrictIndex];
				tmpOutflow = (double) this.outFlows[flowYearIndex][flowDistrictIndex];
				if(this.grdp != null) tmpGRDP = this.grdp[flowYearIndex][flowDistrictIndex];
				else tmpGRDP = 0.0;
				if(this.employeeRate != null) 
					tmpEmploymentRate = this.employeeRate[flowYearIndex][flowDistrictIndex];
				else tmpEmploymentRate = 0.0;
				
				if(tmpPopulation > 0){
					population.add(tmpPopulation);
					agedIndex.add(pData.getAgedRatio(pDataYearIndex, pDataDistrictIndex));	

					populationLog.add(Math.log10(tmpPopulation));
					agedIndexLog.add(-1.0 * Math.log(agedIndex.get(j)) / logBase);
					agedIndexRescaled.add(1.0 - agedIndex.get(j));	
					
					grdp.add(tmpGRDP);
					if(tmpGRDP > 0) grdpPerCapita.add(this.grdp[flowYearIndex][flowDistrictIndex]/tmpPopulation);
					else grdpPerCapita.add(0.0);
					employmentRate.add(tmpEmploymentRate);
					
					netflow.add((double) this.netFlows[flowYearIndex][flowDistrictIndex]);					
					inflow.add(tmpInflow);
					outflow.add(tmpOutflow);
					netflowPerCapita.add(netflow.get(j) / tmpPopulation);
					inflowPerCapita.add(tmpInflow / tmpPopulation);
					outflowPerCapita.add(tmpOutflow / tmpPopulation);
					
					dataCheck = true;
					shiftYearIndex = this.yearList.indexOf(this.assembledYears.get(i)+1);
					if(this.assembledYears.get(i)+1 > this.yearList.get(this.yearList.size()-1)) dataCheck = false;
					else{
						if(this.inFlows[shiftYearIndex][flowDistrictIndex] < 0 ||
								this.outFlows[shiftYearIndex][flowDistrictIndex] < 0) dataCheck = false;		
					}		
					if(dataCheck){
						nextYearNetflow.add((double) this.netFlows[shiftYearIndex][flowDistrictIndex]);		
						nextYearInflow.add((double) this.inFlows[shiftYearIndex][flowDistrictIndex]);
						nextYearOutflow.add((double) this.outFlows[shiftYearIndex][flowDistrictIndex]);
					}else{
						nextYearNetflow.add(0.0);		
						nextYearInflow.add(-1.0);
						nextYearOutflow.add(-1.0);
					}
					nextYearNetflowPerCapita.add(nextYearNetflow.get(j) / tmpPopulation);
					nextYearInflowPerCapita.add(nextYearInflow.get(j) / tmpPopulation);
					nextYearOutflowPerCapita.add(nextYearOutflow.get(j) / tmpPopulation);

					tmpStack = 0.0;
					dataCheck = true;
					if(this.assembledYears.get(i)+midSpan > this.yearList.get(this.yearList.size()-1)) 
						dataCheck = false;
					else{
						for(k=0 ; k<midSpan ; k++){
							shiftYearIndex = this.yearList.indexOf(this.assembledYears.get(i)+k+1);
							if(this.inFlows[shiftYearIndex][flowDistrictIndex] < 0 ||
								this.outFlows[shiftYearIndex][flowDistrictIndex] < 0) dataCheck = false;
						}
					}
					if(dataCheck) for(k=0 ; k<midSpan ; k++){
						shiftYearIndex = this.yearList.indexOf(this.assembledYears.get(i)+k+1);
						tmpStack += (double) this.netFlows[shiftYearIndex][flowDistrictIndex];
					}
					threeYearStackedNetflow.add(tmpStack);
					threeYearStackedNetflowPerCapita.add(tmpStack / tmpPopulation);
					
					tmpStack = 0.0;
					dataCheck = true;
					if(this.assembledYears.get(i)+longSpan > this.yearList.get(this.yearList.size()-1))
						dataCheck = false;
					else{
						for(k=0 ; k<longSpan ; k++){
							shiftYearIndex = this.yearList.indexOf(this.assembledYears.get(i)+k+1);
							if(this.inFlows[shiftYearIndex][flowDistrictIndex] < 0 ||
								this.outFlows[shiftYearIndex][flowDistrictIndex] < 0) dataCheck = false;
						}
					}
					if(dataCheck)  for(k=0 ; k<longSpan ; k++){
						shiftYearIndex = this.yearList.indexOf(this.assembledYears.get(i)+k+1);
						tmpStack += (double) this.netFlows[shiftYearIndex][flowDistrictIndex];
					}
					fiveYearStackedNetflow.add(tmpStack);
					fiveYearStackedNetflowPerCapita.add(tmpStack / tmpPopulation);
					
					tmpStack = 0.0;
					dataCheck = true;
					if(this.assembledYears.get(i)-midSpan+1 < this.yearList.get(0)) dataCheck = false;
					else{
						for(k=0 ; k<midSpan ; k++){
							shiftYearIndex = this.yearList.indexOf(this.assembledYears.get(i)-k);
							if(this.inFlows[shiftYearIndex][flowDistrictIndex] < 0 ||
								this.outFlows[shiftYearIndex][flowDistrictIndex] < 0) dataCheck = false;
						}
					}
					if(dataCheck) for(k=0 ; k<midSpan ; k++){
						shiftYearIndex = this.yearList.indexOf(this.assembledYears.get(i)-k);
						tmpStack += (double) this.netFlows[shiftYearIndex][flowDistrictIndex];
					}
					threeYearPreviousNetflow.add(tmpStack);
					threeYearPreviousNetflowPerCapita.add(tmpStack / tmpPopulation);
					
					tmpStack = 0.0;
					if(this.assembledYears.get(i)-longSpan+1 < this.yearList.get(0)) dataCheck = false;
					else{
						for(k=0 ; k<longSpan ; k++){
							shiftYearIndex = this.yearList.indexOf(this.assembledYears.get(i)-k);
							if(this.inFlows[shiftYearIndex][flowDistrictIndex] < 0 ||
								this.outFlows[shiftYearIndex][flowDistrictIndex] < 0) dataCheck = false;
						}
					}
					if(dataCheck) for(k=0 ; k<longSpan ; k++){
						shiftYearIndex = this.yearList.indexOf(this.assembledYears.get(i)-k);
						tmpStack += (double) this.netFlows[shiftYearIndex][flowDistrictIndex];
					}
					fiveYearPreviousNetflow.add(tmpStack);
					fiveYearPreviousNetflowPerCapita.add(tmpStack / tmpPopulation);
					
				}else{
					tmpDistricts.remove(j--);
				}
			}
			
			listByYear.add(population);									if(i==0) this.titleList.add("Population(P)");
			listByYear.add(agedIndex);									if(i==0) this.titleList.add("Aged_index(Ai)");
			
			listByYear.add(populationLog);							if(i==0) this.titleList.add("Log(P)");
			listByYear.add(agedIndexLog);							if(i==0) this.titleList.add("Log(Ai)");
			listByYear.add(agedIndexRescaled);					if(i==0) this.titleList.add("1-Ai");
			
			listByYear.add(grdp);											if(i==0) this.titleList.add("GRDP");
			listByYear.add(grdpPerCapita);							if(i==0) this.titleList.add("GRDP/P");
			listByYear.add(employmentRate);						if(i==0) this.titleList.add("employment");
			
			listByYear.add(netflow);										if(i==0) this.titleList.add("-1y Netflow");
			listByYear.add(inflow);											if(i==0) this.titleList.add("-1y Inflow");
			listByYear.add(outflow);										if(i==0) this.titleList.add("-1y Outflow");
			listByYear.add(netflowPerCapita);						if(i==0) this.titleList.add("-1y Netflow/cap");
			listByYear.add(inflowPerCapita);							if(i==0) this.titleList.add("-1y Inflow/cap");
			listByYear.add(outflowPerCapita);						if(i==0) this.titleList.add("-1y Outflow/cap");
			
			listByYear.add(nextYearNetflow);						if(i==0) this.titleList.add("+1y Netflow");
			listByYear.add(nextYearInflow);							if(i==0) this.titleList.add("+1y Inflow");
			listByYear.add(nextYearOutflow);						if(i==0) this.titleList.add("+1y Outflow");
			listByYear.add(nextYearNetflowPerCapita);		if(i==0) this.titleList.add("+1y Netflow/cap");
			listByYear.add(nextYearInflowPerCapita);			if(i==0) this.titleList.add("+1y Inflow/cap");
			listByYear.add(nextYearOutflowPerCapita);		if(i==0) this.titleList.add("+1y Outflow/cap");
			
			listByYear.add(threeYearStackedNetflow);						if(i==0) this.titleList.add("+3y Netflow");
			listByYear.add(threeYearStackedNetflowPerCapita);		if(i==0) this.titleList.add("+3y Netflow/cap");
			listByYear.add(fiveYearStackedNetflow);						if(i==0) this.titleList.add("+5y Netflow");
			listByYear.add(fiveYearStackedNetflowPerCapita);		if(i==0) this.titleList.add("+5y Netflow/cap");

			listByYear.add(threeYearPreviousNetflow);						if(i==0) this.titleList.add("-3y Netflow");
			listByYear.add(threeYearPreviousNetflowPerCapita);		if(i==0) this.titleList.add("-3y Netflow/cap");
			listByYear.add(fiveYearPreviousNetflow);						if(i==0) this.titleList.add("-5y Netflow");
			listByYear.add(fiveYearPreviousNetflowPerCapita);		if(i==0) this.titleList.add("-5y Netflow/cap");
			
			this.assembledData.add(listByYear);
			this.assembledDistricts.add(tmpDistricts);
		}
	}
	
	public void removeEmptyAssembledData(){
		int i, j, k;
		
		for(i=0 ; i<this.assembledYears.size() ; i++){
			if(this.assembledData.get(i).get(0).isEmpty()){
				this.assembledYears.remove(i);
				this.assembledDistricts.remove(i);
				this.assembledData.remove(i);
				i--;
			}
		}
		
		for(i=0 ; i<this.assembledYears.size() ; i++){
			for(j=0 ; j<this.assembledDistricts.get(i).size() ; j++){
				if(this.assembledData.get(i).get(0).get(j) <= 0){
					this.assembledDistricts.get(i).remove(j);
					for(k=0 ; k<this.assembledData.get(i).size() ; k++)
						this.assembledData.get(i).get(k).remove(j);
				}
			}
		}
	}
	
	public void reviseAssembledData(int yearSpan){
		int i, j, k;
		int yearIndex, districtIndex;
		boolean integrityCheck;
				
		/*** remove no-migration data ***/
		for(i=0 ; i<this.assembledYears.size() ; i++){
			for(j=0 ; j<this.assembledDistricts.get(i).size() ; j++){
				districtIndex = this.districtList.indexOf(this.assembledDistricts.get(i).get(j));
				integrityCheck = true;
				if(this.assembledData.get(i).get(0).get(j) == 0) integrityCheck = false;
				else if(yearSpan == 0) System.err.println("year span should be bigger than 1 or less than -1.");
				else if(yearSpan > 0){
					if(this.assembledYears.get(i)+yearSpan > this.yearList.get(this.yearList.size()-1)) 
						integrityCheck = false;
					else{
						for(k=0 ; k<yearSpan ; k++){
							yearIndex = this.yearList.indexOf(this.assembledYears.get(i)+k+1);
							if(this.inFlows[yearIndex][districtIndex] < 0 || this.outFlows[yearIndex][districtIndex] < 0) 
								integrityCheck = false;
						}
					}
				}else if(yearSpan < 0){
					if(this.assembledYears.get(i)+yearSpan+1 < this.yearList.get(0)) integrityCheck = false;
					else{
						for(k=0 ; k<Math.abs(yearSpan) ; k++){
							yearIndex = this.yearList.indexOf(this.assembledYears.get(i)-k);
							if(this.inFlows[yearIndex][districtIndex] < 0 || this.outFlows[yearIndex][districtIndex] < 0) 
								integrityCheck = false;
						}
					}
				}
				
				if(!integrityCheck){
					this.assembledDistricts.get(i).remove(j);
					for(k=0 ; k<this.assembledData.get(i).size() ; k++) this.assembledData.get(i).get(k).remove(j);
					j--;
				}
			}
		}
		
		/*** remove empty list ***/
		this.removeEmptyAssembledData();
	}
	
	public void reviseToFitGRDPData(){
		int i, j, k;
		int grdpIndex = this.titleList.indexOf("GRDP");
		boolean integrityCheck;
				
		/*** remove no-GRDP data ***/
		for(i=0 ; i<this.assembledYears.size() ; i++){
			for(j=0 ; j<this.assembledDistricts.get(i).size() ; j++){
				integrityCheck = true;
				if(this.assembledData.get(i).get(0).get(j) == 0) integrityCheck = false;
				else if(this.assembledData.get(i).get(grdpIndex).get(j) <= 0) integrityCheck = false;
				
				if(!integrityCheck){
					this.assembledDistricts.get(i).remove(j);
					for(k=0 ; k<this.assembledData.get(i).size() ; k++) this.assembledData.get(i).get(k).remove(j);
					j--;
				}
			}
		}
		
		/*** remove empty list ***/
		this.removeEmptyAssembledData();
	}
	
	public void reviseToFitEmploymentData(){
		int i, j, k;
		int employmentIndex = this.titleList.indexOf("employment");
		boolean integrityCheck;
				
		/*** remove no-employment rate data ***/
		for(i=0 ; i<this.assembledYears.size() ; i++){
			for(j=0 ; j<this.assembledDistricts.get(i).size() ; j++){
				integrityCheck = true;
				if(this.assembledData.get(i).get(0).get(j) == 0) integrityCheck = false;
				else if(this.assembledData.get(i).get(employmentIndex).get(j) <= 0) integrityCheck = false;
				
				if(!integrityCheck){
					this.assembledDistricts.get(i).remove(j);
					for(k=0 ; k<this.assembledData.get(i).size() ; k++) this.assembledData.get(i).get(k).remove(j);
					j--;
				}
			}
		}
		
		/*** remove empty list ***/
		this.removeEmptyAssembledData();
	}
	
	public void removeSpecificRegions(ArrayList<String> removeList){
		int i, j, k, l;
		boolean removeListContain;
		
		for(i=0 ; i<this.assembledYears.size() ; i++){
			for(j=0 ; j<this.assembledDistricts.get(i).size() ; j++){
				removeListContain = false;
				for(k=0 ; k<removeList.size() ; k++)
					if(this.assembledDistricts.get(i).get(j).contains(removeList.get(k))) removeListContain = true;
				if(removeListContain){
					this.assembledDistricts.get(i).remove(j);
					for(l=0 ; l<this.assembledData.get(i).size() ; l++) this.assembledData.get(i).get(l).remove(j);
					j--;
				}
			}
		}
	}
	
	public void retainSpecificRegions(ArrayList<String> retainList){
		int i, j, k, l;
		boolean retainListContain;
		
		for(i=0 ; i<this.assembledYears.size() ; i++){
			for(j=0 ; j<this.assembledDistricts.get(i).size() ; j++){
				retainListContain = false;
				for(k=0 ; k<retainList.size() ; k++)
					if(this.assembledDistricts.get(i).get(j).contains(retainList.get(k))) retainListContain = true;
				if(!retainListContain){
					this.assembledDistricts.get(i).remove(j);
					for(l=0 ; l<this.assembledData.get(i).size() ; l++) this.assembledData.get(i).get(l).remove(j);
					j--;
				}
			}
		}
	}
	
	public void retainRuralRegions(){
		int i, j, k;
		String ruralMark = "군";
		
		for(i=0 ; i<this.assembledYears.size() ; i++){
			for(j=0 ; j<this.assembledDistricts.get(i).size() ; j++){
				if(!this.assembledDistricts.get(i).get(j).endsWith(ruralMark)){
					this.assembledDistricts.get(i).remove(j);
					for(k=0 ; k<this.assembledData.get(i).size() ; k++) this.assembledData.get(i).get(k).remove(j);
					j--;
				}
			}
		}
	}
	
	public void retainUrbanRegions(){
		int i, j, k;
		String[] urbanMark = {"시", "구"};
		
		for(i=0 ; i<this.assembledYears.size() ; i++){
			for(j=0 ; j<this.assembledDistricts.get(i).size() ; j++){
				if(!this.assembledDistricts.get(i).get(j).endsWith(urbanMark[0]) && 
						!this.assembledDistricts.get(i).get(j).endsWith(urbanMark[1])){
					this.assembledDistricts.get(i).remove(j);
					for(k=0 ; k<this.assembledData.get(i).size() ; k++) this.assembledData.get(i).get(k).remove(j);
					j--;
				}
			}
		}
	}
	
}
