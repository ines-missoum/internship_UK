package com.bt.graphml.group;

import java.util.List;
import com.bt.graphml.LatticeElement;

public interface GroupMaker
{
	/**
	 * create a group directly or indirectly containing all the given elements.
	 * the group directly contains an element if the element is part of the group and not of one of the subgroup.
	 * an element cannot be contained by more than one group.
	 * @param lattice
	 * @return
	 */
	public Group makeGroups(List<LatticeElement> lattice);
}
