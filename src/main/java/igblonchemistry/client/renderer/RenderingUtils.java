package igblonchemistry.client.renderer;

import net.minecraft.client.renderer.GlStateManager;

public class RenderingUtils {
    public static int red(int c) {
        return (c >> 16) & 0xFF;
    }

    public static int green(int c) {
        return (c >> 8) & 0xFF;
    }

    public static int blue(int c) {
        return (c) & 0xFF;
    }

    public static void setColorRGB(int color, int transparency) {
        float r = red(color) / 255.0F;
        float g = green(color) / 255.0F;
        float b = blue(color) / 255.0F;

        GlStateManager.color(r, g, b, (float) transparency);
    }

    public static void setColorRGB(int color) {
        float r = red(color) / 255.0F;
        float g = green(color) / 255.0F;
        float b = blue(color) / 255.0F;

        GlStateManager.color(r, g, b, 255.0F);
    }
}