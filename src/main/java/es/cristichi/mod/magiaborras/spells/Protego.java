package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import es.cristichi.mod.magiaborras.mixinaccess.EntitySpellsAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class Protego extends Spell {
    public static final int PUNISH_COOLDOWM = 60;

    public Protego() {
        super("protego", Text.translatable("magiaborras.spell.protego"), List.of(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK,
                new SpellParticlesBuilder().setRadius(2).setvMar(0.5).sethMar(0.5).setType(SpellParticles.SpellParticleType.SPHERE).setColorStart(new Vector3f(0.2f, 0.2f, 1f)).build(),
                20);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        ((EntitySpellsAccess) magicUser).magiaborras_setProtegoTimer(baseCooldown* 2L);
        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.PROTEGO_SOUNDEVENT));
    }
}
