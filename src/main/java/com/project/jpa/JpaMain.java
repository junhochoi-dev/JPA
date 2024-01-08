package com.project.jpa;

import javax.persistence.*;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
            Member member1 = new Member();
            Member member2 = new Member();
            member1.setRoleType(RoleType.USER);
            member2.setRoleType(RoleType.ADMIN);

            em.persist(member1);
            em.persist(member2);

            tx.commit();
        } catch (Exception e) {
            System.out.println("Exception");
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
