package com.bt.lattice.run;



	import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

	import com.bt.context.Context;
import com.bt.graphml.ConceptUtil;
import com.bt.graphml.LatticeDiagram;
import com.bt.graphml.LatticeElement;
//import com.bt.lattice.Extent;
	import com.bt.lattice.Concept;
import com.bt.lattice.Lattice;

	public class demoOfBounds
	{
		private String csv;
		private String rootName;
		private final String CSVSUFFIX = ".csv";
		private final String LATSUFFIX = ".lat";
		private final boolean silentMode=true;


		public demoOfBounds(String csv)
		{
			if(csv.endsWith(CSVSUFFIX))
			{
				this.csv = csv;
				rootName = csv.substring(0,csv.length() - CSVSUFFIX.length());
			}
			else
			{
				this.csv = csv+CSVSUFFIX;
				rootName = csv;
			}

		}




		public int run()
		{
			Context c;
			try
			{
				c = new Context(new File(csv));
			}
			catch(FileNotFoundException e)
			{
				return 1;
			}
			catch(IOException e)
			{
				return 2;
			}

			System.out.println("csv: (" + c.getObjects().length + " objects and " + c.getAttributes().length + " attributes");

			if(!silentMode)
				System.out.print("creating lattice...");
			Lattice l = new Lattice(c);
			if(!silentMode)
				System.out.println("done (" + l.getConcepts().size() + " concepts)");

/*
 * Use ConceptUtil.isStrictSuperConcept to check whether one concept is a super-concept of a second concept.
 * Note that the Lattice class holds a list of concepts but we need the LatticeDiagram class to find 
 * immediate super and sub concepts 
 */

			for(Concept c1 : l.getConcepts())
			{
				System.out.println("checking :"+ c1.getIntent().toString());
				for(Concept c2 : l.getConcepts())
				{

					if(ConceptUtil.isStrictSuperConcept(c1, c2))
						System.out.println("    super concept : " + c2.getIntent().toString());

				}
			}

			/*
			 * The LatticeDiagram class represents the diagram used by CONEXP
			 * The getAncestor and getDescendant methods return the nodes which are
			 * IMMEDIATELY above / below this node in the lattice. 
			 * The section of code below iterates through nodes and prints a common parent, if one exists.
			 * More coding is required to get upper and lower bounds which are not the immediate parents / children of two nodes
			 * (Sorry!)
			 * Note that because this is a lattice structure, there can only be ONE common parent
			 */

			LatticeDiagram theLattice = ConceptUtil.makeLatticeDiagram(l);
			for(LatticeElement node1 :theLattice.getElements())
			{
				//System.out.println("node1 : "+node1.getConcept().getIntent());
				for(LatticeElement node2 :theLattice.getElements())
				{
					if(node1 != node2)
					{
						getGLB( node1, node2);

						HashSet<LatticeElement> commonAncestors =  new HashSet(node1.getAncestors());
						commonAncestors.retainAll(node2.getAncestors());
						if(commonAncestors.size()>0)
						{
							//System.out.println("          node2 : "+node2.getConcept().getIntent());
							//System.out.println("            -> common parent node : "+commonAncestors.iterator().next().getConcept().getIntent());

						}

					}
				}
			}
			return 0;
		}

		/**
		 * @param args
		 */
		
		public void/*LatticeElement*/ getGLB(LatticeElement node1, LatticeElement node2) {
		/* for now print the GLB of two nodes*/	
			LatticeElement GLB;
			
			HashSet<LatticeElement> commonAncestors =  new HashSet<LatticeElement>(node1.getAncestors());
			commonAncestors.retainAll(node2.getAncestors());
			
			HashSet<LatticeElement> commonAncestorsGLB =  commonAncestors;
			
			if(commonAncestors.size()>0)
			{
				//System.out.println("          node2 : "+node2.getConcept().getIntent());
				//System.out.println("            -> common parent node : "+commonAncestors.iterator().next().getConcept().getIntent());
				
				for( LatticeElement node : commonAncestors) {
					commonAncestorsGLB.retainAll(node.getDescendants());
					if(commonAncestorsGLB.size()==commonAncestors.size()) {/* GLB is an ancestor which has no children in the commonAncestors list : */
						GLB=node;
						System.out.println(node1.getConcept().getIntent()+ "!!!!!" + node2.getConcept().getIntent()+"!!!!!!!!!!!!!!!!!!!!!!! : "+GLB.getConcept().getIntent()+"-------"+GLB.getConcept().getExtent());
					}
					commonAncestorsGLB =  commonAncestors;
				}
			}
			
			//return GLB;
			
			
		}
		public static void main(String[] args)
		{
			String theFile =  //"C:\\Users\\Inès MISSOUM\\Documents\\IG3\\Semestre 2\\internship UK\\creationLattice/026grok";    
			"C:\\\\Users\\\\Inès MISSOUM\\\\Documents\\\\IG3\\\\Semestre 2\\\\internship UK\\\\logfilesAnalysis/lattice";


			if (args.length ==1)
			{
				theFile = args[1];
			}		



			System.out.println("\nprocessing " + theFile + "...");
			demoOfBounds db = new demoOfBounds(theFile+".csv");
			switch(db.run())
			{
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

		
	

