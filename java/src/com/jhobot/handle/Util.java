package com.jhobot.handle;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util
{
    public static String getTimeStamp()
    {
        return new SimpleDateFormat("[MM/dd/yyyy:  HH:mm]").format(Calendar.getInstance().getTime());
    }
}