package ru.dan_bat.dialogue.network

import kotlinx.serialization.Serializable
import net.minecraft.world.entity.player.Player
import ru.dan_bat.dialogue.config.ExtendedDialogData
import ru.dan_bat.dialogue.ClientEvents
import ru.dan_bat.dialogue.config.DialogConfig
import ru.hollowhorizon.hc.common.network.HollowPacket
import ru.hollowhorizon.hc.common.network.HollowPacketHandler
import ru.hollowhorizon.hc.common.utils.rl

@Serializable
data class SerializableDialogData(
    val text: String,
    val iconLocation: String?,
    val config: DialogConfig
) {
    fun toExtended() = ExtendedDialogData(text, iconLocation?.rl, config)
}

fun ExtendedDialogData.toSerializable() = SerializableDialogData(text, iconLocation?.toString(), config)

@Serializable
@HollowPacketHandler(HollowPacketHandler.Direction.TO_CLIENT)
class ShowSingleDialogPacket(
    private val text: String,
    private val iconLocation: String?,
    private val config: DialogConfig
) : HollowPacket {
    override fun handle(player: Player) {
        ClientEvents.showDialog(text, iconLocation?.rl, config)
    }
}

@Serializable
@HollowPacketHandler(HollowPacketHandler.Direction.TO_CLIENT)
class ShowDialogListPacket(
    private val dialogs: List<SerializableDialogData>
) : HollowPacket {
    override fun handle(player: Player) {
        ClientEvents.addDialogsToQueue(dialogs.map { it.toExtended() })
    }
}

@Serializable
@HollowPacketHandler(HollowPacketHandler.Direction.TO_CLIENT)
class HideDialogPacket : HollowPacket {
    override fun handle(player: Player) {
        ClientEvents.hideDialog()
    }
}