package org.woahoverflow.chad.framework.handle

import org.woahoverflow.chad.core.ChadInstance

/**
 * Manages arguments that're inputted on start
 */
object ArgumentHandler {
    /**
     * The values
     */
    @JvmStatic
    val LAUNCH_ARGUMENTS = hashMapOf<String, Boolean>()

    /**
     * If it has ran yet. If it hasn't, this will disallow any activity.
     */
    @JvmStatic
    private var ran: Boolean = false

    /**
     * Loads the arguments
     */
    @JvmStatic
    fun load(args: Array<String>) {
        if (!ran) {
            LAUNCH_ARGUMENTS["TEST_RUN"] = false
            LAUNCH_ARGUMENTS["DISABLE_EXTERNAL_SYNC"] = false
            LAUNCH_ARGUMENTS["DISABLE_STEAM_CACHE"] = false
        }

        val start = System.currentTimeMillis()
        ChadInstance.getLogger().debug("Checking arguments...")

        for (arg in args) {
            if (LAUNCH_ARGUMENTS.keys.contains(arg)) {
                ChadInstance.getLogger().debug("{} has been enabled!", arg)
                LAUNCH_ARGUMENTS[arg.toUpperCase()] = true
            }
        }

        ChadInstance.getLogger().debug("Completed checking arguments! Took ${System.currentTimeMillis()-start}ms")

        ran = true
    }

    /**
     * If an option is toggled
     */
    @JvmStatic
    fun isToggled(option: String): Boolean = LAUNCH_ARGUMENTS.keys.contains(option.toUpperCase()) && LAUNCH_ARGUMENTS[option.toUpperCase()]!!
}