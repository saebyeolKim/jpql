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

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            String query = "select m from Member m join fetch m.team";
            List<Member> resultList = em.createQuery(query, Member.class)
                    .getResultList();
            for (Member member : resultList) {
                System.out.println("s = " + member.getUsername() + ", " + member.getTeam().getName());
                //회원1, 팀A(SQL)
                //회원2, 팀A(1차캐시)
                //회원3, 팀B(SQL)

                //회원100명 -> N(특정회원을 가져오기 위한 쿼리) + 1 (회원가져오기 위한 쿼리)
                //이와 같이 효율성이 없기때문에 join fetch 를 사용, 사용하면 한방 쿼리로 조회
            }

            String query2 = "select distinct t from Team t join fetch t.members";
            List<Team> resultList2 = em.createQuery(query2, Team.class)
                    .getResultList();
            for (Team team : resultList2) {
                System.out.println("team = " + team.getName() + "|members= " + team.getMembers());
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member); //중복쿼리 발생, 팀의 개수는 2개인데 이렇게하면 3개가 나옴
                    //distinct 를 사용해서 같은식별자를 가진 Team 엔티티 제거
                }
            }


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