
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.replication.security.dgws;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.replication.replication.views.Views.checkViewIntegrity;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.replication.views.ViewPath;


@Entity
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
		checkArgument(token.length == 512);

		this.cvr = cvr;
		this.expiresAt = expiresAt;
		this.viewName = viewClass.getAnnotation(ViewPath.class).value();
		this.token = token;
	}
}
