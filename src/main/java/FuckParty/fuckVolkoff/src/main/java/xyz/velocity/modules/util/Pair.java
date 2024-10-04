package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import java.util.Objects;

public class Pair <A,B> {
    public A first;
    public B second;

    public Pair(A first, B second){
        this.first = first;
        this.second = second;
    }

    public void setFirst(A a){
        this.first = a;
    }
    public void setSecond(B b){
        this.second = b;
    }
    public A getFirst(){
        return this.first;
    }
    public B getSecond(){
        return this.second;
    }
    public int hashCode(){
        return Objects.hash(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(this.first, pair.first) && Objects.equals(this.second, pair.second);
    }

    public static <A,B> Pair<A,B> of(A first, B second) {
        return new Pair<>(first, second);
    }

    public static class EitherPair<A,B> extends Pair<A,B> {

        public EitherPair(A first, B second) {
            super(first, second);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            EitherPair<?, ?> pair = (EitherPair<?, ?>) o;
            return (Objects.equals(this.first, pair.first) && Objects.equals(this.second, pair.second)) || (Objects.equals(this.first, pair.second) && Objects.equals(this.second, pair.first));
        }

        public static <A,B> EitherPair<A,B> of(A first, B second) {
            return new EitherPair<>(first, second);
        }
    }

}
