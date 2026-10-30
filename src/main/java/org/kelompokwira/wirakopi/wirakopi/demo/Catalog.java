package org.kelompokwira.wirakopi.wirakopi.demo;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Profile({"demo", "test"})
public class Catalog {
    public record Product(String id, String name, String category, String subtitle, String description, String image, int price) {
        public String formattedPrice() { return money(price); }
    }
    private final List<Product> products = List.of(
        new Product("Americano", "Americano", "Classics", "Bold, clean & beautifully simple", "A rich espresso base, softened with hot water. A clean finish for those who love the character of coffee itself.", "/img/Gun.png", 25000),
        new Product("Macchiato", "Macchiato", "Classics", "A little milk. A lot of character.", "Espresso meets a delicate layer of milk foam. Smooth on the first sip, with a bold coffee finish.", "/img/Mac.png", 30000),
        new Product("Expresso", "Espresso", "Classics", "Small cup, full expression", "A concentrated shot with a deep aroma and a rich, lingering finish. Our simplest coffee ritual.", "/img/Ekspresi.png", 20000),
        new Product("Cappuccino", "Cappuccino", "Milk coffee", "Your everyday moment of comfort", "Rich coffee, warm milk, and a generous cloud of foam. A balanced cup made for slow mornings and good conversations.", "/img/betuah.png", 35000),
        new Product("IceVL", "Iced Vanilla Latte", "Iced favourites", "Smooth espresso, a hint of vanilla", "Espresso and chilled milk with a touch of vanilla syrup. Poured over ice for an easy, refreshing pick-me-up.", "/img/IceVL.png", 30000),
        new Product("IceHL", "Iced Hazelnut Latte", "Iced favourites", "Cool, creamy & a little nutty", "Chilled espresso and milk with mellow hazelnut notes. A refreshing companion for a warm afternoon.", "/img/IceHL.png", 30000)
    );
    public List<Product> all() { return products; }
    public Product find(String id) {
        return products.stream().filter(product -> product.id().equals(id)).findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coffee not found."));
    }
    public static String money(int amount) {
        return "Rp " + NumberFormat.getIntegerInstance(Locale.forLanguageTag("id-ID")).format(amount);
    }
}
