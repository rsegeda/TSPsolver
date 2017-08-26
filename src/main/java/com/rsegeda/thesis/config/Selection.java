package com.rsegeda.thesis.config;

import org.springframework.stereotype.Component;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 26/08/2017.
 */
@Component
public class Selection {

    private Integer value = 0;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
