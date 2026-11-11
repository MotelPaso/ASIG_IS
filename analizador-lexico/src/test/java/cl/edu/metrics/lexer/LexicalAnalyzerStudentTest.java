package cl.edu.metrics.lexer;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LexicalAnalyzerStudentTest {
    private LexicalAnalyzer lexer;

    @Before
    public void init() {
        lexer = new LexicalAnalyzer(new DefaultKeywordCatalog());
    }

    @Test
    public void test01EmptyInput() {
        assertTokens(List.of(), lexer.tokenize(""));
    }

    @Test
    public void test02ExactKeyword() {
        assertTokens(List.of(
                new Token(TokenType.KEYWORD, "let", 0),
                new Token(TokenType.KEYWORD, "print", 4)), lexer.tokenize("let print"));
    }

    @Test
    public void test03IdentifierStartingWithLet() {
        assertTokens(List.of(new Token(TokenType.IDENTIFIER, "letter", 0)), lexer.tokenize("letter"));
    }

    @Test
    public void test04IdentifierWithUnderscore() {
        assertTokens(List.of(new Token(TokenType.IDENTIFIER, "_total2", 0)), lexer.tokenize("_total2"));
    }

    @Test
    public void test05MultiDigitInteger() {
        assertTokens(List.of(new Token(TokenType.INTEGER, "2026", 0)), lexer.tokenize("2026"));
    }

    @Test
    public void test06Punctuation() {
        assertTokens(List.of(
                new Token(TokenType.ASSIGN, "=", 0),
                new Token(TokenType.PLUS, "+", 1),
                new Token(TokenType.LPAREN, "(", 2),
                new Token(TokenType.RPAREN, ")", 3),
                new Token(TokenType.SEMICOLON, ";", 4)), lexer.tokenize("=+();"));
    }

    @Test
    public void test07CompleteExpression() {
        assertTokens(List.of(
                new Token(TokenType.KEYWORD, "let", 0),
                new Token(TokenType.IDENTIFIER, "total", 4),
                new Token(TokenType.ASSIGN, "=", 10),
                new Token(TokenType.INTEGER, "12", 12),
                new Token(TokenType.PLUS, "+", 15),
                new Token(TokenType.IDENTIFIER, "valor", 17),
                new Token(TokenType.SEMICOLON, ";", 22)), lexer.tokenize("let total = 12 + valor;"));
    }

    @Test
    public void test08WhitespaceAndPositions() {
        assertTokens(List.of(
                new Token(TokenType.KEYWORD, "print", 2),
                new Token(TokenType.LPAREN, "(", 8),
                new Token(TokenType.IDENTIFIER, "x", 10),
                new Token(TokenType.RPAREN, ")", 12)), lexer.tokenize(" \nprint ( x )"));
    }

    @Test
    public void test09InvalidCharacter() {
        assertTokens(List.of(
                new Token(TokenType.IDENTIFIER, "abc", 0),
                new Token(TokenType.ERROR, "@", 3),
                new Token(TokenType.INTEGER, "5", 4)), lexer.tokenize("abc@5"));
    }

    @Test
    public void test10ConsecutiveCalls() {
        lexer.tokenize("x");
        assertTokens(List.of(new Token(TokenType.IDENTIFIER, "y", 0)), lexer.tokenize("y"));
    }

    private static void assertTokens(List<Token> expected, List<Token> actual) {
        assertEquals(expected, actual);
    }
}
