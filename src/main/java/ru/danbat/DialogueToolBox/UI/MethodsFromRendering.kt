package ru.danbat.DialogueToolBox.UI

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
import ru.danbat.DialogueToolBox.config.DialogConfig

class MethodsFromRendering {

    fun renderDialogBackground(
        poseStack: PoseStack,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        config: DialogConfig
    ) {
        fill(poseStack, x, y, x + width, y + height, config.backgroundColor)
        renderOutline(poseStack, x, y, width, height, config.borderColor)
    }

    fun renderOutline(
        poseStack: PoseStack,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        color: List<Int>
    ) {
        fill(poseStack, x, y, x + width, y + 1, color)
        fill(poseStack, x, y + height - 1, x + width, y + height, color)
        fill(poseStack, x, y, x + 1, y + height, color)
        fill(poseStack, x + width - 1, y, x + width, y + height, color)
    }

    fun renderIcon(
        poseStack: PoseStack,
        icon: ResourceLocation,
        x: Int,
        y: Int,
        dialogHeight: Int,
        config: DialogConfig
    ) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, icon)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val iconX = x + config.paddingLeft + config.iconPaddingLeft
        val iconY = y + config.paddingUp + config.iconPaddingUp

        blit(
            poseStack,
            iconX,
            iconY,
            0,
            0,
            config.iconSize,
            config.iconSize,
            config.iconSize,
            config.iconSize,
            icon
        )
    }


     private fun fill(poseStack: PoseStack, x1: Int, y1: Int, x2: Int, y2: Int, color: List<Int>) {
        val tessellator = com.mojang.blaze3d.vertex.Tesselator.getInstance()
        val bufferBuilder = tessellator.builder

        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableTexture()

        bufferBuilder.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,
            com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR)

        bufferBuilder.vertex(poseStack.last().pose(), x1.toFloat(), y2.toFloat(), 0f).color(color[0], color[1], color[2], color[3]).endVertex()
        bufferBuilder.vertex(poseStack.last().pose(), x2.toFloat(), y2.toFloat(), 0f).color(color[0], color[1], color[2], color[3]).endVertex()
        bufferBuilder.vertex(poseStack.last().pose(), x2.toFloat(), y1.toFloat(), 0f).color(color[0], color[1], color[2], color[3]).endVertex()
        bufferBuilder.vertex(poseStack.last().pose(), x1.toFloat(), y1.toFloat(), 0f).color(color[0], color[1], color[2], color[3]).endVertex()

        tessellator.end()
        RenderSystem.enableTexture()
    }

    private fun blit(poseStack: PoseStack, x: Int, y: Int, u: Int, v: Int, width: Int, height: Int, textureWidth: Int, textureHeight: Int, characterIcon: ResourceLocation?) {
        // рендерера для иконки
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        characterIcon?.let { RenderSystem.setShaderTexture(0, it) }
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val matrix4f = poseStack.last().pose()
        val bufferBuilder = com.mojang.blaze3d.vertex.Tesselator.getInstance().builder

        bufferBuilder.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX)

        bufferBuilder.vertex(matrix4f, x.toFloat(), (y + height).toFloat(), 0f)
            .uv((u.toFloat() / textureWidth), ((v + height).toFloat() / textureHeight)).endVertex()
        bufferBuilder.vertex(matrix4f, (x + width).toFloat(), (y + height).toFloat(), 0f)
            .uv(((u + width).toFloat() / textureWidth), ((v + height).toFloat() / textureHeight)).endVertex()
        bufferBuilder.vertex(matrix4f, (x + width).toFloat(), y.toFloat(), 0f)
            .uv(((u + width).toFloat() / textureWidth), (v.toFloat() / textureHeight)).endVertex()
        bufferBuilder.vertex(matrix4f, x.toFloat(), y.toFloat(), 0f)
            .uv((u.toFloat() / textureWidth), (v.toFloat() / textureHeight)).endVertex()

        com.mojang.blaze3d.vertex.Tesselator.getInstance().end()
    }
}