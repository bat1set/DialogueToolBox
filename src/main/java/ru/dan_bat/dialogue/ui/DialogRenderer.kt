package ru.dan_bat.dialogue.ui

//? if fabric {
import net.minecraft.client.gui.GuiGraphics
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
*///?}

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import ru.dan_bat.dialogue.MainScreen.LOGGER
import ru.dan_bat.dialogue.config.DialogType
import ru.dan_bat.dialogue.config.ExtendedDialogData
import ru.dan_bat.dialogue.config.DialogConfig
import kotlin.math.max


class DialogRenderer {
    private var currentDialog: ExtendedDialogData? = null
    private var isVisible = false
    private val mc = Minecraft.getInstance()
    private val drawing = MethodsFromRendering()

    fun showDialog(dialogData: ExtendedDialogData) {
        currentDialog = dialogData
        isVisible = true
        LOGGER.info("Показ диалогового окна: текст='${dialogData.text}', type='${dialogData.config.type}'")
    }

    fun hideDialog() {
        isVisible = false
        currentDialog = null
        LOGGER.info("Скрытие диалогового окна")
    }

    fun isDialogVisible(): Boolean = isVisible

    //? if fabric {
    fun render(context: GuiGraphics) {
        val dialog = currentDialog ?: return
        if (!isVisible) return

        val config = dialog.config
        val screenWidth = mc.window.guiScaledWidth
        val screenHeight = mc.window.guiScaledHeight

        val dimensions = calculateDialogDimensions(config, screenWidth, screenHeight)

        // Отрисовка фона и рамки
        drawing.renderDialogBackground(
            context,
            dimensions.x,
            dimensions.y,
            dimensions.width,
            dimensions.height,
            config
        )

        // Отрисовка иконки
        dialog.iconLocation?.let { icon ->
            drawing.renderIcon(context, icon, dimensions.x, dimensions.y, dimensions.height, config)
        }

        // Отрисовка текста
        renderDialogText(context, dimensions.x, dimensions.y, dimensions.width, config, dialog)
    }

    private fun renderDialogText(
        context: GuiGraphics,
        x: Int,
        y: Int,
        dialogWidth: Int,
        config: DialogConfig,
        dialog: ExtendedDialogData
    ) {
        val iconOffsetX = if (dialog.iconLocation?.equals(null) != true) {
            config.iconPaddingLeft + config.iconSize + config.iconTextSpacing + config.iconPaddingRight
        } else {
            0
        }

        val textX = x + config.paddingLeft + iconOffsetX
        val textY = y + config.paddingUp
        val textWidth = dialogWidth - (config.paddingLeft + iconOffsetX + config.paddingRight)

        val component = FormattedTextParser.parseFormattedTextToComponent(dialog.text)
        val fontRenderer = mc.font
        val lines = fontRenderer.split(component, textWidth)

        lines.forEachIndexed { index, line ->
            context.drawString(
                fontRenderer,
                line,
                textX,
                textY + index * fontRenderer.lineHeight,
                config.textColor,
                false
            )
        }
    }
    //?} else {
    /*fun render(poseStack: PoseStack) {
        val dialog = currentDialog ?: return
        if (!isVisible) return

        val config = dialog.config
        val screenWidth = mc.window.guiScaledWidth
        val screenHeight = mc.window.guiScaledHeight

        val dimensions = calculateDialogDimensions(config, screenWidth, screenHeight)

        // Отрисовка фона и рамки
        drawing.renderDialogBackground(
            poseStack,
            dimensions.x,
            dimensions.y,
            dimensions.width,
            dimensions.height,
            config
        )

        // Отрисовка иконки
        dialog.iconLocation?.let { icon ->
            drawing.renderIcon(poseStack, icon, dimensions.x, dimensions.y, config)
        }

        // Отрисовка текста
        renderDialogText(poseStack, dimensions.x, dimensions.y, dimensions.width, config, dialog)
    }

    private fun renderDialogText(
        poseStack: PoseStack,
        x: Int,
        y: Int,
        dialogWidth: Int,
        config: DialogConfig,
        dialog: ExtendedDialogData
    ) {
        val iconOffsetX = if (dialog.iconLocation?.equals(null) != true) {
            config.iconPaddingLeft + config.iconSize + config.iconTextSpacing + config.iconPaddingRight
        } else {
            0
        }

        val textX = x + config.paddingLeft + iconOffsetX
        val textY = y + config.paddingUp
        val textWidth = dialogWidth - (config.paddingLeft + iconOffsetX + config.paddingRight)

        val component = FormattedTextParser.parseFormattedTextToComponent(dialog.text)
        val fontRenderer = mc.font
        val lines: List<net.minecraft.util.FormattedCharSequence> = fontRenderer.split(component, textWidth)

        // Получаем матрицу и буфер для рисования
        val matrix = poseStack.last().pose()
        val buffer: MultiBufferSource = mc.renderBuffers().bufferSource()

        val dropShadow = false
        val displayMode = Font.DisplayMode.NORMAL
        val backgroundColor = 0
        val packedLight = 15728880

        lines.forEachIndexed { index, line ->
            fontRenderer.drawInBatch(
                line,
                textX.toFloat(),
                (textY + index * fontRenderer.lineHeight).toFloat(),
                config.textColor,
                dropShadow,
                matrix,
                buffer,
                displayMode,
                backgroundColor,
                packedLight
            )
        }

        try {
            if (buffer is MultiBufferSource.BufferSource) {
                buffer.endBatch()
            }
        } catch (_: Exception) {
        }
    }
    *///?}

    private fun calculateDialogDimensions(
        config: DialogConfig,
        screenWidth: Int,
        screenHeight: Int
    ): DialogDimensions {
        return when (config.type) {
            DialogType.STATIC -> calculateStaticDimensions(config)
            DialogType.DYNAMIC -> calculateDynamicDimensions(config, screenWidth, screenHeight)
            DialogType.TEXT_ADAPTIVE -> calculateTextAdaptiveDimensions(config, screenWidth, screenHeight)
            DialogType.LIMITED -> calculateLimitedDimensions(config, screenWidth, screenHeight)
        }
    }

    private fun calculateStaticDimensions(config: DialogConfig): DialogDimensions {
        val dialogWidth = config.width ?: config.minWidth
        val dialogHeight = config.height ?: config.minHeight
        val x = config.x ?: 0
        val y = config.y ?: 0

        return DialogDimensions(dialogWidth, dialogHeight, x, y)
    }

    private fun buildDialogDimensions(
        dialogWidth: Int,
        config: DialogConfig,
        screenWidth: Int,
        screenHeight: Int
    ): DialogDimensions {
        val textWidth = calculateAvailableTextWidth(dialogWidth, config)
        val dialogHeight = calculateDialogHeight(mc.font, currentDialog?.text ?: "", textWidth, config)
        val x = (screenWidth - dialogWidth) / 2
        val y = screenHeight - dialogHeight - 30

        return DialogDimensions(dialogWidth, dialogHeight, x, y)
    }

    private fun calculateDynamicDimensions(
        config: DialogConfig,
        screenWidth: Int,
        screenHeight: Int
    ): DialogDimensions {
        val dialogWidth = (screenWidth * 0.5).toInt().coerceIn(350, 500)
        return buildDialogDimensions(dialogWidth, config, screenWidth, screenHeight)
    }

    private fun calculateTextAdaptiveDimensions(
        config: DialogConfig,
        screenWidth: Int,
        screenHeight: Int
    ): DialogDimensions {
        val dialogWidth = calculateTextAdaptiveWidth(config, screenWidth)
        return buildDialogDimensions(dialogWidth, config, screenWidth, screenHeight)
    }


    private fun calculateLimitedDimensions(
        config: DialogConfig,
        screenWidth: Int,
        screenHeight: Int
    ): DialogDimensions {
        val maxDialogWidth = config.maxWidth ?: (screenWidth * 0.8).toInt()
        val maxDialogHeight = config.maxHeight ?: (screenHeight * 0.8).toInt()

        val iconWidth = if (currentDialog?.iconLocation?.equals(null) != true) {
            config.iconPaddingLeft + config.iconSize + config.iconTextSpacing + config.iconPaddingRight
        } else {
            0
        }

        val availableTextWidth = maxDialogWidth - (config.paddingLeft + config.paddingRight + iconWidth)
        val component = FormattedTextParser.parseFormattedTextToComponent(currentDialog?.text ?: "")
        val fontRenderer = mc.font
        val wrappedLines = fontRenderer.split(component, availableTextWidth)

        // Определяем фактическую ширину диалога
        val textLineWidths = wrappedLines.map { fontRenderer.width(it) }
        val maxTextLineWidth = textLineWidths.maxOrNull() ?: 0
        val dialogWidth = (maxTextLineWidth + config.paddingLeft + config.paddingRight + iconWidth)
            .coerceAtLeast(config.minWidth)
            .coerceAtMost(maxDialogWidth)

        // Рассчитываем высоту диалога
        val textHeight = wrappedLines.size * fontRenderer.lineHeight
        val iconHeight = if (currentDialog?.iconLocation?.equals(null) != true) {
            config.iconPaddingUp + config.iconSize + config.iconPaddingDown
        } else {
            0
        }
        val contentHeight = max(textHeight, iconHeight)
        val dialogHeight = (config.paddingUp + contentHeight + config.paddingDown)
            .coerceAtLeast(config.minHeight)
            .coerceAtMost(maxDialogHeight)

        val x = config.x ?: ((screenWidth - dialogWidth) / 2)
        val y = config.y ?: (screenHeight - dialogHeight - 30)

        return DialogDimensions(dialogWidth, dialogHeight, x, y)
    }

    private fun calculateAvailableTextWidth(dialogWidth: Int, config: DialogConfig): Int {
        return if (currentDialog?.iconLocation?.equals(null) != true) {
            dialogWidth - (config.iconSize + config.paddingLeft + config.paddingRight)
        } else {
            dialogWidth - (config.paddingLeft + config.paddingRight)
        }
    }

    private fun calculateTextAdaptiveWidth(config: DialogConfig, screenWidth: Int): Int {
        val fontRenderer = mc.font
        val text = currentDialog?.text ?: ""
        val textWidth = fontRenderer.width(text)
        val iconWidth = currentDialog?.iconLocation?.let { config.iconSize } ?: 0

        return (textWidth + iconWidth + config.paddingLeft + config.paddingRight)
            .coerceAtMost((screenWidth * 0.8).toInt())
            .coerceAtLeast(config.minWidth)
    }

    private fun calculateDialogHeight(
        fontRenderer: Font,
        text: String,
        textWidth: Int,
        config: DialogConfig
    ): Int {
        val component = FormattedTextParser.parseFormattedTextToComponent(text)
        val lines = fontRenderer.split(component, textWidth)
        val textHeight = lines.size * fontRenderer.lineHeight

        val maxDialogHeight = mc.window.guiScaledHeight - 30

        val iconHeight = if (currentDialog?.iconLocation?.equals(null) != true) {
            config.iconPaddingUp + config.iconSize + config.iconPaddingDown
        } else {
            0
        }

        val contentHeight = max(textHeight, iconHeight)
        val calculatedHeight = config.paddingUp + contentHeight + config.paddingDown

        return calculatedHeight.coerceAtMost(maxDialogHeight)
    }
}