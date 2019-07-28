package dev.shog.chad.framework.handle.database

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.PrimaryKey
import com.amazonaws.services.dynamodbv2.document.Table

/**
 * Makes managing a table easier. With the primary key being: [indicator]: (inputted)
 */
class DatabaseHandle(val table: Table, private val indicator: String) {
    /**
     * Gets an Item
     */
    fun getObject(indicator: Any): Item? = table.getItem(this.indicator, indicator)

    /**
     * Sets objects for the [indicator].
     */
    fun setObjects(indicator: Any, vararg updates: AttributeUpdate) {
        table.updateItem(PrimaryKey(this.indicator, indicator), *updates)
    }
}