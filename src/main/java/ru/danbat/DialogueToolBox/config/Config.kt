package ru.danbat.DialogueToolBox.config

/**
 * Класс конфигурации диалогового окна
 *
 * @property type тип диалогового окна
 * @property backgroundColor цвет фона (rgba)
 * @property borderColor цвет рамки (rgba)
 * @property textColor цвет текста (rgba)
 * @property x координата X для статических окон
 * @property y координата Y для статических окон
 * @property width ширина окна для статических и адаптивных окон
 * @property height высота окна для статических и адаптивных окон
 * @property minWidth минимальная ширина окна
 * @property minHeight минимальная высота окна
 * @property maxWidth максимальная ширина окна
 * @property maxHeight максимальная высота окна
 * @property paddingLeft отступ от левого края
 * @property paddingRight отступ от правого края
 * @property paddingUp отступ от верхнего края
 * @property paddingDown отступ от нижнего края
 * @property iconSize размер иконки
 * @property maxLines максимальное количество строк(не активно)
 * @property iconPaddingLeft отступ от левого края иконки
 * @property iconPaddingRight отступ от правого края иконки
 * @property iconPaddingUp отступ от верхнего края иконки
 * @property iconPaddingDown отступ от нижнего края иконки
 * @property iconTextSpacing отступ между иконкой и текстом
 */

data class DialogConfig(
    val type: DialogType = DialogType.DYNAMIC,
    val backgroundColor: List<Int> = listOf(0, 0, 0, 150),
    val borderColor: List<Int> = listOf(255, 255, 255, 255),
    val textColor: Int = 0xFFFFFF,
    val x: Int? = null,
    val y: Int? = null,
    val width: Int? = null,
    val height: Int? = null,
    val minWidth: Int = 100,
    val minHeight: Int = 50,
    val maxWidth: Int? = null,
    val maxHeight: Int? = null,
    val paddingLeft: Int = 10,
    val paddingRight: Int = 10,
    val paddingUp: Int = 10,
    val paddingDown: Int = 10,
    val iconSize: Int = 32,
    val maxLines: Int? = null,
    val iconPaddingLeft: Int = 5,
    val iconPaddingRight: Int = 5,
    val iconPaddingUp: Int = 5,
    val iconPaddingDown: Int = 5,
    val iconTextSpacing: Int = 5
)
