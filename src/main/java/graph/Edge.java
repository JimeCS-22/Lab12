package graph;

import java.util.Objects;

public class Edge {

    private Object elementA;
    private Object elementB;

    public Edge(Object elementA, Object elementB) {
        this.elementA = elementA;
        this.elementB = elementB;
    }

    public Object getElementA() {
        return elementA;
    }

    public void setElementA(Object elementA) {
        this.elementA = elementA;
    }

    public Object getElementB() {
        return elementB;
    }

    public void setElementB(Object elementB) {
        this.elementB = elementB;
    }

    @Override
    public String toString() {
        return "Edge(" + elementA + "-" + elementB + ")";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return (util.Utility.compare(elementA, edge.elementA) == 0 && util.Utility.compare(elementB, edge.elementB) == 0) ||
                (util.Utility.compare(elementA, edge.elementB) == 0 && util.Utility.compare(elementB, edge.elementA) == 0);
    }

    @Override
    public int hashCode() {
        Object firstCanonical = util.Utility.compare(elementA, elementB) < 0 ? elementA : elementB;
        Object secondCanonical = util.Utility.compare(elementA, elementB) < 0 ? elementB : elementA;
        return Objects.hash(firstCanonical, secondCanonical);
    }
}
