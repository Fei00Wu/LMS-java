package LMS;

// JUnit5
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


// Java standard
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.lang.*;

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
        System.setOut(new PrintStream(outStream));
    }

    @AfterEach
    public void restore() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @DisplayName("mainMenu: Exit")
    @Test
    public void mainQuit() {
        ByteArrayInputStream inStream = new ByteArrayInputStream("2\nk\n".getBytes());
        System.setIn(inStream);

        Main.main(null);

        String consoleOutput = outStream.toString();
        assertTrue(consoleOutput.contains("Exiting..."));
    }

    @DisplayName("mainMenu: invalid")
    @Test
    public void mainInvalid() {
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
    public void mainAdminLogin(String status, String resourceFile) {
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

    @DisplayName("mian: Add clerk")
    @Test
    public void mainAddClerk() {
        String inputContent = helper.readFromResource("testMain/AdminAddClerk.txt");
        ByteArrayInputStream inStream = new ByteArrayInputStream(inputContent.getBytes());
        System.setIn(inStream);

        Main.main(null);

        ArrayList<Person> peopleInSystem = Library.getInstance().getPersons();
        boolean personFound = false;
        for(Person p : peopleInSystem) {
            if (p.getClass().getSimpleName().equals("Clerk")
                    && p.getName().equals("Aby")
                    && p.getID() == 1
                    && p.getPhoneNumber() == 123456789
                    && p.getAddress().equals("Homewood"))
            {
                personFound = true;
                break;
            }
        }


        assertTrue(personFound, outStream.toString());


    }



}
