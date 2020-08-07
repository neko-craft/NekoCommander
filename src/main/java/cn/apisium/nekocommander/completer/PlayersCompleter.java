package cn.apisium.nekocommander.completer;

import cn.apisium.nekocommander.ProxiedCommandSender;
import cn.apisium.nekocommander.Utils;
import cn.apisium.nekocommander.impl.FabricCommander;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class PlayersCompleter implements Completer {
    public final static PlayersCompleter INSTANCE = new PlayersCompleter();
    @Override
    @Nullable
    public List<String> complete(final @NotNull ProxiedCommandSender sender, final  @NotNull String[] args) {
        if (Utils.IS_BUKKIT) {
            final Collection<? extends org.bukkit.entity.Player> players = org.bukkit.Bukkit.getOnlinePlayers();
            if (players.isEmpty()) return null;
            final ArrayList<String> list = new ArrayList<>(players.size());
            players.forEach(it -> list.add(it.getName()));
            return list;
        } else return FabricCommander.SERVER == null ? null
            : Arrays.asList(((net.minecraft.server.MinecraftServer) FabricCommander.SERVER).getPlayerNames());
    }
}
