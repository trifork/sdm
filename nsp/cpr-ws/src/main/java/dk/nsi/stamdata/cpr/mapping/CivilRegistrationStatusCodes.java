package dk.nsi.stamdata.cpr.mapping;

import java.util.List;

import com.google.common.collect.ImmutableList;


public interface CivilRegistrationStatusCodes
{
	static final String LIVING_ABROAD = "20";
	static final String CANCELLED = "30";
	static final String DELETED = "50";
	static final String CHANGED = "60";
	static final String MISSING = "70";
	static final String DEAD = "90";

	static final List<String> STATUSES_WITH_NO_ADDRESS = ImmutableList.of(
			LIVING_ABROAD,
			CANCELLED,
			DELETED,
			CHANGED,
			MISSING,
			DEAD
	);

}
