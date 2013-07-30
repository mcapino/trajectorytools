package tt.euclidtime3i.discretization.softconstraints;

import tt.euclid2i.Point;

interface SeparationPenaltyFunction {
    double getPenalty(Point p1, Point p2);
}