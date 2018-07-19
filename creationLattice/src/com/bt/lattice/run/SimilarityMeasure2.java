package com.bt.lattice.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import com.bt.context.Context;
import com.bt.graphml.ConceptUtil;
import com.bt.graphml.LatticeDiagram;
import com.bt.graphml.LatticeElement;
import com.bt.lattice.Concept;
import com.bt.lattice.Element;
import com.bt.lattice.Lattice;

public class SimilarityMeasure2 {

	private String csv;
	private String rootName;
	private final String CSVSUFFIX = ".csv";
	private final String LATSUFFIX = ".lat";
	private final boolean silentMode = true;

	public SimilarityMeasure2(String csvFile) {
		if (csvFile.endsWith(CSVSUFFIX)) {
			this.csv = csvFile;
			rootName = csvFile.substring(0, csvFile.length() - CSVSUFFIX.length());
		} else {
			this.csv = csvFile + CSVSUFFIX;
			rootName = csvFile;
		}

	}

	public int run() {
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
		/* for all the couples of concepts : */
		/* /!\ pas faire les couples dans les deux sens */
		for (LatticeElement node1 : theLattice.getElements()) {
			for (LatticeElement node2 : theLattice.getElements()) {
				if (node1 != node2) {
					float i = getSimilarity(node1, node2);

					System.out.println("node1 : " + node1.getConcept().getIntent());
					System.out.println("node2 : " + node2.getConcept().getIntent());
					System.out.println(i);
					System.out.println("________");

				}
			}
		}
		return 0;
	}

	public int getIntersCardinality(LatticeElement node1, LatticeElement node2) {

		HashSet<Element> extent1 = new HashSet<Element>(node1.getConcept().getExtent().getElements());
		HashSet<Element> extent2 = new HashSet<Element>(node2.getConcept().getExtent().getElements());
		extent1.retainAll(extent2);

		//for (Element el : extent1)
			//System.out.println(el.toString());

		return extent1.size();

	}

	public int getUnionCardinality(LatticeElement node1, LatticeElement node2) {

		HashSet<Element> extent1 = new HashSet<Element>(node1.getConcept().getExtent().getElements());
		HashSet<Element> extent2 = new HashSet<Element>(node2.getConcept().getExtent().getElements());
		extent1.addAll(extent2);

		//for (Element el : extent1)
			//System.out.println(el.toString());

		return extent1.size();

	}

	public float getSimilarity(LatticeElement node1, LatticeElement node2) {

		return (float) getIntersCardinality(node1, node2) / (float) getUnionCardinality(node1, node2);

	}

	public static void main(String[] args) {

		String theFile = // "C:\\Users\\In�s MISSOUM\\Documents\\IG3\\Semestre 2\\internship
							// UK\\creationLattice/026grok";
				"C:\\\\Users\\\\In�s MISSOUM\\\\Documents\\\\IG3\\\\Semestre 2\\\\internship UK\\\\logfilesAnalysis/lattice";

		if (args.length == 1) {
			theFile = args[1];
		}

		System.out.println("\nprocessing " + theFile + "...");
		SimilarityMeasure2 db = new SimilarityMeasure2(theFile + ".csv");
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
