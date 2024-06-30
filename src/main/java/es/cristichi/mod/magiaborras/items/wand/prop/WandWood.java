package es.cristichi.mod.magiaborras.items.wand.prop;

import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;

public enum WandWood {
    NONE(Text.translatable("magiaborras.wandwood.none"), 0),
    ACACIA(Text.translatable("magiaborras.wandwood.acacia"), 1),
    ALDER(Text.translatable("magiaborras.wandwood.alder"), 1),
    APPLE(Text.translatable("magiaborras.wandwood.apple"), 1),
    ASH(Text.translatable("magiaborras.wandwood.ash"), 1),
    ASPEN(Text.translatable("magiaborras.wandwood.aspen"), 1),
    BEECH(Text.translatable("magiaborras.wandwood.beech"), 1),
    BIRCH(Text.translatable("magiaborras.wandwood.birch"), 1),
    BLACKTHORN(Text.translatable("magiaborras.wandwood.blackthorn"), 1),
    BLACK_WALNUT(Text.translatable("magiaborras.wandwood.black_walnut"), 1),
    BRAZILWOOD(Text.translatable("magiaborras.wandwood.brazilwood"), 1),
    CEDAR(Text.translatable("magiaborras.wandwood.cedar"), 1),
    CHERRY(Text.translatable("magiaborras.wandwood.cherry"), 1),
    CHESTNUT(Text.translatable("magiaborras.wandwood.chestnut"), 1),
    CYPRESS(Text.translatable("magiaborras.wandwood.cypress"), 1),
    DOGWOOD(Text.translatable("magiaborras.wandwood.dogwood"), 1),
    EBONY(Text.translatable("magiaborras.wandwood.ebody"), 1),
    ELDER(Text.translatable("magiaborras.wandwood.elder"), 1),
    ELM(Text.translatable("magiaborras.wandwood.elm"), 1),
    ENGLISH_OAK(Text.translatable("magiaborras.wandwood.english_oak"), 1),
    FIR(Text.translatable("magiaborras.wandwood.fir"), 1),
    HAWTHORN(Text.translatable("magiaborras.wandwood.hawthorn"), 1),
    HAZEL(Text.translatable("magiaborras.wandwood.hazel"), 1),
    HOLLY(Text.translatable("magiaborras.wandwood.holly"), 1),
    HORNBEAM(Text.translatable("magiaborras.wandwood.hornbeam"), 1),
    IVY(Text.translatable("magiaborras.wandwood.ivy"), 1),
    LARCH(Text.translatable("magiaborras.wandwood.larch"), 1),
    LAUREL(Text.translatable("magiaborras.wandwood.laurel"), 1),
    MAHOGANY(Text.translatable("magiaborras.wandwood.mahogany"), 1),
    MAPLE(Text.translatable("magiaborras.wandwood.maple"), 1),
    OLIVE(Text.translatable("magiaborras.wandwood.olive"), 1),
    PEAR(Text.translatable("magiaborras.wandwood.pear"), 1),
    PINE(Text.translatable("magiaborras.wandwood.pine"), 1),
    POPLAR(Text.translatable("magiaborras.wandwood.poplar"), 1),
    PRICKLY_ASH(Text.translatable("magiaborras.wandwood.prickly_ash"), 1),
    RED_OAK(Text.translatable("magiaborras.wandwood.red_oak"), 1),
    REDWOOD(Text.translatable("magiaborras.wandwood.redwood"), 1),
    REED(Text.translatable("magiaborras.wandwood.reed"), 1),
    ROSEWOOD(Text.translatable("magiaborras.wandwood.rosewood"), 1),
    ROWAN(Text.translatable("magiaborras.wandwood.rowan"), 1),
    SILVER_LIME(Text.translatable("magiaborras.wandwood.silver_line"), 1),
    SNAKEWOOD(Text.translatable("magiaborras.wandwood.snakewood"), 1),
    SPRUCE(Text.translatable("magiaborras.wandwood.spruce"), 1),
    SUGAR_MAPLE(Text.translatable("magiaborras.wandwood.sugar_maple"), 1),
    SWAMP_MAYHAW(Text.translatable("magiaborras.wandwood.swamp_mayhaw"), 1),
    SYCAMORE(Text.translatable("magiaborras.wandwood.sycamore"), 1),
    TAMARACK(Text.translatable("magiaborras.wandwood.tamarack"), 1),
    VINE(Text.translatable("magiaborras.wandwood.vine"), 1),
    WALNUT(Text.translatable("magiaborras.wandwood.walnut"), 1),
    WILLOW(Text.translatable("magiaborras.wandwood.willow"), 1),
    YEW(Text.translatable("magiaborras.wandwood.yew"), 1);

    private static ArrayList<Integer> cores = null;

    private final Text name;
    private final int chances;

    WandWood(Text name, int chances) {
        this.name = name;
        this.chances = chances;
    }

    public Text getName() {
        return name;
    }

    public static WandWood getRandom(Random rng){
        if (cores == null){
            cores = new ArrayList<>(38);
            for (WandWood core : values()){
                for (int i = 0; i < core.chances; i++) {
                    cores.add(core.ordinal());
                }
            }
        }
        return values()[cores.get(rng.nextInt(cores.size()))];
    }
}
