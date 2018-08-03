package com.bt.lattice.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.bt.context.Context;
import com.bt.graphml.ConceptUtil;
import com.bt.graphml.LatticeDiagram;
import com.bt.graphml.LatticeElement;
import com.bt.lattice.Element;
import com.bt.lattice.Lattice;

import com.bt.logfilesAnalysis.*;

public class SimilarityMeasure2 {

	private final double threshold = 0.5;

	private static Lattice l;
	private String csv;
	private final String CSVSUFFIX = ".csv";
	private final boolean silentMode = true;
	private CreationCsvFile csvFile = new CreationCsvFile();

	public SimilarityMeasure2(String csvFile) {
		if (csvFile.endsWith(CSVSUFFIX)) {
			this.csv = csvFile;
		} else {
			this.csv = csvFile + CSVSUFFIX;
		}

	}

	public HashSet<String> getAllElement(LatticeElement node) {

		HashSet<Element> extent = new HashSet<Element>(node.getConcept().getExtent().getElements());
		HashSet<String> allExamples = new HashSet<String>();

		Type type = new Type("");
		String pattern = "";
		String name;

		// we find all the different elements of the node
		for (Element e : extent) {// for each reference :
			name = e.getName().replaceAll(" ", "");
			pattern = CreationCsvFile.getPattern(name, this.csvFile.getRegExps());
			type = Type.getType(pattern, this.csvFile.getTypes());// to have the type of a pattern
			allExamples.addAll(type.getExamples());// to have all the examples of the type
		}

		return allExamples;
	}

	public int getIntersCardinality(LatticeElement node1, LatticeElement node2) {

		// we find all the different elements of the first node
		HashSet<String> allExamples1 = getAllElement(node1);
		// same for the second node
		HashSet<String> allExamples2 = getAllElement(node2);
		// we take the intersection of the two lists
		allExamples1.retainAll(allExamples2);

		return allExamples1.size();

	}

	public int getUnionCardinality(LatticeElement node1, LatticeElement node2) {

		// we find all the different elements of the first node
		HashSet<String> allExamples1 = getAllElement(node1);
		// same for the second node
		HashSet<String> allExamples2 = getAllElement(node2);

		// we take the union of the two lists
		allExamples1.addAll(allExamples2);

		return allExamples1.size();

	}

	public float getSimilarity(LatticeElement node1, LatticeElement node2) {

		return (float) getIntersCardinality(node1, node2) / (float) getUnionCardinality(node1, node2);

	}

	public void recordAllBigSimilarity(double threshold) throws IOException {
		/*
		 * Record in a file all the couples that have a similarity upper than the
		 * threshold
		 */

		LatticeDiagram theLattice = ConceptUtil.makeLatticeDiagram(l);
		FileWriter file = new FileWriter("highSimilarity.txt");
		String highSimilarityCouple = "";
		try {

			/* for all the couples of concepts : */
			for (int i = 0; i < theLattice.getElements().size(); i++) {
				for (int j = i; j < theLattice.getElements().size(); j++) {
					LatticeElement node1 = theLattice.getElements().get(i);
					LatticeElement node2 = theLattice.getElements().get(j);

					if (node1 != node2) {
						float similarity = getSimilarity(node1, node2);
						if (similarity > threshold) {
							// record
							highSimilarityCouple = "node1 : " + node1.getConcept().getIntent() + "\n node2 : "
									+ node2.getConcept().getIntent() + "\n similarity : " + similarity
									+ "\n ________ \n";
							file.append(highSimilarityCouple);
							// print
							System.out.println("node1 : " + node1.getConcept().getIntent());
							System.out.println("node2 : " + node2.getConcept().getIntent());
							System.out.println("similarity : " + similarity);
							System.out.println("________");

							highSimilarityCouple = "";
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.close();
		}

	}

	public LatticeElement getNode(String field) {
		//////// c'est plutot un getNode
		// print the most specific RegExp of a field(for the human to know) and return
		// the node to
		// apply the getSimilarity method
		/* not always an only specific regexp, here we take the first one */
		LatticeDiagram theLattice = ConceptUtil.makeLatticeDiagram(l);

		boolean found = false;
		int i = 0;

		LatticeElement node = theLattice.getElements().get(0);
		while (i < theLattice.getElements().size() && !found) {
			node = theLattice.getElements().get(i);
			for (Element el : node.getConcept().getExtent().getReducedExtent(node.getDescendants())) {
				if (el.getName().equals(field)) {
					found = true;

				}
			}
			i++;
		}
		return node;
	}

	public static String getMostSpecificRegExpString(LatticeElement node) {
		String s = "";

		if (!node.getConcept().getIntent().reducedIntentString(node.getAncestors()).isEmpty()) {
			String[] parts = node.getConcept().getIntent().reducedIntentString(node.getAncestors()).split("[, ]");
			s = parts[0].replace("\"", "");
		}

		return s;
	}

	public ArrayList<Pattern> getAllPattern() throws IOException {

		// return the list of patterns of the logfile and print it
		FileWriter file = new FileWriter("allPattern.txt");
		BufferedReader br2 = new BufferedReader(new FileReader(CreationCsvFile.getLogfile()));
		String line_logfile;
		String field;
		ArrayList<LatticeElement> listNode = new ArrayList<LatticeElement>();
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		// we find all the differents patterns
		try {

			// we build the pattern of the line
			line_logfile = br2.readLine();
			while (line_logfile != null) {

				String[] parts = line_logfile.split("[, ]");// all the elements of the logfile are separated with a
															// space
				String reference = "";
				listNode = new ArrayList<LatticeElement>();
				for (int i = 0; i < parts.length; i++) {
					reference = Type.findReference(parts[i], this.csvFile.getTypes());
					field = reference + " ";// because the methode getName() used in getMostSpecificRegExp put a space
											// at the end.
					listNode.add(getNode(field));
				}

				Pattern p = Pattern.getPattern(listNode, patterns, l);
				// we check if the pattern doesn't already exist:
				if (p.getNumberOfExamples() == 0) {// new pattern
					patterns.add(p);

				}
				p.addAnExamples();

				line_logfile = br2.readLine();
			}
			
			String s = "\n TOTAL : " + patterns.size() + " patterns \n";
			file.append(s);
			for (Pattern p : patterns) {
				s = "";
				s = p.getNumberOfExamples() + " examples : ";
				for (LatticeElement node : p.getListNode()) {
					s = s + getMostSpecificRegExpString(node) + " ";
				}
				s = s + "\n";
				file.append(s);
			}
			
			System.out.println("All the patterns are recorded (allPattern.txt)");
		
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			br2.close();
			file.close();
		}
		
	
		
		return patterns;
	}

	public void derivePattern() throws IOException {

		// print all the derived patterns
		ArrayList<Pattern> patterns = getAllPattern();// list of all the patterns (without derivation)

		ArrayList<Pattern> derivedPatterns = new ArrayList<Pattern>();// list of
		// derived pattern
		// faire une fonction qui prend en paramettre une liste de pattern
		Pattern p;
		for (int i = 0; i < patterns.size() - 1; i++) {
			for (int j = i + 1; j < patterns.size(); j++) {
				p = combinedPattern(patterns.get(i), patterns.get(j));
				if (p.getListNode().size() != 0) {
					System.out.println(patterns.get(i).toString(l));
					System.out.println(patterns.get(j).toString(l));
					System.out.println("change for : " + p.toString(l));
					System.out.println("_____________________________");
					derivedPatterns.add(p);
				}
			}
		}

		// print the number of derived patterns

	}

	public Pattern combinedPattern(Pattern p1, Pattern p2) {

		// if the two patterns p1 and p2 have a big similarity, return a combined
		// pattern, else return a new pattern.
		boolean similar = (p2.getListNode().size() == p1.getListNode().size());
		Pattern p = new Pattern(new ArrayList<>());
		int i = 0;
		boolean allSame = true;
		LatticeElement n1, n2;
		float similarity;
		while (similar && i < p2.getListNode().size()) {

			n1 = p1.getListNode().get(i);
			n2 = p2.getListNode().get(i);

			similarity = getSimilarity(n1, n2);
			similar = similarity > this.threshold;

			if (similarity == 1.0) {
				p.getListNode().add(n1);

			} else if (similar) {

				allSame = false;
				/** p.changeNode(i, getGLB(n1, n2));CHANGE NODE A SUPP DE PATTERN **/
				p.getListNode().add(getGLB(n1, n2));
				// System.out.println(getMostSpecificRegExpString(p1.getListNode().get(i))+" and
				// "+getMostSpecificRegExpString(p2.getListNode().get(i))+"change for
				// :"+getMostSpecificRegExpString(getGLB(n1, n2))+" because "+
				// getSimilarity(n1, n2));

				// If we find two expressions R1, R2 where this is true, we can replace them
				// with a new regexp pattern R where the regexp at position i is given by
				// R(i) = common ancestor(R1(i), R2(i))
			}

			i++;
		}
		if (!similar || allSame) {
			p = new Pattern(new ArrayList<>());
		}
		return p;
	}

	public LatticeElement getGLB(LatticeElement node1, LatticeElement node2) {
		/* return the GLB */
		LatticeElement GLB = node1;
		LatticeDiagram theLattice = ConceptUtil.makeLatticeDiagram(l);
		boolean found = false;
		int j = 0;

		HashSet<Element> nodeExtent = new HashSet<Element>();
		// System.out.println(!found && j < theLattice.getElements().size());
		// System.out.println(" "+!found );

		Set<Element> list1 = node1.getConcept().getExtent().getReducedExtent(node1.getDescendants());
		Set<Element> list1Extent = new HashSet<Element>(node1.getConcept().getExtent().getElements());
		Set<Element> list2 = node2.getConcept().getExtent().getReducedExtent(node2.getDescendants());
		Set<Element> list2Extent = new HashSet<Element>(node2.getConcept().getExtent().getElements());

		if (list1Extent.containsAll(list2)) {
			GLB = node1;
		} else if (list2Extent.containsAll(list1)) {
			GLB = node2;
		} else {
			while (!found && j < theLattice.getElements().size()) {
				Set<Element> list = list1;
				list.addAll(list2);
				list.addAll(theLattice.getElements().get(j).getConcept().getExtent()
						.getReducedExtent(theLattice.getElements().get(j).getDescendants()));

				nodeExtent = new HashSet<Element>(
						theLattice.getElements().get(j).getConcept().getExtent().getElements());

				if (nodeExtent.size() == list.size()) {
					found = list.containsAll(nodeExtent) && (nodeExtent).containsAll(list);
				}
				j++;
			}
		}

		if (found) {
			GLB = theLattice.getElements().get(j - 1);
		}
		return GLB;
	}

	/************************* RUN *********************************/
	public int run() throws IOException {
		Context c;
		try {
			c = new Context(new File(csv));
		} catch (FileNotFoundException e) {
			return 1;
		} catch (IOException e) {
			return 2;
		}

		System.out.println(
				"csv: (" + c.getObjects().length + " objects and " + c.getAttributes().length + " attributes)");

		if (!silentMode)
			System.out.print("creating lattice...");
		this.l = new Lattice(c);
		if (!silentMode)
			System.out.println("done (" + l.getConcepts().size() + " concepts)");

		recordAllBigSimilarity(this.threshold);
		// getAllPattern();
		//derivePattern();
		return 0;
	}

	public static void main(String[] args) throws IOException {

		String theFile = "C:\\\\Users\\\\Inès MISSOUM\\\\Documents\\\\IG3\\\\Semestre 2\\\\internship UK\\\\creationLattice/lattice";

		if (args.length == 1) {
			theFile = args[1];
		}

		System.out.println("\nprocessing " + theFile + "...");
		SimilarityMeasure2 db = new SimilarityMeasure2(theFile + ".csv");
		db.csvFile.run();
		switch (db.run()) {
		case 1:
			System.err.println("file not found.\n");
			break;
		case 2:
			System.err.println("error while loading. Skipping...\n");
			break;
		case 3:
			System.err.println("error while saving.\n");
			break;
		default:

			System.out.println("completed processing of " + theFile);

		}

	}
}
