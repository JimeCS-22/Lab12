package domain;

import java.util.Objects;

public class Edge {

    private Object elementA; // Primer vértice de la arista
    private Object elementB; // Segundo vértice de la arista

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

    // CRUCIAL para grafos no dirigidos: (A,B) es lo mismo que (B,A)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        // Compara si (A==edge.A && B==edge.B) O (A==edge.B && B==edge.A)
        // Usamos tu Utility.compare para la comparación de los elementos.
        return (util.Utility.compare(elementA, edge.elementA) == 0 && util.Utility.compare(elementB, edge.elementB) == 0) ||
                (util.Utility.compare(elementA, edge.elementB) == 0 && util.Utility.compare(elementB, edge.elementA) == 0);
    }

    // CRUCIAL para grafos no dirigidos: hashCode de (A,B) debe ser igual al de (B,A)
    @Override
    public int hashCode() {
        // Para asegurar que (A,B) y (B,A) tengan el mismo hashCode,
        // puedes usar una forma canónica. Por ejemplo, siempre tomar el elemento
        // que es "menor" lexicográficamente (o según tu Utility.compare) como el primero
        // para calcular el hash.
        Object firstCanonical = util.Utility.compare(elementA, elementB) < 0 ? elementA : elementB;
        Object secondCanonical = util.Utility.compare(elementA, elementB) < 0 ? elementB : elementA;
        return Objects.hash(firstCanonical, secondCanonical);
    }
}
