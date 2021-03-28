package com.digitalpathology.digi_report.utils;

import java.text.DecimalFormat;
import java.util.Random;

public class RandomValGen {
    public float betMinMax(float min, float max){
        Random r = new Random();
        DecimalFormat df = new DecimalFormat("0.00");
        float random = min + r.nextFloat() * (max - min);
        return Float.parseFloat(df.format(random));
    }
}
