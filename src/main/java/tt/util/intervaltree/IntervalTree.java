/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tt.util.intervaltree;

/**
 * @author Alternative
 */
public class IntervalTree<V extends Comparable<V>> {

    public IntervalNode<V> root;

    public IntervalTree() {
        this.root = null;
    }

    private void rotateR(IntervalNode<V> A) {
        //TODO extract simple LR rotations from
        IntervalNode<V> E = A.left.right;
        IntervalNode<V> B = A.left;

        B.right = A;
        if (A.directionFromParent == 0) {
            root = B;
            B.parent = null;
            B.directionFromParent = 0;

        } else {
            if (A.directionFromParent < 0) {
                A.parent.left = B;
            } else {
                A.parent.right = B;
            }
            B.parent = A.parent;
            B.directionFromParent = A.directionFromParent;
        }
        A.parent = B;
        A.directionFromParent = 1;

        A.left = E;
        if (E != null) {
            E.parent = A;
            E.directionFromParent = -1;
            E.refreshUpperHeights();
        } else {
            A.heightL = -1;
            A.refreshUpperHeights();
        }

    }

    private void rotateL(IntervalNode<V> A) {
        IntervalNode<V> E = A.right.left;
        IntervalNode<V> B = A.right;

        B.left = A;
        if (A.directionFromParent == 0) {
            root = B;
            B.parent = null;
            B.directionFromParent = 0;
        } else {
            if (A.directionFromParent < 0) {
                A.parent.left = B;
            } else {
                A.parent.right = B;
            }
            B.parent = A.parent;
            B.directionFromParent = A.directionFromParent;
        }

        A.parent = B;
        A.directionFromParent = -1;
        A.right = E;
        if (E != null) {
            E.parent = A;
            E.directionFromParent = 1;
            E.refreshUpperHeights();
        } else {
            A.heightR = -1;
            A.refreshUpperHeights();
        }

    }

    public void insert(V k) {
        IntervalNode<V> node = root;

        if (root == null) {
            root = new IntervalNode<V>(k);
            node = root;

        } else {
            while (true) {
                if (node.k == k) {
                    return;
                }
                if (node.k.compareTo(k) > 0) {
                    if (node.left == null) {
                        node.left = new IntervalNode<V>(k, -1, node);
                        node = node.left;
                        break;
                    } else {
                        node = node.left;
                    }
                } else {
                    if (node.right == null) {
                        node.right = new IntervalNode<V>(k, 1, node);
                        node = node.right;
                        break;
                    } else {
                        node = node.right;
                    }
                }
            }
        }
        node.refreshUpperHeights();

        int childDirection = 0;
        int grandChildDirection = 0;

        IntervalNode<V> childNode = null;
        while (true) {
            if (node.isUnbalanced() && !(childDirection == 0 || grandChildDirection == 0)) {
                if (grandChildDirection == childDirection) {
                    if (childDirection == -1) {
                        rotateR(node);
                    } else {
                        rotateL(node);
                    }
                } else {
                    if (childDirection == -1) {
                        rotateL(childNode);
                        rotateR(node);

                    } else {
                        rotateR(childNode);
                        rotateL(node);
                    }
                }
                break;
            } else {
                if (node.directionFromParent != 0) {
                    grandChildDirection = childDirection;
                    childDirection = node.directionFromParent;
                    childNode = node;
                    node = node.parent;
                } else {
                    break;
                }

            }
        }
    }

    public void printTree() {
        printTree(this.root, 0);
        System.out.println();
    }

    private void printTree(IntervalNode<V> node, int depth) {
        if (node == null) {
            return;
        }

        for (int i = 0; i < depth; i++) {
            System.out.print(" ");
        }

        System.out.println(node);

        printTree(node.left, depth + 1);
        printTree(node.right, depth + 1);
    }
}
