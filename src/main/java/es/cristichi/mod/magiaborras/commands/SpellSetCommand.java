package es.cristichi.mod.magiaborras.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.Spell;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class SpellSetCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final String label = "magia";

        dispatcher.register(CommandManager.literal(label)
                .requires(ServerCommandSource::isExecutedByPlayer)
                .then(CommandManager.argument("set", StringArgumentType.string())
                        .executes(context -> {
                            ServerPlayerEntity magicUser = context.getSource().getPlayerOrThrow();
                            ItemStack hand = magicUser.getStackInHand(Hand.MAIN_HAND);
                            WandProperties prop = WandProperties.check(hand);
                            if (prop != null) {
                                Spell spell = MagiaBorras.SPELLS.get("");
                                magicUser.sendMessage(Text.translatable("magiaborras.spell.changed_no_spell"));
                                prop.spell = spell;
                                prop.apply(hand);
                                return 1;
                            }
                            throw new SimpleCommandExceptionType(Text.translatable("magiaborras.change_spell.nowand")).create();
                        })
                        .suggests(new SpellSuggestionProvider())
                        .then(CommandManager.argument("spell", StringArgumentType.string())
                                .suggests(new SpellSetSuggestionProvider())
                                .executes(context -> {
                                    if (context.getSource().isExecutedByPlayer()) {
                                        ServerPlayerEntity magicUser = context.getSource().getPlayerOrThrow();
                                        ItemStack hand = magicUser.getStackInHand(Hand.MAIN_HAND);
                                        WandProperties prop = WandProperties.check(hand);
                                        if (prop != null) {
                                            Spell spell = MagiaBorras.SPELLS.get(StringArgumentType.getString(context, "spell"));
                                            if (spell == null) {
                                                magicUser.sendMessage(Text.translatable("magiaborras.spell.changed_wrong_spell", StringArgumentType.getString(context, "spell")));
                                            } else {
                                                magicUser.sendMessage(Text.translatable("magiaborras.spell.changed_spell", spell.getName()));
                                                prop.spell = spell;
                                                prop.apply(hand);
                                            }
                                            return 1;
                                        }
                                        throw new SimpleCommandExceptionType(Text.translatable("magiaborras.change_spell.nowand")).create();
                                    }
                                    return -1;
                                })))

        );
    }

}
