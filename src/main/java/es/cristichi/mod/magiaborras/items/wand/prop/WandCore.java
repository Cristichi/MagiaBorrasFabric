package es.cristichi.mod.magiaborras.items.wand.prop;

import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;

public enum WandCore {
    NONE(Text.translatable("magiaborras.wandcore.none"), 0),
    DRAGON_HEARTSTRING(Text.translatable("magiaborras.wandcore.dragon_heartstring"), 10),
    PHOENIX_FEATHER(Text.translatable("magiaborras.wandcore.phoenix_feather"), 4),
    UNICORN_TAIL_HAIR(Text.translatable("magiaborras.wandcore.unicorn_tail_hair"), 7),
    VEELA_HAIR(Text.translatable("magiaborras.wandcore.veela_hair"), 1),
    THESTRAL_TAIL_HAIR(Text.translatable("magiaborras.wandcore.threstral_tail"), 1),
    TROLL_WHISKER(Text.translatable("magiaborras.wandcore.troll_whisker"), 1),
    CORAL(Text.translatable("magiaborras.wandcore.coral"), 1),
    THUNDERBIRD_TAIL_FEATHER(Text.translatable("magiaborras.wandcore.thunderbird"), 1),
    WAMPUS_CAT_HAIR(Text.translatable("magiaborras.wandcore.wampus_cat"), 1),
    WHITE_RIVER_MONSTER_SPINE(Text.translatable("magiaborras.wandcore.white_river_monster"), 1),
    ROUGAROU_HAIR(Text.translatable("magiaborras.wandcore.rougarou"), 1),
    HORNET_SERPENT_HORN(Text.translatable("magiaborras.wandcore.hornet_serpent"), 1),
    SNALLYGASTER_HEARTSTRING(Text.translatable("magiaborras.wandcore.snallygaster"), 1),
    JACKALOPE_ANTLER(Text.translatable("magiaborras.wandcore.jackalope"), 1),
    KNEAZLE_WHISKER(Text.translatable("magiaborras.wandcore.kneazle"), 1),
    KELPIE_HAIR(Text.translatable("magiaborras.wandcore.kelpie"), 1),
    BASILISK_HORN(Text.translatable("magiaborras.wandcore.basilisk"), 1),
    CURUPIRA_HAIR(Text.translatable("magiaborras.wandcore.curpira"), 1),
    AFRICAN_MERMAID_HAIR(Text.translatable("magiaborras.wandcore.african_mermaid"), 1),
    FAIRY_WING(Text.translatable("magiaborras.wandcore.fairy_wing"), 1);

    private static ArrayList<Integer> cores = null;

    private final Text name;
    private final int chances;

    WandCore(Text name, int chances) {
        this.name = name;
        this.chances = chances;
    }

    public Text getName() {
        return name;
    }

    public static WandCore getRandomCore(Random rng){
        if (cores == null){
            cores = new ArrayList<>(38);
            for (WandCore core : values()){
                for (int i = 0; i < core.chances; i++) {
                    cores.add(core.ordinal());
                }
            }
        }
        return values()[cores.get(rng.nextInt(cores.size()))];
    }
}
