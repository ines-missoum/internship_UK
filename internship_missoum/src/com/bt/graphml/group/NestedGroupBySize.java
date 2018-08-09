package com.bt.graphml.group;

import java.util.ArrayList;
import java.util.List;
import com.bt.graphml.LatticeElement;

/**
 * recursivly groups elements by size following this behaviour:
 * elements that have a extent smaller than mid = min + (max - min) / 2 (min and max being the minimum and maximum sizes) are added to one group,
 * the others to another one.
 * the algorithm is applied on both groups until one of the group contains all the elements.
 * @author Virgile
 *
 */
public class NestedGroupBySize implements GroupMaker
{
	@Override
	public Group makeGroups(List<LatticeElement> lattice)
	{
		Group root = new Group();
		
		if(lattice.size() == 1)
		{
			root.add(lattice.get(0));
			return root;
		}
		
		int max = lattice.get(0).getConcept().getExtent().size(), min = max;
		
		for(LatticeElement e : lattice)
		{
			if(e.getConcept().getExtent().size() > max)
				max = e.getConcept().getExtent().size();
			if(e.getConcept().getExtent().size() < min)
				min = e.getConcept().getExtent().size();
		}
		
		int mid = min + (max - min) / 2;
		List<LatticeElement> smallerThan = new ArrayList<LatticeElement>(), 
							 greaterThan = new ArrayList<LatticeElement>();
		
		for(LatticeElement e : lattice)
			if(e.getConcept().getExtent().size() <= mid)
				smallerThan.add(e);
			else
				greaterThan.add(e);
		
		if(!smallerThan.isEmpty() && !greaterThan.isEmpty())
		{
			root.add(makeGroups(smallerThan));
			root.add(makeGroups(greaterThan));
		}
		else if(smallerThan.isEmpty())
		{
			Group leaf = new Group();
			leaf.addAll(greaterThan);
			root.add(leaf);
		}
		else
		{
			Group leaf = new Group();
			leaf.addAll(smallerThan);
			root.add(leaf);
		}
		
		return root;
	}
}
