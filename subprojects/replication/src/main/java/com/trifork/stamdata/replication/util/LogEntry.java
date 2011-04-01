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

package com.trifork.stamdata.replication.util;

import static javax.persistence.TemporalType.TIMESTAMP;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;


@Entity
public class LogEntry {

	@Id
	@GeneratedValue
	private String id;

	private String message;

	@Temporal(TIMESTAMP)
	private Date createdAt;
	
	protected LogEntry() {
		
	}

	public LogEntry(String message) {

		this.message = message;
	}

	public String getId() {

		return id;
	}

	public String getMessage() {

		return message;
	}

	public Date getCreatedAt() {

		return createdAt;
	}
}
