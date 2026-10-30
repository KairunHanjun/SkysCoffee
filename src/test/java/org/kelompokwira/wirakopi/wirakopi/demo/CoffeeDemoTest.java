package org.kelompokwira.wirakopi.wirakopi.demo;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kelompokwira.wirakopi.wirakopi.Entity.User;
import org.kelompokwira.wirakopi.wirakopi.Repository.WiraRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test") @Transactional
class CoffeeDemoTest {
    @Autowired MockMvc mvc;
    @Autowired CoffeeService service;
    @Autowired WiraRepo users;
    @Autowired DemoOrderRepository orders;
    @Autowired PasswordEncoder encoder;
    @Autowired ApplicationContext context;
    @BeforeEach void setup() {
        account("coffee.demo","Alya Demo","coffee@example.test");account("guest.demo","Raka Demo","guest@example.test");
    }
    private void account(String username,String name,String email) {
        var user=new User();user.setUsername(username);user.setName(name);user.setEmail(email);user.setNo_telp("0000000000");user.setEnabled(true);user.setPassword(encoder.encode("DemoCoffee123!"));users.saveAndFlush(user);
    }
    private MockHttpSession checkoutSession(String token) {var session=new MockHttpSession();session.setAttribute("checkoutToken",token);return session;}
    @Test void publicPagesRenderWithoutCloudServices() throws Exception {
        for(String path:new String[]{"/","/menu","/product?product=Cappuccino","/about","/auth/login","/auth/register","/auth/lupa"})
            mvc.perform(get(path)).andExpect(status().isOk());
        mvc.perform(get("/css/demo.css")).andExpect(status().isOk());
        assertThat(context.getBeansOfType(org.springframework.mail.javamail.JavaMailSender.class)).isEmpty();
        assertThat(context.getBeansOfType(org.kelompokwira.wirakopi.wirakopi.Service.UserServiceImpl.class)).isEmpty();
    }
    @Test void unknownProductIsNotFound() throws Exception {mvc.perform(get("/product?product=Unknown")).andExpect(status().isNotFound());}
    @Test void cartRequiresAuthentication() throws Exception {mvc.perform(get("/cart")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("http://localhost/auth/login"));}
    @Test void loginUsesStoredHashedPassword() throws Exception {
        mvc.perform(formLogin("/auth/login").user("coffee.demo").password("DemoCoffee123!"))
            .andExpect(authenticated().withUsername("coffee.demo")).andExpect(redirectedUrl("/menu"));
        mvc.perform(formLogin("/auth/login").user("coffee.demo").password("wrong"))
            .andExpect(redirectedUrl("/auth/login?error"));
    }
    @Test @WithMockUser(username="coffee.demo") void cartPricesComeFromCatalogue() throws Exception {
        mvc.perform(post("/cart/items").with(csrf()).param("product","Cappuccino").param("quantity","2").param("price","1").param("username","guest.demo"))
            .andExpect(redirectedUrl("/cart"));
        var cart=service.cart("coffee.demo");assertThat(cart.count()).isEqualTo(2);assertThat(cart.subtotal()).isEqualTo(70000);assertThat(cart.total()).isEqualTo(80000);
        assertThat(service.cart("guest.demo").count()).isZero();
        mvc.perform(get("/cart")).andExpect(status().isOk()).andExpect(content().string(containsString("Rp 80.000")));
    }
    @Test @WithMockUser(username="coffee.demo") void quantityUpdatesPersistAndZeroRemovesItem() throws Exception {
        service.add("coffee.demo","Americano",1);
        mvc.perform(post("/cart/quantity").with(csrf()).param("product","Americano").param("quantity","3")).andExpect(status().is3xxRedirection());
        assertThat(service.cart("coffee.demo").subtotal()).isEqualTo(75000);
        mvc.perform(post("/cart/quantity").with(csrf()).param("product","Americano").param("quantity","0")).andExpect(status().is3xxRedirection());
        assertThat(service.cart("coffee.demo").count()).isZero();assertThat(service.cart("coffee.demo").shipping()).isZero();
        mvc.perform(get("/cart")).andExpect(content().string(containsString("Your cart is empty")));
    }
    @Test @WithMockUser(username="coffee.demo") void invalidCartChangesAreRejected() throws Exception {
        mvc.perform(post("/cart/items").with(csrf()).param("product","Unknown")).andExpect(status().isNotFound());
        for(String quantity:new String[]{"0","-1","21","1.5"})
            mvc.perform(post("/cart/items").with(csrf()).param("product","Americano").param("quantity",quantity)).andExpect(status().isBadRequest());
        assertThat(service.cart("coffee.demo").count()).isZero();
    }
    @Test @WithMockUser(username="coffee.demo") void quantityLimitAppliesAcrossRepeatedAdds() throws Exception {
        service.add("coffee.demo","Americano",20);
        mvc.perform(post("/cart/items").with(csrf()).param("product","Americano").param("quantity","1")).andExpect(status().isBadRequest());
        assertThat(service.cart("coffee.demo").count()).isEqualTo(20);
    }
    @Test @WithMockUser(username="coffee.demo") void csrfIsRequiredForCartAndCheckout() throws Exception {
        mvc.perform(post("/cart/items").param("product","Americano")).andExpect(status().isForbidden());
        mvc.perform(post("/checkout")).andExpect(status().isForbidden());
        assertThat(orders.count()).isZero();
    }
    @Test @WithMockUser(username="coffee.demo") void legacyGetActionsCannotModifyCartOrCreateOrder() throws Exception {
        service.add("coffee.demo","Americano",1);
        mvc.perform(get("/user/jemBelanda").param("action","add_Americano")).andExpect(status().isOk());
        mvc.perform(get("/user/jemBelanda").param("action","buy")).andExpect(status().isOk());
        assertThat(service.cart("coffee.demo").count()).isEqualTo(1);assertThat(orders.count()).isZero();
    }
    @Test @WithMockUser(username="coffee.demo") void checkoutCreatesSnapshotClearsCartAndIsIdempotent() throws Exception {
        service.add("coffee.demo","Cappuccino",1);service.add("coffee.demo","IceVL",1);
        String token=UUID.randomUUID().toString();var session=checkoutSession(token);
        for(int repeat=0;repeat<2;repeat++)mvc.perform(post("/checkout").with(csrf()).session(session)
            .param("recipient","Alya Demo").param("address","Jl. Contoh No. 8, Kota Demo").param("checkoutToken",token).param("total","1"))
            .andExpect(status().is3xxRedirection());
        assertThat(orders.count()).isEqualTo(1);assertThat(service.cart("coffee.demo").count()).isZero();
        var order=orders.findAll().getFirst();assertThat(order.getTotal()).isEqualTo(75000);assertThat(order.getStatus()).isEqualTo("SIMULATED");assertThat(order.getLines()).hasSize(2);
        mvc.perform(get("/orders/"+order.getId())).andExpect(status().isOk()).andExpect(content().string(containsString("SIMULATED · NOT PAID")));
    }
    @Test @WithMockUser(username="coffee.demo") void emptyCheckoutDoesNotCreateOrder() throws Exception {
        String token=UUID.randomUUID().toString();mvc.perform(post("/checkout").with(csrf()).session(checkoutSession(token))
            .param("recipient","Alya Demo").param("address","Jl. Contoh No. 8, Kota Demo").param("checkoutToken",token)).andExpect(status().isBadRequest());
        assertThat(orders.count()).isZero();
    }
    @Test @WithMockUser(username="coffee.demo") void checkoutRequiresSessionTokenAndValidAddress() throws Exception {
        service.add("coffee.demo","Americano",1);String token=UUID.randomUUID().toString();
        mvc.perform(post("/checkout").with(csrf()).param("recipient","Alya").param("address","Jl. Contoh No. 8").param("checkoutToken",token)).andExpect(status().isBadRequest());
        mvc.perform(post("/checkout").with(csrf()).session(checkoutSession(token)).param("recipient","Alya").param("address","x").param("checkoutToken",token)).andExpect(status().isBadRequest());
        assertThat(orders.count()).isZero();assertThat(service.cart("coffee.demo").count()).isEqualTo(1);
    }
    @Test @WithMockUser(username="guest.demo") void ordersArePrivateToOwner() throws Exception {
        service.add("coffee.demo","Americano",1);var order=service.checkout("coffee.demo","Alya","Jl. Contoh No. 8",UUID.randomUUID().toString());
        mvc.perform(get("/orders/"+order.getId())).andExpect(status().isNotFound());
    }
    @Test @WithMockUser(username="coffee.demo") void profileChangesOnlyCurrentAccount() throws Exception {
        mvc.perform(post("/profile").with(csrf()).param("name","Alya Updated").param("phone","0000000001").param("username","guest.demo").param("email","changed@example.test"))
            .andExpect(redirectedUrl("/profile"));
        assertThat(service.user("coffee.demo").getName()).isEqualTo("Alya Updated");assertThat(service.user("coffee.demo").getEmail()).isEqualTo("coffee@example.test");
        assertThat(service.user("guest.demo").getName()).isEqualTo("Raka Demo");
        mvc.perform(get("/profile")).andExpect(status().isOk()).andExpect(content().string(containsString("Alya Updated")));
    }
    @Test void registrationWorksLocallyWithoutEmailOtp() throws Exception {
        mvc.perform(post("/auth/register").with(csrf()).param("username","new.demo").param("name","New Demo")
            .param("email","new@example.test").param("phone","0000000000").param("password","Password123!").param("confirmation","Password123!"))
            .andExpect(redirectedUrl("/auth/login"));
        var user=service.user("new.demo");assertThat(user.isEnabled()).isTrue();assertThat(encoder.matches("Password123!",user.getPassword())).isTrue();
    }
    @Test void invalidRegistrationCannotCreateAccount() throws Exception {
        mvc.perform(post("/auth/register").with(csrf()).param("username","new.demo").param("name","New Demo")
            .param("email","not-an-email").param("phone","0000000000").param("password","short").param("confirmation","short"))
            .andExpect(view().name("demo/register"));
        assertThat(users.findByUsername("new.demo")).isEmpty();
    }
    @Test @WithMockUser(username="coffee.demo") void logoutRequiresPostAndCsrf() throws Exception {
        mvc.perform(post("/logout")).andExpect(status().isForbidden());
        mvc.perform(post("/logout").with(csrf())).andExpect(redirectedUrl("/auth/login?logout"));
    }
}
