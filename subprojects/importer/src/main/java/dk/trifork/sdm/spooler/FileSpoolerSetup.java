package dk.trifork.sdm.spooler;

import dk.trifork.sdm.config.Configuration;
import dk.trifork.sdm.importer.FileImporter;
import org.apache.log4j.Logger;


public class FileSpoolerSetup {
    private static Logger logger = Logger.getLogger(FileSpoolerSetup.class);

    public static final String INPUT_DIR = "input";
    public static final String REJECT_DIR = "rejected";
    public static final String PROCESSING_DIR = "processing";
    public static final int DEFAULT_STABLE_SECONDS = 15;

    String name;
    String rootDir;
    String inputPath;
    String rejectPath;
    String processingPath;

    int stableSeconds = 0;

    private Class<? extends FileImporter> importerClass;

    public FileSpoolerSetup(String name, String rootURI) {
        this.name = name;
        this.rootDir = rootURI + "/" + name;
        initDirs();
    }

    public FileSpoolerSetup(String name, String rootURI, Class<? extends FileImporter> importerClass) {
        this(name, rootURI);
        this.importerClass = importerClass;
    }

    public String getName() {
        return name;
    }

    public String getInputPath() {
        return inputPath;
    }

    public String getRejectPath() {
        return rejectPath;
    }


	private void initDirs() {
        inputPath = rootDir + (rootDir.endsWith("/") ? "" : "/") + INPUT_DIR;
        rejectPath = rootDir + (rootDir.endsWith("/") ? "" : "/") + REJECT_DIR;
        processingPath = rootDir + (rootDir.endsWith("/") ? "" : "/") + PROCESSING_DIR;
    }

    int getStableSeconds() {
        if (stableSeconds == 0)
            stableSeconds = Integer.parseInt(getConfig("stableSeconds", "" + DEFAULT_STABLE_SECONDS));
        return stableSeconds;
    }

    String getProcessingPath() {
        return processingPath;
    }

    public Class<? extends FileImporter> getImporterClass() {
        if (importerClass == null) {
            resolveImporterClass(getConfig("importerClass", null));
        }
        return importerClass;
    }

    @SuppressWarnings("unchecked")
    private void resolveImporterClass(String importerName) {
        if (importerName == null) {
            logger.error("Configuration error. You need to configure an import class to spooler " + getName() +
                    ". Set property " + getConfigEntry("importerClass") + " to the class path of the importer");
        }
        try {
            Class<?> importer = Class.forName(importerName);
            importerClass = (Class<? extends FileImporter>) importer;
        } catch (ClassNotFoundException e) {
            logger.error("Configuration error. The configured import class (" + importerName + " to spooler " + getName() +
                    "could net be found. Set property " + getConfigEntry("importerClass") + " to a valid importer");
        } catch (ClassCastException e) {
            logger.error("Configuration error. The configured import class (" + importerName + " to spooler " + getName() +
                    "didn't implement interface " + FileImporter.class.getName() + ". Set property " + getConfigEntry("importerClass") + " to a valid importer");
        }


    }

    String getConfigEntry(String key) {
        return "spooler." + getName() + "." + key;
    }

    String getConfig(String key, String Default) {
        String value = Configuration.getString(getConfigEntry(key));
        if (value != null)
            return value;
        return Default;
    }

}
