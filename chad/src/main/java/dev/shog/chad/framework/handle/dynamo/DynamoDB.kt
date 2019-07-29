package dev.shog.chad.framework.handle.dynamo

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB

/**
 * Manages connections with Amazon AWS's DynamoDB
 */
object DynamoDB {
    /**
     * Required to login to DynamoDB
     */
    var secret = ""
    var id = ""

    var client: AmazonDynamoDB? = null

    /**
     * Initialize the client.
     */
    fun init() {
        client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(id, secret)))
                .withRegion(Regions.US_EAST_2)
                .build()!!
    }

    /**
     * Creates a connection using client.
     *
     * Connects and returns a [DynamoDB]
     */
    fun create(): DynamoDB? {
        if (client == null) { init() }

        return try {
            return DynamoDB(client)
        } catch (ex: Exception) {
            throw ex
        }
    }
}