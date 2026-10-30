package org.kelompokwira.wirakopi.wirakopi.demo;

import java.time.LocalDateTime;
import java.util.*;
import org.kelompokwira.wirakopi.wirakopi.Entity.*;
import org.kelompokwira.wirakopi.wirakopi.JsonObject.*;
import org.kelompokwira.wirakopi.wirakopi.Repository.*;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service @Profile({"demo", "test"}) @Transactional
public class CoffeeService {
    public record CartLine(Catalog.Product product, int quantity) {
        public int lineTotal() { return product.price()*quantity; }
        public String formattedTotal() { return Catalog.money(lineTotal()); }
    }
    public record Cart(List<CartLine> lines, int count, int subtotal, int shipping, int total) {
        public String formattedSubtotal() { return Catalog.money(subtotal); }
        public String formattedShipping() { return Catalog.money(shipping); }
        public String formattedTotal() { return Catalog.money(total); }
    }
    private final WiraRepo users;
    private final UserStuffRepo carts;
    private final DemoOrderRepository orders;
    private final Catalog catalog;
    private final PasswordEncoder encoder;
    public CoffeeService(WiraRepo users, UserStuffRepo carts, DemoOrderRepository orders, Catalog catalog, PasswordEncoder encoder) {
        this.users=users; this.carts=carts; this.orders=orders; this.catalog=catalog; this.encoder=encoder;
    }
    @Transactional(readOnly=true)
    public User user(String username) {
        return users.findByUsername(username).stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));
    }
    @Transactional(readOnly=true)
    public Cart cart(String username) {
        var lines=readCart(user(username)).entrySet().stream().map(entry -> new CartLine(catalog.find(entry.getKey()),entry.getValue())).toList();
        int count=lines.stream().mapToInt(CartLine::quantity).sum();
        int subtotal=lines.stream().mapToInt(CartLine::lineTotal).sum();
        int shipping=count==0?0:10000;
        return new Cart(lines,count,subtotal,shipping,subtotal+shipping);
    }
    private LinkedHashMap<String,Integer> readCart(User user) {
        var result=new LinkedHashMap<String,Integer>();
        carts.findByUser(user).stream().findFirst().ifPresent(cart -> {
            if(cart.getJsonContent()!=null) for(var line:cart.getJsonContent().getStuffArray()) {
                if(!"Shipping Cost".equals(line.getDrinkName()) && line.getAmount()>0) {
                    catalog.find(line.getDrinkName()); result.put(line.getDrinkName(),line.getAmount());
                }
            }
        });
        return result;
    }
    private void saveCart(User user, Map<String,Integer> values) {
        var cart=carts.findByUser(user).stream().findFirst().orElseGet(UserStuff::new);
        var content=new JsonStuff(); var lines=new ArrayList<StuffArray>();
        values.forEach((id,quantity) -> { var line=new StuffArray(id);line.setAmount(quantity);line.setDrinkPrice(catalog.find(id).price());lines.add(line); });
        content.setStuffArray(lines);cart.setUser(user);cart.setJsonContent(content);carts.saveAndFlush(cart);
    }
    public void add(String username, String productId, int quantity) {
        catalog.find(productId); require(quantity>=1 && quantity<=20,"Quantity must be between 1 and 20.");
        var user=user(username);var values=readCart(user);int next=values.getOrDefault(productId,0)+quantity;
        require(next<=20,"A maximum of 20 cups per coffee is allowed in this demo.");values.put(productId,next);saveCart(user,values);
    }
    public void quantity(String username,String productId,int quantity) {
        catalog.find(productId);require(quantity>=0 && quantity<=20,"Quantity must be between 0 and 20.");
        var user=user(username);var values=readCart(user);require(values.containsKey(productId),"Coffee is not in your cart.");
        if(quantity==0)values.remove(productId);else values.put(productId,quantity);saveCart(user,values);
    }
    public DemoOrder checkout(String username,String recipient,String address,String token) {
        var existing=orders.findByCheckoutTokenAndCustomerUsername(token,username);
        if(existing.isPresent())return existing.get();
        require(token!=null && token.matches("[a-f0-9-]{36}"),"Checkout session is invalid. Reload your cart.");
        require(recipient!=null && !recipient.isBlank() && recipient.length()<=100,"Recipient name is required (maximum 100 characters).");
        require(address!=null && address.trim().length()>=10 && address.length()<=300,"Enter a sample address between 10 and 300 characters.");
        var cart=cart(username);require(!cart.lines().isEmpty(),"Add a coffee before placing an order.");
        var order=new DemoOrder();order.setId("SKY-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(Locale.ROOT));
        order.setCustomer(user(username));order.setCreatedAt(LocalDateTime.now());order.setRecipient(recipient.trim());order.setAddress(address.trim());
        order.setSubtotal(cart.subtotal());order.setShipping(cart.shipping());order.setTotal(cart.total());order.setCheckoutToken(token);
        for(var line:cart.lines())order.getLines().add(new DemoOrder.Line(line.product().id(),line.product().name(),line.quantity(),line.product().price()));
        orders.saveAndFlush(order);saveCart(user(username),Map.of());return order;
    }
    @Transactional(readOnly=true)
    public List<DemoOrder> orders(String username) {return orders.findByCustomerUsernameOrderByCreatedAtDesc(username);}
    @Transactional(readOnly=true)
    public DemoOrder order(String username,String id) {return orders.findByIdAndCustomerUsername(id,username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found."));}
    public void profile(String username,String name,String phone) {
        require(name!=null && !name.isBlank() && name.length()<=100,"Name is required (maximum 100 characters).");
        require(phone!=null && phone.matches("[0-9+() -]{6,20}"),"Enter a sample phone number using 6–20 characters.");
        var user=user(username);user.setName(name.trim());user.setNo_telp(phone.trim());users.save(user);
    }
    public void register(String username,String name,String email,String phone,String password,String confirmation) {
        require(username!=null && username.matches("[a-zA-Z0-9._-]{3,30}"),"Username must contain 3–30 letters, digits, dots, dashes, or underscores.");
        require(password!=null && password.length()>=8 && password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length<=72,"Password must have at least 8 characters and at most 72 bytes.");
        require(password.equals(confirmation),"Passwords do not match.");
        require(name!=null && !name.isBlank() && name.length()<=100,"Name is required (maximum 100 characters).");
        require(phone!=null && phone.matches("[0-9+() -]{6,20}"),"Enter a sample phone number using 6–20 characters.");
        require(email!=null && email.length()<=150 && (org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email) || email.matches("[A-Za-z0-9._+-]+@example\\.test")),"Enter a valid sample email address.");
        require(users.findByUsername(username).isEmpty(),"That username is already in use.");
        require(users.findByEmail(email).isEmpty(),"That email address is already in use.");
        var user=new User();user.setUsername(username);user.setName(name.trim());user.setEmail(email);user.setNo_telp(phone);user.setPassword(encoder.encode(password));user.setEnabled(true);users.saveAndFlush(user);
    }
    static void require(boolean valid,String message) {if(!valid)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,message);}
}
