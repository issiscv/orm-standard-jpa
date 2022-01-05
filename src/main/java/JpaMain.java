
import domain.item.Book;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");//엔티티 매니저 팩토리
        EntityManager em = emf.createEntityManager();//엔티티 매니저
        EntityTransaction tx = em.getTransaction();//트랜잭션
        
        tx.begin();//트랜잭션 시작

        try {

            Book book = new Book();
            book.setName("JPA");
            book.setAuthor("나");

            em.persist(book);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
