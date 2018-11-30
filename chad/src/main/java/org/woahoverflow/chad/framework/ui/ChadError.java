package org.woahoverflow.chad.framework.ui;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class ChadError
{
    // Catches a throwable, and sends a string in an error format
    public static void throwError(String error, Throwable throwable)
    {
        // Forms the stacktrace from the throwable
        String stackTrace = Arrays.stream(throwable.getStackTrace()).map(st -> st + "\n").collect(Collectors.joining());

        // Initiates the error UI
        UIHandler.newError(error + '\n' + stackTrace);

        // Adds a log to the main UI
        UIHandler.handle.addLog("Error Occurred!", UIHandler.LogLevel.EXCEPTION);
    }

    // Sends a string in an error format
    public static void throwError(String error)
    {
        // Initiates the error UI with the string error
        UIHandler.newError(error);

        // Adds a log to the main UI
        UIHandler.handle.addLog("Error Occurred!", UIHandler.LogLevel.EXCEPTION);
    }
}
