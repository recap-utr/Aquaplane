package de.seanbri.aquaplane.assessment_approaches.debateIndexing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Utils {

    public static boolean isDirectoryEmpty(final Path directory) throws IOException {
        try (Stream<Path> walk = Files.walk(directory)) {
            List<Path> result = walk.filter(Files::isRegularFile).toList();

            return result.size() < 2;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
