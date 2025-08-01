import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.*;


public class CatalogPlacementsFlexible {

    // Decode y from given base as BigInteger (handles large values)
    public static BigInteger decodeBase(String yStr, int base) {
        return new BigInteger(yStr, base);
    }

    // Perform Lagrange interpolation at x=0 using points (xs[i], ys[i])
    // ys given as BigInteger, converted to double for arithmetic
    public static double lagrangeAtZero(List<Integer> xs, List<BigInteger> ys) {
        int n = xs.size();
        double result = 0;
        for (int i = 0; i < n; i++) {
            double yi = ys.get(i).doubleValue();
            double term = yi;
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    term *= (0.0 - xs.get(j)) / (xs.get(i) - xs.get(j));
                }
            }
            result += term;
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        // Input filename from args or default to t1.json
        String filename = args.length > 0 ? args[0] : "t2.json";

        // Read entire JSON file content as string
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        JSONObject rootObj = new JSONObject(content);

        JSONObject keysObj = rootObj.getJSONObject("keys");
        int n = keysObj.getInt("n"); // total roots
        int k = keysObj.getInt("k"); // number of roots to use (k = degree+1)

        List<Integer> xs = new ArrayList<>();
        List<BigInteger> ys = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            String key = String.valueOf(i);
            if (!rootObj.has(key))
                continue; // skip if not present

            JSONObject entry = rootObj.getJSONObject(key);

            int base = Integer.parseInt(entry.getString("base"));
            String encodedY = entry.getString("value");

            BigInteger yDecoded = decodeBase(encodedY, base);

            xs.add(i); // x is the index as per problem statement
            ys.add(yDecoded);
        }

        // Use only first k points for interpolation as per assignment
        List<Integer> xsSub = xs.subList(0, k);
        List<BigInteger> ysSub = ys.subList(0, k);

        // Optional: print decoded points to debug

        for (int i = 0; i < k; i++) {
            System.out.println("x=" + xsSub.get(i) + ", y=" + ysSub.get(i).toString());
        }

        double secret = lagrangeAtZero(xsSub, ysSub);

        System.out.println("Secret (C): " + Math.round(secret));
    }
}
