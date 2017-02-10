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
public class RSD_Real_Network {


    public static void main(String[] args)throws Exception
    {
        Scanner cin=new Scanner(new File("SN_vandebunt_t5_cleaned.txt"));
        Scanner cin_orginal= new Scanner(new File("vandebunt_t5_cleaned.txt")); //It is a matrix that store the utility for other players

        File writename = new File("RSD_SN_vandebunt_t5_cleaned.txt");
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


        for(int iter=1; iter<=num_cases; iter++)
        {
            LinkedList<Integer> order = new LinkedList<Integer>();

            //for (int i = 0; i < n; i++)
            //   order.add(i + 1); //input the order in the game.


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


            ArrayList<ArrayList<Integer>> value = new ArrayList<ArrayList<Integer>>(); //simulate the preference profile of all players
            ArrayList<LinkedList<Integer>> linked_value = new ArrayList<LinkedList<Integer>>();

            value.clear();
            linked_value.clear();
            for (int i = 0; i <= n; i++) {
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                LinkedList<Integer> tmp1 = new LinkedList<Integer>();
                tmp.clear();
                tmp1.clear();
                for (int j = 0; j <= n; j++) {
                    tmp.add(0);
                }
                value.add(tmp);
                linked_value.add(tmp1);
            }

            for (int i = 1; i <= n; i++) {
                int num_neigh = cin.nextInt();
                for (int j = 1; j <= num_neigh; j++) {
                    Integer tmp = cin.nextInt();
                    value.get(i).set(tmp, n - j + 1); //here we let it be the linear decreasing function
                    //value.get(i).set(tmp, 1<<(n - j)); //here we let it be the exponential decreasing function
                    linked_value.get(i).add(tmp);
                }
            }

            ArrayList<Integer> array= new ArrayList<Integer>(n+1);
            array.add(0);
            for(int i=1; i<=n; i++)
                array.add(0);


            while(order.size()>=2)
            {
                Integer proposer= order.getFirst();
                if(linked_value.get(proposer).isEmpty()==true)
                {
                    for(int k=1; k<=n; k++)
                    {
                        linked_value.get(k).remove(proposer);
                    }
                    order.remove(proposer);
                    continue;
                }

                Integer receiver= linked_value.get(proposer).getFirst();
                array.set(proposer, value.get(proposer).get(receiver));
                array.set(receiver, value.get(receiver).get(proposer));
                for(int k=1; k<=n; k++)
                {
                    linked_value.get(k).remove(receiver);
                    linked_value.get(k).remove(proposer);
                }
                order.remove(proposer);
                order.remove(receiver);


            }

            Integer[] teammate= new Integer[n+1];
            for(int i=1; i<=n; i++)
                teammate[i]=0;

            for (int i = 1; i <= n; i++) {
                if (array.get(i) == 0) {
                    out.write(i + "\r\n");
                } else {
                    teammate[i]= value.get(i).indexOf(array.get(i));
                    out.write(i + " with " + value.get(i).indexOf(array.get(i)) + "\r\n");
                }
            }

            double current_sum=social_welfare(original, n, teammate);
            double current_max_min=max_min_payoff(original, n, teammate);
            double current_variance= variance(original, n, teammate);

            total_sum+=current_sum;
            total_max_min+=current_max_min;
            total_variance+= current_variance;

            social_welfare_array[iter]=current_sum;
            max_min_array[iter]=current_max_min;
            variance_array[iter]=current_variance;

            out.write("The social welfare is " + current_sum + "\r\n");
            out.write("The max min payoff is " + current_max_min + "\r\n");
            out.write("The variance is " + current_variance + "\r\n");

        }

        out.write("\r\n");
        out.write("The average social welfare is " + (double)total_sum/num_cases + "\r\n");
        out.write("The average max min payoff is " + (double)total_max_min/num_cases + "\r\n");
        total_variance=0;
        for (int k=1; k<=num_cases; k++)
            total_variance+= variance_array[k];

        out.write("The average variance is " + (double)total_variance/num_cases + "\r\n");

        for(int k=1; k<=num_cases; k++)
            out.write(social_welfare_array[k]+ " "+ max_min_array[k]+ " " + variance_array[k] + "\r\n");

        out.flush();
        out.close();
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
