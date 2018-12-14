package com.dlens.common2.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Implemented {
	boolean tested() default(false);
	String[] testFiles() default {};
}
