package LMS;

// JUnit5
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// Java io
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

// Java Random
import java.util.Random;

public class StaffTest {
    private Staff staffInTest = new Staff(-1, "staff", "", 0, 0.0);;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStream(){
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public  void restore(){
        System.setOut(originalOut);
        System.setErr(originalErr);
        staffInTest.setIDCount(1);
    }


    @DisplayName("setIDCount: repetitive ID caused by setIDCount")
    @Test
    public void setIDRepeatedIDTest() {
        Staff staffAlice = new Staff(-1, "Alice", "address", 0, 0.0);
        staffAlice.setIDCount(staffAlice.getID() - 1);
        Staff staffBob = new Staff(-1, "Bob", "address", 0, 0.0);
        assertNotEquals(staffAlice.getID(), staffBob.getID(), "First ID: " + Integer.toString(staffAlice.getID())
                +"\tSecond ID: " + Integer.toString(staffBob.getID()));
    }


    @DisplayName("Repetitive ID caused by manually assign ID")
    @Test
    public void repetitiveManulIDTest(){
        Staff staffAlice = new Staff(3, "Alice", "Homewood", 123, 1.00);
        Staff staffBob = new Staff(3, "Bob", "Towson", 456, 1.00);
        assertNotEquals(staffAlice.getID(), staffBob.getID());
    }

    @ParameterizedTest(name = "Staff constructor test - ID:{0}, Name: {1}, Add: {2}, Phone: {3}, Salary: {4}")
    @CsvSource({
            "-4, staff-4, Home, 123, 25.0",
            "-3, staff-3, , 123, 25.0",
            "-2, staff-2, null, 123, 25.0",
            "-1, staff-1, Home, 123, 25.0",
            "0, staff0, Home, 123, 25.0",
            "1, staff1, Home, 456, 17",
            "2, staff2, Home, 456, 0",
            "3, staff3, Home, 789, -25"
    })
    public void staffConstructorTest(
            int id, String name, String address, int phone, double salary
    ){
        Staff currStaff;
        currStaff = new Staff(id, name, address, phone, salary);
        assertEquals(id, currStaff.id, "Failed to assert ID");
        assertEquals(name, currStaff.name, "Failed to assert Name");
        assertEquals(address, currStaff.address, "Failed to assert Address");
        assertEquals(phone, currStaff.phoneNo, "Failed to assert Phone");
        assertEquals(salary, currStaff.salary, "Failed to assert Salary");
    }


    @DisplayName("getName: Normal")
    @Test
    public void getNameNormalTest() {
        assertEquals("staff", staffInTest.getName());
    }

    @DisplayName("getName: empty names")
    @Test
    public void getNameEmptyNamesTest() {
        staffInTest = new Staff(-1, "", "", 0, 0.0);
        assertEquals("", staffInTest.getName());

        staffInTest = new Staff(-1, null, "", 0, 0.0);
        assertNull(staffInTest.getName());
    }

    @DisplayName("setName: normal")
    @Test
    public void setNameNormalTest(){
        staffInTest.setName("super staff");
        assertEquals("super staff", staffInTest.getName());
    }

    @DisplayName("setName: empty names")
    @Test
    public void setNameEmptyNamesTest(){
        staffInTest.setName("");
        assertEquals("", staffInTest.getName());

        assertThrows(NullPointerException.class, () -> {
            staffInTest.setName(null);
        });
    }

    @DisplayName("getAddress: empty address")
    @Test
    public void getAddressEmptyAddressTest() {
        assertEquals("", staffInTest.getAddress());
    }

    @DisplayName("getAddress:  normal")
    @Test
    public void getAddressNormalTest(){
        staffInTest = new Staff(5, "staff", "Somewhere", 123456, 0.0);
        assertEquals("Somewhere", staffInTest.getAddress());
    }

    @DisplayName("setAddress: empty address")
    @Test
    public void setAddressEmptyAddressTest(){
        staffInTest.setAddress("");
        assertEquals("", staffInTest.getAddress());

        assertThrows(NullPointerException.class, () -> {
            staffInTest.setAddress(null);
        });

    }

    @DisplayName("setPhone: Normal")
    @Test
    public void setPhoneNormalTest() {
        staffInTest.setPhone(123);
        assertEquals(123, staffInTest.getPhoneNumber());
    }

    @DisplayName("PrintInfo: Normal")
    @Test
    public void printInfoNormalTest(){
        String expected;
        expected = "-----------------------------------------"
                + "\n\nThe details are: \n\n"
                +  "ID: 1\n"
                + "Name: staff\n"
                + "Address: \n"
                + "Phone No: 0\n\n"
                + "Salary: 0.0\n\n";
        staffInTest.printInfo();
        assertEquals(expected, outContent.toString());
    }

    @DisplayName("PrintInfo: Null name")
    @Test
    public void printInfoNullNameTest(){
        String expected;
        staffInTest.setName(null);
        expected = "-----------------------------------------"
                + "\n\nThe details are: \n\n"
                +  "ID: 1\n"
                + "Name: \n"
                + "Address: \n"
                + "Phone No: 0\n\n"
                + "Salary: 0.0\n\n";
        staffInTest.printInfo();
        assertEquals(expected, outContent.toString());
    }

    @DisplayName("PrintInfo: Null Address")
    @Test
    public void printInfoNullAddressTest(){
        String expected;
        staffInTest.setAddress(null);
        expected = "-----------------------------------------"
                + "\n\nThe details are: \n\n"
                +  "ID: 1\n"
                + "Name: staff\n"
                + "Address: \n"
                + "Phone No: 0\n\n"
                + "Salary: 0.0\n\n";
        staffInTest.printInfo();
        assertEquals(expected, outContent.toString());
    }

    @DisplayName("getPassword: normal")
    @Test
    public void getPasswordNormalTest(){
        assertEquals(Integer.toString(staffInTest.getID()), staffInTest.getPassword());
    }

    @DisplayName("getSalary")
    @Test
    public void getSalaryTest(){
        Random randomGenerator = new Random();
        double maxSalary = 500, minSalary = -500, currSalary = 0;

        for (int i = 0; i < 20; i++) {
            currSalary = randomGenerator.nextDouble() * (maxSalary - minSalary) + minSalary;
            staffInTest = new Staff(i, "staff", "", 123, currSalary);
            assertEquals(currSalary, staffInTest.getSalary());
        }
        staffInTest = new Staff(21, "staff", "", 123, 0);
        assertEquals(0, staffInTest.getSalary());
    }

}
