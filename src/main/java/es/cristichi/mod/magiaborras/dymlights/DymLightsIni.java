package es.cristichi.mod.magiaborras.dymlights;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import es.cristichi.mod.magiaborras.items.wand.WandItem;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;

public class DymLightsIni implements DynamicLightsInitializer {
    @Override
    public void onInitializeDynamicLights(ItemLightSourceManager itemLightSourceManager) {
        DynamicLightHandlers.registerDynamicLightHandler(EntityType.PLAYER, entity -> {
            ItemStack potWand = entity.getInventory().getMainHandStack();
            WandProperties prop = WandProperties.check(potWand);
            if (prop != null)
                return prop.lumos?15:0;
            return 0;
        });

        DynamicLightHandlers.registerDynamicLightHandler(EntityType.ITEM, entity -> {
            if (entity.getStack().getItem() instanceof WandItem) {
                WandProperties prop = WandProperties.check(entity.getStack());
                if (prop != null){
                    return prop.lumos?15:0;
                }
            }
            return 0;
        });
    }
}
