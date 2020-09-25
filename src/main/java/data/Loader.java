package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import model.Flag;
import model.Post;
import model.PostReason;
import model.Reason;

public class Loader {
    private static final SessionFactory factory;
    static {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object: " + ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    private Loader() {
    }

    public static Map<Integer, Post> loadPosts() {
        Map<Integer, Post> postMap = new HashMap<>();

        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            List<Post> posts = session.createQuery("FROM Post", Post.class).getResultList();
            for (Iterator<Post> iterator = posts.iterator(); iterator.hasNext();) {
                Post post = iterator.next();
                postMap.put(post.getId(), post);
            }
            tx.commit();
            System.out.println("Successfully loaded " + postMap.keySet().size() + " post categories.");
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }

        return postMap;
    }

    public static Map<Integer, Reason> loadReasons() {
        Map<Integer, Reason> reasonMap = new HashMap<>();

        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            List<Reason> reasons = session.createQuery("FROM Reason", Reason.class).getResultList();
            for (Iterator<Reason> iterator = reasons.iterator(); iterator.hasNext();) {
                Reason reason = iterator.next();
                reasonMap.put(reason.getId(), reason);
            }
            tx.commit();
            System.out.println("Successfully loaded " + reasonMap.keySet().size() + " Reason weights.");
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }

        return reasonMap;
    }

    public static Map<Integer, List<Integer>> loadPostReasons() {
        Map<Integer, List<Integer>> postReasonsMap = new HashMap<>();
        Map<Integer, List<Integer>> reasonPostsMap = new HashMap<>();

        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            List<PostReason> posts = session.createQuery("FROM PostReason", PostReason.class).getResultList();
            for (Iterator<PostReason> iterator = posts.iterator(); iterator.hasNext();) {
                PostReason post = iterator.next();
                // map post to reasons
                List<Integer> reasonList = postReasonsMap.getOrDefault(post.getPostId(), new ArrayList<>());
                reasonList.add(post.getReasonId());
                postReasonsMap.putIfAbsent(post.getPostId(), reasonList);
                // map reasons to posts
                List<Integer> postList = reasonPostsMap.getOrDefault(post.getReasonId(), new ArrayList<>());
                postList.add(post.getPostId());
                reasonPostsMap.putIfAbsent(post.getReasonId(), postList);
            }
            tx.commit();
            System.out.println("Successfully loaded " + postReasonsMap.keySet().size() + " posts with reasons.");
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return postReasonsMap;
    }

    public static Map<Integer, Flag> loadFlags() {
        Map<Integer, Flag> flagMap = new HashMap<>();

        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            List<Flag> flags = session.createQuery("FROM Flag", Flag.class).getResultList();
            for (Iterator<Flag> iterator = flags.iterator(); iterator.hasNext();) {
                Flag flag = iterator.next();
                flagMap.put(flag.getId(), flag);
            }
            tx.commit();
            System.out.println("Successfully loaded " + flagMap.keySet().size() + " Flags.");
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }

        return flagMap;
    }
}
