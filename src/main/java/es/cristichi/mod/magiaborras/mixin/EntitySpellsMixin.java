package es.cristichi.mod.magiaborras.mixin;

import es.cristichi.mod.magiaborras.mixinaccess.EntitySpellsAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntitySpellsMixin implements EntitySpellsAccess {
    @Shadow public abstract void setGlowing(boolean glowing);
    @Shadow public abstract void setNoGravity(boolean noGravity);
    @Shadow public abstract void setVelocity(Vec3d velocity);
    @Shadow protected abstract void scheduleVelocityUpdate();

    @Shadow public float fallDistance;
    @Unique
    private Long ticksLeftRevelio;
    @Unique
    private Long ticksLeftProtego;
    @Unique
    private MovementType movementType;
    @Unique
    private Vec3d stepMovement;
    @Unique
    private Long ticksLeftMovement;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci){
        if (ticksLeftRevelio != null) {
            if (--this.ticksLeftRevelio <= 0L){
                this.setGlowing(false);
                ticksLeftRevelio = null;
            } else {
                this.setGlowing(true);
            }
        }
        if (ticksLeftProtego != null && --this.ticksLeftProtego <= 0L) {
            ticksLeftProtego = null;
        }
        if (ticksLeftMovement != null && stepMovement != null) {
            //move(movementType, stepMovement);
            setVelocity(stepMovement);
            scheduleVelocityUpdate();
            if (--this.ticksLeftMovement <= 0L){
                ticksLeftMovement = null;
                stepMovement = null;
                setNoGravity(false);
            }
        }
    }

    @Override
    public void magiaborras_setRevelioTimer(long ticks) {
        this.ticksLeftRevelio = ticks;
    }

    @Override
    public void magiaborras_setProtegoTimer(long ticks) {
        ticksLeftProtego = ticks;
    }

    @Override
    public void magiaborras_setMovement(long ticks, Vec3d step, MovementType type) {
        ticksLeftMovement = ticks;
        stepMovement = step;
        movementType = type;
        setNoGravity(true);
    }

    @Override
    public boolean magiaborras_isProtegoActive() {
        return ticksLeftProtego != null;
    }
}