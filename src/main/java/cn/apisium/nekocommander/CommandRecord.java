package cn.apisium.nekocommander;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

public final class CommandRecord extends Record {
    public MethodRecord mainCallback;
    public final HashSet<String> names = new HashSet<>();
    public final HashMap<String, Record> commands = new HashMap<>();
    public final BaseCommand instance;
    public CommandRecord(@NotNull final BaseCommand cmd) {
        instance = cmd;
        final Class<?> clazz = cmd.getClass();
        final Commands commands1 = clazz.getAnnotation(Commands.class);
        if (commands1 == null) return;
        for (final Class<?> clazz1 : clazz.getClasses()) if (clazz1.isAssignableFrom(BaseCommand.class)) try {
            final CommandRecord record = new CommandRecord((BaseCommand) clazz1.getConstructors()[0].newInstance(cmd));
            record.names.forEach(it -> commands.put(it, record));
        } catch (Exception e) {
            Commander.throwSneaky(e);
            throw new RuntimeException();
        }
        for (final Command c : commands1.value()) names.add(c.value());
        for (final Method method : clazz.getMethods()) {
            if (method.getAnnotation(MainCommand.class) != null) mainCallback = new MethodRecord(cmd, method);
            final Commands commands2 = method.getAnnotation(Commands.class);
            if (commands2 != null) for (final Command c : commands2.value()) commands.put(c.value(), new MethodRecord(cmd, method));
        }
        final Permissions ps = clazz.getAnnotation(Permissions.class);
        if (ps != null) for (final Permission p : ps.value()) permissions.add(p.value());
    }
}
