package com.rsegeda.thesis.component;

import com.rsegeda.thesis.algorithm.Algorithm;
import com.vaadin.ui.Label;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.Observable;
import java.util.Observer;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 20/08/2017.
 */
@Slf4j
@EqualsAndHashCode
public class LabelWithObserver extends Label implements Observer {

    private final JmsTemplate jmsTemplate;
    private Algorithm algorithm = null;

    @Autowired
    public LabelWithObserver(JmsTemplate jmsTemplate, Algorithm algorithm) {
        this.jmsTemplate = jmsTemplate;
        this.algorithm = algorithm;
    }

    public void update(Observable o, Object arg) {
        if (o instanceof Algorithm) {
            Algorithm alg = (Algorithm) o;
            this.algorithm = alg;

            if (arg instanceof Integer) {
                this.algorithm.setProgress((Integer) arg);
                this.setValue("Current state is: " + arg);

                if ((Integer) arg == 100) {
                    jmsTemplate.convertAndSend("calculationDone", "");
                }
            }

        } else {
            log.warn("The algorithm was not of the correct type");
        }
    }
}
