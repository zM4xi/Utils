package de.idkwhoami.utils.permission;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public interface IPermissionHolder {

    @SerializedName("id")
    UUID id();

    @SerializedName("superuser")
    boolean isSuperuser();

    @SerializedName("permissions")
    List<String> permissions();
}
