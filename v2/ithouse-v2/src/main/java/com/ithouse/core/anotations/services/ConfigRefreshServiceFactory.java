package com.ithouse.core.anotations.services;

import com.ithouse.core.anotations.injectors.ItHouseDBValueInjector;
import org.springframework.beans.factory.ObjectProvider;

/**
 * Factory and bridge to create ConfigRefreshService instances.
 */
public final class ConfigRefreshServiceFactory {

    private ConfigRefreshServiceFactory() {
    }

    public static ConfigRefreshService create(ObjectProvider<ItHouseDBValueInjector> injectorProvider) {
        return new DefaultConfigRefreshService(injectorProvider);
    }
}
