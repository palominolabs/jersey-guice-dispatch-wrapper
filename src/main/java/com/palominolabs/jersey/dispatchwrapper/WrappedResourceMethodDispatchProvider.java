/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.palominolabs.jersey.dispatchwrapper;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Creates a RequestDispatcher that uses the wrapped provider's dispatcher to actually dispatch the request.
 */
final class WrappedResourceMethodDispatchProvider implements ResourceMethodDispatchProvider {

    private static final Logger logger = LoggerFactory.getLogger(WrappedResourceMethodDispatchProvider.class);

    private final Set<ResourceMethodDispatchWrapperFactory> wrapperFactories;

    private final ResourceMethodDispatchProvider wrappedProvider;

    WrappedResourceMethodDispatchProvider(ResourceMethodDispatchProvider wrappedProvider,
                                          Set<ResourceMethodDispatchWrapperFactory> wrapperFactories) {
        this.wrappedProvider = wrappedProvider;
        this.wrapperFactories = wrapperFactories;
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {

        ImmutableList.Builder<ResourceMethodDispatchWrapper> builder = ImmutableList.builder();

        for (ResourceMethodDispatchWrapperFactory wrapperFactory : wrapperFactories) {
            logger.trace("Invoking factory " + wrapperFactory.getClass().getName());
            ResourceMethodDispatchWrapper wrapper = wrapperFactory.createDispatchWrapper(abstractResourceMethod);
            if (wrapper == null) {
                logger.trace("Factory did not produce a wrapper");
                continue;
            }

            logger.trace("Factory produced a wrapper");
            builder.add(wrapper);
        }

        ImmutableList<ResourceMethodDispatchWrapper> wrappers = builder.build();
        RequestDispatcher innerDispatcher = wrappedProvider.create(abstractResourceMethod);

        if (wrappers.isEmpty()) {
            // just use the plain, un-wrapped dispatcher
            return innerDispatcher;
        }

        return new WrappedRequestDispatcher(innerDispatcher, wrappers);
    }
}
