package com.ithouse.core.anotations.provider;

public interface ItHouseConfigProvider {
    /**
     * Fetch configuration value from the underlying data source.
     *
     * @param configGroup    The configuration group
     * @param configSubGroup The configuration sub-group
     * @return The configuration value, or null if not found
     */
    String getConfigValue(String configGroup, String configSubGroup);
}
