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

package com.trifork.stamdata.replication.security.dgws;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.trifork.stamdata.replication.replication.views.Views.checkViewIntegrity;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.trifork.stamdata.replication.replication.views.View;


@Embeddable
public class Authorization {

	@Id
	@GeneratedValue
	protected BigInteger id;

	protected String cvr;
	protected String viewName;
	protected Date expiresAt;
	
	@Lob
	protected byte[] token; 

	protected Authorization() {

	}

	public Authorization(Class<? extends View> viewClass, String cvr, Date expiresAt, byte[] token) {

		checkNotNull(cvr);
		checkNotNull(expiresAt);
		checkViewIntegrity(viewClass);
		checkNotNull(token);

		this.cvr = cvr;
		this.expiresAt = expiresAt;
		this.viewName = viewClass.getAnnotation(Entity.class).name();
		this.token = token;
	}
}
