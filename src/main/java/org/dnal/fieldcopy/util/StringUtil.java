package org.dnal.fieldcopy.util;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class StringUtil {
	private static String eol = null;
	
	public static String eol() {
		if (eol == null) {
			eol = System.getProperty("line.separator");
		}
		return eol;
	}

    public static String uppify(String name) {
        if (name.length() <= 1) {
            return name.toUpperCase();
        } else {
            String first = name.substring(0, 1).toUpperCase();
            return first + name.substring(1);
        }
    }
    public static String lowify(String name) {
        if (name.length() <= 1) {
            return name.toLowerCase();
        } else {
            String first = name.substring(0, 1).toLowerCase();
            return first + name.substring(1);
        }
    }
	

	public static boolean isNullOrEmpty(String s) {
		if (s == null) {
			return true;
		}
		return s.isEmpty();
	}
	public static boolean hasText(String s) {
		return ! isNullOrEmpty(s);
	}
	
	public static String flatten(List<String> list) {
		StringJoiner joiner = new StringJoiner(",");
		for(String s: list) {
			joiner.add(s.trim());
		}
		return joiner.toString();
	}
	public static String flattenNoComma(List<String> list) {
		StringJoiner joiner = new StringJoiner(" ");
		for(String s: list) {
			joiner.add(s.trim());
		}
		return joiner.toString();
	}
	public static String flattenEx(List<String> list, String delim) {
		StringJoiner joiner = new StringJoiner(delim);
		for(String s: list) {
			joiner.add(s.trim());
		}
		return joiner.toString();
	}
	
	public static String convertToSingleString(List<String> list) {
		StringJoiner joiner = new StringJoiner("\n");
		list.stream().forEach(s -> joiner.add(s));
		return joiner.toString();
	}
	
	public static String atMostChars(String s, int maxlen) {
		if (s.length() > maxlen) {
			String shortened = String.format("%s...", s.subSequence(0, maxlen));
			return shortened;
		}
		return s;
	}

	public static String createFilled(int n, char ch) {
		char[] charArray = new char[n];
		Arrays.fill(charArray, ch);
		String newString = new String(charArray);
		return newString;
	}
	public static String createSpace(int n) {
		return createFilled(n, ' ');
	}

	public static String removeDoubleQuotes(String s) {
		if (s != null && s.startsWith("\"")) {
			int len = s.length();
			return s.substring(1, len - 1);
		}
		return s;
	}

}
