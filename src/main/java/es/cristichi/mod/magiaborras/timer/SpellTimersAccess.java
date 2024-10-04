package es.cristichi.mod.magiaborras.timer;

import net.minecraft.util.math.Vec3d;

public interface SpellTimersAccess {
    void magiaborras_setRevelioTimer(long ticks);
    void magiaborras_setProtegoTimer(long ticks);
    void magiaborras_setMovement(long ticks, Vec3d step);
    boolean magiaborras_isProtegoActive();
}