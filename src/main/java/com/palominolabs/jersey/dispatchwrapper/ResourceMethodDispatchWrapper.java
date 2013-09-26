/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.palominolabs.jersey.dispatchwrapper;

import com.sun.jersey.api.core.HttpContext;

import javax.annotation.concurrent.ThreadSafe;

/**
 * This interface should only be used for things that actually need to wrap the resource method invocation for timing
 * purposes. If you simply need to observe what's coming in and out, use a ResourceFilterFactory.
 *
 * To have an implementation applied in the request chain, implement a corresponding {@link
 * ResourceMethodDispatchWrapperFactory}.
 */
@ThreadSafe
public interface ResourceMethodDispatchWrapper {

    /**
     * Implementers MUST call chain.wrapDispatch (presumably in a finally block).
     *
     * @param resource resource object
     * @param context  request context
     * @param chain    wrapper chain
     */
    void wrapDispatch(Object resource, HttpContext context, ResourceMethodDispatchWrapperChain chain);
}
