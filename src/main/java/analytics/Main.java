package analytics;

import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import model.Post;

public class Main {
    private static SessionFactory factory;

    public static void main(String... args) {
        // Force Java into UTC so LocalDateTime matches, avoids DST issues
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));

        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

        listPosts();
    }

    private static void listPosts() {

        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            List<Post> posts = session.createQuery("FROM Post", Post.class).getResultList();
            for (Iterator<Post> iterator = posts.iterator(); iterator.hasNext();) {
                Post post = iterator.next();
                System.out.println(post.isTp());
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
        }
    }
}
