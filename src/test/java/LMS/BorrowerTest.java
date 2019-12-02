package LMS;
// JUnit5
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// Java standard
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.lang.*;
import java.nio.file.Files;
import java.nio.file.Paths;

// Extra string utils
import org.apache.commons.lang3.StringUtils;

public class BorrowerTest {
    static String pathToResources = "src/test/resources/";
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    // Dummies
    private Borrower borrowerInTest;
    private Book dummyBook;
    private Staff dummyStaff;
    private Loan dummyLoan;
    private HoldRequest dummyHoldRequest;

    @BeforeEach
    public void setupStream(){
        System.setOut(new PrintStream(outStream));

        borrowerInTest = new Borrower(0, "Borrower in test", "", 0);
        dummyBook = new Book(0, "dummyBook", "dummySubject",
                "dummySubject", false);
        dummyStaff = new Staff(0, "dummyStaff", "dummyAddress", 0, 0.0);
        dummyLoan = new Loan(borrowerInTest, dummyBook, dummyStaff,
                null, new Date(), null, false);
        dummyHoldRequest = new HoldRequest(borrowerInTest, dummyBook, new Date());
    }

    @AfterEach
    public void restoreStreams(){
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @AfterAll
    public static void restoreID(){
        Borrower tmp = new Borrower(0, ",","", 0);
        tmp.setIDCount(0);
    }

    @ParameterizedTest(name="Borrower constructor tests: ID:{0}, Name: {1}, Add: {2}, Phone: {3}")
    @CsvSource({
            "-4, staff-4, Home, 123",
            "-3, staff-3, , 123",
            "-2, staff-2, null, 123",
            "-1, staff-1, Home, 123",
            "0, staff0, Home, 123",
            "1, staff1, Home, 456",
            "2, staff2, Home, 456",
            "3, staff3, Home, 789"
    })
    public void testBorrowerConstructor(
            int id, String name, String address, int phone
    ){
        Borrower currBorrower = new Borrower(id, name, address, phone);

        assertTrue(currBorrower.getBorrowedBooks().isEmpty());
        assertTrue(currBorrower.getOnHoldBooks().isEmpty());

        assertEquals(id, currBorrower.id, "Failed to assert ID");
        assertEquals(name, currBorrower.name, "Failed to assert Name");
        assertEquals(address, currBorrower.address, "Failed to assert Address");
        assertEquals(phone, currBorrower.phoneNo, "Failed to assert Phone");

    }


    @DisplayName("addBorrowedBook: normal")
    @Test
    public void testAddBorrowedBookNormal() {
        int after, before = borrowerInTest.getBorrowedBooks().size();

        borrowerInTest.addBorrowedBook(dummyLoan);
        after = borrowerInTest.getBorrowedBooks().size();
        assertEquals(before + 1, after, "Before adding new loan: "
                + Integer.toString(before) + "\tAfter adding new loan: " + Integer.toString(after));

    }

    @DisplayName("addBorrowedBook: null")
    @Test
    public void testAddBorrowedBookNull() {
        int after, before = borrowerInTest.getBorrowedBooks().size();

        borrowerInTest.addBorrowedBook(null);
        after = borrowerInTest.getBorrowedBooks().size();
        assertEquals(before , after, "Before adding null: "
                + Integer.toString(before) + "\tAfter adding null: " + Integer.toString(after));

    }

    @DisplayName("addBorrowedBook: repetitive loans")
    @Test
    public void testAddBorrowedBookRepetitiveLoans() {
        int after, before = borrowerInTest.getBorrowedBooks().size();

        for (int i = 0; i < 10; i++)
            borrowerInTest.addBorrowedBook(dummyLoan);

        after = borrowerInTest.getBorrowedBooks().size();
        assertEquals(before + 1, after, "Before repetitively adding loan: "
                + Integer.toString(before) + "\tAfter repetitively adding loan: " + Integer.toString(after));

    }

    @DisplayName("removeBorrowedBook: normal")
    @Test
    public void testRemoveBorrowedBookNormal() {
        int before = borrowerInTest.getBorrowedBooks().size(), after;
        for (int i = 0; i < 10; i++) {
            borrowerInTest.addBorrowedBook(dummyLoan);
            borrowerInTest.removeBorrowedBook(dummyLoan);
        }
        after =  borrowerInTest.getBorrowedBooks().size();
        assertEquals(before, after, "Before add and remove: "
                + Integer.toString(before) + "\tAfter add and remove: " + Integer.toString(after));

    }

    @DisplayName("removeBorrowedBook: remove while no borrowed book")
    @Test
    public void testRemoveBorrowedBookEmptyListTest() {
        assertThrows(IllegalStateException.class, () ->{
            borrowerInTest.removeBorrowedBook(dummyLoan);
        });
    }

    @DisplayName("removeBorrowedBook: remove null ")
    @Test
    public void testRemoveBorrowedBookNull() {
        Loan dummy = null;
        int before, after;

        before = borrowerInTest.getBorrowedBooks().size();
        borrowerInTest.addBorrowedBook(dummy);
        borrowerInTest.removeBorrowedBook(dummy);
        after =  borrowerInTest.getBorrowedBooks().size();

        assertEquals(before, after);
    }

    @DisplayName("removeBorrowedBook: remove repeated loans")
    @Test
    public void testRemoveBorremoveBorrowedBookRepeated() {
        int before, after;
        before = borrowerInTest.getBorrowedBooks().size();
        for(int i = 0; i < 10; i++) {
            borrowerInTest.addBorrowedBook(dummyLoan);
        }
        borrowerInTest.removeBorrowedBook(dummyLoan);
        after = borrowerInTest.getBorrowedBooks().size();
        assertEquals(before, after);

    }

    @DisplayName("addHoldRequest: Normal")
    @Test
    public void testAddHoldRequestNormal() {
        int before, after;
        HoldRequest currHoldRequest;
        HoldRequest[] allHoldRequests = new HoldRequest[10];

        before = borrowerInTest.getOnHoldBooks().size();

        for (int i = 0; i < 10; i++) {
            currHoldRequest = new HoldRequest(borrowerInTest, dummyBook, new Date());
            allHoldRequests[i] = currHoldRequest;
            borrowerInTest.addHoldRequest(currHoldRequest);
        }
        assertArrayEquals(allHoldRequests, borrowerInTest.getOnHoldBooks().toArray());
    }

    @DisplayName("addHoldRequest: Null")
    @Test
    public void testAddHoldRequestNull() {
        assertThrows(NullPointerException.class, () -> {
            borrowerInTest.addHoldRequest(null);
        });
    }

    @DisplayName("addHoldRequest: Repeated")
    @Test
    public void testAddHoldRequestRepeated() {
        int before, after;
        before = borrowerInTest.getOnHoldBooks().size();
        for (int i = 0; i < 10; i++)
            borrowerInTest.addHoldRequest(dummyHoldRequest);
        after = borrowerInTest.getOnHoldBooks().size();
        assertEquals(before + 1, after);
    }

    @DisplayName("removeHoldRequest: normal")
    @Test
    public void testRemoveHoldRequestNormal(){
        int before, after;
        HoldRequest currHoldRequest;
        HoldRequest[] allHoldRequests = new HoldRequest[10];

        before = borrowerInTest.getOnHoldBooks().size();

        for (int i = 0; i < 10; i++) {
            currHoldRequest = new HoldRequest(borrowerInTest, dummyBook, new Date());
            allHoldRequests[i] = currHoldRequest;
            borrowerInTest.addHoldRequest(currHoldRequest);
        }

        for (int i = 0; i < 10; i++) {
            borrowerInTest.removeHoldRequest(allHoldRequests[i]);
        }

        after = borrowerInTest.getOnHoldBooks().size();
        assertEquals(before, after);
    }

    @DisplayName("removeHoldRequest: Null")
    @Test
    public void testRemoveHoldRequestNull() {
        int before, after;
        HoldRequest hr = null;
        before = borrowerInTest.getOnHoldBooks().size();
        borrowerInTest.addHoldRequest(hr);
        borrowerInTest.removeHoldRequest(hr);
        after = borrowerInTest.getOnHoldBooks().size();
        assertEquals(before, after);
    }

    @DisplayName("removeHoldRequest: Repeated hold requestes")
    @Test
    public void testRemoveHoldRequestRepeated() {
        int before, after;
        HoldRequest hr = null;
        before = borrowerInTest.getOnHoldBooks().size();
        for (int i = 0; i < 10; i++)
            borrowerInTest.addHoldRequest(dummyHoldRequest);
        borrowerInTest.removeHoldRequest(dummyHoldRequest);
        after = borrowerInTest.getOnHoldBooks().size();
        assertEquals(before, after);
    }

    @DisplayName("printInfo: Normal")
    @Test
    public void testPrintInfoNormal() {
        String expectedUI = "", actualUI;
        expectedUI = readFromResource("borrowerPrintInfoNormal.txt")
                .replaceAll("(?m)^[\\s&&[^\\n]]+|[\\s+&&[^\\n]]+$", "");

        borrowerInTest.addBorrowedBook(dummyLoan);
        borrowerInTest.addHoldRequest(dummyHoldRequest);
        borrowerInTest.printInfo();

        actualUI = outStream.toString()
                .replaceAll("(?m)^[\\s&&[^\\n]]+|[\\s+&&[^\\n]]+$", "");
        assertEquals(expectedUI, actualUI);
    }

    @DisplayName("printInfo: Empty records")
    @Test
    public void testPrintInfoEmptyRecords() {
        String expectedUI = "", actualUI;
        expectedUI = readFromResource("borrowerPrintInfoEmptyRecords.txt")
                .replaceAll("(?m)^[\\s&&[^\\n]]+|[\\s+&&[^\\n]]+$", "");

        borrowerInTest.printInfo();

        actualUI = outStream.toString()
                .replaceAll("(?m)^[\\s&&[^\\n]]+|[\\s+&&[^\\n]]+$", "");
        assertEquals(expectedUI, actualUI);
    }

    @ParameterizedTest(name="updateBorrowerInfo:  {0}")
    @CsvSource({
            "updateBorrowerInfoNoUpdates.txt",
            "updateBorrowerInfoName.txt",
            "updateBorrowerInfoAddress.txt",
            "updateBorrowerInfoPhone.txt",
            "updateBorrowerInfoNameAddress.txt",
            "updateBorrowerInfoNamePhone.txt",
            "updateBorrowerInfoAddressPhone.txt",
            "updateBorrowerInfoNameAddressPhone.txt"
    })
    public void testUpdateBorrowerInfo(String resourceFile)
    {
        String inputContent = readFromResource(resourceFile);
        ByteArrayInputStream inStream = new ByteArrayInputStream(inputContent.getBytes());
        System.setIn(inStream);

        Borrower equivalentBorrower = updateInfoParseResource(inputContent, borrowerInTest);
        try {
            borrowerInTest.updateBorrowerInfo();
        }
        catch (Exception e) {
            printInTest(e.getMessage());
        }

        assertEquals(borrowerInTest.name, equivalentBorrower.name, "Name should match");
        assertEquals(borrowerInTest.address, equivalentBorrower.address, "Address should match");
        assertEquals(borrowerInTest.phoneNo, equivalentBorrower.phoneNo, "Phone should match");
    }


    private void printInTest(String content) {
        System.setOut(originalOut);
        System.out.println(content);
        System.setOut(new PrintStream(outStream));
    }

    private InputStream readFromResourceAsStream(String filename) {
        try {
            return Files.newInputStream(Paths.get(pathToResources + filename));
        }
        catch (IOException e) {
            printInTest(e.getMessage());
        }
        return new ByteArrayInputStream(new byte[0]);
    }

    private String readFromResource(String fileName) {
        String content = "";
        String wholePath = pathToResources + fileName;
        try {
            content = new String(Files.readAllBytes(Paths.get(wholePath)),
                    Charset.forName("US-ASCII"));

        } catch (IOException e) {
            printInTest(e.getMessage());
        }
        return content;
    }

    private Borrower updateInfoParseResource(String content, Borrower b) {
        String name = b.name, address = b.address;
        int phone = b.phoneNo;
        String lines[] = content.split("\\r?\\n");
        printInTest("");
        int index = 0;
        if(lines[index] == "y") {
            index++;
            name = lines[index];
        }
        index++;
        if(lines[index] == "y") {
            index++;
            address = lines[index];
        }
        index++;
        if(lines[index] == "y") {
            index++;
            phone = Integer.parseInt(lines[index]);
        }
        Borrower equivalentBorrower = new Borrower(-1, name, address, phone);
        return equivalentBorrower;
    }

    private String slurp(InputStream in) {
        try {
            StringBuilder sb = new StringBuilder();
            Reader reader = new BufferedReader(new InputStreamReader(in));
            int c = 0;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
            return sb.toString();
        }
        catch (IOException e) {
            printInTest(e.getMessage());
            return "";
        }
    }
}
