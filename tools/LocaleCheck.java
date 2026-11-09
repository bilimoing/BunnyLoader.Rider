import com.intellij.DynamicBundle;
import java.text.MessageFormat;
import java.util.*;

public class LocaleCheck {
    public static void main(String[] args) {
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        ClassLoader loader = LocaleCheck.class.getClassLoader();
        String base = "messages.BunnyBundle";
        ResourceBundle english = DynamicBundle.getResourceBundle(loader, base, Locale.ROOT);
        ResourceBundle chinese = DynamicBundle.getResourceBundle(loader, base, Locale.SIMPLIFIED_CHINESE);
        if (english.keySet().size() != 72 || !english.keySet().equals(chinese.keySet())) throw new AssertionError("Key mismatch");
        for (Locale locale : List.of(Locale.ROOT, Locale.ENGLISH, Locale.SIMPLIFIED_CHINESE, Locale.TRADITIONAL_CHINESE, Locale.FRENCH)) {
            ResourceBundle bundle = DynamicBundle.getResourceBundle(loader, base, locale);
            String expected = locale.getLanguage().equals("zh") ? "创建 Mod" : "Create Mod";
            if (!expected.equals(bundle.getString("button.create.mod"))) throw new AssertionError(locale + ": " + bundle.getString("button.create.mod"));
            for (String key : bundle.keySet()) {
                String formatted = new MessageFormat(bundle.getString(key), locale).format(new Object[]{"ARG0", "ARG1", "ARG2", "ARG3"});
                if (formatted.matches("(?s).*\\{[0-9]+}.*")) throw new AssertionError(key);
            }
            String error = MessageFormat.format(bundle.getString("error.dotnet.new"), "7", "stderr", "stdout");
            if (!error.contains("7") || !error.contains("stderr\nstdout") || (locale.getLanguage().equals("en") && !error.contains("Rider's PATH"))) throw new AssertionError(error);
            System.out.println("PASS " + (locale.equals(Locale.ROOT) ? "default" : locale.toLanguageTag()) + ": " + bundle.keySet().size() + " keys; " + expected);
        }
        if (!DynamicBundle.getLocale().equals(Locale.ENGLISH)) throw new AssertionError("IDE locale followed JVM default: " + DynamicBundle.getLocale());
        if (!dev.bunnyloader.rider.BunnyBundle.message("button.create.mod").equals("Create Mod")) throw new AssertionError("Helper default locale");
        System.out.println("PASS IDE English locale overrides Chinese JVM default via BunnyBundle");
    }
}
