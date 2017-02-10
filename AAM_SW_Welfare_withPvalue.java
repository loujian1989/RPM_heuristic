/**
 * Created by loujian on 9/11/16.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.lang.*;

import org.apache.commons.math3.distribution.*;


public class AAM_SW_Welfare_withPvalue {

    public static void main(String[] args)throws Exception
    {
        long startTime = System.currentTimeMillis();

        Scanner cin=new Scanner(new File("SN_newfrat.txt"));
        File writename = new File("AAM_SN_newfrat.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));
        int n = 17; //n means the number of players in the game.
        double benchmark_means_welfare= 0.6803921568627451; //we need to change it

        double benchmark_means_difference= 0.5833333333333334; //if we consider regular network, we just ignore it.

        int num_cases=15;


        double[] social_welfare_array= new double[num_cases+1];
        double[] max_min_array= new double[num_cases+1];
        double[] variance_array= new double[num_cases+1];
        double[] average_max_difference = new double[num_cases+1];
        double[] difference_team_array= new double[num_cases+1];

        double total_sum=0;
        double total_max_min=0;
        double total_max_difference=0;

        double total_correlation_value=0;
        double total_difference_in_team=0;

        int total_deviate=0;
        int total_deviate_profile=0;
        double total_variance=0;

        double[][]rank= new double[n+1][num_cases+1]; //Here is the rank of players in all test cases.
        double[][]utility= new double[n+1][num_cases+1]; //Here is the utility players in all test cases.

        for(int iter=1; iter<=num_cases; iter++) {


            //int order_num; //means the actual number of players after preprocessing
            int depth = n;// depth means search depth

            LinkedList<Integer> order = new LinkedList<Integer>(); //means the list of players in the order
            order.clear();

            //Scanner cin= new Scanner(System.in);
            //n=cin.nextInt(); //the number of players in the game
            //depth=cin.nextInt();//
            //for(int i=0; i<n; i++)
            //    order.add(cin.nextInt()); //input the order in the game.

            for (int i = 0; i < n; i++)
                order.add(i + 1); //input the order in the game.

            /*
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
            */

            for(int i=0; i<order.size(); i++)
                rank[order.get(i)][iter]=(double)(i+1)/n; //get the rank of each player we can get a matrix, rank[i][j] means the utility of player i's utility in the case iter



            ArrayList<ArrayList<Integer>> value = new ArrayList<ArrayList<Integer>>(); //simulate the preference profile of all players
            ArrayList<LinkedList<Integer>> linked_value = new ArrayList<LinkedList<Integer>>();
            ArrayList<LinkedList<Integer>> linked_value_copy = new ArrayList<LinkedList<Integer>>();

            double[][] normal_value = new double[n+1][n+1]; //here means the normalized value each player get
            for(int i=1; i<=n; i++)
                for(int j=1; j<=n; j++)
                    normal_value[i][j]=0;

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

                    normal_value[i][tmp]= 1.0*(number_nei-j+1)/number_nei; //here we normalize the utility of players

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
                teammate[i]=0; //here teammate[i]=0 means no teammmate


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
                            deviate[tmp_player]=1;
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


            for(int i=1; i<=n; i++)
            {
                utility[i][iter]= normal_value[i][teammate[i]];
            }

            //int social =social_welfare(array);
            //int min= max_min_payoff(array);
            //double current_variance = variance(array);

            double social= social_welfare(n, normal_value ,teammate);
            double min= max_min_payoff(n, normal_value, teammate);
            double current_variance= variance(n, normal_value, teammate);
            double current_max_difference= average_max_difference(n, normal_value, teammate);
            double current_difference_in_team= difference_in_team(n, normal_value, teammate);
            double current_correlation= correlation(n, rank, utility, iter);

            total_sum+= social;
            total_max_min+= min;
            total_variance+= current_variance;
            total_max_difference+= current_max_difference;
            total_difference_in_team+= current_difference_in_team;
            total_correlation_value+= current_correlation;

            social_welfare_array[iter]=social;
            max_min_array[iter]=min;
            variance_array[iter]=current_variance;
            average_max_difference[iter]=current_max_difference;
            difference_team_array[iter]= current_difference_in_team;

            int deviate_sum= deviate_num(deviate);
            total_deviate+=deviate_sum;

            if(deviate_sum>0)
                total_deviate_profile++;

            out.write("The social welfare is " + social + "\r\n");
            out.write("The max min payoff is " + min + "\r\n");
            out.write("The variance is "+ current_variance + "\r\n");
            out.write("The maximum difference is "+ current_max_difference + "\r\n");
            out.write("The difference in team is "+ current_difference_in_team + "\r\n");
            out.write("The correlation is "+ current_correlation+ "\r\n");
            out.write("The number of player deviate is " + deviate_sum + ". \r\nThe corresponding deviate vector is\r\n");
            for(int i=1; i<=n; i++)
                out.write(deviate[i]+" ");
            out.write("\r\n");
            out.write("\r\n");

        }

        double average= (double)total_sum/num_cases;
        double max_min_average= (double)total_max_min/num_cases;
        double variance_average= total_variance/num_cases;
        double average_difference= total_max_difference/num_cases;
        double average_difference_in_team= total_difference_in_team/num_cases;
        double average_correlation_value= total_correlation_value/num_cases;


        double means_welfare= means_in_cases(num_cases, social_welfare_array);
        double standard_deviation_welfare= variance_in_cases(num_cases, social_welfare_array);
        double lower_welfare= confidence_interval_low(num_cases, means_welfare, standard_deviation_welfare, 1.96);
        double upper_welfare= confidence_interval_upper(num_cases, means_welfare, standard_deviation_welfare, 1.96);

        double means_difference_team= means_in_cases(num_cases, difference_team_array);
        double standard_deviation_difference_team= variance_in_cases(num_cases, difference_team_array);
        double lower_difference_team= confidence_interval_low(num_cases, means_difference_team, standard_deviation_difference_team, 1.96);
        double upper_difference_team= confidence_interval_upper(num_cases, means_difference_team, standard_deviation_difference_team, 1.96);

        double p_value_welfare= P_value(num_cases, means_welfare, standard_deviation_welfare, benchmark_means_welfare);
        double p_value_difference_in_team= 1-P_value(num_cases, means_difference_team, standard_deviation_difference_team, benchmark_means_difference);

        out.write("The average social welfare is " + average + "\r\n");
        out.write("The average max min is " + max_min_average + "\r\n");
        out.write("The average variance is " + variance_average + "\r\n" );
        out.write("The average max difference is "+ average_difference + "\r\n");
        out.write("The average difference in team is "+ average_difference_in_team+ "\r\n");
        out.write("The average correlation value is "+ average_correlation_value + "\r\n");

        out.write("The total deviate is " + total_deviate + "\r\n");
        out.write("The total number of deviate profiles is " + total_deviate_profile + "\r\n");

        out.write("The confidence interval for social welfare is "+ lower_welfare + " " + upper_welfare + "\r\n");
        out.write("The confidence interval for difference in team is "+ lower_difference_team + " " + upper_difference_team + "\r\n");

        out.write("The P value for social welfare is " + p_value_welfare + "\r\n");
        out.write("The P value for difference in team is "+ p_value_difference_in_team + "\r\n");

        for(int k=1; k<=num_cases; k++)
            out.write(social_welfare_array[k]+ " "+ max_min_array[k]+ "\r\n");


        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        out.write("The total time needed is "+ totalTime);
        out.flush();
        out.close();
        System.out.println(totalTime);
    }

    static double social_welfare(int n, double[][]normalized, Integer[] teammate)
    {
        double sum=0;
        for(int i=1; i<=n;i++)
            sum+=normalized[i][teammate[i]];
        double average = sum/n;
        return average;
    }

    static double max_min_payoff(int n, double[][]normalized, Integer[] teammate)
    {
        double max_min_value=999999999;
        for(int i=1; i<=n;i++)
        {
            if(normalized[i][teammate[i]]<max_min_value)
            {
                max_min_value=normalized[i][teammate[i]];
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

    static double variance(int n, double[][]normalized, Integer[] teammate)
    {
        double average= social_welfare(n, normalized, teammate);

        double variance_value=0;
        for(int i=1; i<=n; i++)
        {
            variance_value+= (normalized[i][teammate[i]]-average)* (normalized[i][teammate[i]]-average);
        }

        variance_value= variance_value/n;
        return variance_value;

    }

    static double average_max_difference(int n, double[][]normalized, Integer[] teammate)
    {
        double[] difference = new double[n+1];
        for(int i=1; i<=n; i++)
            difference[i]=-99999.9;
        for(int i=1; i<=n; i++)
        {
            for(int j=1; j<=n; j++)
            {
                if(j==i)
                    continue;

                double current_difference=Math.abs(normalized[i][teammate[i]]- normalized[j][teammate[j]]);
                if(current_difference>difference[i])
                    difference[i]=current_difference;
            }
        }

        double sum_difference=0;
        for(int i=1; i<=n; i++)
            sum_difference+=difference[i];

        sum_difference/=n;
        return sum_difference;
    }

    static double difference_in_team(int n, double[][]normalized, Integer[] teammate)
    {
        double[] difference = new double[n+1];
        double max_difference= -999999;
        for(int i=1; i<=n; i++)
        {
            if(teammate[i]==0)
                difference[i]=0;
            else
            {
                difference[i]= Math.abs(normalized[i][teammate[i]]- normalized[teammate[i]][i]);
            }

            if(difference[i]>max_difference)
                max_difference=difference[i];
        }

        return max_difference;
    }

    static double correlation(int n, double[][] rank, double[][] utility, int iter)
    {
        double average_rank=0;
        double average_utility=0;
        double variance_rank=0;
        double variance_utility=0;

        for(int i=1;i<=n; i++)
        {
            average_rank+=rank[i][iter];
            average_utility+= utility[i][iter];
        }
        average_rank/=n;
        average_utility/=n;

        for(int i=1; i<=n; i++) {
            variance_rank += (rank[i][iter] - average_rank) * (rank[i][iter] - average_rank);
            variance_utility+= (utility[i][iter] - average_utility) * (utility[i][iter] - average_utility);
        }
        variance_rank/=n;
        variance_utility/=n;

        double stantard_variance_rank= Math.sqrt(variance_rank);
        double stantard_variance_utility= Math.sqrt(variance_utility);

        double covariance=0;
        for(int i=1;i<=n;i++)
        {
            covariance+= (rank[i][iter]- average_rank)*(utility[i][iter] - average_utility);
        }
        covariance/=n;

        double correlation_value= covariance/(stantard_variance_rank* stantard_variance_utility);

        return correlation_value;

    }

    static double means_in_cases (int number_cases, double[] array)
    {
        double means=0;
        for(int i=1;i<=number_cases; i++)
        {
            means+=array[i];
        }
        means/=number_cases;  //we firstly compute the means
        return means;
    }

    static double variance_in_cases(int number_cases, double[]array) //We consider the vairance of social welfare and max difference in team, for use for confidence interval
    { //We get the standard deviation of an array
        double means=means_in_cases(number_cases, array);

        //Then we compute the standard deviation
        double standard_deviation=0;
        for(int i=1; i<=number_cases; i++)
        {
            standard_deviation+= (array[i] - means)*  (array[i] - means);
        }
        standard_deviation/=number_cases;
        standard_deviation= Math.sqrt(standard_deviation);

        return standard_deviation;
    }

    static double confidence_interval_low (int number_cases, double means, double standard_deviation, double confidence_parameter)
    {
        double tmp= Math.sqrt(1.0* number_cases);
        double lower_bound= means - confidence_parameter* standard_deviation/tmp;
        return lower_bound;
    }

    static double confidence_interval_upper (int number_cases, double means, double standard_deviation, double confidence_parameter)
    {
        double tmp= Math.sqrt(1.0* number_cases);
        double upper_bound= means + confidence_parameter* standard_deviation/tmp;
        return upper_bound;
    }

    static double P_value(int number_cases, double means_AAM, double standard_deviation_AAM, double means_RSD)
    {
        double t_star=(means_AAM- means_RSD)/(standard_deviation_AAM/Math.sqrt(number_cases));

        TDistribution t = new TDistribution(number_cases-1);
        double p_value = 1.0 - t.cumulativeProbability(t_star);
        return p_value;
    }


}

