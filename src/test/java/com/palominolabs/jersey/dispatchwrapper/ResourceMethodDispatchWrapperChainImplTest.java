/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.palominolabs.jersey.dispatchwrapper;

import com.google.common.collect.Lists;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import org.junit.Test;

import java.util.ArrayList;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

public final class ResourceMethodDispatchWrapperChainImplTest {

    @Test
    public void testDispatchesWithEmptyIterator() {
        RequestDispatcher dispatcherMock = createStrictMock(RequestDispatcher.class);
        Object resource = new Object();
        HttpContext httpContext = createStrictMock(HttpContext.class);

        dispatcherMock.dispatch(resource, httpContext);

        replay(dispatcherMock);

        ResourceMethodDispatchWrapperChainImpl chain =
            new ResourceMethodDispatchWrapperChainImpl(Lists.<ResourceMethodDispatchWrapper>newArrayList().iterator(),
                dispatcherMock);

        chain.wrapDispatch(resource, httpContext);

        verify(dispatcherMock);
    }

    @Test
    public void testIteratesThenDispatches() {
        RequestDispatcher dispatcherMock = createStrictMock(RequestDispatcher.class);
        Object resource = new Object();
        HttpContext httpContext = createStrictMock(HttpContext.class);

        dispatcherMock.dispatch(resource, httpContext);

        replay(dispatcherMock);

        ArrayList<ResourceMethodDispatchWrapper> list = Lists.newArrayList();
        CountingRequestMethodDispatchWrapper wrapper1 = new CountingRequestMethodDispatchWrapper();
        list.add(wrapper1);
        CountingRequestMethodDispatchWrapper wrapper2 = new CountingRequestMethodDispatchWrapper();
        list.add(wrapper2);
        ResourceMethodDispatchWrapperChainImpl chain =
            new ResourceMethodDispatchWrapperChainImpl(list.iterator(),
                dispatcherMock);

        chain.wrapDispatch(resource, httpContext);

        verify(dispatcherMock);

        assertEquals(1, wrapper1.counter);
        assertEquals(1, wrapper2.counter);
    }

    private static final class CountingRequestMethodDispatchWrapper implements ResourceMethodDispatchWrapper {

        int counter = 0;

        @Override
        public void wrapDispatch(Object resource, HttpContext context, ResourceMethodDispatchWrapperChain chain) {
            counter++;
            chain.wrapDispatch(resource, context);
        }
    }
}
