package ru.radviger.damageindicators.rendering;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ParticleText extends Particle {
    public static IndicatorsConfig config = IndicatorsConfig.mainInstance();
    public boolean criticalhit;
    public int Damage;
    public boolean shouldOnTop;
    boolean heal;
    boolean grow;
    float ul;
    float ur;
    float vl;
    float vr;
    float locX;
    float locY;
    float locZ;
    float red;
    float green;
    float blue;
    float alpha;
    float yOffset;
    FontRenderer fontRenderer;
    private String critical;

    public ParticleText(World world, double x, double y, double z, double velX, double velY, double velZ) {
        this(world, x, y, z, velX, velY, velZ, 0);
        this.criticalhit = true;
        this.particleGravity = -0.05F;
    }

    public ParticleText(World world, double x, double y, double z, double velX, double velY, double velZ, int damage) {
        super(world, x, y, z, velX, velY, velZ);
        this.critical = I18n.format("translation.particle.critical");
        this.criticalhit = false;
        this.heal = false;
        this.grow = true;
        this.shouldOnTop = false;
        this.Damage = damage;
        this.setSize(0.2F, 0.2F);
        this.yOffset = this.height * 1.1F;
        this.setPosition(x, y, z);
        this.motionX = velX;
        this.motionY = velY;
        this.motionZ = velZ;
        float d = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double) d * 0.12D;
        this.motionY = this.motionY / (double) d * 0.12D;
        this.motionZ = this.motionZ / (double) d * 0.12D;
        this.particleTextureJitterX = 1.5F;
        this.particleTextureJitterY = 1.5F;
        this.particleGravity = config.Gravity;
        this.particleScale = config.Size;
        this.particleMaxAge = config.Lifespan;
        this.particleAge = 0;
        if (this.Damage < 0) {
            this.heal = true;
            this.Damage = Math.abs(this.Damage);
        }

        int baseColor = this.heal ? config.healColor : config.DIColor;
        this.red = (float) (baseColor >> 16 & 255) / 255.0F;
        this.green = (float) (baseColor >> 8 & 255) / 255.0F;
        this.blue = (float) (baseColor & 255) / 255.0F;
        this.alpha = config.transparency * 0.9947F;
        this.ul = ((float) this.Damage - (float) MathHelper.floor((float) this.Damage / 16.0F) * 16.0F) % 16.0F / 16.0F;
        this.ur = this.ul + 0.0624375F;
        this.vl = (float) MathHelper.floor((float) this.Damage / 16.0F) * 16.0F / 16.0F / 16.0F;
        this.vr = this.vl + 0.0624375F;
    }

    @Override
    public void move(double x, double y, double z) {
        super.move(x, y, z);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        this.shouldOnTop = Minecraft.getMinecraft().player.canEntityBeSeen(entityIn);
        float rotationYaw = -Minecraft.getMinecraft().player.rotationYaw;
        float rotationPitch = Minecraft.getMinecraft().player.rotationPitch;
        float size = 0.1F * this.particleScale;

        this.locX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        this.locY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        this.locZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

        GlStateManager.pushMatrix();
        if (this.shouldOnTop) {
            GlStateManager.depthFunc(GL11.GL_ALWAYS);
        } else {
            GlStateManager.depthFunc(GL11.GL_LEQUAL);
        }

        GlStateManager.translate(this.locX, this.locY, this.locZ);
        GlStateManager.rotate(rotationYaw, 0, 1, 0);
        GlStateManager.rotate(rotationPitch, 1, 0, 0);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.scale((double) this.particleScale * 0.008D, (double) this.particleScale * 0.008D, (double) this.particleScale * 0.008D);
        if (this.criticalhit) {
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
        }

        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1F, 1F, 1F, 1F);
        if (this.criticalhit && IndicatorsConfig.mainInstance().showCriticalStrikes) {
            this.renderText(this.critical, (float) this.fontRenderer.getStringWidth(this.critical) / -2.0F, (float) this.fontRenderer.FONT_HEIGHT / -2.0F, 204, 0, 0);
        } else if (!this.criticalhit) {
            int color = this.heal ? IndicatorsConfig.mainInstance().healColor : IndicatorsConfig.mainInstance().DIColor;
            this.renderText(String.valueOf(this.Damage), (float) this.fontRenderer.getStringWidth(this.Damage + "") / -2.0F, (float) this.fontRenderer.FONT_HEIGHT / -2.0F, color >> 16 & 255, color >> 8 & 255, color >> 0 & 255);
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.popMatrix();
        if (this.grow) {
            this.particleScale *= 1.08F;
            if ((double) this.particleScale > (double) config.Size * 3.0D) {
                this.grow = false;
            }
        } else {
            this.particleScale *= 0.96F;
        }
    }

    public void renderText(String str, float posX, float posY, int red, int green, int blue) {
        if (IndicatorsConfig.mainInstance().useDropShadows) {
            int r = red;
            int g = green;
            int b = blue;
            if (red > green && red > blue) {
                r = 255;
                g = 0;
                b = 0;
            } else if (green > red && green > blue) {
                r = 0;
                g = 255;
                b = 0;
            } else if (blue > red && blue > green) {
                r = 0;
                g = 0;
                b = 255;
            }

            this.fontRenderer.drawString(str, 1, 1, ((int) ((double) this.alpha * 200.0D) & 255) << 24);
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.2D, -0.2D, 0.0D);
            GlStateManager.scale(1.075D, 1.075D, 1.0D);
            this.fontRenderer.drawString(str, 0, 0, ((int) ((double) this.alpha * 64.0D) & 255) << 24 | ((red + r) / 2 & 255) << 16 | ((green + g) / 2 & 255) << 8 | ((blue + b) / 2 & 255) << 0);
            GlStateManager.popMatrix();

            this.fontRenderer.drawString(str, 0, 0, ((int) ((double) this.alpha * 128.0D) & 255) << 24 | ((red + red + r) / 3 & 255) << 16 | ((green + green + g) / 3 & 255) << 8 | ((blue + blue + b) / 3 & 255) << 0);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.15D, 0.15D, 0.0D);
            GlStateManager.scale(0.95D, 0.95D, 1.0D);
            this.fontRenderer.drawString(str, 0, 0, ((int) ((double) this.alpha * 255.0D) & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | (blue & 255) << 0);
            GlStateManager.popMatrix();
        } else {
            this.fontRenderer.drawString(str, 0, 0, ((int) ((double) this.alpha * 255.0D) & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | (blue & 255) << 0);
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
