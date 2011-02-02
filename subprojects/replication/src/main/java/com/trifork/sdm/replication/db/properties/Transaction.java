package com.trifork.sdm.replication.db.properties;


import java.lang.annotation.*;

import com.google.inject.BindingAnnotation;


@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD })
public @interface Transaction
{
	Database value();
}
