import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by loujian on 8/7/16.
 */
public class AAM_SW_Welfare_Real_Network {


    public static void main(String[] args)throws Exception
    {
        long startTime = System.currentTimeMillis();

        Scanner cin=new Scanner(new File("SN_vandebunt_t5_cleaned.txt")); //It is a linked list that denote the ordinal preference of the profile
        Scanner cin_orginal= new Scanner(new File("vandebunt_t5_cleaned.txt")); //It is a matrix that store the utility for other players

        File writename = new File("AAM_SN_vandebunt_t5_cleaned.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));
        int n = 30; //n means the number of players in the game.
        double max_utility=4.0;

        int num_cases=100;

        double[] social_welfare_array= new double[num_cases+1];
        double[] max_min_array= new double[num_cases+1];
        double[] variance_array= new double[num_cases+1];

        double total_sum=0;
        double total_max_min=0;
        double total_variance=0;
        int total_deviate=0;
        int total_deviate_profile=0;


        //Here we input the orginal utility of the matrix
        int[][] orginal_integer = new int[n+1][n+1];
        double[][] original= new double[n+1][n+1];

        for(int i=1; i<=n; i++)
        {
            for(int j=1; j<=n; j++) {
                orginal_integer[i][j] = cin_orginal.nextInt();
                original[i][j]= 1.0*orginal_integer[i][j]/max_utility;
            }
        }



        for(int iter=1; iter<=num_cases; iter++) {


            //int order_num; //means the actual number of players after preprocessing
            int depth = n;// depth means search depth

            LinkedList<Integer> order = new LinkedList<Integer>(); //means the list of players in the order
            order.clear();

            boolean[] flag_order= new boolean[n+1];
            for(int i=1; i<=n; i++)
                flag_order[i]=false;

            Random rd= new Random();

            while(order.size()<n)
            {
                Integer tmp= rd.nextInt(n);
                tmp++;
                if(flag_order[tmp]==false) {
                    order.add(tmp);
                    flag_order[tmp]=true;
                }
                else
                {
                    continue;
                }
            }



            //Scanner cin= new Scanner(System.in);
            //n=cin.nextInt(); //the number of players in the game
            //depth=cin.nextInt();//
            //for(int i=0; i<n; i++)
            //    order.add(cin.nextInt()); //input the order in the game.

            // for (int i = 0; i < n; i++)
            //    order.add(i + 1); //input the order in the game.



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
                    if(j==i)
                        tmp.add(0);
                    else
                        tmp.add(-1);
                }
                value.add(tmp);
                linked_value.add(tmp1);
                linked_value_copy.add(tmp2);
            }

            for (int i = 1; i <= n; i++)
            {
                Integer number_nei= cin.nextInt();
                for (int j = 1; j <= number_nei; j++) {
                    Integer tmp = cin.nextInt();
                    out.write(tmp+" ");
                    value.get(i).set(tmp, n - j + 1); //here we let it be the linear decreasing function
                    //value.get(i).set(tmp, 1<<(n - j)); //here we let it be the exponential decreasing function
                    linked_value.get(i).add(tmp);
                    linked_value_copy.get(i).add(tmp);
                }

                out.write("\r\n");
            }

            AAM_SN TF = new AAM_SN(n, depth, order, value, linked_value);
            //Team_Form_Pranav TF= new Team_Form_Pranav(n, depth, order, value, linked_value);
            ArrayList<Integer> array = TF.ARM();


            //Now we compute the utilitarian social welfare of all players


            //Now we compute the utilitarian social welfare of all players

            Integer[] teammate= new Integer[n+1];

            for(int i=1; i<=n; i++)
                teammate[i]=0;


            for (int i = 1; i <= n; i++) {
                if (array.get(i) == 0) {
                    out.write(i + "\r\n");
                } else {
                    out.write(i + " with " + value.get(i).indexOf(array.get(i)) + "\r\n");
                    teammate[i]=value.get(i).indexOf(array.get(i));
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
                if(teammate[current_player]!=0) {
                    new_order.add(teammate[current_player]);
                    order.remove(teammate[current_player]);
                }
            }

            //Now we check whether some player has incentive to deviate the result stored into deviate[]
            while(new_order.size()>=2)
            {
                Integer current_player= new_order.getFirst();
                Integer current_teammate= teammate[current_player];
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



            double social =social_welfare(original, n, teammate);
            double min= max_min_payoff(original, n, teammate);
            double current_variance= variance(original, n, teammate);

            total_sum+= social;
            total_max_min+= min;
            total_variance+= current_variance;

            social_welfare_array[iter]=social;
            max_min_array[iter]=min;
            variance_array[iter]=current_variance;

            int deviate_sum= deviate_num(deviate);
            total_deviate+=deviate_sum;

            if(deviate_sum>0)
                total_deviate_profile++;

            out.write("The social welfare is " + social + "\r\n");
            out.write("The max min payoff is " + min + "\r\n");
            out.write("The variance is " + current_variance + "\r\n");
            out.write("The number of player deviate is " + deviate_sum + ". \r\nThe corresponding deviate vector is\r\n");
            for(int i=1; i<=n; i++)
                out.write(deviate[i]+" ");
            out.write("\r\n");
            out.write("\r\n");

        }

        double average= (double)total_sum/num_cases;
        double max_min_average= (double)total_max_min/num_cases;
        out.write("The average social welfare is " + average + "\r\n");
        out.write("The average max min is " + max_min_average + "\r\n");
        out.write("The average variance is " + (double)total_variance/num_cases + "\r\n");

        out.write("The total deviate is " + total_deviate + "\r\n");
        out.write("The total number of deviate profiles is " + total_deviate_profile + "\r\n");

        for(int k=1; k<=num_cases; k++)
            out.write(social_welfare_array[k]+ " "+ max_min_array[k]+  " " + variance_array[k] + "\r\n");


        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        out.write("The total time needed is "+ totalTime);
        out.flush();
        out.close();
        System.out.println(totalTime);
    }

    static double social_welfare(double[][] orignal_utlity, int n, Integer[] teammmate)
    {
        double sum=0;
        for(int i=1; i<=n;i++)
        {
            if(teammmate[i]>0)
                sum+= orignal_utlity[i][teammmate[i]];
        }


        return sum/n;
    }

    static double max_min_payoff(double[][] orignal_utlity, int n, Integer[] teammmate)
    {
        double max_min_value=999999999;
        for(int i=1; i<=n; i++)
        {
            if(teammmate[i]==0)
                max_min_value=0;
            else if( orignal_utlity[i][teammmate[i]]< max_min_value)
            {
                max_min_value= orignal_utlity[i][teammmate[i]];
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

    static double variance(double[][] orignal_utlity, int n, Integer[] teammmate)
    {
        double sum= social_welfare(orignal_utlity, n, teammmate);
        double average= sum;

        double variance_value=0;
        for(int i=1; i<=n; i++)
        {
            if(teammmate[i]==0)
                variance_value+= average*average;
            else
            {
                variance_value+= (orignal_utlity[i][teammmate[i]]- average) * (orignal_utlity[i][teammmate[i]]- average);
            }
        }

        variance_value= variance_value/n;
        return variance_value;

    }


}
