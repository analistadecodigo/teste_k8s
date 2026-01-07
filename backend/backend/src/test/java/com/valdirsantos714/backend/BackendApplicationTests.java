package com.valdirsantos714.backend;

import com.valdirsantos714.backend.configuration.TestContextConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

@Import(TestContextConfiguration.class)
@WebMvcTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
