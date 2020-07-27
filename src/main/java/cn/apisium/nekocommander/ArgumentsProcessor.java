package cn.apisium.nekocommander;

import joptsimple.OptionParser;
import org.jetbrains.annotations.NotNull;

public interface ArgumentsProcessor {
    void processArguments(@NotNull final OptionParser parser);
}
