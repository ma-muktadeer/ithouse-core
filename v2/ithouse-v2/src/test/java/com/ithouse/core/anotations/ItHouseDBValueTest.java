package com.ithouse.core.anotations;

import com.ithouse.core.anotations.injectors.ItHouseDBValueInjector;
import com.ithouse.core.anotations.services.ItHouseDBValueService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@SpringBootTest(classes = { ItHouseDBValueTest.TestConfig.class, ItHouseDBValueTest.TestBean.class })
public class ItHouseDBValueTest {

    @Autowired
    private TestBean testBean;

    @Test
    public void testInjectionDefault() {
        Assertions.assertEquals("default-value", testBean.getValue());
    }

    @Component
    public static class TestBean {
        @ItHouseDBValue(defaultValue = "default-value", configGroup = "TEST", configSubGroup = "SUB")
        private String value;

        public String getValue() {
            return value;
        }
    }

    @Configuration
    public static class TestConfig {
        @Bean
        public ItHouseDBValueService itHouseDBValueService() {
            return new ItHouseDBValueService(java.util.Optional.empty());
        }

        @Bean
        public ItHouseDBValueInjector itHouseDBValueInjector(ItHouseDBValueService service) {
            return new ItHouseDBValueInjector(service);
        }
    }
}
