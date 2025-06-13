package domain;

import java.util.Objects;

public class EdgeWeight {

    private Object edge; //arista
    private Object weight; //peso

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
        if (o == null || getClass() != o.getClass()) return false;
        EdgeWeight that = (EdgeWeight) o;
        return Objects.equals(edge, that.edge) && Objects.equals(weight, that.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edge, weight);
    }


    public int compareTo(EdgeWeight other) {
        // Asumiendo que el peso es siempre un Integer
        return Integer.compare((Integer) this.weight, (Integer) other.weight);
    }
}
