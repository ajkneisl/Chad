package org.woahoverflow.chad.handle;

import org.woahoverflow.chad.handle.logging.Log;
import org.woahoverflow.chad.handle.logging.LogLevel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ALL", "WeakerAccess"})
public class DebugHandler {
    private List<Log> logs = new ArrayList();
    public boolean autoSave = false;

    private String fileName = System.getenv("appdata") + "\\jho\\debugger.log";
    public void writeLog(String domain, Log log) {
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(fileName));  //clears file every time
            output.append("[" + Util.getTimeStamp() + " <" + domain + " " + log.level.toString() + ">] " + log.message);
            output.newLine();
            output.close();
        } catch (Exception e) {
            internalLog("chad.internal.debug", e.getMessage(), LogLevel.EXCEPTION);
            e.printStackTrace();
        }
    }

    public void internalLog(String domain, String msg) {
        Log log = new Log(domain, msg, LogLevel.INFO);
        logs.add(log);
        if (autoSave) {
            writeLog(domain, log);
        }
    }

    public void internalLog(String domain, String msg, LogLevel level) {
        Log log = new Log(domain, msg, level);
        logs.add(log);
        if (autoSave) {
            writeLog(domain, log);
        }
    }

    public List<Log> getLogs(String domain) {
        List<Log> logs_subset = new ArrayList();
        for (Log log : logs) {
            if (log.domain.equalsIgnoreCase(domain)) {
                logs_subset.add(log);
            }
        }
        return logs_subset;
    }

    public List<Log> getLogs(String domain, LogLevel level) {
        List<Log> logs_subset = new ArrayList();
        for (Log log : logs) {
            if (log.domain.equalsIgnoreCase(domain)) {
                if (log.level.equals(level)) {
                    logs_subset.add(log);
                }
            }
        }
        return logs_subset;
    }
}
