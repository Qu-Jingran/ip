package eli;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence and storage failures without touching the user's data file. */
public class EliStorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void missingDataFile_addAndReload_preservesTask() {
        Path dataFile = tempDirectory.resolve("tasks.dat");
        Eli firstRun = new Eli(dataFile);

        assertTrue(firstRun.getResponse("list").contains("task list is clear"));
        assertTrue(firstRun.getResponse("todo read notes").contains("Task captured!"));
        assertTrue(Files.exists(dataFile));

        Eli secondRun = new Eli(dataFile);
        assertTrue(secondRun.getResponse("list").contains("read notes"));
    }

    @Test
    public void corruptDataFile_warnsAndDoesNotOverwriteFile() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.dat");
        Files.writeString(dataFile, "not serialized task data");
        byte[] originalContent = Files.readAllBytes(dataFile);
        Eli eli = new Eli(dataFile);

        String firstResponse = eli.getResponse("list");
        String saveResponse = eli.getResponse("todo read notes");

        assertTrue(firstResponse.contains("could not read tasks.dat"));
        assertTrue(firstResponse.contains("task list is clear"));
        assertTrue(saveResponse.contains("cannot save until tasks.dat"));
        assertArrayEquals(originalContent, Files.readAllBytes(dataFile));
    }

    @Test
    public void unavailableParentFolder_returnsSaveError() {
        Path dataFile = tempDirectory.resolve("missing").resolve("tasks.dat");
        Eli eli = new Eli(dataFile);

        String response = eli.getResponse("todo read notes");

        assertTrue(response.contains("could not save your tasks"));
        assertFalse(Files.exists(dataFile));
    }
}
