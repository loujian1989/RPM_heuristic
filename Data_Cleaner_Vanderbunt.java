import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by loujian on 8/7/16.
 */
public class Data_Cleaner_Vanderbunt {

    public static void main(String[] args)throws Exception
    {
        Scanner cin=new Scanner(new File("vandebunt_t5_cleaned.txt"));
        File writename = new File("SN_vandebunt_t5_RSD.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));
        int n=30;

        Integer[][]orignal = new Integer[n+1][n+1];
        Integer[][]cleaned= new Integer[n+1][n+1];

        for(int i=1; i<=n; i++)
        {
            for(int j=1; j<=n; j++)
            {
                orignal[i][j]= cin.nextInt();
                cleaned[i][j]=0;
            }
        }

        for(int i=1; i<=n; i++)
        {
            for(int j=1; j<=n; j++)
            {
                if(i==j)
                    cleaned[i][j]=0;

                else if(orignal[i][j]==1)
                    cleaned[i][j]=4;
                else if(orignal[i][j]==2)
                    cleaned[i][j]=3;
                else if(orignal[i][j]==3)
                    cleaned[i][j]=2;
                else if(orignal[i][j]==4)
                    cleaned[i][j]=1;
                else if(orignal[i][j]==5)
                    cleaned[i][j]=-1;
                else
                    cleaned[i][j]=0;

            }

        }

        //Here is for AAM
/*
        for(int i=1; i<=n; i++)
        {
            for(int j=1; j<=n; j++)
            {
                if(cleaned[i][j]==0)
                {
                    cleaned[j][i]=0;
                }
                if(cleaned[i][j]==-1)
                {
                    cleaned[i][j]=0;
                    cleaned[j][i]=0;
                }
            }
        }
*/


        for(int i=1; i<=n; i++) {
            for (int j = 1; j <= n; j++) {
                out.write(cleaned[i][j]+ " ");
            }
            out.write("\r\n");
        }









        out.flush();
        out.close();

    }

}
