/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import hashovanie.PreplnovaciBlokOP;
import obal.Obalovac;
import data.Nehnutelnost;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import struktura.Blok;

/**
 *
 * @author Jozef
 */
public class Okno extends javax.swing.JFrame {

    private Obalovac obalka = new Obalovac();
    /**
     * Creates new form Okno
     */
    public Okno() {
        initComponents();
        //vyplnTabulku();
        vyplnHlavnuTabulku();
        vyplnPreplnovaciuTabulku();
    }
    
    /*
    public void vyplnTabulku() {       
        DefaultTableModel model = (DefaultTableModel) tabulka.getModel();
        
        int rows = model.getRowCount(); 
        for(int i = rows - 1; i >=0; i--) {
            model.removeRow(i); 
        }
        
        
        if (obalka.vsetkyDataBlokov() != null) {
            for (int i = 0; i < obalka.vsetkyDataBlokov().size(); i++) {
                Nehnutelnost nn = (Nehnutelnost) obalka.vsetkyDataBlokov().get(i);
                model.addRow(new Object[]{nn.dajID()});                  
            }
        }
        
        int poradove = 1;
        String column3Format = "%40.40s";
        String formatInfo = column3Format + " " + column3Format + " " + column3Format +  column3Format + column3Format;
        for (int i = 0; i < obalka.vsetkyBloky().size(); i++) {
            Blok nn = (Blok) obalka.vsetkyBloky().get(i);
            model.addRow(new Object[]{"Adresa bloku: " + nn.dajAdresuBloku() + " | Susedny blok: " + nn.dajSusednyBlok() + " | Hlbka bloku: " + nn.dajHlbkuBloku() + " | Preplnovaci: " + nn.dajDalsiBlok()});
            for (int y = 0; y < nn.pocetPrvkovVBloku(); y++) {
                Nehnutelnost aa = (Nehnutelnost) nn.dajZaznamy().get(y);
                model.addRow(new Object[]{formatInfo, " ID: " + aa.dajID(), aa.dajSupisneCislo(), aa.dajSuradnicuX()});
                poradove = poradove + 1;
            }                   
        }
        
        if (obalka.dajVelkostSuboruPreplnovacieho() > 0) {
            model.addRow(new Object[]{" *** PREPLNOVACI SUBOR ***"});
            for (int i = 0; i < obalka.vsetkyBlokyPreplnovaci().size(); i++) {
                Blok nn = (Blok) obalka.vsetkyBlokyPreplnovaci().get(i);
                model.addRow(new Object[]{"Adresa bloku: " + nn.dajAdresuBloku() + " | Dalsi blok: " + nn.dajDalsiBlok()});
                for (int y = 0; y < nn.pocetPrvkovVBloku(); y++) {
                    Nehnutelnost aa = (Nehnutelnost) nn.dajZaznamy().get(y);
                    model.addRow(new Object[]{"Poradove cislo: " + (poradove) + " ID: " + aa.dajID()}); 
                    poradove = poradove + 1;
                }                   
            }
        }
    }
    */
    
    public void vyplnHlavnuTabulku() {       
        DefaultTableModel model = (DefaultTableModel) hlavnaTabulka.getModel();
        
        int rows = model.getRowCount(); 
        for(int i = rows - 1; i >=0; i--) {
            model.removeRow(i); 
        }
               
        for (int i = 0; i < obalka.vsetkyBloky().size(); i++) {
            Blok nn = (Blok) obalka.vsetkyBloky().get(i);
            if (nn.dajAdresuBloku() != -1) {
                model.addRow(new Object[]{});
                model.addRow(new Object[]{"AB: " + nn.dajAdresuBloku(), "SB: " + nn.dajSusednyBlok(), "HB: " + nn.dajHlbkuBloku(), "PB: " + nn.dajDalsiBlok()});
                model.addRow(new Object[]{});
                for (int y = 0; y < nn.pocetPrvkovVBloku(); y++) {
                    Nehnutelnost aa = (Nehnutelnost) nn.dajZaznamy().get(y);
                    model.addRow(new Object[]{aa.dajID(), aa.dajSupisneCislo(), aa.dajSuradnicuX(), aa.dajSuradnicuY(), aa.dajPopis()});
                }
            }
        }
        
        if (obalka.dajVelkostSuboruPreplnovacieho() > 0) {
            model.addRow(new Object[]{});
            model.addRow(new Object[]{"*********", "*********", "*********", "*********", "*********"});
            model.addRow(new Object[]{});
            for (int i = 0; i < obalka.vsetkyBlokyPreplnovaci().size(); i++) {
                Blok nn = (Blok) obalka.vsetkyBlokyPreplnovaci().get(i);
                if (nn.dajAdresuBloku() < this.obalka.dajNajvyssiuPreplnovaci()) {
                    model.addRow(new Object[]{});
                    model.addRow(new Object[]{"AB: " + nn.dajAdresuBloku(), "DB: " + nn.dajDalsiBlok()});
                    model.addRow(new Object[]{});
                    for (int y = 0; y < nn.pocetPrvkovVBloku(); y++) {
                        Nehnutelnost aa = (Nehnutelnost) nn.dajZaznamy().get(y);
                        model.addRow(new Object[]{aa.dajID(), aa.dajSupisneCislo(), aa.dajSuradnicuX(), aa.dajSuradnicuY(), aa.dajPopis()}); 
                    }               
                }
            }
        }
    }
    
    public void vyplnPreplnovaciuTabulku() {
        DefaultTableModel model = (DefaultTableModel) preplnovacia.getModel();
        
        int rows = model.getRowCount(); 
        for(int i = rows - 1; i >=0; i--) {
            model.removeRow(i); 
        }
                      
        for (int i = 0; i < obalka.vsetkyPreplnovacieBloky().size(); i++) {
            PreplnovaciBlokOP ob = (PreplnovaciBlokOP) obalka.vsetkyPreplnovacieBloky().get(i);
            model.addRow(new Object[]{ob.dajAdresuBloku(), ob.dajPocetPrvkov(), ob.dajNasledujuciBlok()});                            
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        id = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        vysledok = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        preplnovacia = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        hlavnaTabulka = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        supisne = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pozY = new javax.swing.JTextField();
        pozX = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        popis = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        kolko = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Vytvoriť prázdne bloky");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("ID:");

        jButton2.setText("Pridať");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Vyhľadať");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        vysledok.setText("****");

        jButton4.setText("Generuj");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Zmazať");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        preplnovacia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Adresa", "Prvkov", "Ďalší"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(preplnovacia);
        if (preplnovacia.getColumnModel().getColumnCount() > 0) {
            preplnovacia.getColumnModel().getColumn(0).setResizable(false);
            preplnovacia.getColumnModel().getColumn(1).setResizable(false);
            preplnovacia.getColumnModel().getColumn(2).setResizable(false);
        }

        hlavnaTabulka.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Súpisné", "Súradnica X", "Súradnica Y", "Popis"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(hlavnaTabulka);

        jButton6.setText("Ulož stav");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Načítaj stav");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel2.setText("Súpisné:");

        jLabel3.setText("Poz X:");

        jLabel4.setText("Poz Y:");

        jLabel5.setText("Popis:");

        jButton8.setText("Upraviť");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7)
                .addGap(18, 18, 18)
                .addComponent(jButton6)
                .addGap(19, 19, 19))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                                .addComponent(kolko, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton4)
                                .addContainerGap())))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pozX, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(id, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(supisne, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(pozY, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButton2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jButton5)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton3))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(popis, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton8)))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(vysledok)
                        .addGap(178, 178, 178))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton4)
                    .addComponent(kolko, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vysledok)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2)
                            .addComponent(jButton5)
                            .addComponent(jButton3))
                        .addGap(20, 20, 20)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(supisne, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(popis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton8))
                    .addComponent(jLabel2))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(pozX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(pozY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton6))
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        int idecko = Integer.parseInt(this.id.getText());
        boolean pouzivane = this.obalka.skontrolujPouzite(idecko);
        
        if (pouzivane == true) {
            JOptionPane.showMessageDialog(null, "ID sa uz pouziva.");
        }
        
        if (pouzivane == false) {
            Nehnutelnost nova = new Nehnutelnost();
            nova.nastavID(Integer.parseInt(this.id.getText()));
            nova.nastavSupisneCislo(Integer.parseInt(this.supisne.getText()));
            nova.nastavSuradnicuX(Double.parseDouble(this.pozX.getText()));
            nova.nastavSuradnicuY(Double.parseDouble(this.pozY.getText()));
            String s = new String(this.popis.getText());
            if (s.length() < 20) {
                int doplnit = 20 - s.length();
                for (int i = 0; i < doplnit; i++) {
                    s = s + " ";
                }
            }
            nova.nastavPopis(s);
            this.obalka.vlozData(nova);
            //vyplnTabulku();
            vyplnHlavnuTabulku();
            vyplnPreplnovaciuTabulku();
            
            this.obalka.vlozPouzite(Integer.parseInt(this.id.getText()));
            
            this.id.setText("");
            this.supisne.setText("");
            this.pozX.setText("");
            this.pozY.setText("");
            this.popis.setText("");  
            
            
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        obalka.vytvoreniePociatocnychBlokov();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        Nehnutelnost nova = new Nehnutelnost();
        nova.nastavID(Integer.parseInt(this.id.getText()));
        if (this.obalka.hladajData(nova) == null) {
            this.vysledok.setText("false");
        } else {
            nova = (Nehnutelnost) this.obalka.hladajData(nova);
            this.id.setText("" + nova.dajID());
            this.supisne.setText("" + nova.dajSupisneCislo());
            this.pozX.setText("" + nova.dajSuradnicuX());
            this.pozY.setText("" + nova.dajSuradnicuY());
            this.popis.setText("" + nova.dajPopis());
            this.vysledok.setText("true");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        Random ran = new Random();
        
        int uspech = 0;
        int kolkoo = Integer.parseInt(this.kolko.getText());
        
        while (uspech != kolkoo) {
            int nahodne = ran.nextInt(1000) + 1;
            int nahodne1 = ran.nextInt(10000) + 1;
            int nahodne2 = ran.nextInt(100) + 1;
            int nahodne3 = ran.nextInt(100) + 1;
            
            char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            StringBuilder sb = new StringBuilder(20);
            Random random = new Random();
            for (int i = 0; i < 20; i++) {
                char c = chars[random.nextInt(chars.length)];
                sb.append(c);
            }
            String output = sb.toString();

        
            Nehnutelnost nova = new Nehnutelnost();
            nova.nastavID(nahodne); 
            nova.nastavSupisneCislo(nahodne1);
            nova.nastavSuradnicuX(nahodne2);
            nova.nastavSuradnicuY(nahodne3);
            nova.nastavPopis(output);
            
            //if (this.obalka.hladajData(nova) == null) {
                this.obalka.vlozData(nova);
                uspech = uspech + 1;
                this.obalka.vlozPouzite(nahodne);
            //}
        }
        
        //this.vyplnTabulku();
        this.vyplnHlavnuTabulku();
        this.vyplnPreplnovaciuTabulku();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        Nehnutelnost nova = new Nehnutelnost();
        nova.nastavID(Integer.parseInt(this.id.getText()));
        this.obalka.zmazatData(nova);
        //vyplnTabulku();
        vyplnHlavnuTabulku();
        vyplnPreplnovaciuTabulku();
        
        this.obalka.odstranPouzite(Integer.parseInt(this.id.getText()));
        this.id.setText("");
        this.supisne.setText("");
        this.pozX.setText("");
        this.pozY.setText("");
        this.popis.setText("");
        
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        this.obalka.ulozStav();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        this.obalka.nacitajStav();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        Nehnutelnost nova = new Nehnutelnost();
        nova.nastavID(Integer.parseInt(this.id.getText()));
        nova.nastavSupisneCislo(Integer.parseInt(this.supisne.getText()));
        nova.nastavSuradnicuX(Double.parseDouble(this.pozX.getText()));
        nova.nastavSuradnicuY(Double.parseDouble(this.pozY.getText()));
        
        String s = new String(this.popis.getText());
            if (s.length() < 20) {
                int doplnit = 20 - s.length();
                for (int i = 0; i < doplnit; i++) {
                    s = s + " ";
                }
            }
            nova.nastavPopis(s);
        
        this.obalka.upravaDat(nova);
        //vyplnTabulku();
        vyplnHlavnuTabulku();
        vyplnPreplnovaciuTabulku();
        this.id.setText("");
        this.supisne.setText("");
        this.pozX.setText("");
        this.pozY.setText("");
        this.popis.setText("");
    }//GEN-LAST:event_jButton8ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Okno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Okno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Okno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Okno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Okno().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable hlavnaTabulka;
    private javax.swing.JTextField id;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField kolko;
    private javax.swing.JTextField popis;
    private javax.swing.JTextField pozX;
    private javax.swing.JTextField pozY;
    private javax.swing.JTable preplnovacia;
    private javax.swing.JTextField supisne;
    private javax.swing.JLabel vysledok;
    // End of variables declaration//GEN-END:variables
}
