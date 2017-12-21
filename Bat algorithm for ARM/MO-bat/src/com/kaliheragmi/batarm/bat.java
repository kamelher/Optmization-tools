package com.kaliheragmi.batarm;

public class bat {
	public  int Nbattribue;
	public  Rule best_sol;
	public double best_fitness;
	public Rule solution,best_solution;
	public int frequency,velocity;
	public double A_loudness,rate;
	public double fitness;
	
	/**
	 * @return the solution
	 */
	public Rule getSolution() {
		return solution;
	}


	/**
	 * @param solution the solution to set
	 */
	public void setSolution(Rule solution) {
		this.solution = solution;
	}


	/**
	 * @return the best_solution
	 */
	public Rule getBest_solution() {
		return best_solution;
	}


	/**
	 * @param best_solution the best_solution to set
	 */
	public void setBest_solution(Rule best_solution) {
		this.best_solution = best_solution;
	}


	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}


	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}


	/**
	 * @return the velocity
	 */
	public int getVelocity() {
		return velocity;
	}


	/**
	 * @param velocity the velocity to set
	 */
	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}


	/**
	 * @return the a_loudness
	 */
	public double getA_loudness() {
		return A_loudness;
	}


	/**
	 * @param a_loudness the a_loudness to set
	 */
	public void setA_loudness(double a_loudness) {
		A_loudness = a_loudness;
	}


	/**
	 * @return the rate
	 */
	public double getRate() {
		return rate;
	}


	/**
	 * @param rate the rate to set
	 */
	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getFitness() {
			return fitness;
		}
	
	
		public void setFitness(double fitness) {
			this.fitness = fitness;
		}
		
	public bat(Rule solution, Rule best_solution, int frequency, int velocity,double a_loudness, double rate,double fitness) {
		super();
		this.solution = solution;
		this.best_solution = best_solution;
		this.frequency = frequency;
		this.velocity = velocity;
		A_loudness = a_loudness;
		this.rate = rate;
		this.fitness=fitness;
	}
	

	public void decrase_A() {
		this.A_loudness *=0.9; 
	}
	public void incrase_r(int j) {
		this.rate /=0.9; 
	}

	
}
