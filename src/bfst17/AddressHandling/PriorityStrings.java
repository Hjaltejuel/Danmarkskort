package bfst17.AddressHandling;

import java.io.Serializable;

/**
 * Beskrivelse: PriorityString klassen som fungere som en værdi til at ligge i priorityqueuen i Tst træet
 */
public class PriorityStrings implements Comparable<PriorityStrings>,Serializable {
    double priority;
    String address;

    public PriorityStrings(double priority, String address){
        this.priority = priority;
        this.address = address;
    }

    public String getAddress(){return address;}

    public double getPriority(){return priority;}

    @Override
    public int compareTo(PriorityStrings o) {
        if( getPriority() < o.getPriority()){
            return 1;
        } else if (getPriority() == o.getPriority()){
            return 0;
        } else  {
            return -1;
        }
    }
}
