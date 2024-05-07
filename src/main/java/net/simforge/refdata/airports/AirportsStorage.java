package net.simforge.refdata.airports;

import net.simforge.commons.io.IOHelper;
import net.simforge.commons.legacy.misc.Settings;
import net.simforge.refdata.airports.boundary.Boundary;
import net.simforge.refdata.airports.boundary.BoundaryType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import java.util.Properties;

public class AirportsStorage {
    public static Optional<Boundary> loadBoundary(final String icao) throws IOException {
        final File boundaryFile = getBoundaryFile(icao);
        if (!boundaryFile.exists()) {
            return Optional.empty();
        }

        final String content = IOHelper.loadFile(boundaryFile);
        return Optional.of(loadBoundaryFromProperties(content));
    }

    public static void saveBoundary(final String icao, final Properties asProperties) throws IOException {
        final File boundaryFile = getBoundaryFile(icao);
        //noinspection ResultOfMethodCallIgnored
        boundaryFile.getParentFile().mkdirs();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        asProperties.store(baos, null);
        IOHelper.saveFile(boundaryFile, baos.toString());
    }

    private static File getBoundaryFile(String icao) {
        final String storageRootStr = Settings.get("airports.storage.root");
        final String defaultStorageRootStr = "~/simforge/airports-storage";
        final File boundaryFolder = new File(storageRootStr != null ? storageRootStr : defaultStorageRootStr, "boundaries");

        final String filename = icao.toUpperCase() + ".properties";

        return new File(boundaryFolder, filename);
    }

    public static Boundary loadBoundaryFromProperties(final String content) {
        final Properties properties = loadProperties(content);

        return Boundary.fromProperties(
                BoundaryType.valueOf(properties.getProperty("type")),
                properties.getProperty("data"));
    }

    private static Properties loadProperties(String content) {
        final Properties properties = new Properties();
        try {
            properties.load(new StringReader(content));
        } catch (IOException ignored) {
        }
        return properties;
    }
}
