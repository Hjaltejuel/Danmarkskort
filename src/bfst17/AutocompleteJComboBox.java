package bfst17;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.*;

public class AutocompleteJComboBox extends JComboBox {
    private final TST searcher;
    private boolean firstTime = true;
    private JTextComponent userInput;

    public AutocompleteJComboBox(TST tree) {

        //Makes the arrow invisible and set it so it does nothing
        setUI(new BasicComboBoxUI() {
            protected JButton createArrowButton() {
                return new JButton() {
                    public int getWidth() {
                        return 0;
                    }

                    @Override
                    public synchronized void addMouseListener(MouseListener l) {
                    }
                };
            }
        });
        this.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        this.searcher = tree;
        this.setEditable(true);
        Component c = this.getEditor().getEditorComponent();
        if (c instanceof JTextComponent) {
            userInput = (JTextComponent) c;
            userInput.getDocument().putProperty("key",userInput);
            userInput.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent arg0) {
                }

                public void insertUpdate(DocumentEvent arg0) {
                    this.update();
                }

                public void removeUpdate(DocumentEvent arg0) {
                    this.update();


                }
                public String makeUpperCase(String s) {
                    if (!s.equals("")) {
                        StringBuffer res = new StringBuffer();
                        int i = 0;
                        String[] strArray = s.split(" ");
                        for (String str : strArray) {
                            i++;
                            char[] stringArray = str.trim().toCharArray();
                            stringArray[0] = Character.toUpperCase(stringArray[0]);
                            str = new String(stringArray);
                            if (i != strArray.length) {
                                res.append(str).append(" ");
                            } else res.append(str);

                        }
                        return res.toString();
                    }
                    return "";
                }

                public void update() {
                    SwingUtilities.invokeLater(() -> {
                            ArrayList<String> founds = tree.keysWithPrefix(makeUpperCase(userInput.getText()));
                            if(founds!=null) {
                                ArrayList<String> copyList = new ArrayList<String>();
                                for (String s : founds) {
                                    copyList.add(s.toLowerCase().replace(",",""));
                                }

                                boolean addressWriten =copyList.contains(userInput.getText().toLowerCase().replace(",",""));
                                AutocompleteJComboBox.this.setEditable(false);
                                AutocompleteJComboBox.this.removeAllItems();
                                int index =copyList.indexOf(userInput.getText().toLowerCase().replace(",",""));
                                if (!addressWriten) {
                                    AutocompleteJComboBox.this.addItem(userInput.getText());
                                } else {
                                    removeItem(founds.get(index));
                                    addItem(founds.get(index));
                                }




                                for (String s : founds) {
                                    if(founds.indexOf(s)==index){
                                        if(addressWriten){} else AutocompleteJComboBox.this.addItem(s);
                                    } else
                                        AutocompleteJComboBox.this.addItem(s);
                                }
                                setEditable(true);
                                userInput.requestFocus();
                            } else {AutocompleteJComboBox.this.removeAllItems();
                            userInput.setVisible(false);
                            userInput.setVisible(true);
                            userInput.requestFocus();

                            }
                    });
                }
            });

            userInput.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent arg0) {
                    if (userInput.getText().length() > 0) {
                        setPopupVisible(true);

                    }
                    }

                public void focusLost(FocusEvent arg0) {
                }
            });

        }


    }

}