package ru.danbat.DialogueToolBox

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraft.client.Minecraft

import org.lwjgl.glfw.GLFW

import ru.danbat.DialogueToolBox.MainScreen.Companion.LOGGER
import ru.danbat.DialogueToolBox.UI.DialogRenderer
import ru.danbat.DialogueToolBox.config.DialogConfig
import ru.danbat.DialogueToolBox.config.ExtendedDialogData

@EventBusSubscriber(modid = MainScreen.MODID, bus = EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
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

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGuiOverlayEvent.Post) {
        dialogRenderer.render(event.poseStack)
    }

    @SubscribeEvent
    fun onMouseInput(event: InputEvent.MouseButton) {
        val mc = Minecraft.getInstance()
        val currentTime = System.currentTimeMillis()
        if (event.button == GLFW.GLFW_MOUSE_BUTTON_2 && event.action == GLFW.GLFW_RELEASE) {
            if (mc.screen == null &&
                dialogRenderer.isDialogVisible() &&
                currentTime - dialogStartTime >= DIALOG_START_COOLDOWN &&
                currentTime - lastDialogSwitchTime >= DIALOG_SWITCH_COOLDOWN) {

                LOGGER.info("ПКМ: Показываем следующий диалог. В очереди: ${dialogQueue.size}")

                showNextDialog()
                lastDialogSwitchTime = currentTime
            }
        }
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
        iconLocation: ResourceLocation? = null,
        config: DialogConfig = DialogConfig()
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