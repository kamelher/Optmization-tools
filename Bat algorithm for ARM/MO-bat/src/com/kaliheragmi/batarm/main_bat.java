package com.kaliheragmi.batarm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class main_bat{
	public bat[] bats;
	public int nbBats;
	private ArrayList<ArrayList<Integer>> data = new ArrayList<>();
	double alpha, beta;
	int nbtransaction, nbgeneration, nbattribue;
	double rand = (double) 0.6;

	public main_bat(int nbtransaction, int nbattribue, double minsup,
			double minconf, int nbgeneration, int nbbats) {
		super();

		this.nbtransaction = nbtransaction;
		this.nbgeneration = nbgeneration;
		this.nbattribue = nbattribue;
		this.minsup = minsup;
		this.minconf = minconf;
		this.nbBats = nbbats;
		bats = new bat[nbBats];
	}

	public static ArrayList<Rule> accepted = new ArrayList<>();
	public static ArrayList<Double> fit = new ArrayList<Double>();

	double minsup;
	double minconf;
	private double w1;
	private double w2;
	private double w4;
	private double w3;

	private double o_sup = 0;
	private double o_conf = 0;
	private double o_int = 0;
	private double o_comp = 0;

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

	public double SupportItemset(int[] item) {
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

	private int[] union(int[] x, int[] y) {
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

	public double Confedance(Rule r) {
		// r.printRule();
		if (SupportItemset(r.lhs) == 0)
			return 0;
		return (double) (SupportItemset((union(r.lhs, r.rhs))) / SupportItemset(r.lhs));
	}

	public double comprihensibility(Rule r) {
		// r.printRule();
		// System.out.println("\n"+r.lhs_lenght());
		return (Math.log(1 + r.rhs_lenght()) / Math.log(1 + (r.lhs_lenght() + r
				.rhs_lenght())));
	}

	public double intersting(Rule r) {
		if (SupportItemset(r.lhs) == 0) {
			return 0;
		}
		double intr = (SupportItemset((union(r.lhs, r.rhs))) / SupportItemset(r.lhs));
		intr *= (double) (SupportItemset((union(r.lhs, r.rhs))) / SupportItemset(r.rhs));
		intr *= (double) (1 - (SupportItemset((union(r.lhs, r.rhs))) / this.nbtransaction));
		// System.out.print(": intr="+intr+"\n");
		return intr;
	}

	public double fitness(Rule r, int Npareto) {
		double fit = -1;
		r.splitrule();
		if (!r.testlhs() || !r.testrhs())
			return fit;
		double rsup = SupportItemset(union(r.lhs, r.rhs));
		double rcon = Confedance(r);
		//r.printRule();System.out.println("sup : "+rsup+" conf : "+rcon);
		double intr = intersting(r);
		double comp = comprihensibility(r);

		
		if ((rsup >= this.minsup) && (rcon >= this.minconf)) {
			fit = (double) ((w1 * rsup + w2 * rcon + w3 * comp + w4 * intr) / (w1
					+ w2 + w3 + w4));
//			fit = (double) ((w1 * rsup + w2 * rcon ) / (w1+ w2 ));
//			 o_sup+=rsup;
//			 o_conf+=rcon;
//			 o_int+=intr;
//			 o_comp+=comp;
//			 r.printRule(); System.out.println(" With fitness : "+fit);
//			 System.out.println(" rsup : "+rsup+" conf : "+rcon);
//			 accepted.add(r);
		}
		return fit;
	}

	private void init(int item) {
		for (int i = 0; i < bats.length; i++) {
			Rule r = new Rule(nbattribue, item);

			r.init();
			Random ran = new Random();
			int lower = 0;
			int higher = nbattribue + 1;
			int f = (int) (Math.random() * (higher - lower)) + lower;
			int v = (int) (Math.random() * (higher - lower)) + lower;
			bats[i] = new bat(r, r, f, v, Math.random(), Math.random(),
					fitness(r, 3));
			bats[i].Nbattribue = nbattribue;
			// bats[i].getSolution().printRule();System.out.println("Bat "+i+" F : "+f+" v : "+v);

		}

	}

	public void start(double randhom, int step, int item) {
		// TODO Auto-generated method stub
		double myfitness, Maxfit;
		init(item);
		int j = 0;
		Maxfit = 0;
	
		for (int k = 1; k < 10; k++) { // PARETO FRONT  = 10 POINTS
			j = 0;
			while (j < nbgeneration) {
				//System.out.println("iteration : "+j);
				for (int i = 0; i < bats.length; i++) {
					bats[i].frequency = (int) ((bats[i].Nbattribue - 1) * Math.random());
					bats[i].velocity = (int) bats[i].Nbattribue
							- bats[i].frequency - bats[i].velocity;
					if (bats[i].velocity < 0) {
						bats[i].velocity = 0;
					}

					bats[i].solution
							.genrate_new_sol(bats[i].frequency,
									bats[i].velocity, bats[i].A_loudness,
									Math.random(), step);
					// bats[i].solution.printRule();
					if (randhom > bats[i].rate) {
						int index = (int) bats[i].A_loudness
								* (bats[i].Nbattribue - 1);
						if (index <= 0) {
							index = 0;
						}
						bats[i].solution.genrate_new_sol(1, index,
								bats[i].A_loudness, randhom, step);
					}
					myfitness = fitness(bats[i].getSolution(), k);
					if (myfitness > bats[i].getFitness()) {
						if (myfitness > Maxfit) {
							Maxfit = myfitness;
						}
						bats[i].setFitness(myfitness);
						bats[i].setBest_solution(bats[i].getSolution());
						bats[i].decrase_A();
						bats[i].incrase_r(j);

						double rsup = SupportItemset(union(
								bats[i].getSolution().lhs,
								bats[i].getSolution().rhs));
						o_sup += rsup;
						double rcon = Confedance(bats[i].getSolution());
						o_conf += rcon;
						double intr = intersting(bats[i].getSolution());
						o_int += intr;
						double comp = comprihensibility(bats[i].getSolution());
						o_comp += comp;

						//System.out.print("Bat: " + i + "   :  ");
						//bats[i].getBest_solution().printRule();
						//System.out.println(" With fitness : "
						//		+ fitness(bats[i].getBest_solution(), k));
						//System.out
						//		.println(" rsup : " + rsup + " conf : " + rcon
						//				+ " inter : " + intr + " comp : "
						//				+ comp);
						accepted.add(bats[i].getSolution());

					}

				}
				for (int i = 0; i < bats.length; i++) {
					bats[i].setSolution(bats[i].getBest_solution());
				}
				// System.out.println("iteration : "+j);
				j++;
			}

		}
		
		//System.out.println("max fitness : " + Maxfit);
	}

	public void run() {
		// TODO Auto-generated method stub
		this.w1 = 0.4;
		this.w2 = 0.3;
		this.w3 = 0.2;
		this.w4 = 0.1;
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		//double startMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024d / 1024d;
		//System.out.println("Here we Go bats .....");
		//long lDateTime = System.currentTimeMillis();
		this.start(0.5, 1,40);
		//double endMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024d / 1024d;
		// System.out.println(" memory :" + (endMemory - startMemory));
		//System.out.println("Execution time ==== > "
				//+ (System.currentTimeMillis() - lDateTime) + " Millsecond "
				//+ (System.currentTimeMillis() - lDateTime) / 1000 + " second");
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
		double intersting =0;
		double m_confidence = 0;
		for (int i = 0; i < 1; i++) {
			main_bat b = new main_bat(2178,4, 0.0, 0.9, 1000, 50);
			//System.out.println("loading data \n====== ");
			b.data = b.loaddata("/home/kamel/Mycloud/WorkSpace/MO-bat/src/com/kaliheragmi/batarm/tr.txt");
			Collections.reverse(b.data);
			b.run();
			if(accepted.size()!=0){
			m+=b.o_comp/accepted.size();
			intersting+=b.o_int/accepted.size();
			m_confidence+=b.o_conf/accepted.size();
			}
			accepted.clear();
			fit.clear();
		}
		System.out.println("M_compr = "+m+" M_inter = "+ intersting+" M_Conf = "+ m_confidence);
	}	

}
