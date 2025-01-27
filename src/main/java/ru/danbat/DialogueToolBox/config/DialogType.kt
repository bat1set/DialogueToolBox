package ru.danbat.DialogueToolBox.config

// Enum для типов диалоговых окон
enum class DialogType {
    DYNAMIC,       // Текущая динамическая реализация
    STATIC,        // Статическое окно с фиксированной позицией
    TEXT_ADAPTIVE, // Размер окна адаптируется под длину текста
    LIMITED        // окно вида-адаптивного с ограничениями
}