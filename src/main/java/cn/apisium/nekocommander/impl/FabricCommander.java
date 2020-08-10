package cn.apisium.nekocommander.impl;

import cn.apisium.nekocommander.BaseCommand;
import cn.apisium.nekocommander.CommandRecord;
import cn.apisium.nekocommander.Commander;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public final class FabricCommander extends Commander<LiteralCommandNode<ServerCommandSource>, ServerCommandSource> implements Command<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {
    public static Object SERVER;
    private final HashMap<String, String> map = new HashMap<>();
    public FabricCommander() {
        ServerStartCallback.EVENT.register(it -> SERVER = it);
    }

    @Override
    public @NotNull Commander<LiteralCommandNode<ServerCommandSource>, ServerCommandSource> registerCommand(@NotNull final BaseCommand... commands) {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            for (final BaseCommand command : commands) {
                final CommandRecord record = new CommandRecord(command);
                if (record.names.isEmpty()) throw new RuntimeException("A command without name!");
                record.names.forEach(name -> {
                    final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal(name).executes(this)
                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("args", StringArgumentType.greedyString()).suggests(this).executes(this)));
                    for (final BiConsumer<BaseCommand, LiteralCommandNode<ServerCommandSource>> it : getProcessors()) it.accept(command, node);
                    getCommands().put(name, record);
                });
            }
        });
        return this;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        final Record record = new Record(context);
        return record.args == null ? 0 : runCommand(context.getSource(), record.name, record.label, record.args) ? 1 : 0;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        builder = builder.createOffset(builder.getInput().lastIndexOf(' ') + 1);
        final Record record = new Record(context);
        if (record.args != null) {
            final List<String> list = complete(context.getSource(), record.name, record.label, record.args);
            if (list != null && !list.isEmpty()) list.forEach(builder::suggest);
        }
        return builder.buildFuture();
    }

    private final class Record {
        String[] args = null;
        String name, label;
        Record(final CommandContext<ServerCommandSource> context) {
            String message = context.getInput();
            if (message.startsWith("/")) message = message.substring(1);
            int spaceIndex = message.indexOf(' ');
            label = message.substring(0, spaceIndex);
            final String[] cmd = label.split(":", 2);
            label = cmd[cmd.length - 1];
            name = map.get(label);
            if (name == null || !getCommands().containsKey(name)) return;
            args = message.substring(spaceIndex + 1).split(" ", -1);
        }
    }
}
