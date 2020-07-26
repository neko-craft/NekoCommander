package cn.apisium.nekocommander;

import joptsimple.OptionParser;

public interface ArgumentsProcessor {
    void processArguments(final OptionParser parser);
}
