package cn.apisium.nekocommander;

import cn.apisium.nekocommander.completer.BlocksCompleter;
import cn.apisium.nekocommander.completer.Completer;
import cn.apisium.nekocommander.completer.PlayersCompleter;
import cn.apisium.nekocommander.completer.WorldsCompleter;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MethodRecord extends Record implements Completer {
    private final Method method;
    private OptionParser parser;
    private boolean isOnlyPlayer;
    private final Object[] parameters;
    private final BaseCommand instance;
    private final HashMap<String, Object> completer = new HashMap<>();
    private final ArrayList<String> parameterList = new ArrayList<>();

    public MethodRecord(@Nullable final BaseCommand instance, @NotNull final Method method, final boolean onlyPlayer) {
        this.method = method;
        this.instance = instance;
        isOnlyPlayer = onlyPlayer || method.isAnnotationPresent(PlayerOnly.class);
        for (final Permission p : method.getAnnotationsByType(Permission.class)) permissions.add(p.value());
        for (final Argument arg : method.getAnnotationsByType(Argument.class)) addArgument(arg, null);
        int i = method.getParameterCount();
        parameters = new Object[i];
        final Parameter[] pars = method.getParameters();
        while (i-- > 0) {
            final Parameter par = pars[i];
            final Argument arg = par.getAnnotation(Argument.class);
            final Class<?> type = par.getType();
            if (type == ProxiedCommandSender.class) {
                parameters[i] = ProxiedCommandSender.class;
                continue;
            }
            if (Utils.SENDER_CLASS.isAssignableFrom(type)) {
                if (Utils.IS_BUKKIT && type == org.bukkit.entity.Player.class) isOnlyPlayer = true;
                parameters[i] = Utils.SENDER_CLASS;
                continue;
            }
            if (!Utils.IS_BUKKIT && type == net.minecraft.server.network.ServerPlayerEntity.class) {
                isOnlyPlayer = true;
                parameters[i] = net.minecraft.server.network.ServerPlayerEntity.class;
            }
            if (type == OptionSet.class) {
                if (parser == null) parser = new OptionParser();
                parameters[i] = OptionSet.class;
                continue;
            }
            if (arg == null) {
                if (type.isArray() && type.getComponentType() == String.class) parameters[i] = Arguments.class;
                else if (par.isNamePresent()) addArgument(new Argument() {
                    public @Override Class<? extends Annotation> annotationType() { return this.getClass(); }
                    public @Override String[] value() { return new String[] { par.getName() }; }
                    public @Override Class<?> type() { return type; }
                    public @Override String description() { return ""; }
                    public @Override String[] defaultValues() { return new String[0]; }
                    public @Override boolean required() { return false; }
                    public @Override Class<? extends Completer> completer() { return null; }
                    public @Override String[] completeValues() { return new String[0]; }
                }, type);
            } else {
                addArgument(arg, type);
                if (arg.value().length != 0) parameters[i] = arg.value()[0];
            }
            if (parameters[i] == null) {
                if (!par.isNamePresent()) throw new RuntimeException("Parameter name is not presented!");
                parameters[i] = par.getName();
            }
        }
        final WithArgumentsProcessor processor = method.getAnnotation(WithArgumentsProcessor.class);
        if (processor != null) {
            if (parser == null) parser = new OptionParser();
            try {
                processor.value().newInstance().processArguments(parser);
            } catch (Exception e) {
                Utils.throwSneaky(e);
                throw new RuntimeException();
            }
            parser.recognizedOptions().forEach((k, v) -> {
                final String key = k.length() == 1 ? "-" + k : "--" + k;
                if (!k.equals("[arguments]") && !parameterList.contains(key)) parameterList.add(key);
            });
        }
        if (parser != null) parser.allowsUnrecognizedOptions();
    }

    public boolean invoke(final @NotNull ProxiedCommandSender sender, final @NotNull String[] args) {
        if (isOnlyPlayer && !sender.isPlayer) return false;
        int i = parameters.length;
        final Object[] pars = new Object[i];
        final OptionSet set = parser == null ? null : parser.parse(args);
        while (i-- > 0) {
            final Object obj = parameters[i];
            if (obj == Utils.SENDER_CLASS) pars[i] = sender.origin;
            else if (obj == OptionSet.class) pars[i] = set;
            else if (obj == Arguments.class) pars[i] = args;
            else if (obj == ProxiedCommandSender.class) pars[i] = sender;
            else if (obj instanceof String) pars[i] = set == null ? null : set.valueOf((String) obj);
            else if (!Utils.IS_BUKKIT && obj == net.minecraft.server.network.ServerPlayerEntity.class) pars[i] = sender.entity;
        }
        try {
            final Object ret = method.invoke(instance, pars);
            return ret == null || Boolean.parseBoolean(ret.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addArgument(@NotNull final Argument arg, @Nullable final Class<?> type) {
        if (parser == null) parser = new OptionParser();
        final OptionSpecBuilder builder = parser.acceptsAll(Arrays.asList(arg.value()), arg.description());
        final Class<?> clazz = type != null && arg.type() == String.class ? type : arg.type();
        final ArgumentAcceptingOptionSpec a = (arg.required() ? builder.withRequiredArg() : builder.withOptionalArg()).ofType(clazz);
        final String[] defaultValues = arg.defaultValues();
        int i = defaultValues.length;
        if (i != 0) try {
            final Method method = clazz.getMethod("valueOf", String.class);
            final Object[] arr = new Object[i];
            while (i-- > 0) arr[i] = method.invoke(null, defaultValues[i]);
            a.defaultsTo(arr);
        } catch (final Exception e) {
            Utils.throwSneaky(e);
            throw new RuntimeException();
        }

        final Object comp;
        try {
            final Class<? extends Completer> clazz1 = arg.completer();
            if (clazz1 != Completer.class) comp = clazz1 == PlayersCompleter.class
                ? PlayersCompleter.INSTANCE : clazz1 == WorldsCompleter.class
                    ? WorldsCompleter.INSTANCE : clazz1 == BlocksCompleter.class
                        ? BlocksCompleter.INSTANCE : clazz1.newInstance();
            else if (arg.completeValues().length != 0) comp = Arrays.asList(arg.completeValues());
            else comp = null;
        } catch (Exception e) {
            Utils.throwSneaky(e);
            throw new RuntimeException();
        }
        builder.options().forEach(k -> {
            final String key = k.length() == 1 ? "-" + k : "--" + k;
            parameterList.add(key);
            if (comp != null) completer.put(key, comp);
        });
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public List<String> complete(final @NotNull ProxiedCommandSender sender, final @NotNull String[] args) {
        if (args.length < 2) return parameterList;
        final String arg = args[args.length - 2];
        if (!arg.startsWith("-")) return parameterList;
        final Object comp = completer.get(arg);
        if (comp instanceof Completer) return ((Completer) comp).complete(sender, args);
        else if (comp instanceof List) return (List<String>) comp;
        else return null;
    }
}
