package LMS;
// JUnit5
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// Java io
import javax.security.auth.login.AccountLockedException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

// Java Random
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class LoanTest {
    static private String pathToResources = "src/test/resources/";
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    // Dummies
    private Borrower dummyBorrower;
    private Book dummyBook;
    private Staff dummyStaff;
    private Date rightNow;
    private uiUtils helper = new uiUtils(pathToResources, originalOut, outStream);

    @BeforeEach
    public void setup() {
        dummyBook = new Book(-1, "dummyBook", "dummySubject", "dummyAuthors", false);
        dummyStaff = new Staff(-1, "dummyStaff", "dummyAddress", 0, 0.5);
        dummyBorrower = new Borrower(-1, "dummyBorrower", "dummyAddress", 1);
        rightNow = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);

        System.setOut(new PrintStream(outStream));
    }

    @AfterEach
    public void restore() {
        dummyStaff.setIDCount(0);
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @DisplayName("Loan constructor: Normal")
    @Test
    public void loanConstructorNormal(){
        Loan loanInTest = new Loan(dummyBorrower, dummyBook, dummyStaff, null, rightNow, null, false);

        assertEquals(dummyBook, loanInTest.getBook());
        assertEquals(dummyBorrower, loanInTest.getBorrower());
        assertEquals(dummyStaff, loanInTest.getIssuer());
        assertEquals(rightNow, loanInTest.getIssuedDate());
        assertFalse(loanInTest.getFineStatus());
    }

    @ParameterizedTest(name="loanConstructor: null - {0}")
    @CsvSource({
            "book",
            "borrower",
            "issuer",
            "date"
    })
    public void loanConstructorNullArgs(String nullable){
        if (nullable.equals("book"))
            dummyBook = null;
        if (nullable.equals("borrower"))
            dummyBorrower = null;
        if (nullable.equals("issuer"))
            dummyStaff = null;
        if (nullable.equals("date"))
            rightNow = null;

        assertThrows(Exception.class, () -> {
            new Loan(dummyBorrower, dummyBook, dummyStaff, null, rightNow, null, false);
        }, "Should not allow loan without a " + nullable);
    }

    @DisplayName("Loan constructor: returned date set")
    @Test
    public void loanConstructorReturnedSet(){
        Loan loanInTest = new Loan(dummyBorrower, dummyBook, dummyStaff, dummyStaff, new Date(), rightNow, true);
        assertTrue(loanInTest.getReturnDate().compareTo(loanInTest.getIssuedDate()) >= 0,
                "\nShould not allow return date earlier than issue date:\n Return Date: "
                        + loanInTest.getReturnDate()
                        + "\tIssue date: "
                        + loanInTest.getIssuedDate() + "\n");

    }

    @ParameterizedTest(name="setReceiver: receiver null - {0}")
    @CsvSource({
            "n",
            "y"
    })
    public void setReceiverOptionalNull(String receiverNull) {
        Loan loanInTest = new Loan(dummyBorrower, dummyBook, dummyStaff, null, rightNow, null, false);
        if (receiverNull.equals("y"))
            dummyStaff = null;
        try {
            loanInTest.setReceiver(dummyStaff);
            assertNotNull(loanInTest.getReceiver(), "Receiver should not be null");
        } catch(Exception e) {
            assertNull(dummyStaff,  "Should not have exception"); }
    }

    @ParameterizedTest(name="setReturnDate: return date null - {0}")
    @CsvSource({
            "n",
            "y"
    })
    public void setReturnDateOptionalNull(String receiverNull) {
        Loan loanInTest = new Loan(dummyBorrower, dummyBook, dummyStaff, null, rightNow, null, false);
        Date returnDate = null;
        if (receiverNull.equals("n"))
            returnDate = new Date();

        try {
            loanInTest.setReturnedDate(returnDate);
            assertNotNull(loanInTest.getReturnDate(), "Return date should not be null");
        } catch(Exception e) {
            assertNull(returnDate,  "Should not have exception");
        }

    }

    @DisplayName("payFine: No fine due")
    @Test
    public void payFineNoAmountDue() {
        String expected, actual;
        Loan loanInTest = new Loan(dummyBorrower, dummyBook,
                dummyStaff, dummyStaff,
                rightNow, rightNow, false);
        loanInTest.payFine();
        expected = helper.cleanString("\nNo fine is generated.");
        actual = helper.cleanString(outStream.toString());
        assertEquals(expected, actual);
    }

    @ParameterizedTest(name="payFine: Fine required: finePerDay {0}, " +
            "overDueDays {1}, userPaid {2}, expectedFine {3}")
    @CsvSource({
            "1.2, 5, y, 6.0",
            "1.2, 5, n, 6.0",
            "0, 1, y, 0",
            "1.2, 3, y, 3.6",
            "3.1, 2, n, 6.2",
            "1.2, 0, n, 0",
            "-0.5, 3, y, -1.5",
            "1.5, -3, n, -4.5"
    })
    public void payFineNonZero(double finePerDay, int overDueDays,
                               char userPaid, double expectedFine) {
                String expectedUIOut, actualUIOut;
        if (expectedFine > 0)
            expectedUIOut = "\nTotal Fine generated: Rs "
                    + expectedFine +
                    "\nDo you want to pay? (y/n)\n";

        else
            expectedUIOut = "\nNo fine is generated.\n";

        ByteArrayInputStream inStream = new ByteArrayInputStream((userPaid + "\n").getBytes());
        System.setIn(inStream);

        Date issuedDate = new Date(System.currentTimeMillis() - (long)overDueDays * 24 * 3600 *1000);

        Loan loanInTest = new Loan(dummyBorrower, dummyBook,
                dummyStaff, dummyStaff,
                issuedDate, null, false);

        Library.getInstance().setFine(finePerDay);
        loanInTest.payFine();
        actualUIOut = outStream.toString();

        helper.printInTest(expectedUIOut + "\n" + actualUIOut);

        assertEquals(expectedUIOut, actualUIOut);
        if (expectedFine <= 0 || userPaid == 'y')
            assertTrue(loanInTest.getFineStatus());
        else
            assertFalse(loanInTest.getFineStatus());

    }

    @ParameterizedTest(name="setFineStatus: set to {0}")
    @CsvSource({
            "true",
            "false"
    })
    public void setFineStatus(boolean input) {
        Loan loanInTest = new Loan(dummyBorrower,
                dummyBook, dummyStaff, null,
                rightNow, null, false);
        loanInTest.setFineStatus(input);
        assertEquals(input, loanInTest.getFineStatus());
    }

    @DisplayName("renewIssuedBook: Normal")
    @Test
    public void renewIssuedBookNormal() {
        Date issuedDate = new Date(System.currentTimeMillis() - (long)10 * 24 * 3600 * 1000);
        Loan loanInTest = new Loan(dummyBorrower,
                dummyBook, dummyStaff, null,
                issuedDate, null, false);
        loanInTest.renewIssuedBook(rightNow);
        assertEquals(rightNow, loanInTest.getIssuedDate());
    }

    @DisplayName("renewIssuedBook: Null date")
    @Test
    public void renewIssuedBookNull() {
        Date issuedDate = new Date(System.currentTimeMillis() - (long)10 * 24 * 3600 * 1000);
        Loan loanInTest = new Loan(dummyBorrower,
                dummyBook, dummyStaff, null,
                issuedDate, null, false);
        loanInTest.renewIssuedBook(null);
        assertNotNull(loanInTest.getIssuedDate(),
                "Should not allow issued date to be null");
    }

}
