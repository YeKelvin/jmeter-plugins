package org.apache.jmeter.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class sign {
    public static void main(String[] args) {
        Instant instant = Instant.now() ;
        System.out.println(instant.toEpochMilli());
//        Date d=new Date();
//        DateFormat format=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
//        System.out.println(format.format(d));
    }
}