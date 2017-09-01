package com.rsegeda.thesis.algorithm;

/**
 * Copyright 2017 by Avid Technology, Inc.
 * Created by roman.segeda@avid.com on 20/08/2017.
 */
public interface Algorithm extends Runnable{

    int getProgress();

    void setProgress(int x);

    Thread getThread();

    void start();

    void run();

    void stop();
}
