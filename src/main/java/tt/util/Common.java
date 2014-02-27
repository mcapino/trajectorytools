package tt.util;

import com.google.common.collect.Iterables;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

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

    public static <E> Collection<E> concatenate(final Collection<? extends Collection<? extends E>> collections) {
        return new ConcatenatedCollection<E>(collections);
    }

    private static class ConcatenatedCollection<E> extends AbstractCollection<E> {

        private Iterator<E> iterator;
        private Collection<? extends Collection<? extends E>> collections;

        private ConcatenatedCollection(Collection<? extends Collection<? extends E>> collections) {
            this.collections = collections;
            this.iterator = Iterables.<E>concat(collections).iterator();
        }

        @Override
        public Iterator<E> iterator() {
            return iterator;
        }

        @Override
        public int size() {
            int size = 0;
            for (Collection<? extends E> collection : collections) {
                size += collection.size();
            }
            return size;
        }
    }
}
