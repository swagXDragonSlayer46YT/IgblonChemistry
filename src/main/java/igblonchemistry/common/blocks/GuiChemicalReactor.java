package igblonchemistry.common.blocks;

import com.google.common.collect.Lists;
import igblonchemistry.IgblonChemistry;
import igblonchemistry.chemistry.Chemical;
import igblonchemistry.chemistry.GaseousMixture;
import igblonchemistry.chemistry.Mixture;
import igblonchemistry.client.renderer.RenderingUtils;
import igblonchemistry.util.IgblonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
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
import java.util.HashMap;
import java.util.Map;

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

    public static void renderTiledFluid(int x, int y, int width, int height, float depth, FluidStack fluidStack, ArrayList<Mixture> contents) {
        TextureAtlasSprite fluidSprite = mc.getTextureMapBlocks().getAtlasSprite(fluidStack.getFluid().getStill(fluidStack).toString());

        int y2 = y;

        for (int i = 0; i < contents.size(); i++) {
            RenderingUtils.setColorRGB(contents.get(i).getColorAverage());

            //1 Pixel = 20 liters, this will vary reactor by reactor
            int h = (int) Math.ceil(contents.get(i).getTotalVolume() / 20);

            renderTiledTexture(x, y2 - h, width, h, depth, fluidSprite, fluidStack.getFluid().isGaseous(fluidStack));
            y2 -= h;
        }
    }

    public static void drawGuiTank(int x, int y, int w, int height, float zLevel, ArrayList<Mixture> contents) {
        FluidStack liquid = FluidRegistry.getFluidStack("water", 100);
        renderTiledFluid(x, y, w, 5, zLevel, liquid, contents);
        RenderingUtils.setColorRGB(0xffffff);
    }


    private int[] calcMixtureHeights(ArrayList<Mixture> contents) {
        int[] heights = new int[contents.size()];
        int y = getGuiTop() + 58;

        for (int i = 0; i < heights.length; i++) {
            int h = (int) Math.ceil(contents.get(i).getTotalVolume() / 20);
            y -= h;
            heights[i] = y;
        }

        return heights;
    }

    private Mixture getMixtureHovered(TileChemicalReactor chemicalReactor, int y) {
        int[] heights = calcMixtureHeights(chemicalReactor.getContents());

        for(int i = 0; i < heights.length; i++) {
            if(y >= heights[i]) {
                return chemicalReactor.getContents().get(i);
            }
        }

        return null;
    }

    public void getReactorTooltip(int guiLeft, int guiTop, int mouseX, int mouseY) {
        if (mouseX < guiLeft + 56 || mouseX > guiLeft + 116 || mouseY > guiTop + 58 || mouseY < guiTop + 8) {
            return;
        }

        Mixture mixture = getMixtureHovered(this.chemicalReactor, mouseY);
        GaseousMixture gaseousMixture = this.chemicalReactor.getContainedGas();

        ArrayList<String> text = Lists.newArrayList();

        String header = "";

        if (mixture != null) {
            if (mixture.getComponents().size() == 1) {
                if (mixture.getTemperature() > mixture.getComponents().entrySet().iterator().next().getKey().getMeltingPoint()) {
                    header = "Liquid";
                } else {
                    header = "Solid";
                }
            } else {
                if (mixture.getIsAqueous()) {
                    header = "Aqueous Mixture";
                } else {
                    header = "Mixture";
                }
            }

            text.add(TextFormatting.GOLD + "" + TextFormatting.UNDERLINE + header);
            text.add(TextFormatting.RESET + "Total Volume: " + TextFormatting.AQUA + IgblonUtils.roundToDigit(mixture.getTotalVolume(), 2) + " Liters");

            if (mixture.getHasPH()) {
                text.add(TextFormatting.WHITE + "pH: " + TextFormatting.GREEN + "" + IgblonUtils.roundToDigit(mixture.getPH(), 2));
            }

            text.add(TextFormatting.WHITE + "Temperature: " + TextFormatting.RED + "" + IgblonUtils.roundToDigit(mixture.getTemperature(), 1) + " Kelvin");
            text.add("");
            text.add(TextFormatting.GOLD + "" + TextFormatting.UNDERLINE + "Components");

            HashMap<Chemical, Double> components = mixture.getComponents();
            double[] individualVolumes = mixture.getIndividualVolumes();
            int i = 0;

            for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
                text.add(TextFormatting.RESET + entry.getKey().getName() + ": " + TextFormatting.GRAY + IgblonUtils.roundToDigit(entry.getValue(), 2) + " mol " + TextFormatting.DARK_GRAY + "(" + IgblonUtils.roundToDigit(individualVolumes[i], 2) + " Liters)");
                i++;
            }
        } else {
            if (gaseousMixture.getComponents().size() == 0){
                header = "Vacuum";
            } else if (gaseousMixture.getComponents().size() == 1) {
                header = "Gas";
            } else {
                header = "Gaseous Mixture";
            }

            text.add(TextFormatting.AQUA + "" + TextFormatting.UNDERLINE + header);

            text.add(TextFormatting.RESET + "Total Volume: " + TextFormatting.AQUA + IgblonUtils.roundToDigit(gaseousMixture.getTotalVolume(), 2) + " Liters");
            text.add(TextFormatting.RESET + "Total Pressure: " + TextFormatting.YELLOW + IgblonUtils.roundToDigit(gaseousMixture.getPressure(), 2) + " Pascals");

            if (gaseousMixture.getComponents().size() > 0) {
                text.add(TextFormatting.RESET + "Temperature: " + TextFormatting.RED + "" + IgblonUtils.roundToDigit(gaseousMixture.getTemperature(), 1) + " Kelvin");
                text.add("");
                text.add(TextFormatting.AQUA + "" + TextFormatting.UNDERLINE + "Components");

                HashMap<Chemical, Double> components = gaseousMixture.getComponents();
                double[] individualPressures = gaseousMixture.getIndividualPressures();
                double[] individualVolumes = gaseousMixture.getIndividualVolumes();

                int i = 0;

                for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
                    text.add(TextFormatting.RESET + entry.getKey().getName() + ": " + IgblonUtils.roundToDigit(individualVolumes[i] * 100, 2) + "% " + TextFormatting.GRAY + "(" + IgblonUtils.roundToDigit(entry.getValue(), 2) + " mol) " + TextFormatting.DARK_GRAY + "(" + IgblonUtils.roundToDigit(individualPressures[i], 2) + " Pascals)");
                    i++;
                }
            }
        }

        this.drawHoveringText(text, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        drawGuiTank(guiLeft + 56,  guiTop + 58, 63, 256, this.zLevel, this.chemicalReactor.getContents());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);

        getReactorTooltip(guiLeft, guiTop, mouseX, mouseY);
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
