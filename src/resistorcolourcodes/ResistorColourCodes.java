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

    public static PrettyValue getPretty(int value) {

        //TODO check value >=1
        BigDecimal b = new BigDecimal(value);

        b = b.round(new MathContext(2));

//        System.out.println(b);
//        System.out.println("scale = " + b.scale());
//
//        System.out.println("invalue = " + b.intValue());
        int rounded = b.intValue();

        String stringed = String.valueOf(rounded);

//        System.out.println("stringed = " + stringed);
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
//
//        System.out.println("zerocount = " + zeroCount);
//
//        System.out.println("/3 = " + (zeroCount / 3));

        String postfix = powerOfTen(len - 1);

//        //integer arthimetic.
//        int threeCharCount = (len - 1) / 3;
//        int threeZeroCount
//        System.out.println("/3 = " + threeCharCount);
//        int numMinusZeros = rounded / (int) Math.pow(10, threeZeroCount * 3);
//
//        String pretty = String.valueOf(numMinusZeros) + postfix;
        String pretty = "";

//        pretty += stringed.charAt(0);
        //how many zeros were lopped off with the postfix?
//        int zerosDealtWith = zeroCount < 3 ? 0 : ((zeroCount % 3) + 1) * 3;
         int zerosDealtWith = zeroCount < 3 ? 0 : (zeroCount / 3) * 3;

        int charsLeftToDealWith = len - zerosDealtWith;

        if (charsLeftToDealWith < 4) {
            pretty += stringed.substring(0, charsLeftToDealWith) + postfix;
        } else {
            pretty += stringed.charAt(0) + postfix + stringed.charAt(1);
        }

//        switch (charsLeftToDealWith) {
//            case 0:
//                //oops?
//                break;
//            case 1:
//                break;
//            case 2:
//                //firstdigit + decimalpoint + postfix
//                if (stringed.charAt(1) != '0') {
//                    pretty += "." + stringed.charAt(1);
//                } else {
//                    pretty += "0";
//                }
//                break;
//            case 2:
//                pretty += "00";
//                break;
//            case 3:
//                pretty += "000";
//                break;
//        }
//        for (int i = 0; i < len - threeCharCount * 3; i++) {
//            if (i == 0) {
//                if (stringed.charAt(1) != '0') {
//                    //if the second digit isn't zero, we'll need to put a decimal place in
//                    pretty += "." + stringed.charAt(1);
//                    break;
//                } else {
//                    continue;
//                }
//            } else {
//                pretty += "0";
//            }
//        }
//        if (len > 1) {
//
//        }
//        pretty += postfix;
//        System.out.println("pretty = " + pretty);
        return new PrettyValue(value, pretty, new Color[]{});
    }

    public static final int[] e12 = {10, 12, 15, 18, 22, 27, 33, 39, 47, 56, 68, 82,
        100, 120, 150, 180, 220, 270, 330, 390, 470, 560, 680, 820,
        1000, 1200, 1500, 1800, 2200, 2700, 3300, 3900, 4700, 5600, 6800, 8200,
        10000, 12000, 15000, 18000, 22000, 27000, 33000, 39000, 47000, 56000, 68000, 82000,
        100000, 120000, 150000, 180000, 220000, 270000, 330000, 390000, 470000, 560000, 680000, 820000,
        1000000};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        ResistorColourCodes.getPretty(103456);
        ResistorColourCodes.getPretty(1000);
        for (int i = 0; i < e12.length; i++) {
            System.out.println("e12: " + e12[i] + " =>" + ResistorColourCodes.getPretty(e12[i]).prettyText);
        }

    }

}

class PrettyValue {

    public PrettyValue(int realValue, String prettyText, Color[] colours) {
        this.realValue = realValue;
        this.prettyText = prettyText;
        this.colours = colours;
    }

    public int realValue;
    public String prettyText;
    public Color[] colours;
}
