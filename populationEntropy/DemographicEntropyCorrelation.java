package populationEntropy;

import java.util.ArrayList;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import populationEntropy.MigrationDataAnalyzer;

public class DemographicEntropyCorrelation {

	/**
	 *  Subject: Demographic correlation calculator
	 *  Developer: Jemyung Lee
	 *  Developed Data: 2016.8.22
	 *  Last Modified Data: 2017.8.7 
	 *  Department: Division of Environmental Science and Technology, Kyoto University
	 *  Description: Estimate correlation between factors and migration
	 */
	
	ArrayList<double[][]> correlationList;
	ArrayList<double[][]> pValueList;
	
	MigrationDataAnalyzer mda;
	
	public DemographicEntropyCorrelation(){
		mda = new MigrationDataAnalyzer();
		this.initiate();
	}
	
	public void initiate(){
		this.correlationList = new ArrayList<double[][]>();
		this.pValueList = new ArrayList<double[][]>();
	}
	
	public void calculateCorrelationship() throws CloneNotSupportedException{
		int i, j, k, s;
		int tmpIndex;
		String[] checkTitle = {"GRDP", "employment"};
		double[][] tmpMatrix, tmpCorrelation, tmpPvalue;
		int[] yearSpan = new int[this.mda.titleList.size()];
		RealMatrix tmpCorrelationMatrix;
		RealMatrix tmpPvalueMatrix;
		PearsonsCorrelation pc;
		
		MigrationDataAnalyzer tmpMda;
		
		/*** prepare correlation analysis ***/
		for(i=0 ; i<this.mda.titleList.size() ; i++){
			if(this.mda.titleList.get(i).startsWith("-")) 
				yearSpan[i] = Integer.parseInt(this.mda.titleList.get(i).substring(0, 2));
			else if(this.mda.titleList.get(i).startsWith("+")) 
				yearSpan[i] = Integer.parseInt(this.mda.titleList.get(i).substring(1, 2));
			else yearSpan[i] = 0;
		}
		
		/*** analyze non-year span correlation ***/
		for(i=0 ; i<this.mda.assembledYears.size(); i++){
			tmpMatrix = new double[this.mda.assembledDistricts.get(i).size()][this.mda.assembledData.get(i).size()];
			tmpCorrelation = new double[this.mda.assembledData.get(i).size()][this.mda.assembledData.get(i).size()];
			tmpPvalue = new double[this.mda.assembledData.get(i).size()][this.mda.assembledData.get(i).size()];
			
			for(j=0 ; j<this.mda.assembledDistricts.get(i).size() ; j++)
				for(k=0 ; k<this.mda.assembledData.get(i).size() ; k++)
					tmpMatrix[j][k] = this.mda.assembledData.get(i).get(k).get(j);
			if(this.mda.assembledDistricts.get(i).size()>2){
				pc =  new PearsonsCorrelation(tmpMatrix);
				tmpCorrelationMatrix = pc.getCorrelationMatrix();
				tmpPvalueMatrix = pc.getCorrelationPValues();
				
				for(j=0 ; j<this.mda.titleList.size() ; j++){
					for(k=0 ; k<this.mda.titleList.size() ; k++){
						if(yearSpan[j] == 0 && yearSpan[k] == 0){
							tmpCorrelation[j][k] = tmpCorrelationMatrix.getEntry(j, k);
							tmpPvalue[j][k] = tmpPvalueMatrix.getEntry(j, k);
							if(Double.isNaN(tmpCorrelation[j][k])) tmpCorrelation[j][k] = 0.0;
							if(Double.isNaN(tmpPvalue[j][k])) tmpPvalue[j][k] = 0.0;
						}
					}
				}
			}

			this.correlationList.add(tmpCorrelation);
			this.pValueList.add(tmpPvalue);
		}
	
		/*** analyze GRDP & employment correlation ***/
		for(s=0 ; s<checkTitle.length ; s++){
			tmpMda = this.mda.clone();
			if(s==0) tmpMda.reviseToFitGRDPData();
			else if(s==1) tmpMda.reviseToFitEmploymentData();
	
			for(i=0 ; i<tmpMda.assembledYears.size(); i++){
				tmpIndex = this.mda.assembledYears.indexOf(tmpMda.assembledYears.get(i));
				tmpMatrix = 
						new double[tmpMda.assembledDistricts.get(i).size()][tmpMda.assembledData.get(i).size()];
				tmpCorrelation = this.correlationList.get(tmpIndex);
				tmpPvalue = this.pValueList.get(tmpIndex);
				
				for(j=0 ; j<tmpMda.assembledDistricts.get(i).size() ; j++)
					for(k=0 ; k<tmpMda.assembledData.get(i).size() ; k++)
						tmpMatrix[j][k] = tmpMda.assembledData.get(i).get(k).get(j);
				if(tmpMda.assembledDistricts.get(i).size()>2){
					pc =  new PearsonsCorrelation(tmpMatrix);
					tmpCorrelationMatrix = pc.getCorrelationMatrix();
					tmpPvalueMatrix = pc.getCorrelationPValues();
					
					for(j=0 ; j<tmpMda.titleList.size() ; j++){
						for(k=0 ; k<tmpMda.titleList.size() ; k++){
							if(tmpMda.titleList.get(j).contains(checkTitle[s]) 
									|| tmpMda.titleList.get(k).contains(checkTitle[s])){
								tmpCorrelation[j][k] = tmpCorrelationMatrix.getEntry(j, k);
								tmpPvalue[j][k] = tmpPvalueMatrix.getEntry(j, k);
								if(Double.isNaN(tmpCorrelation[j][k])) tmpCorrelation[j][k] = 0.0;
								if(Double.isNaN(tmpPvalue[j][k])) tmpPvalue[j][k] = 0.0;
							}
						}
					}
				}
			}	
		}
		
	}

}
