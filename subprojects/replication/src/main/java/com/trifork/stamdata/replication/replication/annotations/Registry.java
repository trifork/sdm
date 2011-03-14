package com.trifork.stamdata.replication.replication.annotations;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.*;
import com.google.inject.BindingAnnotation;


@BindingAnnotation
@Target({ FIELD, PARAMETER, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Registry {
}
