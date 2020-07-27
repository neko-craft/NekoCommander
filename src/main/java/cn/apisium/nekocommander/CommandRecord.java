package cn.apisium.nekocommander;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;

public final class CommandRecord extends Record {
    public MethodRecord mainCallback;
    public final HashSet<String> names = new HashSet<>();
    public final HashMap<String, Record> commands = new HashMap<>();
    public final BaseCommand instance;
    @Nullable
    public final List<String> childCommands;
    public CommandRecord(@NotNull final BaseCommand cmd) {
        instance = cmd;
        final Class<?> clazz = cmd.getClass();
        final Command[] commands1 = clazz.getAnnotationsByType(Command.class);
        if (commands1.length == 0) {
            childCommands = null;
            return;
        }
        for (final Class<?> clazz1 : clazz.getClasses()) if (clazz1.isAssignableFrom(BaseCommand.class)) try {
            final CommandRecord record = new CommandRecord((BaseCommand) clazz1.getConstructors()[0].newInstance(cmd));
            record.names.forEach(it -> commands.put(it, record));
        } catch (Exception e) {
            Commander.throwSneaky(e);
            throw new RuntimeException();
        }
        for (final Command c : commands1) names.add(c.value());
        for (final Method method : clazz.getMethods()) {
            if (method.getAnnotation(MainCommand.class) != null) mainCallback = new MethodRecord(cmd, method);
            for (final Command c : method.getAnnotationsByType(Command.class))
                commands.put(c.value(), new MethodRecord(cmd, method));
        }
        childCommands = new ArrayList<>(commands.keySet());
        for (final Permission p : clazz.getAnnotationsByType(Permission.class)) permissions.add(p.value());
    }
}
