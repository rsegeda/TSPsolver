package com.rsegeda.thesis.component;

import com.rsegeda.thesis.algorithm.Algorithm;
import com.vaadin.ui.Label;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 20/08/2017.
 */
@EqualsAndHashCode
public class LabelWithObserver extends Label implements Observer {

    private static Logger logger = LoggerFactory.getLogger(LabelWithObserver.class);

    private Algorithm algorithm = null;

    public LabelWithObserver(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void update(Observable o, Object arg) {
        if (o instanceof Algorithm) {
            Algorithm alg = (Algorithm) o;
            this.algorithm = alg;

            if (arg instanceof Integer) {
                this.algorithm.setValue((Integer) arg);
                this.setValue("Current state is: " + arg);
            }

        } else {
            logger.warn("The algorithm was not of the correct type");
        }
    }
}
