package pcep.db;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;


public class HibernateUtil {
	
	private static StatelessSession statelessSession;
	private static Session session;
    private static final Logger logger = Logger.getLogger(HibernateUtil.class);
    private static Transaction transaction;
    
    
    private static final SessionFactory sessionFactory;
    static {
        try {
            sessionFactory = getSessionFactory();
        } catch (Throwable ex) {
            logger.fatal("Could not initialize Hibernate");
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static void begin() {
    	if (session != null) {
    		transaction = session.beginTransaction();
    	}
    	else if (statelessSession != null) {
    		transaction = statelessSession.beginTransaction();
    	}
    	
    }
    
    public static void commit() {
    	transaction.commit();	
    }
    
    public static void open() {
    	if (session == null || !session.isOpen()) {
    		session = sessionFactory.openSession();	
    	}
    }
    
    public static void openStateless() {
    	if (statelessSession == null) {
    		statelessSession = getSessionFactory().openStatelessSession();
    	}
    }
    
    public static void close() {
    	if (session != null) {
    		session.close();
    		session = null;
    	}
    	
    	if (statelessSession != null) {
    		statelessSession.close();
    		statelessSession = null;
    	}
    	
    }
    
    public static void flush() {
    	session.flush();
    	session.clear();
    }
    
    public static void insert(Object o) {
    	statelessSession.insert(o);
    }
    
    
    public static void save(Object o) {
    	session.save(o);
    }
    
    public static void update(Object o) {
    	session.update(o);
    }
    
    public static Query query(String hql) {
    	return session.createQuery(hql);
    }
    
    public static Criteria criteria(Class<?> clazz) {
    	return session.createCriteria(clazz);
    }
    
    public static Filter filter(String name) {
    	return session.enableFilter(name);
    }

    
    private static SessionFactory getSessionFactory() {
        Configuration configuration = new Configuration()
        .setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver")
        .setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/pcepdb")
        .setProperty("hibernate.connection.username", "pcep")
        .setProperty("hibernate.connection.password", "pcep")
//        .setProperty("hibernate.hbm2ddl.auto", "validate")
//        .setProperty("hibernate.hbm2ddl.auto", "create-drop")
        .setProperty("hibernate.show_sql", "false")
        .setProperty("hibernate.c3p0.acquire_increment", "1")
        .setProperty("hibernate.c3p0.idle_test_period", "15")
        .setProperty("hibernate.c3p0.max_size", "100")
        .setProperty("hibernate.c3p0.max_statements", "0")
        .setProperty("hibernate.jdbc.batch_size", "20")
        .setProperty("hibernate.default_batch_fetch_size", "20")
        .setProperty("hibernate.c3p0.min_size", "10")
        .setProperty("hibernate.c3p0.timeout", "10")
        .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
        .setProperty("hibernate.current_session_context_class", "thread")
        .setProperty("hibernate.cache.provider_class", "org.hibernate.cache.internal.NoCacheProvider")
        .setNamingStrategy(ImprovedNamingStrategy.INSTANCE)
        .addAnnotatedClass(House.class)
		.addAnnotatedClass(MeasurementUnit.class)
		.addAnnotatedClass(MeasurementValue.class)
		.addAnnotatedClass(Sensor.class)
		.addAnnotatedClass(ValueClassification.class)
		.addAnnotatedClass(ValueClassificationRange.class)
        ;
        
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        return configuration.buildSessionFactory(serviceRegistry);
    }
    

}
