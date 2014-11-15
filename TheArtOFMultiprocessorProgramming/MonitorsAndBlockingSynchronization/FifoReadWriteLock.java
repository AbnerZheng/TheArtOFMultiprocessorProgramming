import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FifoReadWriteLock{
	int readAcquires, readReleases;
	boolean writer;
	Lock lock;
	Condition condition;
	ReadLock readLock;
	WriteLock writeLock;
	private class ReadLock{
		public void lock() throws InterruptedException{
			lock.lock();
			try{
				while(writer){
					condition.await();
				}
				/*
				*Ǳ��bug��readAcquiresֻ�Ӳ������������Ӷ���readReleasesƥ�����
				*/
				++readAcquires;
			}finally{
				lock.unlock();
			}
		}
		public void unlock(){
			lock.lock();
			try{
				/*
				*Ǳ��bug��readReleasesֻ�Ӳ������������Ӷ���readAcquiresƥ�����
				*/
				++readReleases;
				if(readAcquires == readReleases)//���������ʱ��ʾ���̳߳���lock
					condition.signalAll();
				//�ҵ�fix bug���룺
				//���������Ϊ��
				/*
				if(readAcquires == readReleases){
					readAcquires = readReleases = 0;
					condition.signalAll();
				}
				*/
			}finally{
				lock.unlock();
			}
		}
	}
	private class WriteLock{
		public void lock() throws InterruptedException{
			lock.lock();
			try{
				while(writer){//���̳߳���д��ʱ��wait
					condition.await();
				}
				//����writerΪtrue����ֹ���µ�readLock������
				writer = true;
				//�����ж���������ʱ��ȴ�
				while(readAcquires != readReleases){
					condition.await();
				}
			}finally{
				lock.unlock();
			}
		}
		public void unlock(){
			writer = false;
			condition.signalAll();
		}
	}
	
	public FifoReadWriteLock(){
		readAcquires = readReleases = 0;
		writer = true;
		lock = new ReentrantLock(true);
		condition = lock.newCondition();
		readLock = new ReadLock();
		writeLock = new WriteLock();
	}
	public ReadLock readLock(){
		return readLock;
	}
	public WriteLock writeLock(){
		return writeLock;
	}
}