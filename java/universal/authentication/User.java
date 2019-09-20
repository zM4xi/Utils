package de.idkwhoami.utils.authentication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import de.idkwhoami.utils.permission.IPermissionHolder;
import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.UUID;

public class User implements IPermissionHolder {

    @Getter
    private transient static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().disableInnerClassSerialization().create();

    private final UUID id;
    private boolean superuser;
    private List<String> permissions;

    /**
     * This constructor is thought of too load the data from the file.
     * That for you enter the file name which should be the corresponding {@link UUID}
     *
     * @param uuidString the file name of which file the data is loaded. Has to be a {@link UUID} formatted string
     */
    public User(String uuidString) {
        if (!uuidString.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
            this.id = null;
            return;
        }
        this.id = UUID.fromString(uuidString);
        try {
            load();
        } catch (IOException e) {
            System.err.println("Unable to load data from file.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public User(UUID id) {
        this(id, false, new ArrayList<>());
    }

    public User(UUID id, boolean superuser) {
        this(id, superuser, new ArrayList<>());
    }

    public User(UUID id, boolean superuser, List<String> permissions) {
        this.id = id;
        this.superuser = superuser;
        this.permissions = permissions;
        try {
            init();
        } catch (IOException e) {
            System.err.println("Unable to create file for user. Continuing without saving data!");
            e.printStackTrace();
        }
    }

    private void load() throws IOException {
        LinkedTreeMap map = GSON.fromJson(Files.newBufferedReader(Path.of(System.getProperty("user.dir"), "user", this.id.toString() + ".json"), StandardCharsets.UTF_8), LinkedTreeMap.class);
        this.superuser = (boolean) map.getOrDefault("superuser", false);
        this.permissions = (List<String>) map.getOrDefault("permissions", new ArrayList<>());
    }

    public void write() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(System.getProperty("user.dir"), "user", this.id.toString() + ".json"), StandardCharsets.UTF_8);
        GSON.toJson(this, writer);
        writer.flush();
        writer.close();
    }

    private void init() throws IOException {
        Files.createDirectories(Paths.get(System.getProperty("user.dir"), "user"));
        if (Files.notExists(Paths.get(System.getProperty("user.dir"), "user", this.id.toString() + ".json")))
            Files.createFile(Paths.get(System.getProperty("user.dir"), "user", id.toString() + ".json"));
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public boolean isSuperuser() {
        return this.superuser;
    }

    @Override
    public List<String> permissions() {
        return permissions;
    }

    public static User fromFile(File file) throws IOException {
        return GSON.fromJson(Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8), User.class);
    }

}
