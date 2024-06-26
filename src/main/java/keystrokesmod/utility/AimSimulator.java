package keystrokesmod.utility;

import akka.japi.Pair;
import keystrokesmod.module.impl.other.anticheats.utils.world.PlayerRotation;
import keystrokesmod.script.classes.Vec3;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class AimSimulator {
    public static double xRandom = 0;
    public static double yRandom = 0;
    public static double zRandom = 0;
    public static long lastRandom = System.currentTimeMillis();

    public static @NotNull Pair<Float, Float> getLegitAim(@NotNull EntityLivingBase target, @NotNull EntityPlayerSP player,
                                                          boolean nearest, boolean lazy,
                                                          boolean noise, Pair<Float, Float> noiseRandom, long delay) {
        float yaw, pitch;

        final double yDiff = target.posY - player.posY;
        Vec3 targetPosition;
        Vec3 targetEyePosition = new Vec3(target.prevPosX, target.prevPosY, target.prevPosZ).add(new Vec3(0, target.getEyeHeight(), 0));
        AxisAlignedBB targetBox = target.getEntityBoundingBox();
        if (yDiff >= 0 && lazy) {
            if (targetEyePosition.y() - yDiff > target.posY) {
                targetPosition = new Vec3(targetEyePosition.x(), targetEyePosition.y() - yDiff, targetEyePosition.z());
            } else {
                targetPosition = new Vec3(target.posX, target.posY + 0.4, target.posZ);
            }
        } else {
            targetPosition = nearest ?
                    RotationUtils.getNearestPoint(targetBox, Utils.getEyePos())
                    : targetEyePosition;
        }

        if (noise) {
            if (System.currentTimeMillis() - lastRandom >= delay) {
                xRandom = random(noiseRandom.first());
                yRandom = random(noiseRandom.second());
                zRandom = random(noiseRandom.first());
                lastRandom = System.currentTimeMillis();
            }

            targetPosition.x = normal(targetBox.maxX, targetBox.minX, targetPosition.x + xRandom);
            targetPosition.y = normal(targetBox.maxY, targetBox.minY, targetPosition.y + yRandom);
            targetPosition.z = normal(targetBox.maxZ, targetBox.minZ, targetPosition.z + zRandom);
        }

        yaw = PlayerRotation.getYaw(targetPosition);
        pitch = PlayerRotation.getPitch(targetPosition);

        return new Pair<>(yaw, pitch);
    }

    private static float random(double multiple) {
        return (float) ((Math.random() - 0.5) * 2 * multiple);
    }

    private static double normal(double max, double min, double current) {
        if (current >= max) return max;
        return Math.max(current, min);
    }

    public static float rotMove(float target, float current, float diff) {
        diff *= (float) Math.min(Math.random() + diff * 0.2, diff);

        if (target > current)
            if (target - current > diff)
                return current + diff;
            else
                return target;
        else
        if (current - target > diff)
            return current - diff;
        else
            return target;
    }
}
