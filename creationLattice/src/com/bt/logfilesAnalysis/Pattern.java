package com.bt.logfilesAnalysis;

import java.util.ArrayList;

import com.bt.graphml.LatticeElement;
import com.bt.lattice.Lattice;
import com.bt.lattice.run.SimilarityMeasure2;

public class Pattern {

	private ArrayList<LatticeElement> listNode = new ArrayList<LatticeElement>();
	private int NumberOfExamples;

	public Pattern(ArrayList<LatticeElement> listNode) {
		this.listNode = listNode;
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
			s = s + SimilarityMeasure2.getMostSpecificRegExpString(node) + " ";
		}
		return s;
	}

	public static Pattern getPattern(ArrayList<LatticeElement> listNode, ArrayList<Pattern> patterns, Lattice l) {
		/*
		 * It researches a pattern (from a nodes list) in the list of patterns . It
		 * returns the pattern found or a new pattern (if not found).
		 */

		boolean found = false;
		boolean equal = true;
		int i = 0;
		int j = 0;
		String specificRegExp1, specificRegExp2;
		Pattern p = new Pattern(listNode);

		while (!found && i < patterns.size()) {
			equal = true;
			if (patterns.get(i).getListNode().size() == listNode.size()) {

				while (equal && j < listNode.size()) {
					// We check if each element of the lists are equals one by one
					specificRegExp1 = SimilarityMeasure2
							.getMostSpecificRegExpString(patterns.get(i).getListNode().get(j));
					specificRegExp2 = SimilarityMeasure2.getMostSpecificRegExpString(listNode.get(j));
					equal = specificRegExp1.equals(specificRegExp2);
					j++;

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
