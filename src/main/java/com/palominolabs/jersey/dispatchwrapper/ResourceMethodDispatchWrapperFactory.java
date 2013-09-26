/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.palominolabs.jersey.dispatchwrapper;

import com.sun.jersey.api.model.AbstractResourceMethod;

import javax.annotation.CheckForNull;

/**
 * Used to apply javax.servlet.Filter-like wrapping to Jersey method dispatch.
 *
 * Register implementing classes via {@link ResourceMethodWrappedDispatchModule}.
 */
public interface ResourceMethodDispatchWrapperFactory {

    /**
     * @param abstractResourceMethod resource method
     * @return a wrapper, or null if the factory elects to not create a wrapper for this method
     */
    @CheckForNull
    ResourceMethodDispatchWrapper createDispatchWrapper(AbstractResourceMethod abstractResourceMethod);
}
