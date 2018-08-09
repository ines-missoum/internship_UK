package com.bt.graphml.group;
import java.util.List;
import java.util.Random;
import com.bt.graphml.LatticeElement;

public class NoGrouping  implements GroupMaker
	{
		@Override
		public Group makeGroups(List<LatticeElement> lattice)
		{
			Group root = new Group();
			
			for(LatticeElement e : lattice)
				root.add(e);
			
	
			
			return root;
		}
	}
