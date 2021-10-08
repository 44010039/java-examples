package net.java.nio2.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NumberOfLineFinderUnitTest  {
    private static final String INPUT_FILE_NAME = "src/main/resources/input.txt";
    private static final int ACTUAL_LINE_COUNT = 45;

    @Test
    public void whenUsingBufferedReader_thenReturnTotalNumberOfLines() {
        int lines = NumberOfLineFinder.getTotalNumberOfLinesUsingBufferedReader(INPUT_FILE_NAME);
        assertEquals(ACTUAL_LINE_COUNT, lines);
    }

    @Test
    public void whenUsingLineNumberReader_thenReturnTotalNumberOfLines() {
        int lines = NumberOfLineFinder.getTotalNumberOfLinesUsingLineNumberReader(INPUT_FILE_NAME);
        assertEquals(ACTUAL_LINE_COUNT, lines);
    }

    @Test
    public void whenUsingScanner_thenReturnTotalNumberOfLines() {
        int lines = NumberOfLineFinder.getTotalNumberOfLinesUsingScanner(INPUT_FILE_NAME);
        assertEquals(ACTUAL_LINE_COUNT, lines);
    }

    @Test
    public void whenUsingNIOFiles_thenReturnTotalNumberOfLines() {
        int lines = NumberOfLineFinder.getTotalNumberOfLinesUsingNIOFiles(INPUT_FILE_NAME);
        assertEquals(ACTUAL_LINE_COUNT, lines);
    }

    @Test
    public void whenUsingNIOFilesReadAllLines_thenReturnTotalNumberOfLines() {
        int lines = NumberOfLineFinder.getTotalNumberOfLinesUsingNIOFilesReadAllLines(INPUT_FILE_NAME);
        assertEquals(ACTUAL_LINE_COUNT, lines);
    }

    @Test
    public void whenUsingNIOFileChannel_thenReturnTotalNumberOfLines() {
        int lines = NumberOfLineFinder.getTotalNumberOfLinesUsingNIOFileChannel(INPUT_FILE_NAME);
        assertEquals(ACTUAL_LINE_COUNT, lines);
    }

    @Test
    public void whenUsingApacheCommonsIO_thenReturnTotalNumberOfLines() {
        int lines = NumberOfLineFinder.getTotalNumberOfLinesUsingApacheCommonsIO(INPUT_FILE_NAME);
        assertEquals(ACTUAL_LINE_COUNT, lines);
    }

    @Test
    public void whenUsingGoogleGuava_thenReturnTotalNumberOfLines() {
        int lines = NumberOfLineFinder.getTotalNumberOfLinesUsingGoogleGuava(INPUT_FILE_NAME);
        assertEquals(ACTUAL_LINE_COUNT, lines);
    }   
}
