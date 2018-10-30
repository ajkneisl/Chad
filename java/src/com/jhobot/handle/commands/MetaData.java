package com.jhobot.handle.commands;

public class MetaData {
    public Category category;
    public boolean isDevOnly;

    public MetaData(Category category, boolean isDevOnly) {
        this.category = category;
        this.isDevOnly = isDevOnly;
    }
}
