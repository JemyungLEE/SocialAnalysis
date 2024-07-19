package industrialDiversity;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class IndustryEntropyCalculator {

	/**
	 *  Subject: 
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2015.10.20
	 *  Last Modified Data: 2015.12.3
	 *  Department: Lab. of rural planning, Kyoto university
	 *  Description: 
	 */

	int startYear, endYear;
	int duration;
	int[] n_region;					//[year]
	int[] n_industry;				//[year]
	int[] n_category;				//[year]
	int n_sizeGroup;				//number of employee size groups
	int n_levelGroup;				//number of profit level groups 
	int n_ageGroup;				//number of groups by business years
	
	int industryClassDepth;	//1:1st_dae, 2:2nd_jung, 3: 3rd_so order depth
	int regionClassDepth;		//2:si_do,    5: si_gun_gu,   7: eup_myun_dong
	int categoryDepth;			//1:1st_dae, 2:2nd_jung, 3: 3rd_so order depth

	
	int[] industClassKey;		//1:대분류, 2:중분류, 3:소분류,  4:세분류,	5:세세분류
	int[] regionClassKey;		//2:시도,  5:시군구,  7:읍면동
	String[] industClassName;
	String[] regionClassName;

	double[] minSize, maxSize, count, total;		//[year]: minimum and maximum employees sizes of companies
	double[] minLevelProfit, maxLevelProfit;		//[year]: minimum and maximum industrial profit per employee
	double[] minAge, maxAge;								//[year]: minimum and maximum business years of companies
	double[] founded, closed, companies; 			//[year]: number of each type of companies in the year
	double[] inFounded, decreased, employees;	//[year]: number of employees in each type in the year
	ArrayList<double[]> sizeSection;
	ArrayList<double[]> levelSection;
	ArrayList<double[]> ageSection;
	
	ArrayList<HashMap<String, String>> locationHashMap;		//<code, name>
	ArrayList<HashMap<String, String>> industryHashMap;		//<code, name>
	
	ArrayList<ArrayList<String>> locatoinCode;				//use to find region's index	
	ArrayList<ArrayList<String>> industryCode;				//use to find industry's index
	ArrayList<ArrayList<String>> locatoinName;				//use to find region's index	
	ArrayList<ArrayList<String>> industryName;				//use to find industry's index
	ArrayList<ArrayList<String>> categoryCode;			//use to find industry's index	
	ArrayList<ArrayList<String>> categoryName;			//use to find industry's index
	ArrayList<ArrayList<ArrayList<String>>> industryListByCategory;		//use to categorize industries by categories
	
	double[]							nationTotal;												//[year]
	ArrayList<double[]>		employeeNation;										//[industry]
	ArrayList<double[]>		employeeRatioNation;								//[industry]
	ArrayList<double[][]>		employee; 												//[region][industry]
	ArrayList<double[][]>		employeeRatio; 										//[region][industry]
	ArrayList<double[]>		employeeSum;											//[region]
	ArrayList<double[][]>		employeeRatioMean;								//[region][industry]
	ArrayList<double[][][]>	employeeByCategory;								//[region][category][industry]
	ArrayList<double[][]>		employeeSumByCategory;						//[region][category]
	ArrayList<double[][][]>	employeeRatioByCategory;						//[region][category][industry]
	ArrayList<double[][]>		employeeByIndustry;								//[category][industry]
	ArrayList<double[]>		employeeSumByIndustry;						//[category]	
	ArrayList<double[][]>		employeeRatioByIndustry;						//[category][industry]
	
	double[]					   		entropyNation;											//[year]	
	ArrayList<double[]>   		entropy;													//[region]
	ArrayList<double[][]> 	entropyByCategory;								//[region][category]
	ArrayList<double[]>  	 	entropyByIndustry;									//[category]
	
	double[]							ruralTotal;												//[year]
	ArrayList<double[]>		employeeRural;										//[industry]
	ArrayList<double[]>		employeeRatioRural;								//[industry]
	ArrayList<double[][]> 	ruralEmployee; 										//[region][industry]
	ArrayList<double[][]> 	ruralEmployeeRatio; 								//[region][industry]
	ArrayList<double[]> 		ruralEmployeeSum;									//[region]
	ArrayList<double[][]> 	ruralEmployeeRatioMean;						//[region][industry]
	ArrayList<double[][][]>	ruralEmployeeByCategory;						//[region][category][industry]
	ArrayList<double[][]> 	ruralEmployeeSumByCategory;				//[region][category]
	ArrayList<double[][][]>	ruralEmployeeRatioByCategory;				//[region][category][industry]
	ArrayList<double[][]> 	ruralEmployeeByIndustry;						//[category][industry]
	ArrayList<double[]> 		ruralEmployeeSumByIndustry;				//[category]	
	ArrayList<double[][]> 	ruralEmployeeRatioByIndustry;				//[category][industry]
	
	double[]					   		entropyRural;											//[year]
	ArrayList<double[]>  	 	ruralEntropy;											//[region]
	ArrayList<double[][]> 	ruralEntropyByCategory;							//[region][category]
	ArrayList<double[]>  	 	ruralEntropyByIndustry;							//[category]
	
	double[]							urbanTotal;												//[year]
	ArrayList<double[]>		employeeUrban;										//[industry]
	ArrayList<double[]>		employeeRatioUrban;								//[industry]
	ArrayList<double[][]> 	urbanEmployee; 										//[region][industry]
	ArrayList<double[][]> 	urbanEmployeeRatio; 								//[region][industry]
	ArrayList<double[]> 		urbanEmployeeSum;								//[region]
	ArrayList<double[][]>		urbanEmployeeRatioMean;						//[region][industry]
	ArrayList<double[][][]>	urbanEmployeeByCategory;					//[region][category][industry]
	ArrayList<double[][]> 	urbanEmployeeSumByCategory;				//[region][category]
	ArrayList<double[][][]> 	urbanEmployeeRatioByCategory;			//[region][category][industry]
	ArrayList<double[][]> 	urbanEmployeeByIndustry;						//[category][industry]
	ArrayList<double[]> 		urbanEmployeeSumByIndustry;				//[category]	
	ArrayList<double[][]> 	urbanEmployeeRatioByIndustry;				//[category][industry]

	double[]					   		entropyUrban;											//[year]
	ArrayList<double[]>   		urbanEntropy;											//[region]
	ArrayList<double[][]> 	urbanEntropyByCategory;						//[region][category]
	ArrayList<double[]>   		urbanEntropyByIndustry;						//[category]

	
	double[]							nationTotalBySize;									//[year]
	ArrayList<double[]>		employeeNationBySize;							//[industry]
	ArrayList<double[]>		employeeRatioNationBySize;					//[industry]
	ArrayList<double[]> 		employeeSumBySize;								//[region]
	ArrayList<double[][]>		employeeBySize; 									//[region][size]
	ArrayList<double[][]>		employeeRatioBySize; 							//[region][size]
	ArrayList<double[][]> 	employeeRatioMeanBySize;					//[region][size]
	ArrayList<double[][]> 	employeeSumByCategoryBySize;			//[region][category]
	ArrayList<double[][][]>	employeeByCategoryBySize; 					//[region][category][size]
	ArrayList<double[][][]>	employeeRatioByCategoryBySize; 			//[region][category][size]
	ArrayList<double[]> 		employeeSumByIndustryBySize;				//[region]
	ArrayList<double[][]>		employeeByIndustryBySize; 					//[region][size]
	ArrayList<double[][]>		employeeRatioByIndustryBySize; 			//[region][size]

	double[]					   		entropyNationBySize;								//[year]
	ArrayList<double[]>		entropyBySize;										//[region]
	ArrayList<double[][]>		entropyBySizeByCategory;						//[region][category]
	ArrayList<double[]>		entropyBySizeByIndustry;						//[category]
	
	double[]							ruralTotalBySize;										//[year]
	ArrayList<double[]>		employeeRuralBySize;								//[industry]
	ArrayList<double[]>		employeeRatioRuralBySize;						//[industry]
	ArrayList<double[]> 		ruralEmployeeSumBySize;						//[region]
	ArrayList<double[][]>		ruralEmployeeBySize; 								//[region][size]
	ArrayList<double[][]>		ruralEmployeeRatioBySize; 					//[region][size]
	ArrayList<double[][]> 	ruralEmployeeRatioMeanBySize;				//[region][size]
	ArrayList<double[][]> 	ruralEmployeeSumByCategoryBySize;	//[region][category]
	ArrayList<double[][][]>	ruralEmployeeByCategoryBySize; 			//[region][category][size]
	ArrayList<double[][][]>	ruralEmployeeRatioByCategoryBySize; 	//[region][category][size]
	ArrayList<double[]> 		ruralEmployeeSumByIndustryBySize;		//[region]
	ArrayList<double[][]>		ruralEmployeeByIndustryBySize; 			//[region][size]
	ArrayList<double[][]>		ruralEmployeeRatioByIndustryBySize; 	//[region][size]

	double[]					   		entropyRuralBySize;								//[year]
	ArrayList<double[]>		ruralEntropyBySize;									//[region]
	ArrayList<double[][]>		ruralEntropyBySizeByCategory;				//[region][category]
	ArrayList<double[]>		ruralEntropyBySizeByIndustry;				//[category]
	
	double[]							urbanTotalBySize;									//[year]
	ArrayList<double[]>		employeeUrbanBySize;							//[industry]
	ArrayList<double[]>		employeeRatioUrbanBySize;					//[industry]
	ArrayList<double[]> 		urbanEmployeeSumBySize;						//[region]
	ArrayList<double[][]>		urbanEmployeeBySize; 							//[region][size]
	ArrayList<double[][]>		urbanEmployeeRatioBySize; 					//[region][size]
	ArrayList<double[][]> 	urbanEmployeeRatioMeanBySize;			//[region][size]
	ArrayList<double[][]> 	urbanEmployeeSumByCategoryBySize;	//[region][category]
	ArrayList<double[][][]>	urbanEmployeeByCategoryBySize; 		//[region][category][size]
	ArrayList<double[][][]>	urbanEmployeeRatioByCategoryBySize;	//[region][category][size]
	ArrayList<double[]> 		urbanEmployeeSumByIndustryBySize;	//[region]
	ArrayList<double[][]>		urbanEmployeeByIndustryBySize; 			//[region][size]
	ArrayList<double[][]>		urbanEmployeeRatioByIndustryBySize; 	//[region][size]

	double[]					   		entropyUrbanBySize;								//[year]
	ArrayList<double[]>		urbanEntropyBySize;								//[region]
	ArrayList<double[][]>		urbanEntropyBySizeByCategory;			//[region][category]
	ArrayList<double[]>		urbanEntropyBySizeByIndustry;				//[category]
	
	
	double[]							nationTotalByLevel;									//[year]
	ArrayList<double[]>		employeeNationByLevel;							//[industry]
	ArrayList<double[]>		employeeRatioNationByLevel;					//[industry]
	ArrayList<double[]> 		employeeSumByLevel;							//[region]
	ArrayList<double[][]>		employeeByLevel; 									//[region][level]
	ArrayList<double[][]>		employeeRatioByLevel; 							//[region][level]
	ArrayList<double[][]> 	employeeRatioMeanByLevel;					//[region][level]
	ArrayList<double[][]> 	employeeSumByCategoryByLevel;			//[region][category]
	ArrayList<double[][][]>	employeeByCategoryByLevel; 				//[region][category][level]
	ArrayList<double[][][]>	employeeRatioByCategoryByLevel;		//[region][category][level]
	ArrayList<double[]> 		employeeSumByIndustryByLevel;			//[region]
	ArrayList<double[][]>		employeeByIndustryByLevel; 					//[region][level]
	ArrayList<double[][]>		employeeRatioByIndustryByLevel; 			//[region][level]

	double[]					   		entropyNationByLevel;							//[year]
	ArrayList<double[]>		entropyByLevel;										//[region]
	ArrayList<double[][]>		entropyByLevelByCategory;					//[region][category]
	ArrayList<double[]>		entropyByLevelByIndustry;						//[category]
	
	double[]							ruralTotalByLevel;									//[year]
	ArrayList<double[]>		employeeRuralByLevel;							//[industry]
	ArrayList<double[]>		employeeRatioRuralByLevel;					//[industry]
	ArrayList<double[]> 		ruralEmployeeSumByLevel;						//[region]
	ArrayList<double[][]>		ruralEmployeeByLevel; 							//[region][level]
	ArrayList<double[][]>		ruralEmployeeRatioByLevel; 					//[region][level]
	ArrayList<double[][]> 	ruralEmployeeRatioMeanByLevel;			//[region][level]
	ArrayList<double[][]> 	ruralEmployeeSumByCategoryByLevel;	//[region][category]
	ArrayList<double[][][]>	ruralEmployeeByCategoryByLevel; 		//[region][category][level]
	ArrayList<double[][][]>	ruralEmployeeRatioByCategoryByLevel; //[region][category][level]
	ArrayList<double[]> 		ruralEmployeeSumByIndustryByLevel;	//[region]
	ArrayList<double[][]>		ruralEmployeeByIndustryByLevel; 			//[region][level]
	ArrayList<double[][]>		ruralEmployeeRatioByIndustryByLevel; 	//[region][level]

	double[]					   		entropyRuralByLevel;								//[year]
	ArrayList<double[]>		ruralEntropyByLevel;								//[region]
	ArrayList<double[][]>		ruralEntropyByLevelByCategory;			//[region][category]
	ArrayList<double[]>		ruralEntropyByLevelByIndustry;				//[category]
	
	double[]							urbanTotalByLevel;									//[year]
	ArrayList<double[]>		employeeUrbanByLevel;							//[industry]
	ArrayList<double[]>		employeeRatioUrbanByLevel;					//[industry]
	ArrayList<double[]> 		urbanEmployeeSumByLevel;					//[region]
	ArrayList<double[][]>		urbanEmployeeByLevel; 							//[region][level]
	ArrayList<double[][]>		urbanEmployeeRatioByLevel; 					//[region][level]
	ArrayList<double[][]> 	urbanEmployeeRatioMeanByLevel;			//[region][level]
	ArrayList<double[][]> 	urbanEmployeeSumByCategoryByLevel;//[region][category]
	ArrayList<double[][][]>	urbanEmployeeByCategoryByLevel; 		//[region][category][level]
	ArrayList<double[][][]>	urbanEmployeeRatioByCategoryByLevel;//[region][category][level]
	ArrayList<double[]> 		urbanEmployeeSumByIndustryByLevel;	//[region]
	ArrayList<double[][]>		urbanEmployeeByIndustryByLevel; 		//[region][level]
	ArrayList<double[][]>		urbanEmployeeRatioByIndustryByLevel;//[region][level]

	double[]					   		entropyUrbanByLevel;								//[year]
	ArrayList<double[]>		urbanEntropyByLevel;								//[region]
	ArrayList<double[][]>		urbanEntropyByLevelByCategory;			//[region][category]
	ArrayList<double[]>		urbanEntropyByLevelByIndustry;			//[category]
	
	double[]							nationTotalByAge;									//[year]
	ArrayList<double[]>		employeeNationByAge;							//[industry]
	ArrayList<double[]>		employeeRatioNationByAge;					//[industry]
	ArrayList<double[]> 		employeeSumByAge;								//[region]
	ArrayList<double[][]>		employeeByAge; 										//[region][age]
	ArrayList<double[][]>		employeeRatioByAge; 								//[region][age]
	ArrayList<double[][]> 	employeeRatioMeanByAge;						//[region][age]
	ArrayList<double[][]> 	employeeSumByCategoryByAge;			//[region][category]	
	ArrayList<double[][][]>	employeeByCategoryByAge; 					//[region][category][age]
	ArrayList<double[][][]>	employeeRatioByCategoryByAge;		 	//[region][category][age]
	ArrayList<double[]> 		employeeSumByIndustryByAge;				//[region]
	ArrayList<double[][]>		employeeByIndustryByAge; 					//[region][age]
	ArrayList<double[][]>		employeeRatioByIndustryByAge; 			//[region][age]

	double[]					   		entropyNationByAge;								//[year]
	ArrayList<double[]>		entropyByAge;											//[region]
	ArrayList<double[][]>		entropyByAgeByCategory;						//[region][category]
	ArrayList<double[]>		entropyByAgeByIndustry;						//[category]
	
	double[]							ruralTotalByAge;										//[year]
	ArrayList<double[]>		employeeRuralByAge;								//[industry]
	ArrayList<double[]>		employeeRatioRuralByAge;						//[industry]
	ArrayList<double[]> 		ruralEmployeeSumByAge;						//[region]
	ArrayList<double[][]>		ruralEmployeeByAge; 								//[region][age]
	ArrayList<double[][]>		ruralEmployeeRatioByAge; 						//[region][age]
	ArrayList<double[][]> 	ruralEmployeeRatioMeanByAge;				//[region][age]
	ArrayList<double[][]> 	ruralEmployeeSumByCategoryByAge;	//[region][category]
	ArrayList<double[][][]>	ruralEmployeeByCategoryByAge; 			//[region][category][age]
	ArrayList<double[][][]>	ruralEmployeeRatioByCategoryByAge; 	//[region][category][age]
	ArrayList<double[]> 		ruralEmployeeSumByIndustryByAge;		//[region]
	ArrayList<double[][]>		ruralEmployeeByIndustryByAge; 			//[region][age]
	ArrayList<double[][]>		ruralEmployeeRatioByIndustryByAge; 	//[region][age]

	double[]					   		entropyRuralByAge;									//[year]
	ArrayList<double[]>		ruralEntropyByAge;									//[region]
	ArrayList<double[][]>		ruralEntropyByAgeByCategory;				//[region][category]
	ArrayList<double[]>		ruralEntropyByAgeByIndustry;				//[category]
	
	double[]							urbanTotalByAge;									//[year]
	ArrayList<double[]>		employeeUrbanByAge;							//[industry]
	ArrayList<double[]>		employeeRatioUrbanByAge;					//[industry]
	ArrayList<double[]> 		urbanEmployeeSumByAge;						//[region]
	ArrayList<double[][]>		urbanEmployeeByAge; 							//[region][age]
	ArrayList<double[][]>		urbanEmployeeRatioByAge; 					//[region][age]
	ArrayList<double[][]> 	urbanEmployeeRatioMeanByAge;			//[region][age]
	ArrayList<double[][]> 	urbanEmployeeSumByCategoryByAge;	//[region][category]
	ArrayList<double[][][]>	urbanEmployeeByCategoryByAge; 			//[region][category][age]
	ArrayList<double[][][]>	urbanEmployeeRatioByCategoryByAge;	//[region][category][age]
	ArrayList<double[]> 		urbanEmployeeSumByIndustryByAge;	//[region]
	ArrayList<double[][]>		urbanEmployeeByIndustryByAge; 			//[region][age]
	ArrayList<double[][]>		urbanEmployeeRatioByIndustryByAge; 	//[region][age]

	double[]					   		entropyUrbanByAge;								//[year]
	ArrayList<double[]>		urbanEntropyByAge;								//[region]
	ArrayList<double[][]>		urbanEntropyByAgeByCategory;				//[region][category]
	ArrayList<double[]>		urbanEntropyByAgeByIndustry;				//[category]
	
	public IndustryEntropyCalculator(int start, int end){
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;
		
		this.initiate();
	}
		
	public IndustryEntropyCalculator(int start, int end, int categoryClass, int industryClass, int regionClass){
		
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;

		this.initiate();
		this.setClassDepth(categoryClass, industryClass, regionClass);
	}
	
	public IndustryEntropyCalculator(int start, int end, int categoryClass, int industryClass, int regionClass,
															int sizeGroups, int levelGroups, int ageGroups){
		
		this.startYear = start;
		this.endYear = end;
		this.duration = end - start + 1;

		this.initiate();
		this.initiateGroupVariables(sizeGroups, levelGroups, ageGroups);
		this.setClassDepth(categoryClass, industryClass, regionClass);
	}
	
	public void initiate(){
		
		this.n_region = new int[this.duration];
		this.n_industry = new int[this.duration];
		this.n_category =  new int[this.duration];
		
		this.employee = new ArrayList<double[][]>();
		this.employeeSum = new ArrayList<double[]>();
		this.employeeRatio = new ArrayList<double[][]>();
		this.entropy =  new ArrayList<double[]>();
		
		this.nationTotal = new double[this.duration];
		this.employeeNation =  new ArrayList<double[]>();
		this.employeeRatioNation =  new ArrayList<double[]>();
		this.entropyNation = new double[this.duration];
		
		this.employeeSumByCategory = new ArrayList<double[][]>();
		this.employeeByCategory = new ArrayList<double[][][]>();
		this.employeeRatioByCategory = new ArrayList<double[][][]>();
		this.entropyByCategory = new ArrayList<double[][]>();
		
		this.employeeByIndustry = new ArrayList<double[][]>();
		this.employeeSumByIndustry = new ArrayList<double[]>();
		this.employeeRatioByIndustry = new ArrayList<double[][]>();
		this.entropyByIndustry =  new ArrayList<double[]>();
		
		this.ruralEmployee = new ArrayList<double[][]>();
		this.ruralEmployeeSum = new ArrayList<double[]>();
		this.ruralEmployeeRatio = new ArrayList<double[][]>();
		this.ruralEntropy =  new ArrayList<double[]>();
		
		this.ruralTotal = new double[this.duration];
		this.employeeRural =  new ArrayList<double[]>();
		this.employeeRatioRural =  new ArrayList<double[]>();
		this.entropyRural = new double[this.duration];
		
		this.ruralEmployeeSumByCategory = new ArrayList<double[][]>();
		this.ruralEmployeeByCategory = new ArrayList<double[][][]>();
		this.ruralEmployeeRatioByCategory = new ArrayList<double[][][]>();
		this.ruralEntropyByCategory = new ArrayList<double[][]>();
		
		this.ruralEmployeeByIndustry = new ArrayList<double[][]>();
		this.ruralEmployeeSumByIndustry = new ArrayList<double[]>();
		this.ruralEmployeeRatioByIndustry = new ArrayList<double[][]>();
		this.ruralEntropyByIndustry =  new ArrayList<double[]>();
		
		this.urbanEmployee = new ArrayList<double[][]>();
		this.urbanEmployeeSum = new ArrayList<double[]>();
		this.urbanEmployeeRatio = new ArrayList<double[][]>();
		this.urbanEntropy =  new ArrayList<double[]>();
		
		this.urbanTotal = new double[this.duration];
		this.employeeUrban =  new ArrayList<double[]>();
		this.employeeRatioUrban =  new ArrayList<double[]>();
		this.entropyUrban = new double[this.duration];
		
		this.urbanEmployeeSumByCategory = new ArrayList<double[][]>();
		this.urbanEmployeeByCategory = new ArrayList<double[][][]>();
		this.urbanEmployeeRatioByCategory = new ArrayList<double[][][]>();
		this.urbanEntropyByCategory = new ArrayList<double[][]>();
		
		this.urbanEmployeeByIndustry = new ArrayList<double[][]>();
		this.urbanEmployeeSumByIndustry = new ArrayList<double[]>();
		this.urbanEmployeeRatioByIndustry = new ArrayList<double[][]>();
		this.urbanEntropyByIndustry =  new ArrayList<double[]>();
	}
	
	public void initiateGroupVariables(int sizeGroups, int levelGroups, int ageGroups){

		this.n_sizeGroup = sizeGroups;
		this.n_levelGroup = levelGroups;
		this.n_ageGroup = ageGroups;
		
		this.minSize = new double[this.duration];
		this.maxSize = new double[this.duration];
		this.minLevelProfit = new double[this.duration];
		this.maxLevelProfit = new double[this.duration];
		this.minAge = new double[this.duration];
		this.maxAge = new double[this.duration];
		this.count = new double[this.duration];
		this.total = new double[this.duration];
		this.founded = new double[this.duration];
		this.closed = new double[this.duration];
		this.companies = new double[this.duration];
		this.inFounded = new double[this.duration];
		this.decreased = new double[this.duration];
		this.employees = new double[this.duration];
		
		this.sizeSection = new ArrayList<double[]>();
		this.levelSection = new ArrayList<double[]>();
		this.ageSection = new ArrayList<double[]>();
		
		this.employeeBySize = new ArrayList<double[][]>();
		this.employeeSumBySize = new ArrayList<double[]>();
		this.employeeRatioBySize = new ArrayList<double[][]>();
		this.entropyBySize =  new ArrayList<double[]>();
		
		this.nationTotalBySize = new double[this.duration];
		this.employeeNationBySize =  new ArrayList<double[]>();
		this.employeeRatioNationBySize =  new ArrayList<double[]>();
		this.entropyNationBySize = new double[this.duration];
		
		this.employeeSumByCategoryBySize = new ArrayList<double[][]>();
		this.employeeByCategoryBySize = new ArrayList<double[][][]>();
		this.employeeRatioByCategoryBySize = new ArrayList<double[][][]>();
		this.entropyBySizeByCategory = new ArrayList<double[][]>();
		
		this.employeeByIndustryBySize = new ArrayList<double[][]>();
		this.employeeSumByIndustryBySize = new ArrayList<double[]>();
		this.employeeRatioByIndustryBySize = new ArrayList<double[][]>();
		this.entropyBySizeByIndustry =  new ArrayList<double[]>();
		
		this.ruralEmployeeBySize = new ArrayList<double[][]>();
		this.ruralEmployeeSumBySize = new ArrayList<double[]>();
		this.ruralEmployeeRatioBySize = new ArrayList<double[][]>();
		this.ruralEntropyBySize =  new ArrayList<double[]>();
		
		this.ruralTotalBySize = new double[this.duration];
		this.employeeRuralBySize =  new ArrayList<double[]>();
		this.employeeRatioRuralBySize =  new ArrayList<double[]>();
		this.entropyRuralBySize = new double[this.duration];
		
		this.ruralEmployeeSumByCategoryBySize = new ArrayList<double[][]>();
		this.ruralEmployeeByCategoryBySize = new ArrayList<double[][][]>();
		this.ruralEmployeeRatioByCategoryBySize = new ArrayList<double[][][]>();
		this.ruralEntropyBySizeByCategory = new ArrayList<double[][]>();
		
		this.ruralEmployeeByIndustryBySize = new ArrayList<double[][]>();
		this.ruralEmployeeSumByIndustryBySize = new ArrayList<double[]>();
		this.ruralEmployeeRatioByIndustryBySize = new ArrayList<double[][]>();
		this.ruralEntropyBySizeByIndustry =  new ArrayList<double[]>();
		
		this.urbanEmployeeBySize = new ArrayList<double[][]>();
		this.urbanEmployeeSumBySize = new ArrayList<double[]>();
		this.urbanEmployeeRatioBySize = new ArrayList<double[][]>();
		this.urbanEntropyBySize =  new ArrayList<double[]>();
		
		this.urbanTotalBySize = new double[this.duration];
		this.employeeUrbanBySize =  new ArrayList<double[]>();
		this.employeeRatioUrbanBySize =  new ArrayList<double[]>();
		this.entropyUrbanBySize = new double[this.duration];
		
		this.urbanEmployeeSumByCategoryBySize = new ArrayList<double[][]>();
		this.urbanEmployeeByCategoryBySize = new ArrayList<double[][][]>();
		this.urbanEmployeeRatioByCategoryBySize = new ArrayList<double[][][]>();
		this.urbanEntropyBySizeByCategory = new ArrayList<double[][]>();
		
		this.urbanEmployeeByIndustryBySize = new ArrayList<double[][]>();
		this.urbanEmployeeSumByIndustryBySize = new ArrayList<double[]>();
		this.urbanEmployeeRatioByIndustryBySize = new ArrayList<double[][]>();
		this.urbanEntropyBySizeByIndustry =  new ArrayList<double[]>();
		
	
		this.employeeByLevel = new ArrayList<double[][]>();
		this.employeeSumByLevel = new ArrayList<double[]>();
		this.employeeRatioByLevel = new ArrayList<double[][]>();
		this.entropyByLevel =  new ArrayList<double[]>();
		
		this.nationTotalByLevel = new double[this.duration];
		this.employeeNationByLevel =  new ArrayList<double[]>();
		this.employeeRatioNationByLevel =  new ArrayList<double[]>();
		this.entropyNationByLevel = new double[this.duration];
		
		this.employeeSumByCategoryByLevel = new ArrayList<double[][]>();
		this.employeeByCategoryByLevel = new ArrayList<double[][][]>();
		this.employeeRatioByCategoryByLevel = new ArrayList<double[][][]>();
		this.entropyByLevelByCategory = new ArrayList<double[][]>();
		
		this.employeeByIndustryByLevel = new ArrayList<double[][]>();
		this.employeeSumByIndustryByLevel = new ArrayList<double[]>();
		this.employeeRatioByIndustryByLevel = new ArrayList<double[][]>();
		this.entropyByLevelByIndustry =  new ArrayList<double[]>();
		
		this.ruralEmployeeByLevel = new ArrayList<double[][]>();
		this.ruralEmployeeSumByLevel = new ArrayList<double[]>();
		this.ruralEmployeeRatioByLevel = new ArrayList<double[][]>();
		this.ruralEntropyByLevel =  new ArrayList<double[]>();
		
		this.ruralTotalByLevel = new double[this.duration];
		this.employeeRuralByLevel =  new ArrayList<double[]>();
		this.employeeRatioRuralByLevel =  new ArrayList<double[]>();
		this.entropyRuralByLevel = new double[this.duration];

		this.ruralEmployeeSumByCategoryByLevel = new ArrayList<double[][]>();
		this.ruralEmployeeByCategoryByLevel = new ArrayList<double[][][]>();
		this.ruralEmployeeRatioByCategoryByLevel = new ArrayList<double[][][]>();
		this.ruralEntropyByLevelByCategory = new ArrayList<double[][]>();
		
		this.ruralEmployeeByIndustryByLevel = new ArrayList<double[][]>();
		this.ruralEmployeeSumByIndustryByLevel = new ArrayList<double[]>();
		this.ruralEmployeeRatioByIndustryByLevel = new ArrayList<double[][]>();
		this.ruralEntropyByLevelByIndustry =  new ArrayList<double[]>();
		
		this.urbanEmployeeByLevel = new ArrayList<double[][]>();
		this.urbanEmployeeSumByLevel = new ArrayList<double[]>();
		this.urbanEmployeeRatioByLevel = new ArrayList<double[][]>();
		this.urbanEntropyByLevel =  new ArrayList<double[]>();
		
		this.urbanTotalByLevel = new double[this.duration];
		this.employeeUrbanByLevel =  new ArrayList<double[]>();
		this.employeeRatioUrbanByLevel =  new ArrayList<double[]>();
		this.entropyUrbanByLevel = new double[this.duration];

		this.urbanEmployeeSumByCategoryByLevel = new ArrayList<double[][]>();
		this.urbanEmployeeByCategoryByLevel = new ArrayList<double[][][]>();
		this.urbanEmployeeRatioByCategoryByLevel = new ArrayList<double[][][]>();
		this.urbanEntropyByLevelByCategory = new ArrayList<double[][]>();
		
		this.urbanEmployeeByIndustryByLevel = new ArrayList<double[][]>();
		this.urbanEmployeeSumByIndustryByLevel = new ArrayList<double[]>();
		this.urbanEmployeeRatioByIndustryByLevel = new ArrayList<double[][]>();
		this.urbanEntropyByLevelByIndustry =  new ArrayList<double[]>();
		
		
		this.employeeByAge = new ArrayList<double[][]>();
		this.employeeSumByAge = new ArrayList<double[]>();
		this.employeeRatioByAge = new ArrayList<double[][]>();
		this.entropyByAge =  new ArrayList<double[]>();
		
		this.nationTotalByAge = new double[this.duration];
		this.employeeNationByAge =  new ArrayList<double[]>();
		this.employeeRatioNationByAge =  new ArrayList<double[]>();
		this.entropyNationByAge = new double[this.duration];

		this.employeeSumByCategoryByAge = new ArrayList<double[][]>();
		this.employeeByCategoryByAge = new ArrayList<double[][][]>();
		this.employeeRatioByCategoryByAge = new ArrayList<double[][][]>();
		this.entropyByAgeByCategory = new ArrayList<double[][]>();
		
		this.employeeByIndustryByAge = new ArrayList<double[][]>();
		this.employeeSumByIndustryByAge = new ArrayList<double[]>();
		this.employeeRatioByIndustryByAge = new ArrayList<double[][]>();
		this.entropyByAgeByIndustry =  new ArrayList<double[]>();
		
		this.ruralEmployeeByAge = new ArrayList<double[][]>();
		this.ruralEmployeeSumByAge = new ArrayList<double[]>();
		this.ruralEmployeeRatioByAge = new ArrayList<double[][]>();
		this.ruralEntropyByAge =  new ArrayList<double[]>();
		
		this.ruralTotalByAge = new double[this.duration];
		this.employeeRuralByAge =  new ArrayList<double[]>();
		this.employeeRatioRuralByAge =  new ArrayList<double[]>();
		this.entropyRuralByAge = new double[this.duration];

		this.ruralEmployeeSumByCategoryByAge = new ArrayList<double[][]>();
		this.ruralEmployeeByCategoryByAge = new ArrayList<double[][][]>();
		this.ruralEmployeeRatioByCategoryByAge = new ArrayList<double[][][]>();
		this.ruralEntropyByAgeByCategory = new ArrayList<double[][]>();
		
		this.ruralEmployeeByIndustryByAge = new ArrayList<double[][]>();
		this.ruralEmployeeSumByIndustryByAge = new ArrayList<double[]>();
		this.ruralEmployeeRatioByIndustryByAge = new ArrayList<double[][]>();
		this.ruralEntropyByAgeByIndustry =  new ArrayList<double[]>();
		
		this.urbanEmployeeByAge = new ArrayList<double[][]>();
		this.urbanEmployeeSumByAge = new ArrayList<double[]>();
		this.urbanEmployeeRatioByAge = new ArrayList<double[][]>();
		this.urbanEntropyByAge =  new ArrayList<double[]>();
		
		this.urbanTotalByAge = new double[this.duration];
		this.employeeUrbanByAge =  new ArrayList<double[]>();
		this.employeeRatioUrbanByAge =  new ArrayList<double[]>();
		this.entropyUrbanByAge = new double[this.duration];

		this.urbanEmployeeSumByCategoryByAge = new ArrayList<double[][]>();
		this.urbanEmployeeByCategoryByAge = new ArrayList<double[][][]>();
		this.urbanEmployeeRatioByCategoryByAge = new ArrayList<double[][][]>();
		this.urbanEntropyByAgeByCategory = new ArrayList<double[][]>();
		
		this.urbanEmployeeByIndustryByAge = new ArrayList<double[][]>();
		this.urbanEmployeeSumByIndustryByAge = new ArrayList<double[]>();
		this.urbanEmployeeRatioByIndustryByAge = new ArrayList<double[][]>();
		this.urbanEntropyByAgeByIndustry =  new ArrayList<double[]>();
	}
	
	public void setIndexMap(){
		
		this.locationHashMap = new ArrayList<HashMap<String, String>>();
		this.industryHashMap = new ArrayList<HashMap<String, String>>();			
		this.locatoinCode = new ArrayList<ArrayList<String>>();			
		this.industryCode = new ArrayList<ArrayList<String>>();		
		this.locatoinName = new ArrayList<ArrayList<String>>();	
		this.industryName = new ArrayList<ArrayList<String>>();	
		this.categoryCode = new ArrayList<ArrayList<String>>();	
		this.categoryName  = new ArrayList<ArrayList<String>>();	
		this.industryListByCategory  = new ArrayList<ArrayList<ArrayList<String>>>();	
	}
	
	public void setClassDepth(int categoryClass, int industryClass, int regionClass){
		industClassKey = new int[]{1,2,3,4,5};
		regionClassKey = new int[]{2,5,7};
		industClassName = new String[]{"1st","2nd","3rd","4th","5th"};
		regionClassName = new String[]{"do","gun","myun"};
		
		this.categoryDepth = this.industClassKey[categoryClass];
		this.industryClassDepth = this.industClassKey[industryClass];
		this.regionClassDepth = this.regionClassKey[regionClass];
	}
	
	public double[] setNormalGroupSection(int n_group, double min, double max){
		
		double section[] = new double[n_group+1];
		double span = (max - min)/n_group;
		
		for(int i=0 ; i<=n_group ; i++) section[i] = min+(span*i);
		
		return section;
	}
	
	public double[] setLogarithmGroupSection(int n_group, double min, double max){
		
		double tmp;
		double section[] = new double[n_group+1];
		double logMin,  logMax;
		double span;
		
		if(min>0){
			 logMin = Math.log10(min);
			 logMax = Math.log10(max);
			 span = (logMax - logMin)/n_group;
			for(int i=0 ; i<=n_group ; i++) section[i] = Math.pow(10, logMin+(span*i));
		}
		else if(min<=0){
			tmp = 1 - min;
			logMin = Math.log10(tmp);
			 logMax = Math.log10(max + tmp);
			 span = (logMax - logMin)/n_group;
			for(int i=0 ; i<=n_group ; i++) section[i] = Math.pow(10, logMin+(span*i)) - tmp;
		}
		
		return section;
	}
	
	public void readLocationCode(int year, String inputFile){	
		String tmpCode, tmpName;
		HashMap<String, String> tmpHashMap;
		ArrayList<String> tmpCodeList, tmpNameList;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			tmpHashMap = new HashMap<String, String>();
			tmpCodeList = new ArrayList<String>();
			tmpNameList = new ArrayList<String>();
			
			while(scan.hasNext()){
				tmpCode = scan.next().trim();
				tmpName = scan.next().trim();	
				
				tmpHashMap.put(tmpCode, tmpName);			//save all region code and name
				
				if( tmpCode.length() == this.regionClassDepth){			
					tmpCodeList.add(tmpCode);
					tmpNameList.add(tmpName);
				}
			}				
			this.n_region[year] = tmpCodeList.size();
			this.locationHashMap.add(tmpHashMap);		
			this.locatoinCode.add(tmpCodeList);
			this.locatoinName.add(tmpNameList);
			
			scan.close();	
		} catch(IOException e) {
			System.err.println("location code reading error.");
		}
			
	}
	
	public void readIndustryCode(int year, String inputFile){		
		String tmpCode, tmpName;
		HashMap<String, String> tmpHashMap;
		ArrayList<String> tmpCodeList, tmpNameList;
		ArrayList<String> tmpCategoryCodeList, tmpCategoryNameList;
		ArrayList<String> tmpIndustryList;
		ArrayList<ArrayList<String>> tmpIndustryByCategoryList;
		
		try{
			File file = new File(inputFile);
			Scanner scan = new Scanner(file);
			
			tmpHashMap = new HashMap<String, String>();
			tmpCodeList = new ArrayList<String>();
			tmpNameList = new ArrayList<String>();
			tmpCategoryCodeList = new ArrayList<String>();
			tmpCategoryNameList = new ArrayList<String>();
			tmpIndustryList = null;
			tmpIndustryByCategoryList = new ArrayList<ArrayList<String>>();
			
			while(scan.hasNext()){
				tmpCode = scan.next().trim();
				tmpName = scan.next().trim();
				
				tmpHashMap.put(tmpCode, tmpName);		//save all industry code and name
				
				if( tmpCode.length() == this.industryClassDepth){
					tmpCodeList.add(tmpCode);
					tmpNameList.add(tmpName);
					tmpIndustryList.add(tmpCode);
				}else if ( tmpCode.length() == this.categoryDepth){				
					tmpCategoryCodeList.add(tmpCode);
					tmpCategoryNameList.add(tmpName);
					if(tmpIndustryList != null) tmpIndustryByCategoryList.add(tmpIndustryList);
					tmpIndustryList = new ArrayList<String>();
				}
			}
			tmpIndustryByCategoryList.add(tmpIndustryList);
			
			this.n_industry[year] = tmpCodeList.size();
			this.n_category[year] = tmpCategoryCodeList.size();
			
			this.industryHashMap.add(tmpHashMap);
			this.industryCode.add(tmpCodeList);
			this.industryName.add(tmpNameList);
			this.categoryCode.add(tmpCategoryCodeList);
			this.categoryName.add(tmpCategoryNameList);
			this.industryListByCategory.add(tmpIndustryByCategoryList);
			
			scan.close();	
		} catch(IOException e) {
			System.err.println("industry code reading error.");
		}
	}
	
	public void readStandardCodes(String tmpPath){
		this.setIndexMap();
		for(int i=0 ; i<this.duration ; i++){				
			this.readIndustryCode(i, tmpPath+"industry_code/"+(this.startYear+i)+"_industry_code.txt");
			this.readLocationCode(i, tmpPath+"location_code/"+(this.startYear+i)+"_location_code.txt");		
		}
	}
	
	public void readEmployee(String filePath, String fileName){
		
		int i, j, k, l;
		String region;
		String industry;
		String category;
		int regionIndex;
		int industryIndex;
		int categoryIndex;
		int min, max, count, avg;			//for minimum, maximum, and average employees
		double workers;
		String inputFile;				
		String tmpRegion, tmpRegionCode, tmpClassifier, tmpCategory;
		
		double			nationTotal;
		double[]		employeeNation;						//[industry]
		double[][]	employee;								//[region][industry]
		double[][][]	employeeByCategory;				//[region][category][industry]
		double[][]	employeeByIndustry;				//[category][industry]
		
		double			ruralTotal;
		double[]		employeeRural;						//[industry]
		double[][]	ruralEmployee;						//[region][industry]
		double[][][]	ruralEmployeeByCategory;		//[region][category][industry]
		double[][]	ruralEmployeeByIndustry;		//[category][industry]
		
		double			urbanTotal;
		double[]		employeeUrban;						//[industry]
		double[][]	urbanEmployee;						//[region][industry]
		double[][][]	urbanEmployeeByCategory;	//[region][category][industry]
		double[][]	urbanEmployeeByIndustry;		//[category][industry]
			
		for(i=0 ; i<this.duration ; i++){			
			
			min = 10000;
			max = 0;
			avg = 0;
			count = 0;
			
			System.out.print(" "+(this.startYear+i)+" ");
			
			inputFile = filePath + (this.startYear+i) + fileName;			
			
			/*** initiate variables ***/
			employeeNation = new double[this.n_industry[i]];	
			employee = new double[this.n_region[i]][this.n_industry[i]];			
			employeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_industry[i]];
			employeeByIndustry = new double[this.n_category[i]][this.n_industry[i]];
			
			employeeRural = new double[this.n_industry[i]];	
			ruralEmployee = new double[this.n_region[i]][this.n_industry[i]];			
			ruralEmployeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_industry[i]];
			ruralEmployeeByIndustry = new double[this.n_category[i]][this.n_industry[i]];
			
			employeeUrban = new double[this.n_industry[i]];	
			urbanEmployee = new double[this.n_region[i]][this.n_industry[i]];			
			urbanEmployeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_industry[i]];
			urbanEmployeeByIndustry = new double[this.n_category[i]][this.n_industry[i]];
			
			nationTotal = 0.0;
			ruralTotal = 0.0;
			urbanTotal = 0.0;
			for(j=0 ; j<this.n_region[i] ; j++){	
				for(k=0 ; k<this.n_industry[i] ; k++){
					employee[j][k] = 0.0;		
					ruralEmployee[j][k] = 0.0;	
					urbanEmployee[j][k] = 0.0;	
					for(l=0 ; l<this.n_category[i] ; l++){
						employeeByCategory[i][l][k] = 0.0;
						ruralEmployeeByCategory[i][l][k] = 0.0;
						urbanEmployeeByCategory[i][l][k] = 0.0;
					}
				}
			}
			for(k=0 ; k<this.n_industry[i] ; k++){
				employeeNation[k] = 0.0;
				employeeRural[k] = 0.0;
				employeeUrban[k] = 0.0;
				for(l=0 ; l<this.n_category[i] ; l++){
					employeeByIndustry[l][k] = 0.0;
					ruralEmployeeByIndustry[l][k] = 0.0;
					urbanEmployeeByIndustry[l][k] = 0.0;
				}
			}
			
			/*** extract microdata ***/
			try{
				File file = new File(inputFile);
				Scanner scan = new Scanner(file);
				
				scan.nextLine();	//read column name
				
				while(scan.hasNext()){					
					/*** read micro-data code ***/
					scan.nextInt();		//read founding year		
					tmpRegionCode = scan.next();
					tmpCategory = scan.next();
					region = tmpRegionCode.substring(0, this.regionClassDepth);
					industry = tmpCategory.substring(1, 1+this.industryClassDepth); 
					if(this.categoryDepth>1) category = tmpCategory.substring(1,1+ this.categoryDepth); 
					else category = tmpCategory.substring(0, this.categoryDepth); 
					workers = scan.nextDouble();
					scan.next();		//read business type
					scan.next();		//read store type
									
					//check minimum and maximum employees
					avg += workers;
					if(workers>max) max = (int) workers;
					if(workers>0 && min>workers) min = (int) workers;
					count++;
					
					/*** find index ***/
					regionIndex = this.locatoinCode.get(i).indexOf(region);
					industryIndex = this.industryCode.get(i).indexOf(industry);
					categoryIndex = this.categoryCode.get(i).indexOf(category);
					
					//System.out.println(region+" "+industry+" "+category+" "+workers+" "+regionIndex+" "+industryIndex+" "+categoryIndex);
					
					/*** add workers ***/
					if(regionIndex >= 0){
						if(industryIndex >= 0){
							nationTotal += workers;
							employeeNation[industryIndex] += workers;
							employee[regionIndex][industryIndex] += workers;
							employeeByCategory[regionIndex][categoryIndex][industryIndex] += workers;
							employeeByIndustry[categoryIndex][industryIndex] += workers;
							
							if(this.locationHashMap.get(i).containsKey(tmpRegionCode)){
								tmpRegion = this.locationHashMap.get(i).get(tmpRegionCode);
								tmpClassifier = tmpRegion.substring(tmpRegion.length()-1);
								if(tmpClassifier.equals("동")){
									urbanTotal += workers;
									employeeUrban[industryIndex] += workers;
									urbanEmployee[regionIndex][industryIndex] += workers;
									urbanEmployeeByCategory[regionIndex][categoryIndex][industryIndex] += workers;
									urbanEmployeeByIndustry[categoryIndex][industryIndex] += workers;
								}else if(tmpClassifier.equals("읍") || tmpClassifier.equals("면")
											|| tmpClassifier.equals("소") || tmpClassifier.equals("림")
											|| tmpClassifier.equals("서") || tmpClassifier.equals("성")){
									ruralTotal += workers;
									employeeRural[industryIndex] += workers;
									ruralEmployee[regionIndex][industryIndex] += workers;
									ruralEmployeeByCategory[regionIndex][categoryIndex][industryIndex] += workers;
									ruralEmployeeByIndustry[categoryIndex][industryIndex] += workers;
								}else{
									System.err.println("region classification by name error:"+tmpRegion+" "+tmpClassifier);
								}
							}else{
								System.err.println("no match region code error: "+tmpRegionCode);
							}
						}	
					}
				}
				
				scan.close();	
			} catch(IOException e) {}
			
			this.nationTotal[i] = nationTotal;
			this.employeeNation.add(employeeNation);
			this.employee.add(employee);
			this.employeeByCategory.add(employeeByCategory);
			this.employeeByIndustry.add(employeeByIndustry);
			
			this.ruralTotal[i] = ruralTotal;
			this.employeeRural.add(employeeRural);
			this.ruralEmployee.add(ruralEmployee);
			this.ruralEmployeeByCategory.add(ruralEmployeeByCategory);
			this.ruralEmployeeByIndustry.add(ruralEmployeeByIndustry);
			
			this.urbanTotal[i] = urbanTotal;
			this.employeeUrban.add(employeeUrban);
			this.urbanEmployee.add(urbanEmployee);
			this.urbanEmployeeByCategory.add(urbanEmployeeByCategory);
			this.urbanEmployeeByIndustry.add(urbanEmployeeByIndustry);
			
			//System.out.println("\tmin:\t"+min+"\tmax:\t"+max+"\taverage:\t"+((double)avg/count));
		}
	}		

	public void readEmployeeBySize(int n_group, String filePath, String fileName){
		int i, j, k, l;
		String region;
		String category;
		int regionIndex;
		int categoryIndex;
		int sizeIndex;
		double workers;
		double min, max, count, total;
		double[] section;
		String inputFile;				
		String tmpRegion, tmpRegionCode, tmpClassifier, tmpCategory;
		
		double			nationTotal;
		double[]		employeeNation;						//[size]
		double[][]	employee;								//[region][size]
		double[][][]	employeeByCategory;				//[region][category][size]
		double[][]	employeeByIndustry;				//[category][size]
		
		double			ruralTotal;
		double[]		employeeRural;						//[size]
		double[][]	ruralEmployee;						//[region][size]
		double[][][]	ruralEmployeeByCategory;		//[region][category][size]
		double[][]	ruralEmployeeByIndustry;		//[category][size]
		
		double			urbanTotal;
		double[]		employeeUrban;						//[size]
		double[][]	urbanEmployee;						//[region][size]
		double[][][]	urbanEmployeeByCategory;	//[region][category][size]
		double[][]	urbanEmployeeByIndustry;		//[category][size]
		
		for(i=0 ; i<this.duration ; i++){		
		
			System.out.print(" "+(this.startYear+i)+" ");
			
			inputFile = filePath + (this.startYear+i) + fileName;
			
			/*** initiate variables ***/
			employeeNation = new double[this.n_sizeGroup];	
			employee = new double[this.n_region[i]][this.n_sizeGroup];			
			employeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_sizeGroup];
			employeeByIndustry = new double[this.n_category[i]][this.n_sizeGroup];
			
			employeeRural = new double[this.n_sizeGroup];
			ruralEmployee = new double[this.n_region[i]][this.n_sizeGroup];			
			ruralEmployeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_sizeGroup];
			ruralEmployeeByIndustry = new double[this.n_category[i]][this.n_sizeGroup];
			
			employeeUrban = new double[this.n_sizeGroup];
			urbanEmployee = new double[this.n_region[i]][this.n_sizeGroup];			
			urbanEmployeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_sizeGroup];
			urbanEmployeeByIndustry = new double[this.n_category[i]][this.n_sizeGroup];
			
			nationTotal = 0.0;
			ruralTotal = 0.0;
			urbanTotal = 0.0;
			for(j=0 ; j<this.n_region[i] ; j++){	
				for(k=0 ; k<this.n_sizeGroup ; k++){
					employee[j][k] = 0.0;		
					ruralEmployee[j][k] = 0.0;	
					urbanEmployee[j][k] = 0.0;	
					for(l=0 ; l<this.n_category[i] ; l++){
						employeeByCategory[i][l][k] = 0.0;
						ruralEmployeeByCategory[i][l][k] = 0.0;
						urbanEmployeeByCategory[i][l][k] = 0.0;
					}
				}
			}
			for(k=0 ; k<this.n_sizeGroup ; k++){
				employeeNation[k] = 0.0;
				employeeRural[k] = 0.0;
				employeeUrban[k] = 0.0;
				for(l=0 ; l<this.n_category[i] ; l++){
					employeeByIndustry[l][k] = 0.0;
					ruralEmployeeByIndustry[l][k] = 0.0;
					urbanEmployeeByIndustry[l][k] = 0.0;
				}
			}
			
			/*** set minimum and maximum sizes ***/
			min = 10000;
			max = 0;
			count = 0;
			total = 0;
			try{
				File file = new File(inputFile);
				Scanner scan = new Scanner(file);
				
				scan.nextLine();	//read column name
				
				while(scan.hasNext()){					
					/*** read micro-data code ***/
					scan.nextInt();		//read founding year		
					scan.next();			//read region code
					scan.next();			//read category code
					workers = scan.nextDouble();
					scan.next();			//read business type
					scan.next();			//read store type
									
					//check minimum and maximum employees
					count++;
					total += workers;
					if(workers>max) max = (int) workers;
					if(workers>0 && min>workers) min = (int) workers;
				}
				this.minSize[i] = min;
				this.maxSize[i] = max; 
				this.count[i] = count;
				this.total[i] = total;
				
				scan.close();	
			} catch(IOException e) {}
			
			/*** set size section ***/
			section = this.setLogarithmGroupSection(n_group, min, max);
			this.sizeSection.add(section);
			
			/*** extract microdata ***/
			try{
				File file = new File(inputFile);
				Scanner scan = new Scanner(file);
				
				scan.nextLine();	//read column name
				
				while(scan.hasNext()){					
					/*** read micro-data code ***/
					scan.nextInt();		//read founding year		
					tmpRegionCode = scan.next();
					tmpCategory = scan.next();
					region = tmpRegionCode.substring(0, this.regionClassDepth);
					if(this.categoryDepth>1) category = tmpCategory.substring(1,1+ this.categoryDepth); 
					else category = tmpCategory.substring(0, this.categoryDepth); 
					workers = scan.nextDouble();
					scan.next();		//read business type
					scan.next();		//read store type
									
					
					/*** find index ***/
					regionIndex = this.locatoinCode.get(i).indexOf(region);
					categoryIndex = this.categoryCode.get(i).indexOf(category);
					sizeIndex = -1;
					for(j=0 ; j<this.n_sizeGroup ; j++) 
						if(workers >= section[j] && workers < section[j+1]) sizeIndex = j;
					if(workers >= section[this.n_sizeGroup]) sizeIndex =  this.n_sizeGroup - 1;
					
					/*** add workers ***/
					if(regionIndex >= 0 && categoryIndex >= 0 && sizeIndex >= 0){
						nationTotal += workers;
						employeeNation[sizeIndex] += workers;
						employee[regionIndex][sizeIndex] += workers;
						employeeByCategory[regionIndex][categoryIndex][sizeIndex] += workers;
						employeeByIndustry[categoryIndex][sizeIndex] += workers;
							
						if(this.locationHashMap.get(i).containsKey(tmpRegionCode)){
							tmpRegion = this.locationHashMap.get(i).get(tmpRegionCode);
							tmpClassifier = tmpRegion.substring(tmpRegion.length()-1);
							if(tmpClassifier.equals("동")){
								urbanTotal += workers;
								employeeUrban[sizeIndex] += workers;
								urbanEmployee[regionIndex][sizeIndex] += workers;
								urbanEmployeeByCategory[regionIndex][categoryIndex][sizeIndex] += workers;
								urbanEmployeeByIndustry[categoryIndex][sizeIndex] += workers;
							}else if(tmpClassifier.equals("읍") || tmpClassifier.equals("면")
										|| tmpClassifier.equals("소") || tmpClassifier.equals("림")
										|| tmpClassifier.equals("서") || tmpClassifier.equals("성")){
								ruralTotal += workers;
								employeeRural[sizeIndex] += workers;
								ruralEmployee[regionIndex][sizeIndex] += workers;
								ruralEmployeeByCategory[regionIndex][categoryIndex][sizeIndex] += workers;
								ruralEmployeeByIndustry[categoryIndex][sizeIndex] += workers;
							}else{
								System.err.println("region classification by name error:"+tmpRegion+" "+tmpClassifier);
							}
						}else{
							System.err.println("no match region code error: "+tmpRegionCode);
						}
					}	
				}
				scan.close();	
			} catch(IOException e) {}
			
			this.nationTotalBySize[i] = nationTotal;
			this.employeeNationBySize.add(employeeNation);
			this.employeeBySize.add(employee);
			this.employeeByCategoryBySize.add(employeeByCategory);
			this.employeeByIndustryBySize.add(employeeByIndustry);
			
			this.ruralTotalBySize[i] = ruralTotal;
			this.employeeRuralBySize.add(employeeRural);
			this.ruralEmployeeBySize.add(ruralEmployee);
			this.ruralEmployeeByCategoryBySize.add(ruralEmployeeByCategory);
			this.ruralEmployeeByIndustryBySize.add(ruralEmployeeByIndustry);
			
			this.urbanTotalBySize[i] = urbanTotal;
			this.employeeUrbanBySize.add(employeeNation);
			this.urbanEmployeeBySize.add(urbanEmployee);
			this.urbanEmployeeByCategoryBySize.add(urbanEmployeeByCategory);
			this.urbanEmployeeByIndustryBySize.add(urbanEmployeeByIndustry);
		}
	}
	
	public void readEmployeeByLevel(int n_group, String profitFile, String filePath, String fileName){
		int i, j, k, l;
		String region;
		String category;
		int regionIndex;
		int categoryIndex;
		int levelIndex;
		double profit, workers;
		double min, max;
		double[] section;
		String inputFile;	
		String tmpProfitCode, tmpProfitName;
		String tmpRegion, tmpRegionCode, tmpClassifier, tmpCategory;
		HashMap<String, Double> profitList;
		HashMap<String, String> profitCode;
		
		double			nationTotal;
		double[]		employeeNation;						//[level]
		double[][]	employee;								//[region][level]
		double[][][]	employeeByCategory;				//[region][category][level]
		double[][]	employeeByIndustry;				//[category][level]
		
		double			ruralTotal;
		double[]		employeeRural;						//[level]
		double[][]	ruralEmployee;						//[region][level]
		double[][][]	ruralEmployeeByCategory;		//[region][category][level]
		double[][]	ruralEmployeeByIndustry;		//[category][level]
		
		double			urbanTotal;
		double[]		employeeUrban;						//[level]
		double[][]	urbanEmployee;						//[region][level]
		double[][][]	urbanEmployeeByCategory;	//[region][category][level]
		double[][]	urbanEmployeeByIndustry;		//[category][level]
		
		for(i=0 ; i<this.duration ; i++){		
		
			System.out.print(" "+(this.startYear+i)+" ");
			
			inputFile = filePath + (this.startYear+i) + fileName;
			
			/*** initiate variables ***/
			employeeNation = new double[this.n_levelGroup];	
			employee = new double[this.n_region[i]][this.n_levelGroup];			
			employeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_levelGroup];
			employeeByIndustry = new double[this.n_category[i]][this.n_levelGroup];
			
			employeeRural = new double[this.n_levelGroup];
			ruralEmployee = new double[this.n_region[i]][this.n_levelGroup];			
			ruralEmployeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_levelGroup];
			ruralEmployeeByIndustry = new double[this.n_category[i]][this.n_levelGroup];
			
			employeeUrban = new double[this.n_levelGroup];
			urbanEmployee = new double[this.n_region[i]][this.n_levelGroup];			
			urbanEmployeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_levelGroup];
			urbanEmployeeByIndustry = new double[this.n_category[i]][this.n_levelGroup];
			
			profitList = new HashMap<String, Double>();
			profitCode = new HashMap<String, String>();
			
			nationTotal = 0.0;
			ruralTotal = 0.0;
			urbanTotal = 0.0;
			for(j=0 ; j<this.n_region[i] ; j++){	
				for(k=0 ; k<this.n_levelGroup ; k++){
					employee[j][k] = 0.0;		
					ruralEmployee[j][k] = 0.0;	
					urbanEmployee[j][k] = 0.0;	
					for(l=0 ; l<this.n_category[i] ; l++){
						employeeByCategory[i][l][k] = 0.0;
						ruralEmployeeByCategory[i][l][k] = 0.0;
						urbanEmployeeByCategory[i][l][k] = 0.0;
					}
				}
			}
			for(k=0 ; k<this.n_levelGroup ; k++){
				employeeNation[k] = 0.0;
				employeeRural[k] = 0.0;
				employeeUrban[k] = 0.0;
				for(l=0 ; l<this.n_category[i] ; l++){
					employeeByIndustry[l][k] = 0.0;
					ruralEmployeeByIndustry[l][k] = 0.0;
					urbanEmployeeByIndustry[l][k] = 0.0;
				}
			}
			
			/*** set minimum and maximum sizes ***/
			min = 10000.0;
			max = 0.0;
			try{
				File file = new File(profitFile);
				Scanner scan = new Scanner(file);
				
				scan.nextLine();	//read index category
				
				while(scan.hasNext()){					
					/*** read micro-data code ***/	
					tmpProfitCode = scan.next();		//read industry code
					tmpProfitName = scan.next();		//read industry name
					profit = scan.nextDouble();			//read profit per employee
									
					profitList.put(tmpProfitCode, profit);
					profitCode.put(tmpProfitCode, tmpProfitName);
					
					//compare code and name
					if(tmpProfitName.equals(this.industryHashMap.get(i).get(tmpProfitCode)) == false) 
						System.err.println("profit code and name doesn't mathch:\t"+tmpProfitCode+"\t"+tmpProfitName+"\t"+this.industryHashMap.get(i).get(tmpProfitCode));
					
					//check minimum and maximum employees
					if(profit>max) max = profit;
					if(profit<min) min = profit;
				}
				this.minLevelProfit[i] = min;
				this.maxLevelProfit[i] = max; 
				
				scan.close();	
			} catch(IOException e) {}
			
			/*** set size section ***/
			section = this.setLogarithmGroupSection(n_group, min, max);
			this.levelSection.add(section);
			
			/*** extract microdata ***/
			try{
				File file = new File(inputFile);
				Scanner scan = new Scanner(file);
				
				scan.nextLine();	//read column name
				
				while(scan.hasNext()){					
					/*** read micro-data code ***/
					scan.nextInt();		//read founding year		
					tmpRegionCode = scan.next();
					tmpCategory = scan.next();
					region = tmpRegionCode.substring(0, this.regionClassDepth);
					if(this.categoryDepth>1) category = tmpCategory.substring(1,1+ this.categoryDepth); 
					else category = tmpCategory.substring(0, this.categoryDepth); 
					workers = scan.nextDouble();
					scan.next();		//read business type
					scan.next();		//read store type	
					
					/*** find index ***/
					regionIndex = this.locatoinCode.get(i).indexOf(region);
					categoryIndex = this.categoryCode.get(i).indexOf(category);
					levelIndex = -1;
					if(profitList.containsKey(tmpCategory.substring(1))){
						profit = profitList.get(tmpCategory.substring(1));
						for(j=0 ; j<this.n_levelGroup ; j++) 
							if(profit >= section[j] && profit < section[j+1]) levelIndex = j;
						if(profit >= section[this.n_levelGroup]) levelIndex =  this.n_levelGroup - 1;
					}
				//	else System.err.println("profit code doesn't exist:\t"+(i+this.startYear)+" year\t"+tmpCategory.substring(1));
					
					/*** add workers ***/
					if(regionIndex >= 0 && categoryIndex >= 0 && levelIndex >= 0){
						nationTotal += workers;
						employeeNation[levelIndex] += workers;
						employee[regionIndex][levelIndex] += workers;
						employeeByCategory[regionIndex][categoryIndex][levelIndex] += workers;
						employeeByIndustry[categoryIndex][levelIndex] += workers;
							
						if(this.locationHashMap.get(i).containsKey(tmpRegionCode)){
							tmpRegion = this.locationHashMap.get(i).get(tmpRegionCode);
							tmpClassifier = tmpRegion.substring(tmpRegion.length()-1);
							if(tmpClassifier.equals("동")){
								urbanTotal += workers;
								employeeUrban[levelIndex] += workers;
								urbanEmployee[regionIndex][levelIndex] += workers;
								urbanEmployeeByCategory[regionIndex][categoryIndex][levelIndex] += workers;
								urbanEmployeeByIndustry[categoryIndex][levelIndex] += workers;
							}else if(tmpClassifier.equals("읍") || tmpClassifier.equals("면")
										|| tmpClassifier.equals("소") || tmpClassifier.equals("림")
										|| tmpClassifier.equals("서") || tmpClassifier.equals("성")){
								ruralTotal += workers;
								employeeRural[levelIndex] += workers;
								ruralEmployee[regionIndex][levelIndex] += workers;
								ruralEmployeeByCategory[regionIndex][categoryIndex][levelIndex] += workers;
								ruralEmployeeByIndustry[categoryIndex][levelIndex] += workers;
							}else{
								System.err.println("region classification by name error:"+tmpRegion+" "+tmpClassifier);
							}
						}else{
							System.err.println("no match region code error: "+tmpRegionCode);
						}
					}	
				}
				scan.close();	
			} catch(IOException e) {}
			
			this.nationTotalByLevel[i] = nationTotal;
			this.employeeNationByLevel.add(employeeNation);
			this.employeeByLevel.add(employee);
			this.employeeByCategoryByLevel.add(employeeByCategory);
			this.employeeByIndustryByLevel.add(employeeByIndustry);
			
			this.ruralTotalByLevel[i] = ruralTotal;
			this.employeeRuralByLevel.add(employeeRural);
			this.ruralEmployeeByLevel.add(ruralEmployee);
			this.ruralEmployeeByCategoryByLevel.add(ruralEmployeeByCategory);
			this.ruralEmployeeByIndustryByLevel.add(ruralEmployeeByIndustry);
			
			this.urbanTotalByLevel[i] = urbanTotal;
			this.employeeUrbanByLevel.add(employeeNation);
			this.urbanEmployeeByLevel.add(urbanEmployee);
			this.urbanEmployeeByCategoryByLevel.add(urbanEmployeeByCategory);
			this.urbanEmployeeByIndustryByLevel.add(urbanEmployeeByIndustry);
		}
	}
	
	public void readEmployeeByAge(int n_group, String filePath, String fileName){
		int i, j, k, l;
		String region;
		String category;
		int regionIndex;
		int categoryIndex;
		int companyAgeIndex;
		int limitAge = 100;
		double workers;
		double min, max, years;
		double established, closed, totalCompany;
		double inEstablished, fired, totalEmployee;
		double[] section;
		String inputFile;				
		String tmpRegion, tmpRegionCode, tmpClassifier, tmpCategory;
		
		double			nationTotal;
		double[]		employeeNation;						//[age]
		double[][]	employee;								//[region][age]
		double[][][]	employeeByCategory;				//[region][category][age]
		double[][]	employeeByIndustry;				//[category][age]
		
		double			ruralTotal;
		double[]		employeeRural;						//[age]
		double[][]	ruralEmployee;						//[region][age]
		double[][][]	ruralEmployeeByCategory;		//[region][category][age]
		double[][]	ruralEmployeeByIndustry;		//[category][age]
		
		double			urbanTotal;
		double[]		employeeUrban;						//[age]
		double[][]	urbanEmployee;						//[region][age]
		double[][][]	urbanEmployeeByCategory;	//[region][category][age]
		double[][]	urbanEmployeeByIndustry;		//[category][age]
		
		for(i=0 ; i<this.duration ; i++){		
		
			System.out.print(" "+(this.startYear+i)+" ");
			
			inputFile = filePath + (this.startYear+i) + fileName;
			
			/*** initiate variables ***/
			employeeNation = new double[this.n_ageGroup];	
			employee = new double[this.n_region[i]][this.n_ageGroup];			
			employeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_ageGroup];
			employeeByIndustry = new double[this.n_category[i]][this.n_ageGroup];
			
			employeeRural = new double[this.n_ageGroup];
			ruralEmployee = new double[this.n_region[i]][this.n_ageGroup];			
			ruralEmployeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_ageGroup];
			ruralEmployeeByIndustry = new double[this.n_category[i]][this.n_ageGroup];
			
			employeeUrban = new double[this.n_ageGroup];
			urbanEmployee = new double[this.n_region[i]][this.n_ageGroup];			
			urbanEmployeeByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_ageGroup];
			urbanEmployeeByIndustry = new double[this.n_category[i]][this.n_ageGroup];
			
			nationTotal = 0.0;
			ruralTotal = 0.0;
			urbanTotal = 0.0;
			for(j=0 ; j<this.n_region[i] ; j++){	
				for(k=0 ; k<this.n_ageGroup ; k++){
					employee[j][k] = 0.0;		
					ruralEmployee[j][k] = 0.0;	
					urbanEmployee[j][k] = 0.0;	
					for(l=0 ; l<this.n_category[i] ; l++){
						employeeByCategory[i][l][k] = 0.0;
						ruralEmployeeByCategory[i][l][k] = 0.0;
						urbanEmployeeByCategory[i][l][k] = 0.0;
					}
				}
			}
			for(k=0 ; k<this.n_ageGroup ; k++){
				employeeNation[k] = 0.0;
				employeeRural[k] = 0.0;
				employeeUrban[k] = 0.0;
				for(l=0 ; l<this.n_category[i] ; l++){
					employeeByIndustry[l][k] = 0.0;
					ruralEmployeeByIndustry[l][k] = 0.0;
					urbanEmployeeByIndustry[l][k] = 0.0;
				}
			}
			
			/*** set minimum and maximum sizes ***/
			min = 10000;
			max = 0;
			established = 0;
			closed = 0;
			totalCompany = 0;
			inEstablished = 0;
			fired = 0;
			totalEmployee = 0;
			
			try{
				File file = new File(inputFile);
				Scanner scan = new Scanner(file);
				
				scan.nextLine();	//read column name
				
				while(scan.hasNext()){					
					/*** read micro-data code ***/
					years = (i+this.startYear) - scan.nextInt();	//read founding year		
					scan.next();													//read region code
					scan.next();													//read category code
					workers = scan.nextDouble();						//read number of employees
					scan.next();													//read business type
					scan.next();													//read store type
									
					//check minimum and maximum employees
					if(years >=0 && years < limitAge){
						totalCompany++;
						totalEmployee += workers;
						if(years>max) max = (int) years;
						if(years>=0 && min>years) min = (int) years;
						if(years == 0){
							established++;
							inEstablished += workers;
						}
					}
				}
				this.minAge[i] = min;
				this.maxAge[i] = max; 
				this.companies[i] = totalCompany;
				this.employees[i] = totalEmployee;
				this.founded[i] = established;
				this.inFounded[i] = inEstablished;
				if(i == 0){
					this.closed[i] = 0;
					this.decreased[i] = 0;
				}else if(i > 0){
					this.closed[i] = this.companies[i-1] - totalCompany + established;
					this.decreased[i] = this.employees[i-1] - totalEmployee + inEstablished;
				}
				
				scan.close();	
			} catch(IOException e) {}
			
			/*** set size section ***/
			section = this.setLogarithmGroupSection(n_group, min, max);
			this.sizeSection.add(section);
			
			/*** extract microdata ***/
			try{
				File file = new File(inputFile);
				Scanner scan = new Scanner(file);
				
				scan.nextLine();	//read column name
				
				while(scan.hasNext()){					
					/*** read micro-data code ***/
					years = (i+this.startYear) - scan.nextInt();
					tmpRegionCode = scan.next();
					tmpCategory = scan.next();
					region = tmpRegionCode.substring(0, this.regionClassDepth);
					if(this.categoryDepth>1) category = tmpCategory.substring(1,1+ this.categoryDepth); 
					else category = tmpCategory.substring(0, this.categoryDepth); 
					workers = scan.nextDouble();
					scan.next();		//read business type
					scan.next();		//read store type
									
					
					/*** find index ***/
					regionIndex = this.locatoinCode.get(i).indexOf(region);
					categoryIndex = this.categoryCode.get(i).indexOf(category);
					companyAgeIndex = -1;
					for(j=0 ; j<this.n_ageGroup ; j++) 
						if(years >= section[j] && years < section[j+1]) companyAgeIndex = j;
					if(years >= section[this.n_ageGroup] && years < limitAge) companyAgeIndex =  this.n_ageGroup-1;
					
					/*** add workers ***/
					if(regionIndex >= 0 && categoryIndex >= 0 && companyAgeIndex >= 0){
						nationTotal += workers;
						employeeNation[companyAgeIndex] += workers;
						employee[regionIndex][companyAgeIndex] += workers;
						employeeByCategory[regionIndex][categoryIndex][companyAgeIndex] += workers;
						employeeByIndustry[categoryIndex][companyAgeIndex] += workers;
							
						if(this.locationHashMap.get(i).containsKey(tmpRegionCode)){
							tmpRegion = this.locationHashMap.get(i).get(tmpRegionCode);
							tmpClassifier = tmpRegion.substring(tmpRegion.length()-1);
							if(tmpClassifier.equals("동")){
								urbanTotal += workers;
								employeeUrban[companyAgeIndex] += workers;
								urbanEmployee[regionIndex][companyAgeIndex] += workers;
								urbanEmployeeByCategory[regionIndex][categoryIndex][companyAgeIndex] += workers;
								urbanEmployeeByIndustry[categoryIndex][companyAgeIndex] += workers;
							}else if(tmpClassifier.equals("읍") || tmpClassifier.equals("면")
										|| tmpClassifier.equals("소") || tmpClassifier.equals("림")
										|| tmpClassifier.equals("서") || tmpClassifier.equals("성")){
								ruralTotal += workers;
								employeeRural[companyAgeIndex] += workers;
								ruralEmployee[regionIndex][companyAgeIndex] += workers;
								ruralEmployeeByCategory[regionIndex][categoryIndex][companyAgeIndex] += workers;
								ruralEmployeeByIndustry[categoryIndex][companyAgeIndex] += workers;
							}else{
								System.err.println("region classification by name error:"+tmpRegion+" "+tmpClassifier);
							}
						}else{
							System.err.println("no match region code error: "+tmpRegionCode);
						}
					}	
				}
				scan.close();	
			} catch(IOException e) {}
			
			this.nationTotalByAge[i] = nationTotal;
			this.employeeNationByAge.add(employeeNation);
			this.employeeByAge.add(employee);
			this.employeeByCategoryByAge.add(employeeByCategory);
			this.employeeByIndustryByAge.add(employeeByIndustry);
			
			this.ruralTotalByAge[i] = ruralTotal;
			this.employeeRuralByAge.add(employeeRural);
			this.ruralEmployeeByAge.add(ruralEmployee);
			this.ruralEmployeeByCategoryByAge.add(ruralEmployeeByCategory);
			this.ruralEmployeeByIndustryByAge.add(ruralEmployeeByIndustry);
			
			this.urbanTotalByAge[i] = urbanTotal;
			this.employeeUrbanByAge.add(employeeNation);
			this.urbanEmployeeByAge.add(urbanEmployee);
			this.urbanEmployeeByCategoryByAge.add(urbanEmployeeByCategory);
			this.urbanEmployeeByIndustryByAge.add(urbanEmployeeByIndustry);
		}
	}
	
	public void rearrangeCategoryList(int[] marker, ArrayList<ArrayList<Integer>> rearrangeList){
		//marker ==0 : no change
		//marker >  0 : number of marker is number of new categories
		int i, j, k;
		int categoryIndex;

		ArrayList<String> tmpIndustryList;
		ArrayList<ArrayList<String>> tmpIndustryByCategoryList;
		
		for(i=0 ; i<this.duration ; i++){	
			if(marker[i] > 0){			
				//alternate industry list by category
				tmpIndustryByCategoryList = new ArrayList<ArrayList<String>>();
				for(j=0 ; j<marker[i] ; j++) tmpIndustryByCategoryList.add(new ArrayList<String>());
				for(j=0 ; j<this.n_category[i] ; j++){
					categoryIndex = rearrangeList.get(i).get(j);
					tmpIndustryList = this.industryListByCategory.get(i).get(j);
					for(k=0 ; k<tmpIndustryList.size(); k++)	
						tmpIndustryByCategoryList.get(categoryIndex).add(tmpIndustryList.get(k));
				}
				this.n_category[i] = marker[i];
				this.industryListByCategory.set(i, tmpIndustryByCategoryList);
			}
		}
	}
	
	public void rearrangeCategoryList(int[] marker, ArrayList<ArrayList<Integer>> rearrangeList, 
												ArrayList<ArrayList<String>> code, ArrayList<ArrayList<String>> name){
		this.rearrangeCategoryList(marker, rearrangeList);
		this.categoryCode = code;
		this.categoryName = name;
	}
	
	public void rearrangeCategoryEmployee(int[] marker, ArrayList<ArrayList<Integer>> rearrangeList){
		//marker ==0 : no change
		//marker >  0 : number of marker is number of new categories
		int i, j, k, l;
		int categoryIndex;
		
		double[][][]	employeeByCategory;						//[region][category][industry]
		double[][]	employeeByIndustry;						//[category][industry]
		double[][][]	ruralEmployeeByCategory;				//[region][category][industry]
		double[][]	ruralEmployeeByIndustry;				//[category][industry]
		double[][][]	urbanEmployeeByCategory;			//[region][category][industry]
		double[][]	urbanEmployeeByIndustry;				//[category][industry]
		
		for(i=0 ; i<this.duration ; i++){	
			if(marker[i] > 0){
				//initiate employee matrix
				employeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				employeeByIndustry = new double[marker[i]][this.n_industry[i]];			
				ruralEmployeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				ruralEmployeeByIndustry = new double[marker[i]][this.n_industry[i]];		
				urbanEmployeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				urbanEmployeeByIndustry = new double[marker[i]][this.n_industry[i]];
				for(k=0 ; k<marker[i]; k++){
					for(l=0 ; l<this.n_industry[i] ; l++){
						employeeByIndustry[k][l] = 0.0;
						ruralEmployeeByIndustry[k][l] = 0.0;
						urbanEmployeeByIndustry[k][l] = 0.0;
						for(j=0 ; j<this.n_region[i] ; j++){
							employeeByCategory[j][k][l] = 0.0;
							ruralEmployeeByCategory[j][k][l] = 0.0;
							urbanEmployeeByCategory[j][k][l] = 0.0;
						}
					}
				}
				
				//rearrange employee matrix
				for(k=0 ; k<this.n_category[i]; k++){
					categoryIndex = rearrangeList.get(i).get(k);
					for(l=0 ; l<this.n_industry[i] ; l++){
						employeeByIndustry[categoryIndex][l] += this.employeeByIndustry.get(i)[k][l];
						ruralEmployeeByIndustry[categoryIndex][l] += this.ruralEmployeeByIndustry.get(i)[k][l];
						urbanEmployeeByIndustry[categoryIndex][l] += this.urbanEmployeeByIndustry.get(i)[k][l];
						for(j=0 ; j<this.n_region[i] ; j++){	
							employeeByCategory[j][categoryIndex][l] += this.employeeByCategory.get(i)[j][k][l];
							ruralEmployeeByCategory[j][categoryIndex][l] += this.ruralEmployeeByCategory.get(i)[j][k][l];
							urbanEmployeeByCategory[j][categoryIndex][l] += this.urbanEmployeeByCategory.get(i)[j][k][l];
						}
					}
				}
				this.employeeByIndustry.set(i, employeeByIndustry);
				this.ruralEmployeeByIndustry.set(i, ruralEmployeeByIndustry);
				this.urbanEmployeeByIndustry.set(i, urbanEmployeeByIndustry);
				this.employeeByCategory.set(i, employeeByCategory);
				this.ruralEmployeeByCategory.set(i, ruralEmployeeByCategory);
				this.urbanEmployeeByCategory.set(i, urbanEmployeeByCategory);
			}
		}
	}
	
	public void rearrangeCategoryEmployeeBySize(int[] marker, ArrayList<ArrayList<Integer>> rearrangeList){
		//marker ==0 : no change
		//marker >  0 : number of marker is number of new categories
		int i, j, k, l;
		int categoryIndex;
		
		double[][][]	employeeByCategory;						//[region][category][industry]
		double[][]	employeeByIndustry;						//[category][industry]
		double[][][]	ruralEmployeeByCategory;				//[region][category][industry]
		double[][]	ruralEmployeeByIndustry;				//[category][industry]
		double[][][]	urbanEmployeeByCategory;			//[region][category][industry]
		double[][]	urbanEmployeeByIndustry;				//[category][industry]
		
		for(i=0 ; i<this.duration ; i++){	
			if(marker[i] > 0){
				//initiate employee matrix
				employeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				employeeByIndustry = new double[marker[i]][this.n_industry[i]];			
				ruralEmployeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				ruralEmployeeByIndustry = new double[marker[i]][this.n_industry[i]];		
				urbanEmployeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				urbanEmployeeByIndustry = new double[marker[i]][this.n_industry[i]];
				for(k=0 ; k<marker[i]; k++){
					for(l=0 ; l<this.n_industry[i] ; l++){
						employeeByIndustry[k][l] = 0.0;
						ruralEmployeeByIndustry[k][l] = 0.0;
						urbanEmployeeByIndustry[k][l] = 0.0;
						for(j=0 ; j<this.n_region[i] ; j++){
							employeeByCategory[j][k][l] = 0.0;
							ruralEmployeeByCategory[j][k][l] = 0.0;
							urbanEmployeeByCategory[j][k][l] = 0.0;
						}
					}
				}
				
				//rearrange employee matrix
				for(k=0 ; k<this.n_category[i]; k++){
					categoryIndex = rearrangeList.get(i).get(k);
					for(l=0 ; l<this.n_sizeGroup ; l++){
						employeeByIndustry[categoryIndex][l] += this.employeeByIndustryBySize.get(i)[k][l];
						ruralEmployeeByIndustry[categoryIndex][l] += this.ruralEmployeeByIndustryBySize.get(i)[k][l];
						urbanEmployeeByIndustry[categoryIndex][l] += this.urbanEmployeeByIndustryBySize.get(i)[k][l];
						for(j=0 ; j<this.n_region[i] ; j++){	
							employeeByCategory[j][categoryIndex][l] += this.employeeByCategoryBySize.get(i)[j][k][l];
							ruralEmployeeByCategory[j][categoryIndex][l] += this.ruralEmployeeByCategoryBySize.get(i)[j][k][l];
							urbanEmployeeByCategory[j][categoryIndex][l] += this.urbanEmployeeByCategoryBySize.get(i)[j][k][l];
						}
					}
				}
				this.employeeByIndustryBySize.set(i, employeeByIndustry);
				this.ruralEmployeeByIndustryBySize.set(i, ruralEmployeeByIndustry);
				this.urbanEmployeeByIndustryBySize.set(i, urbanEmployeeByIndustry);
				this.employeeByCategoryBySize.set(i, employeeByCategory);
				this.ruralEmployeeByCategoryBySize.set(i, ruralEmployeeByCategory);
				this.urbanEmployeeByCategoryBySize.set(i, urbanEmployeeByCategory);
			}
		}
	}
	
	public void rearrangeCategoryEmployeeByLevel(int[] marker, ArrayList<ArrayList<Integer>> rearrangeList){
		//marker ==0 : no change
		//marker >  0 : number of marker is number of new categories
		int i, j, k, l;
		int categoryIndex;
		
		double[][][]	employeeByCategory;						//[region][category][industry]
		double[][]	employeeByIndustry;						//[category][industry]
		double[][][]	ruralEmployeeByCategory;				//[region][category][industry]
		double[][]	ruralEmployeeByIndustry;				//[category][industry]
		double[][][]	urbanEmployeeByCategory;			//[region][category][industry]
		double[][]	urbanEmployeeByIndustry;				//[category][industry]
		
		for(i=0 ; i<this.duration ; i++){	
			if(marker[i] > 0){
				//initiate employee matrix
				employeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				employeeByIndustry = new double[marker[i]][this.n_industry[i]];			
				ruralEmployeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				ruralEmployeeByIndustry = new double[marker[i]][this.n_industry[i]];		
				urbanEmployeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				urbanEmployeeByIndustry = new double[marker[i]][this.n_industry[i]];
				for(k=0 ; k<marker[i]; k++){
					for(l=0 ; l<this.n_industry[i] ; l++){
						employeeByIndustry[k][l] = 0.0;
						ruralEmployeeByIndustry[k][l] = 0.0;
						urbanEmployeeByIndustry[k][l] = 0.0;
						for(j=0 ; j<this.n_region[i] ; j++){
							employeeByCategory[j][k][l] = 0.0;
							ruralEmployeeByCategory[j][k][l] = 0.0;
							urbanEmployeeByCategory[j][k][l] = 0.0;
						}
					}
				}
				
				//rearrange employee matrix
				for(k=0 ; k<this.n_category[i]; k++){
					categoryIndex = rearrangeList.get(i).get(k);
					for(l=0 ; l<this.n_levelGroup ; l++){
						employeeByIndustry[categoryIndex][l] += this.employeeByIndustryByLevel.get(i)[k][l];
						ruralEmployeeByIndustry[categoryIndex][l] += this.ruralEmployeeByIndustryByLevel.get(i)[k][l];
						urbanEmployeeByIndustry[categoryIndex][l] += this.urbanEmployeeByIndustryByLevel.get(i)[k][l];
						for(j=0 ; j<this.n_region[i] ; j++){	
							employeeByCategory[j][categoryIndex][l] += this.employeeByCategoryByLevel.get(i)[j][k][l];
							ruralEmployeeByCategory[j][categoryIndex][l] += this.ruralEmployeeByCategoryByLevel.get(i)[j][k][l];
							urbanEmployeeByCategory[j][categoryIndex][l] += this.urbanEmployeeByCategoryByLevel.get(i)[j][k][l];
						}
					}
				}
				this.employeeByIndustryByLevel.set(i, employeeByIndustry);
				this.ruralEmployeeByIndustryByLevel.set(i, ruralEmployeeByIndustry);
				this.urbanEmployeeByIndustryByLevel.set(i, urbanEmployeeByIndustry);
				this.employeeByCategoryByLevel.set(i, employeeByCategory);
				this.ruralEmployeeByCategoryByLevel.set(i, ruralEmployeeByCategory);
				this.urbanEmployeeByCategoryByLevel.set(i, urbanEmployeeByCategory);
			}
		}
	}
	
	public void rearrangeCategoryEmployeeByAge(int[] marker, ArrayList<ArrayList<Integer>> rearrangeList){
		//marker ==0 : no change
		//marker >  0 : number of marker is number of new categories
		int i, j, k, l;
		int categoryIndex;
		
		double[][][]	employeeByCategory;						//[region][category][industry]
		double[][]	employeeByIndustry;						//[category][industry]
		double[][][]	ruralEmployeeByCategory;				//[region][category][industry]
		double[][]	ruralEmployeeByIndustry;				//[category][industry]
		double[][][]	urbanEmployeeByCategory;			//[region][category][industry]
		double[][]	urbanEmployeeByIndustry;				//[category][industry]
		
		for(i=0 ; i<this.duration ; i++){	
			if(marker[i] > 0){
				//initiate employee matrix
				employeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				employeeByIndustry = new double[marker[i]][this.n_industry[i]];			
				ruralEmployeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				ruralEmployeeByIndustry = new double[marker[i]][this.n_industry[i]];		
				urbanEmployeeByCategory = new double[this.n_region[i]][marker[i]][this.n_industry[i]];
				urbanEmployeeByIndustry = new double[marker[i]][this.n_industry[i]];
				for(k=0 ; k<marker[i]; k++){
					for(l=0 ; l<this.n_industry[i] ; l++){
						employeeByIndustry[k][l] = 0.0;
						ruralEmployeeByIndustry[k][l] = 0.0;
						urbanEmployeeByIndustry[k][l] = 0.0;
						for(j=0 ; j<this.n_region[i] ; j++){
							employeeByCategory[j][k][l] = 0.0;
							ruralEmployeeByCategory[j][k][l] = 0.0;
							urbanEmployeeByCategory[j][k][l] = 0.0;
						}
					}
				}
				
				//rearrange employee matrix
				for(k=0 ; k<this.n_category[i]; k++){
					categoryIndex = rearrangeList.get(i).get(k);
					for(l=0 ; l<this.n_ageGroup ; l++){
						employeeByIndustry[categoryIndex][l] += this.employeeByIndustryByAge.get(i)[k][l];
						ruralEmployeeByIndustry[categoryIndex][l] += this.ruralEmployeeByIndustryByAge.get(i)[k][l];
						urbanEmployeeByIndustry[categoryIndex][l] += this.urbanEmployeeByIndustryByAge.get(i)[k][l];
						for(j=0 ; j<this.n_region[i] ; j++){	
							employeeByCategory[j][categoryIndex][l] += this.employeeByCategoryByAge.get(i)[j][k][l];
							ruralEmployeeByCategory[j][categoryIndex][l] += this.ruralEmployeeByCategoryByAge.get(i)[j][k][l];
							urbanEmployeeByCategory[j][categoryIndex][l] += this.urbanEmployeeByCategoryByAge.get(i)[j][k][l];
						}
					}
				}
				this.employeeByIndustryByAge.set(i, employeeByIndustry);
				this.ruralEmployeeByIndustryByAge.set(i, ruralEmployeeByIndustry);
				this.urbanEmployeeByIndustryByAge.set(i, urbanEmployeeByIndustry);
				this.employeeByCategoryByAge.set(i, employeeByCategory);
				this.ruralEmployeeByCategoryByAge.set(i, ruralEmployeeByCategory);
				this.urbanEmployeeByCategoryByAge.set(i, urbanEmployeeByCategory);
			}
		}
	}
	
	public void calculateEmployeeStatistics(){
		
		int i, j, k, l;
		double			nationTotal;
		double[]		employeeNation;								//[industry]
		double[]		employeeRatioNation;						//[industry]
		double[][]	employee;										//[region][industry]
		double[]		employeeSum;									//[region]
		double[][]	employeeRatio;								//[region][industry]
		double[][][]	employeeByCategory;						//[region][category][industry]
		double[][]	employeeSumByCategory;				//[region][category]
		double[][][]	employeeRatioByCategory;				//[region][category][industry]
		double[][]	employeeByIndustry;						//[category][industry]
		double[]		employeeSumByIndustry;				//[category]
		double[][]	employeeRatioByIndustry;				//[category][industry]
		
		double			ruralTotal;
		double[]		employeeRural;								//[industry]
		double[]		employeeRatioRural;						//[industry]
		double[][]	ruralEmployee;								//[region][industry]
		double[]		ruralEmployeeSum;							//[region]
		double[][]	ruralEmployeeRatio;						//[region][industry]
		double[][][]	ruralEmployeeByCategory;				//[region][category][industry]
		double[][]	ruralEmployeeSumByCategory;		//[region][category]
		double[][][]	ruralEmployeeRatioByCategory;		//[region][category][industry]
		double[][]	ruralEmployeeByIndustry;				//[category][industry]
		double[]		ruralEmployeeSumByIndustry;		//[category]
		double[][]	ruralEmployeeRatioByIndustry;		//[category][industry]
		
		double			urbanTotal;
		double[]		employeeUrban;								//[industry]
		double[]		employeeRatioUrban;						//[industry]
		double[][] 	urbanEmployee;								//[region][industry]
		double[]		urbanEmployeeSum;						//[region]
		double[][]	urbanEmployeeRatio;						//[region][industry]
		double[][][]	urbanEmployeeByCategory;			//[region][category][industry]
		double[][]	urbanEmployeeSumByCategory;		//[region][category]
		double[][][]	urbanEmployeeRatioByCategory;	//[region][category][industry]
		double[][]	urbanEmployeeByIndustry;				//[category][industry]
		double[]		urbanEmployeeSumByIndustry;		//[category]
		double[][]	urbanEmployeeRatioByIndustry;		//[category][industry]
		
		for(i=0 ; i<this.duration ; i++){
			
			/*** initiate variables ***/
			nationTotal = this.nationTotal[i];
			employeeNation = this.employeeNation.get(i);
			employeeRatioNation = new double[this.n_industry[i]];
			
			employee = this.employee.get(i);			
			employeeRatio = new double[this.n_region[i]][this.n_industry[i]];
			employeeSum = new double[this.n_region[i]];		
			
			employeeByCategory = this.employeeByCategory.get(i);
			employeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			employeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_industry[i]];
			
			employeeByIndustry = this.employeeByIndustry.get(i);
			employeeSumByIndustry = new double[this.n_category[i]];
			employeeRatioByIndustry = new double[this.n_category[i]][this.n_industry[i]];
			
			ruralTotal = this.ruralTotal[i];
			employeeRural = this.employeeRural.get(i);
			employeeRatioRural = new double[this.n_industry[i]];
			
			ruralEmployee = this.ruralEmployee.get(i);			
			ruralEmployeeRatio = new double[this.n_region[i]][this.n_industry[i]];
			ruralEmployeeSum = new double[this.n_region[i]];		
			
			ruralEmployeeByCategory = this.ruralEmployeeByCategory.get(i);
			ruralEmployeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			ruralEmployeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_industry[i]];
			
			ruralEmployeeByIndustry = this.ruralEmployeeByIndustry.get(i);
			ruralEmployeeSumByIndustry = new double[this.n_category[i]];
			ruralEmployeeRatioByIndustry = new double[this.n_category[i]][this.n_industry[i]];
			
			urbanTotal = this.urbanTotal[i];
			employeeUrban = this.employeeUrban.get(i);
			employeeRatioUrban = new double[this.n_industry[i]];
			
			urbanEmployee = this.urbanEmployee.get(i);			
			urbanEmployeeRatio = new double[this.n_region[i]][this.n_industry[i]];
			urbanEmployeeSum = new double[this.n_region[i]];		
			
			urbanEmployeeByCategory = this.urbanEmployeeByCategory.get(i);
			urbanEmployeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			urbanEmployeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_industry[i]];
			
			urbanEmployeeByIndustry = this.urbanEmployeeByIndustry.get(i);
			urbanEmployeeSumByIndustry = new double[this.n_category[i]];
			urbanEmployeeRatioByIndustry = new double[this.n_category[i]][this.n_industry[i]];
			
			for(j=0 ; j<this.n_region[i] ; j++){
				employeeSum[j] = 0.0;
				ruralEmployeeSum[j] = 0.0;
				urbanEmployeeSum[j] = 0.0;
			}
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_category[i] ; k++){
					employeeSumByCategory[j][k] = 0.0;
					ruralEmployeeSumByCategory[j][k] = 0.0;
					urbanEmployeeSumByCategory[j][k] = 0.0;
				}
			}
			for(k=0 ; k<this.n_category[i] ; k++){
				employeeSumByIndustry[k] = 0.0;
				ruralEmployeeSumByIndustry[k] = 0.0;
				urbanEmployeeSumByIndustry[k] = 0.0;
			}
			
			/*** sum region's total employees ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_industry[i] ; k++){
					employeeSum[j] += employee[j][k];	
					ruralEmployeeSum[j] += ruralEmployee[j][k];	
					urbanEmployeeSum[j] += urbanEmployee[j][k];	
				}
			}
			for(j=0 ; j<this.n_region[i] ; j++){
				for(l=0 ; l<this.n_category[i] ; l++){
					for(k=0 ; k<this.n_industry[i] ; k++){
						employeeSumByCategory[j][l] += employeeByCategory[j][l][k];
						ruralEmployeeSumByCategory[j][l] += ruralEmployeeByCategory[j][l][k];
						urbanEmployeeSumByCategory[j][l] += urbanEmployeeByCategory[j][l][k];
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				for(k=0 ; k<this.n_industry[i] ; k++){
					employeeSumByIndustry[l] += employeeByIndustry[l][k];
					ruralEmployeeSumByIndustry[l] += ruralEmployeeByIndustry[l][k];
					urbanEmployeeSumByIndustry[l] += urbanEmployeeByIndustry[l][k];
				}
			}
			
			/*** calculate national employee ratio ***/
			for(j=0 ; j<this.n_industry[i] ; j++){
				if(nationTotal > 0) employeeRatioNation[j] = employeeNation[j] / nationTotal;
				else if(nationTotal == 0) employeeRatioNation[j]  = 0.0;
				else{
					employeeRatioNation[j]  = 0.0;
					System.err.println("probability calculation error");
				}
				
				if(ruralTotal > 0) employeeRatioRural[j] = employeeRural[j] / ruralTotal;
				else if(ruralTotal == 0) employeeRatioRural[j]  = 0.0;
				else{
					employeeRatioRural[j]  = 0.0;
					System.err.println("probability calculation error");
				}
				
				if(urbanTotal > 0) employeeRatioUrban[j] = employeeUrban[j] / urbanTotal;
				else if(urbanTotal == 0) employeeRatioUrban[j]  = 0.0;
				else{
					employeeRatioUrban[j]  = 0.0;
					System.err.println("probability calculation error");
				}
			}
			
			/*** calculate employee ratio ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_industry[i] ; k++){
					if(employeeSum[j] > 0) employeeRatio[j][k] = employee[j][k] / employeeSum[j];
					else if(employeeSum[j] == 0) employeeRatio[j][k] = 0.0;
					else{
						employeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(ruralEmployeeSum[j] > 0) ruralEmployeeRatio[j][k] = ruralEmployee[j][k] / ruralEmployeeSum[j];
					else if(ruralEmployeeSum[j] == 0) ruralEmployeeRatio[j][k] = 0.0;
					else{
						ruralEmployeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(urbanEmployeeSum[j]>0) urbanEmployeeRatio[j][k]=urbanEmployee[j][k]/urbanEmployeeSum[j];
					else if(urbanEmployeeSum[j] == 0) urbanEmployeeRatio[j][k] = 0.0;
					else{
						urbanEmployeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
				}
				
				/*** calculate employee ratio by industrial category ***/
				for(l=0 ; l<this.n_category[i] ; l++){
					for(k=0 ; k<this.n_industry[i] ; k++){
						if(employeeSumByCategory[j][l] > 0) 
							employeeRatioByCategory[j][l][k] = employeeByCategory[j][l][k] / employeeSumByCategory[j][l];
						else if(employeeSumByCategory[j][l] == 0) employeeRatioByCategory[j][l][k] = 0.0;
						else{
							employeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
						
						if(ruralEmployeeSumByCategory[j][l] > 0) 
							ruralEmployeeRatioByCategory[j][l][k]
									= ruralEmployeeByCategory[j][l][k] / ruralEmployeeSumByCategory[j][l];
						else if(ruralEmployeeSumByCategory[j][l] == 0) ruralEmployeeRatioByCategory[j][l][k] = 0.0;
						else{
							ruralEmployeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
						
						if(urbanEmployeeSumByCategory[j][l] > 0) 
							urbanEmployeeRatioByCategory[j][l][k]
									= urbanEmployeeByCategory[j][l][k] / urbanEmployeeSumByCategory[j][l];
						else if(urbanEmployeeSumByCategory[j][l] == 0) urbanEmployeeRatioByCategory[j][l][k] = 0.0;
						else{
							urbanEmployeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
					}
				}	
			}
			
			/*** calculate employee ratio by industry***/
			for(l=0 ; l<this.n_category[i] ; l++){
				for(k=0 ; k<this.n_industry[i] ; k++){
					if(employeeSumByIndustry[l] > 0) 
						employeeRatioByIndustry[l][k] = employeeByIndustry[l][k] / employeeSumByIndustry[l];
					else if(employeeSumByIndustry[l] == 0) employeeRatioByIndustry[l][k] = 0.0;
					else{
						employeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(ruralEmployeeSumByIndustry[l] > 0) 
						ruralEmployeeRatioByIndustry[l][k]
								= ruralEmployeeByIndustry[l][k] / ruralEmployeeSumByIndustry[l];
					else if(ruralEmployeeSumByIndustry[l] == 0) ruralEmployeeRatioByIndustry[l][k] = 0.0;
					else{
						ruralEmployeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(urbanEmployeeSumByIndustry[l] > 0) 
						urbanEmployeeRatioByIndustry[l][k]
								= urbanEmployeeByIndustry[l][k] / urbanEmployeeSumByIndustry[l];
					else if(urbanEmployeeSumByIndustry[l] == 0) urbanEmployeeRatioByIndustry[l][k] = 0.0;
					else{
						urbanEmployeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
				}
			}
			
			this.employeeRatioNation.add(employeeRatioNation);
			this.employeeSum.add(employeeSum);
			this.employeeRatio.add(employeeRatio);
			this.employeeSumByCategory.add(employeeSumByCategory);
			this.employeeRatioByCategory.add(employeeRatioByCategory);
			this.employeeSumByIndustry.add(employeeSumByIndustry);
			this.employeeRatioByIndustry.add(employeeRatioByIndustry);
			
			this.employeeRatioRural.add(employeeRatioRural);
			this.ruralEmployeeSum.add(ruralEmployeeSum);
			this.ruralEmployeeRatio.add(ruralEmployeeRatio);
			this.ruralEmployeeSumByCategory.add(ruralEmployeeSumByCategory);
			this.ruralEmployeeRatioByCategory.add(ruralEmployeeRatioByCategory);
			this.ruralEmployeeSumByIndustry.add(ruralEmployeeSumByIndustry);
			this.ruralEmployeeRatioByIndustry.add(ruralEmployeeRatioByIndustry);
			
			this.employeeRatioUrban.add(employeeRatioUrban);
			this.urbanEmployeeSum.add(urbanEmployeeSum);
			this.urbanEmployeeRatio.add(urbanEmployeeRatio);
			this.urbanEmployeeSumByCategory.add(urbanEmployeeSumByCategory);
			this.urbanEmployeeRatioByCategory.add(urbanEmployeeRatioByCategory);
			this.urbanEmployeeSumByIndustry.add(urbanEmployeeSumByIndustry);
			this.urbanEmployeeRatioByIndustry.add(urbanEmployeeRatioByIndustry);
		}
	}
	
	public void calculateEmployeeEntropy(){
		
		int i, j, k, l;

		double tmpProb;
		double subEntropy;
		double base = Math.log(2);
		
		double[]		probabilityNation;					//[industry]
		double[][]	probability;								//[region][industry]
		double[][][]	probabilityByCategory;			//[region][category][industry]
		double[][]	probabilityByIndustry;				//[category][industry]
		double			entropyNation;
		double[]		entropy;									//[region]
		double[][]	entropyByCategory;				//[region][category]
		double[]		entropyByIndustry;					//[category]
		
		double[]		probabilityRural;						//[industry]
		double[][]	ruralProbability;						//[region][industry]
		double[][][]	ruralProbabilityByCategory;	//[region][category][industry]
		double[][]	ruralProbabilityByIndustry;		//[category][industry]
		double			entropyRural;
		double[]		ruralEntropy;							//[region]
		double[][]	ruralEntropyByCategory;			//[region][category]
		double[]		ruralEntropyByIndustry;			//[category]
	
		double[]		probabilityUrban;						//[industry]
		double[][]	urbanProbability;						//[region][industry]
		double[][][]	urbanProbabilityByCategory;	//[region][category][industry]
		double[][]	urbanProbabilityByIndustry;	//[category][industry]	
		double			entropyUrban;
		double[]		urbanEntropy;							//[region]
		double[][]	urbanEntropyByCategory;		//[region][category]
		double[]		urbanEntropyByIndustry;		//[category]
		
		for(i=0 ; i<this.duration ; i++){
			probabilityNation = this.employeeRatioNation.get(i); 			
			entropy = new double[this.n_region[i]];
			probability = this.employeeRatio.get(i);		
			entropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			probabilityByCategory = this.employeeRatioByCategory.get(i);		
			entropyByIndustry = new double[this.n_category[i]];
			probabilityByIndustry = this.employeeRatioByIndustry.get(i);
	
			probabilityRural = this.employeeRatioRural.get(i); 		
			ruralEntropy = new double[this.n_region[i]];
			ruralProbability = this.ruralEmployeeRatio.get(i);		
			ruralEntropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			ruralProbabilityByCategory = this.ruralEmployeeRatioByCategory.get(i);		
			ruralEntropyByIndustry = new double[this.n_category[i]];
			ruralProbabilityByIndustry = this.ruralEmployeeRatioByIndustry.get(i);
			
			probabilityUrban = this.employeeRatioUrban.get(i); 		
			urbanEntropy = new double[this.n_region[i]];
			urbanProbability = this.urbanEmployeeRatio.get(i);		
			urbanEntropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			urbanProbabilityByCategory = this.urbanEmployeeRatioByCategory.get(i);		
			urbanEntropyByIndustry = new double[this.n_category[i]];
			urbanProbabilityByIndustry = this.urbanEmployeeRatioByIndustry.get(i);
			
			/*** calculate national entropy ***/
			entropyNation = 0.0;
			entropyRural = 0.0;
			entropyUrban = 0.0;
			for(j=0 ; j<this.n_industry[i]; j++){			
				tmpProb = probabilityNation[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyNation += subEntropy;
				
				tmpProb = probabilityRural[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyRural += subEntropy;
				
				tmpProb = probabilityUrban[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyUrban += subEntropy;
			}
			this.entropyNation[i] = entropyNation;
			this.entropyRural[i] = entropyRural;
			this.entropyUrban[i] = entropyUrban;
			
			/*** calculate entire entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				entropy[j] = 0.0;
				for(k=0 ; k<this.n_industry[i] ; k++){
					tmpProb = probability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					entropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					entropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_industry[i] ; k++){
						tmpProb = probabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						entropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				entropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_industry[i] ; k++){
					tmpProb = probabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					entropyByIndustry[l] += subEntropy;
				}
			}
			this.entropy.add(entropy);
			this.entropyByCategory.add(entropyByCategory);
			this.entropyByIndustry.add(entropyByIndustry);
			
			/*** calculate rural entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				ruralEntropy[j] = 0.0;
				for(k=0 ; k<this.n_industry[i] ; k++){
					tmpProb = ruralProbability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					ruralEntropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					ruralEntropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_industry[i] ; k++){
						tmpProb = ruralProbabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						ruralEntropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				ruralEntropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_industry[i] ; k++){
					tmpProb = ruralProbabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					ruralEntropyByIndustry[l] += subEntropy;
				}
			}
			this.ruralEntropy.add(ruralEntropy);
			this.ruralEntropyByCategory.add(ruralEntropyByCategory);
			this.ruralEntropyByIndustry.add(ruralEntropyByIndustry);
			
			/*** calculate urban entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				urbanEntropy[j] = 0.0;
				for(k=0 ; k<this.n_industry[i] ; k++){
					tmpProb = urbanProbability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					urbanEntropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					urbanEntropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_industry[i] ; k++){
						tmpProb = urbanProbabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						urbanEntropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				urbanEntropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_industry[i] ; k++){
					tmpProb = urbanProbabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					urbanEntropyByIndustry[l] += subEntropy;
				}
			}
			this.urbanEntropy.add(urbanEntropy);
			this.urbanEntropyByCategory.add(urbanEntropyByCategory);
			this.urbanEntropyByIndustry.add(urbanEntropyByIndustry);
		}		
	}
	
	public void calculateEmployeeSizeStatistics(){
		
		int i, j, k, l;
		
		double			nationTotal;
		double[]		employeeNation;								//[industry]
		double[]		employeeRatioNation;						//[industry]
		double[]		employeeSum;									//[region]
		double[][]	employee;										//[region][size]
		double[][]	employeeRatio;								//[region][size]
		double[][]	employeeSumByCategory;				//[region][category]
		double[][][]	employeeByCategory;						//[region][category][size]
		double[][][]	employeeRatioByCategory;				//[region][category][size]
		double[]		employeeSumByIndustry;				//[category]
		double[][]	employeeByIndustry;						//[category][size]
		double[][]	employeeRatioByIndustry;				//[category][size]

		double			ruralTotal;
		double[]		employeeRural;								//[industry]
		double[]		employeeRatioRural;						//[industry]
		double[]		ruralEmployeeSum;							//[region]
		double[][]	ruralEmployee;								//[region][size]
		double[][]	ruralEmployeeRatio;						//[region][size]
		double[][]	ruralEmployeeSumByCategory;		//[region][category]
		double[][][]	ruralEmployeeByCategory;				//[region][category][size]
		double[][][]	ruralEmployeeRatioByCategory;		//[region][category][size]
		double[]		ruralEmployeeSumByIndustry;		//[category]
		double[][]	ruralEmployeeByIndustry;				//[category][size]
		double[][]	ruralEmployeeRatioByIndustry;		//[category][size]

		double			urbanTotal;
		double[]		employeeUrban;								//[industry]
		double[]		employeeRatioUrban;						//[industry]
		double[]		urbanEmployeeSum;						//[region]
		double[][]	urbanEmployee;								//[region][size]
		double[][]	urbanEmployeeRatio;						//[region][size]
		double[][]	urbanEmployeeSumByCategory;		//[region][category]
		double[][][]	urbanEmployeeByCategory;			//[region][category][size]
		double[][][]	urbanEmployeeRatioByCategory;	//[region][category][size]
		double[]		urbanEmployeeSumByIndustry;		//[category]
		double[][]	urbanEmployeeByIndustry;				//[category][size]
		double[][]	urbanEmployeeRatioByIndustry;		//[category][size]
		
		for(i=0 ; i<this.duration ; i++){
			
			/*** initiate variables ***/
			nationTotal = this.nationTotalBySize[i];
			employeeNation = this.employeeNationBySize.get(i);
			employeeRatioNation = new double[this.n_sizeGroup];
			
			employee = this.employeeBySize.get(i);			
			employeeRatio = new double[this.n_region[i]][this.n_sizeGroup];
			employeeSum = new double[this.n_region[i]];		
			
			employeeByCategory = this.employeeByCategoryBySize.get(i);
			employeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			employeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_sizeGroup];
			
			employeeByIndustry = this.employeeByIndustryBySize.get(i);
			employeeSumByIndustry = new double[this.n_category[i]];
			employeeRatioByIndustry = new double[this.n_category[i]][this.n_sizeGroup];
			
			ruralTotal = this.ruralTotalBySize[i];
			employeeRural = this.employeeRuralBySize.get(i);
			employeeRatioRural = new double[this.n_sizeGroup];
			
			ruralEmployee = this.ruralEmployeeBySize.get(i);			
			ruralEmployeeRatio = new double[this.n_region[i]][this.n_sizeGroup];
			ruralEmployeeSum = new double[this.n_region[i]];		
			
			ruralEmployeeByCategory = this.ruralEmployeeByCategoryBySize.get(i);
			ruralEmployeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			ruralEmployeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_sizeGroup];
			
			ruralEmployeeByIndustry = this.ruralEmployeeByIndustryBySize.get(i);
			ruralEmployeeSumByIndustry = new double[this.n_category[i]];
			ruralEmployeeRatioByIndustry = new double[this.n_category[i]][this.n_sizeGroup];
			
			urbanTotal = this.urbanTotalBySize[i];
			employeeUrban = this.employeeUrbanBySize.get(i);
			employeeRatioUrban = new double[this.n_sizeGroup];
			
			urbanEmployee = this.urbanEmployeeBySize.get(i);			
			urbanEmployeeRatio = new double[this.n_region[i]][this.n_sizeGroup];
			urbanEmployeeSum = new double[this.n_region[i]];		
			
			urbanEmployeeByCategory = this.urbanEmployeeByCategoryBySize.get(i);
			urbanEmployeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			urbanEmployeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_sizeGroup];
			
			urbanEmployeeByIndustry = this.urbanEmployeeByIndustryBySize.get(i);
			urbanEmployeeSumByIndustry = new double[this.n_category[i]];
			urbanEmployeeRatioByIndustry = new double[this.n_category[i]][this.n_sizeGroup];
			
			for(j=0 ; j<this.n_region[i] ; j++){
				employeeSum[j] = 0.0;
				ruralEmployeeSum[j] = 0.0;
				urbanEmployeeSum[j] = 0.0;
			}
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_category[i] ; k++){
					employeeSumByCategory[j][k] = 0.0;
					ruralEmployeeSumByCategory[j][k] = 0.0;
					urbanEmployeeSumByCategory[j][k] = 0.0;
				}
			}
			for(k=0 ; k<this.n_category[i] ; k++){
				employeeSumByIndustry[k] = 0.0;
				ruralEmployeeSumByIndustry[k] = 0.0;
				urbanEmployeeSumByIndustry[k] = 0.0;
			}
			
			/*** sum region's total employees ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_sizeGroup ; k++){
					employeeSum[j] += employee[j][k];	
					ruralEmployeeSum[j] += ruralEmployee[j][k];	
					urbanEmployeeSum[j] += urbanEmployee[j][k];	
				}
			}
			for(j=0 ; j<this.n_region[i] ; j++){
				for(l=0 ; l<this.n_category[i] ; l++){
					for(k=0 ; k<this.n_sizeGroup ; k++){
						employeeSumByCategory[j][l] += employeeByCategory[j][l][k];
						ruralEmployeeSumByCategory[j][l] += ruralEmployeeByCategory[j][l][k];
						urbanEmployeeSumByCategory[j][l] += urbanEmployeeByCategory[j][l][k];
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				for(k=0 ; k<this.n_sizeGroup ; k++){
					employeeSumByIndustry[l] += employeeByIndustry[l][k];
					ruralEmployeeSumByIndustry[l] += ruralEmployeeByIndustry[l][k];
					urbanEmployeeSumByIndustry[l] += urbanEmployeeByIndustry[l][k];
				}
			}
			
			/*** calculate national employee ratio ***/
			for(j=0 ; j<this.n_sizeGroup ; j++){
				if(nationTotal > 0) employeeRatioNation[j] = employeeNation[j] / nationTotal;
				else if(nationTotal == 0) employeeRatioNation[j]  = 0.0;
				else{
					employeeRatioNation[j]  = 0.0;
					System.err.println("probability calculation error");
				}
				
				if(ruralTotal > 0) employeeRatioRural[j] = employeeRural[j] / ruralTotal;
				else if(ruralTotal == 0) employeeRatioRural[j]  = 0.0;
				else{
					employeeRatioRural[j]  = 0.0;
					System.err.println("probability calculation error");
				}
				
				if(urbanTotal > 0) employeeRatioUrban[j] = employeeUrban[j] / urbanTotal;
				else if(urbanTotal == 0) employeeRatioUrban[j]  = 0.0;
				else{
					employeeRatioUrban[j]  = 0.0;
					System.err.println("probability calculation error");
				}
			}
			
			/*** calculate employee ratio ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_sizeGroup ; k++){
					if(employeeSum[j] > 0) employeeRatio[j][k] = employee[j][k] / employeeSum[j];
					else if(employeeSum[j] == 0) employeeRatio[j][k] = 0.0;
					else{
						employeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(ruralEmployeeSum[j] > 0) ruralEmployeeRatio[j][k] = ruralEmployee[j][k] / ruralEmployeeSum[j];
					else if(ruralEmployeeSum[j] == 0) ruralEmployeeRatio[j][k] = 0.0;
					else{
						ruralEmployeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(urbanEmployeeSum[j]>0) urbanEmployeeRatio[j][k]=urbanEmployee[j][k]/urbanEmployeeSum[j];
					else if(urbanEmployeeSum[j] == 0) urbanEmployeeRatio[j][k] = 0.0;
					else{
						urbanEmployeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
				}
				
				/*** calculate employee ratio by industrial category ***/
				for(l=0 ; l<this.n_category[i] ; l++){
					for(k=0 ; k<this.n_sizeGroup ; k++){
						if(employeeSumByCategory[j][l] > 0) 
							employeeRatioByCategory[j][l][k] = employeeByCategory[j][l][k] / employeeSumByCategory[j][l];
						else if(employeeSumByCategory[j][l] == 0) employeeRatioByCategory[j][l][k] = 0.0;
						else{
							employeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
						
						if(ruralEmployeeSumByCategory[j][l] > 0) 
							ruralEmployeeRatioByCategory[j][l][k]
									= ruralEmployeeByCategory[j][l][k] / ruralEmployeeSumByCategory[j][l];
						else if(ruralEmployeeSumByCategory[j][l] == 0) ruralEmployeeRatioByCategory[j][l][k] = 0.0;
						else{
							ruralEmployeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
						
						if(urbanEmployeeSumByCategory[j][l] > 0) 
							urbanEmployeeRatioByCategory[j][l][k]
									= urbanEmployeeByCategory[j][l][k] / urbanEmployeeSumByCategory[j][l];
						else if(urbanEmployeeSumByCategory[j][l] == 0) urbanEmployeeRatioByCategory[j][l][k] = 0.0;
						else{
							urbanEmployeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
					}
				}	
			}
			
			/*** calculate employee ratio by industry***/
			for(l=0 ; l<this.n_category[i] ; l++){
				for(k=0 ; k<this.n_sizeGroup ; k++){
					if(employeeSumByIndustry[l] > 0) 
						employeeRatioByIndustry[l][k] = employeeByIndustry[l][k] / employeeSumByIndustry[l];
					else if(employeeSumByIndustry[l] == 0) employeeRatioByIndustry[l][k] = 0.0;
					else{
						employeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(ruralEmployeeSumByIndustry[l] > 0) 
						ruralEmployeeRatioByIndustry[l][k]
								= ruralEmployeeByIndustry[l][k] / ruralEmployeeSumByIndustry[l];
					else if(ruralEmployeeSumByIndustry[l] == 0) ruralEmployeeRatioByIndustry[l][k] = 0.0;
					else{
						ruralEmployeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(urbanEmployeeSumByIndustry[l] > 0) 
						urbanEmployeeRatioByIndustry[l][k]
								= urbanEmployeeByIndustry[l][k] / urbanEmployeeSumByIndustry[l];
					else if(urbanEmployeeSumByIndustry[l] == 0) urbanEmployeeRatioByIndustry[l][k] = 0.0;
					else{
						urbanEmployeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
				}
			}
			
			this.employeeRatioNationBySize.add(employeeRatioNation);
			this.employeeSumBySize.add(employeeSum);
			this.employeeRatioBySize.add(employeeRatio);
			this.employeeSumByCategoryBySize.add(employeeSumByCategory);
			this.employeeRatioByCategoryBySize.add(employeeRatioByCategory);
			this.employeeSumByIndustryBySize.add(employeeSumByIndustry);
			this.employeeRatioByIndustryBySize.add(employeeRatioByIndustry);
			
			this.employeeRatioRuralBySize.add(employeeRatioRural);
			this.ruralEmployeeSumBySize.add(ruralEmployeeSum);
			this.ruralEmployeeRatioBySize.add(ruralEmployeeRatio);
			this.ruralEmployeeSumByCategoryBySize.add(ruralEmployeeSumByCategory);
			this.ruralEmployeeRatioByCategoryBySize.add(ruralEmployeeRatioByCategory);
			this.ruralEmployeeSumByIndustryBySize.add(ruralEmployeeSumByIndustry);
			this.ruralEmployeeRatioByIndustryBySize.add(ruralEmployeeRatioByIndustry);
			
			this.employeeRatioUrbanBySize.add(employeeRatioUrban);
			this.urbanEmployeeSumBySize.add(urbanEmployeeSum);
			this.urbanEmployeeRatioBySize.add(urbanEmployeeRatio);
			this.urbanEmployeeSumByCategoryBySize.add(urbanEmployeeSumByCategory);
			this.urbanEmployeeRatioByCategoryBySize.add(urbanEmployeeRatioByCategory);
			this.urbanEmployeeSumByIndustryBySize.add(urbanEmployeeSumByIndustry);
			this.urbanEmployeeRatioByIndustryBySize.add(urbanEmployeeRatioByIndustry);
		}
	}
	
	public void calculateEmployeeLevelStatistics(){
		
		int i, j, k, l;
		
		double			nationTotal;
		double[]		employeeNation;								//[industry]
		double[]		employeeRatioNation;						//[industry]
		double[]		employeeSum;									//[region]
		double[][]	employee;										//[region][level]
		double[][]	employeeRatio;								//[region][level]
		double[][]	employeeSumByCategory;				//[region][category]
		double[][][]	employeeByCategory;						//[region][category][level]
		double[][][]	employeeRatioByCategory;				//[region][category][level]
		double[]		employeeSumByIndustry;				//[category]
		double[][]	employeeByIndustry;						//[category][level]
		double[][]	employeeRatioByIndustry;				//[category][level]

		double			ruralTotal;
		double[]		employeeRural;								//[industry]
		double[]		employeeRatioRural;						//[industry]
		double[]		ruralEmployeeSum;							//[region]
		double[][]	ruralEmployee;								//[region][level]
		double[][]	ruralEmployeeRatio;						//[region][level]
		double[][]	ruralEmployeeSumByCategory;		//[region][category]
		double[][][]	ruralEmployeeByCategory;				//[region][category][level]
		double[][][]	ruralEmployeeRatioByCategory;		//[region][category][level]
		double[]		ruralEmployeeSumByIndustry;		//[category]
		double[][]	ruralEmployeeByIndustry;				//[category][level]
		double[][]	ruralEmployeeRatioByIndustry;		//[category][level]

		double			urbanTotal;
		double[]		employeeUrban;								//[industry]
		double[]		employeeRatioUrban;						//[industry]
		double[]		urbanEmployeeSum;						//[region]
		double[][]	urbanEmployee;								//[region][level]
		double[][]	urbanEmployeeRatio;						//[region][level]
		double[][]	urbanEmployeeSumByCategory;		//[region][category]
		double[][][]	urbanEmployeeByCategory;			//[region][category][level]
		double[][][]	urbanEmployeeRatioByCategory;	//[region][category][level]
		double[]		urbanEmployeeSumByIndustry;		//[category]
		double[][]	urbanEmployeeByIndustry;				//[category][level]
		double[][]	urbanEmployeeRatioByIndustry;		//[category][level]
		
		for(i=0 ; i<this.duration ; i++){
			
			/*** initiate variables ***/
			nationTotal = this.nationTotalByLevel[i];
			employeeNation = this.employeeNationByLevel.get(i);
			employeeRatioNation = new double[this.n_levelGroup];
			
			employee = this.employeeByLevel.get(i);			
			employeeRatio = new double[this.n_region[i]][this.n_levelGroup];
			employeeSum = new double[this.n_region[i]];		
			
			employeeByCategory = this.employeeByCategoryByLevel.get(i);
			employeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			employeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_levelGroup];
			
			employeeByIndustry = this.employeeByIndustryByLevel.get(i);
			employeeSumByIndustry = new double[this.n_category[i]];
			employeeRatioByIndustry = new double[this.n_category[i]][this.n_levelGroup];
			
			ruralTotal = this.ruralTotalByLevel[i];
			employeeRural = this.employeeRuralByLevel.get(i);
			employeeRatioRural = new double[this.n_levelGroup];
			
			ruralEmployee = this.ruralEmployeeByLevel.get(i);			
			ruralEmployeeRatio = new double[this.n_region[i]][this.n_levelGroup];
			ruralEmployeeSum = new double[this.n_region[i]];		
			
			ruralEmployeeByCategory = this.ruralEmployeeByCategoryByLevel.get(i);
			ruralEmployeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			ruralEmployeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_levelGroup];
			
			ruralEmployeeByIndustry = this.ruralEmployeeByIndustryByLevel.get(i);
			ruralEmployeeSumByIndustry = new double[this.n_category[i]];
			ruralEmployeeRatioByIndustry = new double[this.n_category[i]][this.n_levelGroup];
			
			urbanTotal = this.urbanTotalByLevel[i];
			employeeUrban = this.employeeUrbanByLevel.get(i);
			employeeRatioUrban = new double[this.n_levelGroup];
			
			urbanEmployee = this.urbanEmployeeByLevel.get(i);			
			urbanEmployeeRatio = new double[this.n_region[i]][this.n_levelGroup];
			urbanEmployeeSum = new double[this.n_region[i]];		
			
			urbanEmployeeByCategory = this.urbanEmployeeByCategoryByLevel.get(i);
			urbanEmployeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			urbanEmployeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_levelGroup];
			
			urbanEmployeeByIndustry = this.urbanEmployeeByIndustryByLevel.get(i);
			urbanEmployeeSumByIndustry = new double[this.n_category[i]];
			urbanEmployeeRatioByIndustry = new double[this.n_category[i]][this.n_levelGroup];
			
			for(j=0 ; j<this.n_region[i] ; j++){
				employeeSum[j] = 0.0;
				ruralEmployeeSum[j] = 0.0;
				urbanEmployeeSum[j] = 0.0;
			}
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_category[i] ; k++){
					employeeSumByCategory[j][k] = 0.0;
					ruralEmployeeSumByCategory[j][k] = 0.0;
					urbanEmployeeSumByCategory[j][k] = 0.0;
				}
			}
			for(k=0 ; k<this.n_category[i] ; k++){
				employeeSumByIndustry[k] = 0.0;
				ruralEmployeeSumByIndustry[k] = 0.0;
				urbanEmployeeSumByIndustry[k] = 0.0;
			}
			
			/*** sum region's total employees ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_levelGroup ; k++){
					employeeSum[j] += employee[j][k];	
					ruralEmployeeSum[j] += ruralEmployee[j][k];	
					urbanEmployeeSum[j] += urbanEmployee[j][k];	
				}
			}
			for(j=0 ; j<this.n_region[i] ; j++){
				for(l=0 ; l<this.n_category[i] ; l++){
					for(k=0 ; k<this.n_levelGroup ; k++){
						employeeSumByCategory[j][l] += employeeByCategory[j][l][k];
						ruralEmployeeSumByCategory[j][l] += ruralEmployeeByCategory[j][l][k];
						urbanEmployeeSumByCategory[j][l] += urbanEmployeeByCategory[j][l][k];
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				for(k=0 ; k<this.n_levelGroup ; k++){
					employeeSumByIndustry[l] += employeeByIndustry[l][k];
					ruralEmployeeSumByIndustry[l] += ruralEmployeeByIndustry[l][k];
					urbanEmployeeSumByIndustry[l] += urbanEmployeeByIndustry[l][k];
				}
			}
			
			/*** calculate national employee ratio ***/
			for(j=0 ; j<this.n_levelGroup ; j++){
				if(nationTotal > 0) employeeRatioNation[j] = employeeNation[j] / nationTotal;
				else if(nationTotal == 0) employeeRatioNation[j]  = 0.0;
				else{
					employeeRatioNation[j]  = 0.0;
					System.err.println("probability calculation error");
				}
				
				if(ruralTotal > 0) employeeRatioRural[j] = employeeRural[j] / ruralTotal;
				else if(ruralTotal == 0) employeeRatioRural[j]  = 0.0;
				else{
					employeeRatioRural[j]  = 0.0;
					System.err.println("probability calculation error");
				}
				
				if(urbanTotal > 0) employeeRatioUrban[j] = employeeUrban[j] / urbanTotal;
				else if(urbanTotal == 0) employeeRatioUrban[j]  = 0.0;
				else{
					employeeRatioUrban[j]  = 0.0;
					System.err.println("probability calculation error");
				}
			}
			
			/*** calculate employee ratio ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_levelGroup ; k++){
					if(employeeSum[j] > 0) employeeRatio[j][k] = employee[j][k] / employeeSum[j];
					else if(employeeSum[j] == 0) employeeRatio[j][k] = 0.0;
					else{
						employeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(ruralEmployeeSum[j] > 0) ruralEmployeeRatio[j][k] = ruralEmployee[j][k] / ruralEmployeeSum[j];
					else if(ruralEmployeeSum[j] == 0) ruralEmployeeRatio[j][k] = 0.0;
					else{
						ruralEmployeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(urbanEmployeeSum[j]>0) urbanEmployeeRatio[j][k]=urbanEmployee[j][k]/urbanEmployeeSum[j];
					else if(urbanEmployeeSum[j] == 0) urbanEmployeeRatio[j][k] = 0.0;
					else{
						urbanEmployeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
				}
				
				/*** calculate employee ratio by industrial category ***/
				for(l=0 ; l<this.n_category[i] ; l++){
					for(k=0 ; k<this.n_levelGroup ; k++){
						if(employeeSumByCategory[j][l] > 0) 
							employeeRatioByCategory[j][l][k] = employeeByCategory[j][l][k] / employeeSumByCategory[j][l];
						else if(employeeSumByCategory[j][l] == 0) employeeRatioByCategory[j][l][k] = 0.0;
						else{
							employeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
						
						if(ruralEmployeeSumByCategory[j][l] > 0) 
							ruralEmployeeRatioByCategory[j][l][k]
									= ruralEmployeeByCategory[j][l][k] / ruralEmployeeSumByCategory[j][l];
						else if(ruralEmployeeSumByCategory[j][l] == 0) ruralEmployeeRatioByCategory[j][l][k] = 0.0;
						else{
							ruralEmployeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
						
						if(urbanEmployeeSumByCategory[j][l] > 0) 
							urbanEmployeeRatioByCategory[j][l][k]
									= urbanEmployeeByCategory[j][l][k] / urbanEmployeeSumByCategory[j][l];
						else if(urbanEmployeeSumByCategory[j][l] == 0) urbanEmployeeRatioByCategory[j][l][k] = 0.0;
						else{
							urbanEmployeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
					}
				}	
			}
			
			/*** calculate employee ratio by industry***/
			for(l=0 ; l<this.n_category[i] ; l++){
				for(k=0 ; k<this.n_levelGroup ; k++){
					if(employeeSumByIndustry[l] > 0) 
						employeeRatioByIndustry[l][k] = employeeByIndustry[l][k] / employeeSumByIndustry[l];
					else if(employeeSumByIndustry[l] == 0) employeeRatioByIndustry[l][k] = 0.0;
					else{
						employeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(ruralEmployeeSumByIndustry[l] > 0) 
						ruralEmployeeRatioByIndustry[l][k]
								= ruralEmployeeByIndustry[l][k] / ruralEmployeeSumByIndustry[l];
					else if(ruralEmployeeSumByIndustry[l] == 0) ruralEmployeeRatioByIndustry[l][k] = 0.0;
					else{
						ruralEmployeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(urbanEmployeeSumByIndustry[l] > 0) 
						urbanEmployeeRatioByIndustry[l][k]
								= urbanEmployeeByIndustry[l][k] / urbanEmployeeSumByIndustry[l];
					else if(urbanEmployeeSumByIndustry[l] == 0) urbanEmployeeRatioByIndustry[l][k] = 0.0;
					else{
						urbanEmployeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
				}
			}
			
			this.employeeRatioNationByLevel.add(employeeRatioNation);
			this.employeeSumByLevel.add(employeeSum);
			this.employeeRatioByLevel.add(employeeRatio);
			this.employeeSumByCategoryByLevel.add(employeeSumByCategory);
			this.employeeRatioByCategoryByLevel.add(employeeRatioByCategory);
			this.employeeSumByIndustryByLevel.add(employeeSumByIndustry);
			this.employeeRatioByIndustryByLevel.add(employeeRatioByIndustry);
			
			this.employeeRatioRuralByLevel.add(employeeRatioRural);
			this.ruralEmployeeSumByLevel.add(ruralEmployeeSum);
			this.ruralEmployeeRatioByLevel.add(ruralEmployeeRatio);
			this.ruralEmployeeSumByCategoryByLevel.add(ruralEmployeeSumByCategory);
			this.ruralEmployeeRatioByCategoryByLevel.add(ruralEmployeeRatioByCategory);
			this.ruralEmployeeSumByIndustryByLevel.add(ruralEmployeeSumByIndustry);
			this.ruralEmployeeRatioByIndustryByLevel.add(ruralEmployeeRatioByIndustry);
			
			this.employeeRatioUrbanByLevel.add(employeeRatioUrban);
			this.urbanEmployeeSumByLevel.add(urbanEmployeeSum);
			this.urbanEmployeeRatioByLevel.add(urbanEmployeeRatio);
			this.urbanEmployeeSumByCategoryByLevel.add(urbanEmployeeSumByCategory);
			this.urbanEmployeeRatioByCategoryByLevel.add(urbanEmployeeRatioByCategory);
			this.urbanEmployeeSumByIndustryByLevel.add(urbanEmployeeSumByIndustry);
			this.urbanEmployeeRatioByIndustryByLevel.add(urbanEmployeeRatioByIndustry);
		}
	}
	
	public void calculateEmployeeAgeStatistics(){
		
		int i, j, k, l;
		
		double			nationTotal;
		double[]		employeeNation;								//[industry]
		double[]		employeeRatioNation;						//[industry]
		double[]		employeeSum;									//[region]
		double[][]	employee;										//[region][age]
		double[][]	employeeRatio;								//[region][age]
		double[][]	employeeSumByCategory;				//[region][category]
		double[][][]	employeeByCategory;						//[region][category][age]
		double[][][]	employeeRatioByCategory;				//[region][category][age]
		double[]		employeeSumByIndustry;				//[category]
		double[][]	employeeByIndustry;						//[category][age]
		double[][]	employeeRatioByIndustry;				//[category][age]

		double			ruralTotal;
		double[]		employeeRural;								//[industry]
		double[]		employeeRatioRural;						//[industry]
		double[]		ruralEmployeeSum;							//[region]
		double[][]	ruralEmployee;								//[region][age]
		double[][]	ruralEmployeeRatio;						//[region][age]
		double[][]	ruralEmployeeSumByCategory;		//[region][category]
		double[][][]	ruralEmployeeByCategory;				//[region][category][age]
		double[][][]	ruralEmployeeRatioByCategory;		//[region][category][age]
		double[]		ruralEmployeeSumByIndustry;		//[category]
		double[][]	ruralEmployeeByIndustry;				//[category][age]
		double[][]	ruralEmployeeRatioByIndustry;		//[category][age]

		double			urbanTotal;
		double[]		employeeUrban;								//[industry]
		double[]		employeeRatioUrban;						//[industry]
		double[]		urbanEmployeeSum;						//[region]
		double[][]	urbanEmployee;								//[region][age]
		double[][] 	urbanEmployeeRatio;						//[region][age]
		double[][]	urbanEmployeeSumByCategory;		//[region][category]
		double[][][]	urbanEmployeeByCategory;			//[region][category][age]
		double[][][]	urbanEmployeeRatioByCategory;	//[region][category][age]
		double[]		urbanEmployeeSumByIndustry;		//[category]
		double[][]	urbanEmployeeByIndustry;				//[category][age]
		double[][]	urbanEmployeeRatioByIndustry;		//[category][age]
		
		for(i=0 ; i<this.duration ; i++){
			
			/*** initiate variables ***/
			nationTotal = this.nationTotalByAge[i];
			employeeNation = this.employeeNationByAge.get(i);
			employeeRatioNation = new double[this.n_ageGroup];
			
			employee = this.employeeByAge.get(i);			
			employeeRatio = new double[this.n_region[i]][this.n_ageGroup];
			employeeSum = new double[this.n_region[i]];		
			
			employeeByCategory = this.employeeByCategoryByAge.get(i);
			employeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			employeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_ageGroup];
			
			employeeByIndustry = this.employeeByIndustryByAge.get(i);
			employeeSumByIndustry = new double[this.n_category[i]];
			employeeRatioByIndustry = new double[this.n_category[i]][this.n_ageGroup];
			
			ruralTotal = this.ruralTotalByAge[i];
			employeeRural = this.employeeRuralByAge.get(i);
			employeeRatioRural = new double[this.n_ageGroup];
			
			ruralEmployee = this.ruralEmployeeByAge.get(i);			
			ruralEmployeeRatio = new double[this.n_region[i]][this.n_ageGroup];
			ruralEmployeeSum = new double[this.n_region[i]];		
			
			ruralEmployeeByCategory = this.ruralEmployeeByCategoryByAge.get(i);
			ruralEmployeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			ruralEmployeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_ageGroup];
			
			ruralEmployeeByIndustry = this.ruralEmployeeByIndustryByAge.get(i);
			ruralEmployeeSumByIndustry = new double[this.n_category[i]];
			ruralEmployeeRatioByIndustry = new double[this.n_category[i]][this.n_ageGroup];
			
			urbanTotal = this.urbanTotalByAge[i];
			employeeUrban = this.employeeUrbanByAge.get(i);
			employeeRatioUrban = new double[this.n_ageGroup];
			
			urbanEmployee = this.urbanEmployeeByAge.get(i);			
			urbanEmployeeRatio = new double[this.n_region[i]][this.n_ageGroup];
			urbanEmployeeSum = new double[this.n_region[i]];		
			
			urbanEmployeeByCategory = this.urbanEmployeeByCategoryByAge.get(i);
			urbanEmployeeSumByCategory = new double[this.n_region[i]][this.n_category[i]];
			urbanEmployeeRatioByCategory = new double[this.n_region[i]][this.n_category[i]][this.n_ageGroup];
			
			urbanEmployeeByIndustry = this.urbanEmployeeByIndustryByAge.get(i);
			urbanEmployeeSumByIndustry = new double[this.n_category[i]];
			urbanEmployeeRatioByIndustry = new double[this.n_category[i]][this.n_ageGroup];
			
			for(j=0 ; j<this.n_region[i] ; j++){
				employeeSum[j] = 0.0;
				ruralEmployeeSum[j] = 0.0;
				urbanEmployeeSum[j] = 0.0;
			}
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_category[i] ; k++){
					employeeSumByCategory[j][k] = 0.0;
					ruralEmployeeSumByCategory[j][k] = 0.0;
					urbanEmployeeSumByCategory[j][k] = 0.0;
				}
			}
			for(k=0 ; k<this.n_category[i] ; k++){
				employeeSumByIndustry[k] = 0.0;
				ruralEmployeeSumByIndustry[k] = 0.0;
				urbanEmployeeSumByIndustry[k] = 0.0;
			}
			
			/*** sum region's total employees ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_ageGroup ; k++){
					employeeSum[j] += employee[j][k];	
					ruralEmployeeSum[j] += ruralEmployee[j][k];	
					urbanEmployeeSum[j] += urbanEmployee[j][k];	
				}
			}
			for(j=0 ; j<this.n_region[i] ; j++){
				for(l=0 ; l<this.n_category[i] ; l++){
					for(k=0 ; k<this.n_ageGroup ; k++){
						employeeSumByCategory[j][l] += employeeByCategory[j][l][k];
						ruralEmployeeSumByCategory[j][l] += ruralEmployeeByCategory[j][l][k];
						urbanEmployeeSumByCategory[j][l] += urbanEmployeeByCategory[j][l][k];
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				for(k=0 ; k<this.n_ageGroup ; k++){
					employeeSumByIndustry[l] += employeeByIndustry[l][k];
					ruralEmployeeSumByIndustry[l] += ruralEmployeeByIndustry[l][k];
					urbanEmployeeSumByIndustry[l] += urbanEmployeeByIndustry[l][k];
				}
			}
			
			/*** calculate national employee ratio ***/
			for(j=0 ; j<this.n_ageGroup ; j++){
				if(nationTotal > 0) employeeRatioNation[j] = employeeNation[j] / nationTotal;
				else if(nationTotal == 0) employeeRatioNation[j]  = 0.0;
				else{
					employeeRatioNation[j]  = 0.0;
					System.err.println("probability calculation error");
				}
				
				if(ruralTotal > 0) employeeRatioRural[j] = employeeRural[j] / ruralTotal;
				else if(ruralTotal == 0) employeeRatioRural[j]  = 0.0;
				else{
					employeeRatioRural[j]  = 0.0;
					System.err.println("probability calculation error");
				}
				
				if(urbanTotal > 0) employeeRatioUrban[j] = employeeUrban[j] / urbanTotal;
				else if(urbanTotal == 0) employeeRatioUrban[j]  = 0.0;
				else{
					employeeRatioUrban[j]  = 0.0;
					System.err.println("probability calculation error");
				}
			}
			
			/*** calculate employee ratio ***/
			for(j=0 ; j<this.n_region[i] ; j++){
				for(k=0 ; k<this.n_ageGroup ; k++){
					if(employeeSum[j] > 0) employeeRatio[j][k] = employee[j][k] / employeeSum[j];
					else if(employeeSum[j] == 0) employeeRatio[j][k] = 0.0;
					else{
						employeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(ruralEmployeeSum[j] > 0) ruralEmployeeRatio[j][k] = ruralEmployee[j][k] / ruralEmployeeSum[j];
					else if(ruralEmployeeSum[j] == 0) ruralEmployeeRatio[j][k] = 0.0;
					else{
						ruralEmployeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(urbanEmployeeSum[j]>0) urbanEmployeeRatio[j][k]=urbanEmployee[j][k]/urbanEmployeeSum[j];
					else if(urbanEmployeeSum[j] == 0) urbanEmployeeRatio[j][k] = 0.0;
					else{
						urbanEmployeeRatio[j][k] = 0.0;
						System.err.println("probability calculation error");
					}
				}
				
				/*** calculate employee ratio by industrial category ***/
				for(l=0 ; l<this.n_category[i] ; l++){
					for(k=0 ; k<this.n_ageGroup ; k++){
						if(employeeSumByCategory[j][l] > 0) 
							employeeRatioByCategory[j][l][k] = employeeByCategory[j][l][k] / employeeSumByCategory[j][l];
						else if(employeeSumByCategory[j][l] == 0) employeeRatioByCategory[j][l][k] = 0.0;
						else{
							employeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
						
						if(ruralEmployeeSumByCategory[j][l] > 0) 
							ruralEmployeeRatioByCategory[j][l][k]
									= ruralEmployeeByCategory[j][l][k] / ruralEmployeeSumByCategory[j][l];
						else if(ruralEmployeeSumByCategory[j][l] == 0) ruralEmployeeRatioByCategory[j][l][k] = 0.0;
						else{
							ruralEmployeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
						
						if(urbanEmployeeSumByCategory[j][l] > 0) 
							urbanEmployeeRatioByCategory[j][l][k]
									= urbanEmployeeByCategory[j][l][k] / urbanEmployeeSumByCategory[j][l];
						else if(urbanEmployeeSumByCategory[j][l] == 0) urbanEmployeeRatioByCategory[j][l][k] = 0.0;
						else{
							urbanEmployeeRatioByCategory[j][l][k] = 0.0;
							System.err.println("probability calculation error");
						}
					}
				}	
			}
			
			/*** calculate employee ratio by industry***/
			for(l=0 ; l<this.n_category[i] ; l++){
				for(k=0 ; k<this.n_ageGroup ; k++){
					if(employeeSumByIndustry[l] > 0) 
						employeeRatioByIndustry[l][k] = employeeByIndustry[l][k] / employeeSumByIndustry[l];
					else if(employeeSumByIndustry[l] == 0) employeeRatioByIndustry[l][k] = 0.0;
					else{
						employeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(ruralEmployeeSumByIndustry[l] > 0) 
						ruralEmployeeRatioByIndustry[l][k]
								= ruralEmployeeByIndustry[l][k] / ruralEmployeeSumByIndustry[l];
					else if(ruralEmployeeSumByIndustry[l] == 0) ruralEmployeeRatioByIndustry[l][k] = 0.0;
					else{
						ruralEmployeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
					
					if(urbanEmployeeSumByIndustry[l] > 0) 
						urbanEmployeeRatioByIndustry[l][k]
								= urbanEmployeeByIndustry[l][k] / urbanEmployeeSumByIndustry[l];
					else if(urbanEmployeeSumByIndustry[l] == 0) urbanEmployeeRatioByIndustry[l][k] = 0.0;
					else{
						urbanEmployeeRatioByIndustry[l][k] = 0.0;
						System.err.println("probability calculation error");
					}
				}
			}
			
			this.employeeRatioNationByAge.add(employeeRatioNation);
			this.employeeSumByAge.add(employeeSum);
			this.employeeRatioByAge.add(employeeRatio);
			this.employeeSumByCategoryByAge.add(employeeSumByCategory);
			this.employeeRatioByCategoryByAge.add(employeeRatioByCategory);
			this.employeeSumByIndustryByAge.add(employeeSumByIndustry);
			this.employeeRatioByIndustryByAge.add(employeeRatioByIndustry);
			
			this.employeeRatioRuralByAge.add(employeeRatioRural);
			this.ruralEmployeeSumByAge.add(ruralEmployeeSum);
			this.ruralEmployeeRatioByAge.add(ruralEmployeeRatio);
			this.ruralEmployeeSumByCategoryByAge.add(ruralEmployeeSumByCategory);
			this.ruralEmployeeRatioByCategoryByAge.add(ruralEmployeeRatioByCategory);
			this.ruralEmployeeSumByIndustryByAge.add(ruralEmployeeSumByIndustry);
			this.ruralEmployeeRatioByIndustryByAge.add(ruralEmployeeRatioByIndustry);
			
			this.employeeRatioUrbanByAge.add(employeeRatioUrban);
			this.urbanEmployeeSumByAge.add(urbanEmployeeSum);
			this.urbanEmployeeRatioByAge.add(urbanEmployeeRatio);
			this.urbanEmployeeSumByCategoryByAge.add(urbanEmployeeSumByCategory);
			this.urbanEmployeeRatioByCategoryByAge.add(urbanEmployeeRatioByCategory);
			this.urbanEmployeeSumByIndustryByAge.add(urbanEmployeeSumByIndustry);
			this.urbanEmployeeRatioByIndustryByAge.add(urbanEmployeeRatioByIndustry);
		}
	}
	
	public void calculateEmployeeSizeEntropy(){
		
		int i, j, k, l;

		double tmpProb;
		double subEntropy;
		double base = Math.log(2);
		
		double[]		probabilityNation;					//[size]
		double[][]	probability;								//[region][size]
		double[][][]	probabilityByCategory;			//[region][category][size]
		double[][]	probabilityByIndustry;				//[category][size]
		double			entropyNation;
		double[]		entropy;									//[region]
		double[][]	entropyByCategory;				//[region][category]
		double[]		entropyByIndustry;					//[category]
		
		double[]		probabilityRural;						//[size]
		double[][]	ruralProbability;						//[region][size]
		double[][][]	ruralProbabilityByCategory;	//[region][category][size]
		double[][]	ruralProbabilityByIndustry;		//[category][size]
		double			entropyRural;
		double[]		ruralEntropy;							//[region]
		double[][]	ruralEntropyByCategory;			//[region][category]
		double[]		ruralEntropyByIndustry;			//[category]
	
		double[]		probabilityUrban;						//[size]
		double[][]	urbanProbability;						//[region][size]
		double[][][]	urbanProbabilityByCategory;	//[region][category][size]
		double[][]	urbanProbabilityByIndustry;	//[category][size]
		double			entropyUrban;
		double[]		urbanEntropy;							//[region]
		double[][]	urbanEntropyByCategory;		//[region][category]
		double[]		urbanEntropyByIndustry;		//[category]
		
		for(i=0 ; i<this.duration ; i++){
			probabilityNation = this.employeeRatioNationBySize.get(i); 
			entropy = new double[this.n_region[i]];
			probability = this.employeeRatioBySize.get(i);		
			entropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			probabilityByCategory = this.employeeRatioByCategoryBySize.get(i);		
			entropyByIndustry = new double[this.n_category[i]];
			probabilityByIndustry = this.employeeRatioByIndustryBySize.get(i);
	
			probabilityRural = this.employeeRatioRuralBySize.get(i); 	
			ruralEntropy = new double[this.n_region[i]];
			ruralProbability = this.ruralEmployeeRatioBySize.get(i);		
			ruralEntropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			ruralProbabilityByCategory = this.ruralEmployeeRatioByCategoryBySize.get(i);		
			ruralEntropyByIndustry = new double[this.n_category[i]];
			ruralProbabilityByIndustry = this.ruralEmployeeRatioByIndustryBySize.get(i);
			
			probabilityUrban = this.employeeRatioUrbanBySize.get(i); 	
			urbanEntropy = new double[this.n_region[i]];
			urbanProbability = this.urbanEmployeeRatioBySize.get(i);		
			urbanEntropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			urbanProbabilityByCategory = this.urbanEmployeeRatioByCategoryBySize.get(i);		
			urbanEntropyByIndustry = new double[this.n_category[i]];
			urbanProbabilityByIndustry = this.urbanEmployeeRatioByIndustryBySize.get(i);
			
			/*** calculate national entropy ***/
			entropyNation = 0.0;
			entropyRural = 0.0;
			entropyUrban = 0.0;
			for(j=0 ; j<this.n_sizeGroup; j++){			
				tmpProb = probabilityNation[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyNation += subEntropy;
				
				tmpProb = probabilityRural[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyRural += subEntropy;
				
				tmpProb = probabilityUrban[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyUrban += subEntropy;
			}
			this.entropyNationBySize[i] = entropyNation;
			this.entropyRuralBySize[i] = entropyRural;
			this.entropyUrbanBySize[i] = entropyUrban;
			
			/*** calculate entire entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				entropy[j] = 0.0;
				for(k=0 ; k<this.n_sizeGroup ; k++){
					tmpProb = probability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					entropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					entropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_sizeGroup ; k++){
						tmpProb = probabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						entropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				entropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_sizeGroup ; k++){
					tmpProb = probabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					entropyByIndustry[l] += subEntropy;
				}
			}
			this.entropyBySize.add(entropy);
			this.entropyBySizeByCategory.add(entropyByCategory);
			this.entropyBySizeByIndustry.add(entropyByIndustry);
			
			/*** calculate rural entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				ruralEntropy[j] = 0.0;
				for(k=0 ; k<this.n_sizeGroup ; k++){
					tmpProb = ruralProbability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					ruralEntropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					ruralEntropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_sizeGroup ; k++){
						tmpProb = ruralProbabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						ruralEntropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				ruralEntropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_sizeGroup ; k++){
					tmpProb = ruralProbabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					ruralEntropyByIndustry[l] += subEntropy;
				}
			}
			this.ruralEntropyBySize.add(ruralEntropy);
			this.ruralEntropyBySizeByCategory.add(ruralEntropyByCategory);
			this.ruralEntropyBySizeByIndustry.add(ruralEntropyByIndustry);
			
			/*** calculate urban entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				urbanEntropy[j] = 0.0;
				for(k=0 ; k<this.n_sizeGroup ; k++){
					tmpProb = urbanProbability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					urbanEntropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					urbanEntropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_sizeGroup ; k++){
						tmpProb = urbanProbabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						urbanEntropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				urbanEntropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_sizeGroup ; k++){
					tmpProb = urbanProbabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					urbanEntropyByIndustry[l] += subEntropy;
				}
			}
			this.urbanEntropyBySize.add(urbanEntropy);
			this.urbanEntropyBySizeByCategory.add(urbanEntropyByCategory);
			this.urbanEntropyBySizeByIndustry.add(urbanEntropyByIndustry);
		}		
	}
	
	public void calculateEmployeeLevelEntropy(){
		
		int i, j, k, l;

		double tmpProb;
		double subEntropy;
		double base = Math.log(2);
		
		double[]		probabilityNation;					//[level]
		double[][]	probability;								//[region][level]
		double[][][]	probabilityByCategory;			//[region][category][level]
		double[][]	probabilityByIndustry;				//[category][level]
		double			entropyNation;
		double[]		entropy;									//[region]
		double[][]	entropyByCategory;				//[region][category]
		double[]		entropyByIndustry;					//[category]
		
		double[]		probabilityRural;						//[level]
		double[][]	ruralProbability;						//[region][level]
		double[][][]	ruralProbabilityByCategory;	//[region][category][level]
		double[][]	ruralProbabilityByIndustry;		//[category][level]
		double			entropyRural;
		double[]		ruralEntropy;							//[region]
		double[][]	ruralEntropyByCategory;			//[region][category]
		double[]		ruralEntropyByIndustry;			//[category]
	
		double[]		probabilityUrban;						//[level]
		double[][]	urbanProbability;						//[region][level]
		double[][][]	urbanProbabilityByCategory;	//[region][category][level]
		double[][]	urbanProbabilityByIndustry;	//[category][level]	
		double			entropyUrban;
		double[]		urbanEntropy;							//[region]
		double[][]	urbanEntropyByCategory;		//[region][category]
		double[]		urbanEntropyByIndustry;		//[category]
		
		for(i=0 ; i<this.duration ; i++){
			probabilityNation = this.employeeRatioNationByLevel.get(i); 
			entropy = new double[this.n_region[i]];
			probability = this.employeeRatioByLevel.get(i);		
			entropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			probabilityByCategory = this.employeeRatioByCategoryByLevel.get(i);		
			entropyByIndustry = new double[this.n_category[i]];
			probabilityByIndustry = this.employeeRatioByIndustryByLevel.get(i);
	
			probabilityRural = this.employeeRatioRuralByLevel.get(i); 
			ruralEntropy = new double[this.n_region[i]];
			ruralProbability = this.ruralEmployeeRatioByLevel.get(i);		
			ruralEntropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			ruralProbabilityByCategory = this.ruralEmployeeRatioByCategoryByLevel.get(i);		
			ruralEntropyByIndustry = new double[this.n_category[i]];
			ruralProbabilityByIndustry = this.ruralEmployeeRatioByIndustryByLevel.get(i);
			
			probabilityUrban = this.employeeRatioUrbanByLevel.get(i); 
			urbanEntropy = new double[this.n_region[i]];
			urbanProbability = this.urbanEmployeeRatioByLevel.get(i);		
			urbanEntropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			urbanProbabilityByCategory = this.urbanEmployeeRatioByCategoryByLevel.get(i);		
			urbanEntropyByIndustry = new double[this.n_category[i]];
			urbanProbabilityByIndustry = this.urbanEmployeeRatioByIndustryByLevel.get(i);
			
			/*** calculate national entropy ***/
			entropyNation = 0.0;
			entropyRural = 0.0;
			entropyUrban = 0.0;
			for(j=0 ; j<this.n_levelGroup; j++){			
				tmpProb = probabilityNation[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyNation += subEntropy;
				
				tmpProb = probabilityRural[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyRural += subEntropy;
				
				tmpProb = probabilityUrban[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyUrban += subEntropy;
			}
			this.entropyNationByLevel[i] = entropyNation;
			this.entropyRuralByLevel[i] = entropyRural;
			this.entropyUrbanByLevel[i] = entropyUrban;
			
			/*** calculate entire entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				entropy[j] = 0.0;
				for(k=0 ; k<this.n_levelGroup ; k++){
					tmpProb = probability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					entropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					entropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_levelGroup ; k++){
						tmpProb = probabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						entropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				entropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_levelGroup ; k++){
					tmpProb = probabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					entropyByIndustry[l] += subEntropy;
				}
			}
			this.entropyByLevel.add(entropy);
			this.entropyByLevelByCategory.add(entropyByCategory);
			this.entropyByLevelByIndustry.add(entropyByIndustry);
			
			/*** calculate rural entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				ruralEntropy[j] = 0.0;
				for(k=0 ; k<this.n_levelGroup ; k++){
					tmpProb = ruralProbability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					ruralEntropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					ruralEntropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_levelGroup ; k++){
						tmpProb = ruralProbabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						ruralEntropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				ruralEntropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_levelGroup ; k++){
					tmpProb = ruralProbabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					ruralEntropyByIndustry[l] += subEntropy;
				}
			}
			this.ruralEntropyByLevel.add(ruralEntropy);
			this.ruralEntropyByLevelByCategory.add(ruralEntropyByCategory);
			this.ruralEntropyByLevelByIndustry.add(ruralEntropyByIndustry);
			
			/*** calculate urban entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				urbanEntropy[j] = 0.0;
				for(k=0 ; k<this.n_levelGroup ; k++){
					tmpProb = urbanProbability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					urbanEntropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					urbanEntropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_levelGroup ; k++){
						tmpProb = urbanProbabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						urbanEntropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				urbanEntropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_levelGroup ; k++){
					tmpProb = urbanProbabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					urbanEntropyByIndustry[l] += subEntropy;
				}
			}
			this.urbanEntropyByLevel.add(urbanEntropy);
			this.urbanEntropyByLevelByCategory.add(urbanEntropyByCategory);
			this.urbanEntropyByLevelByIndustry.add(urbanEntropyByIndustry);
		}		
	}
	
	public void calculateEmployeeAgeEntropy(){
		
		int i, j, k, l;

		double tmpProb;
		double subEntropy;
		double base = Math.log(2);
		
		double[]		probabilityNation;					//[age]
		double[][]	probability;								//[region][age]
		double[][][]	probabilityByCategory;			//[region][category][age]
		double[][]	probabilityByIndustry;				//[category][age]
		double			entropyNation;
		double[]		entropy;									//[region]
		double[][]	entropyByCategory;				//[region][category]
		double[]		entropyByIndustry;					//[category]
		
		double[]		probabilityRural;						//[age]
		double[][]	ruralProbability;						//[region][age]
		double[][][]	ruralProbabilityByCategory;	//[region][category][age]
		double[][]	ruralProbabilityByIndustry;		//[category][age]
		double			entropyRural;
		double[]		ruralEntropy;							//[region]
		double[][]	ruralEntropyByCategory;			//[region][category]
		double[]		ruralEntropyByIndustry;			//[category]
	
		double[]		probabilityUrban;						//[age]
		double[][]	urbanProbability;						//[region][age]
		double[][][]	urbanProbabilityByCategory;	//[region][category][age]
		double[][]	urbanProbabilityByIndustry;	//[category][age]	
		double			entropyUrban;
		double[]		urbanEntropy;							//[region]
		double[][]	urbanEntropyByCategory;		//[region][category]
		double[]		urbanEntropyByIndustry;		//[category]
		
		for(i=0 ; i<this.duration ; i++){
			probabilityNation = this.employeeRatioNationByAge.get(i); 
			entropy = new double[this.n_region[i]];
			probability = this.employeeRatioByAge.get(i);		
			entropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			probabilityByCategory = this.employeeRatioByCategoryByAge.get(i);		
			entropyByIndustry = new double[this.n_category[i]];
			probabilityByIndustry = this.employeeRatioByIndustryByAge.get(i);
	
			probabilityRural = this.employeeRatioRuralByAge.get(i); 
			ruralEntropy = new double[this.n_region[i]];
			ruralProbability = this.ruralEmployeeRatioByAge.get(i);		
			ruralEntropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			ruralProbabilityByCategory = this.ruralEmployeeRatioByCategoryByAge.get(i);		
			ruralEntropyByIndustry = new double[this.n_category[i]];
			ruralProbabilityByIndustry = this.ruralEmployeeRatioByIndustryByAge.get(i);
			
			probabilityUrban = this.employeeRatioUrbanByAge.get(i); 
			urbanEntropy = new double[this.n_region[i]];
			urbanProbability = this.urbanEmployeeRatioByAge.get(i);		
			urbanEntropyByCategory = new double[this.n_region[i]][this.n_category[i]];
			urbanProbabilityByCategory = this.urbanEmployeeRatioByCategoryByAge.get(i);		
			urbanEntropyByIndustry = new double[this.n_category[i]];
			urbanProbabilityByIndustry = this.urbanEmployeeRatioByIndustryByAge.get(i);
			
			/*** calculate national entropy ***/
			entropyNation = 0.0;
			entropyRural = 0.0;
			entropyUrban = 0.0;
			for(j=0 ; j<this.n_ageGroup; j++){			
				tmpProb = probabilityNation[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyNation += subEntropy;
				
				tmpProb = probabilityRural[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyRural += subEntropy;
				
				tmpProb = probabilityUrban[j];
				if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
				else if(tmpProb == 0) subEntropy = 0;
				else{
					subEntropy = 0;
					System.err.println("entropy calculation error");
				}					
				entropyUrban += subEntropy;
			}
			this.entropyNationByAge[i] = entropyNation;
			this.entropyRuralByAge[i] = entropyRural;
			this.entropyUrbanByAge[i] = entropyUrban;
			
			/*** calculate entire entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				entropy[j] = 0.0;
				for(k=0 ; k<this.n_ageGroup ; k++){
					tmpProb = probability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					entropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					entropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_ageGroup ; k++){
						tmpProb = probabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						entropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				entropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_ageGroup ; k++){
					tmpProb = probabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					entropyByIndustry[l] += subEntropy;
				}
			}
			this.entropyByAge.add(entropy);
			this.entropyByAgeByCategory.add(entropyByCategory);
			this.entropyByAgeByIndustry.add(entropyByIndustry);
			
			/*** calculate rural entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				ruralEntropy[j] = 0.0;
				for(k=0 ; k<this.n_ageGroup ; k++){
					tmpProb = ruralProbability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					ruralEntropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					ruralEntropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_ageGroup ; k++){
						tmpProb = ruralProbabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						ruralEntropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				ruralEntropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_ageGroup ; k++){
					tmpProb = ruralProbabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					ruralEntropyByIndustry[l] += subEntropy;
				}
			}
			this.ruralEntropyByAge.add(ruralEntropy);
			this.ruralEntropyByAgeByCategory.add(ruralEntropyByCategory);
			this.ruralEntropyByAgeByIndustry.add(ruralEntropyByIndustry);
			
			/*** calculate urban entropy ***/
			for(j=0 ; j<this.n_region[i]; j++){
				urbanEntropy[j] = 0.0;
				for(k=0 ; k<this.n_ageGroup ; k++){
					tmpProb = urbanProbability[j][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					urbanEntropy[j] += subEntropy;
				}	
				
				for(l=0 ; l<this.n_category[i] ; l++){
					urbanEntropyByCategory[j][l] = 0.0;
					for(k=0 ; k<this.n_ageGroup ; k++){
						tmpProb = urbanProbabilityByCategory[j][l][k];
						if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
						else if(tmpProb == 0) subEntropy = 0;
						else{
							subEntropy = 0;
							System.err.println("entropy calculation error");
						}					
						urbanEntropyByCategory[j][l] += subEntropy;
					}
				}
			}
			for(l=0 ; l<this.n_category[i] ; l++){
				urbanEntropyByIndustry[l] = 0.0;
				for(k=0 ; k<this.n_ageGroup ; k++){
					tmpProb = urbanProbabilityByIndustry[l][k];
					if(tmpProb > 0) subEntropy = -1.0 * tmpProb * Math.log(tmpProb)/base;
					else if(tmpProb == 0) subEntropy = 0;
					else{
						subEntropy = 0;
						System.err.println("entropy calculation error");
					}					
					urbanEntropyByIndustry[l] += subEntropy;
				}
			}
			this.urbanEntropyByAge.add(urbanEntropy);
			this.urbanEntropyByAgeByCategory.add(urbanEntropyByCategory);
			this.urbanEntropyByAgeByIndustry.add(urbanEntropyByIndustry);
		}		
	}
	
	public void normalizeEntropy(){
		int i, j;
		double baseLog = Math.log(2);
		double maxEntropy, maxSizeEntropy, maxLevelEntropy, maxAgeEntropy;
		
		for(i=0 ; i<this.duration ; i++){
			maxEntropy = Math.log(this.n_industry[i])/baseLog;
			maxSizeEntropy = Math.log(this.n_sizeGroup)/baseLog;
			maxLevelEntropy = Math.log(this.n_levelGroup)/baseLog;
			maxAgeEntropy = Math.log(this.n_ageGroup)/baseLog;
			
			this.entropyNation[i]	/= maxEntropy;
			this.entropyRural[i]		/= maxEntropy;
			this.entropyUrban[i]	/= maxEntropy;
			
			this.entropyNationBySize[i]	/= maxSizeEntropy;
			this.entropyRuralBySize[i]		/= maxSizeEntropy;
			this.entropyUrbanBySize[i]		/= maxSizeEntropy;
			
			this.entropyNationByLevel[i]	/= maxLevelEntropy;
			this.entropyRuralByLevel[i]		/= maxLevelEntropy;
			this.entropyUrbanByLevel[i]	/= maxLevelEntropy;
			
			this.entropyNationByAge[i]		/= maxAgeEntropy;
			this.entropyRuralByAge[i]		/= maxAgeEntropy;
			this.entropyUrbanByAge[i]		/= maxAgeEntropy;
			
			for(j=0 ; j<this.n_region[i]; j++){
				this.entropy.get(i)[j] /= maxEntropy;
				this.ruralEntropy.get(i)[j] /= maxEntropy;
				this.urbanEntropy.get(i)[j] /= maxEntropy;
				
				this.entropyBySize.get(i)[j] /= maxSizeEntropy;
				this.ruralEntropyBySize.get(i)[j] /= maxSizeEntropy;
				this.urbanEntropyBySize.get(i)[j] /= maxSizeEntropy;
				
				this.entropyByLevel.get(i)[j] /= maxLevelEntropy;
				this.ruralEntropyByLevel.get(i)[j] /= maxLevelEntropy;
				this.urbanEntropyByLevel.get(i)[j] /= maxLevelEntropy;
				
				this.entropyByAge.get(i)[j] /= maxAgeEntropy;
				this.ruralEntropyByAge.get(i)[j] /= maxAgeEntropy;
				this.urbanEntropyByAge.get(i)[j] /= maxAgeEntropy;
			}
		}
	}
	
	public void normalizeCategoryEntropy(){
		int i, j, k;
		double baseLog = Math.log(2);
		double maxEntropy, maxSizeEntropy, maxLevelEntropy, maxAgeEntropy;
		
		for(i=0 ; i<this.duration ; i++){
			for(k=0 ; k<this.n_category[i] ; k++){
				maxEntropy = Math.log(this.industryListByCategory.get(i).get(k).size())/baseLog;
				maxSizeEntropy = Math.log(this.n_sizeGroup)/baseLog;
				maxLevelEntropy = Math.log(this.n_levelGroup)/baseLog;
				maxAgeEntropy = Math.log(this.n_ageGroup)/baseLog;
				
				this.entropyByIndustry.get(i)[k] /= maxEntropy;
				this.ruralEntropyByIndustry.get(i)[k] /= maxEntropy;
				this.urbanEntropyByIndustry.get(i)[k] /= maxEntropy;
				
				this.entropyBySizeByIndustry.get(i)[k] /= maxSizeEntropy;
				this.ruralEntropyBySizeByIndustry.get(i)[k] /= maxSizeEntropy;
				this.urbanEntropyBySizeByIndustry.get(i)[k] /= maxSizeEntropy;
				
				this.entropyByLevelByIndustry.get(i)[k] /= maxLevelEntropy;
				this.ruralEntropyByLevelByIndustry.get(i)[k] /= maxLevelEntropy;
				this.urbanEntropyByLevelByIndustry.get(i)[k] /= maxLevelEntropy;
				
				this.entropyByAgeByIndustry.get(i)[k] /= maxAgeEntropy;
				this.ruralEntropyByAgeByIndustry.get(i)[k] /= maxAgeEntropy;
				this.urbanEntropyByAgeByIndustry.get(i)[k] /= maxAgeEntropy;
				
				for(j=0 ; j<this.n_region[i]; j++){
					this.entropyByCategory.get(i)[j][k] /= maxEntropy;
					this.ruralEntropyByCategory.get(i)[j][k] /= maxEntropy;
					this.urbanEntropyByCategory.get(i)[j][k] /= maxEntropy;

					this.entropyBySizeByCategory.get(i)[j][k] /= maxSizeEntropy;
					this.ruralEntropyBySizeByCategory.get(i)[j][k] /= maxSizeEntropy;
					this.urbanEntropyBySizeByCategory.get(i)[j][k] /= maxSizeEntropy;
					
					this.entropyByLevelByCategory.get(i)[j][k] /= maxLevelEntropy;
					this.ruralEntropyByLevelByCategory.get(i)[j][k] /= maxLevelEntropy;
					this.urbanEntropyByLevelByCategory.get(i)[j][k] /= maxLevelEntropy;
					
					this.entropyByAgeByCategory.get(i)[j][k] /= maxAgeEntropy;
					this.ruralEntropyByAgeByCategory.get(i)[j][k] /= maxAgeEntropy;
					this.urbanEntropyByAgeByCategory.get(i)[j][k] /= maxAgeEntropy;
				}
			}
		}
	}
	
	public void printEmployeeEntropy(String outputFile){
		int i,j,k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("range");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			pw.print("N_category:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_category[i]);
			pw.println();
			pw.print("N_industry:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_industry[i]);
			pw.println();
			
			/*** print entropy ***/
			pw.println("regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++){	
				pw.print(this.locatoinName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.entropy.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.entropyByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(0).get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.get(0).size() ; j++){	
					pw.print(this.locatoinName.get(0).get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.entropyByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			
			pw.println();
			pw.println();
			
			pw.println("rural regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++){	
				pw.print(this.locatoinName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.ruralEntropy.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("rural industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.ruralEntropyByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("rural regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(0).get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.get(0).size() ; j++){	
					pw.print(this.locatoinName.get(0).get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.ruralEntropyByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			
			pw.println();
			pw.println();
			
			pw.println("urban regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++){	
				pw.print(this.locatoinName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.urbanEntropy.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("urban industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.urbanEntropyByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("urban regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(0).get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.get(0).size() ; j++){	
					pw.print(this.locatoinName.get(0).get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.urbanEntropyByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			
			/*** print employee ***/
			pw.println("regional employees");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++){	
				pw.print(this.locatoinName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.employeeSum.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("industry employees");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.employeeSumByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("regional industry employees");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(0).get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.get(0).size() ; j++){	
					pw.print(this.locatoinName.get(0).get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.employeeSumByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			
			pw.println();
			pw.println();
			
			pw.println("rural regional employees");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++){	
				pw.print(this.locatoinName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.ruralEmployeeSum.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("rural industry employees");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.ruralEmployeeSumByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("rural regional industry employees");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(0).get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.get(0).size() ; j++){	
					pw.print(this.locatoinName.get(0).get(j));		
					for(k=0 ; k<this.n_category[i] ; k++) pw.print("\t"+this.ruralEmployeeSumByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			
			pw.println();
			pw.println();
			
			pw.println("urban regional employees");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++){	
				pw.print(this.locatoinName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.urbanEmployeeSum.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("urban industry employees");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.urbanEmployeeSumByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("urban regional industry employees");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(0).get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.get(0).size() ; j++){	
					pw.print(this.locatoinName.get(0).get(j));		
					for(k=0 ; k<this.n_category[i] ; k++) pw.print("\t"+this.urbanEmployeeSumByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printEmployeeSizeEntropy(String outputFile){
		
		int i,j,k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);

			pw.print("range");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			pw.print("N_groups:"+this.n_sizeGroup);
			pw.print("Max_employees:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.maxSize[i]);
			pw.println();
			pw.print("Total_employees:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.total[i]);
			pw.println();
			pw.print("Total_companies:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.count[i]);
			pw.println();
			pw.println();
			/*** print entropy ***/
			pw.println("regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++){	
				pw.print(this.locatoinName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.entropyBySize.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.entropyBySizeByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(0).get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.get(0).size() ; j++){	
					pw.print(this.locatoinName.get(0).get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.entropyBySizeByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			pw.println();
			pw.println();
			
			pw.println("rural regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++){	
				pw.print(this.locatoinName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.ruralEntropyBySize.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("rural industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.ruralEntropyBySizeByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("rural regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(0).get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.get(0).size() ; j++){	
					pw.print(this.locatoinName.get(0).get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.ruralEntropyBySizeByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			pw.println();
			pw.println();
			
			pw.println("urban regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++){	
				pw.print(this.locatoinName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.urbanEntropyBySize.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("urban industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.urbanEntropyBySizeByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("urban regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(0).get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.get(0).size() ; j++){	
					pw.print(this.locatoinName.get(0).get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.urbanEntropyBySizeByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printEmployeeLevelEntropy(String outputFile){
		
		int i,j,k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);

			pw.print("range");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			pw.println("N_groups:"+this.n_levelGroup);
			pw.print("Max_profit_per_employee:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.maxLevelProfit[i]);
			pw.println();
			pw.print("Min_profit_per_employee:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.minLevelProfit[i]);
			pw.println();
			pw.println();
			/*** print entropy ***/
			pw.println("regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++){	
				pw.print(this.locatoinName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.entropyByLevel.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(0).get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.entropyByLevelByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(0).get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.get(0).size() ; j++){	
					pw.print(this.locatoinName.get(0).get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.entropyByLevelByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			pw.println();
			pw.println();
			
			pw.println("rural regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.size() ; i++){	
				pw.print(this.locatoinName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.ruralEntropyByLevel.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("rural industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.ruralEntropyByLevelByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("rural regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++){	
					pw.print(this.locatoinName.get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.ruralEntropyByLevelByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			pw.println();
			pw.println();
			
			pw.println("urban regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.size() ; i++){	
				pw.print(this.locatoinName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.urbanEntropyByLevel.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("urban industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.urbanEntropyByLevelByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("urban regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++){	
					pw.print(this.locatoinName.get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.urbanEntropyByLevelByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printEmployeeAgeEntropy(String outputFile){
		
		int i, j, k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);

			pw.print("range");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			pw.println("N_gouprs:"+this.n_ageGroup);
			pw.print("Max_business_years:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.maxAge[i]);
			pw.println();
			pw.print("Total_employees:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.employees[i]);
			pw.println();
			pw.print("Employees_in_established_companies:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.inFounded[i]);
			pw.println();
			pw.print("Decreased_employees:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.decreased[i]);
			pw.println();
			pw.print("Total_companies:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.companies[i]);
			pw.println();
			pw.print("Established_companies:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.founded[i]);
			pw.println();
			pw.print("Closed_companies:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.closed[i]);
			pw.println();
			pw.println();
			/*** print entropy ***/
			pw.println("regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++){	
				pw.print(this.locatoinName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.entropyByAge.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.entropyByAgeByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++){	
					pw.print(this.locatoinName.get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.entropyByAgeByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			pw.println();
			pw.println();
			
			pw.println("rural regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.size() ; i++){	
				pw.print(this.locatoinName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.ruralEntropyByAge.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("rural industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.ruralEntropyByAgeByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("rural regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++){	
					pw.print(this.locatoinName.get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.ruralEntropyByAgeByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			pw.println();
			pw.println();
			
			pw.println("urban regional entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();	
			for(i=0 ; i<this.locatoinName.size() ; i++){	
				pw.print(this.locatoinName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.urbanEntropyByAge.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("urban industry entropy");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();		
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.print(this.categoryName.get(i));			
				for(j=0 ; j<this.duration ; j++) pw.print("\t"+this.urbanEntropyByAgeByIndustry.get(j)[i]);			
				pw.println();
			}
			pw.println();
			
			pw.println("urban regional industry entropy");
			for(i=0 ; i<this.n_category[0] ; i++) pw.print("\t"+this.categoryName.get(i));
			pw.println();		
			for(i=0 ; i<this.duration ; i++){
				pw.println("year: "+(this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++){	
					pw.print(this.locatoinName.get(j));		
					for(k=0 ; k<this.n_category[i] ; k++)	pw.print("\t"+this.urbanEntropyByAgeByCategory.get(i)[j][k]);			
					pw.println();
				}
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printEntropyNationResults(String outputFile){
		int i;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("range");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			pw.print("N_category:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_category[i]);
			pw.println();
			pw.print("N_industry:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_industry[i]);
			pw.println();
			
			/*** print national entropy ***/
			pw.println("National_entropy::");
			pw.println("Year\tNation\tRural_Area\tUrbanArea");
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				pw.print("\t"+this.entropyNation[i]);
				pw.print("\t"+this.entropyRural[i]);
				pw.print("\t"+this.entropyUrban[i]);
				pw.println();	
			}
			pw.println();	
			
			pw.close();
		}catch(IOException e) {}
	}
	
	
	public void printEntropyResults(String outputFile){
		int i,j,k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("range");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			pw.print("N_category:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_category[i]);
			pw.println();
			pw.print("N_industry:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_industry[i]);
			pw.println();
			
			/*** print national entropy ***/
			pw.println("National_entropy::");
			pw.println("Year\tNation\tRural_Area\tUrbanArea");
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				pw.print("\t"+this.entropyNation[i]);
				pw.print("\t"+this.entropyRural[i]);
				pw.print("\t"+this.entropyUrban[i]);
				pw.println();	
			}
			pw.println();	
			
			/*** print regional entropy ***/
			pw.println("Regional_entropy::");
			pw.println("Nation:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++) pw.print("\t"+this.locatoinName.get(i));		
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.entropy.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			pw.println("Rural_area:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.size() ; i++) pw.print("\t"+this.locatoinName.get(i));		
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.ruralEntropy.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			pw.println("Urban_area:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.size() ; i++) pw.print("\t"+this.locatoinName.get(i));	
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.urbanEntropy.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			/*** print industry category entropy ***/
			pw.println("Industry_category_entropy:::");
			pw.println("Nation::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));		
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.entropyByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.println("Rural_area::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));		
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.ruralEntropyByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.println("Urban_area::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));	
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.urbanEntropyByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printSizeEntropyResults(String outputFile){
		int i,j,k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("range");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			pw.print("N_category:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_category[i]);
			pw.println();
			pw.print("N_size_group:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_sizeGroup);
			pw.println();
			
			/*** print national entropy ***/
			pw.println("National_entropy::");
			pw.println("Year\tNation\tRural_Area\tUrbanArea");
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				pw.print("\t"+this.entropyNationBySize[i]);
				pw.print("\t"+this.entropyRuralBySize[i]);
				pw.print("\t"+this.entropyUrbanBySize[i]);
				pw.println();	
			}
			pw.println();	
			
			/*** print regional entropy ***/
			pw.println("Regional_entropy::");
			pw.println("Nation:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++) pw.print("\t"+this.locatoinName.get(i));	
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.entropyBySize.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			pw.println("Rural_area:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.size() ; i++) pw.print("\t"+this.locatoinName.get(i));		
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.ruralEntropyBySize.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			pw.println("Urban_area:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.size() ; i++) pw.print("\t"+this.locatoinName.get(i));		
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.urbanEntropyBySize.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			/*** print industry category entropy ***/
			pw.println("Industry_category_entropy:::");
			pw.println("Nation::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));		
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.entropyBySizeByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.println("Rural_area::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));		
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.ruralEntropyBySizeByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.println("Urban_area::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));	
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.urbanEntropyBySizeByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printLevelEntropyResults(String outputFile){
		int i,j,k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("range");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			pw.print("N_category:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_category[i]);
			pw.println();
			pw.print("N_level_group:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_levelGroup);
			pw.println();
			
			/*** print national entropy ***/
			pw.println("National_entropy::");
			pw.println("Year\tNation\tRural_Area\tUrbanArea");
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				pw.print("\t"+this.entropyNationByLevel[i]);
				pw.print("\t"+this.entropyRuralByLevel[i]);
				pw.print("\t"+this.entropyUrbanByLevel[i]);
				pw.println();	
			}
			pw.println();	
			
			/*** print regional entropy ***/
			pw.println("Regional_entropy::");
			pw.println("Nation:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++) pw.print("\t"+this.locatoinName.get(i));		
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.entropyByLevel.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			pw.println("Rural_area:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.size() ; i++) pw.print("\t"+this.locatoinName.get(i));		
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.ruralEntropyByLevel.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			pw.println("Urban_area:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.size() ; i++) pw.print("\t"+this.locatoinName.get(i));	
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.urbanEntropyByLevel.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			/*** print industry category entropy ***/
			pw.println("Industry_category_entropy:::");
			pw.println("Nation::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));		
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.entropyByLevelByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.println("Rural_area::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));	
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.ruralEntropyByLevelByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.println("Urban_area::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));		
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.urbanEntropyByLevelByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printAgeEntropyResults(String outputFile){
		int i,j,k;
		
		try{
			File file = new File(outputFile);
			PrintWriter pw = new PrintWriter(file);
		
			pw.print("range");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+(this.startYear+i));
			pw.println();
			pw.print("N_category:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_category[i]);
			pw.println();
			pw.print("N_business_years_group:");
			for(i=0 ; i<this.duration ; i++) pw.print("\t"+this.n_ageGroup);
			pw.println();
			
			/*** print national entropy ***/
			pw.println("National_entropy::");
			pw.println("Year\tNation\tRural_Area\tUrbanArea");
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				pw.print("\t"+this.entropyNationByAge[i]);
				pw.print("\t"+this.entropyRuralByAge[i]);
				pw.print("\t"+this.entropyUrbanByAge[i]);
				pw.println();	
			}
			pw.println();	
			
			/*** print regional entropy ***/
			pw.println("Regional_entropy::");
			pw.println("Nation:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.get(0).size() ; i++) pw.print("\t"+this.locatoinName.get(i));	
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.entropyByAge.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			pw.println("Rural_area:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.size() ; i++) pw.print("\t"+this.locatoinName.get(i));		
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.ruralEntropyByAge.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			pw.println("Urban_area:");
			pw.print("Year");
			for(i=0 ; i<this.locatoinName.size() ; i++) pw.print("\t"+this.locatoinName.get(i));		
			pw.println();
			for(i=0 ; i<this.duration ; i++){
				pw.print((this.startYear+i));
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.urbanEntropyByAge.get(i)[j]);	
				pw.println();	
			}
			pw.println();	
			
			/*** print industry category entropy ***/
			pw.println("Industry_category_entropy:::");
			pw.println("Nation::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));		
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.entropyByAgeByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.println("Rural_area::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));	
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.ruralEntropyByAgeByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.println("Urban_area::");
			for(i=0 ; i<this.n_category[0] ; i++){	
				pw.println(this.categoryName.get(i)+":");	
				pw.print("Year");
				for(j=0 ; j<this.locatoinName.size() ; j++) pw.print("\t"+this.locatoinName.get(j));	
				pw.println();
				for(j=0 ; j<this.duration ; j++){
					pw.print((this.startYear+j));
					for(k=0 ; k<this.locatoinName.size() ; k++) pw.print("\t"+this.urbanEntropyByAgeByCategory.get(j)[k][i]);	
					pw.println();		
				}
				pw.println();
			}
			pw.println();
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void clearUnusingMemoryIndustry(){
		this.employeeRatioNation = null;
		this.employeeRatio = null; 
		this.employeeSum = null;	
		this.employeeRatioMean = null;
		this.employeeSumByCategory = null;
		this.employeeRatioByCategory = null;
		this.employeeSumByIndustry = null;	
		this.employeeRatioByIndustry = null;
		
		this.employeeRatioRural = null;
		this.ruralEmployeeRatio = null; 
		this.ruralEmployeeSum = null;
		this.ruralEmployeeRatioMean = null;
		this.ruralEmployeeSumByCategory = null;
		this.ruralEmployeeRatioByCategory = null;
		this.ruralEmployeeSumByIndustry = null;	
		this.ruralEmployeeRatioByIndustry = null;
		
		
		this.employeeRatioUrban = null;
		this.urbanEmployeeRatio = null; 
		this.urbanEmployeeSum = null;
		this.urbanEmployeeRatioMean = null;
		this.urbanEmployeeSumByCategory = null;
		this.urbanEmployeeRatioByCategory = null;
		this.urbanEmployeeSumByIndustry = null;	
		this.urbanEmployeeRatioByIndustry = null;
	}
	
	public void clearUnusingMemorySize(){
		this.employeeRatioNationBySize = null;
		this.employeeSumBySize = null;
		this.employeeRatioBySize = null; 
		this.employeeRatioMeanBySize = null;
		this.employeeSumByCategoryBySize = null;
		this.employeeRatioByCategoryBySize = null; 
		this.employeeSumByIndustryBySize = null;
		this.employeeRatioByIndustryBySize = null; 

		this.employeeRatioRuralBySize = null;
		this.ruralEmployeeSumBySize = null;
		this.ruralEmployeeRatioBySize = null; 
		this.ruralEmployeeRatioMeanBySize = null;
		this.ruralEmployeeSumByCategoryBySize = null;	
		this.ruralEmployeeRatioByCategoryBySize = null; 	
		this.ruralEmployeeSumByIndustryBySize = null;		
		this.ruralEmployeeRatioByIndustryBySize = null; 	

		this.employeeRatioUrbanBySize = null;
		this.urbanEmployeeSumBySize = null;
		this.urbanEmployeeRatioBySize = null; 
		this.urbanEmployeeRatioMeanBySize = null;
		this.urbanEmployeeSumByCategoryBySize = null;	
		this.urbanEmployeeRatioByCategoryBySize = null;	
		this.urbanEmployeeSumByIndustryBySize = null;	
		this.urbanEmployeeRatioByIndustryBySize = null; 	
	}
	
	public void clearUnusingMemoryLevel(){
		this.employeeRatioNationByLevel = null;
		this.employeeSumByLevel = null;
		this.employeeRatioByLevel = null; 
		this.employeeRatioMeanByLevel = null;
		this.employeeSumByCategoryByLevel = null;
		this.employeeRatioByCategoryByLevel = null;		
		this.employeeSumByIndustryByLevel = null;
		this.employeeRatioByIndustryByLevel = null; 

		this.employeeRatioRuralByLevel = null;
		this.ruralEmployeeSumByLevel = null;
		this.ruralEmployeeRatioByLevel = null; 
		this.ruralEmployeeRatioMeanByLevel = null;
		this.ruralEmployeeSumByCategoryByLevel = null;	
		this.ruralEmployeeRatioByCategoryByLevel = null; 
		this.ruralEmployeeSumByIndustryByLevel = null;	
		this.ruralEmployeeRatioByIndustryByLevel = null; 	

		this.employeeRatioUrbanByLevel = null;
		this.urbanEmployeeSumByLevel = null;
		this.urbanEmployeeRatioByLevel = null; 
		this.urbanEmployeeRatioMeanByLevel = null;
		this.urbanEmployeeSumByCategoryByLevel = null;
		this.urbanEmployeeRatioByCategoryByLevel = null;
		this.urbanEmployeeSumByIndustryByLevel = null;	
		this.urbanEmployeeRatioByIndustryByLevel = null;
	}
	
	public void clearUnusingMemoryAge(){
		this.employeeRatioNationByAge = null;
		this.employeeSumByAge = null;
		this.employeeRatioByAge = null; 
		this.employeeRatioMeanByAge = null;
		this.employeeSumByCategoryByAge = null;	
		this.employeeRatioByCategoryByAge = null;		 	
		this.employeeSumByIndustryByAge = null;
		this.employeeRatioByIndustryByAge = null; 

		this.employeeRatioRuralByAge = null;
		this.ruralEmployeeSumByAge = null;
		this.ruralEmployeeRatioByAge = null; 
		this.ruralEmployeeRatioMeanByAge = null;
		this.ruralEmployeeSumByCategoryByAge = null;	
		this.ruralEmployeeRatioByCategoryByAge = null; 	
		this.ruralEmployeeSumByIndustryByAge = null;		
		this.ruralEmployeeRatioByIndustryByAge = null; 	

		this.employeeRatioUrbanByAge = null;
		this.urbanEmployeeSumByAge = null;
		this.urbanEmployeeRatioByAge = null; 
		this.urbanEmployeeRatioMeanByAge = null;
		this.urbanEmployeeSumByCategoryByAge = null;	
		this.urbanEmployeeRatioByCategoryByAge = null;	
		this.urbanEmployeeSumByIndustryByAge = null;	
		this.urbanEmployeeRatioByIndustryByAge = null; 	
	}
	
	public void clearEmployeeMemoryIndustry(){
		this.employeeNation = null;
		this.employee = null; 		
		this.employeeByCategory = null;
		this.employeeByIndustry = null;
		
		this.employeeRural = null;
		this.ruralEmployee = null; 
		this.ruralEmployeeByCategory = null;
		this.ruralEmployeeByIndustry = null;
		
		
		this.employeeUrban = null;
		this.urbanEmployee = null; 
		this.urbanEmployeeByCategory = null;
		this.urbanEmployeeByIndustry = null;
	}
	
	public void clearEmployeeMemorySize(){
		this.employeeNationBySize = null;
		this.employeeBySize = null; 
		this.employeeByCategoryBySize = null; 
		this.employeeByIndustryBySize = null; 

		this.employeeRuralBySize = null;
		this.ruralEmployeeBySize = null; 
		this.ruralEmployeeByCategoryBySize = null; 
		this.ruralEmployeeByIndustryBySize = null; 

		this.employeeUrbanBySize = null;
		this.urbanEmployeeBySize = null; 
		this.urbanEmployeeByCategoryBySize = null; 		
		this.urbanEmployeeByIndustryBySize = null; 
	}
	
	public void clearEmployeeMemoryLevel(){
		this.employeeNationByLevel = null;
		this.employeeByLevel = null; 
		this.employeeByCategoryByLevel = null; 
		this.employeeByIndustryByLevel = null; 

		this.employeeRuralByLevel = null;
		this.ruralEmployeeByLevel = null; 
		this.ruralEmployeeByCategoryByLevel = null; 		
		this.ruralEmployeeByIndustryByLevel = null; 

		this.employeeUrbanByLevel = null;
		this.urbanEmployeeByLevel = null; 
		this.urbanEmployeeByCategoryByLevel = null; 		
		this.urbanEmployeeByIndustryByLevel = null; 		
	}
	
	public void clearEmployeeMemoryAge(){
		this.employeeNationByAge = null;
		this.employeeByAge = null; 
		this.employeeByCategoryByAge = null; 
		this.employeeByIndustryByAge = null;
		
		this.employeeRuralByAge = null;
		this.ruralEmployeeByAge = null; 
		this.ruralEmployeeByCategoryByAge = null; 
		this.ruralEmployeeByIndustryByAge = null; 

		this.employeeUrbanByAge = null;
		this.urbanEmployeeByAge = null; 
		this.urbanEmployeeByCategoryByAge = null; 
		this.urbanEmployeeByIndustryByAge = null; 
	}
	
	public static void main(String[] args) {
	
		int start =1998;
		int end = 2012;
		
		int categoryClass	= 0;		//0:대분류, 1: 중분류,  2: 소분류,   3: 세분류,	4:세세분
		int industryClass		= 3;		//0:대분류, 1: 중분류,  2: 소분류,   3: 세분류,	4:세세분
		int regionClass		= 0;	   	 //0: 시도,    1: 시군구,   2: 읍면동
		int sizeGroups			= 20;
		int levelGroups		= 20;
		int ageGroups			= 20;
		
		String[] industClassName = {"1st","2nd","3rd","4th","5th"};
		String[] regionClassName = {"do","gun","myun"};
		
		String filePath = "/Users/Jemyung/Desktop/Research/data_storage/company/";
		String profitFile = filePath + "profit/industry_profit.txt";
		
		String employeeEntropyFile = filePath+"entropy/employeeIndustryEntropy_"
																+start+"-"+end+"_"
																+industClassName[categoryClass]+"_"
																+industClassName[industryClass]+"_"
																+regionClassName[regionClass]+".txt";
		String employeeSizeEntropyFile = filePath+"entropy/employeeSizeEntropy_"
																+start+"-"+end+"_"
																+industClassName[categoryClass]+"_"
																+sizeGroups+"_"
																+regionClassName[regionClass]+".txt";
		String employeeLevelEntropyFile = filePath+"entropy/employeeLevelEntropy_"
																+start+"-"+end+"_"
																+industClassName[categoryClass]+"_"
																+levelGroups+"_"
																+regionClassName[regionClass]+".txt";
		String employeeAgeEntropyFile = filePath+"entropy/employeeBusinessYearsEntropy_"
																+start+"-"+end+"_"
																+industClassName[categoryClass]+"_"
																+ageGroups+"_"
																+regionClassName[regionClass]+".txt";		
		
		String entropyResultsFile = filePath+"entropy/results/industryEntropy_"
																+start+"-"+end+"_"
																+industClassName[categoryClass]+"_"
																+industClassName[industryClass]+"_"
																+regionClassName[regionClass]+".txt";
		String sizeEntropyResultsFile = filePath+"entropy/results/sizeEntropy_"
																+start+"-"+end+"_"
																+industClassName[categoryClass]+"_"
																+sizeGroups+"_"
																+regionClassName[regionClass]+".txt";
		String levelEntropyResultsFile = filePath+"entropy/results/levelEntropy_"
																+start+"-"+end+"_"
																+industClassName[categoryClass]+"_"
																+levelGroups+"_"
																+regionClassName[regionClass]+".txt";
		String ageEntropyResultsFile = filePath+"entropy/results/businessYearsEntropy_"
																+start+"-"+end+"_"
																+industClassName[categoryClass]+"_"
																+ageGroups+"_"
																+regionClassName[regionClass]+".txt";	
		
		IndustryEntropyCalculator iec;
		iec = new IndustryEntropyCalculator(start, end, categoryClass, industryClass, regionClass);
		//iec = new IndustryEntropyCalculator(start, end, categoryClass, industryClass, regionClass, sizeGroups, levelGroups, ageGroups);
		
		System.out.print("codes reading: ");
		iec.readStandardCodes(filePath);
		System.out.println("complete");	
		
		System.out.print("employee reading: ");
		iec.readEmployee(filePath+"extracted/", "_microdataCode.txt");
		//iec.readEmployeeBySize(sizeGroups, filePath+"extracted/", "_microdataCode.txt");
		//iec.readEmployeeByLevel(levelGroups, profitFile, filePath+"extracted/", "_microdataCode.txt");
		//iec.readEmployeeByAge(ageGroups, filePath+"extracted/", "_microdataCode.txt");
		System.out.println("complete");	
		
		System.out.print("statistics calculating: ");		
		iec.calculateEmployeeStatistics();
		//iec.calculateEmployeeSizeStatistics();
		//iec.calculateEmployeeLevelStatistics();
		//iec.calculateEmployeeAgeStatistics();
		System.out.println("complete");	
						
		System.out.print("entropy calculating: ");
		iec.calculateEmployeeEntropy();
		//iec.calculateEmployeeSizeEntropy();
		//iec.calculateEmployeeLevelEntropy();
		//iec.calculateEmployeeAgeEntropy();
		System.out.println("complete");	
		
		System.out.print("entropy printing: ");
		iec.printEmployeeEntropy(employeeEntropyFile);
		//iec.printEmployeeSizeEntropy(employeeSizeEntropyFile);
		//iec.printEmployeeLevelEntropy(employeeLevelEntropyFile);
		//iec.printEmployeeAgeEntropy(employeeAgeEntropyFile);
		iec.printEntropyNationResults(entropyResultsFile);
		//iec.printEntropyResults(entropyResultsFile);
		//iec.printSizeEntropyResults(sizeEntropyResultsFile);
		//iec.printLevelEntropyResults(levelEntropyResultsFile);
		//iec.printAgeEntropyResults(ageEntropyResultsFile);
		System.out.println("complete");	
				
		System.out.println("process complete.");
	}

}