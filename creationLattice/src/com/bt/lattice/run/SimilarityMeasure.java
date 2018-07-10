package com.bt.lattice.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import com.bt.context.Context;
import com.bt.graphml.ConceptUtil;
import com.bt.graphml.LatticeDiagram;
import com.bt.graphml.LatticeElement;
import com.bt.lattice.Concept;
import com.bt.lattice.Lattice;

public class SimilarityMeasure {

	private String csv;
	private String rootName;
	private final String CSVSUFFIX = ".csv";
	private final String LATSUFFIX = ".lat";
	private final boolean silentMode = true;

	public SimilarityMeasure(String csvFile) {
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

		/*
		 * Use ConceptUtil.isStrictSuperConcept to check whether one concept is a
		 * super-concept of a second concept. Note that the Lattice class holds a list
		 * of concepts but we need the LatticeDiagram class to find immediate super and
		 * sub concepts
		 */

		for (Concept c1 : l.getConcepts()) {
			System.out.println("checking :" + c1.getIntent().toString());
			for (Concept c2 : l.getConcepts()) {

				if (ConceptUtil.isStrictSuperConcept(c1, c2))
					System.out.println("    super concept : " + c2.getIntent().toString());

			}
		}

		/*
		 * The LatticeDiagram class represents the diagram used by CONEXP The
		 * getAncestor and getDescendant methods return the nodes which are IMMEDIATELY
		 * above / below this node in the lattice. The section of code below iterates
		 * through nodes and prints a common parent, if one exists. More coding is
		 * required to get upper and lower bounds which are not the immediate parents /
		 * children of two nodes (Sorry!) Note that because this is a lattice structure,
		 * there can only be ONE common parent
		 */

		LatticeDiagram theLattice = ConceptUtil.makeLatticeDiagram(l);
		for (LatticeElement node1 : theLattice.getElements()) {
			// System.out.println("node1 : "+node1.getConcept().getIntent());
			for (LatticeElement node2 : theLattice.getElements()) {
				if (node1 != node2) {
					
					float s= getSimilarity(node1, node2);
					int a= getUnionCardinality(node1, node2);
					int b= getIntersCardinality(node1, node2);
					System.out.println(node1.getConcept().getExtent() + "---" + node2.getConcept().getExtent());
					System.out.println("[ " + node1.getConcept().getExtent().size() + " ; "
							+ node2.getConcept().getExtent().size() + " ]");
					System.out.println(b+"/"+a+"="+s);
					HashSet<LatticeElement> commonAncestors = new HashSet<LatticeElement>(node1.getAncestors());
					commonAncestors.retainAll(node2.getAncestors());
					if (commonAncestors.size() > 0) {
						// System.out.println(" node2 : "+node2.getConcept().getIntent());
						// System.out.println(" -> common parent node :
						// "+commonAncestors.iterator().next().getConcept().getIntent());

					}

				}
			}
		}
		return 0;
	}

	public int /* LatticeElement */ getGLBCardinality(LatticeElement node1, LatticeElement node2) {
		/* for now print the GLB of two nodes */

		HashSet<LatticeElement> commonAncestors = new HashSet<LatticeElement>(node1.getAncestors());
		commonAncestors.retainAll(node2.getAncestors());

		HashSet<LatticeElement> commonAncestorsGLB = commonAncestors;
		int GLBCardinality = node1.getConcept().getExtent().size() + node2.getConcept().getExtent().size();
		if (commonAncestors.size() > 0) {

			for (LatticeElement node : commonAncestors) {
				commonAncestorsGLB.retainAll(node.getDescendants());
				if (commonAncestorsGLB.size() == commonAncestors.size()) {
					/* GLB is an ancestor which has no children in the commonAncestors list : */

					LatticeElement GLB = node;

					GLBCardinality = node.getConcept().getExtent().size(); /* node is the GLB */

					/*System.out.println(node1.getConcept().getExtent() + "!!!!!" + node2.getConcept().getExtent()
							+ "!!!!!!!!!!!!!!!!!!!!!!! : " + GLB.getConcept().getExtent());

					int c = node1.getConcept().getExtent().size()
							+ node2.getConcept().getExtent().size()/*-GLBCardinality*//*;
					System.out.println("[ " + node1.getConcept().getExtent().size() + " ; "
							+ node2.getConcept().getExtent().size() + " ] --> " + GLBCardinality + "___" + c);*/

				}
				commonAncestorsGLB = commonAncestors;
			}
		}

		// return GLB;
		return GLBCardinality;

	}
	
	public int  getUnionCardinality(LatticeElement node1, LatticeElement node2) {
		
		return node1.getConcept().getExtent().size() + node2.getConcept().getExtent().size();

	}
	
	public int getIntersCardinality(LatticeElement node1, LatticeElement node2) {

		HashSet<LatticeElement> commonDescendants = new HashSet<LatticeElement>(node1.getDescendants());
		commonDescendants.retainAll(node2.getDescendants());
		
		HashSet<LatticeElement> commonDescendantsLUB = commonDescendants;
		
		int LUBCardinality = 0;
		
		if (commonDescendants.size() > 0) {

			for (LatticeElement node : commonDescendants) {
				commonDescendantsLUB.retainAll(node.getAncestors());
				if (commonDescendantsLUB.size() == commonDescendants.size()) {
					/* LUB is an ancestor which has no parents in the commonDescendants list : */

					LatticeElement LUB = node;

					LUBCardinality = node.getConcept().getExtent().size(); /* node is the GLB */
					/*System.out.println(node1.getConcept().getExtent() + "!!!!!" + node2.getConcept().getExtent()
							+ "!!!!!!!!!!!!!!!!!!!!!!! : " + LUB.getConcept().getExtent());

					System.out.println("[ " + node1.getConcept().getExtent().size() + " ; "
							+ node2.getConcept().getExtent().size() + " ] --> " + LUBCardinality);*/
				}
				commonDescendantsLUB = commonDescendants;
			}
		}

		
		return LUBCardinality;

	}
	
public float  getSimilarity(LatticeElement node1, LatticeElement node2) {
		
		return (float)getIntersCardinality(node1,node2)/(float)getUnionCardinality(node1, node2);

	}
	
	

	public static void main(String[] args) {
		String theFile = //"C:\\Users\\Inès MISSOUM\\Documents\\IG3\\Semestre 2\\internship UK\\creationLattice/026grok";
				"C:\\\\Users\\\\Inès MISSOUM\\\\Documents\\\\IG3\\\\Semestre 2\\\\internship UK\\\\logfilesAnalysis/lattice";

		if (args.length == 1) {
			theFile = args[1];
		}

		System.out.println("\nprocessing " + theFile + "...");
		SimilarityMeasure db = new SimilarityMeasure(theFile + ".csv");
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
