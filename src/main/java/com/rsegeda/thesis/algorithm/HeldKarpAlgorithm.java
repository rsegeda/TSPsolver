package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.component.Selection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Roman Segeda on 01/08/2017.
 */
public class HeldKarpAlgorithm extends TspAlgorithm {

    private static Logger logger = LoggerFactory.getLogger(HeldKarpAlgorithm.class);

    public HeldKarpAlgorithm(Selection selection) {
        super(selection);
    }

    @Override
    public void run() {

        while (n < 200 && !stopAlgorithm) {

            n = n + 20;
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

    @Override
    public void setStopAlgorithm(boolean stopAlgorithm) {
        this.stopAlgorithm = stopAlgorithm;
    }

}
