package dk.trifork.sdm.model;

import java.util.Calendar;
import java.util.List;


/**
 * A Dataset that is the complete truth within the given validfrom-validto
 * interval. That is, no other records are allowed other than the ones in this
 * dataset.
 * 
 * @author rsl
 * 
 */
public class CompleteDataset<T extends StamdataEntity> extends Dataset<T> {

	private final Calendar ValidFrom;
	private final Calendar ValidTo;

	protected CompleteDataset(Class<T> type, List<T> entities, Calendar validFrom, Calendar ValidTo) {

		super(entities, type);

		this.ValidFrom = validFrom;
		this.ValidTo = ValidTo;
	}

	public CompleteDataset(Class<T> type, Calendar validFrom, Calendar ValidTo) {

		super(type);

		this.ValidFrom = validFrom;
		this.ValidTo = ValidTo;
	}

	public Calendar getValidFrom() {

		return ValidFrom;
	}

	public Calendar getValidTo() {

		return ValidTo;
	}
}
