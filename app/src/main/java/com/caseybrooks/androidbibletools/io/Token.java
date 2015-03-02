package com.caseybrooks.androidbibletools.io;

public class Token {
	public enum Type {
		WORD,
		NUMBER,
		COLON,
		SEMICOLON,
		COMMA,
		DASH,
		DOT,
		WHITESPACE,
	}


	private Type type;
	private String stringValue;
	private int intValue;

	Token(Type type) {
		this.type = type;
	}

	Token(Type type, String value) {
		this.type = type;
		this.stringValue = value;
	}

	Token(Type type, int value) {
		this.type = type;
		this.intValue = value;
	}

	public String getStringValue() { return stringValue; }
	public int getIntValue() { return intValue; }
	public boolean equals(Type type) { return this.type == type; }
}
