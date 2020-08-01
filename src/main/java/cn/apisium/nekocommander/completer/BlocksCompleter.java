package cn.apisium.nekocommander.completer;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class BlocksCompleter implements Completer {
    private final static List<String> blocks = new ArrayList<>(Material.values().length);
    public final static BlocksCompleter INSTANCE = new BlocksCompleter();

    static {
        for (final Material type : Material.values()) blocks.add(type.getKey().getKey());
    }

    @Override
    @NotNull
    public List<String> complete(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return blocks;
    }
}
