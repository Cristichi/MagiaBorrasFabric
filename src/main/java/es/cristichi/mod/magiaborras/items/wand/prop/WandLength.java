package es.cristichi.mod.magiaborras.items.wand.prop;

import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.Range;

import java.util.ArrayList;

public enum WandLength {
    NONE(Text.translatable("magiaborras.wandlength.none"), 1, 8, 0),
    SHORT(Text.translatable("magiaborras.wandlength.short"), 1, 8, 1),
    AVERAGE(Text.translatable("magiaborras.wandlength.average"), 8, 14, 2),
    LONG(Text.translatable("magiaborras.wandlength.long"), 15, Integer.MAX_VALUE, 1);

    private static ArrayList<Integer> lenghts = null;

    private final Text name;
    private final Range<Integer> range;
    private final int chances;

    WandLength(Text name, int minInches, int maxInches, int chances) {
        this.name = name;
        range = Range.of(minInches, maxInches);
        this.chances = chances;
    }

    public Text getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public Range<Integer> getRange() {
        return range;
    }

    public static WandLength getRandom(Random rng){
        if (lenghts == null){
            lenghts = new ArrayList<>(4);
            for (WandLength wandL : values()){
                for (int i = 0; i < wandL.chances; i++) {
                    lenghts.add(wandL.ordinal());
                }
            }
        }
        return values()[lenghts.get(rng.nextInt(lenghts.size()))];
    }
}
