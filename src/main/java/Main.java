import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {
  private static final Gson gson = new Gson();

  public static void main(String[] args) throws Exception {
    String command = args[0];

    if ("decode".equals(command)) {
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
    } else if ("info".equals(command)) {
      String filePath = args[1];
      Torrent torrent = new Torrent(Files.readAllBytes(Path.of(filePath)));
      System.out.println("Tracker URL: " + torrent.announce);
      System.out.println("Length: " + torrent.length);
      System.out.println("Info Hash: "+bytesToHex(torrent.infoHash));
    } else {
      System.out.println("Unknown command: " + command);
    }
  }

  private static Map<String, Object> decodeDict(String bencodedValue) {
    Bencode bencode = new Bencode();
    return bencode.decode(bencodedValue.getBytes(), Type.DICTIONARY);
  }

  private static String bytesToHex(byte[] bytes){
    StringBuilder sb = new StringBuilder();
    for (byte b:bytes){
      sb.append(String.format("%02x",b));
    }
          return sb.toString();
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
                "Only strings are supported at the moment");
          }
        }

  static long decodeBencodeInt(String bencodedString) {
    if (bencodedString.charAt(0) == 'i') {
      long value = 0;
      int i = 1;
      boolean isNegative = false;
      if (bencodedString.charAt(i) == '-') {
        isNegative = true;
        i++;
      }
      while (bencodedString.charAt(i) != 'e') {
        value = value * 10 + Character.getNumericValue(bencodedString.charAt(i));
        i++;
      }
      return isNegative ? -value : value;
    } else {
      throw new RuntimeException("Only strings and integers are supported at the moment");
    }
  }

  static List<Object> decodeList(String bencodedString) {
    Bencode b = new Bencode();
    return b.decode(bencodedString.getBytes(), Type.LIST);
  }
}

class Torrent {
  public String announce;
  public long length;
  public byte[] infoHash;

  public Torrent(byte[] bytes) throws NoSuchAlgorithmException {
    Bencode bencode = new Bencode(false);
    Bencode bencode2 = new Bencode(true);

    Map<String, Object> root = bencode.decode(bytes, Type.DICTIONARY);
    Map<String, Object> info = (Map<String, Object>)root.get("info");

    announce = (String)root.get("announce");
    length = (long)info.get("length");

    MessageDigest digest2 = MessageDigest.getInstance("SHA-1");
    infoHash = digest2.digest(bencode2.encode(
        (Map<String, Object>)bencode2.decode(bytes, Type.DICTIONARY)
            .get("info")));
  }
}
