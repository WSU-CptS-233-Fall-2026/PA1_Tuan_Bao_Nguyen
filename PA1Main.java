/*
 * Programming Assignment #1 - B-Tree
 *
 * Name:    Tuan Bao Nguyen
 * Course:  CPT_S 233
 * Date:    10/14/2026
 *
 * Purpose: Creates and tests a B-Tree (see BTree.java). The program first
 *          runs the provided sample data, then some additional automated
 *          tests (duplicates, ordering, empty tree, several tree orders),
 *          and finally lets the user search the tree interactively.
 *          Enter a non-integer (or end of input) to quit.
 */

/**
 * The main program for creating and testing B-Trees
 * @author Srini Badri
 */
public class PA1Main {

    /**
     * The entry point
     * @param args command line arguments
     */
    public static void main(String[] args) {

        // create an empty B-Tree
        BTree<Integer> tree = new BTree<>(5);

        // insert sample data
        for (int i = 1; i <= 10; i++) {

            int x = i * 10;
            tree.insert(x);

            x = 200 - i * 10;
            tree.insert(x);
        }

        System.out.println("In-order Traversal");

        String s = tree.inorder();
        System.out.println(s);

        // additional test cases
        runTests();

        java.util.Scanner scan =
                new java.util.Scanner(System.in);

        while (true) {

            System.out.print(
                "Enter an integer value to search for: ");

            // stop on end of input or a non-integer
            if (!scan.hasNextInt()) {
                break;
            }
            int y = scan.nextInt();

            boolean found = tree.contains(y);

            if (found)
                System.out.println(
                        "Data " + y +
                        " is present in the tree");
            else
                System.out.println(
                        "Data " + y +
                        " is NOT present in the tree");
        }
    }

    /**
     * Additional automated tests. Prints PASS or FAIL for each one.
     */
    private static void runTests() {
        System.out.println("\nAdditional tests");

        // empty tree
        BTree<Integer> empty = new BTree<>(4);
        check("empty tree has empty traversal", empty.inorder().equals(""));
        check("empty tree contains nothing", !empty.contains(1));

        // duplicates are ignored
        BTree<Integer> dup = new BTree<>(3);
        for (int i = 0; i < 3; i++) {
            dup.insert(7);
            dup.insert(3);
        }
        check("duplicates stored once", dup.inorder().equals("3 7"));

        // ascending, descending and shuffled input for several orders
        for (int m = 3; m <= 8; m++) {
            BTree<Integer> asc = new BTree<>(m);
            BTree<Integer> desc = new BTree<>(m);
            BTree<Integer> mixed = new BTree<>(m);
            StringBuilder expected = new StringBuilder();
            for (int i = 1; i <= 100; i++) {
                asc.insert(i);
                desc.insert(101 - i);
                mixed.insert((i * 37) % 101); // visits 1..100 in scrambled order
                expected.append(i).append(i < 100 ? " " : "");
            }
            check("order " + m + " ascending", asc.inorder().equals(expected.toString()));
            check("order " + m + " descending", desc.inorder().equals(expected.toString()));
            check("order " + m + " scrambled", mixed.inorder().equals(expected.toString()));
            check("order " + m + " contains/missing",
                    mixed.contains(1) && mixed.contains(100)
                    && !mixed.contains(0) && !mixed.contains(101));
        }

        // strings work too, since the tree is generic
        BTree<String> words = new BTree<>(4);
        for (String w : new String[] {"pear", "apple", "fig", "kiwi", "apple", "banana"}) {
            words.insert(w);
        }
        check("string keys", words.inorder().equals("apple banana fig kiwi pear"));
        System.out.println();
    }

    /**
     * Prints the result of a single test.
     * @param name   description of the test
     * @param passed whether the test passed
     */
    private static void check(String name, boolean passed) {
        System.out.println((passed ? "PASS: " : "FAIL: ") + name);
    }
}
