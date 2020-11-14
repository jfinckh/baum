package tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InsertMethods {

    /**
     * Suche Knoten zum einfügen.
     *
     * @param nodePointer Zeiger auf Knoten
     * @param key Key der eingefügt werden soll
     * @return knoten an dem eingefügt werden soll oder null wenn key bereits exisitiert
     * */
    public static Optional<Node> getNodeForInsert(Node nodePointer, int key) {  // suche nach Einfüge-Node -> falls Key bereits vorhanden, wird null zurückgegeben
        if (nodePointer.getKeys().contains(key)) return Optional.empty();
        else {
            while (!nodePointer.getSons().isEmpty()) {
                for (int i = 0; i<nodePointer.getKeys().size(); i++) {
                    if (key < nodePointer.getKeys().get(i)) {
                        nodePointer = nodePointer.getSons().get(i);
                        if (nodePointer.getKeys().contains(key)) return Optional.empty();
                        break;
                    } else if (key > nodePointer.getKeys().get(i) && i == nodePointer.getKeys().size()-1) {
                        nodePointer = nodePointer.getSons().get(i+1);
                        if (nodePointer.getKeys().contains(key)) return Optional.empty();
                        break;
                    }
                }
            }
            if (nodePointer.getKeys().contains(key)) return Optional.empty();
            else return Optional.of(nodePointer);
        }
    }


    public static Node splitRoot(Node nodePointer, int splitKey, int m) {
        List<Integer> keysLeft = new ArrayList<Integer>();
        List<Integer> keysRight = new ArrayList<Integer>();

        for (int i = 0; i<splitKey; i++) {  // die Keys der Split-Node werden geteilt und in 2 verschiedene Listen eingefügt
            keysLeft.add(nodePointer.getKeys().get(i));
            keysRight.add(0, nodePointer.getKeys().get(nodePointer.getKeys().size()-1-i));
        }
        if (m%2 == 0) keysRight.add(0, nodePointer.getKeys().get(splitKey+1));  // bei geradem m erhält die rechte Seite 1 Element mehr

        Node root = new Node(m, nodePointer.getKeys().get(splitKey));  // Split-Key wird zu neuer Node hinzugefügt, diese wird die neue Wurzel

        // Der Wurzel werden 2 Söhne mit den Keys der Split-Node hinzugefügt
        root.setSon(0, new Node(m, keysLeft));
        root.setSon(1, new Node(m, keysRight));
        root.getSons().get(0).setParent(root);
        root.getSons().get(1).setParent(root);

        return root;
    }

    public static Node splitNonRoot(Node nodePointer, int m) {
        int splitKey = (int)Math.floor((m-1)/2);
        int nodeIndex = nodePointer.getParent().getSons().indexOf(nodePointer);
        List<Integer> keysLeft = new ArrayList<Integer>();
        List<Integer> keysRight = new ArrayList<Integer>();

        for (int i = 0; i<splitKey; i++) {   // zunächst gleicher Vorgang wie in splitRoot()
            keysLeft.add(nodePointer.getKeys().get(i));
            keysRight.add(0, nodePointer.getKeys().get(nodePointer.getKeys().size()-1-i));
        }
        if (m%2 == 0) keysRight.add(0, nodePointer.getKeys().get(splitKey+1));

        // Umhängen der parent-son-Beziehungen
        nodePointer.getParent().setSon(nodeIndex, new Node(m, keysLeft));
        nodePointer.getParent().getSons().get(nodeIndex).setParent(nodePointer.getParent());
        nodePointer.getParent().setSon(nodeIndex+1, new Node(m, keysRight));
        nodePointer.getParent().getSons().get(nodeIndex+1).setParent(nodePointer.getParent());

        if (nodePointer.getSons().size() != 0) {
            for (int i = 0; i<=splitKey; i++) {
                nodePointer.getParent().getSons().get(nodeIndex).setSon(nodePointer.getSons().get(i));
                nodePointer.getSons().get(i).setParent(nodePointer.getParent().getSons().get(nodeIndex));
            }
            for (int i = nodePointer.getSons().size()-1; i>splitKey; i--) {
                nodePointer.getParent().getSons().get(nodeIndex+1).getSons().add(0, nodePointer.getSons().get(i));
                nodePointer.getSons().get(i).setParent(nodePointer.getParent().getSons().get(nodeIndex+1));
            }
        }
        nodePointer.getParent().removeSon(nodePointer);
        nodePointer.getParent().insertKey(nodePointer.getKeys().get(splitKey));
        return nodePointer.getParent();
    }


    public static void updateChildParentRelations(Node nodePointer, Node root, int splitKey) {
        for (int i = 0; i<nodePointer.getSons().size(); i++) {
            if (i <= splitKey) {
                nodePointer.getSons().get(i).setParent(root.getSons().get(0));
            } else {
                nodePointer.getSons().get(i).setParent(root.getSons().get(1));
            }
        }
        for (int i = 0; i<=splitKey; i++) {
            root.getSons().get(0).setSon(nodePointer.getSons().get(i));
        }
        for (int i = nodePointer.getSons().size()-1; i>splitKey; i--) {
            root.getSons().get(1).setSon(0, nodePointer.getSons().get(i));
        }
    }
}
