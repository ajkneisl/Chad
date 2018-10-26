package com.jhobot.handle;

@SuppressWarnings("CanBeFinal")
public class Log {
    public String domain;
    public String message;
    public LogLevel level;

    public Log(String domain, String message, LogLevel level) {
        this.domain = domain;
        this.message = message;
        this.level = level;
    }
}
