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
	public int nbBats;
	private ArrayList<ArrayList<Integer>> data = new ArrayList<>();
	double alpha, beta;
	int nbtransaction,nbgeneration,nbattribue;
	double rand = (double) 0.7;
	public main_bat(double alpha, double beta, int nbtransaction,
		 int nbattribue, double minsup,
		double minconf,int nbgeneration, int nbbats) {
		super();
		this.alpha = alpha;
		this.beta = beta;
		this.nbtransaction = nbtransaction;
		this.nbgeneration = nbgeneration;
		this.nbattribue = nbattribue;	
		this.minsup = minsup;
		this.minconf = minconf;
		this.nbBats=nbbats;
		bats = new bat[nbBats];
	}
	public static ArrayList<Rule> accepted = new ArrayList<>();
	public static ArrayList<Double> fit = new ArrayList<Double>();
	
	double minsup;
	double minconf;
	private double o_sup = 0;
	private double o_conf = 0;
	
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
		return (data.get(new Integer(item-1)).size()/(double)this.nbtransaction);
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
				resu = (double)a.size() / (double)nbtransaction;
			}
		}
			
		return resu;
	}

	private int[] union(int []x, int []y) {
		int[] a = new int[x.length+y.length];
		for (int i = 0; i < x.length; i++) {
			a[i]=x[i];
		}
		for (int i = 0; i < y.length; i++) {
			a[x.length+i]=y[i];
		}
		//
		return a;
	}

	public double Confedance(Rule r) {
		//r.printRule();
		if(SupportItemset(r.lhs)==0)return 0;
		return (double) (SupportItemset((union(r.lhs,r.rhs))) / SupportItemset(r.lhs));
	}
	
	
	public double fitness(Rule r) {
		double fit = -1;
		double rsup = SupportItemset(union(r.lhs,r.rhs));	
		
		double rcon = Confedance(r);
		//r.printRule();System.out.println("sup : "+rsup+" conf : "+rcon);
		r.splitrule();
		if (r.testlhs()&&(r.testrhs())) {
		if ((rsup >= this.minsup) && (rcon >= this.minconf)) {
			fit = (double) ((beta * rsup + alpha * rcon)/(alpha+beta) );
			o_sup += rsup;
			o_conf += rcon;
			accepted.add(r);
			main_bat.fit.add(fit);
			//System.err.println(" rsup : "+rsup+" lhs = "+SupportItemset(r.lhs)+" conf : "+rcon+" fit "+fit);
		}
		}
	
		return fit;
	}
	private void init(){
		for (int i = 0; i < bats.length; i++) {
			Rule r= new Rule(nbattribue);
			
			r.init();
			Random ran =new Random();
			int lower = 0;
			int higher = nbattribue + 1;
			int f=(int) (Math.random() * (higher - lower)) + lower;
			int v=(int) (Math.random() * (higher - lower)) + lower;			
			bats[i]=new bat(r, r,f,v, Math.random(), Math.random(), fitness(r));
			bats[i].Nbattribue=nbattribue;
			//bats[i].getSolution().printRule();System.out.println("Bat "+i+" F : "+f+" v : "+v);
		
		}
		
	}
	public void start(double randhom,int step) {
		// TODO Auto-generated method stub
		double myfitness,Maxfit;
		init();
		int j=0;
		Maxfit=0;
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		double startMemory = (runtime.totalMemory() -  runtime.freeMemory())/ 1024d / 1024d;
		//System.out.println("Here we Go bats .....");
		long lDateTime = System.currentTimeMillis();
		while (j<nbgeneration) {		
		for (int i = 0; i < bats.length; i++) {
			bats[i].frequency = (int)((bats[i].Nbattribue-1)*Math.random()); 
			bats[i].velocity = (int)bats[i].Nbattribue-bats[i].frequency-bats[i].velocity;
			if (bats[i].velocity<0) {
				bats[i].velocity=0;
			}
			
			bats[i].solution.genrate_new_sol(bats[i].frequency ,bats[i].velocity,bats[i].A_loudness, randhom,step);
				//bats[i].solution.printRule();
			if (randhom>bats[i].rate) {
				int index = (int)bats[i].A_loudness*(bats[i].Nbattribue-1);
				if (index<=0) {
					index=0;
				}				
				bats[i].solution.genrate_new_sol(1,index, bats[i].A_loudness,randhom,step);
			}				
			myfitness = fitness(bats[i].getSolution()); 
			
			if (myfitness>bats[i].getFitness()) {
				//System.out.println("Loudness : "+bats[i].getA_loudness());
				//System.out.println("rate : "+bats[i].getRate());
				if (myfitness>Maxfit) {
					Maxfit = myfitness;
					
				}
				bats[i].setFitness(myfitness);				
				bats[i].setBest_solution(bats[i].getSolution());				
				bats[i].decrase_A();
				bats[i].incrase_r(j);
				//System.out.print("Bat: "+i +"   :  ");bats[i].getBest_solution().printRule(); System.out.println(" With fitness : "+fitness(bats[i].getBest_solution()));
				double rsup = SupportItemset(union(bats[i].getSolution().lhs,bats[i].getSolution().rhs));	
				
				double rcon = Confedance(bats[i].getSolution());
			
				//System.out.println(" rsup : "+rsup+"conf : "+rcon+" fit "+Maxfit);
				//				accepted.add(bats[i].getSolution());
//				main_bat.fit.add(myfitness);				
				}				
			
		}	
		for (int i = 0; i < bats.length; i++) {
			bats[i].setSolution(bats[i].getBest_solution());
		}
			
		//System.out.println("iteration : "+j);
		j++;
		}
		double endMemory = (runtime.totalMemory() -  runtime.freeMemory())/ 1024d / 1024d;
		System.out.println(" memory :" + (endMemory - startMemory));
		System.out.println("Execution time ==== > "+(System.currentTimeMillis()-lDateTime)+" Millsecond "+(System.currentTimeMillis()-lDateTime)/1000+" second");
		System.out.println("max fitness : "+Maxfit);
	}

	
	public void printdata() {	
		for (ArrayList<Integer> in : data) {
			for (Integer integer : in) {
				System.out.print(integer+" ");
			}
			System.out.println();
		}		
}
	public double overage_fitness() {
		double overage = 0;
			for (int i = 0; i < accepted.size(); i++) {
				overage+=fit.get(i);
			}
			overage=overage/accepted.size();
		return overage;
	}
	public void printaccepted() {
		for (int i = 0; i < accepted.size(); i++) {			
				accepted.get(i).splitrule();
				accepted.get(i).printRule();
				System.out.println(" with fitness :"+fitness(accepted.get(i)));
			}			
		}
	
	public static void main( String args[]){		
		//IBM
		main_bat b = new main_bat(1,1,59602,497,0.0,0.0,200 ,25);	
		//mashroom
		//main_bat b = new main_bat(1,1,8124,119,0.2,0.5,1000 ,50);	
		System.out.println("loading data \n====== ");
		b.data=b.loaddata("/home/kamel/tr.txt");		
		Collections.reverse(b.data);		
		b.start(0.5,1);
		System.out.println(accepted.size());	
		System.out.println(b.o_sup / accepted.size() + " " + b.o_conf/ accepted.size() );
		accepted.clear();
		fit.clear();		
		System.out.println(" ==================================================================================================================== ");

	}

}
