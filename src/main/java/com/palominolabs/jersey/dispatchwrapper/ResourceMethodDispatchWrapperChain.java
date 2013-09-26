/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.palominolabs.jersey.dispatchwrapper;

import com.sun.jersey.api.core.HttpContext;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Jersey analog of a servlet filter chain.
 */
@NotThreadSafe
public interface ResourceMethodDispatchWrapperChain {
    void wrapDispatch(Object resource, HttpContext context);
}
