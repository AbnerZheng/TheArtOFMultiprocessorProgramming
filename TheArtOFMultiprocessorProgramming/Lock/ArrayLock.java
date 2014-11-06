import java.util.concurrent.atomic.*;

class ArrayLock{
	private ThreadLocal<Integer> mySlotIndex = new ThreadLocal<Integer>(){
		protected Integer initialValue(){
			return 0;
		}
	};
	private AtomicInteger tail;
	private volatile boolean []flag;
	private final int size;
	public ArrayLock(int capacity){
		size = capacity;
		flag = new boolean[size];
		flag[0] = true;
		tail = new AtomicInteger(0);
	}
	/*
	 *������size��С���̺߳ܶ�ʱ������������±�(slot)�ػ���
	 *��������Ǳ��bug��ʹ�ö���߳���ͬʱ����critical section�� 
	 */
	public void lock(){
		Integer slot = tail.getAndIncrement() % size;
		mySlotIndex.set(slot);
		while(!flag[slot]){}//���ҵ�slot��spin��ֱ��ֵΪtrue
	}
	public void unlock(){
		Integer slot = mySlotIndex.get();
		flag[slot] = false;//��flag[slot]��Ϊfalse��ʾ�ҷ���ʹ��lock
		flag[(slot + 1) % size] = true;//���ҵĺ��λ���ϵ�ֵ��Ϊtrue����ʾ��lockת�ø���
	}
}