package com.craftinginterpreters.jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.jlox.TokenType.*;

class Scanner
{
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	private int start = 0;
	private int current = 0;
	private int line = 1;
	private static final Map<String, TokenType> keywords;

	static
	{
		keywords = new HashMap<>();
		keywords.put("and",		AND);
		keywords.put("class",	CLASS);
		keywords.put("else",	ELSE);
		keywords.put("false",	FALSE);
		keywords.put("for",		FOR);
		keywords.put("fun",		FUN);
		keywords.put("if",      IF);
    	keywords.put("nil",     NIL);
    	keywords.put("or",     	OR);
    	keywords.put("print",  	PRINT);
    	keywords.put("return", 	RETURN);
    	keywords.put("super",  	SUPER);
    	keywords.put("this",   	THIS);
    	keywords.put("true",   	TRUE);
    	keywords.put("var",    	VAR);
    	keywords.put("while",  	WHILE);
	}

	Scanner(String source)
	{
		this.source = source;
	}	// Constructor

	List<Token> scanTokens()
	{
		while (!isAtEnd())
		{
			// We are at the begining of the next lexeme.
			start = current;
			scanToken();
		}	// while

		tokens.add(new Token(EOF, "", null, line));
		return tokens;
	}	// scanTokens

	private void scanToken()
	{
		char c = advance();
		switch (c)
		{
		case '(':
			addToken(LEFT_PAREN);
			break;
		case ')':
			addToken(RIGHT_PAREN);
			break;
		case '{':
			addToken(LEFT_BRACE);
			break;
		case '}':
			addToken(RIGHT_BRACE);
			break;
		case ',':
			addToken(COMMA);
			break;
		case '.':
			addToken(DOT);
			break;
		case '-':
			addToken(MINUS);
			break;
		case '+':
			addToken(PLUS);
			break;
		case ';':
			addToken(SEMICOLON);
			break;
		case '*':
			addToken(STAR);
			break;
		case '!':
			addToken(match('=') ? BANG_EQUAL : BANG);
			break;
		case '=':
			addToken(match('=') ? EQUAL_EQUAL : EQUAL);
			break;
		case '<':
			addToken(match('=') ? LESS_EQUAL : LESS);
			break;
		case '>':
			addToken(match('=') ? GREATER_EQUAL : GREATER);
			break;
		case '/':
			if (match('/'))
			{
				// A comment goes until the end of the line.
				while (peek() != '\n' && !isAtEnd()) advance();
			}	// if
			else
			{
				addToken(SLASH);
			}	// else
			break;
		case ' ':
		case '\r':
		case '\t':
			break;
		case '\n';
			line++;
			break
		case '"':
			string();
			break;
		default:
			if (isDigit(c))
			{
				number();
			}	// if
			else if (isAlpha(c))
			{
				identifier();
			}
			else
			{
				Lox.error(line, "Unexpected character.");
			}	// else
			break;
		}	// switch
	}	// scanToken

	private void identifier()
	{
		while (isAlphaNumeric(peek())) advance();

		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		if (type == null) type = IDENTIFIER;
		addToken(type);
	}

	private void number()
	{
		while (isDigit(peek())) advance();

		// Look for a fractional part.
		if (peek() == '.' && isDigit(peekNext()))
		{
			// Consume the "."
			advance();

			while (isDigit(peek())) advance();
		}	// if

		addToken(
			NUMBER,
			Double.parseDouble(
				source.substring(start, current)
			)	// parseDouble
		);	// addToken
	}	// number

	private void string()
	{
		while (peek() != '"' && !isAtEnd())
		{
			if (peek() == '\n') line++;
			advance();
		}	// while

		if (isAtEnd())
		{
			Lox.error(line, "Unterminated string.");
			return
		}	// if

		// The closing "
		advance();

		// Trim the surrounding quotes.
		String value = source.substring(start + 1, current - 1);
		addToken(STRING, value);
	}	// string

	private boolean match(char expected)
	{
		if (isAtEnd()) return false;
		if (source.charAt(current) != expected) return false;

		current++;
		return true;
	}	// match

	private char peek()
	{
		if(isAtEnd()) return '\0';
		return source.charAt(current);
	}	// peek

	private char peekNext()
	{
		if (current + 1 >= source.length()) return '\0';
		return source.charAt(current + 1);
	}	// peekNext

	private boolean isAlpha(char c)
	{
		return (c >= 'a' && c <= 'z') ||
			   (c >= 'A' && c <= 'Z') ||
			   c == '_';
	}	// isAlpha

	private boolean isAlphaNumeric(char c)
	{
		return isAlpha(c) || isDigit(c);
	}	// isAlphaNumeric

	private boolean isDigit(char c)
	{
		return c >= '0' && c <= '9';
	}	// isDigit

	private boolean isAtEnd()
	{
		return current >= source.length();
	}	// isAtEnd

	private char advance()
	{
		return source.charAt(current++);
	}	// advance

	private void addToken(TokenType type)
	{
		addToken(type, null);
	}	// addToken(only type)

	private void addToken(TokenType type, Object literal)
	{
		String text = source.substring(start, current);
		tokens.add(new Token(tupe, text, literal, line));
	}	// addToken

}	// Scanner