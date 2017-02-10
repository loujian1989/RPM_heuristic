import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by loujian on 3/26/16.
 */
public class ARM_Strategy {

    public static void main(String[] args)throws Exception
    {
        long startTime = System.currentTimeMillis();

        Scanner cin=new Scanner(new File("ARM_input"));
        File writename = new File("ARM_Strategy_n14_out.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        int num_cases=100;

        int[] social_welfare_array= new int[num_cases+1];
        int[] max_min_array= new int[num_cases+1];

        int total_sum=0;
        int total_max_min=0;
        int total_deviate=0;
        int total_deviate_profile=0;

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

            Team_Formation TF = new Team_Formation(n, depth, order, value, linked_value);
            ArrayList<Integer> array = TF.ARM();


            //Now we compute the utilitarian social welfare of all players


            Integer[] teams= new Integer[n+1];
            for(int i=1; i<=n; i++)
                teams[i]=0; //means every player has no teammate at the very first


            for (int i = 1; i <= n; i++) {
                if (array.get(i) == 0) {
                    out.write(i + "\r\n");
                } else {
                    teams[i]=value.get(i).indexOf(array.get(i)); //get the teammates for every player
                    out.write(i + " with " + teams[i] + "\r\n");
                }
            }


            int[] deviate= new int[n+1];
            for(int i=1; i<=n; i++)
                deviate[i]=0;

            //now we generate a new order that could be more easy to count the number of players that could deviate
            LinkedList<Integer> new_order = new LinkedList<>();
            new_order.clear();
            while(!order.isEmpty())
            {
                Integer current_player= order.getFirst();
                new_order.add(current_player);
                order.remove(current_player);
                if(teams[current_player]!=0) {
                    new_order.add(teams[current_player]);
                    order.remove(teams[current_player]);
                }
            }

            //Now we check whether some player has incentive to deviate the result stored into deviate[]
            while(new_order.size()>=2)
            {
                Integer current_player= new_order.getFirst();
                Integer current_teammate= teams[current_player];
                //we check whether reject players have incentive to cheat
                for(int k=0; k<linked_value_copy.get(current_player).size() && linked_value_copy.get(current_player).get(k)!= current_teammate; k++)
                {
                    Integer deal_player= linked_value_copy.get(current_player).get(k);
                    if(array.get(deal_player)< value.get(deal_player).get(current_player))
                        deviate[deal_player]=1;
                }

                //we check whether accept players have incentive to cheat
                if(new_order.size()>2)
                {
                    for(int k=0; k<linked_value_copy.get(current_teammate).size() && linked_value_copy.get(current_teammate).get(k)!=current_player; k++)
                    {
                        Integer tmp_player= linked_value_copy.get(current_teammate).get(k);
                        if(value.get(tmp_player).get(current_teammate)> array.get(tmp_player)) {
                            deviate[current_teammate]=1;
                        }
                    }
                }

                new_order.remove(current_player);
                new_order.remove(current_teammate);
                for(int k=1; k<=n; k++)
                {
                    linked_value_copy.get(k).remove(current_player);
                    linked_value_copy.get(k).remove(current_teammate);
                }
            }



            int social =social_welfare(array);
            int min= max_min_payoff(array);
            total_sum+= social;
            total_max_min+= min;

            social_welfare_array[iter]=social;
            max_min_array[iter]=min;

            int deviate_sum= deviate_num(deviate);
            total_deviate+=deviate_sum;

            if(deviate_sum>0)
                total_deviate_profile++;

            out.write("The social welfare is " + social + "\r\n");
            out.write("The max min payoff is " + min + "\r\n");
            out.write("The number of player deviate is " + deviate_sum + ". \r\nThe corresponding deviate vector is\r\n");
            for(int i=1; i<=n; i++)
                out.write(deviate[i]+" ");
            out.write("\r\n");
            out.write("\r\n");
            //System.out.println(social_welfare(array));
        }

        double average= (double)total_sum/num_cases;
        double max_min_average= (double)total_max_min/num_cases;
        out.write("The average social welfare is " + average + "\r\n");
        out.write("The average max min is " + max_min_average + "\r\n");
        out.write("The total deviate is " + total_deviate + "\r\n");
        out.write("The total number of deviate profiles is " + total_deviate_profile + "\r\n");

        for(int k=1; k<=num_cases; k++)
            out.write(social_welfare_array[k]+ " "+ max_min_array[k]+ "\r\n");

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        out.write("The total time needed is "+ totalTime+ "\r\n");
        out.flush();
        out.close();
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

    static int deviate_num(int[] deviate)
    {
        int sum=0;
        for(int i=1; i<deviate.length; i++)
            sum+=deviate[i];
        return sum;
    }
}
