package it.lab;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class CatalogRulesTest {
    @Test void zeroStockIsSoldOut() { assertEquals("SOLD_OUT",CatalogRules.status(0)); }
    @Test void positiveStockIsAvailable() { assertEquals("AVAILABLE",CatalogRules.status(1)); }
    @Test void negativeStockIsRejected() { assertThrows(IllegalArgumentException.class,()->CatalogRules.status(-1)); }
}
