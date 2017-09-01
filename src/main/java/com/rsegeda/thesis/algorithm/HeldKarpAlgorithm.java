package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.location.LocationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;

/**
 * Created by Roman Segeda on 01/08/2017.
 */
@Slf4j
public class HeldKarpAlgorithm extends TspAlgorithm {

    public HeldKarpAlgorithm(Selection selection, JmsTemplate jmsTemplate) {
        super(selection, jmsTemplate);
    }

    @Override
    public void run() {

        List<LocationDto> locationDtoList = selection.getLocationDtos();
        List<LocationDto> result = locationDtoList;
        while (progress < 100 && !stopAlgorithm) {

            progress = progress + 10;
            setChanged();

            notifyObservers(progress);

            try {
                Thread.sleep(150);
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

    @Override
    public void setStopAlgorithm(boolean stopAlgorithm) {
        this.stopAlgorithm = stopAlgorithm;
    }

}
