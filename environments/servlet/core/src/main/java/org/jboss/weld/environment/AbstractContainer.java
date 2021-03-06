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

package org.jboss.weld.environment;

import org.jboss.weld.environment.servlet.util.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract container.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractContainer implements Container {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Get class name to check is we can use this container.
     *
     * @return the class name to check
     */
    protected abstract String classToCheck();

    public boolean touch(ContainerContext context) throws Exception {
        Reflections.classForName(classToCheck());
        return true;
    }

    public void destroy(ContainerContext context) {
    }
}
