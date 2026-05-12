package com.pedro.ordemservico;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class OrdemServicoApiApplicationTests extends PostgresContainerTestBase {

	@Test
	void contextLoads() {
	}

}
