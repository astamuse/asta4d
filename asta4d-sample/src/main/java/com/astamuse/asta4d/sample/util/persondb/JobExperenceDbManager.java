package com.astamuse.asta4d.sample.util.persondb;

import java.util.LinkedList;
import java.util.List;

public class JobExperenceDbManager extends AbstractDbManager<JobExperence> {
    private static JobExperenceDbManager instance = new JobExperenceDbManager();

    private JobExperenceDbManager() {
        super();
    }

    public static JobExperenceDbManager instance() {
        return instance;
    }

    protected List<JobExperence> initEntityList() {
        return new LinkedList<>();
    }

}
