package com.sample.java;
import java.util.*;
import java.util.stream.*;
public class Sample1 {
   public static void main(String[] args) {
      List<Integer> generate = Sample1.generate(10);
      System.out.println(generate);
   }
   public static List<Integer> generate(int series) {
      Set<Integer> set = new TreeSet<>();
      return Stream.iterate(1, i -> ++i)
              .filter(i -> i %2 != 0) // lambda expression
              .filter(i -> {
                  set.add(i);
                  return 0 == set.stream()
                           .filter(p -> p != 1)
                           .filter(p -> !Objects.equals(p, i))
                           .filter(p -> i % p == 0)
                           .count();
              })
              .limit(series)
            .collect(Collectors.toList());
   }
}