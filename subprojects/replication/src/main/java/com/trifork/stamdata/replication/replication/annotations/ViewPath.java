package com.trifork.stamdata.replication.replication.annotations;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.*;
import com.google.inject.BindingAnnotation;


@BindingAnnotation
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewPath {
	String value();
}
