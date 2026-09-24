import com.uem.Calculator;
import com.uem.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculatorTestCase {

    public Calculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new Calculator();
    }

    @Test
    void multiplyTest() {
        assertEquals(6, calculator.multiply(2, 3));
    }

    @Test
    void multiplyByZeroTest() {
        assertEquals(0, calculator.multiply(0, 3));
    }

    @Test
    void multiplyNegativeTest() {
        assertEquals(-6, calculator.multiply(-2, 3));
    }

    @Test
    void concatTest() {
        assertEquals("HelloWorld", calculator.concat("Hello", "World"));
    }

    @Test
    void concatNullTest() {
        assertEquals(Calculator.EMPTY, calculator.concat("Hello", null));
    }

    @Test
    void sumTest() {
        assertEquals(5.0, calculator.sum(2.0, 3.0));
    }

    @Test
    void sumNegativeTest() {
        assertEquals(-1.0, calculator.sum(2.0, -3.0));
    }

    @Test
    void discountTest() {
        assertEquals(80.0, calculator.discount(100.0, 20.0));
    }

    @Test
    void discountZeroPercentTest() {
        assertEquals(100.0, calculator.discount(100.0, 0.0));
    }

    @Test
    void discountHundredPercentTest() {
        assertEquals(0.0, calculator.discount(100.0, 100.0));
    }

    @Test
    void discountInvalidPercentTest() {

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.discount(100.0, -10.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.discount(100.0, 110.0);
        });
    }

    @Test
    void calculateTotalTest() {
        List<Double> amounts = List.of(10.0, 20.0, 30.0);

        assertEquals(60.0, calculator.calculateTotal(amounts));
    }

    @Test
    void calculateTotalEmptyListTest() {
        List<Double> amounts = List.of();

        assertEquals(0.0, calculator.calculateTotal(amounts));
    }

    @Test
    void mainTest() {
        assertDoesNotThrow(() -> {
            Main.main(new String[] {});
        });
    }
}