package tt.discrete;

public interface Τrajectory<S> {
    public int getMinTime();
    public int getMaxTime();
    public S get(int t);
}
