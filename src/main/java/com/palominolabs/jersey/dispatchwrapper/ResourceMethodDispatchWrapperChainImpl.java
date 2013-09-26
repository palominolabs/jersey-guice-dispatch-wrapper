/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.palominolabs.jersey.dispatchwrapper;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Iterator;

/**
 * An instance of this will be used for one request.
 */
@NotThreadSafe
final class ResourceMethodDispatchWrapperChainImpl implements ResourceMethodDispatchWrapperChain {

    private final Iterator<ResourceMethodDispatchWrapper> wrapperIter;

    private final RequestDispatcher requestDispatcher;

    ResourceMethodDispatchWrapperChainImpl(Iterator<ResourceMethodDispatchWrapper> wrapperIter,
                                           RequestDispatcher requestDispatcher) {
        this.wrapperIter = wrapperIter;
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void wrapDispatch(Object resource, HttpContext context) {
        if (wrapperIter.hasNext()) {
            ResourceMethodDispatchWrapper wrapper = wrapperIter.next();
            wrapper.wrapDispatch(resource, context, this);
        } else {
            requestDispatcher.dispatch(resource, context);
        }
    }
}
