/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struktura;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jozef
 */
public class Blok<T extends IData> implements IZaznam {
    
    private final int blokovyFaktor;
    private int validnyPocet;
    private int hlbkaBloku;
    private final ArrayList<T> zaznamy;
    private final Class<T> typTriedy;
    private int dalsiBlok;
    private int adresaBloku;
    private int susednyBlok;
    
    public Blok(int paBlokovyFaktor, Class paTypTriedy) {
        this.blokovyFaktor = paBlokovyFaktor;
        this.typTriedy = paTypTriedy;
        
        this.zaznamy = new ArrayList<T>(paBlokovyFaktor);
        for (int i = 0; i < this.blokovyFaktor; i++) {
            try {
                this.zaznamy.add((T) typTriedy.newInstance().vytvorTriedu());
            } catch (InstantiationException ex) {
                Logger.getLogger(Blok.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Blok.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        this.hlbkaBloku = 1;
        this.validnyPocet = 0;
                
        this.dalsiBlok = -1;
        this.adresaBloku = -1;
        this.susednyBlok = -1;
    }
    
    @Override
    public byte[] dajPoleBajtov() {
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //DataOutputStream stream = new DataOutputStream(baos);
        
        byte[] pole = {};
        
        for (T akt : this.zaznamy) {
            byte[] vyt = akt.dajPoleBajtov();
            byte[] vysle = new byte[pole.length + vyt.length];
            
            System.arraycopy(pole, 0, vysle, 0, pole.length);
            System.arraycopy(vyt, 0, vysle, pole.length, vyt.length);
            
            pole = new byte[vysle.length];
            System.arraycopy(vysle, 0, pole, 0, vysle.length);
        }
        
        byte[] doplnujucePremenne = new byte[this.dajDoplnujucePremenne().length];
        System.arraycopy(this.dajDoplnujucePremenne(), 0, doplnujucePremenne, 0, doplnujucePremenne.length);
        
        byte[] konecnePole = new byte[pole.length + doplnujucePremenne.length];
        System.arraycopy(pole, 0, konecnePole, 0, pole.length);
        System.arraycopy(doplnujucePremenne, 0, konecnePole, pole.length, doplnujucePremenne.length);
        
        //System.out.println("Zapisal som do suboru: " + konecnePole.length + " bajtov");
        
        return konecnePole;
    }
    
    @Override
    public void vytvorZPolaBajtov(byte[] paPoleBajtov) {
        //ByteArrayInputStream stream = new ByteArrayInputStream(paPoleBajtov);
        //DataInputStream dis = new DataInputStream(stream);
        
        //System.out.println("Nacital som: " + paPoleBajtov.length + " bajtov");
        for (int i = 0; i < this.blokovyFaktor; i++) {
            //byte[] pole = Arrays.copyOfRange(paPoleBajtov, i * this.zaznamy.get(i).dajVelkost(), (i + 1) * this.zaznamy.get(i).dajVelkost());
            byte[] pole = new byte[this.zaznamy.get(i).dajVelkost()];
            System.arraycopy(paPoleBajtov, i * this.zaznamy.get(i).dajVelkost(), pole, 0, this.zaznamy.get(i).dajVelkost());
            this.zaznamy.get(i).vytvorZPolaBajtov(pole);
        }
        
        byte[] doplnujucePremenne = new byte[this.dajDoplnujucePremenne().length];
        System.arraycopy(paPoleBajtov, this.blokovyFaktor * this.zaznamy.get(0).dajVelkost(), doplnujucePremenne, 0, doplnujucePremenne.length);
        
        this.vytvorZDoplnujucichPremennych(doplnujucePremenne);
    }
    
    @Override
    public int dajVelkost() {
        try {
            return this.typTriedy.newInstance().dajVelkost() * this.blokovyFaktor + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(Blok.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public ArrayList<T> dajZaznamy() {
        return this.zaznamy;
    }
    
    public boolean pridajZaznam(T paData) {
        if (this.validnyPocet < this.blokovyFaktor){
            this.zaznamy.remove(this.validnyPocet);
            this.zaznamy.add(this.validnyPocet, paData);
            this.validnyPocet = this.validnyPocet + 1;
            return true;
        }
        return false;
    }
    
    public ArrayList<T> vsetkyDataBloku() {
        ArrayList<T> vyber = new ArrayList<T>();
        for (int i = 0; i < this.validnyPocet; i++) {
           vyber.add(this.zaznamy.get(i));
        }
        return vyber;
    }
    
    public void vypisPrvkov() {
        System.out.println("*************************** BLOK **************************");    
        for (int i = 0; i < this.zaznamy.size(); i++) {
            System.out.println("ID: " + this.zaznamy.get(i).dajID());
        }
        System.out.println("*************************** KONIEC BLOKU **************************"); 
    }
    
    public int pocetPrvkovVBloku() {
        return this.validnyPocet;
    }
    
    public int dajBlokovyFaktor() {
        return this.blokovyFaktor;
    }
    
    public void hlbkaBlokuPLUS() {
        this.hlbkaBloku = this.hlbkaBloku + 1;
    }
    
    public void hlbkaBlokuMINUS() {
        this.hlbkaBloku = this.hlbkaBloku - 1;
    }
    
    public int dajHlbkuBloku() {
        return this.hlbkaBloku;
    }
    
    public void hblkaBlokuNASTAV(int paHlbka) {
        this.hlbkaBloku = paHlbka;
    }
    
    public byte[] dajDoplnujucePremenne() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(baos);
        
        try {
            stream.writeInt(this.hlbkaBloku);
            stream.writeInt(this.validnyPocet);
            stream.writeInt(this.dalsiBlok);
            stream.writeInt(this.adresaBloku);
            stream.writeInt(this.susednyBlok);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Chyba pocas konverzie do pola bajtov");
        }     
    }
    
    public void vytvorZDoplnujucichPremennych(byte[] paPole) {
        ByteArrayInputStream bais = new ByteArrayInputStream(paPole);
        DataInputStream stream = new DataInputStream(bais);
        
        try {
            this.hlbkaBloku = stream.readInt();
            this.validnyPocet = stream.readInt();
            this.dalsiBlok = stream.readInt();
            this.adresaBloku = stream.readInt();
            this.susednyBlok = stream.readInt();
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pocas konverzie z pola bajtov");
        }
    }
    
    public T odstranZaznam(int paIndexZaznamu) {
        T zaznam = this.zaznamy.remove(paIndexZaznamu);
        this.validnyPocet = this.validnyPocet - 1;
        try {
            this.zaznamy.add(paIndexZaznamu, (T) zaznam.getClass().newInstance().vytvorTriedu());
        } catch (InstantiationException ex) {
            Logger.getLogger(Blok.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Blok.class.getName()).log(Level.SEVERE, null, ex);
        }
        return zaznam;
    }
    
    public boolean zmazZaznam(T paData) {
        for (int i = 0; i < this.zaznamy.size(); i++) {
            if (this.zaznamy.get(i).porovnaj(paData) == true) {
                T zaznam = this.zaznamy.remove(i);  
                try {
                    this.zaznamy.add((T) zaznam.getClass().newInstance().vytvorTriedu());
                } catch (InstantiationException ex) {
                    Logger.getLogger(Blok.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Blok.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.validnyPocet = this.validnyPocet - 1;
                return true;
            }
        }       
        return false;
    }
    
    public int dajDalsiBlok() {
        return this.dalsiBlok;
    }
    
    public void nastavDalsiBlok(int paIndex) {
        this.dalsiBlok = paIndex;
    }
    
    public void nastavAdresuBloku(int paAdresa) {
        this.adresaBloku = paAdresa;
    }
    
    public int dajAdresuBloku() {
        return this.adresaBloku;
    }
    
    public int dajSusednyBlok() {
        return this.susednyBlok;
    }
    
    public void nastavSusednyBlok(int paSusedny) {
        this.susednyBlok = paSusedny;
    }
    
    public T odstranZaznamPosledny() {
        return this.odstranZaznam(this.validnyPocet - 1);
    }
    
    public void uprava(T data) {
        for (int i = 0; i < this.zaznamy.size(); i++) {
            if (this.zaznamy.get(i).dajID() == data.dajID()) {
                this.zaznamy.remove(i);
                this.zaznamy.add(i, data);
                break;
            }
        }
    }
}
