package es.cristichi.mod.magiaborras.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.PlayerDataPS;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class SpellSetSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        if (context.getSource().isExecutedByPlayer()) {
            try {
                if (context.getSource().getPlayerOrThrow().isCreative()){
                    for (String id : MagiaBorras.SPELLS.keySet()){
                        builder.suggest(id);
                    }
                } else {
                    PlayerDataPS.PlayerMagicData data = MagiaBorras.playerDataPS.getOrGenerateData(context.getSource().getPlayerOrThrow());
                    for (String id : data.getUnlockedSpells()){
                        builder.suggest(id);
                    }
                }
            } catch (CommandSyntaxException e) {
            }
        }

        return builder.buildFuture();
    }
}