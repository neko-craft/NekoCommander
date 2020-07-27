package cn.apisium.nekocommander.completer;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class PlayersCompleter implements Completer {
    @Override
    @Nullable
    public List<String> complete(final @NotNull CommandSender sender, final  @NotNull String[] args) {
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) return null;
        final ArrayList<String> list = new ArrayList<>(players.size());
        players.forEach(it -> list.add(it.getName()));
        return list;
    }
}
