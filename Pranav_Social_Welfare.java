import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by loujian on 4/11/16.
 */
public class Pranav_Social_Welfare {
    public static void main(String[] args)throws Exception
    {
        long startTime = System.currentTimeMillis();

        Scanner cin=new Scanner(new File("n14.txt"));
        File writename = new File("Pranav_out_v2.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        int num_cases=100;

        int[] social_welfare_array= new int[num_cases+1];
        int[] max_min_array= new int[num_cases+1];

        int total_sum=0;
        int total_max_min=0;
        int total_deviate=0;
        int total_deviate_profile=0;
        int total_pareto=0;


        for(int iter=1; iter<=num_cases; iter++) {

            int n = 14; //n means the number of players in the game.
            //int order_num; //means the actual number of players after preprocessing
            int depth = 14;// depth means search depth
            LinkedList<Integer> order = new LinkedList<Integer>(); //means the list of players in the order


            //Scanner cin= new Scanner(System.in);
            //n=cin.nextInt(); //the number of players in the game
            //depth=cin.nextInt();//
            //for(int i=0; i<n; i++)
            //    order.add(cin.nextInt()); //input the order in the game.

            for (int i = 0; i < n; i++)
                order.add(i + 1); //input the order in the game.


            ArrayList<ArrayList<Integer>> value = new ArrayList<ArrayList<Integer>>(); //simulate the preference profile of all players
            ArrayList<LinkedList<Integer>> linked_value = new ArrayList<LinkedList<Integer>>();
            ArrayList<LinkedList<Integer>> linked_value_copy = new ArrayList<LinkedList<Integer>>();
            value.clear();
            linked_value.clear();
            linked_value_copy.clear();
            for (int i = 0; i <= n; i++) {
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                LinkedList<Integer> tmp1 = new LinkedList<Integer>();
                LinkedList<Integer> tmp2 = new LinkedList<Integer>();
                tmp.clear();
                tmp1.clear();
                for (int j = 0; j <= n; j++) {
                    tmp.add(0);
                }
                value.add(tmp);
                linked_value.add(tmp1);
                linked_value_copy.add(tmp2);
            }

            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n - 1; j++) {
                    Integer tmp = cin.nextInt();
                    out.write(tmp+" ");
                    value.get(i).set(tmp, n - j + 1); //here we let it be the linear decreasing function
                    //value.get(i).set(tmp, 1<<(n - j)); //here we let it be the exponential decreasing function
                    linked_value.get(i).add(tmp);
                    linked_value_copy.get(i).add(tmp);
                }
                out.write("\r\n");
            }

            Team_Form_Pranav_V2 TF = new Team_Form_Pranav_V2(n, depth, order, value, linked_value);
            ArrayList<Integer> array = TF.ARM();


            //Now we compute the utilitarian social welfare of all players


            //Now we compute the utilitarian social welfare of all players

            Integer[] teammate= new Integer[n+1];

            for (int i = 1; i <= n; i++) {
                if (array.get(i) == 0) {
                    out.write(i + "\r\n");
                    teammate[i]=-1;
                } else {
                    out.write(i + " with " + value.get(i).indexOf(array.get(i)) + "\r\n");
                    teammate[i]=value.get(i).indexOf(array.get(i));
                }
            }

            int social =social_welfare(array);
            int min= max_min_payoff(array);
            total_sum+= social;
            total_max_min+= min;

            social_welfare_array[iter]=social;
            max_min_array[iter]=min;

            out.write("The social welfare is " + social + "\r\n");
            out.write("The max min payoff is " + min + "\r\n");
            //System.out.println(social_welfare(array));

            //Here we deal with the pareto efficiency issue

            boolean[] flag_pair= new boolean[n+1];
            for(int i=1; i<=n; i++)
                flag_pair[i]=true; //at the very first, we assume all player are available

            int number_deviate=0;

            for(int i=1; i<=n; i++) //if A is B's favourite player (or versa), then we don't need to deal with the pair
            {
                Integer A= i;
                Integer B=teammate[i];
                if(linked_value_copy.get(A).get(0)==B || linked_value_copy.get(B).get(0)==A) //if some player is the best for another player, then it is impossible
                {
                    flag_pair[A]=false;
                    flag_pair[B]=false;
                    continue;
                }
            }

            for(int i=1;i<=n; i++) //we deal with every player(also every pair)
            {
                Integer A= i;
                Integer B=teammate[i];

                if(flag_pair[A]==false || flag_pair[B]==false)
                    continue; //these are players are unavailable so we don't need to deal with it

                //now we enumerate players for A to see whether it is possible for player A to deviate
                for(int j=0; j<linked_value_copy.get(A).size(); j++)
                {
                    Integer C= linked_value_copy.get(A).get(j);
                    if(linked_value_copy.get(A).indexOf(C) >= linked_value_copy.get(A).indexOf(B))
                    {
                        flag_pair[A]=false;
                        flag_pair[B]=false;
                        break;  //A only look for better players than B
                    }
                    if(flag_pair[C]==false || C== teammate[A])
                        continue; //C is unavailable, then we don't need to look at C

                    if(linked_value_copy.get(C).indexOf(A) > linked_value_copy.get(C).indexOf(teammate[C]))
                        continue;

                    Integer D= teammate[C];
                    if(linked_value_copy.get(D).indexOf(B) < linked_value_copy.get(D).indexOf(C) && linked_value_copy.get(B).indexOf(D)< linked_value_copy.get(B).indexOf(A))
                    {
                        out.write(A+" "+ B+ " "+ C + " "+ D+ "\r\n");
                        number_deviate++;
                        i=999999;
                        j=999999;
                    }
                }

            }
            out.write("pareto is " + number_deviate + "\r\n");
            total_pareto+= number_deviate;



        }

        double average= (double)total_sum/num_cases;
        double max_min_average= (double)total_max_min/num_cases;
        out.write("The average social welfare is " + average + "\r\n");
        out.write("The average max min is " + max_min_average + "\r\n");
        out.write("The total pareto is " + total_pareto + "\r\n");

        for(int k=1; k<=num_cases; k++)
            out.write(social_welfare_array[k]+ " "+ max_min_array[k]+ "\r\n");

        out.flush();
        out.close();
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
    }

    static int social_welfare(ArrayList<Integer> array)
    {
        int sum=0;
        for(int i=1; i<array.size();i++)
            sum+=array.get(i);

        return sum;
    }

    static int max_min_payoff(ArrayList<Integer> array)
    {
        int max_min_value=999999999;
        for(int i=1; i<array.size();i++)
        {
            if(array.get(i)>0 && array.get(i)<max_min_value)
            {
                max_min_value=array.get(i);
            }
        }

        return max_min_value;
    }

}
