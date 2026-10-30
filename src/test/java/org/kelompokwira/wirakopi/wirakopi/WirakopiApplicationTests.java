package org.kelompokwira.wirakopi.wirakopi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.kelompokwira.wirakopi.wirakopi.demo.Catalog;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class WirakopiApplicationTests {
    @Autowired Catalog catalog;
    @Test void contextLoadsWithSixDemoProducts() { assertThat(catalog.all()).hasSize(6); }
}
