package com.example.qukuailian.util.Other;

public class DifferentialPrivacyImpl {
    public static int signal(double value){
        if(value>0){
            return 1;
        }else if(value<0){
            return -1;
        }else{
            return 0;
        }
    }

    public static double getNoisyDigit(double value, double epsilon){
        double u =  Math.random() - 0.5;
        double noisy_digit = 0.0 - value/epsilon*signal(u)*Math.log(Math.abs(1.0-2*Math.abs(u)));
        return Math.rint(noisy_digit);
    }
}
