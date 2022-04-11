package com.example.qukuailian.util.Other;

public interface DifferentialPrivacy {

    int signal(double value);

    double getNoisyDigit(double value, double epsilon);

}
