package tt.euclid2i.probleminstance;

import tt.euclid2i.Region;
import tt.euclid2i.region.Rectangle;

import java.util.Collection;

/**
 * Created by Vojtech Letal on 2/26/14.
 */
public class EnvironmentImpl implements Environment {

    private Collection<Region> obstacles;
    private Rectangle bounds;

    public EnvironmentImpl(Collection<Region> obstacles, Rectangle bounds) {
        this.obstacles = obstacles;
        this.bounds = bounds;
    }

    public Collection<Region> getObstacles() {
        return obstacles;
    }

    public void setObstacles(Collection<Region> obstacles) {
        this.obstacles = obstacles;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
