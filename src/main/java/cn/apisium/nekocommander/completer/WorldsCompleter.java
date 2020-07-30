package cn.apisium.nekocommander.completer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class WorldsCompleter implements Completer {
    public final static WorldsCompleter INSTANCE = new WorldsCompleter();
    @Override
    @NotNull
    public List<String> complete(final @NotNull CommandSender sender, final @NotNull String[] args) {
        final List<World> list = Bukkit.getWorlds();
        final List<String> ret = new ArrayList<>(list.size());
        list.forEach(it -> ret.add(it.getName()));
        return ret;
    }
}
