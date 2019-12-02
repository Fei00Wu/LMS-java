package LMS;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;


public class ClerkTest {
    private Clerk clerkInTest = new Clerk(0, "clerk","Home", 123, 25.0, 0);

    @ParameterizedTest(name = "Clerk constructor tests - ID:{0}, Name: {1}, Add: {2}, Phone: {3}, Salary: {4}, Desk: {5}")
    @CsvSource({
            "-1, clerk0, Home, 123, 25.0, -4",
            "-1, clerk1, , 123, 25.0, -3",
            "-1, clerk2, null, 123, 25.0, -2",
            "-1, clerk3, Home, 123, 25.0, -1",
            "-1, clerk04, Home, 123, 25.0, 0",
            "-1, clerk5,  Home, 456, 17, 1",
            "-1, clerk6, Home, 456, 0, 2",
            "-1, clerk7, Home, 789, -25, 3"
    })
    public void clerkConstructorTest(
            int id, String name, String address, int phone, double salary, int desk
    ){
        Clerk currClerk = new Clerk(id, name, address, phone, salary, desk);
        assertEquals(desk, currClerk.deskNo, "Failed to assert Desk Number");
    }

    @ParameterizedTest(name = "Print info tests - ID:{0}, Name: {1}, Add: {2}, Phone: {3}, Salary: {4}, Desk: {5}")
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
    public void clerkPrintInfoTest(
            int id, String name, String address, int phone, double salary, int desk
    ){
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        Clerk currClerk = new Clerk(id, name, address, phone, salary, desk);
        String expected;
        expected = "-----------------------------------------"
                + "\n\nThe details are: \n\n"
                +  "ID: " + Integer.toString(id) +"\n"
                + "Name: " + name + "\n"
                + "Address: " + address + "\n"
                + "Phone No: " + Integer.toString(phone) + "\n\n"
                + "Salary: " + Double.toString(salary) + "\n\n"
                + "Desk Number: " + Integer.toString(desk) + "\n";
        currClerk.printInfo();
        assertEquals(expected, outContent.toString());

        System.setOut(originalOut);
        System.setErr(originalErr);
    }

}
