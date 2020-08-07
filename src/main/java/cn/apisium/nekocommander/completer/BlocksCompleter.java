package cn.apisium.nekocommander.completer;

import cn.apisium.nekocommander.ProxiedCommandSender;
import cn.apisium.nekocommander.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class BlocksCompleter implements Completer {
    private final static List<String> blocks = new ArrayList<>();
    public final static BlocksCompleter INSTANCE = new BlocksCompleter();

    static {
        if (Utils.IS_BUKKIT) for (final org.bukkit.Material type : org.bukkit.Material.values()) blocks.add(type.getKey().getKey());
        else net.minecraft.util.registry.Registry.BLOCK.getIds().forEach(it -> blocks.add(it.toString()));
    }

    @Override
    @NotNull
    public List<String> complete(final @NotNull ProxiedCommandSender sender, final @NotNull String[] args) {
        return blocks;
    }
}
