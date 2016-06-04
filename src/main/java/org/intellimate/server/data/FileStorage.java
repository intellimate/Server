package org.intellimate.server.data;

import io.netty.buffer.ByteBuf;
import org.intellimate.server.BadRequestException;
import org.intellimate.server.InternalServerErrorException;
import org.intellimate.server.database.model.tables.records.AppDependencyRecord;
import org.intellimate.server.database.model.tables.records.AppInstanceRecord;
import org.intellimate.server.database.model.tables.records.AppRecord;
import org.intellimate.server.database.model.tables.records.UserRecord;
import org.intellimate.server.database.operations.AppOperations;
import ratpack.stream.TransformablePublisher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author LeanderK
 * @version 1.0
 */
public interface FileStorage {
    CompletableFuture<Long> save(TransformablePublisher<? extends ByteBuf> input, String name);
    CompletableFuture<Long> saveExact(TransformablePublisher<? extends ByteBuf> input, String name);
    void delete(String name);
    String getLink(String name);
    String getLinkForExactName(String name);
    File getFileForName(String name);

    default void processZipFile(String name, UserRecord userRecord, AppRecord appRecord, AppInstanceRecord appInstanceRecord, String version, AppOperations appOperations) throws BadRequestException {
        File file = getFileForName(name);
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry nextEntry = zis.getNextEntry();
            boolean found = false;
            while (nextEntry != null && !found) {
                if (nextEntry.getName().equals("/classes/META-INF/MANIFEST.MF")) {
                    Manifest manifest;
                    manifest = new Manifest(zis);
                    Attributes attributes = manifest.getMainAttributes();
                    checkZipFile(attributes, userRecord, appRecord, appInstanceRecord, version, appOperations);
                    setDependencies(attributes, appInstanceRecord, appOperations);
                }
            }
        } catch (FileNotFoundException e) {
            throw new InternalServerErrorException("unable to open app zip-file", e);
        } catch (IOException e) {
            throw new BadRequestException("unable to open app zip-file, this may be due to an broken zip-file", e);
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    default void checkZipFile(Attributes attrs, UserRecord user, AppRecord app, AppInstanceRecord instance, String version, AppOperations appOperations) throws BadRequestException {
        BiConsumer<Boolean, String> testAttribute = (valid, errorMessage) -> {
          if (!valid) {
              appOperations.setError(errorMessage, instance.getIdAppInstance());
              throw new BadRequestException(errorMessage);
          }
        };

        String id = attrs.getValue("Plugin-Id");
        testAttribute.accept(app.getPackage().equals(id), "Plugin-Id does not match the specified package for the app");

        String attributeVersion = attrs.getValue("Plugin-Version");
        testAttribute.accept(version.equals(attributeVersion), "Plugin-Version does not match targeted version for the app");

        String provider = attrs.getValue("Plugin-Provider");
        testAttribute.accept(user.getName().equals(provider), "Plugin-Provider does not match Username");

        String artifact = attrs.getValue("Artifact-ID");
        testAttribute.accept(app.getName().equals(artifact), "Artifact-ID does not match app-name");

        String serverIdRaw = attrs.getValue("Server-ID");
        int serverID;
        try {
            serverID = Integer.parseInt(serverIdRaw);
        } catch (NumberFormatException e) {
            String error = "Server-ID is not an Integer";
            appOperations.setError(error, instance.getIdAppInstance());
            throw new BadRequestException(error);
        }
        testAttribute.accept(app.getIdApp().equals(serverID), "Server-ID does not match the ID from the App");

        String sdk = attrs.getValue("SDK-Version");
        //TODO check if uploaded
    }

    default void setDependencies(Attributes attrs, AppInstanceRecord appInstanceRecord, AppOperations appOperations) {
        String dependencies = attrs.getValue("Plugin-Dependencies");
        // Make sure this is below SDKVersion!
        if (dependencies != null) {
            dependencies = dependencies.trim();
            if (!dependencies.isEmpty()) {
                Set<String> packages = Arrays.stream(dependencies.split(","))
                        .map(String::trim)
                        .filter(dependency -> !dependency.isEmpty())
                        .collect(Collectors.toSet());

                Map<String, Integer> dependencyMap = appOperations.getAppsForPackages(packages);

                List<AppDependencyRecord> recordList = dependencyMap.entrySet().stream()
                        .map(entry -> {
                            AppDependencyRecord dependencyRecord = new AppDependencyRecord();
                            dependencyRecord.setDependency(entry.getValue());
                            dependencyRecord.setSubject(appInstanceRecord.getIdAppInstance());
                            return dependencyRecord;
                        })
                        .collect(Collectors.toList());

                appOperations.setDependencies(recordList, appInstanceRecord.getIdAppInstance());

                String warnings = packages.stream()
                        .filter(dependency -> !dependencyMap.containsKey(dependency))
                        .map(rawPackage -> String.format("dependency %s could not be added (Not found)", rawPackage))
                        .collect(Collectors.joining(";"));

                appOperations.addWarning(warnings, appInstanceRecord.getIdAppInstance());
            }
        }
    }
}
