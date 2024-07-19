package industrialDiversity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import regionalDiversity.data.SocietyMicroData;

public class SocietyMicrodataAnalyzer {

	int duration;
	int startYear, endYear;
	int n_category;
	int[] n_region;					//[year]
	int[] n_industry;				//[category]		
	int max_regions;
	int max_industries;
	int minAge, maxAge;		//age boundary
	int regionClass;				//0: 시도,    1: 시군구,   2: 읍면동
	int gradeDepth;				//0:very good, 1:good, 3:normal, 4:bad, 5:very bad
	int regionClassDepth;		//2:si_do,    5: si_gun_gu,   7: eup_myun_dong
	String regionClassName;
	double[] weight;				//[grade depth]; weight of each grade
	
	ArrayList<HashMap<String, String>> locationHashMap;	//<code, name>
	ArrayList<ArrayList<String>> locatoinCode;						//use to find region's index	
	ArrayList<ArrayList<String>> locatoinName;						//use to find region's index	
	ArrayList<ArrayList<String>> industryCode;						//use to find category's index	
	ArrayList<ArrayList<String>> industryName;						//use to find category's index	
	
	
	int[][]	responseExistence;				//[year][category]; 0:region, 1:life, 2:live, 3:income, 4:consume, 5:job, 6:area, 7:age, 8:industry
	
	int[][]   regionSamples;					//[year][region]
	int[][][] lifeSatisfaction;					//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] livingCondition;					//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] incomeSatisfaction;			//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] consumptionSatisfaction;	//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] employmentStability;			//[year][region][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	int[][]   ruralRegionSamples;					//[year][region]
	int[][][] ruralLifeSatisfaction;					//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] ruralLivingCondition;					//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] ruralIncomeSatisfaction;			//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] ruralConsumptionSatisfaction;	//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] ruralEmploymentStability;			//[year][region][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	int[][]   urbanRegionSamples;					//[year][region]
	int[][][] urbanLifeSatisfaction;				//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] urbanLivingCondition;				//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] urbanIncomeSatisfaction;			//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] urbanConsumptionSatisfaction;//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] urbanEmploymentStability;		//[year][region][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	int[]	   nationSamples;								//[year]
	int[][] lifeSatisfactionNation;					//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] livingConditionNation;					//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] incomeSatisfactionNation;			//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] consumptionSatisfactionNation;	//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] employmentStabilityNation;			//[year][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	int[]   ruralSamples;										//[year]
	int[][] ruralLifeSatisfactionNation;					//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] ruralLivingConditionNation;					//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] ruralIncomeSatisfactionNation;			//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] ruralConsumptionSatisfactionNation;	//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] ruralEmploymentStabilityNation;			//[year][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	int[]   urbanSamples;										//[year]
	int[][] urbanLifeSatisfactionNation;				//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] urbanLivingConditionNation;				//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] urbanIncomeSatisfactionNation;			//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] urbanConsumptionSatisfactionNation;//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][] urbanEmploymentStabilityNation;		//[year][grade]; 0:very good, 1:good, 2:a little, 3:not at all

	
	int[][][]   regionSamplesCategorized;						//[year][region][category]
	int[][][][] lifeSatisfactionCategorized;					//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] livingConditionCategorized;					//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] incomeSatisfactionCategorized;				//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] consumptionSatisfactionCategorized;	//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] employmentStabilityCategorized;			//[year][region][category][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	int[][][]   ruralRegionSamplesCategorized;					//[year][region][category]
	int[][][][] ruralLifeSatisfactionCategorized;					//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] ruralLivingConditionCategorized;					//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] ruralIncomeSatisfactionCategorized;				//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] ruralConsumptionSatisfactionCategorized;	//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] ruralEmploymentStabilityCategorized;			//[year][region][category][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	int[][][]   urbanRegionSamplesCategorized;					//[year][region][category]
	int[][][][] urbanLifeSatisfactionCategorized;					//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] urbanLivingConditionCategorized;					//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] urbanIncomeSatisfactionCategorized;			//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] urbanConsumptionSatisfactionCategorized;	//[year][region][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][][] urbanEmploymentStabilityCategorized;			//[year][region][category][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	int[][]	 nationSamplesCategorized;								//[year][category]
	int[][][] lifeSatisfactionNationCategorized;					//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] livingConditionNationCategorized;					//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] incomeSatisfactionNationCategorized;				//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] consumptionSatisfactionNationCategorized;	//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] employmentStabilityNationCategorized;			//[year][category][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	int[][]   ruralSamplesCategorized;											//[year][category]
	int[][][] ruralLifeSatisfactionNationCategorized;					//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] ruralLivingConditionNationCategorized;					//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] ruralIncomeSatisfactionNationCategorized;				//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] ruralConsumptionSatisfactionNationCategorized;	//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] ruralEmploymentStabilityNationCategorized;			//[year][category][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	int[][]   urbanSamplesCategorized;										//[year][category]
	int[][][] urbanLifeSatisfactionNationCategorized;					//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] urbanLivingConditionNationCategorized;					//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] urbanIncomeSatisfactionNationCategorized;			//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] urbanConsumptionSatisfactionNationCategorized;	//[year][category][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	int[][][] urbanEmploymentStabilityNationCategorized;			//[year][category][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	
	double[][][] lifeSatisfactionRate;					//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] livingConditionRate;					//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] incomeSatisfactionRate;				//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] consumptionSatisfactionRate;	//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] employmentStabilityRate;			//[year][region][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	double[][][] ruralLifeSatisfactionRate;					//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] ruralLivingConditionRate;					//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] ruralIncomeSatisfactionRate;				//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] ruralConsumptionSatisfactionRate;	//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] ruralEmploymentStabilityRate;			//[year][region][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	double[][][] urbanLifeSatisfactionRate;					//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] urbanLivingConditionRate;					//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] urbanIncomeSatisfactionRate;			//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] urbanConsumptionSatisfactionRate;	//[year][region][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][][] urbanEmploymentStabilityRate;			//[year][region][grade]; 0:very good, 1:good, 2:a little, 3:not at all

	double[][] lifeSatisfactionNationRate;					//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] livingConditionNationRate;					//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] incomeSatisfactionNationRate;				//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] consumptionSatisfactionNationRate;	//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] employmentStabilityNationRate;			//[year][grade]; 0:very good, 1:good, 2:a little, 3:not at all
	
	double[][] ruralLifeSatisfactionNationRate;					//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] ruralLivingConditionNationRate;					//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] ruralIncomeSatisfactionNationRate;				//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] ruralConsumptionSatisfactionNationRate;	//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] ruralEmploymentStabilityNationRate;			//[year][grade]; 0:very good, 1:good, 2:a little, 3:not at all

	double[][] urbanLifeSatisfactionNationRate;					//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] urbanLivingConditionNationRate;					//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] urbanIncomeSatisfactionNationRate;			//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] urbanConsumptionSatisfactionNationRate;	//[year][grade]; 0:very good, 1:good, 2:normal, 3:bad, 4:very bad
	double[][] urbanEmploymentStabilityNationRate;			//[year][grade]; 0:very good, 1:good, 2:a little, 3:not at all

	
	double[][] lifeSatisfactionScore;									//[year][region]
	double[][] livingConditionScore;									//[year][region]
	double[][] incomeSatisfactionScore;								//[year][region]
	double[][] consumptionSatisfactionScore;					//[year][region]
	double[][] employmentStabilityScore;							//[year][region]
	
	double[][] ruralLifeSatisfactionScore;							//[year][region]
	double[][] ruralLivingConditionScore;							//[year][region]
	double[][] ruralIncomeSatisfactionScore;						//[year][region]
	double[][] ruralConsumptionSatisfactionScore;				//[year][region]
	double[][] ruralEmploymentStabilityScore;					//[year][region]
	
	double[][] urbanLifeSatisfactionScore;							//[year][region]
	double[][] urbanLivingConditionScore;							//[year][region]
	double[][] urbanIncomeSatisfactionScore;					//[year][region]
	double[][] urbanConsumptionSatisfactionScore;			//[year][region]
	double[][] urbanEmploymentStabilityScore;					//[year][region]
	
	double[] lifeSatisfactionScoreNation;							//[year]
	double[] livingConditionScoreNation;							//[year]
	double[] incomeSatisfactionScoreNation;						//[year]
	double[] consumptionSatisfactionScoreNation;			//[year]
	double[] employmentStabilityScoreNation;					//[year]

	double[] lifeSatisfactionScoreRural;								//[year]
	double[] livingConditionScoreRural;								//[year]
	double[] incomeSatisfactionScoreRural;						//[year]
	double[] consumptionSatisfactionScoreRural;				//[year]
	double[] employmentStabilityScoreRural;						//[year]
	
	double[] lifeSatisfactionScoreUrban;								//[year]
	double[] livingConditionScoreUrban;								//[year]
	double[] incomeSatisfactionScoreUrban;						//[year]
	double[] consumptionSatisfactionScoreUrban;				//[year]
	double[] employmentStabilityScoreUrban;					//[year]
	
	
	double[][][] lifeSatisfactionScoreCategorized;								//[year][region][category]
	double[][][] livingConditionScoreCategorized;								//[year][region][category]
	double[][][] incomeSatisfactionScoreCategorized;						//[year][region][category]
	double[][][] consumptionSatisfactionScoreCategorized;				//[year][region][category]
	double[][][] employmentStabilityScoreCategorized;						//[year][region][category]
	
	double[][][] ruralLifeSatisfactionScoreCategorized;						//[year][region][category]
	double[][][] ruralLivingConditionScoreCategorized;						//[year][region][category]
	double[][][] ruralIncomeSatisfactionScoreCategorized;				//[year][region][category]
	double[][][] ruralConsumptionSatisfactionScoreCategorized;		//[year][region][category]
	double[][][] ruralEmploymentStabilityScoreCategorized;				//[year][region][category]
	
	double[][][] urbanLifeSatisfactionScoreCategorized;					//[year][region][category]
	double[][][] urbanLivingConditionScoreCategorized;					//[year][region][category]
	double[][][] urbanIncomeSatisfactionScoreCategorized;				//[year][region][category]
	double[][][] urbanConsumptionSatisfactionScoreCategorized;	//[year][region][category]
	double[][][] urbanEmploymentStabilityScoreCategorized;			//[year][region][category]
	
	double[][] lifeSatisfactionScoreNationCategorized;						//[year][category]
	double[][] livingConditionScoreNationCategorized;						//[year][category]
	double[][] incomeSatisfactionScoreNationCategorized;				//[year][category]
	double[][] consumptionSatisfactionScoreNationCategorized;		//[year][category]
	double[][] employmentStabilityScoreNationCategorized;				//[year][category]

	double[][] lifeSatisfactionScoreRuralCategorized;							//[year][category]
	double[][] livingConditionScoreRuralCategorized;						//[year][category]
	double[][] incomeSatisfactionScoreRuralCategorized;					//[year][category]
	double[][] consumptionSatisfactionScoreRuralCategorized;		//[year][category]
	double[][] employmentStabilityScoreRuralCategorized;				//[year][category]
	
	double[][] lifeSatisfactionScoreUrbanCategorized;						//[year][category]
	double[][] livingConditionScoreUrbanCategorized;						//[year][category]
	double[][] incomeSatisfactionScoreUrbanCategorized;				//[year][category]
	double[][] consumptionSatisfactionScoreUrbanCategorized;		//[year][category]
	double[][] employmentStabilityScoreUrbanCategorized;				//[year][category]
	
	public SocietyMicrodataAnalyzer(int category, int regionClass, int gradeDepth, int start, int end){
		this.minAge = 0;
		this.maxAge = 0;	
		this.n_category = category;
		this.regionClass = regionClass;
		this.gradeDepth = gradeDepth;
		this.startYear = start;
		this.endYear = end;
		this.duration = this.endYear - this.startYear + 1;
		this.n_region = new int[this.duration];
		
		this.setClassDepth(regionClass);
		this.setIndexMap();
	}
	
	public SocietyMicrodataAnalyzer(int category, int regionClass, int gradeDepth, int start, int end, int lowAgeBoundary, int upperAgeBoundary){
		this.minAge = lowAgeBoundary;
		this.maxAge = upperAgeBoundary;		
		this.n_category = category;
		this.regionClass = regionClass;
		this.gradeDepth = gradeDepth;
		this.startYear = start;
		this.endYear = end;
		this.duration = this.endYear - this.startYear + 1;
		this.n_region = new int[this.duration];
		this.n_industry = new int[this.duration];
		
		this.setClassDepth(regionClass);
		this.setIndexMap();
	}
	
	public void setIndexMap(){
		
		this.locationHashMap = new ArrayList<HashMap<String, String>>();	
		this.locatoinCode = new ArrayList<ArrayList<String>>();		
		this.locatoinName = new ArrayList<ArrayList<String>>();		
		this.industryCode = new ArrayList<ArrayList<String>>();	
		this.industryName = new ArrayList<ArrayList<String>>();
	}
	
	public void setClassDepth(int regionClass){
		int[] regionClassKey = {2,5,7};
		String[] regionClassName = {"do","gun","myun"};
		
		this.regionClassDepth = regionClassKey[regionClass];
		this.regionClassName = regionClassName[regionClass];
	}
	
	public void setIndustryCode(){
		int i,j;
		
		int transition01 = 2001;
		int transition02 = 2009;
		
		String[] industrCode_type01 = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
		String[] industrCode_type02 = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"};
		String[] industrCode_type03 = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U"};
		
		String[] industryName_type01 = {"농.임업","어업","광업","제조업","전기가스수도업","건설업","도.소매업","숙박 및 음식점업","운수","창고 및 통신업","금융 및 보험업","부동산 임대 및 사업서비스업","기타"};
		String[] industryName_type02 = {"농업및임업(01-02)","어업(05)","광업(10-12)","제조업(15-37)",
															  "전기,가스및수도사업(40-41)","건설업(45-46)","도소매업(50-52)",
															  "숙박및음식점업(55)","운수업(60-63)","통신업(64)","금융및보험업(65-67)",
															  "부동산업및임대업(70-71)","사업서비스업(72-75)","공공행정,국방및사회보장행정(76)",
															  "교육서비스업(80)","보건및사회복지사업(85-86)","오락,문화및운동관련산업(87-88)",
															  "기타공공,수리및개인서비스업(90-93)","가사서비스업(95)","국제및외국기관(99)"};
		String[] industryName_type03 = {"농업, 임업 및 어업","광업","제조업","전기, 가스, 증기 및 수도사업",
												 			  "하수 · 폐기물 처리, 원료재생 및 환경복원업","건설업","도매 및 소매업",
												 			  "운수업","숙박 및 음식점업","출판, 영상, 방송통신 및 정보서비스업","금융 및 보험업",
												 			  "부동산업 및 임대업","전문, 과학 및 기술 서비스업","사업시설관리 및 사업지원 서비스업",
												 			  "공공행정, 국방 및 사회보장 행정","교육 서비스업","보건업 및 사회복지 서비스업",
												 			  "예술, 스포츠 및 여가관련 서비스업","협회 및 단체, 수리  및 기타 개인 서비스업",
												 			  "가구내 고용활동 및 달리 분류되지 않은 자가소비 생산활동","국제 및 외국기관"};	
		
		ArrayList<String> tmpCodeList, tmpNameList;
		
		for(i=0 ; i<this.duration ; i++){
			tmpCodeList = new ArrayList<String>();
			tmpNameList = new ArrayList<String>();
			if(i+this.startYear < transition01){
				this.max_industries = industrCode_type01.length;
				this.n_industry[i] =  industrCode_type01.length;
				for(j=0 ; j<industrCode_type01.length ; j++){
					tmpCodeList.add(industrCode_type01[j]);
					tmpNameList.add(industryName_type01[j]);
				}
				this.industryCode.add(tmpCodeList);
				this.industryName.add(tmpNameList);
			}else if(i+this.startYear < transition02 && i+this.startYear >= transition01){
				this.max_industries = industrCode_type02.length;
				this.n_industry[i] =  industrCode_type02.length;
				for(j=0 ; j<industrCode_type02.length ; j++){
					tmpCodeList.add(industrCode_type02[j]);
					tmpNameList.add(industryName_type02[j]);
				}
				this.industryCode.add(tmpCodeList);
				this.industryName.add(tmpNameList);
			}else if(i+this.startYear >= transition02){
				this.max_industries = industrCode_type03.length;
				this.n_industry[i] =  industrCode_type03.length;
				for(j=0 ; j<industrCode_type03.length ; j++){
					tmpCodeList.add(industrCode_type03[j]);
					tmpNameList.add(industryName_type03[j]);
				}
				this.industryCode.add(tmpCodeList);
				this.industryName.add(tmpNameList);
			}else{
				System.err.println("category code lists over years");
			}
		}
	}
	
	public void initiate(){
		int i, j, k;
		
		this.responseExistence = new int[this.duration][this.n_category];
		this.regionSamples = new int[this.duration][this.max_regions];
		this.lifeSatisfaction = new int[this.duration][this.max_regions][this.gradeDepth];
		this.livingCondition = new int[this.duration][this.max_regions][this.gradeDepth];
		this.incomeSatisfaction = new int[this.duration][this.max_regions][this.gradeDepth];
		this.consumptionSatisfaction = new int[this.duration][this.max_regions][this.gradeDepth];
		this.employmentStability = new int[this.duration][this.max_regions][this.gradeDepth];
		
		this.ruralRegionSamples = new int[this.duration][this.max_regions];
		this.ruralLifeSatisfaction = new int[this.duration][this.max_regions][this.gradeDepth];
		this.ruralLivingCondition = new int[this.duration][this.max_regions][this.gradeDepth];
		this.ruralIncomeSatisfaction = new int[this.duration][this.max_regions][this.gradeDepth];
		this.ruralConsumptionSatisfaction = new int[this.duration][this.max_regions][this.gradeDepth];
		this.ruralEmploymentStability = new int[this.duration][this.max_regions][this.gradeDepth];
		
		this.urbanRegionSamples = new int[this.duration][this.max_regions];
		this.urbanLifeSatisfaction = new int[this.duration][this.max_regions][this.gradeDepth];
		this.urbanLivingCondition = new int[this.duration][this.max_regions][this.gradeDepth];
		this.urbanIncomeSatisfaction = new int[this.duration][this.max_regions][this.gradeDepth];
		this.urbanConsumptionSatisfaction = new int[this.duration][this.max_regions][this.gradeDepth];
		this.urbanEmploymentStability = new int[this.duration][this.max_regions][this.gradeDepth];
	
		this.nationSamples = new int[this.duration];
		this.lifeSatisfactionNation = new int[this.duration][this.gradeDepth];
		this.livingConditionNation = new int[this.duration][this.gradeDepth];				
		this.incomeSatisfactionNation = new int[this.duration][this.gradeDepth];		
		this.consumptionSatisfactionNation = new int[this.duration][this.gradeDepth];
		this. employmentStabilityNation = new int[this.duration][this.gradeDepth];
		
		this.ruralSamples = new int[this.duration];
		this.ruralLifeSatisfactionNation = new int[this.duration][this.gradeDepth];
		this.ruralLivingConditionNation = new int[this.duration][this.gradeDepth];
		this.ruralIncomeSatisfactionNation = new int[this.duration][this.gradeDepth];
		this.ruralConsumptionSatisfactionNation = new int[this.duration][this.gradeDepth];
		this.ruralEmploymentStabilityNation = new int[this.duration][this.gradeDepth];
		
		this.urbanSamples = new int[this.duration];
		this.urbanLifeSatisfactionNation = new int[this.duration][this.gradeDepth];
		this.urbanLivingConditionNation = new int[this.duration][this.gradeDepth];
		this.urbanIncomeSatisfactionNation = new int[this.duration][this.gradeDepth];
		this.urbanConsumptionSatisfactionNation = new int[this.duration][this.gradeDepth];
		this.urbanEmploymentStabilityNation = new int[this.duration][this.gradeDepth];

		
		this.lifeSatisfactionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.livingConditionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.incomeSatisfactionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.consumptionSatisfactionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.employmentStabilityRate = new double[this.duration][this.max_regions][this.gradeDepth];
		
		this.ruralLifeSatisfactionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.ruralLivingConditionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.ruralIncomeSatisfactionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.ruralConsumptionSatisfactionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.ruralEmploymentStabilityRate = new double[this.duration][this.max_regions][this.gradeDepth];
		
		this.urbanLifeSatisfactionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.urbanLivingConditionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.urbanIncomeSatisfactionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.urbanConsumptionSatisfactionRate = new double[this.duration][this.max_regions][this.gradeDepth];
		this.urbanEmploymentStabilityRate = new double[this.duration][this.max_regions][this.gradeDepth];

		this.lifeSatisfactionNationRate = new double[this.duration][this.gradeDepth];
		this.livingConditionNationRate = new double[this.duration][this.gradeDepth];				
		this.incomeSatisfactionNationRate = new double[this.duration][this.gradeDepth];		
		this.consumptionSatisfactionNationRate = new double[this.duration][this.gradeDepth];
		this. employmentStabilityNationRate = new double[this.duration][this.gradeDepth];
		
		this.ruralLifeSatisfactionNationRate = new double[this.duration][this.gradeDepth];
		this.ruralLivingConditionNationRate = new double[this.duration][this.gradeDepth];
		this.ruralIncomeSatisfactionNationRate = new double[this.duration][this.gradeDepth];
		this.ruralConsumptionSatisfactionNationRate = new double[this.duration][this.gradeDepth];
		this.ruralEmploymentStabilityNationRate = new double[this.duration][this.gradeDepth];
		
		this.urbanLifeSatisfactionNationRate = new double[this.duration][this.gradeDepth];
		this.urbanLivingConditionNationRate = new double[this.duration][this.gradeDepth];
		this.urbanIncomeSatisfactionNationRate = new double[this.duration][this.gradeDepth];
		this.urbanConsumptionSatisfactionNationRate = new double[this.duration][this.gradeDepth];
		this.urbanEmploymentStabilityNationRate = new double[this.duration][this.gradeDepth];

		
		this.lifeSatisfactionScore = new double[this.duration][this.max_regions];
		this.livingConditionScore = new double[this.duration][this.max_regions];
		this.incomeSatisfactionScore = new double[this.duration][this.max_regions];
		this.consumptionSatisfactionScore = new double[this.duration][this.max_regions];
		this.employmentStabilityScore = new double[this.duration][this.max_regions];
		
		this.ruralLifeSatisfactionScore = new double[this.duration][this.max_regions];
		this.ruralLivingConditionScore = new double[this.duration][this.max_regions];
		this.ruralIncomeSatisfactionScore = new double[this.duration][this.max_regions];
		this.ruralConsumptionSatisfactionScore = new double[this.duration][this.max_regions];
		this.ruralEmploymentStabilityScore = new double[this.duration][this.max_regions];
		
		this.urbanLifeSatisfactionScore = new double[this.duration][this.max_regions];
		this.urbanLivingConditionScore = new double[this.duration][this.max_regions];
		this.urbanIncomeSatisfactionScore = new double[this.duration][this.max_regions];
		this.urbanConsumptionSatisfactionScore = new double[this.duration][this.max_regions];
		this.urbanEmploymentStabilityScore = new double[this.duration][this.max_regions];
		
		this.lifeSatisfactionScoreNation = new double[this.duration];
		this.livingConditionScoreNation = new double[this.duration];
		this.incomeSatisfactionScoreNation = new double[this.duration];
		this.consumptionSatisfactionScoreNation = new double[this.duration];
		this.employmentStabilityScoreNation = new double[this.duration];
		
		this.lifeSatisfactionScoreRural = new double[this.duration];
		this.livingConditionScoreRural = new double[this.duration];
		this.incomeSatisfactionScoreRural = new double[this.duration];
		this.consumptionSatisfactionScoreRural = new double[this.duration];
		this.employmentStabilityScoreRural = new double[this.duration];
		
		this.lifeSatisfactionScoreUrban = new double[this.duration];
		this.livingConditionScoreUrban = new double[this.duration];
		this.incomeSatisfactionScoreUrban = new double[this.duration];
		this.consumptionSatisfactionScoreUrban = new double[this.duration];
		this.employmentStabilityScoreUrban = new double[this.duration];
		
		for(i=0 ; i<this.duration ; i++){
			this.lifeSatisfactionScoreNation[i] = 0;
			this.livingConditionScoreNation[i] = 0;
			this.incomeSatisfactionScoreNation[i] = 0;
			this.consumptionSatisfactionScoreNation[i] = 0;
			this.employmentStabilityScoreNation[i] = 0;
			
			this.lifeSatisfactionScoreRural[i] = 0;
			this.livingConditionScoreRural[i] = 0;
			this.incomeSatisfactionScoreRural[i] = 0;
			this.consumptionSatisfactionScoreRural[i] = 0;
			this.employmentStabilityScoreRural[i] = 0;
			
			this.lifeSatisfactionScoreUrban[i] = 0;
			this.livingConditionScoreUrban[i] = 0;
			this.incomeSatisfactionScoreUrban[i] = 0;
			this.consumptionSatisfactionScoreUrban[i] = 0;
			this.employmentStabilityScoreUrban[i] = 0;
			
			for(k=0 ; k<this.gradeDepth ; k++){
					this.lifeSatisfactionNation[i][k] = 0;
					this.livingConditionNation[i][k] = 0;
					this.incomeSatisfactionNation[i][k] = 0;
					this.consumptionSatisfactionNation[i][k] = 0;
					this.employmentStabilityNation[i][k] = 0;
					
					this.urbanLifeSatisfactionNation[i][k] = 0;
					this.urbanLivingConditionNation[i][k] = 0;
					this.urbanIncomeSatisfactionNation[i][k] = 0;
					this.urbanConsumptionSatisfactionNation[i][k] = 0;
					this.urbanEmploymentStabilityNation[i][k] = 0;
					
					this.ruralLifeSatisfactionNation[i][k] = 0;
					this.ruralLivingConditionNation[i][k] = 0;
					this.ruralIncomeSatisfactionNation[i][k] = 0;
					this.ruralConsumptionSatisfactionNation[i][k] = 0;
					this.ruralEmploymentStabilityNation[i][k] = 0;
			}
		}
		
		for(i=0 ; i<this.duration ; i++){		
			this.nationSamples[i] = 0;
			this.ruralSamples[i] = 0;
			this.urbanSamples[i] = 0;
			
			for(j=0 ; j<this.n_region[i] ; j++){
				this.regionSamples[i][j] = 0;
				this.ruralRegionSamples[i][j] = 0;
				this.urbanRegionSamples[i][j] = 0;
				
				this.lifeSatisfactionScore[i][j] = 0;
				this.livingConditionScore[i][j] = 0;
				this.incomeSatisfactionScore[i][j] = 0;
				this.consumptionSatisfactionScore[i][j] = 0;
				this.employmentStabilityScore[i][j] = 0;
				
				this.ruralLifeSatisfactionScore[i][j] = 0;
				this.ruralLivingConditionScore[i][j] = 0;
				this.ruralIncomeSatisfactionScore[i][j] = 0;
				this.ruralConsumptionSatisfactionScore[i][j] = 0;
				this.ruralEmploymentStabilityScore[i][j] = 0;
				
				this.urbanLifeSatisfactionScore[i][j] = 0;
				this.urbanLivingConditionScore[i][j] = 0;
				this.urbanIncomeSatisfactionScore[i][j] = 0;
				this.urbanConsumptionSatisfactionScore[i][j] = 0;
				this.urbanEmploymentStabilityScore[i][j] = 0;
				
				for(k=0 ; k<this.gradeDepth ; k++){
					this.lifeSatisfaction[i][j][k] = 0;
					this.livingCondition[i][j][k] = 0;
					this.incomeSatisfaction[i][j][k] = 0;
					this.consumptionSatisfaction[i][j][k] = 0;
					this.employmentStability[i][j][k] = 0;
					
					this.ruralLifeSatisfaction[i][j][k] = 0;
					this.ruralLivingCondition[i][j][k] = 0;
					this.ruralIncomeSatisfaction[i][j][k] = 0;
					this.ruralConsumptionSatisfaction[i][j][k] = 0;
					this.ruralEmploymentStability[i][j][k] = 0;
					
					this.urbanLifeSatisfaction[i][j][k] = 0;
					this.urbanLivingCondition[i][j][k] = 0;
					this.urbanIncomeSatisfaction[i][j][k] = 0;
					this.urbanConsumptionSatisfaction[i][j][k] = 0;
					this.urbanEmploymentStability[i][j][k] = 0;
				}
			}
		}
	}
	
	public void initiateCategorizedVariables(){
		int i, j, k, l;
		
		this.regionSamplesCategorized = new int[this.duration][this.max_regions][this.max_industries];
		this.lifeSatisfactionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.livingConditionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.incomeSatisfactionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.consumptionSatisfactionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.employmentStabilityCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		
		this.ruralRegionSamplesCategorized = new int[this.duration][this.max_regions][this.max_industries];
		this.ruralLifeSatisfactionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.ruralLivingConditionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.ruralIncomeSatisfactionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.ruralConsumptionSatisfactionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.ruralEmploymentStabilityCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		
		this.urbanRegionSamplesCategorized = new int[this.duration][this.max_regions][this.max_industries];
		this.urbanLifeSatisfactionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.urbanLivingConditionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.urbanIncomeSatisfactionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.urbanConsumptionSatisfactionCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
		this.urbanEmploymentStabilityCategorized = new int[this.duration][this.max_regions][this.max_industries][this.gradeDepth];
	
		this.nationSamplesCategorized = new int[this.duration][this.max_industries];
		this.lifeSatisfactionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		this.livingConditionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];				
		this.incomeSatisfactionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];		
		this.consumptionSatisfactionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		this.employmentStabilityNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		
		this.ruralSamplesCategorized = new int[this.duration][this.max_industries];
		this.ruralLifeSatisfactionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		this.ruralLivingConditionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		this.ruralIncomeSatisfactionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		this.ruralConsumptionSatisfactionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		this.ruralEmploymentStabilityNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		
		this.urbanSamplesCategorized = new int[this.duration][this.max_industries];
		this.urbanLifeSatisfactionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		this.urbanLivingConditionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		this.urbanIncomeSatisfactionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		this.urbanConsumptionSatisfactionNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];
		this.urbanEmploymentStabilityNationCategorized = new int[this.duration][this.max_industries][this.gradeDepth];

		
		this.lifeSatisfactionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.livingConditionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.incomeSatisfactionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.consumptionSatisfactionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.employmentStabilityScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		
		this.ruralLifeSatisfactionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.ruralLivingConditionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.ruralIncomeSatisfactionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.ruralConsumptionSatisfactionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.ruralEmploymentStabilityScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		
		this.urbanLifeSatisfactionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.urbanLivingConditionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.urbanIncomeSatisfactionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.urbanConsumptionSatisfactionScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		this.urbanEmploymentStabilityScoreCategorized = new double[this.duration][this.max_regions][this.max_industries];
		
		this.lifeSatisfactionScoreNationCategorized = new double[this.duration][this.max_industries];
		this.livingConditionScoreNationCategorized = new double[this.duration][this.max_industries];
		this.incomeSatisfactionScoreNationCategorized = new double[this.duration][this.max_industries];
		this.consumptionSatisfactionScoreNationCategorized = new double[this.duration][this.max_industries];
		this.employmentStabilityScoreNationCategorized = new double[this.duration][this.max_industries];
		
		this.lifeSatisfactionScoreRuralCategorized = new double[this.duration][this.max_industries];
		this.livingConditionScoreRuralCategorized = new double[this.duration][this.max_industries];
		this.incomeSatisfactionScoreRuralCategorized = new double[this.duration][this.max_industries];
		this.consumptionSatisfactionScoreRuralCategorized = new double[this.duration][this.max_industries];
		this.employmentStabilityScoreRuralCategorized = new double[this.duration][this.max_industries];
		
		this.lifeSatisfactionScoreUrbanCategorized = new double[this.duration][this.max_industries];
		this.livingConditionScoreUrbanCategorized = new double[this.duration][this.max_industries];
		this.incomeSatisfactionScoreUrbanCategorized = new double[this.duration][this.max_industries];
		this.consumptionSatisfactionScoreUrbanCategorized = new double[this.duration][this.max_industries];
		this.employmentStabilityScoreUrbanCategorized = new double[this.duration][this.max_industries];
		
		for(i=0 ; i<this.duration ; i++){
			for(j=0 ; j<this.max_industries ; j++){
				this.lifeSatisfactionScoreNationCategorized[i][j] = 0;
				this.livingConditionScoreNationCategorized[i][j] = 0;
				this.incomeSatisfactionScoreNationCategorized[i][j] = 0;
				this.consumptionSatisfactionScoreNationCategorized[i][j] = 0;
				this.employmentStabilityScoreNationCategorized[i][j] = 0;
				
				this.lifeSatisfactionScoreRuralCategorized[i][j] = 0;
				this.livingConditionScoreRuralCategorized[i][j] = 0;
				this.incomeSatisfactionScoreRuralCategorized[i][j] = 0;
				this.consumptionSatisfactionScoreRuralCategorized[i][j] = 0;
				this.employmentStabilityScoreRuralCategorized[i][j] = 0;
				
				this.lifeSatisfactionScoreUrbanCategorized[i][j] = 0;
				this.livingConditionScoreUrbanCategorized[i][j] = 0;
				this.incomeSatisfactionScoreUrbanCategorized[i][j] = 0;
				this.consumptionSatisfactionScoreUrbanCategorized[i][j] = 0;
				this.employmentStabilityScoreUrbanCategorized[i][j] = 0;
				
				for(k=0 ; k<this.gradeDepth ; k++){
						this.lifeSatisfactionNationCategorized[i][j][k] = 0;
						this.livingConditionNationCategorized[i][j][k] = 0;
						this.incomeSatisfactionNationCategorized[i][j][k] = 0;
						this.consumptionSatisfactionNationCategorized[i][j][k] = 0;
						this.employmentStabilityNationCategorized[i][j][k] = 0;
						
						this.urbanLifeSatisfactionNationCategorized[i][j][k] = 0;
						this.urbanLivingConditionNationCategorized[i][j][k] = 0;
						this.urbanIncomeSatisfactionNationCategorized[i][j][k] = 0;
						this.urbanConsumptionSatisfactionNationCategorized[i][j][k] = 0;
						this.urbanEmploymentStabilityNationCategorized[i][j][k] = 0;
						
						this.ruralLifeSatisfactionNationCategorized[i][j][k] = 0;
						this.ruralLivingConditionNationCategorized[i][j][k] = 0;
						this.ruralIncomeSatisfactionNationCategorized[i][j][k] = 0;
						this.ruralConsumptionSatisfactionNationCategorized[i][j][k] = 0;
						this.ruralEmploymentStabilityNationCategorized[i][j][k] = 0;
				}
			}
		}
		
		for(i=0 ; i<this.duration ; i++){	
			for(l=0 ; l<this.max_industries ; l++){
				this.nationSamplesCategorized[i][l] = 0;
				this.ruralSamplesCategorized[i][l] = 0;
				this.urbanSamplesCategorized[i][l] = 0;
				
				for(j=0 ; j<this.n_region[i] ; j++){
					this.regionSamplesCategorized[i][j][l] = 0;
					this.ruralRegionSamplesCategorized[i][j][l] = 0;
					this.urbanRegionSamplesCategorized[i][j][l] = 0;
					
					this.lifeSatisfactionScoreCategorized[i][j][l] = 0;
					this.livingConditionScoreCategorized[i][j][l] = 0;
					this.incomeSatisfactionScoreCategorized[i][j][l] = 0;
					this.consumptionSatisfactionScoreCategorized[i][j][l] = 0;
					this.employmentStabilityScoreCategorized[i][j][l] = 0;
					
					this.ruralLifeSatisfactionScoreCategorized[i][j][l] = 0;
					this.ruralLivingConditionScoreCategorized[i][j][l] = 0;
					this.ruralIncomeSatisfactionScoreCategorized[i][j][l] = 0;
					this.ruralConsumptionSatisfactionScoreCategorized[i][j][l] = 0;
					this.ruralEmploymentStabilityScoreCategorized[i][j][l] = 0;
					
					this.urbanLifeSatisfactionScoreCategorized[i][j][l] = 0;
					this.urbanLivingConditionScoreCategorized[i][j][l] = 0;
					this.urbanIncomeSatisfactionScoreCategorized[i][j][l] = 0;
					this.urbanConsumptionSatisfactionScoreCategorized[i][j][l] = 0;
					this.urbanEmploymentStabilityScoreCategorized[i][j][l] = 0;
					
					for(k=0 ; k<this.gradeDepth ; k++){
						this.lifeSatisfactionCategorized[i][j][l][k] = 0;
						this.livingConditionCategorized[i][j][l][k] = 0;
						this.incomeSatisfactionCategorized[i][j][l][k] = 0;
						this.consumptionSatisfactionCategorized[i][j][l][k] = 0;
						this.employmentStabilityCategorized[i][j][l][k] = 0;
						
						this.ruralLifeSatisfactionCategorized[i][j][l][k] = 0;
						this.ruralLivingConditionCategorized[i][j][l][k] = 0;
						this.ruralIncomeSatisfactionCategorized[i][j][l][k] = 0;
						this.ruralConsumptionSatisfactionCategorized[i][j][l][k] = 0;
						this.ruralEmploymentStabilityCategorized[i][j][l][k] = 0;
						
						this.urbanLifeSatisfactionCategorized[i][j][l][k] = 0;
						this.urbanLivingConditionCategorized[i][j][l][k] = 0;
						this.urbanIncomeSatisfactionCategorized[i][j][l][k] = 0;
						this.urbanConsumptionSatisfactionCategorized[i][j][l][k] = 0;
						this.urbanEmploymentStabilityCategorized[i][j][l][k] = 0;
					}
				}
			}
		}
	}
	
	public void readLocationCode(String filepath, String filename){		
		int i;
		int count;
		String tmpCode;
		String tmpName;
		HashMap<String, String> tmpHashMap;
		ArrayList<String> tmpCodeList, tmpNameList;
		
		try{
			File file;
			Scanner scan;
			
			this.max_regions = 0;
			for(i=0 ; i<this.duration ; i++){
				count = 0;
				file = new File(filepath+(this.startYear+i)+"_"+filename);
				scan = new Scanner(file);
				tmpHashMap = new HashMap<String, String>();
				tmpCodeList = new ArrayList<String>();
				tmpNameList = new ArrayList<String>();
				
				while(scan.hasNext()){
					tmpCode = scan.next();
					tmpName = scan.next();		
					tmpHashMap.put(tmpCode, tmpName);
					if( tmpCode.length() == this.regionClassDepth){
						tmpCodeList.add(tmpCode);
						tmpNameList.add(tmpName);
						count++;
					}		
				}				
				this.n_region[i] = count;
				this.locatoinCode.add(tmpCodeList);
				this.locatoinName.add(tmpNameList);
				this.locationHashMap.add(tmpHashMap);
				if(this.max_regions < count) this.max_regions = count;
				
				file = null;
				scan.close();	
			}
		} catch(IOException e) {
			System.err.println("location code reading error.");
		}
			
	}
	
	public void extractSocieyMicrodata(String filePath, String fileName){
		/*** reg(region), life(life satisfaction), live(living condition), income(income satisfaction) ***/
		/*** cons(consumption satisfaction), work(employment stability), area(area type) ***/
		/*** job(job satisfaction) ***/
	
		//1:very good, 2:good, 3:normal, 4:bad, 5:very bad, 6:I don't know, 7:no consideration
		//1998: reg(0,2), life(53,54):home life satisfaction, live(87,88):7_no_consideration, income(), cons(), work(), area(130,131):1_dong,2_eup,3_myun, age(128,130), industry(21,23)
		//1999: reg(0,2), life(), live(), income(139,140):6_not_know, cons(140,141):6_not_know, work(), area(), age(198,201), industry(21,23)
		//2000: reg(0,2), life(), live(), income(), cons(), work(), area(), age(128,131), industry(133,135)
		//2001: reg(0,2), life(), live(), income(), cons(), work(), area(), age(157,160), industry(25,26)
		//2002: reg(0,2), life(), live(118,119), income(), cons(), work(), area(), age(147,150), industry(19,20)
		//2003: reg(0,2), life(119,120), live(), income(134,135), cons(135,136), work(), area(), age(15,18), industry(24,25)
		//2004: reg(0,2), life(), live(), income(), cons(), work(), area(), age(45,48), industry(53,54)
		//2005: reg(0,2), life(), live(41,42), income(), cons(), work(), area(), age(21,24), industry(30,31)
		//2006: reg(5,7), life(164,165), live(), income(), cons(), work(), area(8,9), age(17,20), industry(27,28)
		//2007: reg(5,7), life(), live(29,30), income(210,211), cons(215,216), work(), area(8,9), age(16,19), industry(26,27)
		//2008-1: reg(5,7), life(21,22), live(), income(), cons(), work(), area(8,9), age(13,16), industry(171,172)
		//2008-2: reg(5,7), life(23,24), live(), income(), cons(), work(), area(8,9), age(15,18), industry(169,170)
		//2009: reg(7,9), life(204,205), live(118,119), income(177,178), cons(180,181), work(), job(203,204), area(321,322), age(12,15), industry(314,315,)
		//2010: reg(7,9), life(20,21), live(), income(), cons(), work(), area(310,311), age(12,14), industry(240,241)
		//2011: reg(7,9), life(22,23), live(23,24), income(344,345), cons(345,346), work(358,359), area(389,390), age(12,15), industry(359,360)
		
		
		int i, j, k;
		int yearIndex;
		int stdYear = 1998;
		int totalDuration = 14;
		int life=0, living=0, income=0, consumption=0, work=0, age=0;
		String region = null, area = null, industry=null;
		int check[], count, errcount[];
		
		int locationIndex, industryIndex;	
		
		String inputFile;
		String tmpStr;
		String microdataCode;		
		
		int[][] regionCode = new int[totalDuration][2];				//[year][start,end]: region
		int[][] lifeCode = new int[totalDuration][2];					//[year][start,end]: life satisfaction
		int[][] livingCode = new int[totalDuration][2];				//[year][start,end]: living condition
		int[][] incomeCode = new int[totalDuration][2];			//[year][start,end]: income satisfaction
		int[][] consumptionCode = new int[totalDuration][2];	//[year][start,end]: consumption satisfaction
		int[][] workCode = new int[totalDuration][2];				//[year][start,end]: job stability
		int[][] areaCode = new int[totalDuration][2];				//[year][start,end]: area type
		int[][] ageCode = new int[totalDuration][2];					//[year][start,end]: age
		int[][] industryCode = new int[totalDuration][2];			//[year][start,end]: industry
		
		regionCode[0][0]=0; regionCode[0][1]=2; lifeCode[0][0]=53;lifeCode[0][1]=54;livingCode[0][0]=87;livingCode[0][1]=88;incomeCode[0][0]=0;incomeCode[0][1]=0;consumptionCode[0][0]=0;consumptionCode[0][1]=0;workCode[0][0]=0;workCode[0][1]=0;areaCode[0][0]=130;areaCode[0][1]=131;ageCode[0][0]=128;ageCode[0][1]=130;industryCode[0][0]=21;industryCode[0][1]=23;
		regionCode[1][0]=0; regionCode[1][1]=2; lifeCode[1][0]=0;lifeCode[1][1]=0;livingCode[1][0]=0;livingCode[1][1]=0;incomeCode[1][0]=139;incomeCode[1][1]=140;consumptionCode[1][0]=140;consumptionCode[1][1]=141;workCode[1][0]=0;workCode[1][1]=0;areaCode[1][0]=0;areaCode[1][1]=0;ageCode[1][0]=198;ageCode[1][1]=201;industryCode[1][0]=21;industryCode[1][1]=23;
		regionCode[2][0]=0; regionCode[2][1]=2; lifeCode[2][0]=0;lifeCode[2][1]=0;livingCode[2][0]=0;livingCode[2][1]=0;incomeCode[2][0]=0;incomeCode[2][1]=0;consumptionCode[2][0]=0;consumptionCode[2][1]=0;workCode[2][0]=0;workCode[2][1]=0;areaCode[2][0]=0;areaCode[2][1]=0;ageCode[2][0]=139;ageCode[2][1]=142;industryCode[2][0]=133;industryCode[2][1]=135;
		regionCode[3][0]=0; regionCode[3][1]=2; lifeCode[3][0]=0;lifeCode[3][1]=0;livingCode[3][0]=0;livingCode[3][1]=0;incomeCode[3][0]=0;incomeCode[3][1]=0;consumptionCode[3][0]=0;consumptionCode[3][1]=0;workCode[3][0]=0;workCode[3][1]=0;areaCode[3][0]=0;areaCode[3][1]=0;ageCode[3][0]=157;ageCode[3][1]=160;industryCode[3][0]=25;industryCode[3][1]=26;
		regionCode[4][0]=0; regionCode[4][1]=2; lifeCode[4][0]=0;lifeCode[4][1]=0;livingCode[4][0]=118;livingCode[4][1]=119;incomeCode[4][0]=0;incomeCode[4][1]=0;consumptionCode[4][0]=0;consumptionCode[4][1]=0;workCode[4][0]=0;workCode[4][1]=0;areaCode[4][0]=0;areaCode[4][1]=0;ageCode[4][0]=147;ageCode[4][1]=150;industryCode[4][0]=19;industryCode[4][1]=20;
		regionCode[5][0]=0; regionCode[5][1]=2; lifeCode[5][0]=119;lifeCode[5][1]=120;livingCode[5][0]=0;livingCode[5][1]=0;incomeCode[5][0]=134;incomeCode[5][1]=135;consumptionCode[5][0]=135;consumptionCode[5][1]=136;workCode[5][0]=0;workCode[5][1]=0;areaCode[5][0]=0;areaCode[5][1]=0;ageCode[5][0]=15;ageCode[5][1]=18;industryCode[5][0]=24;industryCode[5][1]=25;
		regionCode[6][0]=0; regionCode[6][1]=2; lifeCode[6][0]=0;lifeCode[6][1]=0;livingCode[6][0]=0;livingCode[6][1]=0;incomeCode[6][0]=0;incomeCode[6][1]=0;consumptionCode[6][0]=0;consumptionCode[6][1]=0;workCode[6][0]=0;workCode[6][1]=0;areaCode[6][0]=0;areaCode[6][1]=0;ageCode[6][0]=45;ageCode[6][1]=48;industryCode[6][0]=53;industryCode[6][1]=54;
		regionCode[7][0]=0; regionCode[7][1]=2; lifeCode[7][0]=0;lifeCode[7][1]=0;livingCode[7][0]=41;livingCode[7][1]=42;incomeCode[7][0]=0;incomeCode[7][1]=0;consumptionCode[7][0]=0;consumptionCode[7][1]=0;workCode[7][0]=0;workCode[7][1]=0;areaCode[7][0]=0;areaCode[7][1]=0;ageCode[7][0]=21;ageCode[7][1]=24;industryCode[7][0]=30;industryCode[7][1]=31;
		regionCode[8][0]=5; regionCode[8][1]=7; lifeCode[8][0]=164;lifeCode[8][1]=165;livingCode[8][0]=0;livingCode[8][1]=0;incomeCode[8][0]=0;incomeCode[8][1]=0;consumptionCode[8][0]=0;consumptionCode[8][1]=0;workCode[8][0]=0;workCode[8][1]=0;areaCode[8][0]=8;areaCode[8][1]=9;ageCode[8][0]=17;ageCode[8][1]=20;industryCode[8][0]=27;industryCode[8][1]=28;
		regionCode[9][0]=5; regionCode[9][1]=7; lifeCode[9][0]=0;lifeCode[9][1]=0;livingCode[9][0]=29;livingCode[9][1]=30;incomeCode[9][0]=210;incomeCode[9][1]=211;consumptionCode[9][0]=215;consumptionCode[9][1]=216;workCode[9][0]=0;workCode[9][1]=0;areaCode[9][0]=8;areaCode[9][1]=9;ageCode[9][0]=16;ageCode[9][1]=19;industryCode[9][0]=26;industryCode[9][1]=27;
		regionCode[10][0]=5; regionCode[10][1]=7; lifeCode[10][0]=21;lifeCode[10][1]=22;livingCode[10][0]=0;livingCode[10][1]=0;incomeCode[10][0]=0;incomeCode[10][1]=0;consumptionCode[10][0]=0;consumptionCode[10][1]=0;workCode[10][0]=0;workCode[10][1]=0;areaCode[10][0]=8;areaCode[10][1]=9;ageCode[10][0]=13;ageCode[10][1]=16;industryCode[10][0]=171;industryCode[10][1]=172;
		regionCode[11][0]=7; regionCode[11][1]=9; lifeCode[11][0]=204;lifeCode[11][1]=205;livingCode[11][0]=118;livingCode[11][1]=119;incomeCode[11][0]=177;incomeCode[11][1]=178;consumptionCode[11][0]=180;consumptionCode[11][1]=181;workCode[11][0]=0;workCode[11][1]=0;areaCode[11][0]=321;areaCode[11][1]=322;ageCode[11][0]=12;ageCode[11][1]=15;industryCode[11][0]=314;industryCode[11][1]=315;
		regionCode[12][0]=7; regionCode[12][1]=9; lifeCode[12][0]=20;lifeCode[12][1]=21;livingCode[12][0]=0;livingCode[12][1]=0;incomeCode[12][0]=0;incomeCode[12][1]=0;consumptionCode[12][0]=0;consumptionCode[12][1]=0;workCode[12][0]=0;workCode[12][1]=0;areaCode[12][0]=0;areaCode[12][1]=0;ageCode[12][0]=12;ageCode[12][1]=14;industryCode[12][0]=240;industryCode[12][1]=241;
		regionCode[13][0]=7; regionCode[13][1]=9; lifeCode[13][0]=22;lifeCode[13][1]=23;livingCode[13][0]=23;livingCode[13][1]=24;incomeCode[13][0]=344;incomeCode[13][1]=345;consumptionCode[13][0]=345;consumptionCode[13][1]=346;workCode[13][0]=358;workCode[13][1]=359;areaCode[13][0]=389;areaCode[13][1]=390;ageCode[13][0]=12;ageCode[13][1]=15;industryCode[13][0]=359;industryCode[13][1]=360;
		
		File file;
		Scanner scan;
		
		/*** check response existence ***/
		for(i=0 ; i <this.duration ; i++){
			yearIndex = i + this.startYear - stdYear;
			if(regionCode[yearIndex][0]==0 && regionCode[yearIndex][1]==0) this.responseExistence[i][0] = 0;
			else if(regionCode[yearIndex][1]>0) this.responseExistence[i][0] = 1;
			else System.err.println((i+this.startYear)+"year region code index error");
			
			if(lifeCode[yearIndex][0]==0 && lifeCode[yearIndex][1]==0) this.responseExistence[i][1] = 0;
			else if(lifeCode[yearIndex][1]>0) this.responseExistence[i][1] = 1;
			else System.err.println((i+this.startYear)+"year life code index error");
			
			if(livingCode[yearIndex][0]==0 && livingCode[yearIndex][1]==0) this.responseExistence[i][2] = 0;
			else if(livingCode[yearIndex][1]>0) this.responseExistence[i][2] = 1;
			else System.err.println((i+this.startYear)+"year living code index error");
			
			if(incomeCode[yearIndex][0]==0 && incomeCode[yearIndex][1]==0) this.responseExistence[i][3] = 0;
			else if(incomeCode[yearIndex][1]>0) this.responseExistence[i][3] = 1;
			else System.err.println((i+this.startYear)+"year income code index error");
			
			if(consumptionCode[yearIndex][0]==0 && consumptionCode[yearIndex][1]==0) this.responseExistence[i][4] = 0;
			else if(consumptionCode[yearIndex][1]>0) this.responseExistence[i][4] = 1;
			else System.err.println((i+this.startYear)+"year consumption code index error");
			
			if(workCode[yearIndex][0]==0 && workCode[yearIndex][1]==0) this.responseExistence[i][5] = 0;
			else if(workCode[yearIndex][1]>0) this.responseExistence[i][5] = 1;
			else System.err.println((i+this.startYear)+"year job stability code index error");
			
			if(areaCode[yearIndex][0]==0 && areaCode[yearIndex][1]==0) this.responseExistence[i][6] = 0;
			else if(areaCode[yearIndex][1]>0) this.responseExistence[i][6] = 1;
			else System.err.println((i+this.startYear)+"year area code index error");
			
			if(ageCode[yearIndex][0]==0 && ageCode[yearIndex][1]==0) this.responseExistence[i][7] = 0;
			else if(ageCode[yearIndex][1]>0) this.responseExistence[i][7] = 1;
			else System.err.println((i+this.startYear)+"year age code index error");
			
			if(industryCode[yearIndex][0]==0 && industryCode[yearIndex][1]==0) this.responseExistence[i][8] = 0;
			else if(industryCode[yearIndex][1]>0) this.responseExistence[i][8] = 1;
			else System.err.println((i+this.startYear)+"year age code index error");
		}
		
		
		for(i=0 ; i <this.duration ; i++){
			System.out.print((this.startYear+i)+" ");	
			
			yearIndex = i + this.startYear - stdYear;
			inputFile = filePath+(this.startYear+i)+fileName;

			try{
				file = new File(inputFile);
				scan = new Scanner(file);
				
				count = 0;
				errcount = new int[this.n_category+1];
				check = new int[this.n_category+1];
				for(j=0 ; j< this.n_category+1 ; j++) errcount[j] = 0;
				while(scan.hasNext()){	
					microdataCode = scan.nextLine();
					for(j=0 ; j<this.n_category+1 ; j++) check[j] = 0;
					
					// extract codes
					if(this.responseExistence[i][0]==1){
						tmpStr = microdataCode.substring(regionCode[yearIndex][0], regionCode[yearIndex][1]);
						if(tmpStr.contains(" ") == false) region = tmpStr;
						else check[1]++; 
					}
					if(this.responseExistence[i][1]==1){
						tmpStr = microdataCode.substring(lifeCode[yearIndex][0], lifeCode[yearIndex][1]);
						if(tmpStr.contains(" ") == false) life = Integer.parseInt(tmpStr);
						else check[2]++; 
					}
					if(this.responseExistence[i][2]==1){
						tmpStr = microdataCode.substring(livingCode[yearIndex][0], livingCode[yearIndex][1]);
						if(tmpStr.contains(" ") == false) living = Integer.parseInt(tmpStr);
						else check[3]++; 
					}
					if(this.responseExistence[i][3]==1){
						tmpStr = microdataCode.substring(incomeCode[yearIndex][0], incomeCode[yearIndex][1]);
						if(tmpStr.contains(" ") == false) income = Integer.parseInt(tmpStr);
						else check[4]++; 
					}
					if(this.responseExistence[i][4]==1){
						tmpStr = microdataCode.substring(consumptionCode[yearIndex][0], consumptionCode[yearIndex][1]);
						if(tmpStr.contains(" ") == false)  consumption = Integer.parseInt(tmpStr);
						else check[5]++; 
					}
					if(this.responseExistence[i][5]==1){
						tmpStr = microdataCode.substring(workCode[yearIndex][0], workCode[yearIndex][1]);
						if(tmpStr.contains(" ") == false) work = Integer.parseInt(tmpStr);
						else check[6]++; 
					}
					if(this.responseExistence[i][6]==1){
						tmpStr = microdataCode.substring(areaCode[yearIndex][0], areaCode[yearIndex][1]);
						if(tmpStr.contains(" ") == false) area = tmpStr;
						else check[7]++; 
					}
							
					if(this.responseExistence[i][7]==1){
						tmpStr = microdataCode.substring(ageCode[yearIndex][0], ageCode[yearIndex][1]).trim();
						if(Integer.parseInt(tmpStr)>0 && Integer.parseInt(tmpStr)<150) age = Integer.parseInt(tmpStr);
						else check[8]++; 
					}
					
					if(this.responseExistence[i][8]==1){
						tmpStr = microdataCode.substring(industryCode[yearIndex][0], industryCode[yearIndex][1]).trim();
						if(this.industryCode.get(i).contains(tmpStr)) industry = tmpStr;
						else check[9]++; 
					}
					//check all errors include industry code
					//for(j=1 ; j<this.n_category+1 ; j++) if(check[j] != 0) check[0] = check[j];
					
					//check except industry code
					//for(j=1 ; j<this.n_category ; j++) if(check[j] != 0) check[0] = check[j];
					
					//check only income satisfaction code
					check[0] = check[3];
					
					//add micro-data at data list
					if(check[0] == 0){
						if(this.maxAge == 0 || (age>=this.minAge && age < this.maxAge)){
							if(this.locatoinCode.get(i).contains(region)){
								locationIndex = this.locatoinCode.get(i).indexOf(region);
								this.regionSamples[i][locationIndex]++;
								
								if(this.responseExistence[i][1]==1){
									if(life>0 && life<6) this.lifeSatisfaction[i][locationIndex][life-1]++;
									else if(life<0 || life>7) System.err.println("wrong life code extracted");
								}
								if(this.responseExistence[i][2]==1){
									if(living>0 && living<6) this.livingCondition[i][locationIndex][living-1]++;
									else if(living<0 || living>7) System.err.println("wrong living code extracted");
								}
								if(this.responseExistence[i][3]==1){
									if(income>0 && income<6) this.incomeSatisfaction[i][locationIndex][income-1]++;
									else if(income<0 || income>7) System.err.println("wrong income code extracted");
								}
								if(this.responseExistence[i][4]==1){
									if(consumption>0 && consumption<6) this.consumptionSatisfaction[i][locationIndex][consumption-1]++;
									else if(consumption<0 || consumption>7) System.err.println("wrong consumption code extracted");
								}
								if(this.responseExistence[i][5]==1){
									if(work>0 && work<6) this.employmentStability[i][locationIndex][work-1]++;
									else if(work<0 || work>7) System.err.println("wrong job stability code extracted");
								}
								if(this.responseExistence[i][6]==1){
									if(area.equals("A") || area.equals("1")){
										this.urbanRegionSamples[i][locationIndex]++;
										if(this.responseExistence[i][1]==1)
											if(life>0 && life<6) this.urbanLifeSatisfaction[i][locationIndex][life-1]++;
										if(this.responseExistence[i][2]==1)
											if(living>0 && living<6) this.urbanLivingCondition[i][locationIndex][living-1]++;
										if(this.responseExistence[i][3]==1)
											if(income>0 && income<6) this.urbanIncomeSatisfaction[i][locationIndex][income-1]++;
										if(this.responseExistence[i][4]==1)
											if(consumption>0 && consumption<6) this.urbanConsumptionSatisfaction[i][locationIndex][consumption-1]++;	
										if(this.responseExistence[i][5]==1)
											if(work>0 && work<6) this.urbanEmploymentStability[i][locationIndex][work-1]++;
									}else if(area.equals("B") || area.equals("2") || area.equals("3")){
										this.ruralRegionSamples[i][locationIndex]++;
										if(this.responseExistence[i][1]==1)
											if(life>0 && life<6) this.ruralLifeSatisfaction[i][locationIndex][life-1]++;
										if(this.responseExistence[i][2]==1)
											if(living>0 && living<6) this.ruralLivingCondition[i][locationIndex][living-1]++;
										if(this.responseExistence[i][3]==1)
											if(income>0 && income<6) this.ruralIncomeSatisfaction[i][locationIndex][income-1]++;
										if(this.responseExistence[i][4]==1)
											if(consumption>0 && consumption<6) this.ruralConsumptionSatisfaction[i][locationIndex][consumption-1]++;	
										if(this.responseExistence[i][5]==1)
											if(work>0 && work<6) this.ruralEmploymentStability[i][locationIndex][work-1]++;
									}else System.err.println("wrong area code extracted");
								}
								
								//add categorized data at list
								if(this.responseExistence[i][8]==1){
									industryIndex = this.industryCode.get(i).indexOf(industry);
									this.regionSamplesCategorized[i][locationIndex][industryIndex]++;
									
									if(this.responseExistence[i][1]==1){
										if(life>0 && life<6) this.lifeSatisfactionCategorized[i][locationIndex][industryIndex][life-1]++;
										else if(life<0 || life>7) System.err.println("wrong life code extracted");
									}
									if(this.responseExistence[i][2]==1){
										if(living>0 && living<6) this.livingConditionCategorized[i][locationIndex][industryIndex][living-1]++;
										else if(living<0 || living>7) System.err.println("wrong living code extracted");
									}
									if(this.responseExistence[i][3]==1){
										if(income>0 && income<6) this.incomeSatisfactionCategorized[i][locationIndex][industryIndex][income-1]++;
										else if(income<0 || income>7) System.err.println("wrong income code extracted");
									}
									if(this.responseExistence[i][4]==1){
										if(consumption>0 && consumption<6) this.consumptionSatisfactionCategorized[i][locationIndex][industryIndex][consumption-1]++;
										else if(consumption<0 || consumption>7) System.err.println("wrong consumption code extracted");
									}
									if(this.responseExistence[i][5]==1){
										if(work>0 && work<6) this.employmentStabilityCategorized[i][locationIndex][industryIndex][work-1]++;
										else if(work<0 || work>7) System.err.println("wrong job stability code extracted");
									}
									if(this.responseExistence[i][6]==1){
										if(area.equals("A") || area.equals("1")){
											this.urbanRegionSamplesCategorized[i][locationIndex][industryIndex]++;
											if(this.responseExistence[i][1]==1)
												if(life>0 && life<6) this.urbanLifeSatisfactionCategorized[i][locationIndex][industryIndex][life-1]++;
											if(this.responseExistence[i][2]==1)
												if(living>0 && living<6) this.urbanLivingConditionCategorized[i][locationIndex][industryIndex][living-1]++;
											if(this.responseExistence[i][3]==1)
												if(income>0 && income<6) this.urbanIncomeSatisfactionCategorized[i][locationIndex][industryIndex][income-1]++;
											if(this.responseExistence[i][4]==1)
												if(consumption>0 && consumption<6) this.urbanConsumptionSatisfactionCategorized[i][locationIndex][industryIndex][consumption-1]++;	
											if(this.responseExistence[i][5]==1)
												if(work>0 && work<6) this.urbanEmploymentStabilityCategorized[i][locationIndex][industryIndex][work-1]++;
										}else if(area.equals("B") || area.equals("2") || area.equals("3")){
											this.ruralRegionSamplesCategorized[i][locationIndex][industryIndex]++;
											if(this.responseExistence[i][1]==1)
												if(life>0 && life<6) this.ruralLifeSatisfactionCategorized[i][locationIndex][industryIndex][life-1]++;
											if(this.responseExistence[i][2]==1)
												if(living>0 && living<6) this.ruralLivingConditionCategorized[i][locationIndex][industryIndex][living-1]++;
											if(this.responseExistence[i][3]==1)
												if(income>0 && income<6) this.ruralIncomeSatisfactionCategorized[i][locationIndex][industryIndex][income-1]++;
											if(this.responseExistence[i][4]==1)
												if(consumption>0 && consumption<6) this.ruralConsumptionSatisfactionCategorized[i][locationIndex][industryIndex][consumption-1]++;	
											if(this.responseExistence[i][5]==1)
												if(work>0 && work<6) this.ruralEmploymentStabilityCategorized[i][locationIndex][industryIndex][work-1]++;
										}else System.err.println("wrong area code extracted");
									}
								}
							}
						}
					}
					else for(j=0 ; j<this.n_category+1 ; j++) errcount[j] += check[j];					
					count++;
				}	
				if(errcount[0]>0){
					System.out.print("error:\t"+errcount[0]+"damaged data in\t"+count+" data");
					for(j=1 ; j<this.n_category+1 ; j++) System.out.print("\ttype "+j+": "+errcount[j]);
					System.out.println();
				}else System.out.println();
				scan.close();	
				check = null;
				errcount = null;
			} catch(IOException e) {
				System.err.println("microdata reagind error.");
			}		
		}		
	}
	
	public void calculateSocietyStatistics(){
		int i, j,k;

		for(i=0 ; i<this.duration ; i++){
			for(j=0 ; j<this.n_region[i] ; j++){
				this.nationSamples[i] += this.regionSamples[i][j];
				this.ruralSamples[i] += this.ruralRegionSamples[i][j];
				this.urbanSamples[i] += this.urbanRegionSamples[i][j];
				
				for(k=0 ; k<this.gradeDepth ; k++){
					this.lifeSatisfactionNation[i][k] += this.lifeSatisfaction[i][j][k];
					this.livingConditionNation[i][k] += this.livingCondition[i][j][k];
					this.incomeSatisfactionNation[i][k] += this.incomeSatisfaction[i][j][k];
					this.consumptionSatisfactionNation[i][k] += this.consumptionSatisfaction[i][j][k];
					this.employmentStabilityNation[i][k] +=  this.employmentStability[i][j][k];
						
					this.urbanLifeSatisfactionNation[i][k] += this.urbanLifeSatisfaction[i][j][k];
					this.urbanLivingConditionNation[i][k] += this.urbanLivingCondition[i][j][k];
					this.urbanIncomeSatisfactionNation[i][k] += this.urbanIncomeSatisfaction[i][j][k];
					this.urbanConsumptionSatisfactionNation[i][k] += this.urbanConsumptionSatisfaction[i][j][k];
					this.urbanEmploymentStabilityNation[i][k] +=  this.urbanEmploymentStability[i][j][k];

					this.ruralLifeSatisfactionNation[i][k] += this.ruralLifeSatisfaction[i][j][k];
					this.ruralLivingConditionNation[i][k] += this.ruralLivingCondition[i][j][k];
					this.ruralIncomeSatisfactionNation[i][k] += this.ruralIncomeSatisfaction[i][j][k];
					this.ruralConsumptionSatisfactionNation[i][k] += this.ruralConsumptionSatisfaction[i][j][k];
					this.ruralEmploymentStabilityNation[i][k] +=  this.ruralEmploymentStability[i][j][k];
				}
			}
			
			for(j=0 ; j<this.gradeDepth ; j++){
				this.lifeSatisfactionNationRate[i][j] = (double) this.lifeSatisfactionNation[i][j] / this.nationSamples[i];
				this.livingConditionNationRate[i][j] = (double) this.livingConditionNation[i][j] / this.nationSamples[i];
				this.incomeSatisfactionNationRate[i][j] = (double) this.incomeSatisfactionNation[i][j] / this.nationSamples[i];
				this.consumptionSatisfactionNationRate[i][j] = (double) this.consumptionSatisfactionNation[i][j] / this.nationSamples[i];
				this.employmentStabilityNationRate[i][j] = (double) this.employmentStabilityNation[i][j] / this.nationSamples[i];

				this.ruralLifeSatisfactionNationRate[i][j] = (double) this.ruralLifeSatisfactionNation[i][j] / this.ruralSamples[i];
				this.ruralLivingConditionNationRate[i][j] = (double) this.ruralLivingConditionNation[i][j] / this.ruralSamples[i];
				this.ruralIncomeSatisfactionNationRate[i][j] = (double) this.ruralIncomeSatisfactionNation[i][j] / this.ruralSamples[i];
				this.ruralConsumptionSatisfactionNationRate[i][j] = (double) this.ruralConsumptionSatisfactionNation[i][j] / this.ruralSamples[i];
				this.ruralEmploymentStabilityNationRate[i][j] = (double) this.ruralEmploymentStabilityNation[i][j] / this.ruralSamples[i];

				this.urbanLifeSatisfactionNationRate[i][j] = (double) this.urbanLifeSatisfactionNation[i][j] / this.urbanSamples[i];
				this.urbanLivingConditionNationRate[i][j] = (double) this.urbanLivingConditionNation[i][j] / this.urbanSamples[i];
				this.urbanIncomeSatisfactionNationRate[i][j] = (double) this.urbanIncomeSatisfactionNation[i][j] / this.urbanSamples[i];
				this.urbanConsumptionSatisfactionNationRate[i][j] = (double) this.urbanConsumptionSatisfactionNation[i][j] / this.urbanSamples[i];
				this.urbanEmploymentStabilityNationRate[i][j] = (double) this.urbanEmploymentStabilityNation[i][j] / this.urbanSamples[i];
			}
		}
		
		for(i=0 ; i<this.duration ; i++){
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.gradeDepth ; k++){
					this.lifeSatisfactionRate[i][j][k] = (double) this.lifeSatisfaction[i][j][k] / this.regionSamples[i][j];
					this.livingConditionRate[i][j][k] = (double) this.livingCondition[i][j][k] / this.regionSamples[i][j];
					this.incomeSatisfactionRate[i][j][k] = (double) this.incomeSatisfaction[i][j][k] / this.regionSamples[i][j];
					this.consumptionSatisfactionRate[i][j][k] = (double) this.consumptionSatisfaction[i][j][k] / this.regionSamples[i][j];
					this.employmentStabilityRate[i][j][k] = (double) this.employmentStability[i][j][k] / this.regionSamples[i][j];

					this.urbanLifeSatisfactionRate[i][j][k] = (double) this.urbanLifeSatisfaction[i][j][k] / this.urbanRegionSamples[i][j];
					this.urbanLivingConditionRate[i][j][k] = (double) this.urbanLivingCondition[i][j][k] / this.urbanRegionSamples[i][j];
					this.urbanIncomeSatisfactionRate[i][j][k] = (double) this.urbanIncomeSatisfaction[i][j][k] / this.urbanRegionSamples[i][j];
					this.urbanConsumptionSatisfactionRate[i][j][k] = (double) this.urbanConsumptionSatisfaction[i][j][k] / this.urbanRegionSamples[i][j];
					this.urbanEmploymentStabilityRate[i][j][k] = (double) this.urbanEmploymentStability[i][j][k] / this.urbanRegionSamples[i][j];

					this.ruralLifeSatisfactionRate[i][j][k] = (double) this.ruralLifeSatisfaction[i][j][k] / this.ruralRegionSamples[i][j];
					this.ruralLivingConditionRate[i][j][k] = (double) this.ruralLivingCondition[i][j][k] / this.ruralRegionSamples[i][j];
					this.ruralIncomeSatisfactionRate[i][j][k] = (double) this.ruralIncomeSatisfaction[i][j][k] / this.ruralRegionSamples[i][j];
					this.ruralConsumptionSatisfactionRate[i][j][k] = (double) this.ruralConsumptionSatisfaction[i][j][k] / this.ruralRegionSamples[i][j];
					this.ruralEmploymentStabilityRate[i][j][k] = (double) this.ruralEmploymentStability[i][j][k] / this.ruralRegionSamples[i][j];			
				}
			}
		}
	}
	
	public void calculateCategorizedSocietyStatistics(){
		int i,  j, k, l;

		for(i=0 ; i<this.duration ; i++){
			for(j=0 ; j<this.n_region[i] ; j++){
				for(l=0 ; l<this.n_industry[i] ; l++){
					this.nationSamplesCategorized[i][l] += this.regionSamplesCategorized[i][j][l];
					this.ruralSamplesCategorized[i][l] += this.ruralRegionSamplesCategorized[i][j][l];
					this.urbanSamplesCategorized[i][l] += this.urbanRegionSamplesCategorized[i][j][l];
					
					for(k=0 ; k<this.gradeDepth ; k++){
						this.lifeSatisfactionNationCategorized[i][l][k] += this.lifeSatisfactionCategorized[i][j][l][k];
						this.livingConditionNationCategorized[i][l][k] += this.livingConditionCategorized[i][j][l][k];
						this.incomeSatisfactionNationCategorized[i][l][k] += this.incomeSatisfactionCategorized[i][j][l][k];
						this.consumptionSatisfactionNationCategorized[i][l][k] += this.consumptionSatisfactionCategorized[i][j][l][k];
						this.employmentStabilityNationCategorized[i][l][k] +=  this.employmentStabilityCategorized[i][j][l][k];
							
						this.urbanLifeSatisfactionNationCategorized[i][l][k] += this.urbanLifeSatisfactionCategorized[i][j][l][k];
						this.urbanLivingConditionNationCategorized[i][l][k] += this.urbanLivingConditionCategorized[i][j][l][k];
						this.urbanIncomeSatisfactionNationCategorized[i][l][k] += this.urbanIncomeSatisfactionCategorized[i][j][l][k];
						this.urbanConsumptionSatisfactionNationCategorized[i][l][k] += this.urbanConsumptionSatisfactionCategorized[i][j][l][k];
						this.urbanEmploymentStabilityNationCategorized[i][l][k] +=  this.urbanEmploymentStabilityCategorized[i][j][l][k];
	
						this.ruralLifeSatisfactionNationCategorized[i][l][k] += this.ruralLifeSatisfactionCategorized[i][j][l][k];
						this.ruralLivingConditionNationCategorized[i][l][k] += this.ruralLivingConditionCategorized[i][j][l][k];
						this.ruralIncomeSatisfactionNationCategorized[i][l][k] += this.ruralIncomeSatisfactionCategorized[i][j][l][k];
						this.ruralConsumptionSatisfactionNationCategorized[i][l][k] += this.ruralConsumptionSatisfactionCategorized[i][j][l][k];
						this.ruralEmploymentStabilityNationCategorized[i][l][k] +=  this.ruralEmploymentStabilityCategorized[i][j][l][k];
					}
				}
			}
		}
	}
	
	public void calculateSatisfactionTotalScore(double[] gradeWeight){
		this.weight = gradeWeight;
		this.calculateSatisfactionTotalScore();
	}
	
	public void calculateSatisfactionTotalScore(){
		int i, j,k;

		for(i=0 ; i<this.duration ; i++){
			for(j=0 ; j<this.gradeDepth ; j++){
				this.lifeSatisfactionScoreNation[i] += this.weight[j] * this.lifeSatisfactionNationRate[i][j];
				this.livingConditionScoreNation[i] += this.weight[j] * this.livingConditionNationRate[i][j];
				this.incomeSatisfactionScoreNation[i] += this.weight[j] * this.incomeSatisfactionNationRate[i][j];
				this.consumptionSatisfactionScoreNation[i] += this.weight[j] * this.consumptionSatisfactionNationRate[i][j];
				this.employmentStabilityScoreNation[i] += this.weight[j] * this.employmentStabilityNationRate[i][j];
						
				this.lifeSatisfactionScoreRural[i] += this.weight[j] * this.ruralLifeSatisfactionNationRate[i][j];
				this.livingConditionScoreRural[i] += this.weight[j] * this.ruralLivingConditionNationRate[i][j];
				this.incomeSatisfactionScoreRural[i] += this.weight[j] * this.ruralIncomeSatisfactionNationRate[i][j];
				this.consumptionSatisfactionScoreRural[i] += this.weight[j] * this.ruralConsumptionSatisfactionNationRate[i][j];
				this.employmentStabilityScoreRural[i] += this.weight[j] * this.ruralEmploymentStabilityNationRate[i][j];
	
				this.lifeSatisfactionScoreUrban[i] += this.weight[j] * this.urbanLifeSatisfactionNationRate[i][j];
				this.livingConditionScoreUrban[i] += this.weight[j] * this.urbanLivingConditionNationRate[i][j];
				this.incomeSatisfactionScoreUrban[i] += this.weight[j] * this.urbanIncomeSatisfactionNationRate[i][j];
				this.consumptionSatisfactionScoreUrban[i] += this.weight[j] * this.urbanConsumptionSatisfactionNationRate[i][j];
				this.employmentStabilityScoreUrban[i] += this.weight[j] * this.urbanEmploymentStabilityNationRate[i][j];
			}
		}	
		
		for(i=0 ; i<this.duration ; i++){
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.gradeDepth ; k++){
					this.lifeSatisfactionScore[i][j] += this.weight[k] * this.lifeSatisfactionRate[i][j][k];
					this.livingConditionScore[i][j] += this.weight[k] * this.livingConditionRate[i][j][k];
					this.incomeSatisfactionScore[i][j] += this.weight[k] * this.incomeSatisfactionRate[i][j][k];
					this.consumptionSatisfactionScore[i][j] += this.weight[k] * this.consumptionSatisfactionRate[i][j][k];
					this.employmentStabilityScore[i][j] += this.weight[k] * this.employmentStabilityRate[i][j][k];

					this.ruralLifeSatisfactionScore[i][j] += this.weight[k] * this.ruralLifeSatisfactionRate[i][j][k];
					this.ruralLivingConditionScore[i][j] += this.weight[k] * this.ruralLivingConditionRate[i][j][k];
					this.ruralIncomeSatisfactionScore[i][j] += this.weight[k] * this.ruralIncomeSatisfactionRate[i][j][k];
					this.ruralConsumptionSatisfactionScore[i][j] += this.weight[k] * this.ruralConsumptionSatisfactionRate[i][j][k];
					this.ruralEmploymentStabilityScore[i][j] += this.weight[k] * this.ruralEmploymentStabilityRate[i][j][k];
					
					this.urbanLifeSatisfactionScore[i][j] += this.weight[k] * this.urbanLifeSatisfactionRate[i][j][k];
					this.urbanLivingConditionScore[i][j] += this.weight[k] * this.urbanLivingConditionRate[i][j][k];
					this.urbanIncomeSatisfactionScore[i][j] += this.weight[k] * this.urbanIncomeSatisfactionRate[i][j][k];
					this.urbanConsumptionSatisfactionScore[i][j] += this.weight[k] * this.urbanConsumptionSatisfactionRate[i][j][k];
					this.urbanEmploymentStabilityScore[i][j] += this.weight[k] * this.urbanEmploymentStabilityRate[i][j][k];
				}
			}
		}	
	}
	
	public void calculateCategorizedSatisfactionTotalScore(){
		int i, j, k, l;

		for(i=0 ; i<this.duration ; i++){
			for(l=0 ; l<this.n_industry[i] ; l++){
				for(j=0 ; j<this.gradeDepth ; j++){
					this.lifeSatisfactionScoreNationCategorized[i][l] += (double) this.weight[j] * this.lifeSatisfactionNationCategorized[i][l][j] / this.nationSamplesCategorized[i][l];
					this.livingConditionScoreNationCategorized[i][l] += (double) this.weight[j] * this.livingConditionNationCategorized[i][l][j] / this.nationSamplesCategorized[i][l];
					this.incomeSatisfactionScoreNationCategorized[i][l] += (double) this.weight[j] * this.incomeSatisfactionNationCategorized[i][l][j] / this.nationSamplesCategorized[i][l];
					this.consumptionSatisfactionScoreNationCategorized[i][l] += (double) this.weight[j] * this.consumptionSatisfactionNationCategorized[i][l][j] / this.nationSamplesCategorized[i][l];
					this.employmentStabilityScoreNationCategorized[i][l] += (double) this.weight[j] * this.employmentStabilityNationCategorized[i][l][j] / this.nationSamplesCategorized[i][l];
							
					this.lifeSatisfactionScoreRuralCategorized[i][l] += (double) this.weight[j] * this.ruralLifeSatisfactionNationCategorized[i][l][j] / this.ruralSamplesCategorized[i][l];
					this.livingConditionScoreRuralCategorized[i][l] += (double) this.weight[j] * this.ruralLivingConditionNationCategorized[i][l][j] / this.ruralSamplesCategorized[i][l];
					this.incomeSatisfactionScoreRuralCategorized[i][l] += (double) this.weight[j] * this.ruralIncomeSatisfactionNationCategorized[i][l][j] / this.ruralSamplesCategorized[i][l];
					this.consumptionSatisfactionScoreRuralCategorized[i][l] += (double) this.weight[j] * this.ruralConsumptionSatisfactionNationCategorized[i][l][j] / this.ruralSamplesCategorized[i][l];
					this.employmentStabilityScoreRuralCategorized[i][l] += (double) this.weight[j] * this.ruralEmploymentStabilityNationCategorized[i][l][j] / this.ruralSamplesCategorized[i][l];
		
					this.lifeSatisfactionScoreUrbanCategorized[i][l] += (double) this.weight[j] * this.urbanLifeSatisfactionNationCategorized[i][l][j] / this.urbanSamplesCategorized[i][l];
					this.livingConditionScoreUrbanCategorized[i][l] += (double) this.weight[j] * this.urbanLivingConditionNationCategorized[i][l][j] / this.urbanSamplesCategorized[i][l];
					this.incomeSatisfactionScoreUrbanCategorized[i][l] += (double) this.weight[j] * this.urbanIncomeSatisfactionNationCategorized[i][l][j] / this.urbanSamplesCategorized[i][l];
					this.consumptionSatisfactionScoreUrbanCategorized[i][l] += (double) this.weight[j] * this.urbanConsumptionSatisfactionNationCategorized[i][l][j] / this.urbanSamplesCategorized[i][l];
					this.employmentStabilityScoreUrbanCategorized[i][l] += (double) this.weight[j] * this.urbanEmploymentStabilityNationCategorized[i][l][j] / this.urbanSamplesCategorized[i][l];
				}
			}
		}	
		
		for(i=0 ; i<this.duration ; i++){
			for(j=0 ; j<this.n_region[i] ; j++){
				for(l=0 ; l<this.n_industry[i] ; l++){
					for(k=0 ; k<this.gradeDepth ; k++){
						this.lifeSatisfactionScoreCategorized[i][j][l] += (double) this.weight[k] * this.lifeSatisfactionCategorized[i][j][l][k]  / this.regionSamplesCategorized[i][j][l] ;
						this.livingConditionScoreCategorized[i][j][l] += (double) this.weight[k] * this.livingConditionCategorized[i][j][l][k] / this.regionSamplesCategorized[i][j][l];
						this.incomeSatisfactionScoreCategorized[i][j][l] += (double) this.weight[k] * this.incomeSatisfactionCategorized[i][j][l][k] / this.regionSamplesCategorized[i][j][l];
						this.consumptionSatisfactionScoreCategorized[i][j][l] += (double) this.weight[k] * this.consumptionSatisfactionCategorized[i][j][l][k] / this.regionSamplesCategorized[i][j][l];
						this.employmentStabilityScoreCategorized[i][j][l] += (double) this.weight[k] * this.employmentStabilityCategorized[i][j][l][k] / this.regionSamplesCategorized[i][j][l];
	
						this.ruralLifeSatisfactionScoreCategorized[i][j][l] += (double) this.weight[k] * this.ruralLifeSatisfactionCategorized[i][j][l][k] / this.ruralRegionSamplesCategorized[i][j][l];
						this.ruralLivingConditionScoreCategorized[i][j][l] += (double) this.weight[k] * this.ruralLivingConditionCategorized[i][j][l][k] / this.ruralRegionSamplesCategorized[i][j][l];
						this.ruralIncomeSatisfactionScoreCategorized[i][j][l] += (double) this.weight[k] * this.ruralIncomeSatisfactionCategorized[i][j][l][k] / this.ruralRegionSamplesCategorized[i][j][l];
						this.ruralConsumptionSatisfactionScoreCategorized[i][j][l] += (double) this.weight[k] * this.ruralConsumptionSatisfactionCategorized[i][j][l][k] / this.ruralRegionSamplesCategorized[i][j][l];
						this.ruralEmploymentStabilityScoreCategorized[i][j][l] += (double) this.weight[k] * this.ruralEmploymentStabilityCategorized[i][j][l][k] / this.ruralRegionSamplesCategorized[i][j][l];
						
						this.urbanLifeSatisfactionScoreCategorized[i][j][l] += (double) this.weight[k] * this.urbanLifeSatisfactionCategorized[i][j][l][k] / this.urbanRegionSamplesCategorized[i][j][l];
						this.urbanLivingConditionScoreCategorized[i][j][l] += (double) this.weight[k] * this.urbanLivingConditionCategorized[i][j][l][k] /  this.urbanRegionSamplesCategorized[i][j][l];
						this.urbanIncomeSatisfactionScoreCategorized[i][j][l] += (double) this.weight[k] * this.urbanIncomeSatisfactionCategorized[i][j][l][k] /  this.urbanRegionSamplesCategorized[i][j][l];
						this.urbanConsumptionSatisfactionScoreCategorized[i][j][l] += (double) this.weight[k] * this.urbanConsumptionSatisfactionCategorized[i][j][l][k] /  this.urbanRegionSamplesCategorized[i][j][l];
						this.urbanEmploymentStabilityScoreCategorized[i][j][l] += (double) this.weight[k] * this.urbanEmploymentStabilityCategorized[i][j][l][k] /  this.urbanRegionSamplesCategorized[i][j][l];
					}
				}
			}
		}	
	}
	
	public void printSatisfactionData(String outputFile){		
		int i,j,k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
					
			pw.print("sample size:");
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.n_region[0] ; j++ ) pw.print("\t"+this.regionSamples[i][j]);
				pw.println();
			}
			pw.println();

			pw.println("life satisfaction:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.lifeSatisfactionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[0] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.lifeSatisfaction[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("living condition:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.livingConditionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[0] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.livingCondition[i][j][k]);
					pw.println();
				}
			}
			pw.println();

			pw.print("income satisfaction:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.incomeSatisfactionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.incomeSatisfaction[i][j][k]);
					pw.println();
				}
			}
			
			pw.println();
			pw.print("consumption satisfaction:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.consumptionSatisfactionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.consumptionSatisfaction[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("employment stability:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.employmentStabilityNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.employmentStability[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
				
			pw.print("urban sample size:");
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++ ) pw.print("\t"+this.urbanRegionSamples[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("urban life satisfaction:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanLifeSatisfactionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanLifeSatisfaction[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("urban living condition:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanLivingConditionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanLivingCondition[i][j][k]);
					pw.println();
				}
			}
			pw.println();

			pw.print("urban income satisfaction:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanIncomeSatisfactionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanIncomeSatisfaction[i][j][k]);
					pw.println();
				}
			}
			
			pw.println();
			pw.print("urban consumption satisfaction:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanConsumptionSatisfactionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanConsumptionSatisfaction[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("urban employment stability:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanEmploymentStabilityNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanEmploymentStability[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			pw.println();
			
			pw.print("rural sample size:");
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++ ) pw.print("\t"+this.ruralRegionSamples[i][j]);
					pw.println();
				}
			}
			pw.println();
	
			pw.print("rural life satisfaction:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralLifeSatisfactionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralLifeSatisfaction[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("rural living condition:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralLivingConditionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralLivingCondition[i][j][k]);
					pw.println();
				}
			}
			pw.println();

			pw.print("rural income satisfaction:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralIncomeSatisfactionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralIncomeSatisfaction[i][j][k]);
					pw.println();
				}
			}
			
			pw.println();
			pw.print("rural consumption satisfaction:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralConsumptionSatisfactionNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralConsumptionSatisfaction[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("rural employment stability:");
			pw.println("Nation\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralEmploymentStabilityNation[i][k]);
					pw.println();
				}
			}
			pw.println();
			pw.print("\t");
			for(i=0 ; i<this.n_region[0] ; i++ ){
				pw.print(this.locatoinName.get(0).get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralEmploymentStability[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.println();
			pw.println();
			
			/*
			pw.print("life satisfaction rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.lifeSatisfactionRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("living condition rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.livingConditionRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();

			pw.print("income satisfaction rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.incomeSatisfactionRate[i][j][k]);
					pw.println();
				}
			}
			
			pw.println();
			pw.print("consumption satisfaction rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.consumptionSatisfactionRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("employment stability rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.employmentStabilityRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
							
			pw.print("urban life satisfaction rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}			
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanLifeSatisfactionRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("urban living condition rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanLivingConditionRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();

			pw.print("urban income satisfaction rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanIncomeSatisfactionRate[i][j][k]);
					pw.println();
				}
			}
			
			pw.println();
			pw.print("urban consumption satisfaction rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanConsumptionSatisfactionRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("urban employment stability rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.urbanEmploymentStabilityRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			pw.println();
	
			pw.print("rural life satisfaction rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralLifeSatisfactionRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("rural living condition rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralLivingConditionRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();

			pw.print("rural income satisfaction rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralIncomeSatisfactionRate[i][j][k]);
					pw.println();
				}
			}
			
			pw.println();
			pw.print("rural consumption satisfaction rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralConsumptionSatisfactionRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			
			pw.print("rural employment stability rate:");
			pw.print("\t");
			for(i=0 ; i<this.n_region ; i++ ){
				pw.print(this.locatoinName.get(i));
				for(j=0 ; j<this.gradeDepth ; j++) pw.print("\t");
			}
			pw.println();
			for(i=0 ; i<this.n_region ; i++ ) pw.print("\tVeryGood\tGood\tNormal\tBad\tVeryBad");
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region ; j++)
							for(k=0 ; k<this.gradeDepth ; k++) pw.print("\t"+this.ruralEmploymentStabilityRate[i][j][k]);
					pw.println();
				}
			}
			pw.println();
			*/
			pw.close();
		}catch(IOException e) {}
		
	}
	
	public void printScoreData(String outputFile){		
		int i,j;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
					
			pw.print("sample size:");
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.n_region[i] ; j++ ) pw.print("\t"+this.regionSamples[i][j]);
				pw.println();
			}
			pw.println();

			pw.println("life satisfaction:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][1]==1) 
					pw.println(this.startYear+i+"\t"+this.lifeSatisfactionScoreNation[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.lifeSatisfactionScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.println("living condition:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][2]==1) 
					pw.println(this.startYear+i+"\t"+this.livingConditionScoreNation[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.livingConditionScore[i][j]);
					pw.println();
				}
			}
			pw.println();

			pw.println("income satisfaction:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][3]==1) 
					pw.println(this.startYear+i+"\t"+this.incomeSatisfactionScoreNation[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.incomeSatisfactionScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.println("consumption satisfaction:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][4]==1) 
					pw.println(this.startYear+i+"\t"+this.consumptionSatisfactionScoreNation[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.consumptionSatisfactionScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.println("employment stability:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][5]==1) 
					pw.println(this.startYear+i+"\t"+this.employmentStabilityScoreNation[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.employmentStabilityScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			
				
			pw.println("urban sample size:");
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++ ) pw.print("\t"+this.urbanRegionSamples[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.println("urban life satisfaction:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][1]==1 && this.responseExistence[i][6]==1)
					pw.println(this.startYear+i+"\t"+this.lifeSatisfactionScoreUrban[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.urbanLifeSatisfactionScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.println("urban living condition:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][2]==1 && this.responseExistence[i][6]==1)
					pw.println(this.startYear+i+"\t"+this.livingConditionScoreUrban[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.urbanLivingConditionScore[i][j]);
					pw.println();
				}
			}
			pw.println();

			pw.println("urban income satisfaction:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][3]==1 && this.responseExistence[i][6]==1)
					pw.println(this.startYear+i+"\t"+this.incomeSatisfactionScoreUrban[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.urbanIncomeSatisfactionScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.println("urban consumption satisfaction:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][4]==1 && this.responseExistence[i][6]==1)
					pw.println(this.startYear+i+"\t"+this.consumptionSatisfactionScoreUrban[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.urbanConsumptionSatisfactionScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.println("urban employment stability:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][5]==1 && this.responseExistence[i][6]==1)
					pw.println(this.startYear+i+"\t"+this.employmentStabilityScoreUrban[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.urbanEmploymentStabilityScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			pw.println();
			
			pw.println("rural sample size:");
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++ ) pw.print("\t"+this.ruralRegionSamples[i][j]);
					pw.println();
				}
			}
			pw.println();
	
			pw.println("rural life satisfaction:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][1]==1 && this.responseExistence[i][6]==1)
					pw.println(this.startYear+i+"\t"+this.lifeSatisfactionScoreRural[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][1]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.ruralLifeSatisfactionScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.println("rural living condition:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][2]==1 && this.responseExistence[i][6]==1)
					pw.println(this.startYear+i+"\t"+this.livingConditionScoreRural[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][2]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.ruralLivingConditionScore[i][j]);
					pw.println();
				}
			}
			pw.println();

			pw.println("rural income satisfaction:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][3]==1 && this.responseExistence[i][6]==1)
					pw.println(this.startYear+i+"\t"+this.incomeSatisfactionScoreRural[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][3]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.ruralIncomeSatisfactionScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.println("rural consumption satisfaction:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][4]==1 && this.responseExistence[i][6]==1)
					pw.println(this.startYear+i+"\t"+this.consumptionSatisfactionScoreRural[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][4]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.ruralConsumptionSatisfactionScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.println("rural employment stability:");
			for(i=0 ; i<this.duration ; i++)
				if(this.responseExistence[i][5]==1 && this.responseExistence[i][6]==1)
					pw.println(this.startYear+i+"\t"+this.employmentStabilityScoreRural[i]);
			pw.println();
			
			for(i=0 ; i<this.n_region[0] ; i++ ) pw.print("\t"+this.locatoinName.get(0).get(i));
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				if(this.responseExistence[i][5]==1 && this.responseExistence[i][6]==1){
					pw.print((this.startYear+i));
					for(j=0 ; j<this.n_region[i] ; j++) pw.print("\t"+this.ruralEmploymentStabilityScore[i][j]);
					pw.println();
				}
			}
			pw.println();
			
			pw.close();
		}catch(IOException e) {}
		
	}
	
	public void clearUnusingMemory(){
		
		this.lifeSatisfaction = null;
		this.livingCondition = null;
		this.incomeSatisfaction = null;
		this.consumptionSatisfaction = null;
		this.employmentStability = null;
		
		this.ruralLifeSatisfaction = null;
		this.ruralLivingCondition = null;
		this.ruralIncomeSatisfaction = null;
		this.ruralConsumptionSatisfaction = null;
		this.ruralEmploymentStability = null;
		
		this.urbanLifeSatisfaction = null;
		this.urbanLivingCondition = null;
		this.urbanIncomeSatisfaction = null;
		this.urbanConsumptionSatisfaction = null;
		this.urbanEmploymentStability = null;
			
		this.lifeSatisfactionNation = null;
		this.livingConditionNation = null;
		this.incomeSatisfactionNation = null;
		this.consumptionSatisfactionNation = null;
		this.employmentStabilityNation = null;
		
		this.ruralLifeSatisfactionNation = null;
		this.ruralLivingConditionNation = null;
		this.ruralIncomeSatisfactionNation = null;
		this.ruralConsumptionSatisfactionNation = null;
		this.ruralEmploymentStabilityNation = null;
			
		this.urbanLifeSatisfactionNation = null;
		this.urbanLivingConditionNation = null;
		this.urbanIncomeSatisfactionNation = null;
		this.urbanConsumptionSatisfactionNation = null;
		this.urbanEmploymentStabilityNation = null;
		
		this.lifeSatisfactionCategorized = null;
		this.livingConditionCategorized = null;
		this.incomeSatisfactionCategorized = null;
		this.consumptionSatisfactionCategorized = null;
		this.employmentStabilityCategorized = null;
		
		this.ruralLifeSatisfactionCategorized = null;
		this.ruralLivingConditionCategorized = null;
		this.ruralIncomeSatisfactionCategorized = null;
		this.ruralConsumptionSatisfactionCategorized = null;
		this.ruralEmploymentStabilityCategorized = null;
		
		this.urbanLifeSatisfactionCategorized = null;
		this.urbanLivingConditionCategorized = null;
		this.urbanIncomeSatisfactionCategorized = null;
		this.urbanConsumptionSatisfactionCategorized = null;
		this.urbanEmploymentStabilityCategorized = null;
			
		this.lifeSatisfactionNationCategorized = null;
		this.livingConditionNationCategorized = null;
		this.incomeSatisfactionNationCategorized = null;
		this.consumptionSatisfactionNationCategorized = null;
		this.employmentStabilityNationCategorized = null;
		
		this.ruralLifeSatisfactionNationCategorized = null;
		this.ruralLivingConditionNationCategorized = null;
		this.ruralIncomeSatisfactionNationCategorized = null;
		this.ruralConsumptionSatisfactionNationCategorized = null;
		this.ruralEmploymentStabilityNationCategorized = null;
			
		this.urbanLifeSatisfactionNationCategorized = null;
		this.urbanLivingConditionNationCategorized = null;
		this.urbanIncomeSatisfactionNationCategorized = null;
		this.urbanConsumptionSatisfactionNationCategorized = null;
		this.urbanEmploymentStabilityNationCategorized = null;
		
		this.lifeSatisfactionRate = null;
		this.livingConditionRate = null;
		this.incomeSatisfactionRate = null;
		this.consumptionSatisfactionRate = null;
		this.employmentStabilityRate = null;
		
		this.ruralLifeSatisfactionRate = null;
		this.ruralLivingConditionRate = null;
		this.ruralIncomeSatisfactionRate = null;
		this.ruralConsumptionSatisfactionRate = null;
		this.ruralEmploymentStabilityRate = null;
		
		this.urbanLifeSatisfactionRate = null;
		this.urbanLivingConditionRate = null;
		this.urbanIncomeSatisfactionRate = null;
		this.urbanConsumptionSatisfactionRate = null;
		this.urbanEmploymentStabilityRate = null;
		
		this.lifeSatisfactionNationRate = null;
		this.livingConditionNationRate = null;
		this.incomeSatisfactionNationRate = null;
		this.consumptionSatisfactionNationRate = null;
		this.employmentStabilityNationRate = null;
		
		this.ruralLifeSatisfactionNationRate = null;
		this.ruralLivingConditionNationRate = null;
		this.ruralIncomeSatisfactionNationRate = null;
		this.ruralConsumptionSatisfactionNationRate = null;
		this.ruralEmploymentStabilityNationRate = null;
		
		this.urbanLifeSatisfactionNationRate = null;
		this.urbanLivingConditionNationRate = null;
		this.urbanIncomeSatisfactionNationRate = null;
		this.urbanConsumptionSatisfactionNationRate = null;
		this.urbanEmploymentStabilityNationRate = null;
	}
	
	public void clearSamplesMemory(){
		this.regionSamples = null;
		this.ruralRegionSamples = null;
		this.urbanRegionSamples = null;
		this.nationSamples = null;
		this.ruralSamples = null;
		this.urbanSamples = null;
		
		this.regionSamplesCategorized = null;
		this.ruralRegionSamplesCategorized = null;
		this.urbanRegionSamplesCategorized = null;
		this.nationSamplesCategorized = null;
		this.ruralSamplesCategorized = null;
		this.urbanSamplesCategorized = null;
	}
	
	public static void main(String[] args) {
		
		int n_category = 9;
		int regionClass = 0;		//0:si_do,    1: si_gun_gu,   2: eup_myun_dong
		int gradeDepth = 5;
		int startYear = 1998;
		int endYear = 2011;
		double[] gradeWeight = {2,1,0,-1,-2};
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/society/";
		String locationCodePath = filePath+"location_code/";
		String locationCodeFile = "location_code.txt";
		String outputFile = filePath+"response/"+startYear+"_"+endYear+"_satisfaction.txt";		
		String scoreFile = filePath+"response/"+startYear+"_"+endYear+"_satisfactionScore.txt";		
		
		SocietyMicrodataAnalyzer sma;
		sma = new SocietyMicrodataAnalyzer(n_category, regionClass, gradeDepth, startYear, endYear);
		
		System.out.print("location code reading: ");
		sma.readLocationCode(locationCodePath, locationCodeFile);
		System.out.println("complete");

		System.out.print("variables initializing: ");
		sma.initiate();
		System.out.println("complete");
		
		System.out.println("microdata reading: ");
		sma.extractSocieyMicrodata(filePath, "_Social_MicroData.txt");
		System.out.println("complete");	
		
		System.out.print("statistics calculating: ");
		sma.calculateSocietyStatistics();
		sma.calculateSatisfactionTotalScore(gradeWeight);
		System.out.println("complete");	
		
		System.out.print("data printing: ");
		//sma.printSatisfactionData(outputFile);
		sma.printScoreData(scoreFile);
		System.out.println("complete");	
				
		System.out.println("process complete.");

	}

}
