package de.idkwhoami.utils.permission;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Permitable {

    @SerializedName("required")
    protected String requiredPermission;
    private transient List<UUID> permitCache;

    public Permitable(String required) {
        requiredPermission = required;
        permitCache = new ArrayList<>();
    }

    public boolean hasAccess(IPermissionHolder holder) {
        boolean permitted = permitCache.contains(holder.id()) || holder.permissions().stream().anyMatch(permission -> permission.matches("\\$OVERWRITE\\$") || permission.matches(requiredPermission + ".*"));
        if (permitted)
            permitCache.add(holder.id());
        return permitted;
    }


}
