package com.bt.graphml;

import java.util.List;
import com.bt.lattice.Concept;

public class ConceptUtil
{
	/**
	 * @param a
	 * @param b
	 * @return true if this is a super concept of sub i.e. all elements in THIS intent are in sub intent and THIS is not equal to sub
	 */
	public static boolean isStrictSuperConcept(Concept a, Concept b)
	{
		boolean ret = true;
		if(a.getIntent().size() >= b.getIntent().size())
			ret = false;
		else
		{
			List<Integer> subIntent = b.getIntent().getElements();
			for(int i = 0; ret && (i < a.getIntent().size()); i++)
				ret = subIntent.contains(a.getIntent().getElements().get(i));
		}
		return ret;
	}

	/**
	 * @param a
	 * @param b
	 * @return true if this is a super concept of sub i.e. all elements in THIS intent are in sub intent
	 */
	public static boolean isSuperConcept(Concept a, Concept b)
	{
		boolean ret = true;
		if(a.getIntent().size() > b.getIntent().size())
			ret = false;
		else
		{
			List<Integer> subIntent = b.getIntent().getElements();
			for(int i = 0; ret && (i < a.getIntent().size()); i++)
				ret = subIntent.contains(a.getIntent().getElements().get(i));
		}
		return ret;
	}
	
	//detects the edges in the given lattice
	public static LatticeDiagram makeLatticeDiagram(com.bt.lattice.Lattice l)
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

}
