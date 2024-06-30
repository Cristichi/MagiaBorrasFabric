package es.cristichi.mod.magiaborras.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class EasyList<E> extends ArrayList<E> {
    @SafeVarargs
    public EasyList(E... values) {
        super(values.length);
        this.addAll(Arrays.asList(values));
    }

    public boolean containsAny(@NotNull Collection<?> c) {
        for (E value : this){
            if (c.contains(value)){
                return true;
            }
        }
        return false;
    }
}
