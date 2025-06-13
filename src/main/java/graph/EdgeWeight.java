package graph;

import java.util.Objects;
import java.lang.Comparable;

public class EdgeWeight implements Comparable<EdgeWeight> {

    private Object edge;
    private Object weight;

    public EdgeWeight(Object edge, Object weight) {
        this.edge = edge;
        this.weight = weight;
    }

    public Object getEdge() {
        return edge;
    }

    public void setEdge(Object edge) {
        this.edge = edge;
    }

    public Object getWeight() {
        return weight;
    }

    public void setWeight(Object weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        if(weight==null) return "Edge="+edge;
        else return "Edge="+edge+". Weight="+weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeWeight that = (EdgeWeight) o;
        return Objects.equals(edge, that.edge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edge);
    }

    @Override
    public int compareTo(EdgeWeight other) {
        if (this.weight instanceof Integer && other.weight instanceof Integer) {
            return Integer.compare((Integer) this.weight, (Integer) other.weight);
        }
        if (this.weight == null && other.weight == null) return 0;
        if (this.weight == null) return -1;
        if (other.weight == null) return 1;

        throw new IllegalArgumentException("EdgeWeight comparison expects Integer weights. Found non-Integer or mixed types.");
    }
}