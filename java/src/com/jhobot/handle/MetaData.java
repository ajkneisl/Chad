package com.jhobot.handle;

import com.jhobot.handle.commands.Category;

public class MetaData {
    public Category category;
    public boolean isDevOnly;

    public MetaData(Category category, boolean isDevOnly) {
        this.category = category;
        this.isDevOnly = isDevOnly;
    }
}
