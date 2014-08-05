package tt.euclid2d;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class PointHashEqualsTest {

    @Test
    public void testEqualsAndHash() {
        final Random random = new Random();
        final int samples = 100000;
        final double maxValue = 1;

        Set<Integer> hashes = new HashSet<Integer>();
        for (int i=0; i < samples; i++) {
            Point p = new Point(random.nextDouble()*maxValue, random.nextDouble()*maxValue);
            int hash = p.hashCode();
            hashes.add(hash);
        }

        double collisions = (samples - hashes.size());
        assertTrue(collisions < samples/10);
    }

}
