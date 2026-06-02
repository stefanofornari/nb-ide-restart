/*
 * Copyright 2025 Stefano Fornari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openide.util;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Test-specific implementation of NbPreferences.
 * This version uses in-memory storage and is not tied to the real NetBeans preferences.
 */
public class NbPreferences {
    private static final Map<Class<?>, Preferences> preferencesMap = new HashMap<>();

    /**
     * Get a preferences instance for the given module.
     *
     * @param module the module class
     * @return NbPreferences instance
     */
    public static Preferences forModule(final Class module) {
        return preferencesMap.computeIfAbsent(module, k -> new MemoryPreferences(null, ""));
    }

    private static class MemoryPreferences extends AbstractPreferences {
        private final Map<String, String> storage = new HashMap<>();

        MemoryPreferences(AbstractPreferences parent, String name) {
            super(parent, name);
        }

        @Override
        protected void putSpi(String key, String value) {
            storage.put(key, value);
        }

        @Override
        protected String getSpi(String key) {
            return storage.get(key);
        }

        @Override
        protected void removeSpi(String key) {
            storage.remove(key);
        }

        @Override
        protected void removeNodeSpi() throws BackingStoreException {
        }

        @Override
        protected String[] keysSpi() throws BackingStoreException {
            return storage.keySet().toArray(new String[0]);
        }

        @Override
        protected String[] childrenNamesSpi() throws BackingStoreException {
            return new String[0];
        }

        @Override
        protected AbstractPreferences childSpi(String name) {
            return new MemoryPreferences(this, name);
        }

        @Override
        protected void syncSpi() throws BackingStoreException {
        }

        @Override
        protected void flushSpi() throws BackingStoreException {
        }
    }

}