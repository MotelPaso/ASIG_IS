package cl.edu.metrics.lexer;

import java.util.Set;

public class DefaultKeywordCatalog implements KeywordCatalog {
    private final Set<String> keywords = Set.of("let", "print");

    @Override
    public boolean isKeyword(String candidate) {
        return keywords.contains(candidate);
    }
}
