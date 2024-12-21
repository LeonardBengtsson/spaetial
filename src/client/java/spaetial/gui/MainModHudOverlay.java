package spaetial.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import spaetial.Spaetial;
import spaetial.editing.ClientManager;
import spaetial.input.ModInput;
import spaetial.mixin.client.DrawContextAccessor;

/**
 * Displays various bits of information to the player, such as what actions they can currently perform, what state the mod is in, what operations are queued, etc.
 */
public class MainModHudOverlay implements HudRenderCallback {
    private static final Identifier TEST_TEXTURE = Spaetial.id("textures/test.png");
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        var client = ((DrawContextAccessor) drawContext).getClient();
        if (client == null) return;
        if (client.options.hudHidden) return;

        int windowWidth = client.getWindow().getScaledWidth();
        int windowHeight = client.getWindow().getScaledHeight();

        // (0, 0) is upper left corner
        int x = 0;
        int y = 0;
        int z = 0;
        float u = .1f;
        float v = .1f;
        int width = 100;
        int height = 100;
        int textureWidth = 100;
        int textureHeight = 100;

//        drawContext.drawTexture(TEST_TEXTURE, x, y, z, u, v, width, height, textureWidth, textureHeight);
        var state = ClientManager.getEditingState();
        if (state.getInteractionType().renderHud) {
            drawContext.drawTextWrapped(client.textRenderer, Text.literal("Editing state: " + ClientManager.getEditingState().getDebugText()), 10, 10, windowWidth, 0xffffff);
        }
        {
            // TODO DEBUG
//            var mods = "shift [" + (ModInput.shift() ? "x" : " ") + "] ctrl [" + (ModInput.ctrl() ? "x" : " ") + "] alt [" + (ModInput.alt() ? "x" : " ") + "] f3 [" + (ModInput.f3() ? "x" : " ") + "]";
//            var mouse = "lmb [" + (ModInput.lmb() ? "x" : " ") + "] mmb [" + (ModInput.mmb() ? "x" : " ") + "] rmb [" + (ModInput.rmb() ? "x" : " ") + "]";
//
//            drawContext.drawText(client.textRenderer, mods, 10, 25, 0xffffff, false);
//            drawContext.drawText(client.textRenderer, mouse, 10, 40, 0xffffff, false);
        }
    }
}