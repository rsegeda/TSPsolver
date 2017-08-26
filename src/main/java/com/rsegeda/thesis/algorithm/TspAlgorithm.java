package com.rsegeda.thesis.algorithm;

import java.util.Observable;
import java.util.logging.Logger;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 25/08/2017.
 */
public class TspAlgorithm extends Observable implements Algorithm {
    private static final Logger log =
            Logger.getLogger(HeldKarpAlgorithm.class.getName());

    private Thread thread;

    private int n = 0;

    private boolean stopAlgorithm = false;

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
        log.info("Algorithm started");
    }

    public void run() {

        while (n < 100 && !stopAlgorithm) {

            n = n + 10;
            setChanged();

            notifyObservers(n);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { e.printStackTrace(); }
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
