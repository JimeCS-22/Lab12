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

            DirectedAdjacencyListGraph graph = new DirectedAdjacencyListGraph(50);
            for (char i = 'a'; i <= 'm'; i++) {
                graph.addVertex(i);
            }
            graph.addEdgeWeight('a', 'b', util.Utility.random(20)+2);
            graph.addEdgeWeight('a' , 'c' , util.Utility.random(20)+2);
            graph.addEdgeWeight('a', 'd', util.Utility.random(20)+2);
            graph.addEdgeWeight('b', 'e', util.Utility.random(20)+2);
            graph.addEdgeWeight('c', 'f', util.Utility.random(20)+2);
            graph.addEdgeWeight('d', 'g', util.Utility.random(20)+2);
            graph.addEdgeWeight('e' , 'h' , util.Utility.random(20)+2);
            graph.addEdgeWeight('f', 'i', util.Utility.random(20)+2);
            graph.addEdgeWeight('g' , 'j' , util.Utility.random(20)+2);
            graph.addEdgeWeight('h' , 'k' , util.Utility.random(20)+2);
            graph.addEdgeWeight('i' , 'l' , util.Utility.random(20)+2);
            graph.addEdgeWeight('j' , 'm' , util.Utility.random(20)+2);

            System.out.println(graph);  //toString
            System.out.println("DFS Transversal Tour: "+graph.dfs());
            System.out.println("BFS Transversal Tour: "+graph.bfs());

            //eliminemos vertices
            System.out.println("\nVertex deleted: a");
            graph.removeVertex('a');
            System.out.println(graph);  //toString
            System.out.println("Edge deleted: e---b");
            graph.removeEdge('b', 'e');
            System.out.println(graph);  //toString



        } catch (GraphException | ListException | StackException | QueueException e) {
            throw new RuntimeException(e);

        }
    }
    }