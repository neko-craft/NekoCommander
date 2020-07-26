package cn.apisium.nekocommander;

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
import java.util.Arrays;

public class MethodRecord extends Record {
    private final Method method;
    private OptionParser parser;
    private boolean isOnlyPlayer;
    private final Object[] parameters;
    private final BaseCommand instance;
    public MethodRecord(@Nullable final BaseCommand instance, @NotNull final Method method) {
        this.method = method;
        this.instance = instance;
        final Permissions ps = method.getAnnotation(Permissions.class);
        if (ps != null) for (final Permission p : ps.value()) permissions.add(p.value());
        final Arguments args = method.getAnnotation(Arguments.class);
        if (args != null) for (final Argument arg : args.value()) addArgument(arg);
        int i = method.getParameterCount();
        parameters = new Object[i];
        final Parameter[] pars = method.getParameters();
        while (i-- > 0) {
            final Parameter par = pars[i];
            if (par.getType().isAssignableFrom(CommandSender.class)) {
                isOnlyPlayer = true;
                parameters[i] = CommandSender.class;
                continue;
            }
            if (par.getType().isAssignableFrom(OptionSet.class)) {
                parameters[i] = OptionSet.class;
                continue;
            }
            final Argument arg = par.getAnnotation(Argument.class);
            if (arg == null) parameters[i] = par.getName();
            else {
                addArgument(arg);
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

    private void addArgument(final Argument arg) {
        if (parser == null) parser = new OptionParser();
        final OptionSpecBuilder builder = parser.acceptsAll(Arrays.asList(arg.value()), arg.description());
        final ArgumentAcceptingOptionSpec<String> a = (arg.required() ? builder.withRequiredArg()
            : builder.withOptionalArg());
        if (arg.defaultValues().length != 0) a.defaultsTo(arg.defaultValues());
        a.ofType(arg.type());
    }
}
