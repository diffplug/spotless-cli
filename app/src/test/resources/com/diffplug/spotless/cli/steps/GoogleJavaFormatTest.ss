╔═ disablingFormattingJavadocWithGoogleJavaFormatWorks ═╗
import mylib.UsedA;
import mylib.UsedB;

public class Java {
  /**
   * Some javadoc.
   */
  public static void main(String[] args) {
    System.out.println(
        "A very very very very very very very very very very very very very very very very very very very very very long string that goes beyond the 100-character line length.");
    UsedB.someMethod();
    UsedA.someMethod();
  }
}

╔═ formattingWithAOSPStyleWorks ═╗
import mylib.UsedA;
import mylib.UsedB;

public class Java {
    /** Some javadoc. */
    public static void main(String[] args) {
        System.out.println(
                "A very very very very very very very very very very very very very very very very very very very very very long string that goes beyond the 100-character line length.");
        UsedB.someMethod();
        UsedA.someMethod();
    }
}

╔═ formattingWithGoogleJavaFormatWorks ═╗
import mylib.UsedA;
import mylib.UsedB;

public class Java {
  /** Some javadoc. */
  public static void main(String[] args) {
    System.out.println(
        "A very very very very very very very very very very very very very very very very very very very very very long string that goes beyond the 100-character line length.");
    UsedB.someMethod();
    UsedA.someMethod();
  }
}

╔═ reflowLongStringsWithGoogleJavaFormatWorks ═╗
import mylib.UsedA;
import mylib.UsedB;

public class Java {
  /** Some javadoc. */
  public static void main(String[] args) {
    System.out.println(
        "A very very very very very very very very very very very very very very very very very"
            + " very very very very long string that goes beyond the 100-character line length.");
    UsedB.someMethod();
    UsedA.someMethod();
  }
}

╔═ reorderImportsWithGoogleJavaFormatWorks ═╗
import mylib.UsedA;
import mylib.UsedB;

public class Java {
  /** Some javadoc. */
  public static void main(String[] args) {
    System.out.println(
        "A very very very very very very very very very very very very very very very very very very very very very long string that goes beyond the 100-character line length.");
    UsedB.someMethod();
    UsedA.someMethod();
  }
}

╔═ [end of file] ═╗
