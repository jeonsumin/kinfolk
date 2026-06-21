package com.terry.backend.core.serial.config;

public interface SerialConfiguration<T> {
    T getSerial(Integer value);

    String getDateFormat();

    String getCompareDateFormat();

    String getFormatString();
}
