package industrialDiversity.data;

import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeData {

	int startYear, endYear;
	int duration;
	int n_region;
	int n_industry;
	
	int industryClassDepth;		//2:2nd_jung, 3: 3rd_so order depth
	int regionClassDepth;		//2:si_do,    5: si_gun_gu,   7: eup_myun_dong
	
	HashMap<Integer, String> locationHashMap;		//<code, name>
	HashMap<Integer, String> industryHashMap;	//<code, name>
	ArrayList<Integer> locatoinCode;				//use to find region's index	
	ArrayList<Integer> industryCode;				//use to find industry's index	
	ArrayList<String> locatoinName;				//use to find region's index	
	ArrayList<String> industryName;				//use to find industry's index

	double populationMax;						
	double populationMin;						
	double[][] population;							//[region][year]
	double[][] populationNormalized;			//[region][year]

	double[][] employeeSum;						//[region][year]
	double[][][] employee;							//[region][industry][year]
	double[][][] employeeRatio;					//[region][industry][year]
	double[][] employeeRatioMean;				//[region][industry]
	double[][][] employeeSumByIndustry;		//[region][category][year]
	double[][][][] employeeByIndustry;			//[region][category][industry][year]
	double[][][][] employeeRatioByIndustry;	//[region][category][industry][year]
	
	double[][] entropy;						//[region][year]
	double[][] migration;					//[region][year]	
	double[][][] entropyByIndustry;	//[region][category][year]
	
	double[][] correlation;			//[region][industry]
	double[] correlationMean;		//[industry]
	double[] correlationStd;			//[industry]
	
	double[][] correlationIndustry;	//[industry][industry]
	double[][] groupEmployee;			//[region][year]
	double[] groupCorrelation;			//[region]
	double[] entropyCorrelation;		//[region]
	
	
	public static void main(String[] args) {

	}

}
