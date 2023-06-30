package org.dnal.fieldcopy.bdd.core;

import java.util.Locale;

public enum SnippetType {
    CODEGEN,
    CONVERTER,
    VALUES,
    NOTHING;

    public static String getName(SnippetType type) {
        return type.name().toLowerCase(Locale.ROOT);
    }
}
