package sudoku.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/** FlatMap extension on iterators. */
public class FlatMap<A, B> implements Iterator<B> {
  private Iterator<A> iterator; // null when exhausted
  private Function<? super A, ? extends Iterator<? extends B>> f;
  private Iterator<? extends B> current;

  private FlatMap(Iterator<A> iterator, Function<? super A, ? extends Iterator<? extends B>> f) {
    this.iterator = iterator;
    this.f        = f;
  }

  /** FlatMap operation. */
  public static <A, B> Iterator<B> flatMap(Iterator<A> iterator, 
                                           Function<? super A, ? extends Iterator<? extends B>> f) {
    return new FlatMap<>(iterator, f);
  }

  public boolean hasNext() {
    if (iterator == null) return false;
    while (current == null || !current.hasNext()) {
      if (!iterator.hasNext()) {
        iterator = null;
        f        = null; // for GC
        current  = null; // for GC
        return false;
      }
      current = f.apply(iterator.next());
    }
    return true;
  }

  public B next() {
    if (!hasNext()) throw new NoSuchElementException("empty iterator");
    assert current != null && current.hasNext();
    return current.next();
  }
}
