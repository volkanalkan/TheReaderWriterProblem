
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
public class Test {
    public static void main(String [] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ReadWriteLock RW = new ReadWriteLock();

        // Creating and executing writer threads
        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));
        
        // Creating and executing reader threads
        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));
    }
}
class ReadWriteLock{
    private Semaphore readSemaphore=new Semaphore(1);
    private Semaphore writeSemaphore=new Semaphore(1);
    private int readCount = 0;

    // Method to acquire a read lock
    public void readLock() {
        try{
        	readSemaphore.acquire();
        }
        catch (InterruptedException e) {}

        ++readCount;

        if (readCount == 1){
            try{
            	writeSemaphore.acquire();
            }
            catch (InterruptedException e) {}
        }

        System.out.println("Thread " + Thread.currentThread().getName() + " is reading.");
        readSemaphore.release();
    }
    
    // Method to acquire a write lock
    public void writeLock() {
        try{
        	writeSemaphore.acquire();
        }
        catch (InterruptedException e) {}
        System.out.println("Thread " + Thread.currentThread().getName() + " is writing.");

    }
    
    // Method to release a read lock
    public void readUnLock() throws InterruptedException {

        try{
        	readSemaphore.acquire();
        }
        catch (InterruptedException e) {}

        --readCount;

        if (readCount == 0){
        	writeSemaphore.release();
        }

        System.out.println("Thread " + Thread.currentThread().getName() + " has completed reading.");

        readSemaphore.release();

    }

    // Method to release a write lock
    public void writeUnLock() {
        System.out.println("Thread " + Thread.currentThread().getName() + " has completed writing.");
        writeSemaphore.release();
    }
}

class Writer implements Runnable
{
    private ReadWriteLock customRW;

    // Constructor to initialize the customRW object
    public Writer(ReadWriteLock rw) {
    	customRW = rw;
    }

    // Run method for the writer thread
    public void run() {
        while (true){
            SleepUtilities.nap();
            customRW.writeLock();
            SleepUtilities.nap();
            customRW.writeUnLock();

        }
    }


}

class Reader implements Runnable
{
    private ReadWriteLock customRW;

    // Constructor to initialize the customRW object
    public Reader(ReadWriteLock rw) {
    	customRW = rw;
    }
    
    // Run method for the reader thread
    public void run() {
        while (true){
            SleepUtilities.nap();
            customRW.readLock();


            try {
                SleepUtilities.nap();
                customRW.readUnLock();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}

class SleepUtilities
{
	// Method to sleep for a default duration
    public static void nap() {
        nap(NAP_TIME);
    }

    // Method to sleep for a specified duration
    public static void nap(int duration) {
        int sleeptime = (int) (NAP_TIME * Math.random() );
        try { Thread.sleep(sleeptime*1000); }
        catch (InterruptedException e) {}
    }

    // Constant for the default nap time
    private static final int NAP_TIME = 5;
}