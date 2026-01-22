package com.ithouse.core.anotations.services;

import com.ithouse.core.anotations.injectors.ItHouseDBValueInjector;
import org.springframework.beans.factory.ObjectProvider;

/**
 * Package-private implementation of ConfigRefreshService.
 */
class DefaultConfigRefreshService implements ConfigRefreshService {

    private final ObjectProvider<ItHouseDBValueInjector> injectorProvider;

    DefaultConfigRefreshService(ObjectProvider<ItHouseDBValueInjector> injectorProvider) {
        this.injectorProvider = injectorProvider;
    }

    @Override
    public void refresh() {
        injectorProvider.ifAvailable(ItHouseDBValueInjector::refresh);
    }
}
