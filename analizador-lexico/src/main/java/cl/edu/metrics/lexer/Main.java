package cl.edu.metrics.lexer;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        LexicalAnalyzer analyzer = new LexicalAnalyzer(new DefaultKeywordCatalog());
        LexicalReportService report = new LexicalReportService(analyzer, new DefaultTokenFormatter());
        String input = args.length == 0 ? "let total = 12 + valor;" : String.join(" ", args);
        System.out.println("Entrada: " + input);
        for (String line : report.analyze(input)) {
            System.out.println(line);
        }
    }
}
