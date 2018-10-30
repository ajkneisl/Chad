package com.jhobot.handle.commands;

public class CommandData {
    public Category category;
    public boolean isDevOnly;
    public Command commandClass;

    public CommandData(Category category, boolean isDevOnly, Command commandClass) {
        this.category = category;
        this.isDevOnly = isDevOnly;
        this.commandClass = commandClass;
    }
}
