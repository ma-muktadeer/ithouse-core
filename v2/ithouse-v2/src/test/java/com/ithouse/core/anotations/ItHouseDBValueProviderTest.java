// package com.ithouse.core.anotations;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.ithouse.core.anotations.injectors.ItHouseDBValueInjector;
// import com.ithouse.core.anotations.provider.ItHouseConfigProvider;
// import com.ithouse.core.anotations.services.ItHouseDBValueService;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.stereotype.Component;

// import java.util.Optional;

// @SpringBootTest(classes = { ItHouseDBValueProviderTest.TestConfig.class,
// ItHouseDBValueProviderTest.TestBean.class })
// public class ItHouseDBValueProviderTest {

// @Autowired
// private TestBean testBean;

// @Test
// public void testInjectionFromProvider() {
// Assertions.assertEquals("provider-value", testBean.getValue());
// }

// @Component
// public static class TestBean {
// @ItHouseDBValue(defaultValue = "default-value", configGroup = "TEST",
// configSubGroup = "SUB")
// private String value;

// public String getValue() {
// return value;
// }
// }

// @Configuration
// public static class TestConfig {
// @Bean
// public ItHouseConfigProvider itHouseConfigProvider() {
// return (group, subGroup) -> {
// if ("TEST".equals(group) && "SUB".equals(subGroup)) {
// return "provider-value";
// }
// return null;
// };
// }

// @Bean
// public ItHouseDBValueService itHouseDBValueService(ItHouseConfigProvider
// provider) {
// return new ItHouseDBValueService(Optional.of(provider));
// }

// @Bean
// public ItHouseDBValueInjector itHouseDBValueInjector(ItHouseDBValueService
// service, ObjectMapper objectMapper) {
// return new ItHouseDBValueInjector(service, objectMapper);
// }
// }
// }
