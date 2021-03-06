/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.injection.producer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import javax.enterprise.context.spi.CreationalContext;
import javax.interceptor.AroundConstruct;
import javax.interceptor.InvocationContext;

import org.jboss.weld.exceptions.WeldException;
import org.jboss.weld.injection.AroundConstructCallback;
import org.jboss.weld.interceptor.proxy.InterceptionContext;
import org.jboss.weld.interceptor.proxy.InterceptorInvocation;
import org.jboss.weld.interceptor.proxy.InterceptorInvocationContext;
import org.jboss.weld.interceptor.proxy.SimpleInterceptionChain;
import org.jboss.weld.interceptor.spi.metadata.ClassMetadata;
import org.jboss.weld.interceptor.spi.metadata.InterceptorMetadata;
import org.jboss.weld.interceptor.spi.model.InterceptionModel;
import org.jboss.weld.interceptor.spi.model.InterceptionType;
import org.jboss.weld.manager.BeanManagerImpl;

/**
 * Delegating {@link Instantiator} that takes care of {@link AroundConstruct} interceptor invocation.
 *
 * @author Jozef Hartinger
 *
 */
public class ConstructorInterceptionInstantiator<T> extends ForwardingInstantiator<T> {

    private final InterceptionModel<ClassMetadata<?>, ?> model;

    public ConstructorInterceptionInstantiator(Instantiator<T> delegate, InterceptionModel<ClassMetadata<?>, ?> model) {
        super(delegate);
        this.model = model;
    }

    @Override
    public T newInstance(CreationalContext<T> ctx, BeanManagerImpl manager, AroundConstructCallback<T> callback) {

        InterceptionContext interceptionContext = InterceptionContext.forConstructorInterception(model, ctx, manager);
        // build interceptor invocations
        final Collection<InterceptorInvocation> interceptorInvocations = new ArrayList<InterceptorInvocation>(model.getConstructorInvocationInterceptors().size());
        for (InterceptorMetadata<?> interceptorMetadata : model.getConstructorInvocationInterceptors()) {
            interceptorInvocations.add(interceptorMetadata.getInterceptorInvocation(interceptionContext.getInterceptorInstance(interceptorMetadata), InterceptionType.AROUND_CONSTRUCT));
        }

        AroundConstructCallback<T> aroundConstructCallback = new AroundConstructCallback<T>() {
            @Override
            public T aroundConstruct(Object[] parameters, final ConstructionHandle<T> constructionHandle) {

                /*
                 * The AroundConstruct interceptor method can access the constructed instance using InvocationContext.getTarget
                 * method after the InvocationContext.proceed completes.
                 */
                final AtomicReference<T> target = new AtomicReference<T>();

                SimpleInterceptionChain chain = new SimpleInterceptionChain(interceptorInvocations) {
                    @Override
                    protected Object interceptorChainCompleted(InvocationContext invocationCtx) throws Exception {
                        // all the interceptors were invoked, call the constructor now
                        target.set(constructionHandle.construct(invocationCtx.getParameters()));
                        return null;
                    }
                };

                InterceptorInvocationContext invocationCtx = new InterceptorInvocationContext(chain, delegate().getConstructor(), parameters) {
                    @Override
                    public Object getTarget() {
                        return target.get();
                    }
                };

                try {
                    chain.invokeNextInterceptor(invocationCtx);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new WeldException(e);
                }
                return target.get();
            }
        };
        return delegate().newInstance(ctx, manager, aroundConstructCallback);
    }

    @Override
    public String toString() {
        return "ConstructorInterceptionInstantiator wrapping " + delegate();
    }
}
