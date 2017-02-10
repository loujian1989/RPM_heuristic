/**
 * Created by loujian on 11/12/16.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.lang.System;
import java.io.*;
import java.util.Scanner;

public class AAM_SN_Heuristic_noIES {

    int n; //number of players
    int depth; //the depth of search
    double lower_accept;
    double upper_reject;

    LinkedList<Integer> order; //means the list of players in the order
    ArrayList<ArrayList<Integer>> value; //simulate the preference profile of all players, they are all values
    ArrayList<LinkedList<Integer>> linked_value; //the ordinal preferences of all players

    AAM_SN_Heuristic_noIES (int n, int depth, LinkedList<Integer> order1, ArrayList<ArrayList<Integer>> value1, ArrayList<LinkedList<Integer>> linked_value1, double lower_accept, double upper_reject)
    {
        this.n= n;
        this.depth= depth;
        this.lower_accept= lower_accept;
        this.upper_reject= upper_reject;

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

        array= DFS(n, root_node, array, depth, value, linked_value, 0, lower_accept, upper_reject);

        return array;
    }

    private static ArrayList<Integer> DFS (int n, Node node, ArrayList<Integer> array, int depth, ArrayList<ArrayList<Integer>> value, ArrayList<LinkedList<Integer>> linked_value, int rank, double lower_accept, double upper_reject)
    {
        if(depth<=0|| node.order.size()==0 ||node.order.size()==1)
        {
            return array;
        }

        if(rank==0) {

            /*

            //Here we do IES procedure
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


        //We will compute the heuristic of the accepting or rejecting the proposal.
        //We firstly copy current node and preference into the order_test and test_preference
        LinkedList<Integer> order_test = new LinkedList<Integer>(); //the order if the receiver accept the offer
        order_test.clear();
        order_test.addAll(node.order);

        //If the receiver accepts the offer, then we will remove the two players from preference
        ArrayList<LinkedList<Integer>> test_preference= new ArrayList<LinkedList<Integer>>();
        test_preference.clear();

        LinkedList<Integer> non_sense1= new LinkedList<>();
        non_sense1.add(1);
        test_preference.add(non_sense1); //just to fill the index 0, non-sense here
        for(int k=1; k<=n; k++)
        {
            LinkedList<Integer> tmp1_list= new LinkedList<>();
            tmp1_list.addAll(node.preference.get(k));
            test_preference.add(tmp1_list);
        }

        double accept_or_reject_ratio=0;
        double better_ratio= (double)node.preference.get(receiver).indexOf(proposer)/ (node.preference.get(receiver).size());
        int total_other_player= node.preference.get(receiver).size()-1; //Here it is possible to be 0

        double total_other_ratio=0;
        for(int k=0; k<node.preference.get(receiver).size(); k++)
        {
            Integer other_player= node.preference.get(receiver).get(k);
            if(other_player==proposer) //we only consider players that is better than proposer for the receiver
                break;

            total_other_ratio+= 1 - node.preference.get(other_player).indexOf(receiver)/ node.preference.get(other_player).size();
        }

        if (total_other_player==0)
            accept_or_reject_ratio=0; //there is no other player, then it has to accept the proposal
        else
            accept_or_reject_ratio= better_ratio* (total_other_ratio/total_other_player);

        //Then we will look at accept_or_reject_ratio to prune some subtrees

        if(accept_or_reject_ratio<= lower_accept) //Then we will accept the proposal
        {
            order_test.remove(proposer);
            order_test.remove(receiver);
            for(int k=1; k<=n; k++) {
                if(test_preference.get(k).indexOf(receiver)!=-1)
                    test_preference.get(k).remove(receiver);
                if(test_preference.get(k).indexOf(proposer)!=-1)
                    test_preference.get(k).remove(proposer);
            }

            ArrayList<Integer> test_array = new ArrayList<Integer>(n+1); //denote the value each player could get by accepting the offer
            //accept_array.clear();
            test_array.addAll(array);
            test_array.set(proposer, value.get(proposer).get(receiver));
            test_array.set(receiver, value.get(receiver).get(proposer));

            if(!order_test.isEmpty())
            {
                Integer newplayer = order_test.getFirst();
                Node accept_node = new Node(newplayer, test_array, order_test, test_preference);
                int tmp_depth=depth-1;
                if(tmp_depth>=order_test.size()) //if the depth is greater than the order size, then we do not need to search that deep
                    tmp_depth=order_test.size();
                test_array = DFS(n, accept_node, test_array, tmp_depth, value, linked_value, 0, lower_accept, upper_reject);
                return test_array;
            }
            else
                return test_array;

        }

        else if(accept_or_reject_ratio> upper_reject)
        {
            int tmp_depth_1= depth;
            if(rank== test_preference.get(proposer).size()-1)
            {
                rank=0;
                order_test.removeFirst();
                for(int k=1; k<=n; k++) {
                    if(test_preference.get(k).indexOf(proposer)!=-1)
                        test_preference.get(k).remove(proposer);
                }
                tmp_depth_1--;
                if(tmp_depth_1>=order_test.size())
                    tmp_depth_1=order_test.size();
            }
            else
            {
                rank++;
            }

            Integer newplayer1=order_test.getFirst();
            Node reject_node= new Node(newplayer1, array, order_test, test_preference);

            ArrayList<Integer> test_array= DFS(n, reject_node, array, tmp_depth_1, value, linked_value, rank, lower_accept, upper_reject);
            return test_array;
        }



        else
        { //between the two threshold, then we have to do the backward reduction

            //we consider the case in which accept the offer
            LinkedList<Integer> order_accept = new LinkedList<Integer>(); //the order if the receiver accept the offer
            order_accept.clear();
            order_accept.addAll(node.order);
            order_accept.remove(proposer);
            order_accept.remove(receiver); //if it is accept, remove the two players from the order to get the new order

            //If the receiver accepts the offer, then we will remove the two players from preference
            ArrayList<LinkedList<Integer>> accept_preference = new ArrayList<LinkedList<Integer>>();
            accept_preference.clear();

            LinkedList<Integer> non_sense = new LinkedList<>();
            non_sense.add(1);
            accept_preference.add(non_sense); //just to fill the index 0, non-sense here
            for (int k = 1; k <= n; k++) {
                LinkedList<Integer> tmp1_list = new LinkedList<>();
                tmp1_list.addAll(node.preference.get(k));
                accept_preference.add(tmp1_list);
            }

            for (int k = 1; k <= n; k++) {
                if (accept_preference.get(k).indexOf(receiver) != -1)
                    accept_preference.get(k).remove(receiver);
                if (accept_preference.get(k).indexOf(proposer) != -1)
                    accept_preference.get(k).remove(proposer);
            }

            ArrayList<Integer> accept_array = new ArrayList<Integer>(n + 1); //denote the value each player could get by accepting the offer
            //accept_array.clear();
            accept_array.addAll(array);
            accept_array.set(proposer, value.get(proposer).get(receiver));
            accept_array.set(receiver, value.get(receiver).get(proposer));

            if (!order_accept.isEmpty()) {
                Integer newplayer = order_accept.getFirst();
                Node accept_node = new Node(newplayer, accept_array, order_accept, accept_preference);
                int tmp_depth = depth - 1;
                if (tmp_depth >= order_accept.size()) //if the depth is greater than the order size, then we do not need to search that deep
                    tmp_depth = order_accept.size();
                accept_array = DFS(n, accept_node, accept_array, tmp_depth, value, linked_value, 0, lower_accept, upper_reject);
            }

            //We consider cases in which reject the offer
            LinkedList<Integer> order_reject = new LinkedList<Integer>(); //the order if the receiver rejects the offer
            order_reject.clear();
            order_reject.addAll(node.order);
            Integer tmp = order_reject.getFirst();

            ArrayList<LinkedList<Integer>> reject_preference = new ArrayList<LinkedList<Integer>>();
            reject_preference.clear();
            LinkedList<Integer> non_sense2 = new LinkedList<>();
            non_sense2.add(1);
            reject_preference.add(non_sense2); //just to fill the index 0, non-sense here

            for (int k = 1; k <= n; k++) {
                LinkedList<Integer> tmp_list = new LinkedList<>();
                tmp_list.addAll(node.preference.get(k));
                reject_preference.add(tmp_list);
            }

            int tmp_depth_1 = depth;
            if (rank == reject_preference.get(proposer).size() - 1) // will revise it later
            {
                rank = 0;
                order_reject.removeFirst();
                for (int k = 1; k <= n; k++) {
                    if (reject_preference.get(k).indexOf(proposer) != -1)
                        reject_preference.get(k).remove(proposer);
                }
                tmp_depth_1--;
                if (tmp_depth_1 >= order_reject.size())
                    tmp_depth_1 = order_reject.size();
            } else {
                rank++;
            }

            Integer newplayer1 = order_reject.getFirst();
            Node reject_node = new Node(newplayer1, array, order_reject, reject_preference);

            ArrayList<Integer> reject_array = DFS(n, reject_node, array, tmp_depth_1, value, linked_value, rank, lower_accept, upper_reject);

            if (accept_array.get(receiver) >= reject_array.get(receiver))
                return accept_array;
            else
                return reject_array;
        }

    }

}
