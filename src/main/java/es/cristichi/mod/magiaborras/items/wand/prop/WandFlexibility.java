package es.cristichi.mod.magiaborras.items.wand.prop;

import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;

public enum WandFlexibility {
    NONE(Text.translatable("magiaborras.wandflex.none"), 0),
    UNYIELDING(Text.translatable("magiaborras.wandflex.unyielding"), 1),
    RATHER_BENDY(Text.translatable("magiaborras.wandflex.rather_bendy"), 2),
    SUPPLE(Text.translatable("magiaborras.wandflex.supple"), 2),
    ELASTIC(Text.translatable("magiaborras.wandflex.elastic"), 1);

    private static ArrayList<Integer> flexes = null;

    private final Text name;
    private final int chances;

    WandFlexibility(Text name, int chances) {
        this.name = name;
        this.chances = chances;
    }

    public Text getName() {
        return name;
    }

    public static WandFlexibility getRandom(Random rng){
        if (flexes == null){
            flexes = new ArrayList<>(6);
            for (WandFlexibility flex : values()){
                for (int i = 0; i < flex.chances; i++) {
                    flexes.add(flex.ordinal());
                }
            }
        }
        return values()[flexes.get(rng.nextInt(flexes.size()))];
    }
}
