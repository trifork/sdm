package com.trifork.stamdata.importer;

public static class ComponentModule extends ServletModule
    {
        @Override
        protected void configureServlets()
        {
            final CompositeConfiguration config = ConfigurationLoader.loadConfiguration();

            // Parse the properties from the configuration files.
            //
            final Set<OldParserContext> parsers = ParserConfiguration.getOldConfiguredParsers(config);
            final Set<ParserContext> newParsers = ParserConfiguration.getConfiguredParsers(config);

            // HACK: Because we are not using the ConfigurationLoader (yet!) we cannot easily bind properties
            // to named constants.
            //
            bindConstant().annotatedWith(Names.named("rootDir")).to(config.getString("rootDir"));
            bindConstant().annotatedWith(Names.named("file.stabilization.period")).to(config.getInt("file.stabilization.period"));

            // Bind the configured parser. (The old parser configurations will be phased out)
            //
            bind(new TypeLiteral<Set<OldParserContext>>() {}).toInstance(ImmutableSet.copyOf(parsers));
            bind(new TypeLiteral<Set<ParserContext>>() {}).toInstance(ImmutableSet.copyOf(newParsers));

            // Serve the status servlet.
            //
            bind(ComponentMonitor.class).to(DataManagerComponentMonitor.class);
            install(new MonitoringModule());

            serve("/").with(GUIServlet.class);

            // Bind the required dependencies.
            //
            bind(JobManager.class).in(Scopes.SINGLETON);
            bind(ParserScheduler.class).in(Scopes.SINGLETON);
            bind(DatabaseStatus.class).in(Scopes.SINGLETON);
        }
