/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tt.util.intervaltree;

/**
 * @author Alternative
 */
public class IntervalNode<V extends Comparable<V>> implements Comparable<IntervalNode<V>> {

    public int heightL, heightR, directionFromParent;
    public IntervalNode<V> left, right, parent;
    public V k;

    public double intervalMin;
    public double intervalMax;

    public IntervalNode(V k, int directionFromParent, IntervalNode parent) {
        this.k = k;
        this.left = null;
        this.right = null;
        this.directionFromParent = directionFromParent;
        this.parent = parent;
        this.heightL = -1;
        this.heightR = -1;
    }

    public IntervalNode(V k) {
        this.k = k;
        this.left = null;
        this.right = null;
        this.directionFromParent = 0;
        this.parent = null;
        this.heightL = -1;
        this.heightR = -1;
    }

    public void refreshUpperHeights() {
        if (this.parent != null) {
            if (directionFromParent == -1) {
                this.parent.heightL = max(this.heightL, this.heightR) + 1;
            } else {
                this.parent.heightR = max(this.heightL, this.heightR) + 1;
            }
            this.parent.refreshUpperHeights();
        }
    }


    @Override
    public String toString() {
        String side, ok;
        if (this.directionFromParent == 0) {
            side = "0";
        } else {
            if (this.directionFromParent > 0) {
                side = "R";
            } else {
                side = "L";
            }
        }
        if (this.isUnbalanced()) {
            ok = "xx";
        } else {
            ok = "oo";
        }
        return String.format("%s>%s> %d L:%d R:%d", ok, side, k, heightL, heightR);
    }

    public boolean isUnbalanced() {
        int difference = this.heightL - this.heightR;
        return difference < -1 || difference > 1;
    }

    private int max(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    @Override
    public int compareTo(IntervalNode<V> that) {
        return this.k.compareTo(that.k);
    }
}
