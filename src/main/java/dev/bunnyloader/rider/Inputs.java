/*
 * Decompiled with CFR 0.152.
 */
package dev.bunnyloader.rider;

import java.util.Set;

final class Inputs {
    private static final Set<String> KEYWORDS = Set.of("abstract", "as", "base", "bool", "break", "byte", "case", "catch", "char", "checked", "class", "const", "continue", "decimal", "default", "delegate", "do", "double", "else", "enum", "event", "explicit", "extern", "false", "finally", "fixed", "float", "for", "foreach", "goto", "if", "implicit", "in", "int", "interface", "internal", "is", "lock", "long", "namespace", "new", "null", "object", "operator", "out", "override", "params", "private", "protected", "public", "readonly", "ref", "return", "sbyte", "sealed", "short", "sizeof", "stackalloc", "static", "string", "struct", "switch", "this", "throw", "true", "try", "typeof", "uint", "ulong", "unchecked", "unsafe", "ushort", "using", "virtual", "void", "volatile", "while");

    private Inputs() {
    }

    static boolean isIdentifier(String string) {
        return string.matches("[A-Za-z_][A-Za-z0-9_]*") && !KEYWORDS.contains(string);
    }

    static boolean isNamespace(String string) {
        if (string.isBlank()) {
            return false;
        }
        for (String string2 : string.split("\\.", -1)) {
            if (Inputs.isIdentifier(string2)) continue;
            return false;
        }
        return true;
    }
}

