package graph;

import domain.list.SinglyLinkedList;

public class Vertex {
    public Object data;
    private boolean visited; //para los recorridos DFS, BFS
    public SinglyLinkedList edgesList; //lista de aristas

    //Constructor
    public Vertex(Object data){
        this.data = data;
        this.visited = false;
        this.edgesList = new SinglyLinkedList();
    }

    public Object getData() { // If data is private
        return data;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return util.Utility.compare(data, vertex.data) == 0; // Or simply data.equals(vertex.data)
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }
}
