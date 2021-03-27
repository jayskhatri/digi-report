package com.digitalpathology.digi_report.utils;

import java.util.Random;

public class RandomValGen {
    public float betMinMax(float min, float max){
        Random r = new Random();
        float random = min + r.nextFloat() * (max - min);
        return random;
    }
}
