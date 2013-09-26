This library allows you to wrap the invocation of Jersey 1 resource methods like how you can wrap servlets with a good ol' servlet `javax.servlet.Filter`.

When writing `Filter`s, it's easy to have code like this:

```
setUpSomeStuff();
chain.doFilter(request, response);
takeSomeMeasurements();
```

Since you're directly in the method invocation chain, you can easily take timing measurements, use local variables, etc.

Jersey has request and response filters, but those don't really do the same thing as servlet filters. They make it easy to inspect or modify incoming requests or outgoing responses, but since your code isn't wrapping the actual invocation of the request processing logic, taking timing measurements or anything else best done with local variables is tricky. You can use thread locals, but it's messy.

Fortunately, Jersey has `ResourceMethodDispatchAdapter` and `ResourceMethodDispatchProvider` which, together with a `RequestDispatcher`, are involved in actually calling your JAX-RS resource methods. You can use these to do `Filter`-style logic that directly wraps the invocation of your resource logic, but it takes a fair amount of wiring.

This library makes that wiring easier (with an assist from Guice).

First, define a `ResourceMethodDispatchWrapper`. This is what will actually be involved in the request method invocation. Here, we'll have a wrapper that simply prints out the resource method name that it's invoking.
```
class LoggingWrapper implements ResourceMethodDispatchWrapper {

    private final String resourceMethodName;

    LoggingWrapper(String resourceMethodName) {
        this.resourceMethodName = resourceMethodName;
    }

    @Override
    public void wrapDispatch(Object resource, HttpContext context,
        ResourceMethodDispatchWrapperChain chain) {
        System.out.println("Executing method " + resourceMethodName);
        chain.wrapDispatch(resource, context);
    }
}
```

Next, we define a `ResourceMethodDispatchWrapperFactory` implementation to create `LoggingWrapper` instances:

```
class LoggingWrapperFactory implements ResourceMethodDispatchWrapperFactory {

    @Override
    public ResourceMethodDispatchWrapper createDispatchWrapper(
        AbstractResourceMethod abstractResourceMethod) {
        String methodName = abstractResourceMethod.getMethod.getName();

        if (methodName.equals("dontLogMe")) {
            return null; // don't wrap this particular method
        }

        return new LoggingWrapper(methodName);
    }
}
```

Note that your Factory logic can control on a per-resource-method basis how to do filtering.

Finally, register your Factory class:
```
// in your Guice module's configure() method
ResourceMethodWrappedDispatchModule.bindWrapperFactory(binder(),
    ResourceMethodDispatchWrapperFactory.class);
```