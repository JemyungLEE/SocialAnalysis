package regionalAddedValue.data;

import java.util.ArrayList;
import java.util.HashMap;

public class AddedValueData {

	int categoryDepth;
	
	ArrayList<String> industryList;
	HashMap<String, Double> addedValueMap;
	
	public AddedValueData(int classificationDepth){
		this.initate(classificationDepth);
	}
	
	public void initate(int classificationDepth){
		this.categoryDepth = classificationDepth;
		this.initate();
	}
	
	public void initate(){
		this.industryList = new ArrayList<String>();
		this.addedValueMap = new HashMap<String, Double>();
	}
}
