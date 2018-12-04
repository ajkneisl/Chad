package org.woahoverflow.chad.framework.ui;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Sends an error to the UI
 *
 * @author sho
 * @since 0.6.3 B2
 */
public final class ChadError
{

    /**
     * Throws an error
     *
     * @param error The string error
     * @param throwable The throwable
     */
    public static void throwError(String error, Throwable throwable)
    {
        // Forms the stacktrace from the throwable
        String stackTrace = Arrays.stream(throwable.getStackTrace()).map(st -> st + "\n").collect(Collectors.joining());

        // Initiates the error UI
        UIHandler.newError(error + '\n' + stackTrace);

        // Adds a log to the main UI
        UIHandler.handle.addLog("Error Occurred!", UIHandler.LogLevel.EXCEPTION);
    }

    /**
     * Throws an error
     *
     * @param error The string error
     */
    public static void throwError(String error)
    {
        // Initiates the error UI with the string error
        UIHandler.newError(error);

        // Adds a log to the main UI
        UIHandler.handle.addLog("Error Occurred!", UIHandler.LogLevel.EXCEPTION);
    }
}
