package main;

import static utils.ConversorUtils.*;

public class Teste {


    public static void main(String[] args) {
        int id = 99999;
        byte[] idArr = intToArrayByte(id, 2);
        int t = getShortFromBytes(idArr);
        System.out.println();

    }

}
