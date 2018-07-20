package com.bt.lattice.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import com.bt.context.Context;
import com.bt.graphml.ConceptUtil;
import com.bt.graphml.LatticeDiagram;
import com.bt.graphml.LatticeElement;
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
		FileWriter file = new FileWriter("highSimilarity.txt");
		String highSimilarityCouple="";
		try {
			
			/* for all the couples of concepts : */
			for (int i=0; i < theLattice.getElements().size(); i++) {
				for (int j=i;  j < theLattice.getElements().size() ; j++) {
					LatticeElement node1 = theLattice.getElements().get(i); 
					LatticeElement node2 = theLattice.getElements().get(j);
			
					if (node1 != node2) {
						float similarity = getSimilarity(node1, node2);
						if (similarity > threshold) {
							highSimilarityCouple= "node1 : " + node1.getConcept().getIntent()+"\n";
							highSimilarityCouple+= "node2 : " + node2.getConcept().getIntent()+"\n";
							highSimilarityCouple += "similarity : "+similarity+"\n";
							highSimilarityCouple +="________ \n";
							file.append(highSimilarityCouple);
							System.out.println("node1 : " + node1.getConcept().getIntent());
							System.out.println("node2 : " + node2.getConcept().getIntent());
							System.out.println("similarity : "+similarity);
							System.out.println("________");
							
							highSimilarityCouple="";
						}

					}
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.close();
		}
		
		getMostSpecificRegExp("Sep ", l);//un espace a chaque fois
		return 0;
	}

	public int getIntersCardinality(LatticeElement node1, LatticeElement node2) {

		HashSet<Element> extent1 = new HashSet<Element>(node1.getConcept().getExtent().getElements());
		HashSet<Element> extent2 = new HashSet<Element>(node2.getConcept().getExtent().getElements());
		extent1.retainAll(extent2);

		// for (Element el : extent1)
		// System.out.println(el.toString());

		return extent1.size();

	}

	public int getUnionCardinality(LatticeElement node1, LatticeElement node2) {

		HashSet<Element> extent1 = new HashSet<Element>(node1.getConcept().getExtent().getElements());
		HashSet<Element> extent2 = new HashSet<Element>(node2.getConcept().getExtent().getElements());
		extent1.addAll(extent2);

		// for (Element el : extent1)
		// System.out.println(el.toString());

		return extent1.size();

	}

	public float getSimilarity(LatticeElement node1, LatticeElement node2) {

		return (float) getIntersCardinality(node1, node2) / (float) getUnionCardinality(node1, node2);

	}
	
	public void getMostSpecificRegExp(String field, Lattice l) {
		/* not always an only specific regexp, here we take the first one*/
		LatticeDiagram theLattice = ConceptUtil.makeLatticeDiagram(l);
		/*for (LatticeElement node : theLattice.getElements()) {
			if(node.getConcept().getExtent().getReducedExtent(node.getDescendants()).contains(field)) {
				
			}
		}*/

		boolean found =false;
		int i=0;
	
		LatticeElement node = theLattice.getElements().get(0); 
		while(i < theLattice.getElements().size() && !found) {
			node = theLattice.getElements().get(i); 
				for(Element el:node.getConcept().getExtent().getReducedExtent(node.getDescendants())) {
					if(el.getName().equals(field)) {
						found=true;
					}
			}
			
			i++;
			
		}
		System.out.println(node.getConcept().getExtent().getReducedExtent(node.getDescendants()));
		System.out.println(node.getConcept().getIntent());
		System.out.println(found);
	}

	public static void main(String[] args) throws IOException {
		
		double threshold = 0.9;
		String theFile = // "C:\\Users\\Inès MISSOUM\\Documents\\IG3\\Semestre 2\\internship
							// UK\\creationLattice/026grok";
				"C:\\\\Users\\\\Inès MISSOUM\\\\Documents\\\\IG3\\\\Semestre 2\\\\internship UK\\\\logfilesAnalysis/lattice";

		if (args.length == 1) {
			theFile = args[1];
		}

		System.out.println("\nprocessing " + theFile + "...");
		SimilarityMeasure2 db = new SimilarityMeasure2(theFile + ".csv");
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
