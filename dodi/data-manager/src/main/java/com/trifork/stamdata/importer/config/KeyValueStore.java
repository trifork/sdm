package com.trifork.stamdata.importer.config;

/**
 * A key value store for keeping state information.
 * 
 * A parser can use this for keeping sequence and versioning information.
 */
public interface KeyValueStore {

    /**
     * Gets the value for a key.
     * 
     * @param key
     *            the key of the value to look up.
     * @return the value for the key, possibly {@code null}.
     */
    String get(String key);

    /**
     * Sets the value for the given key.
     * 
     * @precondition The the value is non-empty.
     * 
     * @param key
     *            The key for which the value is stored.
     * @param value
     *            A non-empty string. If null any existing recorded value is
     *            deleted.
     */
    void set(String key, String value);
}
