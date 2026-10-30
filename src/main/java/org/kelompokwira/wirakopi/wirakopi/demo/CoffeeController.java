package org.kelompokwira.wirakopi.wirakopi.demo;

import java.security.Principal;
import java.util.UUID;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @Profile({"demo", "test"})
public class CoffeeController {
    private final Catalog catalog;
    private final CoffeeService service;
    public CoffeeController(Catalog catalog,CoffeeService service) {this.catalog=catalog;this.service=service;}
    @ModelAttribute public void shared(Model model, Authentication authentication) {
        boolean signedIn=authentication!=null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
        String username=signedIn?authentication.getName():null;
        model.addAttribute("signedIn",signedIn);model.addAttribute("username",username);
        model.addAttribute("cartCount",signedIn?service.cart(username).count():0);
    }
    @GetMapping("/") String home(Model model) {model.addAttribute("featured",catalog.all().stream().filter(p -> !p.id().equals("Expresso") && !p.id().equals("IceHL")).toList());return "demo/home";}
    @GetMapping({"/menu","/static/menu"}) String menu(Model model) {model.addAttribute("products",catalog.all());return "demo/menu";}
    @GetMapping({"/product","/static/product"}) String product(@RequestParam(defaultValue="Cappuccino") String product,Model model) {
        model.addAttribute("product",catalog.find(product));return "demo/product";
    }
    @GetMapping({"/about","/static/about","/static/about.html"}) String about() {return "demo/about";}
    @GetMapping("/auth/login") String login(Authentication authentication) {
        return authentication!=null && !(authentication instanceof AnonymousAuthenticationToken)?"redirect:/profile":"demo/login";
    }
    @GetMapping("/auth/register") String register() {return "demo/register";}
    @PostMapping("/auth/register") String register(@RequestParam String username,@RequestParam String name,@RequestParam String email,
        @RequestParam String phone,@RequestParam String password,@RequestParam String confirmation,Model model,RedirectAttributes redirect) {
        try {service.register(username,name,email,phone,password,confirmation);}
        catch(ResponseStatusException error) {model.addAttribute("error",error.getReason());return "demo/register";}
        redirect.addFlashAttribute("message","Demo account created locally. You can now sign in.");return "redirect:/auth/login";
    }
    @GetMapping({"/auth/lupa","/auth/WiraSuruhFigma"}) String recovery() {return "demo/recovery";}
    @GetMapping({"/cart","/user/jemBelanda"}) String cart(Principal principal,Model model,HttpSession session) {
        model.addAttribute("cart",service.cart(principal.getName()));model.addAttribute("customer",service.user(principal.getName()));
        String token=UUID.randomUUID().toString();session.setAttribute("checkoutToken",token);model.addAttribute("checkoutToken",token);return "demo/cart";
    }
    @PostMapping({"/cart/items","/user/addcart"}) String add(Principal principal,@RequestParam String product,
        @RequestParam(defaultValue="1") int quantity,RedirectAttributes redirect) {
        service.add(principal.getName(),product,quantity);redirect.addFlashAttribute("message","Coffee added to your cart.");return "redirect:/cart";
    }
    @PostMapping("/cart/quantity") String quantity(Principal principal,@RequestParam String product,@RequestParam int quantity,RedirectAttributes redirect) {
        service.quantity(principal.getName(),product,quantity);redirect.addFlashAttribute("message","Cart updated.");return "redirect:/cart";
    }
    @PostMapping("/checkout") String checkout(Principal principal,@RequestParam String recipient,@RequestParam String address,
        @RequestParam String checkoutToken,HttpSession session) {
        if(!checkoutToken.equals(session.getAttribute("checkoutToken")))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Checkout session expired. Return to your cart and try again.");
        var order=service.checkout(principal.getName(),recipient,address,checkoutToken);return "redirect:/orders/"+order.getId();
    }
    @GetMapping("/orders/{id}") String order(Principal principal,@PathVariable String id,Model model) {
        model.addAttribute("order",service.order(principal.getName(),id));return "demo/order";
    }
    @GetMapping({"/profile","/user/userProfile"}) String profile(Principal principal,Model model) {
        model.addAttribute("customer",service.user(principal.getName()));model.addAttribute("orders",service.orders(principal.getName()));return "demo/profile";
    }
    @PostMapping("/profile") String profile(Principal principal,@RequestParam String name,@RequestParam String phone,RedirectAttributes redirect) {
        service.profile(principal.getName(),name,phone);redirect.addFlashAttribute("message","Your demo profile has been updated.");return "redirect:/profile";
    }
    @ExceptionHandler(ResponseStatusException.class) ModelAndView invalid(ResponseStatusException error, Authentication authentication, Model model) {
        shared(model,authentication);
        var view=new ModelAndView("demo/error",model.asMap());view.setStatus(error.getStatusCode());view.addObject("code",error.getStatusCode().value());view.addObject("error",error.getReason());return view;
    }
}
