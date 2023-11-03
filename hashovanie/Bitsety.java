/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hashovanie;

import java.util.BitSet;

/**
 *
 * @author Jozef
 */
public class Bitsety {
    public Bitsety() {    
    }
    
    public boolean porovnaj() {
        BitSet prvy;
        prvy = BitSet.valueOf(new long[] {85});
        BitSet druhy;
        druhy = BitSet.valueOf(new long[] {100});
        int pocetRovnakych = 0;
        
        for (int i = 0; i < 20; i++) {                             
            if (prvy.get(i) == druhy.get(i)) {
                pocetRovnakych = pocetRovnakych + 1;
            }
        }
            
        if (pocetRovnakych == 20) {
            return true;
        } else {
            return false;
        }
    }
    
    public int cislo() {
        BitSet prvy;
        prvy = BitSet.valueOf(new long[] {20});
        
        int hodnota = 0;
        int start = 1;
        for (int i = 0; i < 2; i++) {
            if (prvy.get(i)) {
                hodnota = hodnota + start;
            }
            start = start * 2;
        }
        
        return hodnota;
    }
    
    public int cislo1() {
        BitSet prvy;
        prvy = BitSet.valueOf(new long[] {21});
        
        int hodnota = 0;
        int start = 1;
        for (int i = 0; i < 5; i++) {
            if (prvy.get(i)) {
                hodnota = hodnota + start;
            }
            start = start * 2;
        }
        
        return hodnota;
    }
}
