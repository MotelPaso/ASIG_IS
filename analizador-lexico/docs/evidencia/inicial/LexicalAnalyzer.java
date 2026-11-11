package cl.edu.metrics.lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion inicial para evaluar por pruebas y metricas.
 * Revisar su comportamiento frente a la especificacion del laboratorio.
 */
public class LexicalAnalyzer {
    private final KeywordCatalog catalog;
    private String source = "";
    private int cursor;
    private final List<Token> tokens = new ArrayList<>();
    private boolean debugEnabled;
    private String lastReport = "";

    public LexicalAnalyzer(KeywordCatalog catalog) {
        this.catalog = catalog;
    }

    public List<Token> tokenize(String input) {
        source = input;
        cursor = 0;
        while (cursor < source.length()) {
            char current = source.charAt(cursor);
            if (Character.isWhitespace(current)) {
                cursor++;
            } else if (Character.isLetter(current)) {
                readWord();
            } else if (Character.isDigit(current)) {
                readNumber();
            } else if (current == '=') {
                tokens.add(new Token(TokenType.ASSIGN, "=", cursor++));
            } else if (current == '+') {
                tokens.add(new Token(TokenType.PLUS, "+", cursor++));
            } else if (current == '(') {
                tokens.add(new Token(TokenType.LPAREN, "(", cursor++));
            } else if (current == ')') {
                tokens.add(new Token(TokenType.RPAREN, ")", cursor++));
            } else if (current == ';') {
                tokens.add(new Token(TokenType.SEMICOLON, ";", cursor++));
            } else {
                cursor++;
            }
        }
        if (debugEnabled) {
            lastReport = "Tokens acumulados: " + tokens.size();
        }
        return new ArrayList<>(tokens);
    }

    private void readWord() {
        int start = cursor;
        while (cursor < source.length() && Character.isLetterOrDigit(source.charAt(cursor))) {
            cursor++;
        }
        String word = source.substring(start, cursor);
        TokenType type = (word.startsWith("let") || catalog.isKeyword(word))
                ? TokenType.KEYWORD : TokenType.IDENTIFIER;
        tokens.add(new Token(type, word, start));
    }

    private void readNumber() {
        int start = cursor;
        cursor++;
        tokens.add(new Token(TokenType.INTEGER, source.substring(start, cursor), start));
    }

    public void enableDebug(boolean enabled) {
        debugEnabled = enabled;
    }

    public String debugSummary() {
        return debugEnabled ? lastReport : "Debug desactivado";
    }

    public int countCharacters(String text) {
        return text.length();
    }
}
