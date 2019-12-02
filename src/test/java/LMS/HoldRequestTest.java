package LMS;
// JUnit5
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.internal.matchers.Null;

// Java standard
import java.beans.ConstructorProperties;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.lang.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HoldRequestTest {
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // Dummies
    private Borrower dummyBorrower;
    private Book dummyBook;
    private Staff dummyStaff;
    private Loan dummyLoan;
    private HoldRequest holdRequestInTest;

    @BeforeEach
    public void setup(){
        System.setOut(new PrintStream(outStream));
        dummyBorrower = new Borrower(0, "Borrower in test", "", 0);
        dummyBook = new Book(0, "dummyBook", "dummySubject",
                "dummySubject", false);
        dummyStaff = new Staff(0, "dummyStaff", "dummyAddress", 0, 0.0);
        dummyLoan = new Loan(dummyBorrower, dummyBook, dummyStaff,
                null, new Date(), null, false);

        holdRequestInTest = new HoldRequest(dummyBorrower, dummyBook, new Date());
    }

    @AfterEach
    public void restore(){
        System.setOut(originalOut);
        dummyBorrower.setIDCount(0);
    }

    @DisplayName("HoldRequest Constructor: Normal")
    @Test
    public void constructorNormalTest(){
        Date today = new Date();
        HoldRequest hr = new HoldRequest(dummyBorrower, dummyBook, today);
        assertEquals(dummyBorrower, hr.getBorrower());
        assertEquals(dummyBook, hr.getBook());
        assertEquals(today, hr.getRequestDate());

    }

    @DisplayName("HoldRequest Constructor: Null borrower")
    @Test
    public void constructorNullBorrowerTest(){
        Date today = new Date();
        assertThrows(Exception.class, ()->{
            HoldRequest hr = new HoldRequest(null, dummyBook, today);
        }, "Should not allow hold request without borrower");
    }

    @DisplayName("HoldRequest Constructor: Null book")
    @Test
    public void constructorNullBookTest(){
        Date today = new Date();
        assertThrows(Exception.class, ()->{
            HoldRequest hr = new HoldRequest(dummyBorrower, null, today);
        }, "Should not allow hold request without book");
    }

    @DisplayName("HoldRequest Constructor: Null date")
    @Test
    public void constructorNullDateTest(){
        Date today = new Date();
        assertThrows(Exception.class, ()->{
            HoldRequest hr = new HoldRequest(dummyBorrower, dummyBook, null);
        }, "Should not allow hold request without date");
    }

    @DisplayName("print: Normal")
    @Test
    public void printNormalTest(){
        String expected = holdRequestInTest.getBook().getTitle() + "\t\t\t\t"
                + holdRequestInTest.getBorrower().getName() +"\t\t\t\t"
                + holdRequestInTest.getRequestDate() + "\n";
        holdRequestInTest.print();
        assertEquals(expected, outStream.toString());
    }

    @DisplayName("print: Null book")
    @Test
    public void printNullBookTest(){
        Date stamp = new Date();
        HoldRequest hr = new HoldRequest(dummyBorrower, null, stamp);
        String expected = "\t\t\t\t"
                + hr.getBorrower().getName() +"\t\t\t\t"
                + hr.getRequestDate() + "\n";
        hr.print();
        assertEquals(expected, outStream.toString());
    }

    @DisplayName("print: Null borrower")
    @Test
    public void printNullBorrowerTest(){
        Date stamp = new Date();
        HoldRequest hr = new HoldRequest(null, dummyBook, stamp);
        String expected = dummyBook.getTitle() +  "\t\t\t\t"
                 +"\t\t\t\t"
                + hr.getRequestDate() + "\n";
        hr.print();
        assertEquals(expected, outStream.toString());
    }

}
