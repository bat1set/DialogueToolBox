package ru.dan_bat.dialogue

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.hollowhorizon.hc.api.Init
import ru.hollowhorizon.hc.common.events.SubscribeEvent
import ru.hollowhorizon.hc.common.events.registry.RegisterCommandsEvent

@Init
object MainScreen {
    const val MODID = "dialogue"
    const val MODNAME = "DialogueToolBox"
    @JvmField val LOGGER: Logger = LoggerFactory.getLogger(MODNAME)

    init {
        LOGGER.info("Loading DialogueToolBox with HollowCore")
        ClientEvents
        LOGGER.info("DialogueToolBox loaded")
    }

    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        /*
        LOGGER.info("Регистрация команд")
        Command.onRegisterCommands(event.dispatcher)
        TestCommandsForJava.registerCommands(event.dispatcher)
        TestCommandsForKotlin.onRegisterCommands(event.dispatcher)
         */
    }


}