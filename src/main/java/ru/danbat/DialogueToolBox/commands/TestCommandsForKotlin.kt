package ru.danbat.DialogueToolBox.commands

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer
import ru.danbat.DialogueToolBox.api.DialogAPI
import ru.danbat.DialogueToolBox.config.DialogType
// думаю тут всё интуитивно понятно :>
class TestCommandsForKotlin {
    companion object {
        fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
            dispatcher.register(Commands.literal("kdialog")
                .requires { source -> source.hasPermission(2) }
                .then(
                    Commands.literal("shop")
                    .executes { context ->
                        val player = context.source.playerOrException
                        showShopDialog(player)
                        1
                    })
                .then(Commands.literal("achievement")
                    .executes { context ->
                        val player = context.source.playerOrException
                        showAchievementDialog(player)
                        1
                    })
                .then(Commands.literal("story")
                    .executes { context ->
                        val player = context.source.playerOrException
                        showStorySequence(player)
                        1
                    }))
        }

        private fun showShopDialog(player: ServerPlayer) {
            DialogAPI.showDialog(player) {
                setText("""
                    <color=#FFD700><b>Добро пожаловать в Волшебный магазин!</b></color>
                    
                    <color=#E6E6FA>Особые предметы сегодняшнего дня:</color>
                    <color=#FF69B4>• Зачарованный меч – 500 г</color>
                    <color=#87CEEB>• Волшебное зелье – 250 г</color>
                    <color=#98FB98>• Свиток телепортации – 100 г</color>
                    
                    <i>Нажмите на товар, чтобы купить!</i>
                """.trimIndent())
                setIcon("icons/shop")
                setType(DialogType.TEXT_ADAPTIVE)
                setBackgroundColor(25, 25, 112, 200)
                setBorderColor(255, 215, 0, 255)
                setMinSize(350, 200)
                setPadding(20, 20, 20, 20)
                setIconSize(64)
            }
        }

        private fun showAchievementDialog(player: ServerPlayer) {
            DialogAPI.showDialog(player) {
                setText("""
                    <color=#00FF00><b>Достижение разблокировано!</b></color>
                    
                    <color=#FFFFFF>Мастер-строитель</color>
                    <i>Постройте 100 построек</i>
                    
                    <color=#FFD700>Награда: набор специальных строительных блоков</color>
                """.trimIndent())
                setIcon("icons/achievement")
                setType(DialogType.DYNAMIC)
                setBackgroundColor(0, 0, 0, 180)
                setBorderColor(0, 255, 0, 255)
                setTextColor(0xE6E6FA)
                setIconTextSpacing(15)
            }
        }

        private fun showStorySequence(player: ServerPlayer) {
            DialogAPI.showDialogs(player) {
                dialog("""
                    <color=#FF6B6B><b>Глава 1: Начало</b></color>
                    
                    <i>В мире безграничных возможностей...</i>
                    <color=#FFFFFF>Ваше путешествие начинается здесь.</color>
                """.trimIndent()) {
                    setIcon("icons/story/chapter1")
                    setType(DialogType.LIMITED)
                    setBackgroundColor(20, 20, 20, 220)
                    setBorderColor(255, 107, 107, 255)
                    setMaxSize(450, 250)
                }

                dialog("""
                    <color=#4ECDC4><b>Ваш первый квест</b></color>
                    
                    <i>Старейшина деревни подходит к вам...</i>
                    <color=#FFFFFF>Найдите древние артефакты!</color>
                """.trimIndent()) {
                    setIcon("icons/story/quest")
                    setType(DialogType.LIMITED)
                    setBackgroundColor(20, 20, 20, 220)
                    setBorderColor(78, 205, 196, 255)
                    setMaxSize(450, 250)
                }

                dialog("""
                    <color=#FFD93D><b>Таинственная встреча</b></color>
                    
                    <i>Странная фигура наблюдает издалека...</i>
                    <color=#FFFFFF>Какие секреты они хранят?</color>
                """.trimIndent()) {
                    setIcon("icons/story/mystery")
                    setType(DialogType.LIMITED)
                    setBackgroundColor(20, 20, 20, 220)
                    setBorderColor(255, 217, 61, 255)
                    setMaxSize(450, 250)
                }
            }
        }
    }
}