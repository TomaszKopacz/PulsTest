package com.tomaszkopacz.pulseoxymeter.model;

import java.io.Serializable;

/**
 * Created by tomaszkopacz on 28.11.17.
 * Model - class holding package of bytes from CMS50EW.
 */

public class CMSData implements Serializable{

    private int startByte;
    private int byte1;
    private int byte2;
    private int waveformByte;
    private int byte4;
    private int pulseByte;
    private int saturationByte;
    private int byte7;
    private int byte8;

    public int getWaveformByte() {
        return waveformByte;
    }

    public void setWaveformByte(int waveformByte) {
        this.waveformByte = waveformByte;
    }

    public int getPulseByte() {
        return pulseByte;
    }

    public void setPulseByte(int pulseByte) {
        this.pulseByte = pulseByte;
    }

    public int getSaturationByte() {
        return saturationByte;
    }

    public void setSaturationByte(int saturationByte) {
        this.saturationByte = saturationByte;
    }
}
