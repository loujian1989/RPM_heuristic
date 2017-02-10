/**
 * Created by loujian on 3/22/16.
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;
public class Generate_Cases {

    public static void main(String[] args)throws Exception
    {

        File writename = new File("n14.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        int n=14;

        int num_case=100;
        int[][]array= new int[n][n-1];

        for(int iter=0; iter<num_case; iter++)
        {

            Random rd = new Random();

            for (int i = 0; i < n; i++) {
                boolean[] flag = new boolean[n - 1];
                for (int j = 0; j < n - 1; j++)
                    flag[j] = false;
                for (int j = 0; j < n - 1; j++) {
                    while (true) {
                        int tmp = rd.nextInt(n - 1);
                        if (flag[tmp] == false) {
                            array[i][j] = tmp;
                            flag[tmp] = true;
                            break;
                        }
                    }
                }

                for (int j = 0; j < n - 1; j++) {
                    array[i][j]++;
                }
                for (int j = 0; j < n - 1; j++) {
                    if (array[i][j] >= i + 1)
                        array[i][j]++;
                }
            }


            for (int i = 0; i < n; i++) {
                out.write(n-1 + " ");
                for (int j = 0; j < n - 1; j++) {
                    out.write(array[i][j] + " ");
                }
                out.write("\r\n");
            }

            out.write("\r\n");
        }

        out.flush();
        out.close();


    }


}
