package com.project.jpa;

import javax.persistence.*;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
            // Create
//            Member member = new Member();
//            member.setId(3L);
//            member.setName("TEST");
//            em.persist(member);

            // Read
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember.id \t\t = " + findMember.getId());
//            System.out.println("findMember.name \t = " + findMember.getName());

            // Update
//            Member findMember = em.find(Member.class, 2L);
//            findMember.setName("HelloJPA");

            // Delete
//            Member findMember = em.find(Member.class, 1L);
//            em.remove(findMember);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
