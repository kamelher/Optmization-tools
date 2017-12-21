package com.kaliheragmi.batarm;

import java.util.LinkedList;
import java.util.Random;

import javax.swing.plaf.SliderUI;

public class Rule {
	int NBattribute;
	int rule[], lhs[], rhs[];
	Random r = new Random();

	/**
	 * 
	 * @param nb
	 *            nombres des attribue dans la BDD(Items) I={I1,I2,....,In} nb =
	 *            n
	 */
	public Rule(int nb) {
		// TODO Auto-generated constructor stub
		this.NBattribute = nb;
		this.rule = new int[NBattribute + 1];

	}

	/**
	 * Intalisation de la regle .
	 */
	public int[] init() {
		int index;
		int lower = 0;
		int higher = NBattribute + 1;
		this.rule[0] = (int) NBattribute / 2 + 1;
		// System.out.println(this.rule[0]);
		for (int i = 1; i < this.rule.length; i++) {

			index = (int) (Math.random() * (higher - lower)) + lower;
			while (exists(rule, index)) {
				index = (int) (Math.random() * (higher - lower)) + lower;
			}
			int index1 = (int) (Math.random() * (higher - lower)) + lower;
			if (index1 > NBattribute - 25) {
				index = 0;
			}
			this.rule[i] = index;
			/*
			 * this.rule[i] = 0;
			 */}
		this.splitrule();
		return this.rule;
	}

	public void splitrule() {
		lhs = new int[this.rule[0]];
		rhs = new int[this.rule.length - this.rule[0] - 1];
		System.arraycopy(rule, 1, lhs, 0, this.rule[0]);
		System.arraycopy(rule, this.rule[0] + 1, rhs, 0, this.rule.length
				- this.rule[0] - 1);
	}

	private boolean exists(int[] t, int x) {
		for (int i = 1; i < t.length; i++) {
			if (t[i] == x) {
				return true;
			}
		}
		return false;
	}

	public boolean equal(Rule r) {
		for (int i = 0; i < r.rule.length; i++) {
			if (r.rule[i] != this.rule[i]) {
				return false;
			}
		}
		return true;
	}

	public void printRule() {
		System.out.println();
		System.out.print(" lhs : ");
		for (int i = 0; i < this.lhs.length; i++) {
			if (this.lhs[i]!=0) {
				System.out.print(this.lhs[i] + " ");
			}
			
		}

		System.out.print(" rhs : ");
		for (int i = 0; i < this.rhs.length; i++) {
			if (this.rhs[i]!=0) {
				System.out.print(this.rhs[i] + " ");
			}
			
		}

	}

	private boolean test(int[] tmp3) {
		boolean tmp = false, tmp2 = false;
		// System.out.println(tmp3[0]);

		for (int i = 1; i <= tmp3[0]; i++) {
			if (tmp3[i] != 0) {
				tmp = true;
			}
		}
		for (int i = tmp3[0]; i < tmp3.length; i++) {
			if (tmp3[i] != 0) {
				tmp2 = true;
			}
		}
		return tmp && tmp2;
	}

	public boolean testlhs() {
		if (lhs.length==0) {
			return false;
		}else {
			if ((lhs.length==1)&&(lhs[0]==0)) {
				return false;
			}
		}
		for (int i = 0; i < lhs.length; i++) {
			// System.out.print(lhs[i]);
			if (lhs[i] != 0) {
				return true;
			}
		}
		return false;
	}

	public boolean testrhs() {
		if (rhs.length==0) {
			return false;
		}else {
			if ((rhs.length==1)&&(rhs[0]==0)) {
				return false;
			}
		}
		for (int i = 0; i < rhs.length; i++) {
			if (rhs[i] != 0) {
				return true;
			}
		}
		return false;
	}

	public void delete_out_rang(int[] p, int lower, int higher) {
		for (int i = 0; i < p.length; i++) {
			if ((p[i] < lower) || (p[i] > higher)) {
				p[i] = 0;
			}
		}
	}

	public void decompose(int[] p) {
		for (int i = 0; i < p.length; i++) {
			LinkedList<Integer> indix = new LinkedList<>();
			int nb = 0;
			int index = p[i];
			for (int j = 1; j < p.length; j++) {
				if (p[j] == index) {
					indix.add(j);
				}
			}

			if (indix.size() > 1) {
				int r = (int) (Math.random() * (indix.size()));
				indix.remove(r);
				for (int j = 0; j < indix.size(); j++) {
					p[indix.get(j)] = 0;
				}
			}

		}

	}/*
	 * public void decompose(int[] p,int step) { for (int i = 1; i < p.length;
	 * i++) { int index =p[i]+1; while (exists(p, index)) {
	 * //System.out.println("kjhgkjhg"); index++;
	 * 
	 * } p[i]=index; if (p[i]>NBattribute) { p[i]=0; } }
	 * 
	 * }
	 */

	public void genrate_new_sol(int f, int v, double A, double r, int step) {
		int[] tmp = this.rule;

		if (v == 0) {
			v++;
		}
		for (int i = 0; i <= f; i++) {
			if (A < r) {
				// System.out.println("I'm here man ==== A<r");
				if (test(rule)) {
					rule[v - 1] -= step;
				} else {

				}

			} else {
				// System.out.println("I'm here man ==== A>r");
				if (test(rule)) {
					rule[v - 1] += step;					
				} else {

				}
			}
			delete_out_rang(rule, 0, NBattribute);
			decompose(rule);
			splitrule();
			//printRule();
			if ((testlhs() == false)&&(testrhs() == false)) {
				int index=1;
				while(exists(rule, index)){
					index++;
				}
				rule[rule[0]]=index;
				while(exists(rule, index)){
					index++;
				}
				rule[rule[0]+1]=index;
			}
			if ((testlhs() == false)) {
						//System.out.println("lhs false");
						rule[0]++;
						int index=1;
						while(exists(rule, index)){
							index++;
						}
						rule[rule[0]]=index;
						
			} 
			if (testrhs() == false) {
					//System.out.println("rhs false");
					rule[0]--;
					int index=1;
					while(exists(rule, index)){
						index++;
					}
					rule[rule[0]+1]=index;
				}
			
			splitrule();

		}
		
		// printRule();
		splitrule();

	}

	public void print() {
		for (int i = 0; i < rule.length; i++) {
			System.out.print(rule[i] + " ");
		}
	}

	public static void main(String[] args) {
		Rule r = new Rule(8);
		int[] a = { 1, 0, 0, 0, 0, 0, 0, 0, 0 };
		r.rule = a;
		r.splitrule();
		r.print();
		r.printRule();
		System.out.println(r.testlhs());
		System.out.println(r.testrhs());
		r.genrate_new_sol(2, 7, 0.8, 0.7, 1);
		System.out.println("\n===================");
		r.print();
		r.printRule();
		r.decompose(r.rule);
		r.splitrule();
		System.out.println("\n===================");
		r.print();
		r.printRule();

	}
}
