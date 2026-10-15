import com.uem.Article;
import com.uem.Order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrdersJsonTest {

    @Test
    public void testLoadOrders() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        try (InputStream input = getClass().getResourceAsStream("/orders.json")) {

            assertNotNull(input);

            List<Order> orders = mapper.readValue(
                    input,
                    new TypeReference<List<Order>>() {
                    });

            assertEquals(5, orders.size());

            assertEquals("O001", orders.get(0).getId());
            assertEquals("O002", orders.get(1).getId());
            assertEquals("O003", orders.get(2).getId());
            assertEquals("O004", orders.get(3).getId());
            assertEquals("O005", orders.get(4).getId());
        }
    }

    @Test
    public void testArticlesLoadedCorrectly() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        try (InputStream input = getClass().getResourceAsStream("/orders.json")) {

            assertNotNull(input);

            List<Order> orders = mapper.readValue(
                    input,
                    new TypeReference<List<Order>>() {
                    });

            assertEquals(3, orders.get(0).getArticulos().size());
            assertEquals(2, orders.get(1).getArticulos().size());
            assertEquals(2, orders.get(2).getArticulos().size());
            assertEquals(1, orders.get(3).getArticulos().size());
            assertEquals(4, orders.get(4).getArticulos().size());

            Article article = orders.get(0).getArticulos().get(0);

            assertEquals("Keyboard", article.getNombre());
            assertEquals(2, article.getCantidad());
            assertEquals(50.0, article.getPrecio());
            assertEquals(10.0, article.getDescuento());
        }
    }

    @Test
    public void testOrderCalculations() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        try (InputStream input = getClass().getResourceAsStream("/orders.json")) {

            assertNotNull(input);

            List<Order> orders = mapper.readValue(
                    input,
                    new TypeReference<List<Order>>() {
                    });

            Order order = orders.get(0);

            assertEquals(150.0, order.getGrossTotal(), 0.0001);
            assertEquals(139.0, order.getDiscountedTotal(), 0.0001);
        }
    }
}