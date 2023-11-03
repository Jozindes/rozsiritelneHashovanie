/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import struktura.IData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jozef
 */
public class Nehnutelnost implements IData<Nehnutelnost> {
    private int identifikacneCislo;
    private int supisneCislo;
    private String popis;
    private double poziciaX;
    private double poziciaY;
    
    private final static int POPIS_DLZKA = 20;
    
    public Nehnutelnost() {
        this.identifikacneCislo = -1;
        this.supisneCislo = -1;
        this.popis = "11111222223333344444";
        this.poziciaX = -1.0;
        this.poziciaY = -1.0;
    }
    
    @Override
    public byte[] dajPoleBajtov() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(baos);
        
        try {
            stream.writeInt(identifikacneCislo);
            stream.writeInt(supisneCislo);
            stream.writeChars(popis);
            stream.writeDouble(poziciaX);
            stream.writeDouble(poziciaY);
            
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Chyba pocas konverzie do pola bajtov");
        }     
    }
    
    @Override
    public void vytvorZPolaBajtov(byte[] paPole) {
        ByteArrayInputStream bais = new ByteArrayInputStream(paPole);
        DataInputStream stream = new DataInputStream(bais);
        
        try {
            this.identifikacneCislo = stream.readInt();
            this.supisneCislo = stream.readInt();
            this.popis = "";
            for (int i = 0; i < POPIS_DLZKA; i++) {
                this.popis = this.popis + stream.readChar();
            }
            this.poziciaX = stream.readDouble();
            this.poziciaY = stream.readDouble();
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pocas konverzie z pola bajtov");
        }
    }
    
    @Override
    public int dajVelkost() {
        return Integer.BYTES + Integer.BYTES + POPIS_DLZKA * Character.BYTES + Double.BYTES + Double.BYTES;
    }
    
    @Override
    public BitSet dajHash() {
        BitSet bs;
        bs = BitSet.valueOf(new long[] {this.identifikacneCislo});
        return bs;      
    }
    
    @Override
    public boolean porovnaj(Nehnutelnost paData) {
        if (this.identifikacneCislo == paData.identifikacneCislo) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public Nehnutelnost vytvorTriedu() {
        return new Nehnutelnost();
    }
    
    public void nastavID(int paID) {
        this.identifikacneCislo = paID;
    }
    
    @Override
    public int dajID() {
        return this.identifikacneCislo;
    }
    
    public void nastavSupisneCislo(int paSupisne) {
        this.supisneCislo = paSupisne;
    }
    
    public int dajSupisneCislo() {
        return this.supisneCislo;
    }
    
    public void nastavPopis(String paPopis) {
        this.popis = paPopis;
    }
    
    public String dajPopis() {
        return this.popis;
    }
    
    public void nastavSuradnicuX(double paSurX) {
        this.poziciaX = paSurX;
    }
    
    public double dajSuradnicuX() {
        return this.poziciaX;
    }
    
    public void nastavSuradnicuY(double paSurY) {
        this.poziciaY = paSurY;
    }
    
    public double dajSuradnicuY() {
        return this.poziciaY;
    }
}
