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

    @Shadow public abstract Text getDisplayName();

    @Unique
    private Long ticksLeftRevelio;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci){
        if (ticksLeftRevelio != null && --this.ticksLeftRevelio <= 0L) {
            this.setGlowing(false);
            ticksLeftRevelio = null;
        }
    }

    @Override
    public void magiaborras_setRevelioTimer(long ticksUntilSomething) {
        this.ticksLeftRevelio = ticksUntilSomething;
    }
}