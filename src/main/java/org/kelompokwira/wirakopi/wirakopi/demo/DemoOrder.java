package org.kelompokwira.wirakopi.wirakopi.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kelompokwira.wirakopi.wirakopi.Entity.User;

@Entity @Table(name="demo_orders") @Getter @Setter @NoArgsConstructor
public class DemoOrder {
    @Id private String id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) private User customer;
    private LocalDateTime createdAt;
    private String recipient;
    @Column(length=300) private String address;
    private int subtotal;
    private int shipping;
    private int total;
    private String status = "SIMULATED";
    @Column(unique=true, nullable=false) private String checkoutToken;
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="demo_order_lines", joinColumns=@JoinColumn(name="order_id"))
    @OrderColumn(name="line_position")
    private List<Line> lines = new ArrayList<>();
    public String formattedTotal() { return Catalog.money(total); }
    public String formattedSubtotal() { return Catalog.money(subtotal); }
    public String formattedShipping() { return Catalog.money(shipping); }
    public String formattedDate() { return createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", java.util.Locale.ENGLISH)); }
    @Embeddable @Getter @Setter @NoArgsConstructor
    public static class Line {
        private String productId;
        private String name;
        private int quantity;
        private int unitPrice;
        public Line(String productId, String name, int quantity, int unitPrice) {
            this.productId=productId; this.name=name; this.quantity=quantity; this.unitPrice=unitPrice;
        }
        public String formattedTotal() { return Catalog.money(quantity * unitPrice); }
    }
}
