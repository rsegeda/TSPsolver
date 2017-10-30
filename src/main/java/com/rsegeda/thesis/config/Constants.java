package com.rsegeda.thesis.config;

import com.google.common.collect.ImmutableList;

/**
 * Created by Roman Segeda on 02/07/2017.
 */
public class Constants {

    public static final String APP_NAME = "Pathfinder - TSP solver";

    public static final String DRUNKEN_SAILOR_ALGORITHM = "Drunken sailor - as a mock-up";
    public static final String THE_HELD_KARP_LOWER_BOUND = "The Held-Karp Lower Bound";
    public static final String STATE_UPDATE_JMS = "stateUpdate";
    public static final String ANT_COLONY_OPTIMIZATION = "Ant Colony Optimization";
    public static final String LIN_KERNIGHAN = "Lin Kerninghan";

    public static final String MODE_DISTANCE = "Distance";
    public static final String MODE_TIME = "Time";

    public static final ImmutableList<String> MODES = ImmutableList.of("" +
                    MODE_DISTANCE,
            MODE_TIME
    );

    public static final ImmutableList<String> ALGORITHMS = ImmutableList.of("" +
                    THE_HELD_KARP_LOWER_BOUND,
            ANT_COLONY_OPTIMIZATION,
            LIN_KERNIGHAN,
            DRUNKEN_SAILOR_ALGORITHM
    );

    public static final String PREPARING_STATE = "Preparing";
    public static final String COMPUTING_STATE = "Computing";
    public static final String READY_STATE = "Ready";

    private Constants() {}
}
