/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.importer.parsers;

import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Used to scope objects to a parser execution.
 *
 * Scopes a single execution of a block of code. Apply this scope with a
 * try/finally block:
 * <pre><code>
 *   scope.enter();
 *   try {
 *     // explicitly seed some seed objects...
 *     scope.seed(Key.get(SomeObject.class), someObject);
 *     // create and access scoped objects
 *   } finally {
 *     scope.exit();
 *   }
 * </code></pre>
 * The scope can be initialized with one or more seed values by calling
 * <code>seed(key, value)</code> before the injector will be called upon to
 * provide for this key. A typical use is for a servlet filter to enter/exit the
 * scope, representing a Request Scope, and seed HttpServletRequest and
 * HttpServletResponse.  For each key inserted with seed(), you must include a
 * corresponding binding:
 * <pre><code>
 *   bind(key)
 *       .toProvider(ParserScope.&lt;KeyClass&gt;seededKeyProvider())
 *       .in(ScopeAnnotation.class);
 * </code></pre>
 *
 * @author Jesse Wilson
 * @author Fedor Karpelevitch
 * @author Thomas Børlum
 */
public class ParserScope implements Scope
{
    private static final Provider<Object> SEEDED_KEY_PROVIDER =
            new Provider<Object>()
            {
                public Object get()
                {
                    throw new IllegalStateException("If you got here then it means that" +
                            " your code asked for scoped object which should have been" +
                            " explicitly seeded in this scope by calling" +
                            " ParserScope.seed(), but was not.");
                }
            };

    private final ThreadLocal<Map<Key<?>, Object>> values = new ThreadLocal<Map<Key<?>, Object>>();

    public void enter(ParserContext context)
    {
        checkState(values.get() == null, "A scoping block is already in progress.");
        values.set(Maps.<Key<?>, Object>newHashMap());
        
        seed(Key.get(ParserContext.class), context);
    }

    public void exit()
    {
        checkState(values.get() != null, "No parser execution is in progress.");
        values.remove();
    }

    public <T> void seed(Key<T> key, T value)
    {
        Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

        checkState(!scopedObjects.containsKey(key), "A value for the key %s was already seeded in this scope. Old value: %s New value: %s", key, scopedObjects.get(key), value);

        scopedObjects.put(key, value);
    }

    public <T> void seed(Class<T> clazz, T value)
    {
        seed(Key.get(clazz), value);
    }

    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped)
    {
        return new Provider<T>()
        {
            public T get()
            {
                Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

                @SuppressWarnings("unchecked")
                T current = (T) scopedObjects.get(key);

                if (current == null && !scopedObjects.containsKey(key))
                {
                    current = unscoped.get();
                    scopedObjects.put(key, current);
                }

                return current;
            }
        };
    }

    private <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key)
    {
        Map<Key<?>, Object> scopedObjects = values.get();

        if (scopedObjects == null)
        {
            throw new OutOfScopeException("Cannot access " + key + " outside of a parser execution scope.");
        }

        return scopedObjects;
    }

    /**
     * Returns a provider that always throws exception complaining that the object
     * in question must be seeded before it can be injected.
     *
     * @return typed provider
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Provider<T> seededKeyProvider()
    {
        return (Provider<T>) SEEDED_KEY_PROVIDER;
    }
}
