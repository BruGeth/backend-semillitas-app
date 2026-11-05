package com.semillitas.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requiere MySQL; el arranque completo se valida en el despliegue cloud")
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
