package ru.dan_bat.dialogue

import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import ru.dan_bat.dialogue.MainScreen.LOGGER
import ru.dan_bat.dialogue.ui.DialogRenderer
import ru.dan_bat.dialogue.config.DialogConfig
import ru.dan_bat.dialogue.config.ExtendedDialogData
import ru.hollowhorizon.hc.common.events.SubscribeEvent
import ru.hollowhorizon.hc.common.events.client.render.GuiOverlay
import ru.hollowhorizon.hc.common.events.client.render.RenderOverlayEvent
import ru.hollowhorizon.hc.common.events.tick.TickEvent

object ClientEvents {
    // Задержка между переключениями диалогов в миллисекундах
    private const val DIALOG_SWITCH_COOLDOWN = 500L
    // Задержка при старте диалога
    private const val DIALOG_START_COOLDOWN = 1000L // потом сделать настраиваемым...
    private var lastDialogSwitchTime = 0L
    private var dialogStartTime = 0L

    private val dialogRenderer = DialogRenderer()
    private var dialogQueue: MutableList<ExtendedDialogData> = mutableListOf()
    private var currentDialog: ExtendedDialogData? = null
    private var wasRightMouseButtonDown = false

    @SubscribeEvent
    fun onRenderOverlay(event: RenderOverlayEvent.Post) {
        if (event.overlay == GuiOverlay.CHAT_PANEL) {
            //? if fabric {
            dialogRenderer.render(event.guiGraphics)
            //?} else {
            /*
            dialogRenderer.render(event.guiGraphics.pose())
            *///?}
        }
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.Client) {
        val mc = Minecraft.getInstance()
        if (mc.screen != null) {
            wasRightMouseButtonDown = false // Сбрасываем состояние, если открыт экран
            return
        }

        val isRightMouseButtonDown = mc.mouseHandler.isRightPressed

        // Обнаруживаем отпускание правой кнопки мыши
        if (!isRightMouseButtonDown && wasRightMouseButtonDown) {
            val currentTime = System.currentTimeMillis()
            if (dialogRenderer.isDialogVisible() &&
                currentTime - dialogStartTime >= DIALOG_START_COOLDOWN &&
                currentTime - lastDialogSwitchTime >= DIALOG_SWITCH_COOLDOWN
            ) {

                LOGGER.info("ПКМ: Показываем следующий диалог. В очереди: ${dialogQueue.size}")
                showNextDialog()
                lastDialogSwitchTime = currentTime
            }
        }
        wasRightMouseButtonDown = isRightMouseButtonDown
    }

    fun addDialogsToQueue(dialogs: List<ExtendedDialogData>) {
        // Добавляем только уникальные диалоги
        val uniqueDialogs = dialogs.distinctBy { it.text }

        LOGGER.info("Добавление диалогов в очередь. Количество: ${uniqueDialogs.size}")

        dialogQueue.addAll(uniqueDialogs)

        // Если в данный момент диалоговое окно не отображается, покажем первое
        if (currentDialog == null) {
            showNextDialog()
        }
    }

    fun showDialog(
        text: String,
        iconLocation: ResourceLocation?,
        config: DialogConfig
    ) {
        val dialogData = ExtendedDialogData(text, iconLocation, config)
        LOGGER.info("Диалог в очереди: '$text' иконка: '${iconLocation?.path}'")
        dialogQueue.add(dialogData)

        if (currentDialog == null) {
            showNextDialog()
        }
    }

    // Метод для показа следующего диалога
    private fun showNextDialog() {
        // Очищаем дубликаты
        dialogQueue = dialogQueue.distinctBy { it.text }.toMutableList()

        if (dialogQueue.isNotEmpty()) {
            currentDialog = dialogQueue.removeAt(0)
            LOGGER.info("Следующий диалог: '${currentDialog?.text}', осталось в очереди: ${dialogQueue.size}")
            dialogRenderer.showDialog(currentDialog!!)
            dialogStartTime = System.currentTimeMillis()
        } else {
            LOGGER.info("Очередь диалогов пуста")
            currentDialog = null
            dialogRenderer.hideDialog()
        }
    }

    // на будущие
    fun clearDialogQueue() {
        dialogQueue.clear()
        currentDialog = null
        dialogRenderer.hideDialog()
    }

    fun hideDialog() {
        LOGGER.info("Скрытие диалога")
        dialogRenderer.hideDialog()
        currentDialog = null
        dialogQueue.clear()
    }
}