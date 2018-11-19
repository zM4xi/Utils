/*
 * Copyright (c) 2018 zM4xi
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.zm4xi.jnr.object.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Configuration {

    private LinkedHashMap<String, Object> defaultConfigurationData;
    private LinkedHashMap<String, Object> configurationData;

    private final FileType fileType;
    private File file;

    /**
     * Internal Constructor to setup and initialize the configuration
     *
     * @param type determines which configuration interpreter is used
     * @param path represents the path to the file as a {@link File}
     * @param name represents the name of the file as a {@link String}
     */
    private Configuration(FileType type, File path, String name) {
        this.fileType = type;
        this.file = new File(path.getAbsolutePath(), name + type.getExtension());
        initializeConfiguration();
    }

    /**
     * Internal Constructor to setup and initialize the configuration
     *
     * @param type determines which configuration interpreter is used
     * @param path represents the path to the file as a {@link String}
     * @param name represents the name of the file as a {@link String}
     */
    private Configuration(FileType type, String path, String name) {
        this.fileType = type;
        this.file = new File(new File(path), name + type.getExtension());
        initializeConfiguration();
    }

    /**
     * Used to output messages from the processing to the console
     *
     * @param level   {@link Level} of importance
     * @param message the message itself as a {@link String}
     */
    private static void log(Level level, String message) {
        LocalDateTime dateTime = LocalDateTime.now();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm / EEE / MM.dd.yyyy");
        String date = format.format(Date.from(dateTime.toInstant(ZoneOffset.UTC)));
        String out = "[" + date + "] " + level.getLocalizedName() + ": " + message;
        System.out.println(out);
    }

    /**
     * Initializes the configuration
     */
    private void initializeConfiguration() {
        configurationData = new LinkedHashMap<>();
        defaultConfigurationData = new LinkedHashMap<>();
        handleFile();
    }

    /**
     * Used to store data to the actual file
     */
    protected void writeData() {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(this.file));
        } catch (FileNotFoundException e) {
            log(Level.WARNING, "Could not find file! \n Error: " + e.getMessage());
        }

        try {
            switch (this.fileType) {
                case JSON:
                    writeJson(writer, new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create());
                    break;

                case YAML:
                    writeYaml(writer);
                    break;

                case PROPERTY:
                    writeProperty(writer);
                    break;

                default:
                    log(Level.WARNING, "Looks like something went wrong please consider stopping the programm!");
            }
        } catch (Exception e) {
            log(Level.WARNING, "Could not write data to file! \n Error: " + e.getMessage());
        }
    }

    /**
     * Given that the {@link FileType} is JSON this will save the data stored in the configuration in JSON format
     *
     * @param writer an {@link OutputStreamWriter} to write the data to the file
     * @param gson   the {@link Gson} converter to interpret the data
     * @throws IOException
     */
    private void writeJson(OutputStreamWriter writer, Gson gson) throws IOException {
        writer.write(gson.toJson(configurationData));
        writer.flush();
        writer.close();
    }

    /**
     * Given that the {@link FileType} is YAML this will save the data stored in the configuration in Yaml format
     *
     * @param writer an {@link OutputStreamWriter} to write the data to the file
     * @throws IOException
     */
    private void writeYaml(OutputStreamWriter writer) throws IOException {
        Yaml yaml = new Yaml();
        yaml.dump(configurationData, writer);
        writer.flush();
        writer.close();
    }

    /**
     * Given that the {@link FileType} is PROPERTY this will save the data stored in the configuration in Properties format
     *
     * @param writer an {@link OutputStreamWriter} to write the data to the file
     * @throws IOException
     */
    private void writeProperty(OutputStreamWriter writer) throws IOException {
        Properties properties = new Properties();
        properties.putAll(configurationData);
        properties.store(writer, null);
        writer.flush();
        writer.close();
    }

    /**
     * Used to load the data from the actual file
     */
    protected void loadData() {
        try {
            switch (this.fileType) {
                case JSON:

                    Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
                    loadJson(gson);

                    break;

                case YAML:
                    loadYaml();
                    break;

                case PROPERTY:
                    loadProperties();
                    break;

                default:
                    log(Level.WARNING, "Looks like something went wrong please consider stopping the programm!");
            }
        } catch (Exception e) {
            log(Level.WARNING, "Could not load data from file! \n Error: " + e.getMessage());
        }
    }

    /**
     * Given that the {@link FileType} is JSON this will load the data stored in the file in Json format
     *
     * @param gson the {@link Gson} converter to interpret the data
     * @throws FileNotFoundException
     */
    private void loadJson(Gson gson) throws FileNotFoundException {
        LinkedHashMap<?, ?> temp = gson.fromJson(new InputStreamReader(new FileInputStream(this.file)), LinkedHashMap.class);
        configurationData =  this.file.length() > 0 ? gson.fromJson(new InputStreamReader(new FileInputStream(this.file)), LinkedHashMap.class) : new LinkedHashMap<>();
        defaultConfigurationData.forEach( (k, v) -> configurationData.putIfAbsent(k, v));
    }

    /**
     * Given that the {@link FileType} is YAML this will load the data stored in the file in Yaml format
     *
     * @throws FileNotFoundException
     */
    private void loadYaml() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        configurationData = this.file.length() > 0 ? yaml.load(new FileInputStream(this.file)) : new LinkedHashMap<>();
        defaultConfigurationData.forEach( (k, v) -> configurationData.putIfAbsent(k, v));
    }

    /**
     * Given that the {@link FileType} is PROPERTY this will load the data stored in the file in Properties format
     *
     * @throws FileNotFoundException
     */
    private void loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(this.file));
        for (Map.Entry entry : properties.entrySet()) {
            configurationData.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        defaultConfigurationData.forEach( (k, v) -> configurationData.putIfAbsent(k, v));
    }

    /**
     * Used to create path to the file aswell as creating the file itself
     */
    private void handleFile() {
        try {
            if (this.file.getParentFile().mkdirs()) log(Level.INFO, "Successfully created Configurations Folder Path!");
            if (this.file.createNewFile()) log(Level.INFO, "Configuration File created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Json implements IConfigurable, IArrayConfigurable, ISaveable, IDefaultable {

        private Configuration configuration;

        /**
         * Initializing the configuration with the {@link FileType} JSON
         *
         * @param path represents the path to the file as a {@link File}
         * @param name represents the name of the file as a {@link String}
         */
        public Json(File path, String name) {
            configuration = new Configuration(FileType.JSON, path, name);
        }

        /**
         * Initializing the configuration with the {@link FileType} JSON
         *
         * @param path represents the path to the file as a {@link String}
         * @param name represents the name of the file as a {@link String}
         */
        public Json(String path, String name) {
            configuration = new Configuration(FileType.JSON, path, name);
        }

        /**
         * Adding / Setting an entry in the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param value a {@link Object} as the value
         */
        private void set(String key, Object value) {
            configuration.configurationData.put(key, value);
        }

        @Override
        public Set<String> getKeys() {
            return configuration.configurationData.keySet();
        }


        @Override
        public void clear() {
            configuration.configurationData = new LinkedHashMap<>();
            configuration.configurationData.clear();
        }

        @Override
        public boolean exists(String key) {
            return configuration.configurationData.containsKey(key);
        }

        @Override
        public void setInt(String key, int value) {
            set(key, value);
        }

        @Override
        public void setBoolean(String key, boolean value) {
            set(key, value);
        }

        @Override
        public void setDouble(String key, double value) {
            set(key, value);
        }

        @Override
        public void setFloat(String key, float value) {
            set(key, value);
        }

        @Override
        public void setString(String key, String value) {
            set(key, value);
        }

        @Override
        public void setDate(String key, Date value) {
            set(key, value);
        }

        @Override
        public void setLong(String key, long value) {
            set(key, value);
        }

        @Override
        public void setArray(String key, Object[] array) {
            set(key, array);
        }

        @Override
        public void setIntegerArray(String key, int[] array) {
            set(key, array);
        }

        @Override
        public void setDoubleArray(String key, double[] array) {
            set(key, array);
        }

        @Override
        public void setFloatArray(String key, float[] array) {
            set(key, array);
        }

        @Override
        public void setLongArray(String key, long[] array) {
            set(key, array);
        }

        @Override
        public void setStringArray(String key, String[] array) {
            set(key, array);
        }

        /**
         * Add/Set a {@link ArrayList} into the configuration
         *
         * @param key  a {@link String} to be used as identifier
         * @param list a {@link ArrayList} as value
         */
        public void setArrayList(String key, ArrayList<?> list) {
            set(key, list);
        }

        /**
         * Add/Set a {@link Map} into the configuration
         *
         * @param key a {@link String} to be used as identifier
         * @param map a {@link Map} as value
         */
        public void setMap(String key, Map<?, ?> map) {
            set(key, map);
        }

        /**
         * Add/Set a {@link Object} into the configuration
         *
         * @param key    a {@link String} to be used as identifier
         * @param object a {@link Object} as value
         */
        public void setClass(String key, Object object) {
            set(key, object);
        }

        @Override
        public int getInteger(String key) {
            return new Double(get(key, Double.class)).intValue();
        }

        @Override
        public double getDouble(String key) {
            return get(key, Double.class);
        }

        @Override
        public String getString(String key) {
            return get(key, String.class);
        }

        @Override
        public long getLong(String key) {
            return new Double(get(key, Double.class)).longValue();
        }

        @Override
        public float getFloat(String key) {
            return new Double(get(key, Double.class)).floatValue();
        }

        @Override
        public boolean getBoolean(String key) {
            return get(key, Boolean.class);
        }

        @Override
        public Date getDate(String key) {
            return get(key, Date.class);
        }

        @Override
        public void save() {
            configuration.writeData();
            log(Level.INFO, "Written Configuration Data to File! (Written to: " + configuration.file.getAbsolutePath() + ")");
        }

        @Override
        public void save(File newFile) {
            File clone = configuration.file;
            configuration.file = newFile;
            configuration.writeData();
            configuration.file = clone;
            log(Level.INFO, "Written Configuration Data to new File! (Written to: " + newFile.getAbsolutePath() + ")");
        }

        @Override
        public void load() {
            configuration.loadData();
        }

        @Override
        public void load(File newFile) {
            File clone = configuration.file;
            configuration.file = newFile;
            configuration.loadData();
            configuration.file = clone;
        }

        /**
         * Used to retrive data from the configuration
         *
         * @param key   a {@link String} as identifier
         * @param clazz to determine which data type the data has
         * @return a generic result with the type of the given class
         */
        public <T> T get(String key, Class<T> clazz) {
            return clazz.cast(configuration.configurationData.get(key));
        }

        @Override
        public boolean existsDefault(String key) { return configuration.defaultConfigurationData.containsKey(key); }

        @Override
        public void setDefault(String key, boolean value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, int value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, double value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, String value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, long value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, float value) { configuration.defaultConfigurationData.put(key, value); }
    }

    public static class YAML implements IConfigurable, IArrayConfigurable, ISaveable, IDefaultable {

        private Configuration configuration;

        /**
         * Initializing the configuration with the {@link FileType} YAML
         *
         * @param path represents the path to the file as a {@link File}
         * @param name represents the name of the file as a {@link String}
         */
        public YAML(File path, String name) {
            configuration = new Configuration(FileType.YAML, path, name);
        }

        /**
         * Initializing the configuration with the {@link FileType} YAML
         *
         * @param path represents the path to the file as a {@link File}
         * @param name represents the name of the file as a {@link String}
         */
        public YAML(String path, String name) {
            configuration = new Configuration(FileType.YAML, path, name);
        }

        /**
         * Adding / Setting an entry in the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param value a {@link Object} as the value
         */
        private void set(String key, Object value) {
            configuration.configurationData.put(key, value);
        }

        @Override
        public Set<String> getKeys() {
            return configuration.configurationData.keySet();
        }

        @Override
        public void clear() {
            configuration.configurationData = new LinkedHashMap<>();
            configuration.configurationData.clear();
        }

        @Override
        public boolean exists(String key) {
            return configuration.configurationData.containsKey(key);
        }

        @Override
        public void setInt(String key, int value) {
            set(key, value);
        }

        @Override
        public void setBoolean(String key, boolean value) {
            set(key, value);
        }

        @Override
        public void setDouble(String key, double value) {
            set(key, value);
        }

        @Override
        public void setFloat(String key, float value) {
            set(key, value);
        }

        @Override
        public void setString(String key, String value) {
            set(key, value);
        }

        @Override
        public void setDate(String key, Date value) {
            set(key, value);
        }

        @Override
        public void setLong(String key, long value) {
            set(key, value);
        }

        @Override
        public void setArray(String key, Object[] array) {
            set(key, array);
        }

        @Override
        public void setIntegerArray(String key, int[] array) {
            set(key, array);
        }

        @Override
        public void setDoubleArray(String key, double[] array) {
            set(key, array);
        }

        @Override
        public void setFloatArray(String key, float[] array) {
            set(key, array);
        }

        @Override
        public void setLongArray(String key, long[] array) {
            set(key, array);
        }

        @Override
        public void setStringArray(String key, String[] array) {
            set(key, array);
        }

        /**
         * Add/Set a {@link ArrayList} into the configuration
         *
         * @param key  a {@link String} to be used as identifier
         * @param list a {@link ArrayList} as value
         */
        public void setList(String key, List<?> list) {
            set(key, list);
        }

        /**
         * Add/Set a {@link Map} into the configuration
         *
         * @param key a {@link String} to be used as identifier
         * @param map a {@link Map} as value
         */
        public void setMap(String key, Map<?, ?> map) {
            set(key, map);
        }

        @Override
        public int getInteger(String key) {
            return new Double(get(key, Double.class)).intValue();
        }

        @Override
        public double getDouble(String key) {
            return get(key, Double.class);
        }

        @Override
        public String getString(String key) {
            return get(key, String.class);
        }

        @Override
        public long getLong(String key) {
            return new Double(get(key, Double.class)).longValue();
        }

        @Override
        public float getFloat(String key) {
            return new Double(get(key, Double.class)).floatValue();
        }

        @Override
        public boolean getBoolean(String key) {
            return get(key, Boolean.class);
        }

        @Override
        public Date getDate(String key) {
            return get(key, Date.class);
        }

        @Override
        public void save() {
            configuration.writeData();
            log(Level.INFO, "Written Configuration Data to File! (Written to: " + configuration.file.getAbsolutePath() + ")");
        }

        @Override
        public void save(File newFile) {
            File clone = configuration.file;
            configuration.file = newFile;
            configuration.writeData();
            configuration.file = clone;
            log(Level.INFO, "Written Configuration Data to new File! (Written to: " + newFile.getAbsolutePath() + ")");
        }

        @Override
        public void load() {
            configuration.loadData();
        }

        @Override
        public void load(File newFile) {
            File clone = configuration.file;
            configuration.file = newFile;
            configuration.loadData();
            configuration.file = clone;
        }

        /**
         * Used to retrive data from the configuration
         *
         * @param key   a {@link String} as identifier
         * @param clazz to determine which data type the data has
         * @return a generic result with the type of the given class
         */
        private <T> T get(String key, Class<T> clazz) {
            return clazz.cast(configuration.configurationData.get(key));
        }

        @Override
        public boolean existsDefault(String key) { return configuration.defaultConfigurationData.containsKey(key); }

        @Override
        public void setDefault(String key, boolean value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, int value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, double value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, String value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, long value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, float value) { configuration.defaultConfigurationData.put(key, value); }

    }

    public static class Property implements IConfigurable, ISaveableSimpleTypes, IDefaultable {

        private Configuration configuration;

        /**
         * Initializing the configuration with the {@link FileType} PROPERTY
         *
         * @param path represents the path to the file as a {@link File}
         * @param name represents the name of the file as a {@link String}
         */
        public Property(File path, String name) {
            configuration = new Configuration(FileType.PROPERTY, path, name);
        }

        /**
         * Initializing the configuration with the {@link FileType} PROPERTY
         *
         * @param path represents the path to the file as a {@link File}
         * @param name represents the name of the file as a {@link String}
         */
        public Property(String path, String name) {
            configuration = new Configuration(FileType.PROPERTY, path, name);
        }

        /**
         * Adding / Setting an entry in the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param value a {@link Object} as the value
         */
        private void set(String key, Object value) {
            configuration.configurationData.put(key, value);
        }

        @Override
        public Set<String> getKeys() {
            return configuration.configurationData.keySet();
        }

        @Override
        public void clear() {
            configuration.configurationData = new LinkedHashMap<>();
            configuration.configurationData.clear();
        }

        @Override
        public boolean exists(String key) {
            return configuration.configurationData.containsKey(key);
        }

        @Override
        public void setInt(String key, int value) {
            set(key, String.valueOf(value));
        }

        @Override
        public void setBoolean(String key, boolean value) {
            set(key, String.valueOf(value));
        }

        @Override
        public void setDouble(String key, double value) {
            set(key, String.valueOf(value));
        }

        @Override
        public void setFloat(String key, float value) {
            set(key, String.valueOf(value));
        }

        @Override
        public void setString(String key, String value) {
            set(key, value);
        }

        @Override
        public void setDate(String key, Date value) {
            set(key, String.valueOf(value));
        }

        @Override
        public void setLong(String key, long value) {
            set(key, String.valueOf(value));
        }

        @Override
        public int getInteger(String key) {
            return new Double(getDouble(key)).intValue();
        }

        @Override
        public double getDouble(String key) {
            return Double.parseDouble(get(key, String.class));
        }

        @Override
        public String getString(String key) {
            return get(key, String.class);
        }

        @Override
        public long getLong(String key) {
            return new Double(getDouble(key)).longValue();
        }

        @Override
        public float getFloat(String key) {
            return new Double(getDouble(key)).floatValue();
        }

        @Override
        public boolean getBoolean(String key) {
            return Boolean.parseBoolean(get(key, String.class));
        }

        @Override
        public void save() {
            configuration.writeData();
            log(Level.INFO, "Written Configuration Data to File! (Written to: " + configuration.file.getAbsolutePath() + ")");
        }

        @Override
        public void save(File newFile) {
            File clone = configuration.file;
            configuration.file = newFile;
            configuration.writeData();
            configuration.file = clone;
            log(Level.INFO, "Written Configuration Data to new File! (Written to: " + newFile.getAbsolutePath() + ")");
        }

        @Override
        public void load() {
            configuration.loadData();
        }

        @Override
        public void load(File newFile) {
            File clone = configuration.file;
            configuration.file = newFile;
            configuration.loadData();
            configuration.file = clone;
        }

        /**
         * Used to retrive data from the configuration
         *
         * @param key   a {@link String} as identifier
         * @param clazz to determine which data type the data has
         * @return a generic result with the type of the given class
         */
        public <T> T get(String key, Class<T> clazz) {
            return clazz.cast(configuration.configurationData.get(key));
        }

        @Override
        public boolean existsDefault(String key) { return configuration.defaultConfigurationData.containsKey(key); }

        @Override
        public void setDefault(String key, boolean value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, int value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, double value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, String value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, long value) { configuration.defaultConfigurationData.put(key, value); }

        @Override
        public void setDefault(String key, float value) { configuration.defaultConfigurationData.put(key, value); }

    }

    private interface IDefaultable {

        /**
         * Check if a default key already exists
         *
         * @param key a {@link String} as identifier
         * @return a {@link Boolean} true when key exists or false if not
         */
        boolean existsDefault(String key);

        /**
         * Add a new default value to the configuration
         *
         *
         * @param key a {@link String} as identifier
         * @param value a {@link Boolean} as value
         */
        void setDefault(String key, boolean value);
        /**
         * Add a new default value to the configuration
         *
         *
         * @param key a {@link String} as identifier
         * @param value a {@link Integer} as value
         */
        void setDefault(String key, int value);
        /**
         * Add a new default value to the configuration
         *
         *
         * @param key a {@link String} as identifier
         * @param value a {@link Double} as value
         */
        void setDefault(String key, double value);
        /**
         * Add a new default value to the configuration
         *
         *
         * @param key a {@link String} as identifier
         * @param value a {@link String} as value
         */
        void setDefault(String key, String value);
        /**
         * Add a new default value to the configuration
         *
         *
         * @param key a {@link String} as identifier
         * @param value a {@link Long} as value
         */
        void setDefault(String key, long value);
        /**
         * Add a new default value to the configuration
         *
         *
         * @param key a {@link String} as identifier
         * @param value a {@link Float} as value
         */
        void setDefault(String key, float value);

    }

    private interface ISaveableSimpleTypes {

        /**
         * Retrieve a {@link Integer} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link Integer}
         */
        int getInteger(String key);

        /**
         * Retrieve a {@link Double} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link Double}
         */
        double getDouble(String key);

        /**
         * Retrieve a {@link String} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link String}
         */
        String getString(String key);

        /**
         * Retrieve a {@link Long} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link Long}
         */
        long getLong(String key);

        /**
         * Retrieve a {@link Float} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link Float}
         */
        float getFloat(String key);

        /**
         * Retrieve a {@link Boolean} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link Boolean}
         */
        boolean getBoolean(String key);

        /**
         * Store the data from the configuration into the file
         */
        void save();

        /**
         * In case you want to store the data somewhere else
         * then the given file in the constructor
         * You can use this method to do so
         *
         * @param newFile the file to store the data to
         */
        void save(File newFile);

        /**
         * Load the data from the file into the configuration
         */
        void load();

        /**
         * In case you want load the data from somewhere else
         * then the fiven file in the constructor
         * You can use this method to do so
         *
         * @param newFile the file to load the data from
         */
        void load(File newFile);
    }

    private interface ISaveable {

        /**
         * Retrieve a {@link Integer} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link Integer}
         */
        int getInteger(String key);

        /**
         * Retrieve a {@link Double} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link Double}
         */
        double getDouble(String key);

        /**
         * Retrieve a {@link String} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link String}
         */
        String getString(String key);

        /**
         * Retrieve a {@link Long} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link Long}
         */
        long getLong(String key);

        /**
         * Retrieve a {@link Float} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link Float}
         */
        float getFloat(String key);

        /**
         * Retrieve a {@link Boolean} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link Boolean}
         */
        boolean getBoolean(String key);

        /**
         * Retrieve a {@link Date} from the configuration
         *
         * @param key a {@link String} as identifier
         * @return the in the configuratio stored {@link Date}
         */
        Date getDate(String key);

        /**
         * Store the data from the configuration into the file
         */
        void save();

        /**
         * In case you want to store the data somewhere else
         * then the given file in the constructor
         * You can use this method to do so
         *
         * @param newFile the file to store the data to
         */
        void save(File newFile);

        /**
         * Load the data from the file into the configuration
         */
        void load();

        /**
         * In case you want load the data from somewhere else
         * then the fiven file in the constructor
         * You can use this method to do so
         *
         * @param newFile the file to load the data from
         */
        void load(File newFile);
    }

    private interface IArrayConfigurable {

        /**
         * Add/Set a {@link Object[]} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param array a {@link Object[]} as value
         */
        void setArray(String key, Object[] array);

        /**
         * Add/Set a {@link Integer[]} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param array a {@link Integer[]} as value
         */
        void setIntegerArray(String key, int[] array);

        /**
         * Add/Set a {@link Double[]} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param array a {@link Double[]} as value
         */
        void setDoubleArray(String key, double[] array);

        /**
         * Add/Set a {@link Float[]} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param array a {@link Float[]} as value
         */
        void setFloatArray(String key, float[] array);

        /**
         * Add/Set a {@link Long[]} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param array a {@link Long[]} as value
         */
        void setLongArray(String key, long[] array);

        /**
         * Add/Set a {@link String[]} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param array a {@link String[]} as value
         */
        void setStringArray(String key, String[] array);
    }

    private interface IConfigurable {

        /**
         * Get all keys in the configuration
         *
         * @return a {@link Set<String>} of all available keys
         */
        Set<String> getKeys();

        /**
         * Used to empty/delete the configuration
         */
        void clear();

        /**
         * Checks if a key is already used in the configuration
         *
         * @param key a {@link String} to be used as identifier
         * @return true if the key already exists and false if it doesn't
         */
        boolean exists(String key);

        /**
         * Add/Set a {@link Integer} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param value a {@link Integer} as value
         */
        void setInt(String key, int value);

        /**
         * Add/Set a {@link Boolean} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param value a {@link Boolean} as value
         */
        void setBoolean(String key, boolean value);

        /**
         * Add/Set a {@link Double} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param value a {@link Double} as value
         */
        void setDouble(String key, double value);

        /**
         * Add/Set a {@link Float} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param value a {@link Float} as value
         */
        void setFloat(String key, float value);

        /**
         * Add/Set a {@link String} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param value a {@link String} as value
         */
        void setString(String key, String value);

        /**
         * Add/Set a {@link Date} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param value a {@link Date} as value
         */
        void setDate(String key, Date value);

        /**
         * Add/Set a {@link Long} into the configuration
         *
         * @param key   a {@link String} to be used as identifier
         * @param value a {@link Long} as value
         */
        void setLong(String key, long value);
    }

    private enum FileType {
        JSON(".json"),
        YAML(".yml"),
        PROPERTY(".properties");

        private String extension;

        FileType(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }
    }
}
