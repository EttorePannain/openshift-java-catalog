package it.lab;
public final class CatalogRules {
    private CatalogRules() {}
    public static String status(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stock negativo");
        return stock == 0 ? "SOLD_OUT" : "AVAILABLE";
    }
}
