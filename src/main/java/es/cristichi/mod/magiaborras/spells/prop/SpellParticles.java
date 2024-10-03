package es.cristichi.mod.magiaborras.spells.prop;

import org.joml.Vector3f;

/**
 * This class contains all the possible information needed for drawing Spell particles in clients.
 * It is important to note that only the SpellParticleType is mandatory, and the rest are used in different types.
 */
public final class SpellParticles {
    private double radius;
    private double vMar;
    private double hMar;
    private SpellParticleType type;
    private Vector3f colorStart;
    private Vector3f colorEnd;
    private float size;

    public SpellParticles(double radius, double vMar, double hMar, SpellParticleType type, Vector3f colorStart, Vector3f colorEnd, float size) {
        this.radius = radius;
        this.vMar = vMar;
        this.hMar = hMar;
        this.type = type;
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
        this.size = size;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getVMar() {
        return vMar;
    }

    public void setvMar(double vMar) {
        this.vMar = vMar;
    }

    public double getHMar() {
        return hMar;
    }

    public void sethMar(double hMar) {
        this.hMar = hMar;
    }

    public SpellParticleType getType() {
        return type;
    }

    public void setType(SpellParticleType type) {
        this.type = type;
    }

    public Vector3f getColorStart() {
        return colorStart;
    }

    public void setColorStart(Vector3f colorStart) {
        this.colorStart = colorStart;
    }

    public Vector3f getColorEnd() {
        return colorEnd;
    }

    public void setColorEnd(Vector3f colorEnd) {
        this.colorEnd = colorEnd;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public enum SpellParticleType {
        NO_PARTICLES, RAY, SPHERE, FLOOR
    }
}
