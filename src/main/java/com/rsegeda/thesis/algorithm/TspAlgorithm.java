package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.component.Selection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Observable;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 25/08/2017.
 */
public class TspAlgorithm extends Observable implements Algorithm {

    private static final Logger logger = LoggerFactory.getLogger(TspAlgorithm.class);

    public final Selection selection;
    public Thread thread;
    public int n = 0;
    public boolean stopAlgorithm = false;

    @Autowired
    public TspAlgorithm(Selection selection) {
        this.selection = selection;
    }

    public int getValue() {
        return n;
    }

    public void setValue(int x) {
        this.n = x;
        setChanged();
        notifyObservers();    // makes the observers print null
    }

    public Thread getThread() {
        return thread;
    }

    public void start() {
        n = 0;
        thread = new Thread(this);
        thread.start();
        logger.info("Algorithm started");
    }

    public void run() {

        while (n < 100 && !stopAlgorithm) {

            n = n + 10;
            setChanged();

            notifyObservers(n);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { logger.error("Cannot call sleep on thread.", e); }
        }

        this.setValue(100);
    }

    @Override
    public void stop() {
        setStopAlgorithm(true);
    }

    public void setStopAlgorithm(boolean stopAlgorithm) {
        this.stopAlgorithm = stopAlgorithm;
    }


}
