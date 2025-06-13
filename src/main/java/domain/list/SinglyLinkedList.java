package domain.list;

public class SinglyLinkedList implements List {
    private Node first; //apuntador al inicio de la lista
    private int count;

    public SinglyLinkedList() {
        this.first = null; //la lista no existe
        this.count = 0;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public void clear() {
        this.first = null; //anulamos la lista
        this.count = 0;
    }

    @Override
    public boolean isEmpty() {
        return this.first == null; //si es nulo está vacía
    }

    @Override
    public boolean contains(Object element) {
        if (isEmpty()) {
            return false;
        }
        Node aux = first;
        while (aux != null) {
            if (util.Utility.compare(aux.data, element) == 0) {
                return true;
            }
            aux = aux.next; //lo movemos al sgte nodo
        }
        return false; //indica q el elemento no existe
    }

    @Override
    public void add(Object element) {
        Node newNode = new Node(element);
        if (isEmpty()) {
            first = newNode;
        } else {
            Node aux = first;
            //mientras no llegue al ult nodo
            while (aux.next != null) {
                aux = aux.next;
            }
            //una vez que se sale del while, quiere decir q
            //aux esta en el ult nodo, por lo q lo podemos enlazar
            //con el nuevo nodo
            aux.next = newNode;
        }
        this.count++;
    }

    @Override
    public void addFirst(Object element) {
        Node newNode = new Node(element);
        if (isEmpty()) {
            first = newNode;
        } else {
            newNode.next = first;
            first = newNode;
        }
        this.count++;
    }

    @Override
    public void addLast(Object element) {
        add(element);
    }

    @Override
    public void addInSortedList(Object element) {
    }

    @Override
    public void remove(Object element) throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }
        //Caso 1. El elemento a suprimir esta al inicio
        if (util.Utility.compare(first.data, element) == 0) {
            first = first.next; //saltamos el primer nodo
            this.count--;
        } else {  //Caso 2. El elemento a suprimir puede estar al medio o final
            Node prev = first; //dejo un apuntador al nodo anterior
            Node aux = first.next;
            // Primero verificamos que aux no sea nulo.
            while (aux != null && util.Utility.compare(aux.data, element) != 0) {
                prev = aux;
                aux = aux.next;
            }
            //se sale cuando alcanza nulo o cuando encuentra el elemento
            if (aux != null && util.Utility.compare(aux.data, element) == 0) {
                //ya lo encontro, procedo a desenlazar el nodo
                prev.next = aux.next;
                this.count--;
            } else {
            }
        }
    }

    @Override
    public Object removeFirst() throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }
        Object removedData = first.data;
        first = first.next;
        this.count--;
        return removedData;
    }

    @Override
    public Object removeLast() throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }
        if (first.next == null) {
            Object removedData = first.data;
            first = null;
            this.count--;
            return removedData;
        }
        Node aux = first;
        while (aux.next.next != null) {
            aux = aux.next;
        }
        Object removedData = aux.next.data;
        aux.next = null;
        this.count--;
        return removedData;
    }

    @Override
    public void sort() throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }

        for (int i = 0; i < size() - 1; i++) {
            for (int j = i + 1; j < size(); j++) {
                if (util.Utility.compare(getNode(j).data, getNode(i).data) < 0) {
                    Object aux = getNode(i).data;
                    getNode(i).data = getNode(j).data;
                    getNode(j).data = aux;
                }
            }
        }
    }

    @Override
    public int indexOf(Object element) throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }
        Node aux = first;
        int index = 0;
        while (aux != null) {
            if (util.Utility.compare(aux.data, element) == 0) {
                return index;
            }
            index++;
            aux = aux.next;
        }
        return -1;
    }

    @Override
    public Object getFirst() throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }
        return first.data;
    }

    @Override
    public Object getLast() throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }
        Node aux = first;
        while (aux.next != null) {
            aux = aux.next;
        }
        return aux.data;
    }

    @Override
    public Object getPrev(Object element) throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }
        if (util.Utility.compare(first.data, element) == 0) {
            return "It's the first, it has no previous";
        }
        Node aux = first;
        while (aux.next != null) {
            if (util.Utility.compare(aux.next.data, element) == 0) {
                return aux.data;
            }
            aux = aux.next;
        }
        return "Does not exist in Single Linked List";
    }

    @Override
    public Object getNext(Object element) throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }
        Node aux = first;
        while (aux != null && util.Utility.compare(aux.data, element) != 0) {
            aux = aux.next;
        }
        if (aux != null && aux.next != null) {
            return aux.next.data;
        }
        return "Does not exist or has no next in Single Linked List";
    }

    @Override
    public Node getNode(int index) throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }
        // Aquí debes usar 'count' para el límite, no llamar a size() que itera.
        if (index < 0 || index >= count) {
            throw new ListException("Invalid index: " + index);
        }
        Node aux = first;
        int i = 0;
        while (aux != null) {
            if (i == index) {
                return aux;
            }
            i++;
            aux = aux.next;
        }
        return null;
    }

    public Node getNode(Object element) throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }
        Node aux = first;
        while (aux != null) {
            if (util.Utility.compare(aux.data, element) == 0) {
                return aux;
            }
            aux = aux.next;
        }
        return null;
    }

    @Override
    public String toString() {
        String result = "Singly Linked List Content:";
        if (isEmpty()) {
            return result + " List is Empty.";
        }
        Node aux = first;
        while (aux != null) {
            result += "\n" + aux.data;
            aux = aux.next;
        }
        return result;
    }

    public Object get(int index) throws ListException {
        if (isEmpty()) {
            throw new ListException("Singly Linked List is Empty");
        }
        // Aquí también usar 'count'
        if (index < 0 || index >= count) { // Usar 'count' directamente
            throw new ListException("Invalid index: " + index);
        }

        Node aux = first;
        int i = 0;
        while (aux != null) {
            if (util.Utility.compare(i, index) == 0) {
                return aux.data;
            }
            i++;
            aux = aux.next;
        }
        return null;
    }
}