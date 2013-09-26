/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.palominolabs.jersey.dispatchwrapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.Provider;
import java.util.Set;

/**
 * When Jersey is configured to use this class, it is applied on all ResourceMethodDispatchProvider instances by Jersey.
 * This is how our ResourceMethodDispatchWrapperFactory instances get into the request pipeline.
 */
@Provider
@Singleton
final class WrappedResourceMethodDispatchAdapter implements ResourceMethodDispatchAdapter {

    private static final Logger logger = LoggerFactory.getLogger(WrappedResourceMethodDispatchAdapter.class);

    private final Set<ResourceMethodDispatchWrapperFactory> wrapperFactories;

    @Inject
    WrappedResourceMethodDispatchAdapter(Set<ResourceMethodDispatchWrapperFactory> wrapperFactories) {
        this.wrapperFactories = wrapperFactories;
    }

    @Override
    public ResourceMethodDispatchProvider adapt(ResourceMethodDispatchProvider provider) {
            logger.debug("Wrapped dispatch enabled; " + wrapperFactories.size() + " wrapper factories registered");
            return new WrappedResourceMethodDispatchProvider(provider, wrapperFactories);
    }
}
