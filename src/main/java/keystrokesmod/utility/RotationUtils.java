package keystrokesmod.utility;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.module.impl.other.anticheats.utils.world.PlayerRotation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class RotationUtils {
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static float renderPitch;
    public static float prevRenderPitch;
    public static float renderYaw;
    public static float prevRenderYaw;

    public static void setRenderYaw(float yaw) {
        mc.thePlayer.rotationYawHead = yaw;
        if (Settings.rotateBody.isToggled() && Settings.fullBody.isToggled()) {
            mc.thePlayer.renderYawOffset = yaw;
        }
    }

    public static float[] getRotations(BlockPos blockPos, final float n, final float n2) {
        final float[] array = getRotations(blockPos);
        return fixRotation(array[0], array[1], n, n2);
    }

    public static float[] getRotations(final BlockPos blockPos) {
        final double n = blockPos.getX() + 0.45 - mc.thePlayer.posX;
        final double n2 = blockPos.getY() + 0.45 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        final double n3 = blockPos.getZ() + 0.45 - mc.thePlayer.posZ;
        return new float[] { mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float((float)(Math.atan2(n3, n) * 57.295780181884766) - 90.0f - mc.thePlayer.rotationYaw), clamp(mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float((float)(-(Math.atan2(n2, MathHelper.sqrt_double(n * n + n3 * n3)) * 57.295780181884766)) - mc.thePlayer.rotationPitch)) };
    }

    public static float interpolateValue(float tickDelta, float old, float newFloat) {
        return old + (newFloat - old) * tickDelta;
    }

    public static float @NotNull [] getRotations(Entity entity, final float n, final float n2) {
        final float[] array = getRotations(entity);
        if (array == null) {
            return new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch };
        }
        return fixRotation(array[0], array[1], n, n2);
    }

    public static double distanceFromYaw(final Entity entity, final boolean b) {
        return Math.abs(MathHelper.wrapAngleTo180_double(i(entity.posX, entity.posZ) - ((b && PreMotionEvent.setRenderYaw()) ? RotationUtils.renderYaw : mc.thePlayer.rotationYaw)));
    }

    public static float i(final double n, final double n2) {
        return (float)(Math.atan2(n - mc.thePlayer.posX, n2 - mc.thePlayer.posZ) * 57.295780181884766 * -1.0);
    }

    public static boolean notInRange(final BlockPos blockPos, final double n) {
        AxisAlignedBB box = BlockUtils.getCollisionBoundingBox(blockPos);
        keystrokesmod.script.classes.Vec3 eyePos = Utils.getEyePos();
        if (box == null) {
            return eyePos.distanceTo(keystrokesmod.script.classes.Vec3.convert(blockPos)) > n;
        } else {
            return eyePos.distanceTo(getNearestPoint(box, eyePos)) > n;
        }
    }

    public static float[] getRotations(final Entity entity) {
        if (entity == null) {
            return null;
        }
        final double n = entity.posX - mc.thePlayer.posX;
        final double n2 = entity.posZ - mc.thePlayer.posZ;
        double n3;
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            n3 = entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.9 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        } else {
            n3 = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        }
        return new float[]{mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float((float) (Math.atan2(n2, n) * 57.295780181884766) - 90.0f - mc.thePlayer.rotationYaw), clamp(mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float((float) (-(Math.atan2(n3, MathHelper.sqrt_double(n * n + n2 * n2)) * 57.295780181884766)) - mc.thePlayer.rotationPitch) + 3.0f)};
    }

    public static float[] getRotationsPredicated(final Entity entity, final int ticks) {
        if (entity == null) {
            return null;
        }
        if (ticks == 0) {
            return getRotations(entity);
        }
        double posX = entity.posX;
        final double posY = entity.posY;
        double posZ = entity.posZ;
        final double n2 = posX - entity.lastTickPosX;
        final double n3 = posZ - entity.lastTickPosZ;
        for (int i = 0; i < ticks; ++i) {
            posX += n2;
            posZ += n3;
        }
        final double n4 = posX - mc.thePlayer.posX;
        double n5;
        if (entity instanceof EntityLivingBase) {
            n5 = posY + entity.getEyeHeight() * 0.9 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        }
        else {
            n5 = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        }
        final double n6 = posZ - mc.thePlayer.posZ;
        return new float[] { mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float((float)(Math.atan2(n6, n4) * 57.295780181884766) - 90.0f - mc.thePlayer.rotationYaw), clamp(mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float((float)(-(Math.atan2(n5, MathHelper.sqrt_double(n4 * n4 + n6 * n6)) * 57.295780181884766)) - mc.thePlayer.rotationPitch) + 3.0f) };
    }

    public static float clamp(final float n) {
        return MathHelper.clamp_float(n, -90.0f, 90.0f);
    }

    public static float[] fixRotation(float n, float n2, final float n3, final float n4) {
        float n5 = n - n3;
        final float abs = Math.abs(n5);
        final float n7 = n2 - n4;
        final float n8 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final double n9 = n8 * n8 * n8 * 1.2;
        final float n10 = (float) (Math.round((double) n5 / n9) * n9);
        final float n11 = (float) (Math.round((double) n7 / n9) * n9);
        n = n3 + n10;
        n2 = n4 + n11;
        if (abs >= 1.0f) {
            final int n12 = (int) Settings.randomYawFactor.getInput();
            if (n12 != 0) {
                final int n13 = n12 * 100 + Utils.randomizeInt(-30, 30);
                n += Utils.randomizeInt(-n13, n13) / 100.0;
            }
        } else if (abs <= 0.04) {
            n += ((abs > 0.0f) ? 0.01 : -0.01);
        }
        return new float[]{n, clamp(n2)};
    }

    public static float angle(final double n, final double n2) {
        return (float) (Math.atan2(n - mc.thePlayer.posX, n2 - mc.thePlayer.posZ) * 57.295780181884766 * -1.0);
    }

    public static MovingObjectPosition rayCast(final double distance, final float yaw, final float pitch) {
        final Vec3 getPositionEyes = mc.thePlayer.getPositionEyes(1.0f);
        final float n4 = -yaw * 0.017453292f;
        final float n5 = -pitch * 0.017453292f;
        final float cos = MathHelper.cos(n4 - 3.1415927f);
        final float sin = MathHelper.sin(n4 - 3.1415927f);
        final float n6 = -MathHelper.cos(n5);
        final Vec3 vec3 = new Vec3(sin * n6, MathHelper.sin(n5), cos * n6);
        return mc.theWorld.rayTraceBlocks(getPositionEyes, getPositionEyes.addVector(vec3.xCoord * distance, vec3.yCoord * distance, vec3.zCoord * distance), false, false, false);
    }
    
    public static boolean rayCast(final float yaw, final float pitch, @NotNull EntityLivingBase target) {
        AxisAlignedBB targetBox = target.getCollisionBoundingBox();
        keystrokesmod.script.classes.Vec3 fromPos = new keystrokesmod.script.classes.Vec3(mc.thePlayer).add(0, mc.thePlayer.getEyeHeight(), 0);

        float minYaw = Float.MAX_VALUE;
        float maxYaw = Float.MIN_VALUE;
        float minPitch = Float.MAX_VALUE;
        float maxPitch = Float.MIN_VALUE;

        for (double x : new double[]{targetBox.minX, targetBox.maxX}) {
            for (double y : new double[]{targetBox.minY, targetBox.maxY}) {
                for (double z : new double[]{targetBox.minZ, targetBox.maxZ}) {
                    final keystrokesmod.script.classes.Vec3 hitPos = new keystrokesmod.script.classes.Vec3(x, y, z);
                    final double distance = getFarthestPoint(targetBox, fromPos).distanceTo(fromPos);
                    final float yaw1 = PlayerRotation.getYaw(hitPos);
                    final float pitch1 = PlayerRotation.getPitch(hitPos);

                    MovingObjectPosition hitResult = rayCast(distance, yaw1, pitch1);
                    if (hitResult == null) continue;

                    if (minYaw > yaw1) minYaw = yaw1;
                    if (maxYaw < yaw1) maxYaw = yaw1;
                    if (minPitch > pitch1) minPitch = pitch1;
                    if (maxPitch < pitch1) maxPitch = pitch1;
                }
            }
        }

        return yaw >= minYaw && yaw <= maxYaw && pitch >= minPitch && pitch <= maxPitch;
    }

    @Contract("_, _ -> new")
    public static @NotNull Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    @Contract("_, _ -> new")
    public static @NotNull keystrokesmod.script.classes.Vec3 getNearestPoint(@NotNull AxisAlignedBB from, @NotNull keystrokesmod.script.classes.Vec3 to) {
        double pointX, pointY, pointZ;
        if (to.x() >= from.maxX) {
            pointX = from.maxX;
        } else pointX = Math.max(to.x(), from.minX);
        if (to.y() >= from.maxY) {
            pointY = from.maxY;
        } else pointY = Math.max(to.y(), from.minY);
        if (to.z() >= from.maxZ) {
            pointZ = from.maxZ;
        } else pointZ = Math.max(to.z(), from.minZ);

        return new keystrokesmod.script.classes.Vec3(pointX, pointY, pointZ);
    }

    @Contract("_, _ -> new")
    public static @NotNull keystrokesmod.script.classes.Vec3 getFarthestPoint(@NotNull AxisAlignedBB from, @NotNull keystrokesmod.script.classes.Vec3 to) {
        double pointX, pointY, pointZ;
        if (to.x() < from.maxX) {
            pointX = from.maxX;
        } else pointX = Math.min(to.x(), from.minX);
        if (to.y() < from.maxY) {
            pointY = from.maxY;
        } else pointY = Math.min(to.y(), from.minY);
        if (to.z() < from.maxZ) {
            pointZ = from.maxZ;
        } else pointZ = Math.min(to.z(), from.minZ);

        return new keystrokesmod.script.classes.Vec3(pointX, pointY, pointZ);
    }
}
