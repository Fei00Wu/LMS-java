package LMS;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class uiUtils {
    private String resourcePath;
    private PrintStream originalOut;
    ByteArrayOutputStream outStream;

    public uiUtils(
            String rPath,
            PrintStream orgOut,
            ByteArrayOutputStream classOut

    ) {
        resourcePath = rPath;
        originalOut = orgOut;
        outStream = classOut;
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

    public void printInTest(String content) {
        System.setOut(originalOut);
        System.out.println(content);
        System.setOut(new PrintStream(outStream));
    }

    public String readFromResource(String fileName) {
        String content = "";
        String wholePath = resourcePath + fileName;
        try {
            content = new String(Files.readAllBytes(Paths.get(wholePath)),
                    Charset.forName("US-ASCII"));

        } catch (IOException e) {
            printInTest(e.getMessage());
        }
        return content;
    }

    public String slurp(InputStream in) {
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
