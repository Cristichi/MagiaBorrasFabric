package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class Expelliarmus extends Spell {
    public Expelliarmus() {
        super("expelliarmus", Text.translatable("magiaborras.spell.expelliarmus"), List.of(SpellCastType.USE),
                Spell.LIVING_ENTITIES, Spell.NO_BLOCK,
                new SpellParticlesBuilder().setType(SpellParticles.SpellParticleType.RAY).setColorStart(new Vector3f(0.8f, 0, 0)).build(),
                40);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hitEnt = (EntityHitResult) hit;
            Entity ent = hitEnt.getEntity();
            if (ent instanceof LivingEntity livingEnt){
                ItemStack handStack = livingEnt.getStackInHand(Hand.MAIN_HAND);
                if (handStack != null){
                    ItemEntity dropped = livingEnt.dropStack(handStack);
                    if (dropped != null){
                        livingEnt.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                        dropped.setGlowing(true);
                        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.EXPELLIARMUS_SOUNDEVENT));
                    }
                }
            }
        }
        return new Result(ActionResult.FAIL, 0, null);
    }
}
