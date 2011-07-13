package com.trifork.stamdata.importer.jobs.sikrede.model;

import com.trifork.stamdata.importer.persistence.CompleteDataset;
import com.trifork.stamdata.importer.util.DateUtils;

import java.util.Date;

/**
 * User: frj
 * Date: 7/12/11
 * Time: 10:12 AM
 *
 * @Author frj
 */
public class SikredeDataset extends CompleteDataset<Sikrede>{

    public SikredeDataset(Date validFrom) {
        super(Sikrede.class, validFrom, DateUtils.FUTURE);
    }
}
