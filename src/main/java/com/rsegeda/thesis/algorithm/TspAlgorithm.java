package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.location.LocationDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;
import java.util.Observable;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 25/08/2017.
 */
@Slf4j
public class TspAlgorithm extends Observable implements Algorithm {

    public final Selection selection;
    public final JmsTemplate jmsTemplate;
    @Getter
    public Thread thread;
    @Getter
    public int progress = 0;
    public boolean stopAlgorithm = false;

    @Autowired
    public TspAlgorithm(Selection selection, JmsTemplate jmsTemplate) {
        this.selection = selection;
        this.jmsTemplate = jmsTemplate;
    }

    public void setProgress(int x) {
        this.progress = x;
        setChanged();
        notifyObservers();    // makes the observers print null
    }

    public void start() {
        progress = 0;
        thread = new Thread(this);
        thread.start();
        log.info("Algorithm started");
    }

    public void run() {

        List<LocationDto> locationDtoList = selection.getLocationDtos();
        List<LocationDto> result = locationDtoList;
        while (progress < 100 && !stopAlgorithm) {

            progress = progress + 10;
            setChanged();

            notifyObservers(progress);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { log.error("Cannot call sleep on thread.", e); }
        }

        setProgress(100);
        int index = 1;
        for (LocationDto locationDto : result) {
            locationDto.setIndex(index);
            index++;
        }
        selection.setCalculatedLocationDtos(result);
        jmsTemplate.convertAndSend("algorithmResult", "");
    }

    @Override
    public void stop() {
        setStopAlgorithm(true);
    }

    public void setStopAlgorithm(boolean stopAlgorithm) {
        this.stopAlgorithm = stopAlgorithm;
    }


}
