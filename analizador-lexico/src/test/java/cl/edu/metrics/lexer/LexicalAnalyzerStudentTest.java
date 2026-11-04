package cl.edu.metrics.lexer;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Actividad: reemplace @Ignore por @Test activo y escriba las aserciones.
 * Los contratos esperados se describen en docs/GUIA_ESTUDIANTES.md.
 * Se dejan cuerpos vacios intencionalmente: NO son soluciones.
 */
public class LexicalAnalyzerStudentTest {
    private LexicalAnalyzer lexer;

    @Before
    public void init() {
        lexer = new LexicalAnalyzer(new DefaultKeywordCatalog());
    }

    @Ignore("Pendiente 01: cadena vacia") @Test
    public void test01EmptyInput() { fail("Implementar test01"); }

    @Ignore("Pendiente 02: palabra reservada exacta") @Test
    public void test02ExactKeyword() { fail("Implementar test02"); }

    @Ignore("Pendiente 03: identificador con prefijo let") @Test
    public void test03IdentifierStartingWithLet() { fail("Implementar test03"); }

    @Ignore("Pendiente 04: identificador con guion bajo") @Test
    public void test04IdentifierWithUnderscore() { fail("Implementar test04"); }

    @Ignore("Pendiente 05: entero de varios digitos") @Test
    public void test05MultiDigitInteger() { fail("Implementar test05"); }

    @Ignore("Pendiente 06: simbolos") @Test
    public void test06Punctuation() { fail("Implementar test06"); }

    @Ignore("Pendiente 07: expresion completa") @Test
    public void test07CompleteExpression() { fail("Implementar test07"); }

    @Ignore("Pendiente 08: espacios y saltos") @Test
    public void test08WhitespaceAndPositions() { fail("Implementar test08"); }

    @Ignore("Pendiente 09: caracter invalido") @Test
    public void test09InvalidCharacter() { fail("Implementar test09"); }

    @Ignore("Pendiente 10: independencia entre llamadas") @Test
    public void test10ConsecutiveCalls() { fail("Implementar test10"); }
}
