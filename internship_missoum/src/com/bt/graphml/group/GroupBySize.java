package com.bt.graphml.group;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.bt.graphml.LatticeElement;

/**
 * group the elements by size. elements that are the only one of their size are left alone.
 * @author Virgile
 *
 */
public class GroupBySize implements GroupMaker
{
	@Override
	public Group makeGroups(List<LatticeElement> lattice)
	{
		Group root = new Group();
		Map<Integer, Group> groups = new LinkedHashMap<Integer, Group>();

		for(LatticeElement e : lattice)
		{
			int size = e.getConcept().getExtent().size();
			if(!groups.containsKey(size))
				groups.put(size, new Group());
			groups.get(size).add(e);
		}

		for(Entry<Integer, Group> e : groups.entrySet())
			if(e.getValue().size() == 1)
				root.addAll(e.getValue().getElements());
			else
				root.add(e.getValue());

		return root;
	}
}
