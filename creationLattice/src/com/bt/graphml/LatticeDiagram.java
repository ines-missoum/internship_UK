package com.bt.graphml;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.bt.lattice.Concept;

/**
 * experimental implementation of lattice, doesn't worry too much about efficiency
 * @author Trevor Martin
 *
 */
public class LatticeDiagram
{
	List<LatticeElement> theLattice;
	LatticeElement topElement;
	LatticeElement bottomElement;
	private final boolean LATTICEDEBUG = false;

	public LatticeDiagram(Concept top, Concept bottom)
	{
		topElement = new LatticeElement(top);
		bottomElement = new LatticeElement(bottom);
		topElement.addDesc(bottomElement);
		bottomElement.addAnc(topElement);

		theLattice = new ArrayList<LatticeElement>();
		theLattice.add(topElement);
		theLattice.add(bottomElement);
	}

	public boolean addConcept(Concept newConcept)
	{
		boolean ret = true;
		LatticeElement newElement = new LatticeElement(newConcept);
		if(LATTICEDEBUG)
			System.out.println("addConcept: " + newElement.label() + " = " + newElement.getConcept().getIntent().toString());
		theLattice.add(newElement);
		if(LATTICEDEBUG)
			fullPrintLattice();
		addConceptFromTop(newElement, newConcept);
		addConceptFromBottom(newElement, newConcept);

		if(LATTICEDEBUG)
		{
			if(transitivityCheck(topElement) == false)
			{
				fullPrintLattice();
				System.out.println(" Major error :: call ?? System.exit(1)");
			}
			System.out.println("added #anc = " + newElement.getAncestors().size());

			System.out.println("added #desc = " + newElement.getDescendants().size());
			fullPrintLattice();
		}
		return ret;
	}

	/**
	 * look for max undetected lower neighbours (where no parent is a superset)
	 * @param newEl
	 */
	public void addLowerNeighbours(LatticeElement newEl)
	{
		LinkedHashSet<LatticeElement> checkQueue = new LinkedHashSet<LatticeElement>();
		Concept theConc = newEl.getConcept();
		LinkedHashSet<LatticeElement> nextQueue = null;
		LinkedHashSet<LatticeElement> lowerN = new LinkedHashSet<LatticeElement>();

		checkQueue.add(bottomElement);
		while(!checkQueue.isEmpty())
		{
			nextQueue = new LinkedHashSet<LatticeElement>();
			for(LatticeElement next : checkQueue)
			{
				LinkedHashSet<LatticeElement> possLowerN = new LinkedHashSet<LatticeElement>(); // if this is non-empty, there is at least one super concept from check q that is not supercocnept orr
				// checkQueue.remove(next);
				if(ConceptUtil.isSuperConcept(theConc, next.getConcept()))
					for(LatticeElement nextLayer : next.getAncestors())
						if(ConceptUtil.isSuperConcept(theConc, nextLayer.getConcept()))
							nextQueue.add(nextLayer);
						else if(!ConceptUtil.isSuperConcept(nextLayer.getConcept(), theConc))
							possLowerN.add(next); // we have reached the
				checkQueue = nextQueue;
				if(nextQueue.isEmpty())
					lowerN.addAll(possLowerN);
			}
		}
		if(lowerN.size() > 0)
			for(LatticeElement next : lowerN)
			{
				next.addAnc(newEl);
				newEl.addDesc(next);
			}
	}

	public void fullPrintLattice()
	{
		System.out.println("fullPrintLattice START");
		System.out.println("top = " + topElement.label());

		for(LatticeElement next : theLattice)
		{
			System.out.println("next = " + next.label());
			System.out.println(next.latticeNeighbourString());
		}

		System.out.println("bottom = " + bottomElement.label());
		System.out.println("fullPrintLattice END");
	}

	public LatticeElement get(int i)
	{
		return theLattice.get(i);
	}

	public List<LatticeElement> getElements()
	{
		return theLattice;
	}

	public void printLattice()
	{
		System.out.println("top = " + topElement.toString());
		System.out.println("ancestors = " + topElement.getAncestors().size());
		System.out.println("descendants = " + topElement.getDescendants().size());
		System.out.println("bottom = " + bottomElement.toString());
		System.out.println("ancestors = " + bottomElement.getAncestors().size());

		for(LatticeElement next : theLattice)
			System.out.println("next = " + next.toString());
	}

	public int size()
	{
		return theLattice.size();
	}
	
	public int getUnivSize()
	{
		return topElement.getConcept().getExtent().size();
	}


	public boolean transitivityCheck(LatticeElement thisNode)
	{
		Set<LatticeElement> checkQueue = thisNode.getDescendants();
		boolean ret = true;

		for(LatticeElement latticeElement : checkQueue)
		{
			Concept concept1 = latticeElement.getConcept();
			for(LatticeElement latticeElement2 : checkQueue)
			{
				Concept concept2 = latticeElement2.getConcept();
				if(ConceptUtil.isStrictSuperConcept(concept1, concept2))
				{
					System.out.println("Lattice::transitivityCheck >> Link between descendants of " + thisNode.label() + "(concept = " + thisNode.getConcept() + " )\nconcept1 = " + concept1.toString() + "\nconcept2 = " + concept2.toString());
					ret = false;
				}
			}
		}
		return ret;
	}

	public boolean transitivityCheck1(LatticeElement thisNode)
	{
		Set<LatticeElement> checkQueue = thisNode.getDescendants();
		boolean ret = true;

		for(LatticeElement latticeElement : checkQueue)
		{
			Concept concept1 = latticeElement.getConcept();
			for(LatticeElement latticeElement2 : checkQueue)
			{
				Concept concept2 = latticeElement2.getConcept();
				if(ConceptUtil.isStrictSuperConcept(concept1, concept2))
				{
					System.out.println("Lattice::transitivityCheck >> Link between descendants of " + thisNode.label() + "\nconcept1 = " + concept1.toString() + "\nconcept2 = " + concept2.toString());
					ret = false;
				}
			}
		}
		return ret;
	}

	/**
	 * note that addFromTop has inserted newElement into the lattice where it sits between super and sub already existing. We need to
	 * find each concept that is a subtype of newElement where all ancestors are not supertypes, and add those as descendants of new
	 * (and new as anc of subtype)
	 */
	void addConceptFromBottom(LatticeElement newElement, Concept newConcept)
	{
		LinkedHashSet<LatticeElement> checkQueue = new LinkedHashSet<LatticeElement>();
		checkQueue.add(bottomElement);
		LinkedHashSet<LatticeElement> nextQueue = null;

		for(boolean checkMore = true; checkMore; checkQueue = nextQueue)
		{
			if(LATTICEDEBUG)
				System.out.println("addConceptFromBottom check queue length = " + checkQueue.size());
			checkMore = false;
			nextQueue = new LinkedHashSet<LatticeElement>();
			for(LatticeElement next : checkQueue)
			{
				boolean addedAncestorToQueue = false;
				// checkQueue.remove(next);
				// System.out.println("compare = " + next.getConcept().getIntent().toString());

				if(ConceptUtil.isSuperConcept(newConcept, next.getConcept()))
				{
					// ArrayList <LatticeElement> addAnc = new ArrayList <LatticeElement>(); // avoid changing next->descendants
					boolean nextIsAnc = false; // set to true if we find new == ancestor of next

					for(LatticeElement upperNeighbour : next.getAncestors())
						if(upperNeighbour == newElement)
							nextIsAnc = true;
						else if(ConceptUtil.isSuperConcept(newConcept, upperNeighbour.getConcept()))
						{
							nextQueue.add(upperNeighbour);
							checkMore = true;
							addedAncestorToQueue = true;
							if(LATTICEDEBUG)
								System.out.println("addConceptFromBottom nql = " + nextQueue.size() + " before = " + upperNeighbour.label() + " intent=" + upperNeighbour.getConcept().getIntent());
						}
						else if(ConceptUtil.isSuperConcept(upperNeighbour.getConcept(), newConcept))
							System.err.println("addConceptFromBottom: " + newElement.label() + " should be between = " + next.label() + " and " + upperNeighbour.label());
					// else - NOTHING because we need to know there are no super/sub relations between newElement and next descendants before we add it to list of next desc.
					if((addedAncestorToQueue == false) && (nextIsAnc == false)) // i.e. newElement is not supertype of ANY descendants of next
					{
						newElement.addDesc(next); // we check for upper neighbours in add from top
						next.getAncestors().add(newElement);
						if(LATTICEDEBUG)
							System.out.println("addConceptFromBottom immed before = " + next.label());
					}
					// if(addAnc.size() > 0)
					// next.getAncestors().addAll(addAnc);
				}
			}
		}
		return;
	}

	void addConceptFromTop(LatticeElement newElement, Concept newConcept)
	{
		Set<LatticeElement> checkQueue = new LinkedHashSet<LatticeElement>();
		checkQueue.add(topElement);
		Set<LatticeElement> nextQueue = null;

		for(boolean checkMore = true; checkMore; checkQueue = nextQueue)
		{
			nextQueue = new LinkedHashSet<LatticeElement>();
			checkMore = false;
			for(LatticeElement next : checkQueue)
				if(ConceptUtil.isSuperConcept(next.getConcept(), newConcept))
				{
					ArrayList<LatticeElement> delDesc = new ArrayList<LatticeElement>(); // avoid changing next->descendants
					ArrayList<LatticeElement> addDesc = new ArrayList<LatticeElement>(); // avoid changing next->descendants
					boolean insertedElement = false; // set to true if we find next > newConcept > descendant of next

					for(LatticeElement lowerNeighbour : next.getDescendants())
						if(ConceptUtil.isSuperConcept(lowerNeighbour.getConcept(), newConcept))
						{
							nextQueue.add(lowerNeighbour);
							checkMore = true;
							if(LATTICEDEBUG)
								System.out.println("addConcept after = " + next.getConcept().getIntent());
							// System.out.println("add to queue = " + nextLayer.getConcept().getIntent().toString());
						}
						else if(ConceptUtil.isSuperConcept(newConcept, lowerNeighbour.getConcept()))
						{
							delDesc.add(lowerNeighbour); // next.removeDesc(nextLayer);
							// next.addDesc(newElement);
							newElement.addAnc(next);
							newElement.addDesc(lowerNeighbour);
							lowerNeighbour.removeAnc(next);
							lowerNeighbour.addAnc(newElement);
							insertedElement = true;
							if(LATTICEDEBUG)
								System.out.println("addConcept between (" + next.getDescendants().size() + ")= " + next.getConcept().getIntent() + " and " + lowerNeighbour.getConcept().getIntent());
						}
					// else - NOTHING because we need to know there are no super/sub relations between newElement and next descendants before we add it to list of next desc.
					if((checkMore == false) && (insertedElement == false)) // i.e. newElement is not supertype of ANY descendants of next
					{
						newElement.addAnc(next); // we can check for lower neighbours later
						addDesc.add(newElement);
						if(LATTICEDEBUG)
							System.out.println("addConceptimmed after (" + next.getDescendants().size() + ")= " + next.getConcept().getIntent() + " to del = " + addDesc.size());
					}

					if(delDesc.size() > 0)
					{
						next.addDesc(newElement);
						next.getDescendants().removeAll(delDesc);
					}
					if(addDesc.size() > 0)
						next.getDescendants().addAll(addDesc);
				}
		}
	}
}
