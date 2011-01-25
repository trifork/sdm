package com.trifork.sdm.replication.settings;


import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;


@Qualifier
@Retention(RUNTIME)
public @interface DbPassword
{
}
