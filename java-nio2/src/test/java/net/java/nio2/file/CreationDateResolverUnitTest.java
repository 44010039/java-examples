package net.java.nio2.file;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class CreationDateResolverUnitTest {
    private final CreationDateResolver creationDateResolver = new CreationDateResolver();

    @Test
    public void givenFile_whenGettingCreationDateTimeFromBasicAttributes_thenReturnDate() throws Exception {

        final File file = File.createTempFile("createdFile", ".txt");
        final Path path = file.toPath();

        final Instant response = creationDateResolver.resolveCreationTimeWithBasicAttributes(path);

        assertTrue(Instant
          .now()
          .until(response, ChronoUnit.SECONDS) < 1);

    }

    @Test
    public void givenFile_whenGettingCreationDateTimeFromAttribute_thenReturnDate() throws Exception {

        final File file = File.createTempFile("createdFile", ".txt");
        final Path path = file.toPath();

        final Optional<Instant> response = creationDateResolver.resolveCreationTimeWithAttribute(path);

        response.ifPresent((value) -> {
            assertTrue(Instant
            .now()
            .until(value, ChronoUnit.SECONDS) < 1);
        });

    } 
}
