import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by loujian on 8/4/16.
 */
public class Irving {

    int n; //number of players
    ArrayList<LinkedList<Integer>> linked_value; //the ordinal preferences of all players
    ArrayList<ArrayList<Integer>> value; //simulate the preference profile of all players, they are all values

    Irving(int n, ArrayList<ArrayList<Integer>> value1, ArrayList<LinkedList<Integer>> linked_value1)
    {
        this.n= n;

        value = new ArrayList<ArrayList<Integer>>(); //simulate the preference profile of all players
        linked_value= new ArrayList<LinkedList<Integer>>();
        value.clear();
        linked_value.clear();
        value.addAll(value1);
        linked_value.addAll(linked_value1);
    }

    static Integer[] Irving_Team_Form (int n, ArrayList<LinkedList<Integer>> linked_value, ArrayList<ArrayList<Integer>> value)
    {

        //We first do the first round in Irving's algorithm, that is to mark the current

        boolean[] flag= new boolean[n+1]; //means the whether each player has proposed to someone and accepted.
        for(int i=1; i<=n; i++)
            flag[i]=false;//At the very first no one has proposed and been accepted

        int[] propose = new int[n+1];
        int[] accept= new int[n+1]; // Here we mark the current "propose to" and "accept whom"

        for(int i=1; i<=n; i++) //Here -1 means no one
        {
            propose[i]= -1;
            accept[i]= -1;
        }

        boolean is_stable = true; //denote whether it is stable
        while(is_stable==true)
        {
            int free_proposer=-1; //every round we need to find out one proposer that is free

            for(int i=1; i<=n; i++)
            {
                if(flag[i]==false)
                {
                    free_proposer = i;
                    break;
                }
            }

            if(free_proposer==-1) //if means there is no such a free_proposer, then we can go to the next step.
                break;

            //Now we will mark corresponding propose[free_proposer]

            for(int i=0; i<linked_value.get(free_proposer).size()+1 ; i++)
            {
                if(i==linked_value.get(free_proposer).size()) //we know that there is no stable matching for the profile
                {
                    is_stable= false; //we know that the player free_proposer cannot find a player to accept her offer
                    break;
                }

                Integer free_accepter= linked_value.get(free_proposer).get(i);
                if(accept[free_accepter]==-1) //if the player is available, then we match them together
                {
                    propose[free_proposer]=free_accepter;
                    accept[free_accepter]=free_proposer; //match them together
                    flag[free_proposer]=true;
                    break;
                }
                else //If the player is not available, we need to compare the roommate of the player and free_proposer
                {
                    if(value.get(free_accepter).get(free_proposer) > value.get(free_accepter).get(accept[free_accepter])) //if the free_proposer is better for the player, we change the current matching
                    {
                        flag[accept[free_accepter]]=false;
                        propose[accept[free_accepter]]=-1;
                        propose[free_proposer]= free_accepter;
                        accept[free_accepter]= free_proposer;
                        flag[free_proposer]=true;
                        break;
                    }
                }
            }
        }

        if(is_stable==false)
            return null;

        //We will start the the second procedure of Irving's algorithm
        //We will cross out all the players that are impossible to appear

        for(Integer i=1; i<=n; i++) //for each player i
        {
            for(Integer object= 1; object<=n; object++)
            {
                if(object==i)
                    continue;

                if(linked_value.get(i).indexOf(object)!=-1) //it means object exists in linked_value
                {
                    if(value.get(i).get(object) < value.get(i).get(accept[i]))
                    {
                        linked_value.get(i).remove(object);
                        linked_value.get(object).remove(i);
                    }
                }
            }
        }

        //We will do the third step, to cross out the cycles
        is_stable=true;
        while(is_stable==true)
        {
            Integer starter=-1;

            for(Integer i=1; i<=n; i++)
            {
                if(linked_value.get(i).size()==0)
                {
                    is_stable=false;
                    break;
                }
            }

            for(Integer i=1; i<=n; i++)
            {
                if(linked_value.get(i).size()>=2)
                {
                    starter=i;
                    break;
                }
            }
            if(starter==-1 || is_stable==false)
                break;


            LinkedList<Integer> p = new LinkedList<Integer>();
            LinkedList<Integer> q = new LinkedList<Integer>();
            p.clear();
            q.clear();

            p.add(starter);

            Integer player= linked_value.get(starter).get(1);//get the second one in the preference of starter
            q.add(player);

            int[] exists= new int[n+1];
            for(int i=1; i<=n; i++)
                exists[i]=-1;

            exists[starter]=0; //mark the position of the first player in array p.

            int begin= -1;
            int end= -1; //the index of the circle

            for(int i=1; ; i++)
            {
                player= linked_value.get(player).getLast();
                p.add(player);

                if(exists[player]!=-1)
                {
                    begin=exists[player];
                    end=i;
                    break;
                }
                exists[player]=i;
                player= linked_value.get(player).get(1);
                q.add(player);

            }

            //Then we could reduce the preferences again
            for(int i=begin; i<end; i++)
            {
                Integer q_player= q.get(i);
                Integer p_player= p.get(i+1);

                linked_value.get(q_player).remove(p_player);
                linked_value.get(p_player).remove(q_player);
            }

        }

        if(is_stable==false)
            return null;


        Integer[] teammate= new Integer[n+1];



        for(int i=1; i<=n; i++)
            teammate[i]= linked_value.get(i).getFirst();

        return teammate;


    }



}
