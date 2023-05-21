package io.joshinn.mythicplaceholders;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyScope;

public enum Scope implements PropertyScope {
    DATA("data");

    private final String scope;
    private Scope(String scope){this.scope = scope;}

    @Override
    public String get() {
        return scope;
    }
}
