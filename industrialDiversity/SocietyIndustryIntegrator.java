package industrialDiversity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class SocietyIndustryIntegrator {
	
	public SocietyIndustryIntegrator(){
		
	}
	
	public void proceedSocietyDataAnalysis(SocietyMicrodataAnalyzer sma,  double[] gradeWeight, String filePath,
																		String locationCodePath, String locationCodeFile){
		
		System.out.println("society data analysis process is started.");
		System.out.print("location code reading: ");
		sma.readLocationCode(locationCodePath, locationCodeFile);
		System.out.println("complete");

		System.out.print("variables initializing: ");
		sma.initiate();
		System.out.println("complete");
		
		System.out.print("microdata reading: ");
		sma.extractSocieyMicrodata(filePath, "_Social_MicroData.txt");
		System.out.println("complete");	
		
		System.out.print("statistics calculating: ");
		sma.calculateSocietyStatistics();
		sma.calculateSatisfactionTotalScore(gradeWeight);
		System.out.println("complete");	
				
		System.out.print("memory clearing: ");
		sma.clearUnusingMemory();
		sma.clearSamplesMemory();
		System.out.println("complete");	
		
		System.out.println("society data analysis process is completed.");
		System.out.println();
	}
	
	public void proceedIndustryDataAnalysis(IndustryEntropyCalculator iec, String filePath, String profitFile){
		
		System.out.println("industry data analysis process is started.");
		System.out.print("codes reading: ");
		iec.readStandardCodes(filePath);
		System.out.println("complete");	
		System.out.println();
		
		System.out.println("[industry entropy] ");
		System.out.print("employee reading: ");
		iec.readEmployee(filePath+"extracted/", "_microdataCode.txt");
		System.out.println("complete");	
		
		System.out.print("statistics calculating: ");		
		iec.calculateEmployeeStatistics();
		System.out.println("complete");	
						
		System.out.print("entropy calculating: ");
		iec.calculateEmployeeEntropy();
		System.out.println("complete");	
				
		System.out.print("memory clearing: ");
		iec.clearUnusingMemoryIndustry();
		iec.clearEmployeeMemoryIndustry();
		System.out.println("complete");	
		System.out.println();
		
		System.out.println("[size entropy] ");
		System.out.print("employee reading: ");
		iec.readEmployeeBySize(iec.n_sizeGroup, filePath+"extracted/", "_microdataCode.txt");
		System.out.println("complete");	
		
		System.out.print("statistics calculating: ");	
		iec.calculateEmployeeSizeStatistics();
		System.out.println("complete");	
						
		System.out.print("entropy calculating: ");
		iec.calculateEmployeeSizeEntropy();
		System.out.println("complete");	
		
		System.out.print("memory clearing: ");
		iec.clearUnusingMemorySize();
		iec.clearEmployeeMemorySize();
		System.out.println("complete");	
		System.out.println();
		
		System.out.println("[level entropy] ");
		System.out.print("employee reading: ");
		iec.readEmployeeByLevel(iec.n_levelGroup, profitFile, filePath+"extracted/", "_microdataCode.txt");
		System.out.println("complete");	
		
		System.out.print("statistics calculating: ");		
		iec.calculateEmployeeLevelStatistics();
		System.out.println("complete");	
						
		System.out.print("entropy calculating: ");
		iec.calculateEmployeeLevelEntropy();
		System.out.println("complete");	
		
		System.out.print("memory clearing: ");
		iec.clearUnusingMemoryLevel();
		iec.clearEmployeeMemoryLevel();
		System.out.println("complete");	
		System.out.println();
		
		System.out.println("[age entropy] ");
		System.out.print("employee reading: ");
		iec.readEmployeeByAge(iec.n_ageGroup, filePath+"extracted/", "_microdataCode.txt");
		System.out.println("complete");	
		
		System.out.print("statistics calculating: ");		
		iec.calculateEmployeeAgeStatistics();
		System.out.println("complete");	
						
		System.out.print("entropy calculating: ");
		iec.calculateEmployeeAgeEntropy();
		System.out.println("complete");	
		
		System.out.print("memory clearing: ");
		iec.clearUnusingMemoryAge();
		iec.clearEmployeeMemoryAge();
		System.out.println("complete");	
		
		System.out.print("entropy normalizing: ");
		iec.normalizeEntropy();
		System.out.println("complete");	
		
		System.out.println("industry data analysis process is completed.");
	}
	

	
	public void printTotalIntegratedResults(String outputFile, SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.print("range");
			for(i=0 ; i<iec.duration ; i++) pw.print("\t"+(iec.startYear+i));
			pw.println();
			pw.print("N_industry:");
			for(i=0 ; i<iec.duration ; i++) pw.print("\t"+iec.n_industry[i]);
			pw.println();
			pw.println("N_size_groups:"+iec.n_sizeGroup);
			pw.println("N_level_groups:"+iec.n_levelGroup);
			pw.println("N_age_groups:"+iec.n_ageGroup);
			
			
			/*** print life satisfaction v.s. entropy ***/
			pw.println("life satisfaction: nation");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1){
					pw.print(iec.startYear+i+"\t"+sma.lifeSatisfactionScoreNation[i]);
					pw.print("\t"+iec.entropyNation[i]);
					pw.print("\t"+iec.entropyNationBySize[i]);
					pw.print("\t"+iec.entropyNationByLevel[i]);
					pw.print("\t"+iec.entropyNationByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			pw.println("life satisfaction: rural");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
					pw.print(iec.startYear+i+"\t"+sma.lifeSatisfactionScoreRural[i]);
					pw.print("\t"+iec.entropyRural[i]);
					pw.print("\t"+iec.entropyRuralBySize[i]);
					pw.print("\t"+iec.entropyRuralByLevel[i]);
					pw.print("\t"+iec.entropyRuralByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			pw.println("life satisfaction: urban");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
					pw.print(iec.startYear+i+"\t"+sma.lifeSatisfactionScoreUrban[i]);
					pw.print("\t"+iec.entropyUrban[i]);
					pw.print("\t"+iec.entropyUrbanBySize[i]);
					pw.print("\t"+iec.entropyUrbanByLevel[i]);
					pw.print("\t"+iec.entropyUrbanByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			/*** print living condition v.s. entropy ***/
			pw.println("living condition: nation");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1){
					pw.print(iec.startYear+i+"\t"+sma.livingConditionScoreNation[i]);
					pw.print("\t"+iec.entropyNation[i]);
					pw.print("\t"+iec.entropyNationBySize[i]);
					pw.print("\t"+iec.entropyNationByLevel[i]);
					pw.print("\t"+iec.entropyNationByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			pw.println("living condition: rural");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
					pw.print(iec.startYear+i+"\t"+sma.livingConditionScoreRural[i]);
					pw.print("\t"+iec.entropyRural[i]);
					pw.print("\t"+iec.entropyRuralBySize[i]);
					pw.print("\t"+iec.entropyRuralByLevel[i]);
					pw.print("\t"+iec.entropyRuralByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			pw.println("living condition: urban");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
					pw.print(iec.startYear+i+"\t"+sma.livingConditionScoreUrban[i]);
					pw.print("\t"+iec.entropyUrban[i]);
					pw.print("\t"+iec.entropyUrbanBySize[i]);
					pw.print("\t"+iec.entropyUrbanByLevel[i]);
					pw.print("\t"+iec.entropyUrbanByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			/*** print  income satisfaction v.s. entropy ***/
			pw.println("income satisfaction: nation");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1){
					pw.print(iec.startYear+i+"\t"+sma.incomeSatisfactionScoreNation[i]);
					pw.print("\t"+iec.entropyNation[i]);
					pw.print("\t"+iec.entropyNationBySize[i]);
					pw.print("\t"+iec.entropyNationByLevel[i]);
					pw.print("\t"+iec.entropyNationByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			pw.println("income satisfaction: rural");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
					pw.print(iec.startYear+i+"\t"+sma.incomeSatisfactionScoreRural[i]);
					pw.print("\t"+iec.entropyRural[i]);
					pw.print("\t"+iec.entropyRuralBySize[i]);
					pw.print("\t"+iec.entropyRuralByLevel[i]);
					pw.print("\t"+iec.entropyRuralByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			pw.println("income satisfaction: urban");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
					pw.print(iec.startYear+i+"\t"+sma.incomeSatisfactionScoreUrban[i]);
					pw.print("\t"+iec.entropyUrban[i]);
					pw.print("\t"+iec.entropyUrbanBySize[i]);
					pw.print("\t"+iec.entropyUrbanByLevel[i]);
					pw.print("\t"+iec.entropyUrbanByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			/*** print consumption satisfaction v.s. entropy ***/
			pw.println("consumption satisfaction: nation");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1){
					pw.print(iec.startYear+i+"\t"+sma.consumptionSatisfactionScoreNation[i]);
					pw.print("\t"+iec.entropyNation[i]);
					pw.print("\t"+iec.entropyNationBySize[i]);
					pw.print("\t"+iec.entropyNationByLevel[i]);
					pw.print("\t"+iec.entropyNationByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			pw.println("consumption satisfaction: rural");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
					pw.print(iec.startYear+i+"\t"+sma.consumptionSatisfactionScoreRural[i]);
					pw.print("\t"+iec.entropyRural[i]);
					pw.print("\t"+iec.entropyRuralBySize[i]);
					pw.print("\t"+iec.entropyRuralByLevel[i]);
					pw.print("\t"+iec.entropyRuralByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			pw.println("consumption satisfaction: urban");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
					pw.print(iec.startYear+i+"\t"+sma.consumptionSatisfactionScoreUrban[i]);
					pw.print("\t"+iec.entropyUrban[i]);
					pw.print("\t"+iec.entropyUrbanBySize[i]);
					pw.print("\t"+iec.entropyUrbanByLevel[i]);
					pw.print("\t"+iec.entropyUrbanByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			/*** print employment stability v.s. entropy ***/
			pw.println("employment stability: nation");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1){
					pw.print(iec.startYear+i+"\t"+sma.employmentStabilityScoreNation[i]);
					pw.print("\t"+iec.entropyNation[i]);
					pw.print("\t"+iec.entropyNationBySize[i]);
					pw.print("\t"+iec.entropyNationByLevel[i]);
					pw.print("\t"+iec.entropyNationByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			pw.println("employment stability: rural");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
					pw.print(iec.startYear+i+"\t"+sma.employmentStabilityScoreRural[i]);
					pw.print("\t"+iec.entropyRural[i]);
					pw.print("\t"+iec.entropyRuralBySize[i]);
					pw.print("\t"+iec.entropyRuralByLevel[i]);
					pw.print("\t"+iec.entropyRuralByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			pw.println("employment stability: urban");
			pw.print("Year\tSatisfaction score");
			pw.print("\tIndustry entropy");
			pw.print("\tEmployee size entopty");
			pw.print("\tProfit level entopty");
			pw.print("\tBusiness year entopty");
			pw.println();
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
					pw.print(iec.startYear+i+"\t"+sma.employmentStabilityScoreUrban[i]);
					pw.print("\t"+iec.entropyUrban[i]);
					pw.print("\t"+iec.entropyUrbanBySize[i]);
					pw.print("\t"+iec.entropyUrbanByLevel[i]);
					pw.print("\t"+iec.entropyUrbanByAge[i]);
					pw.println();	
				}
			}
			pw.println();
			
			pw.close();
		}catch(IOException e) {}	
	}
	
	public void printCategorialIntegratedResults(String outputFile, SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i,j;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.print("range");
			for(i=0 ; i<iec.duration ; i++) pw.print("\t"+(iec.startYear+i));
			pw.println();
			pw.print("N_category:");
			for(i=0 ; i<iec.duration ; i++) pw.print("\t"+iec.n_category[i]);
			pw.println();
			pw.print("N_industry:");
			for(i=0 ; i<iec.duration ; i++) pw.print("\t"+iec.n_industry[i]);
			pw.println();
			pw.println("N_size_groups:"+iec.n_sizeGroup);
			pw.println("N_level_groups:"+iec.n_levelGroup);
			pw.println("N_age_groups:"+iec.n_ageGroup);
			
			for(j=0 ; j<iec.n_category[0] ; j++){
				/*** print life satisfaction v.s. entropy ***/
				pw.println();
				pw.println("category: "+iec.categoryName.get(i).get(j));
				pw.println();
				pw.println("life satisfaction: nation");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][1]==1){
						pw.print(iec.startYear+i+"\t"+sma.lifeSatisfactionScoreNation[i]);
						pw.print("\t"+iec.entropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				pw.println("life satisfaction: rural");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
						pw.print(iec.startYear+i+"\t"+sma.lifeSatisfactionScoreRural[i]);
						pw.print("\t"+iec.ruralEntropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				pw.println("life satisfaction: urban");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
						pw.print(iec.startYear+i+"\t"+sma.lifeSatisfactionScoreUrban[i]);
						pw.print("\t"+iec.urbanEntropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				/*** print living condition v.s. entropy ***/
				pw.println("living condition: nation");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][2]==1){
						pw.print(iec.startYear+i+"\t"+sma.livingConditionScoreNation[i]);
						pw.print("\t"+iec.entropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				pw.println("living condition: rural");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
						pw.print(iec.startYear+i+"\t"+sma.livingConditionScoreRural[i]);
						pw.print("\t"+iec.ruralEntropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				pw.println("living condition: urban");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
						pw.print(iec.startYear+i+"\t"+sma.livingConditionScoreUrban[i]);
						pw.print("\t"+iec.urbanEntropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				/*** print  income satisfaction v.s. entropy ***/
				pw.println("income satisfaction: nation");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][3]==1){
						pw.print(iec.startYear+i+"\t"+sma.incomeSatisfactionScoreNation[i]);
						pw.print("\t"+iec.entropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				pw.println("income satisfaction: rural");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
						pw.print(iec.startYear+i+"\t"+sma.incomeSatisfactionScoreRural[i]);
						pw.print("\t"+iec.ruralEntropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				pw.println("income satisfaction: urban");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
						pw.print(iec.startYear+i+"\t"+sma.incomeSatisfactionScoreUrban[i]);
						pw.print("\t"+iec.urbanEntropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				/*** print consumption satisfaction v.s. entropy ***/
				pw.println("consumption satisfaction: nation");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][4]==1){
						pw.print(iec.startYear+i+"\t"+sma.consumptionSatisfactionScoreNation[i]);
						pw.print("\t"+iec.entropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				pw.println("consumption satisfaction: rural");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
						pw.print(iec.startYear+i+"\t"+sma.consumptionSatisfactionScoreRural[i]);
						pw.print("\t"+iec.ruralEntropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				pw.println("consumption satisfaction: urban");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
						pw.print(iec.startYear+i+"\t"+sma.consumptionSatisfactionScoreUrban[i]);
						pw.print("\t"+iec.urbanEntropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				/*** print employment stability v.s. entropy ***/
				pw.println("employment stability: nation");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][5]==1){
						pw.print(iec.startYear+i+"\t"+sma.employmentStabilityScoreNation[i]);
						pw.print("\t"+iec.entropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.entropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				pw.println("employment stability: rural");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
						pw.print(iec.startYear+i+"\t"+sma.employmentStabilityScoreRural[i]);
						pw.print("\t"+iec.ruralEntropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.ruralEntropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
				
				pw.println("employment stability: urban");
				pw.print("Year\tSatisfaction score");
				pw.print("\tIndustry entropy");
				pw.print("\tEmployee size entopty");
				pw.print("\tProfit level entopty");
				pw.print("\tBusiness year entopty");
				pw.println();
				for(i=0 ; i<iec.duration ; i++){
					if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
						pw.print(iec.startYear+i+"\t"+sma.employmentStabilityScoreUrban[i]);
						pw.print("\t"+iec.urbanEntropyByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyBySizeByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyByLevelByIndustry.get(i)[j]);
						pw.print("\t"+iec.urbanEntropyByAgeByIndustry.get(i)[j]);
						pw.println();	
					}
				}
				pw.println();
			}
			
			pw.close();
		}catch(IOException e) {}	
	}
	
	public void printRegionalIntegratedResults(String outputFile, SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i, k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.print("range");
			for(i=0 ; i<iec.duration ; i++) pw.print("\t"+(iec.startYear+i));
			pw.println();
			pw.print("N_industry:");
			for(i=0 ; i<iec.duration ; i++) pw.print("\t"+iec.n_industry[i]);
			pw.println();
			pw.println("N_size_groups:"+iec.n_sizeGroup);
			pw.println("N_level_groups:"+iec.n_levelGroup);
			pw.println("N_age_groups:"+iec.n_ageGroup);
			
			
			/*** print life satisfaction v.s. entropy ***/
			pw.println("life satisfaction: nation");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1){	
					pw.println("year: "+(iec.startYear+i));	
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i] ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.lifeSatisfactionScore[i][k]);
						pw.print("\t"+iec.entropy.get(i)[k]);
						pw.print("\t"+iec.entropyBySize.get(i)[k]);
						pw.print("\t"+iec.entropyByLevel.get(i)[k]);
						pw.print("\t"+iec.entropyByAge.get(i)[k]);
						pw.println();	
					}
				}
			}
			pw.println();
			
			pw.println("life satisfaction: rural");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.ruralLifeSatisfactionScore[i][k]);
						pw.print("\t"+iec.ruralEntropy.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyBySize.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyByLevel.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyByAge.get(i)[k]);
						pw.println();	
					}
				}
			}
			pw.println();
			
			pw.println("life satisfaction: urban");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.urbanLifeSatisfactionScore[i][k]);
						pw.print("\t"+iec.urbanEntropy.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyBySize.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyByLevel.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyByAge.get(i)[k]);
						pw.println();	
					}
				}
			}
			pw.println();
			
			/*** print living condition v.s. entropy ***/
			pw.println("living condition: nation");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.livingConditionScore[i][k]);
						pw.print("\t"+iec.entropy.get(i)[k]);
						pw.print("\t"+iec.entropyBySize.get(i)[k]);
						pw.print("\t"+iec.entropyByLevel.get(i)[k]);
						pw.print("\t"+iec.entropyByAge.get(i)[k]);
						pw.println();	
					}
					
				}
			}
			pw.println();
			
			pw.println("living condition: rural");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.ruralLivingConditionScore[i][k]);
						pw.print("\t"+iec.ruralEntropy.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyBySize.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyByLevel.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyByAge.get(i)[k]);
						pw.println();	
					}
				}
			}
			pw.println();
			
			pw.println("living condition: urban");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.urbanLivingConditionScore[i][k]);
						pw.print("\t"+iec.urbanEntropy.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyBySize.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyByLevel.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyByAge.get(i)[k]);
						pw.println();	
					}
				}
			}
			pw.println();
			
			/*** print  income satisfaction v.s. entropy ***/
			pw.println("income satisfaction: nation");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.incomeSatisfactionScore[i][k]);
						pw.print("\t"+iec.entropy.get(i)[k]);
						pw.print("\t"+iec.entropyBySize.get(i)[k]);
						pw.print("\t"+iec.entropyByLevel.get(i)[k]);
						pw.print("\t"+iec.entropyByAge.get(i)[k]);
						pw.println();	
					}
					
				}
			}
			pw.println();
			
			pw.println("income satisfaction: rural");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.ruralIncomeSatisfactionScore[i][k]);
						pw.print("\t"+iec.ruralEntropy.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyBySize.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyByLevel.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyByAge.get(i)[k]);
						pw.println();	
					}
				}
			}
			pw.println();
			
			pw.println("income satisfaction: urban");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.urbanIncomeSatisfactionScore[i][k]);
						pw.print("\t"+iec.urbanEntropy.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyBySize.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyByLevel.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyByAge.get(i)[k]);
						pw.println();	
					}
				}
			}
			pw.println();
			
			/*** print consumption satisfaction v.s. entropy ***/
			pw.println("consumption satisfaction: nation");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.consumptionSatisfactionScore[i][k]);
						pw.print("\t"+iec.entropy.get(i)[k]);
						pw.print("\t"+iec.entropyBySize.get(i)[k]);
						pw.print("\t"+iec.entropyByLevel.get(i)[k]);
						pw.print("\t"+iec.entropyByAge.get(i)[k]);
						pw.println();	
					}
					
				}
			}
			pw.println();
			
			pw.println("consumption satisfaction: rural");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.ruralConsumptionSatisfactionScore[i][k]);
						pw.print("\t"+iec.ruralEntropy.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyBySize.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyByLevel.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyByAge.get(i)[k]);
						pw.println();	
					}
				}
			}
			pw.println();
			
			pw.println("consumption satisfaction: urban");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.urbanConsumptionSatisfactionScore[i][k]);
						pw.print("\t"+iec.urbanEntropy.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyBySize.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyByLevel.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyByAge.get(i)[k]);
						pw.println();	
					}
				}
			}
			pw.println();
			
			/*** print employment stability v.s. entropy ***/
			pw.println("employment stability: nation");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.employmentStabilityScore[i][k]);
						pw.print("\t"+iec.entropy.get(i)[k]);
						pw.print("\t"+iec.entropyBySize.get(i)[k]);
						pw.print("\t"+iec.entropyByLevel.get(i)[k]);
						pw.print("\t"+iec.entropyByAge.get(i)[k]);
						pw.println();	
					}
					
				}
			}
			pw.println();
			
			pw.println("employment stability: rural");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.ruralEmploymentStabilityScore[i][k]);
						pw.print("\t"+iec.ruralEntropy.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyBySize.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyByLevel.get(i)[k]);
						pw.print("\t"+iec.ruralEntropyByAge.get(i)[k]);
						pw.println();	
					}
				}
			}
			pw.println();
			
			pw.println("employment stability: urban");
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
					pw.println("year: "+(iec.startYear+i));			
					pw.print("Region\tSatisfaction score");
					pw.print("\tIndustry entropy");
					pw.print("\tEmployee size entopty");
					pw.print("\tProfit level entopty");
					pw.print("\tBusiness year entopty");
					pw.println();	
					for(k=0 ; k<sma.n_region[i]  ; k++){	
						pw.print(sma.locatoinName.get(i).get(k));
						pw.print("\t"+sma.urbanEmploymentStabilityScore[i][k]);
						pw.print("\t"+iec.urbanEntropy.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyBySize.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyByLevel.get(i)[k]);
						pw.print("\t"+iec.urbanEntropyByAge.get(i)[k]);
						pw.println();	
					}
				}
			}
			pw.println();
			
			pw.close();
		}catch(IOException e) {}	
	}
	
	public void printRegionalCategorialIntegratedResults(String outputFile, SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		int i, j, k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.print("range");
			for(i=0 ; i<iec.duration ; i++) pw.print("\t"+(iec.startYear+i));
			pw.println();
			pw.print("N_category:");
			for(i=0 ; i<iec.duration ; i++) pw.print("\t"+iec.n_category[i]);
			pw.println();
			pw.print("N_industry:");
			for(i=0 ; i<iec.duration ; i++) pw.print("\t"+iec.n_industry[i]);
			pw.println();
			pw.println("N_size_groups:"+iec.n_sizeGroup);
			pw.println("N_level_groups:"+iec.n_levelGroup);
			pw.println("N_age_groups:"+iec.n_ageGroup);
			
			
			/*** print life satisfaction v.s. entropy ***/
			for(i=0 ; i<iec.duration ; i++){
				pw.println("year: "+(iec.startYear+i));	
				pw.println();
				if(sma.responseExistence[i][1]==1){				
					pw.println("life satisfaction: nation");
					pw.println();
					for(j=0 ; j<iec.n_category[i] ; j++){
						pw.println("category: "+iec.categoryName.get(i).get(j));
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.lifeSatisfactionScore[i][k]);
							pw.print("\t"+iec.entropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
					pw.println("life satisfaction: rural");
					pw.println();
					for(j=0 ; j<iec.n_category[i] ; j++){			
						pw.println("category: "+iec.categoryName.get(i).get(j));
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.ruralLifeSatisfactionScore[i][k]);
							pw.print("\t"+iec.ruralEntropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
					pw.println("life satisfaction: urban");
					pw.println();		
					for(j=0 ; j<iec.n_category[i] ; j++){
						pw.println("category: "+iec.categoryName.get(i).get(j));
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.urbanLifeSatisfactionScore[i][k]);
							pw.print("\t"+iec.urbanEntropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				/*** print living condition v.s. entropy ***/
				pw.println("living condition: nation");
				if(sma.responseExistence[i][2]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){
						pw.println("category: "+iec.categoryName.get(i).get(j));	
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.livingConditionScore[i][k]);
							pw.print("\t"+iec.entropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				pw.println("living condition: rural");
				if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){				
						pw.println("category: "+iec.categoryName.get(i).get(j));
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.ruralLivingConditionScore[i][k]);
							pw.print("\t"+iec.ruralEntropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				pw.println("living condition: urban");
				if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){			
						pw.println("category: "+iec.categoryName.get(i).get(j));		
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.urbanLivingConditionScore[i][k]);
							pw.print("\t"+iec.urbanEntropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				/*** print  income satisfaction v.s. entropy ***/
				pw.println("income satisfaction: nation");
				if(sma.responseExistence[i][3]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){			
						pw.println("category: "+iec.categoryName.get(i).get(j));		
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.incomeSatisfactionScore[i][k]);
							pw.print("\t"+iec.entropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				pw.println("income satisfaction: rural");
				if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){				
						pw.println("category: "+iec.categoryName.get(i).get(j));		
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.ruralIncomeSatisfactionScore[i][k]);
							pw.print("\t"+iec.ruralEntropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				pw.println("income satisfaction: urban");
				if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){				
						pw.println("category: "+iec.categoryName.get(i).get(j));	
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.urbanIncomeSatisfactionScore[i][k]);
							pw.print("\t"+iec.urbanEntropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				/*** print consumption satisfaction v.s. entropy ***/
				pw.println("consumption satisfaction: nation");
				if(sma.responseExistence[i][4]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){	
						pw.println("category: "+iec.categoryName.get(i).get(j));	
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.consumptionSatisfactionScore[i][k]);
							pw.print("\t"+iec.entropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				pw.println("consumption satisfaction: rural");
				if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){	
						pw.println("category: "+iec.categoryName.get(i).get(j));	
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.ruralConsumptionSatisfactionScore[i][k]);
							pw.print("\t"+iec.ruralEntropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				pw.println("consumption satisfaction: urban");
				if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){	
						pw.println("category: "+iec.categoryName.get(i).get(j));
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.urbanConsumptionSatisfactionScore[i][k]);
							pw.print("\t"+iec.urbanEntropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				/*** print employment stability v.s. entropy ***/
				pw.println("employment stability: nation");
				if(sma.responseExistence[i][5]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){	
						pw.println("category: "+iec.categoryName.get(i).get(j));
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.employmentStabilityScore[i][k]);
							pw.print("\t"+iec.entropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.entropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				pw.println("employment stability: rural");
				if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){	
						pw.println("category: "+iec.categoryName.get(i).get(j));
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.ruralEmploymentStabilityScore[i][k]);
							pw.print("\t"+iec.ruralEntropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.ruralEntropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
				
				pw.println("employment stability: urban");
				if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
					for(j=0 ; j<iec.n_category[i] ; j++){	
						pw.println("category: "+iec.categoryName.get(i).get(j));
						pw.print("Region\tSatisfaction score");
						pw.print("\tIndustry entropy");
						pw.print("\tEmployee size entopty");
						pw.print("\tProfit level entopty");
						pw.print("\tBusiness year entopty");
						pw.println();	
						for(k=0 ; k<sma.n_region[i]  ; k++){	
							pw.print(sma.locatoinName.get(i).get(k));
							pw.print("\t"+sma.urbanEmploymentStabilityScore[i][k]);
							pw.print("\t"+iec.urbanEntropyByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyBySizeByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyByLevelByCategory.get(i)[k][j]);
							pw.print("\t"+iec.urbanEntropyByAgeByCategory.get(i)[k][j]);
							pw.println();	
						}
						pw.println();
					}
				}
				pw.println();
			}
			pw.close();
		}catch(IOException e) {}	
	}
	
	
	public static void main(String[] args) {

		SocietyIndustryIntegrator	sii;
		SocietyMicrodataAnalyzer	sma;
		IndustryEntropyCalculator	iec;
		
		/*** common properties ***/
		String[] industClassName = {"1st","2nd","3rd","4th","5th"};
		String[] regionClassName = {"do","gun","myun"};
		
		int regionClass	= 0;	   	 		//0: 시도,    1: 시군구,   2: 읍면동
		int startYear		= 1998;
		int endYear			= 2011;
		
		/*** society data properties ***/
		int n_category				= 7;
		int gradeDepth			= 5;

		double[] gradeWeight = {2,1,0,-1,-2};
		
		String societyFilePath = "/Users/Jemyung/Desktop/Research/data_storage/society/";
		String locationCodePath = societyFilePath+"location_code/";
		String locationCodeFile = "location_code.txt";
		
		/*** industry data properties ***/
		int categoryClass	= 0;		//0:대분류, 1: 중분류,  2: 소분류,   3: 세분류,	4:세세분
		int industryClass		= 3;		//0:대분류, 1: 중분류,  2: 소분류,   3: 세분류,	4:세세분
		int n_group				= 20;
		int sizeGroups			= n_group;
		int levelGroups		= n_group;
		int ageGroups			= n_group;
		
		String industryFilePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
		String indusrtyProfitFile = industryFilePath + "profit/industry_profit.txt";
		
		/*** integration process ***/		
		String totalIntegrationFile = societyFilePath + "integration/TotalIntegration_"
															+startYear+"-"+endYear+"_"
															+industClassName[categoryClass]+"_"
															+industClassName[industryClass]+"_"
															+regionClassName[regionClass]+"_"
															+n_group+".txt";
		String regionalIntegrationFile = societyFilePath + "integration/RegionalIntegration_"
															+startYear+"-"+endYear+"_"
															+industClassName[categoryClass]+"_"
															+industClassName[industryClass]+"_"
															+regionClassName[regionClass]+"_"
															+n_group+".txt";
		String regionalcategorialIntegrationFile = societyFilePath + "integration/RegionalCategorialIntegration_"
															+startYear+"-"+endYear+"_"
															+industClassName[categoryClass]+"_"
															+industClassName[industryClass]+"_"
															+regionClassName[regionClass]+"_"
															+n_group+".txt";	
		
		sii = new SocietyIndustryIntegrator();
		sma = new SocietyMicrodataAnalyzer(n_category, regionClass, gradeDepth, startYear, endYear);
		iec = new IndustryEntropyCalculator(startYear, endYear, categoryClass, industryClass, regionClass, sizeGroups, levelGroups, ageGroups);
		
		sii.proceedSocietyDataAnalysis(sma,  gradeWeight, societyFilePath, locationCodePath, locationCodeFile);
		sii.proceedIndustryDataAnalysis(iec, industryFilePath, indusrtyProfitFile);
		System.out.println();
		
		System.out.print("Integrated data printing: ");
		sii.printTotalIntegratedResults(totalIntegrationFile, sma, iec);
		sii.printRegionalIntegratedResults(regionalIntegrationFile, sma, iec);
		sii.printRegionalCategorialIntegratedResults(regionalcategorialIntegrationFile, sma, iec);
		System.out.println("complete");	
		
		System.out.println();
		System.out.println("all processes is completed");
	}
	
}
