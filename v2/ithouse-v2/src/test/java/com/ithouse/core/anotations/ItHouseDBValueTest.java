package com.ithouse.core.anotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ithouse.core.anotations.config.ItHouseValueAutoConfiguration;
import com.ithouse.core.anotations.provider.ItHouseConfigProvider;
import com.ithouse.core.anotations.services.ConfigRefreshService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ItHouseDBValueTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ItHouseValueAutoConfiguration.class))
            .withBean(ObjectMapper.class)
            .withUserConfiguration(TestConfig.class);

    @Test
    void valueIsInjectedAndRefreshed() {
        contextRunner.run(context -> {
            TestBean testBean = context.getBean(TestBean.class);
            ConfigRefreshService refreshService = context.getBean(ConfigRefreshService.class);
            TestConfigProvider provider = context.getBean(TestConfigProvider.class);

            // Verify initial injection (starts with default value if provider returns null,
            // but our test provider will return "initial" immediately)
            assertThat(testBean.getValue()).isEqualTo("initial");

            // Update provider value
            provider.setValue("updated");

            // Trigger refresh
            refreshService.refresh();

            // Verify updated value
            assertThat(testBean.getValue()).isEqualTo("updated");
        });
    }

    @Configuration
    static class TestConfig {
        @Bean
        public TestBean testBean() {
            return new TestBean();
        }

        @Bean
        public TestConfigProvider testConfigProvider() {
            return new TestConfigProvider();
        }
    }

    static class TestBean {
        @ItHouseDBValue(defaultValue = "default", configSubGroup = "TEST_KEY")
        private String value;

        public String getValue() {
            return value;
        }
    }

    static class TestConfigProvider implements ItHouseConfigProvider {
        private String value = "initial";

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String getConfigValue(String configGroup, String configSubGroup) {
            if ("TEST_KEY".equals(configSubGroup)) {
                return value;
            }
            return null;
        }
    }
}
