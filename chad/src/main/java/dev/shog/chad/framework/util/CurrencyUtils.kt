@file:JvmName("CurrencyUtils")

package dev.shog.chad.framework.util

/**
 * Makes sure a user's bet is valid, and can be done
 */
fun validBet(bet: Long, balance: Long, otherBalance: Long? = null): Boolean {
    if (otherBalance != null) {
        if (bet > otherBalance) return false
        if (bet + otherBalance < 0) return false
    }

    if (0 >= bet) return false
    if (bet > balance) return false
    if (bet + balance < 0) return false

    return true
}