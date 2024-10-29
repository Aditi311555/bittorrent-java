import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// import com.dampcake.bencode.Bencode; - available if you need it!
public class Main {
  private static final Gson gson = new Gson();
  public static void main(String[] args) throws Exception {
    // You can use print statements as follows for debugging, they'll be visible
    // when running tests.
    String command = args[0];
    if ("decode".equals(command)) {
      //  Uncomment this block to pass the first stage
      String bencodedValue = args[1];
      String decoded;
      try {
        if (bencodedValue.charAt(0) == 'i') {
          long decodedValue = decodeBencodeInt(bencodedValue);
          System.out.println(decodedValue);
        } else if (bencodedValue.charAt(0) == 'l') {
          List<Object> decodedList = decodeList(bencodedValue);
          System.out.println(gson.toJson(decodedList));
        } else if (bencodedValue.charAt(0) == 'd') {
          Map<String, Object> dict = decodeDict(bencodedValue);
          System.out.println(gson.toJson(dict));
        } else {
          decoded = decodeBencode(bencodedValue);
          System.out.println(gson.toJson(decoded));
        }
      } catch (RuntimeException e) {
        System.out.println(e.getMessage());
        return;
      }
    } else {
      System.out.println("Unknown command: " + command);
    }
  }
  private static Map<String, Object> decodeDict(String bencodedValue) {
    Bencode bencode = new Bencode();
    return bencode.decode(bencodedValue.getBytes(), Type.DICTIONARY);
  }
  static String decodeBencode(String bencodedString) {
    if (Character.isDigit(bencodedString.charAt(0))) {
      int firstColonIndex = 0;
      for (int i = 0; i < bencodedString.length(); i++) {
        if (bencodedString.charAt(i) == ':') {
          firstColonIndex = i;
          break;
        }
      }
      int length =
          Integer.parseInt(bencodedString.substring(0, firstColonIndex));
      return bencodedString.substring(firstColonIndex + 1,
                                      firstColonIndex + 1 + length);
    } else {
      throw new RuntimeException(
          "Only strings and integers are supported at the moment");
    }
  }
  static long decodeBencodeInt(String bencodedString) {
    if (bencodedString.charAt(0) == 'i') {
      long value = 0;
      int i = 1;
      boolean flag = false;
      while (true) {
        if (bencodedString.charAt(i) == 'e') {
          break;
        } else if (bencodedString.charAt(i) == '-') {
          flag = true;
        } else {
          value =
              value * 10 + Character.getNumericValue(bencodedString.charAt(i));
        }
        i++;
      }
      if (flag)
        return value * (-1);
      else
        return value;
    } else {
      throw new RuntimeException(
          "Only strings and integers are supported at the moment");
    }
  }
  static List<Object> decodeList(String bencode) {
    Bencode b = new Bencode();
    return b.decode(bencode.getBytes(), Type.LIST);
  }
}