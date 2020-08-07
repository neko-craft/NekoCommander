package cn.apisium.nekocommander.completer;

import cn.apisium.nekocommander.ProxiedCommandSender;
import cn.apisium.nekocommander.Utils;
import cn.apisium.nekocommander.impl.FabricCommander;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class WorldsCompleter implements Completer {
    public final static WorldsCompleter INSTANCE = new WorldsCompleter();

    @Override
    @NotNull
    public List<String> complete(final @NotNull ProxiedCommandSender sender, final @NotNull String[] args) {
        final List<String> list = new ArrayList<>();
        if (Utils.IS_BUKKIT) org.bukkit.Bukkit.getWorlds().forEach(it -> list.add(it.getName()));
        else if (FabricCommander.SERVER != null) {
            ((net.minecraft.server.MinecraftServer) FabricCommander.SERVER).getWorldRegistryKeys()
                .forEach(it -> list.add(it.getValue().toString()));
        }
        return list;
    }
}
