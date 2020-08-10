package cn.apisium.nekocommander;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class Commander <T, S> {
    private boolean unsafeRegister;
    private String defaultUsage, defaultPermissionMessage, defaultDescription;
    private final ArrayList<BiConsumer<BaseCommand, T>> processors = new ArrayList<>();
    private final HashMap<String, CommandRecord> commands = new HashMap<>();
    private final static List<String> EMPTY = Collections.emptyList();
    private CommandExecutor onCommand;
    private TabCompleter tabCompleter;
    private PreCommandExecutor preCommandExecutor;

    @SuppressWarnings("unused")
    @NotNull
    public Commander<T, S> addCommandProcessor(@NotNull final BiConsumer<BaseCommand, T> fn) {
        processors.add(fn);
        return this;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander<T, S> unregisterCommand(@NotNull final BaseCommand command) {
        final CommandRecord record = new CommandRecord(command);
        if (record.names.isEmpty()) throw new RuntimeException("A command without name!");
        record.names.forEach(commands::remove);
        return this;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander<T, S> unregisterCommand(@NotNull final String command) {
        commands.remove(command);
        return this;
    }

    @SuppressWarnings("unused")
    @NotNull
    public abstract Commander<T, S> registerCommand(@NotNull final BaseCommand...commands);

    @SuppressWarnings("unused")
    @NotNull
    public Commander<T, S> setDefaultPermissionMessage(final @Nullable String defaultPermissionMessage) {
        this.defaultPermissionMessage = defaultPermissionMessage;
        return this;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander<T, S> setDefaultUsage(final @Nullable String defaultUsage) {
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
    public Commander<T, S> setDefaultDescription(final @Nullable String defaultDescription) {
        this.defaultDescription = defaultDescription;
        return this;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Commander<T, S> setUnsafeRegister(boolean unsafeRegister) {
        this.unsafeRegister = unsafeRegister;
        return this;
    }

    @SuppressWarnings("unused")
    public boolean isUnsafeRegister() {
        return unsafeRegister;
    }

    @SuppressWarnings("unused")
    @NotNull
    public HashMap<String, CommandRecord> getCommands() {
        return commands;
    }

    @SuppressWarnings("unused")
    public void onPreCommand(@Nullable final PreCommandExecutor onCommand) {
        this.preCommandExecutor = onCommand;
    }

    @SuppressWarnings("unused")
    @Nullable
    public PreCommandExecutor getPreCommand() {
        return preCommandExecutor;
    }

    @SuppressWarnings("unused")
    public void onCommand(@Nullable final CommandExecutor onCommand) {
        this.onCommand = onCommand;
    }

    @SuppressWarnings("unused")
    @Nullable
    public CommandExecutor getOnCommand() {
        return onCommand;
    }

    @SuppressWarnings("unused")
    public void onTabComplete(@Nullable final TabCompleter tabCompleter) {
        this.tabCompleter = tabCompleter;
    }

    @SuppressWarnings("unused")
    @Nullable
    public TabCompleter getOnTabComplete() {
        return tabCompleter;
    }

    @SuppressWarnings("unused")
    @NotNull
    public ArrayList<BiConsumer<BaseCommand, T>> getProcessors() {
        return processors;
    }

    @SuppressWarnings("unused")
    public boolean runCommand(final S sender, String command, final String label, final String[] args) {
        CommandRecord record = commands.get(command);
        if (record == null) return false;
        final ProxiedCommandSender pcs = ProxiedCommandSender.newInstance(sender);
        if (preCommandExecutor != null && !preCommandExecutor.onPreCommand(pcs, record, command, args)) return true;
        MethodRecord method = record.mainCallback;
        int i = 0, len = args.length;
        boolean result = false;
        loop: for (; i < len; i++) {
            Record obj = record.commands.get(args[i]);
            if (obj instanceof CommandRecord) {
                record = (CommandRecord) obj;
                for (final String p : obj.permissions) if (!pcs.hasPermission(p)) {
                    if (defaultPermissionMessage != null) pcs.sendMessage(defaultPermissionMessage);
                    result = true;
                    break loop;
                }
                continue;
            }
            if (obj == null) obj = record.mainCallback;
            if (obj == null) break;
            for (final String p : obj.permissions) if (!pcs.hasPermission(p)) {
                if (defaultPermissionMessage != null) pcs.sendMessage(defaultPermissionMessage);
                result = true;
                break loop;
            }
            if (obj instanceof MethodRecord) {
                method = (MethodRecord) obj;
                break;
            }
        }
        if (method != null) result = method.invoke(pcs,
            i + 1 >= args.length ? new String[0] : Arrays.copyOfRange(args, i + 1, args.length));
        if (onCommand != null) result = onCommand.onCommand(pcs, record, label, args, result);
        return result;
    }

    @Nullable
    @SuppressWarnings("unused")
    public List<String> complete(@NotNull final S sender, @NotNull final String command, @NotNull final String alias, @NotNull final String[] args) {
        CommandRecord record = commands.get(command);
        if (record == null) return null;
        final ProxiedCommandSender pcs = ProxiedCommandSender.newInstance(sender);
        List<String> result = record.childCommands;
        loop : for (int i = 0, len = args.length; i < len; i++) {
            final Record obj = record.commands.get(args[i]);
            if (i == len - 1 && obj != null) break;
            if (obj == null) break;
            for (final String p : obj.permissions) if (!pcs.hasPermission(p)) {
                result = EMPTY;
                break loop;
            }
            if (obj instanceof CommandRecord) {
                record = (CommandRecord) obj;
                continue;
            }
            if (obj instanceof MethodRecord) {
                final List<String> ret = ((MethodRecord) obj).complete(pcs, Arrays.copyOfRange(args, Math.min(args.length - 1, i + 1), args.length));
                result = ret == null ? EMPTY : ret;
                break;
            }
        }
        if (tabCompleter != null) result = tabCompleter.onTabComplete(pcs, record, alias, args, result == EMPTY ? new ArrayList<>() : result);
        return result;
    }
}
