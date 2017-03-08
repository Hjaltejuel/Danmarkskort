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

public class AutocompleteJComboBox extends JComboBox {
    private final StringSearchable searcher;

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
        this.setBorder(BorderFactory.createLineBorder(Color.black));

        this.searcher = s;
        this.setEditable(true);
        Component c = this.getEditor().getEditorComponent();
        if(c instanceof JTextComponent) {
            final JTextComponent userInput = (JTextComponent)c;
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
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            ArrayList founds = new ArrayList(AutocompleteJComboBox.this.searcher.search(userInput.getText().toLowerCase()));
                            HashSet foundSet = new HashSet();
                            Iterator var3 = founds.iterator();
                            String s;
                            while(var3.hasNext()) {
                                s = (String)var3.next();
                                foundSet.add(s.toLowerCase());
                            }

                            AutocompleteJComboBox.this.setEditable(false);
                            AutocompleteJComboBox.this.removeAllItems();
                            if(!foundSet.contains(userInput.getText().toLowerCase())) {
                                AutocompleteJComboBox.this.addItem(userInput.getText());
                            }



                            var3 = founds.iterator();

                            while(var3.hasNext()) {
                                s = (String)var3.next();
                                AutocompleteJComboBox.this.addItem(s);
                            }

                            AutocompleteJComboBox.this.setEditable(true);
                            AutocompleteJComboBox.this.setPopupVisible(true);
                            userInput.requestFocus();
                        }
                    });
                }
            });
            userInput.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent arg0) {
                    if(userInput.getText().length() > 0) {
                        AutocompleteJComboBox.this.setPopupVisible(true);
                    }

                }
                public void focusLost(FocusEvent arg0) {
                }
            });
        }

    }
}
