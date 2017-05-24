package db_utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benny on 18/05/2017.
 */

public class Permutations {

    private final List<List<Integer>> listOfAllPermutations;

    public Permutations() {
        this.listOfAllPermutations = new ArrayList<>();
    }

    public List<List<Integer>> perm(List<Integer> mainList)
    {
        this.listOfAllPermutations.clear();
        ArrayList<Integer> prefix = new ArrayList<>();
        perm_aux(prefix, mainList);
        return this.listOfAllPermutations;
    }
    private void perm_aux(List<Integer> prefix, List<Integer> suffixMain) {
        int n = suffixMain.size();
        if (n == 0){
            this.listOfAllPermutations.add(prefix);
            return;
        }
        else {
            for (int i = 0; i < n; i++)
            {
                List<Integer> newMainList = new ArrayList<>(suffixMain.subList(0,i));
                newMainList.addAll(suffixMain.subList(i+1,n));
                List<Integer> newPrefix = new ArrayList<>(prefix);
                newPrefix.add(suffixMain.get(i));
                perm_aux( newPrefix , newMainList);
            }
        }

    }
}
