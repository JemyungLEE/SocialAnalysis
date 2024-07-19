package companyDuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class ArtificialNeuralNetwork {

	public ArtificialNeuralNetwork(){
		
	}
		
	public double generate_random_number(Random r){	       
	    		
		return (r.nextDouble() - 0.5);
	}
	
	public double SIG_function(double temp){
	       
		return 1.0/(1.0+Math.exp(-temp));
	}
	
	public void process(String[] argv) {
				
		int i, j, k;
	    
	    int ndata = 0;
	    int[] no;
	    double[] input, output, obs;    
	    
	    int nin = 0;	//number of input node
	    int nhid = 0;	//number of hide node
	    int nout = 0;	//number of output node
	    
		double rfMax=0, rfMin=0;
		double roMax=0, roMin=0;
	    
	    String inputfile = argv[1];
	    String outputfile = argv[2];
	    

	    try{
			File file = new File(inputfile);
			Scanner scan = new Scanner(file);
			
			//Read nin, nhid, nout, max, min
			scan.next();
			nin = scan.nextInt();
			scan.next();
			nhid = scan.nextInt();
			scan.next();
			nout = scan.nextInt();
			scan.next();
			rfMax = scan.nextDouble();
			scan.next();
			rfMin = scan.nextDouble();
			scan.next();
			roMax = scan.nextDouble();
			scan.next();
			roMin = scan.nextDouble();
			
			//Check input data integrity
			while(scan.hasNextLine()==true){
				i=scan.nextInt();
				if(i-ndata==1){
					scan.nextLine();
					ndata++;
				}
				else System.err.println("input file error");
			}
			scan.close();
	    } catch (Exception e) {
			System.out.println("read error");		
		}
	    
	    no = new int[ndata];
	    input = new double[ndata * nin];
	    output = new double[ndata * nout];
	    obs = new double[ndata * nout];		
	  

/*****************************************************************************/
/*                               Learning mode                               */
/*****************************************************************************/
	    if(argv[0].equals("LEM") == true){
            
	    	obs = new double[ndata*nout];
	    	double[] W1 = new double[(nin+1)*nhid];
	    	double[] W2 = new double[(nhid+1)*nout];
	    	double[] olddw1 = new double[(nin+1)*nhid];
	    	double[] olddw2 = new double[(nhid+1)*nout];
	    	double[] dw1 = new double[(nin+1)*nhid];
	    	double[] dw2 = new double[(nhid+1)*nout];

            // ETA: momentum rate
            // alpha: learning rate
            // AMAXE: Available maximum error
            double ETA = 0.95;
            double ALPHA = 0.7;
            double AMAXE = 0.001;
	    	
            //Read input data
	    	try{
    			File file = new File(inputfile);
    			Scanner scan = new Scanner(file);
 
    			
    			for(i=0 ; i<14 ; i++) scan.next();
    			
                for(i=0; i<ndata; i++){
                	no[i] = scan.nextInt();                         
                	for(j=0; j<nin; j++) input[i*nin+j]=scan.nextDouble();
                	for(j=0; j<nout; j++) obs[i*nout+j]=scan.nextDouble();
                }
    			scan.close();
    	    } catch (Exception e) {
    			System.out.println("data read error");		
    		}
            
// Initialization of weight W1(input-hidden later), W2(hidden-output layer)
//            double  DSEED = 47483647.;
            Random r = new Random();
            Date t = new Date();
            r.setSeed(t.getTime());
            for(i=0; i<=nin; i++){
            	for(j=0; j<nhid; j++){
            		W1[j+i*nhid] = generate_random_number(r);
            	}
            }
            for(i=0; i<=nhid; i++){
            	for(j=0; j<nout; j++){
            		W2[j+i*nout] = generate_random_number(r);
            	}
            }
// Initialization of delta weight olddw1, olddw2            
            for(i=0; i<=nin; i++){
            	for(j=0; j<nhid; j++){
            		olddw1[j+i*nhid] = 0.0;
            	}
            }
            for(i=0; i<=nhid; i++){
            	for(j=0; j<nout; j++){
            		olddw2[j+i*nout] = 0.0;
            	}
            }
// Choose an input-output pair
            int iter;
            double[] X = new double[nin+1];
            double[] Y = new double[nout];
            double[] H = new double[nhid+1];
            double[] EP = new double[nout];
            double anet, bnet;
            double[] delta1 = new double[nhid];
            double[] delta2 = new double[nout];
            double sumdw, sum_diff, total_err;
            
            iter = 0;
            do{
            	total_err = 0.0;  
            	for(i=0; i<ndata; i++){
            		X[0] = 0.0;
            		for(j=0; j<nin; j++){
            			X[j+1] = input[i*nin+j];
            		}
            		for(j=0; j<nout; j++){
            			Y[j] = obs[i*nout+j];
            		}

// Calculation of NET(P,J)
            		H[0] = 1.0;
            		for(j=0; j<nhid; j++){
            			anet = 0.0;
            			for(k=0; k<=nin ; k++){
            				anet += X[k] * W1[k+j*(nin+1)];
            			}
            			H[j+1] = SIG_function(anet);
            		}
// Calculation of NET(P,K)                     
            		for(j=0; j<nout; j++){
            			bnet = 0.0;
            			for(k=0; k<=nhid; k++){
            				bnet += H[k] * W2[k+j*(nhid+1)];
            			}
            			output[i*nout+j] = SIG_function(bnet);
            		}
// Calculation of adjustment of weight (hidden-output)
            		for(j=0; j<nout; j++){
            			delta2[j] = output[i*nout+j] * (1 - output[i*nout+j])
            								* (Y[j] - output[i*nout+j]);
            		}
            		for(j=0; j<nhid; j++){
            			sumdw = 0.0;
            			for(k=0; k<nout; k++){
            				sumdw += delta2[k] * W2[j+1+k*(nhid+1)];
            			}
            			delta1[j] = H[j+1] * (1 - H[j+1]) * sumdw;
            		}
// Weight adjustment (hidden - output)
            		for(j=0; j<nout; j++){
            			for(k=0; k<=nhid; k++){
            				dw2[k+j*(nhid+1)] = ETA * delta2[j] * H[k] + ALPHA 
            									* olddw2[k+j*(nhid+1)];
            			}
            		}
            		for(j=0; j<nhid; j++){
            			for(k=0; k<=nin; k++){
            				dw1[k+j*(nin+1)] = ETA * delta1[j] * X[k] + ALPHA
            									* olddw1[k+j*(nin+1)];
            			}
            		}
// Weight Adjustment (input-hidden)
            		for(j=0; j<nhid; j++){
            			for(k=0; k<=nin; k++){
            				olddw1[k+j*(nin+1)] = dw1[k+j*(nin+1)];
            				W1[k+j*(nin+1)] += dw1[k+j*(nin+1)];
            			}
            		}
            		for(j=0; j<nout; j++){
            			for(k=0; k<=nhid; k++){
            				olddw2[k+j*(nhid+1)] = dw2[k+j*(nhid+1)];
            				W2[k+j*(nhid+1)]+= dw2[k+j*(nhid+1)];
            			}
            		}
// Error calculation                     
            		sum_diff = 0.0;
            		for(j=0; j<nout; j++){
            			EP[j] = Math.abs(Y[j] - output[i*nout+j]);
            			sum_diff += Math.pow(EP[j], 2);
            		}
            		total_err += 0.5 * sum_diff;
            	}
            	iter++;

            	
            //}while(total_err > AMAXE && iter < 10000);
            	
            if((iter/100000.0) - (int)iter/100000 == 0.0) 
            	System.out.println(iter+" iters\ttotal_error:\t"+total_err);
            	
            
            //}while(iter < 100000);
            }while(total_err > 0.00001);
            System.out.println("iteration:"+iter+"\t error:"+total_err);
            
    		try{
    			File file = new File(argv[3]);
    			PrintWriter pw = new PrintWriter(file);
    			pw.print("nin: "+nin+"\tnhid: "+nhid+"\tnout: "+nout
    					    +"\tndata: "+ndata+"\titer: "+iter);
    			pw.printf("\ttotal_error: %.6f", total_err);
    			pw.println();
    			for(i=0; i<=nin; i++){
    				for(j=0; j<nhid; j++){
    					pw.println(W1[j+i*nhid]);
    				}
    			}
    			for(i=0; i<=nhid; i++){
    				for(j=0; j<nout; j++){
    					pw.println(W2[j+i*nout]);
    				}
    			}
    			System.out.println("weight file: "+argv[3]);
    			pw.close();
    		}catch(FileNotFoundException e) {
    			e.printStackTrace();
    		}    		
    	}    	

/*****************************************************************************/
/*                              Simulation mode                              */
/*****************************************************************************/
	    else if(argv[0].equals("SIM") == true){
	    	
	    	int iter;
	    	double total_err;          
    		double[] W1 = new double[(nin+1)*nhid];
    		double[] W2 = new double[(nhid+1)*nout];

    		String fwt = argv[3];
    		
    		try{
    			File file = new File(fwt);
    			Scanner scan = new Scanner(file);
    			
    			//Read nin, nhid, nout, max, min
    			scan.next();
    			nin = scan.nextInt();
    			scan.next();
    			nhid = scan.nextInt();
    			scan.next();
    			nout = scan.nextInt();
    			scan.next();
    			ndata = scan.nextInt();
    			scan.next();
    			iter = scan.nextInt();
    			scan.next();
    			total_err = scan.nextDouble();
    			
    			//Read weighting factor from learning result
    			for(i=0; i<=nin; i++)
    				for(j=0; j<nhid; j++)
    					W1[j+i*nhid] = scan.nextDouble();
    			for(i=0; i<=nhid; i++)
    				for(j=0; j<nout; j++)
    					W2[j+i*nout] = scan.nextDouble();
    			scan.close();
    		}catch(FileNotFoundException e) { 
    			e.printStackTrace(); 
    		}

     		//check the number of data
    	    try{
    			File file = new File(inputfile);
    			Scanner scan = new Scanner(file);
    			
    			ndata=0;
    			//Read nin, nhid, nout, max, min
    			scan.next();
    			nin = scan.nextInt();
    			scan.next();
    			nhid = scan.nextInt();
    			scan.next();
    			nout = scan.nextInt();
    			scan.next();
    			rfMax = scan.nextDouble();
    			scan.next();
    			rfMin = scan.nextDouble();
    			scan.next();
    			roMax = scan.nextDouble();
    			scan.next();
    			roMin = scan.nextDouble();
    			
    			//Check input data integrity
    			while(scan.hasNextLine()==true){
    				i=scan.nextInt();
    				if(i-ndata==1){
    					scan.nextLine();
    					ndata++;
    				}
    				else System.err.println("input file error");
    			}
    			scan.close();
    	    } catch (Exception e) {
    			System.out.println("read error");		
    		}
    		
    		//Read input data
    		try{
    			File file = new File(inputfile);
    			Scanner scan = new Scanner(file);
    			
    			//Read nin, nhid, nout, max, min
    			scan.next();
    			nin = scan.nextInt();
    			scan.next();
    			nhid = scan.nextInt();
    			scan.next();
    			nout = scan.nextInt();
    			scan.next();
    			rfMax = scan.nextDouble();
    			scan.next();
    			rfMin = scan.nextDouble();
    			scan.next();
    			roMax = scan.nextDouble();
    			scan.next();
    			roMin = scan.nextDouble();
    			
    			for(i=0; i<ndata; i++){
    				no[i] = scan.nextInt();

    				for(j=0; j<nin; j++){
    					input[i*nin+j] = scan.nextDouble();
    				}
    				
    				for(j=0; j<nout; j++){
    					obs[i*nout+j] = scan.nextDouble();
    					//System.out.println(""+obs[i*nout+j]);
    				}
    				
    			}
    			
    			scan.close();
    		}catch(FileNotFoundException e) { 
    			e.printStackTrace(); 
    		}
       
// Simulation
    		double[] X=new double[nin+1];
    		double[] Y=new double[nout];
    		double[] H=new double[nhid+1];
    		double anet, bnet;
          
    		try{
    			File file = new File(outputfile);
    			PrintWriter pw = new PrintWriter(file);
    			
    			pw.println("nin: "+nin+"\tnhid: "+nhid+"\tnout: "+nout
    													+"\tndata: "+ndata);
    			pw.println("Max_rainfall: "+rfMax+"\tMin_rainfall: "+rfMin);
    			pw.println("Max_runoff: "+roMax+"\tMin_runoff: "+roMin);
    			
				pw.println("No.\tOutput\tTarget");
    			for(i=0; i<ndata; i++){ 
    				//System.out.println("data no.: "+(i+1));
    				X[0] = 0.0;
    				for(j=0; j<nin; j++){
    					X[j+1] = input[i*nin+j];
    				}
    				for(j=0; j<nout; j++){
    					Y[j] = obs[i*nout+j];
    				}
                   
// Calculation of NET(P,J)
    				H[0] = 1.0;
    				for(j=0; j<nhid; j++){
    					anet = 0.0;
    					for(k=0; k<=nin; k++){
    						anet += X[k] * W1[k+j*(nin+1)];
    					}
    					H[j+1] = SIG_function(anet);
    				}
                  
    				
// Calculation of NET(P,K)    			

    				for(j=0; j<nout; j++){
    					bnet = 0.0;
    					for(k=0; k<=nhid; k++){
    						bnet += H[k] * W2[k+j*(nhid+1)];
    					}
    					output[i*nout+j] = SIG_function(bnet);
    					pw.printf("%5d\t%6.5f\t%7.5f", 
    							no[i], output[i*nout+j], Y[j]);
    					pw.println();
    				}
    				
    			}
    			pw.close();
    			System.out.println("simlation output: "+outputfile);
    		}catch(FileNotFoundException e) { 
    			e.printStackTrace(); 
    		}
    	}
	}      
	
	public static void main(String[] args){
		int nhid;
		ArtificialNeuralNetwork ann;
		
		String filePath = "/Users/jml/Desktop/Research/data_storage/company/ann/population/total_year/";
		String[] test = new String[3];
		
		test[0] = "20";
		test[1] = "40";
		test[2] = "60";
		
		String input_lem;
		String input_sim;
		String output;
		String weight;
		String[] files;
		
		for(int i=2 ; i<3 ; i++){
		
		//input_lem = "ann_input_pop_mig_lem_"+test[i]+".txt";
		//input_sim = "ann_input_pop_mig_sim_"+test[i]+".txt";
		//output = "result_pop_mig_"+test[i]+".txt";
		//weight = "weight_pop_mig_"+test[i]+".txt";
		
			

		input_lem = "input_lem_2011total_log_per_20_120_1.txt";
		input_sim = "input_sim_2011total_log_per_20_120_1.txt";
		output = "result_2011total_log_per_120.txt";
		weight = "weight_2011total_log_per_120.txt";

			
		files = new String[4];
		
		
		//for(nhid=19 ; nhid<20 ; nhid++){
			//input = "input_sim/input_"+(rainNum+runoffNum)+"_"+nhid+"_1.txt";
			//weight = "weight/weight_"+(rainNum+runoffNum)+"_"+nhid+"_1.txt";
			//output = "result/result_"+(rainNum+runoffNum)+"_"+nhid+"_1.txt";
									
			files[0] = "LEM";
			files[1] = filePath + input_lem;
			files[2] = filePath + output;
			files[3] = filePath + weight;
		
			ann = new ArtificialNeuralNetwork();
			ann.process(files);	
			
			
			files[0] = "SIM";
			files[1] = filePath + input_sim;
			files[2] = filePath + output;
			files[3] = filePath + weight;
		
			ann = new ArtificialNeuralNetwork();
			ann.process(files);
		//}
		}
		
		
		
		System.out.println("process complete");
	}

}
