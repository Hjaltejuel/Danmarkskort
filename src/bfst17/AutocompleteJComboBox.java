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
import java.util.concurrent.*;

public class AutocompleteJComboBox extends JComboBox {
    private final StringSearchable searcher;
    private boolean firstTime = true;
    private JTextComponent userInput;

    public AutocompleteJComboBox(StringSearchable s) {

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
        this.searcher = s;
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

                public void update() {
                    SwingUtilities.invokeLater(() -> {

                            ArrayList<String> founds = new ArrayList(AutocompleteJComboBox.this.searcher.search(userInput.getText().toLowerCase()));
                            HashSet<String> foundSet = new HashSet();
                            for(String s: founds){
                                foundSet.add(s.toLowerCase());
                            }

                            AutocompleteJComboBox.this.setEditable(false);
                            AutocompleteJComboBox.this.removeAllItems();
                        if (!foundSet.contains(userInput.getText().toLowerCase())) {
                            AutocompleteJComboBox.this.addItem(userInput.getText());
                        }
                            int i = 0;
                            for(String s: foundSet){

                                StringBuffer res = new StringBuffer();

                                String[] strArray = s.split(" ");
                                for (String str : strArray) {
                                    i++;
                                    char[] stringArray = str.trim().toCharArray();
                                    stringArray[0] = Character.toUpperCase(stringArray[0]);
                                    str = new String(stringArray);
                                    if(i!=strArray.length) {
                                        res.append(str).append(" ");
                                    } else res.append(str);

                                }
                                AutocompleteJComboBox.this.addItem(res.toString());
                            }
                            setEditable(true);
                            userInput.requestFocus();
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