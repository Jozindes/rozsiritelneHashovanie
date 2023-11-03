/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struktura;

import java.util.BitSet;

/**
 *
 * @author Jozef
 */
public interface IData<T> extends IZaznam<T> {
    public BitSet dajHash();
    public boolean porovnaj(T paData);
    public T vytvorTriedu();
    public int dajID(); // pouzivam na vypis
}
