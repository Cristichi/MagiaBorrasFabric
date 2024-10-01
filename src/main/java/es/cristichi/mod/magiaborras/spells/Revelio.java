package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.timer.RevelioStopTimerAccess;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.List;

public class Revelio extends Spell {
    private static final double MAX_AREA = 20;

    public Revelio() {
        super("revelio", Text.translatable("magiaborras.spell.revelio"), List.of(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK, null, 60);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        double power = properties.getPower(magicUser);
        List<Entity> ents = world.getOtherEntities(magicUser, magicUser.getBoundingBox().expand(MAX_AREA*power));
        for (Entity ent : ents){
            ent.setGlowing(true);
            ((RevelioStopTimerAccess) ent).magiaborras_setRevelioTimer(baseCooldown);
        }
        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.REVELIO_CAST));
    }
}
