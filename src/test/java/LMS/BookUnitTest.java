package LMS;
// JUnit5
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// Java standard
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.lang.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BookUnitTest {

    static private String pathToResources = "src/test/resources/";
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    // Dummies
    private Borrower dummyBorrower;
    private Book bookInTest;
    private Staff dummyStaff;
    private Loan dummyLoan;
    private HoldRequest dummyHoldRequest;

    @BeforeEach
    public void setupStream(){
        System.setOut(new PrintStream(outStream));

        dummyBorrower = new Borrower(0, "dummyBorrower", "", 0);
        bookInTest = new Book(0, "TestBook", "Software Engineering",
                "Somebody", false);
        dummyStaff = new Staff(0, "dummyStaff", "dummyAddress", 0, 0.0);
        dummyLoan = new Loan(dummyBorrower, bookInTest, dummyStaff,
                null, new Date(), null, false);
        dummyHoldRequest = new HoldRequest(dummyBorrower, bookInTest, new Date());
    }

    @AfterEach
    public void restoreStreams(){
        System.setOut(originalOut);
        System.setIn(originalIn);
        bookInTest.setIDCount(0);
    }


    @ParameterizedTest(name="BorroBookwer constructor tests: ID:{0}, " +
            "Title: {1}, Subject: {2}, Author: {3}, Issued: {4}")
    @CsvSource({
            "-4, book-4, subject-4, author-4, false",
            "-3, book-3, , author-3, false",
            "-2, book-2, null, author-2, false",
            "-1, book-1, subject-1, , false",
            "0, book0, subject0, null, false",
            "1, book1, subject1, author1, true",
            "2, book2, subject2, author2, true",
            "3, book3, subject3, author3, false"
    })
    public void bookConstructorTests(
            int id, String title, String subject, String author, boolean issued
    ){
        Book currBook = new Book(id, title, subject, author, issued);

        assertTrue(currBook.getHoldRequests().isEmpty());
        assertEquals(title, currBook.getTitle());
        assertEquals(subject, currBook.getSubject());
        assertEquals(author, currBook.getAuthor());
        assertEquals(issued, currBook.getIssuedStatus());
        assertEquals(id, currBook.getID());
    }

    @DisplayName("Book constructor: repeated id")
    @Test
    public void bookConstructorRepeatedID(){
        Book repeatedBook = new Book(0, "tiile", "subject", "authors", false);
        assertNotEquals(bookInTest.getID(),
                repeatedBook.getID(), "Should not allow books to have the same id");
    }

    @DisplayName("Book setIDCount: Normal")
    @Test
    public void booksetIDCountNormal(){
        bookInTest.setIDCount(5);
        Book newBook = new Book(-1, "","", "", false);
        assertEquals(newBook.getID(), 6);
    }

    @DisplayName("addHoldRequest: Normal")
    @Test
    public void addHoldRequestNormal(){
        HoldRequest[] addedHoldReqquest = new HoldRequest[10];
        ArrayList<HoldRequest> allHoldReqquest;
        bookInTest.setIssuedStatus(true);
        
        assert(bookInTest.getHoldRequests().isEmpty());
        for(int i = 0; i < 10; i++ ) {
            HoldRequest hr = new HoldRequest(dummyBorrower, bookInTest, new Date());
            bookInTest.addHoldRequest(hr);
            addedHoldReqquest[i] = hr;
        }
        allHoldReqquest = bookInTest.getHoldRequests();
        for (int i = 0; i < 10; i++) {
            assertEquals(addedHoldReqquest[i], allHoldReqquest.get(i));
        }

    }

    @DisplayName("addHoldRequest: Not issued")
    @Test
    public void addHoldRequestNotIssued(){
        bookInTest.setIssuedStatus(false);
        assertThrows(Exception.class, ()->{
           bookInTest.addHoldRequest(dummyHoldRequest); 
        }, "Should not add hold request when not issued");
    }

    @DisplayName("addHoldRequest: Different Book")
    @Test
    public void addHoldRequestDifferentBook(){
        Book diffBook = new Book(-1, "Another Book", "Another Subject", 
                "Another Author", true);
        HoldRequest hr = new HoldRequest(dummyBorrower, diffBook, new Date());
        int before, after;
        
        before = bookInTest.getHoldRequests().size();
        bookInTest.setIssuedStatus(true);
        bookInTest.addHoldRequest(hr);
        after = bookInTest.getHoldRequests().size();
        assertEquals(before, after, "Hold request for another book should not matter");
    }

    @DisplayName("addHoldRequest: Null hold request")
    @Test
    public void addHoldRequestNullRequest(){
        int before, after;
        before = bookInTest.getHoldRequests().size();
        bookInTest.setIssuedStatus(true);
        bookInTest.addHoldRequest(null);
        after = bookInTest.getHoldRequests().size();
        assertEquals(before, after, "Null request should not matter");
    }

    @DisplayName("removeHoldRequest: Normal")
    @Test
    public void removeHoldRequestNormal(){
        bookInTest.setIssuedStatus(true);
        bookInTest.addHoldRequest(dummyHoldRequest);
        bookInTest.removeHoldRequest();
        assertTrue(bookInTest.getHoldRequests().isEmpty());
    }

    @DisplayName("removeHoldRequest: Empty list")
    @Test
    public void removeHoldRequestEmptyList(){
        bookInTest.removeHoldRequest();
        assertTrue(bookInTest.getHoldRequests().isEmpty());
    }


    @DisplayName("makeHoldRequest: Normal")
    @Test
    public void makeHoldRequestNormal(){
        bookInTest.makeHoldRequest(dummyBorrower);
        ArrayList<HoldRequest> hr = bookInTest.getHoldRequests();
        assertEquals(hr.get(0).borrower, dummyBorrower);
    }

    @DisplayName("makeHoldRequest: Already borrowed")
    @Test
    public void makeHoldRequestBorrowed(){
        int before, after;
        Loan loanForThis = new Loan(dummyBorrower, bookInTest, dummyStaff,
                null, new Date(), null, false);
        dummyBorrower.addBorrowedBook(loanForThis);

        before = bookInTest.getHoldRequests().size();
        bookInTest.makeHoldRequest(dummyBorrower);
        after = bookInTest.getHoldRequests().size();
        assertEquals(before, after);
    }

    @DisplayName("makeHoldRequest: Already held")
    @Test
    public void makeHoldRequestHeld(){
        int before, after;
        HoldRequest hr = new HoldRequest(dummyBorrower, bookInTest, new Date());
        dummyBorrower.addHoldRequest(hr);

        bookInTest.makeHoldRequest(dummyBorrower);
        before = bookInTest.getHoldRequests().size();
        bookInTest.makeHoldRequest(dummyBorrower);
        after = bookInTest.getHoldRequests().size();
        assertEquals(before, after);
    }

    @DisplayName("printHoldRequests: Empty list")
    @Test
    public void printHoldRequestsEmpty(){
        String expected, actual;
        expected = cleanString("\nNo Hold Requests.");

        bookInTest.printHoldRequests();
        actual = cleanString(outStream.toString());

        assertEquals(expected, actual);
    }

    @DisplayName("printHoldRequests: Normal")
    @Test
    public void printHoldRequestNormal(){
        String expected, actual;
        bookInTest.makeHoldRequest(dummyBorrower);
        bookInTest.printHoldRequests();

        expected = cleanString("\nNo Hold Requests.");
        actual = cleanString(outStream.toString());

        assertNotEquals(expected, actual);
    }

    @ParameterizedTest(name="changeBookInfo: {0}")
    @CsvSource({
            "changeBookInfoNoChange.txt",
            "changeBookInfoAuthor.txt",
            "changeBookInfoSubject.txt",
            "changeBookInfoTitle.txt",
            "changeBookInfoAuthorSubject.txt",
            "changeBookInfoAuthorTitle.txt",
            "changeBookInfoSubjectTitle.txt",
            "changeBookInfoAuthorSubjectTitle.txt"
    })
    public void changeBookInfoTests(String resourceFile){
        String inputContent = readFromResource(resourceFile);
        ByteArrayInputStream inStream = new ByteArrayInputStream(inputContent.getBytes());
        System.setIn(inStream);

        Book equivalentBook = updateInfoParseResource(inputContent, bookInTest);
        try {
            bookInTest.changeBookInfo();
        }
        catch (Exception e) {
            printInTest(e.getMessage());
        }

        assertEquals(bookInTest.getTitle(), equivalentBook.getTitle(), "Title should match");
        assertEquals(bookInTest.getSubject(), equivalentBook.getSubject(), "Subject should match");
        assertEquals(bookInTest.getAuthor(), equivalentBook.getAuthor(), "Author should match");
    }



    static String cleanString(String str) {
        String newStr = str
                .replaceAll("(?m)^[\\s&&[^\\n]]+|[\\s+&&[^\\n]]+$", "")
                .replaceAll("(?m)^\\s", "");
        String lines[] = newStr.split("\\r?\\n");
        String res = "";
        ArrayList<String> cleanLines = new ArrayList<String>();
        for (int i = 1; i < lines.length; i++)
        {
            if(lines[i] != "")
                cleanLines.add(lines[i]);
        }
        for (int i = 0; i < cleanLines.size(); i++)
            res += cleanLines.get(i) + "\n";
        return res;
    }
    private void printInTest(String content) {
        System.setOut(originalOut);
        System.out.println(content);
        System.setOut(new PrintStream(outStream));
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

    private Book updateInfoParseResource(String content, Book b) {
        String title = b.getTitle(),
                subject = b.getSubject(),
                author = b.getAuthor();
        String lines[] = content.split("\\r?\\n");
        printInTest("");
        int index = 0;
        if(lines[index] == "y") {
            index++;
            author = lines[index];
        }
        index++;
        if(lines[index] == "y") {
            index++;
            subject = lines[index];
        }
        index++;
        if(lines[index] == "y") {
            index++;
            title = lines[index];
        }
        Book equivalentBook = new Book(-1, title, subject, author, false);
        return equivalentBook;
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
