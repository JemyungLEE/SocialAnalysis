package industrialDiversity;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.commons.math3.stat.correlation.*;
import org.apache.commons.math3.stat.ranking.RankingAlgorithm;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;

public class SocietyIndustryRelationshipAnalyzer {
	
	int duration;
	int startYear;
	int endYear;
	
	RealMatrix lifeRnation;
	RealMatrix lifeRrural;
	RealMatrix lifeRurban;
	RealMatrix lifePnation;
	RealMatrix lifePrural;
	RealMatrix lifePurban;
			
	RealMatrix livingRnation;
	RealMatrix livingRrural;
	RealMatrix livingRurban;
	RealMatrix livingPnation;
	RealMatrix livingPrural;
	RealMatrix livingPurban;

	RealMatrix incomeRnation;
	RealMatrix incomeRrural;
	RealMatrix incomeRurban;
	RealMatrix incomePnation;
	RealMatrix incomePrural;
	RealMatrix incomePurban;

	RealMatrix consumptionRnation;
	RealMatrix consumptionRrural;
	RealMatrix consumptionRurban;
	RealMatrix consumptionPnation;
	RealMatrix consumptionPrural;
	RealMatrix consumptionPurban;
	
	RealMatrix stabilityRnation;
	RealMatrix stabilityRrural;
	RealMatrix stabilityRurban;
	RealMatrix stabilityPnation;
	RealMatrix stabilityPrural;
	RealMatrix stabilityPurban;
	
	ArrayList<RealMatrix> regionalRcorrelationNation;		//year
	ArrayList<RealMatrix> regionalRcorrelationRural;			//year
	ArrayList<RealMatrix> regionalRcorrelationUrban;		//year
	ArrayList<RealMatrix> regionalPvalueNation;					//year
	ArrayList<RealMatrix> regionalPvalueRural;					//year
	ArrayList<RealMatrix> regionalPvalueUrban;					//year
	
	ArrayList<RealMatrix> categorialRcorrelationNation;		//year
	ArrayList<RealMatrix> categorialRcorrelationRural;		//year
	ArrayList<RealMatrix> categorialRcorrelationUrban;		//year
	ArrayList<RealMatrix> categorialPvalueNation;				//year
	ArrayList<RealMatrix> categorialPvalueRural;				//year
	ArrayList<RealMatrix> categorialPvalueUrban;				//year
	
	ArrayList<ArrayList<RealMatrix>> regionalRcorrelationTimeSeriesNation;		//satisfaction, region
	ArrayList<ArrayList<RealMatrix>> regionalRcorrelationTimeSeriesRural;			//satisfaction, region
	ArrayList<ArrayList<RealMatrix>> regionalRcorrelationTimeSeriesUrban;		//satisfaction, region
	ArrayList<ArrayList<RealMatrix>> regionalPvalueTimeSeriesNation;				//satisfaction, region
	ArrayList<ArrayList<RealMatrix>> regionalPvalueTimeSeriesRural;					//satisfaction, region
	ArrayList<ArrayList<RealMatrix>> regionalPvalueTimeSeriesUrban;					//satisfaction, region
	
	ArrayList<ArrayList<RealMatrix>> regionalCategorialRcorrelationNation;		//year, category
	ArrayList<ArrayList<RealMatrix>> regionalCategorialRcorrelationRural;			//year, category
	ArrayList<ArrayList<RealMatrix>> regionalCategorialRcorrelationUrban;			//year, category
	ArrayList<ArrayList<RealMatrix>> regionalCategorialPvalueNation;					//year, category
	ArrayList<ArrayList<RealMatrix>> regionalCategorialPvalueRural;					//year, category
	ArrayList<ArrayList<RealMatrix>> regionalCategorialPvalueUrban;					//year, category
	
	ArrayList<ArrayList<RealMatrix>> categorialRegionalRcorrelationNation;		//year, region
	ArrayList<ArrayList<RealMatrix>> categorialRegionalRcorrelationRural;			//year, region
	ArrayList<ArrayList<RealMatrix>> categorialRegionalRcorrelationUrban;			//year, region
	ArrayList<ArrayList<RealMatrix>> categorialRegionalPvalueNation;					//year, region
	ArrayList<ArrayList<RealMatrix>> categorialRegionalPvalueRural;					//year, region
	ArrayList<ArrayList<RealMatrix>> categorialRegionalPvalueUrban;					//year, region
	
	public SocietyIndustryRelationshipAnalyzer(){
		
	}
	
	public SocietyIndustryRelationshipAnalyzer(int start, int end){
		this.initiate(start, end);
	}
	
	public SocietyIndustryRelationshipAnalyzer(SocietyMicrodataAnalyzer sma){
		this.initiate(sma);
	}
	
	public SocietyIndustryRelationshipAnalyzer(IndustryEntropyCalculator iec){
		this.initiate(iec);
	}
	
	public void initiate(int start, int end){
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start +1;
	}
	
	public void initiate(SocietyMicrodataAnalyzer sma){
		this.startYear = sma.startYear;
		this.endYear = sma.endYear;
		this.duration = sma.duration;
	}
	
	public void initiate(IndustryEntropyCalculator iec){
		this.startYear = iec.startYear;
		this.endYear = iec.endYear;
		this.duration = iec.duration;
	}
	
	public void correlationPearsonsNation(SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i;
		int rowCnt;
		int column = 5;
		double[][] tmpMatrix;
		PearsonsCorrelation pc;
		
		/*** compare life satisfaction v.s. entropy ***/
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][1]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][1]==1){
				tmpMatrix[rowCnt][0] = sma.lifeSatisfactionScoreNation[i];
				tmpMatrix[rowCnt][1] = iec.entropyNation[i];
				tmpMatrix[rowCnt][2] = iec.entropyNationBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyNationByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyNationByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.lifeRnation = pc.getCorrelationMatrix();
			this.lifePnation = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
			
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.lifeSatisfactionScoreRural[i];
				tmpMatrix[rowCnt][1] = iec.entropyRural[i];
				tmpMatrix[rowCnt][2] = iec.entropyRuralBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyRuralByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyRuralByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.lifeRrural = pc.getCorrelationMatrix();
			this.lifePrural = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.lifeSatisfactionScoreUrban[i];
				tmpMatrix[rowCnt][1] = iec.entropyUrban[i];
				tmpMatrix[rowCnt][2] = iec.entropyUrbanBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyUrbanByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyUrbanByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.lifeRurban = pc.getCorrelationMatrix();
			this.lifePurban = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		/*** compare living condition v.s. entropy ***/
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][2]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][2]==1){
				tmpMatrix[rowCnt][0] = sma.livingConditionScoreNation[i];
				tmpMatrix[rowCnt][1] = iec.entropyNation[i];
				tmpMatrix[rowCnt][2] = iec.entropyNationBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyNationByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyNationByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.livingRnation = pc.getCorrelationMatrix();
			this.livingPnation = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
			
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.livingConditionScoreRural[i];
				tmpMatrix[rowCnt][1] = iec.entropyRural[i];
				tmpMatrix[rowCnt][2] = iec.entropyRuralBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyRuralByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyRuralByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.livingRrural = pc.getCorrelationMatrix();
			this.livingPrural = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.livingConditionScoreUrban[i];
				tmpMatrix[rowCnt][1] = iec.entropyUrban[i];
				tmpMatrix[rowCnt][2] = iec.entropyUrbanBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyUrbanByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyUrbanByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.livingRurban = pc.getCorrelationMatrix();
			this.livingPurban = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		/*** compare  income satisfaction v.s. entropy ***/
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][3]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][3]==1){
				tmpMatrix[rowCnt][0] = sma.incomeSatisfactionScoreNation[i];
				tmpMatrix[rowCnt][1] = iec.entropyNation[i];
				tmpMatrix[rowCnt][2] = iec.entropyNationBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyNationByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyNationByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.incomeRnation = pc.getCorrelationMatrix();
			this.incomePnation = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
			
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.incomeSatisfactionScoreRural[i];
				tmpMatrix[rowCnt][1] = iec.entropyRural[i];
				tmpMatrix[rowCnt][2] = iec.entropyRuralBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyRuralByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyRuralByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.incomeRrural = pc.getCorrelationMatrix();
			this.incomePrural = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.incomeSatisfactionScoreUrban[i];
				tmpMatrix[rowCnt][1] = iec.entropyUrban[i];
				tmpMatrix[rowCnt][2] = iec.entropyUrbanBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyUrbanByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyUrbanByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.incomeRurban = pc.getCorrelationMatrix();
			this.incomePurban = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		/*** compare consumption satisfaction v.s. entropy ***/
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][4]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][4]==1){
				tmpMatrix[rowCnt][0] = sma.consumptionSatisfactionScoreNation[i];
				tmpMatrix[rowCnt][1] = iec.entropyNation[i];
				tmpMatrix[rowCnt][2] = iec.entropyNationBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyNationByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyNationByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.consumptionRnation = pc.getCorrelationMatrix();
			this.consumptionPnation = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.consumptionSatisfactionScoreRural[i];
				tmpMatrix[rowCnt][1] = iec.entropyRural[i];
				tmpMatrix[rowCnt][2] = iec.entropyRuralBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyRuralByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyRuralByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.consumptionRrural = pc.getCorrelationMatrix();
			this.consumptionPrural = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.consumptionSatisfactionScoreUrban[i];
				tmpMatrix[rowCnt][1] = iec.entropyUrban[i];
				tmpMatrix[rowCnt][2] = iec.entropyUrbanBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyUrbanByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyUrbanByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.consumptionRurban = pc.getCorrelationMatrix();
			this.consumptionPurban = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		/*** compare employment stability v.s. entropy ***/
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][5]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][5]==1){
				tmpMatrix[rowCnt][0] = sma.employmentStabilityScoreNation[i];
				tmpMatrix[rowCnt][1] = iec.entropyNation[i];
				tmpMatrix[rowCnt][2] = iec.entropyNationBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyNationByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyNationByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.stabilityRnation = pc.getCorrelationMatrix();
			this.stabilityPnation = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.employmentStabilityScoreRural[i];
				tmpMatrix[rowCnt][1] = iec.entropyRural[i];
				tmpMatrix[rowCnt][2] = iec.entropyRuralBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyRuralByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyRuralByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.stabilityRrural = pc.getCorrelationMatrix();
			this.stabilityPrural = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.employmentStabilityScoreUrban[i];
				tmpMatrix[rowCnt][1] = iec.entropyUrban[i];
				tmpMatrix[rowCnt][2] = iec.entropyUrbanBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyUrbanByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyUrbanByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc =  new PearsonsCorrelation(tmpMatrix);
			this.stabilityRurban = pc.getCorrelationMatrix();
			this.stabilityPurban = pc.getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
	}
	
	public void correlationPearsonsRegion(SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i, j, k;
		int row;
		int column = 9;
		int tmpRow;
		boolean nanChecker;			//true: it's o.k,  false: has NaN
		boolean[] checker;				//true: it's o.k,  false: has NaN
		double[][] tmpMatrix, tmpStorage;
		PearsonsCorrelation pc;
		
		this.regionalRcorrelationNation	= new ArrayList<RealMatrix>();
		this.regionalRcorrelationRural	= new ArrayList<RealMatrix>();
		this.regionalRcorrelationUrban	= new ArrayList<RealMatrix>();
		this.regionalPvalueNation			= new ArrayList<RealMatrix>();
		this.regionalPvalueRural				= new ArrayList<RealMatrix>();
		this.regionalPvalueUrban			= new ArrayList<RealMatrix>();
		
		for(i=0 ; i<sma.duration ; i++){
			row = sma.n_region[i];
			checker = new boolean[row];
			
			/*** in national areas ***/
			tmpMatrix = new double[row][column];	
			for(j=0 ; j<row ; j++){
				tmpMatrix[j][0] = sma.lifeSatisfactionScore[i][j];
				tmpMatrix[j][1] = sma.livingConditionScore[i][j];
				tmpMatrix[j][2] = sma.incomeSatisfactionScore[i][j];
				tmpMatrix[j][3] = sma.consumptionSatisfactionScore[i][j];
				tmpMatrix[j][4] = sma.employmentStabilityScore[i][j];
				tmpMatrix[j][5] = iec.entropy.get(i)[j];
				tmpMatrix[j][6] = iec.entropyBySize.get(i)[j];
				tmpMatrix[j][7] = iec.entropyByLevel.get(i)[j];
				tmpMatrix[j][8] = iec.entropyByAge.get(i)[j];			
			}
			//check NaN-data rows
			tmpRow = row;
			for(j=0 ; j<row ; j++){
				nanChecker = true;
				for(k=0 ; k<column ; k++) if(Double.isNaN(tmpMatrix[j][k])) nanChecker = false;
				if(nanChecker) checker[j] = true;
				else{
					tmpRow--;
					checker[j] = false;
				}
			}
			//eliminate NaN-data rows
			if(tmpRow < row && tmpRow > 2){
				tmpStorage = new double[tmpRow][column];
				tmpRow = 0;
				for(j=0 ; j<row ; j++){
					if(checker[j]){
						for(k=0 ; k<column ; k++) tmpStorage[tmpRow][k] = tmpMatrix[j][k];
						tmpRow++;
					}
				}
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpStorage);
				this.regionalRcorrelationNation.add(pc.getCorrelationMatrix());
				this.regionalPvalueNation.add(pc.getCorrelationPValues());
			}else if(tmpRow == row && row>2){		
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpMatrix);
				this.regionalRcorrelationNation.add(pc.getCorrelationMatrix());
				this.regionalPvalueNation.add(pc.getCorrelationPValues());
			}else{
				this.regionalRcorrelationNation.add(null);
				this.regionalPvalueNation.add(null);
				System.err.println((this.startYear+i)+" year nation correlation data lack");
			}
			tmpMatrix = null;
			pc = null;

			/*** in rural areas ***/
			tmpMatrix = new double[row][column];	
			for(j=0 ; j<row ; j++){
				tmpMatrix[j][0] = sma.ruralLifeSatisfactionScore[i][j];
				tmpMatrix[j][1] = sma.ruralLivingConditionScore[i][j];
				tmpMatrix[j][2] = sma.ruralIncomeSatisfactionScore[i][j];
				tmpMatrix[j][3] = sma.ruralConsumptionSatisfactionScore[i][j];
				tmpMatrix[j][4] = sma.ruralEmploymentStabilityScore[i][j];
				tmpMatrix[j][5] = iec.ruralEntropy.get(i)[j];
				tmpMatrix[j][6] = iec.ruralEntropyBySize.get(i)[j];
				tmpMatrix[j][7] = iec.ruralEntropyByLevel.get(i)[j];
				tmpMatrix[j][8] = iec.ruralEntropyByAge.get(i)[j];			
			}
			//check NaN-data rows
			tmpRow = row;
			for(j=0 ; j<row ; j++){
				nanChecker = true;
				for(k=0 ; k<column ; k++) if(Double.isNaN(tmpMatrix[j][k])) nanChecker = false;
				if(nanChecker) checker[j] = true;
				else{
					tmpRow--;
					checker[j] = false;
				}
			}
			//eliminate NaN-data rows
			if(tmpRow < row && tmpRow > 2){
				tmpStorage = new double[tmpRow][column];
				tmpRow = 0;
				for(j=0 ; j<row ; j++){
					if(checker[j]){
						for(k=0 ; k<column ; k++) tmpStorage[tmpRow][k] = tmpMatrix[j][k];
						tmpRow++;
					}
				}
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpStorage);
				this.regionalRcorrelationRural.add(pc.getCorrelationMatrix());
				this.regionalPvalueRural.add(pc.getCorrelationPValues());
			}else if(tmpRow == row && row>2){		
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpMatrix);
				this.regionalRcorrelationRural.add(pc.getCorrelationMatrix());
				this.regionalPvalueRural.add(pc.getCorrelationPValues());
			}else{
				this.regionalRcorrelationRural.add(null);
				this.regionalPvalueRural.add(null);
				System.err.println((this.startYear+i)+" year rural correlation data lack");
			}
			tmpMatrix = null;
			pc = null;
			
			/*** in urban areas ***/
			tmpMatrix = new double[row][column];	
			for(j=0 ; j<row ; j++){
				tmpMatrix[j][0] = sma.urbanLifeSatisfactionScore[i][j];
				tmpMatrix[j][1] = sma.urbanLivingConditionScore[i][j];
				tmpMatrix[j][2] = sma.urbanIncomeSatisfactionScore[i][j];
				tmpMatrix[j][3] = sma.urbanConsumptionSatisfactionScore[i][j];
				tmpMatrix[j][4] = sma.urbanEmploymentStabilityScore[i][j];
				tmpMatrix[j][5] = iec.urbanEntropy.get(i)[j];
				tmpMatrix[j][6] = iec.urbanEntropyBySize.get(i)[j];
				tmpMatrix[j][7] = iec.urbanEntropyByLevel.get(i)[j];
				tmpMatrix[j][8] = iec.urbanEntropyByAge.get(i)[j];			
			}
			//check NaN-data rows
			tmpRow = row;
			for(j=0 ; j<row ; j++){
				nanChecker = true;
				for(k=0 ; k<column ; k++) if(Double.isNaN(tmpMatrix[j][k])) nanChecker = false;
				if(nanChecker) checker[j] = true;
				else{
					tmpRow--;
					checker[j] = false;
				}
			}
			//eliminate NaN-data rows
			if(tmpRow < row && tmpRow > 2){
				tmpStorage = new double[tmpRow][column];
				tmpRow = 0;
				for(j=0 ; j<row ; j++){
					if(checker[j]){
						for(k=0 ; k<column ; k++) tmpStorage[tmpRow][k] = tmpMatrix[j][k];
						tmpRow++;
					}
				}
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpStorage);
				this.regionalRcorrelationUrban.add(pc.getCorrelationMatrix());
				this.regionalPvalueUrban.add(pc.getCorrelationPValues());
			}else if(tmpRow == row && row>2){		
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpMatrix);
				this.regionalRcorrelationUrban.add(pc.getCorrelationMatrix());
				this.regionalPvalueUrban.add(pc.getCorrelationPValues());
			}else{
				this.regionalRcorrelationUrban.add(null);
				this.regionalPvalueUrban.add(null);
				System.err.println((this.startYear+i)+" year urban correlation data lack");
			}
			tmpMatrix = null;
			pc = null;
		}
	}
	
	public void correlationPearsonsRegionTimeSeries(SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i, j;
		int n_years;
		int rowCnt;
		int column = 5;
		double[][] tmpMatrix;
		ArrayList<RealMatrix> tmpRmatrix, tmpPmatrix;
		
		PearsonsCorrelation pc;
		
		this.regionalRcorrelationTimeSeriesNation	= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalRcorrelationTimeSeriesRural		= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalRcorrelationTimeSeriesUrban		= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalPvalueTimeSeriesNation				= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalPvalueTimeSeriesRural				= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalPvalueTimeSeriesUrban				= new ArrayList<ArrayList<RealMatrix>>();
		
		/*** compare life satisfaction v.s. entropy ***/
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][1]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1){
					tmpMatrix[rowCnt][0] = sma.lifeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.entropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.entropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.entropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.entropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesNation.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesNation.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.ruralLifeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.ruralEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.ruralEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.ruralEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.ruralEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesRural.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesRural.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.urbanLifeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.urbanEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.urbanEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.urbanEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.urbanEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesUrban.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesUrban.add(tmpPmatrix);

		
		/*** compare living condition v.s. entropy ***/
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][2]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1){
					tmpMatrix[rowCnt][0] = sma.livingConditionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.entropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.entropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.entropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.entropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesNation.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesNation.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.ruralLivingConditionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.ruralEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.ruralEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.ruralEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.ruralEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesRural.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesRural.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.urbanLivingConditionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.urbanEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.urbanEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.urbanEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.urbanEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesUrban.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesUrban.add(tmpPmatrix);
		
		
		/*** compare income satisfaction v.s. entropy ***/
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][3]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1){
					tmpMatrix[rowCnt][0] = sma.incomeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.entropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.entropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.entropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.entropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesNation.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesNation.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.ruralIncomeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.ruralEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.ruralEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.ruralEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.ruralEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesRural.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesRural.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.urbanIncomeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.urbanEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.urbanEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.urbanEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.urbanEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesUrban.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesUrban.add(tmpPmatrix);
		
		
		/*** compare consumption satisfaction v.s. entropy ***/
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][4]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1){
					tmpMatrix[rowCnt][0] = sma.consumptionSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.entropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.entropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.entropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.entropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesNation.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesNation.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.ruralConsumptionSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.ruralEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.ruralEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.ruralEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.ruralEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesRural.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesRural.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.urbanConsumptionSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.urbanEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.urbanEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.urbanEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.urbanEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesUrban.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesUrban.add(tmpPmatrix);
		
		
		/*** compare job stability v.s. entropy ***/
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][5]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1){
					tmpMatrix[rowCnt][0] = sma.employmentStabilityScore[i][j];
					tmpMatrix[rowCnt][1] = iec.entropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.entropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.entropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.entropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesNation.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesNation.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.ruralEmploymentStabilityScore[i][j];
					tmpMatrix[rowCnt][1] = iec.ruralEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.ruralEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.ruralEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.ruralEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesRural.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesRural.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.urbanEmploymentStabilityScore[i][j];
					tmpMatrix[rowCnt][1] = iec.urbanEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.urbanEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.urbanEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.urbanEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesUrban.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesUrban.add(tmpPmatrix);
	}
	
	public void correlationPearsonsRegionIndustry(SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i, j, k, l;
		int row;
		int column = 9;
		int tmpRow;
		boolean nanChecker;			//true: it's o.k,  false: has NaN
		boolean[] checker;				//true: it's o.k,  false: has NaN
		double[][] tmpMatrix, tmpStorage;
		PearsonsCorrelation pc;
		
		ArrayList<RealMatrix> tmpRnation;
		ArrayList<RealMatrix> tmpRrural;
		ArrayList<RealMatrix> tmpRurban;
		ArrayList<RealMatrix> tmpPnation;
		ArrayList<RealMatrix> tmpPrural;
		ArrayList<RealMatrix> tmpPurban;
		
		this.regionalCategorialRcorrelationNation= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalCategorialRcorrelationRural = new ArrayList<ArrayList<RealMatrix>>();
		this.regionalCategorialRcorrelationUrban = new ArrayList<ArrayList<RealMatrix>>();
		this.regionalCategorialPvalueNation = new ArrayList<ArrayList<RealMatrix>>();
		this.regionalCategorialPvalueRural = new ArrayList<ArrayList<RealMatrix>>();
		this.regionalCategorialPvalueUrban = new ArrayList<ArrayList<RealMatrix>>();
		
		for(i=0 ; i<sma.duration ; i++){
			row = sma.n_region[i];
			checker = new boolean[row];
			tmpRnation	= new ArrayList<RealMatrix> ();
			tmpRrural		= new ArrayList<RealMatrix> ();
			tmpRurban	= new ArrayList<RealMatrix> ();
			tmpPnation	= new ArrayList<RealMatrix> ();
			tmpPrural		= new ArrayList<RealMatrix> ();
			tmpPurban	= new ArrayList<RealMatrix> ();
			
			for(k=0 ; k<iec.n_category[i] ; k++){
				/*** in national areas ***/
				tmpMatrix = new double[row][column];	
				for(j=0 ; j<row ; j++){
					tmpMatrix[j][0] = sma.lifeSatisfactionScore[i][j];
					tmpMatrix[j][1] = sma.livingConditionScore[i][j];
					tmpMatrix[j][2] = sma.incomeSatisfactionScore[i][j];
					tmpMatrix[j][3] = sma.consumptionSatisfactionScore[i][j];
					tmpMatrix[j][4] = sma.employmentStabilityScore[i][j];
					tmpMatrix[j][5] = iec.entropyByCategory.get(i)[j][k];
					tmpMatrix[j][6] = iec.entropyBySizeByCategory.get(i)[j][k];
					tmpMatrix[j][7] = iec.entropyByLevelByCategory.get(i)[j][k];
					tmpMatrix[j][8] = iec.entropyByAgeByCategory.get(i)[j][k];		
				}				
				//check NaN-data rows
				tmpRow = row;
				for(j=0 ; j<row ; j++){
					nanChecker = true;
					for(l=0 ; l<column ; l++) if(Double.isNaN(tmpMatrix[j][l])) nanChecker = false;
					if(nanChecker) checker[j] = true;
					else{
						tmpRow--;
						checker[j] = false;
					}
				}
				//eliminate NaN-data rows
				if(tmpRow < row && tmpRow > 0){
					tmpStorage = new double[tmpRow][column];
					tmpRow = 0;
					for(j=0 ; j<row ; j++){
						if(checker[j]){
							for(l=0 ; l<column ; l++) tmpStorage[tmpRow][l] = tmpMatrix[j][l];
							tmpRow++;
						}
					}
					if(tmpRow>3){		//analyze correlation
						pc =  new PearsonsCorrelation(tmpStorage);
						tmpRnation.add(pc.getCorrelationMatrix());
						tmpPnation.add(pc.getCorrelationPValues());
					}else{
						tmpRnation.add(null);
						tmpPnation.add(null);
						System.err.println((this.startYear+i)+" year nation regional categorial correlation data lack");
					}
				}else if(row>2){		//analyze correlation
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpRnation.add(pc.getCorrelationMatrix());
					tmpPnation.add(pc.getCorrelationPValues());
				}else{
					tmpRnation.add(null);
					tmpPnation.add(null);
					System.err.println((this.startYear+i)+" year nation regional categorial correlation data lack");
				}
				tmpMatrix = null;
				pc = null;
				
				/*** in rural areas ***/
				tmpMatrix = new double[row][column];	
				for(j=0 ; j<row ; j++){
					tmpMatrix[j][0] = sma.ruralLifeSatisfactionScore[i][j];
					tmpMatrix[j][1] = sma.ruralLivingConditionScore[i][j];
					tmpMatrix[j][2] = sma.ruralIncomeSatisfactionScore[i][j];
					tmpMatrix[j][3] = sma.ruralConsumptionSatisfactionScore[i][j];
					tmpMatrix[j][4] = sma.ruralEmploymentStabilityScore[i][j];
					tmpMatrix[j][5] = iec.ruralEntropyByCategory.get(i)[j][k];
					tmpMatrix[j][6] = iec.ruralEntropyBySizeByCategory.get(i)[j][k];
					tmpMatrix[j][7] = iec.ruralEntropyByLevelByCategory.get(i)[j][k];
					tmpMatrix[j][8] = iec.ruralEntropyByAgeByCategory.get(i)[j][k];		
				}
				//check NaN-data rows
				tmpRow = row;
				for(j=0 ; j<row ; j++){
					nanChecker = true;
					for(l=0 ; l<column ; l++) if(Double.isNaN(tmpMatrix[j][l])) nanChecker = false;
					if(nanChecker) checker[j] = true;
					else{
						tmpRow--;
						checker[j] = false;
					}
				}
				//eliminate NaN-data rows
				if(tmpRow < row && tmpRow > 0){
					tmpStorage = new double[tmpRow][column];
					tmpRow = 0;
					for(j=0 ; j<row ; j++){
						if(checker[j]){
							for(l=0 ; l<column ; l++) tmpStorage[tmpRow][l] = tmpMatrix[j][l];
							tmpRow++;
						}
					}
					if(tmpRow>3){		//analyze correlation
						pc =  new PearsonsCorrelation(tmpStorage);
						tmpRrural.add(pc.getCorrelationMatrix());
						tmpPrural.add(pc.getCorrelationPValues());
					}else{
						tmpRrural.add(null);
						tmpPrural.add(null);
						System.err.println((this.startYear+i)+" year rural regional categorial correlation data lack");
					}
				}else if(row>2){		//analyze correlation
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpRrural.add(pc.getCorrelationMatrix());
					tmpPrural.add(pc.getCorrelationPValues());
				}else{
					tmpRrural.add(null);
					tmpPrural.add(null);
					System.err.println((this.startYear+i)+" year rural regional categorial correlation data lack");
				}
				tmpMatrix = null;
				pc = null;
				
				/*** in urban areas ***/
				tmpMatrix = new double[row][column];	
				for(j=0 ; j<row ; j++){
					tmpMatrix[j][0] = sma.urbanLifeSatisfactionScore[i][j];
					tmpMatrix[j][1] = sma.urbanLivingConditionScore[i][j];
					tmpMatrix[j][2] = sma.urbanIncomeSatisfactionScore[i][j];
					tmpMatrix[j][3] = sma.urbanConsumptionSatisfactionScore[i][j];
					tmpMatrix[j][4] = sma.urbanEmploymentStabilityScore[i][j];
					tmpMatrix[j][5] = iec.urbanEntropyByCategory.get(i)[j][k];
					tmpMatrix[j][6] = iec.urbanEntropyBySizeByCategory.get(i)[j][k];
					tmpMatrix[j][7] = iec.urbanEntropyByLevelByCategory.get(i)[j][k];
					tmpMatrix[j][8] = iec.urbanEntropyByAgeByCategory.get(i)[j][k];			
				}
				//check NaN-data rows
				tmpRow = row;
				for(j=0 ; j<row ; j++){
					nanChecker = true;
					for(l=0 ; l<column ; l++) if(Double.isNaN(tmpMatrix[j][l])) nanChecker = false;
					if(nanChecker) checker[j] = true;
					else{
						tmpRow--;
						checker[j] = false;
					}
				}
				//eliminate NaN-data rows
				if(tmpRow < row && tmpRow > 0){
					tmpStorage = new double[tmpRow][column];
					tmpRow = 0;
					for(j=0 ; j<row ; j++){
						if(checker[j]){
							for(l=0 ; l<column ; l++) tmpStorage[tmpRow][l] = tmpMatrix[j][l];
							tmpRow++;
						}
					}
					if(tmpRow>3){		//analyze correlation
						pc =  new PearsonsCorrelation(tmpStorage);
						tmpRurban.add(pc.getCorrelationMatrix());
						tmpPurban.add(pc.getCorrelationPValues());
					}else{
						tmpRurban.add(null);
						tmpPurban.add(null);
						System.err.println((this.startYear+i)+" year urban regional categorial correlation data lack");
					}
				}else if(row>2){		//analyze correlation
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpRurban.add(pc.getCorrelationMatrix());
					tmpPurban.add(pc.getCorrelationPValues());
				}else{
					tmpRurban.add(null);
					tmpPurban.add(null);
					System.err.println((this.startYear+i)+" year urban regional categorial correlation data lack");
				}
				tmpMatrix = null;
				pc = null;
			}
			this.regionalCategorialRcorrelationNation.add(tmpRnation);
			this.regionalCategorialRcorrelationRural.add(tmpRrural);
			this.regionalCategorialRcorrelationUrban.add(tmpRurban);
			this.regionalCategorialPvalueNation.add(tmpPnation);
			this.regionalCategorialPvalueRural.add(tmpPrural);
			this.regionalCategorialPvalueUrban.add(tmpPurban);
		}
	}
	
	public void correlationPearsonsCategory(SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i, j, k;
		int row;
		int column = 9;
		int tmpRow;
		boolean nanChecker;			//true: it's o.k,  false: has NaN
		boolean[] checker;				//true: it's o.k,  false: has NaN
		double[][] tmpMatrix, tmpStorage;
		PearsonsCorrelation pc;
		
		this.categorialRcorrelationNation	= new ArrayList<RealMatrix>();
		this.categorialRcorrelationRural		= new ArrayList<RealMatrix>();
		this.categorialRcorrelationUrban	= new ArrayList<RealMatrix>();
		this.categorialPvalueNation			= new ArrayList<RealMatrix>();
		this.categorialPvalueRural				= new ArrayList<RealMatrix>();
		this.categorialPvalueUrban			= new ArrayList<RealMatrix>();
		
		for(i=0 ; i<sma.duration ; i++){
			row = sma.n_industry[i];
			checker = new boolean[row];
			
			/*** in national areas ***/
			tmpMatrix = new double[row][column];	
			for(j=0 ; j<row ; j++){
				tmpMatrix[j][0] = sma.lifeSatisfactionScoreNationCategorized[i][j];
				tmpMatrix[j][1] = sma.livingConditionScoreNationCategorized[i][j];
				tmpMatrix[j][2] = sma.incomeSatisfactionScoreNationCategorized[i][j];
				tmpMatrix[j][3] = sma.consumptionSatisfactionScoreNationCategorized[i][j];
				tmpMatrix[j][4] = sma.employmentStabilityScoreNationCategorized[i][j];
				tmpMatrix[j][5] = iec.entropyByIndustry.get(i)[j];
				tmpMatrix[j][6] = iec.entropyBySizeByIndustry.get(i)[j];
				tmpMatrix[j][7] = iec.entropyByLevelByIndustry.get(i)[j];
				tmpMatrix[j][8] = iec.entropyByAgeByIndustry.get(i)[j];			
			}
			//check NaN-data rows
			tmpRow = row;
			for(j=0 ; j<row ; j++){
				nanChecker = true;
				for(k=0 ; k<column ; k++) if(Double.isNaN(tmpMatrix[j][k])) nanChecker = false;
				if(nanChecker) checker[j] = true;
				else{
					tmpRow--;
					checker[j] = false;
				}
			}
			//eliminate NaN-data rows
			if(tmpRow < row && tmpRow > 2){
				tmpStorage = new double[tmpRow][column];
				tmpRow = 0;
				for(j=0 ; j<row ; j++){
					if(checker[j]){
						for(k=0 ; k<column ; k++) tmpStorage[tmpRow][k] = tmpMatrix[j][k];
						tmpRow++;
					}
				}
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpStorage);
				this.categorialRcorrelationNation.add(pc.getCorrelationMatrix());
				this.categorialPvalueNation.add(pc.getCorrelationPValues());
			}else if(tmpRow == row && row>2){		
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpMatrix);
				this.categorialRcorrelationNation.add(pc.getCorrelationMatrix());
				this.categorialPvalueNation.add(pc.getCorrelationPValues());
			}else{
				this.categorialRcorrelationNation.add(null);
				this.categorialPvalueNation.add(null);
				System.err.println((this.startYear+i)+" year categorial nation correlation data lack");
			}
			tmpMatrix = null;
			pc = null;

			/*** in rural areas ***/
			tmpMatrix = new double[row][column];	
			for(j=0 ; j<row ; j++){
				tmpMatrix[j][0] = sma.lifeSatisfactionScoreRuralCategorized[i][j];
				tmpMatrix[j][1] = sma.livingConditionScoreRuralCategorized[i][j];
				tmpMatrix[j][2] = sma.incomeSatisfactionScoreRuralCategorized[i][j];
				tmpMatrix[j][3] = sma.consumptionSatisfactionScoreRuralCategorized[i][j];
				tmpMatrix[j][4] = sma.employmentStabilityScoreRuralCategorized[i][j];
				tmpMatrix[j][5] = iec.ruralEntropyByIndustry.get(i)[j];
				tmpMatrix[j][6] = iec.ruralEntropyBySizeByIndustry.get(i)[j];
				tmpMatrix[j][7] = iec.ruralEntropyByLevelByIndustry.get(i)[j];
				tmpMatrix[j][8] = iec.ruralEntropyByAgeByIndustry.get(i)[j];		
			}
			//check NaN-data rows
			tmpRow = row;
			for(j=0 ; j<row ; j++){
				nanChecker = true;
				for(k=0 ; k<column ; k++) if(Double.isNaN(tmpMatrix[j][k])) nanChecker = false;
				if(nanChecker) checker[j] = true;
				else{
					tmpRow--;
					checker[j] = false;
				}
			}
			//eliminate NaN-data rows
			if(tmpRow < row && tmpRow > 2){
				tmpStorage = new double[tmpRow][column];
				tmpRow = 0;
				for(j=0 ; j<row ; j++){
					if(checker[j]){
						for(k=0 ; k<column ; k++) tmpStorage[tmpRow][k] = tmpMatrix[j][k];
						tmpRow++;
					}
				}
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpStorage);
				this.categorialRcorrelationRural.add(pc.getCorrelationMatrix());
				this.categorialPvalueRural.add(pc.getCorrelationPValues());
			}else if(tmpRow == row && row>2){		
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpMatrix);
				this.categorialRcorrelationRural.add(pc.getCorrelationMatrix());
				this.categorialPvalueRural.add(pc.getCorrelationPValues());
			}else{
				this.categorialRcorrelationRural.add(null);
				this.categorialPvalueRural.add(null);
				System.err.println((this.startYear+i)+" year categorial rural correlation data lack");
			}
			tmpMatrix = null;
			pc = null;
			
			/*** in urban areas ***/
			tmpMatrix = new double[row][column];	
			for(j=0 ; j<row ; j++){
				tmpMatrix[j][0] = sma.lifeSatisfactionScoreUrbanCategorized[i][j];
				tmpMatrix[j][1] = sma.livingConditionScoreUrbanCategorized[i][j];
				tmpMatrix[j][2] = sma.incomeSatisfactionScoreUrbanCategorized[i][j];
				tmpMatrix[j][3] = sma.consumptionSatisfactionScoreUrbanCategorized[i][j];
				tmpMatrix[j][4] = sma.employmentStabilityScoreUrbanCategorized[i][j];
				tmpMatrix[j][5] = iec.urbanEntropyByIndustry.get(i)[j];
				tmpMatrix[j][6] = iec.urbanEntropyBySizeByIndustry.get(i)[j];
				tmpMatrix[j][7] = iec.urbanEntropyByLevelByIndustry.get(i)[j];
				tmpMatrix[j][8] = iec.urbanEntropyByAgeByIndustry.get(i)[j];				
			}
			//check NaN-data rows
			tmpRow = row;
			for(j=0 ; j<row ; j++){
				nanChecker = true;
				for(k=0 ; k<column ; k++) if(Double.isNaN(tmpMatrix[j][k])) nanChecker = false;
				if(nanChecker) checker[j] = true;
				else{
					tmpRow--;
					checker[j] = false;
				}
			}
			//eliminate NaN-data rows
			if(tmpRow < row && tmpRow > 2){
				tmpStorage = new double[tmpRow][column];
				tmpRow = 0;
				for(j=0 ; j<row ; j++){
					if(checker[j]){
						for(k=0 ; k<column ; k++) tmpStorage[tmpRow][k] = tmpMatrix[j][k];
						tmpRow++;
					}
				}
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpStorage);
				this.categorialRcorrelationUrban.add(pc.getCorrelationMatrix());
				this.categorialPvalueUrban.add(pc.getCorrelationPValues());
			}else if(tmpRow == row && row>2){		
				//analyze correlation
				pc =  new PearsonsCorrelation(tmpMatrix);
				this.categorialRcorrelationUrban.add(pc.getCorrelationMatrix());
				this.categorialPvalueUrban.add(pc.getCorrelationPValues());
			}else{
				this.categorialRcorrelationUrban.add(null);
				this.categorialPvalueUrban.add(null);
				System.err.println((this.startYear+i)+" year categorial urban correlation data lack");
			}
			tmpMatrix = null;
			pc = null;
		}
	}
	
	public void correlationPearsonsRegionCategory(SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i, j, k, l;
		int row;
		int column = 9;
		int tmpRow;
		boolean nanChecker;			//true: it's o.k,  false: has NaN
		boolean[] checker;				//true: it's o.k,  false: has NaN
		double[][] tmpMatrix, tmpStorage;
		PearsonsCorrelation pc;
		
		ArrayList<RealMatrix> tmpRnation;
		ArrayList<RealMatrix> tmpRrural;
		ArrayList<RealMatrix> tmpRurban;
		ArrayList<RealMatrix> tmpPnation;
		ArrayList<RealMatrix> tmpPrural;
		ArrayList<RealMatrix> tmpPurban;
		
		this.regionalCategorialRcorrelationNation= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalCategorialRcorrelationRural = new ArrayList<ArrayList<RealMatrix>>();
		this.regionalCategorialRcorrelationUrban = new ArrayList<ArrayList<RealMatrix>>();
		this.regionalCategorialPvalueNation = new ArrayList<ArrayList<RealMatrix>>();
		this.regionalCategorialPvalueRural = new ArrayList<ArrayList<RealMatrix>>();
		this.regionalCategorialPvalueUrban = new ArrayList<ArrayList<RealMatrix>>();
		
		for(i=0 ; i<sma.duration ; i++){
			row = sma.n_region[i];
			checker = new boolean[row];
			tmpRnation	= new ArrayList<RealMatrix> ();
			tmpRrural		= new ArrayList<RealMatrix> ();
			tmpRurban	= new ArrayList<RealMatrix> ();
			tmpPnation	= new ArrayList<RealMatrix> ();
			tmpPrural		= new ArrayList<RealMatrix> ();
			tmpPurban	= new ArrayList<RealMatrix> ();
			
			for(k=0 ; k<iec.n_category[i] ; k++){
				/*** in national areas ***/
				tmpMatrix = new double[row][column];	
				for(j=0 ; j<row ; j++){
					tmpMatrix[j][0] = sma.lifeSatisfactionScoreCategorized[i][j][k];
					tmpMatrix[j][1] = sma.livingConditionScoreCategorized[i][j][k];
					tmpMatrix[j][2] = sma.incomeSatisfactionScoreCategorized[i][j][k];
					tmpMatrix[j][3] = sma.consumptionSatisfactionScoreCategorized[i][j][k];
					tmpMatrix[j][4] = sma.employmentStabilityScoreCategorized[i][j][k];
					tmpMatrix[j][5] = iec.entropyByCategory.get(i)[j][k];
					tmpMatrix[j][6] = iec.entropyBySizeByCategory.get(i)[j][k];
					tmpMatrix[j][7] = iec.entropyByLevelByCategory.get(i)[j][k];
					tmpMatrix[j][8] = iec.entropyByAgeByCategory.get(i)[j][k];		
				}				
				//check NaN-data rows
				tmpRow = row;
				for(j=0 ; j<row ; j++){
					nanChecker = true;
					for(l=0 ; l<column ; l++) if(Double.isNaN(tmpMatrix[j][l])) nanChecker = false;
					if(nanChecker) checker[j] = true;
					else{
						tmpRow--;
						checker[j] = false;
					}
				}
				//eliminate NaN-data rows
				if(tmpRow < row && tmpRow > 0){
					tmpStorage = new double[tmpRow][column];
					tmpRow = 0;
					for(j=0 ; j<row ; j++){
						if(checker[j]){
							for(l=0 ; l<column ; l++) tmpStorage[tmpRow][l] = tmpMatrix[j][l];
							tmpRow++;
						}
					}
					if(tmpRow>3){		//analyze correlation
						pc =  new PearsonsCorrelation(tmpStorage);
						tmpRnation.add(pc.getCorrelationMatrix());
						tmpPnation.add(pc.getCorrelationPValues());
					}else{
						tmpRnation.add(null);
						tmpPnation.add(null);
						System.err.println((this.startYear+i)+" year nation regional categorial correlation data lack");
					}
				}else if(row>2){		//analyze correlation
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpRnation.add(pc.getCorrelationMatrix());
					tmpPnation.add(pc.getCorrelationPValues());
				}else{
					tmpRnation.add(null);
					tmpPnation.add(null);
					System.err.println((this.startYear+i)+" year nation regional categorial correlation data lack");
				}
				tmpMatrix = null;
				pc = null;
				
				/*** in rural areas ***/
				tmpMatrix = new double[row][column];	
				for(j=0 ; j<row ; j++){
					tmpMatrix[j][0] = sma.ruralLifeSatisfactionScoreCategorized[i][j][k];
					tmpMatrix[j][1] = sma.ruralLivingConditionScoreCategorized[i][j][k];
					tmpMatrix[j][2] = sma.ruralIncomeSatisfactionScoreCategorized[i][j][k];
					tmpMatrix[j][3] = sma.ruralConsumptionSatisfactionScoreCategorized[i][j][k];
					tmpMatrix[j][4] = sma.ruralEmploymentStabilityScoreCategorized[i][j][k];
					tmpMatrix[j][5] = iec.ruralEntropyByCategory.get(i)[j][k];
					tmpMatrix[j][6] = iec.ruralEntropyBySizeByCategory.get(i)[j][k];
					tmpMatrix[j][7] = iec.ruralEntropyByLevelByCategory.get(i)[j][k];
					tmpMatrix[j][8] = iec.ruralEntropyByAgeByCategory.get(i)[j][k];		
				}
				//check NaN-data rows
				tmpRow = row;
				for(j=0 ; j<row ; j++){
					nanChecker = true;
					for(l=0 ; l<column ; l++) if(Double.isNaN(tmpMatrix[j][l])) nanChecker = false;
					if(nanChecker) checker[j] = true;
					else{
						tmpRow--;
						checker[j] = false;
					}
				}
				//eliminate NaN-data rows
				if(tmpRow < row && tmpRow > 0){
					tmpStorage = new double[tmpRow][column];
					tmpRow = 0;
					for(j=0 ; j<row ; j++){
						if(checker[j]){
							for(l=0 ; l<column ; l++) tmpStorage[tmpRow][l] = tmpMatrix[j][l];
							tmpRow++;
						}
					}
					if(tmpRow>3){		//analyze correlation
						pc =  new PearsonsCorrelation(tmpStorage);
						tmpRrural.add(pc.getCorrelationMatrix());
						tmpPrural.add(pc.getCorrelationPValues());
					}else{
						tmpRrural.add(null);
						tmpPrural.add(null);
						System.err.println((this.startYear+i)+" year rural regional categorial correlation data lack");
					}
				}else if(row>2){		//analyze correlation
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpRrural.add(pc.getCorrelationMatrix());
					tmpPrural.add(pc.getCorrelationPValues());
				}else{
					tmpRrural.add(null);
					tmpPrural.add(null);
					System.err.println((this.startYear+i)+" year rural regional categorial correlation data lack");
				}
				tmpMatrix = null;
				pc = null;
				
				/*** in urban areas ***/
				tmpMatrix = new double[row][column];	
				for(j=0 ; j<row ; j++){
					tmpMatrix[j][0] = sma.urbanLifeSatisfactionScoreCategorized[i][j][k];
					tmpMatrix[j][1] = sma.urbanLivingConditionScoreCategorized[i][j][k];
					tmpMatrix[j][2] = sma.urbanIncomeSatisfactionScoreCategorized[i][j][k];
					tmpMatrix[j][3] = sma.urbanConsumptionSatisfactionScoreCategorized[i][j][k];
					tmpMatrix[j][4] = sma.urbanEmploymentStabilityScoreCategorized[i][j][k];
					tmpMatrix[j][5] = iec.urbanEntropyByCategory.get(i)[j][k];
					tmpMatrix[j][6] = iec.urbanEntropyBySizeByCategory.get(i)[j][k];
					tmpMatrix[j][7] = iec.urbanEntropyByLevelByCategory.get(i)[j][k];
					tmpMatrix[j][8] = iec.urbanEntropyByAgeByCategory.get(i)[j][k];			
				}
				//check NaN-data rows
				tmpRow = row;
				for(j=0 ; j<row ; j++){
					nanChecker = true;
					for(l=0 ; l<column ; l++) if(Double.isNaN(tmpMatrix[j][l])) nanChecker = false;
					if(nanChecker) checker[j] = true;
					else{
						tmpRow--;
						checker[j] = false;
					}
				}
				//eliminate NaN-data rows
				if(tmpRow < row && tmpRow > 0){
					tmpStorage = new double[tmpRow][column];
					tmpRow = 0;
					for(j=0 ; j<row ; j++){
						if(checker[j]){
							for(l=0 ; l<column ; l++) tmpStorage[tmpRow][l] = tmpMatrix[j][l];
							tmpRow++;
						}
					}
					if(tmpRow>3){		//analyze correlation
						pc =  new PearsonsCorrelation(tmpStorage);
						tmpRurban.add(pc.getCorrelationMatrix());
						tmpPurban.add(pc.getCorrelationPValues());
					}else{
						tmpRurban.add(null);
						tmpPurban.add(null);
						System.err.println((this.startYear+i)+" year urban regional categorial correlation data lack");
					}
				}else if(row>2){		//analyze correlation
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpRurban.add(pc.getCorrelationMatrix());
					tmpPurban.add(pc.getCorrelationPValues());
				}else{
					tmpRurban.add(null);
					tmpPurban.add(null);
					System.err.println((this.startYear+i)+" year urban regional categorial correlation data lack");
				}
				tmpMatrix = null;
				pc = null;
			}
			this.regionalCategorialRcorrelationNation.add(tmpRnation);
			this.regionalCategorialRcorrelationRural.add(tmpRrural);
			this.regionalCategorialRcorrelationUrban.add(tmpRurban);
			this.regionalCategorialPvalueNation.add(tmpPnation);
			this.regionalCategorialPvalueRural.add(tmpPrural);
			this.regionalCategorialPvalueUrban.add(tmpPurban);
		}
	}
	
	public void correlationPearsonsCategoryRegion(SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i, j, k, l;
		int row;
		int column = 9;
		int tmpRow;
		boolean nanChecker;			//true: it's o.k,  false: has NaN
		boolean[] checker;				//true: it's o.k,  false: has NaN
		double[][] tmpMatrix, tmpStorage;
		PearsonsCorrelation pc;
		
		ArrayList<RealMatrix> tmpRnation;
		ArrayList<RealMatrix> tmpRrural;
		ArrayList<RealMatrix> tmpRurban;
		ArrayList<RealMatrix> tmpPnation;
		ArrayList<RealMatrix> tmpPrural;
		ArrayList<RealMatrix> tmpPurban;
		
		this.categorialRegionalRcorrelationNation= new ArrayList<ArrayList<RealMatrix>>();
		this.categorialRegionalRcorrelationRural = new ArrayList<ArrayList<RealMatrix>>();
		this.categorialRegionalRcorrelationUrban = new ArrayList<ArrayList<RealMatrix>>();
		this.categorialRegionalPvalueNation = new ArrayList<ArrayList<RealMatrix>>();
		this.categorialRegionalPvalueRural = new ArrayList<ArrayList<RealMatrix>>();
		this.categorialRegionalPvalueUrban = new ArrayList<ArrayList<RealMatrix>>();
		
		for(i=0 ; i<sma.duration ; i++){
			row = iec.n_category[i];
			checker = new boolean[row];
			tmpRnation	= new ArrayList<RealMatrix> ();
			tmpRrural		= new ArrayList<RealMatrix> ();
			tmpRurban	= new ArrayList<RealMatrix> ();
			tmpPnation	= new ArrayList<RealMatrix> ();
			tmpPrural		= new ArrayList<RealMatrix> ();
			tmpPurban	= new ArrayList<RealMatrix> ();
			
			for(k=0 ; k<sma.n_region[i] ; k++){
				/*** in national areas ***/
				tmpMatrix = new double[row][column];	
				for(j=0 ; j<row ; j++){
					tmpMatrix[j][0] = sma.lifeSatisfactionScoreCategorized[i][k][j];
					tmpMatrix[j][1] = sma.livingConditionScoreCategorized[i][k][j];
					tmpMatrix[j][2] = sma.incomeSatisfactionScoreCategorized[i][k][j];
					tmpMatrix[j][3] = sma.consumptionSatisfactionScoreCategorized[i][k][j];
					tmpMatrix[j][4] = sma.employmentStabilityScoreCategorized[i][k][j];
					tmpMatrix[j][5] = iec.entropyByCategory.get(i)[k][j];
					tmpMatrix[j][6] = iec.entropyBySizeByCategory.get(i)[k][j];
					tmpMatrix[j][7] = iec.entropyByLevelByCategory.get(i)[k][j];
					tmpMatrix[j][8] = iec.entropyByAgeByCategory.get(i)[k][j];	
				}				
				//check NaN-data rows
				tmpRow = row;
				for(j=0 ; j<row ; j++){
					nanChecker = true;
					for(l=0 ; l<column ; l++) if(Double.isNaN(tmpMatrix[j][l])) nanChecker = false;
					if(nanChecker) checker[j] = true;
					else{
						tmpRow--;
						checker[j] = false;
					}
				}
				//eliminate NaN-data rows
				if(tmpRow < row && tmpRow > 0){
					tmpStorage = new double[tmpRow][column];
					tmpRow = 0;
					for(j=0 ; j<row ; j++){
						if(checker[j]){
							for(l=0 ; l<column ; l++) tmpStorage[tmpRow][l] = tmpMatrix[j][l];
							tmpRow++;
						}
					}
					if(tmpRow>3){		//analyze correlation
						pc =  new PearsonsCorrelation(tmpStorage);
						tmpRnation.add(pc.getCorrelationMatrix());
						tmpPnation.add(pc.getCorrelationPValues());
					}else{
						tmpRnation.add(null);
						tmpPnation.add(null);
						System.err.println((this.startYear+i)+" year nation categorial regional correlation data lack");
					}
				}else if(row>2){		//analyze correlation
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpRnation.add(pc.getCorrelationMatrix());
					tmpPnation.add(pc.getCorrelationPValues());
				}else{
					tmpRnation.add(null);
					tmpPnation.add(null);
					System.err.println((this.startYear+i)+" year nation categorial regional correlation data lack");
				}
				tmpMatrix = null;
				pc = null;
				
				/*** in rural areas ***/
				tmpMatrix = new double[row][column];	
				for(j=0 ; j<row ; j++){
					tmpMatrix[j][0] = sma.ruralLifeSatisfactionScoreCategorized[i][k][j];
					tmpMatrix[j][1] = sma.ruralLivingConditionScoreCategorized[i][k][j];
					tmpMatrix[j][2] = sma.ruralIncomeSatisfactionScoreCategorized[i][k][j];
					tmpMatrix[j][3] = sma.ruralConsumptionSatisfactionScoreCategorized[i][k][j];
					tmpMatrix[j][4] = sma.ruralEmploymentStabilityScoreCategorized[i][k][j];
					tmpMatrix[j][5] = iec.ruralEntropyByCategory.get(i)[k][j];
					tmpMatrix[j][6] = iec.ruralEntropyBySizeByCategory.get(i)[k][j];
					tmpMatrix[j][7] = iec.ruralEntropyByLevelByCategory.get(i)[k][j];
					tmpMatrix[j][8] = iec.ruralEntropyByAgeByCategory.get(i)[k][j];	
				}
				//check NaN-data rows
				tmpRow = row;
				for(j=0 ; j<row ; j++){
					nanChecker = true;
					for(l=0 ; l<column ; l++) if(Double.isNaN(tmpMatrix[j][l])) nanChecker = false;
					if(nanChecker) checker[j] = true;
					else{
						tmpRow--;
						checker[j] = false;
					}
				}
				//eliminate NaN-data rows
				if(tmpRow < row && tmpRow > 0){
					tmpStorage = new double[tmpRow][column];
					tmpRow = 0;
					for(j=0 ; j<row ; j++){
						if(checker[j]){
							for(l=0 ; l<column ; l++) tmpStorage[tmpRow][l] = tmpMatrix[j][l];
							tmpRow++;
						}
					}
					if(tmpRow>3){		//analyze correlation
						pc =  new PearsonsCorrelation(tmpStorage);
						tmpRrural.add(pc.getCorrelationMatrix());
						tmpPrural.add(pc.getCorrelationPValues());
					}else{
						tmpRrural.add(null);
						tmpPrural.add(null);
						System.err.println((this.startYear+i)+" year rural categorial regional correlation data lack");
					}
				}else if(row>2){		//analyze correlation
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpRrural.add(pc.getCorrelationMatrix());
					tmpPrural.add(pc.getCorrelationPValues());
				}else{
					tmpRrural.add(null);
					tmpPrural.add(null);
					System.err.println((this.startYear+i)+" year rural categorial regional correlation data lack");
				}
				tmpMatrix = null;
				pc = null;
				
				/*** in urban areas ***/
				tmpMatrix = new double[row][column];	
				for(j=0 ; j<row ; j++){
					tmpMatrix[j][0] = sma.urbanLifeSatisfactionScoreCategorized[i][k][j];
					tmpMatrix[j][1] = sma.urbanLivingConditionScoreCategorized[i][k][j];
					tmpMatrix[j][2] = sma.urbanIncomeSatisfactionScoreCategorized[i][k][j];
					tmpMatrix[j][3] = sma.urbanConsumptionSatisfactionScoreCategorized[i][k][j];
					tmpMatrix[j][4] = sma.urbanEmploymentStabilityScoreCategorized[i][k][j];
					tmpMatrix[j][5] = iec.urbanEntropyByCategory.get(i)[k][j];
					tmpMatrix[j][6] = iec.urbanEntropyBySizeByCategory.get(i)[k][j];
					tmpMatrix[j][7] = iec.urbanEntropyByLevelByCategory.get(i)[k][j];
					tmpMatrix[j][8] = iec.urbanEntropyByAgeByCategory.get(i)[k][j];		
				}
				//check NaN-data rows
				tmpRow = row;
				for(j=0 ; j<row ; j++){
					nanChecker = true;
					for(l=0 ; l<column ; l++) if(Double.isNaN(tmpMatrix[j][l])) nanChecker = false;
					if(nanChecker) checker[j] = true;
					else{
						tmpRow--;
						checker[j] = false;
					}
				}
				//eliminate NaN-data rows
				if(tmpRow < row && tmpRow > 0){
					tmpStorage = new double[tmpRow][column];
					tmpRow = 0;
					for(j=0 ; j<row ; j++){
						if(checker[j]){
							for(l=0 ; l<column ; l++) tmpStorage[tmpRow][l] = tmpMatrix[j][l];
							tmpRow++;
						}
					}
					if(tmpRow>3){		//analyze correlation
						pc =  new PearsonsCorrelation(tmpStorage);
						tmpRurban.add(pc.getCorrelationMatrix());
						tmpPurban.add(pc.getCorrelationPValues());
					}else{
						tmpRurban.add(null);
						tmpPurban.add(null);
						System.err.println((this.startYear+i)+" year urban categorial regional correlation data lack");
					}
				}else if(row>2){		//analyze correlation
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpRurban.add(pc.getCorrelationMatrix());
					tmpPurban.add(pc.getCorrelationPValues());
				}else{
					tmpRurban.add(null);
					tmpPurban.add(null);
					System.err.println((this.startYear+i)+" year urban categorial regional correlation data lack");
				}
				tmpMatrix = null;
				pc = null;
			}
			this.categorialRegionalRcorrelationNation.add(tmpRnation);
			this.categorialRegionalRcorrelationRural.add(tmpRrural);
			this.categorialRegionalRcorrelationUrban.add(tmpRurban);
			this.categorialRegionalPvalueNation.add(tmpPnation);
			this.categorialRegionalPvalueRural.add(tmpPrural);
			this.categorialRegionalPvalueUrban.add(tmpPurban);
		}
	}
	
	public void correlationSpearmansNation(SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i;
		int rowCnt;
		int column = 5;
		double[][] tmpMatrix;
		SpearmansCorrelation pc;
		
		/*** compare life satisfaction v.s. entropy ***/
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][1]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][1]==1){
				tmpMatrix[rowCnt][0] = sma.lifeSatisfactionScoreNation[i];
				tmpMatrix[rowCnt][1] = iec.entropyNation[i];
				tmpMatrix[rowCnt][2] = iec.entropyNationBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyNationByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyNationByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.lifeRnation = pc.getCorrelationMatrix();
			this.lifePnation = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
			
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.lifeSatisfactionScoreRural[i];
				tmpMatrix[rowCnt][1] = iec.entropyRural[i];
				tmpMatrix[rowCnt][2] = iec.entropyRuralBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyRuralByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyRuralByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.lifeRrural = pc.getCorrelationMatrix();
			this.lifePrural = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.lifeSatisfactionScoreUrban[i];
				tmpMatrix[rowCnt][1] = iec.entropyUrban[i];
				tmpMatrix[rowCnt][2] = iec.entropyUrbanBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyUrbanByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyUrbanByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.lifeRurban = pc.getCorrelationMatrix();
			this.lifePurban = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		/*** compare living condition v.s. entropy ***/
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][2]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][2]==1){
				tmpMatrix[rowCnt][0] = sma.livingConditionScoreNation[i];
				tmpMatrix[rowCnt][1] = iec.entropyNation[i];
				tmpMatrix[rowCnt][2] = iec.entropyNationBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyNationByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyNationByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.livingRnation = pc.getCorrelationMatrix();
			this.livingPnation = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
			
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.livingConditionScoreRural[i];
				tmpMatrix[rowCnt][1] = iec.entropyRural[i];
				tmpMatrix[rowCnt][2] = iec.entropyRuralBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyRuralByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyRuralByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.livingRrural = pc.getCorrelationMatrix();
			this.livingPrural = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.livingConditionScoreUrban[i];
				tmpMatrix[rowCnt][1] = iec.entropyUrban[i];
				tmpMatrix[rowCnt][2] = iec.entropyUrbanBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyUrbanByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyUrbanByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.livingRurban = pc.getCorrelationMatrix();
			this.livingPurban = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		/*** compare  income satisfaction v.s. entropy ***/
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][3]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][3]==1){
				tmpMatrix[rowCnt][0] = sma.incomeSatisfactionScoreNation[i];
				tmpMatrix[rowCnt][1] = iec.entropyNation[i];
				tmpMatrix[rowCnt][2] = iec.entropyNationBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyNationByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyNationByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.incomeRnation = pc.getCorrelationMatrix();
			this.incomePnation = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
			
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.incomeSatisfactionScoreRural[i];
				tmpMatrix[rowCnt][1] = iec.entropyRural[i];
				tmpMatrix[rowCnt][2] = iec.entropyRuralBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyRuralByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyRuralByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.incomeRrural = pc.getCorrelationMatrix();
			this.incomePrural = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.incomeSatisfactionScoreUrban[i];
				tmpMatrix[rowCnt][1] = iec.entropyUrban[i];
				tmpMatrix[rowCnt][2] = iec.entropyUrbanBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyUrbanByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyUrbanByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.incomeRurban = pc.getCorrelationMatrix();
			this.incomePurban = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		/*** compare consumption satisfaction v.s. entropy ***/
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][4]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][4]==1){
				tmpMatrix[rowCnt][0] = sma.consumptionSatisfactionScoreNation[i];
				tmpMatrix[rowCnt][1] = iec.entropyNation[i];
				tmpMatrix[rowCnt][2] = iec.entropyNationBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyNationByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyNationByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.consumptionRnation = pc.getCorrelationMatrix();
			this.consumptionPnation = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.consumptionSatisfactionScoreRural[i];
				tmpMatrix[rowCnt][1] = iec.entropyRural[i];
				tmpMatrix[rowCnt][2] = iec.entropyRuralBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyRuralByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyRuralByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.consumptionRrural = pc.getCorrelationMatrix();
			this.consumptionPrural = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.consumptionSatisfactionScoreUrban[i];
				tmpMatrix[rowCnt][1] = iec.entropyUrban[i];
				tmpMatrix[rowCnt][2] = iec.entropyUrbanBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyUrbanByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyUrbanByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.consumptionRurban = pc.getCorrelationMatrix();
			this.consumptionPurban = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		/*** compare employment stability v.s. entropy ***/
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][5]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][5]==1){
				tmpMatrix[rowCnt][0] = sma.employmentStabilityScoreNation[i];
				tmpMatrix[rowCnt][1] = iec.entropyNation[i];
				tmpMatrix[rowCnt][2] = iec.entropyNationBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyNationByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyNationByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.stabilityRnation = pc.getCorrelationMatrix();
			this.stabilityPnation = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.employmentStabilityScoreRural[i];
				tmpMatrix[rowCnt][1] = iec.entropyRural[i];
				tmpMatrix[rowCnt][2] = iec.entropyRuralBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyRuralByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyRuralByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.stabilityRrural = pc.getCorrelationMatrix();
			this.stabilityPrural = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
		
		rowCnt = 0;
		pc = new SpearmansCorrelation();
		for(i=0 ; i<iec.duration ; i++)	
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1) rowCnt++;
		tmpMatrix = new double[rowCnt][column];
		rowCnt = 0;
		for(i=0 ; i<iec.duration ; i++){
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
				tmpMatrix[rowCnt][0] = sma.employmentStabilityScoreUrban[i];
				tmpMatrix[rowCnt][1] = iec.entropyUrban[i];
				tmpMatrix[rowCnt][2] = iec.entropyUrbanBySize[i];
				tmpMatrix[rowCnt][3] = iec.entropyUrbanByLevel[i];
				tmpMatrix[rowCnt][4] = iec.entropyUrbanByAge[i];
				rowCnt++;
			}
		}
		if(rowCnt>2){
			pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
			this.stabilityRurban = pc.getCorrelationMatrix();
			this.stabilityPurban = pc.getRankCorrelation().getCorrelationPValues();
		}
		tmpMatrix = null;
		pc = null;
	}

	public void correlationSpearmansRegion(SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i, j, k;
		int row;
		int column = 9;
		int tmpRow;
		boolean nanChecker;			//true: it's o.k,  false: has NaN
		boolean[] checker;				//true: it's o.k,  false: has NaN
		double[][] tmpMatrix, tmpStorage;
		SpearmansCorrelation pc;
		
		this.regionalRcorrelationNation	= new ArrayList<RealMatrix>();
		this.regionalRcorrelationRural	= new ArrayList<RealMatrix>();
		this.regionalRcorrelationUrban	= new ArrayList<RealMatrix>();
		this.regionalPvalueNation			= new ArrayList<RealMatrix>();
		this.regionalPvalueRural				= new ArrayList<RealMatrix>();
		this.regionalPvalueUrban			= new ArrayList<RealMatrix>();
		
		for(i=0 ; i<sma.duration ; i++){
			row = sma.n_region[i];
			checker = new boolean[row];
			
			/*** in national areas ***/
			tmpMatrix = new double[row][column];	
			for(j=0 ; j<row ; j++){
				tmpMatrix[j][0] = sma.lifeSatisfactionScore[i][j];
				tmpMatrix[j][1] = sma.livingConditionScore[i][j];
				tmpMatrix[j][2] = sma.incomeSatisfactionScore[i][j];
				tmpMatrix[j][3] = sma.consumptionSatisfactionScore[i][j];
				tmpMatrix[j][4] = sma.employmentStabilityScore[i][j];
				tmpMatrix[j][5] = iec.entropy.get(i)[j];
				tmpMatrix[j][6] = iec.entropyBySize.get(i)[j];
				tmpMatrix[j][7] = iec.entropyByLevel.get(i)[j];
				tmpMatrix[j][8] = iec.entropyByAge.get(i)[j];			
			}
			//check NaN-data rows
			tmpRow = row;
			for(j=0 ; j<row ; j++){
				nanChecker = true;
				for(k=0 ; k<column ; k++) if(Double.isNaN(tmpMatrix[j][k])) nanChecker = false;
				if(nanChecker) checker[j] = true;
				else{
					tmpRow--;
					checker[j] = false;
				}
			}
			//eliminate NaN-data rows
			if(tmpRow < row && tmpRow > 2){
				tmpStorage = new double[tmpRow][column];
				tmpRow = 0;
				for(j=0 ; j<row ; j++){
					if(checker[j]){
						for(k=0 ; k<column ; k++) tmpStorage[tmpRow][k] = tmpMatrix[j][k];
						tmpRow++;
					}
				}
				//analyze correlation
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpStorage));			
				this.regionalRcorrelationNation.add(pc.getCorrelationMatrix());
				this.regionalPvalueNation.add(pc.getRankCorrelation().getCorrelationPValues());
			}else if(tmpRow == row && row>2){		
				//analyze correlation
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));			
				this.regionalRcorrelationNation.add(pc.getCorrelationMatrix());
				this.regionalPvalueNation.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				this.regionalRcorrelationNation.add(null);
				this.regionalPvalueNation.add(null);
				System.err.println((this.startYear+i)+" year nation correlation data lack");
			}
			tmpMatrix = null;
			pc = null;

			/*** in rural areas ***/
			tmpMatrix = new double[row][column];	
			for(j=0 ; j<row ; j++){
				tmpMatrix[j][0] = sma.ruralLifeSatisfactionScore[i][j];
				tmpMatrix[j][1] = sma.ruralLivingConditionScore[i][j];
				tmpMatrix[j][2] = sma.ruralIncomeSatisfactionScore[i][j];
				tmpMatrix[j][3] = sma.ruralConsumptionSatisfactionScore[i][j];
				tmpMatrix[j][4] = sma.ruralEmploymentStabilityScore[i][j];
				tmpMatrix[j][5] = iec.ruralEntropy.get(i)[j];
				tmpMatrix[j][6] = iec.ruralEntropyBySize.get(i)[j];
				tmpMatrix[j][7] = iec.ruralEntropyByLevel.get(i)[j];
				tmpMatrix[j][8] = iec.ruralEntropyByAge.get(i)[j];			
			}
			//check NaN-data rows
			tmpRow = row;
			for(j=0 ; j<row ; j++){
				nanChecker = true;
				for(k=0 ; k<column ; k++) if(Double.isNaN(tmpMatrix[j][k])) nanChecker = false;
				if(nanChecker) checker[j] = true;
				else{
					tmpRow--;
					checker[j] = false;
				}
			}
			//eliminate NaN-data rows
			if(tmpRow < row && tmpRow > 2){
				tmpStorage = new double[tmpRow][column];
				tmpRow = 0;
				for(j=0 ; j<row ; j++){
					if(checker[j]){
						for(k=0 ; k<column ; k++) tmpStorage[tmpRow][k] = tmpMatrix[j][k];
						tmpRow++;
					}
				}
				//analyze correlation
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpStorage));
				this.regionalRcorrelationRural.add(pc.getCorrelationMatrix());
				this.regionalPvalueRural.add(pc.getRankCorrelation().getCorrelationPValues());
			}else if(tmpRow == row && row>2){		
				//analyze correlation
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));	
				this.regionalRcorrelationRural.add(pc.getCorrelationMatrix());
				this.regionalPvalueRural.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				this.regionalRcorrelationRural.add(null);
				this.regionalPvalueRural.add(null);
				System.err.println((this.startYear+i)+" year rural correlation data lack");
			}
			tmpMatrix = null;
			pc = null;
			
			/*** in urban areas ***/
			tmpMatrix = new double[row][column];	
			for(j=0 ; j<row ; j++){
				tmpMatrix[j][0] = sma.urbanLifeSatisfactionScore[i][j];
				tmpMatrix[j][1] = sma.urbanLivingConditionScore[i][j];
				tmpMatrix[j][2] = sma.urbanIncomeSatisfactionScore[i][j];
				tmpMatrix[j][3] = sma.urbanConsumptionSatisfactionScore[i][j];
				tmpMatrix[j][4] = sma.urbanEmploymentStabilityScore[i][j];
				tmpMatrix[j][5] = iec.urbanEntropy.get(i)[j];
				tmpMatrix[j][6] = iec.urbanEntropyBySize.get(i)[j];
				tmpMatrix[j][7] = iec.urbanEntropyByLevel.get(i)[j];
				tmpMatrix[j][8] = iec.urbanEntropyByAge.get(i)[j];			
			}
			//check NaN-data rows
			tmpRow = row;
			for(j=0 ; j<row ; j++){
				nanChecker = true;
				for(k=0 ; k<column ; k++) if(Double.isNaN(tmpMatrix[j][k])) nanChecker = false;
				if(nanChecker) checker[j] = true;
				else{
					tmpRow--;
					checker[j] = false;
				}
			}
			//eliminate NaN-data rows
			if(tmpRow < row && tmpRow > 2){
				tmpStorage = new double[tmpRow][column];
				tmpRow = 0;
				for(j=0 ; j<row ; j++){
					if(checker[j]){
						for(k=0 ; k<column ; k++) tmpStorage[tmpRow][k] = tmpMatrix[j][k];
						tmpRow++;
					}
				}
				//analyze correlation
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpStorage));
				this.regionalRcorrelationUrban.add(pc.getCorrelationMatrix());
				this.regionalPvalueUrban.add(pc.getRankCorrelation().getCorrelationPValues());
			}else if(tmpRow == row && row>2){		
				//analyze correlation
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				this.regionalRcorrelationUrban.add(pc.getCorrelationMatrix());
				this.regionalPvalueUrban.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				this.regionalRcorrelationUrban.add(null);
				this.regionalPvalueUrban.add(null);
				System.err.println((this.startYear+i)+" year urban correlation data lack");
			}
			tmpMatrix = null;
			pc = null;
		}
	}
	
	public void correlationSpearmansRegionTimeSeries(SocietyMicrodataAnalyzer sma, IndustryEntropyCalculator iec){
		
		int i, j;
		int n_years;
		int rowCnt;
		int column = 5;
		double[][] tmpMatrix;
		ArrayList<RealMatrix> tmpRmatrix, tmpPmatrix;
		
		SpearmansCorrelation pc;
		
		this.regionalRcorrelationTimeSeriesNation	= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalRcorrelationTimeSeriesRural		= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalRcorrelationTimeSeriesUrban		= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalPvalueTimeSeriesNation				= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalPvalueTimeSeriesRural				= new ArrayList<ArrayList<RealMatrix>>();
		this.regionalPvalueTimeSeriesUrban				= new ArrayList<ArrayList<RealMatrix>>();
		
		/*** compare life satisfaction v.s. entropy ***/
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][1]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1){
					tmpMatrix[rowCnt][0] = sma.lifeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.entropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.entropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.entropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.entropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));	
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesNation.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesNation.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.ruralLifeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.ruralEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.ruralEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.ruralEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.ruralEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));	
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesRural.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesRural.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][1]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.urbanLifeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.urbanEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.urbanEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.urbanEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.urbanEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesUrban.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesUrban.add(tmpPmatrix);

		
		/*** compare living condition v.s. entropy ***/
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][2]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1){
					tmpMatrix[rowCnt][0] = sma.livingConditionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.entropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.entropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.entropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.entropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesNation.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesNation.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.ruralLivingConditionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.ruralEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.ruralEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.ruralEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.ruralEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesRural.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesRural.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][2]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.urbanLivingConditionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.urbanEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.urbanEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.urbanEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.urbanEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesUrban.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesUrban.add(tmpPmatrix);
		
		
		/*** compare income satisfaction v.s. entropy ***/
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][3]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1){
					tmpMatrix[rowCnt][0] = sma.incomeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.entropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.entropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.entropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.entropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesNation.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesNation.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.ruralIncomeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.ruralEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.ruralEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.ruralEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.ruralEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesRural.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesRural.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][3]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.urbanIncomeSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.urbanEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.urbanEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.urbanEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.urbanEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesUrban.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesUrban.add(tmpPmatrix);
		
		
		/*** compare consumption satisfaction v.s. entropy ***/
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][4]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1){
					tmpMatrix[rowCnt][0] = sma.consumptionSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.entropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.entropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.entropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.entropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesNation.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesNation.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.ruralConsumptionSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.ruralEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.ruralEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.ruralEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.ruralEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesRural.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesRural.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][4]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.urbanConsumptionSatisfactionScore[i][j];
					tmpMatrix[rowCnt][1] = iec.urbanEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.urbanEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.urbanEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.urbanEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesUrban.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesUrban.add(tmpPmatrix);
		
		
		/*** compare job stability v.s. entropy ***/
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)	if(sma.responseExistence[i][5]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1){
					tmpMatrix[rowCnt][0] = sma.employmentStabilityScore[i][j];
					tmpMatrix[rowCnt][1] = iec.entropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.entropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.entropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.entropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesNation.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesNation.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.ruralEmploymentStabilityScore[i][j];
					tmpMatrix[rowCnt][1] = iec.ruralEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.ruralEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.ruralEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.ruralEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesRural.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesRural.add(tmpPmatrix);
		
		n_years = 0;
		for(i=0 ; i<iec.duration ; i++)
			if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1) n_years++;
		tmpRmatrix = new ArrayList<RealMatrix>();
		tmpPmatrix = new ArrayList<RealMatrix>();
		for(j=0 ; j<iec.n_region[0] ; j++){
			rowCnt = 0;
			tmpMatrix = new double[n_years][column];
			for(i=0 ; i<iec.duration ; i++){
				if(sma.responseExistence[i][5]==1 && sma.responseExistence[i][6]==1){
					tmpMatrix[rowCnt][0] = sma.urbanEmploymentStabilityScore[i][j];
					tmpMatrix[rowCnt][1] = iec.urbanEntropy.get(i)[j];
					tmpMatrix[rowCnt][2] = iec.urbanEntropyBySize.get(i)[j];
					tmpMatrix[rowCnt][3] = iec.urbanEntropyByLevel.get(i)[j];
					tmpMatrix[rowCnt][4] = iec.urbanEntropyByAge.get(i)[j];
					if(Double.isNaN(tmpMatrix[rowCnt][0]) == false) rowCnt++;
				}
			}
			if(rowCnt>2){
				pc = new SpearmansCorrelation(MatrixUtils.createRealMatrix(tmpMatrix));	
				tmpRmatrix.add(pc.getCorrelationMatrix());
				tmpPmatrix.add(pc.getRankCorrelation().getCorrelationPValues());
			}else{
				tmpRmatrix.add(null);
				tmpPmatrix.add(null);
			}
			tmpMatrix = null;
			pc = null;
		}
		this.regionalRcorrelationTimeSeriesUrban.add(tmpRmatrix);
		this.regionalPvalueTimeSeriesUrban.add(tmpPmatrix);

	}
	
	public void proceedSocietyDataAnalysis(SocietyMicrodataAnalyzer sma,  double[] gradeWeight, String filePath,
			String locationCodePath, String locationCodeFile){

		System.out.println("society data analysis process is started.");
		System.out.print("location code reading: ");
		sma.readLocationCode(locationCodePath, locationCodeFile);
		System.out.println("complete");

		System.out.print("variables initializing: ");
		sma.setIndustryCode();
		sma.initiate();
		sma.initiateCategorizedVariables();
		System.out.println("complete");

		System.out.print("microdata reading: ");
		sma.extractSocieyMicrodata(filePath, "_Social_MicroData.txt");
		System.out.println("complete");	

		System.out.print("statistics calculating: ");
		sma.calculateSocietyStatistics();
		sma.calculateCategorizedSocietyStatistics();
		sma.calculateSatisfactionTotalScore(gradeWeight);
		sma.calculateCategorizedSatisfactionTotalScore();
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
	
	public void proceedIndustryDataAnalysis(IndustryEntropyCalculator iec, String filePath, String profitFile,
			int[] marker, ArrayList<ArrayList<Integer>> rearrangeList,
			ArrayList<ArrayList<String>> code, ArrayList<ArrayList<String>> name){

		System.out.println("industry data analysis process is started.");
		System.out.print("codes reading: ");
		iec.readStandardCodes(filePath);
		System.out.println("complete");	
		System.out.println();
		
		System.out.println("[industry entropy] ");
		System.out.print("employee reading: ");
		iec.readEmployee(filePath+"extracted/", "_microdataCode.txt");
		System.out.println("complete");	
		
		System.out.print("data rearranging: ");		
		iec.rearrangeCategoryList(marker, rearrangeList, code, name);
		iec.rearrangeCategoryEmployee(marker, rearrangeList);
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
		
		System.out.print("data rearranging: ");		
		iec.rearrangeCategoryList(marker, rearrangeList, code, name);
		iec.rearrangeCategoryEmployeeBySize(marker, rearrangeList);
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
		
		System.out.print("data rearranging: ");		
		iec.rearrangeCategoryList(marker, rearrangeList, code, name);
		iec.rearrangeCategoryEmployeeByLevel(marker, rearrangeList);
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
		
		System.out.print("data rearranging: ");		
		iec.rearrangeCategoryList(marker, rearrangeList, code, name);
		iec.rearrangeCategoryEmployeeByAge(marker, rearrangeList);
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
		iec.normalizeCategoryEntropy();
		System.out.println("complete");	
		
		System.out.println("industry data analysis process is completed.");
	}
	
	public void printNationalCorrlation(String outputFile){
		int i, j;
		int categories = 5;
		String[] title = {"Satisfaction", "H_industry", "H_employeeSize", "H_profitLevel", "H_businessYear"};
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			/*** print life satisfaction v.s. entropy ***/
			if(this.lifeRnation != null){
				pw.println("life satisfaction: nation");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.lifeRnation.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.lifeRnation.getColumnDimension() ; j++) pw.print("\t"+this.lifeRnation.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.lifePnation.getColumnDimension() ; j++) pw.print("\t"+this.lifePnation.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			if(this.lifeRrural != null){
				pw.println("life satisfaction: rural");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.lifeRrural.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.lifeRrural.getColumnDimension() ; j++) pw.print("\t"+this.lifeRrural.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.lifePrural.getColumnDimension() ; j++) pw.print("\t"+this.lifePrural.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			if(this.lifeRurban != null){
				pw.println("life satisfaction: urban");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.lifeRurban.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.lifeRurban.getColumnDimension() ; j++) pw.print("\t"+this.lifeRurban.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.lifePurban.getColumnDimension() ; j++) pw.print("\t"+this.lifePurban.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			/*** print living condition v.s. entropy ***/
			if(this.livingRnation != null){
				pw.println("living condition: nation");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.livingRnation.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.livingRnation.getColumnDimension() ; j++) pw.print("\t"+this.livingRnation.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.livingPnation.getColumnDimension() ; j++) pw.print("\t"+this.livingPnation.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			if(this.livingRrural != null){
			pw.println("living condition: rural");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.livingRrural.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.livingRrural.getColumnDimension() ; j++) pw.print("\t"+this.livingRrural.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.livingPrural.getColumnDimension() ; j++) pw.print("\t"+this.livingPrural.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			if(this.livingRurban != null){
				pw.println("living condition: urban");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.livingRurban.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.livingRurban.getColumnDimension() ; j++) pw.print("\t"+this.livingRurban.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.livingPurban.getColumnDimension() ; j++) pw.print("\t"+this.livingPurban.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			/*** print  income satisfaction v.s. entropy ***/
			if(this.incomeRnation != null){
				pw.println("income satisfaction: nation");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.incomeRnation.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.incomeRnation.getColumnDimension() ; j++) pw.print("\t"+this.incomeRnation.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.incomePnation.getColumnDimension() ; j++) pw.print("\t"+this.incomePnation.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			if(this.incomeRrural != null){
			pw.println("income satisfaction: rural");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.incomeRrural.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.incomeRrural.getColumnDimension() ; j++) pw.print("\t"+this.incomeRrural.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.incomePrural.getColumnDimension() ; j++) pw.print("\t"+this.incomePrural.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			if(this.incomeRurban != null){
				pw.println("income satisfaction: urban");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.incomeRurban.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.incomeRurban.getColumnDimension() ; j++) pw.print("\t"+this.incomeRurban.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.incomePurban.getColumnDimension() ; j++) pw.print("\t"+this.incomePurban.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			/*** print consumption satisfaction v.s. entropy ***/
			if(this.consumptionRnation != null){
				pw.println("consumption satisfaction: nation");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.consumptionRnation.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.consumptionRnation.getColumnDimension() ; j++) pw.print("\t"+this.consumptionRnation.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.consumptionPnation.getColumnDimension() ; j++) pw.print("\t"+this.consumptionPnation.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			if(this.consumptionRrural != null){
				pw.println("consumption satisfaction: rural");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.consumptionRrural.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.consumptionRrural.getColumnDimension() ; j++) pw.print("\t"+this.consumptionRrural.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.consumptionPrural.getColumnDimension() ; j++) pw.print("\t"+this.consumptionPrural.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			if(this.consumptionRurban != null){
				pw.println("consumption satisfaction: urban");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.consumptionRurban.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.consumptionRurban.getColumnDimension() ; j++) pw.print("\t"+this.consumptionRurban.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.consumptionPurban.getColumnDimension() ; j++) pw.print("\t"+this.consumptionPurban.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			/*** print employment stability v.s. entropy ***/
			if(this.stabilityRnation != null){
				pw.println("employment stability: nation");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.stabilityRnation.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.stabilityRnation.getColumnDimension() ; j++) pw.print("\t"+this.stabilityRnation.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.stabilityPnation.getColumnDimension() ; j++) pw.print("\t"+this.stabilityPnation.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			if(this.stabilityRrural != null){
				pw.println("employment stability: rural");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.stabilityRrural.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.stabilityRrural.getColumnDimension() ; j++) pw.print("\t"+this.stabilityRrural.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.stabilityPrural.getColumnDimension() ; j++) pw.print("\t"+this.stabilityPrural.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			if(this.stabilityRurban != null){
				pw.println("employment stability: urban");
				pw.print("Correlation");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.print("\t\tP-value");
				for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
				pw.println();
				
				for(i=0 ; i<this.stabilityRurban.getRowDimension() ; i++){
					pw.print(title[i]);
					for(j=0 ; j<this.stabilityRurban.getColumnDimension() ; j++) pw.print("\t"+this.stabilityRurban.getEntry(i, j));
					pw.print("\t\t"+title[i]);
					for(j=0 ; j<this.stabilityPurban.getColumnDimension() ; j++) pw.print("\t"+this.stabilityPurban.getEntry(i, j));
					pw.println();
				}
				pw.println();
			}
			
			pw.close();
		}catch(IOException e) {}	
	}
		
	public void printRegionalCorrelation(String outputFile){
		int i, j, k;
		int n_category = 9;
		boolean[] checker = new boolean[n_category];		//true: is NaN,  false: has value
		String[] title = {"S_life", "S_living","S_income","S_consumption","S_stability","H_industry","H_size", "H_level", "H_year"};
		RealMatrix tmpRmatrix, tmpPmatrix;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			for(i=0 ; i<this.duration ; i++){
				pw.println("Year: "+(this.startYear+i));
				pw.println("nation");
				tmpRmatrix = this.regionalRcorrelationNation.get(i);
				tmpPmatrix = this.regionalPvalueNation.get(i);
				if(tmpRmatrix != null){
					/*** check value existence ***/
					for(j=0 ; j<n_category ; j++) 
						checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
					/*** print existing values ***/
					pw.print("Correlation");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
					for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
					pw.print("\t\tP-value");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
					pw.println();
					for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
						if(checker[j] == false){
							pw.print(title[j]);
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == false) pw.print("\t"+tmpRmatrix.getEntry(j, k));
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == true) pw.print("\t");
							pw.print("\t\t"+title[j]);
							for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
								if(checker[k] == false) pw.print("\t"+tmpPmatrix.getEntry(j, k));
							pw.println();
						}
					}
					pw.println();
				}
				
				pw.println("rural area");
				tmpRmatrix = this.regionalRcorrelationRural.get(i);
				tmpPmatrix = this.regionalPvalueRural.get(i);
				if(tmpRmatrix != null){
					/*** check value existence ***/
					for(j=0 ; j<n_category ; j++) 
						checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
					/*** print existing values ***/
					pw.print("Correlation");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
					for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
					pw.print("\t\tP-value");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
					pw.println();
					for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
						if(checker[j] == false){
							pw.print(title[j]);
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == false) pw.print("\t"+tmpRmatrix.getEntry(j, k));
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == true) pw.print("\t");
							pw.print("\t\t"+title[j]);
							for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
								if(checker[k] == false) pw.print("\t"+tmpPmatrix.getEntry(j, k));
							pw.println();
						}
					}
					pw.println();
				}
				
				pw.println("urban area");
				tmpRmatrix = this.regionalRcorrelationUrban.get(i);
				tmpPmatrix = this.regionalPvalueUrban.get(i);
				if(tmpRmatrix != null){
					/*** check value existence ***/
					for(j=0 ; j<n_category ; j++) 
						checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
					/*** print existing values ***/
					pw.print("Correlation");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
					for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
					pw.print("\t\tP-value");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
					pw.println();
					for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
						if(checker[j] == false){
							pw.print(title[j]);
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == false) pw.print("\t"+tmpRmatrix.getEntry(j, k));
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == true) pw.print("\t");
							pw.print("\t\t"+title[j]);
							for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
								if(checker[k] == false) pw.print("\t"+tmpPmatrix.getEntry(j, k));
							pw.println();
						}
					}
					pw.println();
				}
			}	
			pw.close();
		}catch(IOException e) {}	
	}
	
	public void printRegionalTimeSeriesCorrelation(String outputFile, IndustryEntropyCalculator iec){
		int i, j, k, l;
		int categories = 5;
		RealMatrix tmpRmatrix, tmpPmatrix;
		String[] title = {"Satisfaction", "H_industry", "H_employeeSize", "H_profitLevel", "H_businessYear"};
		String[] satisfactionSort = {"life satisfaction", "living condition", "income satisfaction", "consumption satisfaction", "employeement stability"};
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			/*** print satisfaction v.s. entropy ***/
			for(k=0 ; k< this.regionalRcorrelationTimeSeriesNation.size() ; k++){
				for(l=0 ; l<this.regionalRcorrelationTimeSeriesNation.get(k).size() ; l++){
					tmpRmatrix = this.regionalRcorrelationTimeSeriesNation.get(k).get(l);
					tmpPmatrix = this.regionalPvalueTimeSeriesNation.get(k).get(l);
					if(tmpRmatrix != null){
						pw.println(satisfactionSort[k]+" nation:\t"+iec.locatoinName.get(0).get(l));
						pw.print("Correlation");
						for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
						pw.print("\t\tP-value");
						for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
						pw.println();
						
						for(i=0 ; i<tmpRmatrix.getRowDimension() ; i++){
							pw.print(title[i]);
							for(j=0 ; j<tmpRmatrix.getColumnDimension() ; j++) pw.print("\t"+tmpRmatrix.getEntry(i, j));
							pw.print("\t\t"+title[i]);
							for(j=0 ; j<tmpPmatrix.getColumnDimension() ; j++) pw.print("\t"+tmpPmatrix.getEntry(i, j));
							pw.println();
						}
						pw.println();
					}
					
					tmpRmatrix = this.regionalRcorrelationTimeSeriesRural.get(k).get(l);
					tmpPmatrix = this.regionalPvalueTimeSeriesRural.get(k).get(l);
					if(tmpRmatrix != null){
						pw.println(satisfactionSort[k]+" rural:\t"+iec.locatoinName.get(0).get(l));
						pw.print("Correlation");
						for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
						pw.print("\t\tP-value");
						for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
						pw.println();
						
						for(i=0 ; i<tmpRmatrix.getRowDimension() ; i++){
							pw.print(title[i]);
							for(j=0 ; j<tmpRmatrix.getColumnDimension() ; j++) pw.print("\t"+tmpRmatrix.getEntry(i, j));
							pw.print("\t\t"+title[i]);
							for(j=0 ; j<tmpPmatrix.getColumnDimension() ; j++) pw.print("\t"+tmpPmatrix.getEntry(i, j));
							pw.println();
						}
						pw.println();
					}
					
					tmpRmatrix = this.regionalRcorrelationTimeSeriesUrban.get(k).get(l);
					tmpPmatrix = this.regionalPvalueTimeSeriesUrban.get(k).get(l);
					if(tmpRmatrix != null){
						pw.println(satisfactionSort[k]+" urban:\t"+iec.locatoinName.get(0).get(l));
						pw.print("Correlation");
						for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
						pw.print("\t\tP-value");
						for(i=0 ; i<categories ; i++) pw.print("\t"+title[i]);
						pw.println();
						
						for(i=0 ; i<tmpRmatrix.getRowDimension() ; i++){
							pw.print(title[i]);
							for(j=0 ; j<tmpRmatrix.getColumnDimension() ; j++) pw.print("\t"+tmpRmatrix.getEntry(i, j));
							pw.print("\t\t"+title[i]);
							for(j=0 ; j<tmpPmatrix.getColumnDimension() ; j++) pw.print("\t"+tmpPmatrix.getEntry(i, j));
							pw.println();
						}
						pw.println();
					}
					pw.println();
				}
				pw.println();
			}
			pw.close();
		}catch(IOException e) {}	
	}
	
	public void printRegionalCategorialCorrelation(String outputFile, IndustryEntropyCalculator iec){
		int i, j, k, l;
		int n_category = 9;
		boolean[] checker = new boolean[n_category];		//true: is Nan,  false: has value
		String[] title = {"S_life", "S_living","S_income","S_consumption","S_stability","H_industry","H_size", "H_level", "H_year"};
		RealMatrix tmpRmatrix, tmpPmatrix;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			for(i=0 ; i<this.duration ; i++){
				for(l=0 ; l<iec.n_category[i] ; l++){
					pw.println("Year: "+(this.startYear+i)+"\tCategory"+iec.categoryName.get(i).get(l));
					
					pw.println("nation");			
					tmpRmatrix = this.regionalCategorialRcorrelationNation.get(i).get(l);	
					tmpPmatrix = this.regionalCategorialPvalueNation.get(i).get(l);

					if(tmpRmatrix != null){
						/*** check value existence ***/
						for(j=0 ; j<n_category ; j++) 
							checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
						/*** print existing values ***/
						pw.print("Correlation");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
						for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
						pw.print("\t\tP-value");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
						pw.println();
						for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
							if(checker[j] == false){
								pw.print(title[j]);
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == false) 
										pw.print("\t"+tmpRmatrix.getEntry(j, k));
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == true)  pw.print("\t");
								pw.print("\t\t"+title[j]);
								for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
									if(checker[k] == false) 
										pw.print("\t"+tmpPmatrix.getEntry(j, k));
								pw.println();
							}
						}
						pw.println();
					}
					
					pw.println("rural area");
					tmpRmatrix = this.regionalCategorialRcorrelationRural.get(i).get(l);		
					tmpPmatrix = this.regionalCategorialPvalueRural.get(i).get(l);
					
					if(tmpRmatrix != null){
						/*** check value existence ***/
						for(j=0 ; j<n_category ; j++) 
							checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
						/*** print existing values ***/
						pw.print("Correlation");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
						for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
						pw.print("\t\tP-value");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
						pw.println();
						for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
							if(checker[j] == false){
								pw.print(title[j]);
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == false) 
										pw.print("\t"+tmpRmatrix.getEntry(j, k));
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == true)  pw.print("\t");
								pw.print("\t\t"+title[j]);
								for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
									if(checker[k] == false) 
										pw.print("\t"+tmpPmatrix.getEntry(j, k));
								pw.println();
							}
						}
						pw.println();
					}
					
					pw.println("urban area");
					tmpRmatrix = this.regionalCategorialRcorrelationUrban.get(i).get(l);	
					tmpPmatrix = this.regionalCategorialPvalueUrban.get(i).get(l);

					if(tmpRmatrix != null){
						/*** check value existence ***/
						for(j=0 ; j<n_category ; j++) 
							checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
						/*** print existing values ***/
						pw.print("Correlation");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
						for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
						pw.print("\t\tP-value");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
						pw.println();
						for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
							if(checker[j] == false){
								pw.print(title[j]);
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == false) 
										pw.print("\t"+tmpRmatrix.getEntry(j, k));
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == true)  pw.print("\t");
								pw.print("\t\t"+title[j]);
								for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
									if(checker[k] == false) 
										pw.print("\t"+tmpPmatrix.getEntry(j, k));
								pw.println();
							}
						}
						pw.println();
					}
				}
			}
			
			pw.close();
		}catch(IOException e) {}	
	}
	
	public void printCategorialCorrelation(String outputFile){
		int i, j, k;
		int n_category = 9;
		boolean[] checker = new boolean[n_category];		//true: is NaN,  false: has value
		String[] title = {"S_life", "S_living","S_income","S_consumption","S_stability","H_industry","H_size", "H_level", "H_year"};
		RealMatrix tmpRmatrix, tmpPmatrix;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			for(i=0 ; i<this.duration ; i++){
				pw.println("Year: "+(this.startYear+i));
				pw.println("nation");
				tmpRmatrix = this.categorialRcorrelationNation.get(i);
				tmpPmatrix = this.categorialPvalueNation.get(i);
				if(tmpRmatrix != null){
					/*** check value existence ***/
					for(j=0 ; j<n_category ; j++) 
						checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
					/*** print existing values ***/
					pw.print("Correlation");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
					for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
					pw.print("\t\tP-value");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
					pw.println();
					for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
						if(checker[j] == false){
							pw.print(title[j]);
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == false) pw.print("\t"+tmpRmatrix.getEntry(j, k));
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == true) pw.print("\t");
							pw.print("\t\t"+title[j]);
							for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
								if(checker[k] == false) pw.print("\t"+tmpPmatrix.getEntry(j, k));
							pw.println();
						}
					}
					pw.println();
				}
				
				pw.println("rural area");
				tmpRmatrix = this.categorialRcorrelationRural.get(i);
				tmpPmatrix = this.categorialPvalueRural.get(i);
				if(tmpRmatrix != null){
					/*** check value existence ***/
					for(j=0 ; j<n_category ; j++) 
						checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
					/*** print existing values ***/
					pw.print("Correlation");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
					for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
					pw.print("\t\tP-value");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
					pw.println();
					for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
						if(checker[j] == false){
							pw.print(title[j]);
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == false) pw.print("\t"+tmpRmatrix.getEntry(j, k));
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == true) pw.print("\t");
							pw.print("\t\t"+title[j]);
							for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
								if(checker[k] == false) pw.print("\t"+tmpPmatrix.getEntry(j, k));
							pw.println();
						}
					}
					pw.println();
				}
				
				pw.println("urban area");
				tmpRmatrix = this.categorialRcorrelationUrban.get(i);
				tmpPmatrix = this.categorialPvalueUrban.get(i);
				if(tmpRmatrix != null){
					/*** check value existence ***/
					for(j=0 ; j<n_category ; j++) 
						checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
					/*** print existing values ***/
					pw.print("Correlation");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
					for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
					pw.print("\t\tP-value");
					for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
					pw.println();
					for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
						if(checker[j] == false){
							pw.print(title[j]);
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == false) pw.print("\t"+tmpRmatrix.getEntry(j, k));
							for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
								if(checker[k] == true) pw.print("\t");
							pw.print("\t\t"+title[j]);
							for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
								if(checker[k] == false) pw.print("\t"+tmpPmatrix.getEntry(j, k));
							pw.println();
						}
					}
					pw.println();
				}
			}	
			pw.close();
		}catch(IOException e) {}	
	}
	
	public void printCategorialRegionalCorrelation(String outputFile, IndustryEntropyCalculator iec){
		int i, j, k, l;
		int n_category = 9;
		boolean[] checker = new boolean[n_category];		//true: is Nan,  false: has value
		String[] title = {"S_life", "S_living","S_income","S_consumption","S_stability","H_industry","H_size", "H_level", "H_year"};
		RealMatrix tmpRmatrix, tmpPmatrix;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
			
			for(i=0 ; i<this.duration ; i++){
				for(l=0 ; l<iec.n_region[i] ; l++){
					pw.println("Year: "+(this.startYear+i)+"\tRegion"+iec.locatoinName.get(i).get(l));
					
					pw.println("nation");			
					tmpRmatrix = this.categorialRegionalRcorrelationNation.get(i).get(l);	
					tmpPmatrix = this.categorialRegionalPvalueNation.get(i).get(l);

					if(tmpRmatrix != null){
						/*** check value existence ***/
						for(j=0 ; j<n_category ; j++) 
							checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
						/*** print existing values ***/
						pw.print("Correlation");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
						for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
						pw.print("\t\tP-value");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
						pw.println();
						for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
							if(checker[j] == false){
								pw.print(title[j]);
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == false) 
										pw.print("\t"+tmpRmatrix.getEntry(j, k));
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == true)  pw.print("\t");
								pw.print("\t\t"+title[j]);
								for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
									if(checker[k] == false) 
										pw.print("\t"+tmpPmatrix.getEntry(j, k));
								pw.println();
							}
						}
						pw.println();
					}
					
					pw.println("rural area");
					tmpRmatrix = this.categorialRegionalRcorrelationRural.get(i).get(l);		
					tmpPmatrix = this.categorialRegionalPvalueRural.get(i).get(l);

					if(tmpRmatrix != null){
						/*** check value existence ***/
						for(j=0 ; j<n_category ; j++) 
							checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
						/*** print existing values ***/
						pw.print("Correlation");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
						for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
						pw.print("\t\tP-value");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
						pw.println();
						for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
							if(checker[j] == false){
								pw.print(title[j]);
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == false) 
										pw.print("\t"+tmpRmatrix.getEntry(j, k));
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == true)  pw.print("\t");
								pw.print("\t\t"+title[j]);
								for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
									if(checker[k] == false) 
										pw.print("\t"+tmpPmatrix.getEntry(j, k));
								pw.println();
							}
						}
						pw.println();
					}
					
					pw.println("urban area");
					tmpRmatrix = this.categorialRegionalRcorrelationUrban.get(i).get(l);	
					tmpPmatrix = this.categorialRegionalPvalueUrban.get(i).get(l);

					if(tmpRmatrix != null){
						/*** check value existence ***/
						for(j=0 ; j<n_category ; j++) 
							checker[j] = Double.isNaN(tmpRmatrix.getEntry((tmpRmatrix.getRowDimension()-1), j));
						/*** print existing values ***/
						pw.print("Correlation");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false) pw.print("\t"+title[j]);
						for(j=0 ; j<n_category ; j++) if(checker[j] == true) pw.print("\t");
						pw.print("\t\tP-value");
						for(j=0 ; j<n_category ; j++) if(checker[j] == false)  pw.print("\t"+title[j]);
						pw.println();
						for(j=0 ; j<tmpRmatrix.getRowDimension() ; j++){
							if(checker[j] == false){
								pw.print(title[j]);
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == false) 
										pw.print("\t"+tmpRmatrix.getEntry(j, k));
								for(k=0 ; k<tmpRmatrix.getColumnDimension() ; k++)
									if(checker[k] == true)  pw.print("\t");
								pw.print("\t\t"+title[j]);
								for(k=0 ; k<tmpPmatrix.getColumnDimension() ; k++) 
									if(checker[k] == false) 
										pw.print("\t"+tmpPmatrix.getEntry(j, k));
								pw.println();
							}
						}
						pw.println();
					}
				}
			}
			pw.close();
		}catch(IOException e) {}	
	}
	
	public int[] rearrangeListGenerator(int startYear, int endYear, ArrayList<ArrayList<Integer>> rearrangeList){
		int i, j;
		int start = 1998;
		int end = 2011;
		int year;
		int[] marker;
		int transition01 = 2001;
		int transition02 = 2009;
		int span = endYear - startYear +1;
		ArrayList<Integer> tmpList;
		
		int[] tmpMarker = {12, 12, 12, 0, 0, 0, 0, 0, 0, 20, 20, 0, 0, 0};
		
		int[] alternate01 = {0,1,2,3,4,5,6,7,8,8,9,10,10,11,11,11,11,11,11,11};
		int[] alternate02 = {0,2,3,4,4,5,6,8,7,9,10,11,12,12,13,14,15,16,17,18,19};
		
		marker = new int[span];
		
		for(i=0 ; i<span; i++){
			year = startYear - start + i;
			marker[i] = tmpMarker[year];
			if(tmpMarker[year] == 0) rearrangeList.add(null);
			else if(tmpMarker[year] > 0){
				tmpList = new ArrayList<Integer>();
				if(startYear+i < transition01) for(j=0 ; j<alternate01.length ; j++) tmpList.add(alternate01[j]);
				else if(startYear+i < transition02) for(j=0 ; j<alternate02.length ; j++) tmpList.add(alternate02[j]);
				else System.err.println("rearrange list generate error.");
				rearrangeList.add(tmpList);
			}
		}
		
		return marker;
	}
	
	public void analyzePearsonCorrelations(SocietyIndustryRelationshipAnalyzer sira, SocietyMicrodataAnalyzer	sma, IndustryEntropyCalculator	iec){
		
		System.out.print("Correlation analyzing: ");
		System.out.print("national  ");
		sira.correlationPearsonsNation(sma, iec);
		System.out.print("regional  ");
		sira.correlationPearsonsRegion(sma, iec);
		sira.correlationPearsonsRegionTimeSeries(sma, iec);
		System.out.print("categorial ");
		sira.correlationPearsonsRegionIndustry(sma, iec);
		System.out.println("complete");	
	}
	
	public void analyzePearsonCategorialCorrelations(SocietyIndustryRelationshipAnalyzer sira, SocietyMicrodataAnalyzer	sma, IndustryEntropyCalculator	iec){
	
		System.out.print("Categorial Correlation analyzing: ");
		System.out.print("categorial\t");
		sira.correlationPearsonsCategory(sma, iec);
		System.out.print("region  categorial\t");
		sira.correlationPearsonsRegionCategory(sma, iec);
		System.out.print("categorial region\t");
		sira.correlationPearsonsCategoryRegion(sma, iec);
		System.out.println("complete");	
	}
	
	public void analyzeSpearmanCorrelations(SocietyIndustryRelationshipAnalyzer sira, SocietyMicrodataAnalyzer	sma, IndustryEntropyCalculator	iec){
		
		System.out.print("Correlation analyzing: ");
		System.out.print("national  ");
		sira.correlationSpearmansNation(sma, iec);
		System.out.print("regional  ");
		sira.correlationSpearmansRegion(sma, iec);
		sira.correlationSpearmansRegionTimeSeries(sma, iec);
	}
	
	public static void main(String[] args) {

		SocietyMicrodataAnalyzer	sma;
		IndustryEntropyCalculator	iec;
		SocietyIndustryRelationshipAnalyzer sira;
		
		/*** common properties ***/
		String[] industClassName = {"1st","2nd","3rd","4th","5th"};
		String[] regionClassName = {"do","gun","myun"};
		
		int regionClass	= 0;	   	 		//0: 시도,    1: 시군구,   2: 읍면동
		int startYear		= 1998;
		int endYear			= 2011;
		int minAge			= 0;
		int maxAge			= 0;
		
		/*** society data properties ***/
		int n_category				= 9;
		int gradeDepth			= 5;

		double[] gradeWeight = {2,1,0,-1,-2};
		
		String societyFilePath = "/Users/Jemyung/Desktop/Research/data_storage/society/";
		String locationCodePath = societyFilePath+"location_code/";
		String locationCodeFile = "location_code.txt";
		String correlationFilePath = societyFilePath + "correlation/" +minAge+"_"+maxAge+"/";
		String categorizedCorrelationFilePath = societyFilePath + "correlation/categorized/" +minAge+"_"+maxAge+"/";
		
		
		/*** industry data properties ***/
		int categoryClass	= 0;		//0:대분류, 1: 중분류,  2: 소분류,   3: 세분류,	4:세세분
		int industryClass		= 3;		//0:대분류, 1: 중분류,  2: 소분류,   3: 세분류,	4:세세분
		int n_group				= 20;
		int sizeGroups			= n_group;
		int levelGroups		= n_group;
		int ageGroups			= n_group;
		
		String industryFilePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
		String indusrtyProfitFile = industryFilePath + "profit/industry_profit.txt";
		/////////
		correlationFilePath = societyFilePath + "correlation/" +"errorIncluded/";
		
		/*** file path definition ***/
		String fileTag = startYear+"-"+endYear+"_"+minAge+"-"+maxAge+"_"+industClassName[categoryClass]
								 +"_"+industClassName[industryClass]+"_"+regionClassName[regionClass]+"_"+n_group;
		String totalCorrelationFile = correlationFilePath+"TotalCor_"+fileTag+".txt";
		String regionalCorrelationFile = correlationFilePath+"RegCor_"+fileTag+".txt";
		String regionalTimeSeriesCorrelationFile = correlationFilePath+"RegTimeCor_"+fileTag+".txt";
		String regionalCategorialCorrelationFile = correlationFilePath+"RegCatCor_"+fileTag+".txt";
	//////////
		//regionalCategorialCorrelationFile = categorizedCorrelationFilePath+"RegCatCor_"+fileTag+".txt";
		String categorialCorrelationFile = categorizedCorrelationFilePath+"CategoryCor_"+fileTag+".txt";
		String categorialReionalCorrelationFile = categorizedCorrelationFilePath+"CategoryRegCor_"+fileTag+".txt";
		
		String totalSpearmansCorrelationFile = correlationFilePath+"TotalSpeCor_"+fileTag+".txt";
		String regionalSpearmansCorrelationFile = correlationFilePath+"RegSpeCor_"+fileTag+".txt";
		String regionalTimeSeriesSpearmansCorrelationFile = correlationFilePath+"RegTimeSpeCor_"+fileTag+".txt";

		/*** declare ***/
		sira = new SocietyIndustryRelationshipAnalyzer(startYear, endYear);
		sma = new SocietyMicrodataAnalyzer(n_category, regionClass, gradeDepth, startYear, endYear, minAge, maxAge);
		iec = new IndustryEntropyCalculator(startYear, endYear, categoryClass, industryClass, regionClass, sizeGroups, levelGroups, ageGroups);
		
		/*** rearrange data properties ***/
		int[] marker;
		ArrayList<ArrayList<Integer>> rearrangeList  = new ArrayList<ArrayList<Integer>>();
		marker = sira.rearrangeListGenerator(startYear, endYear, rearrangeList);
		////////////
		/*** correlation analysis process ***/	
		sira.proceedSocietyDataAnalysis(sma,  gradeWeight, societyFilePath, locationCodePath, locationCodeFile);
		sira.proceedIndustryDataAnalysis(iec, industryFilePath, indusrtyProfitFile);
//		sira.proceedIndustryDataAnalysis(iec, industryFilePath, indusrtyProfitFile, marker, rearrangeList, sma.industryCode, sma.industryName);
		System.out.println();
		
		
		sira.analyzePearsonCorrelations(sira, sma, iec);
		
		System.out.print("Correlation results printing: ");
		System.out.print("national  ");
		sira.printNationalCorrlation(totalCorrelationFile);
		System.out.print("regional  ");
		sira.printRegionalCorrelation(regionalCorrelationFile);
		sira.printRegionalTimeSeriesCorrelation(regionalTimeSeriesCorrelationFile, iec);
		System.out.print("categorial  ");
		sira.printRegionalCategorialCorrelation(regionalCategorialCorrelationFile, iec);
		System.out.println("complete");	
		
		
		/***
		sira.analyzePearsonCategorialCorrelations(sira, sma, iec);
		
		System.out.print("Categorial Correlation results printing: ");
		System.out.print("national\t ");
		sira.printCategorialCorrelation(categorialCorrelationFile);
		System.out.print("regional categorial\t");
		sira.printRegionalCategorialCorrelation(regionalCategorialCorrelationFile, iec);
		System.out.print("categorial  regional\t");
		sira.printCategorialRegionalCorrelation(categorialReionalCorrelationFile, iec);
		System.out.println("complete");	
		***/
		
		/***
		sira.analyzeSpearmanCorrelations(sira, sma, iec);
		
		System.out.print("Correlation results printing: ");
		System.out.print("national  ");
		sira.printNationalCorrlation(totalSpearmansCorrelationFile);
		System.out.print("regional  ");
		sira.printRegionalCorrelation(regionalSpearmansCorrelationFile);
		sira.printRegionalTimeSeriesCorrelation(regionalTimeSeriesSpearmansCorrelationFile, iec);
		System.out.println("complete");	
		***/
		
		System.out.println();
		System.out.println("all processes is completed");
		
		/***
		int k, l;
		int classNumber;
		int[] indClass, grpClass;
		int[] youngest, oldest;
		
		youngest = new int[] {60, 70, 80, 15, 15, 36};
		oldest = new int[] {69, 79, 120, 65, 35, 65};
				
		indClass = new int[]{2, 3, 3, 4};
		grpClass = new int[] {20, 100, 200, 400};
		if(indClass.length>=grpClass.length) classNumber = indClass.length;
		else if(indClass.length<grpClass.length) classNumber = grpClass.length;
		else classNumber = 0;
		
		for(l=0 ; l<youngest.length && youngest.length == oldest.length; l++){
			minAge = youngest[l];
			maxAge = oldest[l];
			if(maxAge == 0) System.out.println("age boundary: all age");
			else System.out.println("age boundary: "+minAge+"-"+maxAge);
			for( k=0 ; k<classNumber; k++){
				if(k<indClass.length) industryClass = indClass[k];		
				if(k<grpClass.length) n_group = grpClass[k];		
				System.out.println("industry class: "+industClassName[indClass[k]]+"\t"+"group size: "+grpClass[k]);
				sizeGroups = n_group;
				levelGroups = n_group;
				ageGroups = n_group;			
				
				fileTag = startYear+"-"+endYear+"_"+minAge+"-"+maxAge+"_"+industClassName[categoryClass]
						 +"_"+industClassName[industryClass]+"_"+regionClassName[regionClass]+"_"+n_group;
				totalCorrelationFile = correlationFilePath+"TotalCor_"+fileTag+".txt";
				regionalCorrelationFile = correlationFilePath+"RegCor_"+fileTag+".txt";
				regionalTimeSeriesCorrelationFile = correlationFilePath+"RegTimeCor_"+fileTag+".txt";
				regionalCategorialCorrelationFile = correlationFilePath+"RegCatCor_"+fileTag+".txt";
	
				regionalCategorialCorrelationFile = categorizedCorrelationFilePath+"RegCatCor_"+fileTag+".txt";
				categorialCorrelationFile = categorizedCorrelationFilePath+"CategoryCor_"+fileTag+".txt";
				categorialReionalCorrelationFile = categorizedCorrelationFilePath+"CategoryRegCor_"+fileTag+".txt";
	
				totalSpearmansCorrelationFile = correlationFilePath+"TotalSpeCor_"+fileTag+".txt";
				regionalSpearmansCorrelationFile = correlationFilePath+"RegSpeCor_"+fileTag+".txt";
				regionalTimeSeriesSpearmansCorrelationFile = correlationFilePath+"RegTimeSpeCor_"+fileTag+".txt";
	
				sira = new SocietyIndustryRelationshipAnalyzer(startYear, endYear);
				sma = new SocietyMicrodataAnalyzer(n_category, regionClass, gradeDepth, startYear, endYear, minAge, maxAge);
				iec = new IndustryEntropyCalculator(startYear, endYear, categoryClass, industryClass, regionClass, sizeGroups, levelGroups, ageGroups);
	
				sira.proceedSocietyDataAnalysis(sma,  gradeWeight, societyFilePath, locationCodePath, locationCodeFile);
				//sira.proceedIndustryDataAnalysis(iec, industryFilePath, indusrtyProfitFile);
				sira.proceedIndustryDataAnalysis(iec, industryFilePath, indusrtyProfitFile, marker, rearrangeList, sma.industryCode, sma.industryName);
				System.out.println();
	
	
				
				sira.analyzePearsonCorrelations(sira, sma, iec);
				
				System.out.print("Correlation results printing: ");
				System.out.print("national  ");
				sira.printNationalCorrlation(totalCorrelationFile);
				System.out.print("regional  ");
				sira.printRegionalCorrelation(regionalCorrelationFile);
				sira.printRegionalTimeSeriesCorrelation(regionalTimeSeriesCorrelationFile, iec);
				System.out.print("categorial  ");
				sira.printRegionalCategorialCorrelation(regionalCategorialCorrelationFile, iec);
				System.out.println("complete");	
				
	
				sira.analyzePearsonCategorialCorrelations(sira, sma, iec);
				
				System.out.print("Categorial Correlation results printing: ");
				System.out.print("national\t ");
				sira.printCategorialCorrelation(categorialCorrelationFile);
				System.out.print("regional categorial\t");
				sira.printRegionalCategorialCorrelation(regionalCategorialCorrelationFile, iec);
				System.out.print("categorial  regional\t");
				sira.printCategorialRegionalCorrelation(categorialReionalCorrelationFile, iec);
				System.out.println("complete");	
				
				
				sira.analyzeSpearmanCorrelations(sira, sma, iec);
				
				System.out.print("Correlation results printing: ");
				System.out.print("national  ");
				sira.printNationalCorrlation(totalSpearmansCorrelationFile);
				System.out.print("regional  ");
				sira.printRegionalCorrelation(regionalSpearmansCorrelationFile);
				sira.printRegionalTimeSeriesCorrelation(regionalTimeSeriesSpearmansCorrelationFile, iec);
				System.out.println("complete");	
				
				
				System.out.println();
				System.out.println("all processes is completed");
			}
		}
	***/
	}
}
