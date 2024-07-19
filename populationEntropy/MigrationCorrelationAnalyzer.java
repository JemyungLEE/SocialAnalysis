package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import populationEntropy.MigrationDataAnalyzer;

public class MigrationCorrelationAnalyzer {

	ArrayList<double[][]> correlationList;
	ArrayList<double[][]> pValueList;
	
	MigrationDataAnalyzer mda;
	
	public MigrationCorrelationAnalyzer(){
		mda = new MigrationDataAnalyzer();
		this.initiate();
	}
	
	public void initiate(){
		this.correlationList = new ArrayList<double[][]>();
		this.pValueList = new ArrayList<double[][]>();
	}
	
	public void calculateCorrelationship() throws CloneNotSupportedException{
		int i, j, k, s;
		int tmpIndex;
		int[] spanSize = {1, 3, 5, -1, -3, -5};
		String[] checkTitle = {"GRDP", "employment"};
		double[][] tmpMatrix, tmpCorrelation, tmpPvalue;
		int[] yearSpan = new int[this.mda.titleList.size()];
		RealMatrix tmpCorrelationMatrix;
		RealMatrix tmpPvalueMatrix;
		PearsonsCorrelation pc;
		
		MigrationDataAnalyzer tmpMda;
		
		/*** prepare correlation analysis ***/
		for(i=0 ; i<this.mda.titleList.size() ; i++){
			if(this.mda.titleList.get(i).startsWith("-")) 
				yearSpan[i] = Integer.parseInt(this.mda.titleList.get(i).substring(0, 2));
			else if(this.mda.titleList.get(i).startsWith("+")) 
				yearSpan[i] = Integer.parseInt(this.mda.titleList.get(i).substring(1, 2));
			else yearSpan[i] = 0;
		}
		
		/*** analyze non-year span correlation ***/
		for(i=0 ; i<this.mda.assembledYears.size(); i++){
			tmpMatrix = new double[this.mda.assembledDistricts.get(i).size()][this.mda.assembledData.get(i).size()];
			tmpCorrelation = new double[this.mda.assembledData.get(i).size()][this.mda.assembledData.get(i).size()];
			tmpPvalue = new double[this.mda.assembledData.get(i).size()][this.mda.assembledData.get(i).size()];
			
			for(j=0 ; j<this.mda.assembledDistricts.get(i).size() ; j++)
				for(k=0 ; k<this.mda.assembledData.get(i).size() ; k++)
					tmpMatrix[j][k] = this.mda.assembledData.get(i).get(k).get(j);
			if(this.mda.assembledDistricts.get(i).size()>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpCorrelationMatrix = pc.getCorrelationMatrix();
				tmpPvalueMatrix = pc.getCorrelationPValues();
				
				for(j=0 ; j<this.mda.titleList.size() ; j++){
					for(k=0 ; k<this.mda.titleList.size() ; k++){
						if(yearSpan[j] == 0 && yearSpan[k] == 0){
							tmpCorrelation[j][k] = tmpCorrelationMatrix.getEntry(j, k);
							tmpPvalue[j][k] = tmpPvalueMatrix.getEntry(j, k);
							if(Double.isNaN(tmpCorrelation[j][k])) tmpCorrelation[j][k] = 0.0;
							if(Double.isNaN(tmpPvalue[j][k])) tmpPvalue[j][k] = 0.0;
						}
					}
				}
			}

			this.correlationList.add(tmpCorrelation);
			this.pValueList.add(tmpPvalue);
		}
		
		/*** analyze year shifted correlation ***/
		for(s=0 ; s<spanSize.length ; s++){
			tmpMda = this.mda.clone();
			tmpMda.reviseAssembledData(spanSize[s]);

			for(i=0 ; i<tmpMda.assembledYears.size(); i++){
				tmpIndex = this.mda.assembledYears.indexOf(tmpMda.assembledYears.get(i));
				tmpMatrix = new double[tmpMda.assembledDistricts.get(i).size()][tmpMda.assembledData.get(i).size()];
				tmpCorrelation = this.correlationList.get(tmpIndex);
				tmpPvalue = this.pValueList.get(tmpIndex);
				
				for(j=0 ; j<tmpMda.assembledDistricts.get(i).size() ; j++)
					for(k=0 ; k<tmpMda.assembledData.get(i).size() ; k++)
						tmpMatrix[j][k] = tmpMda.assembledData.get(i).get(k).get(j);
				if(tmpMda.assembledDistricts.get(i).size()>2){
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpCorrelationMatrix = pc.getCorrelationMatrix();
					tmpPvalueMatrix = pc.getCorrelationPValues();
					
					for(j=0 ; j<tmpMda.titleList.size() ; j++){
						for(k=0 ; k<tmpMda.titleList.size() ; k++){
							if(yearSpan[j] == spanSize[s] ||  yearSpan[k] == spanSize[s]){
								tmpCorrelation[j][k] = tmpCorrelationMatrix.getEntry(j, k);
								tmpPvalue[j][k] = tmpPvalueMatrix.getEntry(j, k);
								if(Double.isNaN(tmpCorrelation[j][k])) tmpCorrelation[j][k] = 0.0;
								if(Double.isNaN(tmpPvalue[j][k])) tmpPvalue[j][k] = 0.0;
							}
						}
					}
				}
			}
		}
	
		/*** analyze GRDP & employment correlation ***/
		for(s=0 ; s<checkTitle.length ; s++){
			tmpMda = this.mda.clone();
			if(s==0) tmpMda.reviseToFitGRDPData();
			else if(s==1) tmpMda.reviseToFitEmploymentData();
	
			for(i=0 ; i<tmpMda.assembledYears.size(); i++){
				tmpIndex = this.mda.assembledYears.indexOf(tmpMda.assembledYears.get(i));
				tmpMatrix = 
						new double[tmpMda.assembledDistricts.get(i).size()][tmpMda.assembledData.get(i).size()];
				tmpCorrelation = this.correlationList.get(tmpIndex);
				tmpPvalue = this.pValueList.get(tmpIndex);
				
				for(j=0 ; j<tmpMda.assembledDistricts.get(i).size() ; j++)
					for(k=0 ; k<tmpMda.assembledData.get(i).size() ; k++)
						tmpMatrix[j][k] = tmpMda.assembledData.get(i).get(k).get(j);
				if(tmpMda.assembledDistricts.get(i).size()>2){
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpCorrelationMatrix = pc.getCorrelationMatrix();
					tmpPvalueMatrix = pc.getCorrelationPValues();
					
					for(j=0 ; j<tmpMda.titleList.size() ; j++){
						for(k=0 ; k<tmpMda.titleList.size() ; k++){
							if(tmpMda.titleList.get(j).contains(checkTitle[s]) 
									|| tmpMda.titleList.get(k).contains(checkTitle[s])){
								tmpCorrelation[j][k] = tmpCorrelationMatrix.getEntry(j, k);
								tmpPvalue[j][k] = tmpPvalueMatrix.getEntry(j, k);
								if(Double.isNaN(tmpCorrelation[j][k])) tmpCorrelation[j][k] = 0.0;
								if(Double.isNaN(tmpPvalue[j][k])) tmpPvalue[j][k] = 0.0;
							}
						}
					}
				}
			}	
		}
		
	}
	
	public void analyzeMigrationData(String[] populationFile, String inflowFile, String outflowFile){
		
		DataReader dr = new DataReader();
		dr.setFirstLevelDistrictIndicator();
		dr.readPopulationData(populationFile[0]);
		if(populationFile.length > 1)
			for(int i=1 ; i<populationFile.length ; i++) dr.addPopulationData(populationFile[i]);
		dr.removeRedundancy();
		
		EntropyCalculator ec = new EntropyCalculator();
		ec.calculateAgedIndex(dr.pData);
		ec.calculateProbability(dr.pData);
		ec.calculateEntropy(dr.pData);
		
		this.mda.readMigrationData(inflowFile, outflowFile);
		this.mda.assemblePopulationAndMigration(dr.pData);
		this.mda.removeEmptyAssembledData();
	}
	
	public void analyzeMigrationGRDP(String[] populationFile, String inflowFile, String outflowFile,
																	String grdpFile){
		
		DataReader dr = new DataReader();
		dr.setFirstLevelDistrictIndicator();
		dr.readPopulationData(populationFile[0]);
		if(populationFile.length > 1)
			for(int i=1 ; i<populationFile.length ; i++) dr.addPopulationData(populationFile[i]);
		dr.removeRedundancy();
		
		EntropyCalculator ec = new EntropyCalculator();
		ec.calculateAgedIndex(dr.pData);
		ec.calculateProbability(dr.pData);
		ec.calculateEntropy(dr.pData);
		
		this.mda.readMigrationData(inflowFile, outflowFile);
		this.mda.readGRDPdata(grdpFile);
		this.mda.assemblePopulationAndMigration(dr.pData);
	//	this.mda.reviseToFitGRDPData();
	}
	
	public void analyzeMigrationEmploymentRate(String[] populationFile, String inflowFile, String outflowFile,
																				String employmentFile){

		DataReader dr = new DataReader();
		dr.setFirstLevelDistrictIndicator();
		dr.readPopulationData(populationFile[0]);
		if(populationFile.length > 1)
			for(int i=1 ; i<populationFile.length ; i++) dr.addPopulationData(populationFile[i]);
		dr.removeRedundancy();

		EntropyCalculator ec = new EntropyCalculator();
		ec.calculateAgedIndex(dr.pData);
		ec.calculateProbability(dr.pData);
		ec.calculateEntropy(dr.pData);

		this.mda.readMigrationData(inflowFile, outflowFile);
		this.mda.readEmploymentRates(employmentFile);
		this.mda.assemblePopulationAndMigration(dr.pData);
	//	this.mda.reviseToFitEmploymentData();
	}
	
	public void analyzeMigrationData(String[] populationFile, String inflowFile, String outflowFile,
															String grdpFile, String employmentFile){

		DataReader dr = new DataReader();
		dr.setFirstLevelDistrictIndicator();
		dr.readPopulationData(populationFile[0]);
		if(populationFile.length > 1)
			for(int i=1 ; i<populationFile.length ; i++) dr.addPopulationData(populationFile[i]);
		dr.removeRedundancy();
		
		EntropyCalculator ec = new EntropyCalculator();
		ec.calculateAgedIndex(dr.pData);
		ec.calculateProbability(dr.pData);
		ec.calculateEntropy(dr.pData);
		
		this.mda.readMigrationData(inflowFile, outflowFile);
		this.mda.readGRDPdata(grdpFile);
		this.mda.readEmploymentRates(employmentFile);
		this.mda.assemblePopulationAndMigration(dr.pData);
	}
	
	public void analyzeMigrationData(String[] populationFile, String inflowFile, String outflowFile, 
														ArrayList<String> removeList){

		DataReader dr = new DataReader();
		dr.setFirstLevelDistrictIndicator();
		dr.readPopulationData(populationFile[0]);
		if(populationFile.length > 1)
			for(int i=1 ; i<populationFile.length ; i++) dr.addPopulationData(populationFile[i]);
		dr.removeRedundancy();
		
		EntropyCalculator ec = new EntropyCalculator();
		ec.calculateAgedIndex(dr.pData);
		ec.calculateProbability(dr.pData);
		ec.calculateEntropy(dr.pData);
		
		this.mda.readMigrationData(inflowFile, outflowFile);
		this.mda.assemblePopulationAndMigration(dr.pData);
		this.mda.removeEmptyAssembledData();
		
		if(removeList.size()>0) this.mda.removeSpecificRegions(removeList);
	}
	
	public void analyzeMigrationData(ArrayList<String> retainList, String[] populationFile, 
															String inflowFile, String outflowFile){

		DataReader dr = new DataReader();
		dr.setFirstLevelDistrictIndicator();
		dr.readPopulationData(populationFile[0]);
		if(populationFile.length > 1)
			for(int i=1 ; i<populationFile.length ; i++) dr.addPopulationData(populationFile[i]);
		dr.removeRedundancy();

		EntropyCalculator ec = new EntropyCalculator();
		ec.calculateAgedIndex(dr.pData);
		ec.calculateProbability(dr.pData);
		ec.calculateEntropy(dr.pData);

		this.mda.readMigrationData(inflowFile, outflowFile);
		this.mda.assemblePopulationAndMigration(dr.pData);
		this.mda.removeEmptyAssembledData();

		if(retainList.size()>0) this.mda.retainSpecificRegions(retainList);
}
	
	public void printAllCorrelationship(String outputFile){
		int i, j, k;

		if(this.mda.titleList.size() != this.mda.assembledData.get(0).size()) 
			System.err.println("titles doesn't match with data.");
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			for(i=0 ; i<this.mda.assembledYears.size(); i++){
				pw.println(this.mda.assembledYears.get(i));
				pw.print("Correlation");
				for(j=0 ; j<this.correlationList.get(i).length ; j++) pw.print("\t");
				pw.println("P-value");
					
				for(j=0 ; j<this.mda.titleList.size() ; j++) pw.print("\t"+this.mda.titleList.get(j));
				pw.print("\t\t");
				for(j=0 ; j<this.mda.titleList.size() ; j++) pw.print("\t"+this.mda.titleList.get(j));
				pw.println();
				
				for(j=0 ; j<this.correlationList.get(i).length ; j++){
					pw.print(this.mda.titleList.get(j));
					for(k=0 ; k<this.correlationList.get(i)[j].length ; k++) pw.print("\t"+this.correlationList.get(i)[j][k]);
					pw.print("\t\t"+this.mda.titleList.get(j));
					for(k=0 ; k<this.pValueList.get(i)[j].length ; k++) pw.print("\t"+this.pValueList.get(i)[j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.close();
		}catch(IOException e) {
			System.err.println(e);			
		}
	}
	
	public void printCorrelationship(String outputFile){
		int i, j, k;		
		String[] rowTitle = {"-1y Net", "-1y Net/cap", 
										"+1y Net", "+1y Net/cap", "+3y Net", "+3y Net/cap",
										"+5y Net", "+5y Net/cap", 
	//									"-3y Net", "-3y Net/cap",
	//									"-5y Net", "-5y Net/cap"
										};
		String[] columnTitle = {"Population(P)", "Entropy(H)", "Aged_index(Ai)",
											"GRDP", "GRDP/P", "employment", 
				//							"Log(P)", "Log(Ai)", "1-Ai", "1/H", "1-H", "1-H/max", 
											"Log(Ai)(1/H)"
				//							,"Log(Ai)(1-H)", "Log(Ai)(1-H/max)", "(1-Ai)(1/H)", "(1-Ai)(1-H)",
				//							"(1-Ai)(1-H/max)", "(H)(Ai)", "(H)Log(Ai)"
											};
		
		ArrayList<String> rowList = new ArrayList<String>();
		ArrayList<String> columnList = new ArrayList<String>();

		int rowSize = rowTitle.length;
		int columnSize = columnTitle.length;
		double tmpSum, tmpValue;
		boolean yearCheck;
		int[] rowIndex = new int[rowSize];
		int[] columnIndex = new int[columnSize];
		boolean[] rowCheck = new boolean[this.mda.titleList.size()];
		boolean[] columnCheck = new boolean[this.mda.titleList.size()];
		
		if(this.mda.titleList.size() != this.mda.assembledData.get(0).size()) 
			System.err.println("titles doesn't match with data.");
		
		Collections.addAll(rowList, rowTitle);
		Collections.addAll(columnList, columnTitle);
		
		for(i=0 ; i<rowSize ; i++) rowIndex[i] = this.mda.titleList.indexOf(rowList.get(i));
		for(i=0 ; i<columnSize ; i++) columnIndex[i] = this.mda.titleList.indexOf(columnList.get(i));
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			for(i=0 ; i<this.mda.assembledYears.size(); i++){
				/*** check data existence ***/
				yearCheck = false;
				for(j=0 ; j<rowCheck.length ; j++) rowCheck[j] = false;
				for(j=0 ; j<columnCheck.length ; j++) columnCheck[j] = false;
				
				for(j=0 ; j<rowSize ; j++){
					tmpSum = 0.0;
					for(k=0 ; k<columnSize ; k++){
						tmpValue = this.correlationList.get(i)[rowIndex[j]][columnIndex[k]];
						if(!Double.isNaN(tmpValue)) tmpSum += Math.abs(tmpValue);
					}
					if(tmpSum > 0){
						rowCheck[rowIndex[j]] = true;
						yearCheck = true;
					}
				}
				
				for(j=0 ; j<columnSize ; j++){
					tmpSum = 0.0;
					for(k=0 ; k<rowSize ; k++){
						tmpValue = this.correlationList.get(i)[rowIndex[k]][columnIndex[j]];
						if(!Double.isNaN(tmpValue)) tmpSum += Math.abs(tmpValue);
					}
					if(tmpSum > 0) columnCheck[columnIndex[j]] = true;
				}
				
				/*** print correlation ***/
				if(yearCheck){	
					pw.println(this.mda.assembledYears.get(i));
					
					pw.print("Correlation");
					for(j=0 ; j<columnSize; j++) if(columnCheck[columnIndex[j]]) pw.print("\t");
					pw.println("\t\tP-value");
						
					for(j=0 ; j<columnSize ; j++) if(columnCheck[columnIndex[j]]) pw.print("\t"+columnList.get(j));
					pw.print("\t\t");
					for(j=0 ; j<columnSize ; j++) if(columnCheck[columnIndex[j]]) pw.print("\t"+columnList.get(j));
					pw.println();
					
					for(j=0 ; j<rowSize ; j++){
						if(rowCheck[rowIndex[j]]){
							pw.print(this.mda.titleList.get(rowIndex[j]));
							for(k=0 ; k<columnSize ; k++) if(columnCheck[columnIndex[k]])
								pw.print("\t"+this.correlationList.get(i)[rowIndex[j]][columnIndex[k]]);
							pw.print("\t\t"+this.mda.titleList.get(rowIndex[j]));
							for(k=0 ; k<columnSize ; k++) if(columnCheck[columnIndex[k]])
								pw.print("\t"+this.pValueList.get(i)[rowIndex[j]][columnIndex[k]]);
							pw.println();
						}
					}
					pw.println();
				}
			}
			pw.println();
			
			pw.close();
		}catch(IOException e) {
			System.err.println(e);			
		}
	}
	
	public static void main(String[] args) throws CloneNotSupportedException {
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/population/";
		String[] populationFile = new String[1];
		//populationFile[0] =  filePath + "migration/population_survey_data.txt";
		populationFile[0] =  filePath + "migration/populatoin ID registered mid-year_modified.txt";
		//String[] populationFile = new String[2];
		//populationFile[0] =  filePath + "migration/population_registered_data.txt";
		//populationFile[1] =  filePath + "migration/population_registered_data2.txt";
		
		String grdpFile = filePath + "migration/GRDP_data_updated.txt";
		String employmentRateFile = filePath + "migration/Employment_rate.txt";
		String inflowFile = filePath + "migration/migration_in.txt";
		String outflowFile = filePath + "migration/migration_out.txt";
		
		//String outputFile = filePath + "migration/populatoin_migration_survey_assembled.txt";
		//String correlationFile = filePath + "migration/correlation_population_migration_survey_employment.txt";
		//String outputFile = filePath + "migration/populatoin_migration_registered_assembled.txt";
		//String correlationFile = filePath + "migration/correlation_population_migration_registered_grdp.txt";
		//String outputFile = filePath + "migration/populatoin_migration_registered_assembled.txt";
		String correlationFile = filePath + "migration/correlation_population_migration_mid-year.txt";
		String outputFile = filePath + "migration/populatoin_migration_mid-year_assembled.txt";
		
		MigrationCorrelationAnalyzer mca = new MigrationCorrelationAnalyzer();
		
		ArrayList<String> removeList = new ArrayList<String>();
		removeList.add("서울특별시");
		removeList.add("광역시");
		removeList.add("경기도");
		
		ArrayList<String> retainList = new ArrayList<String>();
		retainList.add("서울특별시");
		//retainList.add("광역시");
		//retainList.add("경기도");
		
		System.out.println("correlation analysis process start:");
		//mca.analyzeMigrationData(populationFile, inflowFile, outflowFile);
		//mca.analyzeMigrationData(populationFile, inflowFile, outflowFile, removeList);
		//mca.analyzeMigrationData(retainList, populationFile, inflowFile, outflowFile);
		//mca.analyzeMigrationGRDP(populationFile, inflowFile, outflowFile, grdpFile);
		//mca.analyzeMigrationEmploymentRate(populationFile, inflowFile, outflowFile, employmentRateFile);
		mca.analyzeMigrationData(populationFile, inflowFile, outflowFile, grdpFile, employmentRateFile);

		//mca.mda.reviseToFitGRDPData();
		//mca.mda.reviseToFitEmploymentData();
		//mca.mda.removeSpecificRegions(removeList);
		//mca.mda.retainSpecificRegions(retainList);
		//mca.mda.retainUrbanRegions();
		//mca.mda.retainRuralRegions();
		//mca.mda.retainAgedRegion();
		//mca.mda.removeAgedRegion();
		mca.mda.printAssembledData(outputFile);
		//mca.mda.printNormalizedAssembledData(outputFile);
		System.out.println("data setting complete.");
		
		mca.calculateCorrelationship();
		System.out.println("correlation calculation complete.");
		//mca.printCorrelationship(correlationFile);
		//mca.printAllCorrelationship(correlationFile);
		System.out.println("process complete.");
		
	}

}
