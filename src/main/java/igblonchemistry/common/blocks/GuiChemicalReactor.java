package igblonchemistry.common.blocks;

import com.google.common.collect.Lists;
import igblonchemistry.IgblonChemistry;
import igblonchemistry.chemistry.Mixture;
import igblonchemistry.client.renderer.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class GuiChemicalReactor extends GuiContainer {

    protected static Minecraft mc = Minecraft.getMinecraft();

    protected final TileChemicalReactor chemicalReactor;

    public static final int WIDTH = 180;
    public static final int HEIGHT = 152;

    private static final ResourceLocation background = new ResourceLocation(IgblonChemistry.MODID, "textures/gui/chemical_reactor.png");

    public GuiChemicalReactor(TileChemicalReactor tileEntity, ContainerChemicalReactor container) {
        super(container);

        this.chemicalReactor = tileEntity;

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    public static void putTiledTextureQuads(BufferBuilder renderer, int x, int y, int width, int height, float depth, TextureAtlasSprite sprite, boolean upsideDown) {
        float u1 = sprite.getMinU();
        float v1 = sprite.getMinV();

        // tile vertically
        do {
            int renderHeight = Math.min(sprite.getIconHeight(), height);
            height -= renderHeight;

            float v2 = sprite.getInterpolatedV((16f * renderHeight) / (float) sprite.getIconHeight());

            // we need to draw the quads per width too
            int x2 = x;
            int width2 = width;
            // tile horizontally
            do {
                int renderWidth = Math.min(sprite.getIconWidth(), width2);
                width2 -= renderWidth;

                float u2 = sprite.getInterpolatedU((16f * renderWidth) / (float) sprite.getIconWidth());

                if(upsideDown) {
                    renderer.pos(x2, y, depth).tex(u2, v1).endVertex();
                    renderer.pos(x2, y + renderHeight, depth).tex(u2, v2).endVertex();
                    renderer.pos(x2 + renderWidth, y + renderHeight, depth).tex(u1, v2).endVertex();
                    renderer.pos(x2 + renderWidth, y, depth).tex(u1, v1).endVertex();
                } else {
                    renderer.pos(x2, y, depth).tex(u1, v1).endVertex();
                    renderer.pos(x2, y + renderHeight, depth).tex(u1, v2).endVertex();
                    renderer.pos(x2 + renderWidth, y + renderHeight, depth).tex(u2, v2).endVertex();
                    renderer.pos(x2 + renderWidth, y, depth).tex(u2, v1).endVertex();
                }

                x2 += renderWidth;
            } while(width2 > 0);

            y += renderHeight;
        } while(height > 0);
    }

    public static void renderTiledTexture(int x, int y, int width, int height, float depth, TextureAtlasSprite sprite, boolean upsideDown) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        putTiledTextureQuads(worldrenderer, x, y, width, height, depth, sprite, upsideDown);

        tessellator.draw();
    }

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

    public static void renderTiledFluid(int x, int y, int width, int height, float depth, FluidStack fluidStack, ArrayList<Mixture> contents) {
        TextureAtlasSprite fluidSprite = mc.getTextureMapBlocks().getAtlasSprite(fluidStack.getFluid().getStill(fluidStack).toString());

        int y2 = y;

        for (int i = 0; i < contents.size(); i++) {
            setColorRGB(contents.get(i).getColorAverage());

            //1 Pixel = 20 liters, this will vary reactor by reactor
            int h = (int) Math.ceil(contents.get(i).getTotalVolume() / 20);

            renderTiledTexture(x, y2 - h, width, h, depth, fluidSprite, fluidStack.getFluid().isGaseous(fluidStack));
            y2 -= h;
        }
    }

    public static void drawGuiTank(int x, int y, int w, int height, float zLevel, ArrayList<Mixture> contents) {
        FluidStack liquid = FluidRegistry.getFluidStack("water", 100);
        renderTiledFluid(x, y, w, 5, zLevel, liquid, contents);
        setColorRGB(0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        drawGuiTank(206,  109, 63, 256, this.zLevel, this.chemicalReactor.getContents());
    }

    public void getReactorTooltip() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);

        ArrayList<String> text = Lists.newArrayList();
        text.add(TextFormatting.GOLD + "f");
        this.drawHoveringText(text, mouseX, mouseY);
    }

    /*
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        mouseX -= guiLeft;
        mouseY -= guiTop;


    }

     */
}
