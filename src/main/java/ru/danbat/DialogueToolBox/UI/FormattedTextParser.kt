package ru.danbat.DialogueToolBox.UI

import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.TextColor

object FormattedTextParser {

    // Метод для парсинга форматированного текста в Component
    fun parseFormattedTextToComponent(text: String): Component {
        val root = Component.literal("")
        val tagRegex = Regex("<(\\/)?(\\w+)(?:=(#[0-9a-fA-F]{6}|\\w+))?>")

        val stack = mutableListOf<MutableComponent>()
        var currentComponent: MutableComponent = Component.literal("")
        stack.add(currentComponent)

        var index = 0
        while (index < text.length) {
            val remainingText = text.substring(index)
            val matchResult = tagRegex.find(remainingText)

            if (matchResult != null && matchResult.range.first == 0) {
                // Обрабатываем тег
                val isClosingTag = matchResult.groupValues[1] == "/"
                val tagName = matchResult.groupValues[2]
                val tagValue = matchResult.groupValues.getOrNull(3)

                index += matchResult.range.last + 1

                if (isClosingTag) {
                    // Закрывающий тег
                    if (stack.size > 1) {
                        val finishedComponent = stack.removeAt(stack.size - 1)
                        stack.last().append(finishedComponent)
                        currentComponent = stack.last()
                    }
                } else {
                    // Открывающий тег
                    val newComponent = Component.literal("")
                    when (tagName.lowercase()) {
                        "b" -> newComponent.withStyle { it.withBold(true) }
                        "i" -> newComponent.withStyle { it.withItalic(true) }
                        "color" -> {
                            tagValue?.let {
                                val color = parseColor(it)
                                if (color != null) {
                                    newComponent.withStyle { style -> style.withColor(TextColor.fromRgb(color)) }
                                }
                            }
                        }
                    }
                    stack.add(newComponent)
                    currentComponent = newComponent
                }
            } else {
                // Обрабатываем текст
                val nextTagIndex = matchResult?.range?.first?.plus(index) ?: text.length
                val textSegment = text.substring(index, nextTagIndex)
                val literalComponent = Component.literal(textSegment)
                currentComponent.append(literalComponent)
                index = nextTagIndex
            }
        }

        // оставшиеся компоненты
        while (stack.size > 1) {
            val finishedComponent = stack.removeAt(stack.size - 1)
            stack.last().append(finishedComponent)
        }

        root.append(stack.first())
        return root
    }

    private fun parseColor(colorString: String): Int? {
        return when {
            colorString.startsWith("#") -> {
                colorString.drop(1).toIntOrNull(16)?.or(0xFF000000.toInt())
            }
            else -> {
                val color = ChatFormatting.getByName(colorString.uppercase())
                color?.color ?: 0xFFFFFFFF.toInt()
            }
        }
    }
}