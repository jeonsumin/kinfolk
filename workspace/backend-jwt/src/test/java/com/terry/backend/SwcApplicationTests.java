package com.terry.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RpbmctcHVycG9zZS1vbmx5LTEyMzQ1Njc4OTAxMjM0NTY=",
        "jwt.token-validity=1800",
        "jwt.refresh-token-validity=604800",
        "spring.datasource.url=jdbc:log4jdbc:mysql://localhost:3306/test",
        "spring.datasource.username=test",
        "spring.datasource.password=test"
})
class SwcApplicationTests {

	@Test
	void contextLoads() {
	}

}
