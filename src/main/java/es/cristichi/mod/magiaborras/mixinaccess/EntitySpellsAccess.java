package es.cristichi.mod.magiaborras.mixinaccess;

import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public interface EntitySpellsAccess {
    void magiaborras_setRevelioTimer(long ticks);
    void magiaborras_setProtegoTimer(long ticks);
    void magiaborras_setMovement(long ticks, Vec3d step, MovementType type);
    boolean magiaborras_isProtegoActive();
}