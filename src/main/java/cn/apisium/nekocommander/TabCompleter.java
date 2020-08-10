package cn.apisium.nekocommander;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface TabCompleter {
    @Nullable
    List<String> onTabComplete(@NotNull final ProxiedCommandSender sender, @NotNull CommandRecord command, @NotNull final String label, @NotNull final String[] args, @Nullable final List<String> result);
}
