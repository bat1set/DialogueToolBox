package ru.danbat.DialogueToolBox.commands

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer
import net.minecraft.commands.arguments.EntityArgument
import ru.danbat.DialogueToolBox.api.DialogAPI

import ru.danbat.DialogueToolBox.config.DialogConfig
import ru.danbat.DialogueToolBox.config.DialogType
import ru.danbat.DialogueToolBox.config.ExtendedDialogData
// будет наверное меняться... но всё оставлю как пример ^_^
object Command {
    fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("dialog")
                .requires { source -> source.hasPermission(2) }
                .then(
                    Commands.literal("welcome")
                        .executes { context ->
                            val player = context.source.player
                                ?: return@executes 0
                            sendHello(player)
                            1
                        }
                        .then(Commands.argument("player", EntityArgument.player())
                            .executes { context ->
                                val player = EntityArgument.getPlayer(context, "player")
                                sendHello(player)
                                1
                            }
                        )
                )
                .then(
                    Commands.literal("NPCHello")
                        .executes { context ->
                            val player = context.source.player
                                ?: return@executes 0
                            NPCHello(player)
                            1
                        }
                        .then(Commands.argument("player", EntityArgument.player())
                            .executes { context ->
                                val player = EntityArgument.getPlayer(context, "player")
                                NPCHello(player)
                                1
                            }
                        )
                )
                .then(Commands.literal("Quastion1")
                    .executes { context ->
                        val player = context.source.player
                            ?: return@executes 0
                        quastion1(player)
                        1
                    }
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { context ->
                            val player = EntityArgument.getPlayer(context, "player")
                            quastion1(player)
                            1
                        }
                    )
                )
                .then(Commands.literal("Quastion2")
                    .executes { context ->
                        val player = context.source.player
                            ?: return@executes 0
                        quastion2(player)
                        1
                    }
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { context ->
                            val player = EntityArgument.getPlayer(context, "player")
                            quastion2(player)
                            1
                        }
                    )
                )
                .then(Commands.literal("Quastion3")
                    .executes { context ->
                        val player = context.source.player
                            ?: return@executes 0
                        quastion3(player)
                        1
                    }
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { context ->
                            val player = EntityArgument.getPlayer(context, "player")
                            quastion3(player)
                            1
                        }
                    )
                )
                .then(Commands.literal("Quastion4")

                    .executes { context ->
                        val player = context.source.player
                            ?: return@executes 0
                        quastion4(player)
                        1
                    }
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { context ->
                            val player = EntityArgument.getPlayer(context, "player")
                            quastion4(player)
                            1
                        }
                    )
                )
                .then(Commands.literal("Quastion5")
                    .executes { context ->
                        val player = context.source.player
                            ?: return@executes 0
                        quastion5(player)
                        1
                    }
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { context ->
                            val player = EntityArgument.getPlayer(context, "player")
                            quastion5(player)
                            1
                        }
                    )
                )
                .then(Commands.literal("Api")
                    .executes { context ->
                        val player = context.source.player
                            ?: return@executes 0
                        whatWithAPI(player)
                        1
                    }
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { context ->
                            val player = EntityArgument.getPlayer(context, "player")
                            whatWithAPI(player)
                            1
                        }
                    )
                )
                .then(Commands.literal("bye")
                    .executes { context ->
                        val player = context.source.player
                            ?: return@executes 0
                        goodBye(player)
                        1
                    }
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { context ->
                            val player = EntityArgument.getPlayer(context, "player")
                            goodBye(player)
                            1
                        }
                    )

                )
        )
    }
    val config = DialogConfig(
        type = DialogType.DYNAMIC,
        backgroundColor = listOf(0, 0, 0, 15),
    )
    private fun sendHello(player: ServerPlayer) {
        DialogAPI.showDialogs(
            player,
            listOf(
                ExtendedDialogData(
                    """
                   <b><color=#ff9933>Привет всем!!!</color></b>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   И давно не виделись... прошло больше <b>месяца</b>, как никак.
                   <color=#333d33>боже... </color>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   Наверное вам интересно, что же я всё это время делал, ну раз вы зашли на это видео...
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   <color=#12ff39><b>Так вот, я делал API к моду!!!!!</b></color>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   <b>...</b>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   Знаете, когда я делал API, я думал, что создаю что-то грандиозное, а на деле — эххх...
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   Не в смысле, что <i>API плохое</i>, а в смысле, что для рядового пользователя это мало что значит...
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   Я бы и хотел сказать, что всё, что я сделал, было адом, и после этого API я больше никогда его делать не буду, но...
                   <color=red><b>это наглая ложь!</b></color>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   Чуть позже я затрону тему API, так что не волнуйтесь, если вы пришли сюда ради него.
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   А сейчас мы поговорим о насущном — <color=#87ceeb><b>"Что, где и когда?"</b></color>.
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   А если точнее, то: <i>"<color=#ff4500>Где скачать мод?</color> <color=#4682b4>Когда он выйдет?</color> <color=#32cd32>Зачем я это делаю?</color> <color=#daa520>Что я хочу от своего мода?</color> <color=#8a2be2>И для кого он?</color>"</i>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   Эти вопросы были самыми частыми, так что я посчитал их важными для публикации.
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   Далее я отвечу на каждый из этих вопросов и покажу примеры с кодом.  
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   Я укажу таймкоды для тех, кого интересуют конкретные вопросы.
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                   А теперь скажу: <color=#32cd32><b>удачного вам просмотра!</b></color>
                    """.trimIndent(),
                    null,
                    config
                ),
            )
        )
    }


    private fun NPCHello(player: ServerPlayer) {
        DialogAPI.showDialogs(
            player,
            listOf(
                ExtendedDialogData(
                    """
                       Привет, я представление разработчика мода
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Какой вопрос тебя интересует первым?
                    """.trimIndent(),
                    null,
                    config
                ),
            )
        )
    }

    private fun quastion1(player: ServerPlayer) {
        DialogAPI.showDialogs(
            player,
            listOf(
                ExtendedDialogData(
                    """
                       <b>"Для кого этот мод?"</b>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Вообще не раз об этом думал и всё время прихожу к единому ответу...
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Я делаю мод не то чтобы для себя, скорее для людей, которые хотят больше <color=#ff9933>интерактива</color> в своих модах или сюжетных сборках.
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Так или иначе, я думаю, что он скорее для <color=#32cd32><b>разработчиков других модов</b></color>, чем для <color=#c9cc31><b>среднестатистических игроков</b></color>.
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Хах, иронично, что я буду делать ветку модов с приставкой <color=#daa520>"ToolBox"</color> в названии.
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Наверное, это всё, что я хочу сказать в ответе на этот вопрос.
                    """.trimIndent(),
                    null,
                    config
                ),
            )
        )

    }


    private fun quastion2(player: ServerPlayer) {
        DialogAPI.showDialogs(
            player,
            listOf(
                ExtendedDialogData(
                    """
                       <b>"Зачем я это делаю?"</b>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Вообще, вопрос не прям популярный, но в начале создания мода слышал <color=#87ceeb>очень часто</color>.
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Я делаю мод лично для своей <color=#ff9933>практики</color> и <color=#32cd32><b>облегчения создания интерактива</b></color> для людей.
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Если вы думаете, что это не особо причина, я вам скажу, что большая часть моих работ основывается на этих <color=#ffa500>правилах</color> :>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Более явного ответа нет.
                    """.trimIndent(),
                    null,
                    config
                ),
            )
        )
    }

    private fun quastion3(player: ServerPlayer) {
        DialogAPI.showDialogs(
            player,
            listOf(
                ExtendedDialogData(
                    """
                        <b>"Что я жду от мода?"</b>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Хмммм, вопрос не из простых...
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Вообще, что можно ждать от своего мода?
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        То, что он станет популярным? Или что он кому-то будет полезным?
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Всегда не понимал, что отвечать на такой вопрос...
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Скажу лишь то, что мне кажется правильным!
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Я ничего не жду
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        А вы что думали?
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Я не банальный человек :) 
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        А если быть серьёзным... я вам точно ничего сказать не могу, я не люблю гадать
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Не хочу быть разочарован своими мечтами...
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Так что <color=#32cd32>следующий вопрос</color>
                    """.trimIndent(),
                    null,
                    config
                ),
            )
        )
    }

    private fun quastion4(player: ServerPlayer) {
        DialogAPI.showDialogs(
            player,
            listOf(
                ExtendedDialogData(
                    """
                        <b>"Когда мод выйдет?"</b>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Вообще сказать точно не могу, мод мне кажется сырым...
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Да, я сделал <color=#32cd32>API</color>, да, я сделал его более-менее стабильным.
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Но всё равно люди как будто ждут от моего мода довольно много...
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Но так или иначе, после этого видео, я выпущу мод в течение ±недели и выпущу на эту тему пост в сообществе
                        <color=#333d33>(если разберусь...)</color>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                        Наверное, такой ответ будет <color=#87ceeb>более-менее хорошим</color>
                    """.trimIndent(),
                    null,
                    config
                ),
            )
        )
    }

    private fun quastion5(player: ServerPlayer) {
        DialogAPI.showDialogs(
            player,
            listOf(
                ExtendedDialogData(
                    """
                       <b>"Где скачать мод?"</b>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Вопрос хороший и прежде временный
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Скорее всего я поступлю не как все и выложу мод на <color=#1e90ff>GitHub</color>, чтобы каждый мог помогать в разработке и чтобы люди могли сами изучить то, над чем я работал почти 2 месяца
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Наверное, я так же выложу мод на <color=#ff6347>curseforge</color> и <color=#32cd32>modrinth</color> в виде зависимости для других модов
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Не зря же мой мод это <color=#daa520>"ToolBox"</color> :>
                    """.trimIndent(),
                    null,
                    config
                ),
            )
        )
    }

    private fun whatWithAPI(player: ServerPlayer) {
        DialogAPI.showDialogs(
            player,
            listOf(
                ExtendedDialogData(
                    """
                       <b>"что с API?"</b>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       А точно, я же обещал вам его показать, хмммм
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Всё что вы увидите дальше не является нарушением правил мультивселенных и является лишь предоставление информации на заданный вопрос
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       И так слева вы видите код написанный мною на <color=#32cd32>kotlin</color>, этот то самый диалог, который вы сейчас видите
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Из интересного есть специальный <color=#ff6347>dataclass</color>, который содержит информацию о диалоге из-за этого код будет более структурированным
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Так-же он поддерживает одиночные диалоги, что вроде бы должно меньше нагружать систему, хотя это не точно :>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       С права вы видите пример на <color=#ff6347>java</color>, который сделан с тем же api и поддерживает те-же функции, но немного в другой обертке
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Так же я в коде оставлю пример как на <color=#32cd32>kotlin</color>, так и на <color=#ff6347>java</color>, и если вы забудете как что-либо делать, просто вспомните про них :>
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Подчеркну, примеры что буду в исходном коде будут отличаться от того что использую я, просто потому что устал и покажу я все возможные методы уже в документации, как только доделаю сайт
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Еще вопросы?
                    """.trimIndent(),
                    null,
                    config
                ),
            )

        )
    }

    private fun goodBye(player: ServerPlayer) {
        DialogAPI.showDialogs(
            player,
            listOf(
                ExtendedDialogData(
                    """
                       Где ты?
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Аааа как ты тут оказался и кто ты?
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Знаешь тебе этого знать не нужно...
                    """.trimIndent(),
                    null,
                    config
                ),
                ExtendedDialogData(
                    """
                       Кстати обернись
                    """.trimIndent(),
                    null,
                    config
                ),
            )
        )
    }
}