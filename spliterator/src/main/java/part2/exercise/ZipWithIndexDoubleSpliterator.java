package part2.exercise;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class ZipWithIndexDoubleSpliterator extends Spliterators.AbstractSpliterator<IndexedDoublePair> {
    private final OfDouble inner;
    private int currentIndex;

    public ZipWithIndexDoubleSpliterator(OfDouble inner) {
        this(0, inner);
    }

    private ZipWithIndexDoubleSpliterator(int firstIndex, OfDouble inner) {
        super(inner.estimateSize(), inner.characteristics());
        currentIndex = firstIndex;
        this.inner = inner;
    }

    @Override
    public int characteristics() {
        return inner.characteristics();
    }

    @Override
    public boolean tryAdvance(Consumer<? super IndexedDoublePair> action) {
        return inner.tryAdvance((double e) -> action.accept(new IndexedDoublePair(currentIndex++, e)));
    }

    @Override
    public void forEachRemaining(Consumer<? super IndexedDoublePair> action) {
        inner.forEachRemaining((double e) -> action.accept(new IndexedDoublePair(currentIndex++, e)));
    }

    @Override
    public Spliterator<IndexedDoublePair> trySplit() {
        if (inner.hasCharacteristics(Spliterator.SIZED | Spliterator.SUBSIZED)) {
            OfDouble resInner = inner.trySplit();

            if (resInner == null) {
                return null;
            }

            int currPosition = currentIndex;
            currentIndex += resInner.estimateSize();
            return new ZipWithIndexDoubleSpliterator(currPosition, inner.trySplit());
        }

        return super.trySplit();
    }

    @Override
    public long estimateSize() {
        return inner.estimateSize();
    }
}
