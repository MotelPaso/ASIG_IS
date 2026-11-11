package cl.edu.metrics.lexer;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class LexicalAnalyzerExtraTest {
    private LexicalAnalyzer lexer;

    @Before
    public void init() {
        lexer = new LexicalAnalyzer(new DefaultKeywordCatalog());
    }

    @Test
    public void test11CaseSensitivity() {
        assertEquals(List.of(
                new Token(TokenType.IDENTIFIER, "PRINT", 0),
                new Token(TokenType.KEYWORD, "let", 6)), lexer.tokenize("PRINT let"));
    }

    @Test
    public void test12WordBoundary() {
        assertEquals(List.of(
                new Token(TokenType.IDENTIFIER, "let2", 0),
                new Token(TokenType.IDENTIFIER, "letx", 5),
                new Token(TokenType.IDENTIFIER, "letter", 10)), lexer.tokenize("let2 letx letter"));
    }

    @Test
    public void test13UnderscoreAndLeadingDigit() {
        assertEquals(List.of(
                new Token(TokenType.IDENTIFIER, "a_b", 0),
                new Token(TokenType.INTEGER, "1", 4),
                new Token(TokenType.IDENTIFIER, "abc", 5)), lexer.tokenize("a_b 1abc"));
    }

    @Test
    public void test14ConsecutiveErrors() {
        assertEquals(List.of(
                new Token(TokenType.ERROR, "@", 0),
                new Token(TokenType.ERROR, "@", 1)), lexer.tokenize("@@"));
    }

    @Test
    public void test15NonAsciiIsError() {
        assertEquals(List.of(
                new Token(TokenType.IDENTIFIER, "caf", 0),
                new Token(TokenType.ERROR, "é", 3),
                new Token(TokenType.ERROR, "ñ", 5)), lexer.tokenize("café ñ"));
    }
}
