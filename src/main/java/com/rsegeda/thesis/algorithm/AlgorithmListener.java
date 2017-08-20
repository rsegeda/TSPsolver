//package com.rsegeda.thesis.algorithm;
//
//import java.util.Observable;
//import java.util.Observer;
//
///**
// * Created by Roman Segeda on 20/08/2017.
// */
//
//public class AlgorithmListener implements Observer {
//
//    private Algorithm algorithm = null;
//
//    public AlgorithmListener(Algorithm algorithm) {
//        this.algorithm = algorithm;
//    }
//
//    public int getValue() {
//        return algorithm.getValue();
//    }
//
//    public void update(Observable o, Object arg) {
//        if (o instanceof Algorithm) {
//            Algorithm algorithm = (Algorithm) o;
//            this.algorithm = algorithm;
//
//            if (arg instanceof Integer) {
//                this.algorithm.setValue((Integer) arg);
//
//            } else {
//                System.out.println("The algorithm was not of the correct type");
//            }
//
//        }
//    }
//}
