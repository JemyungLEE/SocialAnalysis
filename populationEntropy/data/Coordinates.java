package populationEntropy.data;

import java.util.HashMap;
import java.util.Iterator;


public class Coordinates {

	int coordinatesNumber;
	double maxLatitude;
	double minLatitude;
	double maxLongitude;
	double minLongitude;
	
	HashMap<String, double[]> coordinates;		//<district, [latitude, longitude]>
	
	public Coordinates(){
		
		this.initiate();
	}
	
	
	public Coordinates(double maxLat, double minLat, double maxLong, double minLong){
		
		this.initiate(maxLat, minLat, maxLong, minLong);
	}
	
	public void initiate(){
		
		this.coordinates = new HashMap<String, double[]>(); 
	}

	public void initiate(double maxLat, double minLat, double maxLong, double minLong){
		
		this.maxLatitude = maxLat;
		this.minLatitude = minLat;
		this.maxLongitude = maxLong;
		this.minLongitude = minLong;
		
		this.coordinates = new HashMap<String, double[]>(); 
	}	
			
	public void setMaxLatitude(double value){
		this.maxLatitude = value;
	}

	public void setMinLatitude(double value){
		this.minLatitude = value;
	}
	
	public void setMaxLongitude(double value){
		this.maxLongitude = value;
	}
	
	public void setMinLongitude(double value){
		this.minLongitude = value;
	}
	
	public void setCoordinates(String district, double latitude, double longitude){
		
		double[] points = new double[2];
		
		points[0] = latitude;
		points[1] = longitude;
		
		this.coordinates.put(district, points);
	}	
	
	public double getMaxLatitude(){
		return this.maxLatitude;
	}

	public double getMinLatitude(){
		return this.minLatitude;
	}
	
	public double getMaxLongitude(){
		return this.maxLongitude;
	}
	
	public double getMinLongitude(){
		return this.minLongitude;
	}
	
	public boolean hasDistrict(String district){
		return this.coordinates.containsKey(district);
	}
	
	public boolean hasSimilarDistrict(String district){
		String tmpName;
		Iterator<String> iterDistricts = this.coordinates.keySet().iterator();
		
		while(iterDistricts.hasNext()){
			tmpName = iterDistricts.next();
			if(district.substring(0, district.length()-1).equals(tmpName.substring(0, tmpName.length()-1)))
				return true;
		}
		
		return false;
	}
	
	public String getSimilarDistrict(String district){
		String tmpName;
		Iterator<String> iterDistricts = this.coordinates.keySet().iterator();
		
		while(iterDistricts.hasNext()){
			tmpName = iterDistricts.next();
			if(district.substring(0, district.length()-1).equals(tmpName.substring(0, tmpName.length()-1)))
				return tmpName;
		}
		
		return null;
	}
	
	public double[] getCoordinates(String district){		
		return this.coordinates.get(district);
	}
	
	public double getLatitude(String district){
		double[] points;
		
		points = this.coordinates.get(district);
		return points[0];
	}

	public double getLongitude(String district){
		double[] points;
		
		points = this.coordinates.get(district);
		return points[1];		
	}
	
}
