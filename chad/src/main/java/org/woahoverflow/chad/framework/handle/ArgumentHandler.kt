package org.woahoverflow.chad.framework.handle

import org.woahoverflow.chad.core.ChadInstance

/**
 * The values
 */
private val values = hashMapOf<String, Boolean>()

/**
 * If it has ran yet. If it hasn't, this will disallow any activity.
 */
private var ran: Boolean = false

/**
 * Loads the arguments
 */
fun load(args: Array<String>) {
    if (!ran) {
        values["TEST_RUN"] = false
        values["DISABLE_UI"] = false
        values["DISABLE_UI_UPDATE"] = false
        values["DISABLE_EXTERNAL_SYNC"] = false
    }

    val start = System.currentTimeMillis()
    ChadInstance.getLogger().debug("Checking arguments...")

    for (arg in args) {
        if (values.keys.contains(arg.toUpperCase().substring(1))) {
            ChadInstance.getLogger().debug("{} has been enabled!", arg.toUpperCase().substring(1))
            values[arg.toUpperCase().substring(1)] = true
        }
    }

    ChadInstance.getLogger().debug("Completed checking arguments! Took ${System.currentTimeMillis()-start}ms")

    ran = true
}

/**
 * If an option is toggled
 */
fun isToggled(option: String): Boolean {
    return values.keys.contains(option.toUpperCase()) && values[option.toUpperCase()]!!
}