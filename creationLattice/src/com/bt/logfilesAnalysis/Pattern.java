package com.bt.logfilesAnalysis;

import java.util.ArrayList;

import com.bt.graphml.LatticeElement;
import com.bt.lattice.Lattice;
import com.bt.lattice.run.SimilarityMeasure2;

public class Pattern {

	private ArrayList<LatticeElement> listNode = new ArrayList<LatticeElement>();
	private int NumberOfExamples;

	public Pattern(ArrayList<LatticeElement> listRegExp) {
		super();
		this.listNode = listRegExp;
		NumberOfExamples = 0;
	}

	public ArrayList<LatticeElement> getListNode() {
		return listNode;
	}

	public int getNumberOfExamples() {
		return NumberOfExamples;
	}

	public void addAnExamples() {
		NumberOfExamples++;
	}

	public void changeNode(int i, LatticeElement newNode) {
		this.listNode.set(i, newNode);
	}

	public String toString(Lattice l) {
		String s = "";
		for (LatticeElement node : this.getListNode()) {
			s = s + SimilarityMeasure2.getMostSpecificRegExpString(node, l) + " ";
		}
		return s;
	}

	public static Pattern getPattern(ArrayList<LatticeElement> listNode, ArrayList<Pattern> patterns, Lattice l) {
		/*
		 * It researches a type (from a pattern String) in the list. It returns the type
		 * found or a new type (if not found).
		 */

		boolean found = false;
		boolean equal = true;
		int i = 0;
		int j = 0;
		String specificRegExp1, specificRegExp2;
		Pattern p = new Pattern(listNode);

		while (!found && i < patterns.size()) {
			equal=true;
			if (patterns.get(i).getListNode().size() == listNode.size()) {
				
				while (equal && j < listNode.size()) {
					
					specificRegExp1 = SimilarityMeasure2
							.getMostSpecificRegExpString(patterns.get(i).getListNode().get(j), l);
					specificRegExp2 = SimilarityMeasure2.getMostSpecificRegExpString(listNode.get(j), l);
					equal = specificRegExp1.equals(specificRegExp2);
					//System.out.println(specificRegExp1+"=="+specificRegExp2+" is "+equal);
					j++;

					// pour chaque element de la liste je verifie s'ils sont egales
				}
				found = equal;
			}

			i++;

		}

		if (found) {
			p = patterns.get(i - 1);
		}

		return p;

	}

}
