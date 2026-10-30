package org.kelompokwira.wirakopi.wirakopi.demo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DemoOrderRepository extends JpaRepository<DemoOrder, String> {
    List<DemoOrder> findByCustomerUsernameOrderByCreatedAtDesc(String username);
    Optional<DemoOrder> findByIdAndCustomerUsername(String id, String username);
    Optional<DemoOrder> findByCheckoutTokenAndCustomerUsername(String token, String username);
}
