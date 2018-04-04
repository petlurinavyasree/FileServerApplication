package multithreadfileserver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;

public class FileOperations {

    ReadWriteLock readWriteLock = null;

    public FileOperations(ReadWriteLock readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    public String readFile(String fileName) {
        String outmessage = null;

        System.out.println(Thread.currentThread()+" readFile - start - time="+new Timestamp(System.currentTimeMillis()));
        try {
            readWriteLock.lockRead(fileName);
            System.out.println(Thread.currentThread()+" readFile - after lockRead - time="+new Timestamp(System.currentTimeMillis()));
            
            // Adding delay to test the multi-thread access scenarios
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
            }

            String fileContent = new String(Files.readAllBytes(Paths.get(fileName)));
            outmessage = "File read requested at: " + new Timestamp(System.currentTimeMillis()) + "File Content: " + fileContent;
            
            System.out.println(Thread.currentThread()+" readFile - end reading - time="+new Timestamp(System.currentTimeMillis()));

        } catch (InterruptedException e) {
            outmessage = "InterruptedException occurred while trying to lock the file "
                    + new Timestamp(System.currentTimeMillis());
        } catch (IOException e) {
            outmessage = "IOException occurred while reading the file " + new Timestamp(System.currentTimeMillis());
        } finally {
            System.out.println(Thread.currentThread()+" readFile - finally - time="+new Timestamp(System.currentTimeMillis()));
            // release the read lock
            readWriteLock.unlockRead(fileName);
        }
        System.out.println(Thread.currentThread()+" readFile - end - time="+new Timestamp(System.currentTimeMillis()));
        return outmessage;
    }

    public String writeFile(String fileName, String fileContent) {
        String outmessage = null;
        System.out.println(Thread.currentThread()+" writeFile - start - time="+new Timestamp(System.currentTimeMillis()));
        try {
            readWriteLock.lockWrite(fileName);
            System.out.println(Thread.currentThread()+" writeFile - after lockWrite - time="+new Timestamp(System.currentTimeMillis()));
            
            // Adding delay to test the multi-thread access scenarios
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
            }

            Path path = Paths.get(fileName);
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
            }
            
            System.out.println("fileName="+fileName);

            BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            writer.write(fileContent);

            outmessage = "File written successfully at " + new Timestamp(System.currentTimeMillis());
            writer.close();
            System.out.println(Thread.currentThread()+" writeFile - end writing - time="+new Timestamp(System.currentTimeMillis()));
        } catch (InterruptedException e) {
            outmessage = "InterruptedException while trying to lock " + new Timestamp(System.currentTimeMillis());
        } catch (IOException e) {
            outmessage = "IOException occurred while writing the file content "
                    + new Timestamp(System.currentTimeMillis());
        } catch (Exception e) {
            outmessage = e.getClass().getSimpleName() + " occurred while creating/writing to the file "
                    + new Timestamp(System.currentTimeMillis());
        } finally {
            try {
                System.out.println(Thread.currentThread()+" writeFile - finally - time="+new Timestamp(System.currentTimeMillis()));
                readWriteLock.unlockWrite(fileName);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println(Thread.currentThread()+" writeFile - end - time="+new Timestamp(System.currentTimeMillis()));
        return outmessage;
    }

    public String deleteFile(String fileName) {
        String outmessage = null;
        System.out.println(Thread.currentThread()+" deleteFile - start - time="+new Timestamp(System.currentTimeMillis()));
        try {
            readWriteLock.lockWrite(fileName);
            System.out.println(Thread.currentThread()+" deleteFile - after lockWrite - time="+new Timestamp(System.currentTimeMillis()));
            
            // Adding delay to test the multi-thread access scenarios
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
            }
            Files.delete(Paths.get(fileName));
            outmessage = "File deleted successfully at " + new Timestamp(System.currentTimeMillis());

            System.out.println(Thread.currentThread()+" deleteFile - end deleting - time="+new Timestamp(System.currentTimeMillis()));
        } catch (InterruptedException e1) {
            outmessage = "InterruptedException occurred while trying to lock the file "
                    + new Timestamp(System.currentTimeMillis());
        } catch (IOException e) {
            outmessage = "IOException occurred while deleting the file " + new Timestamp(System.currentTimeMillis());
        } catch (Exception e) {
            outmessage = e.getClass().getSimpleName() + " occurred while deleting the file "
                    + new Timestamp(System.currentTimeMillis());
        } finally {
            try {
                System.out.println(Thread.currentThread()+" deleteFile - finally - time="+new Timestamp(System.currentTimeMillis()));
                readWriteLock.unlockWrite(fileName);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println(Thread.currentThread()+" deleteFile - end - time="+new Timestamp(System.currentTimeMillis()));
        return outmessage;
    }
}
