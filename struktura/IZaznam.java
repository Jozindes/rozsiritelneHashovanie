/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struktura;

/**
 *
 * @author Jozef
 */
public interface IZaznam<T> {
    public byte[] dajPoleBajtov();
    public void vytvorZPolaBajtov(byte[] paPole);
    public int dajVelkost();
}
