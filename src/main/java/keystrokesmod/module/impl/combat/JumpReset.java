package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;
import keystrokesmod.event.JumpEvent;
import keystrokesmod.mixins.impl.entity.EntityAccessor;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.TimeUnit;

public class JumpReset extends Module {
    private final SliderSetting minDelay;
    private final SliderSetting maxDelay;
    private final SliderSetting chance;
    private final SliderSetting maxFallDistance;
    private final ButtonSetting ignoreFire;
    private boolean jump;

    public JumpReset() {
        super("Jump Reset", category.combat);
        this.registerSetting(minDelay = new SliderSetting("Min delay", 0, 0, 150, 1, "ms"));
        this.registerSetting(maxDelay = new SliderSetting("Max delay", 0, 0, 150, 1, "ms"));
        this.registerSetting(chance = new SliderSetting("Chance", 80, 0, 100, 1, "%"));
        this.registerSetting(maxFallDistance = new SliderSetting("Max fall distance", 3, 1, 8, 0.5));
        this.registerSetting(ignoreFire = new ButtonSetting("Ignore fire", true));
    }

    public void onDisable() {
        jump = false;
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent ev) {
        if (Utils.nullCheck()) {
            if (chance.getInput() == 0) {
                return;
            }
            if (mc.thePlayer.maxHurtTime <= 0) {
                jump = false;
                return;
            }
            if (ignoreFire.isToggled() && ((EntityAccessor) mc.thePlayer).getFire() > 0) {
                jump = false;
                return;
            }
            if (mc.thePlayer.fallDistance > maxFallDistance.getInput()) {
                jump = false;
                return;
            }
            if (mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime) {
                jump = true;
            }
            if (!jump || mc.thePlayer.hurtTime == 0) {
                jump = false;
                return;
            }
            if (chance.getInput() != 100.0D) {
                double ch = Math.random();
                if (ch >= chance.getInput() / 100.0D) {
                    return;
                }
            }
            if (jump) {
                long delay = (long) (Math.random() * (maxDelay.getInput() - minDelay.getInput()) + minDelay.getInput());
                if (delay == 0) {
                    if (mc.thePlayer.onGround) mc.thePlayer.jump();
                    jump = false;
                } else {
                    Raven.getExecutor().schedule(() -> {
                        if (mc.thePlayer.onGround) mc.thePlayer.jump();
                        jump = false;
                    }, delay, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    @SubscribeEvent
    public void onJump(JumpEvent e) {
        if (!Utils.nullCheck() || !jump) {
            return;
        }
        jump = false;
    }
}
