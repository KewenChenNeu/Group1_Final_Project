package Business.DB4OUtil;

import Business.ConfigureASystem;
import Business.EcoSystem;
import Business.Network.Network;
import Business.Enterprise.Enterprise;
import Business.Organization.Organization;
import Business.UserAccount.UserAccount;
import Business.Employee.Employee;
import Business.WorkQueue.WorkRequest;
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.ta.TransparentPersistenceSupport;
import java.nio.file.Paths;

/**
 *
 * @author rrheg
 * @author Lingfeng
 */
public class DB4OUtil {

    // Database file path - stored in project root directory
    private static final String FILENAME = Paths.get("Databank.db4o").toAbsolutePath().toString();
    
    // Singleton instance
    private static DB4OUtil dB4OUtil;
    
    /**
     * Get singleton instance of DB4OUtil
     */
    public synchronized static DB4OUtil getInstance() {
        if (dB4OUtil == null) {
            dB4OUtil = new DB4OUtil();
        }
        return dB4OUtil;
    }
    
    /**
     * Private constructor for singleton pattern
     */
    private DB4OUtil() {
    }

    /**
     * Shutdown database connection
     */
    protected synchronized static void shutdown(ObjectContainer conn) {
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * Create and configure database connection
     */
    private ObjectContainer createConnection() {
        try {
            EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
            
            // Enable transparent persistence for automatic dirty tracking
            config.common().add(new TransparentPersistenceSupport());
            
            // Controls the number of objects in memory (depth of object graph to activate)
            config.common().activationDepth(Integer.MAX_VALUE);
            
            // Controls the depth/level of updation of Object
            config.common().updateDepth(Integer.MAX_VALUE);

            // Configure cascade on update for all major classes
            // This ensures changes to nested objects are saved
            config.common().objectClass(EcoSystem.class).cascadeOnUpdate(true);
            config.common().objectClass(Network.class).cascadeOnUpdate(true);
            config.common().objectClass(Enterprise.class).cascadeOnUpdate(true);
            config.common().objectClass(Organization.class).cascadeOnUpdate(true);
            config.common().objectClass(UserAccount.class).cascadeOnUpdate(true);
            config.common().objectClass(Employee.class).cascadeOnUpdate(true);
            config.common().objectClass(WorkRequest.class).cascadeOnUpdate(true);

            // Open database file
            ObjectContainer db = Db4oEmbedded.openFile(config, FILENAME);
            return db;
            
        } catch (Exception ex) {
            System.err.println("Error creating database connection: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Store/Save the EcoSystem to database
     * 
     * @param system The EcoSystem to save
     */
    public synchronized void storeSystem(EcoSystem system) {
        ObjectContainer conn = null;
        try {
            conn = createConnection();
            if (conn != null) {
                conn.store(system);
                conn.commit();
                System.out.println("System saved successfully.");
            }
        } catch (Exception ex) {
            System.err.println("Error storing system: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    /**
     * Retrieve the EcoSystem from database
     * If no system exists, creates a new one using ConfigureASystem
     * 
     * @return The EcoSystem (either retrieved or newly created)
     */
    public EcoSystem retrieveSystem() {
        ObjectContainer conn = null;
        EcoSystem system = null;
        
        try {
            conn = createConnection();
            if (conn != null) {
                ObjectSet<EcoSystem> systems = conn.query(EcoSystem.class);
                
                if (systems.size() == 0) {
                    // No existing system found, create new one
                    System.out.println("No existing data found. Initializing new system...");
                    system = ConfigureASystem.configure();
                } else {
                    // Retrieve the most recent system
                    system = systems.get(systems.size() - 1);
                    System.out.println("System retrieved successfully.");
                }
            }
        } catch (Exception ex) {
            System.err.println("Error retrieving system: " + ex.getMessage());
            ex.printStackTrace();
            // If error occurs, try to create a fresh system
            system = ConfigureASystem.configure();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        
        return system;
    }
    
    /**
     * Delete the database file (for testing/reset purposes)
     * 
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteDatabase() {
        try {
            java.io.File dbFile = new java.io.File(FILENAME);
            if (dbFile.exists()) {
                return dbFile.delete();
            }
            return true;
        } catch (Exception ex) {
            System.err.println("Error deleting database: " + ex.getMessage());
            return false;
        }
    }
}







