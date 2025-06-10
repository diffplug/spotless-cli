╔═ itCanExecuteAllMutators ═╗
package eu.solven.cleanthat.engine.java.refactorer.cases.do_not_format_me;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LiteralsFirstInComparisonsCases {

	public boolean isHardcoded(String input) {
		return "hardcoded".equals(input);
	}

	public boolean isPresent(Optional<?> optional) {
		return !optional.isEmpty();
	}

	public static boolean isPresent(List<String> list, Predicate<String> predicate) {
		Stream<String> stream = list.stream();
		return stream.anyMatch(predicate);
	}

	public static boolean isStringAAA(String str) {
		return "AAA".equals(str);
	}

	public static void clean(Collection collection) {
		collection.clear();
	}
}

╔═ itLetsEnableDraftMutators/excluding draft mutators ═╗
package eu.solven.cleanthat.engine.java.refactorer.cases.do_not_format_me;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LiteralsFirstInComparisonsCases {

	public boolean isHardcoded(String input) {
		return input.equals("hardcoded");
	}

	public boolean isPresent(Optional<?> optional) {
		return !optional.isEmpty();
	}

	public static boolean isPresent(List<String> list, Predicate<String> predicate) {
		Stream<String> stream = list.stream();
		return stream.anyMatch(predicate);
	}

	public static boolean isStringAAA(String str) {
		return str.equals("AAA");
	}

	public static void clean(Collection collection) {
		collection.clear();
	}
}

╔═ itLetsEnableDraftMutators/including draft mutators ═╗
package eu.solven.cleanthat.engine.java.refactorer.cases.do_not_format_me;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LiteralsFirstInComparisonsCases {

	public boolean isHardcoded(String input) {
		return input.equals("hardcoded");
	}

	public boolean isPresent(Optional<?> optional) {
		return !optional.isEmpty();
	}

	public static boolean isPresent(List<String> list, Predicate<String> predicate) {
		Stream<String> stream = list.stream();
		return stream.anyMatch(predicate);
	}

	public static boolean isStringAAA(String str) {
		return str.equals("AAA");
	}

	public static void clean(Collection collection) {
		collection.clear();
	}
}

╔═ itLetsEnableSpecificMutators ═╗
package eu.solven.cleanthat.engine.java.refactorer.cases.do_not_format_me;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LiteralsFirstInComparisonsCases {

	public boolean isHardcoded(String input) {
		return "hardcoded".equals(input);
	}

	public boolean isPresent(Optional<?> optional) {
		return !optional.isEmpty();
	}

	public static boolean isPresent(List<String> list, Predicate<String> predicate) {
		Stream<String> stream = list.stream();
		return stream.filter(predicate).findAny().isPresent();
	}

	public static boolean isStringAAA(String str) {
		return "AAA".equals(str);
	}

	public static void clean(Collection collection) {
		collection.removeAll(collection);
	}
}

╔═ itRunsWithDefaultOptions ═╗
package eu.solven.cleanthat.engine.java.refactorer.cases.do_not_format_me;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LiteralsFirstInComparisonsCases {

	public boolean isHardcoded(String input) {
		return input.equals("hardcoded");
	}

	public boolean isPresent(Optional<?> optional) {
		return !optional.isEmpty();
	}

	public static boolean isPresent(List<String> list, Predicate<String> predicate) {
		Stream<String> stream = list.stream();
		return stream.anyMatch(predicate);
	}

	public static boolean isStringAAA(String str) {
		return str.equals("AAA");
	}

	public static void clean(Collection collection) {
		collection.removeAll(collection);
	}
}

╔═ [end of file] ═╗
