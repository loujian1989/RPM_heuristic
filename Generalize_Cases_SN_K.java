/**
 * Created by loujian on 5/19/16.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Generalize_Cases_SN_K {

    public static void main(String[] args)throws Exception
    {
        File writename = new File("SN_n80k6.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        int num_case=100;
        int n=80;
        int k=6;
        int[][]array= new int[n+1][n];

        int iter=0;
        while(iter<num_case)
        {
            Random rd = new Random();

            int[] order =new int[n];
            boolean[] flag = new boolean[n+1];
            for (int j = 0; j < n; j++)
                flag[j] = false;

            for(int i=0; i<n; i++)
            {
                while(true)
                {
                    int tmp=rd.nextInt(n);
                    if(flag[tmp]==false)
                    {
                        order[i]=tmp;
                        flag[tmp]=true;
                        break;
                    }
                }
                //out.write(order[i]+"\r\n");
            }


            int[][] first_k= new int[n+1][n];


            if(k%2==0) {
                for (int i = 0; i < n; i++) {
                    int current_player = order[i];
                    for (int j = 1; j <= k / 2; j++) {
                        first_k[current_player][j] = order[(i + j) % n];
                    }
                    for (int j = 1; j <= k / 2; j++) {
                        first_k[current_player][k / 2 + j] = order[(i - j + n) % n];
                    }
                }

            }



            /*
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= k; j++) {
                    out.write(first_k[i][j] + " ");
                }
                out.write("\r\n");
            }
            */

            //int[][] first_k= new int[n+1][k+1];



            /*

            ArrayList<ArrayList<Integer>> first_k= new ArrayList<ArrayList<Integer>>();
            ArrayList<Integer>tmp1= new ArrayList<Integer>();
            for(int i=0; i<n; i++)
            {
                ArrayList<Integer>tmp= new ArrayList<Integer>();
                first_k.add(tmp);
            }


            Degree_Sort[] circle = new Degree_Sort[n];

            for(int i=0; i<n; i++)
            {
                circle[i]= new Degree_Sort(order[i], k);

                //out.write(circle[i].getLabel()+ " "+ circle[i].getDegree());
            }

            Arrays.sort(circle);



            int length=n;
            while(circle[0].getDegree()!=0)
            {
                int first_node=circle[0].getLabel();
                int tmp_degree= circle[0].getDegree();
                for(int j=1; j<=tmp_degree; j++)
                {
                    int another_node= circle[j].getLabel();
                    first_k.get(first_node).add(another_node);
                    first_k.get(another_node).add(first_node);

                    circle[0].setDegree(0);
                    circle[j].setDegree(circle[j].getDegree()-1);
                }

                Arrays.sort(circle);
            }

            /*
            for(int i=0; i<n; i++) {
                for (int j = 0; j < k; j++)
                    out.write(first_k.get(i).get(j)+ " ");
                out.write("\r\n");
            }
            */

            /*
            for(int i=0; i<n; i++)
                for(int j=0; j<k; j++)
                {
                    first_k.get(i).set(j, first_k.get(i).get(j)+1);
                }


            */


            for(int i=0; i<n; i++)
                for(int j=1; j<=k; j++)
                    first_k[i][j]++;



            //move the numbers from frist_k to array
            /*
            for (int i = 1; i <= n; i++) {
                boolean[] flag_tmp = new boolean[n+1];
                boolean[] pos= new boolean[k+1];

                for(int j=1; j<=n; j++)
                    flag_tmp[j]=false;
                for(int j=1; j<=k; j++)
                    pos[j]=false;
                flag_tmp[i]=true;

                for(int j=1; j<=k; j++)
                {
                    while(true)
                    {
                        int tmp= rd.nextInt(k);
                        tmp++;
                        if(pos[tmp]==false)
                        {
                            //array[i][tmp]= first_k.get(i-1).get(j-1);
                            array[i][tmp]= first_k[i-1][j];
                            pos[tmp]=true;
                            //flag_tmp[first_k.get(i-1).get(j-1)]=true;
                            flag_tmp[first_k[i-1][j]]=true;
                            break;
                        }
                    }
                }

                for(int j=k+1; j<=n-1; j++)
                {
                    while(true)
                    {
                        int tmp=rd.nextInt(n);
                        tmp++;
                        if(flag_tmp[tmp]==false)
                        {
                            array[i][j]=tmp;
                            flag_tmp[tmp]=true;
                            break;
                        }
                    }
                }

            }

            */

            /*
            for (int i = 1; i <= n; i++) {
                boolean[] flag = new boolean[n];
                for (int j = 0; j < n - 1; j++)
                    flag[j] = false;
                for (int j = 0; j < n - 1; j++) {
                    while (true) {
                        int tmp = rd.nextInt(n-1);
                        tmp++;
                        if (flag[tmp] == false) {
                            array[i][j+1] = tmp;
                            flag[tmp] = true;
                            break;
                        }
                    }
                }

                for (int j = 1; j <= n - 1; j++) {
                    if (array[i][j] >= i )
                        array[i][j]++;
                }
            }
            */





            //now we test whether the preference is k-reciprocal



            /*

            boolean[][] flag= new boolean[n+1][n+1];
            for(int i=1; i<=n; i++)
                for(int j=1; j<=n; j++)
                    flag[i][j]=false;

            for(int i=1; i<=n; i++)
            {
                for(int j=1; j<=k; j++)
                {
                    flag[i][array[i][j]]=true;
                }
            }

            for(int i=1; i<=n-1; i++)
                for(int j=i+1; j<=n; j++)
                {
                    if((flag[i][j]== false && flag[j][i]== true)||(flag[i][j]== true && flag[j][i]== false))
                    {
                        counter=true;
                        i=n+1;
                        j=n+1;
                    }
                }
            */






            for (int i = 0; i < n; i++) {
                out.write(k+" ");
                for (int j = 1; j <= k; j++) {
                    out.write(first_k[i][j] + " ");
                }
                out.write("\r\n");
            }

            out.write("\r\n");


            iter++;

        }

        out.flush();
        out.close();
    }

}
