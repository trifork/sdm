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

package com.trifork.stamdata.replication.replication.views;

import static com.trifork.stamdata.Namespace.STAMDATA_3_0;

import java.math.BigInteger;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.trifork.stamdata.HistoryOffset;


@XmlType(namespace=STAMDATA_3_0 + "/common")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
public abstract class View {

	public abstract String getId();

	public abstract BigInteger getRecordID();

	public abstract Date getUpdated();

	/**
	 * Gets the offset (revision) for the record.
	 *
	 * @see HistoryOffset
	 */
	public String getOffset() {

		return new HistoryOffset(getRecordID().toString(), getUpdated()).toString();
	}
}
