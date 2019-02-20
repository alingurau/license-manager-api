package com.fortech.model.message.response;

import javax.validation.constraints.NotNull;

public class ListBySalesManTerm {

    @NotNull
    private String term;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
