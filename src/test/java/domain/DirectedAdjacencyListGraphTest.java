package domain;

import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectedAdjacencyListGraphTest {

    @Test
    void test() {

        try {

            //Crear e instanciar
            DirectedAdjacencyListGraph graph = new DirectedAdjacencyListGraph(50);
            for (char i = 'a'; i <= 'm'; i++) {
                graph.addVertex(i);
            }
            //Agregar vertices , aristas y pesos
            graph.addEdgeWeight('a', 'b', util.Utility.random(50)+10);
            graph.addEdgeWeight('a' , 'c' , util.Utility.random(50)+10);
            graph.addEdgeWeight('a', 'd', util.Utility.random(50)+10);
            graph.addEdgeWeight('b', 'e', util.Utility.random(50)+10);
            graph.addEdgeWeight('c', 'f', util.Utility.random(50)+10);
            graph.addEdgeWeight('d', 'g', util.Utility.random(50)+10);
            graph.addEdgeWeight('e' , 'h' , util.Utility.random(50)+10);
            graph.addEdgeWeight('f', 'i', util.Utility.random(50)+10);
            graph.addEdgeWeight('g' , 'j' , util.Utility.random(50)+10);
            graph.addEdgeWeight('h' , 'k' , util.Utility.random(50)+10);
            graph.addEdgeWeight('i' , 'l' , util.Utility.random(50)+10);
            graph.addEdgeWeight('j' , 'm' , util.Utility.random(50)+10);

            //To String y recorridos
            System.out.println(graph);  //toString
            System.out.println("DFS Transversal Tour: "+graph.dfs());
            System.out.println("BFS Transversal Tour: "+graph.bfs());

//            //eliminemos vertices
            System.out.println("\nVertex deleted: e");
            graph.removeVertex('e');
            System.out.println(graph);  //toString
            System.out.println("\n Vertex deleted: f");
            graph.removeVertex('f');
            System.out.println(graph);
            System.out.println("\n Vertex deleted: g");
            graph.removeVertex('g');
            System.out.println(graph);

            //suprima aristas
            System.out.println("Edge deleted: h---k");
            graph.removeEdge('h', 'k');

            System.out.println("Edge deleted: i---l");
            graph.removeEdge('i', 'l');

            System.out.println("Edge deleted: j---m");
            graph.removeEdge('j', 'm');

            //Mostrar por consola despues de la eliminacion
            System.out.println(graph);

        } catch (GraphException | ListException | StackException | QueueException e) {
            throw new RuntimeException(e);

        }
    }
    }