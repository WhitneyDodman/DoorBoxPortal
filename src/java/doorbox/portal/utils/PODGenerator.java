/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doorbox.portal.utils;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author torinw
 */
public class PODGenerator {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        Random r = new Random(System.currentTimeMillis());
        for (int i = 10000001; i <= 10000100; i++) {
            int rand;
            String podCode;
            do {
                rand = r.nextInt(99999999);
                if (rand < 0) rand *= -1;
                podCode = String.valueOf(rand);
                for (int l = podCode.length(); l < 8; l++) {
                    podCode = "0" + podCode;
                }
            } while (rand < 10000000 || list.contains(podCode));
            System.out.println(podCode);
        }
    }
}
