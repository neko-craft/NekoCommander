package cn.apisium.nekocommander.completer;

import cn.apisium.nekocommander.ProxiedCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Completer {
    @Nullable
    List<String> complete(final @NotNull ProxiedCommandSender sender, final @NotNull String[] args);
}
