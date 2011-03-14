package com.trifork.stamdata.replication.security.dgws;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.trifork.stamdata.replication.replication.views.Views.checkViewIntegrity;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.trifork.stamdata.replication.replication.views.View;


@Embeddable
public class Authorization {

	@Id
	@GeneratedValue
	protected BigInteger id;

	protected String cvr;
	protected String viewName;
	protected Date expiresAt;

	protected Authorization() {

	}

	public Authorization(Class<? extends View> viewClass, String cvr, Date expiresAt) {

		checkNotNull(cvr);
		checkNotNull(expiresAt);
		checkViewIntegrity(viewClass);

		this.cvr = cvr;
		this.expiresAt = expiresAt;
		this.viewName = viewClass.getAnnotation(Entity.class).name();
	}
}
