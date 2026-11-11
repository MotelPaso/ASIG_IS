package cl.edu.metrics.lexer;

import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {
    private final KeywordCatalog catalog;

    public LexicalAnalyzer(KeywordCatalog catalog) {
        this.catalog = catalog;
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int cursor = 0;
        while (cursor < input.length()) {
            char current = input.charAt(cursor);
            if (isWhitespace(current)) {
                cursor++;
            } else if (isIdentifierStart(current)) {
                cursor = readWord(input, cursor, tokens);
            } else if (isAsciiDigit(current)) {
                cursor = readNumber(input, cursor, tokens);
            } else {
                tokens.add(new Token(symbolType(current), String.valueOf(current), cursor));
                cursor++;
            }
        }
        return tokens;
    }

    private int readWord(String input, int start, List<Token> tokens) {
        int cursor = start + 1;
        while (cursor < input.length() && isIdentifierPart(input.charAt(cursor))) {
            cursor++;
        }
        String word = input.substring(start, cursor);
        TokenType type = catalog.isKeyword(word) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
        tokens.add(new Token(type, word, start));
        return cursor;
    }

    private static int readNumber(String input, int start, List<Token> tokens) {
        int cursor = start + 1;
        while (cursor < input.length() && isAsciiDigit(input.charAt(cursor))) {
            cursor++;
        }
        tokens.add(new Token(TokenType.INTEGER, input.substring(start, cursor), start));
        return cursor;
    }

    private static TokenType symbolType(char current) {
        return switch (current) {
            case '=' -> TokenType.ASSIGN;
            case '+' -> TokenType.PLUS;
            case '(' -> TokenType.LPAREN;
            case ')' -> TokenType.RPAREN;
            case ';' -> TokenType.SEMICOLON;
            default -> TokenType.ERROR;
        };
    }

    private static boolean isWhitespace(char current) {
        return current == ' ' || current == '\t' || current == '\n' || current == '\r';
    }

    private static boolean isAsciiLetter(char current) {
        return current >= 'A' && current <= 'Z' || current >= 'a' && current <= 'z';
    }

    private static boolean isAsciiDigit(char current) {
        return current >= '0' && current <= '9';
    }

    private static boolean isIdentifierStart(char current) {
        return isAsciiLetter(current) || current == '_';
    }

    private static boolean isIdentifierPart(char current) {
        return isIdentifierStart(current) || isAsciiDigit(current);
    }
}
