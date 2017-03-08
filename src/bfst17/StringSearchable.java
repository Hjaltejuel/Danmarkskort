package bfst17;

import java.util.*;

public class StringSearchable {
    private List<String> databaseInfo = new ArrayList();
    PriorityQueue<PriorityStrings> priority;
    private HashSet<String> duplicateChecker;
    public StringSearchable(List<String> databaseInfo) {
        this.databaseInfo.addAll(databaseInfo);
    }

    public Collection<String> search(String userInput) {
        priority = new PriorityQueue<>();
        duplicateChecker = new HashSet<>();
        ArrayList founds = new ArrayList();
        Iterator var3 = this.databaseInfo.iterator();
        while(var3.hasNext()) {
            String s = (String)var3.next();

            if(s.indexOf(userInput) == 0 && s.contains(userInput)) {
                double n = ((double) userInput.length()/(double) s.length());
                if(!duplicateChecker.contains(s)){
                    priority.add(new PriorityStrings(n,s));
                    duplicateChecker.add(s);
                }
                }

            }
            int i = 0;
            while(i <10 && !priority.isEmpty()){
                founds.add(priority.poll().getAddress());
                i++;
            }


        return founds;
    }
}
