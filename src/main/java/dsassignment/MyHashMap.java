package dsassignment;
import java.io.Serializable;

class Entry<K,V> implements Serializable{
    final K key;
    V value;

    Entry<K,V> next;  // pointer to next pair if have the same hashcode/bucket

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }
}


public class MyHashMap<K,V> implements Serializable {

    private static final int SIZE = 16;

    @SuppressWarnings("unchecked")
    Entry<K,V> bucket[] = new Entry[SIZE];


//-------------------------------------------------put data ------------------------------------------

    public void put(K index, V value) {
        Entry<K, V> newEntry = new Entry<>(index, value);
        int hashCode = Math.abs(index.hashCode() % SIZE);
        Entry<K, V> current = (Entry<K, V>) bucket[hashCode];
        Entry<K, V> previous = null;

        // Check if the index already exists in the bucket
        while (current != null) {
            if (current.key.equals(index)) {
                // If the index already exists, check and update the data type
                if (!current.getValue().getClass().equals(value.getClass())) {
                    // Handle the case where the data type is different
                    System.out.println("Error: Data type mismatch for index " + index);
                    return; // or throw an exception if you prefer
                }

                // Update the existing entry with the new value
                current.setValue(value);
                return;
            }
            previous = current;
            current = current.next;
        }

        // If the index is not found in the bucket, add a new entry
        if (previous == null) {
            // If the bucket is empty
            bucket[hashCode] = (Entry<K, V>) newEntry;
        } else {
            // If there are existing entries in the bucket
            previous.next = newEntry;
        }
    }


//-------------------------------------------------get data ------------------------------------------
    public V get(K key) {
        int hashCode = key.hashCode() % SIZE;
        Entry<K,V> e = bucket[hashCode];

        
        while (e != null) {
            if(e.key.equals(key)){
                return e.getValue();
            }
            e = e.next;
                
        }       
        return null;
    }

//-------------------------------------------------remove data ------------------------------------------
    public void remove(K key) {
        int hashCode = Math.abs(key.hashCode() % SIZE);  // Ensure a non-negative hash code
        Entry<K, V> current = bucket[hashCode];
        Entry<K, V> previous = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (previous == null) {
                    // If the matching entry is the first in the bucket
                    bucket[hashCode] = current.next;
                } else {
                    // If the matching entry is not the first in the bucket
                    previous.next = current.next;
                }
                break; // Exit the loop once the entry is removed
            }
            previous = current;
            current = current.next;
        }
    }

    
//-------------------------------------------------clear all data ------------------------------------------
    public void clear(){
        for(int i = 0; i < SIZE; i++){
            bucket[i] = null;
        }
    }

 //-------------------------------------------------print data ------------------------------------------
    public void print(){
        for(Entry<K,V> e : bucket){
            if(e != null){
                System.out.print("key : "+e.getKey()+" -- value : "+e.getValue()); 
                e = e.next;
                while(e != null){
                     System.out.print("------> key : "+e.getKey()+" -- value : "+e.getValue());
                     e = e.next;
                }
                System.out.println();
             }
            else{
                System.out.println("Bucket is empty");
            }
        }
    }
  
}
