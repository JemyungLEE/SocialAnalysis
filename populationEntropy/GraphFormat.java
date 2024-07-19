package populationEntropy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import populationEntropy.data.*;

public class GraphFormat {

	public void readInteraction(String inputFile, CurvatureData cData){
		
	}
	
	public void readCoordinate(String inputFile, InteractionData iData){
		int i;		
		int district = iData.getDistricNumber();
		String districtName;
		double latitude, longitude;
		
		try{
			File file = new File(inputFile);			
			Scanner scan = new Scanner(file);		
			
			scan.nextLine();					
			
			while(scan.hasNext()){	
				districtName = scan.next();
				latitude = scan.nextDouble();
				longitude = scan.nextDouble();
				
				for(i=0 ; i<district ; i++)
					if(districtName.equals(iData.getDistrictName(i)))
						iData.setLatLongCoordinates(i, latitude, longitude);
			}			
			scan.close();			
		} catch(IOException e) {
			System.err.print("Graph Format: reading coordinates error\t");
		}
	}
	
	public void readCoordinate(String inputFile, CurvatureData cData){
		int i;		
		int district = cData.getDistricNumber();
		String districtName;
		double latitude, longitude;
		
		try{
			File file = new File(inputFile);			
			Scanner scan = new Scanner(file);		
			
			scan.nextLine();					
			
			while(scan.hasNext()){	
				districtName = scan.next();
				latitude = scan.nextDouble();
				longitude = scan.nextDouble();
				
				for(i=0 ; i<district ; i++){
					if(districtName.equals(cData.getDistrictName(i))){
						cData.setLatLongCoordinates(i, latitude, longitude);
					}
				}
			}			
			scan.close();			
		} catch(IOException e) {
			System.err.print("Graph Format: reading coordinates error\t");
		}
	}
	
	public void readCoordinate(String inputFile, CurvatureData cData, DataReader_Japan drj){	
		int codeIndex;
		String code;
		double latitude, longitude;
		
		try{
			File file = new File(inputFile);			
			Scanner scan = new Scanner(file);		
			
			scan.nextLine();					
			
			while(scan.hasNext()){	
				code = scan.next();
				latitude = scan.nextDouble();
				longitude = scan.nextDouble();
				
				if(drj.standardCode.contains(code)){
					codeIndex = drj.standardCode.indexOf(code);
					cData.setLatLongCoordinates(codeIndex, latitude, longitude);
				}
			}			
			scan.close();			
		} catch(IOException e) {
			System.err.print("Graph Format: reading coordinates error\t");
		}
	}
	
	public void makeGDFFormatInteraction(String outputFile, InteractionData iData){
		int i, j;
		int year;
		int duration = iData.getNumberOfYears();
		int district = iData.getDistricNumber();
		double minLat = iData.getMinLatitude();
		double minLong = iData.getMinLongitude();
		double interaction;
		
		for(year=0 ; year<duration ; year++){
			try{
				File file = new File(outputFile.replace(".gdf", "_IN_"+iData.getYear(year)+".gdf"));
				PrintWriter pw = new PrintWriter(file);
				
				pw.println("nodedef>name VARCHAR, label VARCHAR, latitude DOUBLE, longitude DOUBLE");
				for(i=0 ; i<district ; i++)
					if(iData.getLatLongLatitude(i) > minLat && iData.getLatLongLongitude(i) > minLong)
						pw.println("s"+i+","+iData.getDistrictName(i)
											+","+iData.getLatLongLongitude(i)+","+iData.getLatLongLatitude(i));					
				
				pw.println("edgedef>node1 VARCHAR, node2 VARCHAR, directed BOOLEAN, weight DOUBLE");			
				for(i=0 ; i<district ; i++){
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong){
						for(j=0 ; j<district ; j++)
							if(iData.getLatitude(j) > minLat || iData.getLongitude(j) > minLong)
								pw.println("s"+i+",s"+j+",true,"+iData.getInteractionIn(year, i, j));
					}else {
						System.out.print("coordinate error:"+iData.getDistrictName(i));
						System.out.print("\tlatitude: "+iData.getLongitude(i)+"\tlongitude: "+iData.getLatitude(i));
						System.out.println("\tmin. lat.: "+minLat+"\tmin. long,: "+minLong);
					}
				}
				pw.close();
				
				file = new File(outputFile.replace(".gdf", "_OUT_"+iData.getYear(year)+".gdf"));
				pw = new PrintWriter(file);
					
				pw.println("nodedef>name VARCHAR, label VARCHAR, latitude DOUBLE, longitude DOUBLE");			
				for(i=0 ; i<district ; i++)
					if(iData.getLatLongLatitude(i) > minLat && iData.getLatLongLongitude(i) > minLong)
						pw.println("s"+i+","+iData.getDistrictName(i)
								+","+iData.getLatLongLongitude(i)+","+iData.getLatLongLatitude(i));					
					
				pw.println("edgedef>node1 VARCHAR, node2 VARCHAR, directed BOOLEAN, weight DOUBLE");			
				for(i=0 ; i<district ; i++)
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong)
						for(j=0 ; j<district ; j++)
							if(iData.getLatitude(j) > minLat || iData.getLongitude(j) > minLong)
								pw.println("s"+i+",s"+j+",true,"+iData.getInteractionOut(year, i, j));
				pw.close();
				
				file = new File(outputFile.replace(".gdf", "_NET_"+iData.getYear(year)+".gdf"));
				pw = new PrintWriter(file);
					
				pw.println("nodedef>name VARCHAR, label VARCHAR, latitude DOUBLE, longitude DOUBLE");		
				for(i=0 ; i<district ; i++)
					if(iData.getLatLongLatitude(i) > minLat && iData.getLatLongLongitude(i) > minLong)
						pw.println("s"+i+","+iData.getDistrictName(i)
								+","+iData.getLatLongLongitude(i)+","+iData.getLatLongLatitude(i));					
					
				pw.println("edgedef>node1 VARCHAR, node2 VARCHAR, directed BOOLEAN, weight DOUBLE");			
				for(i=0 ; i<district ; i++){
					if(iData.getLatitude(i) > minLat || iData.getLongitude(i) > minLong){
						for(j=0 ; j<district ; j++){
							if(iData.getLatitude(j) > minLat || iData.getLongitude(j) > minLong){
								interaction = iData.getInteractionNet(year, i, j);
								if(interaction > 0) pw.println("s"+i+",s"+j+",true,"+interaction);
							}							
						}						
					}
				}
				pw.close();
				
			}catch(IOException e) {}
		}
	}
	
	public void makeGDFFormatInteraction(String outputFile, CurvatureData cData){
		
		int i, j;
		int year;
		int duration = cData.getNumberOfYears();
		int district = cData.getDistricNumber();
		double minLat = cData.getMinLatitude();
		double maxLat = cData.getMaxLatitude();
		double minLong = cData.getMinLongitude();
		double maxLong = cData.getMaxLongitude();
		double interaction;
		
		for(year=0 ; year<duration ; year++){
			try{
				File file = new File(outputFile.replace(".gdf", "_"+cData.getYear(year)+".gdf"));
				PrintWriter pw = new PrintWriter(file);
				
				pw.println("nodedef>name VARCHAR, label VARCHAR, latitude DOUBLE, longitude DOUBLE");
				
				for(i=0 ; i<district ; i++)
					if(cData.getLatLongLatitude(i) > minLat && cData.getLatLongLongitude(i) > minLong)
						pw.println("s"+i+","+cData.getDistrictName(i)
											+","+cData.getLatLongLongitude(i)+","+cData.getLatLongLatitude(i));					
				
				pw.println("edgedef>node1 VARCHAR, node2 VARCHAR, directed BOOLEAN, weight DOUBLE");
				
				for(i=0 ; i<district ; i++){
					if(cData.getLatitude(i) > minLat || cData.getLongitude(i) > minLong){
						for(j=0 ; j<district ; j++){
							if(cData.getLatitude(j) > minLat || cData.getLongitude(j) > minLong){
								interaction = cData.getInteraction(year, i, j);
								if(interaction > 0){
									pw.println("s"+i+",s"+j+",true,"+interaction);
								}
							}							
						}						
					}else {
						System.out.print("coordinate error:"+cData.getDistrictName(i));
						System.out.print("\tlatitude: "+cData.getLongitude(i)+"\tlongitude: "+cData.getLatitude(i));
						System.out.println("\tmin. lat.: "+minLat+"\tmin. long,: "+minLong);
					}

				}
				
				
				pw.close();
			}catch(IOException e) {}
		}
	}

}
