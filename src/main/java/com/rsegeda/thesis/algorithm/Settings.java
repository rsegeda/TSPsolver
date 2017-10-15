package com.rsegeda.thesis.algorithm;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Created by Roman Segeda on 13/10/2017.
 */
@Component
@Data
public class Settings {

    //    Ant Colony Algorithm:

    int AOT_NUMBER_OF_ITERATIONS = 1000;
    double AOT_NUMBER_OF_TRAILS = 1.0;
    int AOT_ALPHA = 1;
    int AOT_BETA = 5;
    double AOT_EVAPORATION = 0.5;
    double AOT_ANT_GROUP_SIZE = 0.8;
}
