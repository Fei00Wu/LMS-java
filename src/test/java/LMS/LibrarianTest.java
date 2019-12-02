package LMS;
// JUnit5
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class LibrarianTest {

    @ParameterizedTest(name = "Librarian constructor tests - ID:{0}, Name: {1}, Add: {2}, Phone: {3}, Salary: {4}, Office: {5}")
    @CsvSource({
            "-1, librarian0, Home, 123, 25.0, -4",
            "-1, librarian1, , 123, 25.0, -3",
            "-1, librarian2, null, 123, 25.0, -2",
            "-1, librarian3, Home, 123, 25.0, -1",
            "-1, librarian4, Home, 123, 25.0, 0",
            "-1, librarian5,  Home, 456, 17, 1",
            "-1, librarian6, Home, 456, 0, 2",
            "-1, librarian7, Home, 789, -25, 3"
    })
    public void clerkConstructorTests(
            int id, String name, String address, int phone, double salary, int office
    ){
        Librarian currLibrarian = new Librarian(id, name, address, phone, salary, office);
        assertEquals(office, currLibrarian.officeNo);
    }

    @ParameterizedTest(name = "Librarian printInfo tests - ID:{0}, Name: {1}, Add: {2}, Phone: {3}, Salary: {4}, Office: {5}")
    @CsvSource({
            "-4, clerk-4, Home, 123, 25.0, -4",
            "-3, clerk-3, , 123, 25.0, -3",
            "-2, clerk-2, null, 123, 25.0, -2",
            "-1, clerk-1, Home, 123, 25.0, -1",
            "-0, clerk0, Home, 123, 25.0, 0",
            "1, clerk1,  Home, 456, 17, 1",
            "2, clerk2, Home, 456, 0, 2",
            "3, clerk3, Home, 789, -25, 3"
    })
    public void clerkPrintInfo(
            int id, String name, String address, int phone, double salary, int office
    ){
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        Librarian currLibrarian = new Librarian(id, name, address, phone, salary, office);
        String expected;
        expected = "-----------------------------------------"
                + "\n\nThe details are: \n\n"
                +  "ID: " + Integer.toString(id) +"\n"
                + "Name: " + name + "\n"
                + "Address: " + address + "\n"
                + "Phone No: " + Integer.toString(phone) + "\n\n"
                + "Salary: " + Double.toString(salary) + "\n\n"
                + "Office Number: " + Integer.toString(office) + "\n";
        currLibrarian.printInfo();
        assertEquals(expected, outContent.toString());

        System.setOut(originalOut);
        System.setErr(originalErr);
    }

}
