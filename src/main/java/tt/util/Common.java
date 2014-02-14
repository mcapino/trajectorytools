package tt.util;

public class Common {

    public static int[] prefillArray(int length, int value) {
        int[] arr = new int[length];
        for (int i = 0; i < length; i++) {
            arr[i] = value;
        }
        return arr;
    }

    public static double[] prefillArray(int length, double value) {
        double[] arr = new double[length];
        for (int i = 0; i < length; i++) {
            arr[i] = value;
        }
        return arr;
    }

    public static byte[] prefillArray(int length, byte value) {
        byte[] arr = new byte[length];
        for (int i = 0; i < length; i++) {
            arr[i] = value;
        }
        return arr;
    }

    public static boolean[] prefillArray(int length, boolean value) {
        boolean[] arr = new boolean[length];
        for (int i = 0; i < length; i++) {
            arr[i] = value;
        }
        return arr;
    }

}
