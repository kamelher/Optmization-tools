package com.kaliheragmi.batarm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class main_bat {
	public bat[] bats;
	public int nbpop;
	public population[] Spop;
	public LinkedList<bat> M_pop ;
	private static ArrayList<ArrayList<Integer>> data = new ArrayList<>();
	double alpha, beta;
	static int nbtransaction;
	int nbgeneration;
	int nbattribue;
	double rand = (double) 0.7;
	public static double Maxfit = 0;
	public main_bat(int nbtransaction,int item, int nbattribue, double minsup,double minconf, int nbgeneration, int pop, int nbbats) {
		this.nbtransaction = nbtransaction;
		this.nbgeneration = nbgeneration;
		this.nbattribue = nbattribue;
		this.minsup = minsup;
		this.minconf = minconf;
		this.nbpop = pop;
		Spop = new population[nbpop];
		for (int i = 0; i < Spop.length; i++) {
			Spop[i] = new population(nbattribue, item, nbbats);
		}
		M_pop = new LinkedList<bat>();
	}

	public static ArrayList<Rule> accepted = new ArrayList<>();
	public static ArrayList<Double> fit = new ArrayList<Double>();

	static double minsup;
	static double minconf;
	private static double w1;
	private static double w2;

	private static double o_sup = 0;
	private static double o_conf = 0;


	public static <T> ArrayList<T> intersection(ArrayList<T> setA,
			ArrayList<T> setB) {
		ArrayList<T> tmp = new ArrayList<T>();
		if (setA == null) {
			return setB;
		}
		if (setB == null) {
			return setA;
		}
		for (T x : setA)
			if (setB.contains(x))
				tmp.add(x);

		return tmp;
	}

	public ArrayList<ArrayList<Integer>> loaddata(String link) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					link)));
			ArrayList<Integer> a;
			String line;
			String[] tr;
			while ((line = reader.readLine()) != null) {
				tr = line.split(" ");
				a = new ArrayList<>();
				for (int i = 0; i < tr.length; i++) {
					a.add(Integer.valueOf(tr[i]));
				}
				result.add(a);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.getStackTrace();
		}
		return result;
	}

	private double Support(int item) {
		return (data.get(new Integer(item - 1)).size() / (double) this.nbtransaction);
	}

	public static double SupportItemset(int[] item) {
		ArrayList<Integer> a = null;
		double resu = 0;
		for (int i = 0; i < item.length; i++) {
			if (item[i] != 0) {
				a = data.get(new Integer(item[i] - 1));
				break;
			}
		}

		for (int i = 0; i < item.length; i++) {
			if (item[i] != 0) {
				a = intersection(a, data.get(new Integer(item[i] - 1)));
			}
			if (a == null) {
				resu = 0;
			} else {
				resu = (double) a.size() / (double) nbtransaction;
			}
		}

		return resu;
	}

	private static int[] union(int[] x, int[] y) {
		int[] a = new int[x.length + y.length];
		for (int i = 0; i < x.length; i++) {
			a[i] = x[i];
		}
		for (int i = 0; i < y.length; i++) {
			a[x.length + i] = y[i];
		}
		//
		return a;
	}

	public static double Confedance(Rule r) {
		// r.printRule();
		if (SupportItemset(r.lhs) == 0)
			return 0;
		return (double) (SupportItemset((union(r.lhs, r.rhs))) / SupportItemset(r.lhs));
	}



	public static double fitness(Rule r) {
		double fit = -1;
		r.splitrule();
		if (!r.testlhs() || !r.testrhs())
			return fit;
		double rsup = SupportItemset(union(r.lhs, r.rhs));
		double rcon = Confedance(r);

		if ((rsup > main_bat.minsup) && (rcon > main_bat.minconf)) {
			fit = (double) ((w1 * rsup + w2 * rcon));
			//fit = (double) ((w1 * rsup + w2 * rcon ) / (w1+ w2 ));
			 o_sup+=rsup;
			 o_conf+=rcon;
			 if (fit>Maxfit) {
				Maxfit = fit;
				//System.out.println(" With fitness : "+fit);

			}
			 //r.printRule(); System.out.println(" With fitness : "+fit);
			 //System.out.println(" rsup : "+rsup+" conf : "+rcon);
			 accepted.add(r);
		}
		return fit;
	}



	public void start(double randhom, int step, int item) {
		// TODO Auto-generated method stub
		double myfitness;
		int predefined = 20;
		int nb_generation = 0;
		for (int i = 0; i < Spop.length; i++) {
			Spop[i].init(item);
		}
		int j = 0;
		System.out.println("Here we Go bats .....");
		long lDateTime = System.currentTimeMillis();
			j = 0;
			while (j < nbgeneration) {
				nb_generation++;
				for (int i = 0; i < Spop.length; i++) {
					Spop[i].generate();
				}				
				
				if(nb_generation > predefined){					
					nb_generation =0;
					for (int i = 0; i < Spop.length; i++) {
						Spop[i].localbest = Spop[(i+1)%Spop.length].localbest;
						//System.out.println(Spop[i].localbest);
						for (int k = 0; k < Spop[i].bats.length; k++) {
							try {
								//Spop[i].localbest.solution.printRule();
								Spop[i].bats[k].solution = Spop[i].localbest.solution;
							} catch (Exception e) {
								// TODO: handle exception
							}						
						}
					}					
				}
				for (int i = 0; i < Spop.length; i++) {
					M_pop.add(Spop[i].getbest());
				}
				for (int i = 0; i < M_pop.size(); i++) {
					try{
					M_pop.get(i).frequency = (int)((M_pop.get(i).Nbattribue-1)*Math.random()); 
					M_pop.get(i).velocity = (int)M_pop.get(i).Nbattribue-bats[i].frequency-bats[i].velocity;
					if (M_pop.get(i).velocity<0) {
						M_pop.get(i).velocity=0;
					}
					
					M_pop.get(i).solution.genrate_new_sol(M_pop.get(i).frequency ,M_pop.get(i).velocity,M_pop.get(i).A_loudness, randhom,step);
						//bats[i].solution.printRule();
					if (randhom>M_pop.get(i).rate) {
						int index = (int)M_pop.get(i).A_loudness*(M_pop.get(i).Nbattribue-1);
						if (index<=0) {
							index=0;
						}				
						M_pop.get(i).solution.genrate_new_sol(1,index, M_pop.get(i).A_loudness,randhom,step);
					}				
					myfitness = fitness(M_pop.get(i).getSolution()); 
					
					if (myfitness>M_pop.get(i).getFitness()) {
						//System.out.println("Loudness : "+bats[i].getA_loudness());
						//System.out.println("rate : "+bats[i].getRate());
						if (myfitness>Maxfit) {
							Maxfit = myfitness;
							
						}
						M_pop.get(i).setFitness(myfitness);				
						M_pop.get(i).setBest_solution(bats[i].getSolution());				
						M_pop.get(i).decrase_A();
						M_pop.get(i).incrase_r();
						//System.out.print("Bat: "+i +"   :  ");bats[i].getBest_solution().printRule(); System.out.println(" With fitness : "+fitness(bats[i].getBest_solution()));
						double rsup = SupportItemset(union(M_pop.get(i).getSolution().lhs,M_pop.get(i).getSolution().rhs));	
						
						double rcon = Confedance(M_pop.get(i).getSolution());
					
						//System.out.println(" rsup : "+rsup+"conf : "+rcon+" fit "+Maxfit);
						//				accepted.add(bats[i].getSolution());
//						main_bat.fit.add(myfitness);				
						}
					}catch(Exception e){}
				}
				//System.out.println(j);
				//M_pop.clear();
				j++;
			}

		System.out.println("Execution time ==== > "
				+ (System.currentTimeMillis() - lDateTime) + " Millsecond "
				+ (System.currentTimeMillis() - lDateTime) / 1000 + " second");
		System.out.println("max fitness : " + Maxfit);
System.out.println("overage confidance : " +  o_conf/accepted.size());
		System.out.println("overage supporte : " + o_sup/accepted.size());
	}

	public void start_Mp() {
		// TODO Auto-generated method stub
		this.w1 = 0.5;
		this.w2 = 0.5;
		this.start(0.5, 1,40);

	}

	public void printdata() {
		for (ArrayList<Integer> in : data) {
			for (Integer integer : in) {
				System.out.print(integer + " ");
			}
			System.out.println();
		}
	}

	public double overage_fitness() {
		double overage = 0;
		for (int i = 0; i < accepted.size(); i++) {
			overage += fit.get(i);
		}
		overage = overage / accepted.size();
		return overage;
	}

	public void printaccepted() {
		for (int i = 0; i < accepted.size(); i++) {
			accepted.get(i).splitrule();
			accepted.get(i).printRule();
			// System.out.println(" with fitness :"+fitness(accepted.get(i)));
		}
	}

	public static void main(String args[]) {
		double m =0;
		double m_size = 0;
		for (int i = 0; i < 10; i++) {

			//basketball
			//main_bat b = new main_bat(96,40, 5, 0.2, 0.01, 1000, 10,5);
//BF
			//main_bat b = new main_bat(252,40, 15, 0.2, 0.01, 1000, 10,5);
			//Quak Qk.txt
			 main_bat b = new main_bat(2178,40, 4, 0.01, 0.7, 1000, 10,5);
			//ibm-standard.txt
		     //main_bat b = new main_bat(1000,40, 20, 0.2, 0.5, 1000, 10,5);
			//chess
			//main_bat b = new main_bat(3196,75, 37, 0.2, 0.5, 200, 10,5);
			//mashroom
			//main_bat b = new main_bat(8124,119, 23, 0.0, 0.01, 200, 10,5);
			//pumsb
			 //main_bat b = new main_bat(40385,7116, 50, 0.0, 0.0, 100, 10,5);
			//BMS1.txt
			//main_bat b = new main_bat(59602,497, 125, 0.0, 0.0, 200, 10,5);
			//retrail.txt
			//main_bat b = new main_bat(88162,16469, 50, 0.0, 0.0, 200, 10,5);
			//connect
			//main_bat b = new main_bat(100000,999, 10, 0.0, 0.0, 200, 10,5);

			System.out.println("loading data \n====== Hybrid");
			b.data = b.loaddata("/home/kamel/tr.txt");
			Collections.reverse(b.data);
			b.start_Mp();
			m+=Maxfit;
			m_size+=accepted.size();
			Maxfit=0;
			o_conf=0;
			o_sup=0;
			//System.out.println(b.o_sup / accepted.size() + " " + b.o_conf/ accepted.size());
			System.out.println("Accepted : "+ accepted.size());
			accepted.clear();
			fit.clear();
			System.out.println(" ==================================================================================================================== ");

		}
		System.out.println("moyen = "+m/10);
		System.out.println("accepted = "+m_size/10);

	}

}
