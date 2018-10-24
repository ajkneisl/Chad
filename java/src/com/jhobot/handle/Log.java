package com.jhobot.handle;

public class Log {
    public String message;
    public LogLevel level;

    public Log(String message, LogLevel level) {
        this.message = message;
        this.level = level;
    }
}
