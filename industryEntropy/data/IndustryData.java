package industryEntropy.data;

import java.util.ArrayList;

public class IndustryData {

	int classNumber;		//number of profit levels
	int industryNumber;		//number of industry classifications
	
	double maxValue;
	double minValue;
	double[] section;		//[class+1]
	
	int[] code;				//[industry]
	int[] level;			//[industry]
	double[] value;			//[industry]	
	String[] title;			//[industry]
	public ArrayList<Integer> codeList;				//[industry]: for checking
	public ArrayList<ArrayList<Integer>> classList;	//[class]
	
	
	public IndustryData(){}
	
	public IndustryData(int classNum, int industryNum){
		
		this.initiate(classNum, industryNum);
	}

	public void initiate(int classNum, int industryNum){
		this.classNumber = classNum;
		this.industryNumber = industryNum;
		
		this.section = new double[classNum+1];
		this.code = new int[industryNum];
		this.level = new int[industryNum];
		this.value = new double[industryNum];
		this.title = new String[industryNum];
		this.codeList = new ArrayList<Integer>();
		this.classList = new ArrayList<ArrayList<Integer>>();
		for(int i=0 ; i<classNum ; i++) this.classList.add(new ArrayList<Integer>());
	}
	
	/*** set data ***/
	public void setClassNumber(int value){
		this.classNumber = value;
	}
	
	public void setIndustryNumber(int value){
		this.industryNumber = value;
	}
	
	public void setMax(double value){
		this.maxValue = value;
	}
	
	public void setMin(double value){
		this.minValue = value;
	}	

	public void setSection(int classN, double value){
		this.section[classN] = value;
	}
	
	public void setCode(int industry, int value){
		this.code[industry] = value;
	}

	public void setLevel(int industry, int value){
		this.level[industry] = value;
	}
	
	public void setValue(int industry, double value){
		this.value[industry] = value;
	}
	
	public void setTitle(int industry, String value){
		this.title[industry] = value;
	}
	
	/*** get data ***/
	public int getClassNumber(){
		return this.classNumber;
	}
	
	public int getIndustryNumber(){
		return this.industryNumber;
	}
	
	public double getMax(){
		return this.maxValue;
	}
	
	public double getMin(){
		return this.minValue;
	}	

	public double getSection(int classN){
		return this.section[classN];
	}
	
	public int getCode(int industry){
		return this.code[industry];
	}

	public int getLevel(int industry){
		return this.level[industry];
	}
	
	public double getValue(int industry){
		return this.value[industry];
	}
	
	public String getTitle(int industry){
		return this.title[industry];
	}
	
}
