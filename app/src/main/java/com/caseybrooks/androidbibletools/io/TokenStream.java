package com.caseybrooks.androidbibletools.io;

import java.util.LinkedList;
import java.util.Stack;

public class TokenStream {
	LinkedList<Character> chars;
	Stack<Token> ungetTokens;

	public TokenStream(String expression) {
		String toParse = expression.replaceAll("\\s+", "~");
		chars = new LinkedList<>();
		for(int i = 0; i < toParse.length(); i++) {
			chars.add(toParse.charAt(i));
		}
		ungetTokens = new Stack<>();
	}

	public Token get() {
		try {
			if(ungetTokens.size() > 0) {
				return ungetTokens.pop();
			}
			else if(chars.size() > 0) {
				char ch = chars.removeFirst();
				String s;

				switch(ch) {
				case '~':
					return get();
				case ':':
					return new Token(Token.Type.COLON);
				case ';':
					return new Token(Token.Type.SEMICOLON);
				case ',':
					return new Token(Token.Type.COMMA);
				case '-':
					return new Token(Token.Type.DASH);
				case '.':
					return new Token(Token.Type.DOT);
				case '/':
					return new Token(Token.Type.SLASH);
				case '\\':
					return new Token(Token.Type.BACKSLASH);
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					s = "";
					s += ch;
					while(chars.size() > 0 && chars.getFirst() != null &&
							Character.isDigit(chars.getFirst())) {
						s += chars.removeFirst();
					}
					return new Token(Token.Type.NUMBER, Integer.parseInt(s));
				default:
					s = "";
					s += ch;
					while(chars.size() > 0 && chars.getFirst() != null &&
							Character.isLetter(chars.getFirst())) {
						s += chars.removeFirst();
					}

					//we have our word, but we need to ensure we have removed all
					// nonword characters that may have gotten through the above lexing cases
					s = s.replaceAll("\\W", "");

					return new Token(Token.Type.WORD, s);
				}
			}
			else {
				return null;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void unget(Token token) {
		ungetTokens.push(token);
	}

	@Override
	public String toString() {
		String s = "";
		for(int i = 0; i < chars.size(); i++) {
			s += chars.get(i);
		}
		return s;
	}
}
