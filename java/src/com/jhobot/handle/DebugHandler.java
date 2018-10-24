package com.jhobot.handle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugHandler {
    private Map<String, Log> logs = new HashMap();

    public void internalLog(String domain, String msg) {
        logs.put(domain, new Log(msg, LogLevel.INFO));
    }

    public void internalLog(String domain, String msg, LogLevel level) {
        logs.put(domain, new Log(msg, level));
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
