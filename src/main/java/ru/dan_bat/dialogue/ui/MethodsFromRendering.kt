package ru.dan_bat.dialogue.ui

//? if fabric {
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
//?} else {

/*import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
*///?}

import ru.dan_bat.dialogue.config.DialogConfig

class MethodsFromRendering {

    //? if fabric {
    fun renderDialogBackground(
        context: GuiGraphics,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        config: DialogConfig
    ) {
        val bgColor = colorToArgb(config.backgroundColor)
        val borderColor = colorToArgb(config.borderColor)

        // Фон
        context.fill(x, y, x + width, y + height, bgColor)

        // Рамка (1px)
        context.fill(x, y, x + width, y + 1, borderColor) // Верх
        context.fill(x, y + height - 1, x + width, y + height, borderColor) // Низ
        context.fill(x, y, x + 1, y + height, borderColor) // Лево
        context.fill(x + width - 1, y, x + width, y + height, borderColor) // Право
    }

    fun renderIcon(
        context: GuiGraphics,
        icon: ResourceLocation,
        x: Int,
        y: Int,
        dialogHeight: Int,
        config: DialogConfig
    ) {
        val iconX = x + config.paddingLeft + config.iconPaddingLeft
        val iconY = y + config.paddingUp + config.iconPaddingUp

        // drawTexture принимает: texture, x, y, u, v, width, height, textureWidth, textureHeight
        context.blit(
            icon,
            iconX,
            iconY,
            0f,
            0f,
            config.iconSize,
            config.iconSize,
            config.iconSize,
            config.iconSize
        )
    }

    private fun colorToArgb(color: List<Int>): Int {
        require(color.size == 4) { "Цвет должен содержать 4 компонента (RGBA)" }
        val r = color[0] and 0xFF
        val g = color[1] and 0xFF
        val b = color[2] and 0xFF
        val a = color[3] and 0xFF
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
    //?} else {
    

    /*fun renderDialogBackground(
        poseStack: PoseStack,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        config: DialogConfig
    ) {
        fillRect(poseStack, x, y, x + width, y + height, config.backgroundColor)
        renderOutline(poseStack, x, y, width, height, config.borderColor)
    }

    private fun renderOutline(
        poseStack: PoseStack,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        color: List<Int>
    ) {
        // Верхняя граница
        fillRect(poseStack, x, y, x + width, y + 1, color)
        // Нижняя граница
        fillRect(poseStack, x, y + height - 1, x + width, y + height, color)
        // Левая граница
        fillRect(poseStack, x, y, x + 1, y + height, color)
        // Правая граница
        fillRect(poseStack, x + width - 1, y, x + width, y + height, color)
    }

    fun renderIcon(
        poseStack: PoseStack,
        icon: ResourceLocation,
        x: Int,
        y: Int,
        config: DialogConfig
    ) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, icon)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val iconX = x + config.paddingLeft + config.iconPaddingLeft
        val iconY = y + config.paddingUp + config.iconPaddingUp

        blitTexture(
            poseStack,
            iconX,
            iconY,
            0,
            0,
            config.iconSize,
            config.iconSize,
            config.iconSize,
            config.iconSize
        )

        RenderSystem.disableBlend()
    }

    private fun fillRect(
        poseStack: PoseStack,
        x1: Int,
        y1: Int,
        x2: Int,
        y2: Int,
        color: List<Int>
    ) {
        require(color.size == 4) { "Цвет должен содержать 4 компонента (RGBA)" }

        val tessellator = Tesselator.getInstance()
        val bufferBuilder = tessellator.builder

        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)

        val pose = poseStack.last().pose()
        val r = (color[0] and 0xFF) / 255f
        val g = (color[1] and 0xFF) / 255f
        val b = (color[2] and 0xFF) / 255f
        val a = (color[3] and 0xFF) / 255f

        bufferBuilder.vertex(pose, x1.toFloat(), y2.toFloat(), 0f)
            .color(r, g, b, a)
            .endVertex()
        bufferBuilder.vertex(pose, x2.toFloat(), y2.toFloat(), 0f)
            .color(r, g, b, a)
            .endVertex()
        bufferBuilder.vertex(pose, x2.toFloat(), y1.toFloat(), 0f)
            .color(r, g, b, a)
            .endVertex()
        bufferBuilder.vertex(pose, x1.toFloat(), y1.toFloat(), 0f)
            .color(r, g, b, a)
            .endVertex()

        tessellator.end()
        RenderSystem.disableBlend()
    }

    private fun blitTexture(
        poseStack: PoseStack,
        x: Int,
        y: Int,
        u: Int,
        v: Int,
        width: Int,
        height: Int,
        textureWidth: Int,
        textureHeight: Int
    ) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val matrix = poseStack.last().pose()
        val tessellator = Tesselator.getInstance()
        val bufferBuilder = tessellator.builder

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)

        val u0 = u.toFloat() / textureWidth
        val u1 = (u + width).toFloat() / textureWidth
        val v0 = v.toFloat() / textureHeight
        val v1 = (v + height).toFloat() / textureHeight

        bufferBuilder.vertex(matrix, x.toFloat(), (y + height).toFloat(), 0f)
            .uv(u0, v1)
            .endVertex()
        bufferBuilder.vertex(matrix, (x + width).toFloat(), (y + height).toFloat(), 0f)
            .uv(u1, v1)
            .endVertex()
        bufferBuilder.vertex(matrix, (x + width).toFloat(), y.toFloat(), 0f)
            .uv(u1, v0)
            .endVertex()
        bufferBuilder.vertex(matrix, x.toFloat(), y.toFloat(), 0f)
            .uv(u0, v0)
            .endVertex()

        tessellator.end()
        RenderSystem.disableBlend()
    }
    *///?}

}
