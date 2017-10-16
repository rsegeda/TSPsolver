package com.rsegeda.thesis.algorithm;

import lombok.Data;

/**
 * Created by Roman Segeda on 13/10/2017.
 */
@Data
public class Settings {

    //    Ant Colony Algorithm:

    int aotNumberOfIterations = 1000;
    double aotNumberOfTrails = 1.0;
    int aotAlpha = 1;
    int aotBeta = 5;
    double aotEvaporation = 0.5;
    double aotAntGroupSize = 0.8;
}
