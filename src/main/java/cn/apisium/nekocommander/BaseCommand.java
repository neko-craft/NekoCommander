package cn.apisium.nekocommander;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BaseCommand {
    @Nullable
    default List<String> onTabComplete() { return null; }
}
