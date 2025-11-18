package ru.dan_bat.dialogue.ui

import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.TextColor

object FormattedTextParser {

    // Поддерживаемые теги: <b>, <i>, <color=#rrggbb> или <color=name>
    private val tagRegex = Regex("<(/)?(\\w+)(?:=(#[0-9a-fA-F]{6}|\\w+))?>")

    fun parseFormattedTextToComponent(text: String): Component {
        val root: MutableComponent = Component.literal("")
        val stack = mutableListOf<MutableComponent>()
        var currentComponent: MutableComponent = Component.literal("")
        stack.add(currentComponent)

        var index = 0
        while (index < text.length) {
            val remainingText = text.substring(index)
            val matchResult = tagRegex.find(remainingText)

            if (matchResult != null && matchResult.range.first == 0) {
                val isClosingTag = matchResult.groupValues[1] == "/"
                val tagName = matchResult.groupValues[2]
                val tagValue = matchResult.groupValues.getOrNull(3)
                index += matchResult.range.last + 1

                if (isClosingTag) {
                    if (stack.size > 1) {
                        val finishedComponent = stack.removeLast()
                        stack.last().append(finishedComponent)
                        currentComponent = stack.last()
                    }
                } else {
                    var newComponent: MutableComponent = Component.literal("")
                    when (tagName.lowercase()) {
                        "b" -> newComponent = Component.literal("").withStyle { it.withBold(true) }
                        "i" -> newComponent = Component.literal("").withStyle { it.withItalic(true) }
                        "color" -> {
                            tagValue?.let {
                                parseColor(it)?.let { color ->
                                    newComponent = Component.literal("").withStyle { style ->
                                        style.withColor(TextColor.fromRgb(color))
                                    }
                                }
                            }
                        }
                        else -> newComponent = Component.literal("")
                    }
                    stack.add(newComponent)
                    currentComponent = newComponent
                }
            } else {
                val nextTagIndexRelative = matchResult?.range?.first ?: remainingText.length
                val textSegment = remainingText.substring(0, nextTagIndexRelative)
                currentComponent.append(Component.literal(textSegment))
                index += textSegment.length
            }
        }

        while (stack.size > 1) {
            val finishedComponent = stack.removeLast()
            stack.last().append(finishedComponent)
        }

        root.append(stack.first())
        return root
    }

    private fun parseColor(colorString: String): Int? {
        return when {
            colorString.startsWith("#") -> {
                val hex = colorString.drop(1)
                val intVal = hex.toIntOrNull(16) ?: return null
                intVal or (0xFF shl 24)
            }
            else -> {
                val cf = runCatching { ChatFormatting.valueOf(colorString.uppercase()) }.getOrNull()
                cf?.color
            }
        }
    }
}
