import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Gson gson = new Gson();
    private static int currentIndex = 0; // To keep track of the position in the bencoded string

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: decode <bencodedString>");
            return;
        }

        String command = args[0];
        if ("decode".equals(command)) {
            String bencodedValue = args[1];
            Object decoded;
            try {
                currentIndex = 0; // Reset index for each decode call
                decoded = decodeBencode(bencodedValue);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                return;
            }
            // Output should be formatted as expected
            System.out.println(gson.toJson(decoded));
        } else {
            System.out.println("Unknown command: " + command);
        }
    }

    static Object decodeBencode(String bencodedString) {
        char startChar = bencodedString.charAt(currentIndex);

        // Decode a string
        if (Character.isDigit(startChar)) {
            int firstColonIndex = currentIndex;
            while (bencodedString.charAt(firstColonIndex) != ':') {
                firstColonIndex++;
            }
            int length = Integer.parseInt(bencodedString.substring(currentIndex, firstColonIndex));
            currentIndex = firstColonIndex + 1;
            String str = bencodedString.substring(currentIndex, currentIndex + length);
            currentIndex += length;
            return str;
        }
        // Decode an integer
        else if (startChar == 'i') {
            int endIndex = bencodedString.indexOf('e', currentIndex);
            long number = Long.parseLong(bencodedString.substring(currentIndex + 1, endIndex));
            currentIndex = endIndex + 1;
            return number;
        }
        // Decode a list
        else if (startChar == 'l') {
            currentIndex++;
            List<Object> list = new ArrayList<>();
            while (bencodedString.charAt(currentIndex) != 'e') {
                list.add(decodeBencode(bencodedString)); // Decode each element in the list
            }
            currentIndex++; // Move past 'e'
            return list; // Return the decoded list
        }
        
        throw new RuntimeException("Invalid bencoded format.");
    }
}
