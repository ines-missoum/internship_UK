package com.bt.lattice;

	
	

	import java.io.BufferedOutputStream;
	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileOutputStream;
	import java.io.IOException;
import java.util.Iterator;
	import java.util.LinkedHashSet;
	import java.util.List;
import java.util.Set;

import com.bt.graphml.LatticeElement;
import com.bt.graphml.LatticeDiagram;

	public class LatticePrint 
	{

		private Lattice theLattice;
		private LatticeDiagram theLatticeStructure = null;

		public LatticePrint(File latticeFile)
		{
			try {
				theLattice = new com.bt.lattice.Lattice(latticeFile);
				theLatticeStructure = makeLatticeDiagram(theLattice);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		

	
		private void toPrint() 
		{
			
				
				// add edges
				for(LatticeElement e : theLatticeStructure.getElements())
				{
					System.out.println(" element " + e + " neighbourhood :: " + e.latticeNeighbourString());
	
					
					/// WARNING - doesn't work !!!! *************  
					
					/* Set<LatticeElement> desc = e.getDescendants();
					Iterator<LatticeElement> it = desc.iterator();
					if(it.hasNext())
					{
						Extent union=it.next().getConcept().getExtent();
						while(it.hasNext())
							union.unionWith(it.next().getConcept().getExtent().getElements());
						System.out.println("dsc = " + union);

					}
					else
						
						System.out.println("dsc = nULL" );
						*/
				}
				
			
			
			
		}


		//detects the edges in the given lattice
		private  LatticeDiagram makeLatticeDiagram(com.bt.lattice.Lattice l)
		{
			List<Concept> theConcepts = l.getConcepts();

			// if(theConcepts.size() > 2)
			// {
			Concept topConcept = theConcepts.get(0); // NB super concept = larger extent = smaller intent
			Concept bottomConcept = theConcepts.get(1);

			if(bottomConcept.getIntent().size() < topConcept.getIntent().size()) // 11-8 is it possible for this to happen ??
			{
				topConcept = bottomConcept;
				bottomConcept = theConcepts.get(0);
			}

			for(int i = 2; i < theConcepts.size(); i++) // find largest (bottom) and smallest (top) concept intensions
			{
				Concept thisConcept = theConcepts.get(i);

				if(thisConcept.getIntent().size() > bottomConcept.getIntent().size())
					bottomConcept = thisConcept;

				else if(thisConcept.getIntent().size() < topConcept.getIntent().size())
					topConcept = thisConcept;
			}

			LatticeDiagram theLattice = new LatticeDiagram(topConcept, bottomConcept);

			for(int i = 0; i < theConcepts.size(); i++)
			{
				Concept thisConcept = theConcepts.get(i);

				if((thisConcept != topConcept) && (thisConcept != bottomConcept))
					theLattice.addConcept(thisConcept);
			}
			// }

			return theLattice;
		}

		public static void main(String args[])
		{
			final String DIR = "/Users/entpm/eclipse code/fuzzy-edit-distance-12m/latticeData/";
		//	final String DIR = "/Users/entpm/BT code/cyberdata/accessExpt/"; //"/Users/entpm/BT code/zhan proj/accessData/CSVFiles/";
			 final String LATSUFFIX = ".lat";

			boolean addDir = false;
			 
//			String[] INFILES ={"accFSP", "accRGR", "acc1830", "urspeed"}; //{   "t2005" , "t2006" , "t2007", "t2008", "t2009", "t2010"}	;
//			String[] INFILES = {"ukci1", "ukci2", "ukci3", "a10252010","accRGR"};;
			//{   "a10252005" ,  "a10252006" ,   "a10252007",  "a10252008"    , "a10252009"  , "a10252010", "fcl2005" ,  "fcl2006" ,   "fcl2007",  "fcl2008"    , "fcl2009"  , "fcl2010" };
			String[] INFILES = {   "ukci1" ,  "ukci2",  "ukci3"  };
					String[] fileList;
			
			if (args.length >0)
				fileList = args;
			else
			{
				fileList = INFILES;
				addDir = true;
			}	
			System.out.println("LatticePrint::" + args.length + " filelist ::" + fileList.length);
			
			for(String file : fileList)
	{
		String f;
		if(addDir) f = DIR+file;
		else
			f=file;
		System.out.println("processing " + file + "...");
	LatticePrint c = new LatticePrint(new File(f+LATSUFFIX));
	c.toPrint();
	System.out.println(f + " done.");
		}
		}


}
