package regionalAddedValue.data;

import java.util.ArrayList;
import java.util.HashMap;

public class CompanyData {
	int numberOfYears;
	int locationCodeDepth;
	int industryCodeDepth;
	
	ArrayList<Integer> yearList;											//<year>
	ArrayList<Integer> numberOfCompany;
	ArrayList<Integer> numberOfRegion;
	ArrayList<Integer> numberOfIndustry;
	ArrayList<Integer> numberOfDuration;
	ArrayList<Integer> numberOfSize;
	ArrayList<Integer> maxEmployees, totalEmployees;
	ArrayList<Double> averageEmployees;
	
	ArrayList<int[]> totalCompany, totalEmployee;								//<year>[region]
	ArrayList<int[]> startupCompany, startupEmployee;										
	ArrayList<int[]> totalRuralCompany, totalRuralEmployee;									
	ArrayList<int[]> startupRuralCompany, startupRuralEmployee;						
	ArrayList<int[]> totalUrbanCompany, totalUrbanEmployee;								
	ArrayList<int[]> startupUrbanCompany, startupUrbanEmployee;		
	
	ArrayList<int[][]> totalCompanyByIndustry, totalEmployeeByIndustry;			//<year>[region][industry]
	ArrayList<int[][]> startupCompanyByIndustry, startupEmployeeByIndustry;					
	ArrayList<int[][]> totalRuralCompanyByIndustry, totalRuralEmployeeByIndustry;		
	ArrayList<int[][]> startupRuralCompanyByIndustry, startupRuralEmployeeByIndustry;
	ArrayList<int[][]> totalUrbanCompanyByIndustry, totalUrbanEmployeeByIndustry;		
	ArrayList<int[][]> startupUrbanCompanyByIndustry, startupUrbanEmployeeByIndustry;
	
	ArrayList<int[][]> totalCompanyByDuration, totalEmployeeByDuration;			//<year>[region][industry]
	ArrayList<int[][]> startupCompanyByDuration, startupEmployeeByDuration;					
	ArrayList<int[][]> totalRuralCompanyByDuration, totalRuralEmployeeByDuration;		
	ArrayList<int[][]> startupRuralCompanyByDuration, startupRuralEmployeeByDuration;
	ArrayList<int[][]> totalUrbanCompanyByDuration, totalUrbanEmployeeByDuration;		
	ArrayList<int[][]> startupUrbanCompanyByDuration, startupUrbanEmployeeByDuration;
	
	ArrayList<int[][]> totalCompanyBySize, totalEmployeeBySize;			//<year>[region][industry]
	ArrayList<int[][]> startupCompanyBySize, startupEmployeeBySize;					
	ArrayList<int[][]> totalRuralCompanyBySize, totalRuralEmployeeBySize;		
	ArrayList<int[][]> startupRuralCompanyBySize, startupRuralEmployeeBySize;
	ArrayList<int[][]> totalUrbanCompanyBySize, totalUrbanEmployeeBySize;		
	ArrayList<int[][]> startupUrbanCompanyBySize, startupUrbanEmployeeBySize;
	
	ArrayList<Integer> sumOfCompany, sumOfEmployee;								//<year>
	ArrayList<Integer> sumOfStartupCompany, sumOfStartupEmployee;										
	ArrayList<Integer> sumOfRuralCompany, sumOfRuralEmployee;									
	ArrayList<Integer> sumOfStartupRuralCompany, sumOfStartupRuralEmployee;						
	ArrayList<Integer> sumOfUrbanCompany, sumOfUrbanEmployee;								
	ArrayList<Integer> sumOfStartupUrbanCompany, sumOfStartupUrbanEmployee;		
	
	ArrayList<int[]> sumOfCompanyByIndustry, sumOfEmployeeByIndustry;			//<year>[industry]
	ArrayList<int[]> sumOfStartupCompanyByIndustry, sumOfStartupEmployeeByIndustry;					
	ArrayList<int[]> sumOfRuralCompanyByIndustry, sumOfRuralEmployeeByIndustry;		
	ArrayList<int[]> sumOfStartupRuralCompanyByIndustry, sumOfStartupRuralEmployeeByIndustry;
	ArrayList<int[]> sumOfUrbanCompanyByIndustry, sumOfUrbanEmployeeByIndustry;		
	ArrayList<int[]> sumOfStartupUrbanCompanyByIndustry, sumOfStartupUrbanEmployeeByIndustry;
	
	ArrayList<int[]> sumOfCompanyByDuration, sumOfEmployeeByDuration;			//<year>[industry]
	ArrayList<int[]> sumOfStartupCompanyByDuration, sumOfStartupEmployeeByDuration;					
	ArrayList<int[]> sumOfRuralCompanyByDuration, sumOfRuralEmployeeByDuration;		
	ArrayList<int[]> sumOfStartupRuralCompanyByDuration, sumOfStartupRuralEmployeeByDuration;
	ArrayList<int[]> sumOfUrbanCompanyByDuration, sumOfUrbanEmployeeByDuration;		
	ArrayList<int[]> sumOfStartupUrbanCompanyByDuration, sumOfStartupUrbanEmployeeByDuration;
	
	ArrayList<int[]> sumOfCompanyBySize, sumOfEmployeeBySize;			//<year>[industry]
	ArrayList<int[]> sumOfStartupCompanyBySize, sumOfStartupEmployeeBySize;					
	ArrayList<int[]> sumOfRuralCompanyBySize, sumOfRuralEmployeeBySize;		
	ArrayList<int[]> sumOfStartupRuralCompanyBySize, sumOfStartupRuralEmployeeBySize;
	ArrayList<int[]> sumOfUrbanCompanyBySize, sumOfUrbanEmployeeBySize;		
	ArrayList<int[]> sumOfStartupUrbanCompanyBySize, sumOfStartupUrbanEmployeeBySize;
	
	ArrayList<ArrayList<String>> locationCodeList;
	ArrayList<ArrayList<String>> industryCodeList;
	
	ArrayList<HashMap<String, String>> locationCodeMap;			//<code, region name>
	ArrayList<HashMap<String, String>> industryCodeMap;			//<code, industrial category>
	
	public CompanyData(){
		
	}
	
	public CompanyData(int[] years, int locationDepth, int industryDepth){
		this.initiate(years, locationDepth, industryDepth);
	}
	
	public void initiate(int[] years, int locationDepth, int industryDepth){
		this.numberOfYears = years.length;
		this.locationCodeDepth = locationDepth;
		this.industryCodeDepth = industryDepth;
		this.yearList = new ArrayList<Integer>();
		for(int i=0 ; i<years.length ; i++) this.yearList.add(years[i]);
		
		this.initiate();
		this.initateDetailedVariables();
	}
	
	public void initiate(){
		this.locationCodeList = new ArrayList<ArrayList<String>>();
		this.industryCodeList = new ArrayList<ArrayList<String>>();
		
		this.locationCodeMap = new ArrayList<HashMap<String, String>>();
		this.industryCodeMap = new ArrayList<HashMap<String, String>>();	
		
		this.numberOfCompany = new ArrayList<Integer>();
		this.numberOfRegion = new ArrayList<Integer>();
		this.numberOfIndustry = new ArrayList<Integer>();
		this.numberOfDuration = new ArrayList<Integer>();
		this.numberOfSize = new ArrayList<Integer>();
		this.maxEmployees = new ArrayList<Integer>();
		this.totalEmployees = new ArrayList<Integer>();
		this.averageEmployees = new ArrayList<Double>();

		this.totalCompany = new ArrayList<int[]>();
		this.totalEmployee = new ArrayList<int[]>();
		this.startupCompany = new ArrayList<int[]>();
		this.startupEmployee = new ArrayList<int[]>();
		this.totalRuralCompany = new ArrayList<int[]>();
		this.totalRuralEmployee = new ArrayList<int[]>();
		this.startupRuralCompany = new ArrayList<int[]>();
		this.startupRuralEmployee = new ArrayList<int[]>();
		this.totalUrbanCompany = new ArrayList<int[]>();
		this.totalUrbanEmployee = new ArrayList<int[]>();
		this.startupUrbanCompany = new ArrayList<int[]>();
		this.startupUrbanEmployee = new ArrayList<int[]>();
		
		this.sumOfCompany = new ArrayList<Integer>();
		this.sumOfEmployee = new ArrayList<Integer>();
		this.sumOfStartupCompany = new ArrayList<Integer>();
		this.sumOfStartupEmployee = new ArrayList<Integer>();
		this.sumOfRuralCompany = new ArrayList<Integer>();
		this.sumOfRuralEmployee = new ArrayList<Integer>();
		this.sumOfStartupRuralCompany = new ArrayList<Integer>();
		this.sumOfStartupRuralEmployee = new ArrayList<Integer>();
		this.sumOfUrbanCompany = new ArrayList<Integer>();
		this.sumOfUrbanEmployee = new ArrayList<Integer>();
		this.sumOfStartupUrbanCompany = new ArrayList<Integer>();
		this.sumOfStartupUrbanEmployee = new ArrayList<Integer>();
	}
	
	public void initateDetailedVariables(){
		this.totalCompanyByIndustry = new ArrayList<int[][]>();
		this.totalEmployeeByIndustry = new ArrayList<int[][]>();
		this.startupCompanyByIndustry = new ArrayList<int[][]>();
		this.startupEmployeeByIndustry = new ArrayList<int[][]>();
		this.totalRuralCompanyByIndustry = new ArrayList<int[][]>();
		this.totalRuralEmployeeByIndustry = new ArrayList<int[][]>();
		this.startupRuralCompanyByIndustry = new ArrayList<int[][]>();
		this.startupRuralEmployeeByIndustry = new ArrayList<int[][]>();
		this.totalUrbanCompanyByIndustry = new ArrayList<int[][]>();
		this.totalUrbanEmployeeByIndustry = new ArrayList<int[][]>();
		this.startupUrbanCompanyByIndustry = new ArrayList<int[][]>();
		this.startupUrbanEmployeeByIndustry = new ArrayList<int[][]>();
		
		this.totalCompanyByDuration = new ArrayList<int[][]>();
		this.totalEmployeeByDuration = new ArrayList<int[][]>();
		this.startupCompanyByDuration = new ArrayList<int[][]>();
		this.startupEmployeeByDuration = new ArrayList<int[][]>();
		this.totalRuralCompanyByDuration = new ArrayList<int[][]>();
		this.totalRuralEmployeeByDuration = new ArrayList<int[][]>();
		this.startupRuralCompanyByDuration = new ArrayList<int[][]>();
		this.startupRuralEmployeeByDuration = new ArrayList<int[][]>();
		this.totalUrbanCompanyByDuration = new ArrayList<int[][]>();
		this.totalUrbanEmployeeByDuration = new ArrayList<int[][]>();
		this.startupUrbanCompanyByDuration = new ArrayList<int[][]>();
		this.startupUrbanEmployeeByDuration = new ArrayList<int[][]>();
		
		this.totalCompanyBySize = new ArrayList<int[][]>();
		this.totalEmployeeBySize = new ArrayList<int[][]>();
		this.startupCompanyBySize = new ArrayList<int[][]>();
		this.startupEmployeeBySize = new ArrayList<int[][]>();
		this.totalRuralCompanyBySize = new ArrayList<int[][]>();
		this.totalRuralEmployeeBySize = new ArrayList<int[][]>();
		this.startupRuralCompanyBySize = new ArrayList<int[][]>();
		this.startupRuralEmployeeBySize = new ArrayList<int[][]>();
		this.totalUrbanCompanyBySize = new ArrayList<int[][]>();
		this.totalUrbanEmployeeBySize = new ArrayList<int[][]>();
		this.startupUrbanCompanyBySize = new ArrayList<int[][]>();
		this.startupUrbanEmployeeBySize = new ArrayList<int[][]>();
		
		this.sumOfCompanyByIndustry = new ArrayList<int[]>();
		this.sumOfEmployeeByIndustry = new ArrayList<int[]>();
		this.sumOfStartupCompanyByIndustry = new ArrayList<int[]>();
		this.sumOfStartupEmployeeByIndustry = new ArrayList<int[]>();
		this.sumOfRuralCompanyByIndustry = new ArrayList<int[]>();
		this.sumOfRuralEmployeeByIndustry = new ArrayList<int[]>();
		this.sumOfStartupRuralCompanyByIndustry = new ArrayList<int[]>();
		this.sumOfStartupRuralEmployeeByIndustry = new ArrayList<int[]>();
		this.sumOfUrbanCompanyByIndustry = new ArrayList<int[]>();
		this.sumOfUrbanEmployeeByIndustry = new ArrayList<int[]>();
		this.sumOfStartupUrbanCompanyByIndustry = new ArrayList<int[]>();
		this.sumOfStartupUrbanEmployeeByIndustry = new ArrayList<int[]>();
		
		this.sumOfCompanyByDuration = new ArrayList<int[]>();
		this.sumOfEmployeeByDuration = new ArrayList<int[]>();
		this.sumOfStartupCompanyByDuration = new ArrayList<int[]>();
		this.sumOfStartupEmployeeByDuration = new ArrayList<int[]>();
		this.sumOfRuralCompanyByDuration = new ArrayList<int[]>();
		this.sumOfRuralEmployeeByDuration = new ArrayList<int[]>();
		this.sumOfStartupRuralCompanyByDuration = new ArrayList<int[]>();
		this.sumOfStartupRuralEmployeeByDuration = new ArrayList<int[]>();
		this.sumOfUrbanCompanyByDuration = new ArrayList<int[]>();
		this.sumOfUrbanEmployeeByDuration = new ArrayList<int[]>();
		this.sumOfStartupUrbanCompanyByDuration = new ArrayList<int[]>();
		this.sumOfStartupUrbanEmployeeByDuration = new ArrayList<int[]>();
		
		this.sumOfCompanyBySize = new ArrayList<int[]>();
		this.sumOfEmployeeBySize = new ArrayList<int[]>();
		this.sumOfStartupCompanyBySize = new ArrayList<int[]>();
		this.sumOfStartupEmployeeBySize = new ArrayList<int[]>();
		this.sumOfRuralCompanyBySize = new ArrayList<int[]>();
		this.sumOfRuralEmployeeBySize = new ArrayList<int[]>();
		this.sumOfStartupRuralCompanyBySize = new ArrayList<int[]>();
		this.sumOfStartupRuralEmployeeBySize = new ArrayList<int[]>();
		this.sumOfUrbanCompanyBySize = new ArrayList<int[]>();
		this.sumOfUrbanEmployeeBySize = new ArrayList<int[]>();
		this.sumOfStartupUrbanCompanyBySize = new ArrayList<int[]>();
		this.sumOfStartupUrbanEmployeeBySize = new ArrayList<int[]>();
	}
	
	public void addYearList(int year){
		this.yearList.add(year);
	}

	public void addNumberOfCompany(int number){
		this.numberOfCompany.add(number);
	}

	public void addNumberOfRegion(int number){
		this.numberOfRegion.add(number);
	}

	public void addNumberOfIndustry(int number){
		this.numberOfIndustry.add(number);
	}

	public void addNumberOfDuration(int number){
		this.numberOfDuration.add(number);
	}

	public void addNumberOfSize(int number){
		this.numberOfSize.add(number);
	}
	
	public void addMaxEmployees(int value){
		this.maxEmployees.add(value);
	}

	public void addTotalEmployees(int value){
		this.totalEmployees.add(value);
	}

	public void addAverageEmployees(double value){
		this.averageEmployees.add(value);
	}

	public void addTotalCompany(int[] list){
		this.totalCompany.add(list);
	}

	public void addTotalEmployee(int[] list){
		this.totalEmployee.add(list);
	}

	public void addStartupCompany(int[] list){
		this.startupCompany.add(list);
	}

	public void addStartupEmployee(int[] list){
		this.startupEmployee.add(list);
	}

	public void addTotalRuralCompany(int[] list){
		this.totalRuralCompany.add(list);
	}

	public void addTotalRuralEmployee(int[] list){
		this.totalRuralEmployee.add(list);
	}

	public void addStartupRuralCompany(int[] list){
		this.startupRuralCompany.add(list);
	}

	public void addStartupRuralEmployee(int[] list){
		this.startupRuralEmployee.add(list);
	}

	public void addTotalUrbanCompany(int[] list){
		this.totalUrbanCompany.add(list);
	}

	public void addTotalUrbanEmployee(int[] list){
		this.totalUrbanEmployee.add(list);
	}

	public void addStartupUrbanCompany(int[] list){
		this.startupUrbanCompany.add(list);
	}

	public void addStartupUrbanEmployee(int[] list){
		this.startupUrbanEmployee.add(list);
	}

	public void addTotalCompanyByIndustry(int[][] list){
		this.totalCompanyByIndustry.add(list);
	}

	public void addTotalEmployeeByIndustry(int[][] list){
		this.totalEmployeeByIndustry.add(list);
	}

	public void addStartupCompanyByIndustry(int[][] list){
		this.startupCompanyByIndustry.add(list);
	}

	public void addStartupEmployeeByIndustry(int[][] list){
		this.startupEmployeeByIndustry.add(list);
	}

	public void addTotalRuralCompanyByIndustry(int[][] list){
		this.totalRuralCompanyByIndustry.add(list);
	}

	public void addTotalRuralEmployeeByIndustry(int[][] list){
		this.totalRuralEmployeeByIndustry.add(list);
	}

	public void addStartupRuralCompanyByIndustry(int[][] list){
		this.startupRuralCompanyByIndustry.add(list);
	}

	public void addStartupRuralEmployeeByIndustry(int[][] list){
		this.startupRuralEmployeeByIndustry.add(list);
	}

	public void addTotalUrbanCompanyByIndustry(int[][] list){
		this.totalUrbanCompanyByIndustry.add(list);
	}

	public void addTotalUrbanEmployeeByIndustry(int[][] list){
		this.totalUrbanEmployeeByIndustry.add(list);
	}

	public void addStartupUrbanCompanyByIndustry(int[][] list){
		this.startupUrbanCompanyByIndustry.add(list);
	}

	public void addStartupUrbanEmployeeByIndustry(int[][] list){
		this.startupUrbanEmployeeByIndustry.add(list);
	}

	public void addTotalCompanyByDuration(int[][] list){
		this.totalCompanyByDuration.add(list);
	}

	public void addTotalEmployeeByDuration(int[][] list){
		this.totalEmployeeByDuration.add(list);
	}

	public void addStartupCompanyByDuration(int[][] list){
		this.startupCompanyByDuration.add(list);
	}

	public void addStartupEmployeeByDuration(int[][] list){
		this.startupEmployeeByDuration.add(list);
	}

	public void addTotalRuralCompanyByDuration(int[][] list){
		this.totalRuralCompanyByDuration.add(list);
	}

	public void addTotalRuralEmployeeByDuration(int[][] list){
		this.totalRuralEmployeeByDuration.add(list);
	}

	public void addStartupRuralCompanyByDuration(int[][] list){
		this.startupRuralCompanyByDuration.add(list);
	}

	public void addStartupRuralEmployeeByDuration(int[][] list){
		this.startupRuralEmployeeByDuration.add(list);
	}

	public void addTotalUrbanCompanyByDuration(int[][] list){
		this.totalUrbanCompanyByDuration.add(list);
	}

	public void addTotalUrbanEmployeeByDuration(int[][] list){
		this.totalUrbanEmployeeByDuration.add(list);
	}

	public void addStartupUrbanCompanyByDuration(int[][] list){
		this.startupUrbanCompanyByDuration.add(list);
	}

	public void addStartupUrbanEmployeeByDuration(int[][] list){
		this.startupUrbanEmployeeByDuration.add(list);
	}

	public void addTotalCompanyBySize(int[][] list){
		this.totalCompanyBySize.add(list);
	}

	public void addTotalEmployeeBySize(int[][] list){
		this.totalEmployeeBySize.add(list);
	}

	public void addStartupCompanyBySize(int[][] list){
		this.startupCompanyBySize.add(list);
	}

	public void addStartupEmployeeBySize(int[][] list){
		this.startupEmployeeBySize.add(list);
	}

	public void addTotalRuralCompanyBySize(int[][] list){
		this.totalRuralCompanyBySize.add(list);
	}

	public void addTotalRuralEmployeeBySize(int[][] list){
		this.totalRuralEmployeeBySize.add(list);
	}

	public void addStartupRuralCompanyBySize(int[][] list){
		this.startupRuralCompanyBySize.add(list);
	}

	public void addStartupRuralEmployeeBySize(int[][] list){
		this.startupRuralEmployeeBySize.add(list);
	}

	public void addTotalUrbanCompanyBySize(int[][] list){
		this.totalUrbanCompanyBySize.add(list);
	}

	public void addTotalUrbanEmployeeBySize(int[][] list){
		this.totalUrbanEmployeeBySize.add(list);
	}

	public void addStartupUrbanCompanyBySize(int[][] list){
		this.startupUrbanCompanyBySize.add(list);
	}

	public void addStartupUrbanEmployeeBySize(int[][] list){
		this.startupUrbanEmployeeBySize.add(list);
	}

	public void addSumOfCompany(int value){
		this.sumOfCompany.add(value);
	}

	public void addSumOfEmployee(int value){
		this.sumOfEmployee.add(value);
	}

	public void addSumOfStartupCompany(int value){
		this.sumOfStartupCompany.add(value);
	}

	public void addSumOfStartupEmployee(int value){
		this.sumOfStartupEmployee.add(value);
	}

	public void addSumOfRuralCompany(int value){
		this.sumOfRuralCompany.add(value);
	}

	public void addSumOfRuralEmployee(int value){
		this.sumOfRuralEmployee.add(value);
	}

	public void addSumOfStartupRuralCompany(int value){
		this.sumOfStartupRuralCompany.add(value);
	}

	public void addSumOfStartupRuralEmployee(int value){
		this.sumOfStartupRuralEmployee.add(value);
	}

	public void addSumOfUrbanCompany(int value){
		this.sumOfUrbanCompany.add(value);
	}

	public void addSumOfUrbanEmployee(int value){
		this.sumOfUrbanEmployee.add(value);
	}

	public void addSumOfStartupUrbanCompany(int value){
		this.sumOfStartupUrbanCompany.add(value);
	}

	public void addSumOfStartupUrbanEmployee(int value){
		this.sumOfStartupUrbanEmployee.add(value);
	}
	
	public void addSumOfCompanyByIndustry(int[] list){
		this.sumOfCompanyByIndustry.add(list);
	}

	public void addSumOfEmployeeByIndustry(int[] list){
		this.sumOfEmployeeByIndustry.add(list);
	}

	public void addSumOfStartupCompanyByIndustry(int[] list){
		this.sumOfStartupCompanyByIndustry.add(list);
	}

	public void addSumOfStartupEmployeeByIndustry(int[] list){
		this.sumOfStartupEmployeeByIndustry.add(list);
	}

	public void addSumOfRuralCompanyByIndustry(int[] list){
		this.sumOfRuralCompanyByIndustry.add(list);
	}

	public void addSumOfRuralEmployeeByIndustry(int[] list){
		this.sumOfRuralEmployeeByIndustry.add(list);
	}

	public void addSumOfStartupRuralCompanyByIndustry(int[] list){
		this.sumOfStartupRuralCompanyByIndustry.add(list);
	}

	public void addSumOfStartupRuralEmployeeByIndustry(int[] list){
		this.sumOfStartupRuralEmployeeByIndustry.add(list);
	}

	public void addSumOfUrbanCompanyByIndustry(int[] list){
		this.sumOfUrbanCompanyByIndustry.add(list);
	}

	public void addSumOfUrbanEmployeeByIndustry(int[] list){
		this.sumOfUrbanEmployeeByIndustry.add(list);
	}

	public void addSumOfStartupUrbanCompanyByIndustry(int[] list){
		this.sumOfStartupUrbanCompanyByIndustry.add(list);
	}

	public void addSumOfStartupUrbanEmployeeByIndustry(int[] list){
		this.sumOfStartupUrbanEmployeeByIndustry.add(list);
	}

	public void addSumOfCompanyByDuration(int[] list){
		this.sumOfCompanyByDuration.add(list);
	}

	public void addSumOfEmployeeByDuration(int[] list){
		this.sumOfEmployeeByDuration.add(list);
	}

	public void addSumOfStartupCompanyByDuration(int[] list){
		this.sumOfStartupCompanyByDuration.add(list);
	}

	public void addSumOfStartupEmployeeByDuration(int[] list){
		this.sumOfStartupEmployeeByDuration.add(list);
	}

	public void addSumOfRuralCompanyByDuration(int[] list){
		this.sumOfRuralCompanyByDuration.add(list);
	}

	public void addSumOfRuralEmployeeByDuration(int[] list){
		this.sumOfRuralEmployeeByDuration.add(list);
	}

	public void addSumOfStartupRuralCompanyByDuration(int[] list){
		this.sumOfStartupRuralCompanyByDuration.add(list);
	}

	public void addSumOfStartupRuralEmployeeByDuration(int[] list){
		this.sumOfStartupRuralEmployeeByDuration.add(list);
	}

	public void addSumOfUrbanCompanyByDuration(int[] list){
		this.sumOfUrbanCompanyByDuration.add(list);
	}

	public void addSumOfUrbanEmployeeByDuration(int[] list){
		this.sumOfUrbanEmployeeByDuration.add(list);
	}

	public void addSumOfStartupUrbanCompanyByDuration(int[] list){
		this.sumOfStartupUrbanCompanyByDuration.add(list);
	}

	public void addSumOfStartupUrbanEmployeeByDuration(int[] list){
		this.sumOfStartupUrbanEmployeeByDuration.add(list);
	}

	public void addSumOfCompanyBySize(int[] list){
		this.sumOfCompanyBySize.add(list);
	}

	public void addSumOfEmployeeBySize(int[] list){
		this.sumOfEmployeeBySize.add(list);
	}

	public void addSumOfStartupCompanyBySize(int[] list){
		this.sumOfStartupCompanyBySize.add(list);
	}

	public void addSumOfStartupEmployeeBySize(int[] list){
		this.sumOfStartupEmployeeBySize.add(list);
	}

	public void addSumOfRuralCompanyBySize(int[] list){
		this.sumOfRuralCompanyBySize.add(list);
	}

	public void addSumOfRuralEmployeeBySize(int[] list){
		this.sumOfRuralEmployeeBySize.add(list);
	}

	public void addSumOfStartupRuralCompanyBySize(int[] list){
		this.sumOfStartupRuralCompanyBySize.add(list);
	}

	public void addSumOfStartupRuralEmployeeBySize(int[] list){
		this.sumOfStartupRuralEmployeeBySize.add(list);
	}

	public void addSumOfUrbanCompanyBySize(int[] list){
		this.sumOfUrbanCompanyBySize.add(list);
	}

	public void addSumOfUrbanEmployeeBySize(int[] list){
		this.sumOfUrbanEmployeeBySize.add(list);
	}

	public void addSumOfStartupUrbanCompanyBySize(int[] list){
		this.sumOfStartupUrbanCompanyBySize.add(list);
	}

	public void addSumOfStartupUrbanEmployeeBySize(int[] list){
		this.sumOfStartupUrbanEmployeeBySize.add(list);
	}
	
	public void addLocationCodeList(ArrayList<String> list){
		this.locationCodeList.add(list);
	}
	
	public void addIndustryCodeList(ArrayList<String> list){
		this.industryCodeList.add(list);
	}
	
	public void addLocationCodeMap(HashMap<String, String> map){
		this.locationCodeMap.add(map);
	}
	
	public void addIndustryCodeMap(HashMap<String, String> map){
		this.industryCodeMap.add(map);
	}
	
	public int getYearList(int year){
		return this.yearList.get(year);
	}

	public int getNumberOfYears(){
		return this.numberOfYears;
	}

	public int getNumberOfCompany(int year){
		return this.numberOfCompany.get(year);
	}

	public int getNumberOfRegion(int year){
		return this.numberOfRegion.get(year);
	}

	public int getNumberOfIndustry(int year){
		return this.numberOfIndustry.get(year);
	}

	public int getNumberOfDuration(int year){
		return this.numberOfDuration.get(year);
	}
	
	public int getNumberOfSize(int year){
		return this.numberOfSize.get(year);
	}
	
	public int getMaxEmployees(int year){
		return this.maxEmployees.get(year);
	}

	public int getTotalEmployees(int year){
		return this.totalEmployees.get(year);
	}

	public double getAverageEmployees(int year){
		return this.averageEmployees.get(year);
	}

	public int[] getTotalCompany(int year){
		return this.totalCompany.get(year);
	}

	public int[] getTotalEmployee(int year){
		return this.totalEmployee.get(year);
	}

	public int[] getStartupCompany(int year){
		return this.startupCompany.get(year);
	}

	public int[] getStartupEmployee(int year){
		return this.startupEmployee.get(year);
	}

	public int[] getTotalRuralCompany(int year){
		return this.totalRuralCompany.get(year);
	}

	public int[] getTotalRuralEmployee(int year){
		return this.totalRuralEmployee.get(year);
	}

	public int[] getStartupRuralCompany(int year){
		return this.startupRuralCompany.get(year);
	}

	public int[] getStartupRuralEmployee(int year){
		return this.startupRuralEmployee.get(year);
	}

	public int[] getTotalUrbanCompany(int year){
		return this.totalUrbanCompany.get(year);
	}

	public int[] getTotalUrbanEmployee(int year){
		return this.totalUrbanEmployee.get(year);
	}

	public int[] getStartupUrbanCompany(int year){
		return this.startupUrbanCompany.get(year);
	}

	public int[] getStartupUrbanEmployee(int year){
		return this.startupUrbanEmployee.get(year);
	}

	public int[][] getTotalCompanyByIndustry(int year){
		return this.totalCompanyByIndustry.get(year);
	}

	public int[][] getTotalEmployeeByIndustry(int year){
		return this.totalEmployeeByIndustry.get(year);
	}

	public int[][] getStartupCompanyByIndustry(int year){
		return this.startupCompanyByIndustry.get(year);
	}

	public int[][] getStartupEmployeeByIndustry(int year){
		return this.startupEmployeeByIndustry.get(year);
	}

	public int[][] getTotalRuralCompanyByIndustry(int year){
		return this.totalRuralCompanyByIndustry.get(year);
	}

	public int[][] getTotalRuralEmployeeByIndustry(int year){
		return this.totalRuralEmployeeByIndustry.get(year);
	}

	public int[][] getStartupRuralCompanyByIndustry(int year){
		return this.startupRuralCompanyByIndustry.get(year);
	}

	public int[][] getStartupRuralEmployeeByIndustry(int year){
		return this.startupRuralEmployeeByIndustry.get(year);
	}

	public int[][] getTotalUrbanCompanyByIndustry(int year){
		return this.totalUrbanCompanyByIndustry.get(year);
	}

	public int[][] getTotalUrbanEmployeeByIndustry(int year){
		return this.totalUrbanEmployeeByIndustry.get(year);
	}

	public int[][] getStartupUrbanCompanyByIndustry(int year){
		return this.startupUrbanCompanyByIndustry.get(year);
	}

	public int[][] getStartupUrbanEmployeeByIndustry(int year){
		return this.startupUrbanEmployeeByIndustry.get(year);
	}

	public int[][] getTotalCompanyByDuration(int year){
		return this.totalCompanyByDuration.get(year);
	}

	public int[][] getTotalEmployeeByDuration(int year){
		return this.totalEmployeeByDuration.get(year);
	}

	public int[][] getStartupCompanyByDuration(int year){
		return this.startupCompanyByDuration.get(year);
	}

	public int[][] getStartupEmployeeByDuration(int year){
		return this.startupEmployeeByDuration.get(year);
	}

	public int[][] getTotalRuralCompanyByDuration(int year){
		return this.totalRuralCompanyByDuration.get(year);
	}

	public int[][] getTotalRuralEmployeeByDuration(int year){
		return this.totalRuralEmployeeByDuration.get(year);
	}

	public int[][] getStartupRuralCompanyByDuration(int year){
		return this.startupRuralCompanyByDuration.get(year);
	}

	public int[][] getStartupRuralEmployeeByDuration(int year){
		return this.startupRuralEmployeeByDuration.get(year);
	}

	public int[][] getTotalUrbanCompanyByDuration(int year){
		return this.totalUrbanCompanyByDuration.get(year);
	}

	public int[][] getTotalUrbanEmployeeByDuration(int year){
		return this.totalUrbanEmployeeByDuration.get(year);
	}

	public int[][] getStartupUrbanCompanyByDuration(int year){
		return this.startupUrbanCompanyByDuration.get(year);
	}

	public int[][] getStartupUrbanEmployeeByDuration(int year){
		return this.startupUrbanEmployeeByDuration.get(year);
	}

	public int[][] getTotalCompanyBySize(int year){
		return this.totalCompanyBySize.get(year);
	}

	public int[][] getTotalEmployeeBySize(int year){
		return this.totalEmployeeBySize.get(year);
	}

	public int[][] getStartupCompanyBySize(int year){
		return this.startupCompanyBySize.get(year);
	}

	public int[][] getStartupEmployeeBySize(int year){
		return this.startupEmployeeBySize.get(year);
	}

	public int[][] getTotalRuralCompanyBySize(int year){
		return this.totalRuralCompanyBySize.get(year);
	}

	public int[][] getTotalRuralEmployeeBySize(int year){
		return this.totalRuralEmployeeBySize.get(year);
	}

	public int[][] getStartupRuralCompanyBySize(int year){
		return this.startupRuralCompanyBySize.get(year);
	}

	public int[][] getStartupRuralEmployeeBySize(int year){
		return this.startupRuralEmployeeBySize.get(year);
	}

	public int[][] getTotalUrbanCompanyBySize(int year){
		return this.totalUrbanCompanyBySize.get(year);
	}

	public int[][] getTotalUrbanEmployeeBySize(int year){
		return this.totalUrbanEmployeeBySize.get(year);
	}

	public int[][] getStartupUrbanCompanyBySize(int year){
		return this.startupUrbanCompanyBySize.get(year);
	}

	public int[][] getStartupUrbanEmployeeBySize(int year){
		return this.startupUrbanEmployeeBySize.get(year);
	}
	
	public int getSumOfCompany(int year){
		return this.sumOfCompany.get(year);
	}

	public int getSumOfEmployee(int year){
		return this.sumOfEmployee.get(year);
	}

	public int getSumOfStartupCompany(int year){
		return this.sumOfStartupCompany.get(year);
	}

	public int getSumOfStartupEmployee(int year){
		return this.sumOfStartupEmployee.get(year);
	}

	public int getSumOfRuralCompany(int year){
		return this.sumOfRuralCompany.get(year);
	}

	public int getSumOfRuralEmployee(int year){
		return this.sumOfRuralEmployee.get(year);
	}

	public int getSumOfStartupRuralCompany(int year){
		return this.sumOfStartupRuralCompany.get(year);
	}

	public int getSumOfStartupRuralEmployee(int year){
		return this.sumOfStartupRuralEmployee.get(year);
	}

	public int getSumOfUrbanCompany(int year){
		return this.sumOfUrbanCompany.get(year);
	}

	public int getSumOfUrbanEmployee(int year){
		return this.sumOfUrbanEmployee.get(year);
	}

	public int getSumOfStartupUrbanCompany(int year){
		return this.sumOfStartupUrbanCompany.get(year);
	}

	public int getSumOfStartupUrbanEmployee(int year){
		return this.sumOfStartupUrbanEmployee.get(year);
	}
	
	public int[] getSumOfCompanyByIndustry(int year){
		return this.sumOfCompanyByIndustry.get(year);
	}

	public int[] getSumOfEmployeeByIndustry(int year){
		return this.sumOfEmployeeByIndustry.get(year);
	}

	public int[] getSumOfStartupCompanyByIndustry(int year){
		return this.sumOfStartupCompanyByIndustry.get(year);
	}

	public int[] getSumOfStartupEmployeeByIndustry(int year){
		return this.sumOfStartupEmployeeByIndustry.get(year);
	}

	public int[] getSumOfRuralCompanyByIndustry(int year){
		return this.sumOfRuralCompanyByIndustry.get(year);
	}

	public int[] getSumOfRuralEmployeeByIndustry(int year){
		return this.sumOfRuralEmployeeByIndustry.get(year);
	}

	public int[] getSumOfStartupRuralCompanyByIndustry(int year){
		return this.sumOfStartupRuralCompanyByIndustry.get(year);
	}

	public int[] getSumOfStartupRuralEmployeeByIndustry(int year){
		return this.sumOfStartupRuralEmployeeByIndustry.get(year);
	}

	public int[] getSumOfUrbanCompanyByIndustry(int year){
		return this.sumOfUrbanCompanyByIndustry.get(year);
	}

	public int[] getSumOfUrbanEmployeeByIndustry(int year){
		return this.sumOfUrbanEmployeeByIndustry.get(year);
	}

	public int[] getSumOfStartupUrbanCompanyByIndustry(int year){
		return this.sumOfStartupUrbanCompanyByIndustry.get(year);
	}

	public int[] getSumOfStartupUrbanEmployeeByIndustry(int year){
		return this.sumOfStartupUrbanEmployeeByIndustry.get(year);
	}

	public int[] getSumOfCompanyByDuration(int year){
		return this.sumOfCompanyByDuration.get(year);
	}

	public int[] getSumOfEmployeeByDuration(int year){
		return this.sumOfEmployeeByDuration.get(year);
	}

	public int[] getSumOfStartupCompanyByDuration(int year){
		return this.sumOfStartupCompanyByDuration.get(year);
	}

	public int[] getSumOfStartupEmployeeByDuration(int year){
		return this.sumOfStartupEmployeeByDuration.get(year);
	}

	public int[] getSumOfRuralCompanyByDuration(int year){
		return this.sumOfRuralCompanyByDuration.get(year);
	}

	public int[] getSumOfRuralEmployeeByDuration(int year){
		return this.sumOfRuralEmployeeByDuration.get(year);
	}

	public int[] getSumOfStartupRuralCompanyByDuration(int year){
		return this.sumOfStartupRuralCompanyByDuration.get(year);
	}

	public int[] getSumOfStartupRuralEmployeeByDuration(int year){
		return this.sumOfStartupRuralEmployeeByDuration.get(year);
	}

	public int[] getSumOfUrbanCompanyByDuration(int year){
		return this.sumOfUrbanCompanyByDuration.get(year);
	}

	public int[] getSumOfUrbanEmployeeByDuration(int year){
		return this.sumOfUrbanEmployeeByDuration.get(year);
	}

	public int[] getSumOfStartupUrbanCompanyByDuration(int year){
		return this.sumOfStartupUrbanCompanyByDuration.get(year);
	}

	public int[] getSumOfStartupUrbanEmployeeByDuration(int year){
		return this.sumOfStartupUrbanEmployeeByDuration.get(year);
	}

	public int[] getSumOfCompanyBySize(int year){
		return this.sumOfCompanyBySize.get(year);
	}

	public int[] getSumOfEmployeeBySize(int year){
		return this.sumOfEmployeeBySize.get(year);
	}

	public int[] getSumOfStartupCompanyBySize(int year){
		return this.sumOfStartupCompanyBySize.get(year);
	}

	public int[] getSumOfStartupEmployeeBySize(int year){
		return this.sumOfStartupEmployeeBySize.get(year);
	}

	public int[] getSumOfRuralCompanyBySize(int year){
		return this.sumOfRuralCompanyBySize.get(year);
	}

	public int[] getSumOfRuralEmployeeBySize(int year){
		return this.sumOfRuralEmployeeBySize.get(year);
	}

	public int[] getSumOfStartupRuralCompanyBySize(int year){
		return this.sumOfStartupRuralCompanyBySize.get(year);
	}

	public int[] getSumOfStartupRuralEmployeeBySize(int year){
		return this.sumOfStartupRuralEmployeeBySize.get(year);
	}

	public int[] getSumOfUrbanCompanyBySize(int year){
		return this.sumOfUrbanCompanyBySize.get(year);
	}

	public int[] getSumOfUrbanEmployeeBySize(int year){
		return this.sumOfUrbanEmployeeBySize.get(year);
	}

	public int[] getSumOfStartupUrbanCompanyBySize(int year){
		return this.sumOfStartupUrbanCompanyBySize.get(year);
	}

	public int[] getSumOfStartupUrbanEmployeeBySize(int year){
		return this.sumOfStartupUrbanEmployeeBySize.get(year);
	}
	
	public ArrayList<String> getLocationCodeList(int year){
		return this.locationCodeList.get(year);
	}
	
	public ArrayList<String> getIndustryCodeList(int year){
		return this.industryCodeList.get(year);
	}
	
	public HashMap<String, String> getLocationCodeMap(int year){
		return this.locationCodeMap.get(year);
	}
	
	public HashMap<String, String> getIndustryCodeMap(int year){
		return this.industryCodeMap.get(year);
	}
}
