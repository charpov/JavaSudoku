package sudoku.util;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Memoizer that caches iterator elements. */
public class Memoizer<A> {
  private final Iterator<? extends A> sourceIterator;
  private final ArrayList<A> cache;

  public Memoizer(Iterator<? extends A> iterator) {
    this.sourceIterator = iterator;
    this.cache          = new ArrayList<>();
  }

  private final class MemoIterator implements Iterator<A> {
    private int out;

    public boolean hasNext() {
      return out < cache.size() || sourceIterator.hasNext();
    }

    public A next() {
      if (out < cache.size()) return cache.get(out++);
      if (!sourceIterator.hasNext()) throw new NoSuchElementException("empty iterator");
      A next = sourceIterator.next();
      cache.add(next);
      out++;
      return next;
    }
  }

  public Iterator<A> iterator() {
    return new MemoIterator();
  }

  public Stream<A> stream() {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
        iterator(), Spliterator.IMMUTABLE), false);
  }
}