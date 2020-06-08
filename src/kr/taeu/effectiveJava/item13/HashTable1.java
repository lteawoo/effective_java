package kr.taeu.effectiveJava.item13;

/* 
 * 단순히 버킷의 clone만 재귀호출한 해시테이블
 * 복제본은 자신만의 버킷을 갖지만, 버킷은 원본과 같은 연결 리스트를 참조(여기서는 엔트리 배열)
 */
public class HashTable1 implements Cloneable {
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
  }

  @Override
  protected Object clone() {
    try {
      HashTable1 result = (HashTable1) super.clone();
      result.buckets = buckets.clone();
      return result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
