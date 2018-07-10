package com.bt.graphml;

import java.util.LinkedHashSet;
import java.util.Set;
import com.bt.graphml.group.Group;
import com.bt.lattice.Concept;

public class LatticeElement
{
	private Group father;
	private Concept thisConcept;
	private Set<LatticeElement> ancestors;
	private Set<LatticeElement> descendants;

	public LatticeElement(Concept theCon)
	{
		thisConcept = theCon;
		ancestors = new LinkedHashSet<LatticeElement>();
		descendants = new LinkedHashSet<LatticeElement>();
	}

	public void addAnc(LatticeElement anc)
	{
		// if(!ancestors.contains(anc)) // unnecessary
		ancestors.add(anc); // NB should we check for existing descendants that are supersets of desc ??
	}

	public void addDesc(LatticeElement desc)
	{
		// if(!descendants.contains(desc))
		descendants.add(desc); // NB should we check for existing descendants that are subsets of desc ??
	}

	public Set<LatticeElement> getAncestors()
	{
		return ancestors;
	}

	public Concept getConcept()
	{
		return thisConcept;
	}

	public Set<LatticeElement> getDescendants()
	{
		return descendants;
	}

	public Group getFather()
	{
		return father;
	}

	public void setFather(Group father)
	{
		if(this.father != null)
			this.father.remove(this);
		this.father = father;
		if(father != null && !father.contains(this))
			father.add(this);
	}
	
	@Override
	public int hashCode()
	{
		return thisConcept.hashCode();
	}

	public String label()
	{
		if(father == null)
			return "n" + Integer.toHexString(hashCode());
		else
			return father.label() + "::n" + Integer.toHexString(hashCode());
	}

	public String latticeNeighbourString()
	{
		StringBuilder ans = new StringBuilder(label() + "\nAncestors = ");

		for(LatticeElement latticeElement : ancestors)
			ans.append(latticeElement.label() + " ");
		ans.append("\nDescendants = ");

		for(LatticeElement latticeElement : descendants)
			ans.append(latticeElement.label() + " ");
		ans.append("\n");

		return ans.toString();
	}

	public void removeAnc(LatticeElement anc)
	{
		// if(ancestors.contains(anc)) // unnecessary
		ancestors.remove(anc); // should we throw error ??
	}

	public void removeDesc(LatticeElement desc)
	{
		// if(descendants.contains(desc)) // unnecessary
		descendants.remove(desc); // should we throw error ??
	}

	@Override
	public String toString()
	{
		return thisConcept.toString();
	}
}
