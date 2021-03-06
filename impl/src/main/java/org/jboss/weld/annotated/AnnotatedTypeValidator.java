/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.annotated;

import static org.jboss.weld.logging.messages.MetadataMessage.INVALID_PARAMETER_POSITION;
import static org.jboss.weld.logging.messages.MetadataMessage.METADATA_SOURCE_RETURNED_NULL;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

import org.jboss.weld.exceptions.IllegalArgumentException;

/**
 * Validates that methods of an {@link Annotated} implementation return sane values.
 * @author Jozef Hartinger
 *
 */
public class AnnotatedTypeValidator {

    private AnnotatedTypeValidator() {
    }

    public static void validateAnnotated(Annotated annotated) {
        checkNotNull(annotated.getAnnotations(), "getAnnotations()", annotated);
        checkNotNull(annotated.getBaseType(), "getBaseType()", annotated);
        checkNotNull(annotated.getTypeClosure(), "getTypeClosure()", annotated);
    }

    public static void validateAnnotatedParameter(AnnotatedParameter<?> parameter) {
        validateAnnotated(parameter);
        if (parameter.getPosition() < 0) {
            throw new IllegalArgumentException(INVALID_PARAMETER_POSITION, parameter.getPosition(), parameter);
        }
        checkNotNull(parameter.getDeclaringCallable(), "getDeclaringCallable()", parameter);
    }

    public static void validateAnnotatedMember(AnnotatedMember<?> member) {
        validateAnnotated(member);
        checkNotNull(member.getJavaMember(), "getJavaMember()", member);
        checkNotNull(member.getDeclaringType(), "getDeclaringType()", member);
    }

    public static void validateAnnotatedType(AnnotatedType<?> type) {
        validateAnnotated(type);
        checkNotNull(type.getJavaClass(), "getJavaClass()", type);
        checkNotNull(type.getFields(), "getFields()", type);
        checkNotNull(type.getConstructors(), "getConstructors()", type);
        checkNotNull(type.getMethods(), "getMethods()", type);
    }

    private static void checkNotNull(Object expression, String methodName, Object target) {
        if (expression == null) {
            throw new IllegalArgumentException(METADATA_SOURCE_RETURNED_NULL, methodName, target);
        }
    }
}
