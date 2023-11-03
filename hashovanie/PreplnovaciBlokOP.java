/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hashovanie;

/**
 *
 * @author Jozef
 */
public class PreplnovaciBlokOP {
    
    private int adresaBloku;
    private int nasledujuciBlok = -1;
    private int pocetPrvkov = 0;
    
    PreplnovaciBlokOP(int paAdresaBloku) {
        this.adresaBloku = paAdresaBloku;
    }
    
    public void nastavNasledujuciBlok(int paNasledujuci) {
        this.nasledujuciBlok = paNasledujuci;
    }
    
    public int dajNasledujuciBlok() {
        return this.nasledujuciBlok;
    }
    
    public void nastavPocetPrvkov(int paPocet) {
        this.pocetPrvkov = paPocet;
    }
    
    public int dajPocetPrvkov() {
        return this.pocetPrvkov;
    }
    
    public void nastavAdresuBloku(int paAdresa) {
        this.adresaBloku = paAdresa;
    }
    
    public int dajAdresuBloku() {
        return this.adresaBloku;
    }
}
