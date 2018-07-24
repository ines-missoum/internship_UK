package com.bt.lattice.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.bt.context.Context;
import com.bt.graphml.ConceptUtil;
import com.bt.graphml.LatticeDiagram;
import com.bt.graphml.LatticeElement;
import com.bt.lattice.Element;
import com.bt.lattice.Lattice;

import com.bt.logfilesAnalysis.*;

public class SimilarityMeasure2 {

	private String csv;
	private String rootName;
	private final String CSVSUFFIX = ".csv";
	private final String LATSUFFIX = ".lat";
	private final boolean silentMode = true;
	private final String logfile = "C:\\Users\\Inès MISSOUM\\Documents\\IG3\\Semestre 2\\internship UK\\logfilesAnalysis/petit_test_log.txt";
	private CreationCsvFile csvFile = new CreationCsvFile();
	// logfile to use to create the csv file so there is no bug S

	public SimilarityMeasure2(String csvFile) {
		if (csvFile.endsWith(CSVSUFFIX)) {
			this.csv = csvFile;
			rootName = csvFile.substring(0, csvFile.length() - CSVSUFFIX.length());
		} else {
			this.csv = csvFile + CSVSUFFIX;
			rootName = csvFile;
		}

	}

	/************************* RUN *********************************/
	public int run(double threshold) throws IOException {
		Context c;
		try {
			c = new Context(new File(csv));
		} catch (FileNotFoundException e) {
			return 1;
		} catch (IOException e) {
			return 2;
		}

		System.out
				.println("csv: (" + c.getObjects().length + " objects and " + c.getAttributes().length + " attributes");

		if (!silentMode)
			System.out.print("creating lattice...");
		Lattice l = new Lattice(c);
		if (!silentMode)
			System.out.println("done (" + l.getConcepts().size() + " concepts)");

		LatticeDiagram theLattice = ConceptUtil.makeLatticeDiagram(l);

		recordAllBigSimilarity(theLattice, threshold);

		// derivePattern(logfile, "", l);
		// System.out.println(getMostSpecificRegExp("Sep ", l));
		// un espace a chaque fois
		return 0;
	}

	public int getIntersCardinality(LatticeElement node1, LatticeElement node2) {

		HashSet<Element> extent1 = new HashSet<Element>(node1.getConcept().getExtent().getElements());
		HashSet<Element> extent2 = new HashSet<Element>(node2.getConcept().getExtent().getElements());
		
		
		HashSet<String> allExamples1 = new HashSet<String>();
		HashSet<String> allExamples2 = new HashSet<String>();;
		Type type=new Type("");
		String pattern ="";
		String name;
		
		for (Element e1: extent1) {
			name = e1.getName().replaceAll(" ", "");
			pattern = CreationCsvFile.getPattern(name,  this.csvFile.getRegExps());
			type=Type.getType(pattern, this.csvFile.getTypes());// to have the type of a pattern 
			allExamples1.addAll(type.getExamples());// to have all the examples of the type
		}
		
		for (Element e2: extent2) {
			name = e2.getName().replaceAll(" ", "");
			pattern = CreationCsvFile.getPattern(name,  this.csvFile.getRegExps());
			type=Type.getType(pattern, this.csvFile.getTypes());// to have the type of a pattern 
			allExamples2.addAll(type.getExamples());// to have all the examples of the type
		}


		allExamples1.retainAll(allExamples2);

		return allExamples1.size();


	}

	public int getUnionCardinality(LatticeElement node1, LatticeElement node2) {

		HashSet<Element> extent1 = new HashSet<Element>(node1.getConcept().getExtent().getElements());
		HashSet<Element> extent2 = new HashSet<Element>(node2.getConcept().getExtent().getElements());
		HashSet<String> allExamples1 = new HashSet<String>();
		HashSet<String> allExamples2 = new HashSet<String>();;
		Type type=new Type("");
		String pattern ="";
		String name;
		// pb : les listes allExamples sont vides pk getname a un espace en trop a la fin
		
		for (Element e1: extent1) {
			name = e1.getName().replaceAll(" ", "");
			pattern = CreationCsvFile.getPattern(name,  this.csvFile.getRegExps());
			type=Type.getType(pattern, this.csvFile.getTypes());// to have the type of a pattern 
			allExamples1.addAll(type.getExamples());// to have all the examples of the type
		}
		//System.out.println("ext1 ==== "+ extent1.toString());
		//System.out.println("allex1 ==== "+ allExamples1.toString());
		
		for (Element e2: extent2) {
			name = e2.getName().replaceAll(" ", "");
			pattern = CreationCsvFile.getPattern(name,  this.csvFile.getRegExps());
			type=Type.getType(pattern, this.csvFile.getTypes());// to have the type of a pattern 
			allExamples2.addAll(type.getExamples());// to have all the examples of the type
		}
		//System.out.println("ext2 ==== "+ extent2.toString());
		//System.out.println("allex2 ==== "+ allExamples2.toString());

		allExamples1.addAll(allExamples2);

		return allExamples1.size();

	}

	public float getSimilarity(LatticeElement node1, LatticeElement node2) {

		return (float) getIntersCardinality(node1, node2) / (float) getUnionCardinality(node1, node2);

	}

	public void recordAllBigSimilarity(LatticeDiagram theLattice, double threshold) throws IOException {
		/*
		 * Record in a file all the couples that have a similarity upper than the
		 * threshold
		 */

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

							highSimilarityCouple = "node1 : " + node1.getConcept().getIntent() + "\n";
							highSimilarityCouple += "node2 : " + node2.getConcept().getIntent() + "\n";
							highSimilarityCouple += "similarity : " + similarity + "\n";
							highSimilarityCouple += "________ \n";
							file.append(highSimilarityCouple);
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

	public String getMostSpecificRegExp(String field, Lattice l) {
		/* not always an only specific regexp, here we take the first one */
		LatticeDiagram theLattice = ConceptUtil.makeLatticeDiagram(l);
		/*
		 * for (LatticeElement node : theLattice.getElements()) {
		 * if(node.getConcept().getExtent().getReducedExtent(node.getDescendants()).
		 * contains(field)) {
		 * 
		 * } }
		 */

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
		// System.out.println(node.getConcept().getExtent().getReducedExtent(node.getDescendants()));
		/*
		 * System.out.println(node.getConcept().getIntent()); System.out.println(found);
		 */

		String s = "";
		if (!node.getConcept().getIntent().getElements().isEmpty()) {
			Iterator<Integer> it = node.getConcept().getIntent().getElements().iterator();
			s = l.getAttributes()[it.next()];
		}
		// System.out.println(s);
		return s;
	}

	public void derivePattern(String logfile, String regExpFile, Lattice l) throws IOException {

		BufferedReader br2 = new BufferedReader(new FileReader(logfile));
		String line_logfile;
		String regExp;
		String field;
		try {

			line_logfile = br2.readLine();
			while (line_logfile != null) {
				// line_logfile = br2.readLine();
				String[] parts = line_logfile.split("[, ]");// all the elements of the logfile are separated with a
															// space

				for (int i = 0; i < parts.length; i++) {
					field = parts[i] + " ";
					regExp = getMostSpecificRegExp(field, l);
					System.out.print(regExp + " ");
				}
				System.out.println();
				line_logfile = br2.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			br2.close();
		}

	}

	public static void main(String[] args) throws IOException {

		double threshold = 0.2;
		String theFile = // "C:\\Users\\Inès MISSOUM\\Documents\\IG3\\Semestre 2\\internship
							// UK\\creationLattice/026grok";
				"C:\\\\Users\\\\Inès MISSOUM\\\\Documents\\\\IG3\\\\Semestre 2\\\\internship UK\\\\logfilesAnalysis/lattice";

		if (args.length == 1) {
			theFile = args[1];
		}

		System.out.println("\nprocessing " + theFile + "...");
		SimilarityMeasure2 db = new SimilarityMeasure2(theFile + ".csv");
		db.csvFile.run();
		switch (db.run(threshold)) {
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
