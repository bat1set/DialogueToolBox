package ru.danbat.DialogueToolBox.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.danbat.DialogueToolBox.api.DialogAPI;
import ru.danbat.DialogueToolBox.config.DialogConfig;
import ru.danbat.DialogueToolBox.config.DialogType;
import ru.danbat.DialogueToolBox.config.ExtendedDialogData;

import java.util.ArrayList;
import java.util.List;

public class TestCommandsForJava {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("jdialog")
                .requires(source -> source.hasPermission(2))

                .then(Commands.literal("welcome")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            showWelcomeDialog(player);
                            return 1;
                        }))
                .then(Commands.literal("quest")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            showQuestDialog(player);
                            return 1;
                        }))
                .then(Commands.literal("tutorial")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            showTutorialSequence(player);
                            return 1;
                        })));
    }

    private static void showWelcomeDialog(ServerPlayer player) {
        DialogAPI.showDialog(
                player,
                "<color=#FFD700>Добро пожаловать на Сервер!</color>\n" +
                        "<b>Rules:</b>\n" +
                        "1. <color=#FF6B6B>Будьте уважительным к другим игрокам</color>\n" +
                        "2. <color=#4ECDC4>Не гриферить</color>\n" +
                        "3. <color=#95A5A6>Веселиться!</color>",
                "icons/welcome",
                new DialogAPI.DialogBuilder()
                        .setType(DialogType.DYNAMIC)
                        .setBackgroundColor(20, 20, 20, 20)
                        .setBorderColor(255, 215, 0, 255)
                        .setTextColor(0xFFFFFF)
                        .setPadding(15, 15, 15, 15)
                        .build()
        );
    }


    private static void showQuestDialog(ServerPlayer player) {
        DialogAPI.showDialog(
                player,
                "<color=#FFA500><b>Доступен новый квест!</b></color>\n\n" +
                        "<i>Древний Дракон проснулся...</i>\n" +
                        "<color=#ADD8E6>Награда: 1000 золотых монет</color>",
                "icons/quest",
                new DialogAPI.DialogBuilder()
                        .setType(DialogType.DYNAMIC)
                        .setBackgroundColor(40, 40, 40, 180)
                        .setBorderColor(255, 165, 0, 255)
                        .setIconSize(48)
                        .setIconTextSpacing(10)
                        .setMaxLines(5)
                        .build()
        );
    }

    private static void showTutorialSequence(ServerPlayer player) {
        List<ExtendedDialogData> dialogs = new ArrayList<>();

        // Шаг 1
        DialogConfig step1Config = new DialogAPI.DialogBuilder()
                .setType(DialogType.LIMITED)
                .setBackgroundColor(30, 30, 30, 200)
                .setBorderColor(0, 255, 0, 255)
                .build();

        dialogs.add(new ExtendedDialogData(
                "<color=#00FF00><b>Учебное пособие по движениям</b></color>\n" +
                        "используй <color=#FFFFFF>WASD</color> кнопки передвижения\n" +
                        "нажми <color=#FFFFFF>Space</color> прыгать",
                new ResourceLocation("id", "icons/movement"),
                step1Config
        ));

        // Шаг 2
        DialogConfig step2Config = new DialogAPI.DialogBuilder()
                .setType(DialogType.LIMITED)
                .setBackgroundColor(30, 30, 30, 200)
                .setBorderColor(0, 0, 255, 255)
                .build();

        dialogs.add(new ExtendedDialogData(
                "<color=#4169E1><b>Управление инвентарем</b></color>\n" +
                        "нажми <color=#FFFFFF>E</color> открыть инвентарь\n" +
                        "<i>Попробуйте организовать свои вещи по инвенторю!</i>",
                new ResourceLocation("id", "icons/inventory"),
                step2Config
        ));

        DialogAPI.showDialogs(player, dialogs);
    }
}