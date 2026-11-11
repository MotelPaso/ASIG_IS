package cl.edu.metrics.lexer;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class KeywordCatalogMockTest {
    private KeywordCatalog catalog;
    private LexicalAnalyzer lexer;

    @Before
    public void init() {
        catalog = mock(KeywordCatalog.class);
        lexer = new LexicalAnalyzer(catalog);
        when(catalog.isKeyword("custom")).thenReturn(true);
        when(catalog.isKeyword("other")).thenReturn(false);
    }

    @Test
    public void testCatalogControlsClassification() {
        assertEquals(List.of(
                new Token(TokenType.KEYWORD, "custom", 0),
                new Token(TokenType.IDENTIFIER, "other", 7)), lexer.tokenize("custom other"));
        verify(catalog).isKeyword("custom");
        verify(catalog).isKeyword("other");
    }
}
