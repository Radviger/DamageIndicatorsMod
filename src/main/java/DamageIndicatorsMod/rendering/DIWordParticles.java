package DamageIndicatorsMod.rendering;

import DamageIndicatorsMod.configuration.DIConfig;
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
public class DIWordParticles extends Particle {
    public static DIConfig diConfig = DIConfig.mainInstance();
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

    public DIWordParticles(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
        this(par1World, par2, par4, par6, par8, par10, par12, 0);
        this.criticalhit = true;
        this.particleGravity = -0.05F;
    }

    public DIWordParticles(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, int damage) {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.critical = "Critical!";
        this.criticalhit = false;
        this.heal = false;
        this.grow = true;
        this.shouldOnTop = false;
        this.Damage = damage;
        this.setSize(0.2F, 0.2F);
        this.yOffset = this.height * 1.1F;
        this.setPosition(par2, par4, par6);
        this.motionX = par8;
        this.motionY = par10;
        this.motionZ = par12;
        float var15 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double) var15 * 0.12D;
        this.motionY = this.motionY / (double) var15 * 0.12D;
        this.motionZ = this.motionZ / (double) var15 * 0.12D;
        this.particleTextureJitterX = 1.5F;
        this.particleTextureJitterY = 1.5F;
        this.particleGravity = diConfig.Gravity;
        this.particleScale = diConfig.Size;
        this.particleMaxAge = diConfig.Lifespan;
        this.particleAge = 0;
        if (this.Damage < 0) {
            this.heal = true;
            this.Damage = Math.abs(this.Damage);
        }

        try {
            int baseColor = this.heal ? diConfig.healColor : diConfig.DIColor;
            this.red = (float) (baseColor >> 16 & 255) / 255.0F;
            this.green = (float) (baseColor >> 8 & 255) / 255.0F;
            this.blue = (float) (baseColor & 255) / 255.0F;
            this.alpha = diConfig.transparency * 0.9947F;
            this.ul = ((float) this.Damage - (float) MathHelper.floor((float) this.Damage / 16.0F) * 16.0F) % 16.0F / 16.0F;
            this.ur = this.ul + 0.0624375F;
            this.vl = (float) MathHelper.floor((float) this.Damage / 16.0F) * 16.0F / 16.0F / 16.0F;
            this.vr = this.vl + 0.0624375F;
        } catch (Throwable ignored) {
        }

    }

    @Override
    public void move(double x, double y, double z) {
        super.move(x, y, z);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        this.shouldOnTop = Minecraft.getMinecraft().player.canEntityBeSeen(entityIn);
        double rotationYaw = -Minecraft.getMinecraft().player.rotationYaw;
        double rotationPitch = Minecraft.getMinecraft().player.rotationPitch;
        float size = 0.1F * this.particleScale;

        this.locX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        this.locY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        this.locZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

        GL11.glPushMatrix();
        if (this.shouldOnTop) {
            GL11.glDepthFunc(519);
        } else {
            GL11.glDepthFunc(515);
        }

        GL11.glTranslatef(this.locX, this.locY, this.locZ);
        GL11.glRotated(rotationYaw, 0.0D, 1.0D, 0.0D);
        GL11.glRotated(rotationPitch, 1.0D, 0.0D, 0.0D);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glScaled((double) this.particleScale * 0.008D, (double) this.particleScale * 0.008D, (double) this.particleScale * 0.008D);
        if (this.criticalhit) {
            GL11.glScaled(0.5D, 0.5D, 0.5D);
        }

        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2896);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glEnable(3008);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.criticalhit && DIConfig.mainInstance().showCriticalStrikes) {
            this.renderText(this.critical, (float) this.fontRenderer.getStringWidth(this.critical) / -2.0F, (float) this.fontRenderer.FONT_HEIGHT / -2.0F, 204, 0, 0);
        } else if (!this.criticalhit) {
            int color = this.heal ? DIConfig.mainInstance().healColor : DIConfig.mainInstance().DIColor;
            this.renderText(String.valueOf(this.Damage), (float) this.fontRenderer.getStringWidth(this.Damage + "") / -2.0F, (float) this.fontRenderer.FONT_HEIGHT / -2.0F, color >> 16 & 255, color >> 8 & 255, color >> 0 & 255);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthFunc(515);
        GL11.glPopMatrix();
        if (this.grow) {
            this.particleScale *= 1.08F;
            if ((double) this.particleScale > (double) diConfig.Size * 3.0D) {
                this.grow = false;
            }
        } else {
            this.particleScale *= 0.96F;
        }

    }

    public void renderText(String str, float posX, float posY, int red, int green, int blue) {
        if (DIConfig.mainInstance().useDropShadows) {
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
            GL11.glPushMatrix();
            GL11.glTranslated(-0.2D, -0.2D, 0.0D);
            GL11.glScaled(1.075D, 1.075D, 1.0D);
            this.fontRenderer.drawString(str, 0, 0, ((int) ((double) this.alpha * 64.0D) & 255) << 24 | ((red + r) / 2 & 255) << 16 | ((green + g) / 2 & 255) << 8 | ((blue + b) / 2 & 255) << 0);
            GL11.glPopMatrix();
            this.fontRenderer.drawString(str, 0, 0, ((int) ((double) this.alpha * 128.0D) & 255) << 24 | ((red + red + r) / 3 & 255) << 16 | ((green + green + g) / 3 & 255) << 8 | ((blue + blue + b) / 3 & 255) << 0);
            GL11.glPushMatrix();
            GL11.glTranslated(0.15D, 0.15D, 0.0D);
            GL11.glScaled(0.95D, 0.95D, 1.0D);
            this.fontRenderer.drawString(str, 0, 0, ((int) ((double) this.alpha * 255.0D) & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | (blue & 255) << 0);
            GL11.glPopMatrix();
        } else {
            this.fontRenderer.drawString(str, 0, 0, ((int) ((double) this.alpha * 255.0D) & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | (blue & 255) << 0);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
