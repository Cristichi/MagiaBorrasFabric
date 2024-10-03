package es.cristichi.mod.magiaborras.spells.prop;

import org.joml.Vector3f;

public final class SpellParticles {
    private double radius;
    private double vMar;
    private double hMar;
    private SpellParticleType type;
    private Vector3f colorStart;
    private Vector3f colorEnd;

    public SpellParticles(double radius, double vMar, double hMar, SpellParticleType type, Vector3f colorStart, Vector3f colorEnd) {
        this.radius = radius;
        this.vMar = vMar;
        this.hMar = hMar;
        this.type = type;
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
    }

    public SpellParticles(SpellParticleType type, Vector3f colorStart, Vector3f colorEnd) {
        this.radius = 0;
        this.vMar = 0;
        this.hMar = 0;
        this.type = type;
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
    }


    public SpellParticles(SpellParticleType type, Vector3f colorStart) {
        this.radius = 0;
        this.vMar = 0;
        this.hMar = 0;
        this.type = type;
        this.colorStart = colorStart;
        this.colorEnd = colorStart;
    }

    public SpellParticles(double radius, double vMar, double hMar, SpellParticleType type, Vector3f colorStart) {
        this.radius = radius;
        this.vMar = vMar;
        this.hMar = hMar;
        this.type = type;
        this.colorStart = colorStart;
        this.colorEnd = colorStart;
    }

    public SpellParticles(SpellParticleType type) {
        this.radius = 0;
        this.vMar = 0;
        this.hMar = 0;
        this.type = type;
        this.colorStart = new Vector3f(0,0,0);
        this.colorEnd = colorStart;
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

    public enum SpellParticleType {
        NO_PARTICLES, RAY, SPHERE, FLOOR
    }
}
