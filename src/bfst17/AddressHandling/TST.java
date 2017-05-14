package bfst17.AddressHandling;
import bfst17.OSMData.OSMNode;


import sun.awt.image.ImageWatched;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Beskrivelse: TST klassen. TST klassen er et ændret tst træ, som kan indeholde TST interfaces
 * @param <TSTInterface>
 */
public class TST<TSTInterface> implements Serializable {
    private int n;
    private Node<TSTInterface> root;
    //Suffix and Preffix for the addresses
    private String suffix = "";
    private String prefix = "";
    // A pattern for a postcode and a city
    Pattern pattern = Pattern.compile("\\d{4}+.*");

    private static class Node<TSTInterface> implements Serializable{
        private char c;
        private Node<TSTInterface> left, mid, right;
        private TSTInterface val;
    }

    public TST() {
    }

    public int size() {
        return n;
    }

    /**
     * Beskrivelse: retunerer værdien af en key med et kald til den anden get metode. Metoden bliver brugt til søgning af addresser
     * Hvis get finder en duplicateAddressNode så vælger den, den value som passer med den søgte addresses suffix
     * @param unfilteredKey
     * @return TSTInterface
     */
    public TSTInterface get(String unfilteredKey) {
        String suffix = "";
        //ingen kommaer
        String key = unfilteredKey.replace(",", "");
        //matcher efter det postkode by pattern
        Matcher matcher = pattern.matcher(key);
        if(matcher.find()){
            //gem suffix
            suffix = key.substring(matcher.start(),matcher.end());
            //gemmer prefix
            key = key.substring(0,matcher.start()-1);
        }
        if (key == null) {
            throw new IllegalArgumentException("calls get() with null argument");
        }
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        //retunerer værdien for den søgte addresse
        Node<TSTInterface> x = get(root, key, 0);
        if (x == null) return null;
        //check for at se om der er duplikationer
        if(x.val instanceof DuplicateAddressNode){
            //løb igennem listen af duplikationer indtil vi finder et match
            for(bfst17.AddressHandling.TSTInterface node: (DuplicateAddressNode)(x.val)){
                if(node instanceof AddressNode){
                if(node.getAddress().equals(suffix)) {
                    return (TSTInterface) node;
                 }
                }
                //hvis det er en by der bliver fundet, så kan vi ikke identificere den rigtige og bliver derfor nød til at vælge en random
                else {
                    int randomNum = ThreadLocalRandom.current().nextInt(0, ((DuplicateAddressNode) x.val).size());
                    return (TSTInterface) ((DuplicateAddressNode) x.val).get(randomNum);
                }
                }
            }
        return x.val;
    }

    /**
     * Beskrivelse: retunerer en node som er slutningen af en række noder som daner den søgte key
     * @param x: noden som vi sammenligner nøglen med
     * @param key: nøglen som bliver søgt efter
     * @param d: indekset vi er på
     * @return Node
     */
    private Node<TSTInterface> get(Node<TSTInterface> x, String key, int d) {
        if (x == null) return null;
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        //gemmer den char vi nuværende er på i keyen
        char c = key.charAt(d);
        //laver recursive kald til get for at gå dybbere ned i træet
        //hvis den givne char er mindre end den char vi sammenligner med gå til venstre
        if (c < x.c) return get(x.left, key, d);
        // hvis den givne char er større end den char vi sammenligner med gå til højre
         else if (c > x.c) return get(x.right, key, d);
        //hvis den givne char matcher den char vi sammenligner med gå ned i midten
         else if (d < key.length() - 1) return get(x.mid, key, d + 1);
        else return x;
    }

    /**
     * Description: the put method which starts the recursive chain of calls, calls put with the root node
     * @param key: the key to be placed in the tree
     * @param val: the val to be placed in the tree
     */
    public void put(String key, TSTInterface val) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with null key");
        }
        n++;
        //starts the recursive chain
        root = put(root, key, val, 0);
    }

    /**
     * Description: den recursive put metode som placerer en key og en val ind i træet
     * @param x:
     * @param key:
     * @param val:
     * @param d:
     * @return: Node
     */
    private Node<TSTInterface> put(Node<TSTInterface> x, String key, TSTInterface val, int d) {
        //Charen på det nuværende index
        char c = key.charAt(d);
        //hvis vi når et sted hvor sammenlignings noden er null, så ved vi at det er en ny key, og vi laver derfor en ny node
        if (x == null) {
            x = new Node<TSTInterface>();
            x.c = c;
        }
        //hvis den givne char er mindre end den char vi sammenligner med gå til venstre
        if (c < x.c) x.left = put(x.left, key, val, d);
        //hvis den givne char er større end den char vi sammenligner med så gå til højre
        else if (c > x.c) x.right = put(x.right, key, val, d);
        //hvis den givne char er lig med den char vi sammenligner med så gå ned i midten
        else if (d < key.length() - 1) x.mid = put(x.mid, key, val, d + 1);
        //hvis der allerede er en key med det prefix vi prøver at indsætte så indsæt værdien
        else  if(x.val != null){
            //Hvis der allerede er en liste af værdier så add til den liste
            if(x.val instanceof DuplicateAddressNode){
                ((DuplicateAddressNode) x.val).add((bfst17.AddressHandling.TSTInterface)val);
            } else {
                //hvis der kun er en værdi så lav en ny DuplicateAddressNode list og add dem til den
                DuplicateAddressNode addresArray = new DuplicateAddressNode();
                addresArray.add((bfst17.AddressHandling.TSTInterface) x.val);
                addresArray.add((bfst17.AddressHandling.TSTInterface) val);
                x.val = (TSTInterface) addresArray;
            }
            //ellers så bare add værdien
        } else x.val = val;
        return x;
    }

    /**
     * Description: Metode for at returnere en liste med Addresser der matcher det søgte prefix
     * @param prefixUser
     * @return ArrayList<String> top 5 resultater
     */
    public ArrayList<String> keysWithPrefix(String prefixUser) {
        //kan ikke matche med ingenting
        if (prefixUser != "") {
                //sætter prefix
                this.prefix = prefixUser;
                //spliter ved kommaet, kommaet er derved skillelinjen mellem prefix og suffix
                String[] split = prefix.split(",");
                //reseter værdierne
                this.suffix = "";
                prefix = "";
                //løb gennem split arrayet og lav prefix og suffix
                prefix = split[0];
                if (split.length > 1) {
                    suffix = split[1];
                }

                if (prefix == null) {
                    throw new IllegalArgumentException("calls keysWithPrefix() with null argument");
                }
                //lav en ny priorityqueue og find noden der passer til det søgte ord
                PriorityQueue<PriorityStrings> queue = new PriorityQueue<>();
                Node<TSTInterface> x = get(root, prefix, 0);

                if (x == null) return makeArray(queue);

                //hvis vi finder et direkte match
                if (x.val != null) {
                    //hvis der er duplikationer så add dem alle til queuen
                    if (x.val instanceof DuplicateAddressNode) {
                        for (bfst17.AddressHandling.TSTInterface node : ((DuplicateAddressNode) x.val)) {
                            if (node instanceof AddressNode) {
                                addAddressNodeToQueue(queue, (AddressNode) node, prefix);
                            } else {
                                addOtherNodeToQueue(queue, prefix);
                            }
                        }
                    } else
                        //hvis der kun er en match så add det med en priority på 1 så vi er sikrer på det kommer først
                        if (x.val instanceof AddressNode) {
                            queue.add(new PriorityStrings(1, prefix + ", " + x.val.toString()));
                        } else {
                            queue.add(new PriorityStrings(1, prefix));
                        }
                }
                //starter det recursive kald til at adde til queuen
                addToQueue(x.mid, new StringBuilder(prefix), queue);

                return makeArray(queue);
            } else return null;
        }

    /**
     * Beskrivelse: metode til at lave en liste med de top 5 resultater
     * @param q: Prioritets queue med top resultaterne
     * @return: Arraylist<top 5 resultater></>
     */
    public ArrayList<String> makeArray(PriorityQueue<PriorityStrings> q){
        ArrayList<String> returnList = new ArrayList<>();
        for(int i = 0; i<5 && !q.isEmpty();i++){

            returnList.add(q.poll().getAddress());
        }
        return returnList;
    }


    /**
     * Beskrivelse: Recursiv metode til at adde til queuen
     * @param x
     * @param prefix
     * @param queue
     */
    private void addToQueue(Node<TSTInterface> x, StringBuilder prefix, PriorityQueue<PriorityStrings> queue) {
        if (x == null) return;
        addToQueue(x.left, prefix, queue);

        //Hvis vi finder et endepunkt, så add det
        if (x.val != null ) {
            // hele addressen
            String address = prefix.toString() + x.c;
            // hvis der er flere resultater så add dem alle
            if(x.val instanceof  DuplicateAddressNode){
                for(bfst17.AddressHandling.TSTInterface node: ((DuplicateAddressNode)x.val)){
                    if(node instanceof AddressNode) {
                        addAddressNodeToQueue(queue, (AddressNode) node, address);
                    } else if(node instanceof OSMNode){
                        addOtherNodeToQueue(queue,address);
                    }
                }
            }
            //Hvis der er et resultat så add det
             else if(x.val instanceof AddressNode)
            {
                addAddressNodeToQueue(queue,(AddressNode)x.val,address);
            }
                else{addOtherNodeToQueue(queue,address);}

        }
        //gå ned i midten
        addToQueue(x.mid, prefix.append(x.c), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        //gå til højre
        addToQueue(x.right, prefix, queue);
    }

    /**
     * Beskrivelse: Metode for at finde en similarity på en addresse og adde den til en queue
     * @param queue
     * @param x
     * @param address
     */
    public void addAddressNodeToQueue(PriorityQueue<PriorityStrings> queue, AddressNode x, String address){

        String suffixNodeString = x.toString();
        //Finder en similarity mellem det prefix du har skrevet og det prefix søgeren fik
        double similarity = similarity(suffixNodeString,suffix);
        //Udregner en int værdi baseret på forskellen mellem længden af det brugeren har skrevet, og det som søgeren har fundet
        double n = (((double) this.prefix.length()  / ((double) address.length() + x.toString().length())+similarity));

        queue.add(new PriorityStrings(n,address + ", " +x.toString()));
    }

    /**
     * Beskrivelse: Metode for at tilføje alle andre noder til queuen
     * @param queue
     * @param address
     */
    public void addOtherNodeToQueue(PriorityQueue<PriorityStrings> queue, String address){
        double n = ((double) this.prefix.length()  / (double) address.length());
        queue.add(new PriorityStrings(n,address));
    }

    /**
     * Beskrivelse: Metode for at kalde Levenshtein distance formel for at finde en forskel.
     */
    public double similarity(String addressPrefix, String prefix) {
        String longer = addressPrefix, shorter = prefix;
        return (longer.length() - editDistance(longer, shorter)) / (double) longer.length();
    }

    /**
     * Beskrivelse: Levenshtein distance formel for at beregne en lighed mellem to Strings
     * @param s1
     * @param s2
     * @return
     */
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();


        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}


