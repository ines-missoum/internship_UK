package com.bt.logfilesAnalysis;

import java.util.ArrayList;

public class Pattern {

	private ArrayList<String> listRegExp = new ArrayList<String>();

	public Pattern(ArrayList<String> listRegExp) {
		super();
		this.listRegExp = listRegExp;
	}

	public String getRegExp(int i) {
		return listRegExp.get(i);
	}

	public void setListRegExp(ArrayList<String> listRegExp) {
		this.listRegExp = listRegExp;
	}
	

}
