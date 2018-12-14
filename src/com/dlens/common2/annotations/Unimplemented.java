package com.dlens.common2.annotations;

import java.util.Date;

public @interface Unimplemented {
	ImplementationStatus status() default(ImplementationStatus.UNIMPLEMENTED);
}

