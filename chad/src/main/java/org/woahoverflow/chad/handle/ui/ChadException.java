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
        ChadVar.UI_HANDLER.newError(error + "\n" + b.toString());
        ChadVar.UI_HANDLER.addLog("Error Occurred!", UIHandler.LogLevel.EXCEPTION);
    }
    public static void error(String error)
    {
        ChadVar.UI_HANDLER.newError(error);
        ChadVar.UI_HANDLER.addLog("Error Occurred!", UIHandler.LogLevel.EXCEPTION);
    }
}
