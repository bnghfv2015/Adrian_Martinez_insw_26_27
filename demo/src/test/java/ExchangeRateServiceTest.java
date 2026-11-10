import com.uem.service.ExchangeRateService;
import com.uem.service.ExchangeRateService.ExchangeRate;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// Se prueba solo la lectura del JSON, asi los tests no dependen de internet
public class ExchangeRateServiceTest {

    private final ExchangeRateService service = new ExchangeRateService();

    @Test
    public void testParseValidResponse() throws IOException {
        String json = "{\"amount\":1.0,\"base\":\"EUR\",\"date\":\"2026-11-06\",\"rates\":{\"USD\":1.1629}}";

        ExchangeRate rate = service.parse(json);

        assertEquals(1.1629, rate.getRate(), 0.00001);
        assertEquals("2026-11-06", rate.getDate());
    }

    @Test
    public void testParseResponseWithoutUsd() {
        String json = "{\"amount\":1.0,\"base\":\"EUR\",\"date\":\"2026-11-06\",\"rates\":{\"GBP\":0.87}}";

        assertThrows(IOException.class, () -> service.parse(json));
    }
}