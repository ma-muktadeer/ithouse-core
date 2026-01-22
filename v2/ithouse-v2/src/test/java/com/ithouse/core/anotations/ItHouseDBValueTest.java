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

    @Test
    void multiTypeInjection() {
        contextRunner.run(context -> {
            MultiTypeBean bean = context.getBean(MultiTypeBean.class);
            TestConfigProvider provider = context.getBean(TestConfigProvider.class);

            // Set provider values
            provider.addConfig("INT_VAL", "123");
            provider.addConfig("LONG_VAL", "456789");
            provider.addConfig("BOOL_VAL", "true");

            // Trigger refresh
            context.getBean(ConfigRefreshService.class).refresh();

            // Verify
            assertThat(bean.getIntValue()).isEqualTo(123);
            assertThat(bean.getLongValue()).isEqualTo(456789L);
            assertThat(bean.isBoolValue()).isTrue();
        });
    }

    @Configuration
    static class TestConfig {
        @Bean
        public TestBean testBean() {
            return new TestBean();
        }

        @Bean
        public MultiTypeBean multiTypeBean() {
            return new MultiTypeBean();
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

    static class MultiTypeBean {
        @ItHouseDBValue(defaultValue = "0", configSubGroup = "INT_VAL")
        private int intValue;

        @ItHouseDBValue(defaultValue = "0", configSubGroup = "LONG_VAL")
        private Long longValue;

        @ItHouseDBValue(defaultValue = "false", configSubGroup = "BOOL_VAL")
        private boolean boolValue;

        public int getIntValue() {
            return intValue;
        }

        public Long getLongValue() {
            return longValue;
        }

        public boolean isBoolValue() {
            return boolValue;
        }
    }

    static class TestConfigProvider implements ItHouseConfigProvider {
        private String value = "initial";
        private final Map<String, String> configs = new HashMap<>();

        public void setValue(String value) {
            this.value = value;
        }

        public void addConfig(String key, String val) {
            configs.put(key, val);
        }

        @Override
        public String getConfigValue(String configGroup, String configSubGroup) {
            if ("TEST_KEY".equals(configSubGroup)) {
                return value;
            }
            return configs.get(configSubGroup);
        }
    }
}
