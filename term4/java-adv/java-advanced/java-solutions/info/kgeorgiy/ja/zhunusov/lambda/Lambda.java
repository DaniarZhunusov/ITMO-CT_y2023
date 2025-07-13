package info.kgeorgiy.ja.zhunusov.lambda;

import info.kgeorgiy.java.advanced.lambda.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Lambda implements EasyLambda {

    @Override
    public <T> Spliterator<T> binaryTreeSpliterator(Trees.Binary<T> tree) {
        return new BinaryTreeSpliterator<>(tree);
    }

    @Override
    public <T> Spliterator<T> sizedBinaryTreeSpliterator(Trees.SizedBinary<T> tree) {
        return new SizedBinaryTreeSpliterator<>(tree);
    }

    @Override
    public <T> Spliterator<T> naryTreeSpliterator(Trees.Nary<T> tree) {
        return new NaryTreeSpliterator<>(tree);
    }

    @Override
    public <T> Collector<T, ?, Optional<T>> first() {
        return Collectors.reducing((a, b) -> a);
    }

    @Override
    public <T> Collector<T, ?, Optional<T>> last() {
        return Collectors.reducing((a, b) -> b);
    }

    @Override
    public <T> Collector<T, ?, Optional<T>> middle() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> list.isEmpty() ? Optional.empty() : Optional.of(list.get(list.size() / 2))
        );
    }

    @Override
    public Collector<CharSequence, ?, String> commonPrefix() {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
            if (list.isEmpty()) return "";
            return findCommon(list.toArray(new CharSequence[0]), true);
        });
    }

    @Override
    public Collector<CharSequence, ?, String> commonSuffix() {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
            if (list.isEmpty()) return "";
            return findCommon(list.toArray(new CharSequence[0]), false);
        });
    }

    private String findCommon(CharSequence[] sequences, boolean prefix) {
        String result = sequences[0].toString();
        for (CharSequence seq : sequences) {
            String str = seq.toString();
            int minLen = Math.min(result.length(), str.length());
            int i = 0;
            while (i < minLen &&
                    (prefix ? result.charAt(i) == str.charAt(i) : result.charAt(result.length() - 1 - i) == str.charAt(str.length() - 1 - i))) {
                i++;
            }
            result = prefix ? result.substring(0, i) : result.substring(result.length() - i);
            if (result.isEmpty()) break;
        }
        return result;
    }

    private abstract static class AbstractTreeSpliterator<T, N> implements Spliterator<T> {
        protected final Deque<N> stack;
        protected final long size;

        protected AbstractTreeSpliterator(Deque<N> stack, long size) {
            this.stack = stack;
            this.size = size;
        }

        @Override
        public long estimateSize() {
            return size;
        }

        @Override
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE;
        }

        //note -- как и в прошлых двух, в абстрактный сплититератор можно вынести и tryAdvance и trySplit

    }

    private static class BinaryTreeSpliterator<T> extends AbstractTreeSpliterator<T, Trees.Binary<T>> {
        BinaryTreeSpliterator(Trees.Binary<T> tree) {
            super(new ArrayDeque<>(), calculateSize(tree));
            if (tree != null) {
                stack.push(tree);
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            while (!stack.isEmpty()) {
                Trees.Binary<T> node = stack.pop();

                if (node instanceof Trees.Leaf<T> leaf) {
                    action.accept(leaf.value());
                    return true;
                } else if (node instanceof Trees.Binary.Branch<T> branch) {
                    if (branch.right() != null) {
                        stack.push(branch.right());
                    }
                    if (branch.left() != null) {
                        stack.push(branch.left());
                    }
                }
            }
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            if (stack.isEmpty()) {
                return null;
            }
            Trees.Binary<T> node = stack.pop();
            if (node instanceof Trees.Binary.Branch<T> branch) {
                stack.push(branch.right());
                return new BinaryTreeSpliterator<>(branch.left());
            } else if (node instanceof Trees.Leaf<T> leaf) {
                return new BinaryTreeSpliterator<>(leaf);
            }
            return null;
        }

        private static <T> long calculateSize(Trees.Binary<T> tree) {
            if (tree == null) {
                return 0;
            }
            if (tree instanceof Trees.Leaf<T>) {
                return 1;
            } else if (tree instanceof Trees.Binary.Branch<T> branch) {
                return calculateSize(branch.left()) + calculateSize(branch.right());
            }
            return 0;
        }
    }

    private static class SizedBinaryTreeSpliterator<T> extends AbstractTreeSpliterator<T, Trees.SizedBinary<T>> {
        SizedBinaryTreeSpliterator(Trees.SizedBinary<T> tree) {
            super(new ArrayDeque<>(), tree.size());
            stack.push(tree);
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            while (!stack.isEmpty()) {
                Trees.SizedBinary<T> node = stack.pop();

                if (node instanceof Trees.Leaf<T> leaf) {
                    action.accept(leaf.value());
                    return true;
                } else if (node instanceof Trees.SizedBinary.Branch<T> branch) {
                    if (branch.right() != null) {
                        stack.push(branch.right());
                    }
                    if (branch.left() != null) {
                        stack.push(branch.left());
                    }
                }
            }
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            if (stack.isEmpty()) {
                return null;
            }
            Trees.SizedBinary<T> node = stack.pop();
            if (node instanceof Trees.SizedBinary.Branch<T> branch) {
                stack.push(branch.right());
                return new SizedBinaryTreeSpliterator<>(branch.left());
            } else if (node instanceof Trees.Leaf<T> leaf) {
                return new SizedBinaryTreeSpliterator<>(leaf);
            }
            return null;
        }
    }

    private static class NaryTreeSpliterator<T> extends AbstractTreeSpliterator<T, Trees.Nary<T>> {
        //note -- у вас код работает крайне медленно и фактически не проходит TL.
        // В конструкторах новых Spliterator-ов вызывается calculateSize, который проходит по всем узлам поддерева,
        // и если дерево большое или сплиты выполняются часто, то этот дополнительный обход становится вычислительно затратным.
        // Особенно это актуально для NaryTreeSpliterator, он же и работает дольше всех.
        NaryTreeSpliterator(Trees.Nary<T> tree) {
            super(new ArrayDeque<>(), calculateSize(tree));
            if (tree != null) {
                stack.push(tree);
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            while (!stack.isEmpty()) {
                Trees.Nary<T> node = stack.pop();

                if (node instanceof Trees.Leaf<T> leaf) {
                    action.accept(leaf.value());
                    return true;
                } else if (node instanceof Trees.Nary.Node<T> naryNode) {
                    List<Trees.Nary<T>> children = naryNode.children();
                    ListIterator<Trees.Nary<T>> iterator = children.listIterator(children.size());
                    while (iterator.hasPrevious()) {
                        stack.push(iterator.previous());
                    }
                }
            }
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            if (stack.isEmpty()) {
                return null;
            }
            Trees.Nary<T> node = stack.pop();
            if (node instanceof Trees.Nary.Node<T> naryNode) {
                List<Trees.Nary<T>> children = naryNode.children();
                int mid = children.size() / 2;
                if (mid > 0) {
                    List<Trees.Nary<T>> leftChildren = children.subList(0, mid);
                    List<Trees.Nary<T>> rightChildren = children.subList(mid, children.size());

                    stack.push(new Trees.Nary.Node<>(rightChildren));
                    return new NaryTreeSpliterator<>(new Trees.Nary.Node<>(leftChildren));
                }
            }
            return null;
        }

        private static <T> long calculateSize(Trees.Nary<T> tree) {
            if (tree == null) {
                return 0;
            }
            if (tree instanceof Trees.Leaf<T>) {
                return 1;
            } else if (tree instanceof Trees.Nary.Node<T> nodeBranch) {
                long size = 0;
                for (Trees.Nary<T> child : nodeBranch.children()) {
                    size += calculateSize(child);
                }
                return size;
            }
            return 0;
        }
    }
}
