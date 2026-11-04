package cl.edu.metrics.lexer;

import java.util.ArrayList;
import java.util.List;

/** Separa el formato de salida de la tokenizacion. */
public class LexicalReportService {
    private final LexicalAnalyzer analyzer;
    private final TokenFormatter formatter;

    public LexicalReportService(LexicalAnalyzer analyzer, TokenFormatter formatter) {
        this.analyzer = analyzer;
        this.formatter = formatter;
    }

    public List<String> analyze(String source) {
        List<String> lines = new ArrayList<>();
        for (Token token : analyzer.tokenize(source)) {
            lines.add(formatter.format(token));
        }
        return lines;
    }
}
