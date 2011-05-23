package com.trifork.stamdata.lookup.security.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

@BindingAnnotation
@Target({ FIELD, PARAMETER, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizedClients {

}
