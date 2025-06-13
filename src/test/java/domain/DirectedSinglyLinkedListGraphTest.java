package domain;

import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectedSinglyLinkedListGraphTest {

    @Test
    void test() {

        try {

            DirectedSinglyLinkedListGraph graph = new DirectedSinglyLinkedListGraph();


            for (char i = 'a'; i <= 'j'; i++) {
                graph.addVertex(i);
            }


            graph.addEdgeWeight('a', 'b', "Juan");
            graph.addEdgeWeight('a', 'c', "Maria");
            graph.addEdgeWeight('a', 'd', "Pedro");
            graph.addEdgeWeight('b', 'f', "Laura");
            graph.addEdgeWeight('f', 'e', "Carlos");
            graph.addEdgeWeight('f', 'j', "Ana");
            graph.addEdgeWeight('c', 'g', "Luis");
            graph.addEdgeWeight('g', 'j', "Sofia");
            graph.addEdgeWeight('d', 'h', "Diego");
            graph.addEdgeWeight('h', 'i', "Elena");

            System.out.println(graph);
            System.out.println("-----------------------------------");

            System.out.println("\n--- Recorridos Iniciales del Grafo ---");
            System.out.println("Recorrido DFS: " + graph.dfs());
            System.out.println("Recorrido BFS: " + graph.bfs());
            System.out.println("--------------------------------------");

            System.out.println("\n--- Removiendo Vértices 'e', 'j', 'i' ---");
            graph.removeVertex('e');
            graph.removeVertex('j');
            graph.removeVertex('i');
            System.out.println("-----------------------------------------");

            System.out.println("\n--- Remociendo Aristas 'c-g', 'd-h', 'a-b' ---");
            graph.removeEdge('c', 'g');
            graph.removeEdge('d', 'h');
            graph.removeEdge('a', 'b');
            System.out.println("-----------------------------------------------");

            System.out.println("Recorrido DFS: " + graph.dfs());
            System.out.println("Recorrido BFS: " + graph.bfs());

            System.out.println(graph);


        } catch (GraphException | ListException | StackException | QueueException e) {
            e.printStackTrace();
        }
    }
}