import com.uem.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArticleTest {

    private Article article;

    @BeforeEach
    public void setUp() {
        article = new Article("Teclado", 2, 30.0, 10.0);
    }

    @Test
    public void testGetGrossAmount() {
        assertEquals(60.0, article.getGrossAmount());
    }

    @Test
    public void testGetDiscountedAmount() {
        assertEquals(54.0, article.getDiscountedAmount());
    }

    @Test
    public void testGetters() {
        assertEquals("Teclado", article.getNombre());
        assertEquals(2, article.getCantidad());
        assertEquals(30.0, article.getPrecio());
        assertEquals(10.0, article.getDescuento());
    }

    @Test
    public void testSetters() {
        article.setNombre("Raton");
        article.setCantidad(3);
        article.setPrecio(20.0);
        article.setDescuento(5.0);

        assertEquals("Raton", article.getNombre());
        assertEquals(3, article.getCantidad());
        assertEquals(20.0, article.getPrecio());
        assertEquals(5.0, article.getDescuento());
    }

    @Test
    public void testToString() {
        String resultado = article.toString();

        assertTrue(resultado.contains("Teclado"));
        assertTrue(resultado.contains("2"));
        assertTrue(resultado.contains("30.0"));
        assertTrue(resultado.contains("10.0"));
    }

}