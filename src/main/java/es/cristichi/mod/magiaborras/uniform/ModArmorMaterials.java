package es.cristichi.mod.magiaborras.uniform;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModArmorMaterials {
    public static final RegistryEntry<ArmorMaterial> UNIFORM_MATERIAL = registerMaterial(
            "school",
            // Defense (protection) point values for each armor piece.
            Map.of(
                    ArmorItem.Type.HELMET, 2,
                    ArmorItem.Type.CHESTPLATE, 3,
                    ArmorItem.Type.LEGGINGS, 4,
                    ArmorItem.Type.BOOTS, 2,
                    ArmorItem.Type.BODY, 4
            ),
            // Enchantability. For reference, leather has 15, iron has 9, and diamond has 10.
            15,
            // The sound played when the armor is equipped.
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            // The ingredient(s) used to repair the armor.
            () -> Ingredient.ofItems(MagiaBorras.MOONSTONE_ITEM),
            0.0F,
            0.0F,
            // If it's dyable.
            false,
            // Layers
            null
    );

    // Within the ModArmorMaterials class
    public static void initialize() {}

    public static RegistryEntry<ArmorMaterial> registerMaterial(String id, Map<ArmorItem.Type, Integer> defensePoints,
                int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredientSupplier,
                float toughness, float knockbackResistance, boolean dyeable, @Nullable List<ArmorMaterial.Layer> layers) {
        // Get the supported layers for the armor material
        if (layers == null){
            layers = List.of(
                    // The ID of the texture layer, the suffix, and whether the layer is dyeable.
                    // We can just pass the armor material ID as the texture layer ID.
                    // We have no need for a suffix, so we'll pass an empty string.
                    // We'll pass the dyeable boolean we received as the dyeable parameter.
                    new ArmorMaterial.Layer(Identifier.of(MagiaBorras.MOD_ID, id), "", dyeable)
            );
        }

        ArmorMaterial material = new ArmorMaterial(defensePoints, enchantability, equipSound, repairIngredientSupplier,
                layers, toughness, knockbackResistance);
        // Register the material within the ArmorMaterials registry.
        material = Registry.register(Registries.ARMOR_MATERIAL, Identifier.of(MagiaBorras.MOD_ID, id), material);

        // The majority of the time, you'll want the RegistryEntry of the material - especially for the ArmorItem constructor.
        return RegistryEntry.of(material);
    }
}
