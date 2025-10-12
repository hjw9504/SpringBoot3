package com.example.jdkproject;

import com.example.jdkproject.service.TestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JdkTestApplicationTests {

	private final TestService testService;

	@Autowired
	JdkTestApplicationTests(TestService testService) {
		this.testService = testService;
	}

	@Test
	void initTest() {
		// given

		// when
		String test = testService.initTest();

		// then
		Assertions.assertEquals(test, "HELLO");
	}
}
