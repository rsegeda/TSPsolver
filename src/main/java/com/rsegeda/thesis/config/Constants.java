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

    public static String THE_HELD_KARP_LOWER_BOUND = "The Held-Karp Lower Bound";
    public static String ANT_COLONY_OPTIMIZATION = "Ant Colony Optimization";
    public static String LIN_KERNIGHAN = "Lin Kerninghan";
    public static String GENETIC_ALGORITHM = "Genetic algorith";

    public static final ImmutableList<String> ALGORITHMS = ImmutableList.of("" +
                    THE_HELD_KARP_LOWER_BOUND,
            ANT_COLONY_OPTIMIZATION,
            LIN_KERNIGHAN,
            GENETIC_ALGORITHM
    );


}
