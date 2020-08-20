package editor;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {
    //create necessary UI components
    private JTextField field;
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private JCheckBox useRegexCheckBox;
    private File file;
    private int index = 0;
    private final ArrayList<Integer> indexList = new ArrayList<>();
    private int position = 0;
    private String searchText ="";
    private String text="";
    private int lengthOfSearch;

    public TextEditor() { //Creates base frame for GUI and creates the menu and UI
        super("Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        createUI();
        createMenu();
        pack();
        setVisible(true);
        setLayout(new BorderLayout());
        setResizable(false);
        this.add(fileChooser);
        fileChooser.setVisible(false);
    }

    private void createMenu() {

        // Create File Menu items
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadMenu = new JMenuItem("Open");
        JMenuItem saveMenu = new JMenuItem("Save");
        JMenuItem exitMenu = new JMenuItem("Exit");
        JMenu searchMenu = new JMenu("Search");
        JMenuItem startSearch = new JMenuItem("Start search");
        JMenuItem prevSearch = new JMenuItem("Previous search");
        JMenuItem nextMatch = new JMenuItem("Next match");
        JMenuItem useRegex = new JMenuItem("Use regular expressions");

        //Set names for the menu items
        fileMenu.setName("MenuFile");
        loadMenu.setName("MenuOpen");
        saveMenu.setName("MenuSave");
        exitMenu.setName("MenuExit");
        searchMenu.setName("MenuSearch");
        startSearch.setName("MenuStartSearch");
        prevSearch.setName("MenuPreviousMatch");
        nextMatch.setName("MenuNextMatch");
        useRegex.setName("MenuUseRegExp");
        setJMenuBar(menu);

        // Add Mnemonics to Menu's
        fileMenu.setMnemonic(KeyEvent.VK_F);
        searchMenu.setMnemonic(KeyEvent.VK_S);
        //Add actions to menu items
        useRegex.addActionListener(actionEvent -> useRegexCheckBox.setSelected(!useRegexCheckBox.isSelected()));

        loadMenu.addActionListener(actionEvent -> loadFile());

        saveMenu.addActionListener(actionEvent -> saveFile());

        exitMenu.addActionListener(event -> System.exit(0));

        startSearch.addActionListener(actionEvent -> {
            if(useRegexCheckBox.isSelected()) {
                searchRegex();
            } else {
                search();
            }
        });

        prevSearch.addActionListener(actionEvent -> {
            if(useRegexCheckBox.isSelected()) {
                prevRegex();
            } else {
                prevItem();
            }
        });

        nextMatch.addActionListener(actionEvent -> {
            if(useRegexCheckBox.isSelected()) {
                nextRegex();
            } else {
                nextItem();
            }
        });



        // Add menu items
        fileMenu.add(loadMenu);
        fileMenu.add(saveMenu);
        fileMenu.addSeparator();
        fileMenu.add(exitMenu);
        searchMenu.add(startSearch);
        searchMenu.add(prevSearch);
        searchMenu.add(nextMatch);
        searchMenu.add(useRegex);
        menu.add(fileMenu);
        menu.add(searchMenu);
    } //Done

    private void createUI() {
        ImageIcon loadIcon = new ImageIcon("Icons/loadIcon.png");
        ImageIcon saveIcon = new ImageIcon("Icons/saveIcon.png");
        ImageIcon searchIcon = new ImageIcon("Icons/searchIcon.png");
        ImageIcon previousIcon = new ImageIcon("Icons/previousIcon.png");
        ImageIcon nextIcon = new ImageIcon("Icons/nextIcon.png");
        JPanel panelFile = new JPanel();
        JPanel textPanel = new JPanel();
        JButton save = new JButton(saveIcon);
        JButton load = new JButton(loadIcon);
        JButton startSearchButton = new JButton(searchIcon);
        JButton prevMatchButton = new JButton(previousIcon);
        JButton nextMatchButton = new JButton(nextIcon);
        useRegexCheckBox = new JCheckBox("Use Regex");
        this.textArea = new JTextArea("");
        this.field = new JTextField(20);
        this.fileChooser = new JFileChooser();
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(500, 500));
        textPanel.setLayout(new GridLayout(1, 1, 0, 0));

        field.setName("SearchField");
        scroll.setName("ScrollPane");
        textArea.setName("TextArea");
        save.setName("SaveButton");
        load.setName("OpenButton");
        panelFile.setName("Text Editor");
        startSearchButton.setName("StartSearchButton");
        prevMatchButton.setName("PreviousMatchButton");
        nextMatchButton.setName("NextMatchButton");
        useRegexCheckBox.setName("UseRegExCheckbox");
        fileChooser.setName("FileChooser");


        //Save Button and its action
        save.addActionListener(actionEvent -> saveFile());

        //Load button and its action
        load.addActionListener(actionEvent -> loadFile());

        //Search button and its action
        startSearchButton.addActionListener(actionEvent -> {
            if(useRegexCheckBox.isSelected()) {
                searchRegex();
            } else {
                search();
            }
        });

        nextMatchButton.addActionListener(actionEvent -> {
            if(useRegexCheckBox.isSelected()) {
                nextRegex();
            } else {
                nextItem();
            }
        });

        prevMatchButton.addActionListener(actionEvent -> {
            if(useRegexCheckBox.isSelected()) {
                prevRegex();
            } else {
                prevItem();
            }
        });


        //add panel to frame
        panelFile.add(save);
        panelFile.add(load);
        panelFile.add(field);
        panelFile.add(startSearchButton);
        panelFile.add(prevMatchButton);
        panelFile.add(nextMatchButton);
        panelFile.add(useRegexCheckBox);
        //textPanel.add(textArea);
        textPanel.add(scroll);

        //Set up panel for scroll pane
        add(panelFile, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        //this.add(fileChooser);
    } //Done

    private void saveFile() {
        // Uses File chooser to save the text
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setVisible(true);
        int response = fileChooser.showSaveDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            try (PrintWriter printWriter = new PrintWriter(file)) {
                printWriter.print(textArea.getText());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } //Done

    private void loadFile() {
        //Uses JFilechooser to open a text file into a text area
        //String fileName = field.getText();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setVisible(true);
        int response = fileChooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            textArea.setText("");
            file = fileChooser.getSelectedFile();
            try {
                Scanner scan = new Scanner(file);
                if (file.isFile()) {
                    while (scan.hasNextLine()) {
                        textArea.append(scan.nextLine() + "\n");
                    }
                }
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(new JFrame(), "File Not Found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } //Done

    private void search() { //Need to start on new thread
        searchText = field.getText().toLowerCase();
        text = textArea.getText().toLowerCase();
        //Add all indexes of found text to array list
        index = 0;
        indexList.clear();
         if (!searchText.isEmpty() && !text.isEmpty() && text.contains(searchText)) {
                if(text.indexOf(searchText,index) == 0) {
                    indexList.add(text.indexOf(searchText, index));
                }
                 while (true) {
                     index = text.indexOf(searchText, index + 1);
                     if (index == -1) {
                         break;
                     } else {
                         indexList.add(index);
                     }
                 }
                 index = 0;
                 index = text.indexOf(searchText, index);
                 textArea.setCaretPosition(index + searchText.length() - 1);
                 textArea.select(index, index + searchText.length());
                 textArea.grabFocus();
             } else {
             if (!text.contains(searchText)){
                 JOptionPane.showMessageDialog(new JFrame(),"Search term not found.", "Error", JOptionPane.ERROR_MESSAGE);
             } else {
                 JOptionPane.showMessageDialog(new JFrame(), "Nothing to search!", "Error", JOptionPane.ERROR_MESSAGE);
             }
        }

    }

    private void searchRegex(){
        searchText = "";
        text = "";
        searchText = field.getText().toLowerCase();
        text = textArea.getText().toLowerCase();
        //Add all indexes of found text to array list
        Pattern pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        index = 0;
        indexList.clear();
        lengthOfSearch = 0;
        if (!searchText.isEmpty() && !text.isEmpty() && matcher.find()){
            indexList.add(matcher.start());
            while(matcher.find()) {
                indexList.add(matcher.start());
                lengthOfSearch = matcher.group().length();
            }
            index = indexList.get(0);
            textArea.setCaretPosition(index + lengthOfSearch);
            textArea.select(index, index + lengthOfSearch);
            textArea.grabFocus();
        } else {
            if (!matcher.find()){
                JOptionPane.showMessageDialog(new JFrame(),"Search term not found.", "Error", JOptionPane.ERROR_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Nothing to search!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void nextItem() {
        if (text.equals(textArea.getText().toLowerCase())) {
            if (!searchText.isEmpty() && !text.isEmpty()) {
                if (position < indexList.size() - 1) {
                    position = indexList.indexOf(index) + 1;
                    if (position == 0) {
                        position = 1;
                    }
                } else {
                    position = 0;
                }
                index = text.indexOf(searchText, indexList.get(position));
                textArea.setCaretPosition(index + searchText.length());
                textArea.select(index, index + searchText.length());
                textArea.grabFocus();
            } else if (indexList.isEmpty()) {
                JOptionPane.showMessageDialog(new JFrame(), "Please start search first", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Nothing to search!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            search();
        }
    }
    
    private void nextRegex() {
        if (text.equals(textArea.getText().toLowerCase())) {
            if (!searchText.isEmpty() && !text.isEmpty()) {
                if (position < indexList.size() - 1) {
                    position = indexList.indexOf(index) + 1;
                    if (position == 0) {
                        position = 1;
                    }
                } else {
                    position = 0;
                }

                index = indexList.get(position);
                textArea.setCaretPosition(index + lengthOfSearch);
                textArea.select(index, index + lengthOfSearch);
                textArea.grabFocus();
            } else if (indexList.isEmpty()) {
                JOptionPane.showMessageDialog(new JFrame(), "Please start search first", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Nothing to search!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            searchRegex();
        }
    }

    private void prevRegex() {
        if (text.equals(textArea.getText().toLowerCase())) {
        if (!searchText.isEmpty() && !text.isEmpty()){
                position = indexList.indexOf(index) - 1;
                if(position < 0){
                    position = indexList.size() - 1;
                }

            index = indexList.get(position);
            textArea.setCaretPosition(index + lengthOfSearch);
            textArea.select(index, index + lengthOfSearch);
            textArea.grabFocus();
        } else if (indexList.isEmpty()) {
            JOptionPane.showMessageDialog(new JFrame(),"Please start search first", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(new JFrame(),"Nothing to search!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        } else {
            searchRegex();
        }
    }

    private void prevItem() {
        if (text.equals(textArea.getText().toLowerCase())) {
            if (!searchText.isEmpty() && !text.isEmpty()) {
                position = indexList.indexOf(index) - 1;
                if (position < 0) {
                    position = indexList.size() - 1;
                }
                index = text.toLowerCase().indexOf(searchText, indexList.get(position));
                textArea.setCaretPosition(index + searchText.length());
                textArea.select(index, index + searchText.length());
                textArea.grabFocus();
            } else if (indexList.isEmpty()) {
                JOptionPane.showMessageDialog(new JFrame(), "Please start search first", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Nothing to search!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            searchRegex();
        }
    }

}
