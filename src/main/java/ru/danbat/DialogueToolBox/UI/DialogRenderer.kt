package ru.danbat.DialogueToolBox.UI

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font

import ru.danbat.DialogueToolBox.MainScreen.Companion.LOGGER
import ru.danbat.DialogueToolBox.config.DialogType
import ru.danbat.DialogueToolBox.config.ExtendedDialogData
import ru.danbat.DialogueToolBox.config.DialogConfig
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

    fun render(poseStack: PoseStack) {
        if (!isVisible || currentDialog == null) return

        val config = currentDialog!!.config
        val screenWidth = mc.window.guiScaledWidth
        val screenHeight = mc.window.guiScaledHeight

        val (dialogWidth, dialogHeight, x, y) = calculateDialogDimensions(config, screenWidth, screenHeight)

        drawing.renderDialogBackground(poseStack, x, y, dialogWidth, dialogHeight, config)

        currentDialog?.iconLocation?.let { icon ->
            drawing.renderIcon(poseStack, icon, x, y, dialogHeight, config)
        }

        renderDialogText(poseStack, x, y, dialogWidth, config)
    }

    private fun renderDialogText(
        poseStack: PoseStack,
        x: Int,
        y: Int,
        dialogWidth: Int,
        config: DialogConfig
    ) {
        val iconOffsetX = if (currentDialog?.iconLocation != null)
            config.iconPaddingLeft + config.iconSize + config.iconTextSpacing + config.iconPaddingRight
        else
            0

        val textX = x + config.paddingLeft + iconOffsetX
        val textY = y + config.paddingUp

        val textWidth = dialogWidth - (config.paddingLeft + iconOffsetX + config.paddingRight)

        val component = FormattedTextParser.parseFormattedTextToComponent(currentDialog?.text ?: "")
        val fontRenderer = mc.font
        val lines = fontRenderer.split(component, textWidth)

        lines.forEachIndexed { index, line ->
            fontRenderer.draw(
                poseStack,
                line,
                textX.toFloat(),
                (textY + index * fontRenderer.lineHeight).toFloat(),
                config.textColor
            )
        }
    }

    private fun calculateDialogDimensions(
        config: DialogConfig,
        screenWidth: Int,
        screenHeight: Int
    ): DialogDimensions {
        val fontRenderer = mc.font

        val dialogWidth: Int
        val dialogHeight: Int

        // Вычисляем позицию по X и Y
        val x: Int
        val y: Int

        when (config.type) {
            DialogType.STATIC -> {
                dialogWidth = config.width ?: config.minWidth
                dialogHeight = config.height ?: config.minHeight
                x = config.x ?: 0
                y = config.y ?: 0
            }

            DialogType.DYNAMIC -> {
                dialogWidth = (screenWidth * 0.5).toInt().coerceIn(350, 500)
                val textWidth = dialogWidth - (
                        if (currentDialog?.iconLocation != null)
                            config.iconSize + config.paddingLeft + config.paddingRight
                        else
                            config.paddingLeft + config.paddingRight
                        )

                dialogHeight = calculateDialogHeight(
                    fontRenderer,
                    currentDialog?.text ?: "",
                    textWidth,
                    config
                )
                x = (screenWidth - dialogWidth) / 2
                y = screenHeight - dialogHeight - 30
            }

            DialogType.TEXT_ADAPTIVE -> {
                dialogWidth = calculateTextAdaptiveWidth(config, screenWidth)
                val textWidth = dialogWidth - (
                        if (currentDialog?.iconLocation != null)
                            config.iconSize + config.paddingLeft + config.paddingRight
                        else
                            config.paddingLeft + config.paddingRight
                        )
                dialogHeight = calculateDialogHeight(
                    fontRenderer,
                    currentDialog?.text ?: "",
                    textWidth,
                    config
                )
                x = (screenWidth - dialogWidth) / 2
                y = screenHeight - dialogHeight - 30
            }

            DialogType.LIMITED -> {
                /*
                в будущем оптимизировать, он слишком затратный для рендера лол
                 */
                val maxDialogWidth = config.maxWidth ?: (screenWidth * 0.8).toInt()
                val maxDialogHeight = config.maxHeight ?: (screenHeight * 0.8).toInt()

                val iconWidth = if (currentDialog?.iconLocation != null)
                    config.iconPaddingLeft + config.iconSize + config.iconTextSpacing + config.iconPaddingRight
                else 0

                // Рассчитываем доступную ширину текста
                val availableTextWidth = maxDialogWidth - (config.paddingLeft + config.paddingRight + iconWidth)

                // Используем fontRenderer для разбивки текста и определения фактической ширины диалога
                val component = FormattedTextParser.parseFormattedTextToComponent(currentDialog?.text ?: "")
                val wrappedLines = fontRenderer.split(component, availableTextWidth)

                // Определяем фактическую ширину диалога на основе ширины самых длинных строк
                val textLineWidths = wrappedLines.map { fontRenderer.width(it) }
                val maxTextLineWidth = textLineWidths.maxOrNull() ?: 0
                dialogWidth = (maxTextLineWidth + config.paddingLeft + config.paddingRight + iconWidth)
                    .coerceAtLeast(config.minWidth)
                    .coerceAtMost(maxDialogWidth)

                // Рассчитываем высоту диалога
                val textHeight = wrappedLines.size * fontRenderer.lineHeight
                val iconHeight = if (currentDialog?.iconLocation != null)
                    config.iconPaddingUp + config.iconSize + config.iconPaddingDown
                else 0
                val contentHeight = max(textHeight, iconHeight)
                dialogHeight = (config.paddingUp + contentHeight + config.paddingDown)
                    .coerceAtLeast(config.minHeight)
                    .coerceAtMost(maxDialogHeight)

                // Определяем позицию по X и Y
                x = config.x ?: ((screenWidth - dialogWidth) / 2)
                y = config.y ?: (screenHeight - dialogHeight - 30)
            }
        }

        return DialogDimensions(dialogWidth, dialogHeight, x, y)
    }

    private fun calculateTextAdaptiveWidth(config: DialogConfig, screenWidth: Int): Int {
        val fontRenderer = mc.font
        val text = currentDialog?.text ?: ""
        val textWidth = fontRenderer.width(text)
        return (textWidth +
                (currentDialog?.iconLocation?.let { config.iconSize } ?: 0) +
                config.paddingLeft +
                config.paddingRight
                )
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

        val iconHeight = if (currentDialog?.iconLocation != null)
            config.iconPaddingUp + config.iconSize + config.iconPaddingDown
            else 0

        val contentHeight = max(textHeight, iconHeight)

        val calculatedHeight = config.paddingUp + contentHeight + config.paddingDown

        return calculatedHeight.coerceAtMost(maxDialogHeight)
    }


}
