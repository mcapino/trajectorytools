package tt.euclid2i.probleminstance;

import java.util.Collection;

import tt.euclid2i.Region;
import tt.euclid2i.region.Rectangle;

public interface Environment {

    public Collection<Region> getObstacles();

    public Rectangle getBounds();

}
