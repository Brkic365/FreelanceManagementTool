package hr.tvz.java.freelance.freelancemanagementtool.controller;

import hr.tvz.java.freelance.freelancemanagementtool.model.Entity;

/**
 * Sučelje koje definira ugovor za sve kontrolere koji se koriste za
 * izmjenu ili dodavanje entiteta.
 *
 * @param <T> Tip entiteta kojim ovaj kontroler upravlja.
 */
public interface EditController<T extends Entity> {

    /**
     * Postavlja entitet koji će se uređivati.
     * @param entity Entitet za uređivanje.
     */
    void setEntityToEdit(T entity);

    /**

     * Provjerava je li korisnik kliknuo na gumb za spremanje.
     * @return true ako je spremanje potvrđeno, inače false.
     */
    boolean isSaveClicked();
}