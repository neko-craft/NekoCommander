package cn.apisium.nekocommander.completer;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Completer {
    @Nullable
    List<String> complete(final @NotNull CommandSender sender, final @NotNull String[] args);
}
