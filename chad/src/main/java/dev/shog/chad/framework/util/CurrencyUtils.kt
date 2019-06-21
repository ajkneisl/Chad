@file:JvmName("CurrencyUtils")

package dev.shog.chad.framework.util

/**
 * Makes sure a user's bet is valid, and can be done
 */
fun validBet(bet: Long, balance: Long, otherBalance: Long? = null): Boolean {
    when {
        0 >= bet -> return false
        bet > balance -> return false
        bet + balance < 0 -> return false
        
        otherBalance != null -> {
            when {
                bet > otherBalance -> return false
                bet + otherBalance < 0 -> return false
            }
        }
    }

    return true
}