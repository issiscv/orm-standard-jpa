package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");//엔티티 매니저 팩토리
        EntityManager em = emf.createEntityManager();//엔티티 매니저
        EntityTransaction tx = em.getTransaction();//트랜잭션
        
        tx.begin();//트랜잭션 시작

        try {

            for (int i = 0; i < 10; i++) {
                em.persist(new Member((long) i, "member" + i));
            }

            List<Member> members = em.createQuery("select m from Member m", Member.class)
                    .getResultList();

            for (Member member : members) {
                System.out.println("member.getId() = " + member.getId());
                System.out.println("member.getUsername() = " + member.getUsername());
            }

            tx.commit();//커밋
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
