package bfst17.GUI;

import bfst17.AddressHandling.TST;

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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Beskrivelse: Klassen AutocompleteJCombobox, autocompleteren som extender JComboBox
 */
public class AutocompleteJComboBox extends JComboBox {
    private static TST searcher;
    private JTextComponent userInput;
    private Timer timer;


    public void setTree(TST tree){
        searcher = tree;
    }

    /**
     * Beskrivelse: Konstruktoren til autocompleteren, sætter hele completeren op
     * @param tree
     */
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

        //sætter listeneren til documentet i comboboxen til en ny documentlistener
        Component c = this.getEditor().getEditorComponent();
        if (c instanceof JTextComponent) {
            userInput = (JTextComponent) c;
            userInput.getDocument().putProperty("key",userInput);
            userInput.getDocument().addDocumentListener(new DocumentListener() {

                public void changedUpdate(DocumentEvent arg0) {
                }

                public void insertUpdate(DocumentEvent arg0) {
                    //kalder update for at opdatere søgeresultaterne
                    update();
                }

                public void removeUpdate(DocumentEvent arg0) {
                    //kalder update for at opdatere søgeresultaterne
                    update();
                }

                /**
                 * Beskrivelse: makeUpperCase metoden, laver hvert bogstav ved hvert mellemrum stort
                 * @param s
                 * @return
                 */
                public String makeUpperCase(String s) {
                    if (!s.equals("")) {
                        StringBuffer res = new StringBuffer();
                        int i = 0;
                        String[] strArray = s.split(" ");
                        for (String str : strArray) {
                            if(!str.equals("")) {
                                i++;
                                char[] stringArray = str.trim().toCharArray();
                                //laver den første char stor
                                stringArray[0] = Character.toUpperCase(stringArray[0]);
                                str = new String(stringArray);
                                if (i != strArray.length) {
                                    res.append(str).append(" ");
                                } else res.append(str);
                            }

                        }
                        return res.toString();
                    }
                    return "";
                }

                /**
                 * Beskrivelse: Update metoden, sørger for at hente en liste af fundne elementer og vise dem i comboboxen
                 */
                public void update() {
                    SwingUtilities.invokeLater(() -> {
                            //De fundne elementer
                            ArrayList<String> founds = AutocompleteJComboBox.searcher.keysWithPrefix(makeUpperCase(userInput.getText()));
                            if(founds!=null) {
                                ///laver en copylist som man kan søge i
                                ArrayList<String> copyList = new ArrayList<String>();
                                for (String s : founds) {
                                    copyList.add(s.toLowerCase().replace(",",""));
                                }
                                //sætter en boolean hvis copylisten indeholder ordet du har søgt på
                                boolean addressWriten =copyList.contains(userInput.getText().toLowerCase().replace(",",""));
                                //fjerner alle elementer
                                AutocompleteJComboBox.this.setEditable(false);
                                AutocompleteJComboBox.this.removeAllItems();
                                //finder indekset af det element som du har søgt på
                                int index =copyList.indexOf(userInput.getText().toLowerCase().replace(",",""));
                                if (!addressWriten) {
                                    //hvis det brugeren har søgt på ikke helt præcis var der, så bare add selve userinputet først
                                    //så vil comboboxen vælge det element
                                    AutocompleteJComboBox.this.addItem(userInput.getText());
                                } else {
                                    //hvis elementet er der, så fjern det fra listen og add det igen så det kommer først og ikke to gange
                                    removeItem(founds.get(index));
                                    addItem(founds.get(index));
                                }
                                //løber igennem listen og adder alle bortset fra det søgte element som er forest
                                for (String s : founds) {
                                    if(founds.indexOf(s)==index){
                                        if(addressWriten){} else AutocompleteJComboBox.this.addItem(s);
                                    } else
                                        AutocompleteJComboBox.this.addItem(s);
                                }

                                setEditable(true);
                                userInput.requestFocus();
                            } else {
                                AutocompleteJComboBox.this.removeAllItems();
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