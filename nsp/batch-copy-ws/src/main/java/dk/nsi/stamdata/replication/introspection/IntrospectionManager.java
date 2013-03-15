package dk.nsi.stamdata.replication.introspection;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class IntrospectionManager {

    private IntrospectionConfig introspectionConfig;

    private TableIntrospector tableIntrospector;

    @Inject
    public IntrospectionManager(IntrospectionConfig introspectionConfig, TableIntrospector tableIntrospector) {
        this.introspectionConfig = introspectionConfig;
        this.tableIntrospector = tableIntrospector;
    }

}
