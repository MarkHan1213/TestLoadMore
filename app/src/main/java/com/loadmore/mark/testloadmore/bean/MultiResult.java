package com.loadmore.mark.testloadmore.bean;

import java.util.List;

/**
 * Created by Mark.Han on 2017/5/8.
 */
public class MultiResult {
    private List<Results> results;

    private Page page;

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public List<Results> getResults() {
        return this.results;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Page getPage() {
        return this.page;
    }
}
