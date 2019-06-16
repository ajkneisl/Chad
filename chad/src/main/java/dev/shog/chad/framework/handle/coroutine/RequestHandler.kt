@file:JvmName("RequestHandler")

package dev.shog.chad.framework.handle.coroutine

import sx.blah.discord.handle.impl.obj.*
import sx.blah.discord.handle.obj.*
import sx.blah.discord.util.RequestBuffer
import java.util.*


/**
 * The result to a request
 */
data class RequestResult internal constructor(val result: Any)

/**
 * If the [RequestResult.result] is a Unit. If this is true, there is no result to grab.
 */
fun RequestResult.isUnit(): Boolean = result is Unit

/**
 * If the [RequestResult.result] is a Unit. If this is true, there is no result to grab.
 * Also has a Unit if it's true.
 */
fun RequestResult.isUnit(onError: () -> Unit) {
    if (result is Unit) onError.invoke()
}

/**
 * The different types that can be required.
 */
enum class Types {
    IMessage, IGuild, IReaction, IChannel, IVoiceChannel, IUser, IRole
}

/**
 * Requires a [List] to be the same type as [type].
 */
private fun RequestResult.requireListType(type: Types): List<*>? {
    val colRes = result as List<*>

    if (colRes.isEmpty()) return Collections.singletonList(type)

    val typeClass = when (type) {
        Types.IMessage -> Message::class
        Types.IGuild -> Guild::class
        Types.IReaction -> Reaction::class
        Types.IChannel -> Channel::class
        Types.IVoiceChannel -> VoiceChannel::class
        Types.IUser -> User::class
        Types.IRole -> Role::class
    }

    return if (colRes[0]!!::class == typeClass) {
        colRes
    } else null
}

/**
 * Gets the [RequestResult.result] as a list of IMessage(s).
 */
fun RequestResult.asIMessageList(): List<IMessage> {
    val pf = requireListType(Types.IMessage) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:L>IMessage")

    return pf as List<IMessage>
}

/**
 * Gets the [RequestResult.result] as a list of IReaction(s).
 */
fun RequestResult.asIReactionList(): List<IReaction> {
    val pf = requireListType(Types.IReaction) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:L>IReaction")

    return pf as List<IReaction>
}

/**
 * Gets the [RequestResult.result] as a list of IGuild(s).
 */
fun RequestResult.asIGuildList(): List<IGuild> {
    val pf = requireListType(Types.IGuild) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:L>IGuild")

    return pf as List<IGuild>
}

/**
 * Gets the [RequestResult.result] as a list of IUser(s).
 */
fun RequestResult.asIUserList(): List<IUser> {
    val pf = requireListType(Types.IUser) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:L>IUser")

    return pf as List<IUser>
}

/**
 * Gets the [RequestResult.result] as a list of IChannel(s).
 */
fun RequestResult.asIChannelList(): List<IChannel> {
    val pf = requireListType(Types.IChannel) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:L>IChannel")

    return pf as List<IChannel>
}

/**
 * Gets the [RequestResult.result] as a list of IVoiceChannel(s).
 */
fun RequestResult.asIVoiceChannelList(): List<IVoiceChannel> {
    val pf = requireListType(Types.IVoiceChannel) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:L>IVoiceChannel")

    return pf as List<IVoiceChannel>
}

/**
 * Gets the [RequestResult.result] as a list of IRole(s).
 */
fun RequestResult.asIRoleList(): List<IRole> {
    val pf = requireListType(Types.IRole) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:L>IRole")

    return pf as List<IRole>
}

/**
 * Gets the [RequestResult.result] as an IMessage.
 */
fun RequestResult.asIMessage(): IMessage {
    val pf = requireType(Types.IMessage) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:IMessage")

    return pf as IMessage
}

/**
 * Gets the [RequestResult.result] as an IGuild.
 */
fun RequestResult.asIGuild(): IGuild {
    val pf = requireType(Types.IGuild) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:IGuild")

    return pf as IGuild
}

/**
 * Gets the [RequestResult.result] as an IReaction.
 */
fun RequestResult.asIReaction(): IReaction {
    val pf = requireType(Types.IReaction) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:IReaction")

    return pf as IReaction
}

/**
 * Gets the [RequestResult.result] as an IChannel.
 */
fun RequestResult.asIChannel(): IChannel {
    val pf = requireType(Types.IChannel) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:IChannel")

    return pf as IChannel
}

/**
 * Gets the [RequestResult.result] as an IVoiceChannel.
 */
fun RequestResult.asIVoiceChannel(): IVoiceChannel {
    val pf = requireType(Types.IVoiceChannel) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:IVoiceChannel")

    return pf as IVoiceChannel
}

/**
 * Gets the [RequestResult.result] as an IUser.
 */
fun RequestResult.asIUser(): IUser {
    val pf = requireType(Types.IUser) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:IUser")

    return pf as IUser
}

/**
 * Gets the [RequestResult.result] as an IRole.
 */
fun RequestResult.asIRole(): IRole {
    val pf = requireType(Types.IRole) ?: throw IllegalArgumentException("INV_REQUEST_RESULT:IRole")

    return pf as IRole
}

/**
 * Gets the [RequestResult.result] as a Boolean.
 */
fun RequestResult.asBoolean(): Boolean {
    if (result !is Boolean) throw IllegalArgumentException("INV_REQUEST_RESULT:Boolean")

    return result
}

/**
 * Requires a type
 */
fun RequestResult.requireType(type: Types): Any? {
    val typeClass = when (type) {
        Types.IMessage -> Message::class
        Types.IGuild -> Guild::class
        Types.IReaction -> Reaction::class
        Types.IChannel -> Channel::class
        Types.IVoiceChannel -> VoiceChannel::class
        Types.IUser -> User::class
        Types.IRole -> Role::class
    }

    return if (result::class != typeClass)
        null
    else {
        if (isUnit())
            null
        else result
    }
}

/**
 * RequestBuffer, for coroutines.
 */
fun request(req: () -> Any): RequestResult = RequestResult(RequestBuffer.request(req).get())