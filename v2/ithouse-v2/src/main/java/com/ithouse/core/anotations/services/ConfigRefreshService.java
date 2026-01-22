package com.ithouse.core.anotations.services;

/**
 * Functional interface to trigger a refresh of all @ItHouseDBValue annotated
 * fields.
 * This is the public API that users should call to update their configurations.
 */
@FunctionalInterface
public interface ConfigRefreshService {

    /**
     * Refreshes all annotated fields with the latest values from the database.
     */
    void refresh();
}
