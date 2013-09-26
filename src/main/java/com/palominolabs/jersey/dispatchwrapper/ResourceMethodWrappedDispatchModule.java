/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.palominolabs.jersey.dispatchwrapper;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;

public final class ResourceMethodWrappedDispatchModule extends AbstractModule {
    @Override
    protected void configure() {
        // ensure there is always at least an empty set
        Multibinder.newSetBinder(binder(), ResourceMethodDispatchWrapperFactory.class);

        // expose adapter to jersey
        bind(WrappedResourceMethodDispatchAdapter.class);
    }

    /**
     * @param binder binder
     * @param klass  factory class to register
     */
    public static void bindWrapperFactory(Binder binder, Class<? extends ResourceMethodDispatchWrapperFactory> klass) {
        Multibinder.newSetBinder(binder, ResourceMethodDispatchWrapperFactory.class).addBinding().to(klass);
    }

    /**
     * @param binder  binder
     * @param factory factory instance to register
     */
    public static void bindWrapperFactory(Binder binder, ResourceMethodDispatchWrapperFactory factory) {
        Multibinder.newSetBinder(binder, ResourceMethodDispatchWrapperFactory.class).addBinding().toInstance(factory);
    }
}
