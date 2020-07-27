package cn.apisium.nekocommander;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;

public class Commander implements CommandExecutor, TabCompleter {
    private final Plugin plugin;
    private boolean unsafeRegister;
    private String defaultUsage, defaultPermissionMessage, defaultDescription;
    private final ArrayList<BiConsumer<BaseCommand, PluginCommand>> processors = new ArrayList<>();
    private final HashMap<String, CommandRecord> commands = new HashMap<>();
    private final static Constructor<PluginCommand> pluginCommandConstructor;
    private final static SimpleCommandMap commandMap;

    static {
        try {
            pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            final Server server = Bukkit.getServer();
            final Field field = server.getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (SimpleCommandMap) field.get(server);
        } catch (final Exception e) {
            throwSneaky(e);
            throw new RuntimeException();
        }
    }

    public Commander(@NotNull final Plugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander addCommandProcessor(@NotNull final BiConsumer<BaseCommand, PluginCommand> fn) {
        processors.add(fn);
        return this;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander unregisterCommand(@NotNull final BaseCommand command) {
        final CommandRecord record = new CommandRecord(command);
        if (record.names.isEmpty()) throw new RuntimeException("A command without name!");
        record.names.forEach(commands::remove);
        return this;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander unregisterCommand(@NotNull final String command) {
        commands.remove(command);
        return this;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander registerCommand(@NotNull final BaseCommand ...commands0) {
        for (final BaseCommand command : commands0) {
            final CommandRecord record = new CommandRecord(command);
            if (record.names.isEmpty()) throw new RuntimeException("A command without name!");
            record.names.forEach(name -> {
                PluginCommand cmd = plugin.getServer().getPluginCommand(name);
                if (cmd == null) {
                    if (!unsafeRegister) throw new RuntimeException("No such command registered: " + name);
                    try {
                        cmd = pluginCommandConstructor.newInstance(name, plugin);
                        commandMap.register(plugin.getDescription().getName(), cmd);
                        plugin.getLogger().warning("Registered command Unsafely: " + name);
                    } catch (final Exception e) {
                        throwSneaky(e);
                        throw new RuntimeException();
                    }
                }
                if (defaultDescription != null) cmd.setDescription(defaultDescription);
                if (defaultPermissionMessage != null) cmd.setPermissionMessage(defaultPermissionMessage);
                if (defaultUsage != null) cmd.setUsage(defaultUsage);
                cmd.setExecutor(this);
                for (final BiConsumer<BaseCommand, PluginCommand> it : processors) it.accept(command, cmd);
                commands.put(name, record);
            });
        }
        return this;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander setDefaultPermissionMessage(final @Nullable String defaultPermissionMessage) {
        this.defaultPermissionMessage = defaultPermissionMessage;
        return this;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander setDefaultUsage(final @Nullable String defaultUsage) {
        this.defaultUsage = defaultUsage;
        return this;
    }

    @SuppressWarnings("unused")
    @Nullable
    public String getDefaultPermissionMessage() {
        return defaultPermissionMessage;
    }

    @SuppressWarnings("unused")
    @Nullable
    public String getDefaultUsage() {
        return defaultUsage;
    }

    @SuppressWarnings("unused")
    @Nullable
    public String getDefaultDescription() {
        return defaultDescription;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander setDefaultDescription(final @Nullable String defaultDescription) {
        this.defaultDescription = defaultDescription;
        return this;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander setUnsafeRegister(boolean unsafeRegister) {
        this.unsafeRegister = unsafeRegister;
        return this;
    }

    @SuppressWarnings("unused")
    public boolean getUnsafeRegister() {
        return unsafeRegister;
    }

    @SuppressWarnings("unused")
    @NotNull
    public HashMap<String, CommandRecord> getCommands() {
        return commands;
    }

    @SuppressWarnings("unused")
    @NotNull
    public ArrayList<BiConsumer<BaseCommand, PluginCommand>> getProcessors() {
        return processors;
    }

    @Override
    public boolean onCommand(final CommandSender sender, org.bukkit.command.Command command, final String label, final String[] args) {
        CommandRecord record = commands.get(command.getName());
        if (record == null) return false;
        for (int i = 0, len = args.length; i < len; i++) {
            Record obj = record.commands.get(args[i]);
            if (obj instanceof CommandRecord) {
                record = (CommandRecord) obj;
                continue;
            }
            if (obj == null) obj = record.mainCallback;
            if (obj == null) return false;
            for (final String p : obj.permissions) if (!sender.hasPermission(p)) return false;
            if (obj instanceof MethodRecord) return ((MethodRecord) obj).invoke(sender, Arrays.copyOfRange(args, i, args.length));
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Throwable> void throwSneaky(final @NotNull Throwable exception) throws T {
        throw (T) exception;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        CommandRecord record = commands.get(command.getName());
        if (record == null) return null;
        for (int i = 0, len = args.length; i < len; i++) {
            Record obj = record.commands.get(args[i]);
            if (obj instanceof CommandRecord) {
                record = (CommandRecord) obj;
                continue;
            }
            if (obj == null) obj = record.mainCallback;
            if (obj == null) return null;
            for (final String p : obj.permissions) if (!sender.hasPermission(p)) return null;
            if (obj instanceof MethodRecord) return ((MethodRecord) obj).complete(sender, Arrays.copyOfRange(args, i, args.length));
        }
        return record.childCommands;
    }
}
