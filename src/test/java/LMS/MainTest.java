package LMS;

// JUnit5
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


// Java standard
import java.io.*;
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
    public void mainInvalidChoice() {
        ByteArrayInputStream inStream = new ByteArrayInputStream("4\n2\nq\n".getBytes());
        System.setIn(inStream);

        Main.main(null);

        String consoleOutput = outStream.toString();
        assertTrue(consoleOutput.contains("Invalid Input"));
    }

    @ParameterizedTest(name="administrator: Administrator login - login {0}")
    @CsvSource({
            "Pass, testMain/AdminLoginPass.txt",
            "Fail, testMain/AdminLoginFail.txt"
    })
    public void AdminLogin(String status, String resourceFile) {
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

    @DisplayName("Admin menu test")
    @Test
    public void adminMenuTest() {
        String inputContent = helper.readFromResource("testMain/AdminMenu.txt");
        ByteArrayInputStream inStream = new ByteArrayInputStream(inputContent.getBytes());
        System.setIn(inStream);

        Main.main(null);
        String consoleOutput = helper.cleanString(outStream.toString());

        assertTrue(consoleOutput.contains("Librarian with name Leon created successfully."),
                "Add librarian failed. Console output: \n"
                        + consoleOutput);
        assertTrue(consoleOutput.contains("Clerk with name Aby created successfully."),
                "Add clerk failed. Console output: \n"
                        + consoleOutput);
        assertTrue(consoleOutput.contains("Currently, Library has no books."),
                "View all books failed. Console output: \n"
                        + consoleOutput);
        assertTrue(consoleOutput.contains("No issued books."),
                "View issued books failed. Console output: \n"
                        + consoleOutput);

    }

    @ParameterizedTest(name="main: User login: incorrect information")
    @CsvSource({
            "testMain/userLoginClerkWrongUser.txt",
            "testMain/userLoginClerkWrongPassword.txt",
            "testMain/userLoginLibrarianWrongPassword.txt",
            "testMain/userLoginLibrarianWrongUser.txt"

    })
    public void mainUserLoginFailure(String resourceFile) {
        String inputContent = helper.readFromResource(resourceFile);
        ByteArrayInputStream inStream = new ByteArrayInputStream(inputContent.getBytes());
        System.setIn(inStream);

        Main.main(null);

        String consoleOutput = helper.cleanString(outStream.toString());
        String expectedLine = "Sorry! Wrong ID or Password";

        assertTrue(consoleOutput.contains(expectedLine), consoleOutput);
    }

    @DisplayName("Librarian Menu")
    @Test
    public void librarianMenu() {
        String inputContent = helper.readFromResource("testMain/LibrarianMenu.txt");
        ByteArrayInputStream inStream = new ByteArrayInputStream(inputContent.getBytes());
        System.setIn(inStream);

        Main.main(null);

        String consoleOutput = helper.cleanString(outStream.toString());
        assertTrue(consoleOutput.contains("Welcome to Librarian's Portal"),
                "Login fialed. Console output: \n" + consoleOutput);
        helper.printInTest(consoleOutput);
    }


    @DisplayName("Clerk Menu")
    @Test
    public void clerkMenu() {
        String inputContent = helper.readFromResource("testMain/ClerkMenu.txt");
        ByteArrayInputStream inStream = new ByteArrayInputStream(inputContent.getBytes());
        System.setIn(inStream);

        Main.main(null);

        String consoleOutput = helper.cleanString(outStream.toString());
        assertTrue(consoleOutput.contains("Welcome to Clerk's Portal"),
                "Login fialed. Console output: \n" + consoleOutput);
        helper.printInTest(consoleOutput);
    }




}
