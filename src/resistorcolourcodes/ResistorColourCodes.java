/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resistorcolourcodes;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 * @author Luke
 */
public class ResistorColourCodes {

    public ResistorColourCodes() {

    }

    public static String powerOfTen(int zeroCount) {

        switch (zeroCount / 3) {
            case 0:
                return "";
            case 1:
                return "k";
            case 2:
                return "M";
            default:
                //not quite sure when we'll need this
                return "G";
        }
    }

    public static Color[] getColours(int value) {
        BigDecimal b = new BigDecimal(value);

        b = b.round(new MathContext(2));

        System.out.println(b);

        System.out.println("scale = " + b.scale());

        System.out.println("invalue = " + b.intValue());

        int rounded = b.intValue();

        String stringed = String.valueOf(rounded);

        System.out.println("stringed = " + stringed);

        int len = stringed.length();
        // this is pointless, we round to 2sigfigs so we can just count number of zeros.  all zeroes will be at the end.
        // having just said that, this is more flexible and it turns out java doesn't have a built in "count all isntances" option...
        int zeroCount = 0;
        //count the zeros on the end
        for (int i = len - 1; i >= 0; i--) {
            if (stringed.charAt(i) == '0') {
                zeroCount++;
            } else {
                break;
            }
        }

        System.out.println("zerocount = " + zeroCount);

        System.out.println("/3 = " + (zeroCount / 3));

        String postfix = powerOfTen(zeroCount);

        //integer arthimetic.
        int threeZeroCount = zeroCount / 3;

        int numMinusZeros = rounded / (int) Math.pow(10, threeZeroCount * 3);

        String pretty = String.valueOf(numMinusZeros) + postfix;

        System.out.println("pretty = " + pretty);

        return new Color[]{};
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ResistorColourCodes.getColours(1234567);
    }

}
