package org.keycloak.theme;

import org.keycloak.Config;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
    import org.keycloak.models.RealmModel;

import java.util.function.Function;

public class DefaultThemeSelectorProvider implements ThemeSelectorProvider {

    public static final String LOGIN_THEME_KEY = "login_theme";
    public static final String EMAIL_THEME_KEY = "email_theme";

    private final KeycloakSession session;

    public DefaultThemeSelectorProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public String getThemeName(Theme.Type type) {
        String name = null;

        switch (type) {
            case WELCOME:
                name = Config.scope("theme").get("welcomeTheme");
                break;
            case LOGIN:
                name = getThemeNameFromClient(LOGIN_THEME_KEY, RealmModel::getLoginTheme);
                break;
            case ACCOUNT:
                name = session.getContext().getRealm().getAccountTheme();
                break;
            case EMAIL:
                name = getThemeNameFromClient(EMAIL_THEME_KEY, RealmModel::getEmailTheme);
                break;
            case ADMIN:
                name = session.getContext().getRealm().getAdminTheme();
                break;
        }

        if (name == null || name.isEmpty()) {
            name = getDefaultThemeName(type);
        }

        return name;
    }

    @Override
    public void close() {
    }

    private String getThemeNameFromClient(String themeKey, Function<RealmModel, String> fallback) {
        ClientModel client = session.getContext().getClient();
        String name = null;
        if (client != null) {
            name = client.getAttribute(themeKey);
        }

        if (name != null && !name.trim().isEmpty()) {
            return name;
        }

        return fallback.apply(session.getContext().getRealm());
    }

}
