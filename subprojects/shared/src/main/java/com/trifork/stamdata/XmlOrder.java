package com.trifork.stamdata;


import java.lang.annotation.*;


@Target(value = { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlOrder
{
	int value();
}
