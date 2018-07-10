package com.bt.graphml;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;









import com.bt.lattice.Concept;
public class ReducedLabelLatticeToJSON {



	private LatticeDiagram theLattice;
	private int edgeID=1;

	public ReducedLabelLatticeToJSON(com.bt.lattice.Lattice lattice)
	{
		this(makeLattice(lattice));
	}

	public ReducedLabelLatticeToJSON(File f) throws IOException
	{
		this(new com.bt.lattice.Lattice(f));
	}

	public ReducedLabelLatticeToJSON(LatticeDiagram l)
	{
		theLattice = l;
	}

	public ReducedLabelLatticeToJSON(String file) throws IOException
	{
		this(new File(file));
	}

	//writes the previously created lattice to a json file
	public void toJson(String out) throws FileNotFoundException, IOException
	{
		
		FileWriter outFile = new FileWriter(new  File( out));
boolean firstWrite=true;

		outFile.write("[\n");	
		// add nodes
		for(LatticeElement e : theLattice.getElements())
		{
			if(firstWrite)
			{
				firstWrite=false;
			}
			else
				outFile.write(",");
			outFile.write("{\"group\":\"nodes\",\n");
			outFile.write(getNodeJSONString(e));
			
		}	
			
	//	firstWrite=true;
		// add edges
//		outFile.write("],\nedges: [\n");
		outFile.write(" \n\n");
		for(LatticeElement e : theLattice.getElements())
			for(LatticeElement desc : e.getDescendants())
			{
				if(firstWrite)
					firstWrite=false;
				else
					outFile.write(",");
				outFile.write(getEdgeJSONString(e, desc));
			}
	outFile.write("\n]\n");
		
		
	//	outFile.write("\n};\n");

		outFile.close();
	}
	
	public static void main(String args[])
	{
	//	final String DIR = "/Users/entpm/eclipse code/fuzzy-edit-distance-12m/latticeData/";
		 final String LATSUFFIX = ".lat";

	//	final String DIR = "/Users/entpm/eclipse code/latticeData/";
			// final String DIR =  "/Users/entpm/eclipse code/latticeData/"; ///Users/entpm/BT code/cyberdata/accessExpt/"; // "";
			//"/Users/entpm/BT code/cyberdata/accessExpt/";
		// final String DIR =  "/Users/entpm/conexp-1.3/";
		 //	 		 final String DIR = "/Users/entpm/eclipse code/virgileFCA/" ;// "/Users/entpm/eclipse code/latticeData/";
			//    "/Users/entpm/eclipse code/fuzzy-edit-distance-12m/latticeData/";   //"/Users/entpm/BT code/cyberdata/";
	//  /Users/entpm/BT code/cyberdata/accessExpt/"; //"/Users/entpm/BT code/zhan proj/accessData/CSVFiles/";
	boolean addDir = false;
	boolean silentMode=false;
     String theDir =  "/Users/entpm/eclipse code/latticeData/";    // "/Users/entpm/BT code/cyberdata/VASTChal2014MC2-20140430/";
	String[] INFILES = {"eNames401","eNames402","eNames403","eNames404","eNames405","eNames406","eNames407",
			   "eNames408","eNames409","eNames410","bNames401","bNames402",
			   "bNames403","bNames404","bNames405","bNames406","bNames407","bNames408","bNames409","bNames410",
			   "02grok"
};
	//	String[] INFILES = {"dest"};
//	String[] INFILES = {"fw08", "fp08", "fs08", "fa08", "fw09", "fp09", "fs09", "fa09", "fw10", "fp10", "fs10", "fa10"};
	/*			String[] INFILES = {"fcl2010km2", "v1fcl2010km2", "fcl2010km3", "v1fcl2010km3", "fcl2010km4", "v1fcl2010km4", "fcl2010km5", "v1fcl2010km5",
					"fcl2010km6", "v1fcl2010km6", "fcl2010km7", "v1fcl2010km7", "fcl2010km8", "v1fcl2010km8", "fcl2010km9", "v1fcl2010km9",
					"fcl2010km10", "v1fcl2010km10", "fcl2010km11", "v1fcl2010km11", "fcl2010km12", "v1fcl2010km12", 
					"fcl2010km13", "v1fcl2010km13", "fcl2010km14", "v1fcl2010km14", "fcl2010km15", "v1fcl2010km15", "fcl2010km16", "v1fcl2010km16", 
					"fcl2010km17", "v1fcl2010km17", "fcl2010km18", "v1fcl2010km18", "fcl2010km19", "v1fcl2010km19",  
					"fcl2010km20", "v1fcl2010km20", "fcl2010"};
*/
			String[] fileList;
		
		if (args.length >0)
		{
			ArrayList<String> fList=new ArrayList<String>();
			for(int i = 0; i < args.length; i++)
			{
				if(args[i].equals("-D") && (++i < args.length))
				{
					addDir = true;
					theDir = args[i];
				}
				
				else

					fList.add(args[i]);
			}		
			fileList = fList.toArray(new String[fList.size()]);
			}		else
		{
			fileList = INFILES;
			addDir = true;
		}	
		if(!silentMode)
			System.out.println("ReducedLabelLatticeToJSON::" + args.length + " filelist ::" + fileList.length);
		
		for(String file : fileList)
{
	String f;
	if(addDir) f = theDir+file;
	else
		f=file;
	if(!silentMode)
	System.out.println("processing " + file + "...");
try

			{
	ReducedLabelLatticeToJSON c = new ReducedLabelLatticeToJSON(f+LATSUFFIX); //GroupRandomly()); // GroupBySize());
				c.toJson(f + ".json");
				if(!silentMode)
				System.out.println(f + " done.");
			}
			catch(IOException e)
			{
				System.err.println("error with " + file + ": " + e.getMessage());
			}
	}
	}

	//converts the given edge to an xml element
	private  String getEdgeJSONString(LatticeElement source, LatticeElement target)
	{
		StringBuffer edgeStr = new StringBuffer("{ \"group\":\"edges\",\"data\": {\"id\":\"e" + (edgeID++)+"\"");

		edgeStr.append( ", \"source\":");
		edgeStr.append("\""+source.label()+"\"");
		edgeStr.append( ", \"target\":");
		edgeStr.append("\""+target.label()+"\"");
		edgeStr.append( "}}\n");

		return edgeStr.toString();
	}

	//converts the given concept's extent to an xml element
//	private static Element getExtentElement(Concept c)
//	{
//		Element extent = new Element("data");
//		extent.setAttribute("key", "e");
//		extent.addContent(c.getExtent().toString());
//
//		return extent;
//	}
//	//converts the given concept's reduced extent to an  xml element
//	private String getReducedExtentElement(Concept c, Set<LatticeElement> set)
//	{
//		Element extent = new Element("data");
//		extent.setAttribute("key", "r");
//		extent.addContent(c.getExtent().reducedExtentString(set));
//
//		return extent;
//	}
//	//converts the given concept's reduced intent and extent to an  xml element
//	private static Element getReducedLabelElement(Concept c, Set<LatticeElement> desc, Set<LatticeElement> anc)
//	{
//		Element extent = new Element("data");
//		extent.setAttribute("key", "x");
//		String redInt=c.getIntent().reducedIntentString(anc);
//		if(redInt.length()>0) 
//		extent.addContent(redInt+"\n"+c.getExtent().reducedExtentString(desc));
//		else
//			extent.addContent(c.getExtent().reducedExtentString(desc));
//
//		return extent;
//	}
//
//	//converts the given concept's extent to an abbreviated xml element
//	private static Element getAbbrevExtentElement(Concept c)
//	{
//		Element extent = new Element("data");
//		extent.setAttribute("key", "s");
//		extent.addContent(c.getExtent().abbrevExtent(20));
//
//		return extent;
//	}
//
//
//	//converts the given concept's cardinality to an xml element
//	private static Element getCardElement(Concept c)
//	{
//		Element extent = new Element("data");
//		extent.setAttribute("key", "c");
//		extent.addContent(c.getExtent().cardAsString());
//
//		return extent;
//	}
//	private static Element getFullDescElement(Concept c)
//	{
//		Element extent = new Element("data");
//		extent.setAttribute("key", "f");
//		extent.addContent(c.getIntent().toString() + "\n"+ c.getExtent().toString());
//
//		return extent;
//	}
//
//	//converts the given group (but not its edges) to an xml element
//	private static Element getGroup(Group group)
//	{
//		Element graph = new Element("graph");
//		String id = group.label();
//
//		graph.setAttribute("id", id);
//		graph.setAttribute("edgedefault", "undirected");
//
//		for(LatticeElement e : group.getElements())
//			graph.addContent(getNode(e));
//
//		for(Group g : group.getSubGroups())
//		{
//			Element node = new Element("node");
//			Element h = getGroup(g);
//			node.setAttribute("id", h.getAttributeValue("id"));
//			node.addContent(h);
//			graph.addContent(node);
//		}
//
//		return graph;
//	}
//
	//converts the given concept's intent to an xml element
//	private String  getIntentElement(Concept c)
//	{
//		retrun (c.getIntent().toString());
//
//		return intent;
//	}

	//converts the given element to an xml element
	private  String getNodeJSONString(LatticeElement e)
	{
		StringBuffer nodeStr = new StringBuffer("  \"data\": { \"id\": \"" + e.label() + "\"\n");

		nodeStr.append(",\"i\":"+ e.getConcept().getIntent().toQuotedString()+"\n");
		nodeStr.append(",\"e\":"+ e.getConcept().getExtent().toQuotedString()+"\n");
		nodeStr.append(",\"c\":"+ e.getConcept().getExtent().cardAsString()+"\n");
		nodeStr.append(",\"r\":["+ e.getConcept().getExtent().reducedExtentString(e.getDescendants())+"]\n");
		
		nodeStr.append(",\"x\":["+e.getConcept().getIntent().reducedIntentString(e.getAncestors())+"]\n");
		nodeStr.append("} }\n");
//		nodeStr.append(getExtentElement(e.getConcept()));
//		nodeStr.append(getCardElement(e.getConcept()));
//		nodeStr.append(getAbbrevExtentElement(e.getConcept()));
//		nodeStr.append(getFullDescElement(e.getConcept()));
//		nodeStr.append(getReducedExtentElement(e.getConcept(), e.getDescendants()));
//		nodeStr.append(getReducedLabelElement(e.getConcept(), e.getDescendants(), e.getAncestors()));

		
		
		return nodeStr.toString();
	}

	//detects the edges in the given lattice
	public static LatticeDiagram makeLattice(com.bt.lattice.Lattice l)
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

//	private static void saveDoc(File out, Document doc) throws FileNotFoundException, IOException
//	{
//		XMLOutputter o = new XMLOutputter(Format.getPrettyFormat());
//		o.output(doc, new BufferedOutputStream(new FileOutputStream(out)));
//	}
}
