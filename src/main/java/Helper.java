/****************************
 * Created by Michael Marolt *
 *****************************/

public class Helper {
    public static String twoDimensionalArrayToString(int[][] in) {
        StringBuffer result = new StringBuffer();
        String separator = ",";

        for (int i = 0; i < in.length; ++i) {
            result.append('[');
            for (int j = 0; j < in[i].length; ++j)
                if (j > 0)
                    result.append(separator).append(in[i][j]);
                else
                    result.append(in[i][j]);
            result.append(']');
            result.append("\n");
        }

        return result.toString();
    }
}
