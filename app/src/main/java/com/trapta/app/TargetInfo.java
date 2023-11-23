package com.trapta.app;

import java.util.ArrayList;
import java.util.Collections;

public class TargetInfo implements Comparable<TargetInfo> {
	
	private int id;
	private ArrayList<ArcherInfo> archerList = new ArrayList<ArcherInfo>();

	public TargetInfo(int id) {
		this.id = id;
	}
	
	private class ArcherInfo implements Comparable<ArcherInfo> {
		private String name;
		private char letter;
		private boolean trispot;
		
		public String getName() {
			return name;
		}

		public char getLetter() {
			return letter;
		}
		
		public boolean isTrispot() {
			return trispot;
		}

		
		public ArcherInfo(char letter, String name, boolean trispot) {
			this.letter = letter;
			this.name = name;
			this.trispot = trispot;
		}
		
		@Override
		public int compareTo(ArcherInfo another) {
			if (another.letter<letter) return 1;
			if (another.letter>letter) return -1;
			return 0;
		}
	}
	
	public void addArcher(char letter, String name, boolean trispot) {
		archerList.add(new ArcherInfo(letter, name, trispot));
		Collections.sort(archerList);
	}
	
	public int getArcherCount() {
		return archerList.size();
	}
	
	public String getName(int index) {
		if (index<archerList.size()) return archerList.get(index).getName();
		return "?";
		
	}
	
	public boolean isTrispot(int index) {
		if (index<archerList.size()) return archerList.get(index).isTrispot();
		return false;
	}
	
	public char getLetter(int index) {
		if (index<archerList.size()) return archerList.get(index).getLetter();
		return '?';
		
	}
	
	public int getId() {
		return id;
	}

	public ArrayList<ArcherInfo> getArcherList() {
		return archerList;
	}

	@Override
	public int compareTo(TargetInfo another) {
		if (another.getId()<id) return 1;
		if (another.getId()>id) return -1;
		return 0;
	}
	
	

}
