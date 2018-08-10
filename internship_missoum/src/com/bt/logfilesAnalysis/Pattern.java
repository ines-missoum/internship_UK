package com.bt.logfilesAnalysis;

import java.util.ArrayList;

import com.bt.graphml.LatticeElement;
import com.bt.lattice.Lattice;
import com.bt.lattice.run.SimilarityMeasure2;

/**
 * 
 * @author Inès MISSOUM BENZIANE
 * 
 */
public class Pattern {

	/** list of lattice nodes **/
	private ArrayList<LatticeElement> listNode = new ArrayList<LatticeElement>();
	/** number of examples of this pattern in the logfile **/
	private int NumberOfExamples;

	/**
	 * Create a new Pattern (from the parameter) with no example
	 * 
	 * @param listNode
	 *            list of lattice nodes
	 */
	public Pattern(ArrayList<LatticeElement> listNode) {
		this.listNode = listNode;
		NumberOfExamples = 0;
	}

	/**
	 * 
	 * @return the list of lattice nodes of the Pattern
	 */
	public ArrayList<LatticeElement> getListNode() {
		return listNode;
	}

	/**
	 * 
	 * @return number of examples of this pattern in the logfile
	 */
	public int getNumberOfExamples() {
		return NumberOfExamples;
	}

	/**
	 * increment the number of examples of the Pattern
	 */
	public void addAnExamples() {
		NumberOfExamples++;
	}

	/**
	 * 
	 * @param l
	 *            the lattice
	 * @return a string in order to display the Pattern (namely its list of lattice
	 *         nodes)
	 */
	public String toString(Lattice l) {
		String s = "";
		for (LatticeElement node : this.getListNode()) {
			s = s + SimilarityMeasure2.getMostSpecificRegExpString(node) + " ";
		}
		return s;
	}

	/**
	 * 
	 * @param patterns list of patterns at the beginning
	 * @param p1 first pattern of the couple
	 * @param p2 second pattern of the couple 
	 * @param l lattice 
	 * @return a list of patterns where the couple of patterns (p1,p2) have been replaced by the combined pattern 
	 */
	public ArrayList<Pattern> replaceCouple(ArrayList<Pattern> patterns, Pattern p1, Pattern p2, Lattice l) {

		ArrayList<Pattern> patternsChanged = patterns;
		if (this.toString(l).equals(p1.toString(l))) {
			p1.NumberOfExamples = p1.NumberOfExamples + p2.NumberOfExamples;
			patternsChanged.remove(p2);
		} else if (this.toString(l).equals(p2.toString(l))) {
			p2.NumberOfExamples = p1.NumberOfExamples + p2.NumberOfExamples;
			patternsChanged.remove(p1);
		} else {
			patternsChanged.remove(p1);
			patternsChanged.remove(p2);
			patternsChanged.add(this);
			this.NumberOfExamples=p1.NumberOfExamples + p2.NumberOfExamples;
		}

		return patternsChanged;

	}

	/**
	 * It researches a pattern (from a nodes list) in the list of patterns
	 * 
	 * @param listNode
	 *            nodes list that is research
	 * @param patterns
	 *            list of patterns (among which the research is made)
	 * @param l
	 *            the lattice
	 * @return the pattern found or a new pattern (if not found)
	 */
	public static Pattern getPattern(ArrayList<LatticeElement> listNode, ArrayList<Pattern> patterns, Lattice l) {

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

		if (found)
			p = patterns.get(i - 1);

		return p;
	}
}
