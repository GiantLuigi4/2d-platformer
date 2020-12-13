package com.tfc.platformer.utils;

import java.io.InputStream;
import java.util.ArrayList;

public class CSVReader {
	private final ArrayList<String[]> rows = new ArrayList<>();
	
	public CSVReader(String file) {
		for (String s : file.split("\n"))
			rows.add(s.split(","));
	}
	
	public CSVReader(InputStream stream) {
		String file = read(stream);
		for (String s : file.split("\n")) {
			rows.add(s.trim().split(","));
		}
	}
	
	private static String read(InputStream stream) {
		byte[] bytes = null;
		try {
			bytes = new byte[stream.available()];
			stream.read(bytes);
			stream.close();
		} catch (Throwable ignored) {
		}
		assert bytes != null;
		return new String(bytes);
	}
	
	public int countRows() {
		return rows.size();
	}
	
	public String[] getRow(int number) {
		return rows.get(number);
	}
}
