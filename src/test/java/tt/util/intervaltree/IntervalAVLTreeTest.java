package tt.util.intervaltree;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class IntervalAVLTreeTest {

    private static final int TEST_LENGTH = 1000;
    private static final int MAX = TEST_LENGTH * 50;
    private static final int MAX_INT = MAX / 10;

    private IntervalAVLTree<Interval> tree;
    private List<Interval> intervals;
    private Random random;

    public IntervalAVLTreeTest() {
        this.random = new Random(15);
        this.tree = new IntervalAVLTree<Interval>();
        this.intervals = new ArrayList<Interval>();
    }

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < TEST_LENGTH; i++) {

            int a = random.nextInt(MAX);
            int b = a + random.nextInt(MAX_INT);

            Interval interval = new Interval(a, b);

            intervals.add(interval);
            tree.insert(interval, interval);
            //tree.printTree();
        }
    }

    @Test
    public void testIntersectsPoint() throws Exception {
        for (int i = 0; i < 100; i++) {
            int val = random.nextInt(MAX);
            int intersects = 0;

            for (Interval interval : intervals) {
                if (interval.intersects(val))
                    intersects++;
            }

            assertTrue(intersects == tree.intersects(val).size());
        }

    }

    @Test
    public void testIntersectsInterval() throws Exception {
        for (int i = 0; i < 100; i++) {
            Interval val = new Interval(random.nextInt(MAX), random.nextInt(MAX));
            int intersects = 0;

            for (Interval interval : intervals) {
                if (interval.intersects(val))
                    intersects++;
            }
            assertTrue(intersects == tree.intersects(val).size());
        }

    }
}
