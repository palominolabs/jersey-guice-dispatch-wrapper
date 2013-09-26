/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.palominolabs.jersey.dispatchwrapper;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.ning.http.client.AsyncHttpClient;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public final class ResourceMethodWrappedDispatchTest {

    private static final int PORT = 28080;
    private Server server;

    private AsyncHttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        httpClient = new AsyncHttpClient();
    }

    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testWorksWithoutBindingAnyWrapperFactories() throws Exception {

        Injector injector = getInjector(new AbstractModule() {
            @Override
            protected void configure() {
                // no op
            }
        });

        server = getServer(injector.getInstance(GuiceFilter.class));
        server.start();

        FooResource resource = injector.getInstance(FooResource.class);

        com.ning.http.client.Response response =
            httpClient.preparePost("http://localhost:" + PORT + "/foo").execute().get();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatusCode());
        assertEquals(1, resource.counter.get());
    }

    @Test
    public void testMultipleWrapperFactories() throws Exception {
        final StubWrapper wrapper1 = new StubWrapper();
        final StubWrapper wrapper2 = new StubWrapper();

        Injector injector = getInjector(new AbstractModule() {
            @Override
            protected void configure() {
                ResourceMethodWrappedDispatchModule.bindWrapperFactory(binder(), new StubWrapperFactory(wrapper1));
                ResourceMethodWrappedDispatchModule.bindWrapperFactory(binder(), new StubWrapperFactory(wrapper2));
            }
        });

        server = getServer(injector.getInstance(GuiceFilter.class));
        server.start();

        FooResource resource = injector.getInstance(FooResource.class);

        com.ning.http.client.Response response =
            httpClient.preparePost("http://localhost:" + PORT + "/foo").execute().get();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatusCode());
        assertEquals(1, resource.counter.get());

        assertEquals(1, wrapper1.counter.get());
        assertEquals(1, wrapper2.counter.get());
    }

    private Server getServer(GuiceFilter filter) {
        Server server = new Server(PORT);
        ServletContextHandler servletHandler = new ServletContextHandler();

        servletHandler.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
                IOException {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("text/plain");
                resp.setContentType("UTF-8");
                resp.getWriter().append("404");
            }
        }), "/*");

        // add guice servlet filter
        servletHandler.addFilter(new FilterHolder(filter), "/*", EnumSet.allOf(DispatcherType.class));

        server.setHandler(servletHandler);

        return server;
    }

    private static Injector getInjector(final Module module) {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                binder().requireExplicitBindings();
                install(new ResourceMethodWrappedDispatchModule());
                install(new ServletModule() {
                    @Override
                    protected void configureServlets() {
                        serve("/*").with(GuiceContainer.class);
                    }
                });
                install(new JerseyServletModule());
                bind(GuiceFilter.class);
                bind(GuiceContainer.class);
                bind(FooResource.class);
                install(module);
            }
        });
    }

    @SuppressWarnings("PublicInnerClass")
    @Path("/foo")
    @Singleton
    public static class FooResource {

        private final AtomicInteger counter = new AtomicInteger();

        @POST
        public void doPost() {
            counter.incrementAndGet();
        }
    }

    private static class StubWrapperFactory implements ResourceMethodDispatchWrapperFactory {

        private final ResourceMethodDispatchWrapper wrapper;

        private StubWrapperFactory(ResourceMethodDispatchWrapper wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public ResourceMethodDispatchWrapper createDispatchWrapper(AbstractResourceMethod abstractResourceMethod) {
            return wrapper;
        }
    }

    private static class StubWrapper implements ResourceMethodDispatchWrapper {

        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public void wrapDispatch(Object resource, HttpContext context, ResourceMethodDispatchWrapperChain chain) {
            try {
                counter.incrementAndGet();
            } finally {
                chain.wrapDispatch(resource, context);
            }
        }
    }
}
