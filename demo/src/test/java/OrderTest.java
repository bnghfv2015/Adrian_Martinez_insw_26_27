import com.uem.Article;
import com.uem.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private Order order;
    private List<Article> articulos;

    @BeforeEach
    public void setUp() {
        articulos = new ArrayList<>();

        articulos.add(new Article("Teclado", 2, 30.0, 10.0));
        articulos.add(new Article("Raton", 1, 20.0, 10.0));

        order = new Order("001", articulos);
    }

    @Test
    public void testGetGrossTotal() {
        assertEquals(80.0, order.getGrossTotal());
    }

    @Test
    public void testGetDiscountedTotal() {
        assertEquals(72.0, order.getDiscountedTotal());
    }

    @Test
    public void testGetters() {
        assertEquals("001", order.getId());
        assertEquals(articulos, order.getArticulos());
    }

    @Test
    public void testSetters() {
        List<Article> nuevosArticulos = new ArrayList<>();
        nuevosArticulos.add(new Article("Monitor", 1, 150.0, 20.0));

        order.setId("002");
        order.setArticulos(nuevosArticulos);

        assertEquals("002", order.getId());
        assertEquals(nuevosArticulos, order.getArticulos());
    }

    @Test
    public void testToString() {
        String resultado = order.toString();

        assertTrue(resultado.contains("001"));
        assertTrue(resultado.contains("Teclado"));
        assertTrue(resultado.contains("Raton"));
    }
}