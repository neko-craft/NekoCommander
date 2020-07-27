package cn.apisium.nekocommander;

import cn.apisium.nekocommander.completer.Completer;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public MethodRecord(@Nullable final BaseCommand instance, @NotNull final Method method) {
        this.method = method;
        this.instance = instance;
        final Permissions ps = method.getAnnotation(Permissions.class);
        if (ps != null) for (final Permission p : ps.value()) permissions.add(p.value());
        final Arguments args = method.getAnnotation(Arguments.class);
        if (args != null) for (final Argument arg : args.value()) addArgument(arg, null);
        int i = method.getParameterCount();
        parameters = new Object[i];
        final Parameter[] pars = method.getParameters();
        while (i-- > 0) {
            final Parameter par = pars[i];
            final Argument arg = par.getAnnotation(Argument.class);
            final Class<?> type = par.getType();
            if (type.isAssignableFrom(CommandSender.class)) {
                isOnlyPlayer = true;
                parameters[i] = CommandSender.class;
                continue;
            }
            if (type.isAssignableFrom(OptionSet.class)) {
                if (parser != null) parser = new OptionParser();
                parameters[i] = OptionSet.class;
                continue;
            }
            if (arg == null) parameters[i] = type.isArray() && type == String.class ? Arguments.class : par.getName();
            else {
                addArgument(arg, type);
                parameters[i] = arg.value().length == 0 ? par.getName() : arg.value()[0];
            }
        }
        final WithArgumentsProcessor processor = method.getAnnotation(WithArgumentsProcessor.class);
        if (processor != null) {
            if (parser == null) parser = new OptionParser();
            try {
                processor.value().newInstance().processArguments(parser);
            } catch (Exception e) {
                Commander.throwSneaky(e);
                throw new RuntimeException();
            }
            parser.recognizedOptions().forEach((k, v) -> {
                final String key = k.length() == 1 ? "-" + k : "--" + k;
                if (!k.equals("[arguments]") && !parameterList.contains(key)) parameterList.add(key);
            });
        }
    }

    @SuppressWarnings("ConstantConditions")
    public boolean invoke(final @NotNull CommandSender sender, final String[] args) {
        if (isOnlyPlayer && !(sender instanceof Player)) return false;
        int i = parameters.length;
        final Object[] pars = new Object[i];
        final OptionSet set = parser == null ? null : parser.parse(args);
        while (i-- > 0) {
            final Object obj = parameters[i];
            if (obj == Player.class) pars[i] = sender;
            else if (obj == OptionSet.class) pars[i] = set;
            else if (obj == Arguments.class) pars[i] = args;
            else if (obj instanceof String) pars[i] = set.valueOf((String) obj);
        }
        try {
            final Object ret = method.invoke(instance, pars);
            return ret == null || Boolean.parseBoolean(ret.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addArgument(@NotNull final Argument arg, @Nullable final Class<?> type) {
        if (parser == null) parser = new OptionParser();
        final OptionSpecBuilder builder = parser.acceptsAll(Arrays.asList(arg.value()), arg.description());
        final ArgumentAcceptingOptionSpec<String> a = (arg.required() ? builder.withRequiredArg()
            : builder.withOptionalArg());
        if (arg.defaultValues().length != 0) a.defaultsTo(arg.defaultValues());
        final Class<?> clazz = type != null && arg.type() == String.class ? type : arg.type();
        a.ofType(clazz);
        final Object comp;
        try {
            if (arg.completer() != Completer.class) comp = arg.completer().newInstance();
            else if (arg.completeValues().length != 0) comp = Arrays.asList(arg.completeValues());
            else comp = null;
        } catch (Exception e) {
            Commander.throwSneaky(e);
            throw new RuntimeException();
        }
        if (comp != null) builder.options().forEach(k -> {
            final String key = k.length() == 1 ? "-" + k : "--" + k;
            parameterList.add(key);
            completer.put(key, comp);
        });
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public List<String> complete(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (args.length < 2) return parameterList;
        final String arg = args[args.length - 2];
        if (!arg.startsWith("-")) return parameterList;
        final Object comp = completer.get(arg);
        if (comp instanceof Completer) return ((Completer) comp).complete(sender, args);
        else if (comp instanceof ArrayList) return (ArrayList<String>) comp;
        else return null;
    }
}
