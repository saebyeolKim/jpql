package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class jpqlMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();     //트랜잭션 시작
        //code
        try {

            Team team = new Team();
            team.setName("member");
            em.persist(team);

            Member member = new Member();
            member.setUsername("TeamA");
            member.setAge(10);
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "select m from Member m left join m.team t on t.name = 'TeamA'";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            tx.commit();    //커밋
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}