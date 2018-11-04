package com.jhobot.handle.ui;

import com.jhobot.core.ChadVar;

public class ChadException
{
    public static void error(String error, Throwable throwable)
    {
        StringBuilder b = new StringBuilder();
        for (StackTraceElement st : throwable.getStackTrace()) {
            b.append(st.toString()).append("\n");
        }
        ChadVar.UI_HANDLER.newError(error + "\n" + b.toString());
        ChadVar.UI_HANDLER.addLog("Error Occurred!");
    }
    public static void error(String error)
    {
        ChadVar.UI_HANDLER.newError(error);
        ChadVar.UI_HANDLER.addLog("Error Occurred!");
    }
}
