package com.example.projetpoobibliotheque;

import org.hibernate.Session;

public class Main {
    public static void main(String[] args) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        System.out.println("Hibernate initialisé. Les tables sont créées !");

        session.getTransaction().commit();
        session.close();
    }
}
