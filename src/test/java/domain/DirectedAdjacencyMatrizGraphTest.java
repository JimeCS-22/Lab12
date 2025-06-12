package domain;

import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import org.junit.jupiter.api.Test;

class DirectedAdjacencyMatrizGraphTest {

    @Test
    void test(){

        try {

            DirectedAdjacencyMatrixGraph graph = new DirectedAdjacencyMatrixGraph(50);
            for (char i = 'a'; i <= 'e'; i++) {
                graph.addVertex(i);
            }
            graph.addEdgeWeight('a', 'b', util.Utility.random(20)+2);
            graph.addEdgeWeight('b' , 'a' , util.Utility.random(20)+2);
            graph.addEdgeWeight('a', 'c', util.Utility.random(20)+2);
            graph.addEdgeWeight('a', 'd', util.Utility.random(20)+2);
            graph.addEdgeWeight('b', 'e', util.Utility.random(20)+2);
            graph.addEdgeWeight('c', 'd', util.Utility.random(20)+2);
            graph.addEdgeWeight('d' , 'c' , util.Utility.random(20)+2);
            graph.addEdgeWeight('c', 'e', util.Utility.random(20)+2);
            graph.addEdgeWeight('d' , 'd' , util.Utility.random(20)+2);

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
