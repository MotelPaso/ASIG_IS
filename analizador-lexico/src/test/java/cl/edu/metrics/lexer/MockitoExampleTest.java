package cl.edu.metrics.lexer;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

/** Ejemplo resuelto: patron @Before + mock + when + thenReturn + verify. */
public class MockitoExampleTest {
    private TokenFormatter formatter;
    private LexicalReportService service;

    @Before
    public void init() {
        formatter = mock(TokenFormatter.class);
        LexicalAnalyzer analyzer = new LexicalAnalyzer(new DefaultKeywordCatalog());
        service = new LexicalReportService(analyzer, formatter);
        when(formatter.format(any(Token.class))).thenReturn("FORMATEADO");
    }

    @Test
    public void testFormatterIsInvoked() {
        List<String> result = service.analyze("x = 1;");
        assertEquals(4, result.size());
        assertEquals("FORMATEADO", result.get(0));
        verify(formatter, times(4)).format(any(Token.class));
    }
}
