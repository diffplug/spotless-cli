╔═ itRemovesUnusedImportsWithDefaultEngine ═╗
/*
 * Some license stuff.
 * Very official.
 */
package hello.world;

import mylib.UsedB;
import mylib.UsedA;

public class Java {
public static void main(String[] args) {
System.out.println("hello");
UsedB.someMethod();
UsedA.someMethod();
}
}
╔═ itRemovesWithExplicitCleanThatEngine ═╗
/*
 * Some license stuff.
 * Very official.
 */
package hello.world;

import mylib.UsedB;
import mylib.UsedA;

public class Java {
public static void main(String[] args) {
System.out.println("hello");
UsedB.someMethod();
UsedA.someMethod();
}
}
╔═ itRemovesWithExplicitDefaultEngine ═╗
/*
 * Some license stuff.
 * Very official.
 */
package hello.world;

import mylib.UsedB;
import mylib.UsedA;

public class Java {
public static void main(String[] args) {
System.out.println("hello");
UsedB.someMethod();
UsedA.someMethod();
}
}
╔═ [end of file] ═╗
