package cn.apisium.nekocommander.impl;

import cn.apisium.nekocommander.BaseCommand;
import cn.apisium.nekocommander.CommandRecord;
import cn.apisium.nekocommander.Commander;
import cn.apisium.nekocommander.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public final class BukkitCommander extends Commander<PluginCommand, CommandSender> implements CommandExecutor, TabCompleter {
    private final Plugin plugin;
    private final static Constructor<PluginCommand> pluginCommandConstructor;
    private final static SimpleCommandMap commandMap;

    static {
        if (Utils.IS_BUKKIT) try {
            pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            final Server server = Bukkit.getServer();
            final Field field = server.getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (SimpleCommandMap) field.get(server);
        } catch (final Exception e) {
            Utils.throwSneaky(e);
            throw new RuntimeException();
        } else {
            pluginCommandConstructor = null;
            commandMap = null;
        }
    }

    @SuppressWarnings("unused")
    public BukkitCommander(@NotNull final Plugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public Commander<PluginCommand, CommandSender> registerCommand(@NotNull final BaseCommand...commands) {
        for (final BaseCommand command : commands) {
            final CommandRecord record = new CommandRecord(command);
            if (record.names.isEmpty()) throw new RuntimeException("A command without name!");
            record.names.forEach(name -> {
                PluginCommand cmd = plugin.getServer().getPluginCommand(name);
                if (cmd == null) {
                    if (!isUnsafeRegister()) throw new RuntimeException("No such command registered: " + name);
                    try {
                        cmd = pluginCommandConstructor.newInstance(name, plugin);
                        commandMap.register(plugin.getDescription().getName(), cmd);
                        plugin.getLogger().warning("Registered command Unsafely: " + name);
                    } catch (final Exception e) {
                        Utils.throwSneaky(e);
                        throw new RuntimeException();
                    }
                }
                if (getDefaultDescription() != null) cmd.setDescription(getDefaultDescription());
                if (getDefaultPermissionMessage() != null) cmd.setPermissionMessage(getDefaultPermissionMessage());
                if (getDefaultUsage() != null) cmd.setUsage(getDefaultUsage());
                cmd.setExecutor(this);
                cmd.setTabCompleter(this);
                for (final BiConsumer<BaseCommand, PluginCommand> it : getProcessors()) it.accept(command, cmd);
                getCommands().put(name, record);
            });
        }
        return this;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        return onCommand(sender, command.getName(), label, args);
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
        return onTabComplete(sender, command.getName(), alias, args);
    }
}
