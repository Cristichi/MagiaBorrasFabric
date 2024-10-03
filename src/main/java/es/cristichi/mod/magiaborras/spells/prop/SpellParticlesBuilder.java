package es.cristichi.mod.magiaborras.spells.prop;

import org.joml.Vector3f;

public class SpellParticlesBuilder {
    private double radius = 0;
    private double vMar = 0;
    private double hMar = 0;
    private SpellParticles.SpellParticleType type;
    private Vector3f colorStart = new Vector3f(0, 0, 0);
    private Vector3f colorEnd = null;
    private float size = 0.6f;
    private boolean borderOnly = false;

    public SpellParticlesBuilder setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public SpellParticlesBuilder setvMar(double vMar) {
        this.vMar = vMar;
        return this;
    }

    public SpellParticlesBuilder sethMar(double hMar) {
        this.hMar = hMar;
        return this;
    }

    public SpellParticlesBuilder setType(SpellParticles.SpellParticleType type) {
        this.type = type;
        return this;
    }

    public SpellParticlesBuilder setColorStart(Vector3f colorStart) {
        this.colorStart = colorStart;
        return this;
    }

    public SpellParticlesBuilder setColorEnd(Vector3f colorEnd) {
        this.colorEnd = colorEnd;
        return this;
    }

    public SpellParticlesBuilder setSize(float size) {
        this.size = size;
        return this;
    }

    public SpellParticlesBuilder setBorderOnly(boolean borderOnly) {
        this.borderOnly = borderOnly;
        return this;
    }

    public SpellParticles build() {
        return new SpellParticles(radius, vMar, hMar, type, colorStart, (colorEnd == null) ? colorStart : colorEnd, size, borderOnly);
    }
}