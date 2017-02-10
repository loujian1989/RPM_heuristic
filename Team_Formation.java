

import com.sun.jdi.connect.spi.TransportService;
import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.lang.System;
import java.io.*;
import java.util.Scanner;

/**
 * Created by loujian on 2/8/16.
 */
public class Team_Formation {

    int n; //means the number of players in the game.
    int depth; //means search depth
    LinkedList<Integer> order; //means the list of players in the order
    ArrayList<ArrayList<Integer>> value; //simulate the preference profile of all players
    ArrayList<LinkedList<Integer>> linked_value;

    Team_Formation(int n, int depth, LinkedList<Integer> order1, ArrayList<ArrayList<Integer>> value1, ArrayList<LinkedList<Integer>> linked_value1)
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

        /*
        Integer[] flag= new Integer[n+1];
        for(int i=1; i<=n; i++) //at the very first, never rejected by any players
            flag[i]=-1;
        */

        array= DFS(n, root_node, array, depth, value, linked_value);

        return array;

    }


    private static ArrayList<Integer> DFS (int n, Node node, ArrayList<Integer> array, int depth, ArrayList<ArrayList<Integer>> value, ArrayList<LinkedList<Integer>> linked_value) {

        //if (depth <= 0 || node.order.size() == 1||node.order.size()==0) //Here we assume that depth is always less than the overall number of players
          //  return array;

        /*
        if(node.order.size()==2)
        {
            Integer proposer = node.order.getFirst();
            Integer receiver= node.order.get(1);
            array.set(proposer, value.get(proposer).get(receiver));
            array.set(receiver, value.get(receiver).get(proposer));
            return array;
        }
        */


        if(depth<=0|| node.order.size()==0 ||node.order.size()==1)
        {
            return array;
        }



        //Here we do IES procedure
        boolean[] flag_profile=new boolean[n+1];
        for(int i=0; i<=n; i++)  //Denote whether some player is still available
            flag_profile[i]=false;

        Integer[] prefer= new Integer[n+1];
        for(int i=0; i<=n; i++)
            prefer[i]=0;
        boolean count=true;
        while(count==true && node.order.size()>1)
        {
            for(int i=1; i<=n; i++)
            {
                if(!node.preference.get(i).isEmpty())
                    flag_profile[i]=true;
                else
                    flag_profile[i]=false;
            }

            for (int i = 1; i <= n; i++)
            {
                if (flag_profile[i] == true)
                    prefer[i] = node.preference.get(i).getFirst();
            }

            count = false;
            for (Integer i = 1; i <= n; i++)
            {
                for (Integer j = i + 1; j <= n; j++)
                {
                    if (flag_profile[i] == true && flag_profile[j] == true && prefer[i] == j && prefer[j] == i)
                    {
                        array.set(i, value.get(i).get(j));
                        array.set(j, value.get(j).get(i));
                        count = true;
                        for (int k = 1; k <= n; k++)
                        {
                            node.preference.get(k).remove(i);
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



        if(depth<=0|| node.order.size()==0 ||node.order.size()==1)
        {
            return array;
        }




        /*
        Integer[] current_flag= new Integer[n+1];  //Denote the player rejected previously
        for(int i=1; i<=n; i++)
            current_flag[i]=reject_flag[i];
        */


        int max_num=-1; //denote the maximum value the node player could get
        int max_index=-1; //denote the corresponding index of maximum value
        ArrayList<ArrayList<Integer>>storage= new ArrayList<ArrayList<Integer>>(n+1);
        //storage.clear();
        //storage.add(array);//what we do is just fill the 0 index of storage
        Integer proposer= node.order.getFirst();

       // boolean propose_or_not=false; //to denote whether the player has chance to propose (may be all other players are pruned)


        //As rejecting share the same subtree, so we firstly deal with rejecting issues

        //now we consider what if the player reject the offer
        LinkedList<Integer> order_reject= new LinkedList<Integer>(); //the order if the receiver rejects the offer
        order_reject.clear();
        order_reject.addAll(node.order);
        Integer tmp =order_reject.getFirst();
        order_reject.removeFirst();
        order_reject.add(tmp); //if it is reject, then remove the first one and place it into the end of the order
        //if it is reject, then array will not change, we could make them the same

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


            /*
            Integer[] tmp_flag= new Integer[n+1];
            for(int k=1; k<=n; k++)
                tmp_flag[k]=current_flag[k];

            if(current_flag[receiver]==-1)
                current_flag[receiver]=proposer;
            else
            {
                if(linked_value.get(receiver).indexOf(current_flag[receiver]) > linked_value.get(receiver).indexOf(proposer))
                    current_flag[receiver]=proposer;
            }
            */

        Integer newplayer1=order_reject.getFirst();
        Node reject_node= new Node(newplayer1, array, order_reject, reject_preference);
        int tmp_depth_1= depth-1;
        if(tmp_depth_1>=order_reject.size())
            tmp_depth_1=order_reject.size();

        ArrayList<Integer> reject_array= DFS(n, reject_node, array, tmp_depth_1, value, linked_value);


        for(int i=0; i<node.preference.get(proposer).size(); i++)
        {
            Integer receiver = node.preference.get(proposer).get(i);


            /*
            if(current_flag[proposer]>=0)
            {
                int current_index= linked_value.get(proposer).indexOf(receiver);
                int previous_index= linked_value.get(proposer).indexOf(current_flag[proposer]);
                if(current_index >= previous_index)
                {
                    storage.add(array);//just add some random things into storage, there is no means here
                    continue;
                }
            }
            */



            //propose_or_not=true;




            //Maybe she could definitely get higher payoff by rejecting the offer, then we have no reason to explore the accept cases
            if(reject_array.get(receiver)> value.get(receiver).get(proposer))
            {
                storage.add(reject_array);
                if(reject_array.get(proposer)>max_num)
                {
                    max_num=reject_array.get(proposer);
                    max_index=i;
                }
                continue;
            }

            //now we consider what if the player receives the offer
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
                accept_preference.get(k).remove(receiver);
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
                accept_array = DFS(n, accept_node, accept_array, tmp_depth, value, linked_value);
            }



            /*
            if(reject_array.get(receiver)> accept_array.get(receiver))
            {
                storage.add(reject_array);
                if(reject_array.get(proposer)>max_num)
                {
                    max_num=reject_array.get(proposer);
                    max_index=i;
                }
            }
            */
            storage.add(accept_array);
            if(accept_array.get(proposer)>max_num)
            {
                max_num=accept_array.get(proposer);
                max_index=i;
            }
        }

        //if(propose_or_not==false)
        //    return array;
        //else
            return storage.get(max_index);
    }

}
