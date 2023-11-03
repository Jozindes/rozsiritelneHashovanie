/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obal;

import hashovanie.Hashovanie;
import hashovanie.PreplnovaciBlokOP;
import data.Nehnutelnost;
import java.util.ArrayList;
import struktura.Blok;
import struktura.IData;

/**
 *
 * @author Jozef
 */
public class Obalovac<T extends IData> {
    
    private Hashovanie instancia;
    
    public Obalovac() {
        this.instancia = new Hashovanie(3, "nehnutelnosti");
    }
    
    public void vytvoreniePociatocnychBlokov() {
        Nehnutelnost nova = new Nehnutelnost();
        this.instancia.vytvoreniePrazdnychBlokovNaZaciatku(nova);
    }
    
    public ArrayList<T> vsetkyDataBlokov() {
        return this.instancia.vsetkyData(new Nehnutelnost());
    }
    
    public void vlozData(Nehnutelnost paData) {
        this.instancia.vlozitData(paData);
    }
    
    public T hladajData(Nehnutelnost paData) {
        return (T) this.instancia.najdiZaznam(paData);
    }
    
    public ArrayList<Blok<T>> vsetkyBloky() {
        return this.instancia.vsetkyBloky(new Nehnutelnost());
    }
    
    public ArrayList<Blok<T>> vsetkyBlokyPreplnovaci() {
        return this.instancia.vsetkyBlokyPreplnovaci(new Nehnutelnost());
    }
    
    public int dajVelkostSuboruPreplnovacieho() {
        return this.instancia.dajVelkostSuboruPreplnovacieho();
    }
    
    public void zmazatData(Nehnutelnost paData) {
        this.instancia.zmaz(paData);
    }
    
    public ArrayList<PreplnovaciBlokOP> vsetkyPreplnovacieBloky() {
        return this.instancia.dajPreplnovacieBloky();
    }
    
    public void ulozStav() {
        this.instancia.zapisDoSuboru();
    }
    
    public void nacitajStav() {
        this.instancia.citajZoSuboru();
    }
    
    public void upravaDat(Nehnutelnost paData) {
        this.instancia.upravitData(paData);
    }
    
    public int dajNajvyssiuPreplnovaci() {
        return this.instancia.dajNajvyssiuPreplnovaci();
    }
    
    public void vlozPouzite(int id) {
        this.instancia.vlozPouzite(id);
    }
    
    public boolean skontrolujPouzite(int id) {
        return this.instancia.skontrolujPouzite(id);
    }
    
    public void odstranPouzite(int id) {
        this.instancia.odstranPouzite(id);
    }
}
