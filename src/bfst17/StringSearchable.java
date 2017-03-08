package bfst17;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class StringSearchable {
    private List<String> databaseInfo = new ArrayList();

    public StringSearchable(List<String> databaseInfo) {
        this.databaseInfo.addAll(databaseInfo);
    }

    public Collection<String> search(String userInput) {
        ArrayList founds = new ArrayList();
        Iterator var3 = this.databaseInfo.iterator();

        while(var3.hasNext()) {
            String s = (String)var3.next();
            if(s.indexOf(userInput) == 0 || s.contains(userInput)) {
                founds.add(s);
            }
        }

        return founds;
    }
}
