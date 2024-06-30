package es.cristichi.mod.magiaborras.spells;


import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Predicate;

public abstract class Spell {
    // TODO: Spells in Hogwards Legacy I think I want to add from the old version
    //  X Accio
    //  - ArrestoMomentum
    //  X Avada
    //  - Crucio
    //  - Depulso
    //  - Expelliarmus
    //  - Finite Incantatem
    //  - Imperio
    //  - Incendio
    //  - Petrificus Totalus
    //  - Stupefy
    //  X Wingardium Leviosa

    // TODO: More Spells from H.L. I like that are not in the old version:
    //  X Alohomora (it opens iron doors/trapdoors)
    //  - Lumos (well I don't know how to do it xD)
    //  - Bombarda
    //  X Diffindo

    static final Predicate<Entity> LIVING_ENTITIES =  (entity -> !entity.isSpectator() && entity.canBeHitByProjectile());
    static final Predicate<Entity> ANY_ENTITY =  (entity -> true);
    static final Predicate<Entity> NO_ENTITY =  (entity -> false);
    static final Predicate<Block> ANY_BLOCK =  (entity -> true);
    static final Predicate<Block> NO_BLOCK =  (entity -> false);

    protected String id;
    protected Text name;
    protected List<SpellCastType> castTypes;
    protected Predicate<Entity> affectableEntities;
    protected Predicate<Block> affectableBlocks;
    @Nullable
    protected Vector3f partColor;
    protected int baseCooldown;

    public Spell(String id, Text name, List<SpellCastType> castTypes, Predicate<Entity> affectableEntities,
                 Predicate<Block> affectableBlocks, @Nullable Vector3f partColor, int baseCooldown) {
        this.id = id;
        this.name = name;
        this.affectableEntities = affectableEntities;
        this.affectableBlocks = affectableBlocks;
        this.castTypes = castTypes;
        this.partColor = partColor;
        this.baseCooldown = baseCooldown;
    }

    public String getId() {
        return id;
    }

    public Text getName() {
        return name;
    }

    public List<SpellCastType> getCastTypes() {
        return castTypes;
    }

    public Predicate<Entity> getAffectableEntities() {
        return affectableEntities;
    }

    public @Nullable Vector3f getParticlesColor() {
        return partColor;
    }

    public abstract Spell.Result use(ItemStack wand, WandProperties properties, PlayerEntity magicUser, World world, HitResult hit);

    public static class Result {
        private TypedActionResult<ItemStack> actionResult;
        private int cooldown;
        private List<SoundEvent> sounds;

        public Result(TypedActionResult<ItemStack> actionResult, int cooldown, List<SoundEvent> sounds) {
            this.actionResult = actionResult;
            this.cooldown = cooldown;
            this.sounds = sounds;
        }

        public TypedActionResult<ItemStack> getActionResult() {
            return actionResult;
        }

        public void setActionResult(TypedActionResult<ItemStack> actionResult) {
            this.actionResult = actionResult;
        }

        public int getCooldown() {
            return cooldown;
        }

        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
        }

        public List<SoundEvent> getSounds() {
            return sounds;
        }

        public void setSounds(List<SoundEvent> sounds) {
            this.sounds = sounds;
        }
    }
}
