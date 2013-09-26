/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.palominolabs.jersey.dispatchwrapper;

import com.google.common.collect.Sets;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import org.junit.Test;

import java.util.HashSet;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

public final class WrappedResourceMethodDispatchProviderTest {

    @Test
    public void testSkipsFactoriesThatProduceNull() {

        ResourceMethodDispatchProvider mockProvider = createStrictMock(ResourceMethodDispatchProvider.class);

        AbstractResourceMethod method = createStrictMock(AbstractResourceMethod.class);

        RequestDispatcher mockDispatcher = createStrictMock(RequestDispatcher.class);
        Object resource = new Object();
        HttpContext context = createStrictMock(HttpContext.class);
        mockDispatcher.dispatch(resource, context);

        expect(mockProvider.create(method)).andReturn(mockDispatcher);

        HashSet<ResourceMethodDispatchWrapperFactory> wrapperFactories = Sets.newHashSet();

        ResourceMethodDispatchWrapperFactory mockFactory1 =
            createStrictMock(ResourceMethodDispatchWrapperFactory.class);
        expect(mockFactory1.createDispatchWrapper(method)).andReturn(null);

        wrapperFactories.add(mockFactory1);

        ResourceMethodDispatchWrapperFactory mockFactory2 =
            createStrictMock(ResourceMethodDispatchWrapperFactory.class);
        StubWrapper wrapper = new StubWrapper();
        expect(mockFactory2.createDispatchWrapper(method))
            .andReturn(wrapper);

        wrapperFactories.add(mockFactory2);

        replay(mockProvider, mockFactory1, mockFactory2, mockDispatcher);
        RequestDispatcher requestDispatcher =
            new WrappedResourceMethodDispatchProvider(mockProvider, wrapperFactories).create(method);

        requestDispatcher.dispatch(resource, context);

        verify(mockProvider, mockFactory1, mockFactory2, mockDispatcher);

        assertEquals(1, wrapper.counter);
    }

    private static class StubWrapper implements ResourceMethodDispatchWrapper {

        private int counter = 0;

        @Override
        public void wrapDispatch(Object resource, HttpContext context, ResourceMethodDispatchWrapperChain chain) {
            counter++;
            chain.wrapDispatch(resource, context);
        }
    }
}
