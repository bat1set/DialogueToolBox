package ru.danbat.DialogueToolBox

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

import ru.danbat.DialogueToolBox.MainScreen.Companion.LOGGER
import ru.danbat.DialogueToolBox.config.DialogConfig
import ru.danbat.DialogueToolBox.config.DialogType
import ru.danbat.DialogueToolBox.config.ExtendedDialogData

object ModNetworking {
    private const val PROTOCOL_VERSION = "1"
    val CHANNEL: SimpleChannel = NetworkRegistry.newSimpleChannel(
        ResourceLocation(MainScreen.MODID, "main"),
        { PROTOCOL_VERSION },
        { it == PROTOCOL_VERSION },
        { it == PROTOCOL_VERSION }
    )

    // Пакет для отправки диалога
    data class ShowDialogPacket(val text: String, val iconPath: String?)

    data class DialogEntry(val text: String, val iconPath: String?)

    data class ShowExtendedDialogPacket(
        val text: String,
        val iconPath: String?,
        val config: DialogConfig
    )

    data class DialogListPacket(val dialogs: List<ShowExtendedDialogPacket>)

    private fun writeDialogConfig(buffer: FriendlyByteBuf, config: DialogConfig) {
        buffer.writeInt(config.type.ordinal)
        buffer.writeInt(config.backgroundColor.size)
        config.backgroundColor.forEach { buffer.writeInt(it) }
        buffer.writeInt(config.borderColor.size)
        config.borderColor.forEach { buffer.writeInt(it) }
        buffer.writeInt(config.textColor)
        buffer.writeBoolean(config.x != null)
        config.x?.let { buffer.writeInt(it) }
        buffer.writeBoolean(config.y != null)
        config.y?.let { buffer.writeInt(it) }
        buffer.writeBoolean(config.width != null)
        config.width?.let { buffer.writeInt(it) }
        buffer.writeBoolean(config.height != null)
        config.height?.let { buffer.writeInt(it) }
        buffer.writeInt(config.minWidth)
        buffer.writeInt(config.minHeight)
        buffer.writeBoolean(config.maxWidth != null)
        config.maxWidth?.let { buffer.writeInt(it) }
        buffer.writeBoolean(config.maxHeight != null)
        config.maxHeight?.let { buffer.writeInt(it) }
        buffer.writeInt(config.paddingLeft)
        buffer.writeInt(config.paddingRight)
        buffer.writeInt(config.paddingUp)
        buffer.writeInt(config.paddingDown)
        buffer.writeInt(config.iconSize)
        buffer.writeBoolean(config.maxLines != null)
        config.maxLines?.let { buffer.writeInt(it) }
        buffer.writeInt(config.iconPaddingLeft)
        buffer.writeInt(config.iconPaddingRight)
        buffer.writeInt(config.iconPaddingUp)
        buffer.writeInt(config.iconPaddingDown)
        buffer.writeInt(config.iconTextSpacing)
    }

    private fun readDialogConfig(buffer: FriendlyByteBuf): DialogConfig {
        val type = DialogType.entries[buffer.readInt()]
        val backgroundColorSize = buffer.readInt()
        val backgroundColor = List(backgroundColorSize) { buffer.readInt() }
        val borderColorSize = buffer.readInt()
        val borderColor = List(borderColorSize) { buffer.readInt() }
        val textColor = buffer.readInt()
        val x = if (buffer.readBoolean()) buffer.readInt() else null
        val y = if (buffer.readBoolean()) buffer.readInt() else null
        val width = if (buffer.readBoolean()) buffer.readInt() else null
        val height = if (buffer.readBoolean()) buffer.readInt() else null
        val minWidth = buffer.readInt()
        val minHeight = buffer.readInt()
        val maxWidth = if (buffer.readBoolean()) buffer.readInt() else null
        val maxHeight = if (buffer.readBoolean()) buffer.readInt() else null
        val paddingLeft = buffer.readInt()
        val paddingRight = buffer.readInt()
        val paddingUp = buffer.readInt()
        val paddingDown = buffer.readInt()
        val iconSize = buffer.readInt()
        val maxLines = if (buffer.readBoolean()) buffer.readInt() else null
        val iconPaddingLeft = buffer.readInt()
        val iconPaddingRight = buffer.readInt()
        val iconPaddingUp = buffer.readInt()
        val iconPaddingDown = buffer.readInt()
        val iconTextSpacing = buffer.readInt()

        return DialogConfig(
            type = type,
            backgroundColor = backgroundColor,
            borderColor = borderColor,
            textColor = textColor,
            x = x,
            y = y,
            width = width,
            height = height,
            minWidth = minWidth,
            minHeight = minHeight,
            maxWidth = maxWidth,
            maxHeight = maxHeight,
            paddingLeft = paddingLeft,
            paddingRight = paddingRight,
            paddingUp = paddingUp,
            paddingDown = paddingDown,
            iconSize = iconSize,
            maxLines = maxLines,
            // Новые свойства
            iconPaddingLeft = iconPaddingLeft,
            iconPaddingRight = iconPaddingRight,
            iconPaddingUp = iconPaddingUp,
            iconPaddingDown = iconPaddingDown,
            iconTextSpacing = iconTextSpacing
        )
    }

    fun registerPackets() {
        LOGGER.info("Регистрация пакетов начата")

        CHANNEL.messageBuilder(ShowDialogPacket::class.java, 0, NetworkDirection.PLAY_TO_CLIENT)
            .encoder { packet: ShowDialogPacket, buffer: FriendlyByteBuf ->
                LOGGER.debug("Кодирование пакета: text='${packet.text}', iconPath='${packet.iconPath}'")
                buffer.writeUtf(packet.text)
                buffer.writeUtf(packet.iconPath ?: "")
            }
            .decoder { buffer: FriendlyByteBuf ->
                val text = buffer.readUtf()
                val iconPath = buffer.readUtf().takeIf { it.isNotEmpty() }
                LOGGER.debug("Декодирование пакета: text='${text}', iconPath='${iconPath}'")
                ShowDialogPacket(text, iconPath)
            }
            .consumerMainThread { packet: ShowDialogPacket, contextSupplier: Supplier<NetworkEvent.Context> ->
                LOGGER.info("Получен пакет ShowDialogPacket: text='${packet.text}', iconPath='${packet.iconPath}'")
                contextSupplier.get().enqueueWork {
                    val icon = packet.iconPath?.let {
                        ResourceLocation(MainScreen.MODID, it)
                    }
                    ClientEvents.showDialog(packet.text, icon)
                }
                contextSupplier.get().packetHandled = true
            }
            .add()
        CHANNEL.messageBuilder(DialogListPacket::class.java, 1, NetworkDirection.PLAY_TO_CLIENT)
            .encoder { packet, buffer ->
                buffer.writeInt(packet.dialogs.size)
                packet.dialogs.forEach { dialog ->
                    buffer.writeUtf(dialog.text)
                    buffer.writeUtf(dialog.iconPath ?: "")
                    writeDialogConfig(buffer, dialog.config)
                }
            }
            .decoder { buffer ->
                val size = buffer.readInt()
                val dialogs = List(size) {
                    val text = buffer.readUtf()
                    val iconPath = buffer.readUtf().takeIf { it.isNotEmpty() }
                    val config = readDialogConfig(buffer)
                    ShowExtendedDialogPacket(text, iconPath, config)
                }
                DialogListPacket(dialogs)
            }
            .consumerMainThread { packet, contextSupplier ->
                contextSupplier.get().enqueueWork {
                    // Отправляем первый диалог(оптимизация :>)
                    packet.dialogs.firstOrNull()?.let { firstDialog ->
                        val icon = firstDialog.iconPath?.let {
                            ResourceLocation(MainScreen.MODID, it)
                        }
                        ClientEvents.showDialog(firstDialog.text, icon, firstDialog.config)
                    }

                    // Если больше одного диалога, остальные добавляем в очередь
                    if (packet.dialogs.size > 1) {
                        ClientEvents.addDialogsToQueue(
                            packet.dialogs.drop(1).map { dialog ->
                                ExtendedDialogData(
                                    dialog.text,
                                    dialog.iconPath?.let { ResourceLocation(MainScreen.MODID, it) },
                                    dialog.config
                                )
                            }
                        )
                    }
                }
                contextSupplier.get().packetHandled = true
            }
            .add()

        CHANNEL.messageBuilder(ShowExtendedDialogPacket::class.java, 2, NetworkDirection.PLAY_TO_CLIENT)
            .encoder { packet, buffer ->
                buffer.writeUtf(packet.text)
                buffer.writeUtf(packet.iconPath ?: "")
                writeDialogConfig(buffer, packet.config)
            }
            .decoder { buffer ->
                val text = buffer.readUtf()
                val iconPath = buffer.readUtf().takeIf { it.isNotEmpty() }
                val config = readDialogConfig(buffer)
                ShowExtendedDialogPacket(text, iconPath, config)
            }
            .consumerMainThread { packet, contextSupplier ->
                contextSupplier.get().enqueueWork {
                    val icon = packet.iconPath?.let {
                        ResourceLocation(MainScreen.MODID, it)
                    }


                    val extendedDialog = ExtendedDialogData(
                        text = packet.text,
                        iconLocation = icon,
                        config = packet.config
                    )

                    // отправка полного диалога
                    ClientEvents.showDialog(extendedDialog.text, extendedDialog.iconLocation, extendedDialog.config)
                }
                contextSupplier.get().packetHandled = true
            }
            .add()
        LOGGER.info("Регистрация пакетов завершена")
    }

    // Метод для отправки пакета игроку(не использовать для прямой отправки есть api >:|)
    fun sendToPlayer(player: ServerPlayer, text: String, iconLocation: ResourceLocation? = null, config: DialogConfig = DialogConfig()) {
        CHANNEL.send(
            PacketDistributor.PLAYER.with { player },
            ShowExtendedDialogPacket(text, iconLocation?.path, config)
        )
    }

    fun sendDialogList(player: ServerPlayer, dialogs: List<ExtendedDialogData>) {
        val dialogPackets = dialogs.map { dialog ->
            ShowExtendedDialogPacket(
                dialog.text,
                dialog.iconLocation?.path,
                dialog.config
            )
        }

        CHANNEL.send(
            PacketDistributor.PLAYER.with { player },
            DialogListPacket(dialogPackets)
        )
    }
}
