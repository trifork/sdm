// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface ClientSpecific {
}
