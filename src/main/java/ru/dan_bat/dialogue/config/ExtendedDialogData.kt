package ru.dan_bat.dialogue.config

import net.minecraft.resources.ResourceLocation

// Расширенный класс диалоговых данных, можно использовать в api
data class ExtendedDialogData(
    val text: String,
    val iconLocation: ResourceLocation?,
    val config: DialogConfig = DialogConfig()
)