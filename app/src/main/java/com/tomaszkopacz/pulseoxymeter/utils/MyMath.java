package com.tomaszkopacz.pulseoxymeter.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomaszkopacz on 19.12.17.
 */

public class MyMath {

    private static final String TAG = "TomaszKopacz";
    private static final double MIN_RR = 0.2;

    public static double countAverage(double[] values){
        double avg = 0;
        for (int i = 0; i < values.length; i++)
            avg += values[i];
        avg = avg/values.length;

        return avg;
    }

    public static double countAverage(List<Integer> values){
        double avg = 0;
        for (int i = 0; i < values.size(); i++)
            avg += values.get(i);
        avg = avg/values.size();

        return avg;
    }

    public static double countStandardDeviation(double[] values){
        double avg = countAverage(values);
        double factor;
        double sum = 0;

        for (int i = 0; i < values.length; i++){
            factor = Math.pow(values[i] - avg, 2);
            sum += factor;
        }

        return sum/values.length;
    }

    public static double[] countRR(double[] time, double[] values){

        double avgValue = countAverage(values);
        double standardDeviation = countStandardDeviation(values);

        double rrStart = -1;
        double rrEnd = -1;
        boolean peakFound = false;
        List<Double> RRs = new ArrayList<>();
        List<Double> peaks = new ArrayList<>();

        for (int i = 0; i < values.length; i++){

            double maxValue = -128;
            while (values[i] > avgValue + 3*standardDeviation){

                peakFound = true;
                if (values[i] > maxValue) {
                    maxValue = values[i];
                    rrEnd = time[i];
                }

                i++;
                if (i == values.length-1)
                    break;
            }

            if (peakFound && rrStart != -1) {
                double rr = rrEnd - rrStart;
                if (rr > MIN_RR) {
                    RRs.add(rrEnd - rrStart);
                    peaks.add(maxValue);
                }
                rrStart = rrEnd;
            }

            if (rrStart == -1)
                rrStart = rrEnd;

            peakFound = false;
        }

        double[] result = new double[RRs.size()];
        for (int i = 0; i < RRs.size(); i++)
            result[i] = RRs.get(i);

        return result;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
