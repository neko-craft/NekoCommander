package cn.apisium.nekocommander;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PreCommandExecutor {
    boolean onPreCommand(@NotNull final ProxiedCommandSender sender, @NotNull CommandRecord command, @NotNull final String label, @NotNull final String[] args);
}
