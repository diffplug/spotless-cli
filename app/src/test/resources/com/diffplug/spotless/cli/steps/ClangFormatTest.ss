╔═ itFormatsFileType/Test.java ═╗
public class Java {
  public static void main(String[] args) { System.out.println("hello"); }
}
╔═ itFormatsFileType/Test.js ═╗
var numbers = [
  1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
];

const p = {
  first : "Peter",
  last : "Pan",
  get fullName() { return this.first + " " + this.last; }
};

const str = "Hello, world!";

var str2 = str.charAt(3) + str[0];

var multilinestr = "Hello \
World";

function test(a, b = "world") {
  let combined = a + b;
  return combined
};

test("Hello");

╔═ itFormatsFileType/Test.m ═╗
- (int)method:(int)i {
  return [self testing_123:i];
}
╔═ itFormatsFileType/Test.proto ═╗
message Testing {
  required string field1 = 1;
  required int32 field2 = 2;
  optional string field3 = 3;
}
╔═ itFormatsFileType/test.c ═╗
#include <stdio.h>
int main() {
  printf("Testing 123");
  return 0;
}
╔═ itFormatsFileType/test.cs ═╗
using System;
using System.Text;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Linq;

namespace Testing {
class Program {
  static void Main(string[] args) {
    string message = "Testing 1, 2, 3";
    Console.WriteLine(message);
  }
}
}
╔═ itFormatsWithSpecificStyle ═╗
public class Java {
  public static void main(String[] args) {
    System.out.println("hello");
  }
}
╔═ [end of file] ═╗
