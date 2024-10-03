package es.cristichi.mod.magiaborras.spells.prop;

import org.joml.Vector3f;

import java.util.Objects;

// Look this is a whole mess, let's just fucking unite all. SpellParticle is a new type of particle that has a type, a radious, vertical and horizontal margins, a vec3f with the center and fuck off even if some types don't use all of them
public final class SpellParticles {
    private final double radius;
    private final double vMar;
    private final double hMar;
    private final SpellParticleType type;
    private final Vector3f colorStart;
    private final Vector3f colorEnd;

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

    public double radius() {
        return radius;
    }

    public double vMar() {
        return vMar;
    }

    public double hMar() {
        return hMar;
    }

    public SpellParticleType type() {
        return type;
    }

    public Vector3f colorStart() {
        return colorStart;
    }

    public Vector3f colorEnd() {
        return colorEnd;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SpellParticles) obj;
        return this.radius == that.radius &&
                this.vMar == that.vMar &&
                this.hMar == that.hMar &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.colorStart, that.colorStart) &&
                Objects.equals(this.colorEnd, that.colorEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(radius, vMar, hMar, type, colorStart, colorEnd);
    }

    @Override
    public String toString() {
        return "SpellParticles[" +
                "radius=" + radius + ", " +
                "vMar=" + vMar + ", " +
                "hMar=" + hMar + ", " +
                "type=" + type + ", " +
                "colorStart=" + colorStart + ", " +
                "colorEnd=" + colorEnd + ']';
    }


    public enum SpellParticleType {
        NO_PARTICLES, RAY, SPHERE, FLOOR
    }
}
