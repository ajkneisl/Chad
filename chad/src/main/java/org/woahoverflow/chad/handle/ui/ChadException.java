package org.woahoverflow.chad.handle.ui;

import org.woahoverflow.chad.core.ChadVar;

public class ChadException
{
    public static void error(String error, Throwable throwable)
    {
        StringBuilder b = new StringBuilder();
        for (StackTraceElement st : throwable.getStackTrace()) {
            b.append(st.toString()).append("\n");
        }
        ChadVar.UI_DEVICE.newError(error + "\n" + b.toString());
        ChadVar.UI_DEVICE.addLog("Error Occurred!", UIHandler.LogLevel.EXCEPTION);
    }
    public static void error(String error)
    {
        ChadVar.UI_DEVICE.newError(error);
        ChadVar.UI_DEVICE.addLog("Error Occurred!", UIHandler.LogLevel.EXCEPTION);
    }
}
