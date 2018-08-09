package com.bt.lattice;

public class Element implements Comparable<Element>
{
	private int index; //index of the object in the lattice
	private double mu; //membership
	private Lattice lattice; //the lattice containing the extent containing this element

	public Element(Lattice l, int index, double mu)
	{
		this.index = index;
		this.mu = mu;
		lattice = l;
	}

	/**
	 * compares the elements names. Used to sort extents.
	 * O(name length)
	 */
	@Override
	public int compareTo(Element e)
	{
		return getName().compareTo(e.getName());
	}

	/**
	 * returns true if both mu and name are equal
	 * O(name lenght)
	 */
	@Override
	public boolean equals(Object o)
	{
		if(o == this)
			return true;
		else if((o == null) || (o.getClass() != getClass()))
			return false;
		else
			return ((Element) o).mu == mu && getName().equals((((Element) o).getName()));
	}

	public int getIndex()
	{
		return index;
	}

	public double getMu()
	{
		return mu;
	}
	public void setMu(double newM)		// only needed for expt union code
	{
		 mu = newM;
	}

	public String getName()
	{
		return lattice.getObjects()[index];
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		long bits = Double.doubleToLongBits(mu);
		hash = 31 * hash + (int) (bits ^ (bits >>> 32));
		hash = 31 * hash + getName().hashCode();
		return hash;
	}

	@Override
	public String toString()
	{
		if(mu == 1)
			return getName();
		else
			return getName() + "/" + mu;
	}
}
