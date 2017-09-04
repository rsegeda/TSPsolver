package com.rsegeda.thesis.config;

import com.google.common.collect.ImmutableList;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
public class Constants {

    public static String GOAL_DISTANCE = "Distance";
    public static String GOAL_TIME = "Time";

    public static final ImmutableList<String> GOALS = ImmutableList.of("" +
                    GOAL_DISTANCE,
            GOAL_TIME
    );

    public static final String THE_HELD_KARP_LOWER_BOUND = "The Held-Karp Lower Bound";
    public static final String ANT_COLONY_OPTIMIZATION = "Ant Colony Optimization";
    public static final String LIN_KERNIGHAN = "Lin Kerninghan";
    public static final String GENETIC_ALGORITHM = "Genetic algorithm";

    public static final ImmutableList<String> ALGORITHMS = ImmutableList.of("" +
                    THE_HELD_KARP_LOWER_BOUND,
            ANT_COLONY_OPTIMIZATION,
            LIN_KERNIGHAN,
            GENETIC_ALGORITHM
    );

    public static final int HOME_TAB_ID = 0;
    public static final int RESULTS_TAB_ID = 1;
    public static final int SETTINGS_TAB = 2;
    public static final int INFO_TAB = 3;

    public static final String PREPARING_STATE = "Preparing";
    public static final String COMPUTING_STATE = "Computing";
    public static final String READY_STATE = "Ready";
}
