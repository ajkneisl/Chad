package org.woahoverflow.chad.handle.commands;

public class CommandData {
    public final Category category;
    public final boolean isDevOnly;
    public final Command commandClass;

    public CommandData(Category category, boolean isDevOnly, Command commandClass) {
        this.category = category;
        this.isDevOnly = isDevOnly;
        this.commandClass = commandClass;
    }
}
