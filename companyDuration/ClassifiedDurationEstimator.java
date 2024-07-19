package companyDuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class ClassifiedDurationEstimator {
	
	int startYear, endYear;
	int totalPeriod;
	int sortOfLocationType;
	int sortOfIndustryType;
	int sortOfScaleType;
	int sortOfBusinessType;
	int sortOfStoreType;
	
	//[current year][founding year][location][industry][scale][business][store]
	int[][][][][][][] company, employee;
	int[][][][][][][] survivalData,employeeSurvivalData;
	int[][][][][][][] durationData, employeeDurationData;
	int[][][][][][][] closedData;
	double[][][][][][][] employeeAverageData;
	double[][][][][][][] survivalRatio, employeeSurvivalRatio;	// = survival(t)/survival(t-1), t: time step
	double[][][][][][][] remainRatio;											// = survival(t)/survival(0), t: time step
	double[][][][][][][] entropy, employeeEntropy;					
	
	public  ClassifiedDurationEstimator(){
		
	}

	public  ClassifiedDurationEstimator(int sYear, int eYear){
		this.startYear = sYear;
		this.endYear = eYear;
		this.totalPeriod = eYear - sYear + 1;
	}
	
	public void initiate(){
		this.variableInitiate();
	}
	
	public void initiate(int period, int location, int industry, int scale, int business, int store){
		this.totalPeriod = period;
		this.sortOfLocationType = location;
		this.sortOfIndustryType = industry;
		this.sortOfScaleType = scale;
		if(business <= 1) this.sortOfBusinessType = 1;
		else this.sortOfBusinessType = business;
		if(store <= 1) this.sortOfStoreType = 1;
		else this.sortOfStoreType = store;
		
		this.variableInitiate();
	}
	
	public void variableInitiate(){
		int i,j,k,l,m, n, o;
		this.company = new int[this.totalPeriod][this.totalPeriod][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.survivalData = new int[this.totalPeriod][this.totalPeriod][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.durationData = new int[this.totalPeriod][this.totalPeriod+1][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.closedData = new int[this.totalPeriod][this.totalPeriod-1][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.survivalRatio = new double[this.totalPeriod][this.totalPeriod-1][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.remainRatio = new double[this.totalPeriod][this.totalPeriod][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.entropy = new double[this.totalPeriod][this.totalPeriod-1][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		
		this.employee = new int[this.totalPeriod][this.totalPeriod][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.employeeSurvivalData = new int[this.totalPeriod][this.totalPeriod][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.employeeAverageData = new double[this.totalPeriod][this.totalPeriod][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.employeeDurationData = new int[this.totalPeriod][this.totalPeriod+1][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.employeeSurvivalRatio = new double[this.totalPeriod][this.totalPeriod-1][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];
		this.employeeEntropy = new double[this.totalPeriod][this.totalPeriod-1][this.sortOfLocationType]
				[this.sortOfIndustryType][this.sortOfScaleType][this.sortOfBusinessType][this.sortOfStoreType];

		
		for(k=0 ; k<this.sortOfLocationType ; k++){
			for(l=0 ; l<this.sortOfIndustryType ; l++){
				for(m=0 ; m<this.sortOfScaleType ; m++){
					for(n=0 ; n<this.sortOfBusinessType ; n++){	
						for(o=0 ; o<this.sortOfStoreType ; o++){							
							for(i=0 ; i<this.totalPeriod ; i++){
								for(j=0 ; j<this.totalPeriod ; j++){
									this.company[i][j][k][l][m][n][o] = 0;
									this.employee[i][j][k][l][m][n][o] = 0;
									this.survivalData[i][j][k][l][m][n][o] = 0;
									this.employeeSurvivalData[i][j][k][l][m][n][o] = 0;
									this.employeeAverageData[i][j][k][l][m][n][o] = 0;
									this.remainRatio[i][j][k][l][m][n][o] = 0.0;
								}
								
								for(j=0 ; j<this.totalPeriod+1 ; j++){
									this.durationData[i][j][k][l][m][n][o] = 0;
									this.employeeDurationData[i][j][k][l][m][n][o] = 0;
								}
		
								for(j=0 ; j<this.totalPeriod-1 ; j++){
									this.closedData[i][j][k][l][m][n][o] = 0;
									this.survivalRatio[i][j][k][l][m][n][o] = 0.0;
									this.employeeSurvivalRatio[i][j][k][l][m][n][o] = 0.0;
								}
							}	
						}
					}
				}	
			}	
		}
	}
	
	public void readCompanyData(String filepath, String filename){		
		int i, j;
		int tmpFyear, tmpLocationType, tmpScaleType, tmpIndustryType, tmpBusinessType, tmpStoreType;
				
		for(i = 0 ; i<this.totalPeriod ; i++){	
			try{
				File file = new File(filepath+(i+this.startYear)+filename);
				Scanner scan = new Scanner(file);
					
				scan.nextLine();
				while(scan.hasNext()){
					tmpFyear = scan.nextInt();
					tmpLocationType = scan.nextInt();
					tmpIndustryType = scan.nextInt();
					tmpScaleType = scan.nextInt();
					tmpBusinessType = scan.nextInt();
					tmpStoreType = scan.nextInt();
										
					if(tmpLocationType<0) tmpLocationType = 2;
					if(tmpIndustryType<0) tmpIndustryType = 0;
					if(tmpBusinessType<0) tmpBusinessType = 4;
					if(tmpStoreType<0) tmpStoreType = 0;
					
					for(j=0 ; j<=i ; j++)
						if(tmpFyear == j+this.startYear)
							this.company[i][j][tmpLocationType][tmpIndustryType][tmpScaleType]
														[tmpBusinessType][tmpStoreType]++;
				}
			
				scan.close();	
			} catch(IOException e) {}				
		}
	}
	
	public void analyzeCompanyMicroData(String filepath, int regionType, int industryClass, 
																	double[] scaleIntervals){		
		int i, j, k, m, n, o, p, q;
		int year;
		int dataSize;
		int tmpEmployees;
		int founded, location, scale, industry, business, store;
		int tmpLocation, tmpScale, tmpIndustry, tmpBusiness, tmpStore;
		String locationcodeFile, industrycodeFile, microdataFile;
		
		for(i = 0 ; i<this.totalPeriod ; i++){	
			year = i+this.startYear;
			System.out.print("\t"+year);
			
			locationcodeFile = filepath+"location_code/"+year+"_location_code.txt";
			industrycodeFile = filepath+"industry_code/"+year+"_industry_code.txt";
			microdataFile = filepath+"extracted/"+year+"_microdataCode.txt";

			CompanyClassifier cc = new CompanyClassifier(year, regionType, industryClass, scaleIntervals);
			cc.proceedClassificationProcess(locationcodeFile, industrycodeFile, microdataFile);

			dataSize = cc.getDataSize();
			
			for(j=0 ; j<dataSize ; j++){
				founded =cc.getCompanyData(j, 0);
				location = cc.getCompanyData(j, 1);
				industry = cc.getCompanyData(j, 2);
				scale = cc.getCompanyData(j, 3);
				tmpEmployees = cc.microdataList.get(j).getWorkers();
				if(this.sortOfBusinessType<=1) business = 0;
				else business = cc.getCompanyData(j, 4);
				if(this.sortOfStoreType<=1) store = 0; 
				else store = cc.getCompanyData(j, 5);
				
				if(location>=0 && industry>=0 && scale>=0){
					for(k=0 ; k<=i ; k++){
						if(founded == k+this.startYear){
							for(m=0; m<2 ; m++){
								if(m==0) tmpLocation = location;
								else tmpLocation = 0;
								for(n=0 ; n<2 ; n++){
									if(n==0) tmpIndustry = industry;
									else tmpIndustry = 0;
									for(o=0 ; o<2 ; o++){
										if(o==0) tmpScale = scale;
										else tmpScale = 0;
										
										if(this.sortOfBusinessType<=1 && this.sortOfStoreType<=1){
											this.company[i][k][tmpLocation][tmpIndustry][tmpScale][business][store]++;
											this.employee[i][k][tmpLocation][tmpIndustry][tmpScale][business][store]
													+= tmpEmployees;
										}else{					
											for(p=0 ; p<2 ; p++){
												if(p==0) tmpBusiness = business;
												else tmpBusiness = 0;
												for(q=0 ; q<2 ; q++){
													if(q==0) tmpStore = store;
													else tmpStore = 0;
										
													this.company[i][k][tmpLocation][tmpIndustry][tmpScale]
																					[tmpBusiness][tmpStore]++;
													this.employee[i][k][tmpLocation][tmpIndustry][tmpScale]
																					[tmpBusiness][tmpStore] += tmpEmployees;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
		
	public void calculateDuration(){
		int i,j,k,l,m,n,o;
				
		for(i=0 ; i<this.totalPeriod ; i++){
			for(j=0 ; j<this.totalPeriod ; j++){
				for(k=0 ; k<this.sortOfLocationType ; k++){
					for(l=0 ; l<this.sortOfIndustryType ; l++){
						for(m=0 ; m<this.sortOfScaleType ; m++){
							for(n=0 ; n<this.sortOfBusinessType ; n++){
								for(o=0 ; o<this.sortOfStoreType ; o++){
									this.survivalData[i][j][k][l][m][n][o] = this.company[j][i][k][l][m][n][o];
									this.employeeSurvivalData[i][j][k][l][m][n][o] = this.employee[j][i][k][l][m][n][o];
									if(this.company[j][i][k][l][m][n][o]>0) this.employeeAverageData[i][j][k][l][m][n][o] = 
									(double) this.employee[j][i][k][l][m][n][o] / (double) this.company[j][i][k][l][m][n][o];
								}
							}
						}
					}
				}
			}
		}
		
		for(k=0 ; k<this.sortOfLocationType ; k++){
			for(l=0 ; l<this.sortOfIndustryType ; l++){
				for(m=0 ; m<this.sortOfScaleType ; m++){
					for(n=0 ; n<this.sortOfBusinessType ; n++){	
						for(o=0 ; o<this.sortOfStoreType ; o++){
							for(i=0 ; i<this.totalPeriod ; i++){		
								this.durationData[i][0][k][l][m][n][o] = this.company[i][i][k][l][m][n][o];
								for(j=1 ; j<this.totalPeriod-i ; j++)	
									this.durationData[i][j][k][l][m][n][o]
											= this.company[i+j-1][i][k][l][m][n][o] - this.company[i+j][i][k][l][m][n][o];
								this.durationData[i][this.totalPeriod-i][k][l][m][n][o]
										= this.company[this.totalPeriod-1][i][k][l][m][n][o];	
								
								this.employeeDurationData[i][0][k][l][m][n][o] = this.employee[i][i][k][l][m][n][o];
								for(j=1 ; j<this.totalPeriod-i ; j++)	
									this.employeeDurationData[i][j][k][l][m][n][o]
											= this.employee[i+j-1][i][k][l][m][n][o] - this.employee[i+j][i][k][l][m][n][o];
								this.employeeDurationData[i][this.totalPeriod-i][k][l][m][n][o]
										= this.employee[this.totalPeriod-1][i][k][l][m][n][o];			
							}
						}
					}
				}
			}
		}
				
		for(k=0 ; k<this.sortOfLocationType ; k++){
			for(l=0 ; l<this.sortOfIndustryType ; l++){
				for(m=0 ; m<this.sortOfScaleType ; m++){
					for(n=0 ; n<this.sortOfBusinessType ; n++){	
						for(o=0 ; o<this.sortOfStoreType ; o++){
							for(i=0 ; i<this.totalPeriod ; i++){		
								for(j=i ; j<this.totalPeriod-1 ; j++){
									this.closedData[i][j][k][l][m][n][o]
											= this.survivalData[i][j][k][l][m][n][o] - this.survivalData[i][j+1][k][l][m][n][o];	
									this.survivalRatio[i][j][k][l][m][n][o]
											= (double) this.survivalData[i][j+1][k][l][m][n][o] 
													/ (double) this.survivalData[i][j][k][l][m][n][o];		
									this.employeeSurvivalRatio[i][j][k][l][m][n][o]
											= (double) this.employeeSurvivalData[i][j+1][k][l][m][n][o] 
													/ (double) this.employeeSurvivalData[i][j][k][l][m][n][o];		
								}
								for(j=i ; j<this.totalPeriod ; j++)
									this.remainRatio[i][j][k][l][m][n][o]
											= (double) this.survivalData[i][j][k][l][m][n][o]
												/ (double) this.survivalData[i][i][k][l][m][n][o];
							}
						}
					}					
				}
			}
		}	
	}
	
	public void printAllCompanyDurationData(String outputfile, String[] region, String[] industry, String[] scale){
		int i, j, k, l, m;
		
		try{
			File file = new File(outputfile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("::Survival rate by duration::");
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					for(m=0 ; m<this.sortOfScaleType ; m++){
						pw.println("Region: "+region[k]+"\tIndustry: "+industry[l]+"\tSize: "+scale[m]);
						pw.print("Estblished year");
						for(i=0 ; i<this.totalPeriod-1 ; i++) pw.print("\t"+(i+1));
						pw.println();
						for(i=0 ; i<this.totalPeriod ; i++){
							pw.print((this.startYear+i));
							for(j=i ; j<this.totalPeriod-1 ; j++) pw.print("\t"+this.survivalRatio[i][j][k][l][m][0][0]);
							for(j=0 ; j<i ; j++) pw.print("\t");
							pw.println();
						}
						pw.println();
					}
				}
			}
			
			pw.println("::Survival rate by year::");
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					for(m=0 ; m<this.sortOfScaleType ; m++){
						pw.println("Region: "+region[k]+"\tIndustry: "+industry[l]+"\tSize: "+scale[m]);
						pw.print("Estblished year");
						for(i=0 ; i<this.totalPeriod-1 ; i++) pw.print("\t"+(this.startYear+i));
						pw.println();
						for(i=0 ; i<this.totalPeriod ; i++){
							pw.print((this.startYear+i));
							for(j=0 ; j<i ; j++) pw.print("\t");
							for(j=i ; j<this.totalPeriod-1 ; j++) pw.print("\t"+this.survivalRatio[i][j][k][l][m][0][0]);
							pw.println();
						}
						pw.println();
					}
				}
			}
			
			pw.println("::Companies by duration::");
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					for(m=0 ; m<this.sortOfScaleType ; m++){
						pw.println("Region: "+region[k]+"\tIndustry: "+industry[l]+"\tSize: "+scale[m]);
						pw.print("Estblished year");
						for(i=0 ; i<this.totalPeriod+1 ; i++) pw.print("\t"+i);
						pw.println();
						for(i=0 ; i<this.totalPeriod ; i++){
							pw.print((this.startYear+i));
							for(j=0 ; j<this.totalPeriod+1-i ; j++) pw.print("\t"+this.durationData[i][j][k][l][m][0][0]);
							for(j=0 ; j<i ; j++) pw.print("\t");
							pw.println();
						}
						pw.println();
					}
				}
			}
			
			pw.println("::Survival companies by year::");
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					for(m=0 ; m<this.sortOfScaleType ; m++){
						pw.println("Region: "+region[k]+"\tIndustry: "+industry[l]+"\tSize: "+scale[m]);
						pw.print("Estblished year");
						for(i=0 ; i<this.totalPeriod ; i++) pw.print("\t"+(this.startYear+i));
						pw.println();
						for(i=0 ; i<this.totalPeriod ; i++){
							pw.print((this.startYear+i));
							for(j=0 ; j<i ; j++) pw.print("\t");
							for(j=i ; j<this.totalPeriod ; j++) pw.print("\t"+this.survivalData[i][j][k][l][m][0][0]);
							pw.println();
						}
						pw.println();
					}
				}
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printAllEmployeeDurationData(String outputfile, String[] region, String[] industry, String[] scale){
		int i, j, k, l, m;
		
		try{
			File file = new File(outputfile);
			PrintWriter pw = new PrintWriter(file);
			
			pw.println("::Survival rate by duration::");
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					for(m=0 ; m<this.sortOfScaleType ; m++){
						pw.println("Region: "+region[k]+"\tIndustry: "+industry[l]+"\tSize: "+scale[m]);
						pw.print("Estblished year");
						for(i=0 ; i<this.totalPeriod-1 ; i++) pw.print("\t"+(i+1));
						pw.println();
						for(i=0 ; i<this.totalPeriod ; i++){
							pw.print((this.startYear+i));
							for(j=i ; j<this.totalPeriod-1 ; j++) pw.print("\t"+this.employeeSurvivalRatio[i][j][k][l][m][0][0]);
							for(j=0 ; j<i ; j++) pw.print("\t");
							pw.println();
						}
						pw.println();
					}
				}
			}
			
			pw.println("::Survival rate by year::");
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					for(m=0 ; m<this.sortOfScaleType ; m++){
						pw.println("Region: "+region[k]+"\tIndustry: "+industry[l]+"\tSize: "+scale[m]);
						pw.print("Estblished year");
						for(i=0 ; i<this.totalPeriod-1 ; i++) pw.print("\t"+(this.startYear+i));
						pw.println();
						for(i=0 ; i<this.totalPeriod ; i++){
							pw.print((this.startYear+i));
							for(j=0 ; j<i ; j++) pw.print("\t");
							for(j=i ; j<this.totalPeriod-1 ; j++) pw.print("\t"+this.employeeSurvivalRatio[i][j][k][l][m][0][0]);
							pw.println();
						}
						pw.println();
					}
				}
			}
			
			pw.println("::Average employees by duration::");
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					for(m=0 ; m<this.sortOfScaleType ; m++){
						pw.println("Region: "+region[k]+"\tIndustry: "+industry[l]+"\tSize: "+scale[m]);
						pw.print("Estblished year");
						for(i=0 ; i<this.totalPeriod ; i++) pw.print("\t"+i);
						pw.println();
						for(i=0 ; i<this.totalPeriod ; i++){
							pw.print((this.startYear+i));
							for(j=i ; j<this.totalPeriod ; j++) pw.print("\t"+this.employeeAverageData[i][j][k][l][m][0][0]);
							for(j=0 ; j<i ; j++) pw.print("\t");
							pw.println();
						}
						pw.println();
					}
				}
			}
			
			pw.println("::Average employees by year::");
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					for(m=0 ; m<this.sortOfScaleType ; m++){
						pw.println("Region: "+region[k]+"\tIndustry: "+industry[l]+"\tSize: "+scale[m]);
						pw.print("Estblished year");
						for(i=0 ; i<this.totalPeriod ; i++) pw.print("\t"+(this.startYear+i));
						pw.println();
						for(i=0 ; i<this.totalPeriod ; i++){
							pw.print((this.startYear+i));
							for(j=0 ; j<i ; j++) pw.print("\t");
							for(j=i ; j<this.totalPeriod ; j++) pw.print("\t"+this.employeeAverageData[i][j][k][l][m][0][0]);
							pw.println();
						}
						pw.println();
					}
				}
			}
			
			pw.println("::Employees by duration::");
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					for(m=0 ; m<this.sortOfScaleType ; m++){
						pw.println("Region: "+region[k]+"\tIndustry: "+industry[l]+"\tSize: "+scale[m]);
						pw.print("Estblished year");
						for(i=0 ; i<this.totalPeriod+1 ; i++) pw.print("\t"+i);
						pw.println();
						for(i=0 ; i<this.totalPeriod ; i++){
							pw.print((this.startYear+i));
							for(j=0 ; j<this.totalPeriod+1-i ; j++) pw.print("\t"+this.employeeDurationData[i][j][k][l][m][0][0]);
							for(j=0 ; j<i ; j++) pw.print("\t");
							pw.println();
						}
						pw.println();
					}
				}
			}
			
			pw.println("::Survival employees by year::");
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					for(m=0 ; m<this.sortOfScaleType ; m++){
						pw.println("Region: "+region[k]+"\tIndustry: "+industry[l]+"\tSize: "+scale[m]);
						pw.print("Estblished year");
						for(i=0 ; i<this.totalPeriod ; i++) pw.print("\t"+(this.startYear+i));
						pw.println();
						for(i=0 ; i<this.totalPeriod ; i++){
							pw.print((this.startYear+i));
							for(j=0 ; j<i ; j++) pw.print("\t");
							for(j=i ; j<this.totalPeriod ; j++) pw.print("\t"+this.employeeSurvivalData[i][j][k][l][m][0][0]);
							pw.println();
						}
						pw.println();
					}
				}
			}
			
			pw.close();
		}catch(IOException e) {}
	}
	
	public void printCompanyData(){
		int i,j,k,l,m,n,o;
		int tmpCompany;
		
		for(i=0 ; i<this.totalPeriod ; i++){
			System.out.print(this.startYear+i+"\t");
			for(j=0 ; j<=i ; j++){
				tmpCompany = 0;
				for(k=0 ; k<this.sortOfLocationType ; k++)
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(m=0 ; m<this.sortOfScaleType ; m++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								for(o=0 ; o<this.sortOfStoreType ; o++)
									tmpCompany += this.company[i][j][k][l][m][n][o];
				System.out.print(tmpCompany+"\t");
			}
			System.out.println();
		}			
	}
	
	public void printStartupCompanyStatistics(){
		int i,j,k,l,m,n,o;
		int[][] total;
		int[][][] scale, business, store;
		
		System.out.println();
		System.out.print("Year\tNation");
		for(l=0 ; l<this.sortOfIndustryType ; l++){
			System.out.print("\t"+l);
			for(m=0 ; m<this.sortOfScaleType ; m++) System.out.print("\tscale_"+(m+1));
			for(n=0 ; n<this.sortOfBusinessType ; n++) System.out.print("\tbusiness_"+(n+1));
			for(o=0 ; o<this.sortOfStoreType ; o++) System.out.print("\tstore_"+(o+1));
		}
		System.out.print("\tUrban");
		for(l=0 ; l<this.sortOfIndustryType ; l++){
			System.out.print("\t"+l);
			for(m=0 ; m<this.sortOfScaleType ; m++) System.out.print("\tscale_"+(m+1));
			for(n=0 ; n<this.sortOfBusinessType ; n++) System.out.print("\tbusiness_"+(n+1));
			for(o=0 ; o<this.sortOfStoreType ; o++) System.out.print("\tstore_"+(o+1));
		}
		System.out.print("\tRural");
		for(l=0 ; l<this.sortOfIndustryType ; l++){
			System.out.print("\t"+l);
			for(m=0 ; m<this.sortOfScaleType ; m++) System.out.print("\tscale_"+(m+1));
			for(n=0 ; n<this.sortOfBusinessType ; n++) System.out.print("\tbusiness_"+(n+1));
			for(o=0 ; o<this.sortOfStoreType ; o++) System.out.print("\tstore_"+(o+1));
		}
		System.out.println();
		
		for(i=0 ; i<this.totalPeriod ; i++){
			System.out.print(this.startYear+i);

			total =  new int[this.sortOfLocationType][this.sortOfIndustryType];
			scale = new int[this.sortOfLocationType][this.sortOfIndustryType][this.sortOfScaleType];
			business = new int[this.sortOfLocationType][this.sortOfIndustryType][this.sortOfBusinessType];
			store = new int[this.sortOfLocationType][this.sortOfIndustryType][this.sortOfStoreType];
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					for(m=0 ; m<this.sortOfScaleType ; m++){
						for(n=0 ; n<this.sortOfBusinessType ; n++){
							for(o=0 ; o<this.sortOfStoreType ; o++){
								total[k][l] += this.company[i][i][k][l][m][n][o];
								scale[k][l][m] +=  this.company[i][i][k][l][m][n][o];
								business[k][l][n] +=  this.company[i][i][k][l][m][n][o];
								store[k][l][o] +=  this.company[i][i][k][l][m][n][o];
							}
						}
					}
				}
			}
			System.out.print("\t");
			for(k=0 ; k<this.sortOfLocationType ; k++){
				for(l=0 ; l<this.sortOfIndustryType ; l++){
					System.out.print("\t"+total[k][l]);
					for(m=0 ; m<this.sortOfScaleType ; m++) System.out.print("\t"+scale[k][l][m]);
					for(n=0 ; n<this.sortOfBusinessType ; n++) System.out.print("\t"+business[k][l][n]);
					for(o=0 ; o<this.sortOfStoreType ; o++) System.out.print("\t"+store[k][l][o]);
				}
			}
			System.out.println();	
		}
	}
	
	public void printSurvivalData(){
		int i,j,k,l,m,n,o;
		int tmpSurvivalData;
		
		System.out.println("Survival Data:");
		for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+(this.startYear+i));
		System.out.println();
		for(i=0 ; i<this.totalPeriod ; i++){
			System.out.print((this.startYear+i)+"\t");
			for(j=0 ; j<i ; j++) System.out.print("\t");
			for(j=i ; j<this.totalPeriod ; j++){
				tmpSurvivalData = 0;
				for(k=0 ; k<this.sortOfLocationType ; k++)
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
				for(k=1 ; k<this.sortOfLocationType ; k++)
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
					for(k=1 ; k<this.sortOfLocationType ; k++)
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
					for(k=1 ; k<this.sortOfLocationType ; k++)
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
					for(k=1 ; k<this.sortOfLocationType ; k++)
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
					for(k=1 ; k<this.sortOfLocationType ; k++)
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
	
	public void printDuration(){
		int i,j,k,l,m,n,o;
		int[][] survivalNumber = new int[this.totalPeriod][this.totalPeriod];
		double[][] survivalRate = new double[this.totalPeriod-1][this.totalPeriod-1];
		double[] averageByYear = new double[this.totalPeriod-1];
		double[] averageByDuration = new double[this.totalPeriod-1];
				
		System.out.println("Duration Data");
		for(i=0 ; i<this.totalPeriod ; i++) System.out.print("\t"+i);
		System.out.println();
		for(i=0 ; i<this.totalPeriod ; i++){
			System.out.print(this.startYear+i+"\t");
			for(j=0 ; j<this.totalPeriod-i ; j++){	
				survivalNumber[i][j] = 0;
				for(k=0 ; k<this.sortOfLocationType ; k++)
					for(l=0 ; l<this.sortOfIndustryType ; l++)
						for(m=0 ; m<this.sortOfScaleType ; m++)
							for(n=0 ; n<this.sortOfBusinessType ; n++)	
								for(o=0 ; o<this.sortOfStoreType ; o++)
									survivalNumber[i][j] += this.durationData[i][j][k][l][m][n][o];				
				System.out.print(survivalNumber[i][j]+"\t");
			}
			for(j=0 ; j<i ; j++) System.out.print("\t");
			System.out.println();
		}
		
		//calculate survival rate
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
		
		//print survival rate with average
		System.out.println();
		System.out.println("Survival rate sorted by duration");
		for(i=0 ; i<this.totalPeriod-1 ; i++) System.out.print("\t"+(i+1));
		System.out.println();		
		for(i=0 ; i<this.totalPeriod-1 ; i++){
			System.out.print(this.startYear+i+"\t");
			for(j=0 ; j<this.totalPeriod-1 ; j++) System.out.print(survivalRate[i][j]+"\t");			
			System.out.println();
		}
		System.out.print("Average\t");
		for(i=0 ; i<this.totalPeriod-1 ; i++) System.out.print(averageByDuration[i]+"\t");
	}
	
	public static void main(String[] args) {
		
		int startyear = 1998;
		int endyear = 2012;
		int period = endyear - startyear +1;
		
		String[] regionType = {"all", "urban", "rural"};
		String[] industryType = {"all", "primary", "secondary", "tertiary"};
		String[] scaleType = {"all", "small", "middle", "large", "huge"};
		String[] businessType = {"all", "personal", "corporation", "foundation", "organization", "government"};
		String[] storeType = {"all", "sole", "head", "branch"};
		double[] scaleInterval = {0.0, 6.0, 30.0, 300.0, 30000.0};
		
		int location = regionType.length;
		int industry = industryType.length;
		int scale = scaleType.length;
		int business = businessType.length;
		int store = storeType.length;
//		int business = 1;
//		int store = 1;
		
		int regionClass = location;
		int industryClass = industry;
		int scaleIntervals = scale;
		
		String filePath ="/Users/Jemyung/Desktop/Research/data_storage/company/";
		String companyFile = filePath + "survival_rate/company_survival_rate.txt";
		String employeeFile = filePath + "survival_rate/employee_survival_rate.txt";
		
		ClassifiedDurationEstimator cde = new ClassifiedDurationEstimator(startyear, endyear);
		
		System.out.print("process: initiating...");
		cde.initiate(period, location, industry, scale, business, store);
		System.out.println("ok");
		System.out.print("process: data reading...");
		cde.analyzeCompanyMicroData(filePath, regionClass, industryClass, scaleInterval);
		//cde.readCompanyData(filePath, companyTypeFile);
		System.out.println("ok");		
		System.out.print("process: calculation ...");
	//	cde.calculateDuration();
		System.out.println("ok");
		System.out.print("process: printing results ...");
	//	cde.sortOfScaleType = 1;
	//	cde.printAllCompanyDurationData(companyFile, regionType, industryType, scaleType);
	//	cde.printAllEmployeeDurationData(employeeFile, regionType, industryType, scaleType);
		cde.printStartupCompanyStatistics();
		System.out.println("ok");
		
		//System.out.println("********");
		//cde.printSurvivalData();
		//System.out.println("********");
		//cde.printDuration();
		/*
		System.out.println("********");
		cde.printDevidedSurvivalData();
		System.out.println("********");
		cde.printSurvivalDataByIndustryByLocation();
		System.out.println("********");
		cde.printSurvivalDataByScaleByLocation();
		System.out.println("********");
		cde.printSurvivalDataByBusinessByLocation();
		System.out.println("********");
		cde.printSurvivalDataByStoreByLocation();
		*/
		System.out.println("process: complete");

	}

}
