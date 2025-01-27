package ru.danbat.DialogueToolBox.api

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import ru.danbat.DialogueToolBox.MainScreen
import ru.danbat.DialogueToolBox.ModNetworking
import ru.danbat.DialogueToolBox.config.DialogConfig
import ru.danbat.DialogueToolBox.config.DialogType
import ru.danbat.DialogueToolBox.config.ExtendedDialogData

object DialogAPI {
    private val LOGGER = MainScreen.LOGGER

    class DialogBuilder {
        private var text: String = ""
        private var iconPath: String? = null
        private var dialogType: DialogType = DialogType.DYNAMIC
        private var backgroundColor: List<Int>? = null
        private var borderColor: List<Int>? = null
        private var textColor: Int? = null
        private var x: Int? = null
        private var y: Int? = null
        private var width: Int? = null
        private var height: Int? = null
        private var minWidth: Int? = null
        private var minHeight: Int? = null
        private var maxWidth: Int? = null
        private var maxHeight: Int? = null
        private var padding: Padding? = null
        private var iconPadding: Padding? = null
        private var iconSize: Int? = null
        private var maxLines: Int? = null
        private var iconTextSpacing: Int? = null

        // геттеры для приватных свойств... пусть хотя бы так
        internal fun getText(): String = text
        internal fun getIconPath(): String? = iconPath

        data class Padding(
            val left: Int,
            val right: Int,
            val top: Int,
            val bottom: Int
        )

        fun setText(text: String) = apply { this.text = text }
        fun setIcon(path: String) = apply { this.iconPath = path }
        fun setType(type: DialogType) = apply { this.dialogType = type }
        fun setBackgroundColor(r: Int, g: Int, b: Int, a: Int = 150) = apply {
            this.backgroundColor = listOf(r, g, b, a)
        }
        fun setBorderColor(r: Int, g: Int, b: Int, a: Int = 255) = apply {
            this.borderColor = listOf(r, g, b, a)
        }
        fun setTextColor(color: Int) = apply { this.textColor = color }
        fun setPosition(x: Int, y: Int) = apply {
            this.x = x
            this.y = y
        }
        fun setSize(width: Int, height: Int) = apply {
            this.width = width
            this.height = height
        }
        fun setMinSize(width: Int, height: Int) = apply {
            this.minWidth = width
            this.minHeight = height
        }
        fun setMaxSize(width: Int, height: Int) = apply {
            this.maxWidth = width
            this.maxHeight = height
        }
        fun setPadding(left: Int, right: Int, top: Int, bottom: Int) = apply {
            this.padding = Padding(left, right, top, bottom)
        }
        fun setIconPadding(left: Int, right: Int, top: Int, bottom: Int) = apply {
            this.iconPadding = Padding(left, right, top, bottom)
        }
        fun setIconSize(size: Int) = apply { this.iconSize = size }
        fun setMaxLines(lines: Int) = apply { this.maxLines = lines }
        fun setIconTextSpacing(spacing: Int) = apply { this.iconTextSpacing = spacing }

        fun build(): DialogConfig {
            return DialogConfig(
                type = dialogType,
                backgroundColor = backgroundColor ?: listOf(0, 0, 0, 150),
                borderColor = borderColor ?: listOf(255, 255, 255, 255),
                textColor = textColor ?: 0xFFFFFF,
                x = x,
                y = y,
                width = width,
                height = height,
                minWidth = minWidth ?: 100,
                minHeight = minHeight ?: 50,
                maxWidth = maxWidth,
                maxHeight = maxHeight,
                paddingLeft = padding?.left ?: 10,
                paddingRight = padding?.right ?: 10,
                paddingUp = padding?.top ?: 10,
                paddingDown = padding?.bottom ?: 10,
                iconSize = iconSize ?: 32,
                maxLines = maxLines,
                iconPaddingLeft = iconPadding?.left ?: 5,
                iconPaddingRight = iconPadding?.right ?: 5,
                iconPaddingUp = iconPadding?.top ?: 5,
                iconPaddingDown = iconPadding?.bottom ?: 5,
                iconTextSpacing = iconTextSpacing ?: 5
            )
        }
    }

    @JvmStatic
    fun showDialog(player: ServerPlayer, text: String, iconPath: String? = null, config: DialogConfig = DialogConfig()) {
        val iconLocation = iconPath?.let { ResourceLocation(MainScreen.MODID, it) }
        ModNetworking.sendToPlayer(player, text, iconLocation, config)
    }

    @JvmStatic
    fun showDialog(player: ServerPlayer, setup: DialogBuilder.() -> Unit) {
        val builder = DialogBuilder().apply(setup)
        val config = builder.build()
        val iconLocation = builder.getIconPath()?.let { ResourceLocation(MainScreen.MODID, it) }
        ModNetworking.sendToPlayer(player, builder.getText(), iconLocation, config)
    }

    @JvmStatic
    fun showDialogs(player: ServerPlayer, dialogs: List<ExtendedDialogData>) {
        ModNetworking.sendDialogList(player, dialogs)
    }

    fun showDialogs(player: ServerPlayer, setup: DialogChainBuilder.() -> Unit) {
        val builder = DialogChainBuilder().apply(setup)
        showDialogs(player, builder.build())
    }

    class DialogChainBuilder {
        private val dialogs = mutableListOf<ExtendedDialogData>()

        fun dialog(text: String, setup: (DialogBuilder.() -> Unit)? = null) {
            val builder = DialogBuilder().setText(text)
            setup?.let { builder.apply(it) }
            val config = builder.build()
            val iconLocation = builder.getIconPath()?.let { ResourceLocation(MainScreen.MODID, it) }
            dialogs.add(ExtendedDialogData(text, iconLocation, config))
        }

        fun build(): List<ExtendedDialogData> = dialogs
    }
}