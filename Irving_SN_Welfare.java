import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by loujian on 8/5/16.
 */
public class Irving_SN_Welfare {


    public static void main(String[] args)throws Exception
    {
        long startTime = System.currentTimeMillis();

        Scanner cin=new Scanner(new File("n10.txt"));
        File writename = new File("tmp.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        int num_cases=1;

        int[] social_welfare_array= new int[num_cases+1];
        int[] max_min_array= new int[num_cases+1];

        int total_sum=0;
        int total_max_min=0;


        int num_unstable=0;

        for(int iter=1; iter<=num_cases; iter++) {

            int n = 10; //n means the number of players in the game.
            //int order_num; //means the actual number of players after preprocessing
            int depth = 5;// depth means search depth
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
                    if(j==i)
                        tmp.add(0);
                    else
                        tmp.add(-1);
                }
                value.add(tmp);
                linked_value.add(tmp1);
                linked_value_copy.add(tmp2);
            }

            for (int i = 1; i <= n; i++) {
                Integer number_nei= cin.nextInt();
                for (int j = 1; j <= number_nei; j++) {
                    int tmp = cin.nextInt();
                    //out.write(tmp+" ");
                    value.get(i).set(tmp, n - j + 1); //here we let it be the linear decreasing function
                    //value.get(i).set(tmp, 1<<(n - j)); //here we let it be the exponential decreasing function
                    linked_value.get(i).add(tmp);
                    linked_value_copy.get(i).add(tmp);
                }

                //out.write("\r\n");
            }

            //AAM_General_SN TF = new AAM_General_SN(n, depth, order, value, linked_value);
            //Team_Form_Pranav TF= new Team_Form_Pranav(n, depth, order, value, linked_value);
            //ArrayList<Integer> array = TF.ARM();

            Irving Irving_case= new Irving(n, value, linked_value);

            Integer[] teammate= new Integer[n+1];
            teammate= Irving_case.Irving_Team_Form(n, linked_value, value);
            if(teammate==null)
            {
                num_unstable++;
                out.write("No stable matching \r\n");
                out.write("\r\n");
                continue;
            }

            ArrayList<Integer> array = new ArrayList<>();
            for(int i=0; i<=n; i++)
                array.add(0);
            for(int i=1;i<=n; i++)
            {
                array.set(i, value.get(i).get(teammate[i]));
            }

            for (int i = 1; i <= n; i++) {
                if (array.get(i) == 0) {
                    out.write(i + "\r\n");
                } else {
                    out.write(i + " with " + value.get(i).indexOf(array.get(i)) + "\r\n");
                }
            }


            //Now we compute the utilitarian social welfare of all players


            //Now we compute the utilitarian social welfare of all players





            int social =social_welfare(array);
            int min= max_min_payoff(array);
            total_sum+= social;
            total_max_min+= min;

            social_welfare_array[iter]=social;
            max_min_array[iter]=min;

            out.write("The social welfare is " + social + "\r\n");
            out.write("The max min payoff is " + min + "\r\n");
            //System.out.println(social_welfare(array));





        }

        double average= (double)total_sum/num_cases;
        double max_min_average= (double)total_max_min/num_cases;
        out.write("The average social welfare is " + average + "\r\n");
        out.write("The average max min is " + max_min_average + "\r\n");

        for(int k=1; k<=num_cases; k++)
            out.write(social_welfare_array[k]+ " "+ max_min_array[k]+ "\r\n");

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        out.write("The total time needed is "+ totalTime + "\r\n");
        out.write("The number of unstable profiles is "+ num_unstable + "\r\n");
        out.flush();
        out.close();
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
