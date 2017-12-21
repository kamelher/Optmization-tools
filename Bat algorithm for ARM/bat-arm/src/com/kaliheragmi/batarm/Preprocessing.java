package com.kaliheragmi.batarm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Preprocessing {

	/**
	 * @param args
	 */
	String[] attribute = {"A","B","C","D","E","F","G"};
	int nb_att;
	Hashtable<Integer, ArrayList<Integer>> vertical_rep;
	private Hashtable<Integer, int[]> trans;
	private File data;
	public Preprocessing(File f, int nbatt) {
		// TODO Auto-generated constructor stub
		this.data =f;
		this.trans = new Hashtable<>();
		this.nb_att = nbatt;
		this.vertical_rep = new Hashtable<Integer, ArrayList<Integer>>();
		for (int i = 0; i < nbatt; i++) {
			ArrayList<Integer> a =new ArrayList<>();
			this.vertical_rep.put(i+1, a);
		}
	}
	
	public void load() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(data));
			String line;
			String[] tr;
			int index = 0;
			int[] tran = new int[nb_att];
			try {
				while ((line = reader.readLine())!=null) {
					tr = line.split(" ");
					for (int i = 0; i < tr.length; i++) {
						tran[i] = Integer.valueOf(tr[i]);
					}
					this.trans.put(index, tran);
					tran = new int[nb_att];
					index++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void change() {		
		for (int i = 0; i < trans.size(); i++) {
			for (int j = 0; j < trans.get(new Integer(i)).length; j++) {
				if (trans.get(new Integer(i))[j]!=0) {
					vertical_rep.get(trans.get(new Integer(i))[j]).add(new Integer(i));
				}
				
			}
			
					
		}
	}
	
	public void printMap() {
		change();
		try {
			BufferedWriter write = new BufferedWriter(new FileWriter(new File("/home/kamel/tr.txt")));
			for (Integer in : vertical_rep.keySet()) {
				
				for (int j = 0; j < vertical_rep.get(in).size(); j++) {
							write.write(vertical_rep.get(in).get(j)+" ");
					}
				write.newLine();
			}
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("==== start preprocessing =========");
		File f = new File("/home/kamel/MEGA/WorkSpace/MO-bat/src/com/kaliheragmi/batarm/DATA/BMS1.txt");
		Preprocessing p =new Preprocessing(f,497);
		p.load();		
		p.printMap();
		System.out.println("==== End preprocessing =========");
	}

}
