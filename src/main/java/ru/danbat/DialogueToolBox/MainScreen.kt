package ru.danbat.DialogueToolBox

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.danbat.DialogueToolBox.commands.Command
import ru.danbat.DialogueToolBox.commands.TestCommandsForJava
import ru.danbat.DialogueToolBox.commands.TestCommandsForKotlin


@Mod(MainScreen.MODID)
class MainScreen {
    val forgeBus = EVENT_BUS

    init {
        LOGGER.info("Loading DialogueToolBox")
        ModNetworking.registerPackets()
        LOGGER.info("ModNetworking registered")
        forgeBus.register(ClientEvents)
        LOGGER.info("ClientEvents registered")
        forgeBus.register(this)
    }

    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        // Регистрация команд
        /*
        потом сделаю дебаг :|
        LOGGER.info("Регистрация команды")
        Command.registerCommands(event.dispatcher)
        TestCommandsForJava.registerCommands(event.dispatcher)
        TestCommandsForKotlin.registerCommands(event.dispatcher)
        */

    }

    companion object {
        const val MODID = "dialogue"
        const val MODNAME = "DialogueToolBox"
        @JvmField val LOGGER: Logger = LoggerFactory.getLogger(MODNAME)
    }

}
