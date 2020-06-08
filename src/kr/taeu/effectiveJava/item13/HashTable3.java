package kr.taeu.effectiveJava.item13;

/* 
 * 반복자를 이용하여 재귀호출 대신함.
 */
public class HashTable3 implements Cloneable {
  private Entry[] buckets = new Entry[100];
  
  private static class Entry {
    final Object key;
    Object value;
    Entry next;
    
    Entry(Object key, Object value, Entry next) {
      this.key = key;
      this.value = value;
      this.next = next;
    }
    
    Entry deepCopy() {
      Entry result = new Entry(key, value, next);
      for (Entry p = result; p.next != null; p = p.next) {
        p.next = new Entry(p.next.key, p.next.value, p.next.next);
      }
      return result;
    }
  }

  @Override
  protected Object clone() {
    try {
      HashTable3 result = (HashTable3) super.clone();
      result.buckets = new Entry[buckets.length];
      for (int i = 0; i < buckets.length; i++) {
        if (buckets[i] != null) {
          result.buckets[i] = buckets[i].deepCopy();
        }
      }
      return result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
