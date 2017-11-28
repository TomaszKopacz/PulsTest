package com.tomaszkopacz.pulseoxymeter.model;

import java.io.Serializable;

/**
 * Created by tomaszkopacz on 28.11.17.
 */

public class CMSData implements Serializable{

    private byte startByte;
    private byte byte1;
    private byte byte2;
    private byte waveformByte;
    private byte byte4;
    private byte pulseByte;
    private byte saturationByte;
    private byte byte7;
    private byte byte8;

    public byte getWaveformByte() {
        return waveformByte;
    }

    public void setWaveformByte(byte waveformByte) {
        this.waveformByte = waveformByte;
    }

    public byte getPulseByte() {
        return pulseByte;
    }

    public void setPulseByte(byte pulseByte) {
        this.pulseByte = pulseByte;
    }

    public byte getSaturationByte() {
        return saturationByte;
    }

    public void setSaturationByte(byte saturationByte) {
        this.saturationByte = saturationByte;
    }
}
