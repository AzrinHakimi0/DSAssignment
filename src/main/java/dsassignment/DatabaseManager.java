package dsassignment;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Date;

import java.io.IOException;

public class DatabaseManager {
    
    MyHashMap<String,Object> databases;

    public DatabaseManager() {
        databases = new MyHashMap<String,Object>();
    }

    public Object searchByIndex(String index) {
        return databases.getValueByIndex(index);
    }

    public void Insert(String index, Number value) { //insert for Number value
       databases.put(index, value);
    }

    public void Insert(String index, String value) { //insert for String value
        databases.put(index, value);
    }

    public void Insert(String index, Object[] value) { // insert for Object array value
        databases.put(index, value);
    }

    public void Insert(String index, char value) { //insert for Character value
        databases.put(index, value);
    }

    public void Insert(String index, Boolean bool) { //insert for Character value
        databases.put(index, bool);
    }

    public void Insert(String index, Date date) { //insert for Character value
        databases.put(index, date);
    }

    public Object Get(String index){   
        return databases.get(index);
    }

    public void Delete(String index){
        databases.remove(index);
    }

    public void ClearDatabase(){
        databases.clear();
    }

    public String getDataType(String index){
        return Get(index).getClass().getSimpleName();
    }

    public void SaveData(String fileName){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(databases);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object searchByKeyOrValue(String keyOrValue) {
        if (databases.containsKey(keyOrValue)) {
            return databases.getValueByIndex(keyOrValue);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public MyHashMap<String, Object> loadData(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (MyHashMap<String, Object>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
  
}
