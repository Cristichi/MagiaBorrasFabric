package es.cristichi.mod.magiaborras.timer;

public interface SpellTimersAccess {
    void magiaborras_setRevelioTimer(long ticks);
    void magiaborras_setProtegoTimer(long ticks);
    boolean magiaborras_isProtegoActive();
}