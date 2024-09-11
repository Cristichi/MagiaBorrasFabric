package es.cristichi.mod.magiaborras.mixin;

import es.cristichi.mod.magiaborras.timer.RevelioStopTimerAccess;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class RevelioStopTimer implements RevelioStopTimerAccess {
    @Shadow public abstract void setGlowing(boolean glowing);

    @Shadow public abstract Text getName();

    @Unique
    private Long ticksUntilSomething;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci){
        if (ticksUntilSomething != null && --this.ticksUntilSomething <= 0L) {
            this.setGlowing(false);
            ticksUntilSomething = null;
        }
    }

    @Override
    public void magiaborras_setRevelioTimer(long ticksUntilSomething) {
        this.ticksUntilSomething = ticksUntilSomething;
    }
}