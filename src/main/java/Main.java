import com.google.gson.Gson;

public class Main {
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        // You can use print statements for debugging; they will be visible when running tests.
        // System.out.println("Logs from your program will appear here!"); // Comment out or remove this line
        
        if (args.length < 2) {
            System.out.println("Usage: decode <bencodedString>");
            return;
        }

        String command = args[0];
        if ("decode".equals(command)) {
            String bencodedValue = args[1];
            Object decoded;
            try {
                decoded = decodeBencode(bencodedValue);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                return;
            }
            // Output should be formatted as expected
            System.out.println(gson.toJson(decoded)); // Make sure the output is strictly the decoded string
        } else {
            System.out.println("Unknown command: " + command);
        }
    }

    static Object decodeBencode(String bencodedString) {
        if (Character.isDigit(bencodedString.charAt(0))) {
            int firstColonIndex = 0;
            for (int i = 0; i < bencodedString.length(); i++) {
                if (bencodedString.charAt(i) == ':') {
                    firstColonIndex = i;
                    break;
                }
            }
            int length = Integer.parseInt(bencodedString.substring(0, firstColonIndex));
            return bencodedString.substring(firstColonIndex + 1, firstColonIndex + 1 + length);
        }else if(bencodedString.startsWith("i"))
        	{return Long.parseLong(bencodedString.substring(1,bencodedString.indexOf("e")));
        	}
        else {
        	}
            throw new RuntimeException("Only strings are supported at the moment");
        }
    }

