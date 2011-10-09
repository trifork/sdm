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
package dk.nsi.stamdata.testing;

import java.util.List;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;


public abstract class AbstractGuiceTestRunner extends BlockJUnit4ClassRunner
{
    private final Injector injector;


    /**
     * Creates a new GuiceTestRunner.
     * 
     * @param classToRun the test class to run
     * @param modules the Guice modules
     * @throws InitializationError if the test class is malformed
     */
    protected AbstractGuiceTestRunner(final Class<?> classToRun, Module... modules) throws InitializationError
    {
        super(classToRun);
        this.injector = Guice.createInjector(modules);
    }


    @Override
    public Object createTest()
    {
        return injector.getInstance(getTestClass().getJavaClass());
    }


    @Override
    protected void validateZeroArgConstructor(List<Throwable> errors)
    {
        // Guice can inject constructors with parameters so we don't want this
        // method to trigger an error
    }


    /**
     * Returns the Guice injector.
     * 
     * @return the Guice injector
     */
    protected Injector getInjector()
    {
        return injector;
    }
    
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier)
    {
        super.runChild(method, notifier);
    }
}
