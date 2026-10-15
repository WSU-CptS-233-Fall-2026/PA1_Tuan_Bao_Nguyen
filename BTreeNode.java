/**
 * The class representation for a B-Tree node
 * @author Srini Badri
 */
public class BTreeNode<E extends Comparable<E>> {

    /*
     * The order of the B-Tree. It is the maximum number of children a node can have.
     */
    int m;

    /*
     * Array of keys
     */
    E[] keys;

    /*
     * Array of child references
     */
    BTreeNode<E>[] children;

    /*
     * Number of keys currently stored in the node
     */
    int numKeys;

    /*
     * Indicates whether the node is a leaf
     */
    boolean leaf;

    /**
     * Constructor
     * @param m the tree order
     * @param leaf true if the node is a leaf
     */
    @SuppressWarnings("unchecked")
    public BTreeNode(int m, boolean leaf) {
        this.m = m;
        this.leaf = leaf;

        keys = (E[]) new Comparable[m - 1];
        if (this.leaf == true)
            children = null;
        else
            children = new BTreeNode[m];

        numKeys = 0;
    }
}
