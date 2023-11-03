/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hashovanie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import struktura.Blok;
import struktura.IData;

/**
 *
 * @author Jozef
 */
public class Hashovanie<T extends IData> {
    
    private int blokovyFaktor;
    private RandomAccessFile subor;
    private int najvyssiaPozicia;
    
    private int blokovyFaktorPreplnujuci;
    private RandomAccessFile preplnujuciSubor;
    private int najvyssiaAdresaVPreplnovacom;
    
    private int[] adresar;
    private int hlbkaAdresara;
    
    private int dlzkaHashu = 2;
    
    ArrayList<PreplnovaciBlokOP> preplnovacieBlokyOp;
    
    private byte[] zmena = null;
    private int zmenaPoz = -1;
    private int kde = -1;
    
    private ArrayList<Integer> pouzite = new ArrayList<Integer>();
     
    public Hashovanie(int paBlokovyFaktor, String paNazovSuboru) {
        this.blokovyFaktor = paBlokovyFaktor;
        try {
            this.subor = new RandomAccessFile(paNazovSuboru, "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.adresar = new int[2];
        this.hlbkaAdresara = 1;
        
        this.najvyssiaPozicia = 0;
        
        // preplnujuci subor
        this.blokovyFaktorPreplnujuci = 3;
        try {
            this.preplnujuciSubor = new RandomAccessFile("preplnujuci", "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.najvyssiaAdresaVPreplnovacom = 0;
        
        this.preplnovacieBlokyOp = new ArrayList<PreplnovaciBlokOP>();
    }
    
    public void vytvorPrazdnyBlok(T paData, int indexDoAdresara) {
        Blok<T> bl;
        bl = new Blok<T>(this.blokovyFaktor, paData.getClass());
        
        bl.nastavAdresuBloku(this.najvyssiaPozicia);
        byte[] blokBajtov = bl.dajPoleBajtov(); 
        

        try {
            this.subor.seek(this.najvyssiaPozicia);
            this.subor.write(blokBajtov);
        } catch (IOException ex) {
            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
        }     
        
        this.adresar[indexDoAdresara] = this.najvyssiaPozicia;
        
        this.najvyssiaPozicia = this.najvyssiaPozicia + bl.dajVelkost();
    }
    
    public void vytvoreniePrazdnychBlokovNaZaciatku(T paData) {
        System.out.println("Vytvoreny novy blok dat na indexe: ");
        System.out.println(this.najvyssiaPozicia);
        this.vytvorPrazdnyBlok(paData, 0);  
        System.out.println("Vytvoreny novy blok dat na indexe: ");
        System.out.println(this.najvyssiaPozicia);
        this.vytvorPrazdnyBlok(paData, 1);
    }
    
    public boolean vlozitData(T paData) {
              
        boolean vlozene = false;
        
        while (vlozene != true) {  
            
            Blok<T> bl;
            bl = new Blok<T>(this.blokovyFaktor, paData.getClass()); // vytvorenie bloku
                         
            int indexAdresar = this.vytvorCisloZBitSetu(paData.dajHash());  // index v adresari
            System.out.println("Index v adresari: " + indexAdresar);
            
            int poziciaVSubore = this.adresar[indexAdresar]; // pozicia v subore
            System.out.println("Pozicia v zakladnom subore: " + poziciaVSubore);
            
            byte[] blokBajtov = new byte[bl.dajVelkost()];
            try {
                this.subor.seek(poziciaVSubore);
                this.subor.read(blokBajtov);
            } catch (IOException ex) {
                Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            bl.vytvorZPolaBajtov(blokBajtov);
                    
            if (bl.pocetPrvkovVBloku() < bl.dajBlokovyFaktor()) { // pocet prvkov je mensi ako blokovy faktor
                bl.pridajZaznam(paData);
                System.out.println(this.ukazBitSet(paData.dajHash()));
                System.out.println("Uspesne vlozene do bloku na indexe: " + indexAdresar);
                System.out.println("Zoznam prvkov v bloku: " + indexAdresar);
                bl.vypisPrvkov();
                
                byte[] zase = bl.dajPoleBajtov();
        
                try {
                    this.subor.seek(poziciaVSubore);
                    this.subor.write(zase);
                } catch (IOException ex) {
                    Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                }
                return true;
            }
            
            if (bl.pocetPrvkovVBloku() == bl.dajBlokovyFaktor()) { // pocet prvkov je rovnaky ako blokovy faktor
                
                if (this.hlbkaAdresara == this.dlzkaHashu + 1) { // ak sa vycerpali vsetky bity z hashu
                    if (bl.dajDalsiBlok() == -1) { // ak povodny blok nema este pokracovanie v preplnujucom subore
                        
                        Blok<T> prepBlok;  
                        prepBlok = new Blok<T>(this.blokovyFaktorPreplnujuci, paData.getClass()); // vytvor novy blok                   
                        prepBlok.pridajZaznam(paData); // pridanie dat
                        
                        prepBlok.nastavAdresuBloku(this.najvyssiaAdresaVPreplnovacom);
                        
                        byte[] blokBajtovPrep = new byte[prepBlok.dajVelkost()];
                        blokBajtovPrep = prepBlok.dajPoleBajtov();
                        try {
                            this.preplnujuciSubor.seek(this.najvyssiaAdresaVPreplnovacom); 
                            this.preplnujuciSubor.write(blokBajtovPrep); // zapis dat do preplnovacieho suboru
                        } catch (IOException ex) {
                            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        bl.nastavDalsiBlok(this.najvyssiaAdresaVPreplnovacom);  // povodnemu bloku nastavim adresu, na ktorej sa nachadza dalsi blok v preplnovacom subore
                        
                        // ulozenie udajov o preplnovacom bloku do OP
                        
                        PreplnovaciBlokOP novy = new PreplnovaciBlokOP(this.najvyssiaAdresaVPreplnovacom);
                        novy.nastavPocetPrvkov(1);
                        this.preplnovacieBlokyOp.add(novy);
                        
                        this.najvyssiaAdresaVPreplnovacom = this.najvyssiaAdresaVPreplnovacom + prepBlok.dajVelkost();  // zmena najvyssej adresy v preplnovacom subore
                        
                        byte[] zase = new byte[bl.dajVelkost()];
                        zase = bl.dajPoleBajtov();  // povodny blok zmenim na pole bajtov
                        try {
                            this.subor.seek(poziciaVSubore);
                            this.subor.write(zase);  // povodny blok zapisem do suboru aj s odkazom na blok do preplnovacieho suboru
                        } catch (IOException ex) {
                            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        
                        
                        return true;
                    }
                    
                    if (bl.dajDalsiBlok() != -1) { // ak povodny blok uz ma pokracovanie v preplnovacom subore
                        
                        int poziciaVPreplnovacom = bl.dajDalsiBlok();
                        while(poziciaVPreplnovacom != -1) { // kym nevlozim
                            Blok<T> prepBlok;
                            prepBlok = new Blok<T>(this.blokovyFaktorPreplnujuci, paData.getClass()); // vytvorenie bloku
                            
                            byte[] blokBajtovPrep = new byte[prepBlok.dajVelkost()];
                            try {
                                this.preplnujuciSubor.seek(poziciaVPreplnovacom);
                                this.preplnujuciSubor.read(blokBajtovPrep);  // nacitanie pola bajtov z preplnujuceho suboru do bloku
                            } catch (IOException ex) {
                                Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            prepBlok.vytvorZPolaBajtov(blokBajtovPrep); // zmena pola bajtov na data bloku z preplnujuceho suboru
                                
                            if (prepBlok.pridajZaznam(paData) == true) { // ak sa data podari vlozit
                                blokBajtovPrep = prepBlok.dajPoleBajtov();
        
                                try {
                                    this.preplnujuciSubor.seek(poziciaVPreplnovacom);
                                    this.preplnujuciSubor.write(blokBajtovPrep); // zapis dat do preplnujuceho suboru
                                } catch (IOException ex) {
                                    Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                                for (int p = 0; p < this.preplnovacieBlokyOp.size(); p++) {
                                    if (this.preplnovacieBlokyOp.get(p).dajAdresuBloku() == poziciaVPreplnovacom) {
                                        this.preplnovacieBlokyOp.get(p).nastavPocetPrvkov(this.preplnovacieBlokyOp.get(p).dajPocetPrvkov() + 1);
                                        break;
                                    }
                                }
                                
                                return true;
                            }                
                            int pomoc = poziciaVPreplnovacom;
                            poziciaVPreplnovacom = prepBlok.dajDalsiBlok();
                            
                            if (poziciaVPreplnovacom == -1) { // ak som nenasiel miesto v preplnovacom, tak treba vytvorit novy blok v preplnovacom
                                Blok<T> prepBlokNovy;  
                                prepBlokNovy = new Blok<T>(this.blokovyFaktorPreplnujuci, paData.getClass()); // vytvor novy blok                   
                                prepBlokNovy.pridajZaznam(paData); // pridanie dat
                        
                                
                                prepBlok.nastavDalsiBlok(this.najvyssiaAdresaVPreplnovacom);  // povodnemu bloku nastavim adresu, na ktorej sa nachadza dalsi blok v preplnovacom subore
                                prepBlokNovy.nastavAdresuBloku(this.najvyssiaAdresaVPreplnovacom);
                                byte[] blokBajtovPrepNovy  = prepBlokNovy.dajPoleBajtov();
                                try {
                                    this.preplnujuciSubor.seek(this.najvyssiaAdresaVPreplnovacom); 
                                    this.preplnujuciSubor.write(blokBajtovPrepNovy); // zapis dat do preplnovacieho suboru
                                } catch (IOException ex) {
                                    Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                                for (int p = 0; p < this.preplnovacieBlokyOp.size(); p++) {
                                    if (this.preplnovacieBlokyOp.get(p).dajAdresuBloku() == pomoc) {
                                        this.preplnovacieBlokyOp.get(p).nastavNasledujuciBlok(this.najvyssiaAdresaVPreplnovacom);
                                        break;
                                    }
                                }
                                
                                PreplnovaciBlokOP novy1 = new PreplnovaciBlokOP(this.najvyssiaAdresaVPreplnovacom);
                                novy1.nastavPocetPrvkov(1);
                                this.preplnovacieBlokyOp.add(novy1);
                        
                                this.najvyssiaAdresaVPreplnovacom = this.najvyssiaAdresaVPreplnovacom + prepBlokNovy.dajVelkost();  // zmena najvyssej adresy v preplnovacom subore
                        
                                byte[] zase = new byte[prepBlok.dajVelkost()];
                                zase = prepBlok.dajPoleBajtov();  // povodny blok zmenim na pole bajtov
                                try {
                                    this.preplnujuciSubor.seek(pomoc);
                                    this.preplnujuciSubor.write(zase);  // povodny blok zapisem do suboru
                                } catch (IOException ex) {
                                    Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                                }                    
                                return true;
                            }
                        }  
                    }
                }
                               
                
                if (bl.dajHlbkuBloku() == this.hlbkaAdresara) { // ak hlbka bloku je rovanka ako hlbka adresara
                    System.out.println("Zvacsujem adresar.");
                    // zdvojnasob adresar
                    int[] dvojitePole = new int[this.adresar.length * 2];
                    for (int i = 0; i < this.adresar.length; i++) {
                        dvojitePole[i * 2] = this.adresar[i];
                        dvojitePole[i * 2 + 1] = this.adresar[i];
                    }
                    this.adresar = new int[dvojitePole.length];
                    System.arraycopy(dvojitePole, 0, this.adresar, 0, dvojitePole.length);
                    
                    this.hlbkaAdresara = this.hlbkaAdresara + 1;
                    System.out.println("Hlbka adresara je " + this.hlbkaAdresara);
                }
                
                
                // rozdelenie bloku
                System.out.println("Rozdelenie bloku");
                Blok<T> novyBlok;
                novyBlok = new Blok<T>(this.blokovyFaktor, paData.getClass());
                
                bl.hlbkaBlokuPLUS(); // zvacsi hlbku stareho bloku
                novyBlok.hblkaBlokuNASTAV(bl.dajHlbkuBloku()); // nastav rovanku hlbku noveho bloku

                BitSet bs1;
                BitSet bs2;
                bs1 = paData.dajHash();
                bs2 = paData.dajHash();
                                             
                bs1.clear(this.dlzkaHashu - bl.dajHlbkuBloku() + 1);
                bs2.set(this.dlzkaHashu - bl.dajHlbkuBloku() + 1);
                
                System.out.println("HASH po zmene: " + this.ukazBitSet(bs1));
                System.out.println("HASH po zmene: " + this.ukazBitSet(bs2));
                
                // vytiahnutie zaznamov zo stareho bloku
                ArrayList<T> vytiahnute = new ArrayList<T>();
                for (int i = 0; i < bl.dajZaznamy().size(); i++) {
                    T zaznam = bl.odstranZaznam(i);
                    vytiahnute.add(zaznam);
                }
                
                
                System.out.println("Hlbka bloku: " + bl.dajHlbkuBloku());
                // rozdelenie zaznamov medzi povodny a novy blok
                for (int i = 0; i < vytiahnute.size(); i++) {
                    if (this.porovnajBitsetyPriRozdelovani(bs1, vytiahnute.get(i).dajHash(), bl.dajHlbkuBloku())) {
                        bl.pridajZaznam(vytiahnute.get(i));
                        System.out.println("VYMENA: vlozil som do povodneho bloku");
                        System.out.println("" + this.ukazBitSet(bs1) + ":" + this.ukazBitSet(vytiahnute.get(i).dajHash()));
                    }
                    if (this.porovnajBitsetyPriRozdelovani(bs2, vytiahnute.get(i).dajHash(), bl.dajHlbkuBloku())) {
                        novyBlok.pridajZaznam(vytiahnute.get(i));
                        System.out.println("VYMENA: vlozil som do noveho bloku");
                        System.out.println("" + this.ukazBitSet(bs2) + ":" + this.ukazBitSet(vytiahnute.get(i).dajHash()));
                    }
                }
                
                for (int adr = 0; adr < this.adresar.length; adr++) {
                    BitSet bso = BitSet.valueOf(new long[] {adr});
                    if (this.porovnajBitsety(bs1, bso, bl.dajHlbkuBloku())) {
                        this.adresar[adr] = poziciaVSubore;
                    }
                    if (this.porovnajBitsety(bs2, bso, bl.dajHlbkuBloku())) {
                        this.adresar[adr] = this.najvyssiaPozicia;
                    }
                }
                
                novyBlok.nastavAdresuBloku(this.najvyssiaPozicia);
                
                bl.nastavSusednyBlok(this.najvyssiaPozicia);
                novyBlok.nastavSusednyBlok(bl.dajAdresuBloku());
                
                byte[] zase = bl.dajPoleBajtov();  // pole bajtov stareho bloku
                byte[] zase1 = novyBlok.dajPoleBajtov(); // pole bajtov noveho bloku
                
                try {
                    this.subor.seek(poziciaVSubore);
                    this.subor.write(zase);

                    this.subor.seek(this.najvyssiaPozicia);
                    this.subor.write(zase1);

                } catch (IOException ex) {
                    Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                this.najvyssiaPozicia = this.najvyssiaPozicia + bl.dajVelkost();
                
            }
        }    
        return true;
        
    }

    
    public int vytvorCisloZBitSetu(BitSet bitSet) {
        // dlka hashu 6
        int hodnota = 0;
        int start = 1;
        
        int odkial = this.dlzkaHashu - this.hlbkaAdresara + 1;
        int pokial = this.dlzkaHashu + 1;
        for (int i = odkial; i < pokial; i++) {
            if (bitSet.get(i)) {
                hodnota = hodnota + start;
            }
            start = start * 2;
        }        
        return hodnota;
    }
    
    public boolean porovnajBitsety(BitSet prvy, BitSet druhy, int kolko) {
            
        int rovnakych = 0;
        
        int odkial = this.dlzkaHashu - kolko + 1;
        int pokial = this.dlzkaHashu + 1;
        int posun = this.dlzkaHashu + 1 - this.hlbkaAdresara;
        
        for (int i = odkial; i < pokial; i++) {       
            if (prvy.get(i) == druhy.get(i - posun)) {
                rovnakych = rovnakych + 1;
            }
        }
        
        if (rovnakych == kolko) {
            return true;
        } else {
            return false;
        }     
    }
    
    public boolean porovnajBitsetyPriRozdelovani(BitSet prvy, BitSet druhy, int kolko) {
            
        int rovnakych = 0;
        
        int odkial = this.dlzkaHashu - kolko + 1;
        int pokial = this.dlzkaHashu + 1;
        
        for (int i = odkial; i < pokial; i++) {       
            if (prvy.get(i) == druhy.get(i)) {
                rovnakych = rovnakych + 1;
            }
        }
        
        if (rovnakych == kolko) {
            return true;
        } else {
            return false;
        }     
    }
    
    public String ukazBitSet(BitSet ktory) {
        String vysledok = "";
        for (int i = this.dlzkaHashu; i > -1; i--) {
            if (ktory.get(i) == true) {
                vysledok = vysledok + "1";
            } else {
                vysledok = vysledok + "0";
            }
        } 
        return vysledok;
    }
    
    public int dajVelkostSuboru() {
        try {
            return (int) this.subor.length();
        } catch (IOException ex) {
            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public int dajVelkostSuboruPreplnovacieho() {
        try {
            return (int) this.preplnujuciSubor.length();
        } catch (IOException ex) {
            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public ArrayList<T> vsetkyData(T paData) {
        ArrayList<T> vyber = new ArrayList<T>();
        
        Blok<T> blsk;
        blsk = new Blok<T>(this.blokovyFaktor, paData.getClass());
        
        for (int i = 0; i < this.dajVelkostSuboru(); i = i + blsk.dajVelkost()) {
            Blok<T> bl;
            bl = new Blok<T>(this.blokovyFaktor, paData.getClass()); // vytvorenie bloku
                        
            byte[] blokBajtov = new byte[bl.dajVelkost()];
            try {
                this.subor.seek(i);
                this.subor.read(blokBajtov);
            } catch (IOException ex) {
                Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            bl.vytvorZPolaBajtov(blokBajtov);
            ArrayList<T> pomoc = bl.vsetkyDataBloku();
            for (int y = 0; y < pomoc.size(); y++) {
                vyber.add(pomoc.get(y));
            }
        }
        return vyber;
    }
    
    public T najdiZaznam(T paData) {
        Blok<T> bl;
        BitSet hash = paData.dajHash();
        
        bl = new Blok<T>(this.blokovyFaktor, paData.getClass()); // vytvorenie bloku
        
        int indexAdresar = this.vytvorCisloZBitSetu(paData.dajHash()); // index v adresari
        
        int poziciaVSubore = this.adresar[indexAdresar]; // pozicia v subore
        
        byte[] blokBajtov = new byte[bl.dajVelkost()];
        try {
            this.subor.seek(poziciaVSubore);
            this.subor.read(blokBajtov); // citam pole bajtov
        } catch (IOException ex) {
            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        //System.out.println("Nacital som zo suboru: " + Arrays.toString(blokBajtov));
        bl.vytvorZPolaBajtov(blokBajtov); // zmena pola bajtov na prvky bloku
        
        for (T akt : bl.dajZaznamy()) {
            if (paData.porovnaj(akt) == true) {
                System.out.println("Nasiel som v zakladnom subore.");
                this.zmena = blokBajtov;
                this.zmenaPoz = poziciaVSubore;
                this.kde = 1;
                return akt;
            }
        }
        
        int nasledujuciBlok = bl.dajDalsiBlok();
        
        if (nasledujuciBlok == -1) {
            System.out.println("Nenasiel som hladany prvok.");
            return null;
        }
        
        while (nasledujuciBlok != -1) { // kym nenajdem dany prvok a neprejdem vsetky bloky v preplnovacom subore
            Blok<T> prepBlok;
            prepBlok = new Blok<T>(this.blokovyFaktorPreplnujuci, paData.getClass()); // vytvorenie bloku
                            
            byte[] blokBajtovPrep = new byte[prepBlok.dajVelkost()];
            try {
                this.preplnujuciSubor.seek(nasledujuciBlok);
                this.preplnujuciSubor.read(blokBajtovPrep);  // nacitanie pola bajtov z preplnujuceho suboru do bloku
            } catch (IOException ex) {
                Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
            }           
            prepBlok.vytvorZPolaBajtov(blokBajtovPrep); // zmena pola bajtov na data bloku z preplnujuceho suboru
            
            for (T akt : prepBlok.dajZaznamy()) {
                if (paData.porovnaj(akt) == true) {
                    System.out.println("Nasiel som v preplnujucom subore.");
                    this.zmena = blokBajtovPrep;
                    this.zmenaPoz = nasledujuciBlok;
                    this.kde = 2;
                    return akt;
                }
            }
            nasledujuciBlok = prepBlok.dajDalsiBlok();
        }
        
        System.out.println("Nenasiel som hladany zaznam.");
        return null;
    }
    
    public ArrayList<Blok<T>> vsetkyBloky(T paData) {
        ArrayList<Blok<T>> vyber = new ArrayList<Blok<T>>();
        
        Blok<T> blsk;
        blsk = new Blok<T>(this.blokovyFaktor, paData.getClass());
        
        for (int i = 0; i < this.dajVelkostSuboru(); i = i + blsk.dajVelkost()) {
            Blok<T> bl;
            bl = new Blok<T>(this.blokovyFaktor, paData.getClass()); // vytvorenie bloku
                        
            byte[] blokBajtov = new byte[bl.dajVelkost()];
            try {
                this.subor.seek(i);
                this.subor.read(blokBajtov);
            } catch (IOException ex) {
                Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            bl.vytvorZPolaBajtov(blokBajtov);
            ArrayList<T> pomoc = bl.vsetkyDataBloku();
            vyber.add(bl);
        }
        return vyber;
    }
    
    public ArrayList<Blok<T>> vsetkyBlokyPreplnovaci(T paData) {
        ArrayList<Blok<T>> vyber = new ArrayList<Blok<T>>();
        
        Blok<T> blsk;
        blsk = new Blok<T>(this.blokovyFaktorPreplnujuci, paData.getClass());
        
        for (int i = 0; i < this.dajVelkostSuboruPreplnovacieho(); i = i + blsk.dajVelkost()) {
            Blok<T> bl;
            bl = new Blok<T>(this.blokovyFaktorPreplnujuci, paData.getClass()); // vytvorenie bloku
                        
            byte[] blokBajtov = new byte[bl.dajVelkost()];
            try {
                this.preplnujuciSubor.seek(i);
                this.preplnujuciSubor.read(blokBajtov);
            } catch (IOException ex) {
                Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            bl.vytvorZPolaBajtov(blokBajtov);
            ArrayList<T> pomoc = bl.vsetkyDataBloku();
            vyber.add(bl);
        }
        return vyber;
    }
    
    public boolean zmaz(T paData) {
        // vyhladanie zaznamu v povodnom subore a zmazanie
        
        Blok<T> bl;
        BitSet hash = paData.dajHash();
        
        bl = new Blok<T>(this.blokovyFaktor, paData.getClass()); // vytvorenie bloku
        
        int indexAdresar = this.vytvorCisloZBitSetu(paData.dajHash()); // index v adresari
        
        int poziciaVSubore = this.adresar[indexAdresar]; // pozicia v subore
        
        byte[] blokBajtov = new byte[bl.dajVelkost()];
        try {
            this.subor.seek(poziciaVSubore);
            this.subor.read(blokBajtov); // citam pole bajtov
        } catch (IOException ex) {
            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
        }

        bl.vytvorZPolaBajtov(blokBajtov); // zmena pola bajtov na prvky bloku
        
        if (bl.zmazZaznam(paData) == true) { // ak sa zaznam podari najst a vymazat, vrat true
            
            // pozri ci ma blok pokracovanie v preplnovacom subore
            if (bl.dajDalsiBlok() != -1) {              
                int poziciaDalsiehoBloku = bl.dajDalsiBlok();                
                while (poziciaDalsiehoBloku != -1) {                   
                    Blok<T> blokPokracovanie;
                    blokPokracovanie = new Blok<T>(this.blokovyFaktorPreplnujuci, paData.getClass()); // vytvorenie bloku                       
                    byte[] blokBajtovPokracovanie = new byte[blokPokracovanie.dajVelkost()];
                    try {
                        this.preplnujuciSubor.seek(poziciaDalsiehoBloku);
                        this.preplnujuciSubor.read(blokBajtovPokracovanie);
                    } catch (IOException ex) {
                        Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    blokPokracovanie.vytvorZPolaBajtov(blokBajtovPokracovanie);
                    
                    T vytiahnuteData = null;
                    if (blokPokracovanie.pocetPrvkovVBloku() > 0) {
                        vytiahnuteData = blokPokracovanie.odstranZaznamPosledny();
                        bl.pridajZaznam(vytiahnuteData);
                        
                        blokBajtovPokracovanie = blokPokracovanie.dajPoleBajtov();
                    
                        try {
                            this.preplnujuciSubor.seek(poziciaDalsiehoBloku);
                            this.preplnujuciSubor.write(blokBajtovPokracovanie); // zapisem pole bajtov spat do suboru
                        } catch (IOException ex) {
                            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("TU - " + poziciaDalsiehoBloku);
                        blokBajtov = bl.dajPoleBajtov();
                        try {
                            this.subor.seek(poziciaVSubore);
                            this.subor.write(blokBajtov); // citam pole bajtov
                        } catch (IOException ex) {
                            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        for (int i = 0; i < this.preplnovacieBlokyOp.size(); i++) {
                            if (this.preplnovacieBlokyOp.get(i).dajAdresuBloku() == poziciaDalsiehoBloku) {
                                this.preplnovacieBlokyOp.get(i).nastavPocetPrvkov(this.preplnovacieBlokyOp.get(i).dajPocetPrvkov() - 1);
                                break;
                            }
                        }
                        
                        // pocitanie ci sa oplati premiestnit prvky - striasanie
                        
                        int poziciaPocitanie = poziciaDalsiehoBloku;
                        int pocetPrvkovVPreplnovacichBlokoch = 0;
                        int pocetPrejdenychBlokov = 0;
                        
                        while (poziciaPocitanie != -1) {
                            for (int o = 0; o < this.preplnovacieBlokyOp.size(); o++) {
                                if (this.preplnovacieBlokyOp.get(o).dajAdresuBloku() == poziciaPocitanie) {
                                    pocetPrvkovVPreplnovacichBlokoch = pocetPrvkovVPreplnovacichBlokoch + this.preplnovacieBlokyOp.get(o).dajPocetPrvkov();
                                    pocetPrejdenychBlokov = pocetPrejdenychBlokov + 1;
                                    poziciaPocitanie = this.preplnovacieBlokyOp.get(o).dajNasledujuciBlok();
                                    break;
                                }
                            }
                            System.out.println("*****");
                        }
                        
                        if ((pocetPrejdenychBlokov - 1) * this.blokovyFaktorPreplnujuci < pocetPrvkovVPreplnovacichBlokoch) {
                            return true;
                        }
                        
                        if ((pocetPrejdenychBlokov - 1) * this.blokovyFaktorPreplnujuci >= pocetPrvkovVPreplnovacichBlokoch) {
                            ArrayList<T> vytiahnuteDataPreplnovacie = new ArrayList<T>();
                            
                            // vytiahnem vsetky dane bloky
                            ArrayList<Blok> vytiahnuteBloky = new ArrayList<Blok>();
                            poziciaPocitanie = poziciaDalsiehoBloku;
                            
                            while (poziciaPocitanie != -1) { // ak ma susedny blok
                
                                Blok<T> vytiahnutyBlokk;
                                vytiahnutyBlokk = new Blok<T>(this.blokovyFaktorPreplnujuci, paData.getClass()); // vytvorenie bloku
                        
                                byte[] blokBajtovvv = new byte[vytiahnutyBlokk.dajVelkost()];
                                try {
                                    this.preplnujuciSubor.seek(poziciaPocitanie);
                                    this.preplnujuciSubor.read(blokBajtovvv);
                                } catch (IOException ex) {
                                    Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                                }
        
                                vytiahnutyBlokk.vytvorZPolaBajtov(blokBajtovvv);
                                
                                for (int a = vytiahnutyBlokk.pocetPrvkovVBloku() - 1; a > -1; a--) {
                                    vytiahnuteDataPreplnovacie.add(vytiahnutyBlokk.odstranZaznam(a));
                                }
                                
                                for (int i = 0; i < this.preplnovacieBlokyOp.size(); i++) {
                                    if (this.preplnovacieBlokyOp.get(i).dajAdresuBloku() == vytiahnutyBlokk.dajAdresuBloku()) {
                                        this.preplnovacieBlokyOp.get(i).nastavPocetPrvkov(0);
                                        break;
                                    }
                                    if (i == this.preplnovacieBlokyOp.size() - 2) {
                                        this.preplnovacieBlokyOp.get(i).nastavNasledujuciBlok(-1);
                                    }
                                }
                                
                                vytiahnuteBloky.add(vytiahnutyBlokk);
                                poziciaPocitanie = vytiahnutyBlokk.dajDalsiBlok();
                                System.out.println("++++++");
                            }
                            
                            int kamVkladam = 0;
                            int pocitam = 0;
                            while (vytiahnuteDataPreplnovacie.size() > 0) {                                     
                                vytiahnuteBloky.get(kamVkladam).pridajZaznam(vytiahnuteDataPreplnovacie.get(0));
                                pocitam = pocitam + 1;
                                
                                
                                for (int i = 0; i < this.preplnovacieBlokyOp.size(); i++) {
                                    if (this.preplnovacieBlokyOp.get(i).dajAdresuBloku() == vytiahnuteBloky.get(kamVkladam).dajAdresuBloku()) {
                                        this.preplnovacieBlokyOp.get(i).nastavPocetPrvkov(this.preplnovacieBlokyOp.get(i).dajPocetPrvkov() + 1);
                                        break;
                                    }
                                }
                                
                                if (pocitam % this.blokovyFaktorPreplnujuci == 0) {
                                    kamVkladam = kamVkladam + 1;
                                }
                                
                                vytiahnuteDataPreplnovacie.remove(0);
                                System.out.println("......");
                            }
                            
                          
                            
                            while (vytiahnuteBloky.size() > 0) {
                
                                if (this.preplnovacieBlokyOp.size() == 2) {
                                    vytiahnuteBloky.get(0).nastavDalsiBlok(-1);
                                }
                        
                                byte[] blokBajtovvv = vytiahnuteBloky.get(0).dajPoleBajtov();
                                try {
                                    this.preplnujuciSubor.seek(vytiahnuteBloky.get(0).dajAdresuBloku());
                                    this.preplnujuciSubor.write(blokBajtovvv);
                                } catch (IOException ex) {
                                    Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                                vytiahnuteBloky.remove(0);
                                System.out.println("----");
                            }
                            
                            // ak je posledny blok prazdny, tak ho vymaz a zmensi adresu
                            int posledny = this.preplnovacieBlokyOp.size() - 1;
                            if (this.preplnovacieBlokyOp.get(posledny).dajPocetPrvkov() == 0) {
                                this.najvyssiaAdresaVPreplnovacom = this.preplnovacieBlokyOp.get(posledny).dajAdresuBloku();
                                this.preplnovacieBlokyOp.remove(posledny);
                            }
                        }
                        
                        // koniec striasania
                        
                        return true;
                    }
                    poziciaDalsiehoBloku = blokPokracovanie.dajDalsiBlok();
                }
            }
            
            if (bl.dajSusednyBlok() != -1) { // ak ma susedny blok
                
                Blok<T> susednyBlok;
                susednyBlok = new Blok<T>(this.blokovyFaktor, paData.getClass()); // vytvorenie bloku
                        
                byte[] blokBajtovSusedny = new byte[susednyBlok.dajVelkost()];
                try {
                    this.subor.seek(bl.dajSusednyBlok());
                    this.subor.read(blokBajtovSusedny);
                } catch (IOException ex) {
                    Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                }
        
                susednyBlok.vytvorZPolaBajtov(blokBajtovSusedny);
                                             
                // ak maju spolu pocet prvkov, ktory sa rovna blokovemu faktoru
                if (bl.pocetPrvkovVBloku() + susednyBlok.pocetPrvkovVBloku() <= this.blokovyFaktor) {
                    
                    ArrayList<T> vytiahnute = new ArrayList<T>();
                    for (int i = 0; i < susednyBlok.dajZaznamy().size(); i++) {
                        T zaznam = susednyBlok.odstranZaznam(i);
                        vytiahnute.add(zaznam);
                    }
                
                    for (int i = 0; i < vytiahnute.size(); i++) {
                        bl.pridajZaznam(vytiahnute.get(i));
                    }
                    
                    susednyBlok.hblkaBlokuNASTAV(1);
                    susednyBlok.nastavAdresuBloku(-1);
                    susednyBlok.nastavDalsiBlok(-1);
                    susednyBlok.nastavSusednyBlok(-1);
                    
                    blokBajtovSusedny = susednyBlok.dajPoleBajtov();
                    
                    try {
                        this.subor.seek(bl.dajSusednyBlok());
                        this.subor.write(blokBajtovSusedny); // zapisem pole bajtov spat do suboru
                    } catch (IOException ex) {
                        Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    for (int i = 0; i < this.adresar.length; i++) {
                        if (this.adresar[i] == bl.dajSusednyBlok() || this.adresar[i] == poziciaVSubore) {
                            this.adresar[i] = poziciaVSubore;
                        }
                        System.out.print("" + this.adresar[i] + " ");
                    }
                    
                    bl.hlbkaBlokuMINUS();
                    bl.nastavSusednyBlok(-1);
                    
                    blokBajtov = bl.dajPoleBajtov();
                    try {
                        this.subor.seek(poziciaVSubore);
                        this.subor.write(blokBajtov);
                    } catch (IOException ex) {
                        Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    
                    // zmensenie adresara
                    if (this.kontrolaHlbkyBlokov(paData) == false) {
                        System.out.println("Zmensujem adresar");
                        int[] jednoPole = new int[this.adresar.length / 2];
                        for (int i = 0; i < jednoPole.length; i++) {
                            jednoPole[i] = this.adresar[i + i];
                        }
                        this.adresar = new int[jednoPole.length];
                        System.arraycopy(jednoPole, 0, this.adresar, 0, jednoPole.length);
                    
                        this.hlbkaAdresara = this.hlbkaAdresara - 1;
                    }
                    
                    return true;
                }
                
            }
            
            blokBajtov = bl.dajPoleBajtov();
            
            try {
                this.subor.seek(poziciaVSubore);
                this.subor.write(blokBajtov); // zapisem pole bajtov spat do suboru
            } catch (IOException ex) {
                Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return true;
        }
        
        // ak blok nema pokracovanie v preplnovacom subore
        if (bl.dajDalsiBlok() == -1) {
            System.out.println("Nemam pokracovanie v preplnovacom subore.");
            return false;
        }
        
        // ak ma blok pokracovanie, tak prejdi bloky v preplnovacom subore
        int dalsiBlok = bl.dajDalsiBlok();
        
        while (dalsiBlok != -1) {
            Blok<T> prepBlok;
            prepBlok = new Blok<T>(this.blokovyFaktorPreplnujuci, paData.getClass()); // vytvorenie bloku
                            
            byte[] blokBajtovPrep = new byte[prepBlok.dajVelkost()];
            try {
                this.preplnujuciSubor.seek(dalsiBlok);
                this.preplnujuciSubor.read(blokBajtovPrep);  // nacitanie pola bajtov z preplnujuceho suboru do bloku
            } catch (IOException ex) {
                Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
            }
            prepBlok.vytvorZPolaBajtov(blokBajtovPrep); // zmena pola bajtov na data bloku z preplnujuceho suboru
            
            if (prepBlok.zmazZaznam(paData)) {
                
                for (int i = 0; i < this.preplnovacieBlokyOp.size(); i++) {
                    if (this.preplnovacieBlokyOp.get(i).dajAdresuBloku() == dalsiBlok) {
                        this.preplnovacieBlokyOp.get(i).nastavPocetPrvkov(this.preplnovacieBlokyOp.get(i).dajPocetPrvkov() - 1);
                        break;
                    }
                }
                
                blokBajtovPrep = prepBlok.dajPoleBajtov();
                try {
                    this.preplnujuciSubor.seek(dalsiBlok);
                    this.preplnujuciSubor.write(blokBajtovPrep);  // zapisanie bajtov spat do suboru
                } catch (IOException ex) {
                    Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                // striasanie
                    int poziciaPocitanie = dalsiBlok;
                    int pocetPrvkovVPreplnovacichBlokoch = 0;
                    int pocetPrejdenychBlokov = 0;
                        
                        while (poziciaPocitanie != -1) {
                            for (int o = 0; o < this.preplnovacieBlokyOp.size(); o++) {
                                if (this.preplnovacieBlokyOp.get(o).dajAdresuBloku() == poziciaPocitanie) {
                                    pocetPrvkovVPreplnovacichBlokoch = pocetPrvkovVPreplnovacichBlokoch + this.preplnovacieBlokyOp.get(o).dajPocetPrvkov();
                                    pocetPrejdenychBlokov = pocetPrejdenychBlokov + 1;
                                    poziciaPocitanie = this.preplnovacieBlokyOp.get(o).dajNasledujuciBlok();
                                    break;
                                }
                            }
                            System.out.println("*****");
                        }
                        
                        if ((pocetPrejdenychBlokov - 1) * this.blokovyFaktorPreplnujuci < pocetPrvkovVPreplnovacichBlokoch) {
                            return true;
                        }
                        
                        if ((pocetPrejdenychBlokov - 1) * this.blokovyFaktorPreplnujuci >= pocetPrvkovVPreplnovacichBlokoch) {
                            ArrayList<T> vytiahnuteDataPreplnovacie = new ArrayList<T>();
                            
                            // vytiahnem vsetky dane bloky
                            ArrayList<Blok> vytiahnuteBloky = new ArrayList<Blok>();
                            poziciaPocitanie = dalsiBlok;
                            
                            while (poziciaPocitanie != -1) { // ak ma susedny blok
                
                                Blok<T> vytiahnutyBlokk;
                                vytiahnutyBlokk = new Blok<T>(this.blokovyFaktorPreplnujuci, paData.getClass()); // vytvorenie bloku
                        
                                byte[] blokBajtovvv = new byte[vytiahnutyBlokk.dajVelkost()];
                                try {
                                    this.preplnujuciSubor.seek(poziciaPocitanie);
                                    this.preplnujuciSubor.read(blokBajtovvv);
                                } catch (IOException ex) {
                                    Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                                }
        
                                vytiahnutyBlokk.vytvorZPolaBajtov(blokBajtovvv);
                                
                                for (int a = vytiahnutyBlokk.pocetPrvkovVBloku() - 1; a > -1; a--) {
                                    vytiahnuteDataPreplnovacie.add(vytiahnutyBlokk.odstranZaznam(a));
                                }
                                
                                for (int i = 0; i < this.preplnovacieBlokyOp.size(); i++) {
                                    if (this.preplnovacieBlokyOp.get(i).dajAdresuBloku() == vytiahnutyBlokk.dajAdresuBloku()) {
                                        this.preplnovacieBlokyOp.get(i).nastavPocetPrvkov(0);
                                        break;
                                    }
                                    if (i == this.preplnovacieBlokyOp.size() - 2) {
                                        this.preplnovacieBlokyOp.get(i).nastavNasledujuciBlok(-1);
                                    }
                                }
                                
                                vytiahnuteBloky.add(vytiahnutyBlokk);
                                poziciaPocitanie = vytiahnutyBlokk.dajDalsiBlok();
                                System.out.println("++++++");
                            }
                             // vkladanie prvkov spat do blokov
                            int kamVkladam = 0;
                            int pocitam = 0;
                            while (vytiahnuteDataPreplnovacie.size() > 0) {                                     
                                vytiahnuteBloky.get(kamVkladam).pridajZaznam(vytiahnuteDataPreplnovacie.get(0));
                                pocitam = pocitam + 1;
                                
                                
                                for (int i = 0; i < this.preplnovacieBlokyOp.size(); i++) {
                                    if (this.preplnovacieBlokyOp.get(i).dajAdresuBloku() == vytiahnuteBloky.get(kamVkladam).dajAdresuBloku()) {
                                        this.preplnovacieBlokyOp.get(i).nastavPocetPrvkov(this.preplnovacieBlokyOp.get(i).dajPocetPrvkov() + 1);
                                        break;
                                    }
                                }
                                
                                if (pocitam % this.blokovyFaktorPreplnujuci == 0) {
                                    kamVkladam = kamVkladam + 1;
                                }
                                
                                vytiahnuteDataPreplnovacie.remove(0);
                                System.out.println("......");
                            }
                            
                          
                            
                            while (vytiahnuteBloky.size() > 0) {
                
                                if (this.preplnovacieBlokyOp.size() == 2) {
                                    vytiahnuteBloky.get(0).nastavDalsiBlok(-1);
                                }
                        
                                byte[] blokBajtovvv = vytiahnuteBloky.get(0).dajPoleBajtov();
                                try {
                                    this.preplnujuciSubor.seek(vytiahnuteBloky.get(0).dajAdresuBloku());
                                    this.preplnujuciSubor.write(blokBajtovvv);
                                } catch (IOException ex) {
                                    Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                                vytiahnuteBloky.remove(0);
                                System.out.println("----");
                            }
                            
                            // ak je posledny blok prazdny, tak ho vymaz a zmensi adresu
                            int posledny = this.preplnovacieBlokyOp.size() - 1;
                            if (this.preplnovacieBlokyOp.get(posledny).dajPocetPrvkov() == 0) {
                                this.najvyssiaAdresaVPreplnovacom = this.preplnovacieBlokyOp.get(posledny).dajAdresuBloku();
                                this.preplnovacieBlokyOp.remove(posledny);
                            }
                        }
                // koniec striasania
                
                return true;
            }
            
            dalsiBlok = prepBlok.dajDalsiBlok();
        }        
        
        
        return false; 
    }
    
    public boolean kontrolaHlbkyBlokov(T paData) {
              
        Blok<T> blsk;
        blsk = new Blok<T>(this.blokovyFaktor, paData.getClass());
        
        for (int i = 0; i < this.dajVelkostSuboru(); i = i + blsk.dajVelkost()) {
            Blok<T> bl;
            bl = new Blok<T>(this.blokovyFaktor, paData.getClass()); // vytvorenie bloku
                        
            byte[] blokBajtov = new byte[bl.dajVelkost()];
            try {
                this.subor.seek(i);
                this.subor.read(blokBajtov);
            } catch (IOException ex) {
                Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            bl.vytvorZPolaBajtov(blokBajtov);
            if (bl.dajHlbkuBloku() >= this.hlbkaAdresara) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<PreplnovaciBlokOP> dajPreplnovacieBloky() {
        return this.preplnovacieBlokyOp;
    }
    
    public void zapisDoSuboru() {
        File file = new File ("pomocne.txt");
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter("pomocne.txt");
            printWriter.println(this.najvyssiaPozicia);
            printWriter.println(this.najvyssiaAdresaVPreplnovacom);
            
            printWriter.println(this.adresar.length);
            for (int i = 0; i < this.adresar.length; i++) {
                printWriter.println(this.adresar[i]);
            }
            printWriter.println(this.hlbkaAdresara);
            
            printWriter.println(this.preplnovacieBlokyOp.size());
            for (int i = 0; i < this.preplnovacieBlokyOp.size(); i++) {
                printWriter.println(this.preplnovacieBlokyOp.get(i).dajAdresuBloku());
                printWriter.println(this.preplnovacieBlokyOp.get(i).dajNasledujuciBlok());
                printWriter.println(this.preplnovacieBlokyOp.get(i).dajPocetPrvkov());
            }
            
            printWriter.println(this.pouzite.size());
            for (int i = 0; i < this.pouzite.size(); i++) {
                printWriter.println(this.pouzite.get(i));
            }
            printWriter.close ();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void citajZoSuboru() {
        FileInputStream fstream;
        try {
            fstream = new FileInputStream("pomocne.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            
            String strLine;
            
            strLine = br.readLine();
            this.najvyssiaPozicia = Integer.parseInt(strLine);
            
            strLine = br.readLine();
            this.najvyssiaAdresaVPreplnovacom = Integer.parseInt(strLine);
            
            strLine = br.readLine();
            int dlzka = Integer.parseInt(strLine);
            this.adresar = new int[dlzka];
            
            for (int i = 0; i < dlzka; i++) {
                strLine = br.readLine();
                int cislo = Integer.parseInt(strLine);
                this.adresar[i] = cislo;
            }
            
            strLine = br.readLine();
            int hlbka = Integer.parseInt(strLine);
            this.hlbkaAdresara = hlbka;
            
            strLine = br.readLine();
            int kolko = Integer.parseInt(strLine);
            
            for (int i = 0; i < kolko; i++) {
                strLine = br.readLine();
                int adresa = Integer.parseInt(strLine);
                PreplnovaciBlokOP novy = new PreplnovaciBlokOP(adresa);
                
                strLine = br.readLine();
                int nasled = Integer.parseInt(strLine);
                novy.nastavNasledujuciBlok(nasled);
                
                strLine = br.readLine();
                int pocet = Integer.parseInt(strLine);
                novy.nastavPocetPrvkov(pocet);
                
                this.preplnovacieBlokyOp.add(novy);
            }
            
            strLine = br.readLine();
            int tolko = Integer.parseInt(strLine); 
            
            for (int i = 0; i < tolko; i++) {
                strLine = br.readLine();
                int adresa = Integer.parseInt(strLine);                
                this.pouzite.add(adresa);
            }
            
            fstream.close();
        } catch (IOException ex) {
            Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);           
        }
    }
    
    public void upravitData(T paData) {
        if (this.kde == 1) {
            Blok<T> blok;
            blok = new Blok<T>(this.blokovyFaktor, paData.getClass()); // vytvorenie bloku
                                      
            blok.vytvorZPolaBajtov(this.zmena);
            
            for (int i = 0; i < blok.dajZaznamy().size(); i++) {
                if (blok.dajZaznamy().get(i).porovnaj(paData)) {
                    blok.uprava(paData);
                    break;
                }
            }
            
            byte[] blokBajtovvv = blok.dajPoleBajtov();
            try {
                this.subor.seek(this.zmenaPoz);
                this.subor.write(blokBajtovvv);
            } catch (IOException ex) {
                Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (this.kde == 2) {
            Blok<T> prepBlok;
            prepBlok = new Blok<T>(this.blokovyFaktorPreplnujuci, paData.getClass()); // vytvorenie bloku
                                      
            prepBlok.vytvorZPolaBajtov(this.zmena);
            
            for (int i = 0; i < prepBlok.dajZaznamy().size(); i++) {
                if (prepBlok.dajZaznamy().get(i).porovnaj(paData)) {
                    prepBlok.uprava(paData);
                    break;
                }
            }
            
            byte[] blokBajtovvv = prepBlok.dajPoleBajtov();
            try {
                this.preplnujuciSubor.seek(this.zmenaPoz);
                this.preplnujuciSubor.write(blokBajtovvv);
            } catch (IOException ex) {
                Logger.getLogger(Hashovanie.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public int dajNajvyssiuPreplnovaci() {
        return this.najvyssiaAdresaVPreplnovacom;
    }
    
    public void vlozPouzite(int id) {
        this.pouzite.add(Integer.valueOf(id));
    }
    
    public boolean skontrolujPouzite(int id) {
        for (int i = 0; i < this.pouzite.size(); i++) {
            if (this.pouzite.get(i) == id) {
                return true;
            }
        }
        return false;
    }
    
    public void odstranPouzite(int id) {
        for (int i = 0; i < this.pouzite.size(); i++) {
            if (this.pouzite.get(i) == id) {
                this.pouzite.remove(i);
            }
        }
    }
}
