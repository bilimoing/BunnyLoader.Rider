package dev.bunnyloader.rider;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/** Messages follow the IDE UI language selected by DynamicBundle. */
public final class BunnyBundle extends DynamicBundle {
    private static final String BUNDLE = "messages.BunnyBundle";
    private static final BunnyBundle INSTANCE = new BunnyBundle();

    private BunnyBundle() {
        super(BunnyBundle.class, BUNDLE);
    }

    public static @NotNull @Nls String message(
            @NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}
