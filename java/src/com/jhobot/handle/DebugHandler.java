package com.jhobot.handle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugHandler {
    private Map<String, Log> logs = new HashMap();
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
        Log log = new Log(msg, LogLevel.INFO);
        logs.put(domain, log);
        if (autoSave) {
            writeLog(domain, log);
        }
    }

    public void internalLog(String domain, String msg, LogLevel level) {
        Log log = new Log(msg, level);
        logs.put(domain, log);
        if (autoSave) {
            writeLog(domain, log);
        }
    }

    public List<Log> getLogs(String domain) {
        List<Log> logs_subset = new ArrayList();
        for (String k : logs.keySet()) {
            if (k.equalsIgnoreCase(domain)) {
                logs_subset.add(logs.get(k));
            }
        }
        return logs_subset;
    }

    public List<Log> getLogs(String domain, LogLevel level) {
        List<Log> logs_subset = new ArrayList();
        for (String k : logs.keySet()) {
            if (k.equalsIgnoreCase(domain)) {
                Log v = logs.get(k);
                if (v.level.equals(level)) {
                    logs_subset.add(logs.get(k));
                }
            }
        }
        return logs_subset;
    }
}
