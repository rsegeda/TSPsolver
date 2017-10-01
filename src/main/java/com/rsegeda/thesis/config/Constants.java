package com.rsegeda.thesis.config;

import com.google.common.collect.ImmutableList;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
public class Constants {

    public static final String APP_NAME = "Pathfinder - TSP solver";
    public static final String MOCKUP_ALGORITHM = "Mockup implementation";
    public static final String THE_HELD_KARP_LOWER_BOUND = "The Held-Karp Lower Bound";
    public static final String STATE_UPDATE_JMS = "stateUpdate";
    private static final String ANT_COLONY_OPTIMIZATION = "Ant Colony Optimization";
    private static final String LIN_KERNIGHAN = "Lin Kerninghan";
    private static final String GENETIC_ALGORITHM = "Genetic algorithm";
    private static final String GOAL_DISTANCE = "Distance";
    private static final String GOAL_TIME = "Time";

    public static final ImmutableList<String> GOALS = ImmutableList.of("" +
                    GOAL_DISTANCE,
            GOAL_TIME
    );

    public static final ImmutableList<String> ALGORITHMS = ImmutableList.of("" +
                    THE_HELD_KARP_LOWER_BOUND,
            ANT_COLONY_OPTIMIZATION,
            LIN_KERNIGHAN,
            GENETIC_ALGORITHM,
            MOCKUP_ALGORITHM
    );

    public static final String PREPARING_STATE = "Preparing";
    public static final String COMPUTING_STATE = "Computing";
    public static final String READY_STATE = "Ready";

    private Constants() {

    }
}
