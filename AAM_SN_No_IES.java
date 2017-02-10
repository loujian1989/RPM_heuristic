/**
 * Created by loujian on 8/6/16.
 */

import com.sun.jdi.connect.spi.TransportService;
import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.lang.System;
import java.io.*;
import java.util.Scanner;

public class AAM_SN_No_IES {

    int n; //number of players
    int depth; //the depth of search

    LinkedList<Integer> order; //means the list of players in the order
    ArrayList<ArrayList<Integer>> value; //simulate the preference profile of all players, they are all values
    ArrayList<LinkedList<Integer>> linked_value; //the ordinal preferences of all players

    AAM_SN_No_IES(int n, int depth, LinkedList<Integer> order1, ArrayList<ArrayList<Integer>> value1, ArrayList<LinkedList<Integer>> linked_value1)
    {
        this.n= n;
        this.depth= depth;

        order= new LinkedList<Integer>(); //means the list of players in the order
        order.clear();
        order.addAll(order1);

        value = new ArrayList<ArrayList<Integer>>(); //simulate the preference profile of all players
        linked_value= new ArrayList<LinkedList<Integer>>();
        value.clear();
        linked_value.clear();
        value.addAll(value1);
        linked_value.addAll(linked_value1);
    }

    public ArrayList<Integer> ARM ()
    {
        Integer root_player= order.getFirst();//get the root node of the search tree
        ArrayList<Integer> array= new ArrayList<Integer>(n+1);
        //array.clear();
        for(int i=0; i<=n;i++)
            array.add(0);
        Node root_node = new Node(root_player, array, order, linked_value);

        array= DFS(n, root_node, array, depth, value, linked_value, 0);

        return array;
    }

    private static ArrayList<Integer> DFS (int n, Node node, ArrayList<Integer> array, int depth, ArrayList<ArrayList<Integer>> value, ArrayList<LinkedList<Integer>> linked_value, int rank)
    {
        if(depth<=0|| node.order.size()==0 ||node.order.size()==1)
        {
            return array;
        }

        if(rank==0) {
            //Here we do IES procedure
            /*
            boolean[] flag_profile = new boolean[n + 1];
            for (int i = 0; i <= n; i++)  //Denote whether some player is still available
                flag_profile[i] = false;

            Integer[] prefer = new Integer[n + 1];
            for (int i = 0; i <= n; i++)
                prefer[i] = 0;
            boolean count = true;
            while (count == true && node.order.size() > 1) {
                for (int i = 1; i <= n; i++) {
                    if (!node.preference.get(i).isEmpty())
                        flag_profile[i] = true;
                    else
                        flag_profile[i] = false;
                }

                for (int i = 1; i <= n; i++) {
                    if (flag_profile[i] == true)
                        prefer[i] = node.preference.get(i).getFirst();
                }

                count = false;
                for (Integer i = 1; i <= n; i++) {
                    for (Integer j = i + 1; j <= n; j++) {
                        if (flag_profile[i] == true && flag_profile[j] == true && prefer[i] == j && prefer[j] == i) {
                            array.set(i, value.get(i).get(j));
                            array.set(j, value.get(j).get(i));
                            count = true;
                            for (int k = 1; k <= n; k++) {
                                if(node.preference.get(k).indexOf(i)!=-1)
                                    node.preference.get(k).remove(i);
                                if(node.preference.get(k).indexOf(j)!=-1)
                                    node.preference.get(k).remove(j);
                            }
                            node.order.remove(i);
                            node.order.remove(j);

                            i = n + 1;
                            j = n + 1;//we could use this way to break the two loops
                        }
                    }
                }
            */


            if (depth <= 0 || node.order.size() == 0 || node.order.size() == 1) {
                return array;
            }
        }


        Integer proposer= node.order.getFirst();

        while(node.preference.get(proposer).isEmpty())
        {
            if(node.order.size()==1)
                return array;
            else
            {
                node.order.removeFirst();
                proposer=node.order.getFirst();
            }
        }

        Integer receiver= node.preference.get(proposer).get(rank);


        //we consider the case in which accept the offer
        LinkedList<Integer> order_accept = new LinkedList<Integer>(); //the order if the receiver accept the offer
        order_accept.clear();
        order_accept.addAll(node.order);
        order_accept.remove(proposer);
        order_accept.remove(receiver); //if it is accept, remove the two players from the order to get the new order

        //If the receiver accepts the offer, then we will remove the two players from preference
        ArrayList<LinkedList<Integer>> accept_preference= new ArrayList<LinkedList<Integer>>();
        accept_preference.clear();

        LinkedList<Integer> non_sense= new LinkedList<>();
        non_sense.add(1);
        accept_preference.add(non_sense); //just to fill the index 0, non-sense here
        for(int k=1; k<=n; k++)
        {
            LinkedList<Integer> tmp1_list= new LinkedList<>();
            tmp1_list.addAll(node.preference.get(k));
            accept_preference.add(tmp1_list);
        }

        for(int k=1; k<=n; k++) {
            if(accept_preference.get(k).indexOf(receiver)!=-1)
                accept_preference.get(k).remove(receiver);
            if(accept_preference.get(k).indexOf(proposer)!=-1)
                accept_preference.get(k).remove(proposer);
        }

        ArrayList<Integer> accept_array = new ArrayList<Integer>(n+1); //denote the value each player could get by accepting the offer
        //accept_array.clear();
        accept_array.addAll(array);
        accept_array.set(proposer, value.get(proposer).get(receiver));
        accept_array.set(receiver, value.get(receiver).get(proposer));

        if(!order_accept.isEmpty())
        {
            Integer newplayer = order_accept.getFirst();
            Node accept_node = new Node(newplayer, accept_array, order_accept, accept_preference);
            int tmp_depth=depth-1;
            if(tmp_depth>=order_accept.size()) //if the depth is greater than the order size, then we do not need to search that deep
                tmp_depth=order_accept.size();
            accept_array = DFS(n, accept_node, accept_array, tmp_depth, value, linked_value, 0);
        }

        //We consider cases in which reject the offer
        LinkedList<Integer> order_reject= new LinkedList<Integer>(); //the order if the receiver rejects the offer
        order_reject.clear();
        order_reject.addAll(node.order);
        Integer tmp =order_reject.getFirst();

        ArrayList<LinkedList<Integer>> reject_preference= new ArrayList<LinkedList<Integer>>();
        reject_preference.clear();
        LinkedList<Integer> non_sense1= new LinkedList<>();
        non_sense1.add(1);
        reject_preference.add(non_sense1); //just to fill the index 0, non-sense here

        for(int k=1; k<=n; k++)
        {
            LinkedList<Integer> tmp_list= new LinkedList<>();
            tmp_list.addAll(node.preference.get(k));
            reject_preference.add(tmp_list);
        }

        int tmp_depth_1= depth;
        if(rank== reject_preference.get(proposer).size()-1) // will revise it later
        {
            rank=0;
            order_reject.removeFirst();
            for(int k=1; k<=n; k++) {
                if(reject_preference.get(k).indexOf(proposer)!=-1)
                    reject_preference.get(k).remove(proposer);
            }
            tmp_depth_1--;
            if(tmp_depth_1>=order_reject.size())
                tmp_depth_1=order_reject.size();
        }
        else
        {
            rank++;
        }

        Integer newplayer1=order_reject.getFirst();
        Node reject_node= new Node(newplayer1, array, order_reject, reject_preference);

        ArrayList<Integer> reject_array= DFS(n, reject_node, array, tmp_depth_1, value, linked_value, rank);

        if(accept_array.get(receiver)>= reject_array.get(receiver))
            return accept_array;
        else
            return reject_array;

    }
}
