package com.rsegeda.thesis.algorithm;

import java.util.Observable;
import java.util.logging.Logger;

/**
 * Created by Roman Segeda on 01/08/2017.
 */
public class HeldKarpAlgorithm extends Observable implements Runnable {

    private final static Logger log =
            Logger.getLogger(HeldKarpAlgorithm.class.getName());

    private Thread thread;

    private int n = 0;

    public HeldKarpAlgorithm(int x) {
        this.n = x;
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
        log.info("Algorithm started");
    }

    public void run() {

        while (n < 100) {

            n = n + 10;
            setChanged();

            notifyObservers(n);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
