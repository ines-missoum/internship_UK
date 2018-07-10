package com.bt.graphml.group;

import java.util.List;
import java.util.Random;
import com.bt.graphml.LatticeElement;

/**
 * randomly group the given elements
 * @author Virgile
 *
 */
public class GroupRandomly implements GroupMaker
{
	@Override
	public Group makeGroups(List<LatticeElement> lattice)
	{
		Random rand = new Random();
		Group root = new Group();
		Group[] groups;
		
		int size = 0;
		while(size < 2)
			size = rand.nextInt(lattice.size());
		
		groups = new Group[size];
		
		for(int i = 0; i < groups.length; i++)
			groups[i] = new Group();
		
		for(LatticeElement e : lattice)
			groups[rand.nextInt(groups.length)].add(e);
		
		for(Group g : groups)
			if(!g.isEmpty())
				root.add(g);
		
		return root;
	}
}
