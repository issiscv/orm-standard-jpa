
import domain.Member;
import domain.Team;
import domain.User;

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

            Team team = new Team();
            team.setName("teamA");
//            team.getUsers().add(user);
            em.persist(team);

            User user = new User();
            user.setUsername("userA");
            user.setTeam(team);
            em.persist(user);

//            em.flush();
//            em.clear();

            Team findTeam = em.find(Team.class, team.getId());
            List<User> users = findTeam.getUsers();

            for (User u : users) {
                System.out.println("u: "+u.getUsername());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
