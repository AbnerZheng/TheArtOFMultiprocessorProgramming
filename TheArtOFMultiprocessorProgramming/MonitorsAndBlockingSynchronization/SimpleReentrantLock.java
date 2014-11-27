import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleReentrantLock{
	Lock lock;
	Condition condition;
	long owner;
	int holdCount;
	
	public SimpleReentrantLock(){
		lock = new ReentrantLock();
		condition = lock.newCondition();
		owner = 0;
		holdCount = 0;
	}
	public void lock() throws InterruptedException{
		long me = Thread.currentThread().getId();
		lock.lock();
		try{
			if(owner == me){//ͬһ���߳�
				++holdCount;
				return;
			}
			while(holdCount != 0){//��ͬ�߳��������߳��Ѿ�����lock��
				condition.await();
			}
			owner = me;
			holdCount = 1;
		}finally{
			lock.unlock();
		}
	}
	public void unlock(){
		lock.lock();
		try{
			if(holdCount == 0 || owner != Thread.currentThread().getId()){
				throw new IllegalMonitorStateException();
			}
			--holdCount;
			if(holdCount == 0){
				condition.signal();
			}
		}finally{
			lock.unlock();
		}
	}
}