package org.kelompokwira.wirakopi.wirakopi.demo;
import org.kelompokwira.wirakopi.wirakopi.Entity.User;
import org.kelompokwira.wirakopi.wirakopi.Repository.WiraRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component @Profile("demo")
public class DemoSeed implements CommandLineRunner {
    private final WiraRepo users;private final CoffeeService service;private final PasswordEncoder encoder;
    public DemoSeed(WiraRepo users,CoffeeService service,PasswordEncoder encoder) {this.users=users;this.service=service;this.encoder=encoder;}
    @Override @Transactional public void run(String...args) {
        if(!users.findByUsername("coffee.demo").isEmpty())return;
        account("coffee.demo","Alya Demo","coffee@example.test");account("guest.demo","Raka Demo","guest@example.test");
        service.add("coffee.demo","Cappuccino",1);service.add("coffee.demo","IceVL",1);
    }
    private void account(String username,String name,String email) {
        var user=new User();user.setUsername(username);user.setName(name);user.setEmail(email);user.setNo_telp("0000000000");user.setEnabled(true);user.setPassword(encoder.encode("DemoCoffee123!"));users.saveAndFlush(user);
    }
}
