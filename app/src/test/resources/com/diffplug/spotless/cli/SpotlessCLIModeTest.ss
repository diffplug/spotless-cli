╔═ applyModeChangesFilesAndExitsWithCode0 ═╗
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

╔═ checkModeLeavesFilesUnchangedAndExitsWithCode1 ═╗

import mylib.Unused;
import mylib.UsedB;
import mylib.UsedA;

public class Java {
/**
 * Some javadoc.
 */
public static void main(String[] args) {
System.out.println("A very very very very very very very very very very very very very very very very very very very very very long string that goes beyond the 100-character line length.");
UsedB.someMethod();
UsedA.someMethod();
}
}
╔═ [end of file] ═╗
