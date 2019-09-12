package de.idkwhoami.utils.other;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * This is a simple way saving all kind of data inside a node based structure.
 * Utilizing a simple string as key with '.' as node indicator.
 * <p>
 * However there are some requirements that your project must meet to be able to use this class
 * - Language Level: >8
 * - Gson Libary: https://github.com/google/gson
 * - Java 11
 */
public class Document {

    private LinkedHashMap<String, Object> data;
    private LinkedList<DocumentNode> nodes;
    public static transient Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().enableComplexMapKeySerialization().create();

    /**
     * Creates a new empty {@link Document} object with a default root node 'document' for internal data
     */
    public Document() {
        this(new LinkedHashMap<>(), new LinkedList<>());
    }

    /**
     * Creates a new empty {@link Document} object with a default root node 'document' for internal data
     *
     * @param data initial data for the root data layer
     */
    public Document(LinkedHashMap<String, Object> data) {
        this(data, new LinkedList<>());
    }

    /**
     * Creates a new empty {@link Document} object with a default root node 'document' for internal data
     *
     * @param data  initial data for the root data layer
     * @param nodes initial root nodes or node trees
     */
    public Document(LinkedHashMap<String, Object> data, LinkedList<DocumentNode> nodes) {
        this.data = data;
        this.nodes = nodes;
        initDocument();
    }

    private void initDocument() {
        DocumentNode node = createRootNode("document");
        node.put("version", "0.0.1");
        node.put("author", "IDK_WHO_AM_I");
    }

    /**
     * @param key root data key or nodeKey separated by '.'
     * @return a {@link String} containing the stored data found at the given key
     */
    public String getString(String key) {
        return get(key, String.class);
    }

    /**
     * @param key root data key or nodeKey separated by '.'
     * @return a {@link Double} containing the stored data found at the given key
     */
    public Double getDouble(String key) {
        return get(key, Double.class);
    }

    /**
     * @param key root data key or nodeKey separated by '.'
     * @return a {@link Integer} containing the stored data found at the given key
     */
    public Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    /**
     * @param key root data key or nodeKey separated by '.'
     * @return a {@link Long} containing the stored data found at the given key
     */
    public Long getLong(String key) {
        return get(key, Long.class);
    }

    /**
     * @param key root data key or nodeKey separated by '.'
     * @return a {@link Float} containing the stored data found at the given key
     */
    public Float getFloat(String key) {
        return get(key, Float.class);
    }

    /**
     * @param key root data key or nodeKey separated by '.'
     * @return a {@link Boolean} containing the stored data found at the given key
     */
    public Boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    /**
     * @param key root data key or nodeKey separated by '.'
     * @return a {@link Object} containing the stored data found at the given key
     */
    public Object getObject(String key) {
        return get(key, Object.class);
    }

    /**
     * @param key root data key
     * @return true if the root data layer contains the given key
     */
    public boolean contains(String key) {
        return data.containsKey(key);
    }

    private <T> T get(String nodeKey, Class<T> clazz) {
        if (data.containsKey(nodeKey)) {
            return clazz.cast(data.get(nodeKey));
        } else {
            if (nodeKey.contains(".")) {
                return getNode(trimNodeKey(nodeKey)).get(trimParentKey(nodeKey), clazz);
            } else {
                return getRootNode(trimNodeKey(nodeKey)).get(trimParentKey(nodeKey), clazz);
            }
        }
    }

    /**
     * Insert a value into the root data layer or any node data layer
     *
     * @param key   root data key or nodeKey separated by '.'
     * @param value {@link T} the value to be inserted
     */
    public <T> void put(String key, T value) {
        if (key.contains(".")) {
            getNode(trimNodeKey(key)).put(trimParentKey(key), value);
        } else {
            data.put(key, value);
        }
    }

    /**
     * Delete any node or data identified by the given key
     *
     * @param nodeKey root data key or nodeKey separated by '.'
     */
    public void remove(String nodeKey) {
        if (nodeKey.contains(".")) {
            getNode(trimNodeKey(nodeKey)).remove(nodeKey);
        } else {
            data.remove(nodeKey);
        }
    }

    /* Document */

    /**
     * Trim the final nodeKey of the given nodeKey
     *
     * @param nodeKey a {@link String} containing a nodeKey separated by '.'
     * @return a {@link String} without the final nodeKey
     */
    public String trimNodeKey(String nodeKey) {
        return nodeKey.substring(0, nodeKey.lastIndexOf("."));
    }

    /**
     * Trim the parentKey of the given nodeKey
     *
     * @param nodeKey a {@link String} containing a nodekey spearated by '.'
     * @return a {@link String} without the parentKey
     */
    public String trimParentKey(String nodeKey) {
        return nodeKey.substring(nodeKey.lastIndexOf(".") + 1);
    }

    /**
     * @param nodeKey root nodeKey (contains no separator)
     * @return true if the root nodes contain the given nodeKey
     */
    public boolean existsRootNode(String nodeKey) {
        return nodes.stream().anyMatch(documentNode -> documentNode.getNodeKeyPath().equalsIgnoreCase(nodeKey));
    }

    /**
     * @param nodeKey root nodeKey (contains no separator)
     * @return {@link DocumentNode} identified by the given root nodeKey
     */
    public DocumentNode getRootNode(String nodeKey) {
        return nodes.stream().filter(documentNode -> documentNode.getNodeKey().equalsIgnoreCase(nodeKey)).findFirst().orElse(null);
    }

    /**
     * Creates a new {@link DocumentNode} on root layer
     *
     * @param nodeKey a {@link String} the node is identified by
     * @return the created {@link DocumentNode} after adding it to the node tree
     */
    private DocumentNode createRootNode(String nodeKey) {
        if (!existsRootNode(nodeKey)) {
            DocumentNode node = new DocumentNode(nodeKey);
            node.setNodeKeyPath(nodeKey);
            nodes.add(node);
            return node;
        }
        return null;
    }

    /* DocumentNode */


    /**
     * Retrieve the node identified by the given nodeKey
     *
     * @param nodeKey root data key or nodeKey separated by '.'
     * @return the created {@link DocumentNode} after adding it to the node tree
     */
    public DocumentNode getNode(String nodeKey) {
        String[] nodeKeys = nodeKey.split("\\.");
        if (nodeKey.contains(".")) {
            AtomicReference<DocumentNode> nodeRef = new AtomicReference<>(getRootNode(nodeKeys[0]));
            nodeRef.get().setNodeKeyPath(nodeKeys[0]);
            Stream.of(nodeKeys).skip(1).forEach(key -> {
                if (nodeRef.get().existsChildNode(key)) {
                    String path = nodeRef.get().getNodeKeyPath() + "." + key;
                    nodeRef.set(nodeRef.get().getChildNode(key));
                    nodeRef.get().setNodeKeyPath(path);
                }
            });
            return nodeRef.get().getNodeKeyPath().equalsIgnoreCase(nodeKey) ? nodeRef.get() : null;
        } else {
            return getRootNode(nodeKey);
        }
    }

    /**
     * Create a new {@link DocumentNode} anywhere in the node tree, without a separator this will create a root node instead
     * Should the nodeKey path contain nodeKey's that do not exists, the missing intervening nodes will be created
     *
     * @param parentKey a {@link String} the node is identified by
     * @return the created {@link DocumentNode} after adding it to the node tree
     */
    public DocumentNode createNode(String parentKey) {
        if (parentKey.contains(".")) {
            String[] nodeKeys = parentKey.split("\\.");
            createRootNode(nodeKeys[0]);
            AtomicReference<DocumentNode> nodeRef = new AtomicReference<>(getRootNode(nodeKeys[0]));
            nodeRef.get().setNodeKeyPath(nodeKeys[0]);
            Stream.of(nodeKeys).skip(1).forEach(key -> {
                String path = nodeRef.get().getNodeKeyPath() + "." + key;
                nodeRef.set(nodeRef.get().createChildNode(key));
                nodeRef.get().setNodeKeyPath(path);
            });
            return nodeRef.get();
        } else {
            return createRootNode(parentKey);
        }
    }

    /**
     * Serializes this {@link Document} object into JSON format
     *
     * @return a {@link String} containing the root data layer and the node tree
     */
    public String toJson() {
        return GSON.toJson(this, new TypeToken<Document>() {
        }.getType());
    }

    /**
     * Writes this {@link Document} object to a file using the given writer
     *
     * @param writer a {@link Appendable} writer to write the formatted JSON to
     * @throws IOException
     */
    public void writeJson(BufferedWriter writer) throws IOException {
        GSON.toJson(this, new TypeToken<Document>() {
        }.getType(), writer);
        writer.flush();
    }

    /**
     * Parses a JSON formatted {@link String} into a {@link Document} object
     *
     * @param json a {@link String} in JSON format
     * @return a {@link Document} containing the data given in the JSON {@link String}
     */
    public static Document fromJson(String json) {
        return GSON.fromJson(json, new TypeToken<Document>() {
        }.getType());
    }

    public static class DocumentNode {

        private String nodeKeyPath;

        private LinkedHashMap<String, Object> nodeData = new LinkedHashMap<>();
        private LinkedList<DocumentNode> childNodes = new LinkedList<>();
        private String nodeKey;

        /**
         * @param nodeKey  a {@link String} the node is identified by
         * @param children initial child nodes
         */
        public DocumentNode(String nodeKey, DocumentNode... children) {
            this.nodeKey = nodeKey;
            childNodes.addAll(Arrays.asList(children));
        }

        /**
         * @param nodeKey  a {@link String} the node is identified by
         * @param nodeData initial node data as {@link LinkedHashMap}
         */
        public DocumentNode(String nodeKey, LinkedHashMap<String, Object> nodeData) {
            this.nodeData = nodeData;
            this.nodeKey = nodeKey;
        }

        /**
         * Create a new {@link DocumentNode} as child node of this node
         *
         * @param nodeKey  a {@Link String} to identify the node
         * @param nodeData initial node data
         * @return the created {@link DocumentNode} after adding it to the node tree
         */
        public DocumentNode createChildNode(String nodeKey, Map.Entry<String, Object>... nodeData) {
            DocumentNode node = new DocumentNode(nodeKey, Maps.newLinkedHashMap());
            node.getNodeData().entrySet().addAll(Arrays.asList(nodeData));
            childNodes.add(node);
            return node;
        }

        /**
         * Delete a child node or a entry in from the data
         *
         * @param key a {@link String} to identify the the entry/node
         */
        public void remove(String key) {
            if (key.contains(".")) {
                childNodes.removeIf(documentNode -> documentNode.getNodeKeyPath().equalsIgnoreCase(key));
            } else {
                nodeData.remove(key);
            }
        }

        /**
         * @param nodeKey a {@link String} the node is identified by
         * @return true if the node was found as child node
         */
        public boolean existsChildNode(String nodeKey) {
            return childNodes.stream().anyMatch(documentNode -> documentNode.getNodeKey().equalsIgnoreCase(nodeKey));
        }

        /**
         * @param nodeKey a {@link String} the node is identified by
         * @return a {@link DocumentNode} identified by the given nodeKey
         */
        public DocumentNode getChildNode(String nodeKey) {
            return childNodes.stream().filter(documentNode -> documentNode.getNodeKey().equalsIgnoreCase(nodeKey)).findFirst().orElse(null);
        }

        /**
         * Insert a value into the data layer of this {@link DocumentNode}
         *
         * @param key   a {@link String} as data key
         * @param value {@link T} the value to be inserted
         */
        public <T> void put(String key, T value) {
            nodeData.put(key, value);
        }

        /**
         * Retireves data identified by the given key and then returned as the given type
         *
         * @param key   a {@link String} as data key
         * @param clazz a {@link Class<T>} to cast the result to
         * @return the to {@link T} casted value
         */
        public <T> T get(String key, Class<T> clazz) {
            return clazz.cast(nodeData.get(key));
        }

        public String getNodeKeyPath() {
            return nodeKeyPath;
        }

        public void setNodeKeyPath(String nodeKeyPath) {
            this.nodeKeyPath = nodeKeyPath;
        }

        public LinkedHashMap<String, Object> getNodeData() {
            return nodeData;
        }

        public String getNodeKey() {
            return nodeKey;
        }
    }

}
