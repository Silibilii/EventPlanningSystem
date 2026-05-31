package ru.kafpin.krswrk.util;

import lombok.Getter;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Управляет локализацией приложения: текущей локалью и ресурсными пучками (ResourceBundle).
 * Сохраняет выбранный язык в системные настройки для сохранения между запусками.
 */
public class LocaleManager {
    private static final String PREFS_LANG_KEY = "language";
    private static final String PREFS_COUNTRY_KEY = "country";
    private static final Preferences prefs = Preferences.userNodeForPackage(LocaleManager.class);

    @Getter
    private static Locale currentLocale;
    @Getter
    private static ResourceBundle bundle;

    static {
        loadSavedLocale();
    }

    /**
     * Загружает сохранённую локаль из Preferences или использует русскую по умолчанию.
     */
    private static void loadSavedLocale() {
        String lang = prefs.get(PREFS_LANG_KEY, "en");
        String country = prefs.get(PREFS_COUNTRY_KEY, "");
        currentLocale = country.isEmpty() ? new Locale(lang) : new Locale(lang, country);
        bundle = ResourceBundle.getBundle("ru.kafpin.krswrk.i18n.messages", currentLocale);
    }

    /**
     * Устанавливает новую локаль, обновляет ResourceBundle и сохраняет выбор в Preferences.
     * @param locale новая локаль
     */
    public static void setLocale(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle("ru.kafpin.krswrk.i18n.messages", currentLocale);
        prefs.put(PREFS_LANG_KEY, locale.getLanguage());
        if (!locale.getCountry().isEmpty()) {
            prefs.put(PREFS_COUNTRY_KEY, locale.getCountry());
        } else {
            prefs.remove(PREFS_COUNTRY_KEY);
        }
    }
}