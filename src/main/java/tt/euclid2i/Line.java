package tt.euclid2i;

public class Line {
    private static final long serialVersionUID = -2519868162204278196L;

    private Point start;
    private Point end;

    private double distance;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
        this.distance = start.distance(end);
    }

    public double getDistance() {
        return distance;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return String.format("(%s : %s)", start, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        if (end != null ? !end.equals(line.end) : line.end != null) return false;
        if (start != null ? !start.equals(line.start) : line.start != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
