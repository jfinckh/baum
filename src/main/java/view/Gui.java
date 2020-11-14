package view;

import com.mxgraph.swing.mxGraphComponent;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import tree.Node;
import tree.Tree;

public class Gui {
    // static int[] keys = {1, 10, 20, 15, 16, 2, 3, 4, 5, 6, 7, 8, 17, 18, 19};
    static int[] keys = {};
    static List<Integer> keysToInsert = new ArrayList<Integer>();
    static List<Integer> keysToDelete = new ArrayList<Integer>();
    static List<Integer> csvKeys = new ArrayList<Integer>();


    public static void main(String[] args) throws InterruptedException {
        int m;


        Box box = new Box(1);
        box.setSize(100, 250);
        box.add(new JLabel("Welcome to the b-tree simulator!"));
        JLabel orderLabel = new JLabel("First, enter the order of the tree:");
        orderLabel.setBorder(new EmptyBorder(15, 0, 10, 0));
        box.add(orderLabel);
        JTextField order = new JTextField();
        box.add(order);
        JLabel question = new JLabel("Do you want to build the tree automatically via a csv file,");
        question.setBorder(new EmptyBorder(20, 0, 0, 0));
        box.add(question);
        box.add(new JLabel("or do you want to build it manually?"));

        String orderString = "";
        Object[] o = {"Automatic input via csv",
                "Manual input"};

        int optionVal = JOptionPane.NO_OPTION;
        while (orderString.equals("") && optionVal != JOptionPane.CLOSED_OPTION) {
            optionVal = JOptionPane.showOptionDialog(null, box, "Welcome", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    o, o[0]);
            orderString = order.getText();
        }

        if (optionVal == JOptionPane.YES_OPTION) {
            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return false;
                }

                @Override
                public String getDescription() {
                    return null;
                }
            });

            int returnVal = fc.showDialog(null, "Choose a CSV file");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    Scanner fileReader = new Scanner(file);
                    List<String> keys = new ArrayList<>();
                    String values = "";
                    while (fileReader.hasNextLine()) {
                        for (String s : fileReader.nextLine().split(",")) {
                            keys.add(s);
                            values += s + " ";
                        }
                    }
                    Object[] options = {"Yes",
                            "Cancel"};
                    int chosenOptionVal = JOptionPane.showOptionDialog(null, "Do you want to add these values?\n" + values, "Please confirm", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options, options[0]);
                    if (chosenOptionVal == JOptionPane.YES_OPTION) {
                        for (String key : keys) {
                            csvKeys.add(Integer.parseInt(key.trim()));
                        }
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (optionVal != JOptionPane.CLOSED_OPTION) {

            int orderInt;
            try {
                 orderInt = Integer.parseInt(orderString.trim());
                 m = orderInt;
            } catch (NumberFormatException e) {  // per default wird m auf 5 gesetzt
                System.out.println("Order input is not a number!");
                m = 5;
            }

            buildTree(m);
        }
    }

    static private void add(Tree tree, String input, JPanel panel, JFrame f) {
        try {
            int inputValue = Integer.parseInt(input);
            if (inputValue >= 0) {
                tree.insert(inputValue);
                mxGraphComponent graphComponent = new Graph(tree).getGraphComponent();
                panel.remove(0);
                panel.add(graphComponent);
                f.revalidate();
                f.repaint();
            }
        } catch (NumberFormatException e) {
            System.out.println("Input is not a number");
        }
    }

    static private void delete(Tree tree, String input, JPanel panel, JFrame f) {
        try {
            int inputValue = Integer.parseInt(input);
            if (inputValue >= 0) {
                tree.delete(inputValue);
                mxGraphComponent graphComponent = new Graph(tree).getGraphComponent();
                panel.remove(0);
                panel.add(graphComponent);
                f.revalidate();
                f.repaint();
            }
        } catch (NumberFormatException e) {
            System.out.println("Input is not a number");
        }
    }

    static private Node search(Tree tree, String input, JPanel panel, JFrame f) {
        int inputValue = Integer.parseInt(input);
        if (inputValue >= 0) {
            Node node = tree.search(inputValue);
            if (node != null) {
                mxGraphComponent graphComponent = new Graph(tree, node).getGraphComponent();
                panel.remove(0);
                panel.add(graphComponent);
                f.revalidate();
                f.repaint();
            }
            return node;
        } else return null;
    }

    static private void addRandomKeys(String minimum, String maximum, String numberOfElements, Tree tree, JPanel panel, JFrame f, Box addQueueBox) {
        int[] randomKeys = new int[Integer.parseInt(numberOfElements)];
        int min = Integer.parseInt(minimum);
        int max = Integer.parseInt(maximum);
        int range = max - min + 1;

        for (int i = 0; i < Integer.parseInt(numberOfElements); i++) {
            randomKeys[i] = (int) (Math.random() * range) + min;
        }

        if (keysToInsert.size() == 0) {
            if (randomKeys[0] >= 0) {
                tree.insert(randomKeys[0]);
                mxGraphComponent graphComponent = new Graph(tree).getGraphComponent();
                panel.remove(0);
                panel.add(graphComponent);
                f.revalidate();
                f.repaint();
            }
            for (int i = 1; i < randomKeys.length; i++) {
                if (randomKeys[i] >= 0) keysToInsert.add(randomKeys[i]);
            }
        } else {
            for (int key : randomKeys) {
                if (key >= 0) keysToInsert.add(key);
            }
        }
        addQueueBox.remove(1);
        String newQueueKeys = "";
        for (int key : keysToInsert) {
            newQueueKeys += key + "  ";
        }

        JTextField newQueueTextfield = new JTextField(newQueueKeys, 30);
        newQueueTextfield.setEnabled(false);
        newQueueTextfield.setDisabledTextColor(Color.BLACK);
        newQueueTextfield.setCaretPosition(0);
        addQueueBox.add(newQueueTextfield);
        addQueueBox.revalidate();
        addQueueBox.repaint();
    }

    private static void buildTree(int m) throws InterruptedException {
        Tree tree = Tree.getNewTree(m);

        JFrame f = new JFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        f.setBounds(0, 0, screenSize.width - 100, screenSize.height - 100);

        Graph graph = new Graph(tree);
        mxGraphComponent graphComponent = graph.getGraphComponent();
        JPanel panel = new JPanel();
        JScrollPane scroller = new JScrollPane(panel);
        panel.add(graphComponent);

        f.getContentPane().add(scroller);

        Box addBox = new Box(0);
        JButton addButton = new JButton("Add");
        JTextField addInput = new JTextField("", 10);
        JLabel addDescription = new JLabel();
        addDescription.setText("Add new elements   ");
        addBox.add(addDescription);
        addBox.add(addInput);
        addBox.add(addButton);

        Box deleteBox = new Box(0);
        JButton deleteButton = new JButton("Delete");
        JTextField deleteInput = new JTextField("", 10);
        JLabel deleteDescription = new JLabel();
        deleteDescription.setText("Delete elements   ");
        deleteBox.add(deleteDescription);
        deleteBox.add(deleteInput);
        deleteBox.add(deleteButton);

        if (keys.length != 0) {
            tree.insert(keys[0]);
            graphComponent = new Graph(tree).getGraphComponent();
            panel.remove(0);
            panel.add(graphComponent);
            if (keys.length > 1) {
                for (int i = 1; i < keys.length; i++) {
                    keysToInsert.add(keys[i]);
                }
            }
        }

        Box addQueueBox = new Box(0);
        JButton nextButton = new JButton("Insert next element");
        String queueKeys = "";
        for (int key : keysToInsert) {
            queueKeys += String.valueOf(key + "  ");
        }
        JTextField queueElements = new JTextField(queueKeys, 30);
        queueElements.setEnabled(false);
        queueElements.setDisabledTextColor(Color.BLACK);
        queueElements.setText(queueKeys);
        queueElements.setCaretPosition(0);
        addQueueBox.add(nextButton);
        addQueueBox.add(queueElements);
        addQueueBox.setBorder(new EmptyBorder(0, 30, 0, 0));
        addBox.add(addQueueBox);
        addBox.setBorder(new EmptyBorder(0, 0, 20, 0));

        Box deleteQueueBox = new Box(0);
        JButton nextDeleteButton = new JButton("Delete next element");
        String deleteQueueKeys = "";
        for (int key : keysToDelete) {
            deleteQueueKeys += String.valueOf(key + "  ");
        }

        JTextField deleteQueueElements = new JTextField(deleteQueueKeys, 30);
        deleteQueueElements.setEnabled(false);
        deleteQueueElements.setDisabledTextColor(Color.BLACK);
        deleteQueueElements.setText(deleteQueueKeys);
        deleteQueueElements.setCaretPosition(0);
        deleteQueueBox.add(nextDeleteButton);
        deleteQueueBox.add(deleteQueueElements);
        deleteQueueBox.setBorder(new EmptyBorder(0, 30, 0, 0));
        deleteBox.add(deleteQueueBox);
        deleteBox.setBorder(new EmptyBorder(0, 0, 20, 0));


        Box searchBox = new Box(0);
        JButton searchButton = new JButton("Search");
        JTextField searchInput = new JTextField("", 10);
        JLabel searchDescription = new JLabel();
        searchDescription.setText("Search node with specific key   ");
        searchBox.add(searchDescription);
        searchBox.add(searchInput);
        searchBox.setBorder(new EmptyBorder(0, 0, 20, 0));
        searchBox.add(searchButton);

        Box searchResultBox = new Box(0);
        JTextField searchResultText = new JTextField("", 30);
        searchResultText.setEnabled(false);
        searchResultText.setDisabledTextColor(Color.BLACK);
        searchResultText.setText(deleteQueueKeys);
        searchResultText.setCaretPosition(0);
        searchResultBox.add(searchResultText);
        searchResultBox.setBorder(new EmptyBorder(0, 30, 0, 0));
        searchBox.add(searchResultBox);

        Box randomValBox = new Box(0);
        JButton randomValButton = new JButton("Add random values");
        randomValBox.setBorder(new EmptyBorder(0, 0, 0, 30));
        randomValBox.add(randomValButton);

        Box changeOrderBox = new Box(0);
        JButton changeOrderButton = new JButton("Change order of tree");
        changeOrderBox.add(changeOrderButton);

        Box bottomBox = new Box(0);
        bottomBox.add(randomValBox);
        bottomBox.add(changeOrderBox);

        bottomBox.setBorder(new EmptyBorder(0, 0, 20, 1000));

        Box controlBox = new Box(1);
        controlBox.add(addBox);
        controlBox.add(deleteBox);
        controlBox.add(searchBox);
        controlBox.add(bottomBox);

        JPanel controlPanel = new JPanel();
        controlPanel.add(controlBox);
        f.add(controlPanel, BorderLayout.PAGE_END);

        addButton.addActionListener(e -> {
            String[] inputArray = addInput.getText().split(",");
            // wenn die Warteschlange leer ist, wird Element direkt zu Baum hinzugefügt, ansonsten wird/werden alle eingegebenen Elemente zur Warteschlange hinzugefügt
            if (keysToInsert.size() == 0) {
                delete(tree, inputArray[0].trim(), panel, f);
                add(tree, inputArray[0].trim(), panel, f);
            } else {
                if (Integer.parseInt(inputArray[0].trim()) >= 0) {
                    keysToInsert.add(Integer.valueOf(inputArray[0].trim()));
                    addQueueBox.remove(1);
                    String newQueueKeys = "";
                    for (int key : keysToInsert) {
                        newQueueKeys += String.valueOf(key + "  ");
                    }
                    JTextField newQueueTextfield = new JTextField(newQueueKeys, 30);
                    newQueueTextfield.setEnabled(false);
                    newQueueTextfield.setDisabledTextColor(Color.BLACK);
                    newQueueTextfield.setCaretPosition(0);
                    addQueueBox.add(newQueueTextfield);
                    addQueueBox.revalidate();
                    addQueueBox.repaint();
                }
            }
            if (inputArray.length > 1) {
                for (int i = 1; i < inputArray.length; i++) {
                    if (Integer.parseInt(inputArray[i].trim()) >= 0)
                        keysToInsert.add(Integer.valueOf(inputArray[i].trim()));
                }
                addQueueBox.remove(1);
                String newQueueKeys = "";
                for (int key : keysToInsert) {
                    newQueueKeys += key + "  ";
                }
                JTextField newQueueTextfield = new JTextField(newQueueKeys, 30);
                newQueueTextfield.setEnabled(false);
                newQueueTextfield.setDisabledTextColor(Color.BLACK);
                newQueueTextfield.setCaretPosition(0);
                addQueueBox.add(newQueueTextfield);
                addQueueBox.revalidate();
                addQueueBox.repaint();
            }
            addInput.setText("");
        });

        deleteButton.addActionListener(e -> {
            String[] inputArray = deleteInput.getText().split(",");
            // wenn die Warteschlange leer ist, wird Element direkt zu Baum hinzugefügt, ansonsten wird/werden alle eingegebenen Elemente zur Warteschlange hinzugefügt
            if (keysToDelete.size() == 0) {
                delete(tree, inputArray[0].trim(), panel, f);
                clearSearchField(searchBox, searchInput);
            } else {
                keysToDelete.add(Integer.valueOf(inputArray[0].trim()));
                deleteQueueBox.remove(1);
                String newQueueKeys = "";
                for (int key : keysToDelete) {
                    newQueueKeys += key + "  ";
                }
                JTextField newQueueTextfield = new JTextField(newQueueKeys, 30);
                newQueueTextfield.setEnabled(false);
                newQueueTextfield.setDisabledTextColor(Color.BLACK);
                newQueueTextfield.setCaretPosition(0);
                deleteQueueBox.add(newQueueTextfield);
                deleteQueueBox.revalidate();
                deleteQueueBox.repaint();
            }
            if (inputArray.length > 1) {
                for (int i = 1; i < inputArray.length; i++) {
                    keysToDelete.add(Integer.valueOf(inputArray[i].trim()));
                }
                deleteQueueBox.remove(1);
                String newQueueKeys = "";
                for (int key : keysToDelete) {
                    newQueueKeys += key + "  ";
                }
                JTextField newQueueTextfield = new JTextField(newQueueKeys, 30);
                newQueueTextfield.setEnabled(false);
                newQueueTextfield.setDisabledTextColor(Color.BLACK);
                newQueueTextfield.setCaretPosition(0);
                deleteQueueBox.add(newQueueTextfield);
                deleteQueueBox.revalidate();
                deleteQueueBox.repaint();
            }
            deleteInput.setText("");
        });

        nextButton.addActionListener(e -> {
            if (keysToInsert.size() > 0) {
                tree.insert(keysToInsert.get(0));
                keysToInsert.remove(0);
                mxGraphComponent graphComponent12 = new Graph(tree).getGraphComponent();
                panel.remove(0);
                panel.add(graphComponent12);
                f.revalidate();
                f.repaint();
                addQueueBox.remove(1);
                String newQueueKeys = "";
                for (int key : keysToInsert) {
                    newQueueKeys += String.valueOf(key + "  ");
                }
                JTextField newQueueTextfield = new JTextField(newQueueKeys, 30);
                newQueueTextfield.setEnabled(false);
                newQueueTextfield.setDisabledTextColor(Color.BLACK);
                newQueueTextfield.setCaretPosition(0);
                addQueueBox.add(newQueueTextfield);
                addQueueBox.revalidate();
                addQueueBox.repaint();
                clearSearchField(searchBox, searchInput);
            }
        });

        nextDeleteButton.addActionListener(e -> {
            if (keysToDelete.size() > 0) {
                tree.delete(keysToDelete.get(0));
                keysToDelete.remove(0);
                mxGraphComponent graphComponent1 = new Graph(tree).getGraphComponent();
                panel.remove(0);
                panel.add(graphComponent1);
                f.revalidate();
                f.repaint();
                deleteQueueBox.remove(1);
                String newQueueKeys = "";
                for (int key : keysToDelete) {
                    newQueueKeys += key + "  ";
                }
                JTextField newQueueTextfield = new JTextField(newQueueKeys, 30);
                newQueueTextfield.setEnabled(false);
                newQueueTextfield.setDisabledTextColor(Color.BLACK);
                newQueueTextfield.setCaretPosition(0);
                deleteQueueBox.add(newQueueTextfield);
                deleteQueueBox.revalidate();
                deleteQueueBox.repaint();
                clearSearchField(searchBox, searchInput);
            }
        });

        searchButton.addActionListener(e -> {
            String searchKey = searchInput.getText();
            Node searchResultNode;
            if (searchKey != null) {
                searchResultNode = search(tree, searchKey.trim(), panel, f);
                if (searchResultNode != null) {
                    searchBox.remove(3);
                    String cost = "Node found! Cost of search: " + String.valueOf(searchResultNode.getSearchCost());
                    JTextField costTextField = new JTextField(cost, 30);
                    costTextField.setEnabled(false);
                    costTextField.setDisabledTextColor(Color.BLACK);
                    costTextField.setCaretPosition(0);
                    costTextField.setBorder(new EmptyBorder(0, 30, 0, 0));
                    searchBox.add(costTextField);
                    searchBox.revalidate();
                    searchBox.repaint();
                } else {
                    searchBox.remove(3);
                    String cost = "Node not found.";
                    JTextField costTextField = new JTextField(cost, 30);
                    costTextField.setEnabled(false);
                    costTextField.setDisabledTextColor(Color.BLACK);
                    costTextField.setCaretPosition(0);
                    costTextField.setBorder(new EmptyBorder(0, 30, 0, 0));
                    searchBox.add(costTextField);
                    searchBox.revalidate();
                    searchBox.repaint();
                }
            }
        });

        JTextField randomMin = new JTextField(5);
        JTextField randomMax = new JTextField(5);
        JTextField numOfElements = new JTextField(5);

        JPanel randomizePanel = new JPanel();
        randomizePanel.add(new JLabel("Minimum value:"));
        randomizePanel.add(randomMin);
        randomizePanel.add(Box.createHorizontalStrut(15)); // a spacer
        randomizePanel.add(new JLabel("Maximum value:"));
        randomizePanel.add(randomMax);
        randomizePanel.add(Box.createHorizontalStrut(15)); // a spacer
        randomizePanel.add(new JLabel("Number of elements:"));
        randomizePanel.add(numOfElements);

        randomValButton.addActionListener(e -> {
            Object[] options = {"Add",
                    "Cancel"};
            int result = JOptionPane.showConfirmDialog(f, randomizePanel, "Add random elements", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                if (randomMin.getText() != null && randomMax.getText() != null && numOfElements.getText() != null) {
                    addRandomKeys(randomMin.getText(), randomMax.getText(), numOfElements.getText(), tree, panel, f, addQueueBox);
                }
                randomMin.setText("");
                randomMax.setText("");
                numOfElements.setText("");
            }
        });

        JTextField newOrder = new JTextField(5);
        JLabel newOrderLabel = new JLabel("Enter the new order for the tree:");
        JPanel orderPanel = new JPanel();
        orderPanel.add(newOrderLabel);
        orderPanel.add(newOrder);

        changeOrderButton.addActionListener(e -> {
            Object[] options = {"Add",
                    "Cancel"};
            int result = JOptionPane.showConfirmDialog(f, orderPanel, "Change order", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                if (newOrder.getText() != null) {
                    int changedOrder;
                    try {
                        changedOrder = Integer.parseInt(newOrder.getText().trim());
                        try {
                            keysToInsert.clear();
                            keysToDelete.clear();
                            f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));

                            Box box = new Box(1);
                            box.setSize(100, 250);
                            box.add(new JLabel("Do you want to build the new tree automatically via a csv file,"));
                            box.add(new JLabel("or do you want to build it manually?"));

                            Object[] o = {"Automatic input via csv",
                                    "Manual input"};

                            int newOptionVal = JOptionPane.NO_OPTION;
                            newOptionVal = JOptionPane.showOptionDialog(null, box, "Welcome", JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    o, o[0]);

                            if (newOptionVal == JOptionPane.YES_OPTION) {
                                final JFileChooser fc = new JFileChooser();
                                fc.setFileFilter(new FileFilter() {
                                    @Override
                                    public boolean accept(File f) {
                                        return false;
                                    }

                                    @Override
                                    public String getDescription() {
                                        return null;
                                    }
                                });

                                int returnVal = fc.showDialog(null, "Choose a CSV file");
                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                    File file = fc.getSelectedFile();
                                    try {
                                        Scanner fileReader = new Scanner(file);
                                        List<String> keys = new ArrayList<>();
                                        String values = "";
                                        while (fileReader.hasNextLine()) {
                                            for (String s : fileReader.nextLine().split(",")) {
                                                keys.add(s);
                                                values += s + " ";
                                            }
                                        }
                                        int chosenOptionVal = JOptionPane.showOptionDialog(null, "Do you want to add these values?\n" + values, "Please confirm", JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE,
                                                null,
                                                options, options[0]);
                                        if (chosenOptionVal == JOptionPane.YES_OPTION) {
                                            for (String key : keys) {
                                                csvKeys.add(Integer.parseInt(key.trim()));
                                            }
                                        }
                                    } catch (FileNotFoundException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } else if (newOptionVal != JOptionPane.CLOSED_OPTION) {
                                buildTree(changedOrder);
                            }
                        } catch (InterruptedException interruptedException) {
                            System.out.println("Something went wrong");
                        }
                    } catch (NumberFormatException nanException) {
                        System.out.println("New order input is not a number");
                    }
                }
            }
        });

        f.setVisible(true);

        if (csvKeys.size() > 0) {
            for (int key : csvKeys) {
                if (key >= 0) {
                    tree.insert(key);
                    graphComponent = new Graph(tree).getGraphComponent();
                    panel.remove(0);
                    panel.add(graphComponent);
                    f.revalidate();
                    f.repaint();
                    Thread.sleep(1000);
                }
            }
        }
    }

    private static void clearSearchField(Box searchBox, JTextField searchInput) {
        searchInput.setText("");
        searchBox.remove(3);
        String cost = "";
        JTextField costTextField = new JTextField(cost, 30);
        costTextField.setEnabled(false);
        costTextField.setDisabledTextColor(Color.BLACK);
        costTextField.setCaretPosition(0);
        costTextField.setBorder(new EmptyBorder(0, 30, 0, 0));
        searchBox.add(costTextField);
        searchBox.revalidate();
        searchBox.repaint();
    }
}
