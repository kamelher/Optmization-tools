package com.kaliheragmi.batarm;

import java.util.Random;

public class population extends Thread {
	double randhom; int step; int item;
	public bat[] bats;
	int nbBats,nbattribue;
	bat localbest;
	public population(int nbatr, int item, int nbbats) {
		// TODO Auto-generated constructor stub
		this.randhom = 0.5;
		this.step = 1;
		this.item = item;
		this.nbBats = nbbats;
		this.nbattribue = nbatr;
		bats = new bat[nbBats];
	}
	
	public void init(int item) {
		
		for (int i = 0; i < bats.length; i++) {
			
			Rule r = new Rule(nbattribue, item);
			r.init();
			Random ran = new Random();
			int lower = 0;
			int higher = nbattribue + 1;
			int f = (int) (Math.random() * (higher - lower)) + lower;
			int v = (int) (Math.random() * (higher - lower)) + lower;
			bats[i] = new bat(r, r, f, v, Math.random(), Math.random(),
					main_bat.fitness(r));
			bats[i].Nbattribue = nbattribue;

		}

	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.generate();
		
	}
	
	public void generate() {
		double myfitness,Maxfit = 0;
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
			myfitness = main_bat.fitness(bats[i].getSolution());
			if (myfitness > bats[i].getFitness()) {
				if (myfitness > Maxfit) {
					Maxfit = myfitness;
					localbest = bats[i];
				}
				bats[i].setFitness(myfitness);
				bats[i].setBest_solution(bats[i].getSolution());
				bats[i].decrase_A();
				bats[i].incrase_r();						
			}
		}
	}
	
	
	public bat getbest() {
		
		return localbest;

	}

}
