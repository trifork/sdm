package dk.nsi.stamdata.replication.models;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.trifork.stamdata.persistence.Persistent;

public class AuthenticationModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<Object> views = Multibinder.newSetBinder(binder(), Object.class, Persistent.class);
        views.addBinding().to(Client.class);
    }
}
