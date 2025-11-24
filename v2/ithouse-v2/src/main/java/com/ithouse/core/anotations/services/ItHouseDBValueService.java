package com.ithouse.core.anotations.services;

import com.ithouse.core.anotations.provider.ItHouseConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ItHouseDBValueService {
    private static final Logger logger = LoggerFactory.getLogger(ItHouseDBValueService.class);

    private final Optional<ItHouseConfigProvider> configProvider;

    public ItHouseDBValueService(Optional<ItHouseConfigProvider> configProvider) {
        this.configProvider = configProvider;
    }

    public String getConfigValue(String configGroup, String configSubGroup, String defaultValue) {
        if (configProvider.isPresent()) {
            String dbValue = configProvider.get().getConfigValue(configGroup, configSubGroup);
            if (dbValue != null) {
                logger.debug("Found config in DB for Group: {}, SubGroup: {}: {}", configGroup, configSubGroup,
                        dbValue);
                return dbValue;
            }
        }

        logger.info("Using default config for Group: {}, SubGroup: {}, Default: {}", configGroup, configSubGroup,
                defaultValue);
        return defaultValue;
    }
}
