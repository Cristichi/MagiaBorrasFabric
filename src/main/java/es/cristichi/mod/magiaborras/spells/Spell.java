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
    //  - Finite (if I figure out what to do with it)
    //  - Imperio
    //  - Incendio
    //  - Petrificus Totalus
    //  - Stupefy
    //  X Wingardium Leviosa

    // TODO: More Spells from H.L. I like that are not in the old version:
    //  X Alohomora (it opens iron doors/trapdoors)
    //  ? Lumos (I need to wait for other people to work dynamic lights on Minecraft 1.21)
    //  - Bombarda
    //  X Diffindo (as a substitute of the "Default" HL spell, since my "Default" does something different)

    // TODO: Other Spells I'd love to implement (do I record the Spell cast .ogg myself?)
    //  - Morsmorde (for my friend, who clearly is not a Death Eater)
    //  - Finite Incantatem (for me to stop the annoying Morsmorde spam)

    static final Predicate<Entity> LIVING_ENTITIES =  (entity -> !entity.isSpectator() && entity.canBeHitByProjectile());
    static final Predicate<Entity> ANY_ENTITY =  (entity -> true);
    static final Predicate<Entity> NO_ENTITY =  (entity -> false);
//    static final Predicate<Block> ANY_BLOCK =  (entity -> true); //Tool for the future
    static final Predicate<Block> NO_BLOCK =  (entity -> false);

    protected String id;
    protected Text name;
    protected List<SpellCastType> castTypes;
    protected Predicate<Entity> affectableEntities;
    protected Predicate<Block> affectableBlocks;
    @Nullable
    protected Vector3f partColor;
    protected int baseCooldown;

    protected Spell(String id, Text name, List<SpellCastType> castTypes, Predicate<Entity> affectableEntities,
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

    public abstract Spell.Result use(ItemStack wand, WandProperties properties,
                                     PlayerEntity magicUser, World world, HitResult hit);

    public record Result(TypedActionResult<ItemStack> actionResult, int cooldown, List<SoundEvent> sounds) {
    }
}
