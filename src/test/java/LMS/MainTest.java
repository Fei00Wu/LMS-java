package LMS;
// JUnit5

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.commons.util.StringUtils;

// Mockito
import javax.swing.*;

import static org.mockito.Mockito.*;

// Java standard
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.lang.*;
import java.util.GregorianCalendar;

public class MainTest {
    private Borrower borrower;
    private Book book;
    private Librarian librarian;
    private Clerk clerk;

    static private String pathToResources = "src/test/resources/";
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private uiUtils helper = new uiUtils(pathToResources, originalOut, outStream);

    @BeforeEach
    public void setup() {
        borrower = mock(Borrower.class);
        book = mock(Book.class);
        librarian = mock(Librarian.class);
        clerk = mock(Clerk.class);

        System.setOut(new PrintStream(outStream));
    }

    @AfterEach
    public void restore() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @DisplayName("mainMenu: Exit")
    @Test
    public void mainMenueQuit() {
        ByteArrayInputStream inStream = new ByteArrayInputStream("2\nk\n".getBytes());
        System.setIn(inStream);

        Main.main(null);

        String consoleOutput = outStream.toString();
        assertTrue(consoleOutput.contains("Exiting..."));
    }

    @DisplayName("mainMenu: invalid")
    @Test
    public void mainMenuInvalid() {
        ByteArrayInputStream inStream = new ByteArrayInputStream("4\n2\nq\n".getBytes());
        System.setIn(inStream);

        Main.main(null);

        String consoleOutput = outStream.toString();
        assertTrue(consoleOutput.contains("Invalid Input"));
    }

    @ParameterizedTest(name="mainMenu: Administrator login - login {0}")
    @CsvSource({
            "Pass, testMain/AdminLoginPass.txt",
            "Fail, testMain/AdminLoginFail.txt"
    })
    public void mainMenuAdminLoginPass(String status, String resourceFile) {
        String inputContent = helper.readFromResource(resourceFile);
        ByteArrayInputStream inStream = new ByteArrayInputStream(inputContent.getBytes());
        System.setIn(inStream);

        Main.main(null);

        String consoleOutput = outStream.toString();
        if (status.equals("Fail"))
            assertTrue(consoleOutput.contains("Wrong Password"), "Actual console output:\n"
                    + helper.cleanString(consoleOutput));
        else
            assertTrue(consoleOutput.contains("Welcome to Admin's Portal"),
                    "Actual console output:\n"
                            + helper.cleanString(consoleOutput));

    }



}
