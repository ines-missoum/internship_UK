package com.bt.graphml.group;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.bt.graphml.LatticeElement;

public class Group
{
	private Group father;
	private Set<Group> subGroups; // groups contained by this group
	private Set<LatticeElement> elements; // elements that don't belong to a subGroup

	public Group()
	{
		this(new HashSet<LatticeElement>());
	}

	public Group(Set<LatticeElement> elements)
	{
		this.elements = elements;
		subGroups = new HashSet<Group>();
	}

	/**
	 * add a subGroup to this group. all the elements contained by the subGroup are removed from this group.
	 * the elements directly contained by g are removed from this
	 * @param g
	 */
	public void add(Group g)
	{
		if(g.father != null)
			g.father.remove(g);
		g.father = this;
		subGroups.add(g);
		elements.removeAll(g.elements);
	}

	/**
	 * add a LatticeElement to this Group. The element is added directly to the group, meaning that it is not add to one of its subgroups.
	 * @param e
	 */
	public void add(LatticeElement e)
	{
		//remove the element from its former father
		if(e.getFather() != null && e.getFather() != this)
			e.getFather().remove(e);
		elements.add(e);
		e.setFather(this); //set the father of the new element to this
	}

	/**
	 * @param e
	 * @return true if the element is directly contained by the group (won't return true if the element is in a subgroup)
	 */
	public boolean contains(LatticeElement e)
	{
		return elements.contains(e);
	}

	/**
	 * @param g
	 * @return true if the group is directly contained by the group (won't return true if the group is in a subgroup)
	 */
	public boolean contains(Group g)
	{
		return subGroups.contains(g);
	}
	
	public void addAll(Collection<LatticeElement> c)
	{
		for(LatticeElement e : c)
			add(e);
	}

	public Set<LatticeElement> getElements()
	{
		return elements;
	}

	public Group getFather()
	{
		return father;
	}

	public Set<Group> getSubGroups()
	{
		return subGroups;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 31 * hash + subGroups.hashCode();
		hash = 31 * hash + elements.hashCode();
		return hash;
	}

	/**
	 * @return the label used as an id in graphML. Father's label is taken in account.
	 */
	public String label()
	{
		if(father == null)
			return "g" + Integer.toHexString(hashCode());
		else
			return father.label() + "::g" + Integer.toHexString(hashCode());
	}

	/**
	 * removes the element from this group only if it is directly contained by this.
	 * @param e
	 * @return
	 */
	public boolean remove(LatticeElement e)
	{
		if(elements.contains(e))
		{
			e.setFather(null);
		}
		return elements.remove(e);
	}

	/**
	 * removes the group from this only if it is directly contained by this.
	 * @param g
	 * @return
	 */
	public boolean remove(Group g)
	{
		if(subGroups.contains(g))
			g.father = null;
		return subGroups.remove(g);
	}

	/**
	 * @return the number of elements in this group, not including the elements of the subgroups
	 */
	public int size()
	{
		return elements.size();
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();

		if(!subGroups.isEmpty())
			b.append("subGroups:\n");
		for(Group g : subGroups)
			b.append(g).append('\n');

		if(!elements.isEmpty())
			b.append("elements:\n");
		for(LatticeElement e : elements)
			b.append(e).append('\n');

		return b.toString();
	}

	/**
	 * remove the given group from the subgroups and add all its elements to this group
	 * 
	 * @param g
	 */
	public void ungroup(Group g)
	{
		if(subGroups.contains(g))
		{
			subGroups.remove(g);
			for(LatticeElement e : g.elements)
				e.setFather(this);
			elements.addAll(g.elements);
		}
	}
	
	/**
	 * @return true both the set of subGroup and elements are empty
	 */
	public boolean isEmpty()
	{
		return subGroups.isEmpty() && elements.isEmpty();
	}
}
