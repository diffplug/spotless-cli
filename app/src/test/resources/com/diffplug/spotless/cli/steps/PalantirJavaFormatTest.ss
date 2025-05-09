╔═ itFormatsJavadoc ═╗
import mylib.Unused;
import mylib.UsedA;
import mylib.UsedB;

/**
 * This is a test class with a long unformatted JavaDoc description. Lorem ipsum dolor sit amet, consectetur adipiscing
 * elit. Vestibulum pulvinar condimentum elit, eget mollis magna sollicitudin in. Aenean pharetra nunc nec luctus
 * consequat. Donec nec tincidunt quam, in auctor ipsum. Nam in sem orci. Maecenas interdum posuere orci a semper. Cras
 * vulputate blandit metus, nec semper urna porttitor at. Praesent velit turpis, consequat in cursus eget, posuere eget
 * magna. Interdum et malesuada fames ac ante ipsum primis in faucibus. Pellentesque ante eros, sagittis sed tempus nec,
 * rutrum ac arcu. Sed porttitor quam at enim commodo dictum. Sed fringilla tincidunt ex in aliquet.
 *
 * @author https://www.lipsum.com/
 * @since 0.0.2
 */
public class Java {
    /**
     * A very simple method that I really like a lot?
     *
     * <p>Care for more details?
     *
     * <ul>
     *   <li>Too
     *   <li>bad
     *   <li>I
     *   <li>don't
     *   <li>have
     *   <li>any
     * </ul>
     *
     * @param args Useless args, but see {@link Unused}, perhaps even {@link UsedA} or even {@link UsedB b }?
     */
    public static void main(String[] args) {
        System.out.println(
                "A very very very very very very very very very very very very very very very very very very very very very long string that goes beyond the 100-character line length.");
        UsedB.someMethod();
        UsedA.someMethod();
    }
}

╔═ itFormatsTextBlocks ═╗
import mylib.UsedA;
import mylib.UsedB;

public class Java {
    public static void main(String[] args) {
        var a = """
          Howdy
          Partner!
        """;
        System.out.println(a);
        UsedB.someMethod();
        UsedA.someMethod();
    }
}

╔═ itFormatsWithDefaultOptions ═╗
import mylib.Unused;
import mylib.UsedA;
import mylib.UsedB;

/** This is a test class with a long unformatted JavaDoc description. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum pulvinar condimentum elit, eget mollis magna sollicitudin in. Aenean pharetra nunc nec luctus consequat. Donec nec tincidunt quam, in auctor ipsum. Nam in sem orci. Maecenas interdum posuere orci a semper. Cras vulputate blandit metus, nec semper urna porttitor at. Praesent velit turpis, consequat in cursus eget, posuere eget magna. Interdum et malesuada fames ac ante ipsum primis in faucibus. Pellentesque ante eros, sagittis sed tempus nec, rutrum ac arcu. Sed porttitor quam at enim commodo dictum. Sed fringilla tincidunt ex in aliquet.
 *             @author              https://www.lipsum.com/
 *    @since  0.0.2
 */
public class Java {
    /**
     * A very simple method that I
     * really
     * like
     * a lot?
     *
     * Care for more details? <ul><li>Too</li><li>bad</li>
     * <li>I</li><li>don't</li><li>have</li>
     *    <li>any</li>
     * </ul>
     *
     *    @param args Useless args, but
     * see   {@link Unused}, perhaps even {@link UsedA} or even {@link UsedB   b }?
     */
    public static void main(String[] args) {
        System.out.println(
                "A very very very very very very very very very very very very very very very very very very very very very long string that goes beyond the 100-character line length.");
        UsedB.someMethod();
        UsedA.someMethod();
    }
}

╔═ itFormatsWithSelectedStyle/AOSP ═╗
import mylib.Unused;
import mylib.UsedA;
import mylib.UsedB;

/** This is a test class with a long unformatted JavaDoc description. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum pulvinar condimentum elit, eget mollis magna sollicitudin in. Aenean pharetra nunc nec luctus consequat. Donec nec tincidunt quam, in auctor ipsum. Nam in sem orci. Maecenas interdum posuere orci a semper. Cras vulputate blandit metus, nec semper urna porttitor at. Praesent velit turpis, consequat in cursus eget, posuere eget magna. Interdum et malesuada fames ac ante ipsum primis in faucibus. Pellentesque ante eros, sagittis sed tempus nec, rutrum ac arcu. Sed porttitor quam at enim commodo dictum. Sed fringilla tincidunt ex in aliquet.
 *             @author              https://www.lipsum.com/
 *    @since  0.0.2
 */
public class Java {
    /**
     * A very simple method that I
     * really
     * like
     * a lot?
     *
     * Care for more details? <ul><li>Too</li><li>bad</li>
     * <li>I</li><li>don't</li><li>have</li>
     *    <li>any</li>
     * </ul>
     *
     *    @param args Useless args, but
     * see   {@link Unused}, perhaps even {@link UsedA} or even {@link UsedB   b }?
     */
    public static void main(String[] args) {
        System.out.println(
                "A very very very very very very very very very very very very very very very very very very very very very long string that goes beyond the 100-character line length.");
        UsedB.someMethod();
        UsedA.someMethod();
    }
}

╔═ itFormatsWithSelectedStyle/GOOGLE ═╗
import mylib.Unused;
import mylib.UsedA;
import mylib.UsedB;

/** This is a test class with a long unformatted JavaDoc description. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum pulvinar condimentum elit, eget mollis magna sollicitudin in. Aenean pharetra nunc nec luctus consequat. Donec nec tincidunt quam, in auctor ipsum. Nam in sem orci. Maecenas interdum posuere orci a semper. Cras vulputate blandit metus, nec semper urna porttitor at. Praesent velit turpis, consequat in cursus eget, posuere eget magna. Interdum et malesuada fames ac ante ipsum primis in faucibus. Pellentesque ante eros, sagittis sed tempus nec, rutrum ac arcu. Sed porttitor quam at enim commodo dictum. Sed fringilla tincidunt ex in aliquet.
 *             @author              https://www.lipsum.com/
 *    @since  0.0.2
 */
public class Java {
  /**
   * A very simple method that I
   * really
   * like
   * a lot?
   *
   * Care for more details? <ul><li>Too</li><li>bad</li>
   * <li>I</li><li>don't</li><li>have</li>
   *    <li>any</li>
   * </ul>
   *
   *    @param args Useless args, but
   * see   {@link Unused}, perhaps even {@link UsedA} or even {@link UsedB   b }?
   */
  public static void main(String[] args) {
    System.out.println(
        "A very very very very very very very very very very very very very very very very very very very very very long string that goes beyond the 100-character line length.");
    UsedB.someMethod();
    UsedA.someMethod();
  }
}

╔═ itFormatsWithSelectedStyle/PALANTIR ═╗
import mylib.Unused;
import mylib.UsedA;
import mylib.UsedB;

/** This is a test class with a long unformatted JavaDoc description. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum pulvinar condimentum elit, eget mollis magna sollicitudin in. Aenean pharetra nunc nec luctus consequat. Donec nec tincidunt quam, in auctor ipsum. Nam in sem orci. Maecenas interdum posuere orci a semper. Cras vulputate blandit metus, nec semper urna porttitor at. Praesent velit turpis, consequat in cursus eget, posuere eget magna. Interdum et malesuada fames ac ante ipsum primis in faucibus. Pellentesque ante eros, sagittis sed tempus nec, rutrum ac arcu. Sed porttitor quam at enim commodo dictum. Sed fringilla tincidunt ex in aliquet.
 *             @author              https://www.lipsum.com/
 *    @since  0.0.2
 */
public class Java {
    /**
     * A very simple method that I
     * really
     * like
     * a lot?
     *
     * Care for more details? <ul><li>Too</li><li>bad</li>
     * <li>I</li><li>don't</li><li>have</li>
     *    <li>any</li>
     * </ul>
     *
     *    @param args Useless args, but
     * see   {@link Unused}, perhaps even {@link UsedA} or even {@link UsedB   b }?
     */
    public static void main(String[] args) {
        System.out.println(
                "A very very very very very very very very very very very very very very very very very very very very very long string that goes beyond the 100-character line length.");
        UsedB.someMethod();
        UsedA.someMethod();
    }
}

╔═ [end of file] ═╗
