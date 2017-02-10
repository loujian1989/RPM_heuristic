import java.io.File;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by loujian on 3/19/16.
 */
public class Approximate_Soulmates {
    public static void main(String[] args)throws Exception
    {
        Scanner cin = new Scanner(new File("preference_input"));
        File writename = new File("approximate_soulmates_n7_1000.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        int num_cases= 1000;

        int total_sum=0;
        int max_min_sum=0;

        for(int iter=0; iter<num_cases; iter++) {
            int n = 7; //n means the number of players in the game.

            //n=cin.nextInt(); //the number of players in the game

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
                for (int j = 1; j <= n - 1; j++) {
                    Integer tmp = cin.nextInt();
                    value.get(i).set(tmp, n - j + 1); //here we let it be the linear decreasing function
                    //value.get(i).set(tmp, 1<<(n - j)); //here we let it be the exponential decreasing function
                    linked_value.get(i).add(tmp);
                }
            }

            boolean[] flag_profile = new boolean[n + 1];
            for (int i = 1; i <= n; i++)
                flag_profile[i] = true;

            Preference_Profile PF = new Preference_Profile(n, linked_value, value, flag_profile);
            int[] final_payoff = PF.Iterative_Eliminating();

            int sum=0;
            int max_min_payoff= 999999999;

            for (int i = 1; i <= n; i++)
            {
                sum+=final_payoff[i];
                if (final_payoff[i] == 0)
                {
                    out.write(i + "\r\n");
                }
                else {
                    if(final_payoff[i]<max_min_payoff)
                        max_min_payoff=final_payoff[i];
                    out.write(i + " with " + value.get(i).indexOf(final_payoff[i]) + "\r\n");
                }
            }
            out.write("The social welfare is " + sum + "\r\n");
            out.write("The max min payoff is "+ max_min_payoff + "\r\n");
            total_sum+=sum;
            max_min_sum+=max_min_payoff;
        }

        double average= (double)total_sum/num_cases;
        double average_max_min_sum= (double)max_min_sum/num_cases;
        out.write("The average social welfare is " + average + "\r\n");
        out.write("The average max min payoff is " + average_max_min_sum + "\r\n");

        out.flush();
        out.close();

    }




}
