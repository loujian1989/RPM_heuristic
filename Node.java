/**
 * Created by loujian on 2/8/16.
 */

import java.util.*;

public class Node {
    public Integer player; //means the proposer of the node
    public ArrayList<Integer> player_value; //means the value vector

    public LinkedList<Integer> order; //means the list of players in the order
    public ArrayList<LinkedList<Integer>> preference;

    public Node(int player, ArrayList<Integer> player_value, LinkedList<Integer> outside_order, ArrayList<LinkedList<Integer>> outside_linked_value)
    {
        this.player=player;

        this.player_value = new ArrayList<Integer>();
        this.player_value.clear();
        this.player_value.addAll(player_value);

        this.order= new LinkedList<Integer>();
        this.order.clear();
        this.order.addAll(outside_order);

        this.preference= new ArrayList<LinkedList<Integer>>();
        this.preference.clear();
        this.preference.addAll(outside_linked_value);
    }

}