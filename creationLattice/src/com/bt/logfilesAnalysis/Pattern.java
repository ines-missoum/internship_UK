package com.bt.logfilesAnalysis;

import java.util.ArrayList;

import com.bt.graphml.LatticeElement;

public class Pattern {

	private ArrayList<LatticeElement> listNode = new ArrayList<LatticeElement>();
	private int NumberOfExamples;

	public Pattern(ArrayList<LatticeElement> listRegExp) {
		super();
		this.listNode = listRegExp;
		NumberOfExamples = 0;
	}

	public ArrayList<LatticeElement> getListRegExp() {
		return listNode;
	}

	public LatticeElement getANode(int i) {
		return listNode.get(i);
	}

	public int getNumberOfExamples() {
		return NumberOfExamples;
	}

	public void addAnExamples() {
		NumberOfExamples++;
	}

	public static Pattern getPattern(ArrayList<LatticeElement> listNode, ArrayList<Pattern> patterns) {
		/*
		 * It researches a type (from a pattern String) in the list. It returns the type
		 * found or a new type (if not found).
		 */

		boolean found = false;
		int i = 0;
		Pattern p = new Pattern(listNode);

		while (!found & i < patterns.size()) {
			if (patterns.get(i).getListRegExp().equals(listNode)) {
				found = true;
				p = patterns.get(i);
			}
			i++;
		}

		return p;

	}

}
