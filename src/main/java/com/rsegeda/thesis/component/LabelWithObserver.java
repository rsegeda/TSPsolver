package com.rsegeda.thesis.component;

import com.rsegeda.thesis.algorithm.HeldKarpAlgorithm;
import com.vaadin.ui.Label;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 20/08/2017.
 */
public class LabelWithObserver extends Label implements Observer {

    private final static Logger log =
            Logger.getLogger(LabelWithObserver.class.getName());

    private HeldKarpAlgorithm heldKarpAlgorithm = null;

    public LabelWithObserver(HeldKarpAlgorithm heldKarpAlgorithm) {
        this.heldKarpAlgorithm = heldKarpAlgorithm;
    }

    public void update(Observable o, Object arg) {
        if (o instanceof HeldKarpAlgorithm) {
            HeldKarpAlgorithm heldKarpAlgorithm = (HeldKarpAlgorithm) o;
            this.heldKarpAlgorithm = heldKarpAlgorithm;

            if (arg instanceof Integer) {
                this.heldKarpAlgorithm.setValue((Integer) arg);
                this.setValue("Current state is: " + arg);
            }

        } else {
            log.warning("The algorithm was not of the correct type");
        }
    }
}
