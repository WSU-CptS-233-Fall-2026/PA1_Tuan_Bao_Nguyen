/**
 * A B-Tree of order m. Duplicate keys are not stored.
 * Insert adds the key to a leaf and splits any node that gets too full.
 * Reference: Introduction to Algorithms (CLRS), Chapter 18, and class notes.
 *
 * @param <E> the type of keys stored in the tree
 * @author Tuan Bao Nguyen
 */
public class BTree<E extends Comparable<E>> {

    // order of the tree (maximum number of children per node)
    private int m;

    // root of the B-Tree
    private BTreeNode<E> root;

    /**
     * Result of splitting an overflowing node: the median key that must move
     * up into the parent and the new right sibling that holds the larger keys.
     */
    private static class Split<E extends Comparable<E>> {
        final E median;
        final BTreeNode<E> right;

        Split(E median, BTreeNode<E> right) {
            this.median = median;
            this.right = right;
        }
    }

    /**
     * Constructor
     * @param m the order of the tree (must be at least 3)
     * @throws IllegalArgumentException if m is less than 3
     */
    public BTree(int m) {
        if (m < 3) {
            throw new IllegalArgumentException("Order must be at least 3");
        }
        this.m = m;
        root = null;
    }

    /**
     * Performs an inorder traversal
     * @return string containing all keys in sorted order, separated by spaces
     */
    public String inorder() {
        StringBuilder sb = new StringBuilder();
        inorder(root, sb);
        return sb.toString().trim();
    }

    /**
     * Helper for inorder(): for each key, visits the child to its left, then
     * the key itself; finally visits the rightmost child.
     * @param node the root of the subtree to traverse (may be null)
     * @param sb   the buffer collecting the keys
     */
    private void inorder(BTreeNode<E> node, StringBuilder sb) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < node.numKeys; i++) {
            if (!node.leaf) {
                inorder(node.children[i], sb);
            }
            sb.append(node.keys[i]).append(' ');
        }
        if (!node.leaf) {
            inorder(node.children[node.numKeys], sb);
        }
    }

    /**
     * Search operation
     * @param x data to search
     * @return true if found, false otherwise
     */
    public boolean contains(E x) {
        return search(root, x);
    }

    /**
     * Helper for contains(): finds the first key that is >= x in the node.
     * If it equals x we are done; otherwise descend into the child at that index.
     * @param node the root of the subtree to search (may be null)
     * @param x    the key to look for
     * @return true if x is in the subtree
     */
    private boolean search(BTreeNode<E> node, E x) {
        if (node == null) {
            return false;
        }
        int i = 0;
        while (i < node.numKeys && x.compareTo(node.keys[i]) > 0) {
            i++;
        }
        if (i < node.numKeys && x.compareTo(node.keys[i]) == 0) {
            return true;
        }
        if (node.leaf) {
            return false;
        }
        return search(node.children[i], x);
    }

    /**
     * Insert operation. Does nothing if x is already in the tree.
     * @param x data to insert
     */
    public void insert(E x) {
        if (x == null || contains(x)) {
            return; // no nulls, no duplicates
        }

        // empty tree: the first key goes into a new leaf root
        if (root == null) {
            root = new BTreeNode<>(m, true);
            root.keys[0] = x;
            root.numKeys = 1;
            return;
        }

        Split<E> split = insert(root, x);

        // the root itself split: grow the tree by one level
        if (split != null) {
            BTreeNode<E> newRoot = new BTreeNode<>(m, false);
            newRoot.keys[0] = split.median;
            newRoot.children[0] = root;
            newRoot.children[1] = split.right;
            newRoot.numKeys = 1;
            root = newRoot;
        }
    }

    /**
     * Recursive insertion helper. Inserts x into the subtree rooted at node.
     * @param node the root of the subtree
     * @param x    the key to insert (known not to be in the tree)
     * @return a Split if node overflowed and was split, otherwise null
     */
    private Split<E> insert(BTreeNode<E> node, E x) {
        // find the position where x belongs
        int i = 0;
        while (i < node.numKeys && x.compareTo(node.keys[i]) > 0) {
            i++;
        }

        if (node.leaf) {
            return addKeyAndChild(node, i, x, null);
        }

        // internal node: insert into the proper child, then absorb any split
        Split<E> childSplit = insert(node.children[i], x);
        if (childSplit == null) {
            return null;
        }
        return addKeyAndChild(node, i, childSplit.median, childSplit.right);
    }

    /**
     * Adds a key at position pos of the node, together with the new right
     * child that goes just after it (null for leaves). If the node has no room,
     * it is split using temporary arrays that can hold the extra key and child.
     * @param node     the node receiving the key
     * @param pos      index where the key belongs
     * @param key      the key to add
     * @param newChild the child to the right of key, or null if node is a leaf
     * @return a Split if the node had to be split, otherwise null
     */
    @SuppressWarnings("unchecked")
    private Split<E> addKeyAndChild(BTreeNode<E> node, int pos, E key,
                                    BTreeNode<E> newChild) {
        // Case 1: room available, shift and insert in place
        if (node.numKeys < m - 1) {
            for (int j = node.numKeys; j > pos; j--) {
                node.keys[j] = node.keys[j - 1];
            }
            node.keys[pos] = key;
            if (!node.leaf) {
                for (int j = node.numKeys + 1; j > pos + 1; j--) {
                    node.children[j] = node.children[j - 1];
                }
                node.children[pos + 1] = newChild;
            }
            node.numKeys++;
            return null;
        }

        // Case 2: node is full. Copy everything into temp arrays with the new
        // key (m keys) and new child (m + 1 children) in place.
        E[] tmpKeys = (E[]) new Comparable[m];
        BTreeNode<E>[] tmpKids = node.leaf ? null : new BTreeNode[m + 1];

        for (int j = 0, k = 0; j < m; j++) {
            if (j == pos) {
                tmpKeys[j] = key;
            } else {
                tmpKeys[j] = node.keys[k++];
            }
        }
        if (!node.leaf) {
            for (int j = 0, k = 0; j <= m; j++) {
                if (j == pos + 1) {
                    tmpKids[j] = newChild;
                } else {
                    tmpKids[j] = node.children[k++];
                }
            }
        }

        // split around the median
        int mid = m / 2;
        BTreeNode<E> right = new BTreeNode<>(m, node.leaf);

        // left node keeps keys [0, mid); clear the slots that moved out
        for (int j = 0; j < mid; j++) {
            node.keys[j] = tmpKeys[j];
        }
        for (int j = mid; j < m - 1; j++) {
            node.keys[j] = null;
        }
        node.numKeys = mid;

        // right node gets keys (mid, m)
        int rightCount = m - mid - 1;
        for (int j = 0; j < rightCount; j++) {
            right.keys[j] = tmpKeys[mid + 1 + j];
        }
        right.numKeys = rightCount;

        // distribute children for internal nodes
        if (!node.leaf) {
            for (int j = 0; j <= mid; j++) {
                node.children[j] = tmpKids[j];
            }
            for (int j = mid + 1; j < m; j++) {
                node.children[j] = null;
            }
            for (int j = 0; j <= rightCount; j++) {
                right.children[j] = tmpKids[mid + 1 + j];
            }
        }

        return new Split<>(tmpKeys[mid], right);
    }
}
