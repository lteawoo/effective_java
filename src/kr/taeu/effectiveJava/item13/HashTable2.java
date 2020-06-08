package kr.taeu.effectiveJava.item13;

/* 
 * 버킷을 구성하는 연결리스트를 복사. 재귀 호출 때문에 스택 프레임을 소비하고, 리스트가 길면 스택오버플로우의 위험이있다.
 */
public class HashTable2 implements Cloneable {
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
      return new Entry(key, value, next == null ? null : next.deepCopy());
    }
  }

  @Override
  protected Object clone() {
    try {
      HashTable2 result = (HashTable2) super.clone();
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
