package regionalDiversity.data;

public class SocietyMicroData {


	int regionCode;
	int lifeSatisfaction;
	
	public SocietyMicroData(){		
	}
	
	public SocietyMicroData(int region, int score){
		this.setMicroData(region, score);
	}
		
	public int getRegionCode(){
		return this.regionCode;
	}	
	
	public int getLifeSatisfaction(){
		return this.lifeSatisfaction;
	}
	
	public void setMicroData(int region, int score){
		this.regionCode = region;
		this.lifeSatisfaction = score;
	}
	
	public void setRegionCode(int value){
		this.regionCode = value;
	}	
	
	public void setLifeSatisfaction(int value){
		this.lifeSatisfaction = value;
	}
}
