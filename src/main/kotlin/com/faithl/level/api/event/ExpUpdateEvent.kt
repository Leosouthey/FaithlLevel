package com.faithl.level.api.event

import com.faithl.level.internal.core.Level
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author Leosouthey
 * @since 2021/12/18-19:07
 **/
class ExpUpdateEvent {

    class Before(
        val level: Level,
        val target: String,
        val changeType: ChangeType,
        var value: Int
    ) : BukkitProxyEvent()

    class After(
        val level: Level,
        val target: String,
        val changeType: ChangeType,
        var value: Int
    ) : BukkitProxyEvent()

}