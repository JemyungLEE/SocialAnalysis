package companyDuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LocalDurationEstimator {

	/**
	 * @param args
	 */
	int startYear, endYear;
	int totalPeriod;
	int sortOfLocation;
	int sortOfIndustryType;
	int sortOfScaleType;
	int sortOfBusinessType;
	int sortOfStoreType;
	
	ArrayList<Integer> locationCode;
	ArrayList<String> locationName;
	
	//[current year][founding year][location][industry][scale][business][store]
	int[][][][][][][] company;
	int[][][][][][][] survivalData;
	int[][][][][][][] durationData;
	int[][][][][][][] closedData;
	double[][][][][][][] survivalRatio;	// = survival(t)/survival(t-1), t: time step
	
	public  LocalDurationEstimator(){
		
	}

	public  LocalDurationEstimator(int sYear, int eYear){
		this.startYear = sYear;
		this.endYear = eYear;
		this.totalPeriod = eYear - sYear + 1;
	}
		
	public void initiate(ArrayList<Integer> locationList, 
									int period, int industry, int scale, int business, int store){
		this.locationCode = locationList;
		
		this.totalPeriod = period;
		this.sortOfLocation = locationList.size();
		this.sortOfIndustryType = industry;
		this.sortOfScaleType = scale;
		this.sortOfBusinessType = business;
		this.sortOfStoreType = store;
		
		this.variableInitiate();
	}
	
	public void variableInitiate(){
		int i,j,k,l,m, n, o;
		this.company = new int[this.totalPeriod][this.totalPeriod][this.sortOfLocation]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.survivalData = new int[this.totalPeriod][this.totalPeriod][this.sortOfLocation]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.durationData = new int[this.totalPeriod][this.totalPeriod+1][this.sortOfLocation]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.closedData = new int[this.totalPeriod][this.totalPeriod-1][this.sortOfLocation]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.survivalRatio = new double[this.totalPeriod][this.totalPeriod-1][this.sortOfLocation]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		
		for(k=0 ; k<this.sortOfLocation ; k++){
			for(l=0 ; l<this.sortOfIndustryType ; l++){
				for(m=0 ; m<this.sortOfScaleType ; m++){
					for(n=0 ; n<this.sortOfBusinessType ; n++){	
						for(o=0 ; o<this.sortOfStoreType ; o++){							
							for(i=0 ; i<this.totalPeriod ; i++){
								for(j=0 ; j<this.totalPeriod ; j++){
									this.company[i][j][k][l][m][n][o] = 0;
									this.survivalData[i][j][k][l][m][n][o] = 0;
								}
								
								for(j=0 ; j<this.totalPeriod+1 ; j++) this.durationData[i][j][k][l][m][n][o] = 0;
		
								for(j=0 ; j<this.totalPeriod-1 ; j++){
									this.closedData[i][j][k][l][m][n][o] = 0;
									this.survivalRatio[i][j][k][l][m][n][o] = 0.0;
								}
							}	
						}
					}
				}	
			}	
		}
	}
	
	public void readLocationCode(String codefile){
		ArrayList<Integer> tmpLocationCode = new ArrayList<Integer>();
		ArrayList<String> tmpLocationName = new ArrayList<String>();
		
		try{
			File file = new File(codefile);
			Scanner scan = new Scanner(file);
			
			tmpLocationCode.add(0);
			tmpLocationName.add("LocationCodeError");
			
			while(scan.hasNext()){
				tmpLocationCode.add(scan.nextInt());
				tmpLocationName.add(scan.next());
			}
			
			scan.close();	
		} catch(IOException e) {}	
		
		this.locationCode = tmpLocationCode;
		this.locationName = tmpLocationName;
	}
	
	public void readCompanyData(String filepath, String filename){		
		int i,j,k,l,m,n,o;
		int tmpFyear;
		int tmpLocationCode;
		int tmpScaleType;
		int tmpIndustryType;
		int tmpBusinessType;
		int tmpStoreType;
				
		for(i = 0 ; i<this.totalPeriod ; i++){	
			try{
				File file = new File(filepath+(i+this.startYear)+filename);
				Scanner scan = new Scanner(file);
					
				scan.nextLine();
				while(scan.hasNext()){
					tmpFyear = scan.nextInt();
					tmpLocationCode = scan.nextInt();
					tmpIndustryType = scan.nextInt();
					tmpScaleType = scan.nextInt();
					tmpBusinessType = scan.nextInt();
					tmpStoreType = scan.nextInt();
							
					if(this.locationCode.contains(tmpLocationCode)==false) tmpLocationCode = 0;
					if(tmpIndustryType<0) tmpIndustryType = 0;
					if(tmpBusinessType<0) tmpBusinessType = 4;
					if(tmpStoreType<0) tmpStoreType = 0;
										
					for(j=0 ; j<=i ; j++)
						if(tmpFyear == j+this.startYear)
							this.company[i][j][this.locationCode.indexOf(tmpLocationCode)]
									[tmpIndustryType][tmpScaleType][tmpBusinessType][tmpStoreType]++;					
				}
				
				scan.close();	
			} catch(IOException e) {}				
		}
		
		for(i=0 ; i<this.totalPeriod ; i++) 
			for(j=0 ; j<this.totalPeriod ; j++)
				for(k=0 ; k<this.sortOfLocation ; k++)
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(m=0 ; m<this.sortOfScaleType ; m++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								for(o=0 ; o<this.sortOfStoreType ; o++)
									this.survivalData[i][j][k][l][m][n][o] = this.company[j][i][k][l][m][n][o];
	}
		
	public void calculateDuration(){
		int i,j,k,l,m,n,o;
				
		for(k=0 ; k<this.sortOfLocation ; k++){
			for(l=0 ; l<this.sortOfIndustryType ; l++){
				for(m=0 ; m<this.sortOfScaleType ; m++){
					for(n=0 ; n<this.sortOfBusinessType ; n++){	
						for(o=0 ; o<this.sortOfStoreType ; o++){
							for(i=0 ; i<this.totalPeriod ; i++){		
								this.durationData[i][0][k][l][m][n][o] = this.company[i][i][k][l][m][n][o];
								for(j=1 ; j<this.totalPeriod-i ; j++)	
									this.durationData[i][j][k][l][m][n][o]
											= this.company[i+j-1][i][k][l][m][n][o] 
												- this.company[i+j][i][k][l][m][n][o];
								this.durationData[i][this.totalPeriod-i][k][l][m][n][o] 
										= this.company[this.totalPeriod-1][i][k][l][m][n][o];			
							}
						}
					}
				}
			}
		}
				
		for(k=0 ; k<this.sortOfLocation ; k++){
			for(l=0 ; l<this.sortOfIndustryType ; l++){
				for(m=0 ; m<this.sortOfScaleType ; m++){
					for(n=0 ; n<this.sortOfBusinessType ; n++){	
						for(o=0 ; o<this.sortOfStoreType ; o++){
							for(i=0 ; i<this.totalPeriod ; i++){		
								for(j=i ; j<this.totalPeriod-1 ; j++){
									this.closedData[i][j][k][l][m][n][o]
											= this.survivalData[i][j][k][l][m][n][o]
													- this.survivalData[i][j+1][k][l][m][n][o];	
									this.survivalRatio[i][j][k][l][m][n][o]
											= (double) this.survivalData[i][j+1][k][l][m][n][o] 
													/ (double) this.survivalData[i][j][k][l][m][n][o];		
								}
							}
						}
					}					
				}
			}
		}	
	}
	
	public void printCompanyData(){
		int i,j,k,l,m,n,o;
		int[][] tmpCompany = new int[this.totalPeriod][this.totalPeriod];
				
		for(i=0 ; i<this.totalPeriod ; i++){
			System.out.print(this.startYear+i+"\t");
			for(j=0 ; j<=i ; j++){
				tmpCompany[i][j] = 0;
				for(k=0 ; k<this.sortOfLocation ; k++)
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(m=0 ; m<this.sortOfScaleType ; m++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								for(o=0 ; o<this.sortOfStoreType ; o++)
									tmpCompany[i][j] += this.company[i][j][k][l][m][n][o];
				System.out.print(tmpCompany[i][j]+"\t");
			}
			System.out.println();
		}	
		
	}
	
	public void printSurvivalData(){
		int i,j,k,l,m,n,o;
		int[][] survivalNumber = new int[this.totalPeriod][this.totalPeriod];
		
		System.out.println("Survival Data:");
		for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
		System.out.println();
		for(i=0 ; i<this.totalPeriod ; i++){
			System.out.print((this.startYear+i)+"\t");
			for(j=0 ; j<i ; j++) System.out.print("\t");
			for(j=i ; j<this.totalPeriod ; j++){
				survivalNumber[i][j] = 0;
				for(k=0 ; k<this.sortOfLocation ; k++)
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(m=0 ; m<this.sortOfScaleType ; m++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								for(o=0 ; o<this.sortOfStoreType ; o++)
									survivalNumber[i][j] += this.survivalData[i][j][k][l][m][n][o];
				System.out.print(survivalNumber[i][j]+"\t");			
			}
			System.out.println();
		}	
	}
	
	public void printDevidedSurvivalData(){
		int i,j,k,l,m,n,o;
		int tmpSurvivalData;
		
		System.out.println("Survival Data: city area");
		for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
		System.out.println();
		for(i=0 ; i<this.totalPeriod ; i++){
			System.out.print((this.startYear+i)+"\t");
			for(j=0 ; j<i ; j++) System.out.print("\t");
			for(j=i ; j<this.totalPeriod ; j++){
				tmpSurvivalData = 0;
				for(l=0 ; l<this.sortOfIndustryType ; l++)
					for(m=0 ; m<this.sortOfScaleType ; m++)
						for(n=0 ; n<this.sortOfBusinessType ; n++)	
							for(o=0 ; o<this.sortOfStoreType ; o++)
								tmpSurvivalData += this.survivalData[i][j][0][l][m][n][o];
				System.out.print(tmpSurvivalData+"\t");			
			}
			System.out.println();
		}	
		
		System.out.println("Survival Data: rural area");
		for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
		System.out.println();
		for(i=0 ; i<this.totalPeriod ; i++){
			System.out.print((this.startYear+i)+"\t");
			for(j=0 ; j<i ; j++) System.out.print("\t");
			for(j=i ; j<this.totalPeriod ; j++){
				tmpSurvivalData = 0;
				for(k=1 ; k<this.sortOfLocation ; k++)
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(m=0 ; m<this.sortOfScaleType ; m++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								for(o=0 ; o<this.sortOfStoreType ; o++)
									tmpSurvivalData += this.survivalData[i][j][k][l][m][n][o];
				System.out.print(tmpSurvivalData+"\t");			
			}
			System.out.println();
		}	
	}
	
	public void printSurvivalDataByIndustryByLocation(){
		int i,j,k,l,m,n,o;
		int tmpSurvivalData;
		

		for(l=0 ; l<this.sortOfIndustryType ; l++){
			System.out.println("Industry type: "+l);
			System.out.println("Survival Data: city area");
			for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
			System.out.println();
			for(i=0 ; i<this.totalPeriod ; i++){
				System.out.print((this.startYear+i)+"\t");
				for(j=0 ; j<i ; j++) System.out.print("\t");
				for(j=i ; j<this.totalPeriod ; j++){
					tmpSurvivalData = 0;
					for(m=0 ; m<this.sortOfScaleType ; m++)
						for(n=0 ; n<this.sortOfBusinessType ; n++)	
							for(o=0 ; o<this.sortOfStoreType ; o++)
								tmpSurvivalData += this.survivalData[i][j][0][l][m][n][o];
					System.out.print(tmpSurvivalData+"\t");			
				}
				System.out.println();
			}	
			
			System.out.println("Survival Data: rural area");
			for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
			System.out.println();
			for(i=0 ; i<this.totalPeriod ; i++){
				System.out.print((this.startYear+i)+"\t");
				for(j=0 ; j<i ; j++) System.out.print("\t");
				for(j=i ; j<this.totalPeriod ; j++){
					tmpSurvivalData = 0;
					for(k=1 ; k<this.sortOfLocation ; k++)
						for(m=0 ; m<this.sortOfScaleType ; m++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								for(o=0 ; o<this.sortOfStoreType ; o++)
									tmpSurvivalData += this.survivalData[i][j][k][l][m][n][o];
					System.out.print(tmpSurvivalData+"\t");			
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public void printSurvivalDataByScaleByLocation(){
		int i,j,k,l,m,n,o;
		int tmpSurvivalData;
		

		for(m=0 ; m<this.sortOfScaleType ; m++){
			System.out.println("Scale type: "+m);
			System.out.println("Survival Data: city area");
			for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
			System.out.println();
			for(i=0 ; i<this.totalPeriod ; i++){
				System.out.print((this.startYear+i)+"\t");
				for(j=0 ; j<i ; j++) System.out.print("\t");
				for(j=i ; j<this.totalPeriod ; j++){
					tmpSurvivalData = 0;
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(n=0 ; n<this.sortOfBusinessType ; n++)	
							for(o=0 ; o<this.sortOfStoreType ; o++)
								tmpSurvivalData += this.survivalData[i][j][0][l][m][n][o];
					System.out.print(tmpSurvivalData+"\t");			
				}
				System.out.println();
			}	
			
			System.out.println("Survival Data: rural area");
			for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
			System.out.println();
			for(i=0 ; i<this.totalPeriod ; i++){
				System.out.print((this.startYear+i)+"\t");
				for(j=0 ; j<i ; j++) System.out.print("\t");
				for(j=i ; j<this.totalPeriod ; j++){
					tmpSurvivalData = 0;
					for(k=1 ; k<this.sortOfLocation ; k++)
						for(l=0 ; l<this.sortOfIndustryType ; l++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								for(o=0 ; o<this.sortOfStoreType ; o++)
									tmpSurvivalData += this.survivalData[i][j][k][l][m][n][o];
					System.out.print(tmpSurvivalData+"\t");			
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public void printSurvivalDataByBusinessByLocation(){
		int i,j,k,l,m,n,o;
		int tmpSurvivalData;
		
		for(n=0 ; n<this.sortOfBusinessType ; n++){
			System.out.println("Type of business entity: "+n);
			System.out.println("Survival Data: city area");
			for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
			System.out.println();
			for(i=0 ; i<this.totalPeriod ; i++){
				System.out.print((this.startYear+i)+"\t");
				for(j=0 ; j<i ; j++) System.out.print("\t");
				for(j=i ; j<this.totalPeriod ; j++){
					tmpSurvivalData = 0;
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(m=0 ; m<this.sortOfScaleType ; m++)	
							for(o=0 ; o<this.sortOfStoreType ; o++)
								tmpSurvivalData += this.survivalData[i][j][0][l][m][n][o];
					System.out.print(tmpSurvivalData+"\t");			
				}
				System.out.println();
			}	
			
			System.out.println("Survival Data: rural area");
			for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
			System.out.println();
			for(i=0 ; i<this.totalPeriod ; i++){
				System.out.print((this.startYear+i)+"\t");
				for(j=0 ; j<i ; j++) System.out.print("\t");
				for(j=i ; j<this.totalPeriod ; j++){
					tmpSurvivalData = 0;
					for(k=1 ; k<this.sortOfLocation ; k++)
						for(l=0 ; l<this.sortOfIndustryType ; l++)
							for(m=0 ; m<this.sortOfScaleType ; m++)		
								for(o=0 ; o<this.sortOfStoreType ; o++)
									tmpSurvivalData += this.survivalData[i][j][k][l][m][n][o];
					System.out.print(tmpSurvivalData+"\t");			
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public void printSurvivalDataByStoreByLocation(){
		int i,j,k,l,m,n,o;
		int tmpSurvivalData;
		

		for(o=0 ; o<this.sortOfStoreType ; o++){
			System.out.println("Type of store network: "+o);
			System.out.println("Survival Data: city area");
			for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
			System.out.println();
			for(i=0 ; i<this.totalPeriod ; i++){
				System.out.print((this.startYear+i)+"\t");
				for(j=0 ; j<i ; j++) System.out.print("\t");
				for(j=i ; j<this.totalPeriod ; j++){
					tmpSurvivalData = 0;
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(m=0 ; m<this.sortOfScaleType ; m++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								tmpSurvivalData += this.survivalData[i][j][0][l][m][n][o];
					System.out.print(tmpSurvivalData+"\t");			
				}
				System.out.println();
			}	
			
			System.out.println("Survival Data: rural area");
			for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
			System.out.println();
			for(i=0 ; i<this.totalPeriod ; i++){
				System.out.print((this.startYear+i)+"\t");
				for(j=0 ; j<i ; j++) System.out.print("\t");
				for(j=i ; j<this.totalPeriod ; j++){
					tmpSurvivalData = 0;
					for(k=1 ; k<this.sortOfLocation ; k++)
						for(l=0 ; l<this.sortOfIndustryType ; l++)
							for(m=0 ; m<this.sortOfScaleType ; m++)
								for(n=0 ; n<this.sortOfBusinessType ; n++)
									tmpSurvivalData += this.survivalData[i][j][k][l][m][n][o];
					System.out.print(tmpSurvivalData+"\t");			
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public void printSurvivalRateByDuration(int printType){
		//print type 0: survival rates & averages by duration
		//print type 1: survival rates
		//print type 2: averages by duration
		
		int i,j,k,l,m,n,o;
		int[][] survivalNumber = new int[this.totalPeriod][this.totalPeriod];
		double[][] survivalRate = new double[this.totalPeriod-1][this.totalPeriod-1];
		double[] averageByYear = new double[this.totalPeriod-1];
		double[] averageByDuration = new double[this.totalPeriod-1];
			
		//calculate survival numbers
		for(i=0 ; i<this.totalPeriod ; i++){
			for(j=0 ; j<this.totalPeriod-i ; j++){	
				survivalNumber[i][j] = 0;
				for(k=0 ; k<this.sortOfLocation ; k++)
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(m=0 ; m<this.sortOfScaleType ; m++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								for(o=0 ; o<this.sortOfStoreType ; o++)
									survivalNumber[i][j] += this.durationData[i][j][k][l][m][n][o];	
			}
		}
		
		//calculate survival rates
		for(i=0 ; i<this.totalPeriod-1 ; i++){	
			for(j=0 ; j<this.totalPeriod-i-1 ; j++){	
				survivalRate[i][j] = (double)survivalNumber[i][j+1]/(double)survivalNumber[i][0];
				averageByYear[i] += survivalRate[i][j]; 
				averageByDuration[j] += survivalRate[i][j];
			}
		}
		for(i=0 ; i<this.totalPeriod-1 ; i++){
			averageByYear[i] /= (double)this.totalPeriod-i-1; 
			averageByDuration[i] /= (double)this.totalPeriod-i-1;			
		}
		
		/*
		//print survival numbers
		if(printType == 0 || printType == 1){
			System.out.println("Survival Numbers by duration");
			for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+i);
			System.out.println();
			for(i=0 ; i<this.totalPeriod ; i++){
				System.out.print(this.startYear+i+"\t");
				for(j=0 ; j<this.totalPeriod-i ; j++) System.out.print(survivalNumber[i][j]+"\t");			
				for(j=0 ; j<i ; j++) System.out.print("\t");
				System.out.println();
			}
		}
		*/
		
		//print survival rates with average
		if(printType == 0 || printType == 1){
			System.out.println();
			System.out.println("Survival rates sorted by duration");
			for(i=0 ; i<this.totalPeriod-1 ; i++) System.out.print("\t"+(i+1));
			System.out.println();		
			for(i=0 ; i<this.totalPeriod-1 ; i++){
				System.out.print(this.startYear+i+"\t");
				for(j=0 ; j<this.totalPeriod-1-i ; j++) System.out.print(survivalRate[i][j]+"\t");	
				for( ; j<this.totalPeriod-1 ; j++) System.out.print("\t");
				System.out.println();
			}
		}
		
		if(printType == 0 || printType == 2){
			System.out.println();
			System.out.println("Average survival rates sorted by duration");
			for(i=0 ; i<this.totalPeriod-1 ; i++) System.out.print("\t"+(i+1));
			System.out.println();
			for(i=0 ; i<this.totalPeriod-1 ; i++) System.out.print(averageByDuration[i]+"\t");
		}
	}

	public void printSurvivalPortionByDurationByLocation(int printType){
		//print type 0: survival rates & averages by duration
		//print type 1: survival rates
		//print type 2: averages by duration
		
		int i,j,k,l,m,n,o;
		int[][] survivalNumber = new int[this.totalPeriod][this.totalPeriod];
		double[][] survivalPortion = new double[this.totalPeriod-1][this.totalPeriod-1];
		double[] averageByYear = new double[this.totalPeriod-1];
		double[] averageByDuration = new double[this.totalPeriod-1];
			
		for(k=1 ; k<this.sortOfLocation ; k++){
			System.out.print("Location: "+this.locationName.get(k)+"\t");
			
			//calculate survival numbers
			for(i=0 ; i<this.totalPeriod ; i++){
				for(j=0 ; j<this.totalPeriod-i ; j++){	
					survivalNumber[i][j] = 0;				
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(m=0 ; m<this.sortOfScaleType ; m++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								for(o=0 ; o<this.sortOfStoreType ; o++)
									survivalNumber[i][j] += this.durationData[i][j][k][l][m][n][o];	
				}
			}		
		
			//calculate survival portion
			for(i=0 ; i<this.totalPeriod-1 ; i++){	
				for(j=0 ; j<this.totalPeriod-i-1 ; j++){
					averageByYear[i] = 0; 
					averageByDuration[j] = 0;
				}
			}					
			for(i=0 ; i<this.totalPeriod-1 ; i++){	
				for(j=0 ; j<this.totalPeriod-i-1 ; j++){	
					if(survivalNumber[i][0]==0) survivalPortion[i][j] = 0; 
					else survivalPortion[i][j] = (double)survivalNumber[i][j+1]/(double)survivalNumber[i][0];
					averageByYear[i] += survivalPortion[i][j]; 
					averageByDuration[j] += survivalPortion[i][j];
				}
			}
			for(i=0 ; i<this.totalPeriod-1 ; i++){
				averageByYear[i] /= (double)(this.totalPeriod-i-1); 
				averageByDuration[i] /= (double)(this.totalPeriod-i-1);			
			}
			
			//print survival portion with average
			if(printType == 0 || printType == 1){				
				System.out.println("Survival rates sorted by duration");
				for(i=0 ; i<this.totalPeriod-1 ; i++) System.out.print("\t"+(i+1));
				System.out.println();		
				for(i=0 ; i<this.totalPeriod-1 ; i++){
					System.out.print(this.startYear+i+"\t");
					for(j=0 ; j<this.totalPeriod-1-i ; j++) System.out.print(survivalPortion[i][j]+"\t");	
					for( ; j<this.totalPeriod-1 ; j++) System.out.print("\t");
					System.out.println();
				}
			}			
			if(printType == 0 || printType == 2){
				System.out.print("average");
				for(i=0 ; i<this.totalPeriod-1 ; i++) System.out.print("\t"+averageByDuration[i]);
				System.out.println();
			}
		}
	}
	
	
	public void printSurvivalRateByDurationByLocation(int printType){
		//print type 0: survival rates & averages by duration
		//print type 1: survival rates
		//print type 2: averages by duration
		
		int i,j,k,l,m,n,o;
		int[][] survivalNumber = new int[this.totalPeriod][this.totalPeriod];
		double[][] survivalRate = new double[this.totalPeriod-1][this.totalPeriod-1];
		double[] averageByYear = new double[this.totalPeriod-1];
		double[] averageByDuration = new double[this.totalPeriod-1];
			
		for(k=1 ; k<this.sortOfLocation ; k++){
			System.out.print("Location: "+this.locationName.get(k)+"\t");
			
			//calculate survival numbers
			for(i=0 ; i<this.totalPeriod ; i++){
				for(j=0 ; j<this.totalPeriod-i ; j++){	
					survivalNumber[i][j] = 0;				
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(m=0 ; m<this.sortOfScaleType ; m++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								for(o=0 ; o<this.sortOfStoreType ; o++)
									survivalNumber[i][j] += this.durationData[i][j][k][l][m][n][o];	
				}
			}		
		
			//calculate survival rates
			for(i=0 ; i<this.totalPeriod-1 ; i++){	
				for(j=0 ; j<this.totalPeriod-i-1 ; j++){
					averageByYear[i] = 0; 
					averageByDuration[j] = 0;
				}
			}					
			for(i=0 ; i<this.totalPeriod-1 ; i++){	
				for(j=0 ; j<this.totalPeriod-i-1 ; j++){	
					if(survivalNumber[i][j]==0) survivalRate[i][j] = 0; 
					else survivalRate[i][j] = (double)survivalNumber[i][j+1]/(double)survivalNumber[i][j];
					averageByYear[i] += survivalRate[i][j]; 
					averageByDuration[j] += survivalRate[i][j];					
				}
			}
			
			for(i=0 ; i<this.totalPeriod-1 ; i++){
				averageByYear[i] /= (double)(this.totalPeriod-i-1); 
				averageByDuration[i] /= (double)(this.totalPeriod-i-1);			
			}
			
			//print survival rates with average
			if(printType == 0 || printType == 1){				
				System.out.println("Survival rates sorted by duration");
				for(i=0 ; i<this.totalPeriod-1 ; i++) System.out.print("\t"+(i+1));
				System.out.println();		
				for(i=0 ; i<this.totalPeriod-1 ; i++){
					System.out.print(this.startYear+i+"\t");
					for(j=0 ; j<this.totalPeriod-1-i ; j++) System.out.print(survivalRate[i][j]+"\t");	
					for( ; j<this.totalPeriod-1 ; j++) System.out.print("\t");
					System.out.println();
				}
			}			
			if(printType == 0 || printType == 2){
				System.out.print("average");
				for(i=0 ; i<this.totalPeriod-1 ; i++) System.out.print("\t"+averageByDuration[i]);
				System.out.println();
			}
		}
	}
	
	public static void main(String[] args) {
		
		int startyear = 1998;
		int endyear = 2010;
		int period = endyear - startyear +1;
		int industry = 3;
		int scale = 5;
		int business = 5;
		int store = 3;
		
		String filePath = "/Users/jml/Desktop/Research/temp/company_code/";
		String companyTypeFile = "_company_type.txt";
		String companyTypeFile_location = "_company_type_locationAdded.txt";
		String locationCodeFile = "/Users/jml/Desktop/Research/temp/location_code/2008_location_codeList.txt"; 
		
		LocalDurationEstimator lde = new LocalDurationEstimator(startyear,endyear);
		
		System.out.print("process: reading location code...");	
		lde.readLocationCode(locationCodeFile);
		System.out.println("ok");
		System.out.print("process: initiating...");		
		lde.initiate(lde.locationCode, period, industry, scale, business, store);
		System.out.println("ok");
		System.out.print("process: data reading...");
		//lde.readCompanyData(filePath, companyTypeFile);
		lde.readCompanyData(filePath, companyTypeFile_location);
		System.out.println("ok");		
		System.out.print("process: calculation ...");
		lde.calculateDuration();
		System.out.println("ok");
		//System.out.println("********");
		//lde.printSurvivalData();
		System.out.println("process: complete");
		System.out.println("********");
		System.out.println("Result(s):");
		//lde.printSurvivalPortionByDurationByLocation(2);
		lde.printSurvivalRateByDurationByLocation(2);
		
		/*
		System.out.println("********");
		lde.printDevidedSurvivalData();
		System.out.println("********");
		lde.printSurvivalDataByIndustryByLocation();
		System.out.println("********");
		lde.printSurvivalDataByScaleByLocation();
		System.out.println("********");
		lde.printSurvivalDataByBusinessByLocation();
		System.out.println("********");
		lde.printSurvivalDataByStoreByLocation();
		*/
	}

}
