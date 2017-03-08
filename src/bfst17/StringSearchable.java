package bfst17;

import java.util.*;

public class StringSearchable {
    private List<String> databaseInfo = new ArrayList();
    PriorityQueue<PriorityStrings> priority = new PriorityQueue<>();
    public StringSearchable(List<String> databaseInfo) {
        this.databaseInfo.addAll(databaseInfo);
    }

    public Collection<String> search(String userInput) {
        ArrayList founds = new ArrayList();
        Iterator var3 = this.databaseInfo.iterator();
        double max = 0;
        while(var3.hasNext()) {
            String s = (String)var3.next();
            if(s.indexOf(userInput) == 0 || s.contains(userInput)) {
                double n = ((double) userInput.length()/(double) s.length());
                priority.add(new PriorityStrings(n,s));
                }

            }
            for(int i = 0; i <10;i++){
                founds.add(priority.poll().getAddress());
            }


        return founds;
    }
}
