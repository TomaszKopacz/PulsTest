package com.tomaszkopacz.pulseoxymeter.utils;

import com.jjoe64.graphview.series.DataPoint;

/**
 * Created by tomaszkopacz on 19.12.17.
 */

public class MyMath {

    public static int countAverage(int[] values){
        int avg = 0;
        for (int i = 0; i < values.length; i++)
            avg += values[i];
        avg = avg/values.length;

        return avg;
    }

    public static DataPoint[] countDifferential(int size, double[] time, int[] signal){

        DataPoint[] differential = new DataPoint[size-1];

        //get time and count difference
        for (int i = 0; i < size - 1; i++){

            double timePoint = time[i];
            double signalPoint = signal[i+1] - signal[i];
            differential[i] = new DataPoint(timePoint, signalPoint);
        }

        return differential;
    }
}
