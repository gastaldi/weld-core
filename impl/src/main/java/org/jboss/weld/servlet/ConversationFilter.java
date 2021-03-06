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
package org.jboss.weld.servlet;

import static org.jboss.weld.logging.messages.ServletMessage.ONLY_HTTP_SERVLET_LIFECYCLE_DEFINED;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.weld.exceptions.IllegalStateException;
import org.jboss.weld.manager.BeanManagerImpl;

/**
 * Filter that handles conversation context activation if mapped by the application. Otherwise, conversation context is
 * activated by {@link WeldListener} at the beginning of the request processing.
 *
 * @see WeldListener
 * @see ConversationContextActivator
 *
 * @author Jozef Hartinger
 *
 */
public class ConversationFilter implements Filter {

    static final String CONVERSATION_FILTER_INITIALIZED = ConversationFilter.class.getName() +  ".initialized";

    @Inject
    private BeanManagerImpl manager;

    private ConversationContextActivator conversationContextActivator;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.conversationContextActivator = new ConversationContextActivator(manager, filterConfig.getServletContext());
        filterConfig.getServletContext().setAttribute(CONVERSATION_FILTER_INITIALIZED, Boolean.TRUE);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            conversationContextActivator.startConversationContext(httpRequest);
            chain.doFilter(request, response);
            /*
             * We do not deactivate the conversation context in the filer. WeldListener takes care of that!
             */
        } else {
            throw new IllegalStateException(ONLY_HTTP_SERVLET_LIFECYCLE_DEFINED);
        }
    }

    @Override
    public void destroy() {
    }
}
