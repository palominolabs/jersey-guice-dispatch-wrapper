/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.palominolabs.jersey.dispatchwrapper;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

/**
 * A RequestDispatcher that wraps the provided dispatcher with the dispatch wrappers.
 */
final class WrappedRequestDispatcher implements RequestDispatcher {

    private final RequestDispatcher wrappedDispatcher;

    private final ImmutableList<ResourceMethodDispatchWrapper> wrappers;

    WrappedRequestDispatcher(RequestDispatcher wrappedDispatcher,
                             ImmutableList<ResourceMethodDispatchWrapper> wrappers) {
        this.wrappedDispatcher = wrappedDispatcher;
        this.wrappers = wrappers;
    }

    @Override
    public void dispatch(Object resource, HttpContext context) {
        new ResourceMethodDispatchWrapperChainImpl(wrappers.iterator(), wrappedDispatcher)
            .wrapDispatch(resource, context);
    }
}
