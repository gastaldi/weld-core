/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.bean.builtin;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.inject.Decorated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.bean.RIBean;
import org.jboss.weld.literal.DecoratedLiteral;
import org.jboss.weld.manager.BeanManagerImpl;

/**
 * Allows a decorator to obtain information about the bean it decorates.
 *
 * @author Jozef Hartinger
 * @see CDI-92
 *
 */
public class DecoratedBeanMetadataBean extends InterceptedBeanMetadataBean {

    public DecoratedBeanMetadataBean(BeanManagerImpl beanManager) {
        super(Decorated.class.getSimpleName() + RIBean.BEAN_ID_SEPARATOR + Bean.class.getSimpleName(), beanManager);
    }

    @Override
    protected void checkInjectionPoint(InjectionPoint ip) {
        if (!(ip.getBean() instanceof Decorator<?>)) {
            throw new IllegalArgumentException("@Decorated Bean<?> can only be injected into a decorator.");
        }
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return Collections.<Annotation> singleton(DecoratedLiteral.INSTANCE);
    }

    @Override
    public String toString() {
        return "Implicit Bean [javax.enterprise.inject.spi.Bean] with qualifiers [@Decorated]";
    }
}
