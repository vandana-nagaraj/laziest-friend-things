package com.example.hiborm;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.hiborm.model.Employee;
import com.example.hiborm.util.HibernateUtil;


public class App {
    public static void main(String[] args) {
        
        // This 'try-with-resources' block ensures the Session is automatically closed, 
        // preventing the IllegalStateException you faced previously.
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            // Start a new transaction.
            Transaction tx = session.beginTransaction();
            
            // Create a new Employee object (this is a 'transient' state).
            Employee emp = new Employee("dushyanth", "dushyath@gmail.com"); 
            
            // This line makes the transient object 'persistent' and prepares it for saving.
            session.persist(emp);
            
            // This 'commits' the transaction, which saves the employee to the database.
            tx.commit();
            
            // Now you can retrieve the data.
            Employee eData = session.get(Employee.class, emp.getId());
            System.out.println("Retrieved the Data: " + eData);
            
            // Start another transaction for the native query.
            tx = session.beginTransaction();
            
            // This executes a native SQL query to get all employees from the database.
            List<Employee> empList = session.createNativeQuery("SELECT * FROM employee", Employee.class)
                    .getResultList(); 
            System.out.println(empList);
            
            // Commit the transaction for the query.
            tx.commit();
            
            // Evict (detach) the object from the session. It's no longer managed by Hibernate.
            session.evict(eData); 
            System.out.println("Is eData attached? " + session.contains(eData));
           
            // Getting the object again will re-attach it to the session.
            Employee ed = session.get(Employee.class, emp.getId());
            
            // This block demonstrates adding and immediately removing an object.
            tx = session.beginTransaction();
            Employee emp1 = new Employee("Ria", "Ria@gmail.com"); 
            session.persist(emp1);
            session.remove(emp1);
            tx.commit();
            
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // This ensures the SessionFactory is closed once all work is done.
            HibernateUtil.close();
            System.out.println("Session pool is closed");
        }
    }
}