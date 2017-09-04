package com.rsegeda.thesis.algorithm;

import com.rsegeda.thesis.component.Selection;
import com.rsegeda.thesis.utils.DirectionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;

/**
 * Created by Roman Segeda on 01/08/2017.
 */
@Slf4j
public class HeldKarpAlgorithm extends TspAlgorithm {

    public HeldKarpAlgorithm(Selection selection, JmsTemplate jmsTemplate, DirectionsService directionsService) {
        super(selection, jmsTemplate, directionsService);
    }

    //    @Override
    //    public List<LocationDto> compute(){
    //        log.info(""+selection.getDistancesMap().size());
    //        return selection.getLocationDtos();
    //    }

    @Override
    public void stop() {
        setStopAlgorithm(true);
    }

    @Override
    public void setStopAlgorithm(boolean stopAlgorithm) {
        this.stopAlgorithm = stopAlgorithm;
    }

}
