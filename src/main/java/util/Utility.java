package util;

import graph.EdgeWeight;
import graph.Vertex;

import java.text.DecimalFormat;
import java.util.Random;

public class Utility {

    //static init
    static {
    }

    public static String format(double value){
        return new DecimalFormat("###,###,###.##").format(value);
    }
    public static String $format(double value){
        return new DecimalFormat("$###,###,###.##").format(value);
    }

    public static void fill(int[] a, int bound) {
        for (int i = 0; i < a.length; i++) {
            a[i] = new Random().nextInt(bound);
        }
    }

    public static int random(int bound) {
        return new Random().nextInt(bound);
    }

    // In util.Utility.java
    public static int compare(Object a, Object b) {
        switch (instanceOf(a, b)){
            case "Integer":
                Integer int1 = (Integer)a; Integer int2 = (Integer)b;
                return int1 < int2 ? -1 : int1 > int2 ? 1 : 0; //0 == equal
            case "String":
                String st1 = (String)a; String st2 = (String)b;
                return st1.compareTo(st2)<0 ? -1 : st1.compareTo(st2) > 0 ? 1 : 0;
            case "Character":
                Character c1 = (Character)a; Character c2 = (Character)b;
                return c1.compareTo(c2)<0 ? -1 : c1.compareTo(c2)>0 ? 1 : 0;
            case "EdgeWeight":
                EdgeWeight ew1 = (EdgeWeight) a ; EdgeWeight ew2 = (EdgeWeight) b;
                // Asumiendo que getEdge() devuelve un objeto comparable (Integer, String, etc.)
                return compare(ew1.getEdge(), ew2.getEdge());
            case "Vertex": // Add this case
                // Asumiendo que tu clase Vertex es 'graph.Vertex' y tiene un campo 'data' directamente accesible
                // Si 'data' es privado, necesitarías un getter como 'v1.getData()'
                Vertex v1 = (Vertex) a;
                Vertex v2 = (Vertex) b;
                return compare(v1.data, v2.data); // Comparar basado en su 'data' interna
        }
        return 2; //Unknown or unhandled type comparison
    }

    private static String instanceOf(Object a, Object b) {
        if(a instanceof Integer && b instanceof Integer) return "Integer";
        if(a instanceof String && b instanceof String) return "String";
        if(a instanceof Character && b instanceof Character) return "Character";
        if (a instanceof EdgeWeight && b instanceof EdgeWeight) return "EdgeWeight";
        if (a instanceof Vertex && b instanceof Vertex) return "Vertex"; // Add this line
        return "Unknown";
    }

    public static int maxArray(int[] a) {
        int max = a[0]; //first element
        for (int i = 1; i < a.length; i++) {
            if(a[i]>max){
                max=a[i];
            }
        }
        return max;
    }

    public static int[] getIntegerArray(int n) {
        int[] newArray = new int[n];
        for (int i = 0; i < n; i++) {
            newArray[i] = random(9999);
        }
        return newArray;
    }


    public static int[] copyArray(int[] a) {
        int n = a.length;
        int[] newArray = new int[n];
        for (int i = 0; i < n; i++) {
            newArray[i] = a[i];
        }
        return newArray;
    }

    public static String show(int[] a, int n) {
        String result="";
        for (int i = 0; i < n; i++) {
            result+=a[i]+" ";
        }
        return result;
    }

    public static String RandomAlphabet() {
        String[] Alphabet = {
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        };

        Random random = new Random();
        int randomIndex = random.nextInt(Alphabet.length);
        return Alphabet[randomIndex];
    }
}