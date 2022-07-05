package dataaccess;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import models.Note;
import models.User;

public class NoteDB {

    public List<Note> getAllNotes() throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try{
            List<Note> noteList = em.createNamedQuery("Note.findAll",Note.class).getResultList();
            return noteList;
        }finally{
            em.close();
        }
    }
    
    public List<Note> getAll(String owner) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try{
            User user = em.find(User.class, owner);
            List<Note> noteList = user.getNoteList();
            return noteList;
        } finally {
            em.close();
        }
    }

    public Note get(int noteId) throws Exception {
       EntityManager em = DBUtil.getEmFactory().createEntityManager();
       
       try {
           Note note = em.find(Note.class, noteId);
           // get all of the notes, from the owner of this note
           // note.getOwner().getNoteList();
           // get the frist name of the owner of this note
           // note.getOwner().getFirstName();
           return note;
       } finally {
           em.close();
       }

    }

    public void insert(Note note) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            // bi-directional relationship
            // add the note to the user's list of notes
            User user = note.getOwner();
            user.getNoteList().add(note);
            // use a transaction to ensure data integrity
            trans.begin();
            // persist will insert the note
            em.persist(note);
            // merge will update the user
            em.merge(user);
            // if all interactions are good, commit the transaction
            trans.commit();
        } catch(Exception e){
            // if there is a problem, roll back to before the transaction began
            trans.rollback();
        } finally {
            em.close();
        }
        
    }

    public void update(Note note) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(note);
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
        } finally {
            em.close();
        }
    }

    public void delete(Note note) throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            User user = note.getOwner();
            user.getNoteList().remove(note);
            trans.begin();
            // we must merge before we remove
            // to ensure that the database recognises this object as an entity
            em.remove( em.merge(note) );
            em.merge(user);
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
        } finally {
            em.close();
        }
    }

}
