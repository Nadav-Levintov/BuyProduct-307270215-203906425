package db_utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by benny on 20/05/2017.
 */
public class PermutationsTest {


    @Test
    public void perm() throws Exception {

        Permutations perm = new Permutations();
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        List<Integer> list3 = new ArrayList<>();
        List<Integer> list4 = new ArrayList<>();

        List<List<Integer>> list1res = new ArrayList<>();
        List<List<Integer>> list2res = new ArrayList<>();
        List<List<Integer>> list3res = new ArrayList<>();
        List<List<Integer>> list4res = new ArrayList<>();


        //permutation size 1
        list1.add(1);
        list1res.addAll(perm.perm(list1));
        assertEquals(list1res.size(),1);
        assertEquals(list1res.get(0).size(),1);
        assertEquals((Integer)(list1res.get(0).get(0)) , (Integer) 1);

        //permutation size 2
        list2.add(1);
        list2.add(2);
        list2res.addAll(perm.perm(list2));
        assertEquals(list2res.size(),2);
        assertEquals(list2res.get(0).size(),2);
        assertEquals(list2res.get(1).size(),2);
        assertEquals((Integer)(list2res.get(0).get(0)) , (Integer) 1);
        assertEquals((Integer)(list2res.get(0).get(1)) , (Integer) 2);
        assertEquals((Integer)(list2res.get(1).get(0)) , (Integer) 2);
        assertEquals((Integer)(list2res.get(1).get(1)) , (Integer) 1);

        //permutation size 3
        list3.add(1);
        list3.add(2);
        list3.add(3);
        list3res.addAll(perm.perm(list3));
        assertEquals(list3res.size(),6);
        assertEquals(list3res.get(0).size(),3);
        assertEquals(list3res.get(1).size(),3);
        assertEquals(list3res.get(2).size(),3);
        assertEquals((Integer)(list3res.get(0).get(0)) , (Integer) 1);
        assertEquals((Integer)(list3res.get(0).get(1)) , (Integer) 2);
        assertEquals((Integer)(list3res.get(0).get(2)) , (Integer) 3);

        assertEquals((Integer)(list3res.get(1).get(0)) , (Integer) 1);
        assertEquals((Integer)(list3res.get(1).get(1)) , (Integer) 3);
        assertEquals((Integer)(list3res.get(1).get(2)) , (Integer) 2);

        assertEquals((Integer)(list3res.get(2).get(0)) , (Integer) 2);
        assertEquals((Integer)(list3res.get(2).get(1)) , (Integer) 1);
        assertEquals((Integer)(list3res.get(2).get(2)) , (Integer) 3);

        assertEquals((Integer)(list3res.get(3).get(0)) , (Integer) 2);
        assertEquals((Integer)(list3res.get(3).get(1)) , (Integer) 3);
        assertEquals((Integer)(list3res.get(3).get(2)) , (Integer) 1);

        assertEquals((Integer)(list3res.get(4).get(0)) , (Integer) 3);
        assertEquals((Integer)(list3res.get(4).get(1)) , (Integer) 1);
        assertEquals((Integer)(list3res.get(4).get(2)) , (Integer) 2);

        assertEquals((Integer)(list3res.get(5).get(0)) , (Integer) 3);
        assertEquals((Integer)(list3res.get(5).get(1)) , (Integer) 2);
        assertEquals((Integer)(list3res.get(5).get(2)) , (Integer) 1);

        //permutation size 4
        list4.add(1);
        list4.add(2);
        list4.add(3);
        list4.add(4);
        list4res.addAll(perm.perm(list4));
        assertEquals(list4res.size(),24);
        assertEquals(list4res.get(0).size(),4);
        assertEquals(list4res.get(1).size(),4);
        assertEquals(list4res.get(2).size(),4);
        assertEquals(list4res.get(3).size(),4);

        assertEquals((Integer)(list4res.get(0).get(0)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(0).get(1)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(0).get(2)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(0).get(3)) , (Integer) 4);

        assertEquals((Integer)(list4res.get(1).get(0)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(1).get(1)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(1).get(2)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(1).get(3)) , (Integer) 3);

        assertEquals((Integer)(list4res.get(2).get(0)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(2).get(1)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(2).get(2)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(2).get(3)) , (Integer) 4);

        assertEquals((Integer)(list4res.get(3).get(0)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(3).get(1)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(3).get(2)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(3).get(3)) , (Integer) 2);

        assertEquals((Integer)(list4res.get(4).get(0)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(4).get(1)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(4).get(2)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(4).get(3)) , (Integer) 3);

        assertEquals((Integer)(list4res.get(5).get(0)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(5).get(1)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(5).get(2)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(5).get(3)) , (Integer) 2);

        assertEquals((Integer)(list4res.get(6).get(0)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(6).get(1)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(6).get(2)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(6).get(3)) , (Integer) 4);

        assertEquals((Integer)(list4res.get(7).get(0)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(7).get(1)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(7).get(2)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(7).get(3)) , (Integer) 3);

        assertEquals((Integer)(list4res.get(8).get(0)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(8).get(1)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(8).get(2)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(8).get(3)) , (Integer) 4);

        assertEquals((Integer)(list4res.get(9).get(0)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(9).get(1)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(9).get(2)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(9).get(3)) , (Integer) 1);

        assertEquals((Integer)(list4res.get(10).get(0)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(10).get(1)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(10).get(2)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(10).get(3)) , (Integer) 3);

        assertEquals((Integer)(list4res.get(11).get(0)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(11).get(1)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(11).get(2)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(11).get(3)) , (Integer) 1);

        assertEquals((Integer)(list4res.get(12).get(0)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(12).get(1)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(12).get(2)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(12).get(3)) , (Integer) 4);

        assertEquals((Integer)(list4res.get(13).get(0)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(13).get(1)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(13).get(2)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(13).get(3)) , (Integer) 2);

        assertEquals((Integer)(list4res.get(14).get(0)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(14).get(1)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(14).get(2)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(14).get(3)) , (Integer) 4);

        assertEquals((Integer)(list4res.get(15).get(0)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(15).get(1)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(15).get(2)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(15).get(3)) , (Integer) 1);

        assertEquals((Integer)(list4res.get(16).get(0)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(16).get(1)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(16).get(2)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(16).get(3)) , (Integer) 2);

        assertEquals((Integer)(list4res.get(17).get(0)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(17).get(1)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(17).get(2)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(17).get(3)) , (Integer) 1);

        assertEquals((Integer)(list4res.get(18).get(0)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(18).get(1)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(18).get(2)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(18).get(3)) , (Integer) 3);

        assertEquals((Integer)(list4res.get(19).get(0)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(19).get(1)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(19).get(2)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(19).get(3)) , (Integer) 2);

        assertEquals((Integer)(list4res.get(20).get(0)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(20).get(1)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(20).get(2)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(20).get(3)) , (Integer) 3);

        assertEquals((Integer)(list4res.get(21).get(0)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(21).get(1)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(21).get(2)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(21).get(3)) , (Integer) 1);

        assertEquals((Integer)(list4res.get(22).get(0)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(22).get(1)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(22).get(2)) , (Integer) 1);
        assertEquals((Integer)(list4res.get(22).get(3)) , (Integer) 2);

        assertEquals((Integer)(list4res.get(23).get(0)) , (Integer) 4);
        assertEquals((Integer)(list4res.get(23).get(1)) , (Integer) 3);
        assertEquals((Integer)(list4res.get(23).get(2)) , (Integer) 2);
        assertEquals((Integer)(list4res.get(23).get(3)) , (Integer) 1);

    }

}